function toggleAuto(hide, hiddenAuto) {
    hide.domNode.parentNode.replaceChild(hiddenAuto.domNode, hide.domNode);
}

function toggleSerialAndPartNumber(hide) {
    toggleAuto(hide, hiddenSerialOrPartNumberAuto);
    hiddenSerialOrPartNumberAuto = hide;
}

function showSerialNumber() {
    toggleSerialAndPartNumber( dijit.byId("causalPart_itemNo") );
    showLabels([
        dojo.byId("toggleToPart"),
        dojo.byId("serialNumberLabel")
    ]);
    hideLabels([
        dojo.byId("toggleToSerial"),
        dojo.byId("partNumberLabel")
    ]);
}

function showPartNumber() {
    toggleSerialAndPartNumber( dijit.byId("causalPart_slNo") );
    showLabels([
        dojo.byId("toggleToSerial"),
        dojo.byId("partNumberLabel")
    ]);
    hideLabels([
        dojo.byId("toggleToPart"),
        dojo.byId("serialNumberLabel")
    ]);
}

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

function fillCausalPartDescription(url, number) {
    twms.ajax.fireJavaScriptRequest(url, { number: number },
        function(details) {
                dojo.byId("causalPartDescription").innerHTML = details[0];
        }, null, false);
}

function wireCausedByToBeDependantOnFaultFound(data, type, request) {
    if (type == "valuechanged") {
        var cbox = dijit.byId("causedBy");
        var url = cbox.dataUrl;
        url = url.replace(/&faultFound=.*/,
                "&faultFound=" + data);
        cbox.dataUrl = url;
        delete cbox,url;
    }
}