<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<title><s:text name="label.uom.mapping"/></title>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css" />

<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("uomMappingsTable"));
        }                                
        function exportToExcel() {
        	exportExcel("/notification/populateCriteria","downloadUomsToExcel.action");
        }                
	    var extraParams = {	    	
	    	view :'view'
	    };
	    
	    function addUom(event, dataId) {
        	var thisTabLabel = getMyTabLabel();			
            parent.publishEvent("/tab/open", {label: "Add Uom",
            								  url: "addUomMapping.action",
            								  decendentOf: thisTabLabel,
            								  forceNewTab: true});
        }
	    
</script>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body smudgeAlert="false">
	<u:actionResults wipeMessages="false" />
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top"
		class="buttonContainer">
	<u:summaryTableButton id="addUomButton" label="button.uom.addUom" onclick="addUom" summaryTableId="uomMappingsTable" cssClass="create_claim_button"/>	
	<u:summaryTableButton id="refreshButton" label="button.common.refresh" 
		onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="uomMappingsTable" /> 
	<u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel"
		onclick="exportToExcel" align="right" cssClass="download_to_excel_button" 
		summaryTableId="uomMappingsTable" />
	</div>

	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false"><u:stylePicker fileName="SummaryTableButton.css" />
	<u:summaryTable
		eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
		id="uomMappingsTable" bodyUrl="mappedUomsBody.action"
		folderName="%{folderName}" previewUrl="uomMappingPreview.action"
		detailUrl="uomMappingDetail.action" extraParamsVar="extraParams"
		previewPaneId="preview" parentSplitContainerId="split"
		populateCriteriaDataOn="/notification/populateCriteria">
		<s:iterator value="tableHeadData">          
            	<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>          
       	</s:iterator>
		<script type="text/javascript"
			src="scripts/SummaryTableTagEventHandler.js"></script>
	</u:summaryTable>		
	
	<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
	</div>
	<jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
</html>
