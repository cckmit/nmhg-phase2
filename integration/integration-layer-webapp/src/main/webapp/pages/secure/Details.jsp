
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<SCRIPT language="JavaScript">
function showDetails(remoteId,methodName) {
	window.open('<%request.getContextPath();%>'+"details!"+methodName+".action?remoteId="+remoteId,
'Payload','width=650,height=350,left=150,top=150,scrollbars=yes,menubar=no,status=no,toolbar=no,resizable=yes');
}
</SCRIPT>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link href="stylesheet/master.css" rel="stylesheet" type="text/css">
<title>:: Integration Layer ::</title>
</head>
<!-- start: added for getting the Calendar in Claim Search screen to work -->
<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
<!-- end -->
<body scroll="no">
<table width="100%"  border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td style="padding:5px; "><img src="image/logoIL.gif" width="158" height="67"></td>
  </tr>
</table>
<table width="100%"  border="0" cellpadding="0" cellspacing="0" id="pageTitle">
  <tr>
    <td width="80%" class="heading"><a href=<%request.getContextPath();%>"summary!display.action"> Summary</a>&nbsp;&nbsp;|&nbsp;&nbsp; <span class="SelectedText">Details</span></td>
  </tr>
</table>
<s:form action="detailsSearch!fetchDetails.action" method="post">
<table width="100%"  border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="padd5">&nbsp;</td>
  </tr>
  <tr>
    <td class="subHead"> Details </td>
  </tr>
</table>
<s:fielderror />
<s:actionerror />
<table width="100%"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="5%" class="label1Bold">&nbsp;</td>
    <td width="13%" valign="middle" class="label1Bold">Date: <img src="image/req.gif" width="12" height="12"  align="absmiddle" /></td>
              <td nowrap="nowrap" class="field" width="87%"><s:text name="Start Date" />
                 <s:textfield name="startDate" id="startDate" size="12" maxlength="12"/>
                 <img src="image/calendar.gif" alt="Choose Date" width="23" height="17" border="0" align="absmiddle"
                 	id="startDate_Trigger" style="cursor: pointer;" />
				<script type="text/javascript">
                    Calendar.setup({
						inputField     :    "startDate",
						ifFormat       :    "%m/%d/%Y",
						button         :    "startDate_Trigger"
					});
				</script>
               <s:text name="End Date" />
                 <s:textfield name="endDate" id="endDate" size="12" maxlength="12"/>
                 <img src="image/calendar.gif" alt="Choose Date" width="23" height="17" border="0" align="absmiddle"
                 	id="endDate_Trigger" style="cursor: pointer;" />
				<script type="text/javascript">
				    Calendar.setup({
				        inputField     :    "endDate",
				        ifFormat       :    "%m/%d/%Y",
				        button         :    "endDate_Trigger"
				    });
				</script>
				
               </td>
	
  </tr>
  		   
  <tr>
    <td width="5%" class="label1Bold">&nbsp;</td>
    <td width="13%" valign="middle" class="label1Bold">BOD Type :</td>
    <td width="82%" nowrap="nowrap" class="field"> <s:select name="bodType" list="bodTypeList" /><s:submit type="button" value="%{'Submit'}" label="Search" cssClass="buttonGeneric"/></td>
  </tr>
</table>
<br />
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="bgColor">
  <tr>
    <td width="2%" nowrap class="columnHead"><div align="center">#</div></td>
    <td width="49%" nowrap class="columnHead">Pay Load </td>
    <td width="49%" nowrap class="columnHead">Error Message </td>
  </tr>
  <s:iterator value="remoteInteractionList" status="rowstatus">
  <tr>

    <td width="2%" nowrap class="column"><s:property value="#rowstatus.index+1"/></td>
    <td width="23%" nowrap class="column"><a href="javascript:showDetails('<s:property value="remoteId"/>','getPayloadById');">
<s:property value="payloadTruncated" /></a></td>
    <td width="75%" nowrap class="column"><a href="javascript:showDetails('<s:property value="remoteId"/>','getErrorMessageById');"><s:property value="errorMessageTruncated" /></a></td>
  </tr>
   </s:iterator>
</table>
</s:form>
<table width="100%" border="0" cellpadding="0" cellspacing="0" id="footer">
  <td class="copyRights">&copy; Copyright 2006, TAVANT Technologies. All rights reserved.</td>
</table>
<s:if test="showMuleLogo=='true'"> 
<table border="0" width="300">
	<tr><td valign="top">MuleSource Inc.</td></tr>
	<tr><td valign="middle"><img src="image/mulesource_license_logo.gif" width="250" height="52"></td></tr>
	<tr><td valign="top">http://www.mulesource.com</td></tr>
</table>
</s:if>
</body>
</html>
