<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css" />

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

    dojo.require("dojox.layout.ContentPane");
    
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "relateCampaignTable"));
        }
        
        function createRelatedCampaign(evt) {
            parent.publishEvent("/tab/open", {
                label: "Create Related Field Product Improvement",
                decendentOf: getMyTabLabel(),
                url: "create_related_campaign.action",
                forceNewTab: true
            });
        }
		function exportToExcel(){
         exportExcel("/relatedCampaign/populateCriteria","export_related_campaign_to_excel.action");
        }

    </script>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		layoutChildPriority="top-bottom" style="width: 100%; height: 100%">
	
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top"
		class="buttonContainer" id="buttonsDiv">
		
		<u:summaryTableButton id="refreshButton"
		label="button.common.refresh" onclick="refreshIt" align="right"
		cssClass="refresh_button" summaryTableId="relateCampaignTable" />
		
		<u:summaryTableButton
		id="createRelatedCampaignButton" label="label.relatedCampaign.createRelatedCampaign"
		onclick="createRelatedCampaign" summaryTableId="relateCampaignTable"
		cssClass="create_warehouse_button" /> 
		
		<u:summaryTableButton
		id="downloadListing" label="button.common.downloadToExcel"
		onclick="exportToExcel" align="right"
		cssClass="download_to_excel_button" summaryTableId="relateCampaignTable" />
	</div>
	
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<%-- We don't need folder name info. Hence just setting some junk value here --%> 
        
        <u:stylePicker fileName="SummaryTableButton.css" /> 
        
        <u:summaryTable
		eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
		id="relateCampaignTable" bodyUrl="get_related_campaign_detail.action"
		folderName="RELATECAMPAIGN" previewUrl="view_related_campaign_preview.action"
		detailUrl="view_related_campaign_detail.action" previewPaneId="preview"
		parentSplitContainerId="split"
		populateCriteriaDataOn="/relatedCampaign/populateCriteria">
		
		<s:iterator value="tableHeadData">
			<u:summaryTableColumn id="%{id}" label="%{title}"
				width="%{widthPercent}" idColumn="%{idColumn}"
				labelColumn="%{labelColumn}" hidden="%{hidden}" />
		</s:iterator>
		<script type="text/javascript"
			src="scripts/SummaryTableTagEventHandler.js"></script>
	</u:summaryTable>
	
	<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
	</div>
	<jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
<authz:ifPermitted resource="warrantyAdminRelatedFPIsManagementReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>
