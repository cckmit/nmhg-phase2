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
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.claimSearchDetail"/></title>
    <s:head theme="twms"/>
	<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
      	dojo.require("twms.widget.TitlePane");
    </script>

</head>
<div id="claimForwardedExternally">
<u:body>
	<div dojoType="dijit.layout.LayoutContainer" >
		
		<div dojoType="dijit.layout.ContentPane" >
		<authz:ifUserInRole roles="processor">
			<s:if test="!task.claim.isPendingRecovery() && displayInitiateRecoveryButton()">
				<input type="button" id="manualSupplierRecovery"
					value="<s:text name='button.supplierRecovery.manualInitiation'/>" />
					
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				var supplierRecoveryButton = dojo.byId("manualSupplierRecovery");
				if(supplierRecoveryButton){
					dojo.connect(dojo.byId("manualSupplierRecovery"), "onclick", function(event){
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
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
             labelNodeClass="section_header">
             <s:push value="task">
                 <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
             </s:push>
        </div>

		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"
			labelNodeClass="section_header"> 
            <s:push value="task">
			  <jsp:include flush="true" page="../common/read/header.jsp"/>		
            </s:push>
            <s:hidden name="claim.id"/>
		</div>
		<s:if test="isLoggedInUserAnInternalUser()">
			<s:if test="task.claim.recoveryClaims.size() > 0">
				<div dojoType="twms.widget.TitlePane"
					title="<s:text name="label.common.recoveryClaimDetails" />"
					labelNodeClass="section_header"><s:push value="task">
					<jsp:include flush="true" page="../common/read/recoveryClaim.jsp" />
				</s:push></div>
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
               <s:push value="task">
                   <jsp:include flush="true" page="../common/read/servicingLocation.jsp"/>
               </s:push>
           </div>
   <div >
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
    </div> 
    <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
    
		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
			labelNodeClass="section_header" >
            <s:push value="task">
			  <jsp:include flush="true" page="../common/read/equipment.jsp"/>
            </s:push>
		</div>
	</s:if>
    
    <s:if test="task.claim.type.type != 'Campaign'">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
					labelNodeClass="section_header">
	                <s:push value="task">
	                  <jsp:include flush="true" page="../common/read/failure.jsp"/>
	                </s:push>
			</div>
	</s:if>
	<s:if test="isAlarmCodesSectionVisible()">		<div >
	  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
		           labelNodeClass="section_header">
                    <s:push value="task">
                           <jsp:include flush="true" page="../common/read/claimAlarmCodeView.jsp" />
					</s:push>
					
		      </div>
		    </div>
		    </s:if>
		    <s:if test="task.claim.laborConfig">
	<s:if test="task.partsClaim && (!task.claim.partInstalled || (task.claim.partInstalled && task.claim.competitorModelBrand!=null))">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
				labelNodeClass="section_header">
				<s:push value="task">
						<jsp:include flush="true" page="../common/read/labor_detail_part.jsp"/>
				</s:push>
			</div>
	</s:if>
	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
				labelNodeClass="section_header">
	            <s:push value="task">
					<jsp:include flush="true" page="../common/read/service_detail.jsp"/>
	            </s:push>
			</div>
	</s:elseif>	</s:if>
	<s:if test="!partsReplacedInstalledSectionVisible" >
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
				labelNodeClass="section_header">
				<s:push value="task">
					<jsp:include flush="true" page="../common/read/component.jsp"/>
				</s:push>	
			</div>
	</s:if>
	<s:elseif test="buPartReplaceableByNonBUPart">
	    <div >
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.partsReplacedInstalled"/>" id="parts_replaced_installed"
	             labelNodeClass="section_header">
	             <s:push value="task">
		            <jsp:include flush="true" page="../common/read/replacedInstalledOEMParts.jsp"/>           
		         </s:push>   
	        </div>
	 	</div>
 	</s:elseif>
 	<s:else> 		 
	    <div>
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="parts_replaced_installed"
	             labelNodeClass="section_header">
	             <s:push value="task">
		            <jsp:include flush="true" page="../common/read/replacedInstalledOnlyOEMParts.jsp"/>     
		            <jsp:include flush="true" page="../common/read/component.jsp"/>     
		         </s:push>   
	        </div>
	 	</div>
 	</s:else>
 	
		
	   <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.travelDetails"/>"
           labelNodeClass="section_header">
           <s:push value="task">
               <jsp:include flush="true" page="../common/read/travelDetails.jsp"/>
           </s:push>
       </div>
       <s:if test="incidentalsAvaialable"> 
       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.miscellaneous"/>"
           labelNodeClass="section_header">
           <s:push value="task">
               <jsp:include flush="true" page="../common/read/otherIncidentals.jsp"/>
           </s:push>
       </div>
       </s:if>

		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
			labelNodeClass="section_header">
            <s:push value="task">
				<jsp:include flush="true" page="../common/read/comment.jsp"/>
            </s:push>
		</div>
	
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
             labelNodeClass="section_header" open="false">
            <s:push value="task">
                 <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
            </s:push>
        </div>

   
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" id="supportDocs"
             labelNodeClass="section_header">
             <s:push value="task">
            	<jsp:include flush="true" page="../common/read/uploadAttachments.jsp"/>
            </s:push>
        </div>
   

		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.paymentDetails"/>"
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
	         <s:if test="claim.supplierRecovery">
	          <div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.viewClaim.SupplierInformation"/>" id="supplier"
             labelNodeClass="section_header">
              <s:push value="task">
               <jsp:include flush="true" page="../common/read/suppliers.jsp"/>  
                 </s:push> 
        </div>
    </div>  
    </s:if>
		
	<div align="center">
		<a id="claimPrint" href="#"><s:text name="link.print.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				var printClaimButton = dojo.byId("claimPrint");
				if(printClaimButton){
					dojo.connect(dojo.byId("claimPrint"), "onclick", function(event){
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
</div>
<authz:ifPermitted resource="claimsForwardedExternallyReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('claimForwardedExternally')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('claimForwardedExternally'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
