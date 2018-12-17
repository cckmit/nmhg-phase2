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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
	<head>
		<meta http-equiv="Context-Type"	content="text/html; charset=ISO-8859-1">
		<title><s:text name="title.common.warranty"/></title>
        <s:head theme="twms" />
        <u:stylePicker fileName="preview.css"/>
         <u:stylePicker fileName="common.css"/>
	</head>
	
	<u:body>
		<div class="policy_section_div" >
			
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
				<thead>
					<tr class="row_head">
						<th><s:text name="label.common.sNo"/></th>
						<th><s:text name="label.listOfValues.code"/></th>
						<th><s:text name="label.common.description"/></th>
						<th><s:text name="label.common.status"/></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="lovs" status="myStatus">
						<tr>
							<td><s:property value="#myStatus.count"/></td>
							<td><s:property value="code"/></td>
							<td><s:property value="description"/></td>
							<td><s:property value="state"/></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
		
	</u:body>
</html>

