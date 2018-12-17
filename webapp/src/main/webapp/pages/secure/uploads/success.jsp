<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="adminPayment.css"/>
<html>
<head>
    <s:head theme="twms"/>
    <title><s:text name="success.uploads.laimUploadSuccessPage"/></title>
</head>
<u:body>
    <div>
        <s:property value="totalClaimsUploaded"/><s:text name="success.uploads.claimsSuccessfullyUploaded"/>
        <br/>
        <br/>

        <p><a href="downloadErrorClaims.action"><s:text name="error.uploads.downloadClaimsWithError"/> </a></p>
    </div>
    <h1></h1>
</u:body>
</html>