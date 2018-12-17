dojo.addOnLoad ( function() {
        doCommonAddOnLoad();
});

function _fetchOtherFieldOnBasedOnSerialNumber(){
    //Do nothing because of the common method we call this to
    //fetch
}

function showAddressDetails(customerId,addressId){
    dlg.hide();
	var params = {
        "warranty.customer":customerId,
        "warranty.customerAddress":addressId
    };
	var indv = dojo.byId("customerType_Individual");

    params.customerType = indv.checked? "Individual" : "Company";

	twms.ajax.fireHtmlRequest("show_customer_details_for_registration.action",params,function(data) {
			var parentDiv = dojo.byId("customerDetailsDiv");
			parentDiv.innerHTML = data;
			getOfferedPolicies();
			delete data, parentDiv;
		});
	delete indv;
}

