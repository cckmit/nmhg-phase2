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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="sectionbgPadding">
		<div class="noBorderCellbg">
		<s:if test="%{taskInstances.size() > 0}">
		<table width="100%" border="0" cellspacing="1" cellpadding="0">
			<tr>
				<td colspan="6" nowrap="nowrap" class="subSectionTitle"><s:text
					name="section.label.claimDetails" /></td>
			</tr>
			<tr>
				<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">

					<s:set name="lastClaimId" value="" />
					<s:set name="firstClaim" value="true" />

					<s:iterator id="taskInstance" value="taskInstances"
						status="taskInstanceStatus">
						<s:set value="#taskInstance.getVariable('recoveryClaim').claim"	name="claim" />
						<s:set value="#taskInstance.getVariable('supplierPartReturn')"	name="supplierPartReturn" />
						<s:if test="!firstClaim && #lastClaimId != #claim.id">
				</table>
				</td>
			</tr>
			<tr>
				<td height="7">
				<div id="separatorbgColor"></div>
				</td>
			</tr>
			</s:if>
			<s:if test="#lastClaimId != #claim.id">
				<s:set name="lastClaimId" value="%{#claim.id}" />
				<s:set name="firstClaim" value="false" />
					<td>
					<table width="100%" border="0" cellpadding="0" cellspacing="0" >
						<tr>
							<td colspan="9" valign="middle" nowrap="nowrap">
							<table width="100%" border="0" cellpadding="0" cellspacing="0" >
								<tr>
									<td width="15%" class="label"><s:text
										name="label.claimNumber" />:</td>
									<td width="20%" class="labelNormal">
										<u:openTab	tabLabel="%{getText('label.claimNumber')} %{#claim.id}"
											url="search_result_detail.action?id=%{#claim.id}"
											id="claimIdForPart%{#claim.id}" cssClass="Inboxlink">
											<s:property value="%{#claim.claimNumber}" />
										</u:openTab>
									</td>
									<td width="18%" class="label"><s:text
										name="label.serialNumber" />:</td>
									<td class="labelNormal"><u:openTab
										tabLabel="%{getText('label.serialNumber')} %{#claim.itemReference.referredInventoryItem.serialNumber}"
										url="inventoryDetail.action?id=%{#claim.itemReference.referredInventoryItem.id}"
										id="SerialNoForPart%{#claim.id}" cssClass="Inboxlink">
										<s:property
											value="%{#claim.itemReference.referredInventoryItem.serialNumber}" />
									</u:openTab></td>
								</tr>
								<tr>
									<td width="15%" nowrap="nowrap" class="label"><s:text
										name="label.modelNumber" />:</td>
									<td width="20%" class="labelNormal"><s:property
										value="%{#claim.itemReference.unserializedItem.model.name}" /></td>
									<td width="18%" nowrap="nowrap" class="label"><s:text
										name="label.claim.workOrderNumber" />:</td>
									<td class="labelNormal"><s:property
										value="%{#claim.workOrderNumber}" /></td>
								</tr>
							</table>
							</td>
						</tr>

						<tr>
							<table width="100%" border="0" cellpadding="0" cellspacing="0"  class="grid borderForTable"  >
							   <tr>
								<td width="3%" valign="middle" nowrap="nowrap" class="colHeaderCenterAlign"></td>
								<td width="9%" valign="middle" nowrap="nowrap" class="colHeaderCenterAlign"><s:text
											name="label.supplierPartNumber" /></td>
								<td width="9%" valign="middle" nowrap="nowrap" class="colHeaderCenterAlign"><s:text
											name="label.partNumber" /></td>
								<td width="40%" valign="middle" nowrap="nowrap" class="colHeaderCenterAlign"><s:text
											name="label.description" /></td>
								
								<td width="12%" valign="middle" nowrap="nowrap"
									class="colHeaderCenterAlign"><s:text
											name="label.common.returnlocation" /></td>
								<td width="11%" nowrap="nowrap" class="colHeaderCenterAlign"><s:text 
									name="label.recoveryClaim.binInfo" /></td>
								<td width="11%" nowrap="nowrap" class="colHeaderCenterAlign"><s:text
											name="columnTitle.recoveryClaim.rgaNumber" /></td>
							  </tr>
						</tr>
						</s:if>

						<tr>
							<td width="3%" nowrap="nowrap" valign="middle" align="center"
								><s:checkbox
								id='_%{#taskInstanceStatus.index}' name="additionalSupplierPartReturns"
								fieldValue="%{#supplierPartReturn.id}" value="false" /></td>
							<td width="9%" nowrap="nowrap" ><s:property
								value="#supplierPartReturn.recoverablePart.supplierItem.number" /></td>
							<td width="9%" nowrap="nowrap" ><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.number" /></td>
							<td width="28%" ><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.description" /></td>
						    
							<td width="6%" ><s:property
									value="#supplierPartReturn.returnLocation.code" /></td>
							<td width="6%" >
							<s:property
									value="#supplierPartReturn.recoverablePart.oemPart.activePartReturn.warehouseLocation" />
							</td>
							<td width="12%"  valign="center"><s:property
								value="#supplierPartReturn.rgaNumber" /></td>
						</tr>
						</s:iterator>
						<%-- Complete the table Begin --%>
					</table>	
					</td>
				<tr>
					<td height="4">
					<div id="separatorbgColor"></div>
					</td>
				</tr>
				<%-- Complete the table End --%>
		</table>
		</td>
	</tr>
</table>
</s:if>
<s:else>
	<s:text name="label.common.noPartAvailable"/>
</s:else>
</div>
</td>
</tr>
</table>