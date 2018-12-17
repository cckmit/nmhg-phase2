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
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.LaborRatesAdminService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.laborType.LaborSplit;
import tavant.twms.domain.laborType.LaborSplitDetailAudit;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.infra.BigDecimalFactory;
import tavant.twms.infra.InstanceOfUtil;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class LaborPaymentComputer extends AbstractPaymentComponentComputer {
    private static final Logger logger = Logger.getLogger(LaborPaymentComputer.class);

    public static final String STANDARD_LABORTYPE = "Standard";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String WARRANTY = "WARRANTY";
    public static final String CERTIFIED = "CERTIFIED";
    public static final String COMPETITOR_MODEL_ITEM = "Labor Hrs";
    public static final String COMPETITOR_MODEL_DESCRIPTION ="No Job Code Available";
    
    private LaborRatesAdminService laborRatesAdminService;

    private ConfigParamService configParamService;
    
    private BusinessUnitService businessUnitService;
    
    private PaymentComponentComputerHelper paymentComponentComputerHelper;
    
    private OrgService orgService;

	public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public ConfigParamService getConfigParamService() {
		return configParamService;
	}


    public void setLaborRatesAdminService(LaborRatesAdminService laborRatesAdminService) {
        this.laborRatesAdminService = laborRatesAdminService;
    }

    public Money computeBaseAmount(PaymentContext paymentContext) {
        Claim forClaim = paymentContext.getClaim();
        GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();
        Money laborRate;
        Policy policy = paymentContext.getPolicy();
        Criteria priceCriteriaForLaborExpense = forClaim.priceCriteriaForLaborExpense(policy);
        final String customerType = forClaim.getCustomerType();
        CalendarDate repairDate = forClaim.getRepairDate();
        Currency baseCurrency = globalConfiguration.getBaseCurrency();
        ServiceProvider forDealer = forClaim.getForDealer();
        boolean isThirdParty = Boolean.FALSE;
        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, forDealer)) {
            Boolean isThirdPartyLogin = orgService.isThirdPartyDealerWithLogin(forDealer.getId());
            if (!isThirdPartyLogin) {
                if (!forClaim.isServiceProviderSameAsFiledByOrg() || forClaim.getFiledBy().isInternalUser())
                    isThirdParty = true;
            }
        }
        if (isThirdParty) {
            laborRate = forClaim.getServiceInformation().getThirdPartyLaborRate();
        } else {
            laborRate = laborRatesAdminService.findLaborRate(priceCriteriaForLaborExpense, repairDate, baseCurrency, customerType);
        }
        
        if (logger.isInfoEnabled()) {
            if (policy == null) {
                logger.info("POLICY IS NULL");
            } else {
                logger.info("PARAMS FOR LABOR RATE FETCHING :: => Claim Type ->" + forClaim.getType() + " :: Warranty Type ->" + policy.getWarrantyType()
                        + " :: Dealer Info ->" + forClaim.getForDealer() + "-" + forClaim.getForDealerShip());
                if (forClaim.getClaimedItems().get(0).getItemReference().isSerialized()) {
                    logger.info(" :: Product Type -> " + forClaim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOfType().getProduct());
                }
            }
        }
        Money baseAmount;
        if(laborRate == null){
        	laborRate= Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());	
        }
        baseAmount = calculateLaborAmount(forClaim, laborRate);     
        paymentContext.setRate(laborRate.times(forClaim.getPayment().getLineItemGroup(Section.LABOR).getPercentageApplicable()).dividedBy(100.00));

        Payment payment = paymentContext.getClaim().getPayment();
        LineItemGroup lineItemGroup = payment.createLineItemGroup(paymentContext.getSectionName());
        if (paymentContext.getClaim().getServiceInformation().getServiceDetail().getLaborPerformed().size() > 0 &&
                this.configParamService.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT.getName())) {
            /**
             * Performance fix: Only if labor split is enabled, we need the labor rate look up logic for labor split
             */
            calculateLaborSplitPayment(paymentContext.getClaim(), lineItemGroup, policy);
        } else if (lineItemGroup.getForLaborSplitAudit() != null && lineItemGroup.getForLaborSplitAudit().size() > 0) {
            lineItemGroup.getForLaborSplitAudit().clear();
        }
        
        return baseAmount;
    }
    
    /**
     * @param claim
     * @return
     * Computes Percentage Acceptance
     * Basically one rate will be set for LAM and the same rate will be used for all claims for LAM dealers.  
     * There will be business unit configuration 100% Labor rate to be picked up for LAM dealers and the value will be yes or no. 
     * If it is yes, then 100% rate is applied and if it is set to no, then normal rules comes into place 
     * (like 80% for non-certified technician and 85% for certified technician, 100% for Govt. and FPI claims).
     */
    public BigDecimal calculateLaborRatePercentage(Claim claim){
    	if(null !=claim.getPolicyCode() && claim.getPolicyCode().equalsIgnoreCase("policy"))
    	{
			return AdminConstants.NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE;
    	}
    	if(paymentComponentComputerHelper.isLAMDealer(claim) && this.configParamService.getBooleanValue(ConfigName.HUNDRED_PERCENT_LABORRATE_TO_BE_PICKED_UP_FOR_LAM_DEALERS.getName())){
    		return BigDecimalFactory.bigDecimalOf(100);
    	}else if(ClaimType.FIELD_MODIFICATION.getType().equals(claim.getClmTypeNameUsingBu(claim.getBusinessUnitInfo().getName()))){
    		return BigDecimalFactory.bigDecimalOf(100);
		}else if (claim.getClaimedItems().get(0) != null
				&& claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem() !=null 
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getWarranty() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getWarranty()
						.getMarketingInformation().getInternalInstallType() != null) {
			return BigDecimalFactory.bigDecimalOf(100);
		}else if (ClaimType.PARTS.getType().equals(claim.getClmTypeNameUsingBu(claim.getBusinessUnitInfo().getName()))
				&& !this.configParamService
						.getBooleanValue(ConfigName.TECHNICIAN_CERTIFICATION_FOR_PARTS_CLAIMS
								.getName())
				&& claim.getServiceInformation().getServiceDetail()
						.getServiceTechnician() == null) { // technician not
															// mandatory to
															// enter
			return AdminConstants.NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE;
		}else if(paymentComponentComputerHelper.isTechnicianCertifed(claim)){
    		return AdminConstants.CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE;
    	}
		else{
    		return AdminConstants.NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE;
    	}
    }
    
    public Money calculateLaborAmount(Claim forClaim, Money laborRate) {
        ServiceInformation serviceInformation = forClaim.getServiceInformation();
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        List<LaborDetail> laborDetails = serviceDetail.getLaborPerformed();
        List<LaborSplit> laborSplit = serviceDetail.getLaborSplit();
        GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();
        Money baseAmt = globalConfiguration.zeroInBaseCurrency();
        Money stateMandateBaseAmt = globalConfiguration.zeroInBaseCurrency();
        MathContext mc = new MathContext(4);
        Money totalAmountForInclusiveHrs = globalConfiguration.zeroInBaseCurrency();
        Money totalAmountForExclusiveHrs = globalConfiguration.zeroInBaseCurrency();
        BigDecimal totalStdPlusAdditionalHrs = new BigDecimal(0.0);
        BigDecimal totalInclusiveHrs = new BigDecimal(0.0);
        BigDecimal totalExclusiveHrs = new BigDecimal(0.0);
        BigDecimal totalStdHrs = new BigDecimal(0.0);
        
        Money partsBaseAmt = globalConfiguration.zeroInBaseCurrency();
        Money sMandatePartsBaseAmt = globalConfiguration.zeroInBaseCurrency();
        Money acceptedAmt = globalConfiguration.zeroInBaseCurrency();
        Money totalAcceptance = globalConfiguration.zeroInBaseCurrency();
        Money stateMandateAmt =  globalConfiguration.zeroInBaseCurrency();
        BigDecimal totalAskedHrs = new BigDecimal(0.0);
        BigDecimal totalAcceptedHrs = new BigDecimal(0.0);
        BigDecimal stateMandateRatePercentage = new BigDecimal(100);
        boolean newlyAddedParts=false;
        IndividualLineItem individualLineItem=null;
		List<IndividualLineItem>  individualLineItems= new ArrayList<IndividualLineItem>();
        LineItemGroup lineItemGroup = forClaim.getPayment().createLineItemGroup(Section.LABOR);

        individualLineItems=lineItemGroup.getIndividualLineItems();
        if(isBuConfigAMER(forClaim))
        {
        	lineItemGroup.setPercentageApplicable(calculateLaborRatePercentage(forClaim));
        }     

        //State Mandate code
        Money sMandateLaborRate=globalConfiguration.zeroInBaseCurrency();
        StateMandates sMandate=forClaim.getStateMandate();
        if(sMandate!=null)
        {
        	if(sMandate.getLaborRateType().getDescription().equals(CUSTOMER))
        		sMandateLaborRate=laborRate;
        	else
        		if(sMandate.getLaborRateType().getDescription().equals(WARRANTY))
        		{
        			sMandateLaborRate=laborRate.times(80).dividedBy(100.00);
        			stateMandateRatePercentage=new BigDecimal(80);
        		}
        		else
        		{
        			sMandateLaborRate=laborRate.times(85).dividedBy(100.00); 
        			stateMandateRatePercentage=new BigDecimal(85);
        		}

        }
        //End
        
        if (laborDetails.size() > 0) {
        	if(forClaim.getCompetitorModelBrand()==null)
        	{ 
        		for(IndividualLineItem individualItem:individualLineItems)
        		{
        			boolean isPartRemoveFromSEction=true;
        			for (LaborDetail labor : laborDetails) {
        				if(individualItem.getServiceProcedureDefinition()==null) 
        					break;

        				if(individualItem.getServiceProcedureDefinition().getId().equals(labor.getServiceProcedure().getDefinition().getId()))
        				{
        					isPartRemoveFromSEction=false;
        					break;
        				}

        			}
        			if(isPartRemoveFromSEction)
        			{      			
        				Money amount=Money.valueOf(BigDecimal.ZERO, forClaim.getCurrencyForCalculation());
        				individualItem.setBaseAmount(amount);	
        				individualItem.setAcceptedAmount(amount);	
        				individualItem.setStateMandateAmount(amount);  		
        			}
        		}
        	}
        	
            for (LaborDetail laborDetail : laborDetails) {   
            	           	
            	         
            		laborDetail.setLaborRate(laborRate);
                      Money cost = laborDetail.cost(true);
                      if (logger.isInfoEnabled()) {
                          logger.info(" cost for laborDetail: [" + laborDetail + "] is [" + cost
                                  + "]");
                      }
                      //cost = cost.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00);
                      partsBaseAmt=cost;
                                          
                      //Existing Code
                      int approvedClaimedItems = 0;
                      List<ClaimedItem> claimedItems = forClaim.getClaimedItems();
                      for (ClaimedItem claimedItem : claimedItems) {
                          if (claimedItem.isProcessorApproved()) {
                              approvedClaimedItems++;
                          }
                      }
                      if (forClaim.getType().getType().equalsIgnoreCase("Campaign")) {
                          if (null != laborDetail.getSpecifiedHoursInCampaign()) {
                              if (null != laborDetail.getAdditionalLaborHours()) {
                                  totalStdPlusAdditionalHrs = laborDetail.getSpecifiedHoursInCampaign().
                                          add(laborDetail.getAdditionalLaborHours())
                                          .divide(new BigDecimal(claimedItems.size()))
                                          .multiply(new BigDecimal(
                                                  approvedClaimedItems));
                              } else {
                                  totalStdPlusAdditionalHrs = laborDetail.getSpecifiedHoursInCampaign()
                                          .divide(new BigDecimal(claimedItems.size()))
                                          .multiply(new BigDecimal(
                                                  approvedClaimedItems));
                              }
                          } else {
                              totalStdPlusAdditionalHrs =laborDetail.getTotalHours(serviceDetail.getStdLaborEnabled());
                          }
                      } else {
                          totalStdPlusAdditionalHrs = laborDetail.getTotalHours(serviceDetail.getStdLaborEnabled());
                      }
                      
                      
                      if (!(laborSplit.size() > 0) && !(forClaim.getType().getType().equalsIgnoreCase("Campaign"))) {
                         // baseAmt = baseAmt.plus(laborDetail.cost(serviceDetail.getStdLaborEnabled()));
                        //  baseAmt = baseAmt.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00);
                      }    
                      
                      
                      if (laborSplit.size() > 0 || forClaim.getType().getType().equalsIgnoreCase("Campaign")) {
                          for (LaborSplit lbrSplit : laborSplit) {
                              if (lbrSplit != null) {
                                  if (lbrSplit.getInclusive()) {
                                      totalInclusiveHrs = totalInclusiveHrs.add(lbrSplit.getHoursSpent(), mc);
                                      totalAmountForInclusiveHrs = totalAmountForInclusiveHrs.plus(laborRate.times(
                                              lbrSplit.getLaborType().getMultiplicationValue()).times(lbrSplit.getHoursSpent()));
                                  } else {
                                      totalExclusiveHrs = totalExclusiveHrs.add(lbrSplit.getHoursSpent());
                                      totalAmountForExclusiveHrs = totalAmountForExclusiveHrs.plus(laborRate.times(
                                              lbrSplit.getLaborType().getMultiplicationValue()).times(lbrSplit.getHoursSpent()));
                                  }
                              }
                          }
                          totalStdHrs = totalStdPlusAdditionalHrs.subtract(totalInclusiveHrs);
                          if (laborRate != null) {
                             // baseAmt = totalAmountForInclusiveHrs.plus(totalAmountForExclusiveHrs.plus(laborRate.times(totalStdHrs)));
                             // baseAmt = baseAmt.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00);
                          }
                      }
                      
                      //End of existing code                   
                      
                      
                  //New code    
                      
                   if(forClaim.getState().equals(ClaimState.DRAFT)||lineItemGroup.createLaborIndividualLineItem(laborDetail,forClaim)==null)
  		            	{   
                	   		newlyAddedParts=true;		            		
  		            		individualLineItem=new IndividualLineItem();
  		            		
  		            		individualLineItem.setAskedHrs(totalStdPlusAdditionalHrs);
  		            		individualLineItem.setAcceptedHrs(totalStdPlusAdditionalHrs);
  		            		if(forClaim.getCompetitorModelBrand()!=null)
  		            		{  		            			
  		            			//individualLineItem.setItemRef(COMPETITOR_MODEL_ITEM);
  		            			//individualLineItem.setDescription(COMPETITOR_MODEL_DESCRIPTION);
  		            			//individualLineItem.setSectionName(Section.LABOR);
  		            		}
  		            		else
  		            		{
  		            			if(laborDetail.getServiceProcedure()!=null){
  		            				/*individualLineItem.setItem(laborDetail.getServiceProcedure().getDefinition().getCode());
  		            				individualLineItem.setDescription(laborDetail.getServiceProcedure().getDefinedFor().getJobCodeDescription());*/
  		            				individualLineItem.setServiceProcedureDefinition(laborDetail.getServiceProcedure().getDefinition());  		            				
  		            			}
  		            		}
  		            		totalAskedHrs=totalAskedHrs.add(totalStdPlusAdditionalHrs);
  		            		totalAcceptedHrs=totalAcceptedHrs.add(totalStdPlusAdditionalHrs);            		
  			            }	
  		            	else
  		            	{		  		            		
  		            		individualLineItem=lineItemGroup.createLaborIndividualLineItem(laborDetail,forClaim); 
  		            		individualLineItem.setAskedHrs(totalStdPlusAdditionalHrs);	            		  
  		            		totalAskedHrs=totalAskedHrs.add(totalStdPlusAdditionalHrs);		            		
		            		if(forClaim.getState().equals(ClaimState.FORWARDED))
		            		{    		            			
		            			totalAcceptedHrs=totalAcceptedHrs.add(totalStdPlusAdditionalHrs);     
		            			individualLineItem.setAcceptedHrs(totalStdPlusAdditionalHrs);
		            		}
		            		else
		            		{
		            			//Assigning zero for migrated claim if accepted hours is null
		            			if(individualLineItem.getAcceptedHrs()==null)
		            				individualLineItem.setAcceptedHrs(BigDecimal.ZERO);
		            			totalAcceptedHrs=totalAcceptedHrs.add(individualLineItem.getAcceptedHrs());   		            		
		            		}	
  		            		
  		            	}
                   
  		            	//Accepted Amount calculation														
  		    			if(forClaim.getPayment().isFlatAmountApplied())
  		    			{    
  		    				if(newlyAddedParts)
  							{
  		    					acceptedAmt=partsBaseAmt.times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
  		    					stateMandateAmt=acceptedAmt;
  		    					acceptedAmt=acceptedAmt.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00);
  		    					totalAcceptance.plus(acceptedAmt);	
  							}
  		    				else
  		    				{
  		    				acceptedAmt=individualLineItem.getAcceptedAmount();
  		    				stateMandateAmt=acceptedAmt;  		    				
  		    				totalAcceptance=totalAcceptance.plus(acceptedAmt); 
  		    				}
  		    			  individualLineItem.setPercentageAcceptance(BigDecimal.ZERO);   	
  		    			}
  		    			else
  		    			{
  		    				acceptedAmt=laborDetail.acceptedCost(individualLineItem.getAcceptedHrs()).times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
  		    				stateMandateAmt=acceptedAmt;
  		    				acceptedAmt=acceptedAmt.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00);
  		    				totalAcceptance=totalAcceptance.plus(acceptedAmt);
  		    			}    	  		            	
  		    			individualLineItem.setBaseAmount(partsBaseAmt.times(lineItemGroup.getPercentageApplicable()).dividedBy(100.00));	
  		    			individualLineItem.setAcceptedAmount(acceptedAmt);	
  		    			
  		    		//StateMandate changes	stateMandateRatePercentage
  		    			sMandatePartsBaseAmt=stateMandateAmt.times(stateMandateRatePercentage).dividedBy(100.00);
  		    			individualLineItem.setStateMandate(forClaim, sMandatePartsBaseAmt,Section.LABOR);
  		    			stateMandateBaseAmt=stateMandateBaseAmt.plus(sMandatePartsBaseAmt);
  		    		//End
  		    			if(newlyAddedParts)
							{
  		    				individualLineItems.add(individualLineItem); 
							}
  		    			baseAmt=baseAmt.plus(individualLineItem.getBaseAmount());
  		         
              }             
                                            
          }	 
        else
        {
        	for(IndividualLineItem individualItem:individualLineItems)
        	{
        		 baseAmt=baseAmt.plus(partsBaseAmt);
        		individualItem.setBaseAmount(partsBaseAmt);	
        		individualItem.setAcceptedAmount(acceptedAmt);	
        		individualItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, forClaim.getCurrencyForCalculation()));  				
        	}
        }
        //stateMandateBaseAmt=stateMandateBaseAmt.times(stateMandateRatePercentage).dividedBy(100.00);;
        
        lineItemGroup.setAskedQtyHrs(totalAskedHrs.setScale(2, BigDecimal.ROUND_HALF_UP).toString());       
        lineItemGroup.setAcceptedQtyHrs(totalAcceptedHrs.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        lineItemGroup.setAcceptedTotal(totalAcceptance);	
        lineItemGroup.setStateMandateRate(sMandateLaborRate);              
        lineItemGroup.setStateMandateRatePercentage(stateMandateRatePercentage);
     /*   lineItemGroup.getIndividualLineItems().clear();
        lineItemGroup.getIndividualLineItems().addAll(individualLineItems);*/
        lineItemGroup.setIndividualLineItems(individualLineItems);
        lineItemGroup.setStateMandate(forClaim, stateMandateBaseAmt, Section.LABOR);   	         	
     
        return baseAmt;
    }

    private void calculateLaborSplitPayment(Claim claimDetails, LineItemGroup lineItemGroup, Policy applicablePolicy) {
        List<LaborSplit> laborSplits = claimDetails.getServiceInformation()
                .getServiceDetail().getLaborSplit();
        List<LaborDetail> laborDetails = claimDetails.getServiceInformation()
                .getServiceDetail().getLaborPerformed();
        List<LaborSplitDetailAudit> laborSplitDetailAudits = new ArrayList<LaborSplitDetailAudit>();
        LaborSplitDetailAudit laborSplitDetailAudit = null;
        Money laborRate;
        BigDecimal totalStdPlusAdditionalHrs = new BigDecimal(0.0);        
        BigDecimal totalInclusiveHrs = new BigDecimal(0.0);
        BigDecimal totalExclusiveHrs = new BigDecimal(0.0);
        BigDecimal totalStdHrs = new BigDecimal(0.0);
        Map<String, LaborSplitDetailAudit> map = new HashMap<String, LaborSplitDetailAudit>();
        GlobalConfiguration globalConfiguration = GlobalConfiguration.getInstance();
        Criteria priceCriteriaForLaborExpense = claimDetails.priceCriteriaForLaborExpense(applicablePolicy);
        final String customerType = claimDetails.getCustomerType();
        CalendarDate repairDate = claimDetails.getRepairDate();
        Currency baseCurrency = globalConfiguration.getBaseCurrency();
        boolean isThirdParty = false;
        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, claimDetails
                .getForDealer())) {
            Boolean isThirdPartyLogin = orgService.isThirdPartyDealerWithLogin(claimDetails
                    .getForDealer().getId());
            if (!claimDetails.isServiceProviderSameAsFiledByOrg()
                    && !(isThirdPartyLogin)) {
                isThirdParty = true;
            } else if (claimDetails.getFiledBy().isInternalUser()
                    && !(isThirdPartyLogin)) {
                isThirdParty = true;
            }
        }
        if (isThirdParty) {
            laborRate = claimDetails.getServiceInformation().getThirdPartyLaborRate();
        } else {
            laborRate = laborRatesAdminService.findLaborRate(priceCriteriaForLaborExpense, repairDate, baseCurrency, customerType);
        }

        if (laborDetails.size() > 0) {
            for (LaborDetail labor : laborDetails) {
                if (claimDetails.getType().getType().equalsIgnoreCase("Campaign")) {
                    if (null != labor.getSpecifiedHoursInCampaign()) {
                        if (null != labor.getAdditionalLaborHours()) {
                            totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
                                    .add(labor.getSpecifiedHoursInCampaign())
                                    .add(labor.getAdditionalLaborHours());
                        } else {
                            totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
                                    .add(labor.getSpecifiedHoursInCampaign());
                        }
                    } else {
                        totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
                                .add(labor.getTotalHours(claimDetails.getServiceInformation()
                                        .getServiceDetail().getStdLaborEnabled()));
                    }

                } else {
                    totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
                            .add(labor.getTotalHours(claimDetails.getServiceInformation()
                                    .getServiceDetail().getStdLaborEnabled()));
                }
            }
        }
        if (laborSplits.size() > 0
                || claimDetails.getType().getType().equals("Campaign")) {
            for (LaborSplit laborSplit : laborSplits) {
                if (laborSplit != null) {
                    if (map.containsKey(laborSplit.getLaborType()
                            .getLaborType())) {
                        laborSplitDetailAudit = (map.get(laborSplit
                                .getLaborType().getLaborType()));
                        laborSplitDetailAudit
                                .setLaborHrs(laborSplitDetailAudit
                                        .getLaborHrs().add(
                                                laborSplit.getHoursSpent()));
                        laborSplitDetailAudit.setLaborRate(laborRate);
                        laborSplitDetailAudit.setMultiplicationValue(laborSplit
                                .getLaborType().getMultiplicationValue());

                    } else {
                        laborSplitDetailAudit = new LaborSplitDetailAudit();
                        laborSplitDetailAudit.setLaborType(laborSplit
                                .getLaborType());
                        laborSplitDetailAudit.setName(laborSplit
                                .getLaborType().getLaborType());
                        laborSplitDetailAudit.setLaborHrs(laborSplit
                                .getHoursSpent());
                        laborSplitDetailAudit.setLaborRate(laborRate);
                        laborSplitDetailAudit.setMultiplicationValue(laborSplit
                                .getLaborType().getMultiplicationValue());

                        laborSplitDetailAudits
                                .add(laborSplitDetailAudit);
                        map.put(laborSplit.getLaborType().getLaborType(),
                                laborSplitDetailAudit);
                    }
                    if (laborSplit.getInclusive()) {
                        totalInclusiveHrs = totalInclusiveHrs.add(laborSplit
                                .getHoursSpent());
                    } else {
                        totalExclusiveHrs = totalExclusiveHrs.add(laborSplit
                                .getHoursSpent());
                    }
                }
            }
        }
        totalStdHrs = totalStdPlusAdditionalHrs.subtract(totalInclusiveHrs);
        if (totalStdHrs.signum() == 1) {
            laborSplitDetailAudit = new LaborSplitDetailAudit();
            laborSplitDetailAudit
                    .setName(STANDARD_LABORTYPE);
            laborSplitDetailAudit.setLaborRate(laborRate);
            laborSplitDetailAudit.setLaborHrs(totalStdHrs);
            laborSplitDetailAudits.add(laborSplitDetailAudit);
        }
        lineItemGroup.setForLaborSplitAudit(laborSplitDetailAudits);
    }

	public BusinessUnitService getBusinessUnitService() {
		return businessUnitService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	public PaymentComponentComputerHelper getPaymentComponentComputerHelper() {
		return paymentComponentComputerHelper;
	}

	public void setPaymentComponentComputerHelper(
			PaymentComponentComputerHelper paymentComponentComputerHelper) {
		this.paymentComponentComputerHelper = paymentComponentComputerHelper;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
}
