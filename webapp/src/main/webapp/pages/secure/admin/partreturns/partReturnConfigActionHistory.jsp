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
<style>
	img#toolTip{cursor:pointer; margin-left:10px;}
</style>

<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>

<s:if test="partReturnDefinition.partReturnDefinitionAudits.size() > 0 ">

<div width="100%">
<div style="margin:10px 0px 0px 0px;" class="mainTitle"><s:text name="title.partReturnConfiguration.actionHistory"></s:text></div>
	<table style="width:100%;" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable"  align="center">
		<thead>
			<tr class="admin_table_header">
				<th class="warColHeader" width="25%"><s:text name="label.partReturnConfiguration.status"/></th>
				<th class="warColHeader" width="25%"><s:text name="label.partReturnConfiguration.name"/></th>
				<th class="warColHeader" width="25%"><s:text name="label.partReturnConfiguration.comments"/></th>
				<th class="warColHeader" width="25%"><s:text name="label.partReturnConfiguration.actionDate"/></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="actionHistory" status="audit">
				<tr>
					<td><s:property value="status"/></td>
					<td><s:property value="d.lastUpdatedBy.completeNameAndLogin"/></td>
					<td><s:property value="comments"/></td>
					<td><s:property value="d.createdOn"/></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>
<div></div>
</s:if>