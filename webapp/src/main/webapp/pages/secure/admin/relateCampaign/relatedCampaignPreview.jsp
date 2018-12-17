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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title><s:text name="title.common.warranty" /></title>
<s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css" />
</head>
<u:body>
	<s:form name="baseForm" id="baseFormId" method="post">
		<u:actionResults />
		<s:hidden name="id" id="blahblah" />
		<div style="background: #F3FBFE; border: 1px solid #EFEBF7">
		<div class="policy_section_heading"><s:text
			name="title.relatedCampaign.add" /></div>

		<table border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
					name="columnTitle.common.FullCode" /> :</td>
				<td><s:textfield name="relateCampaign.code" cssStyle="width:100px"
					value="%{relateCampaign.code}" /></td>
			</tr>
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
					name="columnTitle.common.description" /> :</td>
				<td><s:textarea name="relateCampaign.description" cssClass="textarea" cols="40"
					value="%{relateCampaign.description}" /></td>
			</tr>
		</table>
		</div>

		<div class="admin_section_div" style="margin: 5px; width: 99%">


		<div class="policy_section_heading"><s:text
			name="label.campaign.addCampaign" /></div>
		<s:iterator value="%{relateCampaign.includedCampaigns}">
			<tr>
			<td><s:property value="code"/></td>
			<td><s:property value="description"/></td>
			</tr>
		</s:iterator></div>
	</s:form>
</u:body>
</html>
