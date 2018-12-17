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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<s:if test="claim.state.state.equalsIgnoreCase('Accepted and Closed')
        || claim.state.state.equalsIgnoreCase('Denied and Closed')
        || claim.state.state.equalsIgnoreCase('Closed')">
    <s:if test="loggedInUserAnInternalUser">
        <jsp:include flush="true" page="../common/read/complete_payment_detail.jsp"/>
    </s:if>
    <s:else>
        <jsp:include flush="true" page="../common/read/partial_payment_detail.jsp"/>
    </s:else>
</s:if>
<s:else>
    <s:if test="!loggedInUserAnInternalUser">
        <jsp:include flush="true" page="../common/read/dealerClaimPayment.jsp"/>
    </s:if>
    <s:else>
        <jsp:include flush="true" page="../common/read/partial_payment_detail.jsp"/>
    </s:else>
</s:else>
