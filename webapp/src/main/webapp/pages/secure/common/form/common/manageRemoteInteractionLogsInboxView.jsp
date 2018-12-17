<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title>Remote Interaction Logs</title>
    <s:head theme="twms" />
    <u:stylePicker fileName="SummaryTable.css"/>
    <%--Hack Added for IE to show style in reprocessing dialog--%>
    <u:stylePicker fileName="preview.css"/>
    <u:stylePicker fileName="ui-ext/actionResult/actionResult.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dojox.layout.ContentPane");
    </script>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <script type="text/javascript">
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("rilTable"));
        }

        function refreshAfterDelete(){
        	dijit.byId("deleteDialog").hide();
            refreshIt();
        }
        
        function refreshAfterReprocess(){
            console.log("reprocess");
            dijit.byId("reprocessingDialog").hide();
            refreshIt();
        }

        function reprocessSyncs(){
            var reprocessingDialog = dijit.byId("reprocessingDialog");
            var reprocessingDialogIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/>Processing ...</center>";
            var form = document.getElementById("remoteLogForm");
            form.action = "remoteInteractionLogsReprocess.action";
            var data = dijit.byId("reprocessing_data");
            reprocessingDialog.show();
            reprocessingDialog.setContent(reprocessingDialogIndicator);
            var content = {};
            var reprocessButton = dojo.byId("_summaryTable_button_reprocessButton");
            content[reprocessButton.name] = reprocessButton.value;
            content['syncType'] = "<s:property value="syncType"/>";
            content['actionPerformed'] = "Reprocess";
            dojo.xhrPost({
                    form: form,
                    content: content,
                    load: function(data) {
                        reprocessingDialog.setContent(data);
                    },
                    error: function(error) {
                        reprocessingDialog.setContent(error);
                    }
            }); 
        }

        function deleteSync(){
            var deleteDialog = dijit.byId("deleteDialog");
            var reprocessingDialogIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/>Deleting ...</center>";
            var form = document.getElementById("remoteLogForm");
            form.action = "deleteRemoteInteractionLogs.action";
            var data = dijit.byId("deleting_data");
            deleteDialog.show();
            deleteDialog.setContent(reprocessingDialogIndicator);
            var content = {};
            var deleteButton = dojo.byId("_summaryTable_button_deleteButton");
            var content = {};
            content[deleteButton.name] = deleteButton.value;
            content['syncType'] = "<s:property value="syncType"/>";
            content['actionPerformed'] = "Delete";
            dojo.xhrPost({
                    form: form,
                    content: content,
                    load: function(data) {
                        deleteDialog.setContent(data);
                    },
                    error: function(error) {
                        deleteDialog.setContent(error);
                    }
            });
        }
        
        function exportToExcel(){
         exportExcel("/remoteLogs/populateCriteria","exportRemoteLogsToExcel.action");
        }
        var extraParams = {
            syncType:"<s:property value="syncType"/>",
            transactionId:"<s:property value="transactionId"/>",
            businessId:"<s:property value="businessId"/>",
            statusSelected:"<s:property value="statusSelected"/>",
            includeDeleted:"<s:property value="includeDeleted"/>"
        };
        <s:if test="fromDate!=null">
	    extraParams.fromDate="<s:property value="fromDate"/>";
        </s:if>
        <s:if test="toDate!=null">
	    extraParams.toDate="<s:property value="toDate"/>";
        </s:if>

            function handleShowSearch() {
			var url = "manage_remote_interaction_logs.action";
			url += "?syncType=" + escape(extraParams.syncType);
			url += "&transactionId="+escape(extraParams.transactionId);
			url += "&statusSelected="+escape(extraParams.statusSelected);
			url += "&businessId="+escape(extraParams.businessId);
            url += "&includeDeleted="+escape(extraParams.includeDeleted);
                        if(extraParams.fromDate)
                            url += "&fromDate="+extraParams.fromDate;
                        if(extraParams.toDate)
                            url += "&toDate="+extraParams.toDate;
			/**todo-get rid of this hard coding.*/
			parent.publishEvent("/tab/reload", {label: "Remote Interaction Logs",
												 url: url,decendentOf:"Home",
												tab: getTabHavingId(getTabDetailsForIframe().tabId)});
		}
    // trap for escape key so that dialog closes only when clicked on close button and not by escape key press
    dojo.addOnLoad(function()  {
      dojo.connect(dijit.byId("reprocessing_data").containerNode, 'onkeypress', function (evt) {
        key = evt.keyCode;
        if(key == dojo.keys.ESCAPE) {
                dojo.stopEvent(evt);
        }
      });
   });
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
      <s:form id="remoteLogForm" method="POST" action="remoteInteractionLogsReprocess.action" cssStyle="width: 100%; height: 100%">
      <s:hidden id="inboxViewSortField" name="inboxViewSortField"/>
	  <s:hidden id="inboxViewSortOrder" name="inboxViewSortOrder"/>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
        <s:if  test="isReprocessingRequired()">
            <u:summaryTableButton id="reprocessButton" label="Reprocess" onclick="reprocessSyncs" align="left" summaryTableId="rilTable"/>
        </s:if>
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="rilTable"/>
        <u:summaryTableButton id="queryButton" label="viewInbox_jsp.inboxButton.show_search_query" onclick="handleShowSearch" summaryTableId="rilTable" cssClass="show_search_query_button"/>
        <s:if test="isDeleteButtonRequired()">
            <u:summaryTableButton id="deleteButton" label="label.common.hide" onclick="deleteSync" summaryTableId="rilTable" cssClass="show_search_query_button"/>
        </s:if>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="rilTable"/>

    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" />
