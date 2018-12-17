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

<div id="processorReviewDetail">
<authz:ifUserInRole roles="processor">
<s:if test="editableForProcessor && isAllRecoveryClaimsClosed()">
    <jsp:include flush="true" page="write_Detail.jsp"/>
</s:if>
	<s:else>
	<s:set var="policyCodeEditable" value="true" />
		<jsp:include flush="true" page="read_detail.jsp" />
	</s:else>
</authz:ifUserInRole>
<authz:ifUserNotInRole roles="processor">
    <jsp:include flush="true" page="write_Detail.jsp"/>
</authz:ifUserNotInRole>
</div>
<authz:ifPermitted resource="claimsAppealsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsInProgressReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsPartShippedNotReceivedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsOnHoldForPartReturnReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsNewReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsRejectedPartReturnReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsRepliesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="claimsTransferredReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('processorReviewDetail'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>