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
	tableTdDiv.setAttribute("width","100%");
	var newDiv = document.createElement("div");
	newDiv.setAttribute("width","100%");
	newDiv.setAttribute("height","100%");
	newDiv.setAttribute("id","replacedInstalledPartsWidgetDiv");
	
	var newTable = document.createElement("table");
	
	newTable.setAttribute("width","100%");
    dojo.addClass(newTable, "borderAddClass");
	var newTBody = document.createElement("tbody");
	newTBody.setAttribute("id","divTbody_"+inIndex);
	var newTr = document.createElement("tr");
	var newTd = document.createElement("td");
	newTd.setAttribute("width","100%");
	//--- Creating a seperation between Main and Sub Section Or Multi Claim/Inventory Level ---//
	var tableSpace = document.createElement("table");
	var tableSpaceBody = document.createElement("tbody");
	var tableSpaceTr = document.createElement("tr");
	var tableSpaceTd = document.createElement("td");
		
	tableSpaceTr.appendChild(tableSpaceTd);
	tableSpaceBody.appendChild(tableSpaceTr);
	tableSpace.appendChild(tableSpaceBody);
	newTd.appendChild(tableSpace);
	newTr.appendChild(newTd);
	newTBody.appendChild(newTr);
	newTable.appendChild(newTBody);
	newDiv.appendChild(newTable);

	var checkBoxTableSpace = document.createElement("table");
	var checkBoxTableSpaceBody = document.createElement("tbody");
	
	if(	dojo.byId("multipleClaim").value == "true") {

		if(dojo.isIE){
		    var inventoryLevelRadio = "<INPUT TYPE=RADIO id=multiClaim_inventoryLevel_" + inIndex ;
			var inventoryRadioButton = document.createElement(inventoryLevelRadio + " NAME=\"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].inventoryLevel\" VALUE=\"true\">");

			var claimLevelRadio = "<INPUT TYPE=RADIO id=multiClaim_claimLevel_" + inIndex ;
			var claimRadioButton = document.createElement(claimLevelRadio + " NAME=\"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].inventoryLevel\" VALUE=\"false\">");
		} else {
			var inventoryRadioButton = document.createElement("input");
			inventoryRadioButton.setAttribute("type", "radio");
			inventoryRadioButton.setAttribute("id", "multiClaim_inventoryLevel_"+inIndex);
			inventoryRadioButton.setAttribute("name", "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].inventoryLevel");
			inventoryRadioButton.setAttribute("value", "true");
			var claimRadioButton = document.createElement("input");
			claimRadioButton.setAttribute("type", "radio");
			claimRadioButton.setAttribute("id", "multiClaim_claimLevel_"+inIndex);
			claimRadioButton.setAttribute("name", "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].inventoryLevel");
			claimRadioButton.setAttribute("value", "false");
		}
		var partQuantityRow = document.createElement("tr");
		partQuantityRow.setAttribute("align","center");
		partQuantityRow.setAttribute("width","100%");
		var partQuantityColumn = document.createElement("td");
		partQuantityColumn.setAttribute("nowrap", "nowrap");
		partQuantityColumn.setAttribute("align","center");
		partQuantityColumn.setAttribute("colspan","5");
		partQuantityColumn.setAttribute("width","100%");
		var radioLabel = document.createTextNode("Part Quantity specified per :  ");
		partQuantityColumn.appendChild(radioLabel);
		
		var inventoryLabel = document.createTextNode("  Inventory  ");
		partQuantityColumn.appendChild(inventoryRadioButton);
		partQuantityColumn.appendChild(inventoryLabel);
		
		var claimLabel = document.createTextNode("  Claim  "); 
		partQuantityColumn.appendChild(claimRadioButton);
		partQuantityColumn.appendChild(claimLabel);
		
		partQuantityRow.appendChild(partQuantityColumn);
		checkBoxTableSpaceBody.appendChild(partQuantityRow);
	}

	checkBoxTableSpace.appendChild(checkBoxTableSpaceBody);
	newTd.appendChild(checkBoxTableSpace);

	
	//------ Creation of Replaced Widget ----------//
	var tableReplaced = document.createElement("table");
	tableReplaced.setAttribute("width","100%");
    dojo.addClass(tableReplaced, "tspace");
	
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
    dojo.addClass(tableHussInstalled, "tspace");

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
	//var emptyLineBreak = document.createElement("br");
    //newDiv.appendChild(emptyLineBreak);
	
	tableTdDiv.appendChild(newDiv);
	tableTrDiv.appendChild(tableTdDiv);
	var tablebody = document.getElementById("addRepeatBody");
	tablebody.appendChild(tableTrDiv);
	if( dojo.byId("rowIndexes") == null ) {
		var rowIndex = document.createElement("input");
		rowIndex.type="hidden";
		rowIndex.id="rowIndexes";
		rowIndex.value=parseInt(inIndex)+parseInt(1);
		tablebody.appendChild(rowIndex);
	} else {
		dojo.byId("rowIndexes").value=parseInt(dojo.byId("rowIndexes").value)+parseInt(1);
	}
}

