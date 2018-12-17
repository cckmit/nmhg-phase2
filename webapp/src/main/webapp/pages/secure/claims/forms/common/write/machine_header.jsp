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
<style>
.official .dijitTextBox,
.official .dijitComboBox,
.official .dijitSpinner,
.official .dijitInlineEditor input,
.official .dijitTextArea {
	margin:0 2px 0 0;
}
input{margin-left:2px;}
</style>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");

    dojo.addOnLoad(function() {
        dojo.subscribe("/multipleInventorySearch/uiHandling", null, function() {        	
            commonUIHandlingForMultiCar();
            var serialNumberCombo = dijit.byId("selectedInvItemsAutoCompleter");
            serialNumberCombo.setDisabled(true);
            dojo.html.hide(serialNumberCombo.domNode);
            dojo.byId("selectedInvItemHidden").disabled=true;
        });

        dojo.subscribe("/multipleInventorySearch/uiHandlingwhilePg2toPg1", null, function() {
            commonUIHandlingForMultiCar();
            dojo.html.hide(dojo.byId("serialNumberDisplay"));
        });

        dojo.subscribe("/multipleInventorySearch/itemsSelected", null, function(message) {
            dojo.body().style.cursor = "auto";
            var validationFailed = (message.responseData == "<true>");
            if (dojo.isIE && message.responseData == "<true>") {
                if (dojo.byId("navigateToTopForErrors")) {
                    dojo.byId("navigateToTopForErrors").click();
                }
            }
            dojo.html.setDisplay(dojo.byId("incompatibleInventoriesErrorSection"), validationFailed);
            if (!validationFailed) {
                dijit.byId("equipmentInfoDiv").setContent(message.responseData);
                dojo.publish("/multipleInventorySearch/hide");
                dojo.publish("/multipleInventorySearch/uiHandling");
            }
        });

        function commonUIHandlingForMultiCar() {
            dojo.html.hide(dojo.byId("hoursInServiceLabel"));
            var hoursInServiceInput = dojo.byId("hoursInService");
            hoursInServiceInput.disabled = true;
            dojo.html.hide(hoursInServiceInput);
            dojo.html.hide(dojo.byId("serialNumberLabel"));
        }
    });
</script>

