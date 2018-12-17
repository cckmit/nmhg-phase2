<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@ taglib prefix="t" uri="twms"%>


<s:if test="!authorizeAllowedAction">
<script type="text/javascript">
	// For All the default actions
	dojo.addOnLoad(function(){		
		var idsPresent=["radioReviewCP","onHoldRadio","forwardToDealerRadio","radioSeekAdvice",
						"radioTransfer","denyRadio","acceptRadio","reprocessRadio","approveAndTransferRadio","sendForLateFeeApproval"];
		
		var idToHide=[
"putOnHold","reqInfFromDealer","reqAdvice","transferTo","deny","accept","reProcess","approveAndTransfer"];
		for(var i=0;i<idsPresent.length;i++){
            var radio = dojo.byId(idsPresent[i]);
            if (radio) {
                dojo.connect(radio, "onclick", function(event) {
                    if (event.target.checked) {
                        showEnabledFields(event.target.id);
                    }
                });
            }
		}
		dojo.byId("lateFeeApproval").style.display='none'; 
		//To hide All default action		
		if(dojo.byId("lateFeeChanged") && dojo.byId("lateFeeChanged").value=="true")
			{	
		for(var i=0;i<idToHide.length;i++){
            var radio = dojo.byId(idToHide[i]);
            if (radio) {            	
                       hideRadio(radio);                               
            }		
		}
		dojo.byId("lateFeeApproval").style.display=''; 
		}
		
	});
</script>
</s:if>
<s:else>
<script>
// For All the actions based on the Authority rule 
// Please refer to UserClusterService.java for the values of allowed actions 
dojo.addOnLoad(function(){	
  	var idsPresent = ["onHoldRadio","forwardToDealerRadio","reprocessRadio","approveAndTransferRadio"];
    var iter = idsPresent.length;
	<s:if test="actionAllowedForUser('Request for advice')">
		// Service Managers
		dijit.byId("serviceManagers").setDisabled(true);
        if (serviceManagersLoaded) {
		    dijit.byId("serviceManagers").setDisplayedValue("--Select--");
        }
		idsPresent[iter] = "radioSeekAdvice";
    	iter = iter + 1;
	</s:if>
	<s:if test="actionAllowedForUser('Transfer to')">
	// TO-DO
		// Processors
	if (dijit.byId("processors"))
	{
		dijit.byId("processors").setDisabled(true);
        if (processorsLoaded) {
		    dijit.byId("processors").setDisplayedValue("--Select--");
        }
	}
		idsPresent[iter] = "radioTransfer";
    	iter = iter + 1;
	</s:if>
	
	<s:if test="actionAllowedForUser('Send for late fee approval')">
		if (dijit.byId("warrantySupervisors"))
	{
		dijit.byId("warrantySupervisors").setDisabled(true);
        if (warrantySupervisorsLoaded) {
		    dijit.byId("warrantySupervisors").setDisplayedValue("--Select--");
        }
	}
		idsPresent[iter] = "sendForLateFeeApproval";
    	iter = iter + 1;
	</s:if>
	<s:if test="actionAllowedForUser('Accept') ">
		// Acceptance Reason
		dijit.byId("acceptanceReason").setDisabled(true);
		dijit.byId("acceptanceReason").setDisplayedValue("--Select--");
		idsPresent[iter] = "acceptRadio";
        iter = iter + 1;
	</s:if>
	<s:if test="actionAllowedForUser('Deny')">
		// Rejection Reason
		dojo.byId("rejectionReason").disabled = true;	
		dojo.byId("rejectionReason").value="";
		idsPresent[iter] = "denyRadio";
        iter = iter + 1;
	</s:if>
	<s:if test="actionAllowedForUser('Request review from cp')">
		// CP Advisors
		if(dijit.byId("reviewCPAdvisor") != null){
			dijit.byId("reviewCPAdvisor").setDisabled(true);
			dijit.byId("reviewCPAdvisor").setDisplayedValue("--Select--");
			idsPresent[iter] = "radioReviewCP";
            iter = iter + 1;
		}	
	</s:if>
	
	for(var i=0;i<idsPresent.length-1;i++){
		dojo.connect(dojo.byId(idsPresent[i]), "onclick", function(event) {
			if(event.target.checked){
				showEnabledFields(event.target.id);
			}
   		});
		}	
	
});
</script>
</s:else>

<script type="text/javascript">

