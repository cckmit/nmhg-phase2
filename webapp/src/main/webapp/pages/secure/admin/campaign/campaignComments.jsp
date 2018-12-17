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
	<table style="width:97%;" border="0" cellspacing="0" cellpadding="0" >
		<tbody>
		 <tr class="admin_section_heading">
            <th><s:text name="label.common.campaignComments"/></th>
            <th></th> 
            <th></th>
            <th></th>
            </tr>
			<tr>
				<td width="20%" nowrap="nowrap"><label for="campaign_comments" class="labelStyle"><s:text name="label.common.campaignComments" />:</label></td>
			<td width="25%">
				<s:textarea cols="40" rows="3" name="campaign.comments" id="campaign_comments" value="" cssStyle="margin-top:5px;" />
			</td> 
			</tr>
			
		</tbody>
		</table>
