function hideLabels(labels) {
    for (var i = 0; i < labels.length; i++) {
        if (labels[i]) {
            dojo.html.hide(labels[i]);
        }
    }
}

function showLabels(labels) {
    for (var i = 0; i < labels.length; i++) {
        if (labels[i]) {
            dojo.html.show(labels[i]);
        }
    }
}

function hideInputs(inputs) {
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        
        if(input) {
            input.value = "";
//            if (dojo.hasClass(input, "erroneousField")) {
//                dojo.removeClass(input, "erroneousField");
//                removeErrorImages(getExpectedParent(input, "td"), input.name);
//            }
            
            dojo.html.hide(input);
        }
    }
}
function showInputs(inputs) {
    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i]) {
            dojo.html.show(inputs[i]);
        }
    }
}
function removeErrorImages(td, forField) {
    var errorImgs = td.getElementsByTagName("img");
    for (var i = 0; i < errorImgs.length; i++) {
        if (errorImgs[i].getAttribute("type") == "error") {
            if (errorImgs[i].getAttribute("for") == forField) {
                dojo.dom.removeNode(errorImgs[i]);
            }
        }
    }
    delete errorImgs;
}


var smrReasonSpan = null;
var smrReasonLabel = null;
var dummySmr = null;

dojo.addOnLoad(function() {
    smrReasonSpan = dojo.byId("smrReasonSpan");
    smrReasonLabel = dojo.byId("smrReasonLabel");
    dummySmr = dojo.byId("dummySmr");
    dojo.dom.removeNode(smrReasonSpan);
});

function showSmrReason() {
    dojo.html.show(smrReasonSpan);
    dojo.html.show(smrReasonLabel);
    dojo.dom.replaceNode(dummySmr, smrReasonSpan);
}


function hideSmrReason() {
    dojo.html.hide(smrReasonSpan);
    dojo.html.hide(smrReasonLabel);
    dojo.dom.replaceNode(smrReasonSpan, dummySmr);
}


function showDealerName() {
	dijit.byId("dealerNameAutoComplete").setDisabled(false);
	showLabels([
        dojo.byId("dealerNameLabel"),
        dojo.byId("toggleToDealerNumber")
    ]);
    hideInputs([
    		dijit.byId("dealerNumberAutoComplete")
    ]);
    hideLabels([
        dojo.byId("dealerNumberLabel"),
        dojo.byId("toggleToDealerName"),
        dojo.byId("dealerNumber")
    ]);
    showInputs([
        dojo.byId("dealerName")
    ]);
	dojo.byId("isDealerNumber").value = "false";
	dijit.byId("dealerNumberAutoComplete").setDisabled(true);
}

function showDealerNumber() {
	dijit.byId("dealerNumberAutoComplete").setDisabled(false);
    showLabels([
        dojo.byId("dealerNumberLabel"),
        dojo.byId("toggleToDealerName")
    ]);
    showInputs([
        dojo.byId("dealerNumber")
    ]);
    hideInputs([
        dijit.byId("dealerNameAutoComplete")
    ]);
    hideLabels([
        dojo.byId("dealerNameLabel"),
        dojo.byId("toggleToDealerNumber"),
        dojo.byId("dealerName")
    ]);
	dojo.byId("isDealerNumber").value = "true";
	dijit.byId("dealerNameAutoComplete").setDisabled(true);
}

function populateDescriptionForPart() {
	 // Inside ClaimForm.js
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
