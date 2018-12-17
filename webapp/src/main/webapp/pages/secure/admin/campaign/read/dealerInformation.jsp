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
<%@taglib prefix="u" uri="/ui-ext"%>
<s:head theme="twms"></s:head>
<u:stylePicker fileName="adminPayment.css" />
<u:body>
	<div class="admin_section_div" style="margin: 5px;">
		<div class="admin_section_heading">
			<s:text name="title.common.dealerInfo"></s:text>
		</div>
		<table class="grid">
			<tr>
				<td width="20%" class="labelStyle"><s:text
						name="label.warrantyAdmin.dealerName" /> :</td>
				<td><s:text name="dealerInformationToBeDisplayed.name" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text
						name="label.warrantyAdmin.dealerCity" /> :</td>
				<td><s:text
						name="dealerInformationToBeDisplayed.address.city" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text
						name="label.warrantyAdmin.dealerState" /> :</td>
				<td><s:if test = "dealerInformationToBeDisplayed.address.state != null"><s:text
						name="dealerInformationToBeDisplayed.address.state" /></s:if></td>
				<td></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text
						name="label.warrantyAdmin.dealerCountry" /> :</td>
				<td><s:text
						name="dealerInformationToBeDisplayed.address.country" /></td>
				<td></td>
			</tr>
		</table>
	</div>
</u:body>