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

<%@ page contentType="text/html"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<s:head theme="twms" />

</head>
<u:body>
	<table class="grid" cellpadding="0" cellspacing="0">
		<tr>
			<td><s:select id="servicingLocationId"
					list="servicingLocations" disabled="false" listKey="id" listValue="getShipToCodeAppended()" headerKey="-1"
					name = "modifier.servicingLocation" headerValue="%{getText('label.common.selectHeader')}" cssStyle="width:450px;"/></td>
		</tr>
	</table>
</u:body>
</html>