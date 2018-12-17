
function showCustomerUpdate(customerId, addressId){
	dlg.hide();
	if(operatordlg){
	operatordlg.hide();
	}
	if(dojo.isIE){
		setTimeout(function() {_showCustomerUpdate(customerId, addressId);},500);
	}else{
		_showCustomerUpdate(customerId, addressId);
	}
}

function _showCustomerUpdate(customerId, addressId){
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var dealerName="";
    var dealerId="";
    if(dojo.byId("dealerName")){
		dealerName=dojo.byId("dealerName").value;
	}
    if(dojo.byId("dealerId")){
		dealerId=dojo.byId("dealerId").value;
	}
    var customerUpdateUrl = "show_update_customer.action?customer=" + customerId + "&selectedAddressId="
	        + addressId + "&transfer=false"+"&dealerId="+dealerId+"&dealerName="+dealerName;
	customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	parent.publishEvent("/tab/open", {
		label: "Update Customer",
		url: customerUpdateUrl,
		decendentOf: thisTab.title
	});
	
	delete customerUpdateUrl;
}

function copyInstallDate(isOnChange) {		
	var dateValue = dojo.byId("installDate").value;
	if (dojo.byId("checkboxInstallDate").checked) {
		var installationDateList = dojo
				.query("input[id $= 'installationDateForUnit']");
		for ( var i = 0; i < installationDateList.length; i++) {
			if(installationDateList[i].value==dateValue || installationDateList[i].value== "")
			{
			installationDateList[i].value = dateValue;
			dijit.byId(installationDateList[i].id).setValue(dateValue);
			if(installationDateList[i].parentNode.children.length>1)
			{
			installationDateList[i].parentNode.childNodes[1].value = dateValue;
			installationDateList[i].parentNode.childNodes[1].setAttribute("value",dateValue);
			}			
			}
		}
	} else if(!isOnChange) {
		var installationDateList = dojo
				.query("input[id $='installationDateForUnit']");
		for ( var i = 0; i < installationDateList.length; i++) {
			installationDateList[i].value = "";
			dijit.byId(installationDateList[i].id).setValue("");
			if(installationDateList[i].parentNode.children.length>1)
			{
			installationDateList[i].parentNode.childNodes[1].value = "";
			installationDateList[i].parentNode.childNodes[1].setAttribute("value","");
			}			
		}
	}
		var indexList =  dojo.query("input[id $= 'indexFlag']"); 
		var nameList =  dojo.query("input[id $= 'nameFlag']");  
		for(var i=0;i<indexList.length;i++){ 	 	 					
       	getAllPolicies(indexList[i].value,nameList[i].value);
    }		
} 

function getMatchingCustomers(index) {
	var customerName = dojo.byId("name").value;
	var params={
        customerStartsWith: dojo.string.escape("xhtml", customerName)        
    };
    if(dojo.byId("dealerName")){
    	params.dealerName=dojo.byId("dealerName").value;
    }
    if(dojo.byId("dealerId")){
    	params.dealerId=dojo.byId("dealerId").value;
    }
    if(index){
       params.pageNo=index;
    }
    params.customerType = "Company";
    var warrantyId = dojo.byId("warrantyId");
    if(warrantyId) {		
		params.warranty= dojo.string.escape("xhtml", warrantyId.value);
	}
	params.addressBookType = dojo.byId("addressBookType").value;
	var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	twms.ajax.fireHtmlRequest("get_matching_customers.action",params,function(data) {
			customerSearchResultDiv.destroyDescendants();
			customerSearchResultDiv.setContent(data);
			delete data, customerSearchResultDiv;
		});

	delete customerName, params;
	dlg.show();
}

function getMatchingOperators(index) {
	var operatorName = dojo.byId("operatorName").value;
	var params={
        customerStartsWith: dojo.string.escape("xhtml", operatorName)        
    };
    if(dojo.byId("dealerName")){
    	params.dealerName=dojo.byId("dealerName").value;
    }
    if(dojo.byId("dealerId")){
    	params.dealerId=dojo.byId("dealerId").value;
    }
    if(index){
       params.pageNo=index;
    }
    params.customerType = "Company";
    var warrantyId = dojo.byId("warrantyId");
    if(warrantyId) {		
		params.warranty= dojo.string.escape("xhtml", warrantyId.value);
	}
	params.addressBookTypeForOperator = dojo.byId("addressBookTypesForOperator").value;
	var operatorSearchResultTag = dijit.byId("operatorSearchResultTag");
	operatorSearchResultTag.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	twms.ajax.fireHtmlRequest("get_matching_operators.action",params,function(data) {
		operatorSearchResultTag.destroyDescendants();
		operatorSearchResultTag.setContent(data);
			delete data, operatorSearchResultTag;
		});

	delete operatorName, params;
	operatordlg.show();
}



function getMarketInfo(index,name,serialNumber){
	 var id = "inventoryItemMappings"+index;   			
	 var marketInfoPane = dijit.byId(id+"__marketInfo");
	 var marketInfoTable =  document.getElementById(id+"__marketInformationTable");
	 marketInfoPane.destroyDescendants();
	 marketInfoPane.setContent("");	
	 var productArray = ['BUS','TRAILER','TRUCK','APU','MARINE','FACTORY PARTS'];
	 function include(arr, obj) { 
	 	  for(var i=0; i<arr.length; i++) { 
	 	    if (arr[i] == obj){ return true; } else {return false;}
	 	  } 
	 	}
	 var params={serialNumber:serialNumber,"inventoryItemMapping":name,"nListName":name,"nListIndex":index};
	 var url = "getMarketingInformationForInventory.action?";						 
	 twms.ajax.fireHtmlRequest(url, params, function(data){		 
	 marketInfoPane.setContent(data);	 
	 delete data;
	 });  		
}



