/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.rules.BusinessObjectModel;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.FieldTraversal;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.Type;
/**
 *
 * @author roopali.agrawal
 *
 */
@Deprecated
public class SearchBusinessObjectModel extends BusinessObjectModel{
	private static SearchBusinessObjectModel instance=new SearchBusinessObjectModel();

	public static SearchBusinessObjectModel getInstance() {
        return instance;
    }



	@Override
	protected void prepareTopLevelBOs() {

        Set<DomainType> claimSearchProcessingBOs = this.contextWiseBOs.get("ClaimSearches");
        claimSearchProcessingBOs.add(claim());


	}

	@Override
    protected DomainType claim() {
        String typeName = Claim.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType claim = new DomainType(/* Domain term */"Claim", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            // Simple Fields.
            claim.simpleField("label.common.causedby", "activeClaimAudit.serviceInformation.causedBy",
                    Type.STRING);
            claim.simpleField("label.claim.claimType", "type", Type.STRING);
            claim.simpleField("label.common.conditionFound", "activeClaimAudit.conditionFound", Type.STRING);
            claim.simpleField("label.common.dateFailure", "activeClaimAudit.failureDate", Type.DATE);
            claim.simpleField("label.common.dateInstall", "activeClaimAudit.installationDate",
                    Type.DATE);
            claim.simpleField("label.common.dateRepair", "activeClaimAudit.repairDate", Type.DATE);
            claim.simpleField("label.common.repairStartDate", "activeClaimAudit.repairStartDate", Type.DATE);
            claim.simpleField("label.failure.failureCode", "activeClaimAudit.serviceInformation.faultCode",
                    Type.STRING);
            claim.simpleField("label.common.faultFound", "activeClaimAudit.serviceInformation.faultFound",
                    Type.STRING);
            claim.simpleField("label.common.hoursInService", "hoursInService", Type.INTEGER);
            claim.simpleField("label.common.otherComments", "activeClaimAudit.otherComments", Type.STRING);
            claim.simpleField("label.common.probableCause", "activeClaimAudit.probableCause", Type.STRING);
            claim.simpleField("label.common.smrReason", "activeClaimAudit.reasonForServiceManagerRequest",
                    Type.STRING);
            claim.simpleField("label.common.smrRequest", "serviceManagerRequest",
                    Type.STRING);
            claim.simpleField("label.common.workPerformed", "activeClaimAudit.workPerformed", Type.STRING);

            // One To One Associations.
            claim.oneToOne("label.common.serviceProvider", "forDealer", dealership());
            claim.oneToOne("label.inventory.inventoryItem",
                    "itemReference.referredInventoryItem", inventoryItem());
            claim.oneToOne("label.common.item", "itemReference.referredItem", item());
            claim.oneToOne("label.common.incidentals",
                    "activeClaimAudit.serviceInformation.serviceDetail.travelDetails",
                    travelDetail());
            claim.oneToOne("label.claim.applicablePolicy", "applicablePolicy", policy());
            claim.oneToOne("label.common.causalPart", "activeClaimAudit.serviceInformation.causalPart", item());
            //claim.oneToOne("Claim Payment", "payment", payment());
            claim.oneToMany("label.inventory.oemPartsReplaced",
                    "activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced",
                    oemPartReplaced());
            claim.oneToMany("label.recoveryClaim.outsidePartsReplaced",
                    "activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced",
                    nonOEMPartReplaced());
            claim.oneToMany("label.recoveryClaim.serviceDetails",
                    "activeClaimAudit.serviceInformation.serviceDetail.laborPerformed",
                    laborDetail());
            claim.queryTemplate("label.inventory.oemPartsAmount","{alias}.name='OEM Parts' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.inventory.totalOEMPartsAmount","{alias}.name='OEM Parts' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.common.labor","{alias}.name='Labor' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.common.totalLabor","{alias}.name='Labor' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.common.travel","{alias}.name='Travel' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.campaign.totalTravelClaimed","{alias}.name='Travel' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.inventory.itemFreightDuty","{alias}.name='Item Freight Duty' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.inventory.totalItemFreightDuty","{alias}.name='Item Freight Duty' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.campaign.meals","{alias}.name='Meals' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.campaign.totalMeals","{alias}.name='Meals' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.claim.claimAmount","{alias}.name='Claim Amount' and {alias}.groupTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");
            claim.queryTemplate("label.claim.totalClaimAmount","{alias}.name='Claim Amount' and {alias}.acceptedTotal.amount",
                    Type.BIGDECIMAL, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.payment.lineItemGroups","{alias}");

            claim.queryTemplate("label.inventory.sumofOEMPartsReplacedQuantity","size(claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced)",
                    Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),null,null);

            claim.queryTemplate("label.inventory.sumofNONOEMPartsReplacedQuantity","size(claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced)",
                    Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),null,null);

            claim.queryTemplateForAggregateFunctions("label.claim.sumofAdditionalLaborHours","sum({alias}.additionalLaborHours)",
                    Type.LONG, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.serviceInformation.serviceDetail.laborPerformed","claim.id","{alias}");

            /*claim.queryTemplateForAggregateFunctions("Sum of Suggested Labor Hours","sum({alias}.serviceProcedure.suggestedLabourHours)",
                    Type.LONG, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
                    "claim.serviceInformation.serviceDetail.laborPerformed","claim.id","{alias}");
*/
            claim.queryTemplateForAggregateFunctions("label.inventory.NumberofOEMPartsReplaced","sum({alias}.numberOfUnits)",
                    Type.LONG, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced","claim.id","{alias}");

            claim.queryTemplateForAggregateFunctions("label.inventory.numberofNONOEMPartReplaced","sum({nonoemalias}.numberOfUnits)",
                    Type.LONG, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),"claim.activeClaimAudit.serviceInformation.serviceDetail.nonOEMPartsReplaced","claim.id","{nonoemalias}");

            this.domainTypeSystem.registerDomainType(claim);
            return claim;
        } else {
            return this.domainTypeSystem.getDomainType(typeName);
        }
    }

