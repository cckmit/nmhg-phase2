var prePopulatedDealerName;
var prePopulatedDealerNumber;
dojo.addOnLoad(function() {
    dojo.subscribe("/multipleInventorySearch/searchStatus", null, handleSearchStatus);
    dojo.connect(dojo.byId("resetSearch"), "onclick", function() {
    	if(dojo.byId("searchDealerName")){
    		prePopulatedDealerName=dojo.byId("searchDealerName").value;
			dojo.byId("searchDealerName").value="";
		}
    	if(dojo.byId("searchDealerNumber")){
    		prePopulatedDealerNumber=dojo.byId("searchDealerNumber").value
			dojo.byId("searchDealerNumber").value="";
		}
        if(dojo.byId("searchSerialNumber")){
            dojo.byId("searchSerialNumber").value="";
        }
        if(dojo.byId("searchFactoryOrderNumber")){
            dojo.byId("searchFactoryOrderNumber").value="";
        }
        if(dojo.byId("searchModelNumber")){
		    dojo.byId("searchModelNumber").value="";
        }
        if(dojo.byId("searchEndCustomer")){
		dojo.byId("searchEndCustomer").value="";
		}
		dojo.publish("/setPrePopulatedValues");
	});
});

function handleSearchStatus(message) {
    dojo.html.setDisplay(dojo.byId("noSearchParamsErrorSection"), message.validationFailed);
}
