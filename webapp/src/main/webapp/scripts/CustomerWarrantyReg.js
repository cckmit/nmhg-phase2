dojo.require("twms.widget.Select");
	
function showMachineInfo(index) {
	var prod = dijit.byId('product'+index).getState();
	var deliveryDate = dijit.byId('deliveryDate_'+index).getDate();
	var formattedDate="";
	if(deliveryDate!=undefined && deliveryDate!=''){
		formattedDate = (deliveryDate.getMonth()+1) + '/' + deliveryDate.getDate() + '/' + 
			deliveryDate.getFullYear();  
	}
    twms.ajax.fireHtmlRequest("get_inventory_item.action",{
            itemId: prod.value,
            deliveryDateList: formattedDate,
            hoursOnMachineList: document.getElementById('hoursOnMachine_'+index).value,
            serialNumberList: document.getElementById('serialNumber_'+index).value,
            index:index
        }, function(data) {
			dojo.byId('itemDetails_'+index).innerHTML = data;
		}
    );
}