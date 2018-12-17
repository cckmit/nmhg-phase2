<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
		<title>Integration Server :: Listing Sync Files</title>
		<s:head calendarcss="calendar-blue"/>
	</head>
	<body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="left"><a href='<s:url action="Home"/>' style="color: navy;"> Home </a> &gt; Sync File Page</div>
        <div align="right" style="padding-right: 25px"><a href='<s:url action="Home!logout"/>' style="color: navy;"> Logout </a></div>
		<hr>
		<s:form action="FileTracker!search" method="post" id="searchForm">
			<div align="left">
				Enter search criteria
				<s:actionerror/>
                <s:select label="Sync Type" name="syncType" headerKey="" listKey="type" listValue="type"
					headerValue="Select Sync Type"  
					list='syncTypeOptions'  ></s:select>
				<s:select label="Loading complete?"  name="loadingComplete" headerKey="" 
					headerValue="Select status"  list='#{true : "Yes" , false : "No" }'/>
				<s:datetimepicker name="fromDate" label="Start Date"/>
				<s:datetimepicker name="toDate" label="End date"/>
				<s:submit label="Search" align="left"/>
			</div>
		</s:form>
		<hr>
		
		<div align="left">Displaying list of files:</div>
		<table border="1" cellpadding="0" cellspacing="0">
			<tr>
				<th>Sync type</th>
				<th>File name</th>
				<th>Current record</th>
				<th>Last modified date</th>
				<th>Create date</th>
				<th>Last update date</th>
				<th>Is loading complete</th>
			</tr>
			<s:if test="syncFiles == null or syncFiles.isEmpty()">
				<tr><td colspan="7">No records found</td></tr>
			</s:if>
			<s:else>
				<s:iterator id="id" value="syncFiles" status="status">
					<tr>
						<td><s:property  value="syncType"/></td>
						<td><s:property  value="fileName"/></td>
						<td><s:property  value="currentRecord"/></td>
						<td><s:property  value="fileModifiedDate"/></td>
						<td><s:property  value="createDate"/></td>
						<td><s:property  value="updateDate"/></td>
						<td>
							<s:if test="loadingComplete">Yes</s:if>
							<s:else>No</s:else>
						</td>
					</tr>
				</s:iterator>
			</s:else>
		</table>
		<s:include value="copyright_footer.jsp"></s:include>	
	</body>
</html>