/*

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

*/
function popupPrintpR(win_name){
	var win = "";	
	win = window.open(win_name,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
}
function popUpShipmentTag(shipmentId){	
	if(shipmentId == null || shipmentId == "")
		return;
		
	popupPrintpR('partreturns_displayForPrint.action?shipmentIdString=' +  shipmentId);			    
}
    
    
