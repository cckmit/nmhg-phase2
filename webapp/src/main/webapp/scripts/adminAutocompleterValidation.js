dojo.addOnLoad(function() {
    var dealerAutocompleter = dijit.byId("dealerAutoComplete");
    var dealerGroupAutocompleter = dijit.byId("dealerGroupAutoComplete");
    var createButton = dojo.byId("createButton");
    var updateButton = dojo.byId("updateButton");
    var productTypeAutocompleter = dijit.byId("productType");
   // resetForDealerAutocompleter(dealerAutocompleter,createButton,updateButton);
   // resetForDealerGroupAutocompleter(dealerGroupAutocompleter,createButton,updateButton)
   // resetForProductType(productTypeAutocompleter,createButton,updateButton);
});

function resetForDealerAutocompleter(dealerAutocompleter,createButton,updateButton) {
        dojo.connect(dealerAutocompleter, "onkeyup", function() {
        dealerAutocompleter.setValue(dealerAutocompleter.getDisplayedValue());
        if (!dealerAutocompleter.isValid()) {
            disableSaveButton(createButton,updateButton);
        } else {
            enableSaveButton(createButton,updateButton);
        }
    });
}

function resetForProductType(productTypeAutocompleter,createButton,updateButton) {
    dojo.connect(productTypeAutocompleter, "onkeyup", function() {
        productTypeAutocompleter.setValue(productTypeAutocompleter.getDisplayedValue());
        if (!productTypeAutocompleter.isValid()) {
            disableSaveButton(createButton,updateButton);
        } else {
            enableSaveButton(createButton,updateButton);
        }
    });
}

function resetForDealerGroupAutocompleter(dealerGroupAutocompleter,createButton,updateButton) {
    dojo.connect(dealerGroupAutocompleter, "onkeyup", function() {
        dealerGroupAutocompleter.setValue(dealerGroupAutocompleter.getDisplayedValue());
        if (!dealerGroupAutocompleter.isValid()) {
           disableSaveButton(createButton,updateButton);
        } else {
           enableSaveButton(createButton,updateButton);
        }
    });
}

function enableSaveButton(createButton,updateButton) {
    if (createButton) {
        createButton.disabled = false;
    }
    if (updateButton) {
        updateButton.disabled = false;
    }
}

function disableSaveButton(createButton,updateButton) {
    if (createButton) {
        createButton.disabled = true;
    }
    if (updateButton) {
        updateButton.disabled = true;
    }
}

// this validation is checked on click of the save button in 3 flows(Labor,Travel,PolicyModifier)
function validateAutocompleters(){
    var dealerAutocompleter = dijit.byId("dealerAutoComplete");
    var dealerGroupAutocompleter = dijit.byId("dealerGroupAutoComplete");
    var productTypeAutocompleter = dijit.byId("productType");
    if((dealerAutocompleter.getDisplayedValue()!="" && !dealerAutocompleter.isValid()) ||
            (dealerGroupAutocompleter.getDisplayedValue()!="" && !dealerGroupAutocompleter.isValid()) ||
            (productTypeAutocompleter.getDisplayedValue()!="" && !productTypeAutocompleter.isValid())){
        return false;
    }else{
        return true;
    }
}