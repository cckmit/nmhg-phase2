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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>


<table class="form" style="border-top:none; border-bottom:none; border-left:none; border-right:none;" cellpadding="0" cellspacing="0">
	<tbody>
		<tr>
			<td nowrap="nowrap"><label for="discountType"
				id="discountTypeLabel" class="labelStyle"> <s:text
						name="label.common.discountType" />:
			</label></td>
<%-- 			<td>
			<s:select id="discountType" cssClass="processor_decesion"
					name="warranty.discountType"
					list="getLovsForClass('DiscountType',warranty)" theme="twms"
					listKey="code" listValue="description" value="%{inventoryItem.latestWarranty.discountType.description}" headerKey=""
					headerValue="%{getText('label.common.selectHeader')}" onchange="onChangeValueForDiscountType()"/> </td> --%>
					<td><tda:lov id="discountType" name="warranty.discountType" className="DiscountType"></tda:lov></td>
			<td nowrap="nowrap"><label for="hoursOnPart"
				id="discountNumberLabel" class="labelStyle" hidden="true"> <s:text
						name="label.common.discountNumber" />:
			</label></td>
			<td>

				<div id="discountNumberDropDown" hidden="true">
						<s:select
							id="discountNumberDropDownSelect" list="#{'1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8'}"
							name="warranty.discountNumber" cssStyle="width:100px;"
							cssClass="processor_decesion" headerKey="-1"
							headerValue="%{getText('label.common.selectHeader')}" />
				</div>

				<div id="discountNumberFreeText" hidden="true">
					<s:textfield id="discountNumberFreeTextField"
						value="%{warranty.discountNumber}" />
				</div>
			</td>
			<td nowrap="nowrap" ><label for="hoursOnPart"
				id="discountPercentageLabel" class="labelStyle" hidden="true">
					<s:text name="label.common.discountPercentage" />:
			</label></td>
			<td>
				<div id="discountPercentageFreeText" hidden="true" >
					<s:textfield name="warranty.discountPercentage"
						value="%{warranty.discountPercentage}"
						id="discountPercentageFreeText" />
				</div>
			</td>
		</tr>
	</tbody>
</table>
<script type="text/javascript">
	function onChangeValueForDiscountType() {

		if (dojo.byId('discountType') != null) {
			console.debug(dojo.byId('discountType').value
					+ "discounttype value")
			if (dojo.byId('discountType').value == "MATRIX") {
				dojo.byId("discountNumberLabel").hidden=false;
				dojo.byId("discountNumberDropDown").hidden=false;
				dojo.byId("discountNumberFreeText").hidden=true;
				dojo.byId("discountPercentageLabel").hidden=false;
				dojo.byId("discountPercentageFreeText").hidden=false;
				dojo.byId("discountNumberDropDownSelect").setAttr("name","warranty.discountNumber")
			} else if (dojo.byId('discountType').value == "SPR"
					|| dojo.byId('discountType').value == "PROGRAM") {
				dojo.byId("discountNumberLabel").hidden=false;
				dojo.byId("discountNumberDropDown").hidden=true;
				dojo.byId("discountNumberFreeText").hidden=false;
				dojo.byId("discountPercentageLabel").hidden=false;
				dojo.byId("discountPercentageFreeText").hidden=false;
				dojo.byId("discountNumberFreeTextField").setAttr("name","warranty.discountNumber")
			} else {
				dojo.byId("discountNumberLabel").hidden=true;
				dojo.byId("discountNumberDropDown").hidden=true;
				dojo.byId("discountNumberFreeText").hidden=true;
				dojo.byId("discountPercentageLabel").hidden=true;
				dojo.byId("discountPercentageFreeText").hidden=true;
			}
		}
	}

</script>