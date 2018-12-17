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
function showCustomerUpdate(customerId, addressId){
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
<s:if test="customerType == 'Individual'">
<table border="0" align="center" cellpadding="0" cellspacing="0" id="individual_Search" class="grid borderForTable">
	<thead>
		<tr>
		    <th class="warColHeader" align="left"><s:text name="customer.search.name"/></th>
		     <th class="warColHeader" align="left"><s:text name="customer.search.customerNumber"/></th>
			<th class="warColHeader" nowrap="nowrap" align="left" width="10%">&nbsp;</th>
	   		<th class="warColHeader" align="left" ><s:text name="customer.search.address"/></th>
	   		<th class="warColHeader" align="left"><s:text name="customer.search.city"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" ><s:text name="customer.search.state"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" ><s:text name="customer.search.zip"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" ><s:text name="customer.search.country"/></th>
		</tr>
	</thead>
	<tbody id="individual_customer_list">			
		<s:iterator value="matchingCustomerList" id="customer">
		<s:iterator value="addresses" status="addressStatus">
		<s:hidden name="warrantyId" id="warrantyId" value="warranty.id" ></s:hidden>		
		<tr>
		    <s:if test="#addressStatus.index == 0">
		       <td   rowspan="<s:property value="%{addresses.size()}"/>"><s:property value="%{#customer.name}"/></td>
		    </s:if>
			<td><s:property value="%{#customer.customerId}"/></td>
			<td  nowrap="nowrap"  >
			   <div align="center"><a class="Datalink" 
					    href="javascript:showCustomerUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
			   </div>	
			</td>
			<td><s:property value="addressLine1"/></td>
			<td><s:property value="city"/></td>
			<td><s:property value="state"/></td>
			<td><s:property value="zipCode"/></td>
			<td><s:property value="country"/></td>
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
		    <th class="warColHeader" align="left" ><s:text name="customer.search.company.name"/></th>
		    <th class="warColHeader" align="left"><s:text name="customer.search.customerNumber"/></th>
			<th class="warColHeader" nowrap="nowrap" align="left" >&nbsp;</th>
	   		<th class="warColHeader" align="left" ><s:text name="customer.search.address"/></th>
	   		<th class="warColHeader" align="left" ><s:text name="customer.search.city"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" ><s:text name="customer.search.state"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left" ><s:text name="customer.search.zip"/></th>
	   		<th class="warColHeader" nowrap="nowrap" align="left"><s:text name="customer.search.country"/></th>
		</tr>
	</thead>
	<tbody id="individual_customer_list">	
	<s:if test="matchingCustomerList.size()>0">		
		<s:iterator value="matchingCustomerList" id="customer">
		<s:iterator value="addresses" status="addressStatus">
		<tr>
		    <s:if test="#addressStatus.index == 0">
		       <td   rowspan="<s:property value="%{addresses.size()}"/>"><s:property value="%{#customer.companyName}"/></td>
		    </s:if>
			<td><s:property value="%{#customer.customerId}"/></td>
			<td  nowrap="nowrap"  >
		       <div align="center"><a class="Datalink" 
					    href="javascript:showCustomerUpdate(<s:property value="%{#customer.id}"/>,<s:property value="id"/>)" > Edit </a> 
			   </div>
			</td>	
			<td><s:property value="addressLine1"/></td>
			<td  class="tableDataAmount" ><s:property value="city"/></td>
			<td  class="tableDataAmount" ><s:property value="state"/></td>
			<td><s:property value="zipCode"/></td>
			<td><s:property value="country"/></td>
		</tr>	
		</s:iterator>
		</s:iterator>
		</s:if>
				<s:elseif test ="matchingNAList.size()>0">
		<s:iterator value="matchingNAList" id="NACustomer" status ="custStatus">
		<s:iterator value="orgAddresses" status="addressStatus">
		<tr>
		
		    <s:if test="#addressStatus.index == 0">
		       <td width="23%"  rowspan="<s:property value="%{orgAddresses.size()}"/>"><s:property value="%{#NACustomer.name}"/></td>
		    </s:if>
		    <td><s:property value="%{#NACustomer.serviceProviderNumber}"/></td>
			<td width="10%" nowrap="nowrap"  >
				<script>
			    	addressDetails_<s:property value="#custStatus.index"/>_<s:property value="#addressStatus.index"/> = new Array("<s:property value="%{#NACustomer.name}"/>","<s:property value="city"/>","<s:property value="state"/>","<s:property value="zipCode"/>","<s:property value="country"/>","<s:property value="id"/>");
			    </script>
		
			     <div align="center"> <a class="Datalink"
					href="javascript:showAddressDetails(addressDetails_<s:property value="#custStatus.index"/>_<s:property value="#addressStatus.index"/>)" > <s:text name="button.common.addLabel"/>  </a>
				 </div>
			</td>	
			<td width="22%"  ><s:property value="addressLine1"/></td>
			<td width="13%" class="tableDataAmount" ><s:property value="city"/></td>
			<td width="12%" class="tableDataAmount" ><s:property value="state"/></td>
			<td width="10%" ><s:property value="zipCode"/></td>
			<td width="10%" ><s:property value="country"/></td>
		</tr>	
		</s:iterator>
		

		 </s:iterator>
		</s:elseif>
		
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
							getMatchingCustomers(index);  
						});
					}	 
				});
			</script>
		   </span>	
	   </s:iterator>
	</center>
</div>


