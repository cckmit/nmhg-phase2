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
function showDetails(id,methodName) {
	window.open('<%request.getContextPath();%>'+"errorSummary!"+methodName+".action?id="+id,
'Error Message','width=650,height=350,left=150,top=150,scrollbars=yes,menubar=no,status=no,toolbar=no,resizable=yes');
}
</SCRIPT>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link href="stylesheet/master.css" rel="stylesheet" type="text/css">
<title>:: Integration Adapter ::</title>
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
    <td style="padding:5px; "><img src="image/logo.gif" width="158" height="67"></td>
  </tr>
</table>
<table width="100%"  border="0" cellpadding="0" cellspacing="0" id="pageTitle">
  <tr>
    <td width="80%" class="heading"><a href=<%request.getContextPath();%>"summarySearch.action">Summary</a>&nbsp;&nbsp;|&nbsp;&nbsp; <span class="SelectedText">Error Summary</span>&nbsp;&nbsp; |&nbsp;&nbsp; <a href=<%request.getContextPath();%>"syncDetail!displaySync.action">Sync. Details</a> </td>
  </tr>
</table>
<s:form action="errorSummarySearch!fetchErrorSummary.action" method="post">
<table width="100%"  border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="padd5">&nbsp;</td>
  </tr>
  <tr>
    <td class="subHead">Error Summary </td>
  </tr>
 
</table>
<s:fielderror />
<s:actionerror />
<table width="100%"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="label1Bold">&nbsp;</td>
    <td valign="middle" class="label1Bold">Sync.Type:</td>
    <td nowrap="nowrap" class="field">
	 <label>
		<s:select name="select" name="syncSummaryCriteria.syncType" list="syncTypeList" />
     </label></td>
  </tr>
  <tr>
 <td width="5%" class="label1Bold">&nbsp;</td>
    <td width="8%" valign="middle" class="label1Bold">Date: <img src="image/req.gif" width="12" height="12"  align="absmiddle" /></td>
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
				<s:submit type="button" value="%{'Submit'}" label="Search" cssClass="buttonGeneric" />
               </td>
  </tr>
</table>
</s:form>
<br />
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="bgColor">
  <tr>
    <td width="2%" nowrap class="columnHead"><div align="center">#</div></td>
    <td width="18%" nowrap class="columnHead">Error Message </td>
    <td width="80%" nowrap class="columnHead">Number of Records</td>
  </tr>
   <s:iterator value="errorSummaryList" status="rowstatus">
  <tr>
    <td width="2%" nowrap class="column"><s:property value="#rowstatus.index+1" /></td>
    <td width="18%" nowrap class="column"><a href="javascript:showDetails('<s:property value="id"/>','getErrorMessgeById');"><s:property value="errorMessage" /></a></td>
    <td width="80%" nowrap class="column"><s:property value="numberOfRecords" /></td>
  </tr>
 </s:iterator>
</table>
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
