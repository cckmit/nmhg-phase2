<%@ include file="../expressionEditorHeader.jsp"%>
<%@ taglib prefix="authz" uri="authz" %>
<u:body smudgeAlert="false">
	<%-- START ReadOnly-vs-Normal Rendering... --%>
	<s:if test="!readOnly">
		<u:actionResults />

		<form id="editorForm" method="POST" action="save_search_expression.action">
		<s:if test="!actionMessages.empty">
			<script type="text/javascript">
                dojo.addOnLoad(function () {
                    parent.publishEvent("/accordion/refreshsearchfolders");
                });
        </script>
        </s:if>
	</s:if>
	<%-- END ReadOnly-vs-Normal Rendering... --%>

	<s:hidden name="id" value="%{domainPredicate.id}" />
	<s:hidden name="context" />
	<s:hidden name="folderName" value="Search"/>
	<s:hidden name="dynamicSearch" />
	<s:hidden name="savedQueryId" />
	<s:hidden id="ruleTreeJSON" name="ruleTreeJSON" />
	<s:hidden id="referredPredicatesField" name="referringPredicates" />
	<s:hidden id="isMultiClaimMaintenance" name="isMultiClaimMaintenance"/>
	<s:hidden id="isTransferOrReProcess" name="isTransferOrReProcess"/>
	<s:hidden id ="isMultiRecClaimMaintainace" name="isMultiRecClaimMaintainace"></s:hidden>
    <s:hidden id="isCreateLabelForInventory" name="isCreateLabelForInventory"/>
    <s:hidden id="selectedBusinessUnit" name="selectedBusinessUnit"/>
    <p />
	<div style="background:#F3FBFE;border:1px solid #EFEBF7;margin:5px;width:99%">
	<%@ include file="../expressionEditorTable.jsp"%>
	
	<%-- START ReadOnly-vs-Normal Rendering... --%>
    <s:if test="!readOnly">
	
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="bgColor" style="border:none">
			<tr>
				<td width="70%" height="19" class="labelNormal">
				<div title="<s:text name="label.manageBusinessCondition.addCannedRuleRow"/>">
				<span id="searchPrebuiltExpressions" style="display:none"> <s:text
					name="error.manageBusinessCondition.chooseACondition" /> </span></div>
				</td>
			</tr>
		</table>
      
        <div class="buttonWrapperPrimary">
		<table>
		<tr>
            <td class="label" valign="bottom" >
                <s:if test="isMultiClaimMaintenance!='true' && isMultiRecClaimMaintainace!='true' 
                	&& isTransferOrReProcess!='true' && isCreateLabelForInventory!='true'">
                    <s:checkbox cssClass="buttonGeneric" name="notATemporaryQuery" value="notATemporaryQuery"
                                disabled='%{(id!=null && notATemporaryQuery) ? true:false}'>
                        <s:text name="button.common.save"/></s:checkbox>
                </s:if>
            </td>
         
		<td>
		<s:if test="id!=null">
			<s:submit cssClass="buttonGeneric" id="saveButton"
				value="%{getText('button.common.execute')}" />
			<s:if test="notATemporaryQuery">
				<s:submit cssClass="buttonGeneric"
					value="%{getText('button.common.delete')}"
					action="delete_search_expression">
				</s:submit>
				
				<s:submit cssClass="buttonGeneric"
					value="%{getText('button.common.copy')}"
					action="copy_search_expression">
				</s:submit>

			</s:if>
		</s:if> 
		<s:else>
			<s:submit cssClass="buttonGeneric" id="saveButton"
				value="%{getText('button.common.execute')}" />
		</s:else>
		</td>
		
		<td>
		<button type="button" class="buttonGeneric" id="resetButton">
			<s:text name="button.common.reset" /></button>
			</td>
		</tr>
		</table>
		
		</div>
		</form>

		<%@ include file="../../policy/ruleSearchWizard.jsp"%>
</div>
    <div id="errorBoxContainer" style="display:none">
        <div dojoType="twms.widget.Dialog" id="errorDialog" bgColor="#FFF" bgOpacity="0.5"
             toggle="fade" toggleDuration="250" closeNode="closeErrorDialog"
             title="<s:text name="error.manageBusinessCondition.treeError"/>">
            <div dojoType="dijit.layout.LayoutContainer"
                 style="background: #F3FBFE border: 1px solid #EFEBF7">
                <div dojoType="dijit.layout.ContentPane" id="errorDialogContent" layoutAlign="client"></div>
                <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" id="errorDialogCloseNode">
                    <center>
                        <button id="closeErrorDialog" class="buttonGeneric">
                            <s:text name="button.common.close"/>
                        </button>
                    </center>
                </div>
            </div>
        </div>
    </div>
	</s:if> <%-- END ReadOnly-vs-Normal Rendering... --%>
</u:body>
<authz:ifPermitted resource="warrantyAdminCreateLabelsforinventoryReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	        dojo.html.hide(dojo.byId("addButton"));
	        dojo.html.show(dojo.byId("addButtonReadOnly"));
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsMultiClaimMaintenanceReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	        dojo.html.hide(dojo.byId("addButton"));
	        dojo.html.show(dojo.byId("addButtonReadOnly"));
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsClaimAttributesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	        dojo.html.hide(dojo.byId("addButton"));
	        dojo.html.show(dojo.byId("addButtonReadOnly"));
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsDefineSearchQueryReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	        dojo.html.hide(dojo.byId("addButton"));
	        dojo.html.show(dojo.byId("addButtonReadOnly"));
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="itemsDefinedSearchQueryReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	        dojo.html.hide(dojo.byId("addButton"));
	        dojo.html.show(dojo.byId("addButtonReadOnly"));
	    });
	</script>
</authz:ifPermitted>
</html>
