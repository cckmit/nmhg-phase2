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
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.claim.Claim;

import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.base.Rounding;
import com.domainlanguage.money.Money;

public class OEMPaymentComputer extends AbstractPaymentComponentComputer  {

	private static final String BASE_AMOUNT = "BASE AMOUNT";
	private static final String CP_AMOUNT = "CP AMOUNT";
	private static final String REQ_QTY_HRS="REQ QTY/HRS";
	private static final String ACCEPTED_QTY_HRS="ACCEPTED QTY/HRS";
	
	private static final String ACCEPTED_AMOUNT = "ACCEPTED AMOUNT";
	Map<String, BigDecimal> percentageAcceptance=new HashMap<String, BigDecimal>();
	Map<String, String> requestedAcceptedQty_Hrs=new HashMap<String, String>();
	

	
	private Logger logger = Logger
			.getLogger(OEMPaymentComputer.class.getName());
	private String externalServiceEnabled;
	
	private ItemPriceAdminService itemPriceAdminService;
	
	private UomMappingsService uomMappingsService;
	
	private ConfigParamService configParamService;
	
	private PaymentContext tempPaymentContext;
	private PaymentDefinitionAdminService paymentDefinitionAdminService;

	 public Money computeBaseAmount(PaymentContext paymentContext){
		 tempPaymentContext=paymentContext;
		Claim claim=paymentContext.getClaim();
		Map<String, Money> amounts = computeAtCostAndBasePrices(claim, paymentContext.getPolicy());
		Money baseAmount = amounts.get("BASE AMOUNT");
        Money CPAmount = amounts.get("CP AMOUNT");
       	Payment payment = claim.getPayment();
		LineItemGroup lineItemGroup = payment.createLineItemGroup(paymentContext.getSectionName());
		//Req and accepted qty
		lineItemGroup.setAskedQtyHrs(requestedAcceptedQty_Hrs.get(REQ_QTY_HRS));		
		lineItemGroup.setAcceptedQtyHrs(requestedAcceptedQty_Hrs.get(ACCEPTED_QTY_HRS));
		lineItemGroup.setAcceptedTotal(amounts.get(ACCEPTED_AMOUNT));
						
		paymentContext.setAcceptedCpTotal(CPAmount);
       	List<PartReplaced> partsReplaced = claim.getServiceInformation().getServiceDetail().getPriceForInstalledParts();
		lineItemGroup.getCurrentPartPaymentInfo().clear();
		
		for (PartReplaced partReplaced : partsReplaced) {
			Integer numberOfUnits = partReplaced.getNumberOfUnits();
			final String number;
			Money costPrice = partReplaced.getPricePerUnit();
            if (claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(partReplaced)) {
                number = ((OEMPartReplaced) partReplaced).getItemReference().getUnserializedItem().getNumber();
              
                if (((OEMPartReplaced) partReplaced).isShippedByOem() && costPrice == null) {
                    costPrice = moneyValueOf(0, null);
                }
            } else {
                number = ((InstalledParts) partReplaced).getItem().getNumber(); // the shippedByOEM API is defined
                                                                           // for OEMPartReplaced. ???
            }
			PartPaymentInfo partPaymentInfo = new PartPaymentInfo();
			partPaymentInfo.setPartNumber(number);
			partPaymentInfo.setUnitPrice(costPrice);
			partPaymentInfo.setQuantity(numberOfUnits.longValue());
			lineItemGroup.getCurrentPartPaymentInfo().add(partPaymentInfo);
		}
		
		return baseAmount; 
	}

	public Map<String, Money> computeAtCostAndBasePrices(Claim forClaim, Policy policy){
		logger.info("Inside OEMPaymentComputer :: compute method : externalServiceEnabled = "
                + externalServiceEnabled);
		populatePriceCheckDataForParts(policy, forClaim);
		return computeWithExternalData(forClaim);
	}

