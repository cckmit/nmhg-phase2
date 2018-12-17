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
		searchResultsPaneDealer,
		dealersTable,
		supplierName,
		dealersTableDiv1,
		dealersTableDiv2,
		deleteAllDealers,
		dealersSelectedHiddenInput,
		existingDealersHiddenInput,
		dealerSearchWizard,
		selectedDealers,
		dealerRenderer;
		
	dojo.addOnLoad(function() {
		searchDealersDialog = dijit.byId("searchDealersDialog");
		searchResultsPaneDealer = dijit.byId("searchResultsDealer");
		dealersTable = dojo.byId("dealersTable");
		supplierName = dojo.byId("supplierName");
		dealersTableDiv1 = dojo.byId("dealersTableDiv1");
		dealersTableDiv2 = dojo.byId("dealersTableDiv2");
		deleteAllDealers=dojo.byId("deleteAllDealers");
		dealersSelectedHiddenInput = dojo.byId("reportSearchCriteria.suppliers");
		existingDealersHiddenInput=dojo.byId("existingSuppliers");
		dojo.byId("dialogBoxContainer").style.display = "block";
		dealerSearchWizard= new tavant.twms.DealerSearchWizard(searchResultsPaneDealer,
                "suppliers.action",
                "supplierName",
                "",
                "");
        <s:if test="existingSuppliers!=null && existingSuppliers!=\"\"">
		selectedDealers=[<s:property value="existingSuppliers"/>];
		</s:if>
		<s:else>
		selectedDealers=[];
		</s:else>
		dealerRenderer = new tavant.twms.DealerRenderer(dealersTable,selectedDealers,dealersSelectedHiddenInput,
							existingDealersHiddenInput,dealersTableDiv1,dealersTableDiv2);
	})
	
</script>
</head>
<u:body>

<s:form theme="twms" validate="true">
<u:actionResults/>
<div class="report_section_div">
<table class="report_data_table">
	<tr>
		<td width="38%" class="label1"><s:text name="label.report.chooseReport"/></td>
		<td><s:select label="label" name="reportType"	list="supplierRecoveryReports" required="true"/></td>
		<td><s:submit cssClass="buttonGeneric" action="supplierRecoveryReports" value="%{getText('button.common.go')}" onclick="javascript:deleteData()"/></td>
	</tr>
</table>
<jsp:include flush="true" page="supplierRecoveryByDealer.jsp"/>
<%@include file="supplierSearchWizard.jsp"%>
</div>
<t:hidden id="reportSearchCriteria.suppliers" name="reportSearchCriteria.suppliers" />
<t:hidden id="existingSuppliers" name="existingSuppliers"/>
</s:form>
</u:body>
</html>