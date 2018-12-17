function deliveryReport(selectedBusinessUnit) {
    var businessUnit=selectedBusinessUnit;
    publishEvent("/tab/open", {
        label: i18N.new_warranty_registration,
        url: "create_warranty.action?allowInventorySelection=true&selectedBusinessUnit="+businessUnit,
        decendentOf: i18N.home_tab_label
    });
}

function equipmentTransfer(selectedBusinessUnit) {
    var businessUnit=selectedBusinessUnit;
    publishEvent("/tab/open", {
        label: i18N.warranty_transfer,
        url: "show_warranty_transfer.action?allowInventorySelection=true&selectedBusinessUnit="+businessUnit,
        decendentOf: i18N.home_tab_label
    });
}

