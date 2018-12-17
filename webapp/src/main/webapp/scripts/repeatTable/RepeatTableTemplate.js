dojo.require("dojox.layout.ContentPane");
dojo.require("twms.widget.NumberTextBox");
dojo.require("twms.widget.ValidationTextBox");
dojo.require("twms.widget.Select");
dojo.require("dijit.form.CheckBox");

var subTemplateHussmanInstalled = "<tr><td></td><td></td></tr>";
var subTemplateNonHussman = "<tr><td></td><td></td><td></td><td></td></tr>";



function connectValueAddButton(inIndex,subRowIndex){
		dojo.connect(dojo.byId("addRepeatRow"), "onclick", function(evt){
			var inIndexes ;
			var isProcessorReview = dojo.byId("processorReview").value;
			if (isProcessorReview == 'true') {
            	inIndexes = dojo.byId("addRepeatBody_processor").rows;
            } else {
            	inIndexes = dojo.byId("addRepeatBody").rows;
            }
             if(inIndexes.length!=0){
             	 inIndex = inIndexes.length;
                 inIndex = parseInt(inIndex);
             }
             else {
             	inIndex = 0;
             }
             subRowIndex=parseInt(subRowIndex);

			addRowTable(inIndex,subRowIndex);
            var downloadLinkInit = dojo.byId("downloadInvoice"+inIndex);
            var attachInvoiceLinkInit = dojo.byId("invoice"+inIndex);
            var attachedInvoiceIdInit = dojo.byId("hiddenInvoice_"+inIndex);
             dojo.html.hide(downloadLinkInit);
                dojo.connect(dojo.byId("invoice" + inIndex), "onclick", function(doc) {
                    attachInvoiceFunction(function(doc) {
                        showFileDownloadLink(attachInvoiceLinkInit,downloadLinkInit,
                                attachedInvoiceIdInit, doc[0].id, doc[0].name);
                    });
                });
            dojo.connect(_getUploadDropButton(downloadLinkInit), "onclick", function() {
                dropAttachedInvoice(attachInvoiceLinkInit, downloadLinkInit,attachedInvoiceIdInit);
            });
             dojo.stopEvent(evt);
		});
            
}
            
