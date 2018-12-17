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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<u:stylePicker fileName="common.css"/>
<div style="height:300px;overflow:auto;background:#F3FBFE;border:1px solid #EFEBF7">
<table border="0"  width="100%" cellpadding="0" cellspacing="0" id="individual_Search" class="grid borderForTable">
	<thead>
		<tr>
			<th class="warColHeader" nowrap="nowrap" align="left" width="10%">&nbsp;</th>
			<th class="warColHeader" align="left" width="10%"><s:text name="customer.search.company.name"/></th>
	   		<th class="warColHeader" align="left" width="10%"><s:text name="customer.search.address"/></th>
	   		<th class="warColHeader" align="left" width="13%"><s:text name="customer.search.city"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="12%"><s:text name="customer.search.state"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.zip"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.country"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.contact.name"/></th>
		</tr>
	</thead>
	<tbody id="individual_customer_list">			
		<s:iterator value="matchingCustomerList" id="customer">
		<s:iterator value="addresses">
		<tr>
			<td width="10%" nowrap="nowrap"  >
			  <s:if test="dealerOrganization.id == loggedInUser.belongsToOrganization.id">
				 <div align="center"> 
				 <a class="Datalink" 
					href='javascript:showAddressDetails("<s:property value="%{#customer.id}"/>","<s:property value="%{#customer.companyName}"/>","<s:property value="city"/>","<s:property value="state"/>","<s:property value="zipCode"/>","<s:property value="country"/>","<s:property value="%{#customer.corporateName}"/>")' > <s:text name="button.common.addLabel" /> </a> |  <a class="Datalink" 
				    href="javascript:showCustomerUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a>
				 </div>
			  </s:if>	
			  <s:else>
			     <div align="center"> <a class="Datalink" 
					href='javascript:showAddressDetails("<s:property value="%{#customer.id}"/>","<s:property value="%{#customer.companyName}"/>","<s:property value="city"/>","<s:property value="state"/>","<s:property value="zipCode"/>","<s:property value="country"/>","<s:property value="%{#customer.corporateName}"/>")' > <s:text name="button.common.addLabel" /> </a>
				 </div>
			  </s:else> 
			</td>	
			<td width="23%"  ><s:property value="%{#customer.companyName}"/></td>
			<td width="22%"  ><s:property value="addressLine1"/></td>
			<td width="13%" ><s:property value="city"/></td>
			<td width="12%"  ><s:property value="state"/></td>
			<td width="10%" ><s:property value="zipCode"/></td>
			<td width="10%" ><s:property value="country"/></td>
			<td width="10%" ><s:property value="%{#customer.corporateName}"/></td>			
		</tr>	
		</s:iterator>
		</s:iterator>
		
	</tbody>
</table>
</div>