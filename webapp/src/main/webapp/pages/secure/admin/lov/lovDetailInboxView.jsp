<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
<head>
    <title><s:text name="title.listOfValues"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <u:stylePicker fileName="common.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">
		
		dojo.require("dojox.layout.ContentPane");

    	var extraParam = {
    		lovTypeName : '<s:property value="lovTypeName"/>'
    	};

       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "lovDetailTable"));
        }
       
        function addValue(evt, dataId) {
			var url = "create_lov.action?lovTypeName=" + extraParam.lovTypeName;
            parent.publishEvent("/tab/open", {
                label: i18N.create_lov,
                decendentOf: getMyTabLabel(),
                url: url,
                forceNewTab: true
            });
        }

		function exportToExcel(){
         exportExcel("/lovDetail/populateCriteria","export_lov_detail_to_excel.action");
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="lovDetailTable"/>
        <u:summaryTableButton id="addValueButton"
                    label="button.listOfValues.addValue"
                    onclick="addValue"
                    summaryTableId="lovDetailTable"
                    cssClass="addValue_lov_button"/>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel"
                    onclick="exportToExcel" align="right" cssClass="download_to_excel_button"
                    summaryTableId="lovDetailTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="lovDetailTable"
                          bodyUrl="get_lov_body.action"
                          folderName="LOV_DETAIL"
                          previewUrl="view_lov_preview.action"
                          detailUrl="view_lov.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          extraParamsVar="extraParam"
                          populateCriteriaDataOn="/lovDetail/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminListOfValuesReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>