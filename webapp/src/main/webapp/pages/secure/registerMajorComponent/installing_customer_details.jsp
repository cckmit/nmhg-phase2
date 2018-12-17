<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<s:hidden name="customer" value="%{customer.id}"/>
<s:hidden name="addressForTransfer.type" />
<s:hidden name="addressForTransfer.addressLine"/>
<s:hidden name="addressForTransfer.addressLine2"/>
<s:hidden name="addressForTransfer.addressLine3"/>
<s:hidden name="addressForTransfer.city"/>
<s:hidden name="addressForTransfer.state"/>
<s:hidden name="addressForTransfer.zipCode"/>
<s:hidden name="addressForTransfer.country"/>
<s:hidden name="addressForTransfer.phone"/>
<s:hidden name="addressForTransfer.email"/>
<s:hidden name="addressForTransfer.secondaryPhone"/>
<s:hidden name="customerType" />
<s:hidden name="addressForTransfer.contactPersonName" />

<div class="policy_section_div" style="width:100%">
   <div class="section_header" style="width:100%;margin-right:-10px;">        
      <s:text name="label.majorComponent.installerInformation"/>
   </div>
   
   <table width="100%" cellpadding="0" cellspacing="0" id="comp-addr" class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td width="30%"><s:property value="customer.companyName" /></td>
			<td class="non_editable labelStyle"  width="20%"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="addressForTransfer.contactPersonName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.addressType" /></td>
			<td><s:property value="addressForTransfer.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="addressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="addressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="addressForTransfer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="addressForTransfer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="addressForTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="addressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="addressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="addressForTransfer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="addressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="addressForTransfer.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="customer.corporateName" /></td>
		</tr>
	</tbody>
	</table>
</div>   