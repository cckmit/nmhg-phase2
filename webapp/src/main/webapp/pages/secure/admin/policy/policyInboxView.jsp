<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author aniruddha.chaturvedi
--%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><s:text name="title.managePolicy.inboxView"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/policylabels.js"></script>
    <script type="text/javascript" src="scripts/labels.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("policyConfigTable"));
        }
        
        function createPolicyConfig(event, dataId) {
            var thisTablabel = getMyTabLabel();
            var url = "new_policy.action";
            parent.publishEvent("/tab/open", {
            						label: i18N.new_policy_definition, 
            						url: url, 
            						decendentOf: thisTablabel,
            						forceNewTab: true
            					});
        }
        function exportToExcel(){
         exportExcel("/policyConfig/populateCriteria","exportPolicyToExcel.action");
        }
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
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsdiv">
       
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="policyConfigTable"/>
       
        <u:summaryTableButton id="warrantyRegButton" label="button.managePolicy.createPolicy" onclick="createPolicyConfig" summaryTableId="policyConfigTable" cssClass="new_warranty_registration_button"/>
        <u:summaryTableButton id="labelButton" label="button.common.addLabels" onclick="showLabels" summaryTableId="policyConfigTable" cssClass="addLabels_retailItems_button"/>
        <u:summaryTableButton id="downloadListing" label="button.common.downloadToExcel" onclick="exportToExcel"
               align="right" cssClass="download_to_excel_button" summaryTableId="policyConfigTable"/>
    </div>

    
    
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" />
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="policyConfigTable"
                        bodyUrl="policy_table_body.action" folderName="%{folderName}" previewUrl="policy_preview.action"
                        detailUrl="policy_detail.action" previewPaneId="preview" parentSplitContainerId="split" 
                        multiSelect="true"
            populateCriteriaDataOn="/policyConfig/populateCriteria">
            <s:iterator value="tableHeadData">
            	<s:if test="imageColumn">
            		<script type="text/javascript" src="scripts/tst_commonExt/ImageRenderer.js"></script>
            		<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}"
            			disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"
            			rendererClass="tavant.twms.summaryTableExt.ImageRenderer"	labelColumn="%{labelColumn}" 
            			hidden="%{hidden}"/>
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
  </div>
			<authz:ifPermitted resource="warrantyAdminPolicyDefinitionReadOnlyView">
				<script type="text/javascript">
				    dojo.addOnLoad(function() {
				    	document.getElementById("buttonsdiv").style.display="none";
				    });
				</script>
			</authz:ifPermitted>
  <s:hidden name="labelType" value="%{@tavant.twms.domain.common.Label@POLICY}" />
  <jsp:include flush="true" page="../../common/labelsDialog.jsp"></jsp:include>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
</html>
