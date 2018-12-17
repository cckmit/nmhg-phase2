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
<%@taglib prefix="u" uri="/ui-ext"%>

<s:hidden name="claim" />
<script type="text/javascript">
			dojo.require("dojox.layout.ContentPane");
            dojo.addOnLoad(function() {
            	 var checkAuth = dojo.byId("cmsAuthCheck");
                 if (checkAuth && checkAuth.checked) {
                 	document.getElementById("authNumber").disabled = false;
                 }
                 else{
                     if(document.getElementById("authNumber")!=null){
                 	   document.getElementById("authNumber").disabled = true;
                     }
                 	}
              	 if(checkAuth){
                 dojo.connect(checkAuth, "onclick", function(evt1) {
                     if (evt1.target.checked) {
                     	document.getElementById("authNumber").disabled = false;
                     	setHiddenValue();
                     } else {
                     	document.getElementById("authNumber").disabled = true;
                     	document.getElementById("authNumber1").value=null;
                       	setHiddenValue();
                     }
                   });
              	 }

            	if(dojo.byId("campaignThirdPartyForm")!=null){
            		dojo.html.hide(dojo.byId("viewLink"));
            	}
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
            });
            </script>
			<style>
				.dijitTextBox, .dijitComboBox, .dijitSpinner{
				width:145px;
				}
			</style>

      <s:if test="multiSerialPerClaimAllowedFlag">
	      <script type="text/javascript">
	      dojo.addOnLoad(function() {
		      dojo.connect(dojo.byId("multi_campaign_claim"),"onclick",function(){
		        form=dojo.byId("campaignForm");
		        form.action="multiCampaignClaim.action";
		        form.submit();
		      });
	      });
	      function setHiddenValue(){
              document.getElementById("claim.authNumber").value = document.getElementById("authNumber1").value;
         }
	      </script>
      </s:if>

