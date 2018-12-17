<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%--
  @author kiran.sg
--%>

<html>
<head>
<title><s:text name="title.managePolicy.inventoryInboxView"/></title>
<s:head theme="twms" />

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<script type="text/javascript">

    dojo.require("dojox.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.ContentPane");
    
		 <%-- CR TKTSA-923 --%>
        var pageUrl = "<s:property value="actionUrl"/>";

		function getPageUrl(){
			var pageUrl = "<s:property value="actionUrl"/>"+".action?"+"folderName="+"<s:property value="folderName"/>"+"&"+"taskName="+"<s:property value="taskName"/>"+"&"+"actionUrl="+"<s:property value="actionUrl"/>";
			return pageUrl;
		}
		
		
		function refreshIt() {
			publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("supplyRecoveryInboxTable"));
		}
		
		<%--CR TKTSA-817  --%>
		function switchView(event, dataId) {   
			var folderName = "<s:property value="folderName"/>";
			var actionUrl = "<s:property value="actionUrl"/>";
			var inboxViewType = "<s:property value="inboxViewType"/>";
			
			//quick fix for CR TKTSA-817
			if(folderName.indexOf("_") != -1){
				folderName=folderName.substring(0,folderName.indexOf("_"));
				actionUrl=actionUrl.substring(0,actionUrl.indexOf("_"));
				inboxViewType="recoveryClaim";
			}else{
				folderName=folderName+"_part"; 								
				actionUrl=actionUrl+"_partView";
				inboxViewType="part";
			}

				parent.publishEvent("/tab/reload", {
	                label: "<s:text name="%{switchButtonTabLabel}"/>", 
	                url: "<s:property value="switchButtonActionName"/>.action?"+"folderName="+folderName+"&"+"taskName="+"<s:property value="taskName"/>"+"&"+"actionUrl="+actionUrl+"&"+"inboxViewType="+inboxViewType,
	                tab: getTabHavingId(getTabDetailsForIframe().tabId)
	 	        });
	        	
	    }

		function bulkShipment(event, dataIds) {	
			var transitionTaken = "<s:property value='%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}'/>";
			twms.ajax.fireJavaScriptRequest("generate_bulk_shipment.action",{
				taskIdsForBulkShipment: dataIds,
				//bulkShipment: "true",
				transitionTaken : transitionTaken
		        }, function(details) {
				        if(details[0]){
					        	refreshIt();
					        	//pop up is no more required for bulk shipment - fix for SLMS-1833
					        	//window.open('partShipperUpdateTag_shipmentTag.action?shipment='+details[1],'ShipmentTag',
								//'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
			           		}else{
								dijit.byId("invalidSupplierLocation").show();
			           		}
		           	}
		        );
		}
		<s:if test="inboxViewId!=null && !inboxViewId.trim().equals('')">
	    var extraParams = {
			inboxViewId : <s:property value="inboxViewId"/>
	    };
		</s:if>
		
		function exportToExcel(){
			var actionUrl="<s:property value="actionUrl"/>";
			exportExcel("/supplyRecoveryInbox/populateCriteria",
			actionUrl+ '_exportToExcel.action');
			}

