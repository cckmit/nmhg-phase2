var modelToCheckboxIdMap = {};
var checkboxIdToModelMap = [];
var selectedItemsCount = 0;
var selectAnItemMessage = "Please select at least one Serial Number.";

var submitButton;
var validationTooltip;

dojo.require("twms.widget.NumberTextBox");

function updateSelectedState(data) {
	targetCheckBox = data.checkBox;
	if(targetCheckBox.checked) {
		selectedItemsCount++;
		var indexOfItem=dojo.indexOf(selectedItems,targetCheckBox.value);
		if(indexOfItem < 0){ // add the item only if it is not present in the array
			selectedItems.push(targetCheckBox.value);
		}
	} else {
		var indexOfItem = dojo.indexOf(selectedItems,targetCheckBox.value);
		while(indexOfItem >= 0) {
		    selectedItemsCount--;
			selectedItems.splice(indexOfItem, 1);
			indexOfItem = dojo.indexOf(selectedItems,targetCheckBox.value);
		}
	}
	validateSubmission();
	adjustSelectionConstraints(targetCheckBox.checked, checkboxIdToModelMap[targetCheckBox.id]);
}

dojo.addOnLoad(function() {

    submitButton = dojo.byId("btnSubmitInventories");
    var globalCheckBox = dojo.byId("masterCheckbox");
    var multiCheckBoxControl = new CheckBoxListControl(globalCheckBox);

    // The rowCount variable is available via the action, and hence will have to be declared in the JSP that is
    // including this file, and it has to be done before this script gets included also.

    dojo.subscribe("/checkBoxControl/child/changed",null,updateSelectedState);
    
    for (var i = 0; i < rowCount; i++) {
        var checkBox = dojo.byId("" + i);
        multiCheckBoxControl.addListElement(checkBox);
    }
    if(submitButton){
    	validateSubmission();
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