function hideRadio(radio)
{	
	radio.style.display='none';
	//hideFields()
}
	
 function hideFields(){
	var allIds=["reviewCPAdvisor","serviceManagers","processors","acceptanceReason","rejectionReason","acceptanceReasonForCP"];
	//To hide All default action
	for(var i=0;i<allIds.length;i++){
        var element = dojo.byId(allIds[i]);
        if (element) {            	
        	element.style.display='none';                         
        }
	}	
} 
	
	function setSelectedValuesIntoDropDown(src){
		var selectedValues =  document.getElementById("selectedReasons").value.split(",");                                        
        for(var count= src.options.length-1; count >= 0; count--) {                                                                   
            if(selectedValues.indexOf(src.options[count].value)  != "-1") {                               
           	 src.options[count].selected=true;                                    	 
            }
            if(src.options[count].value ==""){
           	 src.options[count].selected=false;
            }
        }
	}
	
	function showEnabledFields(selectedId){
		var allIds=["radioReviewCP","radioSeekAdvice","radioTransfer","denyRadio","acceptRadio","sendForLateFeeApproval"];
				
				if(dijit.byId("reviewCPAdvisor") != null){				
					dijit.byId("reviewCPAdvisor").setDisabled(true);
					dijit.byId("reviewCPAdvisor").setDisplayedValue("--Select--");
				}
				
				if(dijit.byId("serviceManagers") != null){				
					dijit.byId("serviceManagers").setDisabled(true);
                    if (serviceManagersLoaded) {
                        dijit.byId("serviceManagers").setDisplayedValue("--Select--");
                    }
				}
				if(dijit.byId("processors") != null){		
					dijit.byId("processors").setDisabled(true);
                    if (processorsLoaded) {
                        dijit.byId("processors").setDisplayedValue("--Select--");
                    }
				}
				if(dijit.byId("warrantySupervisors") != null){		
					dijit.byId("warrantySupervisors").setDisabled(true);
                    if (warrantySupervisorsLoaded) {
                        dijit.byId("warrantySupervisors").setDisplayedValue("--Select--");
                    }
				}
				if(dojo.byId("rejectionReason") != null){									
					dojo.byId("rejectionReason").disabled = true;		
					dojo.byId("rejectionReason").value="";
				}
				
				if(dijit.byId("acceptanceReasonForCP")!=null){
					dijit.byId("acceptanceReasonForCP").setDisabled(true);
					dijit.byId("acceptanceReasonForCP").setDisplayedValue("--Select--");
				}
				if(dojo.byId("requestInfo")!=null){										
					dojo.byId("requestInfo").disabled = true;
					dojo.byId("requestInfo").value="";
				}
				if(dojo.byId("putOnHoldReason")!=null){		
					dojo.byId('putOnHoldReason').value = "";					
					dojo.byId("putOnHoldReason").disabled = true;		
					
				}
				if(dojo.byId("rejectionReason")!=null){										
					dojo.byId("rejectionReason").disabled = true;
					dojo.byId("rejectionReason").value="";
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='radioReviewCP'){
					dijit.byId("reviewCPAdvisor").setDisabled(false);
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='onHoldRadio'){				
					dojo.byId("putOnHoldReason").disabled = false;													                           
                    var src = document.getElementById("putOnHoldReason");
                    setSelectedValuesIntoDropDown(src);                                            
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='forwardToDealerRadio'){		
					<s:if test="requestInfoFromDealerList.size()>0"> 					                            
                    var src = document.getElementById("requestInfo");
                    setSelectedValuesIntoDropDown(src);
                    </s:if>
					dojo.byId("requestInfo").disabled = false;
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='radioSeekAdvice'){
					dijit.byId("serviceManagers").setDisabled(false);
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='radioTransfer'){
					dijit.byId("processors").setDisabled(false);
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='sendForLateFeeApproval'){
					dijit.byId("warrantySupervisors").setDisabled(false);
				}
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='denyRadio'){					
					dojo.byId("rejectionReason").disabled = false;					
					 <s:if test="rejectionReasonsList.size()>0">					                      
                    var src = document.getElementById("rejectionReason");
                    setSelectedValuesIntoDropDown(src);
                    </s:if>
					<s:if test = "defaultRejectionReason != null">
					  dojo.byId("rejectionReason").value = "<s:property value="defaultRejectionReason.id"/>";
				    </s:if>
				}
				
				if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='acceptRadio'){
										
					if(dijit.byId("acceptanceReason").disabled){
                        dijit.byId("acceptanceReason").setDisabled(false);
                        <s:if test = "defaultAcceptanceReason != null">
                            dijit.byId("acceptanceReason").setValue(("<s:property value="defaultAcceptanceReason"/>"));
                        </s:if>					
					}					
				}
				else
				{
					if(dijit.byId("acceptanceReason") !=null){ 
						dijit.byId("acceptanceReason").setDisabled(true);
					 	dijit.byId("acceptanceReason").setDisplayedValue("--Select--");		
					}		
				}
				
				if(dijit.byId("acceptanceReasonForCP")!=null){
					if(dojo.indexOf(allIds,selectedId)!=-1 && selectedId=='acceptRadio'){
						dijit.byId("acceptanceReasonForCP").setDisabled(false);
					}
				}
				if(document.getElementById('nextLOAProcessorId') !=null){ 
				    if(selectedId!='approveAndTransferRadio'){ 
				         document.getElementById('nextLOAProcessorId').disabled=true;   
				   }else { 
				         document.getElementById('nextLOAProcessorId').disabled=false; 
				      } 
				} 		
				
	}
	</script>
