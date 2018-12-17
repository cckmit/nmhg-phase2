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
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<div  style="width:100%">
 <div class="admin_section_div" style="width:100%">		
	<table width="100%" class="grid">
		<tr>
			<td width="20%" nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.campaign.code" />:</label></td>
			<td ><s:property value="code"/></td>
			<td width="20%" nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.campaign.classCode" />:</label></td>
			<td ><s:property value="campaignClass.description"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.common.startDate" />:</label></td>
			<td><s:property value="fromDate"/></td>
			<td nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.common.endDate" />:</label></td>
			<td><s:property value="tillDate"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.campaign.description" />:</label></td>
			<td colspan="3"><s:property value="description"/></td>			
			<td>
           </td>		 
		</tr>
	</table>
 </div> 
</div>
