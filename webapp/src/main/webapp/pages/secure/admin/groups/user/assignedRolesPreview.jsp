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
<%
            response.setHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "must-revalidate");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
%>


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
			<div class="admin_section_heading"><s:text name="label.manageProfile.userDetails" />
			</div>
			</td>
		</tr>
		<tr width="100%">
			<td width="20%" class="labelBold"><s:text name="label.manageGroup.userName" /> :</td>
			<td width="30%" class="label"> <s:property value="%{user.firstName}"/>,<s:property value="%{user.lastName}"/> </td>
		
				<s:if test="organisations.entrySet().size() < 2" >
			<s:iterator value = "organisations.entrySet()">					   
			<td width="20%" class="labelBold"><s:text name="label.user.organizationType" />:</td>
			<td width="30%" class="labelNormal"><s:property
			value="value" /></td>			
			</s:iterator>			
			</s:if>
			</tr>			
			<s:else>
			<tr class="row_head">
	<th    ><div align = "center"><s:text name="label.user.organizations" /></div></th>
	
	</tr>
	<tr class="row_head">	
	<th width="50%" ><s:text name="label.user.organizationType" /></th>
	</tr>		
	 <s:iterator value = "organisations.entrySet()">	
	   <tr width="100%">			
		<td width="50%" class="labelNormal"><s:property
			value="value" /></td>
			</tr>
			</s:iterator>
			</s:else>		
			<tr width="100%">
			<td colspan="5">
			<div class="admin_section_subheading"><s:text name="label.user.assignedRoles" /></div>
			
			
			</td>
			</tr>
				
			<tr class="row_head">
		 <th  colspan = "2" > <s:text name="columnTitle.roles.roleName" /> </th>
         <th  colspan = "2"> <s:text name="columnTitle.roles.roleDescription" /> </th>
          
			</tr>	
		<s:iterator value="user.roles" status="iter" >
		<tr>
		    <td colspan = "2"  class="label" ><s:property value="%{name}"/></td>
            <td colspan = "2"  class="label" ><s:property value="%{description}"/></td>
            	
		</tr>
	
     	
		
		</s:iterator>
		
		
		
		
		</table>
		
		
	

	</form>
</u:body>