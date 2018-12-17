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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<td width="12%" style="border: 1px solid #b6c2cf" valign="center">
	<table>
		<s:if test = "!supplierPartReturn.recoverablePart.oemPart.isPartReturnsPresent()">
			<tr>
				<td><s:checkbox id='oemCheckBox_%{#recoverablePartsBeansStatus.index}'
						name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].returnToOEM"
						onclick="toggleOemSupplier(%{#recoverablePartsBeansStatus.index},'oem', %{supplierPartReturn.recoverablePart.oemPart.isPartReturnsPresent()})" />
					<s:text name="label.claim.oem" /></td>
			</tr>
		</s:if>
		<tr>
			<td><s:checkbox
					id='supplierCheckBox_%{#recoverablePartsBeansStatus.index}'
					name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].returnToSupplier"
					onclick="toggleOemSupplier(%{#recoverablePartsBeansStatus.index},'supplier', %{supplierPartReturn.recoverablePart.oemPart.isPartReturnsPresent()})" />
				<s:text name="label.claim.supplier" /></td>
		</tr>
	</table>
</td>
<td width="12%" style="border: 1px solid #b6c2cf" valign="center">
	<s:if test = "!supplierPartReturn.recoverablePart.oemPart.isPartReturnsPresent()">
		<s:select id="oem_location_%{#recoverablePartsBeansStatus.index}" cssStyle="width:65px"
			name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].oemReturnLocation" list="oemLocations"
			listKey="id" listValue="code" value='<s:property value="%{code}"/>'/>
	</s:if>

	<s:select id="supplier_location_%{#recoverablePartsBeansStatus.index}"
		cssStyle="width:65px"
		name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].supplierReturnLocation" list="locations"
		listKey="id" listValue="code" value='<s:property value="%{code}"/>' />
</td>
<script type="text/javascript">
dojo.addOnLoad(function(){
	// Selecting Return To Supplier On Load
	var index = <s:property value='%{#recoverablePartsBeansStatus.index}' />
	var toBeReturned = <s:property value="%{supplierPartReturn.recoverablePart.oemPart.isPartReturnsPresent()}" />;
	toggleOemSupplier(index, 'supplier', toBeReturned);
});
function toggleOemSupplier(index, returnTo, toBeReturned) {
	if(!toBeReturned){
		if (returnTo == "supplier") {
			dojo.byId("oemCheckBox_" + index).checked = false;
			dojo.byId("supplierCheckBox_" + index).checked = true;
			dojo.html.hide(dijit.byId("oem_location_" + index).domNode);
			dojo.html.show(dijit.byId("supplier_location_" + index).domNode);
		} else if (returnTo = "oem") {
			dojo.byId("oemCheckBox_" + index).checked = true;
			dojo.byId("supplierCheckBox_" + index).checked = false;
			dojo.html.show(dijit.byId("oem_location_" + index).domNode);
			dojo.html.hide(dijit.byId("supplier_location_" + index).domNode);
		}
	} else {
		dojo.byId("supplierCheckBox_" + index).checked = true;
	}
}
</script>