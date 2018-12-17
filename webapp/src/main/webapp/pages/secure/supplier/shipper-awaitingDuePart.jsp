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
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
    <s:head theme="twms"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer" layoutChildPriority='top-bottom'
		style="width: 100%; height: 100%;background:#fff; margin: 0; padding: 0; overflow-X: hidden;overflow-y: auto;">
	<u:actionResults/>
	<s:form	action="partShipperGenerateDuePart_submit" theme="twms" validate="true" id="baseFormId">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	
		<div id="separator"></div>
		<div class="policy_section_div">
		<jsp:include flush="true" page="../partreturns/tables/claim_details.jsp" /></div>
		
		<div class="policy_section_div">
		<jsp:include flush="true" page="partsGroupedByClaimForDuePartAwaiting.jsp" /></div>
		
	</s:form>
<authz:ifPermitted resource="partsRecoveryAwaitingShipmentToWarehouseReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
	</div>
</u:body>
</html>