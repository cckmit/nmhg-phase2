<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author kiran.sg
--%>
<html>
<head>
<title><s:text name="title.managePolicy.inventoryInboxView"/></title>
<s:head theme="twms" />

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

   dojo.require("dijit.layout.LayoutContainer");
   dojo.require("dijit.layout.ContentPane");
   dojo.require("dojox.layout.ContentPane");
    
function refreshIt() {
	publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("contractListTable"));
}
   

   
function createContract(event, dataId) {
	var thisTabLabel = getMyTabLabel();
	parent.publishEvent("/tab/open", 
						{label: i18N.new_contract, 
						 url: "create_contract.action", 
						 decendentOf: thisTabLabel ,
						 forceNewTab: true
						 });
}

        function exportToExcel(){
         exportExcel("/contractList/populateCriteria","exportcontractListToExcel.action");
        }

</script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="SummaryTable.css" />
<u:stylePicker fileName="SummaryTableButton.css" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="contractListTable" />
		<u:summaryTableButton id="createContractButton"
			label="viewInbox_jsp.inboxButton.create_contract"
			onclick="createContract" summaryTableId="contractListTable"
			cssClass="new_warranty_registration_button" />
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
            align="right" cssClass="download_to_excel_button" summaryTableId="contractListTable"/>
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable id="contractListTable" eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
			bodyUrl="contracts_table_body.action" folderName="%{folderName}"
			previewUrl="contract_preview.action"
			detailUrl="contract_detail.action" previewPaneId="preview"
			parentSplitContainerId="split"
            populateCriteriaDataOn="/contractList/populateCriteria">
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
<authz:ifPermitted resource="contractAdminMaintainSupplierContractsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>
