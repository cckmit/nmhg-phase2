dojo.require("dojox.layout.ContentPane");
dojo.require("twms.widget.NumberTextBox");
dojo.require("twms.widget.ValidationTextBox");
dojo.require("twms.widget.Select");
dojo.require("dijit.form.CheckBox");

function connectValueAddButton(){
		dojo.connect(dojo.byId("addRepeatRow"), "onclick", function(evt){
			var inIndex;
			if(dojo.byId("rowIndexes") != null) {
				inIndex = dojo.byId("rowIndexes").value;
				inIndex = parseInt(inIndex);
			} else {
				inIndex = dojo.byId("rowIndex").value;
				inIndex = parseInt(inIndex);
			}
             createRowLabels(inIndex);
			 dojo.stopEvent(evt);
		});
            
}

function createRowLabels(inIndex){
	var tableTrDiv = document.createElement("tr");
	tableTrDiv.setAttribute("id","divTrForBody_"+inIndex);
	var tableTdDiv = document.createElement("td");
	tableTdDiv.setAttribute("width","98%");
	var newDiv = document.createElement("div");
	newDiv.setAttribute("width","100%");
	newDiv.setAttribute("height","100%");
	newDiv.setAttribute("id","replacedInstalledPartsWidgetDiv");
	var newTable = document.createElement("table");
	/*if(dojo.isIE) {
		newDiv.setAttribute("className","partReplacedClass");
	}
	else {
		newDiv.setAttribute("class","partReplacedClass");
	}*/
	newTable.setAttribute("width","100%");
	var newTBody = document.createElement("tbody");
	newTBody.setAttribute("id","divTbody_"+inIndex);
	var newTr = document.createElement("tr");
	var newTd = document.createElement("td");
	newTd.setAttribute("width","98%");	
	newTr.appendChild(newTd);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);
	
	//------ Creation of Replaced Widget ----------//
	var tableReplaced = document.createElement("table");
	tableReplaced.setAttribute("width","100%");
	/*if(dojo.isIE) {
		tableReplaced.setAttribute("className","grid");
	}
	else {
		tableReplaced.setAttribute("class","grid");
	}*/
	var tableReplacedBody = document.createElement("tbody");
	tableReplacedBody.setAttribute("width","100%");
	tableReplacedBody.setAttribute("id","addRepeatBody_Replaced_"+inIndex);
	var tableRowReplaced = createNewRow(inIndex,i18N.oem_removed_parts,
								"deleteRepeatRow_Replaced","addRepeatRow_Replaced");
	tableReplacedBody.appendChild(tableRowReplaced);
		
	//-------------  Code for the labels go here (Part Number etc)-----------------//
	var tableRowReplacedLabels = createReplacedLabelsForRow(inIndex);
	tableReplacedBody.appendChild(tableRowReplacedLabels);
	
	
	tableReplaced.appendChild(tableReplacedBody);
	newTd.appendChild(tableReplaced);
	newTr.appendChild(newTd);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);
	//------ Creation of Hussmann Installed Widget ----------//
	var tableHussInstalled = document.createElement("table");
	tableHussInstalled.setAttribute("width","100%");
	/*if(dojo.isIE) {
		tableHussInstalled.setAttribute("className","grid");
	}
	else {
		tableHussInstalled.setAttribute("class","grid");
	}*/
	var tableHussInstalledBody = document.createElement("tbody");
	tableHussInstalledBody.setAttribute("width","100%");
	tableHussInstalledBody.setAttribute("id","addRepeatBody_HussInstalled_"+inIndex);
	var tableRowHussInstalled = createNewHussInstallRow(inIndex,i18N.oem_installed_parts,
								"deleteRepeatRow_HussInstalled","addRepeatRow_HussInstalled");
	tableHussInstalledBody.appendChild(tableRowHussInstalled)
	tableHussInstalled.appendChild(tableHussInstalledBody);
	newDiv.appendChild(tableHussInstalled);	
	
	//-------------  Code for the labels go here (Part Number etc)-----------------//
	var tableRowHussInstallLabels = createHussmannLabelsForRow(inIndex);
	tableHussInstalledBody.appendChild(tableRowHussInstallLabels);
	
	tableHussInstalled.appendChild(tableHussInstalledBody);
	newTd.appendChild(tableHussInstalled);
	newTr.appendChild(newTd);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);	
	
	//------ Creation of Non-Hussmann Installed Widget ----------//
	var tableNonHussInstalled = document.createElement("table");
	tableNonHussInstalled.setAttribute("width","100%");
	/*if(dojo.isIE) {
		tableNonHussInstalled.setAttribute("className","grid");
	}
	else {
		tableNonHussInstalled.setAttribute("class","grid");
	}*/
	/*var tableNonHussInstalledBody = document.createElement("tbody");
	tableNonHussInstalledBody.setAttribute("width","100%");
	tableNonHussInstalledBody.setAttribute("id","addRepeatBody_NonHussInstalled_"+inIndex);
	var tableRowNonHussInstalled = createNewNonHussInstallRow(inIndex,i18N.non_oem_installed_parts,
								"deleteRepeatRow_NonHussInstalled","addRepeatRow_NonHussInstalled");
	tableNonHussInstalledBody.appendChild(tableRowNonHussInstalled)
	tableNonHussInstalled.appendChild(tableNonHussInstalledBody);
	newDiv.appendChild(tableNonHussInstalled);
	
	//-------------  Code for the labels go here (Part Number etc)-----------------//
	var tableRowNonHussInstallLabels = createNonHussmannLabelsForRow(inIndex);
	tableNonHussInstalledBody.appendChild(tableRowNonHussInstallLabels);
	
	tableNonHussInstalled.appendChild(tableNonHussInstalledBody);
	newTd.appendChild(tableNonHussInstalled);
	newTr.appendChild(newTd);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);*/
	
	// --- Creation of Global Delete Button --- //
	var tableTdDelete = document.createElement("td");
	tableTdDelete.setAttribute("width","2%");
	var globalDeleteButton = createDeleteButton("deleteSection",inIndex);
	tableTdDelete.appendChild(globalDeleteButton);// Delete
	newTr.appendChild(tableTdDelete);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);
	// Code for creating an empty line break between the sections
	var emptyLineBreak = document.createElement("br");
    newDiv.appendChild(emptyLineBreak);
	
	tableTdDiv.appendChild(newDiv);
	tableTrDiv.appendChild(tableTdDiv);
	var tablebody = document.getElementById("addRepeatBody");
	tablebody.appendChild(tableTrDiv);
	if( dojo.byId("rowIndexes") == null ) {
		var rowIndex = document.createElement("input");
		rowIndex.type="hidden";
		rowIndex.id="rowIndexes";
		rowIndex.value=parseInt(inIndex)+parseInt(1);
	} else {
		dojo.byId("rowIndexes").value=parseInt(dojo.byId("rowIndexes").value)+parseInt(1);
	}
	
	tablebody.appendChild(rowIndex);
}

