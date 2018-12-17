<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
   <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <s:head theme="twms"/>
 <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>
<u:actionResults/>
<authz:ifUserNotInRole roles="dealer">
<jsp:include flush="true" page="new_warranty_edit.jsp"/>
</authz:ifUserNotInRole>	
<authz:ifUserInRole roles="dealer">
 <jsp:include flush="true" page="edit_warranty_attachment.jsp"/>
</authz:ifUserInRole>
</u:body>
</html>