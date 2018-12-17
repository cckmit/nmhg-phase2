<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.managePayment.paymentDefinition"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.SplitContainer");
        
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "paymentDefinitionTable"));
        }
        

        function createPaymentDefinition(evt) {
			var thisTabLabel = getMyTabLabel();
           	parent.publishEvent("/tab/open", {
					           	label: i18N.newPaymentDefinition,
					           	url: "new_payment_definition.action",
					           	decendentOf: thisTabLabel,
					           	forceNewTab: true
					           	});
        }

        function definePaymentDefinftionForCP(evt) {
        	var thisTabLabel = getMyTabLabel();
           	parent.publishEvent("/tab/open", {
					           	label: i18N.definePaymentDefinitionForCP,
					           	url: "define_payment_definition_for_cp.action",
					           	decendentOf: thisTabLabel,
					           	forceNewTab: true
					           	});
        }

        function exportToExcel(){
         exportExcel("/paymentDefinition/populateCriteria","export_payment_definition_to_excel.action");
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
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="paymentDefinitionTable"/>
        <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
        <u:summaryTableButton id="createPaymentDefinitionButton"
                    label="button.managePayment.createPaymentDefinition"
                    onclick="createPaymentDefinition"
                    summaryTableId="paymentDefinitionTable"
                    cssClass="create_paymentDefinition_button"/>
		</authz:ifNotPermitted>
		<u:summaryTableButton id="downloadListing"
               		label="button.common.downloadToExcel"
               		onclick="exportToExcel"
               		align="right"
               		cssClass="download_to_excel_button"
               		summaryTableId="paymentDefinitionTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="paymentDefinitionTable"
                          bodyUrl="get_payment_definitions_body.action"
                          folderName="PAYMENT_DEFINITION"
                          previewUrl="view_payment_definition.action"
                          detailUrl="detail_payment_definition.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          populateCriteriaDataOn="/paymentDefinition/populateCriteria">
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
  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>