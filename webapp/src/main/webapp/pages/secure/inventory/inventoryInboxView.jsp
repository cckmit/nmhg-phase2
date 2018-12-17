<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
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
        
        var pageUrl = "inventory.action";
    	function getPageUrl(){
    		var folderName="<s:property value="folderName"/>";
    		pageUrl = "inventory.action?"+"folderName="+folderName;
    		return pageUrl;
    	}
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("inventoryTable"));
        }
        
        function registerWarranty(event, dataId) {
            launchNewWarranyRegistration(dataId, "<s:text name="accordion_jsp.inventory.stock"/>","");
        }
        function xferEquipment(event, dataId) {
            launchEquipmentTransfer(dataId, "<s:text name="accordion_jsp.inventory.retailed"/>","");
        }

        function createClaim(event, dataId) {
            var url = "chooseClaimTypeFromInventoryInbox.action?&claim.forDealer=<s:property value='%{@tavant.twms.web.common.SessionUtil@getDealerFromSession(#session).name}'/>";
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

        <s:if test="inboxViewId!=null && !inboxViewId.trim().equals('')">
	    var extraParams = {
		draft : "<s:property value='draft'/>",
			inboxViewId : <s:property value="inboxViewId"/>
	    };
	</s:if>
	<s:else>
	    var extraParams = {
			draft : "<s:property value='draft'/>"
	    };
	</s:else>
        function exportToExcel(){
         exportExcel("/inventory/populateCriteria","exportInvToExcel.action");
        }

        function purchaseExtendedWarranty(event,dataId){
        	var url = "show_extended_warrantyplans.action";
			var decendentOfLocal = "<s:text name="summaryTable.inboxButton.purchase_warranty"/>";
			var tabLabel = i18N.extended_warranty_purchase;
			if (dataId) {
				url += "?inventoryItems="+dataId+"&inventoryItems=";
				tabLabel += " " + dataId;
			}
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel()});
			delete url, tabLabel;
        }
        
       function dealerToDealer(event,dataId){
        	var url = "show_D2D_transfer_inventory.action";
			var decendentOfLocal = "<s:text name="title.dealertodealer.dealertodealertransfer"/>";
			var tabLabel = i18N.dealer_to_dealer;
			if (dataId) {
				url += "?inventoryItem="+dataId;
				tabLabel += " " + dataId;
			}
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel()});
			delete url, tabLabel;
        }

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
  <u:body smudgeAlert="false">
  <s:hidden name="context" value="InventorySearches"/>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%" id="inventoryInboxViewRootLayout">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="inventoryInboxViewButtonPane">
    <div id="inventoryInboxViewOnlyDiv">
        <u:summaryTableButton id="refreshButton" label="viewInbox_jsp.inboxButton.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="inventoryTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="inventoryTable"/>
        <s:if test="stockInventory">
			<s:if test="wntyRegAllowedStock">
	            <u:summaryTableButton id="warrantyRegButton" label="summaryTable.inboxButton.new_warranty_registration" 
	            	onclick="registerWarranty" summaryTableId="inventoryTable" cssClass="new_warranty_registration_button"/>
			</s:if>
			<authz:ifUserInRole roles="inventoryAdmin">
			<u:summaryTableButton id="D2Dbutton" label="title.dealertodealer.dealertodealertransfer" onclick="dealerToDealer" summaryTableId="inventoryTable" cssClass="new_warranty_registration_button"/>
			</authz:ifUserInRole>
			<authz:ifUserNotInRole roles="inventoryAdmin">
			<s:if test="isD2DAllowed()">
            <u:summaryTableButton id="D2Dbutton" label="title.dealertodealer.dealertodealertransfer" onclick="dealerToDealer" summaryTableId="inventoryTable" cssClass="new_warranty_registration_button"/>
            </s:if>
            </authz:ifUserNotInRole>
        </s:if>
        <s:else>
            <u:summaryTableButton  id="equipmentXferButton" label="summaryTable.inboxButton.warranty_transfer" onclick="xferEquipment" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            <s:if test="eligibleForExtendedWarrantyPurchase">
            <u:summaryTableButton  id="extendedWarrantyPurchase" label="summaryTable.inboxButton.purchase_warranty" onclick="purchaseExtendedWarranty" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            </s:if>
            <s:if test="eligibleForRetailMachineTransfer">
            <u:summaryTableButton  id="retailMachineTransfer" label="summaryTable.inboxButton.retail_machine_transfer" onclick="performRMT" summaryTableId="inventoryTable" cssClass="warranty_transfer_button"/>
            </s:if>
        </s:else>
    
    	<div style="clear: both;">
    		<authz:ifUserInRole roles="dealerWarrantyAdmin">
    		<s:if test="isStockClaimAllowed() && stockInventory">
	    		<u:summaryTableButton  id="createClaimButton" label="button.newClaim.createClaim" align="left"  
	 				onclick="createClaim" summaryTableId="inventoryTable" cssClass="create_claim_button" 
	 			/>
	 			</s:if>
	 		<s:elseif test="!stockInventory">
	 		<u:summaryTableButton  id="createClaimButton" label="button.newClaim.createClaim" align="left"  
	 				onclick="createClaim" summaryTableId="inventoryTable" cssClass="create_claim_button" 
	 			/>
	 		</s:elseif>
 			</authz:ifUserInRole>
	 		
	 	</div>
	</div>
 	<%@ include file="../common/inboxViewForm.jsp"%>
    </div>

	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="7" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="inventoryTable"
                  bodyUrl="inventoryBody.action" folderName="%{folderName}"
                  previewUrl="inventoryPreview.action" detailUrl="inventoryDetail.action" extraParamsVar="extraParams"
                  rootLayoutContainerId="inventoryInboxViewRootLayout" buttonContainerId="inventoryInboxViewButtonPane"
                  previewPaneId="preview" parentSplitContainerId="split" enableTableMinimize="true"
                  populateCriteriaDataOn="/inventory/populateCriteria">
            <s:iterator value="tableHeadData">  
                <s:if test="imageColumn">
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer"	labelColumn="%{labelColumn}"
            			hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            	</s:if>
            	<s:else>
                	<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
                </s:else>
            </s:iterator>      
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  <script>
  var obj=document.getElementsByName("inboxViewId")[0];
  var selectedValue;
  if (obj != undefined) {
	  var opts=obj.options;	  
	  for(var i=0;i<opts.length;i++)
	  {
	  	if(opts[i].selected)
	  	{
	  		selectedValue=opts[i].value;
	  		break;
	  	}
	  }		
  }
  summaryTableVars.inventoryTable.extraParamsVar={
			"inboxViewId" : selectedValue
		};
  
  </script>  
  </u:body>
<authz:ifPermitted resource="inventoryRetailedReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("inventoryInboxViewOnlyDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="inventoryStockReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("inventoryInboxViewOnlyDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>

