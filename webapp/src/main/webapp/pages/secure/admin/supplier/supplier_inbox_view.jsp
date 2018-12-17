<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<title>Supplier Inbox View</title>
<s:head theme="twms" />

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
function refreshIt() {
	publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("supplierListTable"));
}
   
 
function createSupplier(event, dataId) {
	var thisTabLabel = getMyTabLabel();
	parent.publishEvent("/tab/open", 
						{label:i18N.create_supplier, 
						 url: "create_supplier.action", 
						 decendentOf: thisTabLabel ,
						 forceNewTab: true
						 });
}

        function exportToExcel(){
         exportExcel("/supplierList/populateCriteria","exportsupplierListToExcel.action");
        }

</script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="SummaryTable.css"/>
<%@include file="/i18N_javascript_vars.jsp"%>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" layoutChildPriority="top-bottom"
	style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
		
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="supplierListTable" />
		<u:summaryTableButton id="createSupplierButton"
			label="button.manageSuppliers.create"
			onclick="createSupplier" summaryTableId="supplierListTable"
			cssClass="new_warranty_registration_button" />
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
            align="right" cssClass="download_to_excel_button" summaryTableId="supplierListTable"/>
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable id="supplierListTable" eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
			bodyUrl="suppliers_table_body.action" folderName="%{folderName}"
			previewUrl="supplier_item_preview.action"
			detailUrl="supplier_item_detail.action" previewPaneId="preview"
			parentSplitContainerId="split"
            populateCriteriaDataOn="/supplierList/populateCriteria">
			<s:iterator value="tableHeadData">
				<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
					idColumn="%{idColumn}" labelColumn="%{labelColumn}"
					hidden="%{hidden}" />
			</s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
		<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
</div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
<authz:ifPermitted resource="contractAdminManageSuppliersReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>
