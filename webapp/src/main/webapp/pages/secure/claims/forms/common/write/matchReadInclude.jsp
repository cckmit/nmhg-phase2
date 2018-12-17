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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<table id="match_read_include"  width="95%" class="grid">
   <tbody>
	<tr>
	<td>	
	<s:if test="task.claim.id!=null && 
				 (task.claim.filedBy.id==loggedInUser.id ||
				 task.claim.forDealer.id==loggedInUser.belongsToOrganization.id)">	
		<table>
			<tbody>
				<tr>			
					<td id = "viewLink" align="left">						
							<a id="show_matchRead_page" class="link" style="display:none">
									<s:text name="link.newClaim.PleaseEnterOwnerInformation"/>
							</a>
							<a id="show_owner_page" class="link" style="display:none">
									<s:text name="link.newClaim.Modifyownerinformation"/>
							</a>												
					</td>
				</tr>
			</tbody>
		</table>
	</s:if>	
	<s:else>
		<table>
			<tbody>
				<tr>
					<s:if test="task.claim.matchReadInfo!=null && task.claim.matchReadInfo.ownerName!=null">
						<td >
							<s:text name="label.warrantyAdmin.ownerName"/>:
						</td>
						<td id = "viewLink" >
							<a id="show_matchRead_score_page" class="link">				
				          		<s:property value="task.claim.matchReadInfo.ownerName"/>  		
							</a>						
						</td>
					</s:if>
				</tr>
			</tbody>
		</table>
	</s:else>
	</td>
	</tr>
   </tbody>  
</table>
<s:if test="task.claim.id!=null && 
				 (task.claim.filedBy.id==loggedInUser.id ||
				 task.claim.forDealer.id==loggedInUser.belongsToOrganization.id)">
    <jsp:include flush="true" page="matchReadOwnerInfo.jsp" />  
</s:if>
<s:elseif test="!task.claim.state.state.equalsIgnoreCase('Draft') && task.claim.matchReadInfo!=null 
				&& task.claim.matchReadInfo.ownerName!=null">
    <jsp:include flush="true" page="../read/matchReadScore.jsp" />
</s:elseif> 