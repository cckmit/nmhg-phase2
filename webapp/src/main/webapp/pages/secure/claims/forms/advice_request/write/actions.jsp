
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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="authz" uri="authz" %>
<table class="grid">
	<tr>
		<td width="25%" nowrap="nowrap" class="labelStyle">
			<s:text name="title.attributes.claimState" />:
		</td>
		<td width="75%" class="labelNormalTop">
			<s:property value="claimDetail.state.state"/>						
		</td>
	</tr>
	<s:iterator value="reasonsList" status="rowCounter">
		<tr>
			<s:if test="#rowCounter.count==1">
				<td width="25%" nowrap="nowrap" class="labelStyle">
					<s:text name='%{claimState}'></s:text>:
				</td>
			</s:if>
			<s:else>
				<td width="25%" nowrap="nowrap" class="labelStyle">
					&nbsp;
				</td>
			</s:else>				
			<td width="75%" class="labelNormalTop">
				<s:property/>				
			</td>
		</tr>			
	</s:iterator>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelStyle">
			<s:text name="label.newClaim.processorComments"/>:
		</td>
		<td width="75%" class="label">
			<s:hidden name="commentsForDisplay" value="%{commentsForDisplay}"/>
			<s:property value="commentsForDisplay"/>
		</td>
	</tr>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelNormalTop labelStyle">
			<s:text name="label.common.comments"/>:
		</td>
		<td width="75%" class="labelNormalTop">
			<t:textarea name="task.claim.internalComment" rows="4" cssStyle="width:70%" value=""></t:textarea>
			<s:hidden name="task.claim.externalComment" value="" />
		</td>
	</tr>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelNormalTop labelStyle">
			<s:text name="lable.common.decision"/>:
		</td>
		<td width="75%" class="labelNormalTop">
			<input name="task.claim.decision" type="radio" checked="true" value ="Recommended to Approve" cssStyle="width:70%" />
			<s:text name="lable.common.advice.approve"/>
		</td>
	</tr>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelNormalTop labelStyle">
				
		</td>
		<td>		
			<input name="task.claim.decision" type="radio" value ="Recommended to Reject" cssStyle="width:70%" />
			<s:text name="lable.common.advice.reject"/>
			<s:hidden name="task.claim.decision" value="" />
		</td>				
	</tr>
</table>
<s:hidden name="mandatedComments[0]" value="internalComments"/>
<div id="separator"></div>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<div class="buttonWrapperPrimary">
	<s:hidden name="task.takenTransition" id="transitionField"/>
	<s:if test="task.claim.type.type == 'Campaign'">
    <s:iterator value="task.transitions" status="status">
     	<s:submit id="campaign_submit_%{#status.index}" value="%{top}" type="button" action="campaign_claim_submit"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("campaign_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
			</script>
		</s:iterator>
    </s:if>
	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
		<s:iterator value="task.transitions" status="status">
			<s:submit id="normal_submit_%{#status.index}" value="%{top}" type="button"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("normal_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
			</script>
		</s:iterator>
	</s:elseif>
	<s:else>
		<s:iterator value="task.transitions" status="status">
			<s:submit id="part_submit_%{#status.index}" value="%{top}" type="button" action="parts_claim_submit"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("part_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
			</script>
		</s:iterator>
	</s:else>

	<authz:ifUserInRole roles="processor">
				<s:if test="!task.claim.isPendingRecovery() && displayInitiateRecoveryButton()">
					<input class="buttonGeneric" type="button" id="manualSupplierRecovery" value="<s:text name="button.supplierRecovery.manualInitiation" />" onclick="initiateSupplierRecovery()" />
			   </s:if>
	</authz:ifUserInRole>

	<script type="text/javascript">
	 function initiateSupplierRecovery(){
		 var supplierRecoveryButton = dojo.byId("manualSupplierRecovery");
		var claim = '<s:property value="task.claim"/>';
		var thisTabLabel = getMyTabLabel();
       	parent.publishEvent("/tab/open", {
			label: "Manual Supplier Recovery Initiation",
		    url: "move_claim_to_pending_recovery_initiation.action?claim="+claim, 
		    decendentOf: thisTabLabel,
		    forceNewTab: true
		});
	 }

	</script>
	</authz:ifNotPermitted>