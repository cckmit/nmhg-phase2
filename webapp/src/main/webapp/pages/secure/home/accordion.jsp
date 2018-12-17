<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

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
<%@taglib prefix="authz" uri="authz"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
</script>

<%-- Warranty Admin Accordion --%>
<authz:ifPermitted resource="warrantyAdminWarrantyAdminTab">
    <jsp:include page="accordionTabs/admin.jsp"/>
</authz:ifPermitted>

<%-- Claim Accordion --%>    
<authz:ifPermitted resource="claimsClaimTab">
    <%-- Claims Accordion Pane --%>
    <jsp:include page="accordionTabs/claim.jsp"/>
</authz:ifPermitted>

<%-- Inventory Accordion Pane --%>
<authz:ifPermitted resource="inventoryInventoryTab">
 	<jsp:include page="accordionTabs/inventory.jsp"/>
</authz:ifPermitted>

<%-- Campaign Inventory --%>    
<authz:ifPermitted resource="fPIFPITab">
	<%-- Campaigns Accordion Pane--%>
	  <jsp:include page="accordionTabs/campaignInventory.jsp"/>
</authz:ifPermitted>

<%-- Supplier Inventory --%>
<%--
    It may not make sense to show due part returns etc to a receiver, but since these data are coming
    from an action class, need to think through if we can prevent only some entries in the iterator
    using role based security.

    For now, I am writing a role based tag where you give a comma seperated set of roles
 --%>
<authz:ifPermitted resource="partReturnsPartReturnsTab">
<%-- Part Returns Accordion Pane --%>
	<s:if test="!isPartShipperLimitedViewOnly()">
   		<jsp:include page="accordionTabs/partReturn.jsp"/>
   	</s:if>
</authz:ifPermitted>

<authz:ifPermitted resource="partsRecoveryPartsRecoveryTab">
    <jsp:include page="accordionTabs/partRecovery.jsp"/>
</authz:ifPermitted>

<%-- Technically supplier recovery is different but in processdefinitino it is part of the workflow --%>
<authz:ifPermitted resource="processorRecoveryProcessorRecoveryTab">
    <jsp:include page="accordionTabs/supplierRecovery.jsp"/>
</authz:ifPermitted>


<authz:ifPermitted resource="contractAdminContractAdminTab">
	<jsp:include page="accordionTabs/contractAdmin.jsp"/>
</authz:ifPermitted>

<authz:ifUserInRole roles="supplier">
    <jsp:include page="accordionTabs/supplierPartReturn.jsp"/>
</authz:ifUserInRole>

<%-- Items Accordion Pane --%>
<authz:ifPermitted resource="itemsItemsTab">
<s:if test="isInternalUser()">	
	<jsp:include page="accordionTabs/item.jsp"/>
</s:if>
<s:else>
	<authz:ifUserInRole roles="dealerWarrantyAdmin,dealerSalesAdministration">
		<jsp:include page="accordionTabs/item.jsp"/>
	</authz:ifUserInRole>
</s:else>
</authz:ifPermitted>

<%--  Warranty Reports Accordion --%>
<authz:ifPermitted resource="reportsReportsTab">
<jsp:include page="accordionTabs/reports.jsp"/>
</authz:ifPermitted>

<authz:ifPermitted resource="dealerInformationDealerSummaryTab">
<jsp:include page="accordionTabs/dealerInformation.jsp"/>
</authz:ifPermitted>

<%-- Warranty Management Accordion Pane --%>
<%-- Added dealerWarrantyAdmin role to fix TSESA-499 --%>
<authz:ifPermitted resource="dRApproval/TransferDRApproval/TransferTab">
<jsp:include page="accordionTabs/warrantyProcess.jsp"/>
</authz:ifPermitted>

<%-- Settings Accordion Pane --%>
<authz:ifPermitted resource="settingsSettingsTab">
	<jsp:include page="accordionTabs/settings.jsp"/>
</authz:ifPermitted>


<authz:ifPermitted resource="accountsAccountsTab">
	<jsp:include page="accordionTabs/accounts.jsp"/>
</authz:ifPermitted>