<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%--
  @author vikas.sasidharan
--%>

<html>
<head>
    <title><s:text name="title.manageBusinessRule"/></title>
    <s:head theme="twms" />

    <script type="text/javascript" src="scripts/domainUtility.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">

        dojo.require("dojox.layout.ContentPane");
        
        var extraParams = {
            context : "ClaimRules"
        };
        
       
        function refreshIt() {
            publishEvent(SUMMARY_TABLE_UTIL.getRefreshFullTopic(
                    "domainRuleTable"));
        }
       
        function createDomainRule() {
            parent.publishEvent("/tab/open", {
                label: i18N.new_domainrule,
                decendentOf: getMyTabLabel(),
                url: "create_domain_rule.action?context=" + extraParams.context,
                forceNewTab: true
            });
        }
		
		function exportToExcel(){
         	exportExcel("/domainRules/populateCriteria","exportRulesToExcel.action");
        }
        
    </script>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="SummaryTable.css"/>
    <%@include file="/i18N_javascript_vars.jsp"%>
  </head>
  <u:body>
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="buttonContainer" id="buttonsDiv">
      
        <u:summaryTableButton id="refreshButton" label="button.common.refresh"
                    onclick="refreshIt" align="right" cssClass="refresh_button"
                    summaryTableId="domainRuleTable"/>
        <u:summaryTableButton id="createDomainRuleButton"
                    label="button.manageBusinessRule.createBusinessRule"
                    onclick="createDomainRule"
                    summaryTableId="domainRuleTable"
                    cssClass="create_domainrule_button"/>
        <u:summaryTableButton id="downloadListing"
        			label="button.common.downloadToExcel" 
        			onclick="exportToExcel"
               		align="right" 
               		cssClass="download_to_excel_button" 
               		summaryTableId="domainRuleTable"/>            
    </div>
    <div dojoType="dijit.layout.SplitContainer" layoutAlign="client"
         orientation="vertical" sizerWidth="4" activeSizing="false" id="split"
         persist="false">
        <%-- We don't need folder name info. Hence just setting some junk value
        here --%>
        <u:stylePicker fileName="SummaryTableButton.css" />
        <u:summaryTable eventHandlerClass="tavant.twms.summaryTable.BasicTwmsEventHandler" id="domainRuleTable"
                          bodyUrl="get_domain_rules_body.action"
                          extraParamsVar="extraParams"
                          folderName="BUSINESS_RULES"
                          previewUrl="manage_domain_rule_preview.action"
                          detailUrl="manage_domain_rule_detail.action"
                          previewPaneId="preview"
                          populateCriteriaDataOn="/domainRules/populateCriteria"
                          parentSplitContainerId="split">
            <s:iterator value="tableHeadData">
                <u:summaryTableColumn id="%{id}" label="%{title}" width="%{widthPercent}"
                            idColumn="%{idColumn}" labelColumn="%{labelColumn}" disableFiltering="%{disableFiltering}" disableSorting="%{disableSorting}"
                            hidden="%{hidden}"/>
            </s:iterator>
        <script type="text/javascript" src="scripts/SummaryTableTagEventHandler.js"></script></u:summaryTable>
        <div dojoType="dojox.layout.ContentPane" id="preview">
        </div>
    </div>
  </div>
  <jsp:include flush="true" page="../../common/ExcelDowloadDialog.jsp"></jsp:include>
  </u:body>
<authz:ifPermitted resource="warrantyAdminClaimsProcessingReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>