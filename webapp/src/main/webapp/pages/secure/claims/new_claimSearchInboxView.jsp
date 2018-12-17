<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<title></title>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css" />
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("claimSearchTable"));
        }
        
	    function exportToExcel(){
         exportExcel("/claimSearch/populateCriteria","exportPreDefinedClaimSearchesToExcel.action");
        }

	    function deleQuery(){
	    	var url = 'deletePredefinedInventorySearchQuery.action?queryId=<s:property value="queryId"/>';			
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
        
        function handleShowSearch(event, dataId) {
//		 var val=document.getElementById("searchString").value;
//		 var url = "showPreDefinedClaimsSearchQuery.action";
//		 url += "?searchString=" + val;
//		 parent.publishEvent("/tab/reload", {
//		 	label: i18N.edit_expression,
//			url: url,
//			decendentOf:"Home",
//		 	tab: getTabHavingId(getTabDetailsForIframe().tabId)});
          var formObj = dojo.byId("showSearchQuerySubmitForm");
          formObj.action = "showPreDefinedClaimsSearchQuery.action";
          formObj.submit(); 
		}
		
		dojo.addOnLoad(function () {			
				parent.publishEvent("/accordion/refreshsearchfolders");					
	    });
	    var extraParams = {
			<s:if test="inboxViewId!=null && !inboxViewId.trim().equals('')">
				inboxViewId : '<s:property value="inboxViewId"/>',
			</s:if>
	    	queryId :'<s:property value="queryId" />',
	    	notATemporaryQuery : '<s:property value="notATemporaryQuery"/>',
	    	savedQueryName : '<s:property value="savedQueryName"/>'
	    };
	    
	    function getPageUrl() {
	    	return 'showPreDefinedClaimSearchResults.action?queryId=<s:property value="queryId" />&'+
	    			'notATemporaryQuery=<s:property value="notATemporaryQuery"/>&'+
	    			'savedQueryName=<s:property value="savedQueryName"/>&'+
	    			'folderName=<s:property value="folderName"/>&'+
	    			'context=<s:property value="context"/>';
	    }
    </script>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body smudgeAlert="false">
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top"
		class="buttonContainer">
	<u:summaryTableButton id="refreshButton"
		label="button.common.refresh" onclick="refreshIt" align="right"
		cssClass="refresh_button" summaryTableId="claimSearchTable" /> 
	<u:summaryTableButton
		id="downloadListing" label="button.common.downloadToExcel"
		onclick="exportToExcel" align="right"
		cssClass="download_to_excel_button" summaryTableId="claimSearchTable" />
	<u:summaryTableButton id="searchButton"
		label="button.viewClaim.showSearchQuery" onclick="handleShowSearch"
		summaryTableId="claimSearchTable" cssClass="show_search_query_button" />
	<s:if test="savedQueryName!=null">
		<u:summaryTableButton id="deleteQueryButton" label="button.common.delete" 
		onclick="deleQuery" summaryTableId="claimSearchTable" cssClass="show_search_query_button"/>
	</s:if>
	<%@ include file="../common/inboxViewForm.jsp"%>	

	</div>
	
	<form name="showSearchQuerySubmitForm" id="showSearchQuerySubmitForm">
	<s:hidden id="folderName1" name="folderName" />
	<s:hidden id="context" name="context" /> 
	<s:hidden id="queryId" name="queryId" />
	<s:hidden name="notATemporaryQuery" />
	<s:hidden name="savedQueryName"/>
	</form>

	
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false"><u:stylePicker fileName="SummaryTableButton.css" />
		<u:summaryTable	eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
		id="claimSearchTable" bodyUrl="preDefinedSearchSummaryBody.action"
		extraParamsVar="extraParams" folderName="%{folderName}"
		previewUrl="view_search_preview.action"
		detailUrl="view_search_detail.action" previewPaneId="preview"
		parentSplitContainerId="split"
		populateCriteriaDataOn="/claimSearch/populateCriteria"
		>

		<s:iterator value="tableHeadData">
			<u:summaryTableColumn id="%{id}" label="%{title}"
				width="%{widthPercent}" idColumn="%{idColumn}"
				labelColumn="%{labelColumn}" hidden="%{hidden}" 
				disableFiltering="%{disableFiltering}"
				disableSorting="%{disableSorting}"/>
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