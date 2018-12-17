<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@ taglib prefix="authz" uri="authz" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
     
 </head>
 <u:actionResults/>
 <u:stylePicker fileName="inboxLikeButton.css"/>
 <style>
 input{
 border:none;
 }
 </style>
  <script type="text/javascript">  	  	
      	dojo.require("dijit.layout.LayoutContainer"); 
      	dojo.require("dijit.layout.ContentPane");

      dojo.addOnLoad(function(){
          dojo.connect(dojo.byId("equipmentHistory"), "onclick", function() {
              if (dojo.byId("equipmentHistory").checked) {
                  formObj.action = "viewEquipmentHistoryForQuickSearch.action?action=EQUIPMENT HISTORY";
              }
          });
          dojo.connect(dojo.byId("warrantyRegistration"), "onclick", function() {
              if (dojo.byId("warrantyRegistration").checked) {
                  formObj.action = "warrantyRegistrationForQuickSearch.action?" +
                        "action=DELIVERY REPORT" + "&allowInventorySelection=true";
              }
          });
          dojo.connect(dojo.byId("dealerToDealer"), "onclick", function() {
             if(dojo.byId("dealerToDealer").checked){
	        		formObj.action = "dealerToDealerForQuickSearch.action?&action=DEALER TO DEALER"
	        			 + "&allowInventorySelection=true";
	        	}
          });
          dojo.connect(dojo.byId("warrantyTransfer"), "onclick", function() {
              if(dojo.byId("warrantyTransfer").checked){
	        		formObj.action = "equipmentTransferForQuickSearch.action?" +
	        		      "action=EQUIPMENT TRANSFER" + "&allowInventorySelection=true";
	        	}
          });
          dojo.connect(dojo.byId("createClaim"), "onclick", function() {
             if(dojo.byId("createClaim").checked){
	        		formObj.action = "createClaimForQuickSearch.action?claim.forDealer=<s:property value='%{@tavant.twms.web.common.SessionUtil@getDealerFromSession(#session).name}'/>&action=CREATE CLAIM";
	        	}
          });
          if (dojo.byId("extendedWarrantyPurchase")) {
              dojo.connect(dojo.byId("extendedWarrantyPurchase"), "onclick", function() {
                  if (dojo.byId("extendedWarrantyPurchase").checked) {
                      formObj.action = "extendedWarrantyPurchaseForQuickSearch.action?action=EXTENDED WARRANTY" + "&allowInventorySelection=true";
                  }
              });
          }
          
          if (dojo.byId("retailMachineTransfer")) {
              dojo.connect(dojo.byId("retailMachineTransfer"), "onclick", function() {
                  if (dojo.byId("retailMachineTransfer").checked) {
                      formObj.action = "retailMachineTransferForQuickSearch.action?action=RETAIL MACHINE TRANSFER" + "&allowInventorySelection=true";
                  }
              });
          }
      });
        
    </script>

<u:body>
  	<s:form name="quickSearchInvetoryForm" id="quickSearchInvetoryForm" action="">
	<s:hidden name="context" />
	<div class="policy_section_div">
	<div  class="section_header">        
        	  		<s:text name="label.common.quickInventorySearch"/>
       		</div>
		<table width="40%" border="0" cellspacing="0" cellpadding="0" class="grid" >
			 <tr> 
			 	 <td  class="labelStyle" width="12%" nowrap="nowrap"><s:text name="label.common.serialNumber" />:</td>
       	 	 	 <td nowrap="nowrap" width="27%">
       	 	 	 	<s:textfield name="serialNumber" id="serialNumber" cssStyle="border:1px solid #AAAAAA"/>
       	 	 	 </td>	
			</tr>
			<tr>
				<td nowrap="nowrap" colspan="2" >
					<input type="radio" name="actionType" id="equipmentHistory" value="equipmentHistory" checked>
                    <s:text name='label.inventory.equipmentHistory' />
                    <script type="text/javascript">
                        var formObj = dojo.byId("quickSearchInvetoryForm");
                        formObj.action="viewEquipmentHistoryForQuickSearch.action?action=EQUIPMENT HISTORY";
                    </script>
                </td>
			</tr>
			<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
			<authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,inventoryAdmin,dealerSalesAdministration">
			<tr>
				<td nowrap="nowrap" colspan="2" >
					<input type="radio" name="actionType" id="warrantyRegistration" 
					value="warrantyRegistration" > 
					<s:text name='viewInbox_jsp.inboxButton.new_warranty_registration' />
				</td>
			</tr>
			<tr>
			<authz:ifUserInRole roles="inventoryAdmin">
			<td nowrap="nowrap" colspan="2">
					<input type="radio" name="actionType" id="dealerToDealer" value="dealerToDealer" > 
					<s:text name='label.inventory.dealerTodealer' />
				</td>	
			</authz:ifUserInRole>
			<authz:ifUserNotInRole roles="inventoryAdmin">
			<s:if test="isD2DAllowed()">
				<td nowrap="nowrap" colspan="2">
					<input type="radio" name="actionType" id="dealerToDealer" value="dealerToDealer" > 
					<s:text name='label.inventory.dealerTodealer' />
				</td>	
			</s:if>
			</authz:ifUserNotInRole>
			</tr>
			<tr>
				<td nowrap="nowrap" colspan="2">
					<input type="radio" name="actionType" id="warrantyTransfer" value="warrantyTransfer" > <s:text name='viewInbox_jsp.inboxButton.warranty_transfer' />
				</td>
			</tr>
                <s:if test="eligibleForExtendedWarrantyPurchase">
                    <tr>
                        <td nowrap="nowrap" colspan="2">
                            <input type="radio" name="actionType" id="extendedWarrantyPurchase"
                                   value="extendedWarrantyPurchase"> <s:text
                                name='summaryTable.inboxButton.purchase_warranty'/>
                        </td>
                    </tr>
                </s:if>
            
			</authz:ifUserInRole>
			<authz:ifUserInRole roles="dealerWarrantyAdmin">
				<tr>
					<td nowrap="nowrap" colspan="2">
						<input type="radio" name="actionType" id="createClaim" value="createClaim" > <s:text name='home_jsp.fileMenu.createClaim' />
					</td>
				</tr>
			</authz:ifUserInRole>
            <s:if test="dealerEligibleToPerformRMT">
                <tr>
                    <td nowrap="nowrap" colspan="2">
                        <input type="radio" name="actionType" id="retailMachineTransfer"
                               value="retailmachineTransfer"> <s:text
                            name='summaryTable.inboxButton.retail_machine_transfer'/>
                    </td>
                </tr>
            </s:if>
            </authz:ifNotPermitted>

        </table>
        <div align="center" class="spacingAtTop" style="margin-bottom:10px;">
					<s:submit type="button"  value="%{getText('button.common.continue')}" cssClass="button" />
				</div>
	</div>
	
	 </s:form>
   </u:body>
</html>