function createNewRow(inIndex,labelName,deleteButtonId,addButtonId) {
	var tableRowReplaced = document.createElement("tr");
	tableRowReplaced.setAttribute("width","100%");
	if(navigator.appName == 'Microsoft Internet Explorer') {
		tableRowReplaced.setAttribute("className","title");
	}
	else {
		tableRowReplaced.setAttribute("class","title");
	}
	var tableTdReplaced = document.createElement("td");
	tableTdReplaced.setAttribute("width","25%");
	var tableTdSpace1 = document.createElement("td");
	tableTdSpace1.setAttribute("width","10%");
    var spanForPartNumber = createLabel(labelName);
    tableTdReplaced.appendChild(spanForPartNumber);
    tableRowReplaced.appendChild(tableTdReplaced);
    tableRowReplaced.appendChild(tableTdSpace1);
//    if(dojo.byId("processorReview").value == 'true') {
        var tableTdSpace2 = document.createElement("td");
        tableTdSpace2.setAttribute("width","25%");
        var tableTdSpace4 = document.createElement("td");
        tableTdSpace4.setAttribute("width","10%");
        var tableTdSpace5 = document.createElement("td");
        tableTdSpace5.setAttribute("width","15%");
        var tableDeleteTdReplaced = document.createElement("td");
        tableDeleteTdReplaced.setAttribute("width","5%");
        //function to create the labels
        tableRowReplaced.appendChild(tableTdSpace2);        
        tableRowReplaced.appendChild(tableTdSpace4);
        tableRowReplaced.appendChild(tableTdSpace5);
        tableRowReplaced.appendChild(tableDeleteTdReplaced);
//    } else {
//        var tableTdSpace = document.createElement("td");
//        tableTdSpace.setAttribute("width","55%");
//        tableRowReplaced.appendChild(tableTdSpace);
//    }
    //function to create add button with a unique id
    var tableAddTdReplaced = document.createElement("td");
    tableAddTdReplaced.setAttribute("width","10%");
    var addReplacedButton = createAddButton(addButtonId,inIndex);
    tableAddTdReplaced.appendChild(addReplacedButton);
	tableRowReplaced.appendChild(tableAddTdReplaced);
	return tableRowReplaced;
}

