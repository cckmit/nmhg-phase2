<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%--
  @author aniruddha.chaturvedi
--%>

<html>
<head>
    <title><s:text name="title.managePolicy.inboxView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/inventoryLabels.js"></script>
    <script type="text/javascript" src="scripts/labels.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");

        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("retailConfigTable"));
        }
       
		function handleShowSearch(event, dataId) {
			var id=document.getElementById("domainPredicateId").value;
			var url = "detail_view_search_expression.action";
			url += "?id=" + id;
			var contextName=document.getElementById("contextName").value;
			url += "&context="+contextName;
			var savedQueryId=document.getElementById("savedQueryId").value;
			url += "&savedQueryId="+savedQueryId;
            var isCreateLabelForInventory=document.getElementById("isCreateLabelForInventory").value;
		    url += "&isCreateLabelForInventory="+isCreateLabelForInventory;
            /**todo-get rid of this hard coding.*/
			parent.publishEvent("/tab/reload", {label: "Edit Expression",
												 url: url,decendentOf:"Home",
												tab: getTabHavingId(getTabDetailsForIframe().tabId)});
		}

        function exportToExcel(){
         exportExcel("/retailConfig/populateCriteria","exportInvSearchToExcel.action");
        }

		dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
	    });

		var extraParams = {
	    	domainPredicateId : <s:property value="domainPredicateId"/>
	    };
    </script>
    <style type="text/css">
        #labels_div {
            background : #F3FBFE;
            opacity : 1.0;
        }
    </style>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <s:hidden id="isCreateLabelForInventory" name="isCreateLabelForInventory"/> 
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer">
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="retailConfigTable"/>

        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="retailConfigTable"/>
        <authz:ifAdmin>
            <s:if test="isCreateLabelForInventory=='true'">
                <u:summaryTableButton id="labelButton" label="button.common.addLabels" onclick="showLabels" summaryTableId="retailConfigTable" cssClass="addLabels_retailItems_button" />
            </s:if>
        </authz:ifAdmin>
        <u:summaryTableButton id="searchButton" label="viewInbox_jsp.inboxButton.show_search_query" onclick="handleShowSearch" summaryTableId="retailConfigTable" cssClass="show_search_query_button" />
    </div>

 

		<s:hidden id="contextName" name="contextName"/>
		<s:hidden id="savedQueryId" name="savedQueryId"/>
		<s:hidden id="domainPredicateId" name="domainPredicateId"/>
		

    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4"
    	activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="retailConfigTable" bodyUrl="inventorySearchBody.action"
        				  populateCriteriaDataOn="/retailConfig/populateCriteria"
        				  folderName="%{folderName}" previewUrl="view_retail_item_preview.action" 
        				  detailUrl="view_retail_item_detail.action" extraParamsVar="extraParams"
                          previewPaneId="preview" parentSplitContainerId="split" multiSelect="true">
            <s:iterator value="tableHeadData">
            	<s:if test="imageColumn">
            		<authz:ifAdmin>
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer"	labelColumn="%{labelColumn}"
            			hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
            		</authz:ifAdmin>
            	</s:if>
            	<s:else>
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
                	labelColumn="%{labelColumn}" hidden="%{hidden}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"/>
              </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div><s:hidden name="labelType" value="%{@tavant.twms.domain.common.Label@INVENTORY}" />
    <jsp:include flush="true" page="../../common/labelsDialog.jsp"></jsp:include>
  	<jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>
