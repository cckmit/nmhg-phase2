<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author rakesh.r
--%>

<html>
<head>
    <title>Inventory Inbox View</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript" src="scripts/googleMaps.js"></script>
    <script type="text/javascript" src="scripts/googleMapsAPI.js"></script>

    <script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        
       	var extraParams = {
       		labelType :'<s:property value="labelType" />'
		};
        
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body >
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%" id="inventoryInboxViewRootLayout">
	<div dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="7" activeSizing="false" id="split" persist="false">
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="inventoryTable"
                  bodyUrl="labelsBody.action" folderName="%{folderName}"
                  previewUrl="labelsPreview.action" detailUrl="labelsDetail.action" extraParamsVar="extraParams"
                  rootLayoutContainerId="inventoryInboxViewRootLayout" buttonContainerId="inventoryInboxViewButtonPane"
                  previewPaneId="preview" parentSplitContainerId="split" enableTableMinimize="true"
                  populateCriteriaDataOn="/inventory/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>
            </s:iterator>
            <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script>
        </u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
     </div>
   </div>
  </u:body>
</html>

