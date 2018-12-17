var searchUsersDialog = null;
var searchResultsPane = null;
dojo.addOnLoad(function() {
	searchUsersDialog = dijit.byId("searchUsersDialog");
	searchResultsPane = dijit.byId("searchResults");
});
function showDialog() {
	dojo.byId("p1").value = "";
	if(dojo.byId("p2")) {
		dojo.byId("p2").value = "";
	}
	dojo.byId("searchResults").innerHTML = "";
	searchUsersDialog.show();
}
function closeDialog() {
	searchUsersDialog.hide();
}	
function searchUsers(submitAction) {
	var params = {
        id: dojo.byId("schemeId").value,
        "userGroup": dojo.byId("groupId").value,
        "userName": dojo.byId("p1").value
    }
	var targetContent=dijit.byId('searchResults');
    targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
   
    twms.ajax.fireHtmlRequest(submitAction,params,function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;
			delete data;
		}
    );

}

function showSelectedUsers(submitAction) {
	closeDialog();
	addCheckedUsers("includedUserNames", "includedUserNames");
	addCheckedUsers("included", "includedUserNames");
	addCheckedUsers("included_hidden", "includedUserNames");
	addCheckedUsers("available", "includedUserNames");
	document.baseForm.action = submitAction +"?" +
	"userGroup=" + dojo.byId("groupId").value;	
	document.baseForm.submit();		
}

function searchUserGroups(submitAction) {
	var url = submitAction;
	var params = { 
		id	: dojo.byId("schemeId").value,
		"userGroup" :dojo.byId("groupId").value,
		groupName : dojo.byId("p1").value,
		groupDescription:dojo.byId("p2").value
    };
    var targetContent=dijit.byId('searchResults');
    targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
	
    twms.ajax.fireHtmlRequest(submitAction,params,function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;
			delete data;
		});
}

function showSelectedUserGroups(submitAction) {
	closeDialog();
	addCheckedUsers("includedGroupNames", "includedGroupNames");
	addCheckedUsers("included", "includedGroupNames");
	addCheckedUsers("included_hidden", "includedGroupNames");
	addCheckedUsers("available", "includedGroupNames");
	document.baseForm.action = submitAction;
	document.baseForm.submit();
}

function addCheckedUsers(objName, newEltName) {
	var elts = document.getElementsByName(objName);
	var divElt = document.getElementById("addedUsersHere");
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