<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title><s:text name="title.campaign.itemSearchInboxView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");

        var extraParams = {
    		"searchCriteria.serialNumber" : "<s:property value="searchCriteria.serialNumber"/>",
    		"searchCriteria.status" : "<s:property value="searchCriteria.status"/>",
    		"searchCriteria.assignedDealerId" : "<s:property value="searchCriteria.assignedDealerId"/>"
    	};
       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("itemSearchTable"));
        }
        function exportToExcel() {
            publishEvent("/itemSearch/populateCriteria", {
            	url : "exportToExcel.action",
            	returnTo : function(newUrl) {
            		getFileDownloader().download(newUrl);
            	}
            });
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
        
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="itemSearchTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel" align="right" cssClass="download_to_excel_button" summaryTableId="itemSearchTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="itemSearchTable"
                        bodyUrl="campaignItemsSearchBody.action" folderName="%{folderName}" previewUrl="previewCampaignItemsSearch.action" detailUrl="detailCampaignItemsSearch.action"
                        previewPaneId="preview" parentSplitContainerId="split" extraParamsVar="extraParams"
                        populateCriteriaDataOn="/itemSearch/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" 
                labelColumn="%{labelColumn}" hidden="%{hidden}" disableSorting="%{disableSorting}" 
                disableFiltering="%{disableFiltering}" />
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>