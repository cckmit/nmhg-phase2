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
			<th width="50%"><s:text name="columnTitle.common.recClaimNo"/></th>
			<th width="50%"><s:text name="label.newClaim.previousState"/></th>			
		</tr>
	</thead>
	<tbody>
	<s:iterator value="previousClaimsForItem" status="status">
	<s:iterator value="recoveryClaims" status="rcstatus">
	
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
			</tr>
		</s:iterator>
		</s:iterator>
	</tbody>
</table>