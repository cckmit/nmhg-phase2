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
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="authz" uri="authz"%>
<%@include file="/i18N_javascript_vars.jsp"%>
<script type="text/javascript" xml:space="preserve">
var dialog;
dojo.addOnLoad(function() {   
			dialog =  dijit.byId("DeleteDialog");  
});	     
         dojo.require("twms.widget.TitlePane");
         dojo.require("dojox.layout.ContentPane");
</script>
<style>
.borderForTable tr td{
border:none !important;
}
</style>
<div style="overflow:auto;width:100%;">
<table class="grid borderForTable" cellpadding="0" cellspacing="0" style="width:99%" >
    <thead>
        <tr class="row_head">
            <th><s:text name="Date"/></th>
            <th><s:text name="label.warrantyAdmin.fromCompany"/></th>
            <th><s:text name="label.warrantyAdmin.moveToCustomer"/></th>
            <th><s:text name="label.warrantyAdmin.customerType"/></th>
            <th><s:text name="label.equipmentInfo.transactionType"/></th> 
            <s:if test="!loggedInUserADealer">           
            <th><s:text name="label.newClaim.invoice"/></th>
            </s:if>
            <th><s:text name="label.common.details"/></th>            
        </tr>
    </thead>
    <tbody>
        <s:set name="isInStock" value="inventoryItem.isInStock" />
        <s:iterator value="transactionsToBeDisplayed" status="itemStatus">
        <tr>                         	 
             <s:if test="invTransactionType.trnxTypeValue=='DR'">
             	<td><s:property value="inventoryItem.deliveryDate" /></td>
             </s:if>
             <s:else>
             	<td><s:property value="transactionDate" /></td>
             </s:else>            	            	                       
           	<s:if test="seller.displayName == null">
                   <td><s:property value="seller.companyName" /></td>
            </s:if>
            <s:else>
                   <td><s:property value="seller.displayName" /></td>
            </s:else>
            <s:if test="transactionDate != null">
                 <s:if test="buyer.displayName == null">
                    <td><s:property value="buyer.companyName" /></td>
                </s:if>
                <s:else>
                    <td><s:property value="buyer.displayName" /></td>
                </s:else>
            </s:if>
            <s:elseif test="buyer.isCustomer() && transactionDate != null">    
           		<s:if test="buyer.displayName == null">
                    <td><s:property value="buyer.companyName" /></td>
                </s:if>
                <s:else>
                    <td><s:property value="buyer.displayName" /></td>
                </s:else> 
           </s:elseif>
           <s:else>
           	<td></td>
           </s:else>
           <s:if test="transactionDate != null">
                <td><s:property value="buyer.getType()" /></td> 
           </s:if>
           <s:else>   
           		<td></td>
           </s:else>           
            <td>
            	<s:property value="invTransactionType.trnxTypeValue" />
            </td>            
            <td>
            <s:if test="transactionDate != null && !loggedInUserADealer">	
            	&nbsp;<s:property value="invoiceNumber"/>
            </s:if>            
            </td>
           <td>
           	<span>
	            <s:if test="(isWarrantyPresent() && transactionDate != null && !(transactedItem.serializedPart)) || invTransactionType.trnxTypeValue.equals('DEALER RENTAL') || invTransactionType.trnxTypeValue.equals('DEMO') ">
	             
					          <s:a id="showTransfer_%{#itemStatus.index}" href="#"><s:text name="label.invTransaction.htmlView"/> </s:a>
				        <script type="text/javascript">
							dojo.addOnLoad(function() {
								dojo.connect(dojo.byId("showTransfer_"+<s:property value="%{#itemStatus.index}"/>), "onclick", function(event){
									var url;
									<s:if test="invTransactionType.trnxTypeValue.equals('TTR') || invTransactionType.trnxTypeValue.equals('TTR_MODIFY')">
									    url = "display_DR_ETR_details.action?warrantyTransactionId="+<s:property value="id"/>+"&forETR="+true;
									    var thisTabLabel = getMyTabLabel();
					                    parent.publishEvent("/tab/open", {
										                    label: "Transfer Report",
										                    url: url, 
										                    decendentOf: thisTabLabel,
										                    forceNewTab: true
					                                       });
									</s:if>
									<s:else>
									    url = "display_DR_ETR_details.action?warrantyTransactionId="+<s:property value="id"/>;
									    var thisTabLabel = getMyTabLabel();
					                    parent.publishEvent("/tab/open", {
										                    label: "Delivery Report",
										                    url: url, 
										                    decendentOf: thisTabLabel,
										                    forceNewTab: true
					                                       });
									</s:else>
								  
				                });
							});	
                        </script>
	             	</s:if>
	           </span>
            
            <authz:ifUserInRole roles="admin,inventoryAdmin">
				   <s:if test="(invTransactionType.trnxTypeValue =='TTR' || invTransactionType.trnxTypeValue == 'TTR_MODIFY')
          				  && transactionDate != null && isLatestTransactionForAType() && !(transactedItem.serializedPart) && isWarrantyPresent()">
						<br><span>
						   <a href="<s:url action="getTransferReportDetails.action?modifyAllowed=%{modifyAllowed}&transactionId=%{id}"/>"><s:text name="label.invTransaction.actionModify" /></a>
						</span>
	            	</s:if>
	            	<s:elseif test="invTransactionType.trnxTypeValue =='DR' || invTransactionType.trnxTypeValue == 'DR_MODIFY' || invTransactionType.trnxTypeValue =='DEALER RENTAL' || invTransactionType.trnxTypeValue =='DEMO'">
	            	     <s:if test=" !(transactedItem.serializedPart) && isWarrantyPresent() && isLatestTransactionForAType() && modifyWRAllowed() && canMondifyBasedOnWindowPeriod()">
		            		<br><span>
		            		<a href="<s:url action="getTransferReportDetails.action?modifyAllowed=%{modifyAllowed}&transactionId=%{id}"/>"><s:text name="label.invTransaction.actionModify" /></a>
		            		</span>
	            		</s:if> 
						<s:if test="transactedItem.serializedPart && canDeleteWarrantyForMajorComponent()">					
							<authz:ifUserInRole roles="inventoryAdmin">
								<a onClick='showDialog(<s:property value="id" />)' style="vertical-align: top;padding-bottom: 5px;" title="<s:text name="label.deleteWarranty.deliveryReport"/>"><s:text
									name="Delete" /></a>
							</authz:ifUserInRole>
						</s:if>
				</s:elseif>
				</authz:ifUserInRole>
	          </td> 
	    </tr>
        </s:iterator>
    </tbody>
