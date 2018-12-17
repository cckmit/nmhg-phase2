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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.infra.BigDecimalFactory;

import com.domainlanguage.base.Rounding;
import com.domainlanguage.money.Money;

public class NonOEMPaymentComputer extends AbstractPaymentComponentComputer {
    private static Logger logger = LogManager.getLogger(NonOEMPaymentComputer.class);
    
    public Money computeBaseAmount(PaymentContext ctx) {
    	Claim claim=ctx.getClaim();
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
        final List<PartReplaced> partsReplaced = serviceDetail.getNonOEMPriceFetchParts();
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        Money baseAmt = Money.valueOf(0.0D,baseCurrency);
        Money partsBaseAmt = Money.valueOf(0.0D,baseCurrency);
    	//Map<String, String> requestedAcceptedQty_Hrs=new HashMap<String, String>();
    
        Integer totalReqQuantity=new Integer(0);
        Integer totalAcceptedQuantity=new Integer(0);
        Money totalAcceptance=Money.valueOf(0.0D, baseCurrency);
        Money acceptedAmt = Money.valueOf(0.0D, baseCurrency);
        Money acceptedAmount = Money.valueOf(0.0D, baseCurrency);
      	LineItemGroup nonOemParts=claim.getPayment().createLineItemGroup(ctx.getSectionName());
		IndividualLineItem individualLineItem=null;
		List<IndividualLineItem>  individualLineItems= new ArrayList<IndividualLineItem>();
		individualLineItems=nonOemParts.getIndividualLineItems();
		NonOEMPartReplaced nonOemPart=null;
		boolean newlyAddedParts=false;
		//End
        
        if (partsReplaced.size() > 0) {
        	//
      		 
        	for(IndividualLineItem individualItem:individualLineItems)
        	{
        		boolean isPartRemoveFromSEction=true;
        		for (PartReplaced nonOEMPart : partsReplaced) {
        			NonOEMPartReplaced nonOEMPartReplaced=null;   
        			if(nonOEMPart instanceof NonOEMPartReplaced)
        			{
        				//isPartRemoveFromSEction=true;
        				nonOEMPartReplaced=(NonOEMPartReplaced)nonOEMPart;
        				if(individualItem.getNonOemPartReplaced()!=null&&individualItem.getNonOemPartReplaced().equals(nonOEMPartReplaced.getDescription()))
        				{
        					isPartRemoveFromSEction=false;
        					break;
        				}
        			}
        				
        		}
        		if(isPartRemoveFromSEction)
        		{      			
        		Money amount=Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation());
        		individualItem.setBaseAmount(amount);	
    			individualItem.setAcceptedAmount(amount);	
    			individualItem.setStateMandateAmount(amount);  		
        		}
        	}
        	
            for (PartReplaced nonOEMPartReplaced : partsReplaced) {
               
                    Money cost = nonOEMPartReplaced.cost();
                    if (logger.isInfoEnabled()) {
                        logger.info(" cost for nonOEMPartReplaced: [" + nonOEMPartReplaced + "] is [" + cost
                                + "]");
                    }    
                    partsBaseAmt=cost;
                    baseAmt = baseAmt.plus(cost);              
                    
                  
                    newlyAddedParts=false;
                    nonOemPart=(NonOEMPartReplaced)nonOEMPartReplaced;                   
					
		            	if(claim.getState().equals(ClaimState.DRAFT)||nonOemParts.createNonOemIndividualLineItem(nonOEMPartReplaced)==null)
		            	{    	
		            		newlyAddedParts=true;	
		            		individualLineItem=new IndividualLineItem();		            		
		            		individualLineItem.setAskedQty(nonOemPart.getNumberOfUnits());
		            		individualLineItem.setAcceptedQty(nonOemPart.getNumberOfUnits());
		            		individualLineItem.setNonOemPartReplaced(nonOemPart.getDescription());     	
		            		totalReqQuantity=totalReqQuantity+nonOemPart.getNumberOfUnits();
		            		totalAcceptedQuantity=totalAcceptedQuantity+nonOemPart.getNumberOfUnits();		            			
			            	//individualLineItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()));			            			            	
		            	}	
		            	else
		            	{ 	            		
		            		individualLineItem=nonOemParts.createNonOemIndividualLineItem(nonOEMPartReplaced); 
		            		individualLineItem.setAskedQty(nonOemPart.getNumberOfUnits());    
		            		totalReqQuantity=totalReqQuantity+nonOemPart.getNumberOfUnits();
		            		
		            		if(claim.getState().equals(ClaimState.FORWARDED))
		            		{    		            			
		            			totalAcceptedQuantity=totalAcceptedQuantity+nonOemPart.getNumberOfUnits();
		            			individualLineItem.setAcceptedQty(nonOemPart.getNumberOfUnits());
		            		}
		            		else
		            		{    		            		
		            		totalAcceptedQuantity=totalAcceptedQuantity+individualLineItem.getAcceptedQty();    		            		
		            		}		            		
	  		            	
		            	}		            	
		            	//Accepted Amount calculation														
		    			if(claim.getPayment().isFlatAmountApplied())
		    			{    
		    				if(newlyAddedParts)
							{
		    					acceptedAmt=partsBaseAmt.times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
		    					totalAcceptance.plus(acceptedAmt);	
							}
		    				else
		    				{
		    				acceptedAmt=individualLineItem.getAcceptedAmount();	
		    				totalAcceptance=totalAcceptance.plus(acceptedAmt); 
		    				}
		    			  individualLineItem.setPercentageAcceptance(BigDecimal.ZERO);   	
		    			}
		    			else
		    			{
		    				acceptedAmount=nonOemPart.getPricePerUnit().times(individualLineItem.getAcceptedQty());
		    				acceptedAmt=acceptedAmount.times(individualLineItem.getPercentageAcceptance()).dividedBy(100.00);
		    				totalAcceptance=totalAcceptance.plus(acceptedAmt);
		    			}           	
		            	
		    			individualLineItem.setBaseAmount(partsBaseAmt);	
		    			individualLineItem.setAcceptedAmount(acceptedAmt);	
		    			individualLineItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()));		
		    			if(newlyAddedParts)
		    			{
		    				individualLineItems.add(individualLineItem); 
		    			}
		       			individualLineItem.setStateMandate(claim, acceptedAmt,ctx.getSectionName());
		       		
            }                        
        } 
        else
        {
        	for(IndividualLineItem individualItem:individualLineItems)
        	{
        		individualItem.setBaseAmount(partsBaseAmt);	
        		individualItem.setAcceptedAmount(acceptedAmt);	
        		individualItem.setStateMandateAmount(Money.valueOf(BigDecimal.ZERO, claim.getCurrencyForCalculation()));  				
        	}
        }
       
        nonOemParts.setAskedQtyHrs(totalReqQuantity.toString());       
        nonOemParts.setAcceptedQtyHrs(totalAcceptedQuantity.toString());
        nonOemParts.setAcceptedTotal(totalAcceptance);            
        //nonOemParts.getIndividualLineItems().clear();  
        //nonOemParts.getIndividualLineItems().addAll(individualLineItems);
        nonOemParts.setIndividualLineItems(individualLineItems);
        nonOemParts.setStateMandate(claim, totalAcceptance, ctx.getSectionName());

     
        return baseAmt;            
                    
                    
                    
                    
               
            }
        }
       
  