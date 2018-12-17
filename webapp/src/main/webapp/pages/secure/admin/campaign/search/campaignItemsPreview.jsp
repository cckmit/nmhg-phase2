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
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"></s:head>
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<u:body>
<u:actionResults/>
<s:form action="saveCampaignItemChanges.action">
<s:hidden name="campaignNotification"/>
<div dojoType="dijit.layout.LayoutContainer"
  style="width: 100%; height: 99%; margin: 0; padding: 0; overflow: hidden;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
			<div class="admin_section_div" style="width: 99%">
	<div class="admin_section_heading"><s:text name="label.campaign.campaignItemDetails"/></div>
	<table width="100%" class="admin_selections">
		<tr>
			<td width="20%" class="admin_data_table"><s:text name="label.common.dealerName" />:</td>
			<td width="20%"><s:property value="campaignNotification.dealership.name"/></td>
			<td width="20%" class="admin_data_table"><s:text name="label.common.serialNumber" />:</td>
			<td width="40%"><s:property value="campaignNotification.item.serialNumber"/></td>
		</tr>
		<tr>
			<td class="admin_data_table"><s:text name="label.common.make" />:</td>
			<td><s:property value="campaignNotification.item.ofType.make"/></td>
			<td class="admin_data_table"><s:text name="label.common.model" />:</td>
			<td><s:property value="campaignNotification.item.ofType.model"/></td>
		</tr>
		<tr>
			<td class="admin_data_table"><s:text name="label.common.status" />:</td>
			<td>
				<s:if test="campaignNotification.notificationStatus == 'COMPLETE'">
				<s:select size="100" list="statuses" theme="twms" listKey="code" listValue="description" 
           						emptyOption="true" name="campaignNotification.notificationStatus" disabled="true"/>
          		</s:if>
           		<s:else>
           			<s:select size="100" list="statuses" theme="twms" listKey="code" listValue="description" 
           						emptyOption="true" name="campaignNotification.notificationStatus"/>
           		</s:else>				
			</td>
			<td colspan="2"/>
		</tr>
	</table>
</div>	
<s:if test="campaignNotification.notificationStatus != 'COMPLETE'">
	<div align="center">
		<s:submit value="%{getText('button.common.save')}" cssClass="buttonGeneric"/>
	</div>
</s:if>
</div>
</div>
</s:form>
</u:body>
</html>