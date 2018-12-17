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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>

    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
     <u:stylePicker fileName="common.css"/>
	<SCRIPT type="text/javascript">
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.ContentPane");
	</SCRIPT>
</head>

<u:body>
	<u:actionResults/>

	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"><s:form
		action="partShipperGenerateShipment_submit" validate="true"
		theme="twms" id="baseFormId">
		<s:hidden name="id" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><jsp:include flush="true" page="partsgroupedbyclaim.jsp" />
				</td>
			</tr>
			<tr>
				<td>
				<div class="buttonWrapperPrimary"><s:submit type="button"
					name="transitionTaken" cssClass="buttonGeneric"
					label="%{getText('button.label.generateShipment')}"
					value="%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}"
					onclick="this.value='%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}'" /></div>
				</td>
			</tr>
		</table>
	</s:form>

	</div>
	</div>

	<div id="showaddresshere"></div>
</u:body>
<authz:ifPermitted resource="partsRecoveryAwaitingShipmenttoSupplierReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
