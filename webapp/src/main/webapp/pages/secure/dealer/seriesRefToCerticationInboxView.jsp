<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author subin.p
--%>

<html>
<head>
    <title><s:text name="title.technician.inboxView" /></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        dojo.require("dojox.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.MultipleInventoryPicker");
        dojo.require("twms.widget.DateTextBox");

        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("seriesRefToCertificationTable"));
        }
        
        function exportToExcel(){
            exportExcel("/seriesRefToCertification/populateCriteria","exportCertificationToExcel.action");
        }     
        
       /*  dojo.byId("create_technician").form.action="showNewCertificationForm.action";  */
        
         function createTechnician(event, dataId) {
            var url = "showNewCertificationForm.action";
            var thisTablabel = getMyTabLabel();
            parent.publishEvent("/tab/open", {
            						label: i18N.new_technician_certification, 
            						url: url, 
            						decendentOf: thisTablabel,
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
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
			
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="seriesRefToCertificationTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="seriesRefToCertificationTable"/> 
               
       <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
       <input id="create_technician" class="buttonGeneric" type="button" value="<s:text name='label.technician.createSeriesToCertification'/>"
							onclick="createTechnician()" />
		</authz:ifNotPermitted>                
		<%-- <u:summaryTableButton id="create_technician" label="label.technician.createTechnician" onclick="createTechnician" summaryTableId="seriesRefToCertification" cssClass="new_warranty_registration_button"/> --%>
    </div>   
    
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> 
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="seriesRefToCertificationTable"
                        bodyUrl="seriesRefToCertification_table_body.action" folderName="%{folderName}" previewUrl="seriesRefToCertification_preview.action"
                        detailUrl="seriesRefToCertification_detail.action" previewPaneId="previewSeriesRefToCertificationTable" parentSplitContainerId="split" 
                        multiSelect="true"
            populateCriteriaDataOn="/seriesRefToCertification/populateCriteria">
            
         <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
                	labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
         </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="previewSeriesRefToCertificationTable">
        </div>
    </div>  
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="settingsSeriesReftoCertificationReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>