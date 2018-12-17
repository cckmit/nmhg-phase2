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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<div class="inboxLikeButtonWrapper" style="text-align:left">
<authz:ifUserInRole roles="supplierRecoveryInitiator, processor, sra, recoveryProcessor">
	<s:form id="processorReopen">
		<authz:ifProcessor>
			<s:hidden name="selectedBusinessUnit" value="%{claim.businessUnitInfo.name}"/>
			<s:if test="claim.eligibleForAppeal && !anyPendingRecoveryInitiationTaskOpen && (claim.loaScheme == null ||(claim.loaScheme != null && isLOAProcessor()))" >
			    <button dojoType="dijit.form.Button" id="reopenClaim"
			        activeImg="../../../image/inbox_button/twmsActive-" inactiveImg="../../../image/inbox_button/twmsButton-"
			        pressedImg="../../../image/inbox_button/twmsPressed-" disabledImg="../../../image/inbox_button/twmsDisabled-">
			        <div>
			            <span class="inboxLikeButtonText">
			            		<s:text name="button.reopen.claim"/> <%--TODO : I18N me !! --%>
			            </span>
			        </div>
			    </button>
		<jsp:include flush="true" page="../../../common/notifyAdmin.jsp"/>	
			    <span>
						<s:if test="claim.eligibleForReopenRecoveryClaim">
								<s:checkbox id="reopenRecoveryClaim" name="reopenRecoveryClaim"/> <%--TODO : I18N me !! --%>
			            		<s:text name="label.reopen.recoveryClaim"/>
			           </s:if>
			    </span>
	
			    <script type="text/javascript">
					dojo.addOnLoad(function() {
				        var reopenClaim = dijit.byId("reopenClaim");
				        reopenClaim.onClick = function() {
				        		var validationDialog = dijit.byId("validations");
				        	 	var days ='<s:property value="getDaysAfterPartShippedForNotification()"/>';
					       	 	var validationIndicator = "Attention: You are about to re-open a claim that has a part return ship date greater than " + days +" days old.Do you want to proceed?";
					        	var validations = dijit.byId("validations_data");
					        	var params = {};
					            twms.ajax.fireHtmlRequest("notify_processor_if_more_days.action?claimId=<s:property value="id"/>",params ,
					                        function(details) {
					                    		if(eval(details)){
					                    		 	validationDialog.show();
					                				validations.setContent(validationIndicator);
					                    		}
					                    		else{
					                    			var frm=document.getElementById('processorReopen');
					    				        	frm.action="claim_reopen_detail.action?claimId=<s:property value="id"/>";
					    							frm.submit();
					                    		}
					                        });
					        } 
				      
				           });
				</script>
		   </s:if>
		   <s:if test="claim.eligibleForProcess && (!showClaimAudit) && (claim.loaScheme == null ||(claim.loaScheme != null && isLOAProcessor() && claim.state.state!='Transferred'))">
		       <s:if test="claim.state.equals(@tavant.twms.domain.claim.ClaimState@PENDING_PAYMENT_SUBMISSION) || claim.state.equals(@tavant.twms.domain.claim.ClaimState@ACCEPTED)">
                    <button dojoType="dijit.form.Button" id="reprocessClaimToMe"
                        activeImg="../../../image/inbox_button/twmsActive-" inactiveImg="../../../image/inbox_button/twmsButton-"
                        pressedImg="../../../image/inbox_button/twmsPressed-" disabledImg="../../../image/inbox_button/twmsDisabled-">
                        <div class="create_button inboxLikeButton">
                            <span class="inboxLikeButtonText">
                                <s:text name="button.reprocessToMe.claim"/>
                            </span>
                        </div>
                    </button>
			 </s:if>
			 <s:else>
			      <button dojoType="dijit.form.Button" id="processClaimToMe"
                     activeImg="../../../image/inbox_button/twmsActive-" inactiveImg="../../../image/inbox_button/twmsButton-"
                     pressedImg="../../../image/inbox_button/twmsPressed-" disabledImg="../../../image/inbox_button/twmsDisabled-">
                     <div class="create_button inboxLikeButton">
                         <span class="inboxLikeButtonText">
                             <s:text name="button.processToMe.claim"/>
                         </span>
                     </div>
                  </button>
              </s:else>
			    <script type="text/javascript">
					dojo.addOnLoad(function() {
				        var reopenClaim = dijit.byId("processClaimToMe");
				        if(reopenClaim){
				            reopenClaim.onClick = function() {
				        	    var frm = document.getElementById('processorReopen');
				        	    if(dojo.byId("taskInitiated") != null && dojo.byId("taskInitiated").value == "true") {
				        		    frm.action="claim_process.action?id=<s:property value='id'/>&contextValue=ClaimSearches&partShippedNotRcvd=true";
				        	    } else {
				        		    frm.action="claim_process.action?claimId=<s:property value='id'/>&contextValue=ClaimSearches";
				        	    }
							    frm.submit();
				            };
				        }
				        var reprocess = dijit.byId("reprocessClaimToMe");
				        if(reprocess){
                        	reprocess.onClick = function() {
                                //get the sync tracker state
                                 twms.ajax.fireJsonRequest("getClaimSyncState.action",{
                                 claimId:"<s:property value='id'/>"
                                 },function(data) {
                                     if(data == "false")
                                     {
                                           var frm = document.getElementById('processorReopen');
                                           if(dojo.byId("taskInitiated") != null && dojo.byId("taskInitiated").value == "true") {
                                               frm.action="claim_process.action?id=<s:property value='id'/>&contextValue=ClaimSearches&partShippedNotRcvd=true";
                                           } else {
                                              frm.action="claim_process.action?claimId=<s:property value='id'/>&contextValue=ClaimSearches&reProcess=true";
                                           }
                                           frm.submit();
                                     }else{
                                         dijit.byId("claim_reprocess_dialog").show();
                                     }
                                 });
                            }
                        }
					});
				</script>
		  </s:if>
	        <s:if test="claim.open">
		      	<button dojoType="dijit.form.Button" id="deactivateClaim"
			        activeImg="../../../image/inbox_button/twmsActive-" inactiveImg="../../../image/inbox_button/twmsButton-"
			        pressedImg="../../../image/inbox_button/twmsPressed-" disabledImg="../../../image/inbox_button/twmsDisabled-">
			        <div class="create_button inboxLikeButton" style="padding-left:0;">
			            <span class="inboxLikeButtonText">
			            	<s:text name="button.common.deActivate"/>
			            </span>
			        </div>
			    </button>
                <script type="text/javascript">
				  var confirmationDialog = null;
	                dojo.addOnLoad(function() {
	                	confirmationDialog = dijit.byId("deactive_confirmation");
						dojo.byId("dialogBoxContainer").style.display = "block";
						
						var deactivateClaim = dijit.byId("deactivateClaim");						
						deactivateClaim.onClick = function() {
							confirmationDialog.show();
						};						
						dojo.connect(dojo.byId('confirmationSubmitButton'), "onclick", function(){
							document.forms[0].action="claim_deactivate.action?id=<s:property value="id"/>&contextValue=ClaimSearches";
	                        document.forms[0].submit();
						});
					    dojo.connect(dojo.byId('closeButton1'), "onclick", function() {
						   confirmationDialog.hide();
				        });
	                    
	                });
			    </script>
			    </s:if>
		  
	   </authz:ifProcessor>
	   <authz:ifUserInRole roles="processor, supplierRecoveryInitiator, sra, recoveryProcessor">
	   <s:if test="(!claim.commercialPolicy && !'Campaign'.equalsIgnoreCase(claim.type.type) &&
							(claim.state.state.equals('Accepted')||claim.state.state.equals('Pending Payment Submission')
																||claim.state.state.equals('Pending Payment Response')) &&
																(!claim.reopened ||claim.reopenRecoveryClaim)) && (displayInitiateRecoveryButton() && !claim.isPendingRecovery())">
					<button id="specifySupplierContract" dojoType="dijit.form.Button">
                        <div>
                            <span class="inboxLikeButtonText">
                                <s:text name="button.supplierRecovery.specifySupplierContract" />
                            </span>
                        </div>
					</button>
				    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            if(dijit.byId("specifySupplierContract")){
                            dojo.connect(dojo.byId("specifySupplierContract"), "onclick", function(event){
                                    var claim = '<s:property value="claim"/>';
                                    var thisTabLabel = getMyTabLabel();
                                    parent.publishEvent("/tab/open", {
                                                        label: "Specify Contract",
                                                        url: "specify_supplier_contract.action?claim="+claim,
                                                        decendentOf: thisTabLabel,
                                                        forceNewTab: true
                                    });
                             });
                            }
                         });
				    </script>
                </s:if>
            </authz:ifUserInRole>
	   <authz:ifUserInRole roles="supplierRecoveryInitiator, sra, recoveryProcessor">
		   <s:if test="displayInitiateRecoveryButton() && !claim.isPendingRecovery()">   
			   <button id="initiateSupplierRecovery"  dojoType="dijit.form.Button">
			    <div>
			           <span class="inboxLikeButtonText">
			    	   <s:text name="button.supplierRecovery.initiateSupplierRecovery"/>
			       	</span>
			    </div>
			   </button>
		   </s:if>
		   <script type="text/javascript">
					dojo.addOnLoad(function() {
				        if(dijit.byId("initiateSupplierRecovery")){   
					        var initiateSupplierRecoveryButton = dijit.byId("initiateSupplierRecovery");    
					        initiateSupplierRecoveryButton.onClick = function(){
								var claim = '<s:property value="claim"/>';
								document.forms[0].action="move_claim_to_pending_recovery_initiation.action?claim=<s:property value="claim"/>";
								document.forms[0].submit();
			                };
				        }
					});
		   </script>
	   </authz:ifUserInRole>
	   <authz:ifProcessor>
	   <s:if test="claim.open">
	            <table width="96%" class="grid">
	                <tr>
	                    <td colspan="2" class="labelStyle" width="34%">
	                        <s:text name="label.common.comments"/> :
	                    </td>
	                    <td width="75%" class="labelNormalTop">
	                        <t:textarea rows="3" cols="80" name="claim.internalComment" id="internalCom"
	                            wrap="physical" cssClass="bodyText" value=""/>
	                    </td>
	                </tr>
	            </table>
	        </s:if>
		  
	   </authz:ifProcessor>
	   
	   </s:form>
   </authz:ifUserInRole>
    <authz:ifUserInRole roles="dealer,sra,supplier,reviewer">

	</authz:ifUserInRole> 
   <authz:ifUserInRole roles="dealer,processor">
  	   	 <s:if test="(claim.isRecoveryClaimClosed() && eligibleForResubmission && !maximumResubmissionExceeded && !anyPendingRecoveryInitiationTaskOpen
   	   	   	   && ((isLoggedInUserAnInternalUser() && (claim.loaScheme == null ||(claim.loaScheme != null && isLOAProcessor()))) || claim.forDealer.id == loggedInUsersDealership.id))">
   	     <button dojoType="dijit.form.Button" id="resubmitClaim"
	          activeImg="../../../image/inbox_button/twmsActive-" inactiveImg="../../../image/inbox_button/twmsButton-"
	          pressedImg="../../../image/inbox_button/twmsPressed-" disabledImg="../../../image/inbox_button/twmsDisabled-">
	          <div class="create_button inboxLikeButton">
	             <span class="inboxLikeButtonText">
	            		 <s:text name="button.resubmit.claim"/>
	             </span>
	          </div>
	     </button>
         <s:if test="checkIfReturnProcessAvailable()" >
	        <span id="partReturn" tabindex="0" style="margin-top:5px; float:left;">
				<img src="image/warning.gif" width="16" height="14" />
			</span>
			<span dojoType="dijit.Tooltip" connectId="partReturn" >
				<s:text name="message.dealer.partreturn.claim.resubmit" />
			</span>
         </s:if>

		 <script type="text/javascript">
			dojo.addOnLoad(function() {
		        var resubmitClaim = dijit.byId("resubmitClaim");
		        resubmitClaim.onClick = function() {
					document.forms[0].action="claim_resubmit_detail.action?claimId=<s:property value="id"/>";
					document.forms[0].submit();
		           };   
			});
		 </script>
		 </s:if>
	</authz:ifUserInRole> 
	
	
	 <div id="dialogBoxContainer" style="display: none">
		    <div dojoType="twms.widget.Dialog" id="deactive_confirmation" bgColor="white"
				bgOpacity="0.5" toggle="fade" toggleDuration="250" style="height: 120px; width: 310px; border: 1px solid #EFEBF7">
				<div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
					style="height: 90px; width: 300px; ">
					<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="closableDialog" style="background: #F3FBFE;">
						<span class="TitleBar" style="font-size: 9pt; display: inline;background: #F3FBFE;"> 
						    <s:text name="label.deActive.confirmation"></s:text>
						</span>
					</div>					
					<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
					    <br><s:text name="message.deActive.confirmation" /> 
					    <br><br>
						<table width="60%" class="buttonWrapperPrimary">				
							<tr>
								<td align="center">
								    <input type="button" id="confirmationSubmitButton" name="Submit2"
										value="<s:text name="label.common.yes"/>" class="buttonGeneric" />				
								</td>								
								<td align="center">
								    <input type="button" id="closeButton1" value="<s:text name="label.common.no"/>" class="buttonGeneric"										 />
								</td>
							</tr>
						</table>
					</div>
					
				</div>
			</div>
      </div> 
</div>

<div dojoType="twms.widget.Dialog" id="claim_reprocess_dialog" title="Claim State Changed" style="width:40%;height:20%;overflow:hidden;">
     <s:text name="message.claim.state.changed" />
     <div align="center"><s:submit cssClass="buttonGeneric" id='claim_reprocess' value="%{getText('button.common.refresh')}" onclick="hideReprocessButton()"></s:submit></div>
</div>

 <script type="text/javascript">
     function hideReprocessButton(){
         //hide dialog box
         dijit.byId("claim_reprocess_dialog").hide();
         var frm = document.getElementById('processorReopen');
         frm.action="viewQuickClaimSearchDetail.action?claimNumber=<s:property value='claim.claimNumber'/>&context='ClaimSearches'";
         frm.submit();
     }
 </script>