</script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="SummaryTable.css" />
<u:stylePicker fileName="SummaryTableButton.css" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
	<s:hidden name="transitionTaken" id="transitionTaken"/>
		<u:summaryTableButton id="refreshButton"
			label="button.common.refresh" onclick="refreshIt"
			align="right" cssClass="refresh_button" summaryTableId="supplyRecoveryInboxTable" />
		<u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
            align="right" cssClass="download_to_excel_button" summaryTableId="supplyRecoveryInboxTable"/>
        <s:if test="!doNotDisplayCustomInbox()">
            <%@ include file="../common/inboxViewForm.jsp"%>
        </s:if>
        <s:if test="isSwitchViewEnabled">
        	<u:summaryTableButton id="warrantyRegButton" label="viewInbox_jsp.inboxButton.%{switchButtonActionName}" onclick="switchView" summaryTableId="supplyRecoveryInboxTable" cssClass="new_warranty_registration_button"/>
        </s:if> 
        <s:if test="folderName.equals('Supplier Parts Claimed')">
	        <u:summaryTableButton id="labelButton" label="button.label.bulk.generateShipment" onclick="bulkShipment"
	         summaryTableId="supplyRecoveryInboxTable" cssClass="addLabels_retailItems_button"/>
         </s:if>
          <s:hidden name="context" value="SupplierRecoveryFolders"/>

   		 <%-- CR TKTSA-923
    	  <s:if test="folderName.equals(@tavant.twms.domain.common.Constants@NEW) || folderName.equals(@tavant.twms.domain.common.Constants@ACCEPTED) || folderName.equals(@tavant.twms.domain.common.Constants@DISPUTED) 
    	  			  || folderName.equals(@tavant.twms.domain.common.Constants@REOPENED_CLAIMS)" >
			   <%@ include file="../common/inboxViewForm.jsp"%>
     		  <br />			
     		  <br />			
  		  </s:if>  --%>
  		  
	</div>
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false">
		 <u:summaryTable id="supplyRecoveryInboxTable" folderName="%{folderName}"
			bodyUrl="%{actionUrl}_table_body.action" 
			previewUrl="%{actionUrl}_preview.action" previewPaneId="preview"
			detailUrl="%{actionUrl}_detail.action"
			parentSplitContainerId="split" multiSelect="true"
			eventHandlerClass="tavant.twms.supplierRecovery.summaryTable.EventHandler"
            populateCriteriaDataOn="/supplyRecoveryInbox/populateCriteria">
			<s:iterator value="tableHeadData">
				<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
					idColumn="%{idColumn}" labelColumn="%{labelColumn}"
					hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}" />
			</s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
		<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
<script type="text/javascript">
dojo.declare("tavant.twms.supplierRecovery.summaryTable.EventHandler", tavant.twms.summaryTable.MultiSelectTwmsEventHandler,{
	onRowDblClick : function(event) {
		var thisTabLabel = getMyTabLabel();
		var url = this._getDetailUrl(event.folder, event.dataId);
		if(url != null) {
		 parent.publishEvent("/tab/open", {
            						label: event.labelPrefix + " " + event.labelSuffix.split(":")[0],
            						url: url,
            						decendentOf: thisTabLabel,
            						forceNewTab: true
            						});
		}
	}
});			
</script>

</div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  <div id="dialogBoxContainer" style="display: none; overflow: auto">
		<div dojoType="twms.widget.Dialog" id="invalidSupplierLocation" bgColor="white"
			bgOpacity="0.5" toggle="fade" toggleDuration="250" title='<s:text name="label.common.errors"/>' style="width : 450px ;height : 150px ;">
			<div dojoType="dijit.layout.LayoutContainer" 
			style="background: #F3FBFE; border: 1px solid #EFEBF7">
				<div id="invalidSupplierLocation_div" >
				 <br></br>
				    <div style="font-size:11pt;color:red;text-align:center;">
				     <s:text name="error.partReturns.shipped.differenLocations"/>
				   </div>
	            </div>
            </div>
		</div>
	</div>
	  <script>
    var obj=document.getElementsByName("inboxViewId")[0];
     if(obj!=null && obj.options != null){
	        var opts=obj.options;
	        var selectedValue;
	        for(var i=0;i<opts.length;i++)
	        {
	        	if(opts[i].selected)
	        	{
	        		selectedValue=opts[i].value;
	        		break;
	        	}
	        }
  	summaryTableVars.supplyRecoveryInboxTable.extraParamsVar={
    			"inboxViewId" : selectedValue
    		};
     }
  </script>
<authz:ifPermitted resource="partsRecoveryAwaitingShipmentReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partsRecoveryAwaitingShipmentToWarehouseReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partsRecoveryAwaitingShipmenttoSupplierReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partsRecoverySupplierPartsShippedReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partsRecoveryShipmentGeneratedReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>	
</u:body>
</html>
