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
<script type="text/javascript">
function showOperatorUpdate(customerId, addressId){
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var customerUpdateUrl = "show_update_customer.action?customer="+customerId +
		"&selectedAddressId=" + addressId;
	customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	customerUpdateUrl = customerUpdateUrl + "&hideSelect=true";
	top.publishEvent("/tab/open", {label: "Update Customer", url: customerUpdateUrl, decendentOf: thisTab.title });
	delete customerUpdateUrl;
}
</script>
<u:stylePicker fileName="common.css"/>
<s:hidden name="dealerName" id="dealerName"></s:hidden>
<s:if test="customerType == 'Individual'">
<table border="0" align="center" cellpadding="0" cellspacing="0" id="individual_Search" class="grid borderForTable">
	<thead>
		<tr>
		    <th class="warColHeader" align="left" width="15%"><s:text name="customer.search.name"/></th>
			<th class="warColHeader" nowrap="nowrap" align="left" width="10%">&nbsp;</th>
	   		<th class="warColHeader" align="left" width="30%"><s:text name="customer.search.address"/></th>
	   		<th class="warColHeader" align="left" width="13%"><s:text name="customer.search.city"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="12%"><s:text name="customer.search.state"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.zip"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.country"/></th>
		</tr>
	</thead>
	<tbody id="individual_customer_list">			
		<s:iterator value="matchingCustomerList" id="customer">
		<s:iterator value="addresses" status="addressStatus">
		<tr>
		    <s:if test="#addressStatus.index == 0">
		       <td width="23%"  rowspan="<s:property value="%{addresses.size()}"/>"><s:property value="%{#customer.name}"/></td>
		    </s:if>
			<td width="10%" nowrap="nowrap"  >
			<s:if test="warranty.forTransaction.buyer !=null ">
		       <div align="center"> <a class="Datalink" 
						href="javascript:showAddressDetailsForOperator(<s:property value="%{#customer.id}"/>,<s:property value="warranty.forTransaction.buyer.address.id"/>,-1,<s:property value="warranty.id"/>)" > Select </a> |  <a class="Datalink" 
					    href="javascript:showOperatorUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
				</div>
			</s:if>
			<s:else>
			   <div align="center"> <a class="Datalink" 
						href="javascript:showAddressDetailsForOperator(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Select </a> |  <a class="Datalink" 
					    href="javascript:showOperatorUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
			   </div>
			</s:else>	
			</td>
			<td width="15%"  ><s:property value="%{#customer.name}"/></td>
			<td width="30%"  ><s:property value="addressLine1"/></td>
			<td width="13%"  ><s:property value="city"/></td>
			<td width="12%"  ><s:property value="state"/></td>
			<td width="10%" ><s:property value="zipCode"/></td>
			<td width="10%" ><s:property value="country"/></td>
		</tr>
		</s:iterator>
		</s:iterator>
		
	</tbody>
</table>
</s:if>
<s:elseif test="customerType == 'Company'">
<table border="0" align="center" cellpadding="0" cellspacing="0" id="individual_Search" class="grid borderForTable">
	<thead>
		<tr>
		    <th class="warColHeader" align="left" width="22%"><s:text name="customer.search.company.name"/></th>
			<th class="warColHeader" nowrap="nowrap" align="left" width="10%">&nbsp;</th>
	   		<th class="warColHeader" align="left" width="23%"><s:text name="customer.search.address"/></th>
	   		<th class="warColHeader" align="left" width="13%"><s:text name="customer.search.city"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="12%"><s:text name="customer.search.state"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.zip"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text name="customer.search.country"/></th>
		</tr>
	</thead>
	<tbody id="individual_customer_list">			
		<s:iterator value="matchingCustomerList" id="customer">
		<s:iterator value="addresses" status="addressStatus">
		<tr>
		    <s:if test="#addressStatus.index == 0">
		       <td width="23%"  rowspan="<s:property value="%{addresses.size()}"/>"><s:property value="%{#customer.companyName}"/></td>
		    </s:if>
			<td width="10%" nowrap="nowrap"  >
			<s:if test="warranty.forTransaction.buyer!=null">
		       <div align="center"> <a class="Datalink" 
						href="javascript:showAddressDetailsForOperator(<s:property value="%{#customer.id}"/>,<s:property value="id"/>,<s:property value="warranty.forTransaction.buyer"/>,<s:property value="warranty.id"/>)" > Select </a> |  <a class="Datalink" 
					    href="javascript:showOperatorUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
			   </div>
			</s:if>
			<s:else>
			   <div align="center"> <a class="Datalink" 
						href="javascript:showAddressDetailsForOperator(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Select </a> |  <a class="Datalink" 
					    href="javascript:showOperatorUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
			   </div>
			</s:else>   
			</td>	
			<td width="22%"  ><s:property value="addressLine1"/></td>
			<td width="13%" class="tableDataAmount" ><s:property value="city"/></td>
			<td width="12%" class="tableDataAmount" ><s:property value="state"/></td>
			<td width="10%" ><s:property value="zipCode"/></td>
			<td width="10%" ><s:property value="country"/></td>
		</tr>	
		</s:iterator>
		</s:iterator>
		
	</tbody>
</table>

</s:elseif>
<div style="padding: 10px 25px 10px 10px">
	<center>
		<s:iterator value="pageNoList" status="pageCounterCust">
			&nbsp;
			<s:if test="pageNoList[#pageCounterCust.index] == (pageNo + 1)">
				<span id="oper_page_<s:property value="%{#pageCounterCust.index}"/>">
			</s:if>	
			<s:else>
				<span id="oper_page_<s:property value="%{#pageCounterCust.index}"/>" style="cursor:pointer;text-decoration:underline">
			</s:else>
			<s:property value="%{intValue()}" />
			<script type="text/javascript">
				dojo.addOnLoad(function(){
					var index = '<s:property value="%{#pageCounterCust.index}"/>';
					var pageNo='<s:property value="pageNo"/>';	
					if(index!=pageNo){
						dojo.connect(dojo.byId("oper_page_"+index),"onclick",function(){
							getMatchingOperators(index); 
						});
					}	 
				});
			</script>
		   </span>	
	   </s:iterator>
	</center>
</div>
