<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>

<style type="text/css">
</style>
<tr>
	<td width="10%">
		<table>
			<tr>
				<td><s:text name="label.partReturn.accept" /></td>
			</tr>
			<tr>
				<td><s:textfield size="2" id="accept_%{#partsCounter}"
						name='uiRecoverablePartsBeans[%{#partsCounter}].accepted'
						value="%{accepted}" /></td>
			</tr>
		</table>
	<td align="center"><s:select id="supplierPartAcceptanceReason_%{#partsCounter}" 
						name="uiRecoverablePartsBeans[%{#partsCounter}].acceptanceCause"
						cssClass="processor_decesion"
						list="getLovsForClass('SupplierPartAcceptanceReason',recoveryClaim)"
						theme="twms" listKey="code"
						listValue="description" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" /></td>
	<td>
		<table>
			<tr>
				<td><s:checkbox
						name="uiRecoverablePartsBeans[%{#partsCounter}].returnToDealer"
						id="returnToDealer_%{#partsCounter}" /></td>
				<td><s:text name="label.partReturn.returnToDealer" /></td>
			</tr>
			<tr>
				<td><s:checkbox
						name="uiRecoverablePartsBeans[%{#partsCounter}].scrap"
						id="scrap_%{#partsCounter}" /></td>
				<td><s:text name="label.partReturn.scrap" /></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td width="10%">
		<table>
			<tr>
				<td><s:text name="label.partReturn.reject" /></td>
			</tr>
			<tr>
				<td><s:textfield size="2" id="reject_%{#partsCounter}"
						name='uiRecoverablePartsBeans[%{#partsCounter}].rejected'
						value="%{rejected}"></s:textfield></td>
			</tr>
		</table>
	</td>
	<td width="90%" align="center"><s:select id="supplierPartRejectionReason_%{#partsCounter}" 
						name="uiRecoverablePartsBeans[%{#partsCounter}].failureCause"
						cssClass="processor_decesion"
						list="getLovsForClass('SupplierPartRejectionReason',recoveryClaim)"
						theme="twms" listKey="code"
						listValue="description" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" /></td>
	<td><s:checkbox
			name="uiRecoverablePartsBeans[%{#partsCounter}].returnToSupplier"
			id="returnToSupplier_%{#partsCounter}" /> <s:text
			name="label.partReturn.returnToSupplier" /></td>
</tr>

<script type="text/javascript">
dojo.addOnLoad(function() {
	dojo.connect(
		dojo.byId("returnToDealer_" + <s:property value="%{#partsCounter}"/>), "onchange",
			function(evt){
				unCheckScrap(<s:property value="%{#partsCounter}"/>);
			}
		);
	dojo.connect(
		dojo.byId("scrap_" + <s:property value="%{#partsCounter}"/>), "onchange",
			function(evt){
				unCheckReturnToDealer(<s:property value="%{#partsCounter}"/>);
			}
		);
});

function unCheckScrap(index){
	if(dojo.byId("returnToDealer_"+index).checked){
		dojo.byId("scrap_"+index).checked = false;
	}
}

function unCheckReturnToDealer(index){
	if(dojo.byId("scrap_"+index).checked){
		dojo.byId("returnToDealer_"+index).checked = false;
	}
}
</script>