<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>


<%-- COMPANY ADDRESS --%>
<div id="comp-addr" style="margin-top:10px">
<table style="width:96%" cellpadding="0" cellspacing="0">
	<tr>
		<td width="16%" class="labelStyle"><s:text name="label.companyName" /></td>
		<td width="40%"><s:textfield name="customer.companyName"  id="company_Name"/></td>
		<td width="16%" class="labelStyle"  id='corporate_Name'><s:text name="label.corporateName" /></td>
		<td id='corporate_Name_textfield'><s:textfield name="customer.corporateName" /></td>
		<td colspan="2"></td>
	</tr>
	<s:if test="customer.id != null">
		<tr>
			<td width="16%" class="labelStyle"><s:text name="customer.search.customerNumber" />:</td>
			<td colspan="3"><s:property value="customer.customerId" /></td>
		</tr>
	</s:if>
</table>





<jsp:include page="company_address.jsp" flush="true" /> 


</div>