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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<table class="form"
	style="border-top:1px solid #EFEBF7; border-bottom: 1px solid #EFEBF7; border-left: 1px solid #EFEBF7; border-right: 1px solid #EFEBF7;"
	cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td nowrap="nowrap" width="25%"><label for="discountType"
				id="discountTypeLabel" class="labelStyle"> <s:text
						name="label.common.discountType" />:
			</label></td>
			<td width="15%" ><s:property value="warranty.discountType" /></td>
			<td nowrap="nowrap" width="25%"><label for="hoursOnPart"
				id="hoursOnPartLabel" class="labelStyle"> <s:text
						name="label.common.discountNumber" />:
			</label></td>
			<td width="25%"> <s:property value="warranty.discountNumber" />
			<td nowrap="nowrap" width="15%"><label for="hoursOnPart"
				id="discountPercentageLabel" class="labelStyle"> <s:text
						name="label.common.discountPercentage" />:
			</label></td>
			<td width="25%"><s:property value="warranty.discountPercentage" />
		</tr>
	</tbody>
</table>
