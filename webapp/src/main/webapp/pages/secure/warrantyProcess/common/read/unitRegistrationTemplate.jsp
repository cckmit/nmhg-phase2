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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<tr>
	<td width="100%" height="100%" style="border: 0">

	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td style="border: 0">
			<div dojoType="dijit.layout.ContentPane">
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.machineInfo"/>"
				labelNodeClass="section_header" open="true">
				<table cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.serialNumber" />:</td>

						<td class="" width="37%">  <u:openTab autoPickDecendentOf="true" id="equipment_%{inventoryItem.id}"
                tabLabel="Inventory %{inventoryItem.serialNumber}"
                url="inventoryDetail.action?id=%{inventoryItem.id}">
                    <u style="cursor: pointer;">
                        <s:property value="inventoryItem.serialNumber" />
                    </u>
                </u:openTab></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.model" />:</td>
						<td class="" width="35%"><s:property value="inventoryItem.ofType.model.name" /></td>

					</tr>

					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.product" />:</td>
						<td class="" width="37%"><s:property value="inventoryItem.ofType.product.name" /></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.shipmentDate" />:</td>
						<td class="" width="35%"><s:property value="inventoryItem.shipmentDate" /></td>
					</tr>

					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.buildDate" />:</td>
						<td class="" width="37%">
						<s:property value="inventoryItem.builtOn" />
						</td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:if test="warranty.transactionType.getTrnxTypeKey() == 'DR'">
						<s:text name="label.deliveryDate" />
						</s:if>
						<s:else>
						<s:text name="label.common.transferDate" />
						</s:else>
						:</td>
						<td class="" width="35%">
							<s:property value="warrantyDeliveryDate" />						
						</td>
					</tr>

					<tr>
					<s:if test="isInstallingDealerEnabled()">
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.dateInstall" />:</td>
						<td class="" width="37%">
							<s:property value="installationDate" />	
						</td>
					</s:if>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.hoursOnMachine" />:</td>
						<td class="" width="35%">
							<s:property value="inventoryItem.hoursOnMachine" />														
							</td>
					</tr>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.oem" />:</td>
						<td class="" width="37%">
						<s:property value="inventoryItem.brandType" />			
						
							</td>
						<td width="20%" class="labelStyle" nowrap="nowrap" colspan="2"></td>
					</tr>					
					
				</tbody>
			</table>
			</div>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<table width="100%"
				id='<s:property value="qualifyId(\"policyDetailsTable\")" />'>
				<tr>
					<td>
					<div dojoType="twms.widget.TitlePane"
						title="<s:text name="label.common.warrantyCoverage"/>"
						labelNodeClass="section_header" open="true">
					<div dojoType="dijit.layout.ContentPane"
						id='<s:property value="qualifyId(\"policyDetails\")" />'><s:if test="!selectedPolicies.empty"><jsp:include
						page="policy_list.jsp" flush="true" /></s:if></div></div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		
		<s:if test="warranty.forItem.getIsDisclaimer()">
			<tr>
				<td style="border : 0">
				 	<div dojoType="twms.widget.TitlePane"
						title="<s:text name="label.disclaimerInfo"/>"
						labelNodeClass="section_header" open="true">
						<div dojoType="dijit.layout.ContentPane"
						id='<s:property value="qualifyId(\"disclaimerDetails\")" />'><s:set name="inventoryItem" value="warranty.forItem"/>
                 	<jsp:include page="disclaimer.jsp" /></div></div>
                 	
				</td>
			</tr>
		</s:if>
			
			<tr>
			<td style="border: 0">

			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.majorComponents"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='<s:property value="qualifyId(\"majorComponentDetails\")" />'>
			<jsp:include page="major_components_info.jsp"></jsp:include></div>
			</div>
			</td>
		</tr>
				<tr>
			<s:if test="buConfigAMER">
			<td style="border: 0">

			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.additionalComponents"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='<s:property value="qualifyId(\"additionalComponentDetails\")" />'>
			<jsp:include page="inventory_additionalcomponents_info.jsp"></jsp:include></div>
			</div>
			</td>
			</s:if>
		</tr>	
		
		<s:if test="warrantyTaskInstance.status.status=='Forwarded'">
			<tr>
				<td style="border:0">
					<div dojoType="twms.widget.TitlePane" title="<s:text name="label.warranty.supportDocs"/>" labelNodeClass="section_header" open="true">
						<jsp:include flush="true" page="../../../warranty/fileUpload/uploadCommonAttachments.jsp"/>
						<jsp:include page="../../../warranty/fileUpload/fileUploadDialogForNList.jsp" />
					</div>
				</td>
			</tr>	
		</s:if>
		<s:property value="displayStockUnitDiscountDetails"/>
		<s:if test="displayStockUnitDiscountDetails()">
		<tr>
			<td style="border:0">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.title.stockUnitDiscount"/>" labelNodeClass="section_header" open="true">
				<jsp:include flush="true" page="../../../warranty/stock_unit_discount_detail.jsp"/>
			</div>
			</td>
		</tr>
		</s:if>
	</table>
	</td>
	<td width="5%">	
	</td>

</tr>

