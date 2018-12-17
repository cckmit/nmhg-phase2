/*Copyright (c)2006 Tavant Technologies
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

package tavant.twms.domain.claim;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.MiscellaneousItemConfiguration;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.LineItem;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.customReports.ReportI18NText;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.laborType.LaborSplit;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.partreturn.PartReturnAudit;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.infra.xstream.XMLBeanConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for converting claim object to xml and vice-versa.
 * 
 * @author roopali.agrawal
 * 
 */
public class ClaimXMLConverter extends XMLBeanConverter {
    // todo-Populate this map using spring
    private Map<Class, String[]> customFieldsToOmit = new HashMap<Class, String[]>();

    private Map<Class, String[]> customFieldsToAlias = new HashMap<Class, String[]>();

    public ClaimXMLConverter() {
        this.customFieldsToOmit.put(Claim.class, new String[] { "claimAudits", "claimXMLConverter","orgService",
                "securityHelper", "eventService", "foc","userProcessComments","recoveryClaims","allowedActionsList","laborRoundupWindow","recoveryInfo","loaScheme",
                "payment", "btsLaborHrs"});
        this.customFieldsToOmit.put(InventoryItem.class,
                new String[] { "warrantyHistory","transactionHistory","reportAnswers","composedOf","inventoryItemAttrVals","operator","installingDealer","oem"});
        this.customFieldsToOmit.put(ClaimedItem.class, new String[] { "claim","d"});
        this.customFieldsToOmit.put(FaultCode.class, new String[] { "treadBucket","d"});
        this.customFieldsToOmit.put(PolicyDefinition.class,
                new String[] { "applicabilityTerms","warrantyType","availability",
                               "transferDetails","i18NPolicyTermsAndConditions","policyFees",
                               "customerTypes","coverageTerms","labels","d","policyDefinitionAudits","applicableServiceProviders","applicableDealerGroups","blackListedServiceProviders"});
        this.customFieldsToOmit.put(RegisteredPolicy.class,
                new String[] { "price","policyAudits","warranty","amount","d"});
        this.customFieldsToOmit.put(ServiceInformation.class, new String[] { "contract","d", "customReportAnswer"});
        this.customFieldsToOmit.put(PartReplaced.class, new String[] {"d"});
        this.customFieldsToOmit.put(FaultCodeDefinition.class, new String[] { "labels","d","partClasses"});
        this.customFieldsToOmit.put(AssemblyDefinition.class, new String[] { "assemblyLevel","d"});
        this.customFieldsToOmit.put(FailureTypeDefinition.class, new String[] { "d"});
        this.customFieldsToOmit.put(FailureCauseDefinition.class, new String[] { "d"});
        this.customFieldsToOmit.put(FailureRootCauseDefinition.class, new String[] { "d"});
        this.customFieldsToOmit.put(OEMPartReplaced.class, new String[] { "shipment","supplierPartReturn","partReturns","partReturns","supplierShipment","partReturnConfiguration","recoverableParts", "customReportAnswer", "supplierReturnNeeded","batteryInfo"});
        this.customFieldsToOmit.put(Payment.class, new String[] {"d"});
        this.customFieldsToOmit.put(LineItemGroup.class, new String[] { "currentPartPaymentInfo","d"});
        this.customFieldsToOmit.put(CreditMemo.class, new String[] { "recoveryClaim","d"});
        this.customFieldsToOmit.put(RuleFailure.class, new String[] { "d"});
        this.customFieldsToOmit.put(LaborDetail.class, new String[] { "jobPerformed","d"});
        this.customFieldsToOmit.put(ServiceProcedure.class, new String[] {"d","definedFor"});
        this.customFieldsToOmit.put(ServiceProcedureDefinition.class, new String[] { "labels","childJobs","d"});
        this.customFieldsToOmit.put(LaborSplit.class, new String[] { "serviceDetail"});
        this.customFieldsToOmit.put(TravelDetail.class, new String[] { "d"});
        this.customFieldsToOmit.put(ServiceDetail.class, new String[] {"d", "inactiveLaborDetails"});
        this.customFieldsToOmit.put(ActionDefinition.class, new String[] {"d"});
        this.customFieldsToOmit.put(Address.class, new String[] {"d", "siteNumber", "location"});
        this.customFieldsToOmit.put(LineItem.class, new String[] {"d"});
        this.customFieldsToOmit.put(AdditionalAttributes.class,new String[] {"attributeAssociations"} );
        this.customFieldsToOmit.put(UomMappings.class, new String[] {"d"});
        this.customFieldsToOmit.put(MiscellaneousItemConfiguration.class, new String[] {"miscellaneousItem"});
        this.customFieldsToOmit.put(ItemGroup.class, new String[] {"failureStructures","isPartOf","consistsOf","labels","includedItems","scheme","nodeInfo","d"});        
        this.customFieldsToOmit.put(MatchReadInfo.class, new String[] {"d"});
        this.customFieldsToOmit.put(Campaign.class,  new String[] {"campaignCoverage","oemPartsToReplace","nonOEMpartsToReplace","hussPartsToReplace","campaignServiceDetail","attachments","d","relatedCampaign"});
        this.customFieldsToOmit.put(PaymentVariable.class, new String[] {"d","section"});
        this.customFieldsToOmit.put(SourceWarehouse.class, new String[] {"d"});
        this.customFieldsToOmit.put(PartReturnAudit.class, new String[] {"forPartReplaced"});
        this.customFieldsToOmit.put(AlarmCode.class, new String[] {"applicableProducts","d"});
        this.customFieldsToOmit.put(ReportI18NText.class,new String[] {"d"});

        //the array should have only 2 elements. 1st is the "from name" and 2nd is the "to name"
        this.customFieldsToAlias.put(Claim.class, new String[] {"equipmentItemReference", "partItemReference"});
    }

    @Override
    public Map<Class, String[]> getCustomFieldsToOmit() {
        return this.customFieldsToOmit;
    }

    public void setCustomFieldsToOmit(Map<Class, String[]> customFieldsToOmit) {
        this.customFieldsToOmit = customFieldsToOmit;
    }

    @Override
    public Map<Class, String[]> getCustomFieldsToAlias() {
        return customFieldsToAlias;
    }

    public void setCustomFieldsToAlias(Map<Class, String[]> customFieldsToAlias) {
        this.customFieldsToAlias = customFieldsToAlias;
    }
}
