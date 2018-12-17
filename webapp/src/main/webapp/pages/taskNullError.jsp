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


<%@taglib prefix="s" uri="/struts-tags"%>
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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title><s:text name="title.common.errorPage"/></title>
<s:head theme="twms"/>
    <style type="text/css">
        .errorSection {
            margin-top: 5px;
            border: 1px solid orange;
            font-size:9pt;
            font-family: monospace;
            font-weight: normal;
            background-color: #cccccc;
            overflow:auto;
            height: 400px;
            padding: 5px;
        }

        #detailsToggler {
            margin-top: 15px;
            text-decoration: underline;
            cursor: pointer;
            color: #cfcfcf;
            font-size: small;
        }
    </style>
</head>
<u:body>
	<div style="font-size:14pt;color:red;margin-left: 5px;padding-left: 50px;">
        An unexpected error has occured while processing the claim. Please try again.
    </div>
</u:body>
</html>