/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.claim.JobDefinition;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.supplier.contract.Contract;

/**
 * @author radhakrishnan.j
 * @deprecated Use
 * BusinessObjectModelFactory.getInstance().getBusinessObjectModel(context)
 * instead
 */
@Deprecated
public class BusinessObjectModel implements IBusinessObjectModel {

    public static BusinessObjectModel getInstance() {
        return _instance;
    }

    public BusinessObjectModel() {
        this.domainTypeSystem = new DomainTypeSystem();
        initialize();
    }

    public Set<String> listAllContexts() {
        final Set<String> allContexts = this.contextWiseBOs.keySet();
        final Set<String> allContextsCopy = new HashSet<String>(allContexts);
        for (String context : allContexts) {
            if (context.indexOf("-flattened") != -1) {
                allContextsCopy.remove(context);
            }
        }

        return allContextsCopy;
    }

    public SortedMap<String, FieldTraversal> getAllLevelDataElementsForRule(String context) {
        SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();
        for (SortedMap<String, FieldTraversal> fieldsForAllTypes : this.fieldsInBO.values()) {
            for (Map.Entry<String, FieldTraversal> field : fieldsForAllTypes.entrySet()) {
                fields.put(field.getValue().getDomainName(), field.getValue());
            }
        }

        return fields;
    }

    public SortedMap<String, FieldTraversal> getTopLevelDataElementsForRule(String context) {
        SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();
        Set<DomainType> businessObjects = this.contextWiseBOs.get(context);
        for (DomainType businessObject : businessObjects) {
            fields.putAll(this.fieldsInBO.get(businessObject));
        }
        return fields;
    }

    public String getExpression(String typeName) {

        DomainType domainType = (DomainType) this.domainTypeSystem.getType(typeName);
        return this.expressionsForTopLevelBOs.get(domainType);
    }

    public FieldTraversal getField(String typeName, String fieldName) {

        DomainType domainType = (DomainType) this.domainTypeSystem.getType(typeName);

        if (fieldName.indexOf("{") != -1) {
            return this.fieldsInBO.get(domainType).get(fieldName);
        }

        String expression = this.expressionsForTopLevelBOs.get(domainType);
        boolean isTopLevelBO = (expression != null);

        if (!isTopLevelBO) {
            expression = "";
        }

        OneToOneAssociation field = new OneToOneAssociation(domainType.getDomainName(), expression,
                domainType);
        FieldTraversal fieldTraversal = new FieldTraversal();
        fieldTraversal.addFieldToPath(field);

        String[] fieldNameParts = fieldName.split("\\.");
        if (buildField(typeName, fieldNameParts, 0, fieldTraversal)) {
            return fieldTraversal;
        } else {
            throw new IllegalArgumentException("No field found for " + "expression [" + fieldName
                    + "]!");
        }
    }

    private boolean buildField(String typeName, String[] fieldNameParts, int startIndex,
            FieldTraversal requiredField) {
        DomainType businessObject = (DomainType) this.domainTypeSystem.getType(typeName);
        String baseFieldName = fieldNameParts[startIndex++];

        String expression = this.expressionsForTopLevelBOs.get(businessObject);
        boolean isTopLevelBO = (expression != null);

        if (isTopLevelBO) {
            if (startIndex == 1) {
                baseFieldName += "." + fieldNameParts[startIndex++];
            } else {
                baseFieldName = businessObject.getName().toLowerCase() + "." + baseFieldName;
            }
        }

        FieldTraversal baseField = getDataElementsForType(businessObject).get(baseFieldName);

        while (baseField == null && startIndex < fieldNameParts.length) {
            baseFieldName += "." + fieldNameParts[startIndex++];
            baseField = getDataElementsForType(businessObject).get(baseFieldName);
        }

        if (baseField == null) {
            return false;
        }

        requiredField.addFieldToPath(baseField.targetField());

        return startIndex >= fieldNameParts.length
                || buildField(baseField.getType(), fieldNameParts, startIndex, requiredField);
    }