function createNewRow(inIndex,labelName,deleteButtonId,addButtonId) {
	
	var tableRowReplaced = document.createElement("tr");
	tableRowReplaced.setAttribute("width","100%");
    dojo.addClass(tableRowReplaced, "title");

    var tableTdReplaced = document.createElement("td");
	tableTdReplaced.setAttribute("width","10%");
	var spanForPartNumber = createLabel(labelName);
    tableTdReplaced.appendChild(spanForPartNumber);
	tableRowReplaced.appendChild(tableTdReplaced);
	
	var tableTdSpace1 = document.createElement("td");
	tableTdSpace1.setAttribute("width","10%");
    var tableTdReplacedSN = document.createElement("td");
	tableTdReplacedSN.setAttribute("width","5%");
	
    tableRowReplaced.appendChild(tableTdSpace1);
    tableRowReplaced.appendChild(tableTdReplacedSN); 
    if(dojo.byId("processorReview").value == 'true') {
    	var tableTdSpaceUOMUP = document.createElement("td");
    	tableTdSpaceUOMUP.setAttribute("width","8%");
        var tableTdSpaceUOM = document.createElement("td");
        tableTdSpaceUOM.setAttribute("width","8%");
        var tableTdSpaceUOMCP = document.createElement("td");
        tableTdSpaceUOMCP.setAttribute("width","8%");
        var tableTdSpace2 = document.createElement("td");
        tableTdSpace2.setAttribute("width","10%");
        var tableTdSpace3 = document.createElement("td");
        tableTdSpace3.setAttribute("width","5%");
        var tableTdSpace4 = document.createElement("td");
        tableTdSpace4.setAttribute("width","8%");
        var tableTdSpace5 = document.createElement("td");
        tableTdSpace5.setAttribute("width","12%");
        var tableDeleteTdReplaced = document.createElement("td");
        tableDeleteTdReplaced.setAttribute("width","5%");
        //function to create the labels
        tableRowReplaced.appendChild(tableTdSpaceUOMUP);
        tableRowReplaced.appendChild(tableTdSpaceUOM);
        tableRowReplaced.appendChild(tableTdSpaceUOMCP);
        tableRowReplaced.appendChild(tableTdSpace2);
        tableRowReplaced.appendChild(tableTdSpace3);
        tableRowReplaced.appendChild(tableTdSpace4);
        tableRowReplaced.appendChild(tableTdSpace5);
        tableRowReplaced.appendChild(tableDeleteTdReplaced);
    } else {
        var tableTdSpace = document.createElement("td");
        tableTdSpace.setAttribute("width","64%");
        tableRowReplaced.appendChild(tableTdSpace);
    }

    var tableFailureReportTdSpace = document.createElement("td");
    tableFailureReportTdSpace.setAttribute("width","7%");
    tableRowReplaced.appendChild(tableFailureReportTdSpace);
    //function to create add button with a unique id
    var tableAddTdReplaced = document.createElement("td");
    tableAddTdReplaced.setAttribute("width","5%");
    tableAddTdReplaced.setAttribute("align","center");
    var addReplacedButton = createAddButton(addButtonId,inIndex);
    tableAddTdReplaced.appendChild(addReplacedButton);
	tableRowReplaced.appendChild(tableAddTdReplaced);
	return tableRowReplaced;
}

