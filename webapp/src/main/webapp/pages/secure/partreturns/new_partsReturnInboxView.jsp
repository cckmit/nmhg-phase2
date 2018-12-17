<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <title>::<s:text name="title.common.warranty" />::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    
    <script type="text/javascript">

       dojo.require("dijit.layout.ContentPane");
       dojo.require("dojox.layout.ContentPane");
       dojo.require("dijit.layout.LayoutContainer");
        
        function refreshIt() {
			publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("partReturnTable"));
        }
        
        function handleShowSearch(event, dataId) {
		  var formObj = dojo.byId("showSearchQuerySubmitForm");
          formObj.action = "showPredefinedPartReturnQuery.action";
          formObj.submit(); 			
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
        
		
		function exportToExcel(){
         	exportExcel("/partReturn/populateCriteria","downloadPartReturnSearchToExcel.action");
        }
		var extraParams = {
			queryId :'<s:property value="queryId"/>',
			notATemporaryQuery : '<s:property value="notATemporaryQuery"/>',
			searchQueryName :'<s:property value="searchQueryName"/>'
		};
		
		dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
   		});
   	    	 		
    </script>
    <style type="text/css">
        #labels_div {
            background : #F3FBFE;
            opacity : 1.0;
        }
    </style>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
 <u:body>
  <form  name="showSearchQuerySubmitForm" id="showSearchQuerySubmitForm">
      <s:hidden id="queryId" name="queryId"/>
      <s:hidden name="contextName"/>
	  <s:hidden name="notATemporaryQuery" />
	  <s:hidden name="searchQueryName" />
	  <s:hidden name="context" />
	   </form>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
     <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="partReturnTable"/>
        <u:summaryTableButton id="queryButton" label="viewInbox_jsp.inboxButton.show_search_query" onclick="handleShowSearch" summaryTableId="partReturnTable" cssClass="new_warranty_registration_button"/>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="partReturnTable"/>
        <s:if test="searchQueryName!=null">
        <u:summaryTableButton id="deleteQueryButton" label="button.common.delete" 
		onclick="deleQuery" summaryTableId="partReturnTable" cssClass="new_warranty_registration_button"/>
		</s:if>
    </div> 
   
	
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4"
   		 activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> 
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="partReturnTable" bodyUrl="preDefinepartReturnSearchBody.action"
        				  extraParamsVar="extraParams"
        				  folderName="%{folderName}" previewUrl="view_partReturn_search_preview.action" 
        				  detailUrl="view_partReturn_search_detail.action" 
        				  populateCriteriaDataOn="/partReturn/populateCriteria"
                          previewPaneId="preview" parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" 
                idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
    
</html>
