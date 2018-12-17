dojo.subscribe("/multipleInventorySearch/hide", null, function() {
    dijit.byId("searchCriteria").hide();
});

dojo.subscribe("/multipleInventorySearch/show", null, function() {
    dijit.byId("searchCriteria").show();
});

dojo.addOnLoad(function() {

	dojo.connect(dojo.byId("searchInventories"), "onclick", function() {
		var targetContent=dijit.byId('searchResultTag');
        targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
	});
	
	dojo.connect(dojo.byId("endCustomerSelect"), "onclick", function(event) {
		if(event.target.checked){			
			dojo.html.show(dojo.byId("addressBookDiv"));
			dojo.byId("searchCompanyName").readOnly=true;
		}else if(!event.target.checked){			
			dojo.html.hide(dojo.byId("addressBookDiv"));
			dojo.byId("searchCompanyName").value="";
			dojo.byId("searchCompanyName").readOnly=false;
		}
	});
	
	dojo.connect(dojo.byId("inventory_type_stock"), "onclick", function(event) {
		if(event.target.checked){
			dojo.byId("searchCompanyName").value="";
			dojo.byId("ownerId").value=null;
			dojo.byId("endCustomerSelect").checked=false;
			dojo.byId("endCustomerSelect").disabled=true;
			dojo.byId("searchCompanyName").readOnly=true;
			dojo.html.hide(dojo.byId("addressBookDiv"));
		}
	});
	dojo.connect(dojo.byId("inventory_type_retail"), "onclick", function(event) {
		if(event.target.checked){
			dojo.byId("endCustomerSelect").disabled=false;
			dojo.byId("searchCompanyName").readOnly=false;
		}
	});
	
	dojo.connect(dojo.byId("resetSearch"), "onclick", function() {
		dojo.byId("inventory_type_retail").checked=true;
		dojo.byId("searchSerialNumber").value="";
		dojo.byId("searchModelNumber").value="";
		dojo.byId("searchCompanyName").value="";
		dojo.byId("searchCompanyName").readOnly=false;
		dojo.byId("endCustomerSelect").disabled=false;
		dojo.byId("endCustomerSelect").checked=false;	
		dojo.html.hide(dojo.byId("addressBookDiv"));	
	});
	
	dojo.byId("claimTypForSearch").value=dijit.byId("type").getDisplayedValue();
	
    new dojo.io.FormBind({
        formNode: dojo.byId("multiCarSearchform"),
        load: function(data, e) {
            var validationFailed = (data == "<true>");
        	dojo.html.setDisplay(dojo.byId("noSearchParamsErrorSection"), validationFailed);        	        
        	var dataToBeDisplayed = (validationFailed) ? "" : data;        	
			dijit.byId('searchResultTag').setContent(dataToBeDisplayed);								            
        },
        error : function(error) {
        	dojo.body().style.cursor = "auto";
        }
    });    
});


// these changes are for search customer
var customerdlg = dijit.byId("CustomerDialogContent");
dojo.addOnLoad(function(){
	dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
});


function getMatchingCustomers() {    
	var customerName ="";
    var params={};
    params["customerStartsWith"]= dojo.string.escape("xhtml", customerName);
    params["customerType"]= "Company";
    params["dealerOrganization"]= dojo.byId("dealerIdForSearch").value;
    var url = "get_matching_customers_for_multi_claim.action?";
	twms.ajax.fireHtmlRequest(url,params,function(data) {
			dijit.byId("customerSearchResults").setContent(data);				
			delete data, parentDiv;
		});
	delete customerName, url;
	dijit.byId("CustomerDialogContent").show();
}

function showAddressDetails(id,name,city,state,zipCode,country,contactName){
	dijit.byId("CustomerDialogContent").hide();	
	dojo.byId("searchCompanyName").value=name;
	dojo.byId("ownerId").value=id;	
}

function closeCustomerDialog(){
    dijit.byId("CustomerDialogContent").hide();    
}

function showCustomerUpdate(customerId, addressId){
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var customerUpdateUrl = "show_update_customer.action?customer="+customerId +
		"&selectedAddressId=" + addressId + "&matchRead=true";
	customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	top.publishEvent("/tab/open", {label: "Update Customer", url: customerUpdateUrl, decendentOf: thisTab.title });
	delete customerUpdateUrl;
}