function createNewHussInstallRow(inIndex,labelName,deleteButtonId,addButtonId){
    var tableRowReplaced = document.createElement("tr");
	tableRowReplaced.setAttribute("width","100%");
	if(navigator.appName == 'Microsoft Internet Explorer') {
		tableRowReplaced.setAttribute("className","title");
	}
	else {
		tableRowReplaced.setAttribute("class","title");
	}
	var tableTdReplaced = document.createElement("td");
	tableTdReplaced.setAttribute("width","25%");
	var tableTdSpace1 = document.createElement("td");
	tableTdSpace1.setAttribute("width","10%");
	var tableTdSpace2 = document.createElement("td");
	tableTdSpace2.setAttribute("width","35%");
	var tableTdSpace3 = document.createElement("td");
	tableTdSpace3.setAttribute("width","20%");	
	var tableAddTdReplaced = document.createElement("td");
	tableAddTdReplaced.setAttribute("width","10%");
	//function to create the labels
	var spanForPartNumber = createLabel(labelName);
	tableTdReplaced.appendChild(spanForPartNumber);
	tableRowReplaced.appendChild(tableTdReplaced);
	tableRowReplaced.appendChild(tableTdSpace1);
	tableRowReplaced.appendChild(tableTdSpace2);
	tableRowReplaced.appendChild(tableTdSpace3);
	//function to create delete button with a unique id
    //var deleteReplacedButton = createDeleteButton(deleteButtonId,inIndex);
    //tableDeleteTdReplaced.appendChild(deleteReplacedButton);
    //function to create add button with a unique id
    var addReplacedButton = createAddButton(addButtonId,inIndex);
    tableAddTdReplaced.appendChild(addReplacedButton);
	tableRowReplaced.appendChild(tableAddTdReplaced);
	return tableRowReplaced;
}

function createNewNonHussInstallRow(inIndex,labelName,deleteButtonId,addButtonId){
    var tableRowReplaced = document.createElement("tr");
	tableRowReplaced.setAttribute("width","100%");
	if(navigator.appName == 'Microsoft Internet Explorer') {
		tableRowReplaced.setAttribute("className","title");
	}
	else {
		tableRowReplaced.setAttribute("class","title");
	}
	var tableTdReplaced = document.createElement("td");
	tableTdReplaced.setAttribute("width","50%");
	var tableTdSpace1 = document.createElement("td");
	tableTdSpace1.setAttribute("width","20%");
	var tableTdSpace2 = document.createElement("td");
	tableTdSpace2.setAttribute("width","20%");
//    var tableTdSpace3 = document.createElement("td");
//	tableTdSpace3.setAttribute("width","10%");
    var tableAddTdReplaced = document.createElement("td");
	tableAddTdReplaced.setAttribute("width","10%");
	//function to create the labels
	var spanForPartNumber = createLabel(labelName);
	tableTdReplaced.appendChild(spanForPartNumber);
	tableRowReplaced.appendChild(tableTdReplaced);
	tableRowReplaced.appendChild(tableTdSpace1);
	tableRowReplaced.appendChild(tableTdSpace2);
//    tableRowReplaced.appendChild(tableTdSpace3);
    //function to create delete button with a unique id
    //var deleteReplacedButton = createDeleteButton(deleteButtonId,inIndex);
    //tableDeleteTdReplaced.appendChild(deleteReplacedButton);
    //function to create add button with a unique id
    var addReplacedButton = createAddButton(addButtonId,inIndex);
    tableAddTdReplaced.appendChild(addReplacedButton);
	tableRowReplaced.appendChild(tableAddTdReplaced);
	return tableRowReplaced;
}

function createLabel(name) {
	var spanForPartNumber = document.createElement("span");
    spanForPartNumber.innerHTML = name;
    return spanForPartNumber;
}

function createDeleteButton(buttonIdname,inIndex) {
	var deleteReplacedButton = document.createElement("div");
    if(navigator.appName == 'Microsoft Internet Explorer') {
    	deleteReplacedButton.setAttribute("className","repeat_del");
    }
    else {
    	deleteReplacedButton.setAttribute("class","repeat_del");
    }
    deleteReplacedButton.setAttribute("id",buttonIdname+"_"+inIndex);
    deleteReplacedButton.onclick = function(evt) {
			dojo.dom.destroyNode(dojo.byId("divTrForBody_"+inIndex));
			dojo.stopEvent(evt);
	}
    		
 	return deleteReplacedButton;
}

function createAddButton(buttonIdName,inIndex) {
	var addButton = document.createElement("div");
    if(navigator.appName == 'Microsoft Internet Explorer') {
    	addButton.setAttribute("className","repeat_add");
    }
    else {
    	addButton.setAttribute("class","repeat_add");
    }
    addButton.setAttribute("id",buttonIdName+"_"+inIndex);
    if(buttonIdName =='addRepeatRow_Replaced') {
		addButton.onclick = function(evt) {
    		createReplacedWidget(inIndex);
	 		dojo.stopEvent(evt);
    	}    
    }else if(buttonIdName=='addRepeatRow_HussInstalled') {
	    addButton.onclick = function(evt) {
	    	createHussInstalledWidget(inIndex);
		 	dojo.stopEvent(evt);
	    }
    }else if (buttonIdName=='addRepeatRow_NonHussInstalled') {
    	addButton.onclick  = function(evt) {
    		createNonHussInstalledWidget(inIndex);
    		dojo.stopEvent(evt);
    	}
    }
    return addButton;
}

