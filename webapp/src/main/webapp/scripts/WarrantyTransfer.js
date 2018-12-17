dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane");
dojo.require("dijit.form.ComboBox");

var spinnerTimerId = null;
var multiCheckBoxControl;
var dlg;
var operatordlg;
var lastUpdatedItemIndex;
var numItems;

dojo.addOnLoad ( function() {

    dojo.connect(dojo.byId("btnSubmit"), "onclick", function() {
        var form = dojo.byId("transfer_warranty");
        form.action = "warranty_transfer.action";
        dojo.byId("btnSubmit").disabled = true;
        dojo.byId("btnEdit").disabled = true;
        form.submit();
    });

    dojo.subscribe("/multipleInventorySearchResults/searchStatus", null, "attachListeners");

    dlg = dijit.byId("DialogContent");
    operatordlg = dijit.byId("DialogContentOperator");
    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
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
      "inventoryItemIndex": index,
      "firstTimeETR" : false
    };
	
	if(dojo.byId("addressBookType")){
		params.addressBookType = dojo.byId("addressBookType").value;
	}
    var policyDetailsPane = dijit.byId("policyDetails_" + index);
    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>"; //TODO: Replace this...

    twms.ajax.fireHtmlRequest("get_warranty_transfer_existing_policies.action", params, function(data) {
            policyDetailsPane.setContent(data);
            dijit.byId("pop_up_for_policy_fetching").hide();
            dojo.html.hide(dojo.byId("policyFetchSection"));
            delete data;
        }
    );

    delete slNo, delivery, hrs;
}

function getAllPolicies(index,name) {		
	var installation = "";
	var installingDealer = "";	
    var id = "inventoryItemMappings"+index;	   
	var slNo =document.getElementById(id+"__inventoryIemSN").value;
	try {
		var delivery = dijit.byId(id+"__deliveryDate").getDisplayedValue();
	} catch(e) {
		console.log("Delivery Date not defined");
	}
    var hrs = dojo.byId(id+"__hoursOnMachine").value;	
    var contractCode = "";
    
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
	try{
    var params = {
      "inventoryItem" : dojo.string.escape("xhtml", slNo),
      "inventoryItem.deliveryDate" : dojo.string.escape("xhtml", delivery),
      "inventoryItem.hoursOnMachine" : dojo.string.escape("xhtml", hrs),
      "inventoryItem.installationDate" : dojo.string.escape("xhtml", installation),
      "customerType" : "Company",
      "nListIndex": index,
      "nListName": name,
      "marketingInformation.contractCode" : contractCode
    };
    
    if(installingDealer != ""){
	    	params.installingDealer = installingDealer;
	}
    
    if(dojo.byId("addressBookType")){
		params.addressBookType = dojo.byId("addressBookType").value;
	}	  	 
    var policyDetailsPane = dijit.byId(id+"__policyDetails");	  
    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>";
    twms.ajax.fireHtmlRequest("get_warranty_transfer_existing_policies.action", params,
        function(data) {
    		policyDetailsPane.setContent(data);	           
            dijit.byId("pop_up_for_policy_fetching").hide();
            dojo.html.hide(dojo.byId("policyFetchSection"));	            
            delete data;
        });	    
    
	} catch(e) {
		console.log("Error occured while fetching policies ", e);
		dijit.byId("pop_up_for_policy_fetching").hide();
        dojo.html.hide(dojo.byId("policyFetchSection"));
	}
    delete slNo,delivery,hrs,id,installation,installingDealer,contractCode;
   }
}

function getPolicies()
{ 	  
      if(dojo.byId("inventoryItemSize"))
	  {
		  numItems = dojo.byId("inventoryItemSize").value;
		  for(var i=0;i< numItems ;i++)
		  {   	
		  	  slNos[i] = dojo.byId("inventoryIemSN_"+i).value;	
		      getOfferedPolicies(i);	                     
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
 
function bindOnChangeForTDateHrs()
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
	if(slNo) {
	    var disclaimerPane = dijit.byId(id+"__disclaimerPage");
	    twms.ajax.fireHtmlRequest("get_ttr_disclaimer.action", params,
	        function(data) {
	    		disclaimerPane.setContent(data);
	    		delete data;
	        });	    
	    delete slNo,id;
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

