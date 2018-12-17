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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<div id="claimForwardedInternally">
<s:if test="editableForDealer && isAllRecoveryClaimsClosed()">
    <jsp:include flush="true" page="write_detail.jsp"/>
</s:if>
<s:else>
    <jsp:include flush="true" page="read_detail.jsp"/>
</s:else>
</div>
<authz:ifPermitted resource="claimsForwardedInternallyReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('claimForwardedInternally')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('claimForwardedInternally'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>