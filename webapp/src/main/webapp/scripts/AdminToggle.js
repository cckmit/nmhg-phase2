/*  General methods  */
function hideLabels(labels) {
    for (var i in labels) {
        dojo.html.hide(labels[i]);
    }
}
function showLabels(labels) {
    for (var i in labels) {
        dojo.html.show(labels[i]);
    }
}

function hideInputs(inputs) {
    for (var i in inputs) {
        var input = inputs[i];
        input.value = "";
    }
}
function showInputs(inputs) {
    for (var i in inputs) {
        dojo.html.show(inputs[i]);
    }
}

/*  Methods for dealer/ dealerGroup handling  */
function showDealer() {
		showLabels([
        dojo.byId("dealerLabel"),
        dojo.byId("toggleToDealerGroup")
    ]);
    hideInputs([
    		dijit.byId("dealerGroupAutoComplete")
    ]);
    hideLabels([
        dojo.byId("dealerGroupLabel"),
        dojo.byId("toggleToDealer"),
        dojo.byId("dealerGroup")
    ]);
    showInputs([
        dojo.byId("dealer")
    ]);
    dojo.byId("isDealerGroup").value = "false";
}

function showDealerGroup() {
    showLabels([
        dojo.byId("dealerGroupLabel"),
        dojo.byId("toggleToDealer")
    ]);
    showInputs([
        dojo.byId("dealerGroup")
    ]);
    hideInputs([
        dijit.byId("dealerAutoComplete")
    ]);
    hideLabels([
        dojo.byId("dealerLabel"),
        dojo.byId("toggleToDealerGroup"),
        dojo.byId("dealer")
    ]);
    dojo.byId("isDealerGroup").value = "true";
}

/*  Methods for item/itemGroup handling  */
function showItem() {
		showLabels([
        dojo.byId("itemLabel"),
        dojo.byId("toggleToItemGroup")
    ]);
    hideInputs([
    		dijit.byId("itemGroupAutoComplete")
    ]);
    hideLabels([
        dojo.byId("itemGroupLabel"),
        dojo.byId("toggleToItem"),
        dojo.byId("itemGroup")
    ]);
    showInputs([
        dojo.byId("item")
    ]);
    dojo.byId("isItemGroup").value = "false";
}

function showItemGroup() {
    showLabels([
        dojo.byId("itemGroupLabel"),
        dojo.byId("toggleToItem")
    ]);
    showInputs([
        dojo.byId("itemGroup")
    ]);
    hideInputs([
        dijit.byId("itemAutoComplete")
    ]);
    hideLabels([
        dojo.byId("itemLabel"),
        dojo.byId("toggleToItemGroup"),
        dojo.byId("item")
    ]);
    dojo.byId("isItemGroup").value = "true";
}


/*  Methods for item/itemGroup handling  */
function showItemWithIndex(index) {
	console.debug('inside item with index' + index);
		showLabels([
        dojo.byId("itemLabel_"+index),
        dojo.byId("toggleToItemGroup_"+index)
    ]);
    hideInputs([
    		dijit.byId("itemGroupAutoComplete_"+index)
    ]);
    hideLabels([
        dojo.byId("itemGroupLabel_"+index),
        dojo.byId("toggleToItem_"+index),
        dojo.byId("itemGroup_"+index)
    ]);
    showInputs([
        dojo.byId("item_"+index)
    ]);
    dojo.byId("isItemGroup_"+index).value = "false";
}

function showItemGroupWithIndex(index) {
	console.debug('inside item group with index' + index);
    showLabels([
        dojo.byId("itemGroupLabel_"+index),
        dojo.byId("toggleToItem_"+index)
    ]);
    showInputs([
        dojo.byId("itemGroup_"+index)
    ]);
    hideInputs([
        dijit.byId("itemAutoComplete_"+index)
    ]);
    hideLabels([
        dojo.byId("itemLabel_"+index),
        dojo.byId("toggleToItemGroup_"+index),
        dojo.byId("item_"+index)
    ]);
    dojo.byId("isItemGroup_"+index).value = "true";
}


