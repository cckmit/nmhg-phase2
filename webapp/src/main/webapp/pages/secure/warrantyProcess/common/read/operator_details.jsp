<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 2:53:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="application/x-json"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:hidden name="operator" value="%{warranty.operator.id}"/>
<s:hidden name="operatorAddressForTransfer" value="%{warranty.operatorAddressForTransfer.id}"/>
<s:if test="warranty.operator.isIndividual()">
	<%-- INDIVIDUAL ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="indiv-addr"
		class="grid">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.name" /></td>
			<td><s:property value="warranty.operator.name" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td><s:property value="warranty.operator.companyName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" /></td>
			<td colspan="3"><s:property value="warranty.operatorAddressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" /></td>
			<td colspan="3"><s:property value="warranty.operatorAddressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" /></td>
			<td colspan="3"><s:property value="warranty.operatorAddressForTransfer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.city" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.state" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.country" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.phone" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.email" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.secondaryPhone" /></td>
		</tr>

	</tbody>
	</table>
</s:if>
<s:else>
	<%-- COMPANY ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="comp-addr"
		class="form " style="margin:0px;">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td><s:property value="warranty.operator.companyName" /></td>
		</tr>

		<tr>
			<td class="non_editable labelStyle"><s:text name="label.address" /></td>
			<td colspan="3"><s:property value="warranty.operatorAddressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.city" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.city" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.state" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.addressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.country" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.addressForTransfer.phone" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.email" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.operatorAddressForTransfer.secondaryPhone" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.corporateName" /></td>
			<td><s:property value="warranty.operator.corporateName" /></td>
		</tr>

	</tbody>
	</table>
</s:else>