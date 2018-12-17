<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
		<title>Integration Server :: Home</title>
	</head>

	<body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="left"><a href='<s:url value="/jsp/index.jsp"/>' style="color: navy;"> Home </a></div>
        <div align="right" style="padding-right: 25px"><a href='<s:url action="Home!logout"/>' style="color: navy;"> Logout </a></div>
		<hr>
		<a href='<s:url action="FileTracker"/>'>File Tracker</a>
		<br>
		<a href='<s:url action="SyncTracker"/>'>Sync Tracker</a>
		<hr>
		<a href='<s:url value="/quartzadmin/index.jsp"/>'>Quartz Administration</a>
		<hr>
		<s:include value="copyright_footer.jsp"></s:include>
	</body>
</html>
	