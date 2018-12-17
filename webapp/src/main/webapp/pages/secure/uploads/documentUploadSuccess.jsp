<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: 15 May, 2008
  Time: 11:40:34 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <s:head theme="twms"/>
    <title>
        Upload Successful!
    </title>
</head>
<body class="official">
    <!--
        Used by the dojo.ioframe.send(...) call in twms.widget.FileUploader widget.
        IMPORTANT: Do *not* use s:textarea, since that doesn't seem to work well with the expectations of ..send()
     -->
    <textarea style="display:none" rows="1" cols="1">
        <s:property value="documentDetailsJSON" />
    </textarea>
</body>
</html>