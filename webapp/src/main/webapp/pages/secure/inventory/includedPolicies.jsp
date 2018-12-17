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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<script type="text/javascript">	
	var terminationCaption = "<s:text name="label.warrantyCoverage.completeTermination"/>";
	var activationCaption = "<s:text name="label.warrantyCoverage.activate"/>";
</script>

<script type="text/javascript" src="scripts/adminWarrantyReg.js"></script>
<form id="policyForm" name="policyForm" method="POST" action="update_warranty.action">
	 <s:hidden name="inventoryItemId" value = "%{inventoryItem.id}"/>
<div class="mainTitle" style="padding-left:2px;padding-bottom:5px;"><s:text name="label.common.warrantyCoverage"/></div>
	<table class="grid borderForTable"  cellpadding="0" cellspacing="0">
   	<thead>
	   	
	   			<authz:ifUserInRole roles="inventoryAdmin">
	   			<s:if test="!inventoryItem.serializedPart || (inventoryItem.serializedPart && @tavant.twms.domain.inventory.InventoryItemSource@MAJORCOMPREGISTRATION.toString().equals(inventoryItem.source.toString()))">
				<tr class="title">
	   		
					 <td align="right" colspan="13">
						<img id="edit_button" class="clickable" src="image/tree/editSystem.gif"/>
						<img  title="Cancel" id="save_button" class="clickable" src="image/tree/cancelIcon.gif"/>
						<img id="addPoliciesImage" class="clickable" src="image/addRow.png"/>
					 </td>
	   	        </tr>
	   	        </s:if>
	   			</authz:ifUserInRole>
	   	
	    <tr class="row_head">
			<th width="10%"><s:text name="label.warrantyAdmin.policyCode"/></th>
		    <th width="10%"><s:text name="label.warrantyAdmin.policyName"/></th>
		    <th width="10%"><s:text name="label.warrantyAdmin.type"/></th>
		    <th width="10%"><s:text name="label.common.startDate"/></th>
		    <th width="10%"><s:text name="label.common.endDate"/></th>
		    <th width="10%"><s:text name="label.common.monthsCovered"/></th>
		  	<s:if test="!(inventoryItem.serializedPart)">
		    <th width="10%"><s:text name="label.warrantyAdmin.hoursCovered"/></th>		   
		    </s:if>
		    <th width="10%"><s:text name="columnTitle.common.status"/></th>
            <s:if test="isDRDoneByLoggedInUser() || isUserInventoryFullView()">
                <th width="10%"><s:text name="label.common.comments"/></th>
                <th width="5%"><s:text name="label.policyAudit.history"></s:text></th>
                <th width="5%"><s:text name="P.O.Number"></s:text></th>
                <th width="10%"><s:text name="label.managePolicy.purchaseDate"></s:text></th>
                <th width="10%"><s:text name="label.extendedwarrantyplans.attachments"></s:text></th>
            </s:if>
        </tr>
   </thead>
   <tbody id="policyBody">
   </tbody>
  </table>
	<jsp:include flush="true" page="policyRegTemplates.jsp"></jsp:include>
	
	<script type="text/javascript">		
		dojo.addOnLoad(function(){
			var originalSize = <s:property value="originalPoliciesSize" escape="false"/>;		
			var registeredPoliciesArray = <s:property value="jSONStringForRegisteredPolicies" escape="false"/>;
			var availablePoliciesArray = <s:property value="jSONStringForAvailablePolicies" escape="false"/>;
			var includedPoliciesArray = <s:property value="jSONStringForAddedPolicies" escape="false"/>;
			var availablePolicyManager = new tavant.twms.PolicyRowManager(dojo.byId("policyBody"),
																dojo.byId('addPoliciesBody'), editableTemplate.markup,
																readonlyTemplate.markup, additionalTemplate.markup,
																editableTemplateForGoodwill.markup, readonlyTemplateForAdded.markup,
																//readAttachmentTemplate,
																availablePoliciesArray, includedPoliciesArray, 
																registeredPoliciesArray, editableTemplate.script, 
																originalSize);
																
			var addPol = dojo.byId('addPoliciesImage');
			if(addPol) { // For non admin user this button wont exist and hence all this is not required..
				<s:if test="editable">
					availablePolicyManager.edit(true);
					dojo.html.hide(dojo.byId('edit_button'));
				</s:if>
				<s:else>
					availablePolicyManager.read(true);
					dojo.html.hide(dojo.byId('save_button'));
					dojo.html.hide(dojo.byId('submit_section'));
				</s:else>
				availablePolicyManager.renderAvailableRows();
																 	
				dojo.connect(dojo.byId('edit_button'), "onclick", function() {
					availablePolicyManager.edit(true);
					dojo.html.hide(dojo.byId('edit_button'));
					dojo.html.show(dojo.byId('save_button'));
					dojo.html.show(dojo.byId('submit_section'));
				});
				
				dojo.connect(dojo.byId('save_button'), "onclick", function() {
					availablePolicyManager.read(true);
					dojo.html.hide(dojo.byId('save_button'));
					dojo.html.show(dojo.byId('edit_button'));
					dojo.html.hide(dojo.byId('submit_section'));
				});
				
				dojo.connect(dojo.byId('add_policies_button'), "onclick", function() {
					availablePolicyManager.pushResults();
					dlg.hide();
				});
			} else {
				availablePolicyManager.read(true);
				availablePolicyManager.renderAvailableRows();
			}			
		});
	</script>
	
	<authz:ifUserInRole roles="inventoryAdmin">
	<s:if test="!inventoryItem.serializedPart">
		<div align="center" id="submit_section">
			<s:submit align="middle" cssClass="button" value="%{getText('button.common.register')}" action="update_warranty"></s:submit>
		</div>
	</s:if>
	<s:if test="inventoryItem.serializedPart && @tavant.twms.domain.inventory.InventoryItemSource@MAJORCOMPREGISTRATION.toString().equals(inventoryItem.source.toString())">
		<div align="center" id="submit_section">
			<s:submit align="middle" cssClass="button" value="%{getText('button.common.register')}" action="update_warranty_majorComp"></s:submit>
		</div>
	</s:if>
	</authz:ifUserInRole>
</form>
