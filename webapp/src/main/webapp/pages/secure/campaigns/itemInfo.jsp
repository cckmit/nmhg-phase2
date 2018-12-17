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

<div  style="width:100%">
   <div class="admin_section_div" style="width:99%;margin:5px;">
	<table width="100%" class="defaultNoBorderTable grid" >
		<tr>
			<td width="20%" nowrap="nowrap" >
			<label class="labelStyle"><s:text name="label.common.serialNumber" /></label></td>
			<td width="20%"><s:property value="serialNumber"/></td>
			<td width="20%" nowrap="nowrap" class="labelStyle">
			<label class="labelStyle"><s:text name="columnTitle.common.product" /></label></td>
			<td width="40%"><s:property value="ofType.product.itemGroupDescription"/></td>
		</tr>
        <tr>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.make" /></label></td>
			<td><s:property value="ofType.make"/></td>
			<td nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.common.model" /></label></td>
			<td><s:property value="ofType.model.description"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.conditions" /></label></td>
			<td><s:property value="conditionType.itemCondition"/></td>
			<td nowrap="nowrap" class="labelStyle"><label class="labelStyle"><s:text name="label.common.buildDate" /></label></td>
			<td><s:property value="builtOn"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.shipmentDate" /></label>:</td>
			<td><s:property value="shipmentDate"/></td>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.deliveryDate" /></label>:</td>
			<td><s:property value="deliveryDate"/></td>
		</tr>
        <tr>
            <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.businessUnitName"/>:</td>
            <td class="" width="35%">
                <s:property value="businessUnitInfo.name"/></td>
                <td nowrap="nowrap" ><label class="labelStyle"><s:text name="columnTitle.listRegisteredWarranties.customer_name" /></label>:</td>
			<td><s:property value="latestBuyer.name"/></td>
           
        </tr>
         <tr>
            <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.campaign.dealer"/>:</td>
            <td><s:property value="currentOwner.name"/></td>       
        </tr>
    </table>
  </div>	
</div>