	private Map<String, Money> computeWithExternalData(Claim forClaim) {
		List<PartReplaced> partReplacedList = forClaim.getServiceInformation().getServiceDetail().getPriceFetchedParts();
		logger.debug("Inside OEMPaymentComputer :: computeWithExternalData method ");
        return fetchAmountForOEMParts(forClaim, partReplacedList);
	}

    /*
      * This API is written to make the amount fetching flow independent of the BU.
      * i.e., for Hussmann Part installed flow and the installed oem flow
      */
    private Map<String, Money> fetchAmountForOEMParts(Claim forClaim,
                                                      List<PartReplaced> partsReplaced) {
        Currency naturalCurrency = forClaim.getCurrencyForCalculation();
        Currency baseCurrency = GlobalConfiguration.getInstance()
                .getBaseCurrency();

        Map<String, Money> amounts = new HashMap<String, Money>();
        Money baseAmt = Money.valueOf(0.0D, baseCurrency);
        Money cpAmt = Money.valueOf(0.0D, baseCurrency);
        Money partCostAmt = Money.valueOf(0.0D, baseCurrency);
        
         Money acceptedAmt = Money.valueOf(0.0D, baseCurrency);
       
             
        logger
                .debug("Inside OEMPaymentComputer :: computeWithExternalData method before invoking itemPriceAdminService");
        boolean isUomEnabled = configParamService
							.getBooleanValue(ConfigName.IS_UOM_ENABLED.getName());
        String costPriceType = configParamService.getStringValue(ConfigName.COST_PRICE_CONFIGURATION.getName());
        
        Integer totalReqQuantity=new Integer(0);
        Integer totalAcceptedQuantity=new Integer(0);     
       	Money totalAcceptance=Money.valueOf(0.0D, baseCurrency);
        Money individualPartAmt = Money.valueOf(0.0D, baseCurrency);       
        Money acceptedAmount = Money.valueOf(0.0D, baseCurrency);
        Money totalUpdatedAmount=Money.valueOf(BigDecimal.ZERO, forClaim.getCurrencyForCalculation());
        Currency currency=null;
		LineItemGroup oemParts=forClaim.getPayment().createLineItemGroup(Section.OEM_PARTS);
		IndividualLineItem individualLineItem=null;
		List<IndividualLineItem>  individualLineItems= new ArrayList<IndividualLineItem>();
		individualLineItems=oemParts.getIndividualLineItems();
		boolean newlyAddedParts=false;
		  LineItemGroup lineItemGroupAudit=null;
	        LineItemGroup lineItemGroupAuditForClaimedAmount=null;    
        if (partsReplaced.size() > 0) {
        	
        	
        	for(IndividualLineItem individualItem:individualLineItems)
        	{
        		boolean isPartRemoveFromSEction=true;
        		for (PartReplaced nonOEMPart : partsReplaced) {
        			InstalledParts oemPartReplaced=null;  
        			if(nonOEMPart instanceof InstalledParts)
        			{
        			//isPartRemoveFromSEction=true;
        			oemPartReplaced=(InstalledParts)nonOEMPart;
        			if(individualItem.getBrandItem().getId()==oemPartReplaced.getBrandItem().getId())
        			{
        				isPartRemoveFromSEction=false;
        				break;
        			}
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
        	
            for (PartReplaced partReplaced : partsReplaced) {           
            	newlyAddedParts=false;
            	
            	currency=partReplaced.getPricePerUnit().breachEncapsulationOfCurrency();
            	
            	
            	 Money cost = partReplaced.cost();
                 if (logger.isInfoEnabled()) {
                     logger.info(" cost for nonOEMPartReplaced: [" + partReplaced + "] is [" + cost
                             + "]");
                 }    
                 individualPartAmt=cost;
                // baseAmt = baseAmt.plus(cost);   
            	
            	// If the oem part is shipped by the OEM,
                // should not be considered towards the claim amount calculation
                // Holds good incase of Campaign Claims.

				if (isUomEnabled) {
					if (forClaim.getServiceInformation().getServiceDetail()
							.isOEMPartReplaced(partReplaced))
						setUomMappingForPart((OEMPartReplaced) partReplaced,
								forClaim.getBusinessUnitInfo().getName());
					else
						setUomMappingForInstalledPart(
								(InstalledParts) partReplaced, forClaim
										.getBusinessUnitInfo().getName());
				}
            	
                // oEMPartReplaced.getReadOnly() For FOC claims do not compute
                OEMPartReplaced oEMPartReplaced;
                if (forClaim.getServiceInformation().getServiceDetail()
                        .isOEMPartReplaced(partReplaced)
                        && (forClaim.getServiceInformation().getServiceDetail()
                        .getHussmanPartsReplacedInstalled().isEmpty())) {
                    oEMPartReplaced = (OEMPartReplaced) partReplaced;
                    if (oEMPartReplaced.getReadOnly()
                            || oEMPartReplaced.isShippedByOem()) {
                        continue;
                    }
                    
                    
                    //CP changes
                    
                    if (!partReplaced.costAtCP(costPriceType).equals(
                            Money.valueOf(0.0D, baseCurrency))) {
                        if (cpAmt.equals(Money.valueOf(0.0D, baseCurrency))) {
                            cpAmt = Money.valueOf(0.0D, naturalCurrency);
                            partCostAmt=Money.valueOf(0.0D, naturalCurrency);
                        }

                        if (cpAmt.breachEncapsulationOfCurrency().equals(
                                partReplaced.costAtCP(costPriceType)
                                        .breachEncapsulationOfCurrency())) {
                        	partCostAmt=partReplaced.costAtCP(costPriceType);
                            cpAmt = cpAmt.plus(partCostAmt);
                        } else {
                            logger
                                    .error("Customer Currency Not matching with recevied currency from price fetch");
                        }
                    }            
                    
                    //End of CP changes
                } else if (!forClaim.getServiceInformation().getServiceDetail()
                        .isOEMPartReplaced(partReplaced)) {

                    if (InstanceOfUtil.isInstanceOfClass(InstalledParts.class, partReplaced)) {
                        if (((InstalledParts) partReplaced).isShippedByOem()) {
                            continue;
                        }
                    }

                    if (partReplaced.getReadOnly()) {
                        continue;
                    }
                    
                    //CP changes
                    if (!partReplaced.costAtCP(costPriceType).equals(
                            Money.valueOf(0.0D, baseCurrency))) {
                        if (cpAmt.equals(Money.valueOf(0.0D, baseCurrency))) {
                            cpAmt = Money.valueOf(0.0D, naturalCurrency);
                            partCostAmt=Money.valueOf(0.0D, naturalCurrency);
                        }
                        if (cpAmt.breachEncapsulationOfCurrency().equals(
                                partReplaced.costAtCP(costPriceType)
                                        .breachEncapsulationOfCurrency())) {
                        	partCostAmt=partReplaced.costAtCP(costPriceType);
                            cpAmt = cpAmt.plus(partCostAmt);
                        } else {
                            logger.error("Customer Currency Not matching with recevied currency from price fetch");
                        }
                    }         
                    
                    //End of CP changes
                    
                }   	
            	
          	  //425 Changes
 				
    					if(partReplaced instanceof InstalledParts)    						
    					{
    						//Newly Added
    						InstalledParts installedPart=(InstalledParts)partReplaced;
    		            	if(forClaim.getState().equals(ClaimState.DRAFT)||oemParts.createIndividualLineItem(partReplaced)==null)
    		            	{    
    		            		newlyAddedParts=true;	
    		            		individualLineItem=new IndividualLineItem();    		            		
    		            			individualLineItem.setAskedQty(partReplaced.getNumberOfUnits());
    		            		individualLineItem.setAcceptedQty(partReplaced.getNumberOfUnits());
    		            		individualLineItem.setBrandItem(installedPart.getBrandItem());          		
    		            		totalReqQuantity=totalReqQuantity+partReplaced.getNumberOfUnits();
    		            		totalAcceptedQuantity=totalAcceptedQuantity+partReplaced.getNumberOfUnits();
    		            	}
    		            	else
    		            	{
    		            		individualLineItem=oemParts.createIndividualLineItem(partReplaced);
    		            		individualLineItem.setAskedQty(partReplaced.getNumberOfUnits());    		            		
    		            		totalReqQuantity=totalReqQuantity+partReplaced.getNumberOfUnits();
    		            		if(forClaim.getState().equals(ClaimState.FORWARDED))
    		            		{    		            			
    		            			totalAcceptedQuantity=totalAcceptedQuantity+partReplaced.getNumberOfUnits();
    		            			individualLineItem.setAcceptedQty(partReplaced.getNumberOfUnits());
    		            		}
    		            		else
    		            		{    		            		
    		            		totalAcceptedQuantity=totalAcceptedQuantity+individualLineItem.getAcceptedQty();    		            		
    		            		}
    		            		
    		            	}    		            	
    						
    						//noOfPart++;  						
    					    						
    						//BigDecimal partAmount=partReplaced.getPricePerUnit().breachEncapsulationOfAmount();
    						//partAmount =partAmount.multiply(new BigDecimal(individualLineItem.getAcceptedQty()));    		           
    					    							    							
    					    if (!partReplaced.cost().equals(
    			                  Money.valueOf(0.0D, baseCurrency))) {
    			                   if (baseAmt.equals(Money.valueOf(0.0D, baseCurrency))) {
    			                       baseAmt = Money.valueOf(0.0D, naturalCurrency);
    			                       acceptedAmt = Money.valueOf(0.0D, naturalCurrency);
    			                       individualPartAmt=Money.valueOf(0.0D, naturalCurrency);
    			                    }
    			                   if (baseAmt.breachEncapsulationOfCurrency().equals(
    			                                partReplaced.cost()
    			                                        .breachEncapsulationOfCurrency())) {
    			                        	//Base amount calculation
    			                	   		//individualPartAmt=Money.valueOf(installedPart.getPricePerUnit().breachEncapsulationOfAmount().multiply(new BigDecimal(installedPart.getNumberOfUnits())),installedPart.getPricePerUnit().breachEncapsulationOfCurrency(),Rounding.HALF_UP);
    		    							baseAmt=baseAmt.plus(cost);
    		    							individualPartAmt=cost;
    		    							//baseAmt=Money.valueOf(baseAmt.breachEncapsulationOfAmount().multiply(new BigDecimal(installedPart.getNumberOfUnits())),installedPart.getUomAdjustedPricePerUnit().breachEncapsulationOfCurrency(),Rounding.HALF_UP);
    			                    } else {
    			                            logger.error("Customer Currency Not matching with recevied currency from price fetch");
    			                      }
    			             }   							
    							//Accepted Amount calculation														
	    						if(forClaim.getPayment().isFlatAmountApplied()){
	    							if(newlyAddedParts)
	    							{	    								
	    								acceptedAmt=individualPartAmt.times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
	    		    					totalAcceptance.plus(acceptedAmt);	
	    							}
	    							else
	    							{
	    								acceptedAmt=individualLineItem.getAcceptedAmount();	
	    			    				totalAcceptance=totalAcceptance.plus(acceptedAmt); 
	    							}   								
	    							individualLineItem.setPercentageAcceptance(BigDecimal.ZERO);   
	    							//Cost Price changes for 0 qty or 0%
	    							if(individualLineItem.getAcceptedAmount()!=null&&individualLineItem.getAcceptedAmount().breachEncapsulationOfAmount().intValue()==0)
	    							{	    							
	    		                            cpAmt = cpAmt.minus(partCostAmt);	    							
	    							}
	    						}
	    						else
	    						{
	    							acceptedAmount=installedPart.getPricePerUnit().times(individualLineItem.getAcceptedQty());
		    						acceptedAmt=acceptedAmount.times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
	    		    				totalAcceptance=totalAcceptance.plus(acceptedAmt);
	    		    				//Cost Price changes for 0 amount
	    		    				if(individualLineItem.getPercentageAcceptance().equals(BigDecimal.ZERO)||individualLineItem.getAcceptedQty().equals(0))
	    							{
	    		    					 cpAmt = cpAmt.minus(partCostAmt);	   
	    							}
	    						}				
    							individualLineItem.setBaseAmount(individualPartAmt);
    							individualLineItem.setAcceptedAmount(acceptedAmt);	
    							individualLineItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, forClaim.getCurrencyForCalculation()));
    							if(newlyAddedParts)
    			    			{    			    				
    							individualLineItems.add(individualLineItem);	
    			    			}
    							individualLineItem.setStateMandate(forClaim, acceptedAmt,Section.OEM_PARTS);		

    							  					       
    					        
    					        if(forClaim.isPriceUpdated())
    					        {
    					        	if(!forClaim.getState().equals(ClaimState.DRAFT))
    					        	{
    					        		if(forClaim.getPaymentForDealerAudit()!=null)
    					        			lineItemGroupAudit=forClaim.getPaymentForDealerAudit().createLineItemGroup(Section.OEM_PARTS);
    					        		if(lineItemGroupAudit!=null)
    					        		{    					        				        	 
    					        			IndividualLineItem individualLineItemsForDealerAudit=lineItemGroupAudit.getIndividualLineItem(individualLineItem.getBrandItem());   
    					        			if(installedPart.getPriceUpdated()&&individualLineItemsForDealerAudit!=null&&!individualLineItemsForDealerAudit.getDealerNetpriceUpdated()&&individualLineItemsForDealerAudit.getBaseAmount().breachEncapsulationOfAmount().floatValue()==0)
    					        			{
    					        				individualLineItemsForDealerAudit.setBaseAmount(individualPartAmt);
    					        				individualLineItemsForDealerAudit.setAcceptedAmount(individualPartAmt);
    					        				//individualLineItemsForDealerAudit.setStateMandate(forClaim, individualPartAmt,Section.OEM_PARTS);
    					        				individualLineItemsForDealerAudit.setDealerNetpriceUpdated(true);
    					        				totalUpdatedAmount=totalUpdatedAmount.plus(individualPartAmt);
    					        			}
    					        		}  

    					        	}
    					        }
    							
    									
    							
    							
    					}	
            }	
           /* oemParts.getIndividualLineItems().clear();
            oemParts.getIndividualLineItems().addAll(individualLineItems);   */
            //oemParts.setStateMandate(forClaim, baseAmt, Section.OEM_PARTS);
            
            	
      	acceptedAmt=totalAcceptance;  		
         }
        else
        {
        	for(IndividualLineItem individualItem:individualLineItems)
        	{
        		individualItem.setBaseAmount(individualPartAmt);	
        		individualItem.setAcceptedAmount(acceptedAmt);	
        		individualItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, forClaim.getCurrencyForCalculation()));  				
        	}
        }
        logger
                .debug("Inside OEMPaymentComputer :: computeWithExternalData method returning baseAmount"
                        + baseAmt.toString());
        