    public SortedMap<String, FieldTraversal> getDataElementsForType(DomainType businessObject) {
        SortedMap<String, FieldTraversal> fields = new TreeMap<String, FieldTraversal>();
        fields.putAll(this.fieldsInBO.get(businessObject));
        return fields;
    }

    void initialize() {

        this.expressionsForTopLevelBOs.put(claim(), "claim");
        this.expressionsForTopLevelBOs.put(policy(), "policy");
        this.expressionsForTopLevelBOs.put(contract(), "contract");
        // expressionsForTopLevelBOs.put(inventoryItem(), "inventoryItem");

        prepareTopLevelBOs();

        // TODO:TEMPORARY
        prepareFullyFlattenedBOs();

        for (Map.Entry<DomainType, String> expressionAndBO : this.expressionsForTopLevelBOs
                .entrySet()) {
            DomainType bo = expressionAndBO.getKey();
            String expression = expressionAndBO.getValue();
            discoverPathsToFields(bo, expression);
        }

        for (Map.Entry<DomainType, SortedMap<String, FieldTraversal>> entry : this.fieldsInBO
                .entrySet()) {
            if(logger.isInfoEnabled())
            {
                logger.info(" Field traversals for type [" + entry.getKey() + "]");
            }
            SortedMap<String, FieldTraversal> fields = entry.getValue();
            for (Map.Entry<String, FieldTraversal> fieldEntry : fields.entrySet()) {
                if(logger.isInfoEnabled())
                {
                    logger.info("\t" + fieldEntry.getValue());
                }
            }
        }
    }

    protected void prepareTopLevelBOs() {

        // FIX-ME: 'ClaimRules' should be called 'ClaimProcessingRules'.
        Set<DomainType> claimProcessingBOs = this.contextWiseBOs.get("ClaimRules");
        claimProcessingBOs.add(claim());

        // FIX-ME: 'PolicyRules' should be called 'PolicyApplicabilityRules'
        Set<DomainType> policyApplicabilityBOs = this.contextWiseBOs.get("PolicyRules");
        policyApplicabilityBOs.add(claim());
        policyApplicabilityBOs.add(policy());
        policyApplicabilityBOs.add(contract());

        Set<DomainType> contractApplicabilityBOs = this.contextWiseBOs
                .get("ContractApplicabilityRules");
        contractApplicabilityBOs.add(claim());
        contractApplicabilityBOs.add(policy());
        contractApplicabilityBOs.add(contract());

        Set<DomainType> entryValidationBOs = this.contextWiseBOs.get("EntryValidationRules");
        entryValidationBOs.add(claim());
        entryValidationBOs.add(policy());
        entryValidationBOs.add(contract());

        Set<DomainType> claimProcessorRoutingBOs = this.contextWiseBOs.get("ClaimProcessorRouting");
        claimProcessorRoutingBOs.add(claim());
        claimProcessorRoutingBOs.add(policy());
        claimProcessorRoutingBOs.add(contract());

        Set<DomainType> recClaimProcessorRoutingBOs = this.contextWiseBOs
                .get("recClaimProcessorRouting");
        recClaimProcessorRoutingBOs.add(claim());
        recClaimProcessorRoutingBOs.add(policy());
        recClaimProcessorRoutingBOs.add(contract());
    }

    protected Set<DomainType> prepareFullyFlattenedBOs() {
        Set<DomainType> fullyFlattenedBOs = null;
        for (String context : listAllContexts()) {
            fullyFlattenedBOs = this.contextWiseBOs.get(context + "-flattened");
            fullyFlattenedBOs.addAll(this.contextWiseBOs.get("ClaimRules"));
            // fullyFlattenedBOs.addAll(claimSearchProcessingBOs);
            fullyFlattenedBOs.add(inventoryItem());
            fullyFlattenedBOs.add(item());
            fullyFlattenedBOs.add(dealership());
            fullyFlattenedBOs.add(laborDetail());
            fullyFlattenedBOs.add(oemPartReplaced());
            fullyFlattenedBOs.add(nonOEMPartReplaced());
            fullyFlattenedBOs.add(travelDetail());

        }
        return fullyFlattenedBOs;
    }