function createReplacedWidget(inIndex) {
    var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);
	var subInc;
	var subRowIndex = dojo.byId("addRepeatBody_Replaced_"+inIndex).rows;
	if(subRowIndex[subRowIndex.length-1].getAttribute("subReplacedRowIndex")!=null) {
		subInc = subRowIndex[subRowIndex.length-1].getAttribute("subReplacedRowIndex");
		subInc=parseInt(subInc)+1;
	} else if(subRowIndex.length >2) {
		subRowIndex = parseInt(subRowIndex.length);
		subInc = subRowIndex-2;
	} else {
		subInc = 0;
	}
//    var isProcessorReview = dojo.byId("processorReview").value;
    /*var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);
	var subRowIndex = tableBodyNode.rows;
	var subInc;
	subRowIndex = parseInt(subRowIndex.length);
    subInc = subRowIndex-2;*/
	var template = "<tr>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
//    if(isProcessorReview == 'true') {
        template = template+"<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
//    }
    template = template +"<td class='partReplacedClass'><div class='repeat_del' id='${deleteRow}'/></td></tr>";
	var markupTxt = dojo.string.substitute(template,{"deleteRow":"deleteRepeatRow_Replaced_"+inIndex+"_"+subInc});
	var tableRow = dojo.html.createNodesFromText(markupTxt);
	tableRow.setAttribute("id","ReplacedRow_"+inIndex+"_"+subInc);
	var tableDataOne = tableRow.childNodes[0];
	tableDataOne.setAttribute("align","center");
	var replacedPartDesc = "replacedPartDescription_"+inIndex+"_"+subInc;
	var partNumberText = getPartNumberReplacedText("replacedPartNumber_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].item",
												replacedPartDesc,inIndex,subInc);
    tableDataOne.appendChild(partNumberText.domNode);
    createHiddenForPartReplaceRow("replacedPartHiddenAttribute_"+inIndex+"_"+subInc,
    "campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].isHussPartInstalled",tableDataOne);    
	var tableDataTwo = tableRow.childNodes[1];
	tableDataTwo.setAttribute("align","center");
    var qunatityText = getQuantityTextField("replacedQuantity_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].noOfUnits");
    tableDataTwo.appendChild(qunatityText);
    var tableDataThree = tableRow.childNodes[2];
	tableDataThree.setAttribute("align","center");
    var descriptionText = getDescriptionTextField(replacedPartDesc);
    tableDataThree.appendChild(descriptionText);
//    if (isProcessorReview == 'true') {
//        	var tableDataThree = tableRow.childNodes[3];
//        	tableDataThree.setAttribute("align","center");
//        	partNumberText = getPartReturnCheckTextField("oemRepPart_"+inIndex+"_"+subInc+"_toBeReturned","campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].partToBeReturned",inIndex,subInc);
//        	tableDataThree.appendChild(partNumberText);
        	var tableDataFour = tableRow.childNodes[3];
        	tableDataFour.setAttribute("align","center");
        	partNumberText = getReturnLocationTextField("oemRepPart_"+inIndex+"_"+subInc+"_location","campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].returnLocation");
        	tableDataFour.appendChild(partNumberText.domNode);
        	var tableDataFive = tableRow.childNodes[4];
        	tableDataFive.setAttribute("align","center");
        	partNumberText = getPaymentDescriptionTextField("oemRepPart_"+inIndex+"_"+subInc+"_paymentCondition","campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].paymentCondition");
        	tableDataFive.appendChild(partNumberText);
        	var tableDataSix  = tableRow.childNodes[5];
        	tableDataSix.setAttribute("align","center");
        	partNumberText = getDueDaysTextField("oemRepPart_"+inIndex+"_"+subInc+"_dueDays","campaign.hussPartsToReplace["+inIndex+"].removedParts["+subInc+"].dueDays");
        	tableDataSix.appendChild(partNumberText);
        	var tableDataSeven = tableRow.childNodes[6];
        	tableDataSeven.setAttribute("align","center");
		    var deleteReplaced = tableDataSeven.lastChild;
		    dojo.connect(deleteReplaced,"onclick",function(evt){
		    	dojo.dom.destroyNode(tableRow);
		    });
//        } else {
//        		var tableDataThree = tableRow.childNodes[3];
//        		tableDataThree.setAttribute("align","center");
//		        var deleteReplaced = tableDataThree.lastChild;
//			    dojo.connect(deleteReplaced,"onclick",function(evt){
//			    	dojo.dom.destroyNode(tableRow);
//			    });
//       	}
    tableRow.setAttribute("subReplacedRowIndex",subInc);
    tableBodyNode.appendChild(tableRow);
}

