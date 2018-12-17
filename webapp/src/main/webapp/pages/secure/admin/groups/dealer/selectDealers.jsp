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
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:hidden name="dealerGroupConfidParam"/>

<div class="admin_section_div" align="left" width="100%"/>
<div style="overflow: auto;width:100%;">
	<table width="100%">
	  <tr>
			<th width="5%" class="admin_table_heading">&nbsp;</th>
	  	<th class="admin_table_heading" colspan="2"><s:text name="label.common.dealerName"/></th>
	  </tr>
	  <tr>
	  	<td class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('included', this.checked)" /></td>
			<td colspan="2" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.alreadySelectedDealers"/></td>
		</tr>
		<s:iterator value="includedDealers">
			<s:if test="%{availableDealers.contains(top)}">
				<tr>
					<td align="center" width="1%">
						<input type="checkbox" name="included" value="<s:property value="name"/>" checked />
					</td>
					<td class="admin_selections"><s:property value="name"/>
					</td>
				</tr>
			</s:if>
			<s:else>
			<tr>
			    <td align="center">	
				   <input type="checkbox" name="included_hidden" value="<s:property value="name"/>" checked style="display:none"/></td>
				   <td class="admin_selections"><s:property value="name"/>
		  		   </td>
		  	</tr>
			</s:else>
		</s:iterator>		
		
		<tr>
			<td width="5%" class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('available', this.checked)" /></td>
			<td colspan="2" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.selectMoreDealers"/></td>
		</tr>
  	<s:iterator value="dealers" status="iter">
			<s:if test="%{includedDealers.contains(top)}"></s:if>
	  	<s:else>
	  		<tr>
					<s:if test="%{availableDealers.contains(top)}">
						<td align="center" width="1%">
		  					<input type="checkbox" name="available" value="<s:property value="top.name"/>"/>
		  					</td>
		  					<td class="admin_selections"><s:property value="name"/>
		  					</td>
		  				
		  			</s:if>
		  			<s:else>
		  				<td align="center">	
		  					<input type="checkbox" value="<s:property value="top.name"/>" disabled/>
		  					</td>
		  					<td class="admin_selections"><s:property value="name"/>
		  				    </td>		  				
		  			</s:else>
			</tr>
	  	</s:else>
	</s:iterator>
	
	</table></div>
</div>