function launchNewWarranyRegistration(/*String*/ dataId, /*String*/ decendentOf,/*String*/ businessUnit) {
	var url = "create_warranty.action";
	var tabLabel = i18N.new_warranty_registration;

    url += "?allowInventorySelection=true"+"&selectedBusinessUnit="+businessUnit;
    if (dataId) {
		url += "&inventoryItem=" + dataId;
		tabLabel += " " + dataId;
	}

    parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOf});

	delete url, tabLabel;
}

function launchEquipmentTransfer(/*String*/ dataId, /*String*/ decendentOf,/*String*/ businessUnit) {
	var url = "show_warranty_transfer.action";
	var tabLabel = i18N.warranty_transfer;

    url += "?allowInventorySelection=true&firstTimeETR=true"+"&selectedBusinessUnit="+businessUnit;
	if (dataId) {
		url += "&inventoryItem=" + dataId;
		tabLabel += " " + dataId;
	}
	parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOf});

	delete url, tabLabel;
}

function createCampaignClaim(/*String*/ dataId, /*String*/ decendentOf) {
	if(dataId == null || dataId.trim.length() == 0){
		return false;
	}
	var url = "create_campaign_claim.action";
	var tabLabel = i18N.createCampaignClaim;
	if (dataId) {
		url += "?id=" + dataId;
		tabLabel += " " + dataId;
	}
	parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : decendentOf});

	delete url, tabLabel;
}
