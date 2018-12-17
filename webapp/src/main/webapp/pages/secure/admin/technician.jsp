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
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<html>
<head>
<s:head theme="twms" />
<script type="text/javascript">
	dojo.require("dojox.layout.ContentPane");
	dojo.addOnLoad(function() {
		dojo.html.hide(dojo.byId("loadingIndicationDiv1"));
	});
	
	function populateDataForTechnician(/*selectedTechnicianName*/ selectedTechName) {
		dojo.html.hide(dojo.byId("technicianDetails"));
		dojo.html.show(dojo.byId("loadingIndicationDiv1"));
		twms.ajax.fireHtmlRequest("show_technician_details.action", {selectedTechnician:selectedTechName},
				function(data) {
					dijit.byId("technicianDetails").setContent(data);
					dojo.html.show(dojo.byId("technicianDetails"));
					dojo.html.hide(dojo.byId("loadingIndicationDiv1"));
				});
	}
	function showTechnicianDetails(){
		dojo.byId("selectedTechnician").value=dojo.byId("technicianId").value;
		populateDataForTechnician(selectedTechnician.value);
	}
</script>
</head>
<u:body>
	<table class="grid" cellpadding="0" cellspacing="0">
		<tr>
			<td id="technicianLabel" class="labelStyle" width="15%"
				nowrap="nowrap"><s:text name="label.technician.technicians" />:</td>
			<td><s:select id="technicianId"
					list="getTechnicianList()" disabled="false" listKey="key" listValue="value"
                                  id="technicianId" headerKey="-1"
					headerValue="%{getText('label.common.selectHeader')}" onChange="showTechnicianDetails()" /></td>
			<s:hidden id="selectedTechnician" value=""/>
		</tr>
	</table>
	<div dojoType="dojox.layout.ContentPane" id="technicianDetails"></div>
	<div id="loadingIndicationDiv1" style="width: 100%; height: 88%;">
		<div class='loadingLidThrobber'>
			<div class='loadingLidThrobberContent'>Loading...</div>
		</div>
	</div>

</u:body>
</html>