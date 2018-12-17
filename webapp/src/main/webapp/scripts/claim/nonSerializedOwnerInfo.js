var customerdlg;
var nonSerializedOwnerDialog;

function showCustomerUpdate(customerId, addressId){
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var customerUpdateUrl = "show_update_customer.action?customer="+customerId +
		"&selectedAddressId=" + addressId ;
	customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	top.publishEvent("/tab/open", {label: "Update Customer", url: customerUpdateUrl, decendentOf: thisTab.title });
	delete customerUpdateUrl;
}


function getMatchingCustomers(index) {
    nonSerializedOwnerDialog.hide();
	var customerName = dojo.byId("name").value;
    var params={};
    if(index){
       params.pageNo=index;
    }
    params.customerStartsWith= dojo.string.escape("xhtml", customerName);
    params.customerType= "Company";
    params.dealerOrganization= dojo.byId("forDealer").value;
    params.selectedBusinessUnit= dojo.byId("forBusinessUnit").value;
    var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	twms.ajax.fireHtmlRequest("get_matching_customers_for_match_read.action",params,function(data) {
			customerSearchResultDiv.destroyDescendants();
			customerSearchResultDiv.setContent(data);	
			delete data, customerSearchResultDiv;
		});
	delete customerName,params;
	customerdlg.show();
}

function ownerInfoDialogCallBack(){
    // [Work Around] : Using dojo.byId seems to return null for some reason.    
    document.getElementById("claim_form").appendChild(nonSerializedOwnerDialog.domNode);
    nonSerializedOwnerDialog.hide();
    dojo.html.hide(dojo.byId("fill_ownerinfo_page"));
    dojo.html.show(dojo.byId("show_ownerinfo_page"));
}

function handleNonSerializedOwner(message) {	 
    
    dojo.byId("chosenAddress").innerHTML = "Owner:" + message.ownerName + "City" + message.ownerCity ; 
    dojo.byId("claimOwnerInformation").value= message.selectedAddressId;    
    dijit.byId("CustomerDialogContent").hide();
    document.getElementById("claim_form").appendChild(nonSerializedOwnerDialog.domNode);
    nonSerializedOwnerDialog.hide();

    dojo.html.hide(dojo.byId("fill_ownerinfo_page"));
    dojo.html.show(dojo.byId("show_ownerinfo_page"));
}

function closeCustomerDialog(){
    customerdlg.hide();
    nonSerializedOwnerDialog.show();
}

//details array represents name,city,state,zipCode,country, addressID respectively by index
function showAddressDetails(details){	
	displayChosenAddress(details[0],details[1],details[2],details[3],details[4]);
    dojo.byId("claimOwnerInformation").value= details[5];        
	customerdlg.hide();
	ownerInfoDialogCallBack();
}

function displayChosenAddress(name,city,state,zipCode,country)
{
	dojo.byId("chosenAddress").innerHTML = '<table class="grid" style="width:25%;margin-top:10px;" cellspacing="0" cellpadding="0"><tr><td class="labelStyle" width="10%" nowrap="nowrap">' 
	+ ownerNameLabel + '</td> <td>' +  name + '</td> </tr> <tr> <td class="labelStyle" nowrap="nowrap">' 
	+ ownerCountryLabel + '</td> <td>' + country + '</td> </tr> <tr> <td class="labelStyle" nowrap="nowrap">' 
	+ ownerStateLabel + '</td> <td>' + state + '</td> </tr> <tr> <td class="labelStyle" nowrap="nowrap">'
	+ ownerCityLabel + '</td> <td>' + city + '</td> </tr> <tr> <td class="labelStyle" nowrap="nowrap">' 
	+ ownerZipLabel + '</td> <td>' + zipCode +'</td></tr></table>'; 
}

dojo.addOnLoad(function() {
			
		   dojo.connect(dojo.byId("fill_ownerinfo_page"), "onclick", function() {
		            		dojo.publish("/nonserializedOwnerInfo/show");
						});	
			dojo.connect(dojo.byId("show_ownerinfo_page"), "onclick", function() {
		            		dojo.publish("/nonserializedOwnerInfo/show");
						}); 
								
			dojo.subscribe("/nonserializedOwnerInfo/show", null, function() {
				var dlg = dijit.byId("nonSerializedOwnerInfo");
				dojo.body().appendChild(dlg.domNode);
		    	dlg.show();    	
		    });
		    		 
		    if(dojo.byId("saveDraftButton")){
		    	 dojo.connect(dojo.byId("saveDraftButton"), "onclick", ownerInfoDialogCallBack);
		    }
		    
		    if(dojo.byId("goToPage1")){
		    	 dojo.connect(dojo.byId("goToPage1"), "onclick", ownerInfoDialogCallBack);
		    }
		    if(isOwnerInfoEntered > 0){
		    	dojo.html.hide(dojo.byId("fill_ownerinfo_page"));
		    	dojo.html.show(dojo.byId("show_ownerinfo_page"));    			    			    	
		    }else{
		    	dojo.html.hide(dojo.byId("show_ownerinfo_page"));
		    	dojo.html.show(dojo.byId("fill_ownerinfo_page"));		    	
		    }
    
		    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
		    customerdlg = dijit.byId("CustomerDialogContent");
		    nonSerializedOwnerDialog = dijit.byId("nonSerializedOwnerInfo");
		    
		    dojo.subscribe("/matchRead/ownerInfo", null, handleNonSerializedOwner);    
		    
});

