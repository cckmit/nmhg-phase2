<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<u:stylePicker fileName="common.css"/>
<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dojox.layout.ContentPane");
	dojo.require("twms.widget.DateTextBox");
	dojo.require("twms.widget.Tooltip"); 
	var selectedItems= [];
    var rowCount =<s:property value = "size" />;     
    var idsInString='<s:property value="selectedItemsIds"/>'; 
    idsInString=idsInString.replace(/\s+/g,'');
    if(idsInString!=null && !idsInString==""){
	    selectedItems=idsInString.split(",");
    } 
    var validationTooltip;    
    var selectedItemsCount = 0;
    var submitButton; 
    function validateSubmission() {    	  
	   var enableSubmission = true;
	   var message;
	   if(selectedItemsCount==0) {
	       enableSubmission = false;
	       message = "Please select at least one Customer.";
	   }
	   if(enableSubmission) {
	   	   submitButton.disabled=false;
	       validationTooltip.clearLabel();
	   } else {
	       	 submitButton.disabled=true;
	         validationTooltip.setLabel(message);
	   }
	}
    dojo.addOnLoad(function(){
    	submitButton  = dojo.byId("addToMergeList"); 
    	 validationTooltip = new twms.widget.Tooltip({
	        showDelay: 100,
	        connectId: ["submitContainer"]
	    });
	    selectedItemsCount = selectedItemsCount + selectedItems.length;
	    validateSubmission();

    	dojo.connect(dojo.byId("masterCheckbox"),"onclick",function(){
    	if(dojo.byId("masterCheckbox").checked){
            selectedItemsCount = 0;
            for (var i=0;i<rowCount;i++){
                var currentElement = dojo.byId(""+i);
                if(currentElement.disabled==false){
                	currentElement.checked=true;
                	selectedItemsCount++;
                }
                var indexOf =dojo.indexOf(selectedItems, currentElement.value);		
				if (indexOf == -1) {
					selectedItems.push(currentElement.value);	
				}
            }
    	} else {
    	    selectedItemsCount = rowCount;
    		for (var j=0;j<rowCount;j++){
    			dojo.byId(""+j).checked=false;
    			selectedItemsCount--;
    		}
    		selectedItems=[];
    	}    	
    	validateSubmission();
    	});
    	setSelectedInventories();
    	for (var i=0;i<rowCount;i++){     	
    	dojo.connect(dojo.byId(""+i), "onchange", function(event) {
    	    var indexOfItem;
            var targetElement = event.target;
            if(targetElement.checked) {
                selectedItemsCount++;
				indexOfItem=dojo.indexOf(selectedItems,targetElement.value);
				if (indexOfItem == -1) {
					selectedItems.push(targetElement.value);
				}
            } else {
				indexOfItem =dojo.indexOf(selectedItems,targetElement.value);
				if (indexOfItem >= 0) {
				    selectedItemsCount--;
					selectedItems.splice(indexOfItem, 1);
				}
            }
            validateSubmission();
        });
        }
    }) ;
	function setSelectedInventories(){
		 for (var i=0; i< rowCount; i++) {
	        var currentElement = dojo.byId(""+i);
	        if(currentElement){
		        var indexOf=dojo.indexOf(selectedItems,currentElement.value);
				if(indexOf >= 0 && currentElement.disabled==false){
					currentElement.checked=true;
				}
			}
		 }
	}
