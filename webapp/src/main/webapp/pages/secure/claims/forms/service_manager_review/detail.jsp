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
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.smReviewDetail"/></title>
	<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.BorderContainer");
      	dojo.require("twms.widget.TitlePane");
   </script>
   <script type="text/javascript" src="scripts/claimUtilities.js"></script>
    <%@ include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
	<u:actionResults/>
	<div id="root">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<s:form  method="post" theme="twms" validate="true" id="claim_form"
        name="claim_submit" action="claim_submit.action">

		<s:hidden name="id"/>
	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
	            labelNodeClass="section_header">
	                <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
	       </div>
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"
				labelNodeClass="section_header">
				<s:push value="task">
					<jsp:include flush="true" page="../common/write/header.jsp"/>
				</s:push> 		
			</div>
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
             	    	<jsp:include flush="true" page="../common/write/claimSpecificAttributes.jsp"/>  
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
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
            <s:push value="task">
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
            </s:push>
        </div>
    </s:if>
    </div> 
      <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
				labelNodeClass="section_header">
				<s:push value="task">	
					<jsp:include flush="true" page="../common/write/equipment.jsp"/>
				</s:push>
				 <s:if test="((task.claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')
		 && isMatchReadApplicable()) || task.claim.matchReadInfo.id!=null) && task.claim.type.type != 'Campaign' ">
                    <jsp:include flush="true" page="../common/write/matchReadInclude.jsp"/>
                     
                     <s:if test="!checkForValidatableCountry(task.claim.matchReadInfo.ownerCountry)">                     
					    <s:hidden name="stateCode" value="task.claim.matchReadInfo.ownerState"/>
					    <s:hidden name="cityCode" value="task.claim.matchReadInfo.ownerCity"/>
					    <s:hidden name="zipCode" value="task.claim.matchReadInfo.ownerZipcode"/> 
					 </s:if>   
                      
                </s:if>	
			</div>
		</s:if>
		
		<s:if test="task.claim.type.type != 'Campaign'">
	    	<jsp:include flush="true" page="../common/read/availablePoliciesPopup.jsp"/>
	    	<s:div>
	    	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
						labelNodeClass="section_header">
						<s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                            <jsp:include flush="true" page="../common/write/failure.jsp"/>
                        </s:if>
					 	<s:else>
					 		<jsp:include flush="true" page="../common/write/failure2.jsp"/>
					 	</s:else>
				</div>
			</s:div>
		</s:if>
		<s:if test="isAlarmCodesSectionVisible()">
	  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
		           labelNodeClass="section_header">
                     <jsp:include flush="true" page="../common/write/claimAlarmCode.jsp" />
					
		      </div>
		</s:if>
		<s:if test="task.claim.laborConfig">
		<s:hidden name="task.claim.laborConfig" value="true"/>
		<s:if test="task.partsClaim && (!task.claim.partInstalled || task.claim.competitorModelBrand!=null)">
		
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
					id="labor_information" labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/write/labor_detail_part.jsp"/>
				</div>
	
		</s:if>		
		<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
					labelNodeClass="section_header">
						<jsp:include flush="true" page="../common/write/serviceProcedures.jsp"/>
				</div>
		</s:elseif>
		</s:if>	
		<s:else>
		<s:hidden name="task.claim.laborConfig" value="false"/>	
		</s:else>
		<s:if test="!partsReplacedInstalledSectionVisible" >
		    <div >
		        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>" id="components_replaced"
		             labelNodeClass="section_header">
		            <jsp:include flush="true" page="../common/write/oemParts.jsp"/>           
		            <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
			            <jsp:include flush="true" page="../common/write/nonOemParts.jsp"/>
			        </s:if>    		       
	                <s:if test="(!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))) && task.claim.miscPartsConfig">	           
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
	 		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.travelDetails"/>"
	            labelNodeClass="section_header">
	                <jsp:include flush="true" page="../common/write/travelDetails.jsp"/>
	        </div>
	        <s:if test="incidentalsAvaialable"> 
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.miscellaneous"/>"
	            labelNodeClass="section_header">
	                <jsp:include flush="true" page="../common/write/otherIncidentals.jsp"/>
	        </div>
	        </s:if>
			<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
				labelNodeClass="section_header">
					<jsp:include flush="true" page="../common/write/description.jsp"/>
			</div>
            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
                 labelNodeClass="section_header" open="false">
                     <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
            </div>
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" id="supportDocs"
                 labelNodeClass="section_header">
				
                	<jsp:include flush="true" page="../common/write/uploadAttachments.jsp"/>
            </div>
         <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true"  id="paymentDetailsEditable">
            <jsp:include flush="true" page="write/payment.jsp"/>
        </div>
	         <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
	             labelNodeClass="section_header">
	             <s:push value="task">
		             <jsp:include flush="true" page="../search_result/prevoiusCreditMemoDetails.jsp"/>
                </s:push>		             
	         </div>
	    <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
        <div>
		    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>" id="actions"
		         labelNodeClass="section_header">
				<jsp:include flush="true" page="write/actions.jsp"/>
		    </div>
		</div>
		</authz:ifNotPermitted>
		<jsp:include page="../common/write/fileUploadDialog.jsp"/>
	</s:form>
	<div align="center">
		<a id="claimPrint" href="#"><s:text name="link.print.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
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
			});	
		</script>
	 </div>
	</div>
	</div>		

</u:body>
<authz:ifPermitted resource="claimsServiceManagerReviewReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('claim_form')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('claim_form'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
