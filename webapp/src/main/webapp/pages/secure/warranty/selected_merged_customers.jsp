<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="common.css"/>
<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dojox.layout.ContentPane");
	dojo.require("twms.widget.DateTextBox");
	dojo.require("twms.widget.Tooltip"); 
	var selectedItems= [];
	var rowCount =<s:property value="mergedCustomerList.size()"/>;
    var validationTooltip;    
    var selectedItemsCount = 0;
    var submitButton; 

    dojo.addOnLoad(function(){
    	selectedItemsCount = selectedItemsCount + selectedItems.length;
    	for (var i=0;i<rowCount;i++){
    		var indexOf =dojo.indexOf(selectedItems, dojo.byId("customer_"+i).value);
    		if (indexOf == -1) {     	
	    		selectedItems.push(dojo.byId("customer_"+i).value);
	    		}
	      }
	      dojo.byId("updateCustomerForm_selectedCustIds").value=selectedItems;
    }) ;
</script>
<div class="policy_section_div" style="width:100%">
<div id="merge_customer_address_list_title" class="section_header"><s:text
	name="label.customer.mergeCustomerAddressList" /></div>
<s:if test="customerType == 'Individual'">
	<table border="0" align="center" cellpadding="0" cellspacing="0" class="grid borderForTable"
		id="individual_Search">
		<thead>
			<tr>
				<th class="warColHeader" align="left" width="30%"><s:text
					name="customer.search.name" /></th>
				<th class="warColHeader" align="left" width="30%"><s:text
					name="customer.search.address" /></th>
				<th class="warColHeader" align="left" width="10%"><s:text
					name="customer.search.city" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.state" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.zip" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.country" /></th>
			</tr>
		</thead>
		<tbody id="individual_customer_list">
			<s:hidden name="selectedMergedCustomerIds"
				id="updateCustomerForm_selectedCustIds" />
			<s:iterator value="mergedCustomerList" id="customer"
				status="customerStatus">
				<input type="hidden"
					name="customer_<s:property value="%{#customerStatus.index}" />"
					value="<s:property value="%{#customer.id}" />"
					id="customer_<s:property value="%{#customerStatus.index}" />" />
				<s:iterator value="addresses" status="addressStatus">
					<tr>
						<s:if test="#addressStatus.index == 0">
							<td width="23%" 
								rowspan="<s:property value="%{addresses.size()}"/>"><s:property
								value="%{#customer.name}" /></td>
						</s:if>
						<td width="20%" ><s:property
							value="%{#mergingCustomer.name}" /></td>
						<td width="30%" ><s:property
							value="addressLine1" /></td>
						<td width="15%" ><s:property
							value="city" /></td>
						<td width="15%" ><s:property
							value="state" /></td>
						<td width="10%" ><s:property
							value="zipCode" /></td>
						<td width="10%" ><s:property
							value="country" /></td>
					</tr>
				</s:iterator>
			</s:iterator>

		</tbody>
	</table>
</s:if>
<s:elseif test="customerType == 'Company'">
	<table border="0" align="center" cellpadding="0" cellspacing="0"
		id="individual_Search" class="grid borderForTable">
		<thead>
			<tr>
				<th class="warColHeader" align="left" width="30%"><s:text
					name="customer.search.company.name" /></th>
				<th class="warColHeader" align="left" width="30%"><s:text
					name="customer.search.address" /></th>
				<th class="warColHeader" align="left" width="10%"><s:text
					name="customer.search.city" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.state" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.zip" /></th>
				<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
					name="customer.search.country" /></th>
			</tr>
		</thead>
		<tbody id="individual_customer_list">
			<s:hidden name="selectedMergedCustomerIds"
				id="updateCustomerForm_selectedCustIds" />
			<s:iterator value="mergedCustomerList" id="customer"
				status="customerStatus">
				<input type="hidden"
					name="customer_<s:property value="%{#customerStatus.index}" />"
					value="<s:property value="%{#customer.id}" />"
					id="customer_<s:property value="%{#customerStatus.index}" />" />
				<s:iterator value="addresses" status="addressStatus">
					<tr>
						<s:if test="#addressStatus.index == 0">
							<td width="30%" 
								rowspan="<s:property value="%{addresses.size()}"/>"><s:property
								value="%{#customer.companyName}" /></td>
						</s:if>
						<td width="30%" ><s:property
							value="addressLine1" /></td>
						<td width="10%" ><s:property
							value="city" /></td>
						<td width="10%" ><s:property
							value="state" /></td>
						<td width="10%" ><s:property
							value="zipCode" /></td>
						<td width="10%" ><s:property
							value="country" /></td>
					</tr>
				</s:iterator>
			</s:iterator>
		</tbody>
	</table>
</s:elseif>
</div>
