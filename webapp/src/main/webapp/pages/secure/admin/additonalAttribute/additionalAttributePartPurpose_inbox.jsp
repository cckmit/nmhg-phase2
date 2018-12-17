<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<title>::<s:text name="title.common.warranty" />::</title>
<s:head theme="twms" />

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">
dojo.require("dojox.layout.ContentPane");
function refreshIt() {
	publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("attributes_partTable"));
}
  
function exportToExcel(){
   exportExcel("/attributes/populateCriteria","exportPartAttributesListToExcel.action");
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
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="attributes_partTable" />
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
            align="right" cssClass="download_to_excel_button" summaryTableId="attributes_partTable"/>
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable id="attributes_partTable" eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
			bodyUrl="attributes_part_table_body.action" folderName="%{folderName}"
			previewUrl="attributes_part_association_preview.action"
			detailUrl="attributes_part_association_detail.action" previewPaneId="preview"
			parentSplitContainerId="split"
            populateCriteriaDataOn="/attributes/populateCriteria">
			<s:iterator value="tableHeadData">
				<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
					idColumn="%{idColumn}" labelColumn="%{labelColumn}"
					hidden="%{hidden}"  disableFiltering="%{disableFiltering}"/>
			</s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
		<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
</div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
</html>
