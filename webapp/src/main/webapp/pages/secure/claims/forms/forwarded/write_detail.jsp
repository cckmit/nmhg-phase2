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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
    <s:head theme="twms"/>
    <title><s:text name="title.viewClaim.draftClaimDetail"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>

    <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.ValidationTextBox");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        
    </script>
    <script type="text/javascript" src="scripts/claimUtilities.js"></script>
  <%@ include file="/pages/secure/claims/claculateTravelAndTransportation.jsp"%>
  <%@ include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body >
<u:actionResults/>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-x:auto; overflow-y:auto;" id="root">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
 
<s:form method="post"  theme="twms" validate="true" id="claim_form"
        name="claim_submit" action="claim_submit.action">

    <s:hidden name="id"/>
     <s:if test="task.claim.type.type == 'Campaign'">
	    <s:hidden name="claim.type.type" value="Campaign"/>
	    <s:hidden name="campaignNotification" /> 
    </s:if>
    <%-- This is for showing error only for dealer. NMHGSLMS-576: RTM-156, 682 --%>
    <authz:ifUserInRole roles="dealer">
	<s:if test="(task.claim.manualReviewConfigured && 
				task.claim.state.state=='Forwarded' && !isWrkOrdTrmCrdAttached(task.claim)) && buConfigAMER">  <!-- SLMSPROD-1281 -->
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.common.errors"/>"
			labelNodeClass="section_header" open="true">
			<table class="borderForTable" id="errorTable" width="100%">
				<tr>
					<td><s:text name="error.forwardedClaim.message" /></td>
				</tr>
			</table>
		</div>
	</s:if>
	</authz:ifUserInRole>
	<div >
	    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
	         id="comment_history"
	         labelNodeClass="section_header">
	             <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
	    </div>
	</div>

    <div>
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails"/>" id="claim_header"
             labelNodeClass="section_header" open="true">
              <s:push value="task">
                <jsp:include flush="true" page="../common/write/header.jsp" />
              </s:push>
        </div>
    </div>
    
    <s:if test="isLoggedInUserAnInternalUser()">
			<s:if test="task.claim.recoveryClaims.size() > 0">
				<div dojoType="twms.widget.TitlePane"
					title="<s:text name="label.common.recoveryClaimDetails" />"
					labelNodeClass="section_header">
					<s:push value="task">
						<jsp:include flush="true" page="../common/read/recoveryClaim.jsp" />
					</s:push>
				</div>
			</s:if>
		</s:if>
    
     <s:if test="task.claim.type.type== 'Campaign'">
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
	     <div >
	
          <div dojoType="twms.widget.TitlePane" title="<s:text name="label.viewClaim.ClaimAttributes"/>" id="attr_details"
             labelNodeClass="section_header"  open="true">
               <jsp:include flush="true" page="../common/write/claimSpecificAttributes.jsp"/>  
           </div>
           
         </div>  
        </s:if> 
    	
   <div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.servicingLocation"/>" id="servicing_locations"
             labelNodeClass="section_header"  open="true">
             <jsp:include flush="true" page="../common/write/servicingLocation.jsp"/>   
        </div>   
    </div>
