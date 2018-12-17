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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html>

<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
<s:head theme="twms" />

<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
	type="text/css">
<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.form.Button");
        dojo.require("dojox.layout.ContentPane");
        dojo.require("twms.widget.Dialog");
    </script>
<%@ include file="/i18N_javascript_vars.jsp"%>
<u:stylePicker fileName="inboxLikeButton.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="base.css" />
<title><s:text name="FIXME" /></title>
</head>
<u:body>
<u:actionResults/>
	<s:hidden name="selectedBusinessUnit"
		value="%{inventoryItem.businessUnitInfo.name}" />
		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.componentInfo" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="majorcomponent_equipmentinfo.jsp" /></div>
		</div>
		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.claimHistory" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_warrantyclaimhistroy.jsp" /></div>
		</div>
		<div>
	</u:body>
</html>
