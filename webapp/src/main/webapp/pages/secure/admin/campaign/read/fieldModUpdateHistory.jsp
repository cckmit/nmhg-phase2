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
<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="t" uri="twms" %>
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="scripts/admin.js"></script>
    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<s:head theme="twms"/>
<u:stylePicker fileName="base.css"/>
 <u:body>

 <div class="admin_section_div" style="width:100%">	
	<table style="width:100%;" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
		<tbody>
		    <tr class="admin_section_heading">
            <th><s:text name="label.manageCampaign.FieldModAuditDetails"/></th>
            <th></th> 
            <th></th>
            <th></th>
            <th></th>
            </tr>
			<tr class="row_head">
				<th class="colHeader non_editable"><s:text name="label.common.user"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.lastModified"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.actionperformed"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.reasonForUpdate"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.comments"/></th>
			</tr>
			<s:iterator value="campaignNotification.fieldModUpdateAudit">
			<tr>
				<TD><s:property value="d.lastUpdatedBy.completeNameAndLogin"/></TD>
				<TD><s:property value="d.createdOn"/></TD>
				<TD><s:property value="actionTaken"/></TD>
				<TD><s:property value="inactiveReason"/></TD>						
				<TD><s:property value="comments"/></TD>				
			</tr>
			</s:iterator>
		</tbody>
		</table>
		</div>
		
</u:body>
</html>
