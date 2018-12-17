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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="t" uri="twms" %>
<style>
.linkColor{
color:blue;
cursor:pointer;
text-decoration:underline;
}
td{
padding-bottom:10px;
}
</style>
	<table style="width:97%;" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
		<tbody>
		 <tr class="admin_section_heading">
            <th><s:text name="label.manageCampaign.campaignAuditDetails"/></th>
            <th></th> 
            <th></th>
            <th></th>
            </tr>
			<tr class="row_head">
				<th class="colHeader non_editable"><s:text name="label.common.user"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.lastModified"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.actionperformed"/></th>
				<th class="colHeader non_editable"><s:text name="label.common.comments"/></th>
			</tr>
			<s:iterator value="campaign.campaignAudits">
			<tr>
				<TD><s:property value="d.lastUpdatedBy.completeNameAndLogin"/></TD>
				<TD><s:property value="d.createdOn"/></TD>
				<TD><s:property value="actionTaken"/></TD>					
				<TD><s:property value="comments"/></TD>				
			</tr>
			</s:iterator>
		</tbody>
		</table>
