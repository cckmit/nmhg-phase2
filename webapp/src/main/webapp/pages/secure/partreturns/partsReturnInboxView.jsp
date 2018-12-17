<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%--
  @author subin.p
--%>

<html>
<head>
    <title><s:text name="label.partReturns.partsReturnInboxView" /></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("partReturnTable"));
        }
        
        function switchView(event, dataId) {            	   		
        	parent.publishEvent("/tab/reload", {
                label: "<s:text name="%{switchButtonTabLabel}"/>", 
                url: "<s:property value="switchButtonActionName"/>.action",
                tab: getTabHavingId(getTabDetailsForIframe().tabId)
 	        });
        }
        
        function exportToExcel(){
         var theActionUrl =  "<s:property value="actionUrl"/>";
         exportExcel("/" + theActionUrl + "/populateCriteria","export" + theActionUrl + "ToExcel.action");
        }       

        var extraParams = {
            <s:if test="inboxViewType != null">
				inboxViewType : '<s:property value="inboxViewType"/>'
			</s:if>
    	}; 
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
			
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="partReturnTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="partReturnTable"/> 
                      
		<s:if test="isSwitchViewEnabled">
        	<u:summaryTableButton id="warrantyRegButton" label="viewInbox_jsp.inboxButton.%{switchButtonLabel}" onclick="switchView" summaryTableId="partReturnTable" cssClass="new_warranty_registration_button"/>
        </s:if>  
		<s:if test="taskName.equals('Overdue Parts')">
			<span id="partReturn" tabindex="0"  style="margin-top:5px; float:left;">
				<img src="image/warning.gif" width="16" height="14" />
			</span>
			<span dojoType="dijit.Tooltip" connectId="partReturn" >
				<s:iterator value="loggedInUser.businessUnits" status="userBUs">
						<s:text name="message.claim.partdue" >							
							<s:param><s:property value="getPartReturnClaimDenialWindowPeriodForBU(name)"/></s:param>
						</s:text>
				</s:iterator>					
			</span>
		</s:if>
		<s:if test="taskName.equals('Shipment Generated') ">
			<span id="partReturn" tabindex="0" style="margin-top:5px; float:left;">
				<img src="image/warning.gif" width="16" height="14" />
			</span>
			<span dojoType="dijit.Tooltip" connectId="partReturn" >
				<s:iterator value="loggedInUser.businessUnits" status="userBUs">
					    <s:text name="message.claim.shipmentGeneratedDue" >							
							<s:param><s:property value="getShipmentGenClaimDenialWindowPeriodForBU(name)"/></s:param>
						</s:text>
					<br/>			
				</s:iterator>					
			</span>
		</s:if>
		<s:if test="taskName.equals('Rejected Parts')">
			<span id="partReturn" tabindex="0"  style="margin-top:5px; float:left;">
				<img src="image/warning.gif" width="16" height="14" />
			</span>
			<span dojoType="dijit.Tooltip" connectId="partReturn" >
				<s:iterator value="loggedInUser.businessUnits" status="userBUs">
						<s:text name="message.dealer.partreturn.windowperiod" >
							<s:param><s:property value="getDealerRequestedWindowPeriodForBU(name)"/></s:param>
						</s:text>
				</s:iterator>
			</span>
		</s:if>
    </div>   
    
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="partReturnTable" 
        				  bodyUrl="%{actionUrl}_table_body.action" 
        				  extraParamsVar="extraParams"
        				  folderName="%{folderName}"  detailUrl="%{actionUrl}_detail.action"
        				  previewUrl="%{actionUrl}_preview.action"
                          previewPaneId="preview" parentSplitContainerId="split"
                          populateCriteriaDataOn="/%{actionUrl}/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"
                                      disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>     
    
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="partReturnsDuePartsReceiptReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partReturnsDuePartsInspectionReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partReturnsAwaitingShipmentforDealerReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partReturnsShipmentGeneratedForDealerReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partReturnsDealerRequestedPartsShippedReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>