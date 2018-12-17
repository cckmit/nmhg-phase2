<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<s:hidden name="inventoryItemMappings[%{#nListIndex}].waiverInformationEditable"/>
<table>
	<s:if test="inventoryItemMappings[#nListIndex].waiverInformationEditable">
		<tr>
			<td width="26%"><label for="agentName" id="agentName"
				class="labelStyle"> <s:text name="label.waiver.agentName" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:textfield
				id="agentNameEditable"
				name="inventoryItemMappings[%{#nListIndex}].dieselTierWaiver.approvedByAgentName"
				cssStyle="width:145px;" /></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text name="label.waiver.agentTitle" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:textfield
				id="agentTitleEditable" name="inventoryItemMappings[%{#nListIndex}].dieselTierWaiver.agentTitle"
				cssStyle="width:145px;" /></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text
				name="label.waiver.agentTelephone" /> </label></td>
			<td width="35%" style="padding-left: 3px;"><s:textfield
				id="agentTelephoneEditable"
				name="inventoryItemMappings[%{#nListIndex}].dieselTierWaiver.agentTelephone"
				cssStyle="width:145px;" /></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text name="label.waiver.agentEmail" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:textfield
				id="agentEmailAddressEditable"
				name="inventoryItemMappings[%{#nListIndex}].dieselTierWaiver.agentEmailAddress"
				cssStyle="width:145px;" /></td>
		</tr>
	</s:if>

	<s:else>
		<tr>
			<td width="26%"><label for="agentName" id="agentName"
				class="labelStyle"> <s:text name="label.waiver.agentName" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:label
				id="agentNameNonEditable" cssStyle="width:145px;"
				value="%{inventoryItem.waiverDuringDr.approvedByAgentName}" /></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text name="label.waiver.agentTitle" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:if
				test="inventoryItem.waiverDuringDr.agentTitle != null">
				<s:label id="agentTitleNonEditable" cssStyle="width:145px;"
					value="%{inventoryItem.waiverDuringDr.agentTitle}" />
			</s:if></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text
				name="label.waiver.agentTelephone" /> </label></td>
			<td width="35%" style="padding-left: 3px;"><s:if
				test="inventoryItem.waiverDuringDr.agentTelephone != null">
				<s:label id="agentTelephoneNonEditable" cssStyle="width:145px;"
					value="%{inventoryItem.waiverDuringDr.agentTelephone}" />
			</s:if></td>
		</tr>
		<tr>
			<td width="26%"><label for="agentTitle" id="agentTitle"
				class="labelStyle"> <s:text name="label.waiver.agentEmail" />
			</label></td>
			<td width="35%" style="padding-left: 3px;"><s:if
				test="inventoryItem.waiverDuringDr.agentEmailAddress != null">
				<s:label id="agentEmailAddressNonEditable" cssStyle="width:145px;"
					value="%{inventoryItem.waiverDuringDr.agentEmailAddress}" />
			</s:if></td>
		</tr>
	</s:else>

	<tr>
		<td width="26%"><label for="agentTitle" id="agentTitle"
			class="labelStyle"> <s:text name="label.waiver.disclaimer" />
		</label></td>
		<td width="70%" style="padding-left: 3px;">
			<s:set value="%{inventoryItemMappings[#nListIndex].dieselTierWaiver.disclaimer.split('\n')}" name="disclaimer"/>
			<s:iterator value="#disclaimer">
				<s:property value="%{[0].top}"/><br/>
			</s:iterator>
		</td>
	</tr>
</table>
