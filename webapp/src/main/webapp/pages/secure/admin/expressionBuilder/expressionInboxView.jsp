<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="authz" uri="authz" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: Apr 16, 2007
  Time: 7:27:44 PM
--%>

<html>
<head>
    <title><s:text name="title.manageBusinessCondition"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        var extraParams = {
            context : "<s:property value="context" />"
        };

        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "expressionTable"));
        }
        

        function createExpression() {
            parent.publishEvent("/tab/open", {
                label: i18N.new_expression,
                decendentOf: getMyTabLabel(),
                url: "new_expression.action?context=<s:property value="context" />",
                forceNewTab: true
            });
        }
        
        function exportToExcel(){
      	    exportExcel("/businessCondition/populateCriteria","exportBusinessConditionsToExcel.action");
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
                    summaryTableId="expressionTable"/>
        <u:summaryTableButton id="createExpressionButton"
                    label="button.manageBusinessCondition.createBusinessCondition"
                    onclick="createExpression"
                    summaryTableId="expressionTable"
                    cssClass="create_expression_button"/>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="expressionTable"/>            
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="expressionTable"
                          bodyUrl="get_expressions_body.action"
                          folderName="BUSINESS_CONDITIONS"
                          previewUrl="preview_view_expression.action"
                          detailUrl="detail_view_expression.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          populateCriteriaDataOn="/businessCondition/populateCriteria"
                          extraParamsVar="extraParams">
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
<authz:ifPermitted resource="warrantyAdminWarrantyBusinessConditionsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="contractAdminManageBusinessConditionsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>