<u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="rilTable"
                          bodyUrl="getRemoteInteractionLogs.action"
                          extraParamsVar="extraParams"
                          folderName="remoteLogs"
                          previewPaneId="preview"
                          populateCriteriaDataOn="/remoteLogs/populateCriteria"
                          parentSplitContainerId="split"
                          previewUrl="showRemoteLogsPreview.action">
            <s:iterator value="tableHeadData">
                <s:if test="checkBoxColumn ">
            		<script type="text/javascript" src="scripts/tst_commonExt/CheckBoxRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			rendererClass="tavant.twms.summaryTableExt.CheckBoxRenderer" labelColumn="%{labelColumn}"
                                hidden="%{hidden}" disableSorting="%{disableSorting}" disableFiltering="%{disableFiltering}" dataType="%{dataType}"/>
            	</s:if>
            	<s:else>
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
                                      labelColumn="%{labelColumn}" hidden="%{hidden}"
                                      disableSorting="%{disableSorting}" disableFiltering="%{disableFiltering}"/>
                </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
<jsp:include flush="true" page="../../ExcelDowloadDialog.jsp"></jsp:include>
<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
</script>

<div dojoType="twms.widget.Dialog" id="reprocessingDialog"
    style="width:95%; height:95%;overflow:auto"
    title="Re-Processing <s:property value="syncType"/>" closable="false" bgColor="blue" >
        <div dojoType="dojox.layout.ContentPane" layoutAlign="client" id="reprocessing_data"
			executeScripts="true" renderStyles="true">
            <%-- AJAX response (HTML)will be added here--%>
        </div>
</div>
<div dojoType="twms.widget.Dialog" id="deleteDialog"
    style="width:95%; height:95%;overflow:auto"
    title="Deleting <s:property value="syncType"/>" closable="false" bgColor="blue" >
        <div dojoType="dojox.layout.ContentPane" layoutAlign="client" id="deleting_data"
			executeScripts="true" renderStyles="true">
            <%-- AJAX response (HTML)will be added here--%>
        </div>
</div>
  </s:form>
</u:body>
</html>