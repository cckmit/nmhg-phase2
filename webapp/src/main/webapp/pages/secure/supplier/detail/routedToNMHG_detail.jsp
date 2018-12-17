<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<head>
<title><s:text name="title.common.warranty" /></title>
<s:head theme="twms" />

<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="inventory.css" />
<u:stylePicker fileName="detailDesign.css" />
<u:stylePicker fileName="master.css" />
<u:stylePicker fileName="common.css" />
<SCRIPT type="text/javascript">
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.ContentPane");
</SCRIPT>
</head>

<u:body>
	<u:actionResults />
	<s:if test="recoverablePartsBeans.size() > 0">
		<div dojoType="dijit.layout.LayoutContainer"
			style="width: 100%; height: 100%; background: white; overflow-y: auto;">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
				<s:form action="routedToNMHG_submit" validate="true" theme="twms">
					<s:hidden name="recoveryClaim" />
					<s:hidden name="id" />
					<div class="policy_section_div">
						<div class="section_header">
							<s:text name="section.label.claimDetails" />
						</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="grid">
							<tr>
								<td width="15%" class="label"><s:text
										name="label.common.claimNumber" />:</td>
								<td><u:openTab
										tabLabel="%{getText('label.claimNumber')} %{recoveryClaim.claim.claimNumber}"
										url="view_search_detail.action?id=%{recoveryClaim.claim.id}"
										id="claimHyperlink" cssClass="alinkclickable">
										<s:property value="%{recoveryClaim.claim.claimNumber}" />
									</u:openTab></td>
								<td width="18%" class="label"><s:text
										name="label.serialNumber" /></td>
								<td class="labelNormal"><u:openTab
										tabLabel="%{getText('label.serialNumber')} %{recoveryClaim.claim.itemReference.referredInventoryItem.serialNumber}"
										url="inventoryDetail.action?id=%{recoveryClaim.claim.itemReference.referredInventoryItem.id}"
										id="SerialNoForPart%{recoveryClaim.claim.id}"
										cssClass="alinkclickable">
										<s:property
											value="%{recoveryClaim.claim.itemReference.referredInventoryItem.serialNumber}" />
									</u:openTab></td>
							</tr>
							<tr>
								<td width="15%" nowrap="nowrap" class="label"><s:text
										name="label.modelNumber" /></td>
								<td width="20%" class="labelNormal"><s:property
										value="%{recoveryClaim.claim.itemReference.unserializedItem.model.name}" /></td>
								<td width="18%" nowrap="nowrap" class="label"><s:text
										name="label.claim.workOrderNumber" /> :</td>
								<td class="labelNormal"><s:property
										value="%{recoveryClaim.claim.workOrderNumber}" /></td>
							</tr>
							<tr>
								<td width="3%" valign="middle" nowrap="nowrap"
									class="warColHeader" align="center"
									style="border: 1px solid #b6c2cf">
									<input id="selectAll_<s:property value="recoveryClaim.id" />" type="checkbox"
                    						checked="checked" value="checkbox" style="border:none"/>
              						<script>
                  						var multiCheckBox = dojo.byId("selectAll_<s:property value="recoveryClaim.id" />");
                  						var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox);
              						</script>
              					</td>
								<td width="9%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="label.supplierPartNumber" /></td>
								<td width="9%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="label.partNumber" /></td>
								<td width="28%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="label.description" /></td>
								<td width="6%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="label.quantity" /></td>
								<td width="12%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf""><s:text
										name="label.routedToNMHG.returnTo" /></td>
								<td width="12%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf""><s:text
										name="label.common.returnlocation" /></td>
								<td width="12%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="label.recoveryClaim.binInfo" /></td>
								<td width="12%" valign="middle" nowrap="nowrap"
									class="warColHeader" style="border: 1px solid #b6c2cf"><s:text
										name="columnTitle.recoveryClaim.rgaNumber" /></td>
							</tr>
							<s:iterator id="recoverablePartsBean" value="recoverablePartsBeans"
								status="recoverablePartsBeansStatus">
								<s:hidden name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].taskInstance" />
								<s:hidden name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].supplierPartReturn" />
								<tr>
									<td width="3%" nowrap="nowrap" valign="middle"
										style="border: 1px solid #b6c2cf" align="center">
										<s:checkbox
											name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].partSelected" value="selected" id="%{#recoverablePartsBeansStatus.index}" />
										<script>
                 							var selectElementId = "<s:property value="%{#recoverablePartsBeansStatus.index}" />";
                          					multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
            							</script>
									</td>
									<td width="9%" nowrap="nowrap"
										style="border: 1px solid #b6c2cf"><s:property
											value="supplierPartReturn.recoverablePart.supplierItem.number" /></td>
									<td width="9%" nowrap="nowrap"
										style="border: 1px solid #b6c2cf"><s:property
											value="supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.number" /></td>
									<td width="40%" style="border: 1px solid #b6c2cf"><s:property
											value="supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.description" /></td>
									<td width="6%" style="border: 1px solid #b6c2cf"><s:property
											value="supplierPartReturn.recoverablePart.oemPart.numberOfUnits" /></td>
									<%@ include file="routedToNMHG_returnLocation.jsp"%>
									<td width="12%" style="border: 1px solid #b6c2cf"
										valign="center"><s:property
											value="supplierPartReturn.returnLocation.code" /></td>
									<td width="12%" style="border: 1px solid #b6c2cf"
										valign="center"><s:textfield cssStyle="margin:5px;"
											size="16" id="rgaNumber_%{#taskInstanceStatus.index}"
											name="recoverablePartsBeans[%{#recoverablePartsBeansStatus.index}].supplierPartReturn.rgaNumber" />
								</tr>
							</s:iterator>
						</table>
					</div>
					<div class="buttonWrapperPrimary">
						<s:submit type="button" name="transitionTaken"
							cssClass="buttonGeneric" label="%{getText('label.routedToNMHG.accept')}"
							value="%{@tavant.twms.jbpm.WorkflowConstants@ROUTED_PART_RETURN_ACCEPTED}"
							onclick="this.value='%{@tavant.twms.jbpm.WorkflowConstants@ROUTED_PART_RETURN_ACCEPTED}'" />
						<s:submit type="button" name="transitionTaken"
							cssClass="buttonGeneric" label="%{getText('label.routedToNMHG.reject')}"
							value="%{@tavant.twms.jbpm.WorkflowConstants@ROUTED_PART_RETURN_REJECTED}"
							onclick="this.value='%{@tavant.twms.jbpm.WorkflowConstants@ROUTED_PART_RETURN_REJECTED}'" />
					</div>
				</s:form>
			</div>
		</div>
	</s:if>
</u:body>
<script type="text/javascript">					
	function enableOrDisableRGA(status,index) {
  		var index = index;
    	status=!status;
    	if(dojo.byId("rgaNumber_"+index)){
    		dojo.byId("rgaNumber_"+index).disabled = status;
    	}
    }

	
</script>