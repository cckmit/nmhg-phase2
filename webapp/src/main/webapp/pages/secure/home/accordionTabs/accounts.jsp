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
<%@taglib prefix="authz" uri="authz"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>

<div dojoType="dijit.layout.ContentPane" id="accounts"
    title="<s:text name="accordion_jsp.accordionPane.accounts" />"
    iconClass="accounts">
  <div dojoType="dijit.layout.ContentPane">
  <authz:ifPermitted resource="accountsRemoteInteractionsLogs">
    <ol>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="accounts_settings" tagType="li" cssClass="action_folder folder"
            tabLabel="%{getText('title.accounts.remote_interactions_logs')}"
            url="manage_remote_interaction_logs.action" catagory="accounts" helpCategory="Accounts/Remote_Interactions_Logs.htm">
            <s:text name="accordionLabel.accounts.remote_interactions_logs" />
        </u:openTab>
    </ol>            
  </authz:ifPermitted>
		<authz:ifPermitted resource="accountsMonthEndScheduler">
			<ol>
				<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
					id="scheduler" tagType="li" cssClass="action_folder folder"
					tabLabel="%{getText('title.accounts.monthEndScheduler')}"
					url="month_end_scheduler.action" catagory="accounts"
					helpCategory="Accounts/Remote_Interactions_Logs.htm">
					<s:text name="accordionLabel.accounts.monthEndScheduler" />
				</u:openTab>
			</ol>
		</authz:ifPermitted>
	</div>
</div>