<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 26, 2008
  Time: 6:42:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
</script>
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
<!--This parameter checks if the claim is serialized or not and since this page is loaded only for non serialized claim the
value is explicitly set to false-->
<s:hidden name="forSerialized" value="false"/>
<s:hidden name="claim"/>

<!-- This is a hack since this flow requires inventoryItem number -->
<%-- <s:if test="claim==null || claim.id==null"> --%>
<%-- <s:hidden name="claim.itemReference.referredInventoryItem" /> --%>
<%-- </s:if> --%>
<table class="form"  cellpadding="0" cellspacing="0" style="border-top:1px solid #EFEBF7; border-bottom:none; border-left:none; border-right:none;">
    <tbody>
    <tr><td style="padding:0;margin:0">&nbsp;</td></tr>
        <tr>
            <td width="26%" >
                <label for="modelNumber" id="modelNumberLabel" class="labelStyle">
                    <s:text name="label.common.baseModelName"/>:
                </label>
            </td>
			<s:if test="claim.id != null" >
				<td colspan="3" id="modelNameDisplay">
	            	<s:property value="claim.itemReference.model.name"  />
				</td>
            </s:if>
            <s:else>
            	<td width="35%">
            		<s:hidden name="claim.itemReference.model" value="%{claim.itemReference.model.id}" id="selectedModelHidden"/>
            	  	<sd:autocompleter id='modelNumber' href='list_claim_model_names.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='claim.itemReference.model' showDownArrow='false' indicator='indicator' key='%{claim.itemReference.model.id}' keyName='claim.itemReference.model' value='%{claim.itemReference.model.name}' notifyTopics='modelChanged/setModelId' loadMinimumCount='2' />	
	                 
	                <script type="text/javascript">
                        dojo.addOnLoad(function() {       
		                        <s:if test="isItemNumberDisplayRequired()">
		                        	dijit.byId("modelNumber").setDisabled(true);
		                        </s:if>	
		                       dojo.subscribe("modelChanged/setModelId", null, function(data, type, request) {
                               var selectedModelId = dojo.byId("selectedModelHidden").value;                                                                                                     
                               if(selectedModelId != data ){
                                   dojo.byId("selectedModelHidden").value = dijit.byId("modelNumber").getValue();                                
                               }
                            });
                        });
                    </script>     
	            </td>
	            <td colspan="2" id="nonserializedLink">
                    <img border="0" align="absmiddle" src="image/linknonSerialized.gif"/> <a id="file_serialized_claim" class="link">
                       <s:text name="label.common.fileSerializedClaim"/>
                    </a>
                    <script type="text/javascript">
                        dojo.addOnLoad(function(){
                            dojo.connect(dojo.byId("file_serialized_claim"), 'onclick', function() {
                            var dealer = <s:property value='%{getLoggedInUser().getCurrentlyActiveOrganization().id}'/>
                            <s:if test="isLoggedInUserADealer() && displayBrandDropDown()">
                             getDealerBrands(dealer,dijit.byId("brandtype"));
                            </s:if>
                                var form = dojo.byId("form");                                
                                form.action = "chooseClaimTypeAndDealer.action?forSerialized=true";
                                form.submit();
                            });
                        })
                    </script>
                </td>
            </s:else>
        </tr>
        
       <s:if test="isItemNumberDisplayRequired()">
        <tr><td>
                <label for="itemNumber" id="itemNumberLabel" class="labelStyle">
                    <s:text name="label.common.itemNumber"/>:
                </label>
            </td>
            <s:if test="claim.id != null" >
				<td  id="itemNumberDisplay" width="35%">
	            	<s:property value="claim.itemReference.referredItem.number" />
				</td>
            </s:if>
            <s:else>
            <td width="35%">
             <sd:autocompleter href='list_claim_item_numbers.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimType=%{claimType}' name='claim.itemReference.referredItem' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' value='%{claim.itemReference.referredItem.alternateNumber}' id='itemNumber' listenTopics='itemNumber/queryAddParams' notifyTopics='itemNumberChanged/description' />
	                     <img style="display: none;" id="indicator" class="indicator"
	                     src="image/indicator.gif" alt="Loading..."/> 
	            <script type="text/javascript">
	            var isOnLoad=true;
                dojo.addOnLoad(function() {                   
                    dojo.subscribe("itemNumberChanged/description", null, fillItemDescription);
                });
                function fillItemDescription(data, type, request) {                	
                    var params = {};
                    params["number"] = data;
                    dojo.byId("itemDescriptionDisplay").innerHTML = '';
                    dijit.byId("modelNumber").setDisplayedValue('');
    	            dijit.byId("modelNumber").setValue('');
    	            dojo.byId("selectedModelHidden").value = '';
                    twms.ajax.fireJsonRequest("findItemDescription.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params, function(details) {
                        if (details) {
                            dojo.byId("itemDescriptionDisplay").innerHTML = details[0];                            
                            dijit.byId("modelNumber").setDisplayedValue(details[1]);
    	                    dijit.byId("modelNumber").setValue(details[2]);
    	                    dojo.byId("selectedModelHidden").value = details[2];
                        }
                    });
                }
                </script>
	         </td>
             </s:else>
            <td class="labelStyle">
                <s:text name="label.common.description"/>:
            </td>
            <s:if test="claim.id != null" >
                    <td  id="itemNumberDescription">
                        <s:property value="claim.itemReference.referredItem.description"/>
                    </td>
                </s:if>
            <s:else>
                <td id="itemNumberDescription">
                        <span id="itemDescriptionDisplay"></span>
                </td>
            </s:else>
        </tr> 
       </s:if>
        <tr><s:if test="displayBrandDropDown()">
            <td width="15%">
                <label for="type" class="labelStyle"><s:text name="label.common.claimBrand"/>:</label>
            </td>
          
             <td style="padding-top:5px;" valign="top"> 
              
            

            <sd:autocompleter id='brandtype' listenTopics='dealer_brand' name='claim.brand' /> 
     <script type="text/javascript">
                        dojo.addOnLoad(function(){
                            
                            var dealer = <s:property value='%{getLoggedInUser().getCurrentlyActiveOrganization().id}'/>
                                                        <s:if test="isLoggedInUserADealer()">
                                                        getDealerBrands(dealer,dijit.byId("brandtype"));
                            </s:if>
                               
                        })
                    </script> </td>
            </s:if>
            <s:else>
                <s:hidden name="claim.brand" id="brandType" value="%{claim.brand}"/>
            </s:else>

