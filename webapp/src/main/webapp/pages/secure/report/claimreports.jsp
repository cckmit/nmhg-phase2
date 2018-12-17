<%--

   Copyright (c)2006 Tavant Technologies All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"> 
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<%@include file="/i18N_javascript_vars.jsp"%>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="reportDesign.css"/>

<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript"src="scripts/jscalendar/calendar-setup.js"></script>
<script type="text/javascript" src="scripts/report.js"></script>
<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.Dialog");
    var searchDealersDialog,
        searchDealerGroupsDialog,
        searchResultsPaneDealer,
        searchResultsPaneDealerGroups,
        dealersTable,
        dealerGroupsTable,
        dealerName,
        dealerGroupName,
        dealersTableDiv1,
        dealerGroupsTableDiv1,
        dealersTableDiv2,
        dealerGroupsTableDiv2,
        deleteAllDealers,
        deleteAllDealerGroups,
        dealersSelectedHiddenInput,
        dealerGroupsSelectedHiddenInput,
        existingDealersHiddenInput,
        existingDealerGroupsHiddenInput,
        dealerSearchWizard,
        dealerGroupSearchWizard;
    
	dojo.addOnLoad(function() {
		searchDealersDialog = dijit.byId("searchDealersDialog");
		searchDealerGroupsDialog = dijit.byId("searchDealerGroupsDialog");
		searchResultsPaneDealer = dijit.byId("searchResultsDealer");
		searchResultsPaneDealerGroups = dijit.byId("searchResultDealerGroups");
		dealersTable = dojo.byId("dealersTable");
		dealerGroupsTable = dojo.byId("dealerGroupsTable");
		dealerName = dojo.byId("dealerName");
		dealerGroupName = dojo.byId("dealerGroupName");
		dealersTableDiv1 = dojo.byId("dealersTableDiv1");
		dealerGroupsTableDiv1 = dojo.byId("dealerGroupsTableDiv1");
		dealersTableDiv2 = dojo.byId("dealersTableDiv2");
		dealerGroupsTableDiv2 = dojo.byId("dealerGroupsTableDiv2");
		deleteAllDealers=dojo.byId("deleteAllDealers");
		deleteAllDealerGroups=dojo.byId("deleteAllDealerGroups");
		dealersSelectedHiddenInput = dojo.byId("reportSearchCriteria.dealers");
		dealerGroupsSelectedHiddenInput = dojo.byId("reportSearchCriteria.dealerGroups");
		existingDealersHiddenInput=dojo.byId("existingDealers");
		existingDealerGroupsHiddenInput=dojo.byId("existingDealerGroups");
		dojo.byId("dialogBoxContainer").style.display = "block";
		dealerSearchWizard= new tavant.twms.DealerSearchWizard(searchResultsPaneDealer,
                "dealers.action",
                "dealerName",
                "",
                "");
		dealerGroupSearchWizard = new tavant.twms.DealerSearchWizard(searchResultsPaneDealerGroups,
                "dealerGroups.action",
                "dealerGroupName",
                "",
                "");
        <s:if test="existingDealers!=null && existingDealers!=\"\"">
		selectedDealers=[<s:property value="existingDealers"/>];
		</s:if>
		<s:else>
		selectedDealers=[];
		</s:else>
        <s:if test="existingDealerGroups!=null && existingDealerGroups!=\"\"">
		selectedDealerGroups=[<s:property value="existingDealerGroups"/>];
		</s:if>
		<s:else>
		selectedDealerGroups=[];
		</s:else>
		dealerRenderer = new tavant.twms.DealerRenderer(dealersTable,selectedDealers,dealersSelectedHiddenInput,
							existingDealersHiddenInput,dealersTableDiv1,dealersTableDiv2);
		dealerGroupRenderer = new tavant.twms.DealerRenderer(dealerGroupsTable,selectedDealerGroups,dealerGroupsSelectedHiddenInput,
							existingDealerGroupsHiddenInput,dealerGroupsTableDiv1,dealerGroupsTableDiv2);
		
	})
	
</script>
</head>
<u:body>
<s:form theme="twms" validate="true">
<div id="actionResults">
	<u:actionResults/>
</div>
<div class="report_section_div">
<table class="report_data_table">
	<tr>
		<td width="38%" class="label1"><s:text name="label.report.chooseReport"/></td>
		<td><s:select name="reportType"	list="claimReports" required="true"/></td>
		<td><s:submit cssClass="buttonGeneric" action="claimReports" value="%{getText('button.common.go')}" onclick="deleteData()"/></td>
	</tr>
</table>
<jsp:include flush="true" page="claimTypeByDealer.jsp"/>
<jsp:include flush="true" page="claimStatusByDealer.jsp"/>
<jsp:include flush="true" page="processingEngineEfficiency.jsp"/>
<jsp:include flush="true" page="claimsByFault.jsp"/>
<jsp:include flush="true" page="warrantyPayout.jsp"/>
<jsp:include flush="true" page="claimsByProduct.jsp"/>
<%@include file="dealerSearchWizard.jsp"%>
<%@include file="dealerGroupSearchWizard.jsp"%>
</div>
<t:hidden id="reportSearchCriteria.dealers" name="reportSearchCriteria.dealers" />
<t:hidden id="reportSearchCriteria.dealerGroups" name="reportSearchCriteria.dealerGroups" />
<t:hidden id="existingDealers" name="existingDealers"/>
<t:hidden id="existingDealerGroups" name="existingDealerGroups"/>
</s:form>
</u:body>
</html>