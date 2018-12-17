<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set var="inventoryItem" value="%{warranty.forItem}" />
<div style="background-color:#F3FBFE; border:1px solid #EFEBF7; margin-top:10px;">
	<table class="grid">
		<tr>
			<td colspan="2" class="labelStyle" >
				<s:set value="%{warranty.dieselTierWaiver.disclaimer.split('\n')}" name="disclaimer"/>
				<s:iterator value="#disclaimer">
					<s:property value="%{[0].top}"/><br/>
				</s:iterator>
			</td>
		</tr>
		<tr>
			<td class="labelStyle" width="20%" style=" border:1px solid #EFEBF7;"><s:text name="label.waiver.agentName" />:</td>
			<s:if test="warranty.dieselTierWaiver.approvedByAgentName != null">
				<td style=" border:1px solid #EFEBF7;"><s:text name="warranty.dieselTierWaiver.approvedByAgentName" /></td>
			</s:if>
		</tr>
		<tr>
			<td class="labelStyle" style=" border:1px solid #EFEBF7;"><s:text name="label.waiver.agentTitle" />:</td>
			<s:if test="warranty.dieselTierWaiver.agentTitle != null">
				<td style=" border:1px solid #EFEBF7;"><s:text name="warranty.dieselTierWaiver.agentTitle" /></td>
			</s:if>
		</tr>
		<tr>
			<td class="labelStyle" style=" border:1px solid #EFEBF7;"><s:text name="label.waiver.agentTelephone" />:</td>
			<s:if test="warranty.dieselTierWaiver.agentTelephone != null">
				<td style=" border:1px solid #EFEBF7;"><s:text name="warranty.dieselTierWaiver.agentTelephone" /></td>
			</s:if>
		</tr>
		<tr>
			<td class="labelStyle" style=" border:1px solid #EFEBF7;"><s:text name="label.waiver.agentEmail" />:</td>
			<s:if test="warranty.dieselTierWaiver.agentEmailAddress != null">
				<td style=" border:1px solid #EFEBF7;"><s:text name="warranty.dieselTierWaiver.agentEmailAddress" /></td>
			</s:if>
		</tr>
	</table>
</div>