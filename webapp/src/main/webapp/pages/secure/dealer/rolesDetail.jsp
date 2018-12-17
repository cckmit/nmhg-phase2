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
<%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<tr>
	<td class="labelStyle" colspan="4"><s:text
		name="label.dealerUser.assignRoles" /></td>
</tr>
<tr>
	<td width="20%"><b><s:text
		name="label.inboxView.availableFields" /></b></td>
	<td rowspan="2" width="10%" valign="middle" align="center"><input
		type="button" class="buttonGeneric" name="right" value="&gt;&gt;"
		onclick="moveSelectedOptionsFromAvailable(this.form['updownlist2'],this.form['list2'])"><br />
	<input type="button" class="buttonGeneric" name="left" value="&lt;&lt;"
		onclick="moveSelectedOptionsFromSelected(this.form['updownlist2'],this.form['list2'])">
	</td>
	<td><b><s:text name="label.inboxView.selectedFields" /></b></td>
</tr>
<tr>
	  <td class="non_editable" valign="top"><s:updownselect
		cssStyle="width:300px;height:200px;" id="updownlist2"
		cssClass="admin_selections" list="dealerRoles" listKey="name"
		listValue="%{getText(displayName)}" size="4" allowMoveDown="false"
		allowMoveUp="false" allowSelectAll="false" theme="simple" /></td>
	 <td class="non_editable" valign="top"><s:updownselect
		cssStyle="width:300px;height:200px;" id="list2"
		cssClass="admin_selections" list="rolesToBeDisplayed" listKey="name"
		listValue="%{getText(displayName)}" name="roleToBeAssigned" size="4"
		allowMoveDown="false" allowMoveUp="false" allowSelectAll="false"
		theme="simple" /></td>  
</tr> 