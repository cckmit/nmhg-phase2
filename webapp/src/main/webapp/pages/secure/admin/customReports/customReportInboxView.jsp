<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 11, 2008
  Time: 11:42:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <title>Custom Report Inbox View</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dojox.layout.ContentPane");
    </script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("customReportTable"));
        }

        function exportToExcel(){
         exportExcel("/customReport/populateCriteria","exportReportsToExcel.action");
        }
        function createReport(event,dataId){
        	var url = "new_custom_report.action";
			var decendentOfLocal = getMyTabLabel();
			var tabLabel = "Create Report";
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOfLocal});		
			delete url, tabLabel;
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
           <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt"
                                 align="right" cssClass="refresh_button" summaryTableId="customReportTable"/>
           <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
                  align="right" cssClass="download_to_excel_button" summaryTableId="customReportTable"/>
           <u:summaryTableButton id="createCustomReport" label="summaryTable.inboxButton.createCustomReport"
            	onclick="createReport" summaryTableId="customReportTable" cssClass="new_warranty_registration_button"/>
        </div>
        <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
            <u:stylePicker fileName="SummaryTableButton.css" />
            <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="customReportTable"
                            bodyUrl="customReportBody.action"
                            folderName="%{folderName}"
                            previewUrl="customReport_preview.action"
                            previewPaneId="preview"
                            detailUrl="customReport_detail.action"
                            parentSplitContainerId="split"
                            populateCriteriaDataOn="/customReport/populateCriteria">
                <s:iterator value="tableHeadData">
                    <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
                </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
            <div dojoType="dojox.layout.ContentPane" id="preview">
            </div>
        </div>
    </div>
    <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
</html>