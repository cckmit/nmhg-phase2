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
<s:form action="summary!fetchSummary.action" method="post">
<table width="100%"  border="0" cellpadding="0" cellspacing="0" id="pageTitle">
  <tr>
    <td width="80%" class="heading">
	<s:property value="errorMessage" />
	</td>
  </tr>
</table>
</s:form>
<table width="100%" border="0" cellpadding="0" cellspacing="0" id="footer">
  <td class="copyRights">&copy; Copyright 2006, TAVANT Technologies. All rights reserved.</td>
</table>
<table border="0" width="300">
	<tr><td valign="top">MuleSource Inc.</td></tr>
	<tr><td valign="middle"><img src="image/mulesource_license_logo.gif" width="250" height="52"></td></tr>
	<tr><td valign="top">http://www.mulesource.com</td></tr>
</table>
</body>
</html>
