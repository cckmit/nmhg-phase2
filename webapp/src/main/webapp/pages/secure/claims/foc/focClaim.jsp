<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
<script type="text/javascript">
		dojo.require("twms.widget.Tree");
		dojo.require("twms.data.EmptyFileWriteStore");
        dojo.require("dijit.form.Button");
        dojo.require("twms.widget.Dialog");
        dojo.require("dojox.layout.ContentPane");
</script>
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
</style>
<script type="text/javascript" src="scripts/twms-widget/widget/Select.js"></script>
<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>		       
</head>
<u:body smudgeAlert="false">
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;" >
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="focWidget">
	<s:form name="focForm" action="submitFOCData" id="focForm">
		   
		   <div class="admin_section_div" >
			    
			    <div class="admin_subsection"> 
			     <u:actionResults />
			    </div> 
				<div class="admin_subsection">
				<div class="admin_section_heading"><s:text
					name="title.serviceLocation.companyInformation" /></div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="policyRegn_table">
					<tr>
						<td><s:text name="label.serviceLocation.companyId" /></td>
						<td><s:property value="focbean.companyId"/>
						<s:hidden name="focbean.companyId" value="%{focbean.companyId}" /></td>
						<td><s:text name="label.serviceLocation.companyName" /></td>
						<td><s:property value="focbean.companyName"/>
						<s:hidden name="focbean.companyName" value="%{focbean.companyName}" />
						<s:hidden name="focbean.orderNo" value="%{focbean.orderNo}" />
						<s:hidden name="focbean.serviceProviderNo" value="%{focbean.serviceProviderNo}" />
						<s:hidden name="focbean.serviceProviderType" value="%{focbean.serviceProviderType}" />
						<s:hidden name="requestType" value="%{requestType}"/>
						<s:hidden name="ploc" value="%{ploc}"/></td>
						
					</tr>
					<tr>
						<td><s:text name="label.common.serviceProviderNo" /></td>
						<td><s:property value="focbean.serviceProviderNo"/></td>
						<td colspan="2" />
					</tr>
				</table>			
				</div>
			
				<div class="admin_subsection">
				<div class="admin_section_heading"><s:text
					name="title.equipment.information" /></div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="policyRegn_table">
					<tr>
						<td><s:text name="label.common.serialNumber" /></td>						
						<td><s:property value="focbean.serialNumber"/>      
	                     <s:hidden name="focbean.serialNumber" value="%{focbean.serialNumber}" />    
						</td>
						<td><s:text name="label.workOrderNumber" /></td>
						<td><s:textfield name="focbean.workOrderNumber" value="%{focbean.workOrderNumber}" /> </td>

					</tr>
					<tr>
						<td><s:text name="label.common.failureDate" /></td>
						<td><sd:datetimepicker name='focbean.failureDate' value='%{focbean.failureDate}' /></td>
						<td><s:text name="label.common.repairDate" /></td>
						<td><sd:datetimepicker name='focbean.repairDate' value='%{focbean.repairDate}' /> </td>
					</tr>
				</table>
				</div>
			
				<div class="admin_subsection">
				<div class="admin_section_heading"><s:text
					name="title.failure.information" /></div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="policyRegn_table">

					 
					<tr>
					  	<td><s:text name="columnTitle.duePartsInspection.causalpart" /></td>
						<td><sd:autocompleter id='causalPart' href='list_causal_part_nos.action?selectedBusinessUnit=Hussmann' name='causalPart' value='%{causalPart.number}' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='causalPart_indicator' />
						</td>

						<u:jsVar varName="spanTemplateText">
							<span title="${fullNameTooltip}">
							   ${completeCode} 
							    <input type="hidden" value="${faultCodeId}" name="faultCodeRef" /> 
								<input 	type="hidden" value="${completeCode}" name="faultCode" /> 
							</span>
						</u:jsVar>
						<td colspan="1">
				            <table>
				 	 	        <tr>
				 	 	            <td ><s:text name="label.common.faultCode"/>:</td>
				 	 	            <td id="launchFaultCodeTree"></td> 	 	            
				 	 	        </tr>
				 	 	    </table>
				        </td>
				        <td>
				        	<table>
				 	 			<tr>
					     			<s:if test="faultCode !=null">
					      				  <td id="existingFaultCode">
					      				  	 <s:hidden value="%{faultCodeRef.id}" name="faultCodeRef" /> 												        
							   				 <s:property value="faultCode" />
						    			  </td>
					    			</s:if>
											<td align="right"><div id="faultCode"></div></td>
								</tr>
							</table>
						</td>
				        
					</tr>
					
					<tr>
						<td><s:text name="label.common.faultFound" /></td>
						<td>
	
						   <s:if test="serialNumberAvailable" >
							  <s:set name="number" 	value="focbean.serialNumber" />
							  <s:set name="faultFounds" value="prepareFaultFoundList(focbean.serialNumber)" />	
						   </s:if>
						   <s:else>
 	    					  <s:set name="number" 	value="" />
    						  <s:set name="faultFounds" value="#{}" />						   
						   </s:else>
						   <s:select id="faultFound" list="#faultFounds"
							name="faultFound" listKey="id" 	listValue="name" emptyOption="true" theme="twms" 
							value="%{faultFound.id.toString()}" />
							 <script type="text/javascript">
				                dojo.addOnLoad(function() {
				                    var faultFound = dijit.byId("faultFound");
				                    var causedBy = dijit.byId("causedBy");
				                    var self = this
      								console.debug('here');      								      								
				      				dojo.connect(faultFound, "onChange",function(newValue) {
				                        dojo.publish("/causedBy/reload", [{
				                            url: "list_caused_by.action?selectedBusinessUnit=Hussmann",
				                            params: {
				                                number: '<s:property value="#number"/>',
				                                faultFound: newValue
				                            },
				                            makeLocal: true
				                        }]);				
				                    });          
				                });
				                </script>
				                
						</td>	
						<td><s:text name="label.newClaim.causedBy" /></td>
						<td><sd:autocompleter id="causedBy" name="causedBy" value="%{causedBy.name}" listenTopics="/causedBy/reload" />
						</td>
						</td>	
					</tr>					
																						
				</table>
				
				<div style="display: none">
				    <div dojoType="twms.widget.Dialog" id="browserForFaultCode" 
				            title="<s:text name="title.newClaim.selectFaultCode"/>" style="width:95%; height:95%;overflow:hidden">
				        <div dojoType="dijit.layout.LayoutContainer">
				             <div dojoType="dijit.layout.ContentPane" layoutAlign="top"  style="width:100%;height:408px;overflow:auto ">
				                <div dojoType="twms.data.EmptyFileWriteStore" data-dojo-id="faultCodeStore">
				                </div>
				                <div dojoType="twms.widget.Tree" id="faultCodeTreeWidget" store="faultCodeStore"
				                     labelAttr="name" label="Fault Location">
				                </div>
				            </div>
				        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="border-top:1px solid #DBBBA6">
				            <button id="addFaultCode" style="margin:1px;"><s:text name="button.newClaim.setFaultCode"/></button>
				        </div>
				        </div>
				    </div>
				</div>
				
				</div>
					<s:if test="serialNumberAvailable" >
					<script type="text/javascript">
					        var faultCodeTree = <s:property value="jsonFaultCodeTree" escape="false"/>;
					        var faultSelected = "<s:property value="faultCodeRef.definition.code"/>";					
					        //  var claim="<s:property value="task.claim"/>"
							var faultCodeId;
							// var isAttributePresent= '<s:property value="task.claim.serviceInformation.faultClaimAttributes.size"/>' >0 && '<s:property value="task.claim.state.state"/>' !='draft' ;
					        var isFaultDialogDisplayed=false;
					        // var isCausalDialogDisplayed=false;					        
					        dojo.addOnLoad(function(){					           
					            var firsttime=true;
					            var treeWidget = dijit.byId("faultCodeTreeWidget");
					            dojo.setSelectable(treeWidget.domNode, false);					           
					            var faultCodeTreeManager = new tavant.twms.FaultCodeTreeManager(faultCodeTree, treeWidget, treeWidget.store, "faultCodeNode");					          
					            var renderer = new tavant.twms.FaultCodeRenderer(dojo.byId("faultCode"), faultCodeTreeManager, faultSelected, spanTemplateText.markup);					           					           
				                var dialog = dijit.byId("browserForFaultCode");
						        dojo.connect(dojo.byId("launchFaultCodeTree"), "onclick", function(event) {
						                dojo.stopEvent(event);
						                dialog.show();
						               if(firsttime) {
						                    faultCodeTreeManager.renderTree();
						                    firsttime=false
						                }
						            });
									
								
								dojo.connect(dojo.byId("addFaultCode"),"onclick", function() {
									   if(dojo.byId("existingFaultCode")){
									       var existingFltCd = dojo.byId("existingFaultCode");
                						   dojo.dom.destroyNode(existingFltCd);
                						}	
						               renderer.renderSelectedCode();
						               faultCodeId=faultCodeTreeManager.selectedNode.faultCode.id;
						               dialog.hide();
						         });								
							});					   
					</script>
					</s:if>
					
					
		</div>
			<div class="admin_section_div">
			 <jsp:include page="../foc/replacedInstalledOEMParts.jsp"  flush="true" />
		    </div>
	    
	    <div class="admin_section_div">
		  <table width="100%" border="0" cellspacing="0" cellpadding="0"
					>
		 	<tr>
	     		<td >
	      			<s:submit value="Submit" cssClass="button" align="right"/>
	     		</td>
	     	</tr>
	      </table> 
	    </div>
	    
	</s:form>
	</div>

	</div>

</u:body>
</html>