function createNewHussInstallRow(inIndex,labelName,deleteButtonId,addButtonId){
    var tableRowReplaced = document.createElement("tr");
	tableRowReplaced.setAttribute("width","100%");
    dojo.addClass(tableRowReplaced, "title");

    var tableTdReplaced0 = document.createElement("td");
	tableTdReplaced0.setAttribute("width","10%");
	var spanForPartNumber = createLabel(labelName);	
	tableTdReplaced0.appendChild(spanForPartNumber);
	tableRowReplaced.appendChild(tableTdReplaced0);	
	
	var tableTdReplaced = document.createElement("td");
	tableTdReplaced.setAttribute("width","10%");
	
	var tableTdSpace1 = document.createElement("td");
	tableTdSpace1.setAttribute("width","5%");
	
	 if(dojo.byId("processorReview").value == 'true') {
	    	var tableTdSpaceUOMUP = document.createElement("td");
	    	tableTdSpaceUOMUP.setAttribute("width","8%");
	        var tableTdSpaceUOM = document.createElement("td");
	        tableTdSpaceUOM.setAttribute("width","8%");
	        var tableTdSpaceUOMCP = document.createElement("td");
	        tableTdSpaceUOMCP.setAttribute("width","8%");
	        var tableTdSpace2 = document.createElement("td");
	    	tableTdSpace2.setAttribute("width","40%");
	 }
	 else {
	var tableTdSpace2 = document.createElement("td");
	tableTdSpace2.setAttribute("width","64%");
	 }

    var tableFailureReportTdSpace3 = document.createElement("td");
	tableFailureReportTdSpace3.setAttribute("width","7%");

    var tableAddTdReplaced = document.createElement("td");
	tableAddTdReplaced.setAttribute("width","5%");
	tableAddTdReplaced.setAttribute("align","center");
	
	//function to create the labels	
	tableRowReplaced.appendChild(tableTdReplaced);
	tableRowReplaced.appendChild(tableTdSpace1);
	 if(dojo.byId("processorReview").value == 'true') {
		 tableRowReplaced.appendChild(tableTdSpaceUOMUP);
		 tableRowReplaced.appendChild(tableTdSpaceUOM);
		 tableRowReplaced.appendChild(tableTdSpaceUOMCP);
	 }
	tableRowReplaced.appendChild(tableTdSpace2);
    tableRowReplaced.appendChild(tableFailureReportTdSpace3);

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
    dojo.addClass(deleteReplacedButton, "repeat_del");

    deleteReplacedButton.setAttribute("id",buttonIdname+"_"+inIndex);
    deleteReplacedButton.onclick = function(evt) {
    	 
    	 var deleteSpan = dojo.byId("deleteSection_" + inIndex); 
         var collectionName = "__remove.task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled";	
       
         var row = getExpectedParent(deleteSpan, "tr");         
         var deleter = document.createElement("input");
         deleter.type = "hidden";
         deleter.name = collectionName;
         deleter.id="remove_"+buttonIdname+"_"+inIndex;
         row.parentNode.appendChild(deleter);                  
     	 dojo.dom.removeNode(row);	
     	 dojo.query("input[id ^= "+value+"]").forEach(function(node){
			if(node){
				dojo.dom.removeNode(node); 
			}
		});
     	 delete deleter;
     	 delete row;
     	 delete collectionName;
     	 delete deleteSpan;
     	    	 
    }
 	return deleteReplacedButton;
}



function createAddButton(buttonIdName,inIndex) {
	var addButton = document.createElement("div");
    dojo.addClass(addButton, "repeat_add");
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
    }
    return addButton;
}