	protected DomainType inventoryItem() {
        String typeName = InventoryItem.class.getSimpleName();
        if (!this.domainTypeSystem.isKnown(typeName)) {
            DomainType inventoryItem = new DomainType(
                    /* Domain term */ "Inventory Item",
                    /** Unique Type Name */typeName);
            inventoryItem.simpleField("label.common.dateOfDelivery", "deliveryDate",
                    Type.DATE);
            inventoryItem.simpleField("label.inventory.dateOfShipment"/* Domain name */,
                    "shipmentDate"/* field name, also the property expression */,
                    Type.DATE /* the type name of the field, either a build-in
                             * type or a domain type for one-to-one and
                             * one-to-many associations
                             */);
            inventoryItem.simpleField("label.common.serialNumber", "serialNumber",
                    Type.STRING);
            inventoryItem.simpleField("label.common.registrationDate", "registrationDate",
                    Type.DATE);
            inventoryItem.simpleField("label.common.usage", "hoursOnMachine", Type.INTEGER);
            this.domainTypeSystem.registerDomainType(inventoryItem);
        }
        return this.domainTypeSystem.getDomainType(typeName);
    }

	protected Set<DomainType> prepareFullyFlattenedBOs() {
		Set<DomainType> fullyFlattenedBOs=null;
		for (String context : listAllContexts()) {
            fullyFlattenedBOs =
                    this.contextWiseBOs.get("ClaimSearches" + "-flattened");
            fullyFlattenedBOs.addAll(this.contextWiseBOs.get("ClaimSearches"));
            //fullyFlattenedBOs.addAll(claimSearchProcessingBOs);
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


	public static void main(String[] args){
		String context="InventoryItemSearches";
		SearchBusinessObjectModel bom=SearchBusinessObjectModel.getInstance();

		SortedMap<String, FieldTraversal> topLevelDataElementsForRule = bom.getTopLevelDataElementsForRule(context);
        List<DomainSpecificVariable> listOfFields = new ArrayList<DomainSpecificVariable>();

        List<FieldTraversal> sortedFields = new ArrayList<FieldTraversal>(
                topLevelDataElementsForRule.values());
        for(FieldTraversal ft:sortedFields){
        	System.out.println("Field Traversal "+ft);
        }
        //ruleJSONSerializer.sortFieldTraversalsByType(sortedFields);

        SortedMap<String, FieldTraversal> allLevelDataElementsForRule =
                bom.getAllLevelDataElementsForRule(context);
        List<DomainSpecificVariable> listOfFieldsForAll =
                new ArrayList<DomainSpecificVariable>();
        for (Map.Entry<String, FieldTraversal> entry : allLevelDataElementsForRule.entrySet()) {
        	//System.out.println(entry.getValue());
            listOfFieldsForAll.add(entry.getValue().getDomainSpecificVariable());
        }



	}

}
