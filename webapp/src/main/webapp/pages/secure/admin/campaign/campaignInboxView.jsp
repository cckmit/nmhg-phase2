<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.campaign.serviceCampaign"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/campaignlabels.js"></script>
    <script type="text/javascript" src="scripts/labels.js"></script>
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "campaignTable"));
        }
        
        function createCampaign() {
			var url = "initialize_campaign.action";
            parent.publishEvent("/tab/open", {
                url: url,
                label: i18N.create_campaign,
                decendentOf: getMyTabLabel(),
                forceNewTab: true
            });
        }
		
		function exportToExcel() {
        	exportExcel("/campaigns/populateCriteria","exportAdminDataToExcel.action");
        }
        
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body >
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="campaignTable"/>
        <u:summaryTableButton id="createCampaignButton"
                    label="button.campaign.createCampaign"
                    onclick="createCampaign"
                    summaryTableId="campaignTable"
                    cssClass="create_campaign_button"/>
       <u:summaryTableButton id="labelButton"
         label="button.common.addLabels"
          onclick="showLabels" 
          summaryTableId="campaignTable" 
          cssClass="addLabels_retailItems_button"/>
        <u:summaryTableButton id="downloadListing"
        			label="button.common.downloadToExcel" 
        			onclick="exportToExcel" 
        			align="right" 
        			cssClass="download_to_excel_button" 
        			summaryTableId="campaignTable"/>            
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" />
         <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="campaignTable"
                          bodyUrl="get_campaigns.action" folderName="CAMPAIGN" previewUrl="preview_campaign.action"
                          detailUrl="initialize_campaign.action" previewPaneId="preview"
                          populateCriteriaDataOn="/campaigns/populateCriteria" parentSplitContainerId="split" multiSelect="true">
            <s:iterator value="tableHeadData">
            <s:if test="imageColumn">
            	<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" 
            			disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer" labelColumn="%{labelColumn}" 
            			hidden="%{hidden}"/> 
            	</s:if>
            	<s:else>
                  <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
              </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
   <s:hidden name="labelType" value="%{@tavant.twms.domain.common.Label@CAMPAIGN}" />
  <jsp:include flush="true" page="../../common/labelsDialog.jsp"></jsp:include>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"/>
  </u:body>
<authz:ifPermitted resource="warrantyAdminFieldProductImprovementReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>