<table class="form" border="0" cellpadding="0" cellspacing="0" style="border-top:1px solid #EFEBF7;
border-bottom:none;border-left:none;
border-right:none;">
	<tbody>
	<tr><td style="margin:0;padding:0" colspan="4">&nbsp;</td></tr>
		<tr>
			<td width="26%" nowrap="nowrap" ><label for="serialNumber" class="labelStyle"> <s:text
				name="label.common.serialNumber" />: </label></td>

			<td width="35%">
			<s:if test="claim.id != null " >
	            <s:property value="claim.itemReference.referredInventoryItem.serialNumber" />
	            <s:hidden name="campaign" value="%{claim.campaign.id}"/>
	            <input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem"
	            	value="<s:property value="claim.itemReference.referredInventoryItem.id"/>"/>
       		</s:if>
       		<s:else>
				<s:if test="campaign.id!=null ||(campaign.id!=null &&
					(fromPendingCampaign.equals(true) || campaign.claim!=null))">
				<input type="hidden" name="campaign" value="<s:property value="campaign"/>"/>
				<input type="hidden" name="campaignNotification" value="<s:property value="campaignNotification"/>"/>
				<s:if test="claim==null || claim.itemReference.referredInventoryItem==null">
					<s:property value="campaignNotification.item.serialNumber"/>
					<input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem"
	            	value="<s:property value="campaignNotification.item.id"/>"/>
				</s:if>
				<s:else>
					<s:property value="claim.itemReference.referredInventoryItem.serialNumber" />
					<input type="hidden" name="claim.claimedItems[0].itemReference.referredInventoryItem"
						value="<s:property value="claim.itemReference.referredInventoryItem.id"/>"/>
				</s:else>
				</s:if>
				<s:else>
				<sd:autocompleter id='serialNumber' href='list_campaign_claim_sl_nos.action' name='claim.claimedItems[0].itemReference.referredInventoryItem' cssStyle='width:145px;' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' value='%{claim.claimedItems[0].itemReference.referredInventoryItem.serialNumber}' key='%{claim.claimedItems[0].itemReference.referredInventoryItem.id}' keyName='claim.claimedItems[0].itemReference.referredInventoryItem' indicator='indicator'/>
	            <img style="display: none;" id="indicator" class="indicator"
	                 src="image/indicator.gif" alt="Loading..."/>
	            <script type="text/javascript">
	            dojo.addOnLoad(function(){
	            	dojo.connect(dijit.byId("serialNumber"),"onChange",function(){
	            	var serialNumber =dijit.byId("serialNumber").getDisplayedValue();
					var params={"serialNumberForCampaignCodes":serialNumber};
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
			<td colspan="2" id="viewLink" align="left">
				<s:if test="multiSerialPerClaimAllowedFlag">
					<img border="0" align="absmiddle" src="image/linknonSerialized.gif"/>
		            <a id="multi_campaign_claim" class="link"> <s:text name="label.campaign.multiClaim"/></a>
				</s:if>
			</td>
		</tr>
		<tr>
			<td width="26%" nowrap="nowrap">
				<label for="serialNumber" class="labelStyle"> <s:text
				name="label.common.campaignCode" />: </label>
			</td>
			<td>
				<s:if test="claim.id !=null && claim.campaign !=null">
					<s:property value="claim.campaign.code"/>
				</s:if>
				<s:elseif test="campaign==null || campaign.id==null">
					<div id="campaignCodeInfoDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client"
	                     executeScripts="true" >
	                </div>
                </s:elseif>
                <s:else>
                	<s:property value="campaign.code"/>
                </s:else>
			</td>
			<td colspan="2"></td>
		</tr>

		<tr>
		 <s:if test="%{showDealerJobNumber()}">
			<td nowrap="nowrap" width="26%">
				<label for="workOrderNumber" class="labelStyle"> <s:text name="label.claim.workOrderNumber" />: </label></td>
			<td width="35%" style="padding-left:7px; padding-left:3px\9;">
			<s:textfield name="claim.workOrderNumber" id="workOrderNumber" cssStyle="width:145px;" /></td>
		  </s:if>
			<s:if test="%{displayCPFlagOnClaimPgOne}">
			<td width="15%" nowrap="nowrap">
				<label for="commercialPolicy" id="commercialPolicy" class="labelStyle"> <s:text
					name="label.newClaim.commercialPolicy" />: </label></td>
			<td nowrap="nowrap"><s:checkbox	name="claim.commercialPolicy" id="comPolicyCheck" cssClass="checkbox" /></td>
			</s:if>
			<s:else>
			<td width="15%" nowrap="nowrap"></td>
			<td nowrap="nowrap"></td>
			</s:else>
		</tr>
		<tr>
			<td colspan="4" style="height:5px;"></td>
		</tr>
		<tr>
			<td nowrap="nowrap" width="26%"><label for="hoursInService" class="labelStyle"><s:text
				name="label.common.hoursOnTruck" />:</label></td>
			<td width="35%" style="padding-left:7px; padding-left:3px\9;"><s:textfield name="claim.hoursInService" id="hoursInService" theme="simple" cssStyle="width:145px;"/></td>
			<td width="20%" nowrap="nowrap">
            <label for="historicalClaimNumber" id="historicalClaimNumberLabel" class="labelStyle">
                     <s:text name="label.common.historicalClaimNumber"/>: 
                </label> : </td>
            <td width="35%">
                <s:textfield name="claim.histClmNo"  cssStyle="width:145px;" id="histClmNo" theme="simple" />
            </td>
		</tr>
		<tr><td style="margin:0;padding:0" colspan="4">&nbsp;</td></tr>
	</tbody>
</table>

<table class="form" border="0" cellpadding="0" cellspacing="0" style="border-top:1px solid #EFEBF7; border-bottom:none; border-left:none; border-right:none;">
	<tbody>
		<tr><td style="margin:0;padding:0" colspan="4">&nbsp;</td></tr>
		<tr>
			<td width="26%" nowrap="nowrap"><label for="repairStartDate" class="labelStyle"> <s:text name="label.viewClaim.repairStartDate" />: </label></td>
			<td width="35%"><sd:datetimepicker name='claim.repairStartDate' value='%{claim.repairStartDate}' id='repairStartDate' /></td>
			<td width="20%" nowrap="nowrap"><label for="dateOfRepair" class="labelStyle"> <s:text name="label.viewClaim.dateOfRepair" />: </label></td>
			<td width="35%"><sd:datetimepicker name='claim.repairDate' value='%{claim.repairDate}' id='dateOfRepair' /></td>
		</tr>
		<tr>
			<td>
			<td>
			 <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="cmsTicketLabel" class="labelStyle">
                    <s:text name="label.viewClaim.cmsTicket"/>:
                </label>
             </td>
             <td>
           	   <s:textfield name="claim.cmsTicketNumber" cssStyle="width:145px;" id="cmsTicketNumber" value='%{claim.cmsTicketNumber}' maxlength="13"  theme="simple" />
             </td>

       </tr>
        <s:if test="%{showAuthorizationReceived()}">
       <tr><td nowrap="nowrap"width="26%">
            	<label for="cmsAuthLabel" class="labelStyle">
                    <s:text name="label.viewClaim.cmsAuth"/>
                </label>
            </td>
            <td width="35%" style="padding-left:8px; style="padding-left:0;">
					<s:checkbox name="claim.cmsAuthCheck" id="cmsAuthCheck" cssClass="checkbox"/>
            </td>
        </tr>
        </s:if>
        <s:if test="%{showAuthorizationNumber()}">
        <tr>
            <td style="padding-bottom:5px;" nowrap="nowrap">
            	<label id="authNumberLabel" class="labelStyle">
                    <s:text name="label.viewClaim.authNumber"/>:
                </label>
             </td>
            <td>
           	  <s:textfield name="claim.authNumber" cssStyle="width:145px;" id="authNumber" theme="simple" maxlength="255" />
            </td>
         </tr>
          </s:if>

          <s:if test="isLoggedInUserAnAdmin() && enableWarrantyOrderClaims()">
                                             <td style="padding-bottom:5px;" nowrap="nowrap">
                                            	<label id="cmsTicketLabel" class="labelStyle">
                                                    <s:text name="label.viewClaim.warrantyOrderClaim"/>:
                                                </label>
                                             </td>
                                             <td style="padding-bottom:5px;" nowrap="nowrap">
                                               <s:checkbox name="claim.warrantyOrder" cssClass="checkbox"/>
                                             </td>

         </s:if>

		<tr>
					</tr>
      <s:if test="dealerEligibleToFillSmrClaim">
		<tr><td nowrap="nowrap"width="26%"><label for="isSmr" class="labelStyle"> <s:text
				name="label.viewClaim.requestSMR" />: </label></td>
			<td width="35%" style="padding-left:8px;"><s:checkbox name="claim.serviceManagerRequest" id="smrCheck" cssClass="checkbox" /></td>
       </tr>
       <tr><td nowrap="nowrap" width="15%"><label id="smrReasonLabel" style="display:none" class="labelStyle"> <s:text
				name="label.viewClaim.smrReason" />: </label>
			</td>
			<td><span id="smrReasonSpan"> <tda:lov
				name="claim.reasonForServiceManagerRequest" id="smrReason"
				className="SmrReason" cssStyle="width:360px;" /> </span> <span id="dummySmr"> <input
				type="hidden" name="claim.reasonForServiceManagerRequest"
				value="null" /> </span>
		  </td>
       </tr>
       </s:if>
		<tr>
		  <td style="margin:0;padding:0">&nbsp;
		  </td>
		</tr>
	</tbody>
</table>