function addRowTable(incInd,subIncIndex){
		var template = "<tr><td><table><tbody><tr><td></td><td></td>";
		var isProcessorReview = dojo.byId("processorReview").value;
		if (isProcessorReview == 'true') {
			template=template+"<td></td><td></td><td></td><td></td>";
		}
		template=template+"<td><div class='repeat_add' id='${addPartReplaced}' width='10%'/></td></tr></tbody></table></td>"+
			   "<td><table><tbody><tr><td></td><td></td><td><div class='repeat_add' id='${hussAddPartInstalled}' width='10%'/></td></tr></tbody></table></td>"+
			   "<td><table><tbody><tr><td></td><td></td><td></td><td></td><td><div class='repeat_add' id='${nonHussAddPartInstalled}' width='10%'/></td></tr></tbody></table></td>"+
               "<td><span id='${invoice}'>Attach Invoice</span> <a id='${downloadInvoice}'><span class='documentName'></span>" +
               "<span><img class='dropUpload' src='image/remove.gif' id='${deleteForUpload}'/></span></a>"+
			   "<td><div class='repeat_del' id='${deleteRow}'/></td></tr>";
        var partNumberText;
        var quantityText;
        var descriptionText;
        var markupTxt = dojo.string.substitute(template,{"addPartReplaced":"addPartReplaced_"+incInd+"_"+subIncIndex,
            "hussAddPartInstalled":"hussAddPartInstalled_"+incInd+"_"+subIncIndex,
            "nonHussAddPartInstalled":"nonHussAddPartInstalled_"+incInd+"_"+subIncIndex,
            "invoice":"invoice"+incInd,
            "downloadInvoice":"downloadInvoice"+incInd,
            "deleteForUpload":"deleteForUpload"+incInd,
            "deleteRow":"deleteRow_"+incInd+"_"+subIncIndex});
        var tableBodyNode ;
        if(isProcessorReview == 'true') {
        	tableBodyNode = dojo.byId("addRepeatBody_processor");
        } else {
        	tableBodyNode = dojo.byId("addRepeatBody");
        }
        var tableRow = dojo.html.createNodesFromText(markupTxt);
        if(tableRow instanceof Array){
         	//Do Nothing
         }
         else
         {
        	var tableRowArray = {};
          	tableRowArray[0] = tableRow;
          	tableRow = tableRowArray;
        }
        //dynamically assigning an ID value for the table Body.
        tableRow[0].childNodes[0].childNodes[0].childNodes[0].setAttribute("id","addReplacedSubBody_"+incInd);
        tableRow[0].childNodes[1].childNodes[0].childNodes[0].setAttribute("id","addHussInstalledSubBody_"+incInd);
        tableRow[0].childNodes[2].childNodes[0].childNodes[0].setAttribute("id","addNonHussInstalledSubBody_"+incInd);
        // the tableRow[0].childNodes[0] gives the <td> for the outer table. the second childNodes gives the <table> node.
        // the third childNodes gives the <tbody> node. the fourth childNodes gives the <tr> node and finally
        // the fifth childNodes gives the <td> node for the inner table...
        // the hierarchy is : <table><tr>.<td>.<table>.<tbody>.<tr>.<td>
		var tableDataOne = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
        tableDataOne.setAttribute("width","2%");
        partNumberText = getPartNumberTextField("replacedPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].itemReference.referredItem");
        tableDataOne.appendChild(partNumberText.domNode);
        var tableDataTwo = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
        tableDataTwo.setAttribute("width","2%");
        quantityText = getQuantityTextField("replacedQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].numberOfUnits");
        tableDataTwo.appendChild(quantityText.domNode);
        if (isProcessorReview == 'true') {
        	var tableDataThirteen = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
        	tableDataThirteen.setAttribute("width","2%");
        	partNumberText = getPartReturnCheckTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_toBeReturned","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partToBeReturned",incInd,subIncIndex);
        	tableDataThirteen.appendChild(partNumberText);
        	var tableDataFourteen = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[3];
        	tableDataFourteen.setAttribute("width","2%");
        	partNumberText = getReturnLocationTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_location","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.returnLocation");
        	tableDataFourteen.appendChild(partNumberText.domNode);
        	var tableDataFifteen  = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[4];
        	tableDataFifteen.setAttribute("width","2%");
        	partNumberText = getPaymentDescriptionTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_paymentCondition","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.paymentCondition");
        	tableDataFifteen.appendChild(partNumberText);
        	var tableDataSixteen  = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[5];
        	tableDataSixteen.setAttribute("width","2%");
        	partNumberText = getDueDaysTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_dueDays","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.dueDays");
        	tableDataSixteen.appendChild(partNumberText.domNode);
        	var tableDataThree = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[6];
        	tableDataThree.setAttribute("width","10%");
        	var incRepeatSubAdd1 = tableDataThree.lastChild;
       		 dojo.connect(incRepeatSubAdd1,"onclick",function(evt) {
        	  connectReplacedPartsSubButton(incInd,subIncIndex);
			  dojo.stopEvent(evt);
       		});
        }
        else {
	        var tableDataThree = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
	        tableDataThree.setAttribute("width","10%");
	        var incRepeatSubAdd1 = tableDataThree.lastChild;
	        dojo.connect(incRepeatSubAdd1,"onclick",function(evt) {
	        	  connectReplacedPartsSubButton(incInd,subIncIndex);
				  dojo.stopEvent(evt);
	        });
	    }
        var tableDataFour = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
        tableDataFour.setAttribute("width","2%");
        partNumberText = getPartNumberTextField("installedHussmanPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].item");
        tableDataFour.appendChild(partNumberText.domNode);
        var tableDataFive = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
        tableDataFive.setAttribute("width","2%");
        quantityText = getQuantityTextField("installedHussmanQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].numberOfUnits");
        tableDataFive.appendChild(quantityText.domNode);
        var tableDataSix = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
        tableDataSix.setAttribute("width","10%");
        var incRepeatSubAdd2 = tableDataSix.lastChild;
        dojo.connect(incRepeatSubAdd2,"onclick",function(evt) {
        	  connectInstalledHusPartsSubButton(incInd,subIncIndex);
			  dojo.stopEvent(evt);
        });
        var tableDataSeven = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
        tableDataSeven.setAttribute("width","2%");
        partNumberText = getNonHussPartNumberTextField("installedNonHussmanPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].partNumber");
        tableDataSeven.appendChild(partNumberText.domNode);
        var tableDataEight = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
        tableDataEight.setAttribute("width","2%");
        quantityText = getQuantityTextField("installedNonHussmanQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].numberOfUnits");
        tableDataEight.appendChild(quantityText.domNode);
        var tableDataNine = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
        tableDataNine.setAttribute("width","2%");
        descriptionText = getNonHussPartNumberTextField("installedNonHussmanDescription_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].description");
        tableDataNine.appendChild(descriptionText.domNode);
        var tableDataTen = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[3];
        tableDataTen.setAttribute("width","2%");
        priceText = getNonHussmannPriceField("installedNonHussmanPrice_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].pricePerUnit",tableDataTen);
        //tableDataTen.appendChild(priceText.domNode);
        var tableDataEleven = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[4];
        tableDataEleven.setAttribute("width","10%");
        var incRepeatSubAdd2 = tableDataEleven.lastChild;
        dojo.connect(incRepeatSubAdd2,"onclick",function(evt) {
        	  connectInstalledNonHusPartsSubButton(incInd,subIncIndex);
			  dojo.stopEvent(evt);
        });
                var tableDataInvoice = tableRow[0].childNodes[3];
                tableDataInvoice .setAttribute("width","10%");
                var hiddenInput = document.createElement("input");
                hiddenInput.type = "hidden";
                hiddenInput.name = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].invoice";
                hiddenInput.id = "hiddenInvoice_"+incInd;
                tableDataInvoice.appendChild(hiddenInput);
                var downloadLink=dojo.byId("downloadInvoice"+incInd);
                var attachInvoice = tableDataInvoice.lastChild;
        var tableDataTwelve = tableRow[0].childNodes[4];
        tableDataTwelve .setAttribute("width","5%");
        var incRepeatDelete = tableDataTwelve.lastChild;
    	dojo.connect(incRepeatDelete, "onclick", function(evt){
    		dojo.dom.destroyNode(tableRow[0]);
   		});
	    tableRow[0].setAttribute("width","33%");
		tableBodyNode.appendChild(tableRow[0]);
}
        
        
function getPartReturnCheckTextField(perId,perName,incInd,subIncIndex){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
	var isPartReturned;
	// To make sure this works in IE and as well as in Mozilla 
	// try block is for IE and catch for other browsers 
	if(navigator.appName == 'Microsoft Internet Explorer'){ 
       isPartReturned = document.createElement("<input type=\"checkbox\" id=\""+perId+"\" name=\""+perName+"\"  "+
       									"onclick=\"alterRepValue("+incInd+","+subIncIndex+") >");  
	}  
    else {  
        isPartReturned = document.createElement("input");
	    isPartReturned.type = "checkbox";  
		isPartReturned.id=perId;
		isPartReturned.name=perName;
		isPartReturned.onclick=function(){alterRepValue(incInd,subIncIndex);};
	}
    return isPartReturned;
}

