var modelToCheckboxIdMap = {};
var checkboxIdToModelMap = [];
var selectedItemsCount = 0;
var selectAnItemMessage = "Please select at least one Customer.";

var submitButton;
var validationTooltip;

dojo.require("twms.widget.NumberTextBox");

dojo.addOnLoad(function() {

    submitButton = dojo.byId("addToMergeList");
    var globalCheckBox = dojo.byId("masterCheckbox");
    var multiCheckBoxControl = new CheckBoxListControl(globalCheckBox);


    // The rowCount variable is available via the action, and hence will have to be declared in the JSP that is
    // including this file, and it has to be done before this script gets included also.

    for (var i = 0; i < rowCount; i++) {
        var checkBox = dojo.byId("" + i);

        multiCheckBoxControl.addListElement(checkBox);

        dojo.connect(checkBox, "onchange", function(event) {
            var changedCheckBox = event.target;
            var wasChecked = changedCheckBox.checked;

            if(wasChecked) {
                selectedItemsCount++;
            } else {
                selectedItemsCount--;
            }

            validateSubmission();

            adjustSelectionConstraints(wasChecked, checkboxIdToModelMap[changedCheckBox.id]);

        });
    }
    if(submitButton){
	    dojo.connect(submitButton, "onclick", function() {
			dojo.byId("selectedIds").value=selectedItems;
	    	dojo.body().style.cursor = "wait";
	   	});
    }

});

function adjustSelectionConstraints(wasChecked, currentModel) {
    var model;
    var checkboxId;
    var allUnchecked = (selectedItemsCount == 0);
    var firstTimeChecked = (selectedItemsCount == 1 && wasChecked);

    if(allUnchecked || firstTimeChecked) {
        for(model in modelToCheckboxIdMap) {
            if(model != currentModel) {
                for(checkboxId in modelToCheckboxIdMap[model]) {
                    dojo.byId(checkboxId).disabled = firstTimeChecked;
                }
            }
        }
    }
}

function validateSubmission() {

    if(selectedItemsCount == 0) {
    	if(submitButton){
        	submitButton.setAttribute("disabled", "disabled");
    	}
    } else {
    	if(submitButton){    	
        	submitButton.removeAttribute("disabled");
    	}
    }
}

function addToModelCheckboxIdMap(model, checkboxId) {
    if(typeof modelToCheckboxIdMap[model] == "undefined") {
        modelToCheckboxIdMap[model] = [];
    }

    modelToCheckboxIdMap[model].push(checkboxId);
    checkboxIdToModelMap[checkboxId] = model;
}