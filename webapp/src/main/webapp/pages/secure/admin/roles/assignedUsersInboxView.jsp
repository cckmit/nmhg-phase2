<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<title><s:text name="title.roles.manageRoles"/></title>
<s:head theme="twms" />
<u:stylePicker fileName="SummaryTable.css" />

<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

 <script>
 dojo.require("dojox.layout.ContentPane");
	    
</script>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<%@include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%">
	

	<div dojoType="dijit.layout.LayoutContainer" layoutAlign="client"
		orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
		persist="false"><u:stylePicker fileName="SummaryTableButton.css" />
	<u:summaryTable
		eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler"
		id="rolesTable" bodyUrl="users_to_roleBody.action"
		folderName="%{folderName}"  previewPaneId="preview" previewUrl="" 
		detailUrl="users_to_roleDetail.action" 
		 parentSplitContainerId="split"
		populateCriteriaDataOn="/notification/populateCriteria">
		<s:iterator value="tableHeadData">          
            	<u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}" idColumn="%{idColumn}" labelColumn="%{labelColumn}" hidden="%{hidden}"/>          
       	</s:iterator>
		<script type="text/javascript"
			src="scripts/SummaryTableTagEventHandler.js"></script>
	</u:summaryTable>
	
	
	<div dojoType="dojox.layout.ContentPane" id="preview"></div>
	</div>
	
	
	</div>
	</div>
	<jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
</u:body>
</html>
