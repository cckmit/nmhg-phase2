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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%> 
<%@taglib prefix="authz" uri="authz"%> 		         

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
  
<table width="98%" class="grid borderForTable" cellpadding="0"
	cellspacing="0">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.partGroup.code" /></th>
			<th><s:text name="label.partGroup.description" /></th>
			<th><s:text name="label.partGroup.quantity" /></th>
			<%-- <th><s:text name="label.partGroup.standardcost" /></th> --%>
		</tr>
	</thead>
	<tbody>
	<s:if test="inventoryPartGroups!=null && inventoryPartGroups.size()>0">
		<s:iterator value="inventoryPartGroups" status="status">
			<tr>
				<td><s:property value="inventoryPartGroups[#status.index].partGroupCode" /></td>
				<td><s:property value="inventoryPartGroups[#status.index].partGroupDescription" /></td>
				<td><s:property value="inventoryPartGroups[#status.index].qty" /></td>
				<%-- <td><s:property value="inventoryPartGroups[#status.index].standardCost" /></td> --%>
			</tr>
		</s:iterator>
		</s:if>
	</tbody>
</table>

