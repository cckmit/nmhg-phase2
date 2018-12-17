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
<s:hidden name="operator" value="%{operator.id}"/>
<s:hidden name="operatorAddressForTransfer.type" />
<s:hidden name="operatorAddressForTransfer.addressLine"/>
<s:hidden name="operatorAddressForTransfer.addressLine2"/>
<s:hidden name="operatorAddressForTransfer.addressLine3"/>
<s:hidden name="operatorAddressForTransfer.city"/>
<s:hidden name="operatorAddressForTransfer.state"/>
<s:hidden name="operatorAddressForTransfer.zipCode"/>
<s:hidden name="operatorAddressForTransfer.country"/>
<s:hidden name="operatorAddressForTransfer.phone"/>
<s:hidden name="operatorAddressForTransfer.email"/>
<s:hidden name="operatorAddressForTransfer.secondaryPhone"/>
<s:hidden name="operatorAddressForTransfer.contactPersonName" />

<s:if test="customerType == 'Individual'">
	<%-- INDIVIDUAL ADDRESS --%>
	<table width="100%" cellpadding="0" cellspacing="0" id="indiv-addr"
		class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.name" /></td>
			<td width="30%"><s:property value="operator.name" /></td>
			<td class="non_editable labelStyle" width="20%"><s:text name="label.companyName" /></td>
			<td><s:property  value="operator.companyName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" ><s:text name="label.addressType" /></td>
			<td><s:property value="operatorAddressForTransfer.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" ><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" ><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle" ><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="operatorAddressForTransfer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="operatorAddressForTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="operatorAddressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="operatorAddressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="operatorAddressForTransfer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="operatorAddressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="operatorAddressForTransfer.secondaryPhone" /></td>
		</tr>
	</tbody>
	</table>
</s:if>
<s:elseif test="customerType == 'Company'">   
	<%-- COMPANY ADDRESS --%>
	<table  width="100%" cellpadding="2" cellspacing="2" id="comp-addr"
		class="form">
	<tbody>
		<tr>
			<td class="non_editable labelStyle" width="15%"><s:text name="label.companyName" /></td>
			<td width="25%"><s:property value="operator.companyName" /></td>
			<td class="non_editable labelStyle"  width="20%"><s:text name="label.contactPersonName" /></td>
			<td><s:property value="operatorAddressForTransfer.contactPersonName" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.addressType" /></td>
			<td><s:property value="operatorAddressForTransfer.type.type" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line2" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine2" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.common.address.line3" />:</td>
			<td colspan="3"><s:property value="operatorAddressForTransfer.addressLine3" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.city" /></td>
			<td><s:property value="operatorAddressForTransfer.city" /></td>
			<td class="non_editable labelStyle"><s:text name="label.state" /></td>
			<td><s:property value="operatorAddressForTransfer.state" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td><s:property value="operatorAddressForTransfer.zipCode" /></td>
			<td class="non_editable labelStyle"><s:text name="label.country" /></td>
			<td><s:property value="operatorAddressForTransfer.country" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
			<td><s:property value="operatorAddressForTransfer.phone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.email" /></td>
			<td><s:property value="operatorAddressForTransfer.email" /></td>
		</tr>
		<tr>
			<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
			<td><s:property value="operatorAddressForTransfer.secondaryPhone" /></td>
			<td class="non_editable labelStyle"><s:text name="label.corporateName" /></td>
			<td><s:property value="operator.corporateName" /></td>
		</tr>
	</tbody>
	</table>
</s:elseif>
<div>
	<center>
		<s:iterator value="pageNoList" status="pageCounter">
			&nbsp;
			<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
				<span id="page_<s:property value="%{#pageCounter.index}"/>">
			</s:if>	
			<s:else>
				<span id="page_<s:property value="%{#pageCounter.index}"/>" style="cursor:pointer;text-decoration:underline">
			</s:else>
			<s:property value="%{intValue()}" />
			<script type="text/javascript">
				dojo.addOnLoad(function(){	
					var index = '<s:property value="%{#pageCounter.index}"/>';
					var pageNo='<s:property value="pageNo"/>';				
					if(index!=pageNo){
						dojo.connect(dojo.byId("page_"+index),"onclick",function(){
							getMatchingOperators(index);  
						});
					}	 
				});
			</script>
		   </span>	
	   </s:iterator>
	</center>
</div>

