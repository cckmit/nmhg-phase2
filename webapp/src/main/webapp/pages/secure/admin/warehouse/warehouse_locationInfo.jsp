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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div >
	<div class="section_header">
		<s:text name="label.common.details"></s:text>
	</div>
	<table width="100%" cellpadding="0" cellspacing="0" border="0" class="grid" align="center">
		<tbody>
			<tr>
				<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.manageWarehouse.wareHouseName"/>:</td>
				<td><s:property value="warehouse.location.code"/></td>
			</tr>
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.address"/>:</td>
				<td><s:property value="warehouse.location.address.addressLine1"/><s:text name=","/></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<s:property value="warehouse.location.address.city"/><s:text name=","/>
					<s:property value="warehouse.location.address.state"/><s:text name=","/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<s:property value="warehouse.location.address.country"/><s:text name=","/>
					<s:property value="warehouse.location.address.zipCode"/><s:text name="."/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td><s:property value="warehouse.contactPersonName"/></td>
			</tr>
		</tbody>
	</table>
</div>