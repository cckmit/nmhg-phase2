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
    //dojo.subscribe("/multipleInventorySearchResults/searchStatus", null, "attachListeners");
    dlg = dijit.byId("DialogContent");
    operatordlg = dijit.byId("DialogContentOperator");
    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
    dojo.connect(dojo.byId("operatorSearchButton"), "onclick","getMatchingOperators");
});

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
	if(dojo.byId("typeOfTransaction").value=="DR"){	
 		twms.ajax.fireHtmlRequest("get_warranty_policy.action", params,
		        function(data) {
		            policyDetailsPane.setContent(data);
                    dijit.byId("pop_up_for_policy_fetching").hide();
                    dojo.html.hide(dojo.byId("policyFetchSection"));
                    delete data;
        	});
 		} 		
 	else{	
 	    twms.ajax.fireHtmlRequest("get_warranty_transfer_existing_policies.action", params,
	        function(data) {
	            policyDetailsPane.setContent(data);
                dijit.byId("pop_up_for_policy_fetching").hide();
                dojo.html.hide(dojo.byId("policyFetchSection"));
                delete data;
	        });
	}
    delete slNo,delivery,hrs;
}

function getPolicies(){
	  if(dojo.byId("inventoryItemSize"))
	  {
		  numItems = dojo.byId("inventoryItemSize").value;
		  for(var i=0;i< numItems ;i++)
		  {
		  	  slNos[i] = dojo.byId("inventoryIemSN_"+i).value;
		      //getOfferedPolicies(i);
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


	function getAllPolicies(index,name) {
		var installation = "";
		var installingDealer = "";
		var delivery = "";
		var hrs = "";
		var slNo = "";
		var contractCode = "";
		
		dojo.html.show(dojo.byId("policyFetchSection"));
		if(dijit.byId("pop_up_for_policy_fetching")){
	      dijit.byId("pop_up_for_policy_fetching").show();
		}
	    var id = "inventoryItemMappings"+index;
		if( typeof(document.getElementById(id+"__inventoryIemSN"))!=undefined 
				&& document.getElementById(id+"__inventoryIemSN")!=null ){
			slNo = document.getElementById(id+"__inventoryIemSN").value;
	    }
		if( dijit.byId(id+"__deliveryDate")){
	     delivery = dijit.byId(id+"__deliveryDate").getDisplayedValue();
		}
		if(dojo.byId(id+"__hoursOnMachine")){
	       hrs = dojo.byId(id+"__hoursOnMachine").value;
	    }
	    try
	    {
	      if(dijit.byId(id+"__installationDateForUnit")){	
	      installation = dijit.byId(id+"__installationDateForUnit").getDisplayedValue();
	      }	
	    }
	    catch(e)
	    {
	    	console.debug("Installation Date Not Captured"+e);
	    }    
	    try
	    {   if(dijit.byId("installingDealerNameAutoComplete")){
	    	installingDealer = dijit.byId("installingDealerNameAutoComplete").getValue();
	        }
	    }
	    catch(e)
	    {
	    	console.debug("Installing Dealer Not Captured"+e);
	    }
	    
	    try{
	    	var contractCode = dijit.byId("contractCode").getValue();
		}
		catch(e){
			console.debug("Contract Code Not Captured"+e);
		}
		
	    var params = {
	      "inventoryItem" : dojo.string.escape("xhtml", slNo),
	      "inventoryItem.deliveryDate" : dojo.string.escape("xhtml", delivery),
	      "inventoryItem.installationDate" : dojo.string.escape("xhtml", installation),
	      "inventoryItem.hoursOnMachine" : dojo.string.escape("xhtml", hrs),
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
	    if(policyDetailsPane){
	    policyDetailsPane.destroyDescendants();
	    policyDetailsPane.setContent("");
	    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>";
	    if(dojo.byId("typeOfTransaction").value=="DR"){	
	    twms.ajax.fireHtmlRequest("get_warranty_policy.action", params,
	        function(data) {		    		
	    		policyDetailsPane.setContent(data);	           
	            dijit.byId("pop_up_for_policy_fetching").hide();
	            dojo.html.hide(dojo.byId("policyFetchSection"));
	            delete data;
	        });	
	    }
	    else{	
	 	    twms.ajax.fireHtmlRequest("get_warranty_transfer_existing_policies.action", params,
		        function(data) {
		            policyDetailsPane.setContent(data);
	                dijit.byId("pop_up_for_policy_fetching").hide();
	                dojo.html.hide(dojo.byId("policyFetchSection"));
	                delete data;
		        });
		}
	    }
	    
	    if((dojo.byId("dealerRentalAllowed").value != "true" && dojo.byId("addressBookType").value =="Dealer Rental")) {
		    twms.ajax.fireHtmlRequest("refresh_customer_info.action", params,
		        function(data) {
		    		dijit.byId("customerDetailsDiv").setContent(data);	
		    		dijit.byId("customerDetailsDiv").innerHTML = data;
		            delete data;
		          
	                    dojo.html.hide(dojo.byId("customerSearchButton"));
	                    dojo.html.hide(dojo.byId("name"));
	                    if(dojo.byId("warranty_operator_info"))
	                     dojo.html.hide(dojo.byId("warranty_operator_info"));                                  
	                                           
	                   });	  
		  }
		    
		    delete slNo,delivery,hrs,id,installation,installingDealer,contractCode;
		    
	}

