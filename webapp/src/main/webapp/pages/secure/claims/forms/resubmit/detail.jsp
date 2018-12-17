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


<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="inboxLikeButton.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.smResponseDetail"/></title>
	
	<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
      	dojo.require("twms.widget.TitlePane");
      	dojo.require("twms.widget.Dialog");
      	dojo.require("twms.widget.Select");
    </script>
</head>

<u:body >
	<div dojoType="dijit.layout.LayoutContainer" >
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults/>
	<s:form  theme="twms" validate="true" id="claim_form"
        name="claim_submit" action="claim_resubmit.action">
			<input type="hidden" name="claimId" value="<s:property value='claim.id'/>"/>


		        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
		             labelNodeClass="section_header">
		                 <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
		        </div>
            
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"	
					labelNodeClass="section_header">
						<jsp:include flush="true" page="../common/read/header.jsp"/>
				</div>
		<s:if test="isLoggedInUserAnInternalUser()">
			<s:if test="claim.recoveryClaims.size() > 0">
				<div dojoType="twms.widget.TitlePane"
					title="<s:text name="label.common.recoveryClaimDetails" />"
					labelNodeClass="section_header"><s:push value="claim">
					<jsp:include flush="true" page="../common/read/recoveryClaim.jsp" />
				</s:push></div>
			</s:if>
		</s:if>
			
			 <s:if test="claim.type=='Campaign'">
			    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.warrantyAdmin.campaignCode"/>" id="campaign_header"
			             labelNodeClass="section_header" >
			    	<s:push value="task.claim.campaign">
					<jsp:include page="/pages/secure/admin/campaign/read/campaignDetails.jsp" flush="true" />
					</s:push>
					<s:if test="!getJSONifiedCampaignAttachmentList().equals('[]')">
									<jsp:include page="/pages/secure/admin/campaign/read/campaignAttachments.jsp" flush="true"/>				
			</s:if>	
				</div>
			</s:if>
			
			<s:if test="claimSpecificAttributes != null && ! claimSpecificAttributes.empty">
	 		<div>
	
       			 <div dojoType="twms.widget.TitlePane" title="<s:text name="label.viewClaim.ClaimAttributes"/>" id="attr_details"
             			labelNodeClass="section_header"  open="true">
             			  <s:push value="task">
             	    	<jsp:include flush="true" page="../common/read/claimSpecificAttributes.jsp"/>  
             	    	 </s:push>
                 </div> 
    		</div>  
    	</s:if> 
    	 
                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.servicingLocation"/>"
                    labelNodeClass="section_header">
                        <jsp:include flush="true" page="../common/read/servicingLocation.jsp"/>
                </div>
    <s:if test="task.partsClaim && task.claim.partItemReference.serialized">    
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.PartDetails"/>" class="scrollYNotX">
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
            <s:push value="task">
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
            </s:push>
        </div>
    </div>
    </s:if>
                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
                     labelNodeClass="section_header">
                    <jsp:include flush="true" page="../common/read/equipment.jsp"/>
                </div>
        
            <s:if test="claim.type.type != 'Campaign'">
		    	<jsp:include flush="true" page="../common/read/availablePoliciesPopup.jsp"/>
					<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
							labelNodeClass="section_header">
							<jsp:include flush="true" page="../common/read/failure.jsp"/>
					</div>
			</s:if>
			<s:if test="isAlarmCodesSectionVisible()" >
	  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
		           labelNodeClass="section_header">
                           <jsp:include flush="true" page="../common/read/claimAlarmCodeView.jsp" />
					
		      </div>
		    </s:if>
		    <s:if test="claim.laborConfig">
			<s:if test="partsClaim && (!claim.partInstalled || (claim.partInstalled && claim.competitorModelBrand!=null))">
					<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
						labelNodeClass="section_header">
						<jsp:include flush="true" page="../common/read/labor_detail_part.jsp"/>
					</div>
			</s:if>
			<s:elseif test="!partsClaim || (claim.partInstalled && (claim.competitorModelBrand == null || claim.competitorModelBrand.isEmpty()))">
					<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
						labelNodeClass="section_header">
							<jsp:include flush="true" page="../common/read/service_detail.jsp"/>
					</div>
			</s:elseif>		</s:if>	
			<s:if test="!partsReplacedInstalledSectionVisible" >
					<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
						labelNodeClass="section_header">
							<jsp:include flush="true" page="../common/read/component.jsp"/>
					</div>
			</s:if>
			<s:elseif test="buPartReplaceableByNonBUPart">
			        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.partsReplacedInstalled"/>" id="parts_replaced_installed"
			             labelNodeClass="section_header">
			            <jsp:include flush="true" page="../common/read/replacedInstalledOEMParts.jsp"/>           
			        </div>
		 	</s:elseif>
		 	<s:else> 		 
			        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="parts_replaced_installed"
			             labelNodeClass="section_header">
			            <jsp:include flush="true" page="../common/read/replacedInstalledOnlyOEMParts.jsp"/>     
			            <jsp:include flush="true" page="../common/read/component.jsp"/>     
			        </div>
		 	</s:else>
 	
			
			  <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.travelDetails"/>"
	              labelNodeClass="section_header">
	                  <jsp:include flush="true" page="../common/read/travelDetails.jsp"/>
	          </div>
	          <s:if test="incidentalsAvaialable">
	          <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.miscellaneous"/>"
	              labelNodeClass="section_header">
	                  <jsp:include flush="true" page="../common/read/otherIncidentals.jsp"/>
	          </div>
	          </s:if>
      		
				
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
					labelNodeClass="section_header">
						<jsp:include flush="true" page="../common/read/comment.jsp"/>
				</div>
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
                     labelNodeClass="section_header" open="false">
                         <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
                </div>
			    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.paymentDetails"/>" id="payment"
			         labelNodeClass="section_header">
			        	<jsp:include flush="true" page="../common/read/payment.jsp"/>
			    </div>
		         <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
		             labelNodeClass="section_header">
		             <jsp:include flush="true" page="../search_result/prevoiusCreditMemoDetails.jsp"/>
		         </div>
		        <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
			    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>" "
			         labelNodeClass="section_header">
					<jsp:include flush="true" page="actions.jsp"/>
			    </div>
			    </authz:ifNotPermitted>
		  
	 <div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.newClaim.supportDocs"/>"
							labelNodeClass="section_header">
							<jsp:include flush="true"
								page="uploadAttachments.jsp" />
						</div>
				</div>				
				<jsp:include page="../../../claims/forms/common/write/fileUploadDialog.jsp"/>
		</s:form>
</div>
	</div>	
	<div align="center">
		<a id="claimPrint" href="#"><s:text name="link.print.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("claimPrint"), "onclick", function(event){
					var claim = '<s:property value="claim"/>';
					var thisTabLabel = getMyTabLabel();
                    parent.publishEvent("/tab/open", {
					                    label: "Print Claim",
					                    url: "printClaim.action?claim="+claim,
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});
		</script>
	 </div>
</u:body>
</html>
