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
<%@page contentType="application/x-json"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<s:if test="warranty.customer.isIndividual()">
	<%-- INDIVIDUAL ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="indiv-addr" class="form">
	<tbody>
		<tr>
			<td class="non_editable" width="16%"><s:text name="label.name" /></td>
			<td width="30%"><s:property value="warranty.customer.name" /></td>
			<td class="non_editable" width="15%"><s:text name="label.companyName" /></td>
			<td><s:property value="warranty.customer.companyName" /></td>
		</tr>		
		<tr>
			<td class="non_editable"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine3" /></td>
			</tr>
		<tr>
			<td class="non_editable"><s:text name="label.city" /></td>
			<td><s:property value="warranty.addressForTransfer.city" /></td>
			<td class="non_editable"><s:text name="label.state" /></td>
			<td><s:property value="warranty.addressForTransfer.state" /></td>
		</tr>
					<tr>
			 <td class="non_editable labelStyle" ><s:text name="label.county" />
          </td>
        <td><s:property value="warranty.addressForTransfer.countyCodeWithName" /></td>
		</tr>
		<tr>
			<td class="non_editable"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.addressForTransfer.zipCode" /></td>
			<td class="non_editable"><s:text name="label.country" /></td>
			<td><s:property value="warranty.addressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.addressForTransfer.phone" /></td>
			<td class="non_editable"><s:text name="label.email" /></td>
			<td><s:property value="warranty.addressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.addressForTransfer.secondaryPhone" /></td>
		</tr>
		 <tr>
		 <td class="non_editable labelStyle"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="warranty.addressForTransfer.contactPersonName" /></td>
	    <td class="non_editable labelStyle" width="20%"><s:text name="label.customerContactTitle" /></td>
        <td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	 </tr>
	</tbody>
	</table>
</s:if>
<s:else>
	<%-- COMPANY ADDRESS --%>
	<div style="background:#F3FBFE; padding-left:10px;">
	<s:if test="isCustomerDetailsNeededForDR_Rental() == 'true'">
	<s:push value="warranty">
	<s:if test="warranty.customerType =='Dealer Rental'" >
	<table width="100%" cellpadding="2" cellspacing="2" id="dealer-addr"
		class="form">
      <tbody>
        <tr>
          <td class="non_editable labelStyle" width="15%"><s:text name="label.companyName" /></td>
          <td width="25%" ><s:property value="forDealer.name" /></td>
        </tr>
       
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line1" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine1" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line2" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine2" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line3" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine3" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.city" /></td>
          <td><s:property value="forDealer.address.city" /></td>
          <td class="non_editable labelStyle"><s:text name="label.state" /></td>
          <td><s:property value="forDealer.address.state" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.zip" /></td>
          <td><s:property value="forDealer.address.zipCode" /></td>
          <td class="non_editable labelStyle"><s:text name="label.country" /></td>
          <td><s:property value="forDealer.address.country" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.phone" /></td>
          <td><s:property value="forDealer.address.phone" /></td>
          <td class="non_editable labelStyle"><s:text name="label.email" /></td>
          <td><s:property value="forDealer.address.email" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.fax" /></td>
          <td><s:property value="forDealer.address.fax" /></td>
        </tr>
        <tr>
			<td class="non_editable labelStyle"><s:text name="label.common.sic.code" />:</td>
			<td><s:property value="forDealer.address.sicCode" /></td>
		</tr>
      </tbody>
    </table>
   
    </s:if>
     </s:push>
     </s:if>
    <s:else >
	<table width="100%" cellpadding="0" cellspacing="0" id="comp-addr" class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%" nowrap="nowrap"><s:text name="label.companyName" /></td>
			<td width="30%"><s:property value="warranty.customer.companyName" /></td>			
		</tr>		
		
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine" /></td>
		</tr>
		
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine2" /></td>
		</tr>
		
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine3" /></td>
			</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="warranty.addressForTransfer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="warranty.addressForTransfer.state" /></td>
		</tr>
					<tr>
			 <td class="non_editable labelStyle" ><s:text name="label.county" />
          </td>
        <td><s:property value="warranty.addressForTransfer.countyCodeWithName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.addressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="warranty.addressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.addressForTransfer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="warranty.addressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.addressForTransfer.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="operator.corporateName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.sic.code" />:</td>
			<td><s:property value="operator.siCode" /></td>
		</tr>
			 <tr>
			 <td class="non_editable labelStyle"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="warranty.addressForTransfer.contactPersonName" /></td>
	    <td class="non_editable labelStyle" width="20%"><s:text name="label.customerContactTitle" /></td>
        <td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	 </tr>
	</tbody>
	</table>

	</s:else>
	</div>
	</s:else>
