/*This js file basically handles the toggling betwwen dealerName and dealerNumber parameters
  This js file is called only if the logged in user is a processor or admin and is filing 
  Warranty Registration on the inventory for the first time(i.e no draft warranty is there on the inventory)
  */
dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");

dojo.addOnLoad(function(){
			//The complete Warranty Registration page is hidden at the time of load
			dojo.html.hide(dojo.byId("warrantyRegDiv"));
			dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function() {
				setUiForDealerName();
			});
			dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
				setUiForDealerNumber();
			});
});
// This API basically handles all the toggling when user wishes to enter dealerName
// Hides the dealerNumber autocompleter after setting the values to empty string and disabling it.
// Without disabling if the autocompleter is hidden then it will pass the value in form.
// Hides the text messages and the div containg the WR page
// Removes all the selected inventories if there are any and set the content to empty string.
// Removes all the selected customers if there are any and set the content to empty string. 
// Enables the dealerNumber autocompleter and set the values to empty string
	/* The same steps are repeated for both the toggling effect*/	
				
function setUiForDealerName(){
	dealerSelect = dijit.byId("dealerNameAutoComplete");				
	dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
	dijit.byId("dealerNumberAutoComplete").setValue("");
	dijit.byId("dealerNumberAutoComplete").setDisplayedValue("");
	dijit.byId("dealerNumberAutoComplete").setDisabled(true);				
	dojo.html.hide(dojo.byId("dealerNumberText"));
	dojo.html.hide(dojo.byId("toggleToDealerName"));
	dojo.html.hide(dojo.byId("warrantyRegDiv")); 
	dojo.byId("customerDetailsDiv").innerHTML = ""; 
	dojo.html.show(dojo.byId("dealerNameText")); 
	dojo.html.show(dijit.byId("dealerNameAutoComplete").domNode);
	dijit.byId("dealerNameAutoComplete").setDisabled(false);
	dijit.byId("dealerNameAutoComplete").setValue(""); 
	dijit.byId("dealerNameAutoComplete").setDisplayedValue("");
	dojo.html.show(dojo.byId("toggleToDealerNumber"));
	dojo.byId("dealerNameOrNumberSelected").value=true;
} 

function setUiForDealerNumber(){
	dealerSelect = dijit.byId("dealerNumberAutoComplete");				
	dojo.html.hide(dijit.byId("dealerNameAutoComplete").domNode);
	dijit.byId("dealerNameAutoComplete").setValue(""); 
	dijit.byId("dealerNameAutoComplete").setDisplayedValue("");
	dijit.byId("dealerNameAutoComplete").setDisabled(true);
	dojo.html.hide(dojo.byId("dealerNameText"));
	dojo.html.hide(dojo.byId("toggleToDealerNumber"));
	dojo.html.hide(dojo.byId("warrantyRegDiv"));	
	dojo.byId("customerDetailsDiv").innerHTML = "";  
	dojo.html.show(dojo.byId("dealerNumberText")); 
	dojo.html.show(dijit.byId("dealerNumberAutoComplete").domNode);
	dijit.byId("dealerNumberAutoComplete").setDisabled(false);
	dijit.byId("dealerNumberAutoComplete").setValue("");
	dijit.byId("dealerNumberAutoComplete").setDisplayedValue("");
	dojo.html.show(dojo.byId("toggleToDealerName"));
	dojo.byId("dealerNameOrNumberSelected").value=false;
}

function setUiForInvalidDealerName(){
	dojo.html.hide(dojo.byId("warrantyRegDiv")); 
	dojo.byId("customerDetailsDiv").innerHTML = ""; 
	dojo.byId("dealerNameOrNumberSelected").value=true;
} 

function setUiForInvalidDealerNumber(){
	dojo.html.hide(dojo.byId("warrantyRegDiv"));	
	dojo.byId("customerDetailsDiv").innerHTML = "";  
	dojo.byId("dealerNameOrNumberSelected").value=false;
}