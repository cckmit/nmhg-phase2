<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<div class="admin_section_div">
<div class="admin_section_heading"><s:text name="label.campaign.incidentals"/></div>
	<table cellspacing="0" cellpadding="0">
	    <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@TRAVEL_DISTANCE_COST_CATEGORY_CODE)">
		    <tr>
		        <td class="admin_data_table">
		            <s:text name="label.campaign.travelDistance"/>:
		        </td>
		        <td>
		            <s:textfield id="travel_distance" size="5" 
		                name="campaign.campaignServiceDetail.travelDetails.distance"/>
		            <s:if test="campaign.campaignServiceDetail.travelDetails==null ||
			            campaign.campaignServiceDetail.travelDetails.uom.equals('miles')">
		            <select name="campaign.campaignServiceDetail.travelDetails.uom">
		            	 <option value="miles"><s:text name="dropdown.common.miles"/></option>
		                 <option value="km"><s:text name="dropdown.common.kilometers"/></option>	               
		            </select>
		            </s:if>
		            <s:else>
		             <select name="campaign.campaignServiceDetail.travelDetails.uom">	            	
		                 <option value="km"><s:text name="dropdown.common.kilometers"/></option>	
		                 <option value="miles"><s:text name="dropdown.common.miles"/></option>               
		            </select>
		           </s:else>
		        </td >
		
		        <td class="admin_data_table">
		            <s:text name="label.campaign.travelLocation"/>:
		        </td>
		        <td>
		            <s:textfield id="travel_location"
		                name="campaign.campaignServiceDetail.travelDetails.location"/>
		        </td>
		    </tr>
		</s:if>
	    <tr>
		    <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@TRAVEL_TRIP_COST_CATEGORY_CODE)">
			    <td class="admin_data_table">
		            <s:text name="label.campaign.travelTrips"/>:
		        </td>
		        <td>
		            <s:textfield id="travel_trips"
		                name="campaign.campaignServiceDetail.travelDetails.trips"/>
		        </td>
	        </s:if>
      		<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@TRAVEL_HOURS_COST_CATEGORY_CODE)">
		        <td  class="admin_data_table">
		            <s:text name="label.campaign.travelHours"/>:
		        </td>
		        <td>
		            <s:textfield id="travel_hours"
		                name="campaign.campaignServiceDetail.travelDetails.hours"/>
		        </td>
		    </s:if>    
	    </tr>
	    <tr>
	        <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@ADDITIONAL_TRAVEL_HOURS_COST_CATEGORY_CODE)">
		        <td  class="admin_data_table">
		            <s:text name="label.campaign.additionalTravelHours"/>:
		        </td>
		        <td>
		            <s:textfield id="add_travel_hours"
		                name="campaign.campaignServiceDetail.travelDetails.additionalHours"/>
		        </td>
	        </s:if>
	        <s:if test="campaign.campaignServiceDetail.campaignSectionPrice.size!=0">
	        	<s:set name="globalIndex" value="campaign.campaignServiceDetail.campaignSectionPrice.size"/>
	        </s:if>
	        <s:else>
		        <s:set name="globalIndex" value="0"/>
	        </s:else>
	        <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE)">
		        <td  class="admin_data_table">
		            <s:text name="label.campaign.meals"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="mealsExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                
		        </td>
	        </s:if>
	    </tr>
	    <tr>
	        <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.campaign.parkingAndToll"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="parkingExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                
		        </td>
		    </s:if>    
	        <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.campaign.perDiem"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="perDiemExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                
		        </td>
		    </s:if>   
	    </tr>
	    <tr>
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.campaign.rentalCharges"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="rentalExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                
		        </td>
		    </s:if>
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE)">		        
		        <td  class="admin_data_table">
		            <s:text name="label.campaign.itemFreightAndDuty"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="dutyExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                
		        </td>
		    </s:if>    
	    </tr>
	    <tr>
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.section.localPurchase"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="localPurchaseExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                
		        </td>
		    </s:if>
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE)">		        
		        <td  class="admin_data_table">
		            <s:text name="label.section.tolls"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="tollsExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                		                
		        </td>
		    </s:if>    
	    </tr>
	    <tr>
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE)">		        
		        <td  class="admin_data_table">
		            <s:text name="label.section.otherFreightAndDuty"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="otherFreightExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                
		        </td>
		    </s:if>    
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@OTHERS_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.section.others"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@OTHERS_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@OTHERS_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@OTHERS_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="othersExpense_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@OTHERS_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                
		        </td>
		    </s:if>
		    
		    <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.section.handlingFee"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="handlingFee_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                
		        </td>
		    </s:if>
		        <s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE)">
		        <td class="admin_data_table">
		            <s:text name="label.section.transportation"/>:
		        </td>
		        <td style="padding:5px 0px 5px 0px">
					<s:if test="campaign.campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE).size!=0">
		                <table>
		                <s:iterator value="campaign.campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE)">
			                  <tr><td>  <t:money id="mealsExpense_unit_price_%{#iter.index}"
							        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].pricePerUnit"
							        value="%{#sectionPrice.pricePerUnit}"
							        size="10" defaultSymbol="%{sectionPrice.pricePerUnit.breachEncapsulationOfCurrency()}"/> 
							        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#iter.index}].sectionName"
							        	value="%{@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE}"></s:hidden>
							  </td></tr>
							</s:if>  
		                </s:iterator>
		                </table>
		            </s:if>
		            <s:else>
		                <table>
		                <s:iterator value="currencies" status="currIter">
		                  <tr><td> <t:money id="handlingFee_unit_price_%{#currIter.index}"
						        name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit"
						        value="%{campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].pricePerUnit}"
						        size="10" defaultSymbol="%{currencyCode}"/> 
						        <s:hidden name="campaign.campaignServiceDetail.campaignSectionPrice[%{#globalIndex}].sectionName"
						        	value="%{@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE}"></s:hidden>
						        </td></tr>
						        <s:set name="globalIndex" value="%{#globalIndex+1}"/>
		                </s:iterator>
		                </table>
		            </s:else>		                		                
		        </td>
		    </s:if>
	    </tr>
	</table>
</div>
