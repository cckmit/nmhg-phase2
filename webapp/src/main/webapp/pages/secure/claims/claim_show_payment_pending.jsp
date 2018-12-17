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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<s:head theme="twms" />
<title><s:text name="title.viewClaim.confirmPayment"/></title>
</head>
<u:body>
<h2><s:text name="title.viewClaim.manualyClickNote"/></h2>
<br>
<s:iterator value="claimsPending">
	<a href="paymentAsyncActionAcknowledge.action?id=<s:property value="id"/>"><s:text name="label.common.claimNumber"/> : <s:property value="claimNumber" /></a>
	<br />
</s:iterator>

<tr>
<td>
<s:text name="label.recoveryClaim.recoveryClaims"/>
</td>
</tr>

<s:iterator value="supplierRecoveryPendingClaims">
<br>
	<a href="paymentAsyncAcknowledgeSRPmt.action?id=<s:property value="id"/>"><s:text name="label.common.claimNumber"/> : <s:property value="claim.claimNumber" /></a>
	<br />
</s:iterator>


</u:body>
</html>