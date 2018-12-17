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
    <s:if test="pageReadOnlyAdditional">
    dojo.addOnLoad(function() {
        twms.util.makePageReadOnly("dishonourReadOnly");
    });
    </s:if>
    dojo.require("dijit.layout.ContentPane");
      var __masterCheckBoxControls = new Array();
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
       function invokePrintPopup(shipmentId){
    		var win = "";
    		var dateTime=getPrintDate();
    		var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&wpra='+true;
    		window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
    		return false;
    	 }

    	function invokePrintPopupForDealer(shipmentId){
    		var win = "";
    		var dateTime=getPrintDate();
    		var actionUrlString = 'partreturns_displayForPrint_for_dealer.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&wpra='+true;
    		window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
    		return false;
    	 }
      </script>

<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
	dojo.require("dojox.layout.ContentPane");
</script>
    <u:body>  
  <%--<body onLoad="updateMasterView('<s:property value='id'/>', '<s:property value="shipmentIdString" />')">--%>
  <u:actionResults wipeMessages="false"/>
   <div class="separator"></div>
  
    <div class="buttonWrapperPrimary">
    <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
	    <s:if test="taskName.equals('Shipment Generated For Dealer')">
	    <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
	        " class="buttonGeneric" onclick="invokePrintPopupForDealer(<s:property value="%{id}"/>)"/>   
	   </s:if>
	   <s:else>
	      <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
	        " class="buttonGeneric" onclick="invokePrintPopup(
	        <s:property value="%{id}"/>
	        )"/>      
	    </s:else>
      </s:if> 
        </div>
 
</u:body>
</html>

