<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author roopali.agrawal
--%>

<html>
<head>
    <title>::<s:text name="title.common.warranty" />::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        function refreshIt() {
			publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("partReturnTable"));
        }
        
        
        function handleShowSearch(event, dataId) {
			var id=document.getElementById("domainPredicateId").value;
			var url = "detail_view_search_expression.action";
			url += "?id=" + id;
			var contextName=document.getElementById("contextName").value;
			url += "&context="+contextName;
			var savedQueryId=document.getElementById("savedQueryId").value;
			url += "&savedQueryId="+savedQueryId;
			/**todo-get rid of this hard coding.*/
			parent.publishEvent("/tab/reload", {label: "Edit Expression",
												 url: url,decendentOf:"Home",
												tab: getTabHavingId(getTabDetailsForIframe().tabId)});
		}
		
		function exportToExcel(){
         	exportExcel("/partReturn/populateCriteria","exportPartReturnSearchToExcel.action");
        }
		
		dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
   		});
   		 
   	    var extraParams = {
	    	domainPredicateId : <s:property value="domainPredicateId"/>
	    };	 		
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
  <s:hidden name="domainPredicateId"/>
<s:hidden name="savedQueryId"/>
<s:hidden name="contextName"/>
  
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="partReturnTable"/>
        <u:summaryTableButton id="queryButton" label="viewInbox_jsp.inboxButton.show_search_query" onclick="handleShowSearch" summaryTableId="partReturnTable" cssClass="new_warranty_registration_button"/>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="partReturnTable"/>
    </div>		
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4"
   		 activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="partReturnTable" bodyUrl="get_partReturn_search_body.action"
        			      extraParamsVar="extraParams"
        				  folderName="%{folderName}" previewUrl="view_partReturn_search_preview.action" 
        				  detailUrl="view_partReturn_search_detail.action" 
        				  populateCriteriaDataOn="/partReturn/populateCriteria"
                          previewPaneId="preview" parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
    
	
</html>
