<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="ui-ext/actionResult/actionResult.css" />

<div dojoType="twms.widget.Dialog" id="displayGoogleMap" bgColor="#FFF"
	bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:97%;height:99%;">
<style type="text/css">
div#map {
	position: relative;
	width: 100%;
	height: 500px;
}
</style>
	
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false "></script>
	
<script type="text/javascript">
	var map;
	var geocoder;
	var marker;
	var mapZoom;
	var customerLocation;
	var toAddress;
	var update_timeout;
	
	function initialize(to,custAddress){
		endCustomerAddress.innerHTML = custAddress;
		dojo.byId('address').value="";
		initializeToAddress(to);
	}
	
	function initializeToAddress(to) {
		if(to==""||to==null){
			dojo.byId('formatedAddress').innerHTML="";			
			currentLocMap();		
		} else {
			toAddress = dojo.byId('formatedAddress');	
			toAddress.innerHTML = to;
			var myOptions = {
				zoom : 8,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById("map_canvas"),
					myOptions);
			marker = new google.maps.Marker({
	            map: map,
	            icon: 'image/red-dot.png'
	        });
			geocoder = new google.maps.Geocoder();
			geocode(to);
			google.maps.event.addListener(map, 'click', function(event) {
			    mapZoom = map.getZoom();
	            customerLocation = event.latLng;
	            update_timeout = setTimeout(function() {
	                placeMarker();
	            }, 200);
	        });
	
	        google.maps.event.addListener(map, 'dblclick', function(event) {
	            clearTimeout(update_timeout);
	        });
		}
	}
	
	function placeMarker() {
        if (mapZoom == map.getZoom()) {
            marker.setPosition(customerLocation);
		    toAddress.innerHTML = '';
            reverseGeocode();
        }
	}

	function reverseGeocode() {
		geocoder.geocode({
			latLng : marker.getPosition()
		}, reverseGeocodeResult);
	}

	function reverseGeocodeResult(results, status) {
		if (status == 'OK') {
			if (results.length == 0) {
				document.getElementById('formatedAddress').innerHTML = 'None';
			} else {
				document.getElementById('formatedAddress').innerHTML = results[0].formatted_address;
				marker.setTitle(toAddress.innerHTML);
			}
		} else {
			/* document.getElementById('formatedAddress').innerHTML = 'Error'; */
			displayMessage();
		}
	}

	function geocode(to) {
		var address = to;
		geocoder.geocode({'address' : address}, function(results,status) {
			if (status == google.maps.GeocoderStatus.OK) {
			      map.setCenter(results[0].geometry.location);
			      marker = new google.maps.Marker({
			          map: map,
			          position: results[0].geometry.location,
			          title: address
			      });
			      if (results.length == 0) {
						document.getElementById('formatedAddress').innerHTML = 'None';
					} else {
						
						document.getElementById('formatedAddress').innerHTML = results[0].formatted_address;
						customerLocation=results[0].formatted_address;
					}
		} else {
			displayMessage();
		}
		});
	}
	
	function displayMessage() {
		dijit.byId("displayGoogleMapMessage").show();

	}
</script>
<!-- Find Place: -->
		<label for="place" class="labelStyle">
		    <s:text name="label.googleMaps.findPlace" />:
		</label>
		<input type="text" id="address" size="75" value=""/>
		<input type="button" value="Go" onclick="geocode(dojo.byId('address').value)">

		<div id="map">
			<div id="map_canvas" style="width: 100%; height: 100%"></div>

		</div>

		<table width="100%">
			<tr>
				<td width="20%"><label for="foundAddress" class="labelStyle"><s:text name="label.googleMaps.address" />:</label></td>
				<td><div id="formatedAddress"></div>
				</td>
			</tr>
			<tr>
				<td width="12%"><label for="endCustomerAddress" class="labelStyle"><s:text name="label.googleMaps.endCustomerAddress" />:</label></td>
				<td width="48%"><div id="endCustomerAddress"></div>
				</td>
				<td width="40%"><a id="reset_travel_location" class="link" onclick="initializeToAddress(dojo.byId('endCustomerAddress').innerHTML)"> <s:text
							name="label.reset.travelLocation" /> </a>
				</td>
			</tr>
			<tr>
			    <td colspan="2" align="center">
                    <input class="buttonGeneric" type="button" id="submitLocation" value="<s:text name='label.common.submit'/>" onclick="closeMap()" />
			    </td>
			</tr>
		</table>
</div>
<div dojoType="twms.widget.Dialog" id="displayGoogleMapMessage"
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250"
	style="width: 50%; height: 200px">
	<body>
		<div id="Error Message For Google Maps" class="twmsActionResults">
			<div
				class="twmsActionResultsSectionWrapper twmsActionResultsMessages ">
				<h4 class="twmsActionResultActionHead">Message</h4>
				<br clear="all" />
				<OL style="width:90%">
					<li><s:text
							name="message.googleMap.customer" />
					</li>
				</OL>
			</div>
		</div>
</div>
<div dojoType="twms.widget.Dialog" id="displayServicingLocMessage"
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250"
	style="width: 50%; height: 200px">
	<body>
		<div id="Error Message For Google Maps" class="twmsActionResults">
			<div
				class="twmsActionResultsSectionWrapper twmsActionResultsMessages ">
				<h4 class="twmsActionResultActionHead">Message</h4>
				<br clear="all" />
				<OL style="width:90%">
					<li><s:text
							name="message.googleMap.servicingLocation" />
					</li>
				</OL>
			</div>
		</div>
</div>
<div dojoType="twms.widget.Dialog" id="displaySerCustAddMessage"
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250"
	style="width: 50%; height: 200px">
	<body>
		<div id="Error Message For Google Maps" class="twmsActionResults">
			<div
				class="twmsActionResultsSectionWrapper twmsActionResultsMessages ">
				<h4 class="twmsActionResultActionHead">Message</h4>
				<br clear="all" />
				<OL style="width:90%">
					<li><s:text
							name="message.googleMap.common" />
					</li>
				</OL>
			</div>
		</div>
</div>