<s:hidden name="selectedReasons" id="selectedReasons"/>
<s:if test="authorizeAllowedAction">
	<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.viewClaim.loaDecision"/>"
			id="userLoaMessages1" labelNodeClass="section_header">
			<jsp:include flush="true"
				page="../../common/read/userLOAMessages.jsp" />
		</div>
	</div>
</s:if>

<table width="96%" class="grid">
	<s:if
		test="(task.claim.state.state=='Transferred') && (task.claim.loaScheme!=null) && findLOASchemesForUser().size()>0">
		<tr>
			<td class="labelStyle" colspan="2"><s:text
					name="label.loaScheme" /></td>
			<td><s:if test="findLOASchemesForUser().size()>1">
					<s:select id="loaScheme" name="task.claim.loaScheme"
						cssClass="processor_decesion" list="findLOASchemesForUser()"
						theme="twms" listKey="id" listValue="code" value=""
						cssStyle="width:200px;" headerKey="-1" onchange="resetLoa()"
						headerValue="%{getText('label.common.selectHeader')}">
						<script type="text/javascript">																
						function resetLoa(){
							resetNewLOAProcessor();
						}
				</script>
					</s:select>
				</s:if> <s:else>
					<s:select id="loaScheme" name="task.claim.loaScheme"
						cssClass="processor_decesion" list="findLOASchemesForUser()"
						theme="twms" listKey="id" listValue="code" value=""
						cssStyle="width:200px;">
						<script type="text/javascript">					
						dojo.addOnLoad(function(){	
							if(!dojo.byId("lateFeeChanged")){
							var loaSchemeSelect = dijit.byId('loaScheme');
							loaSchemeSelect.fireOnLoadOnChange=true;
							dojo.connect(loaSchemeSelect,"onChange",function(){									
								resetNewLOAProcessor();
								});
							}
						});
				</script>
					</s:select>
				</s:else>
			</td>

			<s:hidden name="loaSchemeApplicable" value="true" />
		</tr>
		<script type="text/javascript">
            function resetNewLOAProcessor(){                
                  var url = 'resetNewLOAProcessor.action?task.claim=<s:property value="task.claim"/>';              
                  //var approveAndTransfer = dijit.byId("approve_and_transfer");
            	  //approveAndTransfer.domNode.innerHTML = "<center>Loading...</center>";
            	   var approveAndTransfer =dijit.byId("approve_and_transfer"); 
            	   if(dijit.byId("loaScheme").getValue()!="-1"){
            	   approveAndTransfer.domNode.innerHTML = "<center>Loading...</center>";
                  twms.ajax.fireHtmlRequest(url, {"task.claim.loaScheme": dijit.byId("loaScheme").getValue()}, function(data) {
                	//TODO: Replace this...
                	  approveAndTransfer.setContent(data);
                	  showEnabledFields("loaScheme");
                    }); 
            	   }
            }                  
      </script>
	</s:if>
	
	<%-- Default actions - Start --%>
	<s:if test="!authorizeAllowedAction">
		<!-- Dafault actions -->

		<s:if test="isCPAdvisorEnabled()">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Seek Review')">
						<input id="radioReviewCP" checked="checked" type="radio"
							name="task.takenTransition" value="Seek Review"
							class="processor_decesion" />
						<script type="text/javascript">
                dojo.addOnLoad(function(){
                	dijit.byId("reviewCPAdvisor").setDisabled(false);
                });
                </script>
					</s:if> <s:else>
						<input id="radioReviewCP" type="radio" name="task.takenTransition"
							value="Seek Review" class="processor_decesion" />
					</s:else>
				</td>
				<td class="labelStyle"><s:text
						name="label.newClaim.sendToCPAdvisor" />
				</td>
				<td class="labelNormalTop" colspan="2"><s:select
						id="reviewCPAdvisor" list="CPAdvisor" disabled="true" theme="twms"
						name="task.seekReviewFrom" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
				</td>
			</tr>
		</s:if>
		<tr id="putOnHold">
			<td class="labelStyle"><s:if
					test="task != null && task.takenTransition != null && task.takenTransition.equals('Hold')">
					<input type="radio" checked="checked" name="task.takenTransition"
						value="Hold" class="processor_decesion" id="onHoldRadio" />
				</s:if> <s:else>
					<input type="radio" name="task.takenTransition" value="Hold"
						class="processor_decesion" id="onHoldRadio" />
				</s:else>
			</td>
			<td class="labelStyle" ><s:text
					name="label.newClaim.putOnHold" />
			</td>
			<td>
				<s:select id="putOnHoldReason" name="putOnHoldReasonList"
					cssClass="processor_decesion"  value="putOnHoldReasonList"
					list="getLovsForClass('PutOnHoldReason',task.claim)"
					disabled="true" theme="simple" listKey="id" listValue="description"
					cssStyle="width:400px;" headerKey=""
					headerValue="%{getText('label.common.selectHeader')}" multiple="true"/>
					<script type="text/javascript">
                    dojo.addOnLoad(function() {                    	                       
                    	 <s:if test="putOnHoldReasonList.size()>0">                    	 
                 	 			dojo.byId("putOnHoldReason").disabled = false;                 	 			                  	 			                             
                                var src = document.getElementById("putOnHoldReason");
                                setSelectedValuesIntoDropDown(src);
                 	 			document.getElementById('onHoldRadio').checked = true;  
                 	 			                 	 			
 		        	 	</s:if>	 		        	
                        dojo.connect(dojo.byId("onHoldRadio"), "onclick", function() {
                        	 <s:if test="putOnHoldReasonList.size()>0">                        	 
	                        	 dojo.byId("putOnHoldReason").disabled = false;	                        	               	                        	                       
                                 var src = document.getElementById("putOnHoldReason");
                                 setSelectedValuesIntoDropDown(src);
	                        	 document.getElementById('onHoldRadio').checked = true;	 
	                        	 
        		        	 </s:if>
        		        	 <s:else>
	        		        	 dojo.byId("putOnHoldReason").disabled = false;	 
	        		        	 <s:if test="putOnHoldReasonList.size()>0"> 	        		        	                          
	                                var src = document.getElementById("putOnHoldReason");
	                                setSelectedValuesIntoDropDown(src);
	                                </s:if>
        		        	 </s:else>                        	
                        });
                    });
                </script>
			</td>
		</tr>
		<tr id="reqInfFromDealer">
			<td class="labelStyle"><s:if
					test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Forward to Dealer')">
					<input type="radio" checked="checked" name="task.takenTransition"
						value="Forward to Dealer" class="processor_decesion"
						id="forwardToDealerRadio" />
				</s:if> <s:else>
					<input type="radio" name="task.takenTransition"
						value="Forward to Dealer" class="processor_decesion"
						id="forwardToDealerRadio" />
				</s:else>
			</td>
			<td class="labelStyle"><s:text
					name="label.newClaim.requestInfo" />
			</td>
			<td>
				<s:select id="requestInfo" name="requestInfoFromDealerList"
					cssClass="processor_decesion" value="requestInfoFromDealerList"
					list="getLovsForClass('RequestInfoFromUser',task.claim)"
					disabled="true" theme="simple" listKey="id" listValue="description"
					cssStyle="width:400px;" headerKey=""
					headerValue="%{getText('label.common.selectHeader')}" multiple="true"/>
					<script type="text/javascript">
                    dojo.addOnLoad(function() {
                    	 <s:if test="requestInfoFromDealerList.size()>0">                    	 
                    	 	dojo.byId("requestInfo").disabled = false;                    	 	                        
                            var src = document.getElementById("requestInfo");
                            setSelectedValuesIntoDropDown(src);
                    	 	document.getElementById('forwardToDealerRadio').checked = true;            	 
    		        	 </s:if>
    		        	 <s:else>
    		        	 	dojo.byId("requestInfo").disabled = true;    		        	 	
    		        	 </s:else>
                        dojo.connect(dojo.byId("forwardToDealerRadio"), "onclick", function() {                        	
                        	<s:if test="requestInfoFromDealerList.size()>0"> 
                            var src = document.getElementById("requestInfo");
                            setSelectedValuesIntoDropDown(src);
                            </s:if>
                        	dojo.byId("requestInfo").disabled = false;                        	
                        });
                    });
                </script>
			</td>
		</tr>
		<tr id="reqAdvice">
			<td class="labelNormal">
				
					<input id="radioSeekAdvice" type="radio"
						name="task.takenTransition" value="Seek Advice"
						class="processor_decesion" />
				
			</td>
			<td class="labelStyle"><s:text
					name="label.newClaim.requestAdvice" />
			</td>
			<td class="labelNormalTop" colspan="2"><sd:autocompleter id='serviceManagers' cssStyle='width:400px;' name='task.seekAdviceFrom' disabled='true' value='--Select--' listenTopics='/serviceManagers/reload' /> <img
				style="display: none;" id="loading_service_managers_indicator"
				class="indicator" src="image/indicator.gif" alt="Loading..." /> <script
					type="text/javascript">
                    var serviceManagersLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("radioSeekAdvice"), "onclick", function() {
                            if (!serviceManagersLoaded) {
                                dojo.style(dojo.byId("loading_service_managers_indicator"), "display", "");
                                dojo.publish("/serviceManagers/reload", [{
                                    url: "list_service_managers.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                            dojo.style(dojo.byId("loading_service_managers_indicator"), "display", "none");
                            dijit.byId("serviceManagers").setDisplayedValue("--Select--");
                            serviceManagersLoaded = true;
                        });
                       /*  dojo.connect(dijit.byId("serviceManagers"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_service_managers_indicator"), "display", "none");
                            dijit.byId("serviceManagers").setDisplayedValue("--Select--");
                            serviceManagersLoaded = true;
                        }); */
                    });
                     document.getElementById('radioSeekAdvice').checked = false;
                </script>
			</td>
		</tr>
		<tr id="transferTo">
			<td class="labelStyle">
					<input id="radioTransfer" type="radio" name="task.takenTransition"
						value="Transfer" class="processor_decesion" />
				
			</td>
			<td class="labelStyle"><s:text name="label.newClaim.transferTo" />
			</td>
			<td class="labelNormalTop" colspan="2"><sd:autocompleter id='processors' cssStyle='width:400px;' name='task.transferTo' disabled='true' value='--Select--' listenTopics='/processors/reload' /> <img style="display: none;"
				id="loading_processors_indicator" class="indicator"
				src="image/indicator.gif" alt="Loading..." /> <script
					type="text/javascript">
                    var processorsLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("radioTransfer"), "onclick", function() {
                            if (!processorsLoaded) {
                                dojo.style(dojo.byId("loading_processors_indicator"), "display", "");
                                dojo.publish("/processors/reload", [{
                                    url: "list_processors.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                            dijit.byId("processors").setDisplayedValue("--Select--");
                            processorsLoaded = true;
                        });
                        /* dojo.connect(dijit.byId("processors"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                            dijit.byId("processors").setDisplayedValue("--Select--");
                            processorsLoaded = true;
                        }); */
                    });
                      document.getElementById('radioTransfer').checked = false;
                </script>
			</td>
		</tr>
		<tr id="deny">
			<td class="labelStyle"><s:if
					test="task != null && task.takenTransition != null && task.takenTransition.equals('Deny')">
					<input type="radio"  name="task.takenTransition"
						value="Deny" class="processor_decesion" id="denyRadio" />
					<script type="text/javascript">
		         dojo.addOnLoad(function(){
		     		<s:if test="task.claim.acceptanceReason !=null">
					dijit.byId('acceptanceReason').setValue("<s:property value="task.claim.acceptanceReason.id"/>");							              
				</s:if>
				 <s:if test="rejectionReasonsList.size()>0">				                           
                var src = document.getElementById("acceptanceReason");
                setSelectedValuesIntoDropDown(src);
                </s:if>
				dojo.byId("rejectionReason").disabled = false;		           	
		         });
		         </script>
				</s:if> <s:else>
					<input type="radio" name="task.takenTransition" value="Deny"
						class="processor_decesion" id="denyRadio" />
				</s:else>
			</td>
			<td class="labelStyle"><s:text name="label.newClaim.deny" />
			</td>
			<td class="labelNormalTop" colspan="2"><s:select
					id="rejectionReason" name="rejectionReasonsList" value="rejectionReasonsList"
					cssClass="processor_decesion"
					list="getLovsForClass('RejectionReason',task.claim)"
					disabled="true" theme="simple" listKey="id" listValue="description"
					cssStyle="width:400px;" headerKey="" multiple="true"
					headerValue="%{getText('label.common.selectHeader')}" />
					<script type="text/javascript">
                    dojo.addOnLoad(function() {                    	 
                    	 <s:if test="rejectionReasonsList.size()>0">
                    		dojo.byId("rejectionReason").disabled = false;    
                    		var src = document.getElementById("rejectionReason");
                    		setSelectedValuesIntoDropDown(src);                    		
                     	 	document.getElementById('denyRadio').checked = true;            	 
    		        	 </s:if>
                        dojo.connect(dojo.byId("denyRadio"), "onclick", function() {
                        	 <s:if test="rejectionReasonsList.size()>0">                        	                            
                            var src = document.getElementById("rejectionReason");
                            setSelectedValuesIntoDropDown(src);
                            </s:if>
                        	dojo.byId("rejectionReason").disabled = false;                        	
                        });
                    });
                </script>
			</td>
		</tr>
		<tr id="accept">
			<td class="labelStyle"><s:if
					test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Accept')">
					<input type="radio"  name="task.takenTransition"
						value="Accept" class="processor_decesion" id="acceptRadio" />
					<script type="text/javascript">
            				 dojo.addOnLoad(function(){
            						<s:if test="task.claim.rejectionReason !=null">
            						dojo.byId('rejectionReason').value = <s:property value="task.claim.rejectionReason.id"/>;							              
            					</s:if>
             					dijit.byId("acceptanceReason").setDisabled(false);
             				});
             			</script>
				</s:if> <s:else>
					<input type="radio" name="task.takenTransition" value="Accept"
						class="processor_decesion" id="acceptRadio" />
				</s:else>
			</td>
			<td class="labelStyle" style="padding-right: 140px;"><s:text
					name="label.common.accept" />
			</td>
			<td class="labelNormalTop" colspan="2"><s:if
					test="isCPAdvisorEnabled()">
					<div id="settlementCodeLabel">
						<s:text name="label.newClaim.settlementCode.for.warranty" />:
					</div>
				</s:if>
				<s:select id="acceptanceReason" name="task.claim.acceptanceReason"
					cssClass="processor_decesion"
					list="getLovsForClass('AcceptanceReason',task.claim)"
					disabled="true" theme="twms" listKey="code" listValue="description"
					cssStyle="width:400px;" headerKey=""
					headerValue="%{getText('label.common.selectHeader')}" /><br /> <s:if
					test="isCPAdvisorEnabled()">
						<div id="cpAcceptanceReason">
						<s:text name="label.newClaim.settlementCode.for.cp" />:<br>
						<s:select id="acceptanceReasonForCP"
							name="task.claim.acceptanceReasonForCp"
							cssClass="processor_decesion"
							list="getLovsForClass('AcceptanceReasonForCP',task.claim)"
							disabled="true" listKey="code" listValue="description"
							cssStyle="width:400px;" theme="twms" headerKey=""
							headerValue="%{getText('label.common.selectHeader')}" />
						</div>
				</s:if>
			</td>
		</tr>
		<tr id="reProcess">
			<td class="labelStyle"><s:if
					test="task != null && task.takenTransition != null && task.takenTransition.equals('Re-process')">
					<input type="radio" checked="checked" name="task.takenTransition"
						value="Re-process" class="processor_decesion" id="reprocessRadio" />
				</s:if> <s:else>
					<input type="radio" name="task.takenTransition" value="Re-process"
						class="processor_decesion" id="reprocessRadio" />
				</s:else>
			</td>
			<td class="labelStyle" colspan="3"><s:text
					name="label.newClaim.reprocess" />
			</td>
		</tr>
		<tr id="approveAndTransfer">
			<td colspan="12" width="100%" style="align: left;">
				<div dojoType="dijit.layout.ContentPane" id='approve_and_transfer'
					style="display: table; width: 100%; white-space: nowrap; border: 0px;">
					<jsp:include flush="true" page="approve_and_transfer.jsp" />
				</div>
            </td>
		</tr>
		
		<tr id="lateFeeApproval">
			<td class="labelStyle">
					<input id="sendForLateFeeApproval" type="radio" name="task.takenTransition"
						value="Transfer" class="processor_decesion" />
				
			</td>
			<td class="labelStyle"><s:text name="label.newClaim.sendForLateFeeApproval" />
			</td>
			<td class="labelNormalTop" colspan="2"><sd:autocompleter id='warrantySupervisors' cssStyle='width:400px;' name='task.transferTo' disabled='true' value='--Select--' listenTopics='/warrantySupervisors/reload' /> <img style="display: none;"
				id="loading_processors_indicator" class="indicator"
				src="image/indicator.gif" alt="Loading..." /> <script
					type="text/javascript">
                    var warrantySupervisorsLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("sendForLateFeeApproval"), "onclick", function() {
                            if (!warrantySupervisorsLoaded) {
                                dojo.style(dojo.byId("loading_processors_indicator"), "display", "");
                                dojo.publish("/warrantySupervisors/reload", [{
                                    url: "list_warranty_supervisors.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                            dijit.byId("warrantySupervisors").setDisplayedValue("--Select--");
                            warrantySupervisorsLoaded = true;
                        });
                        /* dojo.connect(dijit.byId("processors"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                            dijit.byId("processors").setDisplayedValue("--Select--");
                            processorsLoaded = true;
                        }); */
                    });
                      document.getElementById('sendForLateFeeApproval').checked = false;
                </script>
			</td>
		</tr>
		
	</s:if>
	<%-- Default actions - End --%>
	<s:else>
		<%-- Allowed actions Based on Rules - Start --%>
		<!-- Allowed actions Based on Rules -->
		<!-- Please refer to UserClusterService.java for the values of allowed actions -->
		<s:if test="actionAllowedForUser('Request review from cp')">
			<s:if test="isCPAdvisorEnabled()">
				<tr>
					<td class="labelStyle"><s:if
							test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Seek Review')">
							<input id="radioReviewCP" checked="checked" type="radio"
								name="task.takenTransition" value="Seek Review"
								class="processor_decesion" />
							<script type="text/javascript">
					dojo.addOnLoad(function(){
						dijit.byId("reviewCPAdvisor").setDisabled(false);
					});
					</script>
						</s:if> <s:else>
							<input id="radioReviewCP" type="radio"
								name="task.takenTransition" value="Seek Review"
								class="processor_decesion" />
						</s:else>
					</td>
					<td class="labelStyle"><s:text
							name="label.newClaim.sendToCPAdvisor" />
					</td>
					<td class="labelNormalTop" colspan="2"><s:select
							id="reviewCPAdvisor" list="CPAdvisor" disabled="true"
							cssStyle="width:400px;" theme="twms" name="task.seekReviewFrom"
							headerKey=""
							headerValue="%{getText('label.common.selectHeader')}" />
					</td>
				</tr>
			</s:if>
		</s:if>
		<s:if test="actionAllowedForUser('Put on hold')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null && task.takenTransition.equals('Hold')">
						<input type="radio" checked="checked" name="task.takenTransition"
							value="Hold" class="processor_decesion" id="onHoldRadio" />
					</s:if> <s:else>
						<input type="radio" name="task.takenTransition" value="Hold"
							class="processor_decesion" id="onHoldRadio" />
					</s:else>
				</td>
				<td class="labelStyle" colspan="3"><s:text
						name="label.newClaim.putOnHold" />
				</td>
			</tr>
		</s:if>
		<s:if test="actionAllowedForUser('Forwarded To Dealer')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Forward to Dealer')">
						<input type="radio" checked="checked" name="task.takenTransition"
							value="Forward to Dealer" class="processor_decesion"
							id="forwardToDealerRadio" />
					</s:if> <s:else>
						<input type="radio" name="task.takenTransition"
							value="Forward to Dealer" class="processor_decesion"
							id="forwardToDealerRadio" />
					</s:else>
				</td>
				<td class="labelStyle" colspan="3"><s:text
						name="label.newClaim.requestInfo" />
				</td>
			</tr>
		</s:if>
		<s:if test="actionAllowedForUser('Request for advice')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Seek Advice')">
						<input id="radioSeekAdvice" checked="checked" type="radio"
							name="task.takenTransition" value="Seek Advice"
							class="processor_decesion" />
						<script type="text/javascript">
			            dojo.addOnLoad(function(){
			            	dijit.byId("serviceManagers").setDisabled(false);
			            });
			            </script>
					</s:if> <s:else>
						<input id="radioSeekAdvice" type="radio"
							name="task.takenTransition" value="Seek Advice"
							class="processor_decesion" />
					</s:else>
				</td>
				<td class="labelStyle"><s:text
						name="label.newClaim.requestAdvice" />
				</td>
				<td class="labelNormalTop" colspan="2"><sd:autocompleter id='serviceManagers' cssStyle='width:400px;' name='task.seekAdviceFrom' disabled='true' value='--Select--' listenTopics='/serviceManagers/reload' /> <img
					style="display: none;" id="loading_service_managers_indicator"
					class="indicator" src="image/indicator.gif" alt="Loading..." /> <script
						type="text/javascript">
                    var serviceManagersLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("radioSeekAdvice"), "onclick", function() {
                            if (!serviceManagersLoaded) {
                                dojo.style(dojo.byId("loading_service_managers_indicator"), "display", "");
                                dojo.publish("/serviceManagers/reload", [{
                                    url: "list_service_managers.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                        });
                        dojo.connect(dijit.byId("serviceManagers"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_service_managers_indicator"), "display", "none");
                            dijit.byId("serviceManagers").setDisplayedValue("--Select--");
                            serviceManagersLoaded = true;
                        });
                    });
                </script>
				</td>
			</tr>
		</s:if>
		<s:if test="actionAllowedForUser('Transfer to')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Transfer')">
						<input id="radioTransfer" checked="checked" type="radio"
							name="task.takenTransition" value="Transfer"
							class="processor_decesion" />
						<script type="text/javascript">
			            dojo.addOnLoad(function(){
			            	dijit.byId("processors").setDisabled(false);
			            });
			            </script>
					</s:if> <s:else>
						<input id="radioTransfer" type="radio" name="task.takenTransition"
							value="Transfer" class="processor_decesion" />
					</s:else>
				</td>
				<td class="labelStyle"><s:text name="label.newClaim.transferTo" />
				</td>
				<td class="labelNormalTop" colspan="2"><sd:autocompleter id='processors' cssStyle='width:400px;' name='task.transferTo' disabled='true' value='--Select--' listenTopics='/processors/reload' /> <img style="display: none;"
					id="loading_processors_indicator" class="indicator"
					src="image/indicator.gif" alt="Loading..." /> <script
						type="text/javascript">
                    var processorsLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("radioTransfer"), "onclick", function() {
                            if (!processorsLoaded) {
                                dojo.style(dojo.byId("loading_processors_indicator"), "display", "");
                                dojo.publish("/processors/reload", [{
                                    url: "list_processors.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                        });
                        dojo.connect(dijit.byId("processors"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                             processorsLoaded = true;
                        });
                    });
                </script>
				</td>
			</tr>
		</s:if>
		<s:if test="actionAllowedForUser('Deny')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null && task.takenTransition.equals('Deny')">
						<input type="radio" checked="checked" name="task.takenTransition"
							value="Deny" class="processor_decesion" id="denyRadio" />
						<script type="text/javascript">
			         dojo.addOnLoad(function(){			           	
			           	dojo.byId("rejectionReason").disabled = false;
			            <s:if test="rejectionReasonsList.size()>0">			           	                             
                        var src = document.getElementById("rejectionReason");
                        setSelectedValuesIntoDropDown(src);
                        </s:if>
			         });
			         </script>
					</s:if> <s:else>
						<input type="radio" name="task.takenTransition" value="Deny"
							class="processor_decesion" id="denyRadio" />
					</s:else>
				</td>
				<td class="labelStyle"><s:text name="label.newClaim.deny" />
				</td>
				<td class="labelNormalTop" colspan="2"><s:select
						id="rejectionReason" name="task.claim.rejectionReason"
						cssClass="processor_decesion"
						list="getLovsForClass('RejectionReason',task.claim)"
						disabled="true" theme="simple" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" multiple="true"/>
						<script type="text/javascript">
                    dojo.addOnLoad(function() {                    	                     	
                    	 <s:if test="rejectionReasonsList.size()>0">
                    		dojo.byId("rejectionReason").disabled = false;    
                     	 	document.getElementById('denyRadio').checked = true;            	 
    		        	 </s:if>
                        dojo.connect(dojo.byId("denyRadio"), "onclick", function() {
                        	dojo.byId("rejectionReason").disabled = false;                        	
                        });
                    });
                </script>
				</td>
			</tr>
		</s:if>
		<s:if test="actionAllowedForUser('Accept')">
			<tr id="acceptanceRadio">
				<td width="4%" class="labelNormal"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Accept')">
						<input type="radio" checked="checked" name="task.takenTransition"
							value="Accept" class="processor_decesion" id="acceptRadio" />
						<script type="text/javascript">
								 dojo.addOnLoad(function(){
									dijit.byId("acceptanceReason").setDisabled(false);
								});
							</script>
					</s:if> <s:else>
						<input type="radio" name="task.takenTransition" value="Accept"
							class="processor_decesion" id="acceptRadio" />
					</s:else>
				</td>
				<td width="16%" class="labelStyle" style="padding-right: 140px;">
					<s:text name="label.common.accept" />
				</td>
				<td width="40%" class="labelNormalTop" colspan="2"><s:if
						test="isCPAdvisorEnabled()">
						<s:if test="task.claim.cpReviewed == true">
							<s:text name="label.newClaim.settlementCode.for.warranty" />
						</s:if>
					</s:if> <s:select id="acceptanceReason" name="task.claim.acceptanceReason"
						cssClass="processor_decesion"
						list="getLovsForClass('AcceptanceReason',task.claim)"
						disabled="true" theme="twms" listKey="code"
						listValue="description" cssStyle="width:400px;" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" /><br /> <s:if
						test="isCPAdvisorEnabled()">
						<s:if test="task.claim.cpReviewed == true">
							<s:text name="label.newClaim.settlementCode.for.cp" />
							<s:select id="acceptanceReasonForCP"
								name="task.claim.acceptanceReasonForCp"
								cssClass="processor_decesion"
								list="getLovsForClass('AcceptanceReasonForCP',task.claim)"
								disabled="true" theme="twms" listKey="code"
								listValue="description" cssStyle="width:400px;" headerKey=""
								headerValue="%{getText('label.common.selectHeader')}" />
						</s:if>
					</s:if>
				</td>
			</tr>

		</s:if>
		<s:if test="actionAllowedForUser('Re-process')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null && task.takenTransition.equals('Re-process')">
						<input type="radio" checked="checked" name="task.takenTransition"
							value="Re-process" class="processor_decesion" id="reprocessRadio" />
					</s:if> <s:else>
						<input type="radio" name="task.takenTransition" value="Re-process"
							class="processor_decesion" id="reprocessRadio" />
					</s:else>
				</td>
				<td class="labelStyle" colspan="3"><s:text
						name="label.newClaim.reprocess" />
				</td>
			</tr>
		</s:if>
		<s:if test ="!isLoggedInUserEligibleForLateFeeApproval()">
			<s:if test="actionAllowedForUser('Send for late fee approval')">
			<tr>
				<td class="labelStyle"><s:if
						test="task != null && task.takenTransition != null &&  task.takenTransition.equals('Transfer')">
						<input id="sendForLateFeeApproval" checked="checked" type="radio"
							name="task.takenTransition" value="Transfer"
							class="processor_decesion" />
						<script type="text/javascript">
			            dojo.addOnLoad(function(){
			            	dijit.byId("warrantySupervisors").setDisabled(false);
			            });
			            </script>
					</s:if> <s:else>
						<input id="sendForLateFeeApproval" type="radio" name="task.takenTransition"
							value="Transfer" class="processor_decesion" />
					</s:else>
				</td>
				<td class="labelStyle"><s:text name="label.newClaim.sendForLateFeeApproval" />
				</td>
				<td class="labelNormalTop" colspan="2"><sd:autocompleter id='warrantySupervisors' cssStyle='width:400px;' name='task.transferTo' disabled='true' value='--Select--' listenTopics='/warrantySupervisors/reload' /> <img style="display: none;"
					id="loading_processors_indicator" class="indicator"
					src="image/indicator.gif" alt="Loading..." /> <script
						type="text/javascript">
                    var warrantySupervisorsLoaded = false;
                    dojo.addOnLoad(function() {
                        dojo.connect(dojo.byId("sendForLateFeeApproval"), "onclick", function() {
                            if (!warrantySupervisorsLoaded) {
                                dojo.style(dojo.byId("loading_processors_indicator"), "display", "");
                                dojo.publish("/warrantySupervisors/reload", [{
                                    url: "list_warranty_supervisors.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                    params: {
                                        id: '<s:property value="task.taskId"/>'
                                    },
                                    makeLocal: true,
                                    initialValue: '--Select--'
                                }]);
                            }
                        });
                        dojo.connect(dijit.byId("warrantySupervisors"), "onDataLoad", function() {
                            dojo.style(dojo.byId("loading_processors_indicator"), "display", "none");
                            warrantySupervisorsLoaded = true;
                        });
                    });
                </script>
				</td>
			</tr>
		</s:if>
	</s:if>

	</s:else>
	<%-- Allowed actions Based on Rules - Start --%>

</table>
