dojo.require("dojox.layout.ContentPane");
dojo.require("twms.widget.NumberTextBox");
dojo.require("twms.widget.ValidationTextBox");
dojo.provide("twms.widget.ValidationTextBox");
dojo.provide("twms.widget.Select");
dojo.require("twms.widget.Select");
dojo.provide("dijit.form.CheckBox");
dojo.require("dijit.form.CheckBox");

var subTemplateHussmanInstalled = "<tr><td></td><td></td></tr>";
var subTemplateNonHussman = "<tr><td></td><td></td><td></td><td></td></tr>";



function connectValueAddButton(inIndex,subRowIndex){
		dojo.connect(dojo.byId("addRepeatRow"), "onclick", function(evt){
             var inIndexes = dojo.byId("addRepeatBodyFoc").rows;
             if(inIndexes.length!=0){
             	 inIndex = inIndexes.length;
                 inIndex = parseInt(inIndex);
             }
             else {
             	inIndex = 0;
             }
             subRowIndex=parseInt(subRowIndex);

			addRowTable(inIndex,subRowIndex);
//            var downloadLinkInit = dojo.byId("downloadInvoice"+inIndex);
//            var attachInvoiceLinkInit = dojo.byId("invoice"+inIndex);
//            var attachedInvoiceIdInit = dojo.byId("hiddenInvoice_"+inIndex);
//             dojo.html.hide(downloadLinkInit);
//                dojo.connect(dojo.byId("invoice" + inIndex), "onclick", function(doc) {
//                    attachInvoiceFunction(function(doc) {
//                        showFileDownloadLink(attachInvoiceLinkInit,downloadLinkInit,
//                                attachedInvoiceIdInit, doc[0].id, doc[0].name);
//                    });
//                });
//            dojo.connect(_getUploadDropButton(downloadLinkInit), "onclick", function() {
//                dropAttachedInvoice(attachInvoiceLinkInit, downloadLinkInit,attachedInvoiceIdInit);
//            });
             dojo.stopEvent(evt);
		});
            
}
            