function getAllMajorComponents(index,name) {
	var id = "inventoryItemMappings" + index;
	var slNo = document.getElementById(id + "__inventoryIemSN").value;
	var params = {
		"inventoryItem" : dojo.string.escape("xhtml", slNo),
		"nListIndex" : index,
		"nListName"	: name,
		"inventoryItemMappingIndex" : index
	};	
	var majorComponentsPane = dijit.byId(id+"__majorComponentDetails");	
	// majorComponentsPane.domNode.innerHTML = "<center>Loading...</center>";
	twms.ajax.fireHtmlRequest("get_major_components.action", params, function(
			data) {			
		majorComponentsPane.setContent(data);
		delete data;
	});

	delete slNo;
}

function showAddressDetailsForOperator(customerId, addressId, organization,warrantyId){
	dlg.hide();
	if(operatordlg){
	operatordlg.hide();
	}
	var params = {
	    operator:  customerId,
        operatorAddress: addressId        
    }
    if(dojo.byId("dealerId")){
        params.forDealer=dojo.byId("dealerId").value;
    }
    if(organization && organization != -1 ) {		
		params.dealerOrganization= organization;
	}
	
	if(warrantyId)
	{
		params.warranty = warrantyId;
	}	
    
    var parentDiv = dojo.byId("operatorDetailsDiv");   
    parentDiv.innerHTML = "Loading...";   

    params.customerType = "Company";

    twms.ajax.fireHtmlRequest("show_operator_details_for_registration.action", params, function(data) {
			parentDiv.innerHTML = data;			
			delete data, parentDiv;
		});
}

function showAddressDetails(customerId, addressId, organization,warrantyId){
	dlg.hide();
	if(operatordlg){
	operatordlg.hide();
	}
	var params = {
	    customer:  customerId,
        customerAddress: addressId        
    }
	
    if(dojo.byId("dealerId")){
        params.forDealer=dojo.byId("dealerId").value;
    }
    if(organization && organization != -1 ) {		
		params.dealerOrganization= organization;
	}
	
	if(warrantyId)
	{
		params.warranty = warrantyId;
	}	
    
    var parentDiv = dojo.byId("customerDetailsDiv");
    var prevCust = '';
	if(dojo.byId("customer"))
		prevCust = dojo.byId("customer").value;
    
    parentDiv.innerHTML = "Loading...";

    params.customerType = "Company";

    twms.ajax.fireHtmlRequest("show_customer_details_for_registration.action", params, function(data) {
			parentDiv.innerHTML = data;
			var newCust = dojo.byId("customer").value;
			if(prevCust != newCust)
				dojo.publish("/registration/customer/changed");
			delete data, parentDiv, prevCust, newCust;
			
			if(0==dojo.byId("industryCodeId").value){
				dijit.byId("industryCode").setValue('-1');
			}else{
				dijit.byId("industryCode").setValue(dojo.byId("industryCodeId").value);
			}
		});
}

dojo.declare("twms.upload.NListAttachmentUploadRenderer", null, {

    _equipment_attchment_row_index : 0,
    _tBody : null,
    _listIndex: 0,
    _markupTemplate: null,
    _scriptTemplate : null,

    constructor : function(/*DomNode (tbody)*/ tBody,
                           /*String (template)*/ template,
                           /*collection of document's id, name and stuff...*/ alreadyUploadedDocList,
                           /*List index number*/ listIndex) {
        this._tBody = tBody;
        this._markupTemplate = template.markup;
        this._scriptTemplate = template.script;
        this._listIndex = listIndex;
        this._addRows(alreadyUploadedDocList);
    },

    requestBatchAddition : function() {
        var showUploadedFiles = dojo.hitch(this, this._addRows);
        var message = {
            callback : function(/*message that the reloaded page(in the dialog) publishes...*/docDataList) {
                showUploadedFiles(docDataList);
            },
        onClose : function() {},
            batchSize : 5
        };
        publishEvent("/uploadDocument/dialog/show_"+this._listIndex, message);
    },

    _addRows : function(/*collection of document's id, name and stuff...*/ dataList) {
        for (var i in dataList) {
            var substitutionMap = {
                id : dataList[i].id,
                name : dataList[i].name,
                user : dataList[i].user,
                share : dataList[i].share,
                inv_type : (dataList[i].inv_type ? dataList[i].inv_type : ""),
                type : (dataList[i].type ? dataList[i].type : ""),
                date : dataList[i].date,
                index : this._equipment_attchment_row_index++
            };
            var rowMarkup = dojo.string.substitute(this._markupTemplate, substitutionMap);
            var rowScript = dojo.string.substitute(this._scriptTemplate,  substitutionMap);
            var row = dojo.html.createNodesFromText(rowMarkup, true);
            dojo.parser.parse(row);
            this._tBody.appendChild(row);
            eval(rowScript);
        }
    }
});