<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 12:00:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@include file="/i18N_javascript_vars.jsp"%>
<html>
<head>
    <title><s:text name="title.newClaim.warrantyProcessView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dojox.layout.ContentPane");
    </script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("warrantyTable"));
        }

        function exportToExcel(){
         exportExcel("/warranty/populateCriteria","exportWarrantyProcessToExcel.action");
        }

        var extraParams = {
            status:"<s:property value="status"/>",
            transactionType:"<s:property value="transactionType"/>"
        };
           
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
  </head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
           <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt"
                                 align="right" cssClass="refresh_button" summaryTableId="warrantyTable"/>
           <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
                  align="right" cssClass="download_to_excel_button" summaryTableId="warrantyTable"/>
        </div>
        <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
            <u:stylePicker fileName="SummaryTableButton.css" />
            <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="warrantyTable"
                            bodyUrl="warrantyBody.action"
                            extraParamsVar="extraParams"
                            folderName="%{folderName}"
                            previewUrl="warrantyProcess_preview.action"
                            previewPaneId="preview"
                            detailUrl="warrantyProcess_detail.action"
                            parentSplitContainerId="split"
                            populateCriteriaDataOn="/warranty/populateCriteria">
                <s:iterator value="tableHeadData">
                    <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
                </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
            <div dojoType="dojox.layout.ContentPane" id="preview">
            </div>
        </div>
    </div>
    <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
</html>
