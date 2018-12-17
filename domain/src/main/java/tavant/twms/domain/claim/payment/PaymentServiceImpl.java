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
package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.PaymentVariableLevel;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.policy.Policy;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;

public class PaymentServiceImpl implements PaymentService {

    private static Logger logger = Logger.getLogger(PaymentServiceImpl.class);

    private ConfigParamService configParamService;

    private CostCategoryRepository costCategoryRepository;

    private PaymentDefinitionAdminService paymentDefinitionAdminService;

    private Map<String, PaymentComponentComputer> paymentComputers = new HashMap<String, PaymentComponentComputer>();

    public CostCategory findCostCategoryByCode(String categoryCode) {
        return costCategoryRepository.findCostCategoryByCode(categoryCode);
    }

    public List<CostCategory> findAllCostCategories() {
        return costCategoryRepository.findAllCostCategories();
    }

    public Payment calculatePaymentForClaim(Claim claim,Money deductable ) throws PaymentCalculationException {
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            Payment payment = claim.getPayment();
            if (payment == null) {
                payment = new Payment();
                claim.setPayment(payment);
            }
            if(claim.getStateMandate()!=null)
            {
            	payment.setStateMandateActive(true);
            }
            else
            {
            	payment.setStateMandateActive(false);
            }
            List<ClaimedItem> claimedItems = claim.getClaimedItems();
            claim.getPayment().setDeductibleAmount(deductable);

            if (claimedItems.size() > 0) {

                ClaimedItem claimedItem = claimedItems.get(0);

                Policy applicablePolicy = claimedItem.getApplicablePolicy();
                // This is to check if the claim is non serialized then the default payment section is considered.
                // Hence we are putting a check to see if is is serialized or not.
                // If it not serialized then set the applicable policy to null.
                if (!claimedItem.getItemReference().isSerialized()
                        && claimedItem.getItemReference().getModel() != null) {
                    calculatePaymentForClaimedItem(claim, payment, null);
                } else {
                    calculatePaymentForClaimedItem(claim, payment, applicablePolicy);
                }
            }
            return payment;
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }

    public Payment calculatePaymentForDeniedClaim(Claim claim) throws PaymentCalculationException {
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            Payment payment = claim.getPayment();
            payment.clear(GlobalConfiguration.getInstance().zeroInBaseCurrency());
            //this.computePaymentWithZeroAmountForEachParts(claim);
            claim.setDisbursedAmount();           
            return payment;
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }
    
    public Payment calculatePaymentForWarrantyOrderClaim(Claim claim) throws PaymentCalculationException {
        try {
            GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
            Payment payment = claim.getPayment();
            payment.clear(GlobalConfiguration.getInstance().zeroInBaseCurrency());
            //this.computePaymentWithZeroAmountForEachParts(claim);
            claim.setDisbursedAmount();            
            return payment;
        } finally {
            GlobalConfiguration.getInstance().removeClaimCurrency();
        }
    }
    
/*    //To set 0 if claim is deny or if claim is warranty order clam
    public void computePaymentWithZeroAmountForEachParts(Claim theClaim)
    {      
    	List<LineItemGroup> lineItemGroups=theClaim.getPayment().getLineItemGroups();
    	BigDecimal accepteddHrs=BigDecimal.ZERO;
    	Integer acceptedQty=new Integer(0);
    	for(LineItemGroup lineItemGroup:lineItemGroups)
    	{
    		for(IndividualLineItem individualLineItem:lineItemGroup.getIndividualLineItems())
    		{
    			individualLineItem.setAcceptedAmount(lineItemGroup.getGroupTotal());
    			individualLineItem.setPercentageAcceptance(BigDecimal.ZERO);
    			//individualLineItem.setAcceptedHrs(accepteddHrs);
    			//individualLineItem.setAcceptedQty(acceptedQty);
    			individualLineItem.setBaseAmount(lineItemGroup.getBaseAmount());
    			individualLineItem.setStateMandateAmount(lineItemGroup.getBaseAmount());
    		}
    		//OemModifier percentage and total amount calculation
    		for(LineItem oemModifier:lineItemGroup.getModifiers())			{				
    			oemModifier.setAcceptedCost(lineItemGroup.getGroupTotal());	
    			oemModifier.setPercentageAcceptance(BigDecimal.ZERO);		
    			oemModifier.setStateMandateAmount(lineItemGroup.getGroupTotal());
    			oemModifier.setSMandateModifierPercent(lineItemGroup.getPercentageAcceptance().doubleValue());		
    		}				
    		lineItemGroup.setGroupTotalStateMandateAmount(lineItemGroup.getBaseAmount());	
    		//lineItemGroup.setAcceptedQtyHrs(accepteddHrs.toString());
    		lineItemGroup.setPercentageAcceptance(BigDecimal.ZERO);
    		if(lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
    		{
    			lineItemGroup.setAcceptedTotalFrCp(lineItemGroup.getBaseAmount());
    			lineItemGroup.setAcceptedTotalFrWnty(lineItemGroup.getBaseAmount());    			
    		}
    	} 	
    }*/

