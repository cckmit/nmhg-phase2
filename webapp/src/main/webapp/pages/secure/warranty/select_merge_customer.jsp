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
   	 	submitButton  = dojo.byId("selectMergeCustomer"); 
    	validationTooltip = new twms.widget.Tooltip({
	        showDelay: 100,
	        connectId: ["submitContainer2"]
	    });
	    selectedItemsCount = selectedItemsCount + selectedItems.length;
    	for (var i=0;i<rowCount;i++){
    		if(dojo.byId("customer_"+i)){
    			var indexOf = dojo.indexOf(selectedItems, dojo.byId("customer_"+i).value);
    			if (indexOf == -1) {     	
	    			selectedItems.push(dojo.byId("customer_"+i).value);
	    		}
    		}
        }
        if(submitButton){
		    dojo.connect(submitButton, "onclick", function() {
				dojo.byId("selectedCustIds").value=selectedItems;
		    	dojo.body().style.cursor = "wait";
		   	});
   		 }
    }) ;
</script>
<div style="width:99%">
<div id="merge_customer_address_list_title" class="mainTitle" ><s:text
	name="label.customer.mergeCustomerAddressList" /></div>
	<s:if test="customerType == 'Individual'">
		<table class="grid borderForTable" cellpadding="0" cellspacing="0"
			id="individual_Search" style="width:100%;">
			<thead>
				<tr>
					<th class="warColHeader" width="15%" align="left"><s:text
						name="label.customer.defaultCustomer" /></th>
					<th class="warColHeader" align="left" width="15%"><s:text
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
				<s:hidden name="selectedMergedCustomerIds" id="selectedCustIds" />
				<s:iterator value="mergedCustomerList" id="customer"
					status="customerStatus">
					<s:iterator value="addresses" status="addressStatus">
						<tr>
							<td width="10%" align="center" nowrap="nowrap"
								><input type="radio"
								name="mergedCustomer" checked="checked"
								value="<s:property value="%{#customer.id}" />" /> <input
								type="hidden" name="customer"
								value="<s:property value="%{#customer.id}" />"
								id="customer_<s:property value="%{#customerStatus.index}" />" />
							</td>
							<s:if test="#addressStatus.index == 0">
								<td width="23%" 
									rowspan="<s:property value="%{addresses.size()}"/>"><s:property
									value="%{#customer.name}" /></td>
							</s:if>
							<td width="15%" ><s:property
								value="%{#customer.name}" /></td>
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
	</s:if>
	<s:elseif test="customerType == 'Company'">
		<table class="grid borderForTable" cellpadding="0" cellspacing="0"
			id="individual_Search">
			<thead>
				<tr>
					<th class="warColHeader" width="20%" align="left"><s:text
						name="label.customer.defaultCustomer" /></th>
					<th class="warColHeader" align="left" width="22%"><s:text
						name="customer.search.company.name" /></th>
					<th class="warColHeader" align="left" width="23%"><s:text
						name="customer.search.address" /></th>
					<th class="warColHeader" align="left" width="10%"><s:text
						name="customer.search.city" /></th>
					<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
						name="customer.search.state" /></th>
					<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
						name="customer.search.zip" /></th>
					<th class="warColHeader" nowrap="nowrap" align="left" width="5%"><s:text
						name="customer.search.country" /></th>
				</tr>
			</thead>
			<tbody id="individual_customer_list">
				<s:hidden name="selectedMergedCustomerIds" id="selectedCustIds" />
				<s:iterator value="mergedCustomerList" id="customer"
					status="customerStatus">
					<s:iterator value="addresses" status="addressStatus">
						<tr>
							<td width="20%" align="left" nowrap="nowrap"
								><input type="radio"
								name="mergedCustomer" checked="checked"
								value="<s:property value="%{#customer.id}" />" /> <input
								type="hidden" name="customer"
								value="<s:property value="%{#customer.id}" />"
								id="customer_<s:property value="%{#customerStatus.index}" />" />
							</td>
							<s:if test="#addressStatus.index == 0">
								<td width="23%" 
									rowspan="<s:property value="%{addresses.size()}"/>"><s:property
									value="%{#customer.companyName}" /></td>
							</s:if>
							<td width="22%" ><s:property
								value="addressLine1" /></td>
							<td width="10%" ><s:property
								value="city" /></td>
							<td width="10%" ><s:property
								value="state" /></td>
							<td width="10%" ><s:property
								value="zipCode" /></td>
							<td width="5%" ><s:property
								value="country" /></td>
						</tr>
					</s:iterator>
				</s:iterator>
			</tbody>
		</table>
	</s:elseif>
	<s:if test="mergedCustomerList.size > 0">
		<div  id="submitContainer2" class="buttons">
		<s:submit align="center"
			name="selectMergeCustomer" id="selectMergeCustomer" type="input"
			value="%{getText('button.customer.confirmMerge')}"
			action="preview_merge"></s:submit>
		</div>
	</s:if>
</div>

