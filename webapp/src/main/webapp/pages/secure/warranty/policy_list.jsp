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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<s:if test="availablePolicies.empty">
	<span style="color: red"> <s:text
			name="error.transferPlan.noPlan" />
	</span>

</s:if>
<s:else>

	<table width="100%" cellpadding="0" cellspacing="0"
		id="policies_%{inventoryItem.id}" class="grid borderForTable">
		<thead>
			<tr>
				<th class="warColHeader" align="left">&nbsp;</th>
				<th class="warColHeader" align="left"><s:text
						name="label.planName" /></th>
				<th class="warColHeader" align="left"><s:text
						name="label.policyType" /></th>
				<th class="warColHeader" align="left"><s:text
						name="label.startDate" /></th>
				<th class="warColHeader" align="left"><s:text
						name="label.endDate" /></th>
				<th class="warColHeader" align="left"><s:text
						name="label.monthsCovered" /></th>
				<th class="warColHeader" align="left"><s:text
						name="label.common.hoursCovered" /></th>

			</tr>
		</thead>
		<tbody id="policy_list">
			<s:iterator value="availablePolicies" status="availablePolicies">
				<tr>
					<td><s:if test="%{extendedPolicies.contains(top)}">
							<s:checkbox
								id="%{qualifyId(\"selectedPolicy\")}_%{#availablePolicies.index}"
								name="inventoryItemMappings[%{#nListIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
								cssClass="policy_checkboxes" fieldValue="%{policyDefinition.id}"
								value="true" disabled="true" />
							<s:hidden
								name="inventoryItemMappings[%{#nListIndex}].extendedPolicies[%{#availablePolicies.index}].policyDefinition"
								value="%{policyDefinition.id}" />
							<s:hidden
								name="inventoryItemMappings[%{#nListIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
								value="%{policyDefinition.id}" />
						</s:if> <s:else>
							<s:checkbox
								id="%{qualifyId(\"selectedPolicy\")}_%{#availablePolicies.index}"
								name="inventoryItemMappings[%{#nListIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
								cssClass="policy_checkboxes" fieldValue="%{policyDefinition.id}"
								value="true" disabled="true" cssStyle="padding:0px;margin:0px">
							</s:checkbox>
							<s:hidden
								name="inventoryItemMappings[%{#nListIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
								value="%{policyDefinition.id}" />

						</s:else> <s:hidden
							name="inventoryItemMappings[%{#nListIndex}].availablePolicies[%{#availablePolicies.index}].policyDefinition"
							value="%{policyDefinition.id}" /></td>
					<td class="warColBodyDiv" align="left"
						cssStyle="white-space: nowrap;"><a
						style="text-decoration: none; cursor: default; color: #000000;"
						title='<s:property value="code" />'><s:property
								value="policyDefinition.code" /></td>
					<td class="warColBodyDiv" align="left"><s:property
							value="%{getText(warrantyType.displayValue)}" /></td>
					<td class="warColBodyDiv" align="left"><s:property
							value="%{getWarrantyStartDate(inventoryItem, policyDefinition)}" /></td>
					<td class="warColBodyDiv" align="left"><s:property
							value="%{getWarrantyEndDate(inventoryItem, policyDefinition)}" /></td>
					<td class="warColBodyDiv" align="left"><s:property
							value="%{getMonthsCovered(inventoryItem, policyDefinition)}" /></td>
					<td class="warColBodyDiv" align="left"><s:property
							value="policyDefinition.coverageTerms.serviceHoursCovered" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:else>


