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
<%@ taglib prefix="u" uri="/ui-ext"%>
	
<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />


<u:body>
	<form name="baseForm" id="baseForm">

	<div class="admin_section_div">

	<table width="100%" cellspacing="0" cellpadding="0">

		<tr width="100%">
			<td colspan="5">
			<div class="admin_section_heading"><s:text name="label.roles.roleDetails" />
			</div>
			</td>
		</tr>
		<tr width="100%">
			<td  width="25%" class="labelBold"><s:text name="label.roles.roleName" />:</td>
			
			<td  width="25%" class="labelNormal"><s:property 
				value="role.name"  /></td>
				<td></td>
				<td  width="25%"class="labelBold"><s:text
				name="label.roles.roleDescription" />:</td>
			<td width="25%" class="labelNormal"><s:property
				 value="role.description"   /></td>
		</tr>
		<tr width="100%">
			<td width="25%" class="labelBold"><s:text
				name="label.roles.roleType" />:</td>
			<td width="25%" class="labelNormal">	<s:property
				 value="role.roleType.type"   /> </td>
		</tr>
		

		<tr width="100%">
			<td colspan="5">
			<div class="admin_section_subheading"><s:text name="label.roles.assignedUsers" /></div>
			
			
			</td>
			</tr>
				
				
		<s:iterator value="userList" status="iter" >
		<td width = "20%" class="labelBold" ><s:property value="%{lastName}"/>,<s:property value="%{firstName}"/></td>
	
     	<s:if test = "%{#iter.index +1 == (#iter.index +1)/5*5 }" >
				<tr>
				</s:if>	
		
		</s:iterator>
		
		
		
		
		</table>
		
		
	

	</form>
</u:body>
