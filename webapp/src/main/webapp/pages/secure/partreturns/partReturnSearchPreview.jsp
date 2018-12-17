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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<s:head theme="twms" />
<title><s:text name="title.common.warranty" /></title>


<u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
</head>

<u:body>
<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.ValidationTextBox");      
        
    </script>
<div  dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
    <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>" id="claim_details"
             labelNodeClass="section_header"  open="true" style="overflow-X: hidden; overflow-Y: auto">
    	<div class="policy_section_div">
			<div class="section_header"><s:text name="title.partReturnConfiguration.claimDetails" /></div>         
				<table class="grid" cellspacing="0" cellpadding="0"
					 width="98%">
					<tr>
						<td class="labelStyle"><s:text name="label.common.claimNumber" />:</td>
						<td class="labelNormal"><u:openTab
							tabLabel="Claim Number %{partReturnClaimSummary.claim.claimNumber}"
							url="view_search_detail.action?id=%{partReturnClaimSummary.claim.id}"
							id="claimIdForPart%{partReturnClaimSummary.claim.id}"
							cssClass="Inboxlink">
							<s:property value="partReturnClaimSummary.claim.claimNumber" />
						</u:openTab></td>
						<td class="labelStyle"><s:text name="label.common.serialNumber" />:</td>
						<td class="labelNormal"><s:if
							test="partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber != null">
							<u:openTab
								tabLabel="Serial Number %{partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber}"
								url="inventoryDetail.action?id=%{partReturnClaimSummary.claim.itemReference.referredInventoryItem.id}"
								id="SerialNoForPart%{partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber}%{partReturnClaimSummary.claim.id}"
								cssClass="Inboxlink">
								<s:property
									value="partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber" />
							</u:openTab>
						</s:if> <s:else>
							<div>-</div>
						</s:else></td>
					</tr>
					<tr>
						<td class="labelStyle"><s:text
							name="label.partReturnConfiguration.modelnumber" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.claim.itemReference.unserializedItem.model.name" /></td>
						<td class="labelStyle"><s:text name="label.common.itemNumber" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.claim.itemReference.unserializedItem.alternateNumber" /></td>
					</tr>
				</table>
		</div>
</div>
      <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" id="part_details"
             labelNodeClass="section_header"  open="true" style="overflow-X: hidden; overflow-Y: auto">
             <div class="policy_section_div">
				<div class="section_header"><s:text name="title.partReturnConfiguration.partDetails" /></div>
				<table class="grid" cellspacing="0" cellpadding="0"
					 width="98%">
				     
					<tr>
						<s:if test="part.itemReference.serialized == true">
							<td class="labelStyle"><s:text name="label.common.serialNumber" />:</td>
							<td class="labelNormal"><s:property
								value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.referredInventoryItem.serialNumber" />
							</td>
						</s:if>
						<td class="labelStyle"><s:text
							name="label.partReturnConfiguration.partNumberWithCol" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
				
					</tr>
				
					<tr>
						<td class="labelStyle"><s:text name="columnTitle.common.description" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.unserializedItem.description" /></td>
						<td class="labelStyle"><s:text name="columnTitle.common.quantity" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.oemPartReplaced.numberOfUnits" /></td>
					</tr>
				
					<tr>
						<td class="labelStyle"><s:text name="columnTitle.common.dueDate" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.dueDate" /></td>
						<td class="labelStyle"><s:text name="label.common.status" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.status" /></td>
					</tr>
				
					<tr>
						<td class="labelStyle"><s:text
							name="label.partReturnConfiguration.carrier" />:</td>
						<td class="labelNormal">
							<a href="<s:property value="partReturnClaimSummary.partReturn.shipment.carrier.url" escape="false"/>" target="_blank">			      
			      				<s:property value="partReturnClaimSummary.partReturn.shipment.carrier.name" /> 
							</a>	
						</td>
						<td class="labelStyle"><s:text
							name="label.partReturnConfiguration.trackingNumber" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.shipment.trackingId" /></td>
					</tr>
				
					<tr>
						<td class="labelStyle"><s:text
							name="label.partReturnConfiguration.shipmentDate" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.shipment.shipmentDate" /></td>
						<td class="labelStyle"><s:text
			                name="label.partReturnConfiguration.shipmentNumber" />:</td>
		                <td class="labelNormal"><s:property 
		                    value="partReturnClaimSummary.partReturn.shipment.transientId"/></td>
					</tr>
				
					<tr>
						<td class="labelStyle"><s:text
							name="columnTitle.partShipperPartsClaimed.location" />:</td>
						<td class="labelNormal"><s:property
							value="partReturnClaimSummary.partReturn.returnLocation.code" /></td>
						<td class="labelStyle"><s:text
			                name="columnTitle.manageWarehouse.wareHouseName" />:</td>
		                <td class="labelNormal"><s:property
		                	value="partReturnClaimSummary.partReturn.warehouseLocation" /></td>
					</tr>
					<tr>
	                    <td class="labelStyle"><s:text
		                 	name="columnTitle.partReturnConfiguration.failurecause" />:</td>
	                	<td class="labelNormal"><s:property
		                	value="partReturnClaimSummary.partReturn.inspectionResult.failureReason.description" /></td>
	               </tr>
				</table>
			</div>
		</div>
	</div>
</u:body>
</html>
