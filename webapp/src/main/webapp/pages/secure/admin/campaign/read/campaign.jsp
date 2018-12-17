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
       <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <script type="text/javascript" src="scripts/admin.js"></script>
</head>
<u:body>
<s:actionmessage />
<s:push value="campaign">
<div dojoType="dijit.layout.LayoutContainer" id="rootLayoutContainer"
  style="width: 100%; height: 99%; margin: 0; padding: 0; overflow: hidden;">
	<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
	
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.campaign.info"/>" style="overflow: hidden;">
			<jsp:include page="campaignInfo.jsp" flush="true" />
		</div>
		
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.campaign.criteria"/>" style="overflow: hidden;">
				<jsp:include page="campaignFilterData.jsp" flush="true" />
		</div>
		<div style="margin:5px;">	
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.campaign.material"/>" style="overflow: hidden;">
			<jsp:include page="campaignParts.jsp" flush="true" />
			<jsp:include page="miscpartreplaced.jsp" flush="true" />
			</div>
		</div>
		
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.campaign.service"/>" style="overflow: hidden;">
			<jsp:include page="campaignServiceDetails.jsp" flush="true" />
		</div>
		
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.campaign.incidentals"/>" style="overflow: hidden;">
			<jsp:include page="campaignIncidentals.jsp" flush="true" />
		</div>

		<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.supplier.recoveryInfo"/>" style="overflow: hidden;">
			<jsp:include page="supplierRecoveryData.jsp" flush="true" />
		</div>
		
	</div>
</div>
</s:push>
</u:body>
</html>