function getPaymentDescriptionTextField(perId,perName){
	 if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	 }
     var textField = document.createElement('select');
     textField.id=perId;
     textField.name=perName;
     textField.disabled=true;
     var paymentLength = document.getElementById("paymentLength");
     var i;
     var emptyList = document.createElement("option");
     emptyList.text=null;
     emptyList.value=null;
     for(i = 0; i < parseInt(paymentLength.value) ; i++ ) {//check this list.
		 var descValues = document.getElementById("paymentConditionsdesc_"+i);
     	 var codeValue = document.getElementById("paymentConditionscode_"+i);
	     var list = document.createElement("option");
	     list.text = descValues.value;
	     list.value = codeValue.value;
	     try {
         textField.add(list, null); //Standard
	     }catch(error) {
	         textField.add(list); // IE only
	     }
	 }
    return textField;
}

function getDueDaysTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
     var numberTextField = new twms.widget.NumberTextBox({
		required: true,
        id:perId,
        name:perName,
        constraints:{min:0,max:99999999999},
        maxLength:3
    });
    return numberTextField;
}

function getReturnLocationTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        maxLength:15,
        store:returnLocationCodeStore,
        searchAttr:"label",
        hasDownArrow:false,
        disabled:true
    });
    return textField;
}
        
function getQuantityTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    var numberTextField = new twms.widget.NumberTextBox({
    	required: true,
        id:perId,
        name:perName,
        constraints:{min:0,max:99999999999},
        maxLength:6
   	});
    return numberTextField;
}