    protected void calculatePaymentForClaimedItem(Claim claim, Payment payment, Policy applicablePolicy)
            throws PaymentCalculationException {
        PaymentDefinition paymentDefinition = getPaymentDefinition(claim, applicablePolicy);
        if (logger.isInfoEnabled()) {
            logger.info("Payment definition " + paymentDefinition.getId()
                    + " used for claim " + claim.getId());
        }
        if (paymentDefinition != null) {
            Map<String, CostCategory> costCategoryMap = getEligibleCostCategories(claim);
            if (logger.isInfoEnabled()) {
                logger.info("For claim " + claim.getId() + " payment : " + payment.getClaimedAmount());
            }
            payment.resetLineItemGroups(paymentDefinition);
            // Calculates the amount associated to each line item group along with the modifiers
            for (PaymentSection paymentSection : paymentDefinition.getPaymentSections()) {
                String sectionName = paymentSection.getSection().getName();
                PaymentContext paymentContext = new PaymentContext(claim, paymentDefinition, applicablePolicy, sectionName);
                if (costCategoryMap.get(sectionName) != null || sectionName.equals(Section.TOTAL_CLAIM)) {
                     paymentComputers.get(sectionName).compute(paymentContext);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Added LineItemGroup: " + sectionName + "for Claim" + claim);
                    }
                }
            }
            if(claim.isLateFeeEnabledFrom61to90days() || claim.isLateFeeEnabledFrom91to120days()){
            	PaymentContext paymentContext = new PaymentContext(claim, paymentDefinition, applicablePolicy, Section.LATE_FEE);
            	 paymentComputers.get(Section.LATE_FEE).compute(paymentContext);
            }
        } else {
            payment.setTotalAmount(GlobalConfiguration.getInstance().zeroInBaseCurrency());
        }
    }

    public LineItemGroup computeSummationSectionForDisplay(Claim claim) {
        LineItemGroup claimedAmountAudit = claim.getPayment().getLineItemGroup(Section.TOTAL_CLAIM);
        if (claimedAmountAudit == null) {
            claimedAmountAudit = claim.getPayment().addLineItemGroup(Section.TOTAL_CLAIM);
        }
        PaymentDefinition paymentDefinition = getPaymentDefinition(claim, claim.getApplicablePolicy());
        PaymentSection section = paymentDefinition.getSectionForName(Section.TOTAL_CLAIM);
        LineItemGroup displayAudit = new LineItemGroup();
        displayAudit.setBaseAmount(claim.getPayment().getLineItemGroupsTotal());
        Money previousLevelAmount = displayAudit.getBaseAmount();
        if (previousLevelAmount.breachEncapsulationOfAmount().floatValue() != new BigDecimal(0).floatValue()) {
            Map<Integer, List<PaymentVariableLevel>> paymentVariablesForLevels = section.getPaymentVariablesForLevels();
            for (Integer level : paymentVariablesForLevels.keySet()) {
                Money currentLevelModifiedAmt = Money.valueOf(0, claim.getCurrencyForCalculation());
                for (PaymentVariableLevel paymentVariableLevel : paymentVariablesForLevels.get(level)) {
                    PaymentVariable paymentVariable = paymentVariableLevel.getPaymentVariable();
                    for (LineItem modifier : claimedAmountAudit.getModifiers()) {
                        if (modifier.getPaymentVariable().equals(paymentVariable)) {
                            LineItem newModifier = new LineItem();
                            Money modifiedAmt;
                            if (modifier.getIsFlatRate()) {
                                modifiedAmt = Money.valueOf(modifier.getModifierPercentage(), claim.getCurrencyForCalculation());
                            } else {
                                modifiedAmt = previousLevelAmount.times(
                                        modifier.getModifierPercentage()).dividedBy(100);
                            }
                            currentLevelModifiedAmt = currentLevelModifiedAmt.plus(modifiedAmt);
                            newModifier.setValue(currentLevelModifiedAmt);
                            newModifier.setName(modifier.getName());
                            newModifier.setModifierPercentage(modifier.getModifierPercentage());
                            newModifier.setIsFlatRate(modifier.getIsFlatRate());
                            displayAudit.getModifiers().add(newModifier);
                            if (displayAudit.getModifierAmount() == null) {
                                displayAudit.setModifierAmount(Money.valueOf(0, claim.getCurrencyForCalculation()));
                            }
                            displayAudit.setModifierAmount(displayAudit.getModifierAmount()
                                    .plus(currentLevelModifiedAmt));
                        }
                    }

                }

                previousLevelAmount = previousLevelAmount.plus(currentLevelModifiedAmt);
            }
        }
        displayAudit.setGroupTotal();
        return displayAudit;
    }

    private PaymentDefinition getPaymentDefinition(Claim claim, Policy applicablePolicy) {
        return paymentDefinitionAdminService.findBestPaymentDefinition(claim, applicablePolicy);
    }

    public Map<String, CostCategory> getEligibleCostCategories(Claim claim) {
        Map<String, CostCategory> allCostCategoriesMap = new HashMap<String, CostCategory>();
        Map<String, CostCategory> costCategoriesMapToReturn = new HashMap<String, CostCategory>();
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        // Set all the cost categories in a map
        List<CostCategory> allCostCategories = costCategoryRepository.findAllCostCategories();
        for (CostCategory costCategory : allCostCategories) {
            allCostCategoriesMap.put(costCategory.getName(), costCategory);
        }
        // Set the BU configured cost categories in the map
        List<Object> configuredCostCategories = configParamService.getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        for (Object configuredCostCategory : configuredCostCategories) {
            costCategoriesMapToReturn.put(((CostCategory) configuredCostCategory).getName(), ((CostCategory) configuredCostCategory));
        }
        //Set the cost categories that were used at the time of filing claim
        if (!ClaimState.DRAFT.getState().equals(claim.getState().getState())) {
            Map<String, Boolean> includedCostCategories = claim.getIncludedCostCategories();
            for (String costCategoryCode : includedCostCategories.keySet()) {
                if (includedCostCategories.get(costCategoryCode)) {
                    costCategoriesMapToReturn.put(costCategoryCode, allCostCategoriesMap.get(costCategoryCode));
                }
            }
        }
        return costCategoriesMapToReturn;
    }

    public void saveCostCategories(List<CostCategory> costCategories) {
        costCategoryRepository.saveCostCategoryProductMapping(costCategories);

        ConfigParam configParam = configParamService.getConfigParamByName(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        for (ConfigValue configValue : configParam.getValues()) {
            boolean isSelected = false;
            for (CostCategory costCategory : costCategories) {
                if (costCategory.getId().toString().equals(configValue.getValue())) {
                    isSelected = true;
                    break;
                }
            }
            configValue.setActive(isSelected);
        }
        configParamService.updateConfig(configParam);
    }

    public void setPaymentComputers(Map<String, PaymentComponentComputer> paymentComputers) {
        this.paymentComputers = paymentComputers;
    }

    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }
}