function createHussInstalledWidget(inIndex) {
	var tableBodyNode = document.getElementById("addRepeatBody_HussInstalled_"+inIndex);
	var subInc;
	var subRowIndex = dojo.byId("addRepeatBody_HussInstalled_"+inIndex).rows;
	if(subRowIndex[subRowIndex.length-1].getAttribute("subHussInstallRowIndex")!=null) {
		subInc = subRowIndex[subRowIndex.length-1].getAttribute("subHussInstallRowIndex");
		subInc=parseInt(subInc)+1;
	} else if(subRowIndex.length >2) {
		subRowIndex = parseInt(subRowIndex.length);
		subInc = subRowIndex-2;
	} else {
		subInc = 0;
	}
	var template = "<tr>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +                                    
                   "<td class='partReplacedClass'><div class='repeat_del' id='${deleteRow}'/></td>" +
                   "</tr>";
	var markupTxt = dojo.string.substitute(template,{"deleteRow":"deleteRepeatRow_HussInstalled_"+inIndex+"_"+subInc}); 
	var tableRow = dojo.html.createNodesFromText(markupTxt);
	tableRow.setAttribute("id","HussmannInstalledRow_"+inIndex+"_"+subInc);
	var tableDataOne = tableRow.childNodes[0];
	tableDataOne.setAttribute("align","center");
	var installedPartDesc = "installedHussPartDescription_"+inIndex+"_"+subInc;
	var partNumberText = getPartNumberInstallText("installedHussmanPartNumber_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].installedParts["+subInc+"].item",
												installedPartDesc,inIndex,subInc);
    tableDataOne.appendChild(partNumberText.domNode);
        createHiddenForPartInstalledRow("installedHussmanPartAttribute_"+inIndex+"_"+subInc,
    "campaign.hussPartsToReplace["+inIndex+"].installedParts["+subInc+"].isHussPartInstalled",tableDataOne);
	var tableDataTwo = tableRow.childNodes[1];
	tableDataTwo.setAttribute("align","center");
    var qunatityText = getQuantityTextField("installedHussmanQuantity_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].installedParts["+subInc+"].noOfUnits");
    tableDataTwo.appendChild(qunatityText);
    var tableDataThree = tableRow.childNodes[2];
	tableDataThree.setAttribute("align","center");
    var descriptionText = getDescriptionTextFieldForInstalled(installedPartDesc);
    tableDataThree.appendChild(descriptionText);
    
    var tableDataFour = tableRow.childNodes[3];
	tableDataFour.setAttribute("align","center");
	var shippedByOem = "shippedByOem"+inIndex+"_"+subInc;
	var shippedByOemCheckBox = createCheckBox("shippedByOem_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].installedParts["+subInc+"].shippedByOem",
												shippedByOem,inIndex,subInc);	    
    tableDataFour.appendChild(shippedByOemCheckBox);               
    
    var tableDataThree = tableRow.childNodes[4];
    tableDataThree.setAttribute("align","center");
    var deleteReplaced = tableDataThree.lastChild;
    dojo.connect(deleteReplaced,"onclick",function(evt){
    	dojo.dom.destroyNode(tableRow);
    });
    tableRow.setAttribute("subHussInstallRowIndex",subInc);
    tableBodyNode.appendChild(tableRow);
}

function createNonHussInstalledWidget(inIndex) {
	var tableBodyNode = document.getElementById("addRepeatBody_NonHussInstalled_"+inIndex);
	var subInc;
	var subRowIndex = dojo.byId("addRepeatBody_NonHussInstalled_"+inIndex).rows;
	if(subRowIndex[subRowIndex.length-1].getAttribute("subNonHussInstallRowIndex")!=null) {
		subInc = subRowIndex[subRowIndex.length-1].getAttribute("subNonHussInstallRowIndex");
		subInc=parseInt(subInc)+1;
	} else if(subRowIndex.length >2) {
		subRowIndex = parseInt(subRowIndex.length);
		subInc = subRowIndex-2;
	} else {
		subInc = 0;
	}
	var template = "<tr>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +                   
                   "<td class='partReplacedClass'><div class='repeat_del' id='${deleteRow}'/></td>" +
                   "</tr>";
	var markupTxt = dojo.string.substitute(template,{"deleteRow":"deleteRepeatRow_NonHussInstalled_"+inIndex+"_"+subInc}); 
	var tableRow = dojo.html.createNodesFromText(markupTxt);
	tableRow.setAttribute("id","NonHussmannInstalledRow_"+inIndex+"_"+subInc);
//	var tableDataOne = tableRow.childNodes[0];
//	tableDataOne.setAttribute("align","center");
//	var partNumberText = getNonHussmannPartNumberTextField("installedNonHussmanPartNumber_"+inIndex+"_"+subInc,"campaign.hussmanPartsReplacedInstalled["+inIndex+"].nonHussmanInstalledParts["+subInc+"].partNumber");
//	partNumberText.width="25%";
//    tableDataOne.appendChild(partNumberText.domNode);
    var tableDataThree = tableRow.childNodes[0];
    tableDataThree.setAttribute("align","center");
    var descriptionText = getNonHussDescriptionTextField("installedNonHussmanDescription_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].nonOEMpartsToReplace["+subInc+"].i18nNonOemPartsDescription[0].description");
    descriptionText.width="50%";
    tableDataThree.appendChild(descriptionText.domNode);     
    createHiddenForLocale("installedNonHussmanDescriptionLocal_"+inIndex+"_"+subInc,
    "campaign.hussPartsToReplace["+inIndex+"].nonOEMpartsToReplace["+subInc+"].i18nNonOemPartsDescription[0].locale",tableDataThree);        
	var tableDataTwo = tableRow.childNodes[1];
	tableDataTwo.setAttribute("align","center");
    var qunatityText = getQuantityTextField("installedNonHussmanQuantity_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].nonOEMpartsToReplace["+subInc+"].noOfUnits");
    tableDataTwo.appendChild(qunatityText);
    
    var tableDateFour = tableRow.childNodes[2];
    tableDateFour.setAttribute("align","center");
    var priceText = getNonHussmannPriceField("installedNonHussmanPrice_"+inIndex+"_"+subInc,"campaign.hussPartsToReplace["+inIndex+"].nonOEMpartsToReplace["+subInc+"].pricePerUnit",tableDateFour);
    var tableDataFive = tableRow.childNodes[3];
    tableDataFive.setAttribute("align","center");
    var deleteReplaced = tableDataFive.lastChild;
    dojo.connect(deleteReplaced,"onclick",function(evt){
    	dojo.dom.destroyNode(tableRow);
    });
    tableRow.setAttribute("subNonHussInstallRowIndex",subInc);
    tableBodyNode.appendChild(tableRow);
}

