<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author janmejay.singh
--%>
<html>
<head>
<title><s:text name="title.managePolicy.inventoryInboxView"/></title>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css"/>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

    dojo.require("dojox.layout.ContentPane");
    
function refreshIt() {
	publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("lrConfigTable"));
}
   

   
function createPolicyModifier(event, dataId) {
	var thisTabLabel = getMyTabLabel();
	var url = "view_policy_price_modifier.action";
	parent.publishEvent("/tab/open", {
								label: i18N.create_policy_modifier, 
								url: url, 
								decendentOf: thisTabLabel,
								forceNewTab: true
								});
}
   

        function exportToExcel(){
         exportExcel("/lrConfig/populateCriteria","exportPolicyModiferToExcel.action");
        }
dojo.require("dijit.layout.LayoutContainer")
</script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
		
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="lrConfigTable" />
		<u:summaryTableButton id="warrantyRegButton"
			label="button.managePolicy.createPolicyModifier"
			onclick="createPolicyModifier" summaryTableId="lrConfigTable"
			cssClass="new_warranty_registration_button" />
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
            align="right" cssClass="download_to_excel_button" summaryTableId="lrConfigTable"/>
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable id="lrConfigTable" eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
			bodyUrl="get_policy_price_modifiers_body.action" folderName="%{folderName}"
			previewUrl="preview_policy_price_modifier.action"
			detailUrl="view_policy_price_modifier.action" previewPaneId="preview"
			parentSplitContainerId="split"
            populateCriteriaDataOn="/lrConfig/populateCriteria">
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
</html>
