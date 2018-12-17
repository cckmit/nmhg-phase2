var dealerNameSelect;
var dealerNumberSelect;

	dojo.addOnLoad(function(){
		dealerNameSelect = dijit.byId("dealerNameAutoComplete");
		dealerNumberSelect = dijit.byId("dealerNumberAutoComplete");
		
		dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
			dealerNumberSelect.setValue("");
			dealerNumberSelect.setDisplayedValue("");
			adjustUIForDcapClaimBox(null, true);
			adjustUIForNameOrNumber(false);
		});
			
		dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function()  {
			dealerNameSelect.setValue("");
			dealerNameSelect.setDisplayedValue("");
			adjustUIForDcapClaimBox(null, true);
			adjustUIForNameOrNumber(true);
		});

		dojo.connect(dealerNameSelect, "onChange", function(newVal) {	
			adjustUIForDcapClaimBox(newVal);
		});
		
		dojo.connect(dealerNumberSelect, "onChange", function(newVal) {	
			adjustUIForDcapClaimBox(newVal);
		});		

	});
	
	function adjustUIForNameOrNumber(/*boolean*/ isDealerName) {
		dojo.byId("dealerNameSelected").value = isDealerName;
		
		dojo.html.setDisplay(dealerNumberSelect.domNode, !isDealerName);
		dealerNumberSelect.setDisabled(isDealerName);
		dojo.html.setDisplay(dojo.byId("dealerNumberText"), !isDealerName);
		console.dir(dojo.byId("toggleToDealerName"));
		dojo.html.setDisplay(dojo.byId("toggleToDealerName"), !isDealerName);
		dojo.html.setDisplay(dojo.byId("dealerNameText"), isDealerName); 
		dojo.html.setDisplay(dealerNameSelect.domNode, isDealerName);
		dealerNameSelect.setDisabled(!isDealerName);
		dojo.html.setDisplay(dojo.byId("toggleToDealerNumber"), isDealerName);
	}

	function adjustUIForDcapClaimBox(dealerId, forceRowsDelete){
		dojo.html.setDisplay(dojo.byId("dcap_claims_table_wrapper"), dealerId);
		lastSavedDealerId = dealerId;
	}
