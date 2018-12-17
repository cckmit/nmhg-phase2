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
<%@taglib prefix="s" uri="/struts-tags" %>
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
    <title><s:text name="title.laborType"/></title>
    <s:head theme="twms"/>
    
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    

    
    <script type="text/javascript" src="scripts/RepeatTable.js"></script>
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript">
        function validate(inputComponent) {

        }
    </script>
</head>
<u:body>
<s:form name="baseForm" id="baseFormId" theme="twms">
<u:actionResults/>
<s:hidden name="laborType" value="%{laborType.id}"/>
<div class="admin_section_div" style="margin:5px;width:99%">
    <div class="admin_section_heading"><s:text name="label.laborType.details" /></div>
    
        <table class="grid" width="100%" style="margin-top:10px;">
            <tr>
              <td>
                  <label><s:text name="label.laborType.laborType.name"/>:</label>
              </td>
              <td class="non_editable">
                  <s:property value="%{laborType.laborType}"/>
              </td>
            </tr>
            <tr>
              <td>
                  <label><s:text name="label.laborType.multiplication.value"/>:</label>
              </td>
              <td> 
                  <s:textfield name="laborType.multiplicationValue" value="%{laborType.multiplicationValue}"/>
              </td>
            </tr>     
        </table>
    </div>
    <div align="center" style="margin-top:10px;">
	  <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
      <s:submit cssClass="buttonGeneric" value="%{getText('button.common.update')}"  action="update_laborType"/>
      <s:submit cssClass="buttonGeneric" value="%{getText('button.common.delete')}"  action="delete_laborType"/>
    </div>
</s:form>
<authz:ifPermitted resource="warrantyAdminManageLaborSplitReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
