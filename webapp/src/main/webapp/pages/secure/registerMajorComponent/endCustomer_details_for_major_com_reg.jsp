<%@taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<s:hidden name="endCustomer" value="%{endCustomer.id}"/>
<s:hidden name="addressForEndCustomerTransfer.type" />
<s:hidden name="addressForEndCustomerTransfer.addressLine"/>
<s:hidden name="addressForEndCustomerTransfer.addressLine2"/>
<s:hidden name="addressForEndCustomerTransfer.addressLine3"/>
<s:hidden name="addressForEndCustomerTransfer.city"/>
<s:hidden name="addressForEndCustomerTransfer.state"/>
<s:hidden name="addressForEndCustomerTransfer.zipCode"/>
<s:hidden name="addressForEndCustomerTransfer.country"/>
<s:hidden name="addressForEndCustomerTransfer.phone"/>
<s:hidden name="addressForEndCustomerTransfer.email"/>
<s:hidden name="addressForEndCustomerTransfer.secondaryPhone"/>
<s:hidden name="addressForEndCustomerTransfer.contactPersonName" />

<div class="policy_section_div" style="width:100%">

   <div class="section_header" style="width:100%;margin-right:-10px;">        
      <s:text name="label.matchRead.ownerInformation"/>
   </div> 
  <table width="100%" cellpadding="0" cellspacing="0" id="comp-addr-end" class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td width="30%"><s:property value="endCustomer.companyName" /></td>
			<td class="non_editable labelStyle"  width="20%"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="addressForEndCustomerTransfer.contactPersonName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.addressType" /></td>
			<td><s:property value="addressForEndCustomerTransfer.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="addressForEndCustomerTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="addressForEndCustomerTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="addressForEndCustomerTransfer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="addressForEndCustomerTransfer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="addressForEndCustomerTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="addressForEndCustomerTransfer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="addressForEndCustomerTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="addressForEndCustomerTransfer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="addressForEndCustomerTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="addressForEndCustomerTransfer.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="endCustomer.corporateName" /></td>
		</tr>
	</tbody>
</table>
</div>

