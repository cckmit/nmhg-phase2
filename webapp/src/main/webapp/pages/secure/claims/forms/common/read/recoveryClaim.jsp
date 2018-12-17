<%--

   Copyright (c)2006 Tavant   Technologies
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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">
	dojo.require("dijit.Tooltip")
</script>
<style type="text/css">
.dijitTooltipContainer {
	position: left;
	background-color: #CCD9FF;
	border: 1px solid gray;
	font-family: Verdana, sans-serif, Arial, Helvetica;
	max-width: 450px;
	font-size: 10px;
	display: block;
}
</style>
<table class="grid borderForTable" cellspacing="0" align="center"
	cellpadding="0" style="width: 95%">
	<thead>
		<tr class="row_head">
			<th><s:text name="columnTitle.common.recClaimNo" /></th>
			<th><s:text name="label.recoveryClaim.recoveryClaimState" /></th>
			<th><s:text name="label.recoveryClaim.internalCommentsExist" /></th>
			<%-- <th><s:text name="label.claim.assignedTo" /></th> --%>

		</tr>
	</thead>
	<tbody>
		<s:iterator value="claim.recoveryClaims" status="status">
			<tr>
				<td align="center">
				
				 <u:openTab decendentOf="history" id="%{id}"
                                    tabLabel="Recovery Claim Number %{recoveryClaimNumber}" url="recovery_claim_search_detail.action?id=%{id}">
						<s:if test="documentNumber !=null && documentNumber.length() > 0" >
		                 	<s:property value="recoveryClaimNumber" />-<s:property
				        value="documentNumber" />
	                   	</s:if>
		                <s:else>
		                  <s:property value="recoveryClaimNumber" />
		               </s:else>	                          
                  </u:openTab>
				
				</td>
				<td align="center"><s:property value="recoveryClaimState.state" /></td>
			<%-- 	<td align="center"><s:property value="currentAssignee" /></td> --%> 
				<s:if test="isInternalCommentsExists( claim.recoveryClaims[#status.index] )"> 
				<td align="center"><s:text name="label.common.yes" /></td>
				</s:if>
				<s:else>
				<td align="center"><s:text name="label.no" /></td>
				</s:else>
			</tr>
		</s:iterator>
	</tbody>
</table>