    public void discoverPathsToFields(DomainType domainType, String expression) {
        if (this.fieldsInBO.containsKey(domainType)) {
            if (logger.isDebugEnabled()) {
                logger.debug(" field traversal paths for type [" + domainType + "] already build");
            }
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(" Building field traversal paths for type [" + domainType
                    + "] with expression [" + expression + "]");
        }

        SortedMap<String, FieldTraversal> fieldsInThisBO = this.fieldsInBO.get(domainType);

        // Recursively traverse the domain type hierarchy
        // 1. For simple fields, identify some unique key ( for now, the
        // property path )
        // 2. For one-to-many associations get the domain type of the many end
        // and repeat recursively.
        OneToOneAssociation field = new OneToOneAssociation(domainType.getDomainName(), expression,
                domainType);
        FieldTraversal root = new FieldTraversal();
        root.addFieldToPath(field);

        // fieldsInThisBO.put(root.getExpression(), root);

        for (Field aField : field.getFields()) {
            FieldTraversal path = new FieldTraversal(root);
            path.addFieldToPath(aField);

            if (logger.isDebugEnabled()) {
                logger.debug(" Adding nested field " + path + " of " + field.getDomainName());
            }

            fieldsInThisBO.put(path.getExpression(), path);

            // FIX-ME: This recursive build should happen for One-To-Ones as
            // well
            // FIX-ME: That can be done once the UI is sorted out.
            if (aField instanceof OneToManyAssociation) {
                OneToManyAssociation oneToMany = (OneToManyAssociation) aField;
                DomainType collectionElementType = oneToMany.getOfType();
                discoverPathsToFields(collectionElementType, "");
            } else if (aField instanceof OneToOneAssociation) {
                OneToOneAssociation oneToOne = (OneToOneAssociation) aField;
                DomainType entityElementType = oneToOne.getOfType();
                discoverPathsToFields(entityElementType, "");
            }
        }
    }

    void discoverPathsThruOneToOne(DomainType reachableFromDomainType,
            DomainType fieldsOfDomainType, FieldTraversal contextPath) {
        if (logger.isDebugEnabled()) {
            logger.debug(" Building field traversal paths for type [" + fieldsOfDomainType
                    + "] with expression [" + contextPath.getExpression() + "]");
        }

        boolean alreadyPopulated = this.fieldsInBO.containsKey(fieldsOfDomainType);
        SortedMap<String, FieldTraversal> fieldsInThisBO = this.fieldsInBO.get(fieldsOfDomainType);

        SortedMap<String, FieldTraversal> fieldsInThisBOViaParent = this.fieldsInBO
                .get(reachableFromDomainType);

        // Recursively traverse the domain type hierarchy
        // 1. For simple fields, identify some unique key ( for now, the
        // property path )
        // 2. For one-to-many associations get the domain type of the many end
        // and repeat recursively.
        OneToOneAssociation field = new OneToOneAssociation("", contextPath.getExpression(),
                fieldsOfDomainType);
        for (Field aField : field.getFields()) {
            FieldTraversal fieldPath = new FieldTraversal(contextPath);
            fieldPath.addFieldToPath(aField);

            if (logger.isDebugEnabled()) {
                logger.debug(" Adding nested field " + fieldPath + " of " + field.getDomainName());
            }

            // fieldsInThisBOViaParent.put(fieldPath.getExpression(),
            // fieldPath);

            // if (!alreadyPopulated) {
            fieldsInThisBO.put(fieldPath.getExpression(), fieldPath);
            // }

            // FIX-ME: This recursive build should happen for One-To-Ones as
            // well
            // FIX-ME: That can be done once the UI is sorted out.
            if (aField instanceof OneToManyAssociation) {
                OneToManyAssociation oneToMany = (OneToManyAssociation) aField;
                DomainType collectionElementType = oneToMany.getOfType();
                discoverPathsToFields(collectionElementType, "");
            } else if (aField instanceof OneToOneAssociation) {
                OneToOneAssociation oneToOne = (OneToOneAssociation) aField;
                discoverPathsThruOneToOne(reachableFromDomainType, oneToOne.getOfType(), fieldPath);
            }
        }
    }

