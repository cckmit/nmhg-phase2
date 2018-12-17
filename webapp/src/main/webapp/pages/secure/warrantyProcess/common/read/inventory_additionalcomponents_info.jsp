<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="common.css" />

<table cellspacing="0" cellpadding="0"
	id="additional_components_details_preview_table"
	class="grid borderForTable" style="width: 96%;">
	<thead>
		<tr class="row_head">
			<th width="10%"><s:text name="label.additionalComponent.type" /></th>
			<th width="10%"><s:text name="label.additionalComponent.subType" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.serialNumber" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.partNumber" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.partDescription" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.DateCode" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.manufacturer" /></th>
			<th width="10%"><s:text name="label.additionalComponent.model" /></th>
		</tr>
	</thead>
	<tbody>
		<s:set name="additionalComponents"
			value="inventoryItemMappings[0].inventoryItem.additionalComponents" />
		<s:if
			test="#additionalComponents!=null && #additionalComponents.size()>0">
			<s:iterator value="#additionalComponents" status="index">
				<tr>
					<td><s:property value="type" /></td>
					<td><s:property value="subType" /></td>
					<td><s:property value="%{serialNumber}" /></td>
					<td><s:property value="partNumber" /></td>
					<td><s:property value="partDescription" /></td>
					<td><s:property value="dateCode" /></td>
					<td><s:property value="manufacturer" /></td>
					<td><s:property value="model" /></td>

				</tr>
			</s:iterator>
		</s:if>
	</tbody>
</table>