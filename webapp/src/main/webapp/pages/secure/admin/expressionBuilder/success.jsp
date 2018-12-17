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
<head><%--FIXME: i don't know if this page needs a head and html wrapping tag... but because it had a body...
im assuming this. Please remove this and fix the page back... if the asumption is not correct...--%>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript">

        dojo.addOnLoad(function () {
            manageRowHide("expressionTable", "<s:property value="id"/>");
        });

    </script>
</head>
<%@ include file="successBody.jsp" %>
</html>