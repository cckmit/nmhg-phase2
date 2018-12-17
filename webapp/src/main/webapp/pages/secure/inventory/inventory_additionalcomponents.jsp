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
<!--  <authz:ifUserNotInRole roles="supplier">
 <s:if test="invItemAdditionalComponents!=null && invItemAdditionalComponents.size()>0">
	<table width="100%">
		<tr>
			<td width="75%">&nbsp;</td>
			<td width="25%"><u:openTab cssClass="link"
				url="viewAdditionalComponentsForUnit.action?inventoryItem=%{inventoryItem.id}"
				id="viewAddComponents"
				tabLabel="Additional Component for %{inventoryItem.serialNumber}"
				autoPickDecendentOf="true" forceNewTab="true">
				<s:text name="label.majorComponent.viewAdditionalComponentsHistory" />
			</u:openTab></td>
		</tr>
	</table>
  </s:if>
</authz:ifUserNotInRole>  -->
<style>
.borderForTable tr td {
	border: none !important;
}
</style>
<table width="98%" class="grid borderForTable" cellpadding="0"
	cellspacing="0">
	<thead>
		<tr class="row_head">
			<th width="10%"><s:text name="label.additionalComponent.type" /></th>
			<th width="10%"><s:text name="label.additionalComponent.subType" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.serialNumber" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.partNumber" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.partDescription" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.DateCode" /></th>
			<th width="10%"><s:text
					name="label.additionalComponent.manufacturer" /></th>
			<th width="10%"><s:text name="label.additionalComponent.model" /></th>
		</tr>
	</thead>
	<tbody>
		<s:if
			test="invItemAdditionalComponents!=null && invItemAdditionalComponents.size()>0">
			<s:iterator value="invItemAdditionalComponents">
				<tr>
					<td><s:property value="type" /></td>
					<td><s:property value="subType" /></td>

					<td><s:property value="%{serialNumber}" /></td>
					<td><s:property value="partNumber" /></td>
					<td><s:property value="partDescription" /></td>
					<td><s:property value="dateCode" /></td>
					<td><s:property value="manufacturer" /></td>
					<td><s:property value="model" /></td>

				</tr>
			</s:iterator>
		</s:if>
	</tbody>
</table>

