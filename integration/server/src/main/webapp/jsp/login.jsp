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
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
		<title>Integration Server :: Home</title>
	</head>

	<body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="center" style="vertical-align: central;padding-top: 100 ">
            <form  action="<s:url action="Home!authenticateUser"/>" method="post" id="loginForm">
          <div class="alert" id="alerts">
            <div align="center"> <s:actionerror/> </div>
          </div>
          <div></div>
          <div>
				<div class="txtLabels">User Name : <input class="txtinput" id="userName" name="login" type="text" accesskey="U"  /></div>
          </div>
          <div class="txtLabels2">Password&nbsp;&nbsp;&nbsp; :             <input class="txtinput" id="password" name="password" type="password" accesskey="P"/></div>
          <div>
            <input type="image" src="image/loginBtn.jpg" id="logMeInButton" border="0" class="button" alt="Login" title="Login"/>
          </div>
		  </form>
		</div>		  
		  <s:include value="copyright_footer.jsp"></s:include>
	</body>
</html>
