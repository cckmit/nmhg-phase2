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
 <authz:ifUserNotInRole roles="supplier">
 <s:if test="inventoryItemCompositon!=null && inventoryItemCompositon.size()>0">
	<table width="100%">
		<tr>
			<td width="85%">&nbsp;</td>
			<td width="15%"><u:openTab cssClass="link"
				url="viewMajorComponentsForUnit.action?inventoryItem=%{inventoryItem.id}"
				id="viewComponents"
				tabLabel="Major Component for %{inventoryItem.serialNumber}"
				autoPickDecendentOf="true" forceNewTab="true">
				<s:text name="label.majorComponent.viewComponentsHistory" />
			</u:openTab></td>
		</tr>
	</table>
  </s:if>
</authz:ifUserNotInRole>
 <style>
.borderForTable tr td{
border:none !important;
}
</style> 
<table width="98%" class="grid borderForTable" cellpadding="0"
	cellspacing="0">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.component.sequenceNumber" /></th>
			<th><s:text name="label.majorComponent.componentSerialNo" /></th>
			<th><s:text name="label.majorComponent.componentPartNo" /></th>
			<th><s:text name="columnTitle.common.componentDescription" /></th>
			<th><s:text name="label.majorComponent.componentManufacturer"/></th>
			<th><s:text name="label.common.seriesTypeDescription"/></th>
			<th><s:text name="Part Fitted Date" /></th>
			<th><s:text name="columnTitle.common.warrantyStartDate" /></th>
			<th><s:text name="columnTitle.common.warrantyEndDate" /></th>
		</tr>
	</thead>
	<tbody>
	<s:if test="inventoryItemCompositon!=null && inventoryItemCompositon.size()>0">
		<s:iterator value="inventoryItemCompositon" status="status">
		<s:if test="status!=null && status!='Inactive'">

			<tr>
				<td><s:property value="sequenceNumber" /></td>
				<td>
					<u:openTab autoPickDecendentOf="true" id="major_Component_Details[%{id}]" cssClass="link"
                           tabLabel="EquipmentInfo %{part.serialNumber}" forceNewTab="true"
                           url="majorComponentInventoryDetail.action?id=%{part.id}&installedOnItem=%{inventoryItem}" catagory="majorComponents">
                         <u style="cursor: pointer;">
                        	<s:property value="part.serialNumber" />
                    	</u>
                    </u:openTab>
				</td>

				<td><s:property value="%{part.ofType.getBrandItemNumber(part.brandType)}" /></td>
				<td><s:property value="serialTypeDescription" /></td>
				
				<td><s:property value="manufacturer" /></td>

				<td><s:property value="part.serialTypeDescription" /></td>

				<td><s:property value="part.installationDate" /></td>

				<td><s:property value="part.wntyStartDate" /></td>
				
				<td><s:property value="part.wntyEndDate" /></td>

			</tr>
			</s:if>
		</s:iterator>
		</s:if>
	</tbody>
</table>

