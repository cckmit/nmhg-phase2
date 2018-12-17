var searchDealersDialog = null;
var searchResultsPane = null;
dojo.addOnLoad(function() {
	searchDealersDialog = dijit.byId("searchDealersDialog");
	searchResultsPane = dijit.byId("searchResults");
})

function showDialog() {
	dojo.byId("p1").value = "";
	if(dojo.byId("p2")) {
		dojo.byId("p2").value = "";
	}
	dojo.byId("searchResults").innerHTML = "";
	searchDealersDialog.show();
}
function closeDialog() {
	searchDealersDialog.hide();
}	


function showSelectedDealers(submitAction) {
	closeDialog();
	addCheckedDealers("includedDealerNames", "includedDealerNames");
	addCheckedDealers("included", "includedDealerNames");
	addCheckedDealers("included_hidden", "includedDealerNames");
	addCheckedDealers("available", "includedDealerNames");
	document.baseForm.action = submitAction +"?" +
		"id=" + dojo.byId("schemeId").value +
		"&dealerGroup=" + dojo.byId("groupId").value;
    document.baseForm.submit();
}

function showSelectedDealerGroups(submitAction) {
	closeDialog();
	addCheckedDealers("includedGroupNames", "includedGroupNames");
	addCheckedDealers("included", "includedGroupNames");
	addCheckedDealers("included_hidden", "includedGroupNames");
	addCheckedDealers("available", "includedGroupNames");
	document.baseForm.action = submitAction;
	document.baseForm.submit();
}

function addCheckedDealers(objName, newEltName) {
	var elts = document.getElementsByName(objName);
	var divElt = document.getElementById("addedDealersHere");
	var selectedElts = new Array();
	
	// Get the selected elements
	for (var i = 0, j = 0; i < elts.length; i++) {
		if (elts[i].checked) {
			selectedElts[j++] = elts[i];
		}
	}
	
	for (elt in selectedElts) {
			var newElt = document.createElement("input");
			newElt.type = "hidden";
			newElt.name = newEltName;
			newElt.value = selectedElts[elt].value;
			divElt.appendChild(newElt);
	}
}