<!--             <td class="labelStyle"> 
             	<label id="serialNumberForNonSerializedClaimLabel"  class="labelStyle" hidden="true"> 
                    <s:text name="label.common.serialNumber"/>: 
                 </label>
             </td> 
            <s:if test="claim.id != null" >
           	<td class="labelStyle"> 
             		<s:if test="claim.itemReference.unszdSlNo!=null && claim.itemReference.unszdSlNo!=''"> 
	            		<s:property value="claim.itemReference.unszdSlNo" /> 
 	            	</s:if> 
 	            	<s:else> 
	            		
            	</s:else> 
 	            	<s:hidden name="claim.itemReference.unszdSlNo" /> 
             	</td> 
             </s:if> >
          <s:else > 
             	<td><s:textfield id="invalidSerialNumber" name="claim.itemReference.unszdSlNo" cssStyle="width:145px;" hidden="true"/></td> 
             </s:else> -->
        </tr>
        
        <tr>
            <td>
                <label for="hoursInService" id="hoursInServiceLabel" class="labelStyle">
                     <s:text name="label.common.hoursOnTruck"/>: 
                </label>
            </td>
             <td nowrap="nowrap" style="padding-bottom:5px;padding-right: 75px;"" >
                <s:textfield name="claim.hoursInService" id="hoursInService" theme="simple" cssStyle="width:145px;" />
            </td> 
            <s:if test="invoiceNumberApplicable && claim.invoiceNumber !=null">
            <td>
                <label for="invoiceNumber" id="invoicNumberLabel" class="labelStyle">
                    <s:text name="label.claim.invoiceNumber"/>:
                </label>
            </td>
            <td>
                <s:textfield name="claim.invoiceNumber" id="invoiceNumber" theme="simple" cssStyle="width:145px;" />
            </td>
            </s:if>
            <s:else>
            	<td>
                <label for="invoiceNumber" id="invoicNumberLabel" class="labelStyle">
                    <s:text name="label.claim.invoiceNumber"/>:
                </label>
            </td>
            <td>
                <s:textfield name="claim.invoiceNumber" id="invoiceNumber" theme="simple" cssStyle="width:145px;" />
            </td>
            </s:else>
        </tr>
        <tr>
            <s:if test="isDateCodeEnabled()">
                <td>
                    <label for="dateCode" id="dateCodeLabel" class="labelStyle">
                        <s:text name="label.common.dateCode"/>:
                    </label>
                </td>
                <td>
                    <s:textfield name="claim.dateCode" id="dateCode" theme="simple" cssStyle="width:145px;"  />
                </td>
            </s:if>
            <td  style="padding-right: 46px;">
                <label for="dateOfPurchase" id="dateOfPurchaseLabel" class="labelStyle">
                    <s:text name="label.common.dateOfPurchase"/>:
                </label>
            </td>
            <td>
                <sd:datetimepicker name='claim.purchaseDate' value='%{claim.purchaseDate}' id='dateOfPurchase' />
            </td>
        </tr>
        <tr><td style="padding:0;margin:0">&nbsp;</td></tr>
    </tbody>
</table>

<jsp:include flush="true" page="datesAndSmr.jsp" />

