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
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms" %>
<%@taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="ruleGroup.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript" src="scripts/ruleGroups.js"></script>
    
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.DNDSource");

        adjustPriorityAfterDragDrop("ruleGroupUpdateForm", "rulesInGroup")
    </script>
</head>
<u:body>
    <u:actionResults/>
    <s:form theme="twms" validate="true" id="ruleGroupUpdateForm">

        <s:hidden name="ruleGroup" />

        <div dojoType="dijit.layout.LayoutContainer" class="maxSize" style="overflow-y:auto;overflow-x:hidden">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top"
                 title="<s:text name="label.manageBusinessRuleGroup.ruleGroupSummary"/>">
                <div class="admin_section_div" style="margin:5px;">
                <div class="admin_section_heading">
                    <s:text name="label.manageBusinessRuleGroup.ruleGroupDetails"/>
                </div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0"
                       class="bgColor" style="margin-top:5px;">
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.ruleGroupName"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:textfield name="ruleGroup.name" size="50" />
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr>
                        <td height="3" colspan="3"/>
                    </tr>
                    
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.priority"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:property value="ruleGroup.priority" />
                        </td>
                        <td width="30%"/>
                    </tr>
                     <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="columnTitle.manageBusinessRule.history.status"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:property value="ruleGroup.status" />
                        </td>
                        <td width="30%"/>
                    </tr>
                    <s:if test = "context == 'ClaimRules'">
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal" align="center">
					<!-- Work around for the Checkbox problem, the hidden parameter ensures that there is some request parameter submitted,
					 which will always set the corresponding ActionForm property.If the checkbox is checked,
					 the 2 parameters will be passed: "ruleGroup.stopRuleProcessingOnSuccess=true&ruleGroup.stopRuleProcessingOnSuccess=false".
					 But the beanutils code only uses the first parameter to set the value. If the checkbox is left unchecked,
					 then there will be only 1 parameter "ruleGroup.stopRuleProcessingOnSuccess=false" which will ensure the property is set. -->
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnSuccess" />
                            <s:hidden name="ruleGroup.stopRuleProcessingOnSuccess" value="false"/>
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnFirstSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal" align="center">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnFirstSuccess" />
                            <s:hidden name="ruleGroup.stopRuleProcessingOnFirstSuccess" value="false"/>
                        </td>
                        <td width="30%"/>
                    </tr>
                    </s:if>
                    <s:else>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="columnTitle.manageBusinessRuleGroup.stopOnNoResult"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnNoResult" />
                            <s:hidden name="ruleGroup.stopRuleProcessingOnNoResult" value="false"/>
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="columnTitle.manageBusinessRuleGroup.stopOnMultipleResult"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnMultipleResult" />
                            <s:hidden name="ruleGroup.stopRuleProcessingOnMultipleResult" value="false"/>
                        </td>
                        <td width="30%"/>
                    </tr>
                    </s:else>
                   
                </table>
          
            </div>
            <div dojoType="dijit.layout.ContentPane" layoutAlign="client"
                 title="<s:text name="label.manageBusinessRuleGroup.rulesList"/>" >
                <div class="admin_section_div" style="margin:5px;">
                    <div class="admin_section_heading">
                        <s:text name="label.manageBusinessRuleGroup.rulesList"/>
                        <span class="priorityChangeHint">
                            (<s:text name="label.manageBusinessRuleGroup.changePriorityByDragDrop"/>)
                        </span>
                    </div>
                    <div id="rulesInGroup">
                    
                        <div class="admin_section_subheading ruleNameSection" style="padding:8px 0 0 8px;">
                            <s:text name="columnTitle.manageBusinessRuleGroup.ruleName"/>
                        </div>
                        <div dojoType="twms.widget.DNDSource" class="container" >
                        <s:if test="ruleGroup.rules.empty">
                            <div style="text-align:center">
                                <s:text name="message.manageBusinessRuleGroup.noRulesPresent" />
                            </div>
                        </s:if>
                        <s:else>
                            <s:iterator value="ruleGroup.rules" status="rulesIter">
                               <s:if test='"ACTIVE".equals(status)'>  
                                <div class="dojoDndItem">
                                    <div class="ruleNameSection">
                                        <s:hidden id="ruleGroupPriority_%{#rulesIter.index}" 
                                        name="ruleGroup.rules[%{#rulesIter.index}].priority" />
                                        <s:property value="name" />
                                    </div>
                                </div>
                                </s:if>
                            </s:iterator>
                        </s:else>
                        </div>
                    </div>
                </div>

                <div align="center">
                    <s:submit value="%{getText('button.common.update')}" cssClass="buttonGeneric"
                              action="update_rule_group"/>
                   <s:if test='"INACTIVE".equals(ruleGroup.status)'>     
                    	<s:submit value="%{getText('button.managePolicy.activate')}" cssClass="buttonGeneric"
                              action="activate_rule_group"/>
                    </s:if>
                    <s:else>
                    	<s:submit value="%{getText('button.managePolicy.deActivate')}" cssClass="buttonGeneric"
                              action="deactivate_rule_group"/>
                    </s:else>          
                    
                    <%-- Fix for NMHGSLMS-1171 
                    <button id="cancelUpdate" class="buttonGeneric">
                        <s:text name="button.common.cancel"/>
                    </button> --%>
					<input type="button"class="buttonGeneric" id="cancelUpdate" value="<s:text name="button.common.cancel"/>"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                           dojo.connect(dojo.byId("cancelUpdate"), "onclick", function() {
                               closeMyTab();
                           });
                        });
                    </script>
                </div>
            </div>
        </div>
    </s:form>
<authz:ifPermitted resource="warrantyAdminClaimProcessorRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('ruleGroupUpdateForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('ruleGroupUpdateForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>