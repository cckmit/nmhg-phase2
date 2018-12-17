<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author mritunjay.kumar
--%>

<html>
<head>
    <title>::<s:text name="title.common.warranty" />::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/supplierLabels.js"></script>
    
    <script type="text/javascript" src="scripts/labels.js"></script>
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic("supplierTable"));
        }
      
		function exportToExcel(){
         exportExcel("/supplier/populateCriteria","exportSupplierToExcel.action");
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
  <div dojoType="dijit.layout.LayoutContainer" layoutChildPriority="top-bottom" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
        
        <u:summaryTableButton id="refreshButton" label="button.common.refresh" onclick="refreshIt" align="right" cssClass="refresh_button" summaryTableId="supplierTable"/>
        <u:summaryTableButton id="labelButton" label="button.common.addLabels" onclick="showLabels" summaryTableId="supplierTable" cssClass="addLabels_retailItems_button"/>
        <u:summaryTableButton id="downloadListing"
               		label="button.common.downloadToExcel"
               		onclick="exportToExcel"
               		align="right"
               		cssClass="download_to_excel_button"
               		summaryTableId="supplierTable"/>
    </div>
   		
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="4" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="supplierTable" bodyUrl="supplier_table_body.action" folderName="%{folderName}" previewUrl="supplier_preview.action" detailUrl="supplier_detail.action"
                          previewPaneId="preview" parentSplitContainerId="split" multiSelect="true"
                          populateCriteriaDataOn="/supplier/populateCriteria">
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
                	labelColumn="%{labelColumn}" hidden="%{hidden}"/>
              </s:else>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <s:hidden name="labelType" value="%{@tavant.twms.domain.common.Label@SUPPLIER}" />
  <jsp:include flush="true" page="../../common/labelsDialog.jsp"></jsp:include>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminViewSupplierReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="contractAdminViewSupplierReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>
