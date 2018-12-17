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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="tda" uri="twmsDomainAware"%>

<s:hidden name="claim" />
<script type="text/javascript">
			dojo.require("dojox.layout.ContentPane");
            dojo.addOnLoad(function() {
            	<s:if test="dealerEligibleToFillSmrClaim">
                var check = dojo.byId("smrCheck");
                if (check.checked) {
                    showSmrReason();
                }
                dojo.connect(check, "onclick", function(evt) {
                    if (evt.target.checked) {
                        showSmrReason();
                    } else {
                        hideSmrReason();
                    }
                });
                </s:if>
              dojo.connect(dojo.byId("multi_campaign_claim"),"onclick",function(){
                form=dojo.byId("campaignThirdPartyForm");               
                form.action="multiCampaignClaim.action";
                form.submit();
              });
            });
            </script>

<table class="form" border="0" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td><label for="serialNumber"> <s:text
				name="label.common.serialNumber" />: </label></td>
				
			<td>
			<s:if test="claim.id != null " >
	            <s:property value="claim.itemReference.referredInventoryItem" />
	            <s:hidden name="campaign"/>	
	            <input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem" 
	            	value="<s:property value="claim.itemReference.referredInventoryItem.id"/>"/>						
       		</s:if>
       		<s:else>
				<s:if test="campaign.id!=null ||(campaign.id!=null && 
					(fromPendingCampaign.equals(true) || campaign.claim!=null))">
				<input type="hidden" name="campaign" value="<s:property value="campaign"/>"/>
				<s:if test="claim==null || claim.itemReference.referredInventoryItem==null">
					<s:property value="campaignNotification.item.serialNumber"/>
					<input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem" 
	            	value="<s:property value="campaignNotification.item.id"/>"/>
				</s:if>
				<s:else>
					<s:property value="claim.itemReference.referredInventoryItem" />
					<input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem" 
	            	value="<s:property value="claim.itemReference.referredInventoryItem"/>"/>	
				</s:else>						
				</s:if>
				<s:else>
				<sd:autocompleter id='serialNumber' href='list_campaign_claim_sl_nos.action' name='claim.claimedItems[0].itemReference.referredInventoryItem' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' value='%{claim.claimedItems[0].itemReference.referredInventoryItem.serialNumber}' indicator='indicator' />
	            <img style="display: none;" id="indicator" class="indicator"
	                 src="image/indicator.gif" alt="Loading..."/>	           
	            <script type="text/javascript">
	            dojo.addOnLoad(function(){
	            	dojo.connect(dijit.byId("serialNumber"),"onChange",function(){
	            	var serialNumber =dijit.byId("serialNumber").getValue();           				
					var params={"claim.claimedItems[0].itemReference.referredInventoryItem.serialNumber":serialNumber};
					var url = "getCampaignCodesForInventory.action?";	
					twms.ajax.fireHtmlRequest(url, params, function(data) {			
						dijit.byId('campaignCodeInfoDiv').setContent(data); 				
						}); 
	            	});
	            });
	            </script>	            
				</s:else>
			</s:else>
			</td>
			<td colspan="2" id = "viewLink">
				<a id="multi_campaign_claim" class="link">				
	            	<s:text name="label.campaign.multiClaim"/>				
				</a>				
			</td>
		</tr>
		<tr>
			<td>
				<label for="serialNumber"> <s:text
				name="label.common.campaignCode" />: </label>
			</td>			
			<td>
				<s:if test="campaign==null || campaign.id==null">
				<div id="campaignCodeInfoDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true" >
                </div>
                </s:if>
                <s:else>
                	<s:property value="campaign.code"/>
                </s:else>
			</td>
		</tr>

		<tr>
			<td><label for="workOrderNumber"> <s:text
				name="label.claim.workOrderNumber" />: </label></td>
			<td align="left"><s:textfield name="claim.workOrderNumber"
				id="workOrderNumber" /></td>

			<td colspan="2" id="toggle" />
		</tr>

		<tr>
			<td><label for="hoursInService"><s:text
				name="label.common.hoursOnTruck" />:</label></td>
			<td align="left" colspan="8"><s:textfield
				name="claim.hoursInService" id="hoursInService" cssClass="numeric" />
			</td>
			<td colspan="2" id="toggle" />
		</tr>	
		
	</tbody>
</table>

<table class="form" border="0" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
		<td><label for="repairStartDate"> <s:text
				name="label.viewClaim.repairStartDate" />: </label></td>
			<td><sd:datetimepicker name='claim.repairStartDate' value='%{claim.repairStartDate}' id='repairStartDate' /></td>
			<td><label for="dateOfRepair"> <s:text
				name="label.viewClaim.dateOfRepair" />: </label></td>
			<td><sd:datetimepicker name='claim.repairDate' value='%{claim.repairDate}' id='dateOfRepair' /></td>
		</tr>
		<s:if test="dealerEligibleToFillSmrClaim">
		<tr>
			<td><label for="isSmr"> <s:text
				name="label.viewClaim.requestSMR" />: </label></td>
			<td align="center">&nbsp;<s:checkbox name="claim.serviceManagerRequest"
				id="smrCheck" cssClass="checkbox" /></td>
		</tr>
		<tr>
			<td><label for="smrReason" id="smrReasonLabel"
				style="display:none"> <s:text
				name="label.viewClaim.smrReason" />: </label></td>
			<td><span id="smrReasonSpan"> <tda:lov
				name="claim.reasonForServiceManagerRequest" id="smrReason"
				className="SmrReason"  cssStyle="width:360px;" /> </span> <span id="dummySmr"> <input
				type="hidden" name="claim.reasonForServiceManagerRequest"
				value="null" /> </span></td>
		</tr>
		</s:if>
			<tr>
	<s:if test="claim.cmsAuthCheck">
       <td style="padding-bottom:5px;" nowrap="nowrap">
            <label id="authNumberLabel" class="labelStyle"> <s:text name="label.viewClaim.authNumber"/>: </label>
        </td> 
        <td><s:property value="claim.authNumber" />
        </td>
   </s:if>  
    <s:if test="claim.getCmsTicketNumber()!= null && !claim.getCmsTicketNumber().isEmpty()">
        <td class="labelStyle" style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.cmsTicket"/>:
        </td>
        <td style="padding-bottom:5px;" nowrap="nowrap">
        <%--<a href="<s:property value='applicationSettings.cmsUrl'/><s:property value='claim.cmsTicketNumber'/>" target="_new"> --%>
        <s:property value="claim.cmsTicketNumber"/>
        <!-- </a> -->
        </td>
   </s:if>
   </tr>   
	</tbody>
</table>
