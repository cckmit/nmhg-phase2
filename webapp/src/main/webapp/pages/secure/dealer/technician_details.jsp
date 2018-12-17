<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<table class="form">
<tr>
    <td class="bold"><s:text name="label.dealerUser.firstName"/></td>
    <td><s:property value="user.firstName" /></td>
    <td class="bold"><s:text name="label.common.address.line1"/></td>
    <td><s:property value="user.address.addressLine1" /></td>
</tr>
<tr>
    <td class="bold"><s:text name="label.dealerUser.lastName"/></td>
    <td><s:property value="user.lastName"/></td>
    <td class="bold"><s:text name="label.common.address.line2"/></td>
    <td><s:property value="user.address.addressLine2"/></td>
</tr>
<tr>
	<td class="bold"><s:text name="label.manageProfile.locale"/></td>
	<td><s:property value="defaultLocale"/></td>
    <td class="bold"><s:text name="label.country"/></td>
    <td><s:property value="user.address.country"/></td>
</tr>
<tr>
    <td class="bold"><s:text name="label.state"/></td>
    <td><s:property value="stateCode"/></td>
    <td class="bold"><s:text name="label.city"/></td>
    <td><s:property value="cityCode"/></td>
</tr>
<tr>
    <td class="bold"><s:text name="label.dealerUser.email"/></td>
    <td><s:property value="user.email"/></td>
    <td class="bold"><s:text name="label.zip"/></td>
    <td><s:property value="zipCode"/></td>
</tr>
<tr>
    <td class="bold"><s:text name="label.common.phone"/></td>
    <td><s:property value="user.address.phone"/></td>
    <td class="bold"><s:text name="label.common.fax"/></td>
    <td><s:property value="user.address.fax"/></td>
</tr>
<s:if test="technicianDetails != null">
	<tr>
		<td class="bold"><s:text name="label.dealerUser.dateOfHire"/> : </td>
		<td><s:property value="technicianDetails.dateOfHire"/></td>
		<td class="bold"><s:text name="label.dealerUser.certification.fromDate"/> : </td>
		<td><s:property value="technicianDetails.certificationFromDate"/></td>
	</tr>
	<tr>
		<td class="bold"><s:text name="label.dealerUser.dateOfRenewal"/> : </td>
		<td><s:property value="technicianDetails.dateOfRenewal"/></td>
		<td class="bold"><s:text name="label.dealerUser.certification.toDate"/> : </td>
		<td><s:property value="technicianDetails.certificationToDate"/></td>
	</tr>
	<tr>
		<td class="bold"><s:text name="label.dealerUser.serviceManagerName"/> : </td>
		<td><s:property value="technicianDetails.serviceManagerName"/></td>
		<td class="bold"><s:text name="label.dealerUser.serviceManagerPhone"/> : </td>
		<td><s:property value="technicianDetails.serviceManagerPhone"/></td>
	</tr>
	<tr>
		<td class="bold"><s:text name="label.dealerUser.serviceManagerAddress"/> : </td>
		<td><s:property value="technicianDetails.serviceManagerAddress"/></td>
		<td class="bold"><s:text name="label.common.comments"/> : </td>
		<td><s:property value="technicianDetails.comments"/></td>
	</tr>
	<tr>
		<td class="bold"><s:text name="label.dealerUser.serviceManagerFax"/> : </td>
		<td><s:property value="technicianDetails.serviceManagerFax"/></td>
		<td class="bold"><s:text name="label.dealerUser.serviceManagerEmail"/> : </td>
		<td><s:property value="technicianDetails.serviceManagerEmail"/></td>
	</tr>
</s:if>
</table>