function getNonHussmannPriceField(perId,perName,tableElement){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    
    var textField1 = document.createElement('input');
    textField1.type="hidden";
    textField1.name=perName;
    textField1.value="$";
    tableElement.appendChild(textField1);
    var textField2 = document.createElement('input');
    textField2.type="text";
    textField2.id=perId;
    textField2.name=perName;
    textField2.setAttribute("width","5%");
    textField2.setAttribute("size","5%");
    tableElement.appendChild(textField2);
}
        
function getPartNumberTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        store:replacedPartStore,
        searchAttr:"label",
        hasDownArrow:false
    });
    return textField;
}

function getNonHussPartNumberTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.ValidationTextBox({
		required: true,
        id:perId,
        name:perName,
        searchAttr:"label",
        hasDownArrow:false
    });
    return textField;
}

function connectReplacedPartsSubButton(incInd,subIncIndex) {
	var inSubIndexes = dojo.byId("addReplacedSubBody_"+incInd).rows;
    if(inSubIndexes[inSubIndexes.length-1].getAttribute("subReplacedRowIndex")!=null) {
		subIncIndex = inSubIndexes[inSubIndexes.length-1].getAttribute("subReplacedRowIndex");
	}
	subIncIndex = parseInt(subIncIndex)+1;
	addReplacedSubRowTable(incInd,subIncIndex);
}

function connectInstalledHusPartsSubButton(incInd,subIncIndex) {
	var inSubIndexes = dojo.byId("addHussInstalledSubBody_"+incInd).rows;
    if(inSubIndexes[inSubIndexes.length-1].getAttribute("subInstalledHussRowIndex")!=null) {
		subIncIndex = inSubIndexes[inSubIndexes.length-1].getAttribute("subInstalledHussRowIndex");
	}
	subIncIndex = parseInt(subIncIndex)+1;
	addHussInstalledSubRowTable(incInd,subIncIndex);
}

function connectInstalledNonHusPartsSubButton(incInd,subIncIndex) {
	var inSubIndexes = dojo.byId("addNonHussInstalledSubBody_"+incInd).rows;
    if(inSubIndexes[inSubIndexes.length-1].getAttribute("subInstalledNonHussRowIndex")!=null) {
		subIncIndex = inSubIndexes[inSubIndexes.length-1].getAttribute("subInstalledNonHussRowIndex");
	}
	subIncIndex = parseInt(subIncIndex)+1;
	addNonHussInstalledSubRowTable(incInd,subIncIndex);
}

