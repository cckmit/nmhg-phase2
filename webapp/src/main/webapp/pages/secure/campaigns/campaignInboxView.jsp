<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title><s:text name="label.campaigns.pendingCampaignsInboxView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("notificationTable"));
        }

        function validateDataSelected(dataId){
            var frm = '<s:text name="error.inventory.invalidFieldModSelected" />';
            alert(frm);
        } 
        
        function createClaim(event, dataId) {
        	   if (dataId=='') {
                   validateDataSelected(dataId);
                }
			else {
				var url = "create_campaign_claim.action";
				var tabLabel = "Campaign Claim ";
				url += "?notificationId=" + dataId+"&fromPendingCampaign=true";
				tabLabel += " " + dataId;

				parent.publishEvent("/tab/open", {
					label: tabLabel,
					url: url,
					decendentOf : "<s:text name="accordion_jsp.campaigns.pendingCampaigns"/>",
					forceNewTab: true
					});
			}
        }

        function exportToExcel() {
        	exportExcel("/notification/populateCriteria","exportToExcel.action");
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body smudgeAlert="false">
  <u:actionResults wipeMessages="false"/>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
        
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="notificationTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel" align="right" cssClass="download_to_excel_button" summaryTableId="notificationTable"/>
       	<u:summaryTableButton id="claimButton" label="summaryTable.inboxButton.create_campaign_claim" onclick="createClaim" summaryTableId="notificationTable" cssClass="create_claim_button"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="notificationTable" bodyUrl="get_assignments.action" folderName="%{folderName}"
        				  previewUrl="preview_assignment.action" detailUrl="detail_assignment_view.action"
                          previewPaneId="preview" parentSplitContainerId="split" populateCriteriaDataOn="/notification/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" 
                labelColumn="%{labelColumn}" hidden="%{hidden}" 
                disableSorting="%{disableSorting}" disableFiltering="%{disableFiltering}" />
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>