      //TO Update Dealer net price to asked Amount  if part price is Zero and they are updating it 1st time(Total level updation)
    
        if(forClaim.isPriceUpdated())
        {
        	if(!forClaim.getState().equals(ClaimState.DRAFT))
        	{

        		if(lineItemGroupAudit!=null)
        		{
        			lineItemGroupAuditForClaimedAmount=forClaim.getPaymentForDealerAudit().createLineItemGroup(Section.TOTAL_CLAIM);
        			Money oldOEMBaseAmount=lineItemGroupAudit.getBaseAmount();
        			Money oldOEMGroupAmount=lineItemGroupAudit.getGroupTotal();
        			Money oldTotalBaseAmount=lineItemGroupAuditForClaimedAmount.getBaseAmount();
        			Money oldTotalGroupAmount=lineItemGroupAuditForClaimedAmount.getGroupTotal();
        			lineItemGroupAudit.setBaseAmount(oldOEMBaseAmount.plus(totalUpdatedAmount));
        			getModifierService().applyModifiers(forClaim, lineItemGroupAudit, tempPaymentContext.getPaymentSection());
        			lineItemGroupAudit.setGroupTotal();     			  
        			lineItemGroupAudit.setAcceptedTotal(lineItemGroupAudit.getGroupTotal()); 
        			//lineItemGroupAudit.setStateMandate(forClaim, lineItemGroupAudit.getBaseAmount().plus(lineItemGroupAudit.getSMandateModifierAmount()), Section.OEM_PARTS);
        			
        			if(oldOEMBaseAmount!=null)
        			{
        				oldTotalBaseAmount=oldTotalBaseAmount.plus(totalUpdatedAmount);
        			}
        			if(oldTotalGroupAmount!=null)
        			{
        				oldTotalGroupAmount=oldTotalGroupAmount.minus(oldOEMGroupAmount).plus(lineItemGroupAudit.getGroupTotal());
        			}
        			lineItemGroupAuditForClaimedAmount.setBaseAmount(oldTotalBaseAmount);
        			lineItemGroupAuditForClaimedAmount.setGroupTotal(oldTotalGroupAmount);
        			lineItemGroupAuditForClaimedAmount.setAcceptedTotal(oldTotalGroupAmount);
        			//lineItemGroupAudit.setStateMandate(forClaim, lineItemGroupAuditForClaimedAmount.getGroupTotalStateMandateAmount().minus(oldTotalGroupAmount).plus(lineItemGroupAudit.getGroupTotalStateMandateAmount()), Section.TOTAL_CLAIM);
        			forClaim.getPaymentForDealerAudit().setClaimedAmount(oldTotalGroupAmount);
        			forClaim.getPaymentForDealerAudit().setTotalAmount(oldTotalGroupAmount);
        			forClaim.getActiveClaimAudit().getPayment().setClaimedAmount(oldTotalGroupAmount);
        		}
        	}

        }       
        
        
       /* oemParts.getIndividualLineItems().clear();
        oemParts.getIndividualLineItems().addAll(individualLineItems);   */
        oemParts.setIndividualLineItems(individualLineItems);
        oemParts.setStateMandate(forClaim, acceptedAmt, Section.OEM_PARTS);
        amounts.put(CP_AMOUNT, cpAmt);
        amounts.put(BASE_AMOUNT, baseAmt);        
      //Newly Added
        amounts.put(ACCEPTED_AMOUNT,acceptedAmt);
        //End
        //for req and accepted qty
        requestedAcceptedQty_Hrs.put(REQ_QTY_HRS, totalReqQuantity.toString());
        requestedAcceptedQty_Hrs.put(ACCEPTED_QTY_HRS, totalAcceptedQuantity.toString());
        return amounts;
    }

    /**
     * Method finds the UOM Mapping for installed Part
     */
    private void setUomMappingForInstalledPart(InstalledParts installedParts, String buName) {
        ItemUOMTypes itemUOMTypes = installedParts.getItem().getUom();
        String baseUomString = StringUtils.stripToEmpty(itemUOMTypes.getType());

        if (!StringUtils.isBlank(baseUomString) && !ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
            UomMappings uom = uomMappingsService.findUomMappingForBaseUom(itemUOMTypes.getName());
            installedParts.setUomMapping(uom);
        }
    }

    private void setUomMappingForPart(OEMPartReplaced oEMPartReplaced, String buName) {
        ItemUOMTypes itemUOMTypes = oEMPartReplaced.getItemReference().getUnserializedItem().getUom();
        String baseUomString = StringUtils.stripToEmpty(itemUOMTypes.getType());

        if (!StringUtils.isBlank(baseUomString) && !ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
            UomMappings uom = uomMappingsService.findUomMappingForBaseUom(itemUOMTypes.getName());
            oEMPartReplaced.setUomMapping(uom);
        }
    }

	private void populatePriceCheckDataForParts(Policy policy, Claim forClaim) {
		List<PartReplaced> partsPriceFetchList = forClaim.getServiceInformation().getServiceDetail().getPriceFetchedParts();
		priceFetchForOEMParts(forClaim, partsPriceFetchList, policy);
	}

	/*
	 *   This part of the code is seperated from populatePriceCheckDataForParts to make it independent 
	 *   of the flow if whether the price fetch is to be done for the Installed part of Hussmann or the 
	 *   InstalledOEMPart.
	 */
	private void priceFetchForOEMParts(Claim forClaim, List<PartReplaced> partsReplaced,
			Policy policy) {
		List<PriceFetchData> priceFetchDataList = itemPriceAdminService
				.findPrice(forClaim, policy);
		Map<String, PriceFetchData> mapOfItemPrices = buildItemPriceMap(priceFetchDataList);
		boolean isAdjustedPrice = configParamService.getBooleanValue((ConfigName.USE_ADJUSTED_PRICE_ON_CLAIM.getName()));
		for (PartReplaced oemPartReplaced : partsReplaced) {
			BrandItem replacedItem;
			if( forClaim.getServiceInformation().getServiceDetail().isOEMPartReplaced(oemPartReplaced)){
				replacedItem = ((OEMPartReplaced)oemPartReplaced).getBrandItem();
			}else {
				if(((InstalledParts)oemPartReplaced).getPriceUpdated()!=null&&((InstalledParts)oemPartReplaced).getPriceUpdated())
				{
					replacedItem = ((InstalledParts)oemPartReplaced).getBrandItem();
					forClaim.setPriceUpdated(true);
					PriceFetchData priceFetch = mapOfItemPrices.get(replacedItem.getItemNumber());
					Money standardCost = priceFetch.getStandardCost();

					if (standardCost != null) {
						oemPartReplaced.setCostPricePerUnit(standardCost);
					}
					continue;
				}
				else {
				replacedItem = ((InstalledParts)oemPartReplaced).getBrandItem();
				}
			}
			// HACK-FIX - when XML -> Object will populate 1 OEMPartReplaced
			// even tho' there are none.
/*			if (replacedItem == null) {
				continue;
			}*/
        
			PriceFetchData priceFetch = mapOfItemPrices.get(replacedItem.getItemNumber());
			Money price = priceFetch.getListPrice();
			Money standardCost = priceFetch.getStandardCost();
			Money materialPrice = priceFetch.getMaterialPrice();
			Money adjustedPrice = priceFetch.getAdjustedListPrice();

			if (standardCost != null) {
				oemPartReplaced.setCostPricePerUnit(standardCost);
			}
			if (materialPrice != null) {
				oemPartReplaced.setMaterialCost(materialPrice);
			}
			if (isAdjustedPrice && (adjustedPrice != null)) {
				oemPartReplaced.setPricePerUnit(adjustedPrice);
			} else{
				if(price != null) {
				    oemPartReplaced.setPricePerUnit(price);
                }
			}
        }
    }
	
	
	private Map<String, PriceFetchData> buildItemPriceMap(
			List<PriceFetchData> priceFetchDataList) {
		Map<String, PriceFetchData> mapOfItemPrices = new HashMap<String, PriceFetchData>();
		for (PriceFetchData priceFetchData : priceFetchDataList) {
			mapOfItemPrices.put(priceFetchData.getBrandItem().getItemNumber(),
					priceFetchData);
		}
		return mapOfItemPrices;
	}
	
	private Money moneyValueOf(double amount, Claim forClaim) {
		return Money.valueOf(amount, GlobalConfiguration.getInstance().getBaseCurrency());
	}

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public PaymentDefinitionAdminService getPaymentDefinitionAdminService() {
        return paymentDefinitionAdminService;
    }

    public void setItemPriceAdminService(ItemPriceAdminService itemPriceAdminService) {
        this.itemPriceAdminService = itemPriceAdminService;
    }

	public void setExternalServiceEnabled(String externalServiceEnabled) {
		this.externalServiceEnabled = externalServiceEnabled;
	}

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}


	
}
