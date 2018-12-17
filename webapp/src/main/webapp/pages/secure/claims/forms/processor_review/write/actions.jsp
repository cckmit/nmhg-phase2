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
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="authz" uri="authz" %>
<script type="text/javascript">
    dojo.addOnLoad(function(){
       dojo.connect(dojo.byId("refreshActionList"),"onclick",function(event){
    	   refreshDecisionSection(event)
       });
    });
    dojo.addOnLoad(function(){
        dojo.subscribe("/refresh/paymentSection",null,refreshDecisionSection);
     });
    function refreshDecisionSection(event) {
    	var form = document.getElementById("claim_form");
        var calculationIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/><s:text name="label.common.refreshing"/></center>";
            dojo.stopEvent(event);
             var actionsSection = dijit.byId("actionList");
             var content = {};
        form.action="refresh_actions.action";
             dojo.xhrPost({
                 form: form,
                 content: content,
                 load: function(data) {
                 	//alert(data);
             		actionsSection.setContent(data);
                     form.action="claim_submit.action";
                 },
                 error: function(error) {
                 }
             });
             actionsSection.setContent(calculationIndicator);
    }
</script>

<table width="96%" class="grid">
    <tr>
        <td colspan="2" class="labelStyle">
            <s:text name="label.newClaim.internalProcessingNotes"/> :
        </td>
        <td width="75%" class="labelNormalTop">
        	 <t:textarea rows="3" cols="80" name="task.claim.internalComment" id="internalCom"
                wrap="physical" cssClass="bodyText" value=""/>
        <s:if test="task!=null">
					<s:hidden name="task.claim.decision" value="" />
		</s:if>
		<s:else>
					<s:hidden name="claim.decision" value="" />
		</s:else>
        </td>
        
    </tr>
    <tr>
        <td colspan="2" class="labelStyle">
            <s:text name="label.newClaim.commentsToDealer"/>:
        </td>
        <td width="75%" class="labelNormalTop">
        	<t:textarea rows="4" cols="80" name="task.claim.externalComment" id="externalCom"
                wrap="physical" cssClass="bodyText" value=""/>
        </td>
       
    </tr>
    <tr>
        <td colspan="2" class="labelStyle">
            <s:text name="label.shippingCommentsToDealer"/>:
        </td>
        <td width="75%" class="labelNormalTop">
        	<s:textarea rows="4" cols="80" name="task.claim.partReturnCommentsToDealer" id="shippingCommentsToDealer"
                wrap="physical" cssClass="bodyText" value="" disabled = "true"/>
        </td>
    </tr>
    <tr>
        <td colspan="1" class="labelStyle">
            <s:text name="label.newClaim.decision"/>
        </td>
        <td colspan="2" class="labelStyle">
        	<p style="text-indent: 20px; margin-left: 10px; margin-top: 10px; margin-bottom: 20px; padding-top: 3px; font-weight: bold; background: url('image/warningsImg.gif') no-repeat;">
        	<a href="#" id="refreshActionList"><s:text name="label.claimprocessor.actions.refreshLink"/></a>
        </td>
    </tr>
</table>

<div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true"  id="actionList">
	<jsp:include flush="true" page="action_list.jsp"/>
</div>

<s:hidden name="mandatedComments[0]" value="internalComments"/>

<s:if test="task.takenTransition=='Forward to Dealer'">
	<s:hidden name="mandatedComments[1]" value="externalComments"/>
</s:if>
<jsp:include flush="true" page="../../common/write/validations.jsp"/>
<table align="center" class="buttons">
    <tr>
        <td align="center" >
		 <s:if test="task.claim.reopened == true && task.claim.type.type == 'Campaign'" >
	     	<s:submit value="%{getText('button.common.validate')}" type="input" action="campaign_claim_reopen_validate" id="validateButton"/>
	     </s:if>
         <s:elseif test="task.claim.reopened == true && (!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))) ">       
	       	<s:submit value="%{getText('button.common.validate')}" type="input" action="claim_reopen_validate" id="validateButton"/>
	     </s:elseif>	
	     <s:elseif test="task.claim.reopened == true">
	     	<s:submit value="%{getText('button.common.validate')}" type="input" action="parts_claim_reopen_validate" id="validateButton"/>
	     </s:elseif>
         <s:elseif test="task.claim.type.type == 'Campaign'">
	       	<s:submit value="%{getText('button.common.validate')}" type="input" action="campaign_processor_claim_validate" id="validateButton"/>
	     </s:elseif>
		 <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
            <s:submit value="%{getText('button.common.validate')}" type="input" action="processor_claim_validate" id="validateButton"/>
          </s:elseif>
          <s:else>
          	<s:submit value="%{getText('button.common.validate')}" type="input" action="parts_processor_claim_validate" id="validateButton"/>
          </s:else>
          <authz:ifUserInRole roles="processor">
				<s:if test="!task.claim.isPendingRecovery() && displayInitiateRecoveryButton()">
					<input type="button" id="manualSupplierRecovery"
						value="<s:text name='button.supplierRecovery.manualInitiation'/>"
						onclick="initiateSupplierRecovery()" />
				</s:if>
		  </authz:ifUserInRole>
		</td>
    </tr>
</table>

<script type = "text/javascript">
function initiateSupplierRecovery() {
    var form = document.getElementById("claim_form");
    
    form.action = "move_claim_to_pending_recovery_initiation.action?claim=<s:property value='task.claim'/>";
    form.submit();
};
</script>
