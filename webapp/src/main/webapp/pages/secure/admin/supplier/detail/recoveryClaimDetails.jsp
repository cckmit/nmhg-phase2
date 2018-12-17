<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<table cellspacing="0" border="0" cellpadding="0" class="grid"
	width="100%">
	<tr>
		<td class="labelStyle" width="16%" nowrap="nowrap"><s:text
				name="columnTitle.common.recClaimNo" />:</td>
		<td class="labelNormal" width="35%"><u:openTab id="%{id}"
				tabLabel="Recovery Claim Number %{recoveryClaimNumber}"
				url="recovery_claim_search_detail.action?id=%{id}">
				<s:if test="recoveryClaim.documentNumber !=null && recoveryClaim.documentNumber.length() > 0" >
			      <s:property value="recoveryClaim.recoveryClaimNumber" />-<s:property
				value="recoveryClaim.documentNumber" />
		       </s:if>
		       <s:else>
		        <s:property value="recoveryClaim.recoveryClaimNumber" />
		      </s:else>		
			</u:openTab></td>
		<td class="labelStyle" width="16%" nowrap="nowrap"><s:text
				name="columnTitle.common.machineSerialNo" />:</td>
		<td><u:openTab
				tabLabel="Serial Number claim.serialNumber"
				url="inventoryDetail.action?id=%{claim.itemReference.referredInventoryItem.id}"
				id="serialNumber" cssClass="link">
				<s:property
					value="claim.itemReference.referredInventoryItem.serialNumber" />
			</u:openTab></td>
	</tr>
	<tr>
		<td class="labelStyle" width="16%" nowrap="nowrap"><s:text
				name="label.partReturnConfiguration.modelnumber" />:</td>
		<td><s:if test="claim.itemReference.isSerialized()">
				<s:property value="claim.itemReference.unserializedItem.model.name" />
			</s:if> <s:else>
				<s:property value="claim.itemReference.model.name" />
			</s:else></td>
	</tr>
</table>