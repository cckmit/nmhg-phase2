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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="label.warranty.DeliveryReport" /></title>
    <s:head theme="twms"/>
    <script type="text/javascript">
        <s:if test="!actionMessages.isEmpty()">
        dojo.addOnLoad(function() {
            manageTableRefresh("customerListTable");
        });
        </s:if>
    </script>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
</head>

<body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults/>
	<div id="warranty_machine_info" class="section_div">
	<div id="warranty_machine_info_title" class="section_heading"><s:text
		name="label.machineInfo" /></div>
	<table width="98%" class="form" cellspacing="0" cellpadding="0">
		<tr>
			<td class="non_editable"><s:text
				name="label.productNameAndModel" /></td>
			<td><s:property value="%{warranty.forItem.ofType.productType.name}" /> - <s:property
				value="%{warranty.forItem.ofType.model}" /></td>

			<td class="non_editable"><s:text name="label.serialNumber" /></td>
			<td><u:openTab decendentOf="history" id="equipment_%{id}" tabLabel="Serial Number %{warranty.forItem.serialNumber}"
					url="inventoryDetail.action?id=%{warranty.forItem.id}"><u style="cursor: pointer;"><s:property
					value="%{warranty.forItem.serialNumber}" /></u></u:openTab></td>
		</tr>

		<tr>
			<td class="non_editable"><s:text name="label.deliveryDate" />:</td>
			<td><s:property value="%{warranty.forItem.deliveryDate}" /></td>
			<td class="non_editable"><s:text name="label.hoursOnMachine" /></td>
			<td><s:property value="%{warranty.forItem.hoursOnMachine}" /></td>
		</tr>
		<tr id="itemDetails">
			<td class="non_editable"><s:text name="label.shipmentDate" />:</td>
			<td><span id="shipmentDate"><s:property
				value="warranty.forItem.shipmentDate" /></span></td>
			<td class="non_editable"><s:text name="label.itemCondition" /></td>
			<td><span id="itemCondition"><s:property
				value="warranty.forItem.conditionType.itemCondition" /></span></td>
		</tr>
	</table>
	</div>
	
	<s:if test="warranty.policies.size() != 0">
		<div id="policy_list" class="section_div">
		<div id="warranty_machine_info_title" class="section_heading"><s:text
		name="label.warrantyInformation" /></div>
		<table width="98%" cellpadding="0" cellspacing="0" id="policies"
			class="grid borderForTable">
			<thead>
				<tr>
			   		<th class="warColHeader" align="left"><s:text name="label.planName"/></th>
			   		<th class="warColHeader" align="left"><s:text name="label.monthsCovered"/></th>
			   		<th class="warColHeader" align="left"><s:text name="label.startDate"/></th>
			   		<th class="warColHeader" align="left"><s:text name="label.endDate"/></th>
			   		<th class="warColHeader" align="left"><s:text name="label.policyType"/></th>
			   		<th class="warColHeader" align="left"><s:text name="label.policyFee"/></th>
				</tr>
			</thead>
			
			<tbody id="policy_list">	
				<s:iterator value="warranty.policies">
					<tr>
						<td><s:property value="code"/></td>
						<td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromDelivery"/></td>
						<td><s:property value="warrantyPeriod.fromDate"/></td>
						<td><s:property value="warrantyPeriod.tillDate"/></td>
						<td><s:property value="%{getText(warrantyType.displayValue)}"/></td>
						<td><s:property value="price"/></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		</div>
	</s:if>
	
	<s:if test="%{preview}">
		<div id="submit" align="center"><input class="buttonGeneric"
			type="button" value='<s:text name="label.cancel"/>'
			onclick="closeCurrentTab()" /></div>
	</s:if>
	</div>
	</div>
</body>
</html>
