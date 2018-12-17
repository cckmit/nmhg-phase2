var searchItemsDialog = null;
var searchResultsPane = null;
var selectedItemsToAdd = null;
dojo.addOnLoad(function() {
	searchItemsDialog = dijit.byId("searchItemsDialog");
	searchResultsPane = dijit.byId("searchResults");
    selectedItemsToAdd = new Array();
})
	

function showDialog() {
	dojo.byId("p1").value = "";
	dojo.byId("p2").value = "";
	if (dojo.byId("searchResultsForOptions")) {
		dojo.byId("searchResultsForOptions").innerHTML = "";
	}
	if (dojo.byId("searchResults")) {
		dojo.byId("searchResults").innerHTML = "";
	}
	searchItemsDialog.show();
}
function closeDialog() {
	searchItemsDialog.hide();
}	
function searchItems(submitAction, pageNo) {

    var params= {
        "id": dojo.byId("schemeId").value,
        "itemGroup": dojo.byId("groupId").value,
        "itemNumber": dojo.byId("p1").value,
        itemDescription: dojo.byId("p2").value,
        pageNo: pageNo
        };
    handleSelectedItems();    
    var targetContent=dijit.byId('searchResults');
    targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
	
	twms.ajax.fireHtmlRequest(submitAction,params,
            function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;            
            markSelectedItems();
			delete data;
		});

}

function searchSupplierItems(submitAction, pageNo) {
	var params = {};	
	var supplierID  = dojo.query("[name='contract.supplier']")[0].value;
		if (dojo.byId("contract_submit_contract")) {
			params = {
				"supplierId" : supplierID,
				"contract" : dojo.byId("contract_submit_contract").value,
				"itemNumber" : dojo.byId("p1").value,
				pageNo : pageNo
			};
		} else {
			params = {
				"supplierId" : supplierID,
				"itemNumber" : dojo.byId("p1").value,
				pageNo : pageNo
			};
		}
	    handleSelectedItems();    
	    var targetContent=dijit.byId('searchResults');
	   // targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
		
		twms.ajax.fireHtmlRequest(submitAction,params,
	            function(data) {
				var parentDiv = dojo.byId("searchResults");
				parentDiv.innerHTML = data;            
	            markSelectedItems();
				delete data;
			});
}


function markSelectedItems(){
    var arr = document.getElementsByName("available");
    for(var j = 0; j < arr.length; j++){
        if(dojo.indexOf(selectedItemsToAdd,arr[j].value) != -1){
            arr[j].checked = 'true';
        }
    }
}

function handleSelectedItems(){
   var arr = document.getElementsByName("available");
   for(var i = 0; i < arr.length; i++){
       if(arr[i].checked && dojo.indexOf(selectedItemsToAdd, arr[i].value) == -1){
           selectedItemsToAdd.push(arr[i].value); 
       }else if(!arr[i].checked && dojo.indexOf(selectedItemsToAdd, arr[i].value) != -1){
           selectedItemsToAdd.splice(dojo.indexOf(selectedItemsToAdd, arr[i].value), 1)
       }
   }
}

function showSelectedItems(submitAction) {
	closeDialog();
	addCheckedItems("includedItemNumbers", "includedItemNumbers");
	addCheckedItems("included", "includedItemNumbers");
	addCheckedItems("included_hidden", "includedItemNumbers");
	addCheckedItems("available", "includedItemNumbers");
	document.baseForm.action = submitAction +"?" +
	"itemGroup=" + dojo.byId("groupId").value;	
	document.baseForm.submit();	
}

function searchGroups(submitAction) {

	var params={id:dojo.byId("schemeId").value,"itemGroup":dojo.byId("groupId").value,
        "groupName":dojo.byId("p1").value,"groupDescription":dojo.byId("p2").value};
	
	var targetContent=dijit.byId('searchResults');
    targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
	
    twms.ajax.fireHtmlRequest(submitAction,params,function(data) {
			var parentDiv = dojo.byId("searchResults");
			parentDiv.innerHTML = data;
			delete data;
	});
}

function showSelectedGroups(submitAction) {
	closeDialog();
	addCheckedItems("includedGroupNames", "includedGroupNames");
	addCheckedItems("included", "includedGroupNames");
	addCheckedItems("included_hidden", "includedGroupNames");
	addCheckedItems("available", "includedGroupNames");
	document.baseForm.action = submitAction;
	document.baseForm.submit();
}

function addCheckedItems(objName, newEltName) {
	var elts = document.getElementsByName(objName);
	var divElt = document.getElementById("addedItemsHere");
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