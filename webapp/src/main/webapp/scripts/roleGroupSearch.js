var searchRolesDialog = null;
var searchResultsPane = null;
dojo.addOnLoad(function() {
	searchRolesDialog = dijit.byId("searchRolesDialog");
	searchResultsPane = dijit.byId("searchResults");
	dojo.byId("dialogBoxContainer").style.display = "block";
});
function showDialog() {
	dojo.byId("p1").value = "";
	if(dojo.byId("p2")) {
		dojo.byId("p2").value = "";
	}
	dojo.byId("searchResults").innerHTML = "";
	searchRolesDialog.show();
}
function closeDialog() {
	searchRolesDialog.hide();
}	
function searchRoles(submitAction) {
	var params = {
        id: dojo.byId("schemeId").value,
        "roleGroup.id": dojo.byId("groupId").value,
        "roleName": dojo.byId("p1").value
    }

    var includedRoleNamesParam=[];
	var elts = document.getElementsByName("includedRoleNames");		
	for (var i = 0; i < elts.length; i++) {
		includedRoleNamesParam.push(elts[i].value);
	}
	elts = document.getElementsByName("included");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedRoleNamesParam.push(elts[i].value);
        }
	}
	elts = document.getElementsByName("available");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedRoleNamesParam.push(elts[i].value);
        }
	}
	elts = document.getElementsByName("included_hidden");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedRoleNamesParam.push(elts[i].value);
        }
	}
    params.includedRoleNames=includedRoleNamesParam;

    twms.ajax.fireHtmlRequest(submitAction,params,function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;
			delete data;
		}
    );

}

function showSelectedRoles(submitAction) {
	closeDialog();
	addCheckedRoles("includedRoleNames", "includedRoleNames");
	addCheckedRoles("included", "includedRoleNames");
	addCheckedRoles("included_hidden", "includedRoleNames");
	addCheckedRoles("available", "includedRoleNames");
	document.baseForm.action = submitAction;
	document.baseForm.submit();
}

function searchRoleGroups(submitAction) {
	var url = submitAction;
	var params = { 
		id	: dojo.byId("schemeId").value,
		roleGroup: {
			id: dojo.byId("groupId").value
		},
		groupName : dojo.byId("p1").value,
		groupDescription:dojo.byId("p2").value
    };
	var includedGroupNamesParamValues=[];
	var elts = document.getElementsByName("includedGroupNames");		
	for (var i = 0; i < elts.length; i++) {
		includedGroupNamesParamValues.push(elts[i].value);
	}
	elts = document.getElementsByName("included");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedGroupNamesParamValues.push(elts[i].value);
        }
	}
	elts = document.getElementsByName("available");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedGroupNamesParamValues.push(elts[i].value);
        }
	}
	elts = document.getElementsByName("included_hidden");
	for (var i = 0; i < elts.length; i++) {
		if (elts[i].checked){
            includedGroupNamesParamValues.push(elts[i].value);
        }
	}

    params.includedGroupNames=includedGroupNamesParamValues;
    twms.ajax.fireHtmlRequest(submitAction,params,function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;
			delete data;
		});
}

function showSelectedRoleGroups(submitAction) {
	closeDialog();
	addCheckedRoles("includedGroupNames", "includedGroupNames");
	addCheckedRoles("included", "includedGroupNames");
	addCheckedRoles("included_hidden", "includedGroupNames");
	addCheckedRoles("available", "includedGroupNames");
	document.baseForm.action = submitAction;
	document.baseForm.submit();
}

function addCheckedRoles(objName, newEltName) {
	var elts = document.getElementsByName(objName);
	var divElt = document.getElementById("addedRolesHere");
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