function createReplacedLabelsForRow(inIndex) {
    //var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);
	//var nameInnerTable = document.createElement("table");
//    var isProcessorReview = dojo.byId("processorReview").value;
    var nameTextInnerTr = document.createElement("tr");
	nameTextInnerTr.setAttribute("width","100%");
	// Used for Span (label preparation) creation in the TD
	var partNumberTextInnerTd = templateForRowLabel(i18N.part_number);
	partNumberTextInnerTd.setAttribute("width","25%");
	nameTextInnerTr.appendChild(partNumberTextInnerTd);
	var quantityTextInnerTd = templateForRowLabel(i18N.quantity);
	quantityTextInnerTd.setAttribute("width","10%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var descriptionTextInnerTd = templateForRowLabel(i18N.description);
//    if(isProcessorReview == 'true') {
        descriptionTextInnerTd.setAttribute("width","25%");
        nameTextInnerTr.appendChild(descriptionTextInnerTd);
		var returnLocationTextInnerTd = templateForRowLabel(i18N.return_location);
		returnLocationTextInnerTd .setAttribute("width","8%");
		nameTextInnerTr.appendChild(returnLocationTextInnerTd);
		var paymentDescriptionTextInnerTd = templateForRowLabel("Payment Description");
		paymentDescriptionTextInnerTd.setAttribute("width","15%");
		nameTextInnerTr.appendChild(paymentDescriptionTextInnerTd);
		var dueDaysTextInnerTd = templateForRowLabel(i18N.days);
		dueDaysTextInnerTd.setAttribute("width","5%");
		nameTextInnerTr.appendChild(dueDaysTextInnerTd);
//	} else {
//        descriptionTextInnerTd.setAttribute("width","53%");
//        nameTextInnerTr.appendChild(descriptionTextInnerTd);
//    }
    var emptyTd = document.createElement("td");
    emptyTd.setAttribute("width","10%");
    if(dojo.isIE) {
		emptyTd.setAttribute("className","partReplacedClass");
	}
	else {
		emptyTd.setAttribute("class","partReplacedClass");
	}
    nameTextInnerTr.appendChild(emptyTd);
    //nameInnerTable.appendChild(nameTextInnerTr);
	//tableBodyNode.appendChild(nameInnerTable);

    return nameTextInnerTr ;
}

function createHussmannLabelsForRow(inIndex) {
	//var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);
	//var nameInnerTable = document.createElement("table");
	var nameTextInnerTr = document.createElement("tr");
	nameTextInnerTr.setAttribute("width","100%");
	// Used for Span (label preparation) creation in the TD
	var partNumberTextInnerTd = templateForRowLabel(i18N.part_number);
	partNumberTextInnerTd.setAttribute("width","25%");
	nameTextInnerTr.appendChild(partNumberTextInnerTd);
	var quantityTextInnerTd = templateForRowLabel(i18N.quantity);
	quantityTextInnerTd.setAttribute("width","10%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var DescriptionTextInnerTd = templateForRowLabel(i18N.description);
	DescriptionTextInnerTd.setAttribute("width","35%");
	nameTextInnerTr.appendChild(DescriptionTextInnerTd);
	var ShipToDealerTextInnerTd = templateForRowLabel("Shipped By OEM");
	ShipToDealerTextInnerTd.setAttribute("width","20%");
	nameTextInnerTr.appendChild(ShipToDealerTextInnerTd);
	
    var emptyTd = document.createElement("td");
    emptyTd.setAttribute("width","10%");
    if(dojo.isIE) {
		emptyTd.setAttribute("className","partReplacedClass");
	}
	else {
		emptyTd.setAttribute("class","partReplacedClass");
	}
    nameTextInnerTr.appendChild(emptyTd);
    //var descriptionTextInnerTd = templateForRowLabel("Description");
	//descriptionTextInnerTd.setAttribute("width","10%");
	//nameTextInnerTr.appendChild(descriptionTextInnerTd)
	//nameInnerTable.appendChild(nameTextInnerTr);
	//tableBodyNode.appendChild(nameInnerTable);
	return nameTextInnerTr ;
}

function createNonHussmannLabelsForRow(inIndex) {
	//var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);
	//var nameInnerTable = document.createElement("table");
	var nameTextInnerTr = document.createElement("tr");
	nameTextInnerTr.setAttribute("width","100%");
	// Used for Span (label preparation) creation in the TD
//	var partNumberTextInnerTd = templateForRowLabel("Part Number");
//	partNumberTextInnerTd.setAttribute("width","25%");
//	nameTextInnerTr.appendChild(partNumberTextInnerTd);
	var quantityTextInnerTd = templateForRowLabel(i18N.description);
	quantityTextInnerTd.setAttribute("width","50%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var quantityTextInnerTd = templateForRowLabel(i18N.quantity);
	quantityTextInnerTd.setAttribute("width","20%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var priceTextInnerTd = templateForRowLabel(i18N.price);
	priceTextInnerTd.setAttribute("width","20%");
	nameTextInnerTr.appendChild(priceTextInnerTd);
    var emptyTd = document.createElement("td");
    emptyTd.setAttribute("width","10%");
    if(dojo.isIE) {
		emptyTd.setAttribute("className","partReplacedClass");
	}
	else {
		emptyTd.setAttribute("class","partReplacedClass");
	}
    nameTextInnerTr.appendChild(emptyTd);
    //nameInnerTable.appendChild(nameTextInnerTr);
	//tableBodyNode.appendChild(nameInnerTable);
	return nameTextInnerTr ;
}

function templateForRowLabel(name){
	var TextInnerTd = document.createElement("td");
    if(dojo.isIE) {
		TextInnerTd.setAttribute("className","partReplacedClass");
	}
	else {
		TextInnerTd.setAttribute("class","partReplacedClass");
	}
    var spanForName = document.createElement("span");
    spanForName.innerHTML = name;
	TextInnerTd.appendChild(spanForName);
	TextInnerTd.align="center";    
	return TextInnerTd;
}

function getPartNumberReplacedText(perId,perName,descId,inIndex,subInc){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        store:replacedPartStore,
        searchAttr:"label",
        hasDownArrow:false,
        sendDisplayedValueOnChange:false,
        notifyTopics:"/replacedPart/description/show/"+inIndex+"/"+subInc
    });
    
    dojo.subscribe("/replacedPart/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
    	if (type != "valuechanged") {
       		return;
    	}
	    twms.ajax.fireJavaScriptRequest("getUnserializedOemPartInfo.action",{
	    		claimType: 'Campaign',
	            number: number
	        }, function(details) {
	        	if(navigator.appName == 'Microsoft Internet Explorer') {
    				dojo.byId("descriptionSpan_"+descId).innerText=details[0];
			    }   
			    else {
			    	dojo.byId("descriptionSpan_"+descId).innerHTML=details[0];
			    }	
	        }
	    );
    });
    return textField;
}

function getPartNumberInstallText(perId,perName,descId,inIndex,subInc){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        store:replacedPartStore,
        searchAttr:"label",
        hasDownArrow:false,
        sendDisplayedValueOnChange:false,
        notifyTopics:"/installedPart/description/show/"+inIndex+"/"+subInc
    });
    
    dojo.subscribe("/installedPart/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
    	if (type != "valuechanged") {
       		return;
    	}
    	 twms.ajax.fireJavaScriptRequest("getUnserializedOemPartInfo.action",{
	    		claimType: 'Campaign',
	            number: number
	        }, function(details) {
	            if(navigator.appName == 'Microsoft Internet Explorer') {
    				dojo.byId("descriptionInstalledSpan_"+descId).innerText=details[0];
			    }   
			    else {
			    	dojo.byId("descriptionInstalledSpan_"+descId).innerHTML=details[0];
			    }	 
	        }
	    );
    });
    return textField;
}

function getNonHussmannPartNumberTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.ValidationTextBox({
		required: true,
        id:perId,
        name:perName,
        hasDownArrow:false
    });
    return textField;
}

function getQuantityTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    var numberTextField = document.createElement('input');
    numberTextField.type='text';
    numberTextField.id=perId;
    numberTextField.name=perName;
    numberTextField.size=3;
    numberTextField.setAttribute("width","10%");
    return numberTextField;
}

function getDescriptionTextField(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    
    var descriptionTextField = document.createElement('td');
    descriptionTextField.id=perId;
    var spanDescription=document.createElement('span');
    spanDescription.id="descriptionSpan_"+perId;
    descriptionTextField.appendChild(spanDescription);
    return descriptionTextField;
}

function getDescriptionTextFieldForInstalled(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }

    var descriptionTextField = document.createElement('td');
    descriptionTextField.id=perId;
    var spanDescription=document.createElement('span');
    spanDescription.id="descriptionInstalledSpan_"+perId;
    descriptionTextField.appendChild(spanDescription);
    return descriptionTextField;
}

function getNonHussDescriptionTextField(perId,perName){
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
    textField2.setAttribute("width","10%");
    textField2.setAttribute("size","5%");
    tableElement.appendChild(textField2);
}
// this is to distinguish between installed and removedParts
function createHiddenForPartReplaceRow(perId,perName,tableElement)
{   
  var  hiddenField = document.createElement('input');
  hiddenField.type="hidden";
  hiddenField.id=perId;
  hiddenField.name=perName;
  hiddenField.value="false";
  tableElement.appendChild(hiddenField);  
}

