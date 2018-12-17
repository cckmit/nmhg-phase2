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
<%@taglib prefix="authz" uri="authz"%>
<%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
<u:body>
<form name="baseForm" id="baseForm">
    <u:actionResults/>

    <s:if test="dealerScheme!=null">
        <s:if test="dealerScheme.id != null">
            <jsp:include flush="true" page="showGroups.jsp"/>
        </s:if>
        <s:else>
            <jsp:include flush="true" page="createScheme.jsp"/>
        </s:else>
    </s:if>
</form>
<authz:ifPermitted resource="warrantyAdminDealerGroupsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>