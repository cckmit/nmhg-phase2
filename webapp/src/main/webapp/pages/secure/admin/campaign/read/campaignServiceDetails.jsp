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

<div dojoType="dijit.layout.ContentPane">
<div class="policy_section_div">
	<div class="admin_section_heading"><s:text name="label.campaign.serviceProcedure"/></div>
	<table class="grid borderForTable" width="100%" cellspacing="0" cellpadding="0" align="center">
		<tr class="row_head"> 
			<th><s:text name="label.campaign.jobCode"/></th>
			<th><s:text name="label.campaign.laborStandards"/></th>
			<th><s:text name="label.campaign.specifiedLaborHours"/></th>
		</tr>
		<s:iterator value="campaign.campaignServiceDetail.campaignLaborLimits">
		<tr>
			<td><s:property value="serviceProcedureDefinition.code"/></td>
			<td><s:property value="laborStandardsUsed"/></td>
			<td class="numeric"><s:property value="specifiedLaborHours"/></td>
		</tr>
		</s:iterator>
	</table>
</div>
</div>
	