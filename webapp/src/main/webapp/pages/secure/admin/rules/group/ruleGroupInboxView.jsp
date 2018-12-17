<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author vikas.sasidharan
--%>
<html>
<head>
    <title><s:text name="title.manageBusinessRuleGroup.listRuleGroups"/></title>
    <s:head theme="twms" />

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">
    	dojo.require("dojox.layout.ContentPane");
    	var extraParams = {
            context : "<s:property value="context" />"
        };

        function noOp() {}

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("ruleGroupTable"));
        }

        function createRuleGroup() {
            parent.publishEvent("/tab/open", {
                label: "<s:text name="title.manageBusinessRuleGroup.createRuleGroup" />",
                decendentOf: getMyTabLabel(),
                url: "setup_rule_group_creation.action?context=" + extraParams.context,
                forceNewTab: true
            });
        }

        function changeRuleGroupsPriority() {
            parent.publishEvent("/tab/open", {
                label: "<s:text name="title.manageBusinessRuleGroup.changeRuleGroupsPriority" />",
                decendentOf: getMyTabLabel(),
                url: "setup_rule_groups_prioritizaton.action?context=" + extraParams.context,
                forceNewTab: true
            });
        }

        function exportToExcel(){
         	exportExcel("/domainRuleGroups/populateCriteria","export_rule_groups_to_excel.action");
        }
        
    </script>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <jsp:include  page="/i18N_javascript_vars.jsp" />
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
       <%--  <u:summaryTableButton id="deleteButton" label="button.common.delete"
                    onclick="noOp" disabled="true" align="right"
                    cssClass="delete_button" summaryTableId="ruleGroupTable"/> --%>
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="ruleGroupTable"/>
       <%--  <u:summaryTableButton id="printButton" label="button.common.print"
                    onclick="noOp" disabled="true" align="right" cssClass="print_button"
                    summaryTableId="ruleGroupTable"/> --%>
        <u:summaryTableButton id="createRuleGroupButton"
                    label="button.manageBusinessRuleGroup.createRuleGroup"
                    onclick="createRuleGroup"
                    summaryTableId="ruleGroupTable"
                    cssClass="create_rule_group_button"/>
        <u:summaryTableButton id="changeRuleGroupsPriorityButton"
                    label="button.manageBusinessRuleGroup.changeRuleGroupsPriority"
                    onclick="changeRuleGroupsPriority"
                    summaryTableId="ruleGroupTable"
                    cssClass="change_rule_groups_priority_button"/>
        <u:summaryTableButton id="downloadListing"
        			label="button.common.downloadToExcel" 
        			onclick="exportToExcel"
               		align="right" 
               		cssClass="download_to_excel_button" 
               		summaryTableId="ruleGroupTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" />
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="ruleGroupTable"
                          bodyUrl="get_rule_groups_body.action"
                          extraParamsVar="extraParams"
                          folderName="BUSINESS_RULE_GROUPS"
                          previewUrl="rule_group_preview.action"
                          detailUrl="rule_group_detail.action"
                          previewPaneId="preview"
                          populateCriteriaDataOn="/domainRuleGroups/populateCriteria"
                          parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminManageBusinessRuleGroupsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>