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
<%@ taglib prefix="authz" uri="authz" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<head>
<html xmlns="http://www.w3.org/1999/xhtml" >

<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="base.css"/>
<s:head theme="twms"/>
    <script type="text/javascript">    
        function deActivateRule() {
          var formObj = dojo.byId("baseForm");
          dojo.byId('activeFlagInd').value="INACTIVE";
          formObj.action = "update_rec_processor_routing_rule.action";
          formObj.submit(); 
		}          
    </script>


</head>
<script type="text/javascript"
        src="scripts/vendor/dojo-widget/dojo.js"></script>
<s:head theme="twms"/>

<form name="baseForm" id="baseForm">
<div style="width:100%;height:100%;overflow-y:auto;overflow-x:hidden">

<s:if test="!actionMessages.empty">
    <script type="text/javascript">

        dojo.addOnLoad(function () {
            manageTableRefresh("domainRuleTable", true);

            var messageSection = dojo.byId("messageSection");
            setTimeout(function() {
                dojo.lfx.html.wipeOut(messageSection, 400, null, function() {
                    dojo.dom.destroyNode(messageSection);
                }).play();
            }, 2000);
        });

    </script>
    <div id="messageSection">
        <table width="100%" border="0" cellspacing="0" cellpadding="0"
               class="grid" align="center">
            <tr>
                <td valign="top">
                    <table width="100%" border="0" cellspacing="3"
                           cellpadding="3"
                           class="successbg">
                        <tr>
                            <td width="4%" class="successTextbold">
                                <div align="center"><img
                                        src="image/applicationSuccessIcon.gif"
                                        width="16" height="16"/></div>
                            </td>
                            <td width="12%" class="successTextbold"><s:text name="message.common.success"/></td>
                            <td class="successTextnormal"><s:actionmessage/></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</s:if>
<s:actionerror/>

<div class="policy_section_div" >
<div class="admin_section_heading">
    <s:text name="label.manageBusinessRule.ruleDetails"/>
</div>
<table width="100%" border="0" cellspacing="0" cellpadding="0"
       class="grid">
    <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td width="70%" height="19" class="labelNormal">
                <s:textfield name="rule.ruleNumber" cssClass="txtField" readonly = "true" id="ruleNumber"/>
            </td>
            <td width="70%"></td>
    </tr>   
    <tr>
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
            <s:text name="label.manageBusinessRule.ruleName"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="ruleAuditName" value="%{rule.getName()}" cssClass="txtField" id="ruleName"/>           
        </td>
        <td width="30%"></td>
    </tr>
    <tr>
        <td height="3" colspan="3"></td>
    </tr>
    <tr>
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
            <s:text name="label.manageBusinessRule.failureMessage"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="ruleAuditFailureMessage" cssClass="txtField"
                         id="failureMessage" value="%{ruleAudit.failureMessage}"/>
        </td>
        <td width="30%"></td>
    </tr>
    <tr>
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
            <s:text name="label.manageBusinessRule.comment"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="rule.d.internalComments" cssClass="txtField"
                         id="comments"  value="%{rule.d.internalComments}"/>
        </td>        
    </tr>    
    <tr>
        <td height="3" colspan="3"></td>
    </tr>
</table>
</div>
<div class="policy_section_div">

    <div class="admin_section_heading">
        <s:text name="label.manageBusinessRule.businessCondition"/>
    </div>
    <table width="100%" class="grid borderForTable">
        <tr class="colHeader">
            <td width="15%"></td>
            <td><s:text name="columnTitle.manageBusinessRule.expression"/></td>
            <td width="15%"></td>
            <td align="center" colspan="2"><s:text name="columnTitle.manageBusinessRule.businessAction"/></td>
        </tr>
        <tr>
            <td class="admin_selections" align="center"><s:text name="label.manageBusinessRule.if"/></td>
            <td class="admin_selections"><s:property value="rule.predicate.name"/></td>
            <td class="admin_selections" align="center"><s:text name="label.manageBusinessRule.then"/></td>

            <td class="admin_selections">
                <select name="result">
                    <s:if test='rule.action.state.equals("assign")'>
                        <option value="assign" selected="selected"><s:text
                                name="dropdown.manageBusinessRule.assignTo"/></option>
                        <option value="not Assign"><s:text name="dropdown.manageBusinessRule.notAssignTo"/></option>
                    </s:if>
                    <s:else>
                        <option value="assign"><s:text name="dropdown.manageBusinessRule.assignTo"/></option>
                        <option value="not Assign" selected="selected"><s:text
                                name="dropdown.manageBusinessRule.notAssignTo"/></option>
                    </s:else>
                </select>
            </td>
            <td class="admin_selections">
                <select name="actionId">
                    <s:iterator value="userClusters">
                        <s:if test="top == userCluster">
                            <option value="<s:property value="name"/>" selected="selected"><s:property
                                    value="name"/></option>
                        </s:if>
                        <s:else>
                            <option value="<s:property value="name"/>"><s:property value="name"/></option>
                        </s:else>
                    </s:iterator>
                </select>
            </td>
        </tr>
    </table>
    <s:hidden name="id" value="%{rule.id}"/>
    <s:hidden name="ruleId" value="%{ruleId}"/>
    <s:hidden name="context" />
    <s:hidden name="activeFlag" value="ACTIVE" id="activeFlagInd"/>    

</div>
<%@ include file="include_rule_audit_history.jsp" %>
<div align="center" class="spacingAtTop">
			<s:if test='"INACTIVE".equals(rule.status)'>           
            <s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.activate')}"
                      action="update_rec_processor_routing_rule" />
            </s:if>                      
            <s:else>
            <script type="text/javascript">
				dojo.addOnLoad(function () {
					dojo.connect(dojo.byId('deactivate_rule'),'onclick',deActivateRule);
				});
			</script>
            <s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.updateRule')}"
                      action="update_rec_processor_routing_rule"/>          
            <%-- Fix for NMHGSLMS-1171 
            <t:button cssClass="buttonGeneric" label="%{getText('button.manageBusinessRule.deActivate')}"
                      id="deactivate_rule" value="%{getText('button.manageBusinessRule.deActivate')}"/> --%>
            <input type="button"class="buttonGeneric" id="deactivate_rule" value="<s:text name="button.manageBusinessRule.deActivate"/>"/> 
            </s:else>
            <s:submit id="i18nRuleDescription" cssClass="buttonGeneric" action="save_i18n_Rule_Description"
                      value="%{getText('button.manageBusinessRule.addI18nRuleDescription')}"/>
       </div>
</div>
</form>
<authz:ifPermitted resource="warrantyAdminRecoveryClaimProcessorRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="contractAdminRecoveryClaimProcessorRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>