var _lastSeenCountry, _lastSeenState, _lastSeenCity;

function getStatesByCountry(value, validatableStateId,freeTextIds, validatableIds){
    if(!dojo.string.isBlank(value) && _lastSeenCountry != value) {
    	_lastSeenCountry = value;         
			twms.ajax.fireJsonRequest("get_states_by_country.action",{
                countryCode: value
            },function(data) {
					var statesAvailable = data.items.length > 0;
					if(statesAvailable){
					    var topicName = "topic_msa_data_state"; 
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
						    var topicName = "topic_msa_data_city"; 
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
					     var topicName = "topic_msa_data_zip"; 
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
            twms.ajax.fireJsonRequest("get_zips_by_country_state_and_city.action",params,function(data) {
					var countyAvailable = data.items.length > 0;
					if(countyAvailable){
					     var topicName = "topic_msa_data_county"; 
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