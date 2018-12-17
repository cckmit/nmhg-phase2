<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 2:53:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="application/x-json"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:hidden id="customerId" name="customer" value="%{warranty.customer.id}"/>
<s:hidden id="customerAddress"  name="addressForTransfer" value="%{warranty.addressForTransfer.id}"/>
<s:if test="warranty.customer.isIndividual()">
	<%-- INDIVIDUAL ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="indiv-addr"
		class="grid">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.name" /></td>
			<td><s:property value="warranty.customer.name" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td><s:property value="warranty.customer.companyName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine3" /></td>
			</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="warranty.addressForTransfer.city" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.state" /></td>
			<td><s:property value="warranty.addressForTransfer.state" /></td>
		</tr>
		<tr>
			 <td class="non_editable labelStyle" ><s:text name="label.county" /></td>
        <td><s:property value="warranty.addressForTransfer.countyCodeWithName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.addressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.country" /></td>
			<td><s:property value="warranty.addressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.addressForTransfer.phone" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.email" /></td>
			<td><s:property value="warranty.addressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.addressForTransfer.fax" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.sic.code" />:</td>
			<td><s:property value="%{warranty.customer.siCode}" /></td>
		</tr>
	 <tr>
	  <tr>
	  <td class="non_editable labelStyle"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	    <td class="non_editable labelStyle" width="20%"><s:text name="label.customerContactTitle" /></td>
        <td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	 </tr>

	</tbody>
	</table>
</s:if>
<s:elseif test="warranty.customer!=null">
	<%-- COMPANY ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="comp-addr"
		class="form " style="margin:0px;">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td><s:property value="warranty.customer.name" /></td>
		</tr>

		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="warranty.addressForTransfer.addressLine3" /></td>
			</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.city" /></td>
			<td><s:property value="warranty.addressForTransfer.city" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.state" /></td>
			<td><s:property value="warranty.addressForTransfer.state" /></td>
		</tr>
					<tr>
			 <td class="non_editable labelStyle" ><s:text name="label.county" />
          </td>
        <td colspan="3"><s:property value="warranty.addressForTransfer.countyCodeWithName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.zip" /></td>
			<td><s:property value="warranty.addressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.country" /></td>
			<td><s:property value="warranty.addressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.phone" /></td>
			<td><s:property value="warranty.addressForTransfer.phone" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.email" /></td>
			<td><s:property value="warranty.addressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.fax" /></td>
			<td><s:property value="warranty.addressForTransfer.fax" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.corporateName" /></td>
			<td><s:property value="%{#corporateName}"/></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.sic.code" />:</td>
			<td><s:property value="%{warranty.customer.siCode}"/></td>
		</tr>
	 <tr>
	   <td class="non_editable labelStyle"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	    <td class="non_editable labelStyle" width="20%"><s:text name="label.customerContactTitle" /></td>
        <td><s:property value="warranty.addressForTransfer.customerContactTitle" /></td>
	 </tr>

	</tbody>
	</table>
</s:elseif>	
<s:else>
	<table width="100%" cellpadding="0" cellspacing="0" id="dealer-addr"
		class="form" style="margin:0px;">
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
      </tbody>
    </table>
</s:else>