function createReplacedWidget(inIndex) {	
 	var showSerialNumber = dojo.byId("showPartSerialNumber").value;
    var tableBodyNode = document.getElementById("addRepeatBody_Replaced_"+inIndex);    
	var subInc;
	var subInx=getCorrectIndex(inIndex);	
	var subRowIndex = dojo.byId("addRepeatBody_Replaced_"+inIndex).rows;	
	if(subRowIndex[subRowIndex.length-1].getAttribute("subReplacedRowIndex")!=null) {
		subInc = subRowIndex[subRowIndex.length-1].getAttribute("subReplacedRowIndex");
		subInc=parseInt(subInc)+1;
	} else if(subRowIndex.length >2) {		
		subRowIndex = parseInt(subRowIndex.length);
		subInc = subRowIndex-1;
	} else {
		
		subInc = 0;
	}	
	subInc = subInx>subInc?subInx:subInc;
    var isProcessorReview = dojo.byId("processorReview").value;
    var template = "<tr>";
    if(showSerialNumber == 'true') {
    	template = template + "<td class='partReplacedClass'></td>";
    }
    
	template = template + "<td class='partReplacedClass'></td>" +                   
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
    if(isProcessorReview == 'true') {
        template = template+"<td class='partReplacedClass'></td>" +
			       "<td class='partReplacedClass'></td>" +
			       "<td class='partReplacedClass'></td>" +
			       "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
    } else {
    	template = template+"<td class='partReplacedClass'></td>";
    }
    
    template = template +"<td class='partReplacedClass'><div class='repeat_del' id='${deleteRow}'/></td></tr>";
        
	var markupTxt = dojo.string.substitute(template,{"deleteRow":"deleteRepeatRow_Replaced_"+inIndex+"_"+subInc});
	var tableRow = dojo.html.createNodesFromText(markupTxt);
	tableRow.setAttribute("id","ReplacedRow_"+inIndex+"_"+subInc);
	
	var replacedPartDesc = "replacedPartDescription_"+inIndex+"_"+subInc;
	var replacedPartUomUP = "replacedPartUomUP_"+inIndex+"_"+subInc;
	var replacedPartUom = "replacedPartUOM_"+inIndex+"_"+subInc;
	var replacedPartUomCP = "replacedPartUomCP_"+inIndex+"_"+subInc;

	var childNodeIndex = -1; // This is used to handel the BU Config for Display serial number

	if(showSerialNumber == 'true') {

		var tableDataZero = tableRow.childNodes[++childNodeIndex];
		tableDataZero.setAttribute("align","center");
		var serialNumberText = getSerialNumberReplacedText("replacedPartSerialNumber_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].itemReference.referredInventoryItem",
													replacedPartDesc,inIndex,subInc);	
		tableDataZero.appendChild(serialNumberText.domNode);
	}
	var tableDataOne = tableRow.childNodes[++childNodeIndex];
	tableDataOne.setAttribute("align","center");	
	var partNumberText = getPartNumberReplacedText("replacedPartNumber_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].itemReference.referredItem",
												replacedPartDesc,inIndex,subInc);
    tableDataOne.appendChild(partNumberText.domNode);
	
	var tableDataTwo = tableRow.childNodes[++childNodeIndex];
	tableDataTwo.setAttribute("align","center");
    var qunatityText = getQuantityTextField("replacedQuantity_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].numberOfUnits");
    qunatityText.setAttribute("readOnly","true");
    tableDataTwo.appendChild(qunatityText);
        	
     if (isProcessorReview == 'true') {   
    	 
	    	var tableDataUomUP = tableRow.childNodes[++childNodeIndex];
	    	tableDataUomUP.setAttribute("align","center");
	 	    var uomUPText = getUOMUPTextField(replacedPartUomUP);
	 	   tableDataUomUP.appendChild(uomUPText);
 	    
	 	    var tableDataUOM = tableRow.childNodes[++childNodeIndex];
	 	   tableDataUOM.setAttribute("align","center");
		    var uomText = getUOMTextField(replacedPartUom);
		    tableDataUOM.appendChild(uomText);
		    
		    var tableDataUomCP = tableRow.childNodes[++childNodeIndex];
		    tableDataUomCP.setAttribute("align","center");
		    var uomCPText = getUOMCPTextField(replacedPartUomCP);
		    tableDataUomCP.appendChild(uomCPText);
		    
    	 	var tableDataThree = tableRow.childNodes[++childNodeIndex];
    		tableDataThree.setAttribute("align","center");
    	    var descriptionText = getDescriptionTextField(replacedPartDesc);
    	    tableDataThree.appendChild(descriptionText);
    	    
        	var tableDataFour = tableRow.childNodes[++childNodeIndex];
        	tableDataFour.setAttribute("align","center");
        	partNumberText = getPartReturnCheckTextField("oemRepPart_"+inIndex+"_"+subInc+"_toBeReturned","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].partToBeReturned",inIndex,subInc);
        	       	 
        	tableDataFour.appendChild(partNumberText);
        	var tableDataFive = tableRow.childNodes[++childNodeIndex];
        	tableDataFive.setAttribute("align","center");
        	partNumberText = getReturnLocationTextField("oemRepPart_"+inIndex+"_"+subInc+"_location","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].partReturn.returnLocation");
        	
        	tableDataFive.appendChild(partNumberText.domNode);
        	var tableDataSix = tableRow.childNodes[++childNodeIndex];
        	tableDataSix.setAttribute("align","center");
        	partNumberText = getPaymentDescriptionTextField("oemRepPart_"+inIndex+"_"+subInc+"_paymentCondition","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].partReturn.paymentCondition");
        	
        	tableDataSix.appendChild(partNumberText);
        	var tableDataSeven  = tableRow.childNodes[++childNodeIndex];
        	tableDataSeven.setAttribute("align","center");
        	partNumberText = getDueDaysTextField("oemRepPart_"+inIndex+"_"+subInc+"_dueDays","task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].replacedParts["+subInc+"].partReturn.dueDays");

        	tableDataSeven.appendChild(partNumberText);

            var tableDataEight = tableRow.childNodes[++childNodeIndex];
            tableDataEight.setAttribute("align", "center");
            var failureReportText = getFailureReportEmptyField("replacedFailureReport_"+inIndex+"_"+subInc);
            tableDataEight.appendChild(failureReportText);  
            
            var tableDataNine = tableRow.childNodes[++childNodeIndex];        	
          	tableDataNine.setAttribute("align","center");
         	var deleteReplaced = tableDataNine.lastChild;
         	dojo.connect(deleteReplaced,"onclick",function(evt){
         	    	dojo.dom.destroyNode(tableRow);
         	    }); 
             
            
        } else {
	        	var tableDataThree = tableRow.childNodes[++childNodeIndex];
	        	tableDataThree.setAttribute("align","center");
	            var descriptionText = getDescriptionTextField(replacedPartDesc);
	            tableDataThree.appendChild(descriptionText);
	            
                var tableDataFour = tableRow.childNodes[++childNodeIndex];
                tableDataFour.setAttribute("align", "center");
                var failureReportText = getFailureReportEmptyField("replacedFailureReport_"+inIndex+"_"+subInc);
                tableDataFour.appendChild(failureReportText);

                if(showSerialNumber == 'true') {
	                var tableDataFive = tableRow.childNodes[++childNodeIndex];                
	        		tableDataFive.setAttribute("align","center");
			        var deleteReplaced = tableDataFive.lastChild;		      
				    dojo.connect(deleteReplaced,"onclick",function(evt){
					   	dojo.dom.destroyNode(tableRow);
				    });
			    }
			    else
			    {
			    	var tableDataFive = tableRow.childNodes[++childNodeIndex];                
        			tableDataFive.setAttribute("align","center");
		        	var deleteReplaced = tableDataFive.lastChild;		      
			    	dojo.connect(deleteReplaced,"onclick",function(evt){
			   		dojo.dom.destroyNode(tableRow);
			   		
			    });
			    }
        	}
     	
    tableRow.setAttribute("subReplacedRowIndex",subInc);
    tableBodyNode.appendChild(tableRow);
}

