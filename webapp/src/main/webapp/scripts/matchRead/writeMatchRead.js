dojo.require("twms.widget.ValidationTextBox");
dojo.require("twms.widget.Tooltip");

var customerdlg;
var matchReadOwnerDialog;
var validationTooltip;
var invalidFields = [];

function addInvalidField(/* id */ fieldId) {
    if (dojo.indexOf(invalidFields, fieldId) == -1) {
        invalidFields.push(fieldId);
    }   
}

function removeInvalidField(/* id */ fieldId) {			
    var indexOfId = dojo.indexOf(invalidFields, fieldId);
    if (indexOfId >= 0) {
        invalidFields.splice(indexOfId, 1);
    }
}

function validateSubmission() { 
    if(invalidFields.length > 0) {
   		dojo.byId("closePopup").setAttribute("disabled", "disabled");
   		if(validationTooltip) {
        	validationTooltip.setLabel("Please enter all the fields correctly");
        }        
    } else {
        dojo.byId("closePopup").removeAttribute("disabled");
        if(validationTooltip) {
        	validationTooltip.clearLabel();
        }
    }
}

function showAddressDetails(details){
	var name = details[0];
	var city = details[1];
	var state = details[2];
	var zipCode = details[3];
	var country = details[4];
	customerdlg.hide();
	var ownerName= dojo.byId("ownerName");
	ownerName.value=name;
	var countryId=dijit.byId("matchReadInfo_country");
	countryId.setValue(country);
	var validatableStateId = dijit.byId("validatable_matchReadInfo_state");
	var freeTextStateId = dojo.byId("free_text_matchReadInfo_state");
	var validatableCityId = dijit.byId("validatable_matchReadInfo_city");
	var freeTextCityId = dojo.byId("free_text_matchReadInfo_city");
	var validatableZipId = dijit.byId("validatable_matchReadInfo_zip");
	var freeTextZipId = dojo.byId("free_text_matchReadInfo_zip");	
    if(country == 'US'){
       validatableStateId.initialValue =state;
       validatableCityId.initialValue = city;       
       validatableZipId.initialValue = zipCode;
       
       validatableStateId.setValue(state);             
                     
    }
    else{
       freeTextStateId.value = state;       
       freeTextCityId.value = city;
       freeTextZipId.value = zipCode;
    }    
	ownerInfoDialogCallBack();
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

function getMatchingCustomers(index) {
    matchReadOwnerDialog.hide();
	var customerName = dojo.byId("name").value;
    var params={};
    if(index){
       params.pageNo=index;
    }
    params.customerStartsWith= dojo.string.escape("xhtml", customerName);
    params.customerType= "Company";
    params.dealerOrganization= dojo.byId("forDealer").value;
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
    document.getElementById("claim_form").appendChild(matchReadOwnerDialog.domNode);
    matchReadOwnerDialog.hide();
    dojo.html.hide(dojo.byId("show_matchRead_page"));
    dojo.html.show(dojo.byId("show_owner_page"));
}
	
function closeCustomerDialog(){
    customerdlg.hide();
    matchReadOwnerDialog.show();
}	

function handleMatchReadOwner(message) {	 
    dojo.byId("ownerName").value= message.ownerName;
    dojo.byId("ownerCity").value= message.ownerCity;
    dojo.byId("ownerState").value= message.ownerState;
    dojo.byId("ownerZipcode").value= message.ownerState;
    dojo.byId("ownerCountry").value= message.ownerCountry;

    dijit.byId("CustomerDialogContent").hide();
    document.getElementById("claim_form").appendChild(matchReadOwnerDialog.domNode);
    matchReadOwnerDialog.hide();

    dojo.html.hide(dojo.byId("show_matchRead_page"));
    dojo.html.show(dojo.byId("show_owner_page"));
}

var _lastSeenCountry, _lastSeenState, _lastSeenCity;

function getStatesByCountry(value, validatableStateId,freeTextIds, validatableIds){
    if(!dojo.string.isBlank(value) && _lastSeenCountry != value) {
    	_lastSeenCountry = value;         
			twms.ajax.fireJsonRequest("get_states_by_country.action",{
                countryCode: value
            },function(data) {
					var statesAvailable = data.items.length > 0;
					if(statesAvailable){
					    var topicName = "topic_matchReadInfo_state"; 
						dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
                        }]);
					}
					dojo.forEach(freeTextIds, function(freeTextId){
					   dojo.html.setDisplay(dojo.byId(freeTextId), !statesAvailable);
					 }
					);
					dojo.html.setDisplay(validatableStateId.domNode, statesAvailable);
					dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, statesAvailable);
					   }
					);
			    	delete data;
				});
      }		
}

