<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.manageGroup"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        var extraParams = {
    		schemeId : "<s:property value="computedSchemeId"/>"
    	};

        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "itemGroupTable"));
        }
        
        function createItemGroup(evt) {
			var url ="create_item_group.action?id=" + "<s:property value="computedSchemeId"/>";
            parent.publishEvent("/tab/open", {
                label: i18N.new_itemGroup,
                decendentOf: getMyTabLabel(),
                url: url,
                forceNewTab: true
            });
        }

 		function exportToExcel(){
         exportExcel("/itemGroup/populateCriteria","export_item_group_to_excel.action");
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
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="itemGroupTable"/>
        <s:if test="!itemScheme.purposeProductStructure">
        <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
            <u:summaryTableButton id="createItemGroupButton"
                        label="button.manageGroup.createItemGroup"
                        onclick="createItemGroup"
                        summaryTableId="itemGroupTable"
                        cssClass="create_itemGroup_button"/>
        </authz:ifNotPermitted>
        </s:if>
		<u:summaryTableButton id="downloadListing"
               		label="button.common.downloadToExcel"
               		onclick="exportToExcel"
               		align="right"
               		cssClass="download_to_excel_button"
               		summaryTableId="itemGroupTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="itemGroupTable"
                          bodyUrl="get_item_groups_body.action"
                          folderName="ITEM_GROUPS"
                          previewUrl="view_item_group_preview.action"
                          detailUrl="view_item_group.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split" extraParamsVar="extraParams"
                          populateCriteriaDataOn="/itemGroup/populateCriteria">
            <s:iterator value="tableHeadData">
            	<s:if test="imageColumn">
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer"	labelColumn="%{labelColumn}"
            			hidden="%{hidden}"/>
            	</s:if>
            	<s:else>
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}"/>
              </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>