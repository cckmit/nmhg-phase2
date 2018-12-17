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
	<s:head theme="twms"/>
    <%--NOTE : have removed admin.js.... didn't find anything that it is doing anyways...--%>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="view_sections.css"/>
     <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <style type="text/css">
        .tabPane {
            border : 1px solid #EFEBF7;
        }
    </style>
</head>
<u:body>
<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" style="width: 100%; height: 100%;">
    <s:push value="campaignNotification.item">
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="tablabel.item.info"/>" class="tabPane"
             layoutAlign="client" style="overflow-x: hidden; overflow-y: auto;">
            <jsp:include page="itemInfo.jsp" flush="true" />
        </div>
    </s:push>
    <s:push value="campaignNotification.campaign">
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="tablabel.campaign.info"/>" class="tabPane"
             layoutAlign="client" style="overflow-x: hidden; overflow-y: auto;">
            <jsp:include page="/pages/secure/admin/campaign/read/campaignInfo.jsp" flush="true" />
        </div>
    </s:push>
</div>
</u:body>
</html>