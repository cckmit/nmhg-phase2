<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author mritunjay.kumar
--%>

<html>
<head>
    <title> </title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.SplitContainer");
        dojo.require("dojox.layout.ContentPane");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("itemSearchTable"));
        }
       
        function exportToExcel(){
         exportExcel("/itemSearch/populateCriteria","exportItemSearchToExcel.action");
        }
        function handleShowSearch(event, dataId) {
		 var id=document.getElementById("domainPredicateId").value;
		 var url = "detail_view_search_expression.action";
		 url += "?id=" + id;
		 var contextName=document.getElementById("contextName").value;
		 url += "&context="+contextName;
		 var savedQueryId=document.getElementById("savedQueryId").value;
		 url += "&savedQueryId="+savedQueryId;
		 parent.publishEvent("/tab/reload", {
		 	label: i18N.edit_expression,
			url: url,
			decendentOf:"Home",
		 	tab: getTabHavingId(getTabDetailsForIframe().tabId)});
		}
		
		dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
	    });
	    var extraParams = {
	    	domainPredicateId : <s:property value="domainPredicateId"/>
	    };
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

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="itemSearchTable"/>
        <u:summaryTableButton id="searchButton" label="button.viewClaim.showSearchQuery" onclick="handleShowSearch" summaryTableId="itemSearchTable" cssClass="show_search_query_button" />

    </div>
    <s:hidden id="contextName" name="contextName"/>
    <s:hidden id="savedQueryId" name="savedQueryId"/>
    <s:hidden id="domainPredicateId" name="domainPredicateId"/>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">

      <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="itemSearchTable" bodyUrl="itemSearchBody.action" 
             extraParamsVar="extraParams"
             folderName="%{folderName}" previewUrl="itemSearchPreview.action" detailUrl="itemSearchDetail.action"
             previewPaneId="preview" parentSplitContainerId="split"
             populateCriteriaDataOn="/itemSearch/populateCriteria">

        <s:iterator value="tableHeadData">
          <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
             labelColumn="%{labelColumn}" hidden="%{hidden}"/>
        </s:iterator>

      <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>

        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>

  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>

</u:body>
</html>
