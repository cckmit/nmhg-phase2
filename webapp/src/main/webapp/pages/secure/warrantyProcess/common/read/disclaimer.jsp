<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set var="inventoryItem" value="%{warranty.forItem}" />
<div class="admin_section_div">
	<table>
		<tr>
			<td colspan="2" class="labelStyle">
				<s:set value="%{warranty.dieselTierWaiver.disclaimer.split('\n')}" name="disclaimer"/>
				<s:iterator value="#disclaimer">
					<s:property value="%{[0].top}"/><br/>
				</s:iterator>
			</td>
		</tr>
		<tr>
			<td class="labelStyle" width="20%"><s:text name="label.waiver.agentName" /></td>
			<td><s:text name="warranty.dieselTierWaiver.approvedByAgentName" /></td>
		</tr>
		<tr>
			<td class="labelStyle"><s:text name="label.waiver.agentTitle" /></td>
			<td >
				<s:if test="warranty.dieselTierWaiver.agentTitle != null">
					<s:text name="warranty.dieselTierWaiver.agentTitle" />
				</s:if>
			</td>
		</tr>
		<tr>
			<td class="labelStyle"><s:text name="label.waiver.agentTelephone" /></td>
			<td >
				<s:if test="iwarranty.dieselTierWaiver.agentTelephone != null">
					<s:text name="warranty.dieselTierWaiver.agentTelephone" />
				</s:if>
			</td>
		</tr>
		<tr>
			<td class="labelStyle"><s:text name="label.waiver.agentEmail" /></td>
			<td >
				<s:if test="warranty.dieselTierWaiver.agentEmailAddress != null">
					<s:text name="warranty.dieselTierWaiver.agentEmailAddress" />
				</s:if>
			</td>
		</tr>
	</table>
</div>
