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

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="ruleSearchWizard.css"/>

    <script type="text/javascript" src="scripts/RuleSearch.js"></script>
    <script type="text/javascript">
        function validate(obj) {
        }
        <s:if test="!actionMessages.isEmpty()">
        dojo.addOnLoad(function() {
            manageTableRefresh("seriesRefToCertification");
        });
        </s:if>
        dojo.require("dijit.layout.LayoutContainer");
    </script>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
        <div dojoType="dijit.layout.ContentPane">
		
		<s:form name="baseForm" id="baseFormId" theme="twms" validate="true" action="save_series_Certificates" method="post" validate="true">
		<u:actionResults/>
			 <jsp:include flush="true" page="series_certification_details.jsp" /> 
			<div align="center">
				<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
						onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
				<input type="submit" value="<s:text name="button.common.save"/>" class="buttonGeneric" id="submitSeriesCertification"/>
			</div>
		</s:form>
    </div>
</div>
</u:body>
</html>