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
<table class="grid" id="failure_details_table">
	<tr>
		<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.labor.totalLaborHours" />:</td>
		<td><s:textfield
			name="task.claim.serviceInformation.serviceDetail.laborPerformed[0].hoursSpent"
			id="labor_hours_spent" /></td>
	</tr>
</table>