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
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.processorReviewDetail"/></title>

    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
      	 dojo.require("twms.widget.TitlePane");
        dojo.require("dojox.layout.ContentPane");
    </script>
    <%@ include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
    
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-x:auto; overflow-y:auto;" id="root">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <s:form  method="post" theme="twms" validate="true" id="claim_form"
        name="claim_submit" action="claim_submit.action">
        <s:hidden name="id"/>
        <div dojoType="dijit.layout.ContentPane" >
        	<u:actionResults/>
        </div>
		<s:if test="task.claim.appealed.booleanValue() == 0">
		<div dojoType="dijit.layout.ContentPane" id="errors">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.errors"/>"
	            labelNodeClass="section_header" open="true">
				<ul>
                <s:iterator value="task.claim.ruleFailures">
                    <s:iterator value="failedRules">
                        <li>
                           (<s:property value="ruleNumber"></s:property>)
                            <s:if test="ruleMsg != null">
                                <s:property value="ruleMsg"/>
                            </s:if>
                            <s:else>
                                <s:property value="defaultRuleMsgInUS"/>
                            </s:else>
                        </li>
                    </s:iterator>
                </s:iterator>
				</ul>
		</div>
		</s:if>

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
	     <div >
	
          <div dojoType="twms.widget.TitlePane" title="<s:text name="label.viewClaim.ClaimAttributes"/>" id="attr_details"
             labelNodeClass="section_header"  open="true">
               <jsp:include flush="true" page="../common/write/claimSpecificAttributes.jsp"/>  
           </div>
           
         </div>  
        </s:if> 
    
	        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.servicingLocation"/>" id="servicing_locations"
	             labelNodeClass="section_header"  open="true">
		        <table class="form" cellspacing="0" cellpadding="0" id="servicing_location">
			        <tr>
			            <td>
			                <label for="servicingLocation"><s:text name="label.viewClaim.servicingLocation"/>:</label>
			            </td>
			            <td>
			            	<s:hidden name="task.claim.servicingLocation" id="thirdPartyServiceLoc" />
			            	<span id="serviceLocationDetail">
				            	<s:if test="task.claim.servicingLocation!=null">
				            	<s:if test="buConfigAMER">
					            	<s:property value="task.claim.servicingLocation.getLocationWithBrand()()"/>
					            	</s:if>
					            	<s:else>
					            	<s:property value="task.claim.servicingLocation.getShipToCodeAppended()"/>
					            	</s:else>
<%-- 					            	<s:property value="task.claim.servicingLocation.location"/> --%>
					            	<s:hidden name="task.claim.servicingLocation.location" id="servicing_location_id" />
				            	</s:if>
			            	</span>
			            </td>
			        </tr>  
			    </table>       	        
	    </div>   
    <s:if test="task.partsClaim && task.claim.partItemReference.serialized">    
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
            <s:push value="task">
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
            </s:push>
        </div>
    </s:if>
        <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
                labelNodeClass="section_header">
                <s:push value="task">
                    <jsp:include flush="true" page="../common/write/equipment.jsp"/>
                </s:push>
                <s:if test="task.claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')
                          && isMatchReadApplicable()">
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
	            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
	                    labelNodeClass="section_header">
	                   <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
							          <jsp:include flush="true" page="../common/write/failure.jsp"/>
							        </s:if>
										 	<s:else>
										 		<jsp:include flush="true" page="../common/write/failure2.jsp"/>
										 	</s:else>
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
		<s:if test="task.partsClaim && (!task.claim.partInstalled || (task.claim.partInstalled && task.claim.competitorModelBrand!=null))">
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
					labelNodeClass="section_header">
					<s:push value="task">
						<jsp:include flush="true" page="../common/write/labor_detail_part.jsp"/>
					</s:push>
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
        <s:if test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
            <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                 executeScripts="true"  id="refreshingDiv">
                <jsp:include flush="true" page="../processor_review/write/supplierRecoveryData.jsp"/>
            </div>
        </s:if>
        <s:else>
            <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                 executeScripts="true"  id="refreshingDiv">
                <jsp:include flush="true" page="../processor_review/read/supplierRecoveryData.jsp"/>
            </div>
        </s:else>
	   
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
             labelNodeClass="section_header" open="false">
            <s:push value="task">
                 <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
            </s:push>
        </div>
        <div >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" id="supportDocs"
                 labelNodeClass="section_header">
                <jsp:include flush="true" page="../common/write/uploadAttachments.jsp"/>
            </div>
        </div>

        <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                     executeScripts="true"  id="paymentDetailsEditable">
                <jsp:include flush="true" page="../processor_review/write/payment.jsp"/>
        </div>
	         <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
	             labelNodeClass="section_header">
	             <s:push value="task">
	             <jsp:include flush="true" page="../search_result/prevoiusCreditMemoDetails.jsp"/>
	             </s:push>
	         </div>
                <div >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>" id="actions"
                 labelNodeClass="section_header">
                <table width="96%" class="grid">
                	<tr>
                		<td width="20%" class="labelStyle">
                			<s:text name="label.claim.accountabilityCode" /> :
                		</td>
                		<td width="23%" class="labelStyle">
                		<s:select id="accountabilityCode" name="task.claim.accountabilityCode" cssClass="processor_decesion" 
                						list="getLovsForClass('AccountabilityCode',task.claim)" listKey="code" listValue="description" 
                						headerKey="" headerValue="%{getText('label.common.selectHeader')}"/>
                					
                		</td>
                		<td  width="28%" class="labelNormalTop" colspan="2">
                			                			
                		</td>
                		<script type="text/javascript">
                		dojo.addOnLoad(function(){
                			 <s:if test="task.claim.accountabilityCode !=null">
							  		dijit.byId('accountabilityCode').setValue("<s:property value="task.claim.accountabilityCode.code"/>");
				            </s:if>
                			 dojo.connect(dijit.byId("accountabilityCode"), "onChange", function(newValue) {
                			 var supRecChkbox=dojo.byId("supplierPartRecoverableId");
                			 var supRecChkBoxHidden=dojo.byId("supplierPartRecoverableHiddenId");
                			 var autoCheckRecoveryFlag = '<s:property value="autoCheckRecoveryFlag"/>'
                			 if(newValue == 'SUP' && supRecChkbox && autoCheckRecoveryFlag == 'true'){
							 		supRecChkbox.checked = "checked";
							 	}else{
							 		supRecChkbox.checked = false;
							 	}
                			});
                		});
                		</script>
                	</tr>
                </table> 
                <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
                	<jsp:include flush="true" page="../processor_review/write/actions.jsp"/>
                </authz:ifNotPermitted>
                
                <jsp:include page="../common/write/fileUploadDialog.jsp"/>
                
            </div>
        </div>

        
    </s:form>
	<jsp:include flush="true" page="../common/write/thirdPartySearchPage.jsp"/>
    
    <div dojoType="twms.widget.Dialog" id="invoices" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250">
			 <div class="dialogContent" dojoType="dijit.layout.LayoutContainer" style="padding: 0; margin: 0; height: 200px; width: 400px;">
		
			     <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="margin:0; padding: 0;height: 23px; background: url('image/menubg_new.gif') repeat-x;">
			         <img id="invoice_hider" style="float: right; margin: 2px;" src="image/CloseRuleWizard.gif"/>
			     </div>
			     <div dojoType="dijit.layout.ContentPane" layoutAlign="client" executeScripts="true" id="invoice_form">
				     <iframe id="fileUploadFrame" style="border: none; width: 100%; height:98%"></iframe>		     
			 	</div>
			</div>
		</div>
    </div>
    </div>
    </div>
</u:body>
</html>
