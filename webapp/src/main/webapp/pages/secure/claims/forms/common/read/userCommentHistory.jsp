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
	position:left;
	background-color:#CCD9FF;
	border:1px solid gray;
	font-family:Verdana, sans-serif, Arial, Helvetica;
	max-width:450px;
	font-size: 10px;
	display: block;
	}
</style>
<table class="grid borderForTable" cellspacing="0" align="center" cellpadding="0" style="width:95%">
	<thead> 
		<tr class="row_head"> 		
			<th ><s:text name="label.common.date" /></th>
			<th ><s:text name="label.claim.claimState"/></th>
			<th ><s:text name="label.common.user"/></th>
			<s:if test="loggedInUserAnInternalUser">
				<th><s:text name="label.common.userType"/></th>
			</s:if>
			<th><s:text name="label.common.externalComments"/></th>
			<s:if test="loggedInUserAnInternalUser">
				   <th><s:text name="label.newClaim.internalComments"/></th>				   
				   <th><s:text name="lable.common.decision"/></th>				   
			</s:if>
		</tr>
	</thead>
	<tbody>
	    <s:set name="repliesSet" value="true" />
		<s:iterator value="claimAudits" status="status">
		   <s:if test = "(loggedInUserAnInternalUser || (!loggedInUserAnInternalUser && (isCommentViewableByDealer(previousState) || isAllAuditsHistoryShownToDealer())))">
				<tr>				 
					<td nowrap="true">
                        <s:if test='%{claimAudit.id!=null && claimAudit.id!=""}'>
                         <s:set name="dateFormat" value="@tavant.twms.dateutil.TWMSDateFormatUtil@getDateFormatForLoggedInUser()"/>
                         <s:date name="updatedTime" format="%{dateFormat}" />
                        </s:if>
                        <s:else>
                        <u:openTab decendentOf="history" id="history_version_%{id}"
                                    tabLabel="Claim Number %{claimNumber} History" url="claim_version_history_detail.action?id=%{id}&showClaimAudit=true">

                                <s:set name="dateFormat" value="@tavant.twms.dateutil.TWMSDateFormatUtil@getDateFormatForLoggedInUser()"/>
                                <s:date name="updatedTime" format="%{dateFormat}" />
                            </u:openTab>
                        
                        </s:else>
                    </td>
                    <td ><s:property value="previousState.state"/>
                    <s:if test="#repliesSet && task.taskName.equals(@tavant.twms.jbpm.WorkflowConstants@FORWARDED) && previousState.state.equals(@tavant.twms.domain.claim.ClaimState@FORWARDED.getState())">
                       <s:if test="!updatedBy.name.equals('system')">
                           <s:hidden name="task.repliesTo" value="%{updatedBy.name}" />
                           <s:set name="repliesSet" value="false" />
                       </s:if>
                    </s:if>
                    </td>
					<td>
						<s:if test="!loggedInUserAnInternalUser">
							<s:if test="updatedBy.name.equals('system')">
								 <s:property value="updatedBy.CompleteNameAndLogin"/>
							</s:if>
							<s:else>
								<s:property value="updatedBy.CompleteNameAndLogin"/> 
							</s:else>
						</s:if>
						<s:else>
								<s:property value="updatedBy.CompleteNameAndLogin"/>
						</s:else>
					</td>
					
					<s:if test="loggedInUserAnInternalUser">
						<td>
							<s:if test="updatedBy.name.equals('system')">
								<s:text name="label.common.userTypeSystem"/>
							</s:if>
							<s:elseif test="updatedBy.isInternalUser()">
								<s:text name="label.common.userTypeInternal"/>
							</s:elseif>
							<s:elseif test="updatedBy != null">
								<s:text name="columnTitle.common.dealer"/>
							</s:elseif>
						</td>
					</s:if>	
					
					
					<td>						
							<s:property value="externalComments"/>

					</td>
					<s:if test="loggedInUserAnInternalUser">
						<td>
								<s:property value="internalComments"/>							
						</td>
						<td>
							    <s:property value="decision"/>
						</td>
					</s:if>				
				</tr>
			  </s:if>
		</s:iterator>
	</tbody>
</table>