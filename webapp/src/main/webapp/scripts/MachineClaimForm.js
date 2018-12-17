function showSerialNumber() {

    if (dijit.byId("modelNumber")) {
        dojo.html.hide(dijit.byId("modelNumber").domNode);
    }
    if (dijit.byId("serialNumber")) {
        dojo.html.show(dijit.byId("serialNumber").domNode);
    }

	dojo.html.hide(dojo.byId("invalidSerialNumber"));

    hideLabels([    	
        dojo.byId("modelNumberLabel"),
        dojo.byId("serialNumberForNonSerializedClaimLabel")
    ]);
    
    showLabels([
    	dojo.byId("serialNumberLabel"),
        dojo.byId("viewLink")
    ]);

}

function showModelNumber() {
	if (dijit.byId("modelNumber")) {
        dojo.html.show(dijit.byId("modelNumber").domNode);
    }
    if (dijit.byId("serialNumber")) {
        dojo.html.hide(dijit.byId("serialNumber").domNode);
    }

    dojo.html.show(dojo.byId("invalidSerialNumber"));
    showLabels([
    	dojo.byId("serialNumberForNonSerializedClaimLabel"),
        dojo.byId("modelNumberLabel")
    ]);
    
    hideLabels([
        dojo.byId("serialNumberLabel"),
        dojo.byId("viewLink")
    ]);
}


function serialNumberUIHandlingForNonSerialized(isSerialNumber)
{
    if (!isSerialNumber) {
        showModelNumber();
        dojo.byId("isSerialized").value = false;
        isSerialNumber = false;
        dojo.byId("nonSerializedCheck").checked = true;
    }
    else
    {
        showSerialNumber();
        dojo.byId("isSerialized").value = true;
        isSerialNumber = true;
        dojo.byId("nonSerializedCheck").checked = false;
    }
}