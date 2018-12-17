<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<div class="admin_section_div">

<table style="width: 30%" class="grid">
	<tr>
		<td><input type="radio"
			name="inventoryItemMappings[<s:property value="%{#nListIndex}" />].disclaimerAccepted"
			id="inventoryItemMappings<s:property value="%{#nListIndex}" />_disclaimerAccept"
			value="true" <s:if test="inventoryItemMappings[#nListIndex].disclaimerAccepted">checked</s:if>
			onClick="showWaiver(<s:property value='%{#nListIndex}' />)" /></td>
		<td class="labelStyle" nowrap="nowrap"><s:text
			name="label.common.accept" /></td>
		<td><input type="radio"
			name="inventoryItemMappings[<s:property value="%{#nListIndex}" />].disclaimerAccepted"
			id="inventoryItemMappings<s:property value="%{#nListIndex}" />__disclaimerReject"
			value="false" <s:if test="!inventoryItemMappings[#nListIndex].disclaimerAccepted">checked</s:if>
			onClick="showDisclaimer(<s:property value='%{#nListIndex}' />)" /></td>
		<td class="labelStyle" nowrap="nowrap"><s:text
			name="label.common.reject" /></td>
	</tr>
</table>

<div
	id="inventoryItemMappings<s:property value="%{#nListIndex}" />_disclaimerInfo"
	<s:if test="!inventoryItemMappings[#nListIndex].disclaimerAccepted">style="display: block"</s:if>
	<s:else>style="display: none"</s:else>
	>
	<s:set value="%{inventoryItemMappings[#nListIndex].dieselTierWaiver.disclaimer.split('\n')}" name="disclaimer"/>
	<s:iterator value="#disclaimer">
		<s:property value="%{[0].top}"/><br/>
	</s:iterator></div>
 	<div id="inventoryItemMappings<s:property value="%{#nListIndex}" />_waiver" 
 	<s:if test="inventoryItemMappings[#nListIndex].disclaimerAccepted">style="display: block"</s:if>
	<s:else>style="display: none"</s:else> >
   	 	<jsp:include page="waiverInformation.jsp"/>
   	 </div>
</div>