    protected DomainType claim() {
        String typeName = Claim.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType claim = new DomainType(/* Domain term */"Claim", /*
                                                                         * Unique
                                                                         * Type
                                                                         * Name
                                                                         */typeName);
            // Simple Fields.
            claim.simpleField("label.common.causedby", "serviceInformation.causedBy.name", Type.STRING);
            claim.simpleField("label.claim.claimType", "type", Type.STRING);
            claim.simpleField("label.common.conditionFound", "conditionFound", Type.STRING);
            claim.simpleField("label.common.dateFailure", "failureDate", Type.DATE);
            claim.simpleField("label.common.dateInstall", "installationDate", Type.DATE);
            claim.simpleField("label.common.dateRepair", "repairDate", Type.DATE);
            claim.simpleField("label.common.repairStartDate", "repairStartDate", Type.DATE);
            claim.simpleField("label.common.faultFound", "serviceInformation.faultFound.name", Type.STRING);
            claim.simpleField("label.common.hoursInService", "hoursInService", Type.INTEGER);
            claim.simpleField("label.common.otherComments", "otherComments", Type.STRING);
            claim.simpleField("label.common.probableCause", "probableCause", Type.STRING);
            claim.simpleField("label.common.smrReason", "reasonForServiceManagerRequest", Type.STRING);
            claim.simpleField("label.common.smrRequest", "serviceManagerRequest", Type.STRING);
            claim.simpleField("label.common.workPerformed", "workPerformed", Type.STRING);

            // One To One Associations.
            claim.oneToOne("label.common.serviceProvider", "forDealer", dealership());
            claim
                    .oneToOne("label.inventory.inventoryItem", "itemReference.referredInventoryItem",
                            inventoryItem());
            claim.oneToOne("label.common.item", "itemReference.referredItem", item());
        	claim.oneToOne("label.common.incidentals","serviceInformation.serviceDetail",
					travelDetail());
            claim.oneToOne("label.failure.failureCode", "serviceInformation.faultCodeRef", failureCode());
            claim.oneToOne("label.claim.applicablePolicy", "applicablePolicy", policy());
            claim.oneToOne("label.common.causalPart", "serviceInformation.causalPart", item());
            claim.oneToOne("label.claim.claimPayment", "payment", payment());
            claim.oneToMany("label.inventory.oemPartsReplaced",
                    "serviceInformation.serviceDetail.oemPartsReplaced", oemPartReplaced());
            claim.oneToMany("label.newClaim.oEMPartReplacedInstalled",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts", oemReplacedParts());
            claim.oneToMany("label.bussinessUnit.clubCarPartsReplaced2",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts", oemInstalledParts());
            claim.oneToMany("label.recoveryClaim.outsidePartsReplaced",
                    "serviceInformation.serviceDetail.nonOEMPartsReplaced", nonOEMPartReplaced());
            claim.oneToMany("label.recoveryClaim.outsidePartsInstalled",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts", nonOEMInstalledParts());
            claim.oneToMany("label.recoveryClaim.serviceDetails", "serviceInformation.serviceDetail.laborPerformed",
                    laborDetail());

            claim
                    .functionField(
                            "label.inventory.totalOEMPartsClaimed",
                            "firstEntryIfAnyNullOtherwise({contextExpression}.payment.paymentComponents.{ ? forCategory.code==\"OEM_PARTS\"}).claimedAmount",
                            Type.MONEY, true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim
                    .functionField(
                            "label.inventory.totalNonOEMPartsClaimed",
                            "firstEntryIfAnyNullOtherwise({contextExpression}.payment.paymentComponents.{ ? forCategory.code==\"NON_OEM_PARTS\"}).claimedAmount",
                            Type.MONEY, true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim
                    .functionField(
                            "label.campaign.totalLaborClaimed",
                            "firstEntryIfAnyNullOtherwise({contextExpression}.payment.paymentComponents.{ ? forCategory.code==\"LABOR\"}).claimedAmount",
                            Type.MONEY, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
            claim
                    .functionField(
                            "label.campaign.totalTravelClaimed",
                            "firstEntryIfAnyNullOtherwise({contextExpression}.payment.paymentComponents.{ ? forCategory.code==\"TRAVEL\"}).claimedAmount",
                            Type.MONEY, true, FunctionField.Types.ONE_TO_ONE.getBaseType());
            claim.functionField("label.inventory.NumberofOEMPartsReplaced",
                    "serviceInformation.serviceDetail.oemPartsReplaced.size", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.bussinessUnit.numberofClubCarReplacedParts",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.replacedParts.size", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.inventory.NumberofOEMInstalledParts",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts.size", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.recoveryClaim.numberofOutsidePartsReplaced",
                    "serviceInformation.serviceDetail.nonOEMPartsReplaced.size", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.recoveryClaim.numberofOutsideReplacedParts",
                    "serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts.size", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.inventory.sumofOEMPartsReplacedQuantity",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.oemPartsReplaced.{numberOfUnits})", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField(" label.inventory.sumofOEMReplacedPartsQuantity",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.hussmanPartsReplacedInstalled.replacedParts.{numberOfUnits})", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField(" label.bussinessUnit.sumofClubCarInstalledPartsQuantity",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.hussmanPartsReplacedInstalled.hussmanInstalledParts.{numberOfUnits})", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.recoveryClaim.sumOfOutsidePartsReplacedQuantity",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.nonOEMPartsReplaced.{numberOfUnits})", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.recoveryClaim.sumOfOutsidePartsReplacedQuantity1",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.hussmanPartsReplacedInstalled.nonHussmanInstalledParts.{numberOfUnits})", Type.INTEGER,
                    true, FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.claim.numberOfServiceDetails",
                    "serviceInformation.serviceDetail.laborPerformed.size", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.claim.sumOfSuggestedLaborHours",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.laborPerformed.{serviceProcedure"
                            + ".suggestedLabourHours})", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());
            claim.functionField("label.claim.sumofAdditionalLaborHours",
                    "sumOfIntegers({contextExpression}.serviceInformation."
                            + "serviceDetail.laborPerformed." + "{additionalLaborHours})",
                    Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY.getBaseType());

            claim.functionField("label.laborType.laborSplit.count",
                    "serviceInformation.serviceDetail.laborSplit.size", Type.INTEGER, true,
                    FunctionField.Types.ONE_TO_MANY.getBaseType());

            this.domainTypeSystem.registerDomainType(claim);
            return claim;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    private DomainType failureCode() {
        String typeName = FaultCode.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType faultCode = new DomainType("Fault Code", typeName);
            faultCode.simpleField("label.failure.faultCodeString", "definition.code", Type.STRING);
            this.domainTypeSystem.registerDomainType(faultCode);
            return faultCode;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    private DomainType contract() {
        String typeName = Contract.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType policy = new DomainType("Contract", typeName);
            this.domainTypeSystem.registerDomainType(policy);
            return policy;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType policy() {
        String typeName = Policy.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType policy = new DomainType("Policy", typeName);

            policy.simpleField("label.common.hoursCovered",
                    "policyDefinition.coverageTerms.serviceHoursCovered", Type.INTEGER);
            policy.simpleField("label.common.itemCondition",
                    "policyDefinition.availability.itemCondition.itemCondition", Type.STRING);
            policy.simpleField("label.common.monthsCoveredFromDelivery",
                    "policyDefinition.coverageTerms.monthsCoveredFromDelivery", Type.INTEGER);
            policy.simpleField("label.shipment.monthsCoveredFromShipment",
                    "policyDefinition.coverageTerms.monthsCoveredFromShipment", Type.INTEGER);
            policy.simpleField("label.common.ownershipState",
                    "policyDefinition.availability.ownershipState.name", Type.STRING);
            policy.simpleField("label.managePolicy.code", "policyDefinition.code",
								Type.STRING);
						policy.simpleField("label.managePolicy.name", "policyDefinition.description",
					Type.STRING);
            policy.simpleField("label.policy.planPrice", "policyDefinition.availability.price", Type.MONEY);
            policy.simpleField("label.policy.transferFees", "policyDefinition.transferDetails.transferFee",
                    Type.MONEY);
            policy.simpleField("label.policy.transferable", "policyDefinition.transferDetails.transferable",
                    Type.BOOLEAN);
            policy.simpleField("label.warranty.warrantyType", "policyDefinition.warrantyType.type", Type.STRING);

            this.domainTypeSystem.registerDomainType(policy);
            return policy;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType payment() {
        String typeName = Payment.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType payment = new DomainType("Payment", typeName);
            payment.simpleField("label.claim.claimAmount", "claimedAmount", Type.MONEY);
            payment.simpleField("label.claim.totalClaimAmount", "totalAmount", Type.MONEY);
            this.domainTypeSystem.registerDomainType(payment);
            return payment;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType travelDetail() {
        String typeName = ServiceDetail.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {

        	DomainType serviceDetail = new DomainType("Incidentals", typeName);
            serviceDetail.simpleField("label.newClaim.perDiem", "perDiem", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.rentalCharges", "rentalCharges", Type.MONEY);
            serviceDetail.simpleField("label.newClaim.parkingToll", "parkingAndTollExpense", Type.MONEY);
            serviceDetail.simpleField("label.campaign.travelDistancemiles", "travelDetails.distance", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelHours", "travelDetails.hours", Type.INTEGER);
            serviceDetail.simpleField("label.campaign.travelLocation", "travelDetails.location", Type.STRING);
            serviceDetail.simpleField("label.campaign.travelTrips", "travelDetails.trips", Type.INTEGER);
            serviceDetail.simpleField("label.newClaim.additionalTravelHrs", "travelDetails.additionalHours", Type.INTEGER);

            this.domainTypeSystem.registerDomainType(serviceDetail);
            return serviceDetail;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType laborDetail() {
        String typeName = LaborDetail.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType laborDetail = new DomainType("Service Details", typeName);
            laborDetail.simpleField("label.common.additionalLaborHours", "additionalLaborHours", Type.INTEGER);
            laborDetail.simpleField("label.common.additionalLaborReason", "reasonForAdditionalHours",
                    Type.STRING);
            laborDetail.simpleField("label.campaign.suggestedLaborHours",
                    "serviceProcedure.suggestedLabourHours", Type.INTEGER);

            laborDetail.oneToOne("label.common.jobCode", "serviceProcedure", serviceProcedure());
            this.domainTypeSystem.registerDomainType(laborDetail);
            return laborDetail;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    private DomainType serviceProcedure() {
        String typeName = ServiceProcedure.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType jobCode = new DomainType("Job Code", typeName);
            jobCode.simpleField("label.common.jobCodeString", "definition.code", Type.STRING);
            this.domainTypeSystem.registerDomainType(jobCode);
            return jobCode;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    private DomainType job() {
        String typeName = Job.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType job = new DomainType("Job Performed", typeName);
            job.simpleField("label.campaign.standardLaborHours", "definition.hoursSpent", Type.INTEGER);
            this.domainTypeSystem.registerDomainType(job);
            return job;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    private DomainType jobDefinition() {
        String typeName = JobDefinition.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType jobDefinition = new DomainType("Job Definition", typeName);
            jobDefinition.simpleField("label.campaign.standardLaborHours", "hoursSpent", Type.INTEGER);
            this.domainTypeSystem.registerDomainType(jobDefinition);
            return jobDefinition;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType nonOEMPartReplaced() {
        String typeName = NonOEMPartReplaced.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType nonOEMPartReplaced = new DomainType("Outside Part Replaced", typeName);
            nonOEMPartReplaced.simpleField("label.common.description", "description", Type.STRING);
            nonOEMPartReplaced.simpleField("label.common.itemNumber", "number", Type.STRING);
            nonOEMPartReplaced.simpleField("label.common.quantity", "pricePerUnit", Type.MONEY);
            nonOEMPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit", Type.MONEY);
            nonOEMPartReplaced.simpleField("label.recoveryClaim.numberofOutsidePartsReplaced", "numberOfUnits",
                    Type.INTEGER);
            this.domainTypeSystem.registerDomainType(nonOEMPartReplaced);
            return nonOEMPartReplaced;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType nonOEMInstalledParts() {
        String typeName = InstalledParts.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType nonOEMPartReplaced = new DomainType("Outside Parts Installed", typeName);
            nonOEMPartReplaced.simpleField("label.common.description", "description", Type.STRING);
            nonOEMPartReplaced.simpleField("label.common.itemNumber", "number", Type.STRING);
            nonOEMPartReplaced.simpleField("label.common.quantity", "pricePerUnit", Type.MONEY);
            nonOEMPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit", Type.MONEY);
            nonOEMPartReplaced.simpleField("label.recoveryClaim.numberofOutsidePartsReplaced", "numberOfUnits",
                    Type.INTEGER);
            this.domainTypeSystem.registerDomainType(nonOEMPartReplaced);
            return nonOEMPartReplaced;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType oemPartReplaced() {
        String typeName = OEMPartReplaced.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType oemPartReplaced = new DomainType("OEM Part Replaced", typeName);
            oemPartReplaced.simpleField("label.common.dueDate", "activePartReturn.dueDate", Type.DATE);
            oemPartReplaced.simpleField("label.campaign.paymentCondition",
                    "activePartReturn.paymentCondition.code", Type.STRING);
            oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits", Type.INTEGER);
            oemPartReplaced.simpleField("label.common.returnlocation", "activePartReturn.warehouseLocation",
                    Type.STRING);
            oemPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit", Type.MONEY);
            oemPartReplaced.oneToOne("label.common.partReplaced", "itemReference.referredItem", item());

            this.domainTypeSystem.registerDomainType(oemPartReplaced);
            return oemPartReplaced;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType oemReplacedParts() {
        String typeName = OEMPartReplaced.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType oemPartReplaced = new DomainType("OEM Replaced Parts", typeName);
            oemPartReplaced.simpleField("label.common.dueDate", "activePartReturn.dueDate", Type.DATE);
            oemPartReplaced.simpleField("label.campaign.paymentCondition",
                    "activePartReturn.paymentCondition.code", Type.STRING);
            oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits", Type.INTEGER);
            oemPartReplaced.simpleField("label.common.returnlocation", "activePartReturn.warehouseLocation",
                    Type.STRING);
            oemPartReplaced.oneToOne("label.common.partReplaced", "itemReference.referredItem", item());

            this.domainTypeSystem.registerDomainType(oemPartReplaced);
            return oemPartReplaced;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType oemInstalledParts() {
        String typeName = InstalledParts.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType oemPartReplaced = new DomainType("OEM Installed Parts", typeName);
            oemPartReplaced.simpleField("label.common.quantity", "numberOfUnits", Type.INTEGER);
            oemPartReplaced.simpleField("label.inventory.unitPrice", "pricePerUnit", Type.MONEY);
            oemPartReplaced.oneToOne("label.common.partReplaced", "itemReference.referredItem", item());

            this.domainTypeSystem.registerDomainType(oemPartReplaced);
            return oemPartReplaced;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

    protected DomainType inventoryItem() {
        String typeName = InventoryItem.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType inventoryItem = new DomainType(
            /* Domain term */"Inventory Item",
            /** Unique Type Name */
            typeName);
            inventoryItem.simpleField("label.common.dateOfDelivery", "deliveryDate", Type.DATE);
            inventoryItem.simpleField("label.inventory.dateOfShipment"/* Domain name */,
                    "shipmentDate"/* field name, also the property expression */, Type.DATE /*
                                                                                             * the
                                                                                             * type
                                                                                             * name
                                                                                             * of
                                                                                             * the
                                                                                             * field,
                                                                                             * either
                                                                                             * a
                                                                                             * build-in
                                                                                             * type
                                                                                             * or a
                                                                                             * domain
                                                                                             * type
                                                                                             * for
                                                                                             * one-to-one
                                                                                             * and
                                                                                             * one-to-many
                                                                                             * associations
                                                                                             */);
            inventoryItem.simpleField("label.common.serialNumber", "serialNumber", Type.STRING);
            inventoryItem.simpleField("label.common.registrationDate", "registrationDate", Type.DATE);
            this.domainTypeSystem.registerDomainType(inventoryItem);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

    protected DomainType inventoryTransaction() {
        String typeName = InventoryTransaction.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType inventoryTransaction = new DomainType(
                    /* Domain term */"Inventory Transaction", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */
                    typeName);
            inventoryTransaction.simpleField("label.warrantyAdmin.transactionDate", "transactionDate", Type.DATE);
            inventoryTransaction.oneToOne("label.common.seller", "seller", party());
            inventoryTransaction.oneToOne("label.common.buyer", "buyer", party());
            inventoryTransaction
                    .simpleField("label.inventory.salesOrderNumber", "salesOrderNumber", Type.INTEGER);
            inventoryTransaction.simpleField("label.inventory.invoiceDate", "invoiceDate", Type.DATE);
            // Avoid back-pointer to inventory item as it introduces cyclic call
            // sequences in this class.
            // An error like this would result in stack-overflow at runtime.
            this.domainTypeSystem.registerDomainType(inventoryTransaction);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

    protected DomainType item() {
        String typeName = Item.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType item = new DomainType(/* Domain term */typeName, /*
                                                                         * Unique
                                                                         * Type
                                                                         * Name
                                                                         */typeName);
            item.simpleField("label.common.itemDescription", "description", Type.STRING);
            item.simpleField("label.common.itemNumber", "number", Type.STRING);
            item.simpleField("label.common.model", "model", Type.STRING);
            item.simpleField("label.common.product", "make", Type.STRING);
            this.domainTypeSystem.registerDomainType(item);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

    private DomainType party() {
        String typeName = Party.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType party = new DomainType(/* Domain term */"Party", /*
                                                                         * Unique
                                                                         * Type
                                                                         * Name
                                                                         */typeName);
            party.simpleField("label.common.name", "name", Type.STRING);
            this.domainTypeSystem.registerDomainType(party);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

    protected DomainType dealership() {
        String typeName = ServiceProvider.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType dealership = new DomainType(/* Domain term */"Dealer", /*
                                                                                 * Unique
                                                                                 * Type
                                                                                 * Name
                                                                                 */typeName);
            dealership.simpleField("label.common.name", "name", Type.STRING);
            dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
            dealership.simpleField("label.common.preferredCurrency", "preferredCurrency.code", Type.STRING);
            this.domainTypeSystem.registerDomainType(dealership);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

    private static Logger logger = LogManager.getLogger(BusinessObjectModel.class);

    private static BusinessObjectModel _instance = new BusinessObjectModel();

    protected DomainTypeSystem domainTypeSystem;

    private static class NonNullValueTreeMap extends TreeMap<String, Set<DomainType>> {
        @Override
        public Set<DomainType> get(Object key) {
            Set<DomainType> value = super.get(key);
            if (value == null) {
                value = new HashSet<DomainType>();
                put((String) key, value);
            }
            return value;
        }
    }

    protected Map<DomainType, String> expressionsForTopLevelBOs = new HashMap<DomainType, String>();

    SortedMap<String, Class> typeNameToClass = new TreeMap<String, Class>();

    protected SortedMap<String, Set<DomainType>> contextWiseBOs = new NonNullValueTreeMap();

    public Map<DomainType, SortedMap<String, FieldTraversal>> fieldsInBO = new HashMap<DomainType, SortedMap<String, FieldTraversal>>() {
        @Override
        public SortedMap<String, FieldTraversal> get(Object key) {
            SortedMap<String, FieldTraversal> value = super.get(key);
            if (value == null) {
                value = new TreeMap<String, FieldTraversal>();
                put((DomainType) key, value);
            }
            return value;
        }

    };

    public DomainTypeSystem getDomainTypeSystem() {
        return this.domainTypeSystem;
    }

    public void setDomainTypeSystem(DomainTypeSystem domainTypeSystem) {
        this.domainTypeSystem = domainTypeSystem;
    }

}
