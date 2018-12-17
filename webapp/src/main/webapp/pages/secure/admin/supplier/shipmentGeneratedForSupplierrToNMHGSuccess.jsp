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
    dojo.require("dijit.layout.ContentPane");
 	dojo.require("dijit.layout.LayoutContainer");
       function invokePrintPopup(shipmentId){
       	var win = "";
       	var name='Parts Shipped to NMHG'
       	var dateTime=getPrintDate();
       	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&taskName='+name;
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
	      <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
	        " class="buttonGeneric" onclick="invokePrintPopup(
	        <s:property value="%{id}"/>
	        )"/>      
        </div>
 
</u:body>
</html>