// Added to Correct Index in the Replaced Installed Widget
function getCorrectIndex(inIndex)
{	var subInx=0;	
	 dojo.query("input[id ^='remove_deleteRepeatRow_']").forEach(function(node, index, arr){	    
	      var nodeId = new String(node.id);
	      var tempIndex = nodeId.substring(nodeId.length-3, nodeId.length);
	      var inIndexTemp = parseInt(tempIndex.charAt(0));	    
	      if(inIndexTemp==inIndex){	    	
	      subInx = subInx > (parseInt(tempIndex.charAt(2))+1) ? subInx : (parseInt(tempIndex.charAt(2))+1);
	      }	          
	  });
	 return subInx;
}

function createHussInstalledWidget(inIndex) {	
    var showSerialNumber = dojo.byId("showPartSerialNumber").value;
	var subRowIndex = dojo.byId("addRepeatBody_HussInstalled_"+inIndex).rows;
	var tableBodyNode = document.getElementById("addRepeatBody_HussInstalled_"+inIndex);	
	var subInc;
	var subInx=getCorrectIndex(inIndex);	
	var isProcessorReview = dojo.byId("processorReview").value;
	if(subRowIndex[subRowIndex.length-1].getAttribute("subHussInstallRowIndex")!=null) {		
		subInc = subRowIndex[subRowIndex.length-1].getAttribute("subHussInstallRowIndex");
		subInc=parseInt(subInc)+1;		
	} else if(subRowIndex.length > 2) {	    
		subRowIndex = parseInt(subRowIndex.length);
		subInc = subRowIndex-1;
	} else {		
		subInc = 0;
	}	
	subInc = subInx>subInc?subInx:subInc;
	var template = "<tr>";
    if(showSerialNumber == 'true') {
    	template = template + "<td class='partReplacedClass'></td>";
    }
    
    if(isProcessorReview == 'true') {
        template = template+"<td class='partReplacedClass'></td>" +
			       "<td class='partReplacedClass'></td>" +
			       "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
    } else 
    	{
    	template = template+ "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" +
                   "<td class='partReplacedClass'></td>" ;
    	}
    template = template +"<td class='partReplacedClass'><div class='repeat_del' id='${deleteRow}'/></td></tr>";
	var markupTxt = dojo.string.substitute(template,{"deleteRow":"deleteRepeatRow_HussInstalled_"+inIndex+"_"+subInc}); 
	var tableRow = dojo.html.createNodesFromText(markupTxt);
	tableRow.setAttribute("id","HussmannInstalledRow_"+inIndex+"_"+subInc);
	
	var childNodeIndex = -1
	if(showSerialNumber == 'true') {
		var tableDataZero = tableRow.childNodes[++childNodeIndex];
		tableDataZero.setAttribute("align","center");
		var serialNumberText = getSerialNumberTextField("installedHussmanSerialNumber_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].hussmanInstalledParts["+subInc+"].serialNumber");																	
	    tableDataZero.appendChild(serialNumberText);
	}
	var installedPartUomUP = "installedPartUomUP_"+inIndex+"_"+subInc;
	var installedPartUom = "installedPartUom_"+inIndex+"_"+subInc;
	var installedPartUomCP = "installedPartUomCP_"+inIndex+"_"+subInc;	
	var tableDataOne = tableRow.childNodes[++childNodeIndex];
	tableDataOne.setAttribute("align","center");
	var installedPartDesc = "installedHussPartDescription_"+inIndex+"_"+subInc;
	var partNumberText = getPartNumberInstallText("installedHussmanPartNumber_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].hussmanInstalledParts["+subInc+"].item",
												installedPartDesc,inIndex,subInc);
    tableDataOne.appendChild(partNumberText.domNode);
	
	var tableDataTwo = tableRow.childNodes[++childNodeIndex];
	tableDataTwo.setAttribute("align","center");
    var qunatityText = getQuantityTextField("installedHussmanQuantity_"+inIndex+"_"+subInc,"task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled["+inIndex+"].hussmanInstalledParts["+subInc+"].numberOfUnits");
    tableDataTwo.appendChild(qunatityText);
    if (isProcessorReview == 'true') {      	 
    	var tableDataInsUomUP = tableRow.childNodes[++childNodeIndex];
    	tableDataInsUomUP.setAttribute("align","center");
 	    var uomUPText = getUOMUPTextField(installedPartUomUP);
 	   tableDataInsUomUP.appendChild(uomUPText);
	    
 	    var tableDataInsUOM = tableRow.childNodes[++childNodeIndex];
 	    tableDataInsUOM.setAttribute("align","center");
	    var uomText = getUOMTextField(installedPartUom);
	    tableDataInsUOM.appendChild(uomText);
	    
	    var tableDataInsUomCP = tableRow.childNodes[++childNodeIndex];
	    tableDataInsUomCP.setAttribute("align","center");
	    var uomCPText = getUOMCPTextField(installedPartUomCP);
	    tableDataInsUomCP.appendChild(uomCPText);
    }
    var tableDataThree = tableRow.childNodes[++childNodeIndex];
	tableDataThree.setAttribute("align","center");
    var descriptionText = getDescriptionTextFieldForInstalled(installedPartDesc);
    tableDataThree.appendChild(descriptionText);

    var tableDataFour = tableRow.childNodes[++childNodeIndex];
	tableDataFour.setAttribute("align","center");
    var failureReportText = getFailureReportEmptyField("installedFailureReport_"+inIndex+"_"+subInc);
    tableDataFour.appendChild(failureReportText);

		var tableDataFive = tableRow.childNodes[++childNodeIndex];
	    tableDataFive.setAttribute("align","center");
	    var deleteReplaced = tableDataFive.lastChild;
	    dojo.connect(deleteReplaced,"onclick",function(evt){
	    	dojo.dom.destroyNode(tableRow);
	    });
    
    tableRow.setAttribute("subHussInstallRowIndex",subInc);
    tableBodyNode.appendChild(tableRow);
}


