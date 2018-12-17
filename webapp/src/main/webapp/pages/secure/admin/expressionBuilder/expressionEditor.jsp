<%@ include file="expressionEditorHeader.jsp" %>
<%@ taglib prefix="authz" uri="authz" %>
<u:body>
<%-- START ReadOnly-vs-Normal Rendering... --%>
<s:if test="!readOnly">
    <u:actionResults wipeMessages="true" />

    <form id="editorForm" method="POST" action="save_expression.action">
    <s:if test="!actionMessages.empty">
    <script type="text/javascript">
        dojo.addOnLoad(function () {
            manageTableRefresh("expressionTable", true);
        });        
        
        
    </script>
    </s:if>
</s:if>
<%-- END ReadOnly-vs-Normal Rendering... --%>

<s:hidden name="id" value="%{domainPredicate.id}"/>
<s:hidden name="context"/>
<s:hidden id="ruleTreeJSON" name="ruleTreeJSON"/>
<s:hidden id="referredPredicatesField" name="referringPredicates"/>

	<!--  Claim Duplicacy related UI. This is put here separately, since some
	of the rule builder screens (such as saved search) don't need it.-->
	<div style="background:#F3FBFE;border:1px solid #EFEBF7" style="width:99%;margin:5px; ">
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
       class="bgColor">
	    <tr>
	        <td class="labelStyle" style="width:20%; color: black; 
	        	padding-top: 5px">
	            <s:text name="label.manageBusinessCondition.conditionType" />:
	        </td>
	        <td class="labelStyle" style="padding-top: 3px">
	            <%-- START ReadOnly-vs-Normal Rendering... --%>
	             <s:if test="readOnly">
					<span style="color:black">
					<s:if test="duplicateCheck">
						<s:text name="label.manageBusinessCondition.duplicateCheck" />
					</s:if>										
					<s:else>
						<if test="systemCondition">
							<s:text name="label.manageBusinessCondition.systemDefinedCondition" />
						</if>
						<s:else>						
							<s:text name="label.manageBusinessCondition.normal" />
						</s:else>	
					</s:else>
					</span>
		     </s:if>
		     <s:else>
				<s:radio id="duplicateCheck" name="duplicateCheck"
							  list="conditionTypes" cssStyle="border: 0;"
							  onclick="conditionTypeChanged(this.value)" />		
	            </s:else>
				<%-- END ReadOnly-vs-Normal Rendering... --%>
	        </td>
	    </tr>
    </table>
<%@ include file="expressionEditorTable.jsp" %>

<%-- START ReadOnly-vs-Normal Rendering... --%>
<s:if test="!readOnly">
<div id="separator"></div>

<table width="100%" border="0" cellspacing="0" cellpadding="0"
       class="bgColor">
    <tr>
        <td width="70%" height="19" class="labelNormal">
            <div title="<s:text name="label.manageBusinessCondition.addCannedRuleRow"/>">
                <span id="searchPrebuiltExpressions" style="cursor:pointer;text-decoration:underline;color:#0000FF">
                    <s:text name="error.manageBusinessCondition.chooseACondition"/>
                </span>
            </div>
        </td>
    </tr>
</table>
</div>
<div class="buttonWrapperPrimary">
   <%--  Fix for NMHGSLMS-1171   
   <button type="button" class="buttonGeneric" id="resetButton">
        <s:text name="button.common.reset"/>
    </button> --%>
	<input type="button"class="buttonGeneric" id="resetButton" value="<s:text name="button.common.reset"/>"/>
    <s:if test="id!=null">
        <s:submit cssClass="buttonGeneric" id="saveButton"
                  value="%{getText('button.common.update')}">
        </s:submit>

    	<s:submit cssClass="buttonGeneric"
        	      value="%{getText('button.common.delete')}"
            	  action="delete_expression">
        </s:submit>
    </s:if>
    <s:else>

        <s:submit cssClass="buttonGeneric" id="saveButton"
                  value="%{getText('button.common.save')}">
        </s:submit>
    </s:else>
</div>
</form>

<%@ include file="../policy/ruleSearchWizard.jsp" %>

<div id="errorBoxContainer" style="display:none">
    <div dojoType="twms.widget.Dialog" id="errorDialog" bgColor="#FFF" bgOpacity="0.5"
         toggle="fade" toggleDuration="250" closeNode="closeErrorDialog"
         title="<s:text name="error.manageBusinessCondition.treeError"/>">
<!--        <div dojoType="dijit.layout.LayoutContainer"
             style="background: #F3FBFE; border: 1px solid #EFEBF7">-->
            <div dojoType="dijit.layout.ContentPane" id="errorDialogContent" layoutAlign="client"></div>
            <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" id="errorDialogCloseNode">
                <center>
                    <button id="closeErrorDialog" class="buttonGeneric">
                        <s:text name="button.common.close"/>
                    </button>
                </center>
            </div>
        </div>
<!--    </div>-->
</div>
</s:if>
<%-- END ReadOnly-vs-Normal Rendering... --%>
</u:body>
<authz:ifPermitted resource="warrantyAdminWarrantyBusinessConditionsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="contractAdminManageBusinessConditionsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('editorForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('editorForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
