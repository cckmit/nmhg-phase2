dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane");
dojo.require("dijit.form.ComboBox");
dojo.require("twms.widget.Dialog");

var dlg=dijit.byId("labelsDialog");
var selectedRowIds;
var selectedLabels;
var emptyStateMap;

dojo.addOnLoad(function() {
	dlg = dijit.byId("labelsDialog");
	selectedRowIds = new Array();
	selectedLabels = new Array();
	emptyStateMap = {value:"", label:""};
	var labelDiv= dojo.byId("labels_div");
	dojo.html.hide(dojo.byId("textfieldForLabel"));
	dojo.connect(dijit.byId('labelsDialog'),"hide", function() {
		clearDialog();
		selectedRowIds = new Array();
		dojo.byId('forErrors').innerHTML = "";
        dojo.html.hide(dojo.byId("noSearchParamsErrorSection"));
    });
	dojo.connect(dojo.byId("forAutoComplete"),"onclick",function(){
		if(dojo.byId("forAutoComplete").checked){
			dojo.html.hide(dojo.byId("textfieldForLabel"));			
    		dojo.byId("textfieldForLabel").disabled=true;	
    		dojo.html.show(dijit.byId("labelAutoComplete").domNode);
    		dijit.byId("labelAutoComplete").setDisabled(false);
			dijit.byId("labelAutoComplete").setValue("");			
		}
	});
	dojo.connect(dojo.byId("fortextField"),"onclick",function(){
		if(dojo.byId("fortextField").checked){						
    		dojo.html.hide(dijit.byId("labelAutoComplete").domNode);
			dijit.byId("labelAutoComplete").setDisabled(true);
			dojo.html.show(dojo.byId("textfieldForLabel"));	
			dojo.byId("textfieldForLabel").disabled=false;		
    		dojo.byId("textfieldForLabel").value="";    				
		}
	});
	
});

function showLabels(event, dataIds) {	
	selectedRowIds = new Array();
	for(var i in dataIds) {
		selectedRowIds.push(dataIds[i]);
	}	
	dlg.show();
	if(dojo.byId("forAutoComplete").checked){
		dojo.html.hide(dojo.byId("textfieldForLabel"));			
		dojo.byId("textfieldForLabel").disabled=true;	
		dojo.html.show(dijit.byId("labelAutoComplete").domNode);
		dijit.byId("labelAutoComplete").setDisabled(false);
		dijit.byId("labelAutoComplete").setValue("");		
	}
	else if(dojo.byId("fortextField").checked){	
		dojo.html.hide(dijit.byId("labelAutoComplete").domNode);
		dijit.byId("labelAutoComplete").setDisabled(true);
		dojo.html.show(dojo.byId("textfieldForLabel"));	
		dojo.byId("textfieldForLabel").disabled=false;		
		dojo.byId("textfieldForLabel").value=""; 		
	}
}

function addLabel() {	
	if(dojo.byId("forAutoComplete").checked && dijit.byId('labelAutoComplete').isValid()){
        var addedLabel = dijit.byId('labelAutoComplete').getDisplayedValue();
		if(addedLabel != "" && !contains(selectedLabels, addedLabel)) {
		selectedLabels.push(addedLabel);		
		dijit.byId('labelAutoComplete').setDisplayedValue("");
		}
		renderLabels();
	}else if(dojo.byId("fortextField").checked){
        var addedLabel = dojo.byId("textfieldForLabel").value;
		var params={createLabel:addedLabel};
		 twms.ajax.fireHtmlRequest(checkActionUrl,params,function(data){		 			 
		 	var validationFailed = (data == "<true>");
        	dojo.html.setDisplay(dojo.byId("noSearchParamsErrorSection"), validationFailed);
        	if(validationFailed==true){
        		data="";
                dojo.byId('forErrors').innerHTML = "";
            }
			if(addedLabel != "" && !contains(selectedLabels, addedLabel) && data!="") {	
				selectedLabels.push(addedLabel);
				dojo.byId("textfieldForLabel").value="";
				dojo.html.hide(dojo.byId("removeLabel"));
				}			
			delete data;
			renderLabels();
		});		
	}	
}

function renderLabels() {
	var labelsListDiv = dojo.byId("listOfLabels");
	var toBeRendered = "";	
	for(var i in selectedLabels) {
		if(i != 0) {
			toBeRendered += ", ";
		}
		toBeRendered += selectedLabels[i];
	}
	labelsListDiv.innerHTML = toBeRendered;
}

function applyLabel() {

    var params={};
    for(var i in selectedLabels) {
		params["chosenLabels["+i+"]"]=selectedLabels[i];
	}
	for(var i in selectedRowIds) {
		params["dataIds["+i+"]"]=selectedRowIds[i];
	}
    twms.ajax.fireHtmlRequest(applyActionUrl,params,function(data) {
			dojo.byId('forErrors').innerHTML = data;
            dojo.html.hide(dojo.byId("noSearchParamsErrorSection"));
            clearDialog();
			delete data;
		});
}

function removeLabel() {
	var url = removeActionUrl;
	 var params={};
    for(var i in selectedLabels) {
		params["chosenLabels["+i+"]"]=selectedLabels[i];
	}
	for(var i in selectedRowIds) {
		params["dataIds["+i+"]"]=selectedRowIds[i];
	}
	twms.ajax.fireHtmlRequest(url,params,function(data) {
			dojo.byId('forErrors').innerHTML = data;
            dojo.html.hide(dojo.byId("noSearchParamsErrorSection"));
            clearDialog();
			delete data;
		});

}

function clearDialog() {
	selectedLabels = new Array();
	clearList();
}

function clearList() {
	selectedLabels = new Array();
	dojo.byId("listOfLabels").innerHTML="";
	dojo.html.show(dojo.byId("removeLabel"));
}

function contains(array, object) {
	for(var i in array) {
		if(array[i] == object) return true;
	}
	return false;
}

function validate(inputComponent) {

}