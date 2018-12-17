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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<table style="width: 100%">
<tr>
<td class="non_editable"><s:text name="label.shipmentDate" />:</td>
<td><span id="shipmentDate"><s:property
	value="warrantyList[0].forItem.shipmentDate" /></span></td>
<td class="non_editable"><s:text name="label.itemCondition" /></td>
<td><span id="itemCondition"><s:property
	value="warrantyList[0].forItem.conditionType.itemCondition" /></span></td>
</tr>
</table>