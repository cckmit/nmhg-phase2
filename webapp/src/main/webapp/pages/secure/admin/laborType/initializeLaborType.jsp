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
    <!-- Scripts and stylesheet for the Calendar -->
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    <!-- End of scripts and stylesheet for the Calendar -->

    <!-- Javascripts and stylesheets for Admin section -->
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
<s:hidden name="laborType.id" />
<div class="admin_section_div" style="margin:5px;width:99%">
    <div class="admin_section_heading"><s:text name="label.laborType.details" /></div>
    
        <table class="grid" width="100%" style="margin-top:10px;">
            <tr>
              <td  width="20%" nowrap="nowrap">
                  <label class="labelStyle"><s:text name="label.laborType.laborType.name"/>:</label>
              </td>
              <td>
                  <s:textfield name="laborType.laborType"/>
              </td>
            </tr>
            <tr>
              <td nowrap="nowrap">
                  <label class="labelStyle"><s:text name="label.laborType.multiplication.value"/>:</label>
              </td>
              <td>
                  <s:textfield name="laborType.multiplicationValue"/>
              </td>
            </tr>     
        </table>
     </div>
    <div align="center" style="margin:10px 0px 0px 0px;">
	  <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
      <s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}"  action="create_labor_type"/>
    </div>
</s:form>
</u:body>
</html>
