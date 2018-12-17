<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: Apr 16, 2007
  Time: 7:27:44 PM
--%>

<html>
<head>
    <title><s:text name="title.manageGroup"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="SummaryTable.css"/>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "userSchemeTable"));
        }
       
        function createUserScheme(evt) {

            parent.publishEvent("/tab/open", {
                label: i18N.new_userScheme,
                decendentOf: getMyTabLabel(),
                url: "create_user_scheme.action",
                forceNewTab: true
            });
        }

        function showGroups(evt, dataId) {

            parent.publishEvent("/tab/open", {
                label: i18N.user_groupScheme,
                decendentOf: getMyTabLabel(),
                url: "list_user_groups.action?schemeId=" + dataId,
                forceNewTab: true
            });
        }

		function exportToExcel(){
         exportExcel("/userScheme/populateCriteria","export_user_scheme_to_excel.action");
        }

    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" style="height:30px;" id="buttonsDiv">
      
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="userSchemeTable"/>
        <u:summaryTableButton id="createUserSchemeButton"
                    label="button.manageGroup.createNewScheme"
                    onclick="createUserScheme"
                    summaryTableId="userSchemeTable"
                    cssClass="create_userScheme_button"/>
        <u:summaryTableButton id="showGroupsButton"
                    label="button.manageGroup.manageGroups"
                    onclick="showGroups"
                    summaryTableId="userSchemeTable"
                    cssClass="manage_groups_button"/>
		<u:summaryTableButton id="downloadListing"
               		label="button.common.downloadToExcel"
               		onclick="exportToExcel"
               		align="right"
               		cssClass="download_to_excel_button"
               		summaryTableId="userSchemeTable"/>
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" /> <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="userSchemeTable"
                          bodyUrl="get_user_schemes_body.action"
                          folderName="USER_GROUPS"
                          previewUrl="show_user_groups.action?preview=true"
                          detailUrl="show_user_groups.action"
                          previewPaneId="preview"
                          parentSplitContainerId="split"
                          populateCriteriaDataOn="/userScheme/populateCriteria">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}"
                            hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminUserGroupsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="contractAdminUserGroupsReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>