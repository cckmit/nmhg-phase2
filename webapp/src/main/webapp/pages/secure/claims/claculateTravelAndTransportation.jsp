
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

				 
<script type="text/javascript">
	//Function to calculate travel and Transportation
function populateTranspotationAndTravel()
		{			
	  	 if(dojo.byId("travel_location")!=null){  
	  		 var travelLoc=dojo.byId("travel_location").innerHTML;
	         if(travelLoc=='')
	        	{
    		 if(dojo.byId('claim_form_travel_location1')!=null)
			 {
			 dojo.byId("travel_location").innerHTML= dojo.byId("claim_form_travel_location1").value;
			 }
		 else
			 {
             dojo.byId("travel_location").innerHTML= dojo.byId("servicing_location_id").value;
			 }             
		 }
	  	 }	  	 
		  	var origin = dojo.byId("servicing_location_id").value.split("-");
			var formatedOrigin="";
			for(var org in origin )
			{	
				if(org!=0)
					formatedOrigin += origin [org]+" ";	    
			}
	
			var claimId=<s:property value="task.claim"/>;			
			var servicingLocation=document.getElementsByName("task.claim.servicingLocation")[0].value;	
			var travelLocation= dojo.byId("travel_location").innerHTML;
				 var url = 'getTranspotation.action';  							
				 twms.ajax.fireJavaScriptRequest(url, {
                     claim: claimId,
                     servicingLocationAddress: formatedOrigin,
                     travelLocationAddress: travelLocation
                 }, function(details) {
                	 if(dojo.byId('transportationCheckBox')!=null && dojo.byId('transportationCheckBox').checked == true)
     					{
                	 if(dojo.byId("transportation_duty")!=null)
                		 {
                    	 dojo.byId("transportation_duty").value = details[0]; 
                    	 dijit.byId("transportation_duty").set("readOnly", true);
                	 	 document.getElementsByName("task.claim.serviceInformation.serviceDetail.transportationAmt")[1].value=details[0];
                		 }
     				  }
                	 else
                		 {
                		 if(dojo.byId("transportation_duty")!=null)
                			 dijit.byId("transportation_duty").set("readOnly", false);
                		 }              
                	 
                	 var distnace=details[1];
                	 var travelHr=details[2];                	
                	//fix for SLMSPROD-1392
                	 if(null!=dojo.byId("travel_hours_temp") && null != details[4]){
                		 dojo.byId("travel_hours_temp").value = details[4]; 
                	 }                	
                	 travelHr=travelHr.toString();                	 
                	 formatedHourAndDistance(travelHr,distnace,false);
                	 formatedHourAndDistance(travelHr,distnace,true);               	 
               });			
		}		
	
</script>
