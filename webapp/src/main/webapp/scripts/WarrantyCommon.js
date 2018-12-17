 function doCommonAddOnLoad() {
    var cbox = dijit.byId('comboSNo');

    if (cbox) {
        dojo.connect(cbox, "onChange","_fetchOtherFieldOnBasedOnSerialNumber");
        delete cbox;
    }
    else {
        //Reload the page we will have serialNumber embedded in page get the attributest for the serialnumber
         var serialNumber = dojo.byId("serialNumberHidden").value;
         if(serialNumber){
                     _fetchOtherFieldOnBasedOnSerialNumber(serialNumber);
         }
     }

        dojo.connect(dijit.byId("deliveryDate"), "onChange", "changeWarrantyStDtToDelivary");
        dojo.connect(dijit.byId("hoursOnMachine"), "onChange", "hoursOnMachineChanged");
        dojo.connect(dijit.byId("mileage"), "onChange", "getOfferedPolicies");
        dojo.connect(dijit.byId("salesMan"), "onChange", "getOfferedPolicies");
        dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
        dlg = dijit.byId("DialogContent");

}

function createCustomer(){
	    dlg.hide();
        //IE 7&8 HACK:
        //Those stupid guys seems to have a problem with the Dialog hiding, before it hides the Dialog its called the open tab method??
        //is called and that is making the tab hiding impossible, new tab is already here, Dialog seems to be hide to where situation?
        //Any way for the time being it seems to be working
        //PS: 500 milli second seems to be the standard time for every IE hack :)
        if(dojo.isIE){
            setTimeout(function() {_createCustomer();},500);
        }else{
            _createCustomer();
        }
    }

function _createCustomer(){
        var serialNumber,company;
        if (dijit.byId("comboSNo")) {
           serialNumber = dijit.byId("comboSNo").getValue();
        }
        else {
           serialNumber = dojo.byId("serialNumberHidden").value;
        }
        company =dojo.byId("customerType_Company").checked
        var thisTabLabel = getMyTabLabel();
        parent.publishEvent("/tab/open", {
                  label: "Create Customer",
                  url: "show_customer.action?serialNumber="+serialNumber+"&company="+company,
                  decendentOf: thisTabLabel,
                  forceNewTab: true,
                  helpCategory: "Inventory/Warranty_Registration.htm"
                  });
}

function _enableSubmit(enable) {
	var submitBtn = dojo.byId("submit_btn");
	submitBtn.disabled = !enable;
	delete submitBtn;
}


function showCustomerUpdate(customerId, addressId){
	dlg.hide();
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var indv = dojo.byId("customerType_Individual");
	var customerUpdateUrl = "show_update_customer.action?customer="+customerId+
		"&selectedAddressId=" + addressId + "&transfer=false";
	if(indv.checked){
		customerUpdateUrl = customerUpdateUrl + "&customerType=Individual";
	} else {
		customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	}
	parent.publishEvent("/tab/open", {label: "Update Customer", url: customerUpdateUrl, decendentOf: thisTab.title});

	delete customerUpdateUrl, indv;

}

 function getMatchingCustomers() {
    var customerName = dojo.byId("name").value;
    var address = dojo.byId("add").value;
    var city = dojo.byId("city").value;
    var state = dojo.byId("state").value;
    var zip = dojo.byId("zip").value;

    var params={
        customerStartsWith: dojo.string.escape("xhtml", customerName),
        addressStartsWith: dojo.string.escape("xhtml", address),
        cityStartsWith: dojo.string.escape("xhtml", city),
        stateStartsWith: dojo.string.escape("xhtml", state),
        zipStartsWith: dojo.string.escape("xhtml", zip),
        serialNumber:(dijit.byId("comboSNo")==null)?dojo.byId("serialNumberHidden").value:dijit.byId("comboSNo").getValue()
        };
	var indv = dojo.byId("customerType_Individual");
    params.customerType=indv.checked?"Individual":"Company";
    var parentDiv = dojo.byId("customerSearchResults");

    if(dijit.byId("comboSNo")==null||!dijit.byId("comboSNo").isValid() || dijit.byId("comboSNo").getDisplayedValue()==""){
        //PS I could have made it one line but if later some alteration needed it will be classifying into comboSNo and serialNumber
        if(dojo.byId("serialNumber")==null||dojo.byId("serialNumber").innerHTML == ""){
            if(dojo.byId("serialNumberHidden")==null || dojo.byId("serialNumberHidden").value==""){
                dojo.byId("addressError").innerHTML="Please Select a Serial Number";
                eraseErrorMessage();
                return;
            }
        }
    }

    twms.ajax.fireHtmlRequest("get_matching_customers.action",params,function(data) {
			parentDiv.innerHTML = data;
			_setupMasterCheckbox(parentDiv);
			delete data, parentDiv;
		});

	delete customerName, indv;
	dlg.show();
}

 function eraseErrorMessage(){
     setTimeout(function() {dojo.query(".erasableErrorMessage").wipeOut().play();},500);
 }
// this adds a delay.
function hoursOnMachineChanged() {
	if (spinnerTimerId != null) {
		clearTimeout(spinnerTimerId);
	}
	spinnerTimerId = setTimeout(getOfferedPolicies, 250);
}

function _setupMasterCheckbox(parentDiv) {

	multiCheckBoxControl = null;
    if (parentDiv == null) {
        parentDiv = dojo.byId("policy_list_div");
    }
    if (parentDiv == null) {
        return;
    }
    var inputs = parentDiv.getElementsByTagName("input");
	for(i = 0; i < inputs.length; i++) {
        var input = inputs[i];
		if (input.type == "checkbox"){
        	if(input.id == "masterCheckBox"){
				multiCheckBoxControl = new CheckBoxListControl(input);
			} else if ( multiCheckBoxControl != null ) {
				multiCheckBoxControl.addListElement(input);
			}
        }
        delete input;
	}
    delete inputs, parentDiv;
}