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
<s:hidden name="dealerGroupConfidParam"/>
<div class="admin_section_div" align="left" width="100%"/>
 <div style="overflow: auto;width:100%;">
	<table width="100%">
	  <tr>
	  	<th width="5%" class="admin_table_heading">&nbsp;</th>
	  	<th class="admin_table_heading"><s:text name="label.manageGroup.groupName"/></th>
	    <th class="admin_table_heading"><s:text name="label.common.description"/></th>
	    <s:if test="dealerGroupConfigParam">
           <th class="admin_table_heading"><s:text name="label.common.code"/></th>
        </s:if>
	  </tr>
	    <tr>
			<td class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('included', this.checked)" /></td>
			<td colspan="3" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.alreadySelectedGroups"/></td>
		</tr>
		<s:iterator value="includedGroups">
		<s:if test="%{availableGroups.contains(top)}">
			<tr>
				<td align="center">
					<input type="checkbox" name="included" value="<s:property value="name"/>" checked />
				</td>
				<td class="admin_selections"><s:property value="name"/></td>
			    <td class="admin_selections"><s:property value="description"/></td>
			    <s:if test="dealerGroupConfigParam">
                     <td class="admin_selections"><s:property value="code"/></td>
                </s:if>
			</tr>
		</s:if>
		<s:else>
			<input type="checkbox" name="included_hidden" value="<s:property value="name"/>" checked style="display:none"/>
		</s:else>
		</s:iterator>
		<tr>
			<td width="5%" class="admin_table_subheading"><input type="checkbox" onclick="toggleOptions('available', this.checked)" /></td>
			<td colspan="3" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.selectMoreGroups"/></td>
		</tr>
	  <s:iterator value="groups" status="iter">
	  	<s:if test="%{includedGroups.contains(top)}">
	  	</s:if>
	  	<s:else>
	  		<tr>
		  		<td align="center">
		  			<s:if test="%{availableGroups.contains(top)}">
		  				<input type="checkbox" name="available" value="<s:property value="top.name"/>" />
		  			</s:if>
		  			<s:else>
		  				<input type="checkbox" value="<s:property value="top.name"/>" disabled />
		  			</s:else>
		  		</td>
		  	  	<td class="admin_selections"><s:property value="name"/></td>
			    <td class="admin_selections"><s:property value="description"/></td>
			    <s:if test="dealerGroupConfigParam">
                     <td class="admin_selections"><s:property value="code"/></td>
                </s:if>
			</tr>
	  	</s:else>
	  </s:iterator>
	</table>
	</div>
</div>