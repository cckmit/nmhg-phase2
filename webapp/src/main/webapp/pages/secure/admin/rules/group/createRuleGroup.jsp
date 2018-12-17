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
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>


    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");

        // For use by successRefreshListing.jsp
        window.frameElement["TST_ID"] = "ruleGroupTable";
    </script>
</head>
<u:body>
    <u:actionResults/>
    <s:form theme="twms" validate="true">

        <s:hidden name="context" />

        <div dojoType="dijit.layout.LayoutContainer" class="maxSize">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top">
                <div class="policy_section_div">
                <div class="admin_section_heading">
                    <s:text name="label.manageBusinessRuleGroup.ruleGroupDetails"/>
                </div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0"
                       class="grid" style="margin-top:5px;">
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
                    <s:if test="context == 'ClaimRules'">
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal" align="center">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnSuccess" />
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="label.manageBusinessRuleGroup.stopOnFirstSuccess"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal" align="center">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnFirstSuccess" />
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
                        </td>
                        <td width="30%"/>
                    </tr>
                    <tr style="padding-top:5px;padding-bottom:5px;">
                        <td height="19" colspan="2" nowrap="nowrap" class="label">
                            <s:text name="columnTitle.manageBusinessRuleGroup.stopOnMultipleResult"/> :
                        </td>
                        <td width="70%" height="19" class="labelNormal">
                            <s:checkbox name="ruleGroup.stopRuleProcessingOnMultipleResult" />
                        </td>
                        <td width="30%"/>
                    </tr>
                    </s:else>
                    <tr>
                        <td height="3" colspan="3"/>
                    </tr>
                </table>
</div>
              

                <div align="center" class="spacingAtTop">
                    <s:submit value="%{getText('button.common.create')}" cssClass="buttonGeneric"
                              action="create_rule_group"/>
                    <button id="cancelCreation" class="buttonGeneric">
                        <s:text name="button.common.cancel"/>
                    </button>

                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                           dojo.connect(dojo.byId("cancelCreation"), "onclick", function() {
                               closeMyTab();
                           });
                        });
                    </script>
                </div>
            </div>
        </div>
    </s:form>
</u:body>
</html>