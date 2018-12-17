dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane");
dojo.require("dijit.form.ComboBox");
dojo.require("twms.widget.Dialog");
dojo.require("dojox.date.php");
dojo.provide("dojox.dtl.utils.date");
dojo.require("dojo.date");
var spinnerTimerId = null;
var multiCheckBoxControl;
var dlg;
var operatordlg;
var numItems;

dojo.addOnLoad ( function() {

	
	if(dojo.byId("btnSubmit"))
	{
	    dojo.connect(dojo.byId("btnSubmit"), "onclick", function() {
	        var form = dojo.byId("register_warranty");
	        form.action = "register_warranty.action";
            dojo.byId("btnSubmit").disabled = true;
            dojo.byId("btnEdit").disabled = true;
            form.submit();
	    });
    }

    dojo.subscribe("/multipleInventorySearchResults/searchStatus", null, "attachListeners");
    
   /* var confirmRegistrationContent = dijit.byId("confirmRegistrationContent");
    confirmRegistrationContent.domNode.innerHTML = "Loading...";*/

    /*new dojo.io.FormBind({
            formNode: dojo.byId("register_warranty"),
            load: function(data, e) {
                dojo.body().style.cursor = "auto";

                confirmRegistrationContent.setContent(data);
            },
            error : function(error) {
                dojo.body().style.cursor = "auto";
            }
    });*/

    dlg = dijit.byId("DialogContent");
    operatordlg = dijit.byId("DialogContentOperator");
    
    var customerSearchButton =dojo.byId("customerSearchButton");
	if(customerSearchButton){		
		dojo.connect(customerSearchButton, "onclick","getMatchingCustomers");
	}
	
	 var operatorSearchButton =dojo.byId("operatorSearchButton");
		if(operatorSearchButton){			
			dojo.connect(operatorSearchButton, "onclick","getMatchingOperators");
		}
});

function attachListeners(message) {
    for(var i=0; i < numItems; i++) {
       dijit.byId("deliveryDate_" + i).onChange = function(value) {
            getOfferedPolicies(this.id.split("_")[1]);
       };
    }
}

