function getStatesByCountry(value, validatableStateId,freeTextIds, validatableIds){
    if(value != null) {          
			twms.ajax.fireJsonRequest("get_states_by_country.action",{
                countryCode: value
            },function(data) {
					var statesAvailable = data.items.length > 0;
					if(statesAvailable){
					    var topicName = "topic_user_state"; 
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
    if(value != null) {
            var params={};
            params.countryCode=dijit.byId(countryId).getValue();
            params.stateCode=value;
            twms.ajax.fireJsonRequest("get_cities_by_country_and_state.action",params,function(data) {
						var citiesAvailable = data.items.length > 0;
					    if(citiesAvailable){
						    var topicName = "topic_user_city"; 
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
    if(value != null) {
            var params={};
            params.countryCode=dijit.byId(countryId).getValue();
            params.stateCode=dijit.byId(stateId).getValue();
            params.cityCode=value;
            twms.ajax.fireJsonRequest("get_zips_by_country_state_and_city.action",params,function(data) {
					var zipsAvailable = data.items.length > 0;
					if(zipsAvailable){
					     var topicName = "topic_user_zip"; 
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
    if(value != null) {
            var params={};
            params.countryCode=dijit.byId(countryId).getValue();
            params.stateCode=dijit.byId(stateId).getValue();
            params.zipCode=value;
            twms.ajax.fireJsonRequest("get_counties_by_country_state_and_zip.action",params,function(data) {
					var countyAvailable = data.items.length > 0;
					if(countyAvailable){
					     var topicName = "topic_user_county"; 
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