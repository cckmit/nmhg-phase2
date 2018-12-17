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
			<th><s:text name="label.option.OptionCode" /></th>
			<th><s:text name="label.option.OrderOptionLineNumber" /></th>
			<th><s:text name="label.option.OptionType" /></th>
			<th><s:text name="label.option.OptionDescription" /></th>
			<s:if test="isInternalUser()">	
			<th><s:text name="label.option.OptionGrossPrice" /></th>		
			<th><s:text name="label.option.OptionNetPrice" /></th>
			<th><s:text name="label.option.OptionGrossValue" /></th>
			<th><s:text name="label.option.OptionDiscountValue" /></th>
			<th><s:text name="label.option.OptionNetValue" /></th>
			<th><s:text name="label.option.OptionDiscountPercent" /></th>
			</s:if>
			<th><s:text name="label.option.ActiveInactiveStatus" /></th>
			<th><s:text name="label.option.SpecialOptionStatus" /></th>
			<th><s:text name="label.common.DieselTier" /></th>				
		</tr>
	</thead>
	<tbody>
	<s:if test="inventoryItemOptions!=null && inventoryItemOptions.size()>0">
		<s:iterator value="inventoryItemOptions" status="status">
		<s:if test='isInternalUser() || inventoryItemOptions[#status.index].activeInactiveStatus == "A"'>
			<tr>
				<td><s:property value="inventoryItemOptions[#status.index].optionCode" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].orderOptionLineNumber" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionType" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionDescription" /></td>
				<s:if test="isInternalUser()">
				<td><s:property value="inventoryItemOptions[#status.index].optionGrossPrice" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionNetPrice" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionGrossValue" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionDiscountValue" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionNetValue" /></td>
				<td><s:property value="inventoryItemOptions[#status.index].optionDiscountPercent" /></td>
				</s:if>
				<s:if test='inventoryItemOptions[#status.index].activeInactiveStatus == "A"'>
					<td>ACTIVE</td>
				</s:if>
				<s:else>
					<td>INACTIVE</td>
				</s:else>
				<td><s:property value="inventoryItemOptions[#status.index].specialOptionStatus" /></td>
			    <td><s:property value="inventoryItemOptions[#status.index].dieselTier" /></td>				
			</tr>
		</s:if>
		</s:iterator>
		</s:if>
	</tbody>
</table>