</table>
</div>
<div id="inventoryDialogBoxContainer" style="display: none; overflow: auto">
	<div dojoType="twms.widget.Dialog" id="add_or_remove_inventory_attachment" bgColor="white"
		    bgOpacity="0.5" toggle="fade" toggleDuration="250" title="Supporting Documents" >
		<div class="dialogContent" dojoType="dijit.layout.LayoutContainer" 
			    style="background: #F3FBFE; width: 100%; height: 400px;">
			<div dojoType="dojox.layout.ContentPane" id="add_or_remove_inventory_attachment_div" 
			        executeScripts="true"></div>
		</div>
	</div>
</div>


	
		<div dojoType="twms.widget.Dialog" id="DeleteDialog" title="<s:text name="label.deleteWarranty.deliveryReport"/>:" 
		 style="width:60%;height:40%;">
		 <s:form action="deleteWarrantyForMC" theme="twms" id="delete_major_com" validate="true" method="POST">
		 <div style="visibility: hidden;" id="errorMessage"><p style="color: #FF0000;" id="errorText"><s:text name="error.warrantyCoverage.commentsMandatory"/></p></div>
			<div class="section_div" style="margin-left:5px;width:99.2%;">
                   <div class="labelStyle"><s:text name="label.deleteDR.reason"/></div>
                    <div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">                        
                        <t:textarea id="deleteComments" name="warranty.modifyDeleteComments" rows="3" cssStyle="width:90%;"></t:textarea>       
                        <s:hidden name="transactionId" id="transactionId"/>                 
                    </div>
     		</div>
		<div class="buttonWrapperPrimary">
	 		<input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onClick="javascript:dialog.hide()"/>
	    	<input type="button" name="Submit3" value="<s:text name='button.common.delete' />" class="buttonGeneric" onClick="submitForm(this,'true')" />
				 <script>
            function submitForm(btn, val) {  
            	var form = dojo.byId("delete_major_com");
            	var deleteComments = dojo.byId("deleteComments").value;
            	deleteComments = deleteComments.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
            	if(deleteComments.length>0)
            	{
  		        form.action = "deleteWarrantyForMC.action";  	          
  	            form.submit();               
            	}
            	else
            	{
                document.getElementById("errorMessage").style.visibility="visible";	
            	}
            }

            function showDialog(id)
            {
                dialog.show();
                document.getElementById('deleteComments').value='';
                document.getElementById('errorMessage').style.visibility='hidden';
                document.getElementById('transactionId').value=id;
                
            }
        </script>

		  	  </div> </s:form>	  
	  </div>
	 
  
<%--  TODO: Before uncommenting the following piece, update the i18N keys in it, according to the new 
		convention. See updated messages_en.properties. --%>
<%--
    <table border="0" cellpadding="0" cellspacing="1" class="form1">
        <thead><tr>
            <th class="invColHeader"> <s:text name="inventory.transaction.transactiondate"/></th>
            <th class="invColHeader"> <s:text name="inventory.transaction.fromCompany"/></th>
            <th class="invColHeader"><s:text name="inventory.transaction.moveToCustomer"/></th>
            <th class="invColHeader"><s:text name="inventory.transaction.customerType"/></th>
        </tr></thead>
        <tbody>
            <s:set name="isInStock" value="inventoryItem.isInStock" />
            <s:iterator value="inventoryItem.transactionHistory">
              <tr>
                <td class="invTableDataWhiteBg"> <s:property value="transactionDate" /> </TD>
                <s:if test="seller.name == null">
                    <td class="invTableDataWhiteBg"> <s:property value="seller.companyName" /> </TD>
                </s:if>
                <s:else>
                    <td class="invTableDataWhiteBg"> <s:property value="seller.name" /> </TD>
                </s:else>
                <s:if test="buyer.name == null">
                    <td class="invTableDataWhiteBg"> <s:property value="buyer.companyName" /> </TD>
                </s:if>
                <s:else>
                    <td class="invTableDataWhiteBg"> <s:property value="buyer.name" /> </TD>
                </s:else>
                <s:if test="transactedItem.dealer == buyer">
                    <td class="invTableDataWhiteBg"> <s:text name="inventory.transaction.dealer"/> </TD>
                </s:if>
                <s:else>
                    <td class="invTableDataWhiteBg"> <s:text name="inventory.transaction.endCustomer"/> </TD>
                </s:else>
              </tr>
            </s:iterator>
        </tbody>
    </table>
--%>
