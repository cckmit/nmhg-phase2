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

<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="scripts/admin.js"></script>
    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>

    <s:head theme="twms"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <style type="text/css">
        h3 {
            color: #555555;
            font-size: 10pt;
            font-weight: bold;
            padding-left: 5px;
            margin-bottom: 0px;
        }
    </style>
</head>
<u:body>
<u:actionResults/>

<s:push value="campaignNotification.item">
	<div>
	    <jsp:include page="itemInfo.jsp" flush="true" />
	</div>
</s:push>
<s:push value="campaignNotification.campaign">
	<!-- Campaign Info -->
	<div>
		<jsp:include page="/pages/secure/admin/campaign/read/campaignInfo.jsp" flush="true" />
	</div>
	<div>
	<jsp:include page="/pages/secure/admin/campaign/read/campaignUpdateInfo.jsp" flush="true" />
   </div>
</s:push>

</u:body>
</html>