<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.manageGroup"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "dealerSchemeTable"));
        }
        
        function createDealerScheme(evt) {

            parent.publishEvent("/tab/open", {
                label: i18N.new_dealerScheme,
                decendentOf: getMyTabLabel(),
                url: "create_dealer_scheme.action",
                forceNewTab: true
            });
        }

        function showGroups(evt, dataId) {

            parent.publishEvent("/tab/open", {
                label: i18N.dealer_groupScheme,
                decendentOf: getMyTabLabel(),
                url: "list_dealer_groups.action?schemeId=" + dataId,
                forceNewTab: true
            });
        }

        function exportToExcel(){
         exportExcel("/dealerScheme/populateCriteria","export_dealer_scheme_to_excel.action");
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer"  style="height:30px;" id="buttonsDiv">
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="dealerSchemeTable"/>
        <u:summaryTableButton id="createDealerSchemeButton"
                    label="button.manageGroup.createNewScheme"
                    onclick="createDealerScheme"
                    summaryTableId="dealerSchemeTable"
                    cssClass="create_dealerScheme_button"/>
        <u:summaryTableButton id="showGroupsButton"
                    label="button.manageGroup.manageGroups"
                    onclick="showGroups"
                    summaryTableId="dealerSchemeTable"
                    cssClass="manage_groups_button"/>
        <u:summaryTableButton id="downloadListing"
                    label="button.common.downloadToExcel"
                    onclick="exportToExcel"
                    align="right"
                    cssClass="download_to_excel_button"
                    summaryTableId="dealerSchemeTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="dealerSchemeTable"
                          bodyUrl="get_dealer_schemes_body.action"
                          folderName="DEALER_GROUPS"
                          previewUrl="show_dealer_groups.action?preview=true"
                          detailUrl="show_dealer_groups.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          populateCriteriaDataOn="/dealerScheme/populateCriteria">
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
<authz:ifPermitted resource="warrantyAdminDealerGroupsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>