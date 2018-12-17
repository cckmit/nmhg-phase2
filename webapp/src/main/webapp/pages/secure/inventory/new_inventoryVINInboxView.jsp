<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%--
  @author janmejay.singh
--%>

<html>
<head>
    <title>Inventory Inbox View</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");

        function closeTabAfterFinishing() {
            var tabDetails = getTabDetailsForIframe();
            var tab = getTabHavingId(tabDetails.tabId);
            parent.publishEvent("/tab/close", {tab:tab});
        }
        
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("inventoryTable"));
        }
        
        function xferEquipment(event, dataId) {
            launchEquipmentTransfer(dataId, "<s:text name="accordion_jsp.inventory.retailed"/>","");
        }
        function createClaim(event, dataId) {        	
            var url = "chooseClaimTypeFromInventoryInbox.action?claim.forDealer=<s:property value='%{@tavant.twms.web.common.SessionUtil@getDealerFromSession(#session).name}'/>";
        	var thisTabLabel = getMyTabLabel();
        	if(dataId){            	
        		url += "&claim.itemReference.referredInventoryItem=" + dataId;
        		url += "&inventoryItem=" + dataId;
        	}			
            parent.publishEvent("/tab/open", {label: i18N.new_claim,
            								  url: url,
            								  decendentOf: thisTabLabel,
            								  forceNewTab: true});
        }
        
		function handleShowSearch(event, dataId) {		
		  var formObj = dojo.byId("showSearchQuerySubmitForm");
          formObj.action = "showQuickVINSearchQuery.action";
          formObj.submit();
		}

        function exportToExcel(){
         exportExcel("/inventory/populateCriteria","downloadInvToExcelForVIN.action");
        }

        function purchaseExtendedWarranty(event,dataId){
        	var url = "show_extended_warrantyplans.action";
			var decendentOfLocal = "<s:text name="summaryTable.inboxButton.purchase_warranty"/>";
			var tabLabel = i18N.extended_warranty_purchase;
			if (dataId) {
				url += "?inventoryItems="+dataId+"&inventoryItems=";
				tabLabel += " " + dataId;
			}
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOfLocal});
			delete url, tabLabel;
        }

        dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
	    });
        
        function performRMT(event,dataId){
        	var url = "show_retail_machine_transfer.action";
			var decendentOfLocal = "<s:text name="summaryTable.inboxButton.retail_machine_transfer"/>";
			var tabLabel = i18N.retail_machine_transfer;
			if (dataId) {
				url += "?inventoryItems="+dataId;
				tabLabel += " " + dataId;
			}
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel()});
			delete url, tabLabel;
        }
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  
   <form  name="showSearchQuerySubmitForm" id="showSearchQuerySubmitForm"></form>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%" id="inventoryInboxViewRootLayout">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="inventoryInboxViewButtonPane">
        
        <u:summaryTableButton id="refreshButton" label="viewInbox_jsp.inboxButton.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="inventoryTable"/>
		<u:summaryTableButton id="queryButton" label="viewInbox_jsp.inboxButton.show_search_query" onclick="handleShowSearch" summaryTableId="inventoryTable" cssClass="new_warranty_registration_button"/>
		
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="inventoryTable"/>
      <authz:ifUserInRole roles="inventoryAdmin,dealer,dealerSalesAdministration">
        <s:if test="inventorySearchCriteria.inventoryType.type=='RETAIL'">
            <u:summaryTableButton  id="equipmentXferButton" label="summaryTable.inboxButton.warranty_transfer" onclick="xferEquipment" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            <s:if test="eligibleForExtendedWarrantyPurchase">
                <u:summaryTableButton  id="extendedWarrantyPurchase" label="summaryTable.inboxButton.purchase_warranty" onclick="purchaseExtendedWarranty" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            </s:if>
            <s:if test="dealerEligibleToPerformRMT">
                <u:summaryTableButton  id="retailMachineTransfer" label="summaryTable.inboxButton.retail_machine_transfer" onclick="performRMT" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            </s:if>
        </s:if>
    
    	<div style="clear: both;">
    		<authz:ifUserInRole roles="dealerWarrantyAdmin,processor">
    		<s:if test="isStockClaimAllowed()">
	    		<u:summaryTableButton  id="createClaimButton" label="button.newClaim.createClaim" align="left"  
	 				onclick="createClaim" summaryTableId="inventoryTable" cssClass="create_claim_button" 
	 			/>
	 			</s:if>
	 			<s:elseif test="inventorySearchCriteria.inventoryType.type=='RETAIL'">
	 		<u:summaryTableButton  id="createClaimButton" label="button.newClaim.createClaim" align="left"  
	 				onclick="createClaim" summaryTableId="inventoryTable" cssClass="create_claim_button" 
	 			/>
	 		</s:elseif>
 			</authz:ifUserInRole>
    		
 		</div>
 		</authz:ifUserInRole>  
    </div>
			

	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="7" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> 
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="inventoryTable"
                  bodyUrl="inventoryQuickSearchBody.action" folderName="%{folderName}"
                  previewUrl="inventoryPreview.action" detailUrl="inventoryDetail.action" 
                  rootLayoutContainerId="inventoryInboxViewRootLayout" buttonContainerId="inventoryInboxViewButtonPane"
                  previewPaneId="preview" parentSplitContainerId="split" enableTableMinimize="true"
                  populateCriteriaDataOn="/inventory/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" 
                hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
 </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>

