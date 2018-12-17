<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: Apr 16, 2007
  Time: 7:27:44 PM
--%>

<html>
<head>
    <title><s:text name="title.manageWarehouse"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/warehouseLabels.js"></script> 
    <script type="text/javascript" src="scripts/labels.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "warehouseTable"));
        }
       
        function createWarehouse(evt) {
            parent.publishEvent("/tab/open", {
                label: i18N.create_warehouse,
                decendentOf: getMyTabLabel(),
                url: "create_warehouse.action",
                forceNewTab: true
            });
        }
		function exportToExcel(){
         exportExcel("/warehouse/populateCriteria","export_warehouse_to_excel.action");
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="warehouseTable"/>        
        <u:summaryTableButton id="createWarehouseButton"
                    label="button.manageWarehouse.createWarehouse"
                    onclick="createWarehouse"
                    summaryTableId="warehouseTable"
                    cssClass="create_warehouse_button"/>
        <u:summaryTableButton id="labelButton" label="button.common.addLabels" onclick="showLabels" summaryTableId="warehouseTable"/>
        <u:summaryTableButton id="downloadListing"
                    label="button.common.downloadToExcel"
                    onclick="exportToExcel"
                    align="right"
                    cssClass="download_to_excel_button"
                    summaryTableId="warehouseTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="warehouseTable"
                          bodyUrl="get_warehouse_body.action"
                          folderName="WAREHOUSE"
                          previewUrl="view_warehouse_preview.action"
                          detailUrl="view_warehouse.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          populateCriteriaDataOn="/warehouse/populateCriteria"  multiSelect="true">
            <s:iterator value="tableHeadData">
                <s:if test="imageColumn">
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer"	labelColumn="%{labelColumn}" 
            			hidden="%{hidden}"/>
            	</s:if>
            	<s:else>
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
                	labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
              </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <s:hidden name="labelType" value="%{@tavant.twms.domain.common.Label@WAREHOUSE}" />
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
   <jsp:include flush="true" page="../../common/labelsDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminManageWarehousesReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>