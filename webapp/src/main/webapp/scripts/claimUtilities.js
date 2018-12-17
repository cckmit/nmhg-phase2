/*This javascript function is used to display the failure reports in differnet tabs for
 * a) Causal Part if it has failure report
 * b) Installed Part if it has failure report
 * c) Replaced Part if it has failure report
 * The function can be later moved to a .js file. But we need to see how the struts tag can be avoided in .js file*/
function displayFailureReport(claimId, customReportAnswerId, failureReportName, itemId, invItemId, serialNumber,replacedPart) {	
	var tabLabel = "";
	try
	{
	tabLabel = i18N.failure_report + "-" + failureReportName;
	}
	catch(e)
	{
	tabLabel = customReportAnswerId;
	}
    var url = "show_failure_report.action";
    if (claimId && claimId != "") {
        url += "?claim=" + claimId;
    } else {
        throw new Error("Display of Failure Report cannot be rendered without Claim Id : Claim Id is empty");
    }   
    if (customReportAnswerId && customReportAnswerId != "") {
        url += "&customReportAnswer=" + customReportAnswerId;
    } else {
        throw new Error("Display of Failure Report cannot be rendered without Custom Report Answer Id : Answer id is empty");
    } 
    if (itemId && itemId != "") {    	
        url += "&item=" + itemId;      
    }
    if (invItemId && invItemId != "") {
        url += "&inventoryItem=" + invItemId;
    }
    if (serialNumber && serialNumber != "") {
        url += "&unSzdSlNo=" + serialNumber;
    }
    if(replacedPart && replacedPart!="") {
    	url += "&replacedPart=" + replacedPart;
    }
    parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel(), forceNewTab : true});
    delete url,tabLabel;
}


function printFailureReport(claimId, customReportAnswerId, itemId, invItemId, serialNumber,replacedPart)
{
    var url = "print_failure_report.action";
    if (claimId && claimId != "") {
        url += "?claim=" + claimId;
    } else {
        throw new Error("Display of Failure Report cannot be rendered without Claim Id : Claim Id is empty");
    }
    if (customReportAnswerId && customReportAnswerId != "") {
        url += "&customReportAnswer=" + customReportAnswerId;
    } else {
        throw new Error("Display of Failure Report cannot be rendered without Custom Report Answer Id : Answer id is empty");
    }
    if (itemId && itemId != "") {
        url += "&item=" + itemId;
    }
    if (invItemId && invItemId != "") {
        url += "&inventoryItem=" + invItemId;
    }
    if (serialNumber && serialNumber != "") {
        url += "&unSzdSlNo=" + serialNumber;
    }    
    if(replacedPart && replacedPart!="") {
    	url += "&replacedPart=" + replacedPart;
    }
    var win = "";
    window.open(url, 'win', 'directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
    delete url,claimId, customReportAnswerId,itemId, invItemId, serialNumber;
    return false;
}
