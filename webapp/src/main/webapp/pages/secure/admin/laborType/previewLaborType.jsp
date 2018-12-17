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
<html>
<head>
    <s:head theme="twms"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>
<s:form name="baseForm" id="baseFormId" theme="twms">
  <div class="admin_section_div" style="margin:5px;width:99%">
    <div class="admin_section_heading">
      <s:text name="label.laborType.details" />
    </div>
    
      <table width="99%" class="grid" cellspacing="0" cellpadding="0">
        <tr>
          <td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.laborType.laborType.name" />:
            </td>
          <td><s:property value="laborType.laborType" /></td>
        </tr>
        <tr>
          <td class="labelStyle"  width="20%" nowrap="nowrap"><s:text name="label.laborType.multiplication.value" />:
            </td>
          <td><s:property value="laborType.multiplicationValue" /></td>
        </tr>        
      </table>
       
  </div>
</s:form>
</u:body>
</html>