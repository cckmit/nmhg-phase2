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
<%@taglib prefix="s" uri="/struts-tags"%>

<table class="grid" id="failure_details_table" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td width="1%" nowrap="nowrap" class="labelStyle"><s:text name="label.labor.totalLaborHours" />:</td>					 
		<td style="padding-left: 30px;"><s:property value='claim.serviceInformation.serviceDetail.laborPerformed[0].hoursSpent' /></td>
		<td width="70%"></td>	
	</tr>
</table>
