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
<div class="admin_section_div" align="left" width="100%"/>
	<table width="100%">
	  <tr>
			<th width="5%" class="admin_table_heading">&nbsp;</th>
	  	<th class="admin_table_heading"><s:text name="label.common.roleName"/></th>
	  </tr>
	  <tr>
	  	<td class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('included', this.checked)" /></td>
			<td colspan="2" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.alreadySelectedRoles"/></td>
		</tr>
		<s:iterator value="includedRoles">
			<s:if test="%{availableRoles.contains(top)}">
				<tr>
					<td align="center">
						<input type="checkbox" name="included" value="<s:property value="name"/>" checked />
					</td>
					<td class="admin_selections"><s:property value="name"/></td>
				</tr>
			</s:if>
			<s:else>
				<input type="checkbox" name="included_hidden" value="<s:property value="name"/>" checked style="display:none"/>
			</s:else>
		</s:iterator>
		<tr>
			<td width="5%" class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('available', this.checked)" /></td>
			<td colspan="1" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.selectMoreRoles"/></td>
		</tr>
  	<s:iterator value="roles" status="iter">
			<s:if test="%{includedRoles.contains(top)}"></s:if>
	  	<s:else>
	  		<tr>
		  		<td align="center">
						<s:if test="%{availableRoles.contains(top)}">
		  				<input type="checkbox" name="available" value="<s:property value="top.name"/>"/>
		  			</s:if>
		  			<s:else>
		  				<input type="checkbox" value="<s:property value="top.name"/>" disabled/>
		  			</s:else>
		  		</td>
		  	  <td class="admin_selections"><s:property value="name"/></td>
				</tr>
	  	</s:else>
  	</s:iterator>
	</table>
</div>