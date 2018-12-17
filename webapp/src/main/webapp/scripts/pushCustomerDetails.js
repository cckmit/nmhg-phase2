function showNewCustomerDetails(customerId, addressId,actionName){
	var hideSelect = dojo.byId('hideSelect').value;
	if(hideSelect == 'false') {
		var parentTabName = getTabDetailsForIframe().folderName;
		var tab = getTabHavingLabel(parentTabName);
		var frame = dojo.query("iframe", tab.domNode)[0];
		var parentById = top.window.frames[frame.name].myById;
		// One of the 2 pairs of parameters are used
		var params = {
            customer:customerId,
            customerAddress:addressId,
		    "warranty.customer":customerId,
            "warranty.customerAddress":addressId
        };
        params.customerType = "Company";
		twms.ajax.fireHtmlRequest(actionName+".action",params,function(data) {
				var parentDiv = parentById("customerDetailsDiv");
				parentDiv.innerHTML = data;
				closeTheTab();		
				delete data, parentDiv;
			}
        );
	}
}

function closeTheTab() {
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	closeTab(thisTab);		
}


function showNewCustomerDetailsForMatchRead(name,city,state,zipCode,country,isMSACountry,selectedAddressId){
	var hideSelect = dojo.byId('hideSelect').value;
	if(hideSelect == 'false') {
		publishOnParentTab("/matchRead/ownerInfo", {
			ownerName : name,
			ownerCity : city,
			ownerState : state,
			ownerZipCode : zipCode,
			ownerCountry : country,
			isMSACountry : isMSACountry,
			selectedAddressId : selectedAddressId
		});
	    closeTheTab();
	}
}