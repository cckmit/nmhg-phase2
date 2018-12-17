dojo.require("twms.widget.Tooltip");

var selectAnItemMessage = "Please select at least one Serial Number.";
var submitButton;
var validationTooltip;
var selectedItemsId= [];

if(idsInString!=null && !idsInString==""){
	selectedItemsId=idsInString.split(",");
} 
dojo.addOnLoad(function() {	
    submitButton = dojo.byId("btnSubmitInventories");	
    var globalCheckBox = dojo.byId("masterCheckbox");	
    
    validationTooltip = new twms.widget.Tooltip({
	        showDelay: 100,
	        connectId: [dojo.byId("submitContainer")]
	    });		
	
   	if(submitButton){
   		bindInventorySelectionForm(submitButton);
   		dojo.connect(submitButton, "onclick", function() {
			dojo.byId("selectedIds").value=selectedItemsId;
	    	dojo.body().style.cursor = "wait";
   		});
   		validateSubmission();
   	}		
	dojo.connect(globalCheckBox, "onclick", function(event) {
		if(event.target.checked){
	    	for (var i = 0; i < rowCount; i++) {
                var currentElement = dojo.byId(String(i));
                currentElement.checked=true;
				var indexOf =dojo.indexOf(selectedItemsId, currentElement.value);		
				if (indexOf == -1) {
					selectedItemsId.push(currentElement.value);	
				}				
			}
		}else{
			for (var j = 0; j < rowCount; j++) {
				var currentElement = dojo.byId(String(j));
				currentElement.checked=false;
			}			
			selectedItemsId=[];
		}
		if(submitButton){
			validateSubmission();
		}
   	});
	
	setSelectedInventories();
    for (var i=0; i<rowCount; i++) {		
        var checkBox = dojo.byId(String(i));        
        dojo.connect(checkBox, "onclick", function(event) {
            var indexOfItem;
            var targetElement = event.target;
            if(targetElement.checked) {
				indexOfItem=dojo.indexOf(selectedItemsId,targetElement.value);
				if (indexOfItem == -1) {
					selectedItemsId.push(targetElement.value);
				}
            } else {
				indexOfItem =dojo.indexOf(selectedItemsId,targetElement.value);
				if (indexOfItem >= 0) {
					selectedItemsId.splice(indexOfItem, 1);
				}
            }
            validateSubmission();
        });
    }
});

function setSelectedInventories(){
	 for (var i=0; i< rowCount; i++) {
        var currentElement = dojo.byId(String(i));
        var indexOf=dojo.indexOf(selectedItemsId,currentElement.value);
		if(indexOf >= 0){
			currentElement.checked=true;
		}
	 }
}
function validateSubmission() {	
    var enableSubmission = true;
    var message;
    if(selectedItemsId.length == 0) {
        enableSubmission = false;
        message = selectAnItemMessage;
    }    	
    if(enableSubmission) {
        submitButton.disabled=false;        
        validationTooltip.clearLabel();
    } else {
       submitButton.disabled=true;
       validationTooltip.setLabel(message);
    }    
}
function bindInventorySelectionForm(button) {
	dojo.connect(dojo.byId("btnSubmitInventories"),"onclick",function(){			
	var params={
			selectedItemsIds:selectedItemsId,
			claim:claimDetail
		};
	var url="";
	var claimType=dojo.byId("claim_type");
	if(claimType && (claimType.value=='Campaign' || claimType.value=='Field Modification')){
	url=url+"displayMultipleEquipInfoForCampaign.action?";
	}else{
	url = url+"displayMultipleEquipInfo.action?";
	}	
	twms.ajax.fireHtmlRequest(url, params, function(data) {			
			dojo.publish("/multipleInventorySearch/itemsSelected",[{responseData : data}]); 				
		}
    );	
    });   
}

dojo.addOnLoad(function(){	
	dojo.connect(dojo.byId("nextButton"), "onclick", function() {
			getInventoriesForNextButton("getInventoriesForNextButton");
   		});
   	dojo.connect(dojo.byId("previousButton"), "onclick", function() {
			getInventoriesForNextButton("getInventoriesForPreviousButton");
   		});
   		
   	document.onkeypress = stopRKey;	
   		
})


function stopRKey(evt) {
  var evt = (evt) ? evt : ((event) ? event : null);
  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
  if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}

 