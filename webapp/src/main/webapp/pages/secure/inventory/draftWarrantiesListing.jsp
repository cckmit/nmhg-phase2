<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author fatima.marneni
--%>

<html>
<head>
    <title><s:text name="label.inventory.draftWarrantiesListing"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("draftWarrantyTable"));
        }
        
        function registerWarranty(event, dataId) {
            launchNewWarranyRegistration(dataId, "<s:text name="accordion_jsp.inventory.stock"/>");
        }
        function xferEquipment(event, dataId) {
            launchEquipmentTransfer(dataId, "<s:text name="accordion_jsp.inventory.retailed"/>");
        }
        var extraParams = {
        		draft : "<s:property value='draft'/>",
                        status: "<s:property value='status'/>",
                        transactionType:"<s:property value='transactionType'/>"
        		};
        
        function exportToExcel(){
         exportExcel("/inventory/populateCriteria","exportDraftWarrantyToExcel.action");
        }
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body >
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%" id="inventoryInboxViewRootLayout">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="inventoryInboxViewButtonPane">
        
        <u:summaryTableButton id="refreshButton" label="viewInbox_jsp.inboxButton.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="draftWarrantyTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="draftWarrantyTable"/>
    </div>

    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="7" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" />
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="draftWarrantyTable"
                  bodyUrl="draftWarrantyBody.action" folderName="%{folderName}"
                  previewUrl="warrantyPreview.action" detailUrl="warrantyDetail.action" extraParamsVar="extraParams"
                  rootLayoutContainerId="inventoryInboxViewRootLayout" buttonContainerId="inventoryInboxViewButtonPane"
                  previewPaneId="preview" parentSplitContainerId="split" enableTableMinimize="true"
                  populateCriteriaDataOn="/inventory/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
            </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>