function getCitiesByCountryAndState(value, countryId, validatableCityId, freeTextIds, validatableIds){
	
	var country = dijit.byId(countryId).getValue();
	if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || value != _lastSeenState)) {	
        _lastSeenCountry = country;
        _lastSeenState = value;
            var params={};
            params.countryCode=country;
            params.stateCode=value;
            twms.ajax.fireJsonRequest("get_cities_by_country_and_state.action",params,function(data) {
						var citiesAvailable = data.items.length > 0;						
					    if(citiesAvailable){
						    var topicName = "topic_matchReadInfo_city"; 
							dojo.publish(topicName, [{
								items: data,
                                makeLocal: true
							}]);  
					    }
					   
					   dojo.forEach(freeTextIds, function(freeTextId){
					      dojo.html.setDisplay(dojo.byId(freeTextId), !citiesAvailable);
					     }
					   );
					   
					  dojo.html.setDisplay(validatableCityId.domNode, citiesAvailable);
					  dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, citiesAvailable);
					    }
					 );
					 
				 delete data;
				});
      }		
}

function getZipsByCountryStateAndCity(value, countryId, stateId, freeTextIds, validatableIds){ 
    var country = dijit.byId(countryId).getValue();
    var state = dijit.byId(stateId).getValue();

    if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || state != _lastSeenState ||
                                       value != _lastSeenCity)) {
        _lastSeenCountry = country;
        _lastSeenState = state;
        _lastSeenCity = value;
        
            var params={};
            params.countryCode=country;
            params.stateCode=state;
            params.cityCode=value;
            twms.ajax.fireJsonRequest("get_zips_by_country_state_and_city.action",params,function(data) {
					var zipsAvailable = data.items.length > 0;
					if(zipsAvailable){
					     var topicName = "topic_matchReadInfo_zip"; 
					     dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						 }]);  
					 }
					dojo.forEach(freeTextIds, function(freeTextId){
					   dojo.html.setDisplay(dojo.byId(freeTextId), !zipsAvailable);
					 }
					);
				    dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, zipsAvailable);
					    }
					 );
				 delete data;
				});
			delete url;
      }		
}

function getCountiesByCountryStateAndZip(value, countryId, stateId, freeTextIds, validatableIds){ 
    var country = dijit.byId(countryId).getValue();
    var state = dijit.byId(stateId).getValue();

    if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || state != _lastSeenState ||
                                       value != _lastSeenZip)) {
        _lastSeenCountry = country;
        _lastSeenState = state;
        _lastSeenZip = value;
        
            var params={};
            params.countryCode=country;
            params.stateCode=state;
            params.zipCode=value;
            twms.ajax.fireJsonRequest("get_counties_by_country_state_and_zip.action",params,function(data) {
					var countyAvailable = data.items.length > 0;
					if(countyAvailable){
					     var topicName = "topic_matchReadInfo_county"; 
					     dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						 }]);  
					 }
					dojo.forEach(freeTextIds, function(freeTextId){
					   dojo.html.setDisplay(dojo.byId(freeTextId), !countyAvailable);
					 }
					);
				    dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, countyAvailable);
					    }
					 );
				 delete data;
				});
			delete url;
      }		
} 

dojo.subscribe("/validationTextbox/validationDone", null, function(message) {
	var elementId = message.source.id;    	
	message.validationFailed ? addInvalidField(elementId) : removeInvalidField(elementId);
	validateSubmission();
});
    
dojo.addOnLoad(function() {
	dojo.connect(dojo.byId("show_matchRead_page"), "onclick", function() {
            		dojo.publish("/matchReadInfo/show");
				});	
	dojo.connect(dojo.byId("show_owner_page"), "onclick", function() {
            		dojo.publish("/matchReadInfo/show");
				}); 		
	dojo.subscribe("/matchReadInfo/show", null, function() {
		var dlg = dijit.byId("ownerInfoMR");
		dojo.body().appendChild(dlg.domNode);
    	dlg.show();    	
    });    

    dojo.forEach(["closePopup", "validateButton"], function(elementId) {
        dojo.connect(dojo.byId(elementId), "onclick", ownerInfoDialogCallBack);
    });
    
    if(dojo.byId("saveDraftButton")){
    	 dojo.connect(dojo.byId("saveDraftButton"), "onclick", ownerInfoDialogCallBack);
    }
    
    if(dojo.byId("goToPage1")){
    	 dojo.connect(dojo.byId("goToPage1"), "onclick", ownerInfoDialogCallBack);
    }

	validationTooltip = new twms.widget.Tooltip({
        showDelay: 100,
        connectId: [dojo.byId("submitSection")]
    });    
    
    if(ismatchReadNull == ""){
    	dojo.html.hide(dojo.byId("show_owner_page"));
    	dojo.html.show(dojo.byId("show_matchRead_page"));
    }else{
    	dojo.html.hide(dojo.byId("show_matchRead_page"));
    	dojo.html.show(dojo.byId("show_owner_page"));    	
    }
    
    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
    customerdlg = dijit.byId("CustomerDialogContent");
    matchReadOwnerDialog = dijit.byId("ownerInfoMR");
    
    dojo.subscribe("/matchRead/ownerInfo", null, handleMatchReadOwner);   
});

