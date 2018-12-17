<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Aug 4, 2008
  Time: 10:33:41 PM

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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>

<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="inboxLikeButton.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.claimSearchDetail"/></title>
	<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
      	dojo.require("twms.widget.TitlePane");
      	dojo.require("dijit.form.Button");
   </script>
</head>
<u:body>
	<s:if test="task.claim.eligibleForAppeal  || task.claim.eligibleForTransfer">
        <s:push value="task">
        <s:hidden id="taskInitiated" value="true"></s:hidden>
        <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
        	<jsp:include flush="true" page="../search_result/buttons.jsp"/>
        </authz:ifNotPermitted>
        </s:push>
    </s:if>

    <div dojoType="dijit.layout.LayoutContainer" 
    style="width: 100%; height: 92%; overflow-X: hidden; overflow-Y: auto;">
        <s:if test="claim.latestAudit.internalComments=='Auto Denied'">
            <div dojoType="dijit.layout.ContentPane" id="errors">
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.errors"/>"
                     labelNodeClass="section_header" open="true">
                    <ul>
                        <s:iterator value="task.claim.ruleFailures">
                            <s:iterator value="failedRules">
                                <s:if test="ruleAction=='Reject Claim'">
                                    <li>
                                        <authz:ifUserInRole roles="processor,admin,dsmAdvisor,dsm">
                                            (<s:property value="ruleNumber"></s:property>)
                                        </authz:ifUserInRole>
                                        <s:if test="ruleMsg != null">
                                            <s:property value="ruleMsg"/>
                                        </s:if>
                                        <s:else>
                                            <s:property value="defaultRuleMsgInUS"/>
                                        </s:else>
                                    </li>
                                </s:if>
                            </s:iterator>
                        </s:iterator>
                    </ul>
                </div>
            </div>
        </s:if>

        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
                 labelNodeClass="section_header">
                <s:push value="task">
                     <jsp:include flush="true" page="../common/read/userCommentHistory.jsp"/>
                </s:push>
            </div>
        </div>

        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"
                labelNodeClass="section_header">
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/header.jsp"/>
                </s:push>
            </div>
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
             			labelNodeClass="section_header">
             			<s:push value="task">
             	    	<jsp:include flush="true" page="../common/read/claimSpecificAttributes.jsp"/>  
             	    	  </s:push>
                 </div> 
    		</div>  
    	</s:if> 
    	
        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.servicingLocation"/>"
                labelNodeClass="section_header">
                <s:push value="task">
                <jsp:include flush="true" page="../common/read/servicingLocation.jsp"/>
                </s:push>
            </div>
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
        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
                labelNodeClass="section_header">
                <s:push value="task">
                <jsp:include flush="true" page="../common/read/equipment.jsp"/>
                </s:push>
                <s:if test="claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
               <table>
                <tbody>
                    <tr>
                        <s:if test="claim.matchReadInfo!=null && claim.matchReadInfo.ownerName!=null">
                            <td >
                                <s:text name="label.warrantyAdmin.ownerName"/>:
                            </td>
                            <td id = "viewLink" >
                                <a id="show_entered_owner_info" class="link">
                                    <s:property value="claim.matchReadInfo.ownerName"/>
                                </a>
                            </td>
                        </s:if>
                    </tr>
                </tbody>
            </table>
            <s:push value="task">
                <s:if test="claim.matchReadInfo!=null && claim.matchReadInfo.ownerName!=null"> 
                   <jsp:include flush="true" page="../common/read/enteredOwnerInfo.jsp"/>
                </s:if>   
            </s:push>
            </s:if>
            </div>
        </div>
        </s:if>
        <s:if test="claim.type.type != 'Campaign'">
            <div dojoType="dijit.layout.ContentPane" >
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
                        labelNodeClass="section_header">
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/failure.jsp"/>
                    </s:push>
                </div>
            </div>
        </s:if>
        <s:if test="isAlarmCodesSectionVisible()" >
	        <div>
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
			<div dojoType="dijit.layout.ContentPane" >
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.laborInformation"/>"
					labelNodeClass="section_header">
					<s:push value="task">
						<jsp:include flush="true" page="../common/read/labor_detail_part.jsp"/>
					</s:push>
				</div>
			</div>
		</s:if>
		 <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
			<div dojoType="dijit.layout.ContentPane" >
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
					labelNodeClass="section_header">
	                <s:push value="task">
	                    <jsp:include flush="true" page="../common/read/service_detail.jsp"/>
	                </s:push>
	            </div>
			</div>
		</s:elseif>
	</s:if>
		<s:if test="!partsReplacedInstalledSectionVisible" >
			<div dojoType="dijit.layout.ContentPane" >
				<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
					labelNodeClass="section_header">
					<s:push value="task">
						<jsp:include flush="true" page="../common/read/component.jsp"/>
					</s:push>	
				</div>
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
	 	<div dojoType="dijit.layout.ContentPane" >
          <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.travelDetails"/>"
              labelNodeClass="section_header">
              <s:push value="task">
                  <jsp:include flush="true" page="../common/read/travelDetails.jsp"/>
              </s:push>
          </div>
        </div>
       <s:if test="incidentalsAvaialable"> 
 	   <div dojoType="dijit.layout.ContentPane" >
          <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.miscellaneous"/>"
              labelNodeClass="section_header">
              <s:push value="task">
                  <jsp:include flush="true" page="../common/read/otherIncidentals.jsp"/>
              </s:push>
          </div>
        </div>
        </s:if>

       <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
                labelNodeClass="section_header">
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/comment.jsp"/>
                </s:push>
            </div>
       </div>

        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" labelNodeClass="section_header">
                <jsp:include flush="true" page="../common/read/uploadAttachments.jsp"/>
            </div>
        </div>

        <s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id ">
            <div dojoType="dijit.layout.ContentPane" >
                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.paymentDetails"/>"
                    labelNodeClass="section_header">
                    <s:push value="task">
                    <jsp:include flush="true" page="../search_result/payment.jsp"/>
                    </s:push>
                </div>
            </div>
        </s:if>

        <div dojoType="dijit.layout.ContentPane" >
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.prevoiusCreditMemoDetails"/>"
                labelNodeClass="section_header">
                <s:push value="task">
                <jsp:include flush="true" page="../search_result/prevoiusCreditMemoDetails.jsp"/>
                </s:push>
            </div>
        </div>

        <authz:ifUserInRole roles="admin,dsmAdvisor,recoveryProcessor,processor,dsm, receiver,inspector,sra,partshipper,system,salesPerson,technician">
            <div dojoType="dijit.layout.ContentPane" >
                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.actions"/>"
                    labelNodeClass="section_header">
                    <s:push value="task">
                    <table class="form">
                        <tr>
                            <td class="labelStyle"><s:text name="title.attributes.claimState" />:</td>
                            <td><s:property value="claim.state"/></td>
                            <td class="labelStyle"><s:text name="title.attributes.rejectionReason" />:</td>
                            <td><s:property value="claim.rejectionReason.description"/></td>
                            <td class="labelStyle"><s:text name="title.attributes.accountabilityCode"/>:</td>
                            <td>
                                <s:if test="claim.accountabilityCode!=null">
                                <s:property value="claim.accountabilityCode.code"/>
                                (<s:property value="claim.accountabilityCode.description"/>)
                                </s:if>
                            </td>
                        </tr>
                    </table>
                    </s:push>
                </div>
            </div>
       </authz:ifUserInRole>

       <s:form id="claimSearchForm"/>
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
    </div>
</u:body>
</html>