function getOfferedPolicies(index) {
    dojo.html.show(dojo.byId("policyFetchSection"));
    dijit.byId("pop_up_for_policy_fetching").show();
    var slNo = slNos[index];
    var delivery = dijit.byId("deliveryDate_" + index).getDisplayedValue();
    var hrs = dojo.byId("hoursOnMachine_" + index).value;
    var params = {
      "inventoryItem" : dojo.string.escape("xhtml", slNo),
      "inventoryItem.deliveryDate" : dojo.string.escape("xhtml", delivery),
      "inventoryItem.hoursOnMachine" : dojo.string.escape("xhtml", hrs),
      "customerType" : "Company",
      "inventoryItemIndex": index
    };
	
	if(dojo.byId("addressBookType")){
		params.addressBookType = dojo.byId("addressBookType").value;
	}
    var policyDetailsPane = dijit.byId("policyDetails_" + index);
    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>"; //TODO: Replace this...

    twms.ajax.fireHtmlRequest("get_warranty_policies.action", params,
        function(data) {
    		policyDetailsPane.domNode.innerHTML = data;
           // policyDetailsPane.setContent(data);
            dijit.byId("pop_up_for_policy_fetching").hide();
            dojo.html.hide(dojo.byId("policyFetchSection"));
            delete data;
        });

    delete slNo,delivery,hrs;
}
function getAllPolicies(index,name) {		
		var installation = "";
		var installingDealer = "";	
		var forDealer = "";
		var customer = "";
	    var id = "inventoryItemMappings"+index;
	    var contractCode = "";
	    
	    try{
	    	customer = dojo.byId("customer").value;
	    	console.debug("Customer"+dojo.byId("customer"));
	    }
	    catch(e){
	    	console.debug("Customer not captured"+e);
	    }
	    try{
		var slNo =document.getElementById(id+"__inventoryIemSN").value;		
	    }
	    catch(e){
	    	console.debug("Serial number not captured"+e);
	    }
	    try{
	    var delivery = dijit.byId(id+"__deliveryDate").getDisplayedValue();	   	    
	    var hrs = dojo.byId(id+"__hoursOnMachine").value;	
	    }
	    catch(e){
	    	console.debug("Delivery Date not captured"+e);
	    }
	    if(slNo)
	    {
	    dojo.html.show(dojo.byId("policyFetchSection"));
	    dijit.byId("pop_up_for_policy_fetching").show();
	    try
	    {  
	    	installingDealer = dijit.byId("installingDealerNameAutoComplete").getValue();
	    }
	    catch(e)
	    {
	    	console.debug("Installing Dealer Not Captured"+e);
	    }	    
	    try
	    {  
	    	forDealer = dijit.byId("dealerNameAutoComplete").getValue();
	    }
	    catch(e)
	    {
	    	console.debug("Dealer name  Not Captured"+e);
	    	try{
	    		forDealer = dijit.byId("dealerNumberAutoComplete").getValue();
	    	}
	    	catch(e){
	    		console.debug("Dealer number  Not Captured"+e);
	    	}
	    }
	    try
	    {
	    installation = dijit.byId(id+"__installationDateForUnit").getDisplayedValue();	
	    }
	    catch(e)
	    {
	    	console.debug("Installation Date Not Captured"+e);
	    }
	    
	    try{
	    	var contractCode = dijit.byId("contractCode").getValue();	
		}
		catch(e){
			console.debug("Contract Code Not Captured"+e);
		}
		toggleMessageForDemoTruckMoreThan80Hours(index);
		    
		try {
	    var params = {
	      "inventoryItem" : dojo.string.escape("xhtml", slNo),
	      "inventoryItem.deliveryDate" : dojo.string.escape("xhtml", delivery),
	      "inventoryItem.installationDate" : dojo.string.escape("xhtml", installation),
	      "inventoryItem.hoursOnMachine" : dojo.string.escape("xhtml", hrs),
	      "customerType" : "Company",
	      "nListIndex": index,
	      "nListName": name,
	      "customer": customer,
	      "marketingInformation.contractCode" : contractCode
	    };	    
	    if(installingDealer != ""){
	    	params.installingDealer = installingDealer;
	    }
	    if(forDealer != ""){
	    	params.forDealer = forDealer;
	    }
	    
	    if(dojo.byId("addressBookType")){
			params.addressBookType = dojo.byId("addressBookType").value;
		}	  	 
	    var policyDetailsPane = dijit.byId(id+"__policyDetails");	  
	    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>";
	    twms.ajax.fireHtmlRequest("get_warranty_policy.action", params,
	        function(data) {
	    		policyDetailsPane.setContent(data);	           
	            dijit.byId("pop_up_for_policy_fetching").hide();
	            dojo.html.hide(dojo.byId("policyFetchSection"));	            
	            delete data;
	        });	    
	    
	  
	  if((dojo.byId("dealerRentalAllowed").value != "true" && dojo.byId("addressBookType").value =="Dealer Rental") /*|| dojo.byId("addressBookType").value =="Demo")*/&& dojo.byId("preOrderBooking") && dojo.byId("preOrderBooking").value!="true") {
	    twms.ajax.fireHtmlRequest("refresh_customer_info.action", params,
	        function(data) {
	    		dijit.byId("customerinfo").setContent(data);	
	    		dijit.byId("customerinfo").innerHTML = data;
	            
	                
	            delete data;
				
                    dojo.html.hide(dojo.byId("customerSearchButton"));
                    dojo.html.hide(dojo.byId("name"));
                    if(dojo.byId("warranty_operator_info"))
                     dojo.html.hide(dojo.byId("warranty_operator_info"));                                  
                                           
                   });
	    }
		} catch(e) {
			console.log("Error occured while fetching policies ", e);
			dijit.byId("pop_up_for_policy_fetching").hide();
            dojo.html.hide(dojo.byId("policyFetchSection"));
		}
	    delete slNo,delivery,hrs,id,installation,installingDealer,forDealer,contractCode;
	    }
	    
	}