<s:hidden name="claim"/>
<div>
<table class="form"  cellpadding="0" cellspacing="0" 
style="border-top:1px solid #EFEBF7;
border-bottom:none;border-left:none;
border-right:none;">
    <tbody>
    <tr><td>&nbsp;</td></tr>
           <tr>
            <td width="26%" nowrap="nowrap" >
                <label for="serialNumber" id="serialNumberLabel" class="labelStyle">
                    <s:text name="label.common.serialNumber"/>:
                </label>
            </td>
            <s:if test="claim.id != null">
                <td colspan="3" id="serialNumberDisplay" style="padding-top:20px; padding-left: 14px;">
                    <s:property value="claim.itemReference.referredInventoryItem.serialNumber"/>
                </td>
            </s:if>
            <s:else>
             <td width="24%" >                	
                 <s:hidden name="selectedItems" value="%{claim.itemReference.referredInventoryItem.id}" id="selectedInvItemHidden"/>
                 <sd:autocompleter id='selectedInvItemsAutoCompleter' href='list_claim_sl_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimType=%{claimType}&dealerBrandsOnly=true' showDownArrow='false' name='claim.itemReference.referredInventoryItem' value='%{claim.itemReference.referredInventoryItem.serialNumber}' key='%{claim.itemReference.referredInventoryItem.id}' keyName='claim.itemReference.referredInventoryItem' notifyTopics='invItemChanged/setSerialNumber' listenTopics="/dealer/modified"/>
                 <script type="text/javascript">
                     dojo.addOnLoad(function() {                            
                         dojo.subscribe("invItemChanged/setSerialNumber", null, function(data, type, request) {
                            var selectedInvId = dojo.byId("selectedInvItemHidden").value; 
                            var serialNumber = dojo.byId("selectedInvItemsAutoCompleter").value;                                                                                   
                            if(serialNumber != data){
                                dojo.byId("selectedInvItemHidden").value = dijit.byId("selectedInvItemsAutoCompleter").getValue();                                
                            }
                         });
                     });
                 </script>
                 <img style="display: none;" id="indicator" class="indicator"
                      src="image/indicator.gif" alt="Loading..."/>
             </td>  
             <td  id="viewLink" style="text-align:left" width="25%">
                 <s:if test="multiSerialPerClaimAllowedFlag">
                     <a id="show_multiCar_search" class="link">
                         <s:text name="label.multiCar.searchSerialNumbers"/>
                     </a>
                 </s:if>
             </td>
             <td  width="25%"></td>
             </s:else>
        </tr>       
        <s:if test="(claim==null  || claim.id == null) && nonSerializedClaimAllowed">
            <tr>
                <td colspan="4" id="nonserializedLink" align="right" style="padding-right:18%;">
                    <img src="image/linknonSerialized.gif" border="0" align="absmiddle"/> <a id="file_non_serialized_claim" class="link">
                       <s:text name="label.common.fileNonSerializedClaim"/>
                    </a>
                    <script type="text/javascript">
                        dojo.addOnLoad(function(){
                            dojo.connect(dojo.byId("file_non_serialized_claim"), 'onclick', function() {
                                var form = dojo.byId("form");
                                var selectedBu = dojo.byId("selectedBusinessUnit");
                                form.action = "chooseClaimTypeAndDealer.action?forSerialized=false";
                                form.submit();
                            });
                        })
                    </script>
                </td>
            </tr>
        </s:if>
        <tr>
            <td colspan="4">
                <div id="equipmentInfoDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true" style="padding-bottom: 3px;">
                    <s:if test="claim != null && (claim.claimedItems.size()>1)">
                        <jsp:include page="../write/dealerEquipment.jsp"/>
                    </s:if>
                </div>
            </td>
        </tr>
        
        <tr>
			<td style="height:2px;" colspan="4"></td>
		</tr>
        <tr>
            <td nowrap="nowrap" width="25%">
                <label for="hoursInService" id="hoursInServiceLabel" class="labelStyle">
                     <s:text name="label.common.hoursOnTruck"/>: 
                </label>
            </td>
            <td width="25%" style="padding-left:0;">
                <s:textfield name="claim.hoursInService"  cssStyle="width:145px;" id="hoursInService" theme="simple" />
            </td>
           
            <!--  <td nowrap="nowrap" width="25%"  style="padding-left: 130px;">
              &nbsp;
            </td>
           
            <td width="25%"  style="padding-left: 5px;">
              &nbsp;
            </td> -->
            <td nowrap="nowrap" width="25%"  style="padding-left: 130px;">
            <label for="historicalClaimNumber" id="historicalClaimNumberLabel" class="labelStyle">
                     <s:text name="label.common.historicalClaimNumber"/>: 
                </label> </td>
            <td width="25%" style="padding-left:0;">
                <s:textfield name="claim.histClmNo"  cssStyle="width:145px;" id="histClmNo" theme="simple" />
            </td>
            
        </tr>
        <tr><td style="padding:0;margin:0">&nbsp;</td></tr>
    </tbody>
</table>
</div>
<div>
<jsp:include flush="true" page="datesAndSmr.jsp"/>
</div>
<script type="text/javascript">
    dojo.addOnLoad(function() {
    <s:if test="claim.id != null">
        var isMultipleItem = "<s:property value="claim.claimedItems.size"/>";
        if (isMultipleItem > 1) {
            dojo.publish("/multipleInventorySearch/uiHandlingwhilePg2toPg1");
        }
    </s:if>
    <s:else>
        var isMultipleItem = "<s:property value="claim.claimedItems.size"/>";        
        if (isMultipleItem > 1) {
            dojo.publish("/multipleInventorySearch/uiHandling");
        }
        <s:if test="multiSerialPerClaimAllowedFlag">
            dojo.connect(dojo.byId("show_multiCar_search"), "onclick",
                    function() {
                        dojo.publish("/multipleInventorySearch/show");
                    });
        </s:if>
    </s:else>


    });

</script>