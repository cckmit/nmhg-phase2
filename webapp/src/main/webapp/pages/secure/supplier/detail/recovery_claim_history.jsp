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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<table class="grid borderForTable" cellpadding="0" cellspacing="0" align="center" style="margin:5px;width:95%" >
<thead>
		<tr class="row_head"> 
			
			<th width="14%"><s:text name="label.newClaim.previousState"/></th>
			<th width="10%"><s:text name="label.common.user"/></th>
			<th width="30%"><s:text name="label.common.externalComments"/></th>
			<authz:ifUserNotInRole roles="supplier"> 
			<th width="30%"><s:text name="label.common.internalComments"/></th>
			</authz:ifUserNotInRole>
			<th width="25%"><s:text name="label.recClaim.acceptRejectReason"/></th>
			<th width="14%"><s:text name="label.common.debitedAmount" /></th>
			<th width="7%"><s:text name="label.common.date" /></th>
			
		</tr>
	</thead>
	<tbody>
		<s:iterator value="recoveryClaim.recoveryClaimAudits" status="status">
			<tr>				
				<td><s:property value="recoveryClaimState.state"/> </td>
				<td>
				<s:if test="isLoggedInUserASupplier() && isBuConfigAMER()">
					<s:property value="displayUserNameForSupplier()"/>
				</s:if>
				<s:else>
					<s:property value="createdBy.completeNameAndLogin"/> 
				</s:else>
				</td>
				<td>
					<s:if test="recoveryClaimState.state!='Auto Disputed Initial Response Period'">
						<s:property value="externalComments"/>
					</s:if>
				</td>
				 <authz:ifUserNotInRole roles="supplier">
				<td>				
					<s:set var = "breakLoop" value = "%{false}" />
					<s:iterator value="createdBy.roles">
						<s:if test="name!='supplier'  && !#breakLoop">
							<s:property value="comments"/> 		
							<s:set var = "breakLoop" value = "%{true}"/>				
						</s:if>						
					</s:iterator>								
				</td>
				</authz:ifUserNotInRole>
				<td>
					<s:if test="recoveryClaim.recoveryClaimAcceptanceReason.description != null && recoveryClaimState.state.equalsIgnoreCase('Ready for Debit')">
						<s:property value="recoveryClaim.recoveryClaimAcceptanceReason.description"/>				
					</s:if>
					<s:elseif test="recoveryClaim.recoveryClaimRejectionReason.description != null  && recoveryClaimState.state.equalsIgnoreCase('Rejected')">
						<s:property value="recoveryClaim.recoveryClaimRejectionReason.description"/>				
					</s:elseif>
					<s:elseif test="recoveryClaim.recoveryClaimCannotRecoverReason.description !=null && recoveryClaimState.state.equalsIgnoreCase('Closed Unrecovered')">
						<s:property value="recoveryClaim.recoveryClaimCannotRecoverReason.description"/>
					</s:elseif>
					<s:elseif test="recoveryClaimState.equals(@tavant.twms.domain.claim.RecoveryClaimState@AUTO_DISPUTED_INITIAL_RESPONSE)">
					     <s:text name="message.recovery.claim.initial.response.period" />
					</s:elseif>
						<s:elseif test="recoveryClaimState.equals(@tavant.twms.domain.claim.RecoveryClaimState@AUTO_DISPUTED_FINAL_RESPONSE)">
					<s:text name="message.recovery.claim.final.response.period" />
					</s:elseif>
				</td>
				<td><s:property value="recoveredAmount"/> </td>
				<td><s:property value="createdOn"/> </td>
			</tr>
		</s:iterator>
	</tbody>
</table>
