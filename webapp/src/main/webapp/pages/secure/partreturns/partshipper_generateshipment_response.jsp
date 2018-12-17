<%--

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

--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>


<html>
  <s:head theme="twms"/>
	<script type="text/javascript" src="scripts/partReturnPopup.js"></script>
	<script type="text/javascript" src="scripts/clientMachineDate.js"></script>
    <script type="text/javascript">

      function popUpShipmentTagForDealer(shipmentId){
         if(shipmentId == null || shipmentId == "")
                return;
         
         var dateTime=getPrintDate();
         popupPrintpR('partreturns_displayForPrint_for_dealer.action?shipmentIdString=' +  shipmentId +'&clientDate='+dateTime);
      }
      function updateMasterView(id, shipmentString) {
      //popUpShipmentTagForDealer(shipmentString);
      var tabDetails = getTabDetailsForIframe();
		// var ids = id.split(",");
		//manageRowHide("partReturnTable", id);
        // parent.hideCompletedRows(tabDetails.tabId, ids);
      }
       dojo.addOnLoad(function() {
              var summaryTableId = getFrameAttribute("TST_ID");
              if (summaryTableId) {
                  manageTableRefresh(summaryTableId, true);
              top.publishEvent("/refresh/folderCount");
              }
          });
      </script>
     </script>

  <body onLoad="updateMasterView('<s:property value='id'/>', '<s:property value="shipmentIdString" /> ')">
  <u:actionResults wipeMessages="false"/>

  </body>
</html>

