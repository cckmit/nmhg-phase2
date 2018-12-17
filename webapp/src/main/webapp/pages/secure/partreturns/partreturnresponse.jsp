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
	<u:stylePicker fileName="partreturn.css"/>
	<u:stylePicker fileName="common.css"/>
    <script type="text/javascript">
      function updateMasterView(id, shipmentString) {
      	popUpShipmentTag(shipmentString);
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
       
       function invokePrintPopupForDealer(shipmentId){
    		var win = "";
    		var dateTime=getPrintDate();
    		var actionUrlString = 'partreturns_displayForPrint_for_dealer.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime;
    		window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
    		return false;
    	 }
      </script>
      
  
 
  <%--<body onLoad="updateMasterView('<s:property value='id'/>', '<s:property value="shipmentIdString" />')">--%>
  <u:actionResults wipeMessages="false"/>
    <s:if test="taskName.equals('Shipment Generated For Dealer')">
   <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
        " class="buttonGeneric" style="margin-left:42%" onclick="invokePrintPopupForDealer(<s:property value="%{id}"/>)"/> 
         </s:if>
  </body>
</html>

