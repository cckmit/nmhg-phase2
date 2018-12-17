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
    dojo.connect(dijit.byId("campaign"),"onChange",function(){    	    
    	dojo.byId("campaign_code").value=dijit.byId("campaign").getDisplayedValue();
    	if(dojo.byId("viewLink")){
    		dojo.html.show(dojo.byId("viewLink"));
    	}
    });
    dojo.subscribe("/multipleInventorySearch/itemsSelected", null, function(message) {  
        	var validationFailed = (message.responseData=="<true>");        	
			if(dojo.isIE && message.responseData=="<true>"){	
				if(dojo.byId("navigateToTopForErrors")){			
						dojo.byId("navigateToTopForErrors").click();
				}
			}					
        	dojo.html.setDisplay(dojo.byId("incompatibleInventoriesErrorSection"), validationFailed);
        	if(!validationFailed){
				dijit.byId("equipmentInfoDiv").setContent(message.responseData);				
			 	dojo.publish("/multipleInventorySearch/hide");
			}
        		
    });   
    
    <s:if test="multiSerialPerClaimAllowedFlag">
     dojo.connect(dojo.byId("show_multiCar_search_link"), "onclick",function() {
            		dojo.publish("/multipleInventorySearch/show");
				});	
	</s:if>			
				
});
</script>
<style>
	.dijitTextBox, .dijitComboBox, .dijitSpinner{
			width:145px;
		}

</style>

<table class="form" border="0" cellpadding="0" cellspacing="0" style="border-top:1px solid #EFEBF7">
	<tbody>	
	<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
		<tr>
			<td colspan="4">
                <div id="equipmentInfoDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true" style="padding-bottom: 3px;">
                     <s:if test="claim.claimedItems.size>0">                               
                        <jsp:include page="../write/dealerEquipment.jsp" />
                      </s:if>     
                </div>
			</td>
		</tr>	
		<tr>			
			<td width="22%" nowrap="nowrap">
				<label for="serialNumber" class="labelStyle"> <s:text
				name="label.common.campaignCode" />: </label>
			</td>			
			<td width="34%">
			<s:if test="campaign!=null">
	            <s:property value='campaign.code' />
	            <s:hidden name="campaign"  />
	            <script type="text/javascript">
	                    dojo.addOnLoad(function() {	                   
	                   	dojo.byId("campaign_code").value='<s:property value="campaign.code"/>';
	                    });
                </script>
       		</s:if>
       		<s:else>
       			<sd:autocompleter id='campaign' name='campaign' href='list_campaign_codes.action' listenTopics='campaignCode/search' loadMinimumCount='1' showDownArrow='false' value='%{campaign.code}' />
          </s:else>
			</td>
            <s:if test="claim.id==null">
			<td colspan="2" id = "viewLink" style="display : none">
            <s:if test="multiSerialPerClaimAllowedFlag">
				<a id="show_multiCar_search_link" class="link">				
	            	<s:text name="label.multiCar.searchSerialNumbers"/>
				</a>
                <script type="text/javascript">
                    dojo.addOnLoad(function(){
                       if(dojo.byId("campaign_code").value){
                           if(dojo.byId("campaign_code").value.value!=''){
                               dojo.html.show(dojo.byId("viewLink"));
                           }
                       }
                    });
                </script>
            </s:if>
			</td>
			</s:if>
		</tr>		
		<tr>
			<td nowrap="nowrap" width="21%"><label for="workOrderNumber" class="labelStyle"> <s:text
				name="label.claim.workOrderNumber" />: </label></td>
			<td align="left" style="padding-left:12px;"><s:textfield name="claim.workOrderNumber"
				id="workOrderNumber" cssStyle="width:145px;" /></td>

			<td colspan="2" id="toggle" />
		</tr>
		<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
	</tbody>
</table>

<table class="form" border="0" cellpadding="0" cellspacing="0" style="border-top:1px solid #EFEBF7">
	<tbody>
	<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
		<tr>
		<td nowrap="nowrap" width="22%"><label for="repairStartDate" class="labelStyle"> <s:text
				name="label.viewClaim.repairStartDate" />: </label></td>
			<td width="34%"><sd:datetimepicker name='claim.repairStartDate' value='%{claim.repairStartDate}' id='repairStartDate' /></td>
			<td nowrap="nowrap" width="22%"><label for="dateOfRepair" class="labelStyle"> <s:text
				name="label.viewClaim.dateOfRepair" />: </label></td>
			<td width="34%"><sd:datetimepicker name='claim.repairDate' value='%{claim.repairDate}' id='dateOfRepair' /></td>
		</tr>
		<s:if test="dealerEligibleToFillSmrClaim">
		<tr>
			<td nowrap="nowrap"><label for="isSmr" class="labelStyle"> <s:text
				name="label.viewClaim.requestSMR" />: </label></td>
			<td width="34%">&nbsp;&nbsp;<s:checkbox name="claim.serviceManagerRequest"
				id="smrCheck" cssClass="checkbox" /></td>
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
        <%-- <a href="<s:property value='applicationSettings.cmsUrl'/> 
        <s:property value='claim.cmsTicketNumber'/>" target="_new"> --%>
        <s:property value="claim.cmsTicketNumber"/>
        <!-- </a> -->
        </td>
   </s:if>
   </tr>   
		<tr>
			<td nowrap="nowrap" width="15%"><label for="smrReason" id="smrReasonLabel"
				style="display:none" class="labelStyle"> <s:text
				name="label.viewClaim.smrReason" />: </label></td>
			<td><span id="smrReasonSpan"> <tda:lov
				name="claim.reasonForServiceManagerRequest" id="smrReason"
				className="SmrReason" cssStyle="width:360px;" /> </span> <span id="dummySmr"> <input
				type="hidden" name="claim.reasonForServiceManagerRequest"
				value="null" /> </span></td>
		</tr>
		<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
	</tbody>
</table>
