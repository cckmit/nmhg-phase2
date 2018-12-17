/**
 * Added for Google Map API
 */

function openMap() {
	dijit.byId("displayGoogleMap").show();
	var location = document.getElementById('travel_location').value;
	var custAddress=null;
	if(document.getElementById('claim_form_travel_location1')!=null)
		custAddress = document.getElementById('claim_form_travel_location1').value;

	var servicingLoc = document.getElementById('servicing_location_id').value;
	var to;
	if ((custAddress == null || custAddress == "")&&(servicingLoc != null || servicingLoc != ""))
		{
		custAddress=servicingLoc;		
		}
	if (location != null && location != "") {
		to = location;
		initialize(to, custAddress);
	} else if ((location == null || location == "") && (custAddress != "")) {
		to = custAddress;
		initialize(to, custAddress);
	} else if ((custAddress == null || custAddress == "") && (servicingLoc != "")) {
		to = servicingLoc;
		initialize(to, custAddress);
	} 
	else {
		displayErrorMessage("NoSerCustAdd");
	}
}

function displayErrorMessage(errorType) {
	var error = errorType;
	if(error=="NoSer"){
		dijit.byId("displayServicingLocMessage").show();
		dojo.byId("travel_location").value="";
		dojo.byId("travel_distance").value="";
		dojo.byId("travel_hours").value="";
		dijit.byId("displayGoogleMap").hide();
	} else {
		dijit.byId("displaySerCustAddMessage").show();
		dijit.byId("displayGoogleMap").hide();
	}
}

function closeMap() {
	if (dojo.byId("servicing_location_id").value == null
			|| dojo.byId("servicing_location_id").value == "") {
		displayErrorMessage("NoSer");
	} else {
		dojo.byId("travel_location").value = dojo.byId("formatedAddress").innerHTML;
		dojo.byId("travel_trips").readOnly=false;
		dijit.byId("displayGoogleMap").hide();
		calculateDistances();
		hasAddressChanged();
	}
}

function hasAddressChanged(){
	var valCustAdd = "";
	var travelLocation = document.getElementById('travel_location').value;
	if (travelLocation != null || travelLocation != "")
		travelLocation = document.getElementById('travel_location').value;
	var endCustAddress=null;
	if(document.getElementById('claim_form_travel_location1')!=null)
	  endCustAddress = document.getElementById('claim_form_travel_location1').value;
	if (endCustAddress != null || endCustAddress != "") {
		valCustAdd = endCustAddress;
	}
	if (valCustAdd != "" && valCustAdd != travelLocation) {
		dojo.byId('travelAddressChanged').checked = true;
	} else {
		dojo.byId('travelAddressChanged').checked = false;
	}
}

function calculateDistances() {
	var service = new google.maps.DistanceMatrixService();
	var origin = dojo.byId("servicing_location_id").value.split("-");
	var formatedOrigin="";
	for(var org in origin )
	{	
		if(org!=0)
			formatedOrigin += origin [org]+" ";	    
	}	
	var destination = document.getElementById('formatedAddress').innerHTML;
	service.getDistanceMatrix({
		origins : [formatedOrigin],
		destinations : [ destination ],
		travelMode : google.maps.TravelMode.DRIVING,
		unitSystem : google.maps.UnitSystem.IMPERIAL,
		avoidHighways : false,
		avoidTolls : false
	}, callback);
}

function timeTravelHour(seconds) {	
	var numHours = Math.floor(seconds / 3600);
	var remainder = (seconds % 3600);
	var numMins = Math.round(remainder / 60);
	var finalMins;
	if (numMins.toString().length == 1) {
		finalMins = "0" + numMins;
	} else {
		finalMins = numMins;
	}
	return numHours + "." + finalMins;
}

function formatedHourAndDistance(travelHr,travelDis,doubleHr)
{	
	travelHr=travelHr.toString();	
	var travelTrp;	
	if(!doubleHr)
		{		
		travelTrp="2";
		}
	else
	 {	
	travelHr=dojo.byId("base_travel_hours").value;
	travelDis=dojo.byId("base_travel_distance").value;	
	if(dojo.byId("travel_trips")!=null)
		 travelTrp = dojo.byId("travel_trips").value;	
	}
	
	var readOnly = dojo.byId("travel_trips").readOnly
	var travelHrsHrMin;
	var travelHrsMin;
	var totalMins;
	var totalSecs;
	if (travelHr != "" && travelDis != "") {		
		if (travelTrp!=""&&travelTrp >= 0 && travelTrp.indexOf(".") == -1 && !isNaN(travelTrp)
				&& !readOnly) {			
			var multiNr = parseInt(travelTrp, 10);			
			if(multiNr == 0 || multiNr==1)		
				{
				if(!doubleHr)
					multiNr=2;
				else
					multiNr=1;
				}			
			if(travelHr.indexOf(".")==-1)
				travelHr=travelHr.concat(".00")				
			travelHrsHrMin = travelHr.substr(0, travelHr.indexOf(".")) * 60;			
			travelHrsMin = travelHr.substr(travelHr.indexOf(".") + 1,
					travelHr.length - 1);			
			totalMins = parseInt(travelHrsHrMin, 10)
					+ parseInt(travelHrsMin, 10);			
			if(multiNr != 0&&travelDis!=0)
				{
				totalSecs = parseInt(totalMins, 10) * multiNr * 60;				
				travelDis = (parseInt(travelDis, 10) * multiNr);
				travelHr = timeTravelHour(totalSecs);					
				}			
			dojo.byId("travel_distance").value = travelDis;
			dojo.byId("travel_hours").value = travelHr;
			//fix for SLMSPROD-1392
			if(null!=dojo.byId("travel_hours_temp")){
       		 dojo.byId("travel_hours_temp").value = travelHr.replace(".", ":"); 
       	 	}
			if(!doubleHr)
			{
				dojo.byId("base_travel_hours").value = travelHr;	
				dojo.byId("base_travel_distance").value =travelDis;
			}
		} else {			
			if (!readOnly && travelTrp==""||travelTrp != 0) {
				dojo.byId("travel_trips").value = 0;
				travelHrsMiles();
				dijit.byId("travelTripDecimal").show();
			}
		}	
}
}
	
	
function callback(response, status) {
	if (status == google.maps.DistanceMatrixStatus.OK) {
		var shortestDistance = null;
		var travelHours = null;
		var finalHours = null;
		dojo.forEach(response.rows, function(row) {
			if(row.elements[0].distance !=null)
			{
			var distance = row.elements[0].distance.value;
			if (shortestDistance == null || distance < shortestDistance) {
				shortestDistance = distance;
				travelHours=row.elements[0].duration.value;
			}
			}
		});			
		if(dojo.byId("travel_distance"))			
			var distance=Math.round(shortestDistance * 0.000621371);
			
			if(dojo.byId("travel_hours")) {								
				finalHours = timeTravelHour(travelHours);							
				/*finalHoursWithRoundTrip=finalHoursWithRoundTrip.toFixed(2);*/									
		}
			formatedHourAndDistance(finalHours,distance,false);		
			formatedHourAndDistance(finalHours,distance,true);		
			recalculateTranspotation();

	}
}

function travelHrsMiles() {
	var travelDis = dojo.byId("base_travel_distance").value;
	var travelHr = dojo.byId("base_travel_hours").value;
	var travelTrp = dojo.byId("travel_trips").value;
	var readOnly = dojo.byId("travel_trips").readOnly;	
	var travelHrsHrMin;
	var travelHrsMin;
	var totalMins;
	var totalSecs;
	formatedHourAndDistance(travelHr,travelDis,true);	
}