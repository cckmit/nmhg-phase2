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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<style>
.borderForTable tr td{
border:none !important;
}
</style>
<table cellspacing="0" cellpadding="0" class="grid borderForTable" width="100%">
  <thead>
    <tr class="row_head">
      <th><s:text name="label.warrantyAdmin.campaignCode"/></th>
      <th><s:text name="label.warrantyAdmin.class"/></th>
      <th><s:text name="label.common.endDate"/></th>
      <th><s:text name="label.common.description"/></th>
      <th><s:text name="label.common.status"/></th>
    </tr>
  </thead>
  <tbody>
   <s:iterator value="campaignNotifications" status="status">
 	  <s:if test="#status.even">
        <tr class="invTableDataWhiteBg">
      </s:if>
      <s:else>
        <tr class="invTableDataAltRow">
      </s:else>
			<td><s:property value="campaign.code"/> </td>
	        <td><s:property value="campaign.campaignClass.description" /> </td>
	        <td><s:property value="campaign.tillDate" /> </td>
	        <td><s:property value="campaign.description" /> </td>
	        <td><s:property value="notificationStatus" /> </td>
     	</tr>
   </s:iterator>
  </tbody>
</table>