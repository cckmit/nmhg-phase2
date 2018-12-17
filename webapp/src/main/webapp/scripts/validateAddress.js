var _lastSeenCountry, _lastSeenState, _lastSeenCity, _lastSeenZip;

function getStatesByCountry(value, validatableStateId,isNew, addressIndex,freeTextIds, validatableIds){
    if(!dojo.string.isBlank(value) && _lastSeenCountry != value) {
    	_lastSeenCountry = value;
    
        twms.ajax.fireJsonRequest("get_states_by_country.action",{
            countryCode:value
        },function(data) {
                    var statesAvailable = data.items.length > 0;
					if(statesAvailable){
					    var topicName = "topic_state_"; 
                        topicName = prepareText(topicName,isNew,addressIndex);
                        dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						}]);  
					}
                    dojo.forEach(freeTextIds, function(freeTextId){
                        var el = dojo.byId(freeTextId);
                        if(el) {
                            dojo.html.setDisplay(el, !statesAvailable);
                        }
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

function getCitiesByCountryAndState(value, validatableCityId, isNew, addressIndex, freeTextIds, validatableIds){
    var countryId = "country_";
	countryId= prepareText(countryId,isNew,addressIndex);

    var country = dijit.byId(countryId).getValue();

    if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || value != _lastSeenState)) {
        _lastSeenCountry = country;
        _lastSeenState = value;
        
        twms.ajax.fireJsonRequest("get_cities_by_country_and_state.action",{
            countryCode: _lastSeenCountry,
            stateCode: _lastSeenState
        },function(data) {
                    var citiesAvailable = data.items.length > 0;
					if(citiesAvailable){
					    var topicName = "topic_city_"; 
	                    topicName = prepareText(topicName,isNew,addressIndex);
                        dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						}]);  
					 }
					dojo.forEach(freeTextIds, function(freeTextId){
                        var el = dojo.byId(freeTextId);
                        if(el) {
                            dojo.html.setDisplay(el, !citiesAvailable);
                        }
                     }
					);
					dojo.html.setDisplay(validatableCityId.domNode, citiesAvailable);
					dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, citiesAvailable);
					   }
					);
				 delete data;
				});
			delete url;
      }		
}

function getZipsByCountryStateAndCity(value, validatableZipId, isNew, addressIndex, freeTextIds, validatableIds){
    var countryId = "country_";
     countryId= prepareText(countryId,isNew,addressIndex);
     var stateId = "validatable_state_"
	 stateId=prepareText(stateId,isNew,addressIndex);

    var country = dijit.byId(countryId).getValue();
    var state = dijit.byId(stateId).getValue();

    if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || state != _lastSeenState ||
                                       value != _lastSeenCity)) {
        _lastSeenCountry = country;
        _lastSeenState = state;
        _lastSeenCity = value;

        twms.ajax.fireJsonRequest("get_zips_by_country_state_and_city.action",{
            countryCode: _lastSeenCountry,
            stateCode: _lastSeenState,
            cityCode: _lastSeenCity
        },function(data) {
					var zipsAvailable = data.items.length > 0;
					if(zipsAvailable){
					    var topicName = "topic_zip_"; 
	                    topicName = prepareText(topicName,isNew,addressIndex);
                        dojo.publish(topicName, [{
							items: data,
                            makeLocal: true
						}]);  
					 }
					dojo.forEach(freeTextIds, function(freeTextId){
                        var el = dojo.byId(freeTextId);
                        if(el) {
                            dojo.html.setDisplay(el, !zipsAvailable);
                        }
                     }
					);
					dojo.html.setDisplay(validatableZipId.domNode, zipsAvailable);
					dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, zipsAvailable);
					   }
					);
				 delete data;
				});
			delete url;
      }		
}

function getCountiesByCountryStateAndZip(value, validatableCountyId, isNew, addressIndex, freeTextIds, validatableIds) {
	var countryId = "country_";
    countryId = prepareText(countryId,isNew,addressIndex);
    var stateId = "validatable_state_"
	 stateId = prepareText(stateId,isNew,addressIndex);

   var country = dijit.byId(countryId).getValue();
   var state = dijit.byId(stateId).getValue();
    
   if(!dojo.string.isBlank(value) && (country != _lastSeenCountry || state != _lastSeenState || /*city != _lastSeenCity ||*/
                                      value != _lastSeenZip)) {
       _lastSeenCountry = country;
       _lastSeenState = state;
       _lastSeenZip = value;

       twms.ajax.fireJsonRequest("get_counties_by_country_state_and_zip.action",{
           countryCode: _lastSeenCountry,
           stateCode: _lastSeenState,
           zipsCode: _lastSeenZip
       },function(data) {
					var countyAvailable = data.items.length > 0;
					if(countyAvailable){
					    var topicName = "topic_county_"; 
	                    topicName = prepareText(topicName,isNew,addressIndex);
                       dojo.publish(topicName, [{
							items: data,
                           makeLocal: true
						}]);  
					 }
					dojo.forEach(freeTextIds, function(freeTextId){
                       var el = dojo.byId(freeTextId);
                       if(el) {
                           dojo.html.setDisplay(el, !countyAvailable);
                       }
                    }
					);
					dojo.html.setDisplay(validatableCountyId.domNode, countyAvailable);
					dojo.forEach(validatableIds, function(validatableId){
					    dojo.html.setDisplay(dijit.byId(validatableId).domNode, countyAvailable);
					   }
					);
				 delete data;
				});
			delete url;
     }		
}

function prepareText(prefix, isNew, addressIndex){
        prefix = prefix + "company_";
        if(isNew){
          prefix=prefix+"new";
        }
        else{
          prefix=prefix + addressIndex;
        }
        
     return prefix;   
}

function moveSelectedOptionsFromAvailable(avail, selected) {
	copySelectedOptions(avail, selected, false, true);
	removeSelectedOptions(avail);
	for ( var i = 0; i < selected.options.length; i++) {
			if (selected.options[i].value == 'technician') {
			dojo.html.show(dojo.byId("technician_details"));
			if(dojo.byId("IsUserAnAdmin").value=="Admin"){
			dojo.html.show(dojo.byId("technician_certification_details"));
			//dojo.html.show(dojo.byId("technician_company_details"));
			//dojo.html.show(dojo.byId("userRoleIsDealerTechnician"));
			//dojo.html.hide(dojo.byId("userRoleIsNotDealerTechnician"));
			}
			break;
		}
	}
}
function moveSelectedOptionsFromSelected(avail, selected, roles) {
	copySelectedOptions(selected, avail, false, true);
	removeSelectedOptions(selected);
	for ( var i = 0; i < avail.options.length; i++) {
		if (avail.options[i].value == 'technician') {
			dojo.html.hide(dojo.byId("technician_details"));
			dojo.html.hide(dojo.byId("technician_certification_details"));
		//	dojo.html.hide(dojo.byId("technician_company_details"))
			//dojo.html.hide(dojo.byId("userRoleIsDealerTechnician"));
		//	dojo.html.show(dojo.byId("userRoleIsNotDealerTechnician"));
			break;
		}
	}
}