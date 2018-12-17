<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
    <title>Inclusive Job Codes Inbox View</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script> 
    
     <script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("inclusiveJobCodeTable"));
        }

        function exportToExcel(){
            exportExcel("/inclusiveJobCode/populateCriteria","exportInclusiveJobCodesToExcel.action");
        }

        function createInclusiveJobCode() {
        	var thisTabLabel = getMyTabLabel();
			var url = "create_inclusive_job_code.action?createInclusiveJobCode=true" ;
            parent.publishEvent("/tab/open", {label: "Create Inclusive Job Code",
            								  url: url,
            								  decendentOf: thisTabLabel,
            								  forceNewTab: true});
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
        		label="viewInbox_jsp.inboxButton.refresh"
        		onclick="refreshIt" 
        		align="right" 
        		cssClass="refresh_button" 
        		summaryTableId="inclusiveJobCodeTable"/>
        		
        <u:summaryTableButton id="downloadListing" 
        		label="button.common.downloadToExcel" 
        		onclick="exportToExcel"
                align="right" 
                cssClass="download_to_excel_button" 
                summaryTableId="inclusiveJobCodeTable"/>		
        		
         <u:summaryTableButton id="createInclusiveJobCodeButton" 
                label="button.manageInclusiveJobCodes.createInclusiveJobCode"  
	 			onclick="createInclusiveJobCode"
	 			align="left"
	 			cssClass="create_inclusive_job_code_button" 
	 			summaryTableId="inclusiveJobCodeTable"/>					
	 
    </div>
    
      <div dojoType="dijit.layout.SplitContainer" 
		layoutAlign="client" orientation="vertical" sizerWidth="7" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" />
        
         <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" 
        		  id="inclusiveJobCodeTable"
                  bodyUrl="get_parent_job_codes.action" 
                  folderName="INCLUSIVE_JOB_CODES"
                  previewUrl="inclusive_job_code_preview.action" 
                  detailUrl="create_inclusive_job_code.action"
               	  previewPaneId="preview" 
                  parentSplitContainerId="split" 
                  rootLayoutContainerId="inventoryInboxViewRootLayout" 
                  buttonContainerId="inventoryInboxViewButtonPane"
                  enableTableMinimize="true"
                  populateCriteriaDataOn="/inclusiveJobCode/populateCriteria">                 
            <s:iterator value="tableHeadData">
 					<u:summaryTableColumn id="%{id}" label="%{title}" 
						width="%{widthPercent}" idColumn="%{idColumn}" 
						labelColumn="%{labelColumn}" hidden="%{hidden}" 
						disableFiltering="%{disableFiltering}" 
						disableSorting="%{disableSorting}"/>
            </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>    
  </div>
   <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>  
 </u:body>      
</html>        
       