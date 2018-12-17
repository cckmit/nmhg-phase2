<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author kiran.sg
--%>

<html>
<head>
<title><s:text name="title.managePolicy.inventoryInboxView"/></title>
<s:head theme="twms" />
<script type="text/javascript" src="scripts/ui-ext/common/utility.js"></script>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript">

    dojo.require("dojox.layout.ContentPane");
    
function registerProduct(event, dataId) {
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	parent.publishEvent("/tab/open", {label: i18N.register_product, 
		url: "customer_create_warranty.action",
		decendentOf: thisTab.label });
}

function purchaseExtendedWarranty(event, dataId) {
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	parent.publishEvent("/tab/open", {label: i18N.purchase_extended_warranty, 
		url: "purchase_extended_warranty.action?warranty="+dataId,
		decendentOf: thisTab.label });
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
		
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="customerListTable" />
		<u:summaryTableButton id="registerProductButton"
			label="viewInbox_jsp.inboxButton.register_product"
			onclick="registerProduct" summaryTableId="customerListTable"
			cssClass="new_warranty_registration_button" />
		<u:summaryTableButton id="purchaseExtendedWarrantyButton"
			label="viewInbox_jsp.inboxButton.purchase_extended_warranty"
			onclick="purchaseExtendedWarranty" summaryTableId="customerListTable"
			cssClass="new_warranty_registration_button" />
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		<u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable id="customerListTable" eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
			bodyUrl="customer_table_body.action" folderName="%{folderName}"
			previewUrl="customer_preview.action"
			detailUrl="customer_detail.action" previewPaneId="preview"
			parentSplitContainerId="split">
			<s:iterator value="tableHeadData">
				<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
					idColumn="%{idColumn}" labelColumn="%{labelColumn}"
					hidden="%{hidden}" />
			</s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
		<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
</div>
</u:body>
</html>
