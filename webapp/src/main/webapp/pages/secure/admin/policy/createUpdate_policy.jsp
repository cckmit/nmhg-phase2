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
<%@ taglib prefix="authz" uri="authz" %>

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
            manageTableRefresh("policyConfigTable");
        });
        </s:if>
        dojo.require("dijit.layout.LayoutContainer");
    </script>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
        <div dojoType="dijit.layout.ContentPane">
	<s:if test="%{policyDefinition.isInActive()}">
		<u:actionResults />
	</s:if>
	<s:else>
		<u:actionResults/>
	</s:else>
	
	<s:if test="policyDefinitionId==null">
		<s:form action="create_policy" method="POST" name="createPolicy" validate="true" id="baseForm">
			<jsp:include flush="true" page="policy_details.jsp" />
			<jsp:include flush="true" page="applicability_terms.jsp" />
			<jsp:include flush="true" page="policy_registrationTerms.jsp" />
			<div align="center">
				<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
						onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
				<input type="submit" value="<s:text name="button.common.save"/>" class="buttonGeneric" id="submitCreatePolicy"/>
			</div>
		</s:form>
	</s:if>
	<s:else>
		<s:form action="update_policy.action" method="POST" name="updatePolicy" validate="true" id="baseForm">
			<jsp:include flush="true" page="policy_details.jsp" />
			<jsp:include flush="true" page="applicability_terms.jsp" />
			<jsp:include flush="true" page="policy_registrationTerms.jsp" />
			<jsp:include flush="true" page="policyDefinitionAudits.jsp" />
			<div align="center">
				<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
					onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
				<s:if test="policyDefinition.currentlyInactive">
					<s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.activate')}" action="policy_activate"/>
				</s:if>
				<s:else>
				    <s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.deActivate')}" action="policy_deactivate"/>
				</s:else>
				<input type="submit" value="<s:text name="button.common.save"/>" id="submitUpdatePolicy" class="buttonGeneric"/>
			</div>
		</s:form>	
		
	</s:else>
	
			<authz:ifPermitted resource="warrantyAdminPolicyDefinitionReadOnlyView">
				<script type="text/javascript">
				    dojo.addOnLoad(function() {
				        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
				            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
				        }
				    });
				</script>
			</authz:ifPermitted>
	
    </div>
</div>
</u:body>
</html>