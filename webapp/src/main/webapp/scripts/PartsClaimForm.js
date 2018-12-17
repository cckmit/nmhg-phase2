function toggleAuto(hide, hiddenAuto) {
    var backingInput = hide.comboBoxValue;
    var shownInput = hide.textInputNode;

//    if (dojo.hasClass(shownInput, "erroneousField")) {
//        dojo.removeClass(shownInput, "erroneousField");
//        removeErrorImages(getExpectedParent(shownInput, "td"), backingInput.name);
//    }

    hide.domNode.parentNode.replaceChild(hiddenAuto.domNode, hide.domNode);
}


var hiddenSerialOrPartNumberAuto;
var hiddenRow = null;
function toggleSerialAndPartNumber(hide) {
    toggleAuto(hide, hiddenSerialOrPartNumberAuto);
    hiddenSerialOrPartNumberAuto = hide;
}
function showSerialNumber() {
    toggleSerialAndPartNumber( dijit.byId("partNumber") );
    showLabels([
        dojo.byId("serialNumberLabel"),
        dojo.byId("warrantyStartDateLabel"),
        dojo.byId("warrantyStartDate"),
        dojo.byId("toggleToPart")
    ]);
    hideLabels([
        dojo.byId("partNumberLabel"),        
        dojo.byId("toggleToSerial")
    ]);
    /*if (hiddenRow == null) {
        var installed = dojo.byId("isInstalled")
        installed.checked = false;
        var trs = getExpectedParent(installed, "tbody").getElementsByTagName("tr");
        hiddenRow = dojo.dom.removeNode(trs[trs.length - 1]);
    }*/
}
function showPartNumber() {
    toggleSerialAndPartNumber( dijit.byId("serialNumber") );
    showLabels([
        dojo.byId("partNumberLabel"),        
        dojo.byId("toggleToSerial")
    ]);
    hideLabels([
        dojo.byId("serialNumberLabel"),
        dojo.byId("warrantyStartDateLabel"),
        dojo.byId("toggleToPart")
    ]);
}

var hiddenEqpSerialOrItemNumberAuto;
function toggleEqpSerialAndItemNumber(hide) {
    toggleAuto(hide, hiddenEqpSerialOrItemNumberAuto);
    hiddenEqpSerialOrItemNumberAuto = hide;
}
function showEqpSerialNumber() {
	dojo.html.show(dijit.byId("eqpSerialNumber").domNode);
    dojo.html.show(dojo.byId("warrantyStartDate"));
    showLabels([
        dojo.byId("eqpSerialNumberLabel"),
        dojo.byId("warrantyStartDateLabel"),
        dojo.byId("toggleToEqpModel")
    ]);
    hideLabels([
        dojo.byId("eqpModelNumberLabel"),
        dojo.byId("toggleToEqpSerial"),
        dojo.byId("serialNumberForNonSerializedEqpClaimLabel")
    ]);
    dojo.html.hide(dijit.byId("eqpModelNumber").domNode);
	dojo.html.hide(dojo.byId("invalidEqpSerialNumber"));
}
function showEqpModelNumber() {
	dojo.html.show(dijit.byId("eqpModelNumber").domNode);
    showLabels([
        dojo.byId("eqpModelNumberLabel"),
        dojo.byId("serialNumberForNonSerializedEqpClaimLabel"),        
        dojo.byId("toggleToEqpSerial")
    ]);
    hideLabels([
        dojo.byId("eqpSerialNumberLabel"),
        dojo.byId("warrantyStartDateLabel"),
        dojo.byId("toggleToEqpModel")
    ]);    
    dojo.html.hide(dijit.byId("warrantyStartDate").domNode);
	dojo.html.show(dojo.byId("invalidEqpSerialNumber"));  
    dojo.html.show(dijit.byId("eqpModelNumber").domNode);
	dojo.html.hide(dijit.byId("eqpSerialNumber").domNode);
}