function createReplacedLabelsForRow(inIndex) {
 	var showSerialNumber = dojo.byId("showPartSerialNumber").value;

    var isProcessorReview = dojo.byId("processorReview").value;
    var nameTextInnerTr = document.createElement("tr");
	nameTextInnerTr.setAttribute("width","100%");
	// Used for Span (label preparation) creation in the TD
	
	if(showSerialNumber == 'true') {
		var serialNumberTextInnerTd = templateForRowLabel(i18N.serial_number);
		serialNumberTextInnerTd.setAttribute("width","10%");
		nameTextInnerTr.appendChild(serialNumberTextInnerTd);
	}
	var partNumberTextInnerTd = templateForRowLabel(i18N.part_number);
		if(showSerialNumber == 'true') {
				partNumberTextInnerTd.setAttribute("width","10%");
		}
		else
		{
			partNumberTextInnerTd.setAttribute("width","20%");	
		}
		
	nameTextInnerTr.appendChild(partNumberTextInnerTd);
	var quantityTextInnerTd = templateForRowLabel(i18N.quantity);
	quantityTextInnerTd.setAttribute("width","5%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var descriptionTextInnerTd = templateForRowLabel(i18N.description);
    if(isProcessorReview == 'true') {
    	var uomUPTextInnerTd = templateForRowLabel(i18N.unitPrice);
    	uomUPTextInnerTd.setAttribute("width","8%");
        nameTextInnerTr.appendChild(uomUPTextInnerTd);
        var uomTextInnerTd = templateForRowLabel(i18N.uomDisplay);
        uomTextInnerTd.setAttribute("width","8%");
        nameTextInnerTr.appendChild(uomTextInnerTd);
        var uomCPTextInnerTd = templateForRowLabel(i18N.unitCostPrice);
        uomCPTextInnerTd.setAttribute("width","8%");
        nameTextInnerTr.appendChild(uomCPTextInnerTd);
        descriptionTextInnerTd.setAttribute("width","10%");
        nameTextInnerTr.appendChild(descriptionTextInnerTd);
	    var partToReturnTextInnerTd = templateForRowLabel(i18N.recovery_return);
		partToReturnTextInnerTd.setAttribute("width","5%");
		nameTextInnerTr.appendChild(partToReturnTextInnerTd);
		var returnLocationTextInnerTd = templateForRowLabel(i18N.return_location);
		returnLocationTextInnerTd .setAttribute("width","8%");
		nameTextInnerTr.appendChild(returnLocationTextInnerTd);
		var paymentDescriptionTextInnerTd = templateForRowLabel(i18N.payment_condition);
		paymentDescriptionTextInnerTd.setAttribute("width","12%");
		nameTextInnerTr.appendChild(paymentDescriptionTextInnerTd);
		var dueDaysTextInnerTd = templateForRowLabel(i18N.days);
		dueDaysTextInnerTd.setAttribute("width","5%");
		nameTextInnerTr.appendChild(dueDaysTextInnerTd);
	} else {
        descriptionTextInnerTd.setAttribute("width","64%");
        nameTextInnerTr.appendChild(descriptionTextInnerTd);
    }

    var failureReportTd = templateForRowLabel(i18N.failure_report);
    failureReportTd.setAttribute("width", "7%");
    nameTextInnerTr.appendChild(failureReportTd);

    var emptyTd = document.createElement("td");
    emptyTd.setAttribute("width","10%");
    dojo.addClass(emptyTd, "partReplacedClass");
    nameTextInnerTr.appendChild(emptyTd);   

    return nameTextInnerTr ;
}

function createHussmannLabelsForRow(inIndex) {
	var showSerialNumber = dojo.byId("showPartSerialNumber").value;
	var isProcessorReview = dojo.byId("processorReview").value;
	var nameTextInnerTr = document.createElement("tr");
	nameTextInnerTr.setAttribute("width","100%");
	// Used for Span (label preparation) creation in the TD
	
	if(showSerialNumber == 'true') {
	var serialNumberTextInnerTd = templateForRowLabel(i18N.serial_number);
	serialNumberTextInnerTd.setAttribute("width","10%");
	nameTextInnerTr.appendChild(serialNumberTextInnerTd);
	}	
	var partNumberTextInnerTd = templateForRowLabel(i18N.part_number);
	if(showSerialNumber == 'true') {
		partNumberTextInnerTd.setAttribute("width","10%");
	}
	else
	{
		partNumberTextInnerTd.setAttribute("width","20%");	
	}
	nameTextInnerTr.appendChild(partNumberTextInnerTd);
	
	var quantityTextInnerTd = templateForRowLabel(i18N.quantity);
	quantityTextInnerTd.setAttribute("width","5%");
	nameTextInnerTr.appendChild(quantityTextInnerTd);
	var DescriptionTextInnerTd = templateForRowLabel(i18N.description);
	
	if(isProcessorReview == 'true') {
	    var uomUPTextInnerTd = templateForRowLabel(i18N.unitPrice);
	    uomUPTextInnerTd.setAttribute("width","8%");
	    nameTextInnerTr.appendChild(uomUPTextInnerTd);
	    var uomTextInnerTd = templateForRowLabel(i18N.uomDisplay);
	    uomTextInnerTd.setAttribute("width","8%");
	    nameTextInnerTr.appendChild(uomTextInnerTd);
	    var uomCPTextInnerTd = templateForRowLabel(i18N.unitCostPrice);
	    uomCPTextInnerTd.setAttribute("width","8%");
	    nameTextInnerTr.appendChild(uomCPTextInnerTd);
	    DescriptionTextInnerTd.setAttribute("width","40%");
	 }
	else {
		DescriptionTextInnerTd.setAttribute("width","64%");	}
	
	
	nameTextInnerTr.appendChild(DescriptionTextInnerTd);

    var failureReportTd = templateForRowLabel(i18N.failure_report);
    failureReportTd.setAttribute("width", "6%");
    nameTextInnerTr.appendChild(failureReportTd);

    var emptyTd = document.createElement("td");
    emptyTd.setAttribute("width","5%");
    dojo.addClass(emptyTd, "partReplacedClass");
    nameTextInnerTr.appendChild(emptyTd);    
	return nameTextInnerTr ;
}


function templateForRowLabel(name){
	var TextInnerTd = document.createElement("td");
    dojo.addClass(TextInnerTd, "partReplacedClass");
    var spanForName = document.createElement("span");
    spanForName.innerHTML = name;
	TextInnerTd.appendChild(spanForName);
	TextInnerTd.align="center";    
	return TextInnerTd;
}

function getPartNumberReplacedText(perId,perName,descId,inIndex,subInc){
	console.debug("insidegetPart");
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
        autoSelectIfSingleResult:true,
        autoSelectFirstValueOnTab:false,
        style: "width: 90px",
        notifyTopics:"/replacedPart/description/show/"+inIndex+"/"+subInc
    });
    
    dojo.subscribe("/replacedPart/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
    	if (type != "valuechanged") {
       		return;
    	}
	    twms.ajax.fireJavaScriptRequest("getUnserializedOemPartDetails.action",{
	    		claimNumber: dojo.byId("claimId").value,
	            number: number
	        }, function(details) {
                dojo.html.set(dojo.byId("descriptionSpan_"+descId), details[0]);
	        }
	    );
		dojo.byId("replacedQuantity_" + inIndex + "_" + subInc).readOnly = false;
    });
    return textField;
}


