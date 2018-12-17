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


<html class="Rule">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>Add New Part</title>

    <script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

    <%-- FIXME: something like <s:property value="#session['session.cssTheme']"/>, someone plz set session.cssTheme it in session and change this --%>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>

</head>

<body style="overflow-X: auto; overflow-Y: auto; width:100%;">
<s:actionerror theme="xhtml" />
<s:fielderror theme="xhtml" />
<s:actionmessage theme="xhtml" />

<div dojoType="dijit.layout.LayoutContainer"><s:form
	action="partShipperUpdateTag_addNewPartsToShipment">
	<s:hidden name="id" value="%{shipment.id}" />
	<s:hidden name="shipment" value="%{shipment.id}" />

	<div id="separatorTop"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="bgColor">
		<tr>
			<td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text
				name="section.label.addParttToTag" /></td>
		</tr>
		<tr>
			<td width="13%" nowrap="nowrap" class="labelBold"><s:text
				name="label.shipmentNo" />:</td>
			<td width="30%"><s:property value="%{shipment.id}" /></td>
			<td width="14%" class="labelBold"><s:text
				name="label.returnLocation" />:</td>
			<td><s:property value="%{shipment.destination.code}" /></td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><jsp:include flush="true" page="partsgroupedbyclaim.jsp" />
			</td>
		</tr>
		<tr>
			<td>
			<s:hidden name="transitionTaken" value="%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}"/>
			<div class="buttonWrapperPrimary">
			<s:if test="%{taskInstances.size() != 0}"><s:submit type="button"
				cssClass="buttonGeneric"
				label="%{getText('button.label.add')}"
				value="%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}"/></s:if><input
				type="button" name="Submit223"
				value='<s:text name="label.cancel" />' class="buttonGeneric"
				onclick="window.close()" /></div>
			</td>
		</tr>
	</table>
</s:form></div>
</body>
</html>