function addRowTable(incInd,subIncIndex){
		var template = "<tr><td><table><tbody><tr><td></td><td></td>";
		template=template+"<td><div class='repeat_add' id='${addPartReplaced}' width='10%'/></td></tr></tbody></table></td>"+
			   "<td><table><tbody><tr><td></td><td></td><td><div class='repeat_add' id='${hussAddPartInstalled}' width='10%'/></td></tr></tbody></table></td>"+
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
        var tableBodyNode = dojo.byId("addRepeatBodyFoc");
        var tableRow = dojo.html.createNodesFromText(markupTxt);
        //dynamically assigning an ID value for the table Body.
        tableRow[0].childNodes[0].childNodes[0].childNodes[0].setAttribute("id","addReplacedSubBodyFoc_"+incInd);
        tableRow[0].childNodes[1].childNodes[0].childNodes[0].setAttribute("id","addHussInstalledSubBodyFoc_"+incInd);
//        tableRow[0].childNodes[2].childNodes[0].childNodes[0].setAttribute("id","addNonHussInstalledSubBody_"+incInd);
        // the tableRow[0].childNodes[0] gives the <td> for the outer table. the second childNodes gives the <table> node.
        // the third childNodes gives the <tbody> node. the fourth childNodes gives the <tr> node and finally
        // the fifth childNodes gives the <td> node for the inner table...
        // the hierarchy is : <table><tr>.<td>.<table>.<tbody>.<tr>.<td>
		var tableDataOne = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
		var replacedPartSection = tableRow[0].childNodes[0];
		replacedPartSection.setAttribute("width","45%");
        tableDataOne.setAttribute("width","45%");
        partNumberText = getPartNumberTextField("replacedPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].itemReference.referredItem");
        tableDataOne.appendChild(partNumberText.domNode);
        var tableDataTwo = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
        tableDataTwo.setAttribute("width","45%");
        quantityText = getQuantityTextField("replacedQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].numberOfUnits");
        tableDataTwo.appendChild(quantityText.domNode);
	    var tableDataThree = tableRow[0].childNodes[0].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
	    tableDataThree.setAttribute("width","10%");
	    var incRepeatSubAdd1 = tableDataThree.lastChild;
        dojo.connect(incRepeatSubAdd1,"onclick",function(evt) {
        	  connectReplacedPartsSubButton(incInd,subIncIndex);
			  dojo.stopEvent(evt);
        });
        var installedPartSection = tableRow[0].childNodes[1];
		installedPartSection.setAttribute("width","45%");
        var tableDataFour = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
        tableDataFour.setAttribute("width","45%");
        partNumberText = getPartNumberTextField("installedHussmanPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].item");
        tableDataFour.appendChild(partNumberText.domNode);
        var tableDataFive = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
        tableDataFive.setAttribute("width","45%");
        quantityText = getQuantityTextField("installedHussmanQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].numberOfUnits");
        tableDataFive.appendChild(quantityText.domNode);
        var tableDataSix = tableRow[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
        tableDataSix.setAttribute("width","10%");
        var incRepeatSubAdd2 = tableDataSix.lastChild;
        dojo.connect(incRepeatSubAdd2,"onclick",function(evt) {
        	  connectInstalledHusPartsSubButton(incInd,subIncIndex);
			  dojo.stopEvent(evt);
        });
        
//        var tableDataSeven = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[0];
//        tableDataSeven.setAttribute("width","2%");
//        partNumberText = getNonHussPartNumberTextField("installedNonHussmanPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].partNumber");
//        tableDataSeven.appendChild(partNumberText.domNode);
//        var tableDataEight = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[1];
//        tableDataEight.setAttribute("width","2%");
//        quantityText = getQuantityTextField("installedNonHussmanQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].numberOfUnits");
//        tableDataEight.appendChild(quantityText.domNode);
//        var tableDataNine = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[2];
//        tableDataNine.setAttribute("width","2%");
//        descriptionText = getNonHussPartNumberTextField("installedNonHussmanDescription_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].description");
//       tableDataNine.appendChild(descriptionText.domNode);
//        var tableDataTen = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[3];
//        tableDataTen.setAttribute("width","2%");
//       priceText = getNonHussmannPriceField("installedNonHussmanPrice_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].price");
//      tableDataTen.appendChild(priceText.domNode);
//        var tableDataEleven = tableRow[0].childNodes[2].childNodes[0].childNodes[0].childNodes[0].childNodes[4];
//        tableDataEleven.setAttribute("width","10%");
//        var incRepeatSubAdd2 = tableDataEleven.lastChild;
//        dojo.connect(incRepeatSubAdd2,"onclick",function(evt) {
//        	  connectInstalledNonHusPartsSubButton(incInd,subIncIndex);
//			  dojo.stopEvent(evt);
//       });
//                var tableDataInvoice = tableRow[0].childNodes[3];
//                tableDataInvoice .setAttribute("width","10%");
//               var hiddenInput = document.createElement("input");
//                hiddenInput.type = "hidden";
//                hiddenInput.name = "hussmanPartsReplacedInstalled["+incInd+"].invoice";
//                hiddenInput.id = "hiddenInvoice_"+incInd;
//                tableDataInvoice.appendChild(hiddenInput);
//                var downloadLink=dojo.byId("downloadInvoice"+incInd);
//                var attachInvoice = tableDataInvoice.lastChild;
        var tableDataTwelve = tableRow[0].childNodes[2];
        tableDataTwelve.setAttribute("width","10%");
        var incRepeatDelete = tableDataTwelve.lastChild;
    	dojo.connect(incRepeatDelete, "onclick", function(evt){
    		dojo.dom.destroyNode(tableRow[0]);
   		});
	    tableRow[0].setAttribute("width","100%");
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
     textField.setAttribute("width","15%")
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
        maxLength:3,
        disabled:true
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

function getNonHussmannPriceField(perId,perName){
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
        
function getPartNumberTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        maxLength:50,
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
        maxLength:25,
        searchAttr:"label",
        hasDownArrow:false
    });
    return textField;
}

