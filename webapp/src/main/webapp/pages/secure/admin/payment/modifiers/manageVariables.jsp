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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    
</head>
<u:body>
<form name="baseForm" id="baseForm">
<div style="font-color:red;">
	<s:actionerror/>
	<s:fielderror/>
</div>
<s:actionmessage/>
<div id="seperator"></div>
<div class="admin_section_div" style="width:100%">
	<div class="admin_section_heading">
		<s:text name="label.managePayment.selectModifier"/>
	</div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-left:3px;">
		<tr>
			<td class="labelStyle" width="15%"><s:text name="label.managePayment.modifierName"/>:</td>
			<td class="labelNormal" width="15%"><s:textfield cssClass="admin_selections" name="paymentVariable.name" size="50" maxlength="200"/></td>
		</tr>
		<tr>
			<td class="labelStyle" width="15%" nowrap="nowrap"><s:text name="label.managePayment.section"/></td>
			<td class="labelNormal" width="85%">
                <s:select name="paymentVariable.section" list="sections"
                          headerKey="" headerValue="--Select--" listKey="id.toString()" listValue="%{getText(messageKey)}" 
			value="id.toString()"  cssClass="admin_selections"></s:select></td>
		</tr>
	
	</table>
</div>	 
<div class="spacingAtTop" align="center">
<s:submit cssClass="buttonGeneric" action="create_payment_variable" value="%{getText('button.common.create')}"/>
</div>
	<div class="admin_section_div"  style="width:100%" id="chooseExistingMod">
		<div class="admin_section_subheading"><s:text name="label.managePayment.chooseModifier"/></div>
		<table class="grid borderForTable" cellpadding="0" cellspacing="0" width="100%" align="center" style="margin:5px;">
			<tr class="row_head">
				<th width="30%"><s:text name="label.managePayment.modifier"/></th>
				<th width="25%"><s:text name="label.managePayment.section"/></th>				
				<th width="15%"><s:text name="label.common.action"/></th>
				<th width="15%"><s:text name="label.common.editModifier"/></th>
				<th width="15%"><s:text name="label.common.delete"/></th>
			</tr>
			<s:iterator value="paymentVariables" status="criteriaStatus">
			<tr> 
				<td> 
					<s:property value="top.name"/>
				</td>
				<td>
					<s:text name="%{getMessageKey(top.section.name)}"/>
					
				</td>
				<td>
					<div id="maintain_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]" style="cursor:pointer;color:blue;text-decoration:underline"><s:text name="label.managePayment.setValues"/></div>
<script type="text/javascript">
	dojo.addOnLoad(
	 	function() {
	  		dojo.connect(dojo.byId("maintain_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]"), "onmousedown", function(event) {
		  		var url = 'list_payment_modifiers.action?paymentVariableId=<s:property value="top.id"/>';
		        var tabLabel = '<s:property value="top.name"/>';
		        var thisTabLabel = getMyTabLabel();
				top.publishEvent("/tab/open", { label: tabLabel, 
												url: url,
												decendentOf: thisTabLabel
												});
	  	});
	});
</script>
				</td>
				<td>
					<div id="internationalize_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]" style="cursor:pointer;color:blue;text-decoration:underline"><s:text name="label.common.internationalize"/></div>				
<script type="text/javascript">
	dojo.addOnLoad(
	 	function() {
	  		dojo.connect(dojo.byId("internationalize_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]"), "onmousedown", function(event) {
		  		var url = 'editModifierName.action?paymentVariableId=<s:property value="top.id"/>';
		        window.location=url;
	  	});
	});
</script>
				</td>
				<td>
					<div id="deactivate_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]" style="cursor:pointer;color:blue;text-decoration:underline"><s:text name="label.common.delete"/></div>				
<script type="text/javascript">
	dojo.addOnLoad(
	 	function() {
	  		dojo.connect(dojo.byId("deactivate_modifier_list_variables[<s:property value="#criteriaStatus.index"/>]"), "onmousedown", function(event) {
		  		var url = 'setupFordeactivateVariable.action?paymentVariableId=<s:property value="top.id"/>';
		        var tabLabel = '<s:property value="top.name"/>';
		        var thisTabLabel = getMyTabLabel();
				top.publishEvent("/tab/open", { label: tabLabel, 
												url: url,
												decendentOf: thisTabLabel
												});
	  	});
	});
</script>
				</td>

				
			</tr>	
			</s:iterator>
		</table>					
	</div>	
	
</form>
<authz:ifPermitted resource="warrantyAdminCreateClaimPaymentModifierReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	    	document.getElementById("chooseExistingMod").style.display="none";
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });

	</script>
</authz:ifPermitted>
</u:body>
</html>