function createHiddenForLocale(perId,perName,tableElement)
{   
  var  hiddenField = document.createElement('input');
  hiddenField.type="hidden";
  hiddenField.id=perId;
  hiddenField.name=perName;
  hiddenField.value= dojo.byId("locale").value;
  tableElement.appendChild(hiddenField);  
}


function createHiddenForPartInstalledRow(perId,perName,tableElement)
{   
  var  hiddenField = document.createElement('input');
  hiddenField.type="hidden";
  hiddenField.id=perId;
  hiddenField.name=perName;
  hiddenField.value="true";
  tableElement.appendChild(hiddenField);  
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
        hasDownArrow:false
        //,disabled:true
    });
    return textField;
}

function getPaymentDescriptionTextField(perId,perName){
	 if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	 }
     var textField = document.createElement('select');
     textField.id=perId;
     textField.name=perName;
     //textField.disabled=false;
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
     var dueDaysTextField = document.createElement("input");
     dueDaysTextField.type="text";
     dueDaysTextField.id=perId;
     dueDaysTextField.name=perName;
     //dueDaysTextField.disabled=true;
     dueDaysTextField.size=3;
    return dueDaysTextField;
}

function getClaimInventoryLevel(inIndex,name,perName) {	
	if(dijit.byId(name+inIndex)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    var tableElement = document.createElement('td');
    var claimInventoryTable = document.createElement('table');
    var claimInventoryTableBody = document.createElement('tbody');
    var inventoryTr = createRadioNLabel(inIndex,"inventory_","Inventory",perName);
    claimInventoryTableBody.appendChild(inventoryTr);
	claimInventoryTable.appendChild(claimInventoryTableBody);
	var claimTr = createRadioNLabel(inIndex,"claim_","Claim",perName);
	claimInventoryTableBody.appendChild(claimTr);
	claimInventoryTable.appendChild(claimInventoryTableBody);
	tableElement.appendChild(claimInventoryTable);
	return tableElement;
}

function createRadioNLabel(inIndex,levelType,levelName,perName){
	var levelTr = document.createElement('tr');
	var levelTd = document.createElement('td');
	var radioId=levelType+name+inIndex;
	if(dojo.isIE) {
	/* This is to satisfy the stupid conditions that the IE sets itself in the creation of a radio button and
		a checkbox. The problem with the normal way of creating radio button is that the IE doesnt support 
		the setting	of the name property on these two controls and the control will be created but the user 
		wont be able to select	or does any kind of action on it. Hence this work around to make the controls work 
		in IE.	This might work or not in FF/Netscape/OPERA hence the else condition*/
		var levelRadio = document.
				createElement("<input type=\"radio\" id=\""+radioId+"\" name=\""+perName+"\"/>");
	} else {
	    var levelRadio = document.createElement('input');
	    levelRadio.type='radio';
		levelRadio.id=radioId;
	    levelRadio.name=perName;
	}
    if(levelName=='Inventory') {
    	levelRadio.value='true';
    } else {
    	levelRadio.value='false';
    }
    levelTd.appendChild(levelRadio);
    var spanForName = document.createElement("span");
    spanForName.innerHTML = levelName;
	levelTd.appendChild(spanForName);
	levelTr.appendChild(levelTd);
	return levelTr;
	
}

function createCheckBox(perId,perName,descId,inIndex,subInc){          
	if(dojo.isIE) {
	/* This is to satisfy the stupid conditions that the IE sets itself in the creation of a radio button and
		a checkbox. The problem with the normal way of creating radio button is that the IE doesnt support 
		the setting	of the name property on these two controls and the control will be created but the user 
		wont be able to select	or does any kind of action on it. Hence this work around to make the controls work 
		in IE.	This might work or not in FF/Netscape/OPERA hence the else condition*/
		var checkBoxShipped = document.
				createElement("<input type=\"checkbox\" id=\""+perId+"\" name=\""+perName+"\"/>");
	} else {
	    var checkBoxShipped = document.createElement('input');
	    checkBoxShipped.type='checkbox';
		checkBoxShipped.id=perId;
	    checkBoxShipped.name=perName;
	}
   return checkBoxShipped;
}