function preparePartInstalled(partInstalled)
{
	// Need to write the logic
	if (partInstalled)
	{
        showLabels([
    	            dojo.byId("hoursInServiceLabel")
    	        ]);
    	dojo.html.show(dojo.byId("hoursInService"));  
	}
	else
	{
        hideLabels([
    	            dojo.byId("hoursInServiceLabel")
    	        ]);
    	dojo.html.hide(dojo.byId("hoursInService"));  
	}
}

	function populateDescriptionForPart() {
		// Inside PartsClaimForm.js
		var currentBrandPartNo = dijit.byId("brandPartNumber").getValue();
		console.debug("Brand Part Number read is " + "[" + currentBrandPartNo + "]");
		 
		if (currentBrandPartNo) {
			twms.ajax.fireHtmlRequest("list_description_for_part.action", {"number": currentBrandPartNo}, function(data) {
				dijit.byId("desc_for_part").setContent(eval(data)[0]);
				dojo.byId("partNumber").value = eval(data)[2];
			});
		}
		else { // Clear existing description from previous value
			dijit.byId("desc_for_part").setContent("");
			dojo.byId("partNumber").value = "";
		}	
	}

	 function populateDescAndPartForInventory() {     	 
		 if(dojo.byId("selectedPartInvItemHidden")){
	   	  twms.ajax.fireHtmlRequest("list_description_part_for_inventory.action", {"inventoryId": dojo.byId("selectedPartInvItemHidden").value}, function(data) {  
	   		  dijit.byId("desc_for_part").setContent(eval(data)[0]);    
	       	 dijit.byId("partNumber").setDisabled(false);
	       	  dojo.byId("partNumber").value=eval(data)[1];     
	       	 dijit.byId("partNumber").setDisabled(true);
	   	  });  
		 }
	     }
	 
		function onPartNotInstalled()
		{
			try
			{
			dijit.byId("eqpSerialNumber").setDisabled(true); 
			dojo.html.hide(dijit.byId("eqpSerialNumber").domNode); 
			dijit.byId("eqpSerialNumber").setValue(""); 
			dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));            
			}
			catch(e)
			{
				console.debug("Equipment Serial Number not applicable");
			}    
			if(dojo.byId("partInstalledHidden")){
	    	dojo.byId("partInstalledHidden").value="false";
			}		
	        if(dojo.byId("selectedInvItemHidden")){
	        	dojo.byId("selectedInvItemHidden").value="";
	        }               
	        dojo.byId("serializedId").value=false;
	        dojo.html.hide(dojo.byId("hoursInServiceLabel"));
	        dojo.byId("hoursInService").disable=true;
	        dojo.byId("hoursInService").value="";
	        dojo.html.hide(dojo.byId("hoursInService"));
	        dojo.html.hide(dojo.byId("hoursOnPartLabel"));
			dojo.byId("hoursOnPart").disable = true;
			dojo.byId("hoursOnPart").value="";
			try
			{
			dojo.html.hide(dijit.byId("productModel").domNode); 
			dojo.html.hide(dojo.byId("productModelLabel"));
			}
			catch(e)
			{
			console.debug("Product Model Not Captured");
			}
	        dojo.html.hide(dojo.byId("hoursOnPart"));  
	        try
			{      
	        dojo.html.hide(dojo.byId("warrantyStartDateLabel"));
	        dijit.byId("warrantyStartDate").setDisabled(true);
	        dijit.byId("warrantyStartDate").setValue("");
	        dojo.html.hide(dijit.byId("warrantyStartDate").domNode);
			}
			catch(e)
			{
			console.debug("Warranty Start Date is not Captured");	
			}
	        if(dojo.byId("isSerialNumber").value != 'true' && !dojo.byId("toggleToProductModel").checked)
	        {
	        dojo.html.show(dojo.byId("purchaseDateLabel"));
	        dijit.byId("purchaseDate").setDisabled(false);                    
	        dojo.html.show(dijit.byId("purchaseDate").domNode);
	        }      
		}	
		
		function onPartInstalled()
		{
			showSerialNumberSection();    	
	        dojo.byId("serializedId").value=true;
	        dojo.html.show(dojo.byId("hoursInServiceLabel"));
	        dojo.byId("hoursInService").disable=false;                    
	        dojo.html.show(dojo.byId("hoursInService"));
	        dojo.html.show(dojo.byId("hoursOnPartLabel"));
			dojo.byId("hoursOnPart").disable = false;
	        dojo.html.show(dojo.byId("hoursOnPart"));  
	    	dojo.byId("partInstalledHidden").value="true";   
	        if(dojo.byId("isSerialNumber").value != 'true')
	        {           
	        dojo.html.show(dojo.byId("warrantyStartDateLabel"));
	        dijit.byId("warrantyStartDate").setDisabled(false);
	        dojo.html.show(dijit.byId("warrantyStartDate").domNode);
	        }
	        dojo.html.hide(dojo.byId("purchaseDateLabel"));
	        dijit.byId("purchaseDate").setDisabled(true);
	        dijit.byId("purchaseDate").setValue("");
	        dojo.html.hide(dijit.byId("purchaseDate").domNode);        
		}

		   function hideSerialNumberSection(){
		    	 if(dijit.byId("eqpSerialNumber")){
		    	        dojo.html.hide(dijit.byId("eqpSerialNumber").domNode);
		    	        
		    	        }
		    	        if(dojo.byId("eqpSerialNumberLabel")){
		    	        dojo.html.hide(dojo.byId("eqpSerialNumberLabel"));
		    	        }
		    	        if(dijit.byId("productModel")){
		    	            dojo.html.hide(dijit.byId("productModel").domNode);
		    	            
		    	        }
		    	        if(dojo.byId("productModelLabel")){
		    	            dojo.html.hide(dojo.byId("productModelLabel"));
		    	        }
		    	       
		    	       

		    }
		    function showSerialNumberSection(){      
		        if (dojo.byId("toggleToProductModel").checked)
		        {
		         	 showProductModel();
		        }
		        else if(dojo.byId("partInstalled").checked)
		        {      
		       		showEqSerialNumber();
		        }
		    }
		    
		    