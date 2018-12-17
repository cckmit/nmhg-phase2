<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<s:hidden name="shipment" value="%{shipment}" />
<table cellspacing="0" border="0" cellpadding="0" width="100%"
	class="grid">
	<tr>
		<td class="labelStyle" width="16%" nowrap="nowrap"><s:text
				name="columnTitle.shipmentGenerated.shipment_no" /> :</td>
		<td class="labelNormal" width="34%"><s:property value = "shipment.id" /></td>
		<td class="labelStyle" width="16%" nowrap="nowrap"><s:text
				name="label.partReturn.returnToLocation" /> :</td>
		<td class="labelNormal" width="34%"><s:property value="shipment.destination.code" /></td>
	</tr>
	<tr>
		<td class="labelStyle" width="18%" nowrap="nowrap"><s:text
				name="label.partReturnConfiguration.carrier" /> :</td>
		<td class="labelNormal" width="36%"><s:property
				value="shipment.carrier.name" /></td>
		<td class="labelStyle" nowrap="nowrap"><s:text
				name="label.common.shipmentDate" /> :</td>
		<td class="labelNormal"><s:property
				value="shipment.shipmentDateForDisplay" /></td>
	</tr>
	<tr>
		<td class="labelStyle" nowrap="nowrap"><s:text
				name="label.common.supplierName" /> :</td>
		<td class="labelNormal"><s:property value="recoveryClaim.contract.supplier.name" />
		</td>

		<td class="labelStyle" nowrap="nowrap"><s:text
				name="label.partReturnConfiguration.trackingNumber" /> :</td>
		<td class="labelNormal"><s:property value="shipment.trackingId" /></td>
	</tr>
</table>