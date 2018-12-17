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
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>

<head>
    <s:head theme="twms"/> 
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css" />
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="inboxLikeButton.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.claimSearchDetail"/></title>
    
	<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
      	dojo.require("twms.widget.TitlePane");
      	dojo.require("dijit.form.Button");
      	dojo.require("twms.widget.Dialog");
   </script>
   <script type="text/javascript" src="scripts/claimUtilities.js"></script>
</head>
<%@ include file="/i18N_javascript_vars.jsp"%>
<u:body>
<div id="buttonsDiv">
	<s:if test="!showClaimAudit">
	<s:if test="claim.ncr">
			<authz:ifUserInRole roles="ncrProcessor">
			<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
						<jsp:include flush="true" page="buttons.jsp"/>
			</authz:ifNotPermitted>			
			</authz:ifUserInRole>
	</s:if>
	<s:else>
			<authz:ifUserNotInRole roles="ncrProcessor">
			<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
						<jsp:include flush="true" page="buttons.jsp"/>
			</authz:ifNotPermitted>				
			</authz:ifUserNotInRole>	
	</s:else>
	</s:if>
</div>



    <s:if test="claim.latestAudit.internalComments=='Auto Denied'">
    <div dojoType="dijit.layout.ContentPane" id="errors"  >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.errors" />"
			labelNodeClass="section_header">
            <ul>
                <s:iterator value="claim.ruleFailures">
                    <s:iterator value="failedRules">
                        <s:if test="ruleAction=='Reject Claim'">
                        <li>
                            <authz:ifUserInRole roles="processor,admin,dsmAdvisor,dsm">
                                (<s:property value="ruleNumber"></s:property>)
                            </authz:ifUserInRole>
                            <authz:ifUserInRole roles="dealer">
                                <s:if test="ruleMsg != null">
                                    <s:property value="ruleMsg"/>
                                </s:if>
                                <s:else>
                                    <s:property value="defaultRuleMsgInUS"/>
                                </s:else>
                            </authz:ifUserInRole>
                            <authz:ifUserNotInRole roles="dealer">
                                <s:property value="defaultRuleMsgInUS"/>
                            </authz:ifUserNotInRole>
                        </li>
                        </s:if>
                    </s:iterator>
                </s:iterator>
            </ul>
         </div>
    </div>
    </s:if>
		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"
			labelNodeClass="section_header"> 
			
			<jsp:include flush="true" page="../common/read/header.jsp"/>
			
		</div>
	<s:if test="isLoggedInUserAnInternalUser()">
		<s:if test="claim.recoveryClaims.size() > 0">
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.common.recoveryClaimDetails" />"
				labelNodeClass="section_header">
				<jsp:include flush="true" page="../common/read/recoveryClaim.jsp" />

			</div>
		</s:if>
	</s:if>
	<s:if test="claim.type.type== 'Campaign'"> 
	    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.campaign.info" />"
					labelNodeClass="section_header">  
    			<s:push value="claim.campaign">
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
             			labelNodeClass="section_header">
             	    	<jsp:include flush="true" page="../common/read/claimSpecificAttributes.jsp"/>  
             	    	
                 </div> 
    		</div>  
    	</s:if> 
	 	
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.servicingLocation"/>"
            labelNodeClass="section_header">
            <jsp:include flush="true" page="../common/read/servicingLocation.jsp"/>
        </div>
     <div >
    <s:if test="partsClaim && claim.partItemReference.serialized">    
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.PartDetails"/>" class="scrollYNotX">
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
           
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
           
        </div>
    </div>
    </s:if>
    </div> 
  <s:if test="claim.type.type != 'Parts' || (claim.partInstalled && (claim.competitorModelBrand == null || claim.competitorModelBrand.isEmpty()))">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
                 labelNodeClass="section_header">
                <jsp:include flush="true" page="../common/read/equipment.jsp"/>
                <s:if test="claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
                    <table>
                        <tbody>
                            <tr>
                                <s:if test="claim.matchReadInfo!=null && claim.matchReadInfo.ownerName!=null">
                                    <td width="22%" class="labelStyle">
                                        <s:text name="label.warrantyAdmin.ownerName"/>:
                                    </td>
                                    <td id="viewLink">
                                        <a id="show_entered_owner_info" class="link">
                                            <s:property value="claim.matchReadInfo.ownerName"/>
                                        </a>
                                    </td>
                                </s:if>
                            </tr>
                        </tbody>
                    </table>
                      <s:if test="claim.matchReadInfo!=null && claim.matchReadInfo.ownerName!=null"> 
                        <jsp:include flush="true" page="../common/read/enteredOwnerInfo.jsp"/>
                       </s:if>  
                </s:if>
            </div>
   </s:if>
    
    <s:if test="claim.type.type != 'Campaign'">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
					labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/read/failure.jsp"/>
			</div>
   </s:if>	
   <s:if test="isAlarmCodesSectionVisible()" >
       <div >
	  	 <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
		           labelNodeClass="section_header">
                     <jsp:include flush="true" page="../common/read/claimAlarmCodeView.jsp" />
					
		      </div>
        </div>
	</s:if>
	<s:if test="claim.laborConfig">	
	<s:if test="partsClaim && (!claim.partInstalled || claim.competitorModelBrand!=null)">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
				labelNodeClass="section_header">
				<jsp:include flush="true" page="../common/read/labor_detail_part.jsp"/>
			</div>
	</s:if>
	<s:elseif test="!partsClaim ||(claim.partInstalled && (claim.competitorModelBrand == null || claim.competitorModelBrand.isEmpty()))">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
				labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/read/service_detail.jsp"/>
			</div>
	</s:elseif>
	</s:if>		
	
	<s:if test="!partsReplacedInstalledSectionVisible" >
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
				labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/read/component.jsp"/>
			</div>
	</s:if>
	<s:elseif test="buPartReplaceableByNonBUPart">
        <div >
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="parts_replaced_installed"
	             labelNodeClass="section_header">
	            <jsp:include flush="true" page="../common/read/replacedInstalledOEMParts.jsp"/>           
	        </div>
	 	</div>
 	</s:elseif>
 	<s:else>
        <div>
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="parts_replaced_installed"
	             labelNodeClass="section_header">
	            <jsp:include flush="true" page="../common/read/replacedInstalledOnlyOEMParts.jsp"/>     
	            <jsp:include flush="true" page="../common/read/component.jsp"/>     
	        </div>
	 	</div>
 	</s:else>
 		
	
	
	
	<s:if test=" isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id
			|| claim.filedBy.id == loggedInUser.id">
			
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
  	</s:if>

	
		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
			labelNodeClass="section_header">
				<jsp:include flush="true" page="../common/read/comment.jsp"/>
		</div>
	
	
	<s:if test=" isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id
			|| claim.filedBy.id == loggedInUser.id">
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
	             labelNodeClass="section_header">
	                 <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
	        </div>
	
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
	             labelNodeClass="section_header" open="true">
	                 <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
	        </div>
	 	
	
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" labelNodeClass="section_header">
	        <jsp:include flush="true" page="../common/read/uploadAttachments.jsp"/>
        </div>
   
    
    </s:if>
    <authz:ifDealer>
    <s:if test="isRecoveryClaimAttachmentAvailable()">  
	  <div dojoType="twms.widget.TitlePane" title="<s:text name="title.recoveryClaimForDealer.supportDocs"/>" 
			labelNodeClass="section_header" open="<s:property value='#expandClaimDetailsByDefault'/>" id="attachmentsPane">
		 <jsp:include flush="true" page="../../../admin/supplier/uploadAttachmentForRecoveryClaim.jsp"/> 
	</div>	
	</s:if>
	</authz:ifDealer>
    <s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id 
    		|| claim.filedBy.id == loggedInUser.id">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.paymentDetails"/>"
                labelNodeClass="section_header">
                <jsp:include flush="true" page="payment.jsp"/>
            </div>
    
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
	            labelNodeClass="section_header">
	            <jsp:include flush="true" page="prevoiusCreditMemoDetails.jsp"/>
	        </div>
    </s:if>
   
      <s:if test="isLoggedInUserAnInternalUser() && claim.supplierRecovery">
    <div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.viewClaim.SupplierInformation"/>" id="supplier"
             labelNodeClass="section_header">
               <jsp:include flush="true" page="../common/read/suppliers.jsp"/>  
                
        </div>
    </div> 
    </s:if>

            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>"
                labelNodeClass="section_header">
                <table class="grid" cellspacing="0" cellpadding="0" >
                   <s:if test="!showClaimAudit">
                    <tr>
                        <td class="label labelStyle" width="20%"><s:text name="title.attributes.claimState" />:</td>
                        <td><s:property value="claim.state"/></td>
                    </tr>                   				
                   </s:if>                                        
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
                   <authz:ifCondition condition="!claim.state.state.equalsIgnoreCase('Draft')">
			            <s:if test="claim.accountabilityCode!=null && isLoggedInUserAnInternalUser()">
				            <tr>
				                <td class="label labelStyle"><s:text name="title.attributes.accountabilityCode"/>:</td>
	                        	<td>   
	                        		<s:property value="claim.accountabilityCode.code"/>
			                        (<s:property value="claim.accountabilityCode.description"/>)
	                        	</td>    
				            </tr>
			            </s:if>
			            <s:if test="claim.acceptanceReason != null">
				            <tr>			            
				                <td class="labelStyle" nowrap="nowrap"><s:text name="title.attributes.acceptanceReason"/>:</td>
				                <td><s:property value="claim.acceptanceReason.description"/></td>
				            </tr>
			            </s:if>
			            
			              <s:if test="claim.rejectionReasons != null">
			              <s:iterator value="displayRejectioReasons()" status="rowCounterReasonsList">
			              <tr>	<s:if test="#rowCounterReasonsList.count==1">
				                	<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.denyReasons"/>:</td>
				                </s:if>
				                <s:else>
				                	<td>&nbsp;</td>
				                </s:else>
				                <td><s:property value="displayRejectioReasons().get(#rowCounterReasonsList.index)"/> </td>
				            </tr>
				            </s:iterator>
				            </s:if>  
			            <s:if test="isLoggedInUserAnInternalUser()">
			            <tr>
			                <td class="labelStyle" nowrap="nowrap"><s:text name="label.newClaim.internalProcessingNotes"/>:</td>
			                <td><s:property value="claim.internalComment"/></td>
			            </tr>
			            </s:if>
	                </authz:ifCondition>                              
                    
                </table>
            </div>


	<s:if test="isLoggedInUserAnInternalUser() && claim.recoveryInfo!=null">
		<div dojoType="dijit.layout.ContentPane" label="Claim Info"
			style="overflow-Y: auto; overflow-x: hidden">
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="claim.recoveryInfoComments.comments"/>"
				labelNodeClass="section_header">
				<table class="grid borderForTable" style="width: 97%;">
					<thead>
						<tr class="row_head">
							<th><s:text name="label.common.date" /></th>
							<th><s:text name="label.common.user" /></th>
							<th><s:text name="label.common.comments" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="claim.recoveryInfo.comments" status="status">
							<tr>
								<td><s:set name="dateFormat"
										value="@tavant.twms.dateutil.TWMSDateFormatUtil@getDateFormatForLoggedInUser()" />
									<s:date name="madeOn.asJavaUtilDate()" format="%{dateFormat}" />
								</td>
								<td><s:property value="madeBy.completeNameAndLogin" /></td>
								<td><s:property value="comment" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</div>
	</s:if>
	
	<s:form id="claimSearchForm"/>

	<s:if test="claim.getState().getState() != 'draft' && (isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id 
    		|| claim.filedBy.id == loggedInUser.id)">   
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
	 </s:if>
	 
</u:body>
<authz:ifPermitted resource="claimsPredefinedSearchReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
</html>
