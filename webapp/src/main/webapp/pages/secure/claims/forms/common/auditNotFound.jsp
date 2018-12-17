<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
        <u:stylePicker fileName="yui/reset.css" common="true"/>
        <s:head theme="twms"/>
        <u:stylePicker fileName="common.css"/>
        <u:stylePicker fileName="form.css"/>
        <u:stylePicker fileName="base.css" />    
    </head>
    <body>
        <u:actionResults/>
    </body>
</html>
