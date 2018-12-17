<%@taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<s:hidden name="endCustomer" value="%{endCustomer.id}"/>
<s:hidden name="addressForTransferToendCustomer.type" />
<s:hidden name="addressForTransferToendCustomer.addressLine"/>
<s:hidden name="addressForTransferToendCustomer.addressLine2"/>
<s:hidden name="addressForTransferToendCustomer.addressLine3"/>
<s:hidden name="addressForTransferToendCustomer.city"/>
<s:hidden name="addressForTransferToendCustomer.state"/>
<s:hidden name="addressForTransferToendCustomer.zipCode"/>
<s:hidden name="addressForTransferToendCustomer.country"/>
<s:hidden name="addressForTransferToendCustomer.phone"/>
<s:hidden name="addressForTransferToendCustomer.email"/>
<s:hidden name="addressForTransferToendCustomer.secondaryPhone"/>
<s:hidden name="addressForTransferToendCustomer.contactPersonName" />

<div class="policy_section_div" style="width:100%" id="endCustomerInfo">

   <div class="section_header" style="width:100%;margin-right:-10px;">        
      <s:text name="label.matchRead.ownerInformation"/>
   </div> 
  <table width="100%" cellpadding="0" cellspacing="0" id="comp-addr-end" class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td width="30%"><s:property value="endCustomer.companyName" /></td>
			<td class="non_editable labelStyle"  width="20%"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="addressForTransferToendCustomer.contactPersonName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.addressType" /></td>
			<td><s:property value="addressForTransferToendCustomer.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="addressForTransferToendCustomer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="addressForTransferToendCustomer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="addressForTransferToendCustomer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="addressForTransferToendCustomer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="addressForTransferToendCustomer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="addressForTransferToendCustomer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="addressForTransferToendCustomer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="addressForTransferToendCustomer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="addressForTransferToendCustomer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="addressForTransferToendCustomer.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="endCustomer.corporateName" /></td>
		</tr>
	</tbody>
</table>
</div>

