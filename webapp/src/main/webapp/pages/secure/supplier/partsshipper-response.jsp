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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
<s:head theme="twms" />
<title>Supplier Recovery Admin Response</title>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>

<script type="text/javascript">
dojo.addOnLoad ( function() {
	<s:if test="! getTaskInstancesForSupplier(id).isEmpty()">
		setTimeout('document.getElementById("partShipperGenerateShipment_preview").submit()', 4000);
	</s:if>
	/* window.open('partShipperUpdateTag_shipmentTag.action?shipment=<s:property value="shipment.id"/>','ShipmentTag',
		'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=680,height=400,top=115,left=230'); */
	
});

</script>
</head>
<u:body>
<u:actionResults wipeMessages="false"/>
<s:form action="partShipperGenerateShipment_preview">
	<s:hidden name="id"/>
</s:form>
</u:body>
</html>