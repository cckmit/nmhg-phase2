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

<s:hidden name="nonCertifiedInstaller" value="%{nonCertifiedInstaller.id}"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.type" />
<s:hidden name="addressForTransferToNonCertifiedInstaller.addressLine"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.addressLine2"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.addressLine3"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.city"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.state"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.zipCode"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.country"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.phone"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.email"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.secondaryPhone"/>
<s:hidden name="addressForTransferToNonCertifiedInstaller.contactPersonName" />

<div class="policy_section_div" style="width:100%" id="nonCertInstallerInfo">
   <div class="section_header" style="width:100%;margin-right:-10px;">        
      <s:text name="label.majorComponent.installerInformation"/>
   </div>
   
   <table width="100%" cellpadding="0" cellspacing="0" id="comp-addr" class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td width="30%"><s:property value="nonCertifiedInstaller.companyName" /></td>
			<td class="non_editable labelStyle"  width="20%"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.contactPersonName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.addressType" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="addressForTransferToNonCertifiedInstaller.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="addressForTransferToNonCertifiedInstaller.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="addressForTransferToNonCertifiedInstaller.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="addressForTransferToNonCertifiedInstaller.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="nonCertifiedInstaller.corporateName" /></td>
		</tr>
	</tbody>
	</table>
</div>   