function toggleMessageForDemoTruckMoreThan80Hours(index) {
	var id = "inventoryItemMappings" + index;
	try {
		var contractCodeDisplayedValue = dijit.byId("contractCode").getDisplayedValue();
		var hoursOnTruck = dojo.byId(id + "__hoursOnMachine").value;
		if(contractCodeDisplayedValue == 'Demo' && hoursOnTruck > 80){
			dojo.html.show(dojo.byId("demoTruckAuthorizationMessage"));
		} else {
			dojo.html.hide(dojo.byId("demoTruckAuthorizationMessage"));
		}
	} catch(e) {
		console.log("Contract Code or Hours On Truck Not Captured" + e);
	}
}

function getDisclaimer(index,name) {		
	var customer = "";
	var slNo;
    var id = "inventoryItemMappings"+index;
    try{
    	if(dojo.byId("addressBookType").value !="Dealer Rental" 
  		  && dojo.byId("addressBookType").value !="Demo")
    	customer = dojo.byId("customer").value;
    }catch(e){
    	console.debug("Customer not captured"+e);
    }
    try{
    	slNo =document.getElementById(id+"__inventoryIemSN").value;		
    }catch(e){
    	console.debug("Serial number not captured"+e);
    }
    var params = {
      "inventoryItem" : dojo.string.escape("xhtml", slNo),
      "nListIndex": index,
      "inventoryItemMappingIndex": index,
      "nListName": name,
      "customer": customer
    };
	params.addressBookType = dojo.byId("addressBookType").value;
	dijit.byId("fetchingDisclamer").show();
	if(slNo) {
	    var disclaimerPane = dijit.byId(id+"__disclaimerPage");
	    twms.ajax.fireHtmlRequest("get_disclaimer.action", params,
	        function(data) {
	    		disclaimerPane.setContent(data);
	    		dijit.byId("fetchingDisclamer").hide();
	    		delete data;
	        });	    
	    delete slNo,id;
	}
}


function getPolicies()
{ 	  
	  if(dojo.byId("inventoryItemSize"))
	  {	  	
		  numItems = dojo.byId("inventoryItemSize").value;
		 
	 	  	
 	  }
 }     
 
 
 function bindOnChangeForDDateHrs()
{ 	  
	  if(dojo.byId("inventoryItemSize"))
	  {	  	
		  numItems = dojo.byId("inventoryItemSize").value;
		  for(var i=0;i< numItems ;i++)
		  {   	
		  	  slNos[i] = dojo.byId("inventoryIemSN_"+i).value;
		      dojo.connect(dojo.byId("hoursOnMachine_"+ i),
		              "onchange", function() {                        
		          getOfferedPolicies(this.id.split("_")[1]);
		      });		                          
		      dojo.connect(dijit.byId("deliveryDate_" + i),
		              "onChange", function() {
		          getOfferedPolicies(this.id.split("_")[1]);
		      });
	 	  }
	 	  	
 	  }
 	  	                          
	
 }
 
function showWaiver(index){
	var waiver ="inventoryItemMappings"+index+"_waiver";
	var disclaimerInfo= "inventoryItemMappings"+index+"_disclaimerInfo";
	dojo.html.hide(dojo.byId(disclaimerInfo));
	dojo.html.show(dojo.byId(waiver));
}
 
function showDisclaimer(index){
	var waiver = "inventoryItemMappings" + index + "_waiver";
	var disclaimerInfo = "inventoryItemMappings" + index + "_disclaimerInfo";
	dojo.html.show(dojo.byId(disclaimerInfo));
	dojo.html.hide(dojo.byId(waiver));
}
 

