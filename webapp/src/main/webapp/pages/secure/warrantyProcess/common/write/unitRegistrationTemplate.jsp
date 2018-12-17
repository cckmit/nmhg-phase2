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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<tr>
	<td width="95%" height="100%" style="border: 0">

	<table width="95%" cellpadding="0" cellspacing="0" border="0">
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

						<td class="" width="37%"><s:hidden
							id="%{qualifyId(\"inventoryIemSN\")}"
							name="%{#nListName}.inventoryItem" value="%{inventoryItem.id}" />
							<s:hidden
							id="%{qualifyId(\"indexFlag\")}"
							name="indexFlag" value="%{#nListIndex}" />
							<s:hidden
							id="%{qualifyId(\"nameFlag\")}"
							name="nameFlag" value="%{#nListName}" />
							
						<s:label id="%{qualifyId(\"serialNumber\")}" theme="twms"							
							value="%{inventoryItem.serialNumber}"
							/></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.model" />:</td>
						<td class="" width="35%"><s:label
							id="%{qualifyId(\"model\")}"
							value="%{inventoryItem.ofType.model.name}" /></td>

					</tr>

					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.product" />:</td>
						<td class="" width="37%"><s:label
							id="%{qualifyId(\"product\")}"
							value="%{inventoryItem.ofType.product.name}" /></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.shipmentDate" />:</td>
						<td class="" width="35%"><s:label
							id="%{qualifyId(\"shipment\")}"
							value="%{inventoryItem.shipmentDate}" /></td>
					</tr>

					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.buildDate" />:</td>
						<td class="" width="37%"><s:label
							id="%{qualifyId(\"build\")}" value="%{inventoryItem.builtOn}" /></td>
						<td width="20%" class="labelStyle" nowrap="nowrap">
						<s:if test="warranty.transactionType.getTrnxTypeKey() == 'DR'">
						<s:text name="label.deliveryDate" />
						</s:if>
						<s:else>
						<s:text name="label.common.transferDate" />
						</s:else>
						:</td>
						<td class="" width="35%"><sd:datetimepicker name='%{#nListName}.warrantyDeliveryDate' value='%{warrantyDeliveryDate}' id='%{qualifyId("deliveryDate")}' />
							<script type="text/javascript">
	           		dojo.addOnLoad(function(){		           	
	            	dojo.connect(dijit.byId('<s:property value="qualifyId(\"deliveryDate\")" />'),"onChange",function(){	            		            		
	            	getAllPolicies('<s:property value="%{#nListIndex}"/>','<s:property value="%{#nListName}" />');	            		
	            	});
		            });

	           		</script></td>
					</tr>

					<tr>
						<s:if test="isInstallingDealerEnabled()">
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.dateInstall" />:</td>
						<td class="" width="37%"><sd:datetimepicker name='%{#nListName}.installationDate' value='%{installationDate}' id='%{qualifyId("installationDateForUnit")}' required='false' />
							
							<script type="text/javascript">
	           		dojo.addOnLoad(function(){		           	
	            	dojo.connect(dijit.byId('<s:property value="qualifyId(\"installationDateForUnit\")" />'),"onChange",function(){	            		            		
	            	getAllPolicies('<s:property value="%{#nListIndex}"/>','<s:property value="%{#nListName}" />');	            		
	            	});
		            });

	           		</script></td>
	           		</s:if>	           		
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.hoursOnMachine" />:</td>
						<td class="" width="35%"><s:textfield
							id="%{qualifyId(\"hoursOnMachine\")}"
							name="%{#nListName}.inventoryItem.hoursOnMachine"
							value="%{inventoryItem.hoursOnMachine}" onchange="getAllPolicies('%{#nListIndex}','%{#nListName}')" />							
							</td>
					</tr>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.oem" />:</td>
						<td class="" width="37%">
						<s:label
							id="%{qualifyId(\"oem\")}"
							value="%{inventoryItem.brandType}" />
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
			<td style="border: 0">
			
				<div dojoType="twms.widget.TitlePane"
					title="<s:text name="label.common.warrantyCoverage"/>"
					labelNodeClass="section_header" open="true">
				<div dojoType="dijit.layout.ContentPane"
					id='<s:property value="qualifyId(\"policyDetails\")" />'><s:if
					test="!availablePolicies.empty">					
					<jsp:include page="../../../warranty/policy_list.jsp" flush="true"/>					
				</s:if></div> </div>
			
			</td>
		</tr>	
					
		<tr>
			<td style="border : 0">
				<div dojoType="twms.widget.TitlePane" title='<s:text name="label.disclaimerInfo"/>' labelNodeClass="section_header">
                	<div dojoType="dijit.layout.ContentPane" id='<s:property value="qualifyId(\"disclaimerPage\")" />'>
                		<s:if test="inventoryItem.latestWarranty.dieselTierWaiver != null">
							<jsp:include page="../read/disclaimer.jsp"/>
                 		</s:if>
                 	</div>
                </div>
			</td>
		</tr>
		<tr>
			<td style="border: 0">

			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.majorComponents"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='<s:property value="qualifyId(\"majorComponentDetails\")" />'>
			<jsp:include page="../../../warranty/warranty_major_components.jsp"></jsp:include></div>
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
			<jsp:include page="../../../warranty/warranty_additional_components.jsp"></jsp:include></div>
			</div>
			</td>
			</s:if>
		</tr>
		
		<s:if test="isMarketInfoApplicable()">
		<tr>
			<td style="border: 0">
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
				<div dojoType="twms.widget.TitlePane"
					title="<s:text name="label.marketInfo"/>"
					labelNodeClass="section_header" open="true">
				<div dojoType="dojox.layout.ContentPane" executeScripts="true"
					scriptSeparation="false"
					id='<s:property value="qualifyId(\"marketInfo\")" />'><jsp:include page="../../../warranty/marketInfo.jsp"></jsp:include></div>
				</div>
			</table>
			</td>
		</tr>
		</s:if>
		
		<tr>
			<td style="border: 0">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.warranty.supportDocs"/>" labelNodeClass="section_header" open="true">
				<jsp:include flush="true" page="../../../warranty/fileUpload/uploadCommonAttachments.jsp"/>
				<jsp:include page="../../../warranty/fileUpload/fileUploadDialogForNList.jsp"/>
			</div>
			</td>
		</tr>
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

