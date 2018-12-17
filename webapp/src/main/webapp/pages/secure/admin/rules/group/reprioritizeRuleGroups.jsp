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
    <u:stylePicker fileName="ruleGroup.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript" src="scripts/ruleGroups.js"></script>

    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.DNDSource");

        // For use by successFullRefresh.jsp
        window.frameElement["TST_ID"] = "ruleGroupTable";
        
        adjustPriorityAfterDragDrop("ruleGroupsPriorityUpdateForm", "ruleGroups");
    </script>
</head>
<u:body>
    <u:actionResults/>
    <s:form theme="twms" validate="true" id="ruleGroupsPriorityUpdateForm">

        <s:hidden name="ruleGroup" />

        <div dojoType="dijit.layout.LayoutContainer" class="maxSize">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="client"
                 title="<s:text name="label.manageBusinessRuleGroup.ruleGroups"/>">
                	<div class="policy_section_div">
                    <div class="admin_section_heading">
                        <s:text name="label.manageBusinessRuleGroup.ruleGroups"/>
                        <span class="priorityChangeHint">
                            (<s:text name="label.manageBusinessRuleGroup.changePriorityByDragDrop"/>)
                        </span>
                    </div>
                    <div id="ruleGroups" style="margin:5px;">
                        <div class="mainTitle ruleNameSection" style="padding: 0">
                            <s:text name="columnTitle.manageBusinessRuleGroup.name"/>
                        </div>
                        <div class="borderTable">&nbsp;</div>
                        <div dojoType="twms.widget.DNDSource" class="container">
                        <s:if test="allRuleGroupsForContext.empty">
                            <div style="text-align:center">
                                <s:text name="message.manageBusinessRuleGroup.noRuleGroupsPresent" />
                            </div>
                        </s:if>
                        <s:else>
                            <s:iterator value="allRuleGroupsForContext" status="ruleGroupsIter">
                                <div class="dojoDndItem">
                                    <div class="ruleNameSection">
                                        <s:hidden name="allRuleGroupsForContext[%{#ruleGroupsIter.index}]"/>
                                        <s:hidden id="ruleGroupPriority_%{#ruleGroupsIter.index}"
                                                  name="allRuleGroupsForContext[%{#ruleGroupsIter.index}].priority" />
                                        <s:property value="name" />
                                    </div>
                                </div>
                            </s:iterator>
                        </s:else>
                        </div>
                    </div>
                </div>

                <div align="center">
                    <s:submit value="%{getText('button.common.update')}" cssClass="buttonGeneric"
                              action="reprioritize_rule_groups"/>
                    <button id="cancelUpdate" class="buttonGeneric">
                        <s:text name="button.common.cancel"/>
                    </button>

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
</u:body>
</html>