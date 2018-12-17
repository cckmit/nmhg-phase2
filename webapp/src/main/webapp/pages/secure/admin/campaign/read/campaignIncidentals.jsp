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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>

<div dojoType="dijit.layout.ContentPane">
<div class="policy_section_div">
	<div class="admin_section_heading"><s:text name="label.campaign.incidentals"/></div>
	<table class="grid"width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td class="admin_data_table"><s:text name="label.campaign.travelDistance"/>:</td>
			<td><s:property value="campaign.campaignServiceDetail.travelDetails.distance"/> </td>
			<td class="admin_data_table"><s:text name="label.campaign.travelLocation"/>:</td>
			<td> <s:property value="campaign.campaignServiceDetail.travelDetails.location"/></td>
		</tr>
		<tr>
			<td class="admin_data_table"><s:text name="label.campaign.travelTrips"/>:</td>
			<td><s:property value="campaign.campaignServiceDetail.travelDetails.trips"/> </td>
			<td class="admin_data_table"><s:text name="label.campaign.travelHours"/>:</td>
			<td><s:property value="campaign.campaignServiceDetail.travelDetails.hours"/> </td>
		</tr>
          <tr>
              <td class="admin_data_table"><s:text name="label.campaign.additionalTravelHours"/>:</td>
              <td><s:property value="campaign.campaignServiceDetail.travelDetails.additionalHours"/></td>
          	  <s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE).size!=0">
	              <td class="admin_data_table"><s:text name="label.campaign.meals"/>:</td>
	              <td>
	                <table>
		                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
		                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@MEALS_HOURS_COST_CATEGORY_CODE)">
			                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
							</s:if>  
		                </s:iterator>
	                </table>
		           </td>
               </s:if>
          </tr>
          <tr>
          	<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.campaign.perDiem"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@PER_DIEM_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>
			<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.campaign.rentalCharges"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@RENTAL_CHARGES_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>            
          </tr>
          <tr>
          	<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.campaign.parkingAndToll"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@PARKING_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>
			<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.campaign.itemFreightAndDuty"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@FREIGHT_DUTY_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>            
          </tr>
          <tr>
          	<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.section.localPurchase"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@LOCAL_PURCHASE_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>
			<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.section.tolls"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@TOLLS_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>            
          </tr>
          <tr>
          	<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.section.handlingFee"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@HANDLING_FEE_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>
            	<s:if test="campaignServiceDetail.getCampaignPriceForSection(@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE).size!=0">
              <td class="admin_data_table"><s:text name="label.section.transportation"/>:</td>
              <td>
                <table>
	                <s:iterator value="campaignServiceDetail.campaignSectionPrice" status="iter" id="sectionPrice">
	                	<s:if test="#sectionPrice.sectionName.equals(@tavant.twms.domain.claim.payment.CostCategory@TRANSPORTATION_COST_CATEGORY_CODE)">
		                  <tr><td><s:property value="%{#sectionPrice.pricePerUnit}"/></td></tr>
						</s:if>  
	                </s:iterator>
                </table>
	           </td>
            </s:if>
          </tr>
    </table>
</div>
</div>