function  defaultClaimLevel(inIndex)  {
	var claimLevel_inIndex=false;
	dojo.query("input[id ^= "+value+"]").forEach(function(node){
		if(node.value=='true'){
			claimLevel_inIndex=true;	
		}
	});

	if(claimLevel_inIndex==true){
	     dojo.byId("multiClaim_claimLevel_"+inIndex).checked=true;
	     dojo.byId("multiClaim_inventoryLevel_"+inIndex).disabled=true;
	}else{
		dojo.byId("multiClaim_claimLevel_"+inIndex).checked=false;
		 dojo.byId("multiClaim_inventoryLevel_"+inIndex).disabled=false;
	}
};




function getSerialNumberReplacedText(perId,perName,descId,inIndex,subInc){

	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
    var textField = new twms.widget.Select({
		required: true,
        id:perId,
        name:perName,
        store:replacedPartSerialNumber,
        searchAttr:"label",
        hasDownArrow:false,
        autoSelectIfSingleResult:true,
        autoSelectFirstValueOnTab:false,
        style: "width: 90px;",
        notifyTopics:"/replacedPartSerialNumber/description/show/"+inIndex+"/"+subInc
    });
    
    dojo.subscribe("/replacedPartSerialNumber/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
    	if (type != "valuechanged") {
       		return;
    	}
	    twms.ajax.fireJavaScriptRequest("getSerializedOemPartDetails.action",{
	            number: dijit.byId("replacedPartSerialNumber_"+inIndex+"_" +subInc).getValue()
	        }, function(details) {
	            dojo.byId("replacedPartNumber_"+inIndex+"_"+subInc).value = details[1];	            
	            dijit.byId("replacedPartNumber_"+inIndex+"_"+subInc).setDisabled(true);	        
                dojo.html.set(dojo.byId("replacedPartDescription_"+inIndex+"_"+subInc), details[0]);
	            dojo.byId("replacedQuantity_"+inIndex+"_"+subInc).value = 1;
             	dojo.byId("replacedQuantity_"+inIndex+"_"+subInc).readOnly = true;	            
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
        store:installedPartStore,
        searchAttr:"label",
        hasDownArrow:false,
        autoSelectIfSingleResult:true,
        autoSelectFirstValueOnTab:false,
        style: "width: 90px;",
        notifyTopics:"/installedPart/description/show/"+inIndex+"/"+subInc
    });
    
    dojo.subscribe("/installedPart/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
    	if (type != "valuechanged") {
       		return;
    	}
	    twms.ajax.fireJavaScriptRequest("getUnserializedOemPartDetails.action",{
	    		claimNumber: dojo.byId("claimId").value,
	            number: number
	        }, function(details) {
                dojo.html.set(dojo.byId("descriptionInstalledSpan_"+descId), details[0]);
	        }
	    );
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

function getSerialNumberTextField(perId,perName){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    var numberTextField = document.createElement('input');
    numberTextField.type='text';
    numberTextField.id=perId;
    numberTextField.name=perName;
    numberTextField.size=15;
    dojo.addClass(numberTextField, "setWidth");
    numberTextField.setAttribute("width","20%");
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

function getUOMUPTextField(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    
    var uomUPTextField = document.createElement('td');
    uomUPTextField.id=perId;
    var spanUomUP=document.createElement('span');
    spanUomUP.id="uomUPSpan_"+perId;
    uomUPTextField.appendChild(spanUomUP);
    return uomUPTextField;
}

function getUOMTextField(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    
	 var uomTextField = document.createElement('td');
	    uomTextField.id=perId;
	    var spanUom=document.createElement('span');
	    spanUom.id="uomUPSpan_"+perId;
	    uomTextField.appendChild(spanUom);
	    return uomTextField;
}

function getUOMCPTextField(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }
    
	  var uomCPTextField = document.createElement('td');
	    uomCPTextField.id=perId;
	    var spanUomCP=document.createElement('span');
	    spanUomCP.id="uomCPSpan_"+perId;
	    uomCPTextField.appendChild(spanUomCP);
	    return uomCPTextField;
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

function getPartReturnCheckTextField(perId,perName,incInd,subIncIndex){
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
	}
	var isPartReturned;
	// To make sure this works in IE and as well as in Mozilla 
	// try block is for IE and catch for other browsers 
	if (dojo.isIE <= 8) {
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
        hasDownArrow:false,
        autoSelectIfSingleResult:true,
        autoSelectFirstValueOnTab:false,
        style: "width: 90px;",
        disabled:true
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
     textField.disabled=true;
     dojo.addClass(textField, "setWidth");
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
     dueDaysTextField.disabled=true;
     dueDaysTextField.size=3;
    return dueDaysTextField;
}

function getFailureReportEmptyField(perId) {
	if(dijit.byId(perId)!=null){
    	dijit.byId(perId).destroyRecursive();
    }

    var failurereportField = document.createElement('td');
    failurereportField.id=perId;
    return failurereportField;
}