function addReplacedSubRowTable(incInd,subIncIndex) {
	var subTemplateHussmanReplaced = "<tr><td></td><td></td>";
	var isProcessorReview = dojo.byId("processorReview").value;
	if (isProcessorReview == 'true') {
		subTemplateHussmanReplaced = subTemplateHussmanReplaced + "<td></td><td></td><td></td><td></td>";
	}
	var subNumberText;
    var subMarkupTxt = dojo.string.substitute(subTemplateHussmanReplaced);
    var subTableBodyNode = dojo.byId("addReplacedSubBody_"+incInd);
    var subTableRow = dojo.html.createNodesFromText(subMarkupTxt);
    var subTableDataOne = subTableRow[0].childNodes[0];
    subTableDataOne.setAttribute("width","2%");
    subNumberText = getPartNumberTextField("replacedPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].itemReference.referredItem");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","2%");
    subNumberText = getQuantityTextField("replacedQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
    if (isProcessorReview == 'true') {
        	var subTableDataThree = subTableRow[0].childNodes[2];
        	subTableDataThree.setAttribute("width","2%");
        	partNumberText = getPartReturnCheckTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_toBeReturned","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partToBeReturned",incInd,subIncIndex);
        	subTableDataThree.appendChild(partNumberText);
        	var subTableDataFour = subTableRow[0].childNodes[3];
        	subTableDataFour.setAttribute("width","2%");
        	partNumberText = getReturnLocationTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_location","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.returnLocation");
        	subTableDataFour.appendChild(partNumberText.domNode);
        	var subTableDataFive  = subTableRow[0].childNodes[4];
        	subTableDataFive.setAttribute("width","2%");
        	partNumberText = getPaymentDescriptionTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_paymentCondition","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.paymentCondition");
        	subTableDataFive.appendChild(partNumberText);
        	var subTableDataSix  = subTableRow[0].childNodes[5];
        	subTableDataSix.setAttribute("width","2%");
        	partNumberText = getDueDaysTextField("oemRepPart_"+incInd+"_"+subIncIndex+"_dueDays","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].partReturn.dueDays");
        	subTableDataSix.appendChild(partNumberText.domNode);
    }
    subTableRow[0].setAttribute("subReplacedRowIndex",subIncIndex);
    subTableRow[0].setAttribute("width","33%");
	subTableBodyNode.appendChild(subTableRow[0]);
}

function addHussInstalledSubRowTable(incInd,subIncIndex) {
	var subNumberText;
    var subMarkupTxt = dojo.string.substitute(subTemplateHussmanInstalled);
    var subTableBodyNode = dojo.byId("addHussInstalledSubBody_"+incInd);
    var subTableRow = dojo.html.createNodesFromText(subMarkupTxt);
    var subTableDataOne = subTableRow[0].childNodes[0];
    subTableDataOne.setAttribute("width","2%");
    subNumberText = getPartNumberTextField("installedHussmanPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].item");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","2%");
    subNumberText = getQuantityTextField("installedHussmanQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
    subTableRow[0].setAttribute("subInstalledHussRowIndex",subIncIndex);
    subTableRow[0].setAttribute("width","33%");
	subTableBodyNode.appendChild(subTableRow[0]);
}

function addNonHussInstalledSubRowTable(incInd,subIncIndex) {
	var subNumberText;
	var subDescriptionText;
    var subMarkupTxt = dojo.string.substitute(subTemplateNonHussman);
    var subTableBodyNode = dojo.byId("addNonHussInstalledSubBody_"+incInd);
    var subTableRow = dojo.html.createNodesFromText(subMarkupTxt);
    var subTableDataOne = subTableRow[0].childNodes[0];
    subTableDataOne.setAttribute("width","2%");
    subNumberText = getNonHussPartNumberTextField("installedNonHussmanPartNumber_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].partNumber");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","2%");
    subNumberText = getQuantityTextField("installedNonHussmanQuantity_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
 	var subTableDataThree = subTableRow[0].childNodes[2];
    subTableDataThree.setAttribute("width","2%");
    subDescriptionText = getNonHussPartNumberTextField("installedNonHussmanDescription_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].description");
    subTableDataThree.appendChild(subDescriptionText.domNode);
    var subTableDataFour = subTableRow[0].childNodes[3];
    subTableDataFour.setAttribute("width","2%");
    subPriceText = getNonHussmannPriceField("installedNonHussmanPrice_"+incInd+"_"+subIncIndex,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].pricePerUnit",subTableDataFour);
    //subTableDataFour.appendChild(subPriceText.domNode);
    subTableRow[0].setAttribute("subInstalledNonHussRowIndex",subIncIndex);
    subTableRow[0].setAttribute("width","33%");
	subTableBodyNode.appendChild(subTableRow[0]);
}

function showFileDownloadLink(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
                              /*domNode [input type="hidden"]*/ attachedInvoiceId,
                                /*Long*/docId, /*String*/fileName) {
    dojo.html.hide(attachInvoiceLink);
    _getFileHolder(downloadLink).innerHTML = fileName;
    downloadLink.url = "downloadDocument.action?docId=" + docId;
    attachedInvoiceId.value=docId;
    dojo.html.show(downloadLink);
    if(fileName.length==0){
        dojo.html.hide(downloadLink);
        dojo.html.show(attachInvoiceLink);
    }
}

function attachInvoiceFunction(/*Function*/ callback) {
    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
}

function _getFileHolder(downloadLink) {
    return getElementByClass("documentName", downloadLink);
}

function _getUploadDropButton(downloadLink) {
    return getElementByClass("dropUpload", downloadLink);
}

function dropAttachedInvoice(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
                             /*domNode [input type="hidden"]*/ attachedInvoiceId) {
    downloadLink.url="";
    attachedInvoiceId.value = "";
    dojo.html.hide(downloadLink);
    dojo.html.show(attachInvoiceLink);
}
