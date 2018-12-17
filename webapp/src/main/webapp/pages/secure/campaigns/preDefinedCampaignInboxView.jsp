<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<title><s:text name="label.campaigns.pendingCampaignsInboxView" /></title>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css" />

<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("notificationTable"));
        }

        function deleQuery(){
	    	var url = 'deletePredefinedInventorySearchQuery.action?queryId=<s:property value="savedQueryId"/>';			
			var param ='<s:property value="queryId"/>';		
			twms.ajax.fireHtmlRequest(url, param,
					function(data){
				parent.publishEvent("/accordion/refreshsearchfolders");
				closeTabAfterFinishing();
				});
		}
		
	    function closeTabAfterFinishing() {
            var tabDetails = getTabDetailsForIframe();
            var tab = getTabHavingId(tabDetails.tabId);
            parent.publishEvent("/tab/close", {tab:tab});
        }
        
        function createClaim(event, dataId) {
        	var url = "create_campaign_claim.action";
			var tabLabel = "Campaign Claim ";

			if (dataId) {
				url += "?notificationId=" + dataId+"&fromPendingCampaign=true";
				tabLabel += " " + dataId;

				parent.publishEvent("/tab/open", {
					label: tabLabel,
					url: url,
					decendentOf : "<s:text name="accordion_jsp.campaigns.pendingCampaigns"/>",
					forceNewTab: true
					});
			}
        }
        
         function handleShowSearch(event, dataId) {

		 
		  var formObj = dojo.byId("showSearchQuerySubmitForm");
          formObj.action = "showPreDefinedCampaignsSearchQuery.action";
          formObj.submit();	
		 	
		}
        
        function exportToExcel() {
        	exportExcel("/notification/populateCriteria","exportToExcelForCampaigns.action");
        }
        
	    var extraParams = {
	    	savedQueryId : '<s:property value="savedQueryId"/>',
	    	notATemporaryQuery : '<s:property value="notATemporaryQuery"/>',
	    	searchQueryName :'<s:property value="searchQueryName"/>'
	    };
	 
	    dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
	    });
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
	<u:summaryTableButton id="refreshButton" label="button.common.refresh" 
		onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="notificationTable" /> 
	<u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel"
		onclick="exportToExcel" align="right" cssClass="download_to_excel_button" 
		summaryTableId="notificationTable" />
	
	<u:summaryTableButton id="searchButton"
		label="button.viewClaim.showSearchQuery" onclick="handleShowSearch"
		summaryTableId="notificationTable" cssClass="show_search_query_button" />
		<s:if test="searchQueryName!=null">
		<u:summaryTableButton id="deleteQueryButton" label="button.common.delete" 
		onclick="deleQuery" summaryTableId="notificationTable" cssClass="show_search_query_button"/>
	</s:if>
	</div>
	<form name="showSearchQuerySubmitForm" id="showSearchQuerySubmitForm" />
		<s:hidden id="savedQueryId" name="savedQueryId" /> 
		<s:hidden name="notATemporaryQuery" />
		<s:hidden name="searchQueryName" />	
	</form>

	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false"><u:stylePicker fileName="SummaryTableButton.css" />
	<u:summaryTable
		eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
		id="notificationTable" bodyUrl="preDefinedCampaignsBody.action"
		folderName="%{folderName}" previewUrl="preview_assignment.action"
		detailUrl="detail_view.action" extraParamsVar="extraParams"
		previewPaneId="preview" parentSplitContainerId="split"
		populateCriteriaDataOn="/notification/populateCriteria">
		<s:iterator value="tableHeadData">
		  <s:if test="imageColumn">
		    <script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            <u:summaryTableColumn id="%{id}" label="%{title}" 
				width="%{widthPercent}" idColumn="%{idColumn}"
            	rendererClass="tavant.twms.summaryTableExt.ImageRenderer"
            	labelColumn="%{labelColumn}" hidden="%{hidden}"/>
          </s:if>
          <s:else>
               	<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
          </s:else>
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
