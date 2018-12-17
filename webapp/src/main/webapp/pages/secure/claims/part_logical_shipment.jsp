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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<title><s:text name="title.viewClaim.generateLogicalShipment"/></title>
<s:head theme="twms"/>
</head>
<u:body>
<h2><s:text name="label.viewClaim.logicalShipments"/></h2>
<br>
<a href="logical_shipment_submit.action"><s:text name="link.viewClaim.generatePartShipments"/></a>
<br/>
<a href="logical_dealer_shipment_submit.action"><s:text name="link.viewClaim.generateDealerShipments"/></a>
<br/>
<br/>
<br/>
<s:if test="#shipments.isEmpty()">
		<s:text name="message.viewClaim.noShipmentGenerated"/>
</s:if>
<s:else>
		<s:text name="message.viewClaim.shipmentsGenerated"/> <br />
	<s:iterator value="shipments">
			<s:text name="message.viewClaim.shipmentsGeneratedPrefix1"/>
			[<s:property value="id" />]
			<s:text name="message.viewClaim.shipmentsGeneratedSuffix1"/> 
			[<s:property value="supplier.name" />]
			<s:text name="message.viewClaim.shipmentsGeneratedSuffix2"/> 
			<s:property value="parts" />
		<s:property value="supplierParts" />
	</s:iterator>
</s:else>


</u:body>
</html>
