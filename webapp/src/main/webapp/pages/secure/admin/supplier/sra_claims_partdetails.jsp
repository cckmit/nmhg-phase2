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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

	<div dojoType="twms.widget.TitlePane"
		title="<s:text name="label.sra.contract.recoverableParts"/>"
		labelNodeClass="section_header" id="oemPartsReplaced" open="true">
		<table class="grid borderForTable" border="0" cellspacing="0"
			cellpadding="0" width="100%">
			<thead>
				<tr class="row_head">
					<th class="colHeaderTop">Part Number</th>
					<th class="colHeaderTop">Supplier Part Number</th>
					<th class="colHeaderTop"><s:text
						name="columnTitle.common.quantity" /></th>
					<th class="colHeaderTop"><s:text
						name="columnTitle.partShipperPartsShipped.tracking_number" /></th>
					<th class="colHeaderTop"><s:text
						name="columnTitle.recoveryClaim.rgaNumber" /></th>
					<th class="colHeaderTop"><s:text
						name="columnTitle.partShipperPartsShipped.carrier" /></th>
					<th class="colHeaderTop">Supplier</th>
					<th class="colHeaderTop">Return</th>
		
				</tr>
			</thead>
			<tbody>
				<s:iterator value="recoveryClaim.recoveryClaimInfo.recoverableParts" status = "recoveredPartStatus"
					id="recoveredPart">
					<tr>
						<td><s:property
							value="oemPart.itemReference.referredItem.number" /></td>
						<td><s:property value="supplierItem.number"></s:property></td>
						<td><s:property value="quantity"></s:property></td>
						<td><s:property value="supplierPartReturns[0].supplierShipment.trackingId" /></td>
						 <s:if test="supplierPartReturns[0]!=null && supplierPartReturns[0].rgaNumber!=null">
						 <td><s:property value="supplierPartReturns[0].rgaNumber" /></td></s:if>
                     	<s:else>
						<td><s:property value="oemPart.partReturns[0].rmaNumber"/></td></s:else>
						<td><s:a id = "carrier_%{#recoveredPartStatus.index}" href="#" onclick="window.open('%{supplierPartReturns[0].carrier.url}')" title="%{supplierPartReturns[0].carrier.code}"><s:property value="supplierPartReturns[0].carrier.name" /></s:a></td>
							
						<%-- TODO supplier action--%>
						<td><s:property value="supplierItem.ownedBy.name"></s:property></td>
						<td align="center"><s:checkbox disabled="true"
							name="supplierReturnNeeded"></s:checkbox></td>
					</tr>
				</s:iterator>
		
		
			</tbody>
		</table>
	</div>

<s:if test="rejectionReason != null || acceptanceReason != null ">
<div id="separator"></div>
<div dojoType="twms.widget.TitlePane" title="Acceptance/Dispute Reasons" labelNodeClass="section_header">
<table>
<tr>
<s:if test="acceptanceReason != null ">
<td width="5%" class="carrierLabel"><s:text name="title.attributes.acceptanceReason"/>:</td>
		  <td width="35%" class="carrierLabelNormal">
		    <s:property value="%{acceptanceReason}" />
		  </td>
</s:if>
<s:if test="rejectionReason != null">
<td width="5%" class="carrierLabel"><s:text name="title.attributes.acceptanceReason"/>:</td>
		  <td width="35%" class="carrierLabelNormal">
		    <s:property value="%{rejectionReason}" />
		  </td>
</s:if>
</tr>
</table>									
</div>
</s:if>

