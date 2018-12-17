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
<%@taglib prefix="s" uri="/struts-tags"%>


<table class="grid" cellspacing="0" cellpadding="0">
	<tr>
		<td class="labelStyle" nowrap="nowrap" width="16%"><s:text name="label.newClaim.conditionFound"/>:</td>
		<td><s:property value="claim.conditionFound"/></td>
		<td class="labelStyle" nowrap="nowrap" width="16%"><s:text name="label.newClaim.workPerformed"/>:</td> 
		<td><s:property value="claim.workPerformed"/></td>		
	</tr>
	<tr>
		<td class="labelStyle" nowrap="nowrap" width="16%"><s:text name="label.newClaim.additionalDetails"/>:</td> 
		<td ><s:property value="claim.otherComments"/></td>
	</tr>
	<tr>
		<%--  <td class="labelStyle" nowrap="nowrap" width="16%"><s:text name="label.newClaim.probableCause"/>:</td> 
		<td ><s:property value="claim.probableCause"/></td> --%>
	</tr>
</table>
