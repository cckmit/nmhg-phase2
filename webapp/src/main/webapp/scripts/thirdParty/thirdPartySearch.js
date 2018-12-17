dojo.addOnLoad(function (){
	dojo.subscribe("/thirdPartySearch/hide", null, function() {
	    dijit.byId("thirdPartySearch").hide();
	});
	
	dojo.subscribe("/thirdPartySearch/show", null, function() {
	    dijit.byId("thirdPartySearch").show();
	});

	//Adding event to reset button on load. This will ensure that everytime we press reset, values in
	//popup are reset. 
	dojo.connect(dojo.byId("resetThirdPartySearch"), "onclick", function() {
		if (dojo.byId("searchThirdPartyNumber"))
			dojo.byId("searchThirdPartyNumber").value="";
		if (dojo.byId("searchThirdPartyName"))
			dojo.byId("searchThirdPartyName").value="";	
	});    
	if(dojo.byId("searchThirdPartiesButton"))
	{
	dojo.connect(dojo.byId("searchThirdPartiesButton"), "onclick", function() {
		var targetContent=dijit.byId('thirdPartyResultDiv');
	        targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
		});
	}
});

function selectThirdParty(thirdPartyIdToSelect, thirdPartyNameSelected)
{
	//hide the beautiful popup window which has been created to select thirdparty
	dijit.byId("thirdPartySearch").hide();
	
	//set the value of the third party text box as the id and value of third party
	dojo.byId("selectedThirdPartyDisplayName").innerHTML = thirdPartyNameSelected;
	dojo.byId("dealer").value = thirdPartyNameSelected;
	if (dojo.byId("thirdPartyName"))
		dojo.byId("thirdPartyName").value = thirdPartyNameSelected;
	dojo.publish("campaignCode/search", [{
				params: {
					"dealerName": dojo.byId("dealer").value
				}
	}]);
	
	if (dojo.byId("serviceLocationDetail"))
	{
		var serviceLocation = dojo.byId("serviceLocationDetail");
		serviceLocation.innerHTML = '';
		var params = {};
		params["thirdPartyName"] = thirdPartyNameSelected;
		twms.ajax.fireJsonRequest("findServiceProvider.action", params, function(details) {
            dojo.byId("serviceLocationDetail").innerHTML = details[0];
			dojo.byId("thirdPartyServiceLoc").value = details[1];
           }
        );
		
	}
	
}