function getAllPoliciesForEdit(index,name) {		
	var installation = "";
	var installingDealer = "";	
	var forDealer = "";
	var customer = "";
    var id = "inventoryItemMappings"+index;
    var contractCode = "";
    
    try{
    	customer = dojo.byId("customer").value;
    	console.debug("Customer"+dojo.byId("customer"));
    }
    catch(e){
    	console.debug("Customer not captured"+e);
    }
    try{
	var slNo =document.getElementById("equipment").value;	
    }
    catch(e){
    	console.debug("Serial number not captured"+e);
    }
    try{
    var delivery = document.getElementById("deliveryDate").value;	   	    
   // var hrs = dojo.byId("hoursOnMachine").value;	
    }
    catch(e){
    	console.debug("Delivery Date not captured"+e);
    }
    if(slNo)
    {
    dojo.html.show(dojo.byId("policyFetchSection"));
    dijit.byId("pop_up_for_policy_fetching").show();
    try
    {  
    	installingDealer = dijit.byId("installingDealerNameAutoComplete").getValue();
    }
    catch(e)
    {
    	console.debug("Installing Dealer Not Captured"+e);
    }	    
    try
    {  
    	//forDealer = dijit.byId("dealerNameAutoComplete").getValue();
    }
    catch(e)
    {
    	//console.debug("Dealer name  Not Captured"+e);
    	//try{
    	//	forDealer = dijit.byId("dealerNumberAutoComplete").getValue();
    	//}
    	//catch(e){
    	//	console.debug("Dealer number  Not Captured"+e);
    	//}
    }
    try
    {
    installation = dijit.byId("installationDateForUnit").getDisplayedValue();	
    }
    catch(e)
    {
    	console.debug("Installation Date Not Captured"+e);
    }
    try{
    	var warranty =document.getElementById("warranty").value;	
        }
        catch(e){
        	console.debug("warranty not captured"+e);
        }
    
    try{
	    var contractCode = dijit.byId("contractCode").getValue();	
	}catch(e){
		console.debug("Contract Code Not Captured"+e);
	}
	
    var params = {
      "inventoryItem" : dojo.string.escape("xhtml", slNo),
      "inventoryItem.deliveryDate" : delivery,
      "inventoryItem.installationDate" : dojo.string.escape("xhtml", installation),
      "customerType" : "Company",
      "nListIndex": index,
      "nListName": name,
      "customer": customer,
      "warranty": warranty,
      "marketingInformation.contractCode" : contractCode
    };	    
    if(installingDealer != ""){
    	params.installingDealer = installingDealer;
    }
    if(forDealer != ""){
    	params.forDealer = forDealer;
    }
    
    if(dojo.byId("addressBookType")){
		params.addressBookType = dojo.byId("addressBookType").value;
	}	  	 
    var policyDetailsPane = dijit.byId("policies");	  
    policyDetailsPane.domNode.innerHTML = "Loading...";
    twms.ajax.fireHtmlRequest("get_warranty_policy_for_edit.action", params,
        function(data) {
    		policyDetailsPane.setContent(data);	           
            dijit.byId("pop_up_for_policy_fetching").hide();
            dojo.html.hide(dojo.byId("policyFetchSection"));	            
            delete data;
        });	    
    
  
    if((dojo.byId("dealerRentalAllowed").value != "true" && dojo.byId("addressBookType").value =="Dealer Rental") /*|| dojo.byId("addressBookType").value =="Demo")*/ && dojo.byId("preOrderBooking") && dojo.byId("preOrderBooking").value!="true") {
    twms.ajax.fireHtmlRequest("refresh_customer_info.action", params,
        function(data) {
    		dijit.byId("customerinfo").setContent(data);	
    		dijit.byId("customerinfo").innerHTML = data;
            
                
            delete data;
			
              //  dojo.html.hide(dojo.byId("customerSearchButton"));
                dojo.html.hide(dojo.byId("name"));
             //   if(dojo.byId("warranty_operator_info"))
              //   dojo.html.hide(dojo.byId("warranty_operator_info"));                                  
                                       
               });
    }
	    
    delete slNo,delivery,id,installation,installingDealer,forDealer,contractCode;
    }
    
}