function connectReplacedPartsSubButton(incInd,subIncIndex) {
	var inSubIndexes = dojo.byId("addReplacedSubBodyFoc_"+incInd).rows;
    if(inSubIndexes[inSubIndexes.length-1].getAttribute("subReplacedRowIndex")!=null) {
		subIncIndex = inSubIndexes[inSubIndexes.length-1].getAttribute("subReplacedRowIndex");
	}
	subIncIndex = parseInt(subIncIndex)+1;
	addReplacedSubRowTable(incInd,subIncIndex);
}

function connectInstalledHusPartsSubButton(incInd,subIncIndex) {
	var inSubIndexes = dojo.byId("addHussInstalledSubBodyFoc_"+incInd).rows;
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
	var subNumberText;
    var subMarkupTxt = dojo.string.substitute(subTemplateHussmanReplaced);
    var subTableBodyNode = dojo.byId("addReplacedSubBodyFoc_"+incInd);
    var subTableRow = dojo.html.createNodesFromText(subMarkupTxt);
    var subTableDataOne = subTableRow[0].childNodes[0];
    subTableDataOne.setAttribute("width","45%");
    subNumberText = getPartNumberTextField("replacedPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].itemReference.referredItem");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","45%");
    subNumberText = getQuantityTextField("replacedQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].replacedParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
    subTableRow[0].setAttribute("subReplacedRowIndex",subIncIndex);
    subTableRow[0].setAttribute("width","45%");
	subTableBodyNode.appendChild(subTableRow[0]);
}

function addHussInstalledSubRowTable(incInd,subIncIndex) {
	var subNumberText;
    var subMarkupTxt = dojo.string.substitute(subTemplateHussmanInstalled);
    var subTableBodyNode = dojo.byId("addHussInstalledSubBodyFoc_"+incInd);
    var subTableRow = dojo.html.createNodesFromText(subMarkupTxt);
    var subTableDataOne = subTableRow[0].childNodes[0];
    subTableDataOne.setAttribute("width","45%");
    subNumberText = getPartNumberTextField("installedHussmanPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].item");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","45%");
    subNumberText = getQuantityTextField("installedHussmanQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].hussmanInstalledParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
    subTableRow[0].setAttribute("subInstalledHussRowIndex",subIncIndex);
    subTableRow[0].setAttribute("width","45%");
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
    subNumberText = getNonHussPartNumberTextField("installedNonHussmanPartNumber_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].partNumber");
    subTableDataOne.appendChild(subNumberText.domNode);
 	var subTableDataTwo = subTableRow[0].childNodes[1];
    subTableDataTwo.setAttribute("width","2%");
    subNumberText = getQuantityTextField("installedNonHussmanQuantity_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].numberOfUnits");
    subTableDataTwo.appendChild(subNumberText.domNode);
 	var subTableDataThree = subTableRow[0].childNodes[2];
    subTableDataThree.setAttribute("width","2%");
    subDescriptionText = getNonHussPartNumberTextField("installedNonHussmanDescription_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].description");
    subTableDataThree.appendChild(subDescriptionText.domNode);
    var subTableDataFour = subTableRow[0].childNodes[3];
    subTableDataFour.setAttribute("width","2%");
    subPriceText = getNonHussmannPriceField("installedNonHussmanPrice_"+incInd+"_"+subIncIndex,"hussmanPartsReplacedInstalled["+incInd+"].nonHussmanInstalledParts["+subIncIndex+"].price");
    subTableDataFour.appendChild(subPriceText.domNode);
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