<div >
    <s:if test="task.partsClaim && task.claim.partItemReference.serialized">    
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
            <s:push value="task">
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
            </s:push>
        </div>
    </s:if>
    </div> 
    <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>" id="equipment_details"
             labelNodeClass="section_header"  open="true" >
            <s:push value="task">
                 <jsp:include flush="true" page="../common/write/equipment.jsp" />
            </s:push>            
            <s:if test="((task.claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')
             && isMatchReadApplicable()) || task.claim.matchReadInfo.id!=null)">
                <jsp:include flush="true" page="../common/write/matchReadInclude.jsp"/> 
            </s:if>
        </div>
    </s:if>
		<s:if test="task.claim.type.type != 'Campaign'">		
		  <jsp:include flush="true" page="../common/read/availablePoliciesPopup.jsp"/>
		  		
	  	    <div >
	  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails"/>" id="failure_details"
		           labelNodeClass="section_header">
				  	<s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
			          <jsp:include flush="true" page="../common/write/failure.jsp"/>
		    	    </s:if>
				 	<s:else>
					 		<jsp:include flush="true" page="../common/write/failure2.jsp"/>
				 	</s:else>
		      </div>
		    </div>		     
		</s:if>
		<s:if test="isAlarmCodesSectionVisible()">
		<div >
	  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
		           labelNodeClass="section_header">
                     <jsp:include flush="true" page="../common/write/claimAlarmCode.jsp" />
					
		      </div>
		    </div>		 	
		</s:if>
		<s:if test="task.claim.laborConfig">
		<s:hidden name="task.claim.laborConfig" value="true"/>
		<s:if test="task.partsClaim && (!task.claim.partInstalled || task.claim.competitorModelBrand!=null)">
			<div >
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
					id="labor_information" labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/write/labor_detail_part.jsp"/>
				</div>
			</div>
		</s:if>		
		<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
		  <div >
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>" id="service_details"
	             labelNodeClass="section_header">
	            <jsp:include flush="true" page="../common/write/serviceProcedures.jsp"/>
	        </div>
		   </div>
		</s:elseif>
		</s:if>
		<s:else>
			<s:hidden name="task.claim.laborConfig" value="false"/>			
		</s:else>
		<s:if test="!partsReplacedInstalledSectionVisible" >
	        <div dojoType="dijit.layout.ContentPane" >
	            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
	                labelNodeClass="section_header">
                    <jsp:include flush="true" page="../common/write/oemParts.jsp"/>
                    <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
	                    <jsp:include flush="true" page="../common/write/nonOemParts.jsp"/>
	                </s:if>
	                <s:if test="task.claim.miscPartsConfig">	           
	            	    <jsp:include flush="true" page="../common/write/miscParts.jsp"/>	                    
	            	</s:if>
	            </div>
	        </div>
        </s:if>        
 		<s:elseif test="buPartReplaceableByNonBUPart">
	    <div >
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.partsReplacedInstalled"/>" id="parts_replaced_installed"
	             labelNodeClass="section_header">
	               <jsp:include flush="true" page="../common/write/replacedInstalledOEMParts.jsp"/>           
	        </div>
	 	</div>
	 	</s:elseif>
	 	<s:else> 		 
		    <div>
		        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="parts_replaced_installed"
		             labelNodeClass="section_header">
		            <jsp:include flush="true" page="../common/write/installedOemParts.jsp"/>     
		            <jsp:include flush="true" page="../common/write/nonOemParts.jsp"/>
		            <s:if test="task.claim.miscPartsConfig">     
		              <jsp:include flush="true" page="../common/write/miscParts.jsp"/>
		            </s:if>   
		        </div>
		 	</div>
	 	</s:else>
	
	<div >
	     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.travelDetails"/>"
	         labelNodeClass="section_header">
	          <jsp:include flush="true" page="../common/write/travelDetails.jsp"/>  
	     </div>
	</div>
	<s:if test="incidentalsAvaialable"> 
    <div >
	     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.miscellaneous"/>"
	         labelNodeClass="section_header">
	          <jsp:include flush="true" page="../common/write/otherIncidentals.jsp"/>  
	     </div>
	</div>
	</s:if>

    <div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.claimDescription"/>" id="claim_description"
             labelNodeClass="section_header">
            <div dojoType="dijit.layout.ContentPane">
                <jsp:include flush="true" page="../common/write/description.jsp"/>
            </div>
        </div>
    </div>
    
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
             labelNodeClass="section_header" open="false">
            <s:push value="task">
                 <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
            </s:push>
        </div>

    <div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" labelNodeClass="section_header">
	        <jsp:include flush="true" page="../common/write/uploadAttachments.jsp"/>
        </div>
    </div>
    
	    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.paymentDetails"/>" id="payment"
	         labelNodeClass="section_header">
	         <s:push value="task">
	        	<jsp:include flush="true" page="../common/read/payment.jsp"/>
	        </s:push>	
	    </div>
         <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
             labelNodeClass="section_header">
             <s:push value="task">
             <jsp:include flush="true" page="../search_result/prevoiusCreditMemoDetails.jsp"/>
             </s:push>
         </div>
       
    <jsp:include page="../common/write/fileUploadDialog.jsp"/>
 <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>" id="actions"
                 labelNodeClass="section_header">
	<table class="grid">
		<tr>
			<td width="25%" nowrap="nowrap" class="labelStyle">
				<s:text name="title.attributes.claimState" />:
			</td>
			<td width="75%" class="labelNormalTop">
				<s:property value="task.claim.state.state"/>						
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
			<td width="25%" nowrap="nowrap" class="labelNormal">
				<s:text name="label.newClaim.processorComments"/>:
			</td>
			<td width="75%" class="label">
				<s:property value="task.claim.externalComment"/>
			</td>
		</tr>
		<tr>
			<td width="25%" nowrap="nowrap" class="labelNormalTop">
				<s:text name="label.common.comments"/>:
			</td>
			<td width="75%" class="labelNormalTop">
				<t:textarea name="task.claim.externalComment" id="commentId" rows="4" cols="24" value=""></t:textarea>
				<s:hidden name="task.claim.internalComment" value="" />
			</td>
		</tr>
	</table>
   </div>
    
    <jsp:include flush="true" page="../common/write/validations.jsp"/>
    <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
    <table class="buttons">
        <tr>
            <td>
                <center>
                    <t:button value="%{getText('button.common.cancel')}" label="%{getText('button.common.cancel')}" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                          });
                    </script>                   
                    <s:if test="task.claim.type.type == 'Campaign'">
                        <s:submit value="%{getText('button.common.validate')}" type="input" action="campaign_draft_claim_validate" id="validateButton"/>
                    </s:if>
                    <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                        <s:submit value="%{getText('button.common.validate')}" type="input" action="draft_claim_validate" id="validateButton"/>
                    </s:elseif>
                    <s:else>
                        <s:submit value="%{getText('button.common.validate')}" type="input" action="parts_draft_claim_validate" id="validateButton"/>
                    </s:else>
                    <authz:ifUserInRole roles="processor">
						<s:if test="!task.claim.isPendingRecovery() && displayInitiateRecoveryButton()">
							<input type="button" id="manualSupplierRecovery"
							value="<s:text name='button.supplierRecovery.manualInitiation'/>" />
								<script type="text/javascript">
									dojo.addOnLoad(function() {
										var supplierRecoveryButton = dojo.byId("manualSupplierRecovery");
										if(supplierRecoveryButton){
											dojo.connect(supplierRecoveryButton, "onclick", function(event){
											var claim = '<s:property value="task.claim"/>';
											var thisTabLabel = getMyTabLabel();
							       			parent.publishEvent("/tab/open", {
									                    label: "Manual Supplier Recovery Initiation",
									                    url: "move_claim_to_pending_recovery_initiation.action?claim="+claim, 
									                    decendentOf: thisTabLabel,
									                    forceNewTab: true
							                          });
												});
							   				}
									});	
								</script>
						</s:if>
					</authz:ifUserInRole>
               </center>
            </td>
        </tr>
    </table>
    </authz:ifNotPermitted>
</s:form>
<div align="center">
		<a id="claimPrint" href="#"><s:text name="link.print.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				var printClaimButton = dojo.byId("claimPrint");
				if(printClaimButton){
					dojo.connect(printClaimButton, "onclick", function(event){
						var claim = '<s:property value="task.claim"/>';
						var thisTabLabel = getMyTabLabel();
                    	parent.publishEvent("/tab/open", {
					                    label: "Print Claim",
					                    url: "printClaim.action?claim="+claim, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                		});
				}
			});				
			
	
		</script>
	 </div>
</div>
</div>
</u:body>
</html>
