<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author janmejay.singh
--%>

<html>
<head>
    <title><s:text name="title.managePayment.inventoryInboxView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("modifierConfigTable"));
        }
       
        function createModifierConfig(event, dataId) {
			var thisTabLabel = getMyTabLabel();
			str = "paymentVariableId=";
			var url = "view_payment_modifier.action?" + str + "<s:property value='paymentVariableId'/>";
			parent.publishEvent("/tab/open", {label: i18N.create_payment_modifier, 
												url: url, 
												decendentOf: thisTabLabel,
												forceNewTab: true
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
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="modifierConfigTable"/>

        <u:summaryTableButton id="warrantyRegButton" label="button.managePayment.createPaymentModifier" onclick="createModifierConfig" summaryTableId="modifierConfigTable" cssClass="create_paymentModifier_button"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="modifierConfigTable" bodyUrl="get_payment_modifier_body.action?paymentVariableId=%{paymentVariableId}" folderName="%{folderName}" previewUrl="preview_payment_modifier.action" detailUrl="view_payment_modifier.action"
                          previewPaneId="preview" parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  </u:body>
</html>