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

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="preview.css"/>

    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.TabContainer");
    </script>
</head>
<u:body>
        <div  dojoType="dijit.layout.TabContainer" tabPosition="bottom" style="height: 300px">
            <div dojoType="dijit.layout.ContentPane"
                 title="<s:text name="label.manageBusinessRuleGroup.ruleGroupSummary"/>">
                <div class="admin_section_heading">
                    <s:text name="label.manageBusinessRuleGroup.ruleGroupDetails"/>
                </div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0"
                       class="grid">
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.ruleGroupName"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:property value="ruleGroup.name" />
                        </td>
                        <td width="30%"/>
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
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:property value="ruleGroup.stopRuleProcessingOnSuccess" />
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnFirstSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:property value="ruleGroup.stopRuleProcessingOnFirstSuccess" />
                        </td>
                        <td width="30%"/>
                    </tr>
                     
                </table>
            </div>
            <div dojoType="dijit.layout.ContentPane"
                 title="<s:text name="label.manageBusinessRuleGroup.rulesList"/>">
                <div class="admin_section_div">
                    <div class="admin_section_heading">
                        <s:text name="label.manageBusinessRuleGroup.rulesList"/>
                    </div>
                    <table width="100%" class="grid borderForTable" cellspacing="0" cellpadding="0">
                        <tr class="row_head"> 
                            <th><s:text name="columnTitle.manageBusinessRuleGroup.ruleName"/></th>
                            <th><s:text name="columnTitle.manageBusinessRuleGroup.businessAction"/></th>
                        </tr>
                        <s:if test="ruleGroup.rules.empty">
                            <tr align="center">
                                <td><s:text name="message.manageBusinessRuleGroup.noRulesPresent" /></td>
                            </tr>
                        </s:if>
                        <s:else>
                            <s:iterator value="ruleGroup.rules">
                                <tr>
                                    <td><s:property value="name" /></td>
                                    <td><s:property value="action.name" /></td>
                                </tr>
                            </s:iterator>
                        </s:else>
                    </table>
                </div>
            </div>
        </div>
</u:body>
</html>