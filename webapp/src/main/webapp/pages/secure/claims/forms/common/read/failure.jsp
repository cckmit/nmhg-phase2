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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<style type="text/css">
    td#launchFaultCodeTree {
        background-image: url(image/searchIcon.gif);
        cursor: pointer;
        background-repeat: no-repeat;
        width : 16px;
        height : 16px;
        margin : 0px;
        padding : 0px;
        border : 0px;
    }
#test iframe { 
width: 1004px; 
height: 790px; 
border: none; } 

#test { 
width:630px; 
height: 300px; 
padding: 0; 
border: inset 1px gray; 
overflow: auto; }	
	
	/*The other styling stuff is comming from serviceProcedure.jsp*/
</style>
<script type="text/javascript">
    function displayFailureReport(dataId, failureReportName) {
        var claimId = '<s:property value="claim.id" />';
        var url = "show_failure_report.action?claim="+claimId;
        if (dataId) {
            url += "&customReportAnswer=" + dataId;
        }
        var failure_report = '<s:text name="label.common.failureReport"/>';
		var tabLabel = failure_report + "-" + failureReportName;		
        parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel()});

        delete url,failure_report,tabLabel;
    }
</script>
<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<table class="grid" cellspacing="0" cellpadding="0">
	<tr>
		<td width="17%" class="labelStyle" nowrap="nowrap">
			<s:text name="label.common.causalPartNumber" />:
		</td>		   
		<td>
			<s:property value="%{claim.serviceInformation.causalBrandPart.itemNumber}" />
		</td>
		<td  width="17%" class="labelStyle" nowrap="nowrap" >
			<s:text name="label.common.causalPartDescription" />:
		</td>
		<td>
			<s:property value="%{getOEMPartCrossRefForDisplay(claim.serviceInformation.causalPart, claim.serviceInformation.oemDealerCausalPart, false, claim.forDealer)}" />
		</td>
	
	<td nowrap="nowrap">
		<a cssStyle="cursor:pointer" href="#">
       				 <span id ="causalPartAttributeLink">
        			</span>
        </a>
	</td>
	</tr>
	<s:if test="!partsClaim || (claim.partInstalled && (claim.competitorModelBrand == null || claim.competitorModelBrand.isEmpty()))">
		<tr>
			<td class="labelStyle" nowrap="nowrap">
				<s:text name="label.common.faultCode" />:
			</td>
			<td>
				<span title="<s:property value="claim.serviceInformation.faultCodeDescription" />">
					<s:property value="claim.serviceInformation.getFaultCodeDescription()" /> (<s:property value="claim.serviceInformation.faultCode" />)
				</span>
			</td>
			
			<td nowrap="nowrap">
				 <s:a cssStyle="cursor:pointer" href="#">
       				 <span id ="fautCodeAttributes">
        			</span>
        		</s:a>
			</td>
		</tr>
	
		<tr>
			<td class="labelStyle" nowrap="nowrap">
				<s:text name="label.common.faultFound" />:
			</td>
			<td>
				<s:property value="claim.serviceInformation.faultFound.name" />
			</td>
			<td class="labelStyle">
				<s:text name="label.newClaim.causedBy" />:
			</td>
			<td>
				<s:property value="claim.serviceInformation.causedBy.name" />
			</td>
			</tr>
			<tr>
			<s:if test="isRootCauseAllowed()">			
				<td class="labelStyle" nowrap="nowrap" >
					<s:text name="label.common.rootCause" />:
				</td>
				<td nowrap="nowrap">
					<s:if test="claim.serviceInformation.rootCause!=null">
						<s:property value="claim.serviceInformation.rootCause.name" />
					</s:if>
					
					<span id ="viewMachineDiagram" class="alinkclickable">
						<s:text name="label.rootCauseFailure.viewMachineDiagram"/>
					</span>
			        <div id = "dialogBoxContainer" style="display: none">
				        <div dojoType="twms.widget.Dialog" title="<s:text name="title.rootCauseFailure.machineDiagramView"/>" 
				        id="browserForFailureRootCauseUrl" >
				           
				                 <div dojoType="dijit.layout.ContentPane" layoutAlign="top"  
				                	 id="machineUrlUserMessageDiv" >
									<div id="test">
				                 
									<iframe id="machineDiagramWindow"  name="machineDiagramWindow" scrolling="no"></iframe>
									</div></div>
				               
				          
				        </div>
			        </div> 
				</td>				
				
				<s:if test="claim.getItemReference().isSerialized()">
					<s:set name="machineUrl" value="claim.itemReference.referredInventoryItem.ofType.model.machineUrl"/>
					<script type="text/javascript">
						 function setMachineUrl() {
							 <s:if test="#machineUrl.trim().length() > 0">	
			    	            		document.getElementById('machineDiagramWindow').src = '<s:property value="#machineUrl"/>';
				    	     </s:if>
				    	     <s:else>
				    	            if(dojo.byId('machineUrlUserMessageDiv')!=null){
				    	            	dojo.byId('machineUrlUserMessageDiv').innerHTML = '<s:text name='text.rootCauseFailure.noMachineDiagram' />';
				    	            }
				    	      </s:else>
			    	     };  
		    	     </script>
				</s:if>
				<s:else>
					<s:set name="machineUrl" value="claim.itemReference.model.machineUrl"/>
					<script type="text/javascript">
						 function setMachineUrl() {
							 <s:if test="#machineUrl.trim().length() > 0">	
			    	            		document.getElementById('machineDiagramWindow').src = '<s:property value="#machineUrl"/>';
				    	     </s:if>
				    	     <s:else>
				    	            if(dojo.byId('machineUrlUserMessageDiv')!=null){
				    	            	dojo.byId('machineUrlUserMessageDiv').innerHTML = '<s:text name='text.rootCauseFailure.noMachineDiagram' />';
				    	            }
				    	      </s:else>
			    	      };  
		    	     </script>
				</s:else>
				
				<script type="text/javascript">
				 dojo.addOnLoad(function() {
						var machineDiagramDialog = dijit.byId("browserForFailureRootCauseUrl");
						var machineDiagramFetched = false;
			            dojo.connect(dojo.byId("viewMachineDiagram"), "onclick", function(event) {
			            	if(machineDiagramDialog)
			            	{
				            	if(!machineDiagramFetched) {
					            	setMachineUrl();
					            	machineDiagramFetched = true;
				            	}
			            		machineDiagramDialog.show();
			            	}	
			            });
		          });  
				</script>
			</s:if>	
		</tr>
        <s:if test="claim.serviceInformation.customReportAnswer.id!=null">
            <tr>
               <td class="labelStyle"><s:text name="label.common.failureReport"/></td>
               <td>
                   <span style="color:blue;text-decoration:underline;cursor:pointer;"
                         id="report_<s:property value="%{claim.serviceInformation.customReportAnswer.id}"/>">
                    <s:text name="home_jsp.menuBar.view"/>
                    <script type="text/javascript">
	                	dojo.addOnLoad(function() {
	                		var reportId = '<s:property value="%{claim.serviceInformation.customReportAnswer.id}"/>';
	                		var failureReportName = '<s:property value="%{claim.serviceInformation.customReportAnswer.customReport.name}"/>';
	                        dojo.connect(dojo.byId("report_"+reportId),"onclick",function(){
	                        	displayFailureReport(reportId, failureReportName);
	                        });
	                    });
	                </script>
                   </span>
                </td>
            </tr>
        </s:if>
    </s:if>
    
</table>
<div id="dialogBoxContainer" style="display: none">
	<div dojoType="twms.widget.Dialog" id="faultCodeAtrribute"
		bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250"
		style="width: 550px; height: 300px;"
		title="<s:text name="label.common.additionalAttributes"/>">
		<div
			style="width: 450px; height: 225px; background: #F3FBFE border:  1px solid #EFEBF7">
			<div dojoType="dijit.layout.ContentPane" id="attributeContentPane"
				layoutAlign="top" executeScripts="true">

				<div id="separator">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"
						style="background: #F3FBFE">
						<tbody>
							<s:iterator value="claim.serviceInformation.faultClaimAttributes"
								status="attribute">
								<tr>
									<td width="10%" class="label" nowrap="nowrap"><s:property
											value="attributes.name" /></td>
									<td width="10%" class="labelNormal" nowrap="nowrap"><s:if test="attrValue.length()>70" >
											<s:textarea theme="simple" value="%{attrValue}"
												readOnly="true" rows="6" cols="70" />
										</s:if> <s:else>
											<s:property value="attrValue" />
										</s:else></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td width="30%">&nbsp;</td>
							<td id="submitSection" align="left" class="buttons"
								style="padding-top: 20px;"><input type="button"
								id="closeFaultCodeAttrPopup"
								value='<s:text name="button.common.close"/>' /></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
        dojo.addOnLoad(function(){
			var faultCodeDiv = dijit.byId('faultCodeAtrribute');

			if(faultCodeDiv){
				faultCodeDiv.formNode=document.getElementById("claim_form");
				var faultClaimAttributesSize="<s:property value="claim.serviceInformation.faultClaimAttributes.size"/>";
				if(faultClaimAttributesSize > 0 ){
						dojo.byId("fautCodeAttributes").innerHTML='<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>'
						dojo.connect(dojo.byId("fautCodeAttributes"),"onclick",function(){
						dojo.publish("/fault/attribute/show");
					});
						dojo.subscribe("/fault/attribute/show", null, function() {
							dlg = faultCodeDiv;

						dlg.show();
					});
						 dojo.subscribe("/fault/attribute/hide", null, function() {
							faultCodeDiv.hide();
								});
						 dojo.connect(dojo.byId("closeFaultCodeAttrPopup"),"onclick",function() {
								dojo.publish("/fault/attribute/hide");
						});


					}
			}
		});
</script>

<s:iterator value="claim.serviceInformation.partClaimAttributes" status="attribute" >
<s:if test="!isAllRecoveryClaimsClosed() && attributes.attributePurpose.purpose=='Part Source Purpose' && isProcessorReview()">
<s:hidden id="additional_atrr_%{#attribute.index}" name="task.claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attrValue" value="" />
<s:hidden name="task.claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attributes" value="%{attributes.id}"/>
</s:if>
</s:iterator> 

<div id = "dialogBoxContainer" style="display: none"> 
	<div dojoType="twms.widget.Dialog" id="causalPartAtrribute" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250" style="width:550px; height:300px;"  title="<s:text name="label.common.additionalAttributes"/>">
		<div  style="width:450px; height:225px;background: #F3FBFE border: 1px solid #EFEBF7">
		 	<div dojoType="dijit.layout.ContentPane" id="causalPartAttributeContentPane1" layoutAlign="top" executeScripts="true">
		        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
		      		<tbody>
		      		
		      		<s:iterator value="claim.serviceInformation.partClaimAttributes" status="attribute" >

							<s:if
								test="!isAllRecoveryClaimsClosed() && attributes.attributePurpose.purpose=='Part Source Purpose' && isProcessorReview()">

								<tr>
									<td width="10%" class="label"><s:property
											value="attributes.name" /> <s:hidden
											name="claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attributes"
											value="%{attributes.id}"/></td>

									<td width="10%" class="labelNormal"><s:if
											test="attributes.attributeType.equals('Number') ||
                                                      attributes.attributeType.equals('Text') ">
											<s:textfield theme="simple"
												name="claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attrValue" value="%{attrValue}" id="additional_attribute_%{#attribute.index}" />
										</s:if> <s:elseif test="attributes.attributeType.equals('Text Area')">
											<t:textarea theme="simple"
												name="claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attrValue" value="%{attrValue}" id="additional_attribute_%{#attribute.index}" />
										</s:elseif> <s:elseif test="attributes.attributeType.equals('Date')">
											<sd:datetimepicker
												name="claim.serviceInformation.partClaimAttributes[%{#attribute.index}].attrValue" value="%{attrValue}" id="additional_attribute_%{#attribute.index}" />
										</s:elseif></td>
								</tr>
								<tr>
									<td height="5px">&nbsp;</td>
								</tr>


							</s:if>
							<s:else>
								<tr>
									<td width="10%" class="label"><s:property
											value="attributes.name" /></td>
									<td width="10%" class="labelNormal"><s:if
											test="attrValue.length()>70">
											<s:textarea theme="simple" value="%{attrValue}"
												readOnly="true" rows="6" cols="70"  />
										</s:if> <s:else>
											<s:property value="attrValue" />
										</s:else></td>
								</tr>
							</s:else>

						</s:iterator>
		       	 </tbody>
		      </table>
		      <table width="100%" cellpadding="0" cellspacing="0">
		      <tbody>
 				<tr>
 				<td width="30%">&nbsp;</td>
       				<td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
       				<input type="button" id="closePartCodeAttrPopup"  value='<s:text name="button.common.close"/>' />
            		</td>
        		</tr>
        		</tbody>
			</table>
		  </div>
		 </div>
		</div>
	</div>

	<script type="text/javascript">
dojo.addOnLoad(function(){	
					var attributeSize="<s:property value="claim.serviceInformation.partClaimAttributes.size"/>";					
					if(attributeSize > 0){
						<s:if test="!isAllRecoveryClaimsClosed() && isProcessorReview()">
							dojo.byId("causalPartAttributeLink").innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>'
					    </s:if>
					    <s:else>
							dojo.byId("causalPartAttributeLink").innerHTML='<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>'
						</s:else>	
							dojo.connect(dojo.byId("causalPartAttributeLink"),"onclick",function(){
							dojo.publish("/causal/attribute/show");
						});
							dojo.subscribe("/causal/attribute/show", null, function() {
							dijit.byId("causalPartAtrribute").show();
							
				    	}); 
							 dojo.subscribe("/causal/attribute/hide", null, function() {
							 	dijit.byId("causalPartAtrribute").hide();
							    	}); 
							 dojo.connect(dojo.byId("closePartCodeAttrPopup"),"onclick",function() {
								 <s:iterator value="claim.serviceInformation.partClaimAttributes" status="attribute" >
								 <s:if test="!isAllRecoveryClaimsClosed() && attributes.attributePurpose.purpose=='Part Source Purpose' && isProcessorReview()">
								 dojo.byId("additional_atrr_"+'<s:property value="%{#attribute.index}" />').value=dojo.byId("additional_attribute_"+'<s:property value="%{#attribute.index}" />').value;	
								 </s:if>
								 </s:iterator>
								 dojo.publish("/causal/attribute/hide");
							}); 
							
				    	
		    			}
				    
 		})	;
 	
</script>
