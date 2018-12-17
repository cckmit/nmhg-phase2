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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="SummaryTable.css" />
<u:stylePicker fileName="SummaryTableButton.css" />


<%--
  @author fatima.marneni
--%>
<html>
<head>
    <title><s:text name="title.manageBusinessRule.DSMAdvisorRouting"/></title>
    <s:head theme="twms" />

    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.SplitContainer");
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "domainRuleTable"));
        }
        
        function createDomainRule() {
            parent.publishEvent("/tab/open", {
                label: i18N.new_domainrule,
                decendentOf: getMyTabLabel(),
                url: "create_DSM_advisor_routing_rule.action?context="+extraParams.context,
                forceNewTab: true
            });
        }
        
        var extraParams = {
            context : "DSMAdvisorRouting"
        };
        
        function exportToExcel(){
            exportExcel("/DSMAdvisorRoutingRules/populateCriteria","exportRulesToExcel.action");
        }
        
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" layoutChildPriority="top-bottom"
       style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer id="buttonsDiv"
                    cssClass="delete_button" summaryTableId="domainRuleTable"/>
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="domainRuleTable"/>
        <u:summaryTableButton id="createProcessorRoutingRuleButton"
                    label="button.manageBusinessRule.createBusinessRule"
                    onclick="createDomainRule"
                    summaryTableId="domainRuleTable"
                    cssClass="create_domainrule_button"/>
        <u:summaryTableButton id="downloadListing"
                    label="button.common.downloadToExcel" 
                    onclick="exportToExcel"
                    align="right" 
                    cssClass="download_to_excel_button" 
                    summaryTableId="domainRuleTable"/>                
                        
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="domainRuleTable"
                          bodyUrl="get_DSM_advisor_routing_rules_body.action"
                          extraParamsVar="extraParams"
                          folderName="BUSINESS_RULES"
                          previewUrl="manage_DSM_advisor_routing_rule_detail.action"
                          detailUrl="manage_DSM_advisor_routing_rule_detail.action"
                          previewPaneId="preview"
                          populateCriteriaDataOn="/DSMAdvisorRoutingRules/populateCriteria"
                          parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminAdvisorRoutingReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>