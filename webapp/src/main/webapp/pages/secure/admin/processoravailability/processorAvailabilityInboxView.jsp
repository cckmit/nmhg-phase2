<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author Jhulfikar Ali
--%>

<html>
<head>
    <title><s:text name="title.processorAvail.processoravailableview"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dojox.layout.ContentPane");
    </script>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">
	    var extraParams = {
	    		"role" : "<s:property value="role"/>"
	    	};

	    function exportToExcel(){
	    	exportExcel("/processorAvailable/populateCriteria","export_processor_avail_to_excel.action");
	    }
	    
	    function refreshIt() {
	        publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("processorTable"));
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
	    <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" 
	    	onclick="exportToExcel" align="right" cssClass="download_to_excel_button" 
	    		summaryTableId="processorTable"/>
    
    	<u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" 
    		align="right" cssClass="refresh_button" summaryTableId="processorTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" />
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="processorTable" 
                        bodyUrl="processorAvailableBody.action?selectedBusinessUnit=%{selectedBusinessUnit}" folderName="%{folderName}" previewUrl="processor_avail_preview.action"
                        previewPaneId="preview" detailUrl="processor_avail_detail.action" parentSplitContainerId="split"
                        extraParamsVar="extraParams" populateCriteriaDataOn="/processorAvailable/populateCriteria">
            <s:iterator value="tableHeadData">
            <s:if test="imageColumn ">
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer" labelColumn="%{labelColumn}"
            			hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}" />
            	</s:if>
            	<s:else>
                	<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" 
                		labelColumn="%{labelColumn}" hidden="%{hidden}" 
                		disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}" />
                </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>
