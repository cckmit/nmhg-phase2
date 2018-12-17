<%@taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>:: TAVANT ::</title>
<link href="css/inv/detailDesign.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript"
	src="scripts/ui-ext/common/inv/dateStr.js"></script>
</head>
<body scroll="yes">
<s:form action="doUploadPartsInventory" method="post"
	enctype="multipart/form-data">

	<div style="width: 100%; overflow-X: none; overflow-Y: auto;">
	<div id="separator"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="header">
		<tr>
			<td width="1%">&nbsp;</td>
			<td width="77%" class="header"><script language="JavaScript"
				type="text/javascript">
				document.write(dateStr);
		</script></td>
			<td width="22%" class="header">Welcome Dealer</td>
		</tr>
	</table>
	<div style="height: 7px"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="1%">&nbsp;</td>
			<td class="ItemsHdr" colspan="3">Upload Parts Inventory</td>
		</tr>
		<tr>
			<td width="1%">&nbsp;</td>
			<td class="labelBoldTop" colspan="3" valign="top">Select file to
			upload:<s:file name="upload" cssClass="buttonGo" /> <!-- <input type="button" class="buttonGo" name="Upload" value="Upload"> -->
			<s:submit value="Upload" cssClass="buttonGo" /></td>
		</tr>
		<tr>
			<td width="1%" colspan="4">&nbsp;</td>

		</tr>
		<tr>
			<td width="1%">&nbsp;</td>
			<td colspan="3"><s:url id="url" action="downloadExcelFile"
				includeParams="none" /> <s:a href="%{url}">
				<s:text
					name="Click here to download parts inventory upload template." />
			</s:a></td>
		</tr>
		<tr>
			<td width="1%" colspan="4">&nbsp;</td>

		</tr>
		<tr>
			<td width="1%">&nbsp;</td>
			<td class="ItemsHdr" colspan="3">Upload History</td>
		</tr>

		<tr>
			<td width="1%">&nbsp;</td>
			<td colspan="3" id="tab_bg">
			<Table width="100%">
				<tr>
					<td id="tabletextNew">File Name</td>
					<td id="tabletextNew">Date of Upload</td>
					<td id="tabletextNew">Total Records</td>
					<td id="tabletextNew">Records Failed</td>
					<td id="tabletextNew">Error Report</td>
				</tr>
				<s:iterator value="documentList" status="status">
					<tr>
						<td id="tabletext"><s:property
							value="documentList[#status.index].fileName" /></td>
						<td id="tabletext"><s:property
							value="documentList[#status.index].d.createdOn" /></td>
						<td id="tabletext"><s:property
							value="documentList[#status.index].uploadHistory[0].numberOfSuccessfulUploads" /></td>
						<td id="tabletext"><s:property
							value="documentList[#status.index].uploadHistory[0].numberOfErrorUploads" /></td>
						<td class="alert"><s:url id="url"
							action="download_error_xls?partInvUploadHistId=%{documentList[#status.index].uploadHistory[0].id}"
							includeParams="all" /> <s:a href="%{url}">
							<s:text name="Error.xls" />
						</s:a> <s:hidden
							name="%{documentList[#status.index].uploadHistory[0].id}"
							value="%{documentList[#status.index].uploadHistory[0].id}" /></td>
					</tr>
				</s:iterator>
			</Table>
			</td>

		</tr>

	</table>
	</div>
	</div>
	<!-- rightContent Complete container id #content-->
	<!-- footer id #footer-->
	<div id="footer">
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td height="4" bgcolor="#84A3CF" colspan="5"></td>
		</tr>
		<tr>
			<td id=""><img src="image/inv/logoWithBg.gif" alt="tavant"
				width="145" height="35" /></td>
			<td id="spacing">&nbsp;</td>
			<td nowrap="nowrap" id="footerCopyrights">&copy; 2008 Tavant
			Technologies, Inc. All Rights Reserved.</td>
			<td width="2%">&nbsp;</td>
			<td width="5%" id="footerPrivacy">| Privacy Policy</td>
		</tr>
	</table>
	</div>	
	<!-- footer id #footer-->
</s:form>
</body>
</html>