</script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script type="text/javascript"
	src="scripts/multiCustomerPicker/multipleCustomerSelection.js"></script>

	<s:hidden name="dealerName" id="dealerName"></s:hidden> 
	<input type="hidden" name="size" value="<s:property value="size"/>" />
	<input type="hidden" name="pageNo" value="<s:property value="pageNo"/>" />
	<s:if test="customerType == 'Individual'">
		<table  cellpadding="0" cellspacing="0" class="grid borderForTable" id="individual_Search">
			<thead>
				<tr>
					<th class="warColHeader" width="5%" align="center" style="padding:0;margin:0"><input
						type="checkbox" id="masterCheckbox" style="border:none;"/></th>
					<th class="warColHeader" align="left" width="20%"><s:text
						name="customer.search.name" /></th>
					<th class="warColHeader" align="left" width="30%"><s:text
						name="customer.search.address" /></th>
					<th class="warColHeader" align="left" width="13%"><s:text
						name="customer.search.city" /></th>
					<th class="warColHeader" nowrap="nowrap"  width="12%"><s:text
						name="customer.search.state" /></th>
					<th class="warColHeader" nowrap="nowrap" align="center" width="10%"style="padding:0;margin:0"><s:text
						name="customer.search.zip" /></th>
					<th class="warColHeader" nowrap="nowrap" width="10%"><s:text
						name="customer.search.country" /></th>
				</tr>
			</thead>
			<tbody id="individual_customer_list">
				<s:hidden name="selectedItemsIds" id="selectedIds" />
				<s:iterator value="matchingCustomerList" id="customer"
					status="customerStatus">
					<s:iterator value="addresses" status="addressStatus">
						<tr>
							<td width="5%" align="center" nowrap="nowrap"
								 style="padding:0;margin:0" ><input type="checkbox"
								name="customer" value="<s:property value="%{#customer.id}" />"
								id="<s:property value="%{#customerStatus.index}"  />" style="border:none;"  /></td>
							<s:if test="#addressStatus.index == 0">
								<td width="23%" 
									rowspan="<s:property value="%{addresses.size()}"/>"><s:property
									value="%{#customer.name}" /></td>
							</s:if>
							<td width="20%" ><s:property
								value="%{#customer.name}" /></td>
							<td width="30%" ><s:property
								value="addressLine1" /></td>
							<td width="13%" ><s:property
								value="city" /></td>
							<td width="12%" ><s:property
								value="state" /></td>
							<td width="10%"  align="center" style="padding:0;margin:0"><s:property
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
		<table class="grid borderForTable" cellpadding="0" cellspacing="0" id="individual_Search">
			<thead>
				<tr>
					<th class="warColHeader" width="5%" align="center" style="padding:0;margin:0"><input
						type="checkbox" id="masterCheckbox" style="border:none" /></th>
					<th class="warColHeader" align="left" width="27%"><s:text
						name="customer.search.company.name" /></th>
					<th class="warColHeader" align="left" width="23%"><s:text
						name="customer.search.address" /></th>
					<th class="warColHeader" align="left" width="13%"><s:text
						name="customer.search.city" /></th>
					<th class="warColHeader" nowrap="nowrap" align="left" width="12%"><s:text
						name="customer.search.state" /></th>
					<th class="warColHeader" nowrap="nowrap" align="center" width="10%" style="padding:0;margin:0;text-align:center"><s:text
						name="customer.search.zip" /></th>
					<th class="warColHeader" nowrap="nowrap" align="left" width="10%"><s:text
						name="customer.search.country" /></th>
				</tr>
			</thead>
			<tbody id="individual_customer_list">
				<s:hidden name="selectedItemsIds" id="selectedIds" />
				<s:iterator value="matchingCustomerList" id="customer"
					status="customerStatus">
					<s:iterator value="addresses" status="addressStatus">
						<tr>
							<td width="5%" align="center" nowrap="nowrap"
								 style="padding:0;margin:0"><input type="checkbox"
								name="customer" value="<s:property value="%{#customer.id}" />"
								id="<s:property value="%{#customerStatus.index}" />" style="border:none"  /></td>
							<s:if test="#addressStatus.index == 0">
								<td width="23%" 
									rowspan="<s:property value="%{addresses.size()}"/>"><s:property
									value="%{#customer.companyName}" /></td>
							</s:if>
							<td width="27%" ><s:property
								value="addressLine1" /></td>
							<td width="13%" ><s:property
								value="city" /></td>
							<td width="12%" ><s:property
								value="state" /></td>
							<td width="10%"  align="center" style="padding:0;margin:0;text-align:center"><s:property
								value="zipCode" /></td>
							<td width="10%" ><s:property
								value="country" /></td>
						</tr>
					</s:iterator>
				</s:iterator>
			</tbody>
		</table>
	</s:elseif>
	<div>
	<center><s:iterator value="pageNoList" status="pageCounter">
			&nbsp;
			<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
			<span id="page_<s:property value="%{#pageCounter.index}"/>">
		</s:if>
		<s:else>
			<span id="page_<s:property value="%{#pageCounter.index}"/>"
				style="cursor: pointer; text-decoration: underline">
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
	</s:iterator></center>
	</div>
	<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
	<div style="margin-top:10px;background:#FFF">
	<center class="buttons"> <s:submit align="center"
		name="addToMergeList" id="addToMergeList" type="input"
		value="%{getText('label.customer.addToMergeList')}" disabled="true"
		action="select_merge_customer"></s:submit></center>
	</div>
	</authz:ifNotPermitted>
