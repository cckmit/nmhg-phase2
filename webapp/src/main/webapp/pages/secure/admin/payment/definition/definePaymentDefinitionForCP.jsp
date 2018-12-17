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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<s:head theme="twms" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<u:body>
	<u:actionResults />
	<s:form id="paymentDefForCPForm" name="paymentDefForCPForm"
		action="update_payment_definition_for_cp">
		<div class="policy_section_div">
		<div class="section_header"><s:text
			name="title.admin.commercialPolicy" /></div>
		<div class="spacer10"></div>
		<table border="0" cellspacing="0" cellpadding="0" width="60%"
			class="borderForTable">
			<thead>
				<tr class="row_head" align="center">
					<th align="center" style="text-align:center;"><s:text name="label.common.select" /></th>
					<th align="center" style="text-align:center;"><s:text
						name="columnTitle.managePayment.policyCategory" /></th>
					<th align="center" style="text-align:center;"><s:text name="label.common.claimTypes" /></th>
				</tr>
			</thead>
			<s:iterator value="allPaymentDefinitions" status="iter">
				<tr height="60">
				<s:if test="criteria.applForCommPolicyClaims">
				
				<s:hidden name="paymentDefinitionsForCP[0]" value="%{id}"/>		
				</s:if>		
					<td align="center"><input type="radio"
						name="paymentDefinitionsForCP[1]" value="<s:property value="id"/>"
						id="paymentForCP_<s:property value="#iter.index"/>"
						<s:if test="criteria.applForCommPolicyClaims">checked="checked"</s:if> />
					</td>
					<td align="center">
					 <span style="color:blue;cursor:pointer;text-decoration:underline">
 	    	    		<u:openTab cssClass="link" url="detail_payment_definition.action?id=%{criteria.id}&folderName='PAYMENT_DEFINITION'"
		        			id="policyLink_%{#iter.index}" tabLabel="Payment Definition %{criteria.identifier}"
	                		autoPickDecendentOf="true">
							<s:property value="criteria.identifier" />
			         	</u:openTab>
			         </span>
					</td>
					<td align="center"><s:text name="%{getText(criteria.clmTypeName)}" /></td>
				</tr>
			</s:iterator>
		</table>
		<div align="center" style="width: 60%; margin-top: 10px;"><input id="updateCP"
			class="buttonGeneric" type="submit"
			value="<s:text name='button.common.update'/>"	/></div>
		</div>
	</s:form>
</u:body>
</html>