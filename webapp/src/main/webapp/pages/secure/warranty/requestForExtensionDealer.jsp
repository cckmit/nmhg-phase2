<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="t" uri="twms" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="title.reduced.coverage.request.Extension"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
	<style type="text/css">
	.warColHeader{line-height:20px; border:1px solid #EFEBF7;}
	.grid{width:98%;}
	</style>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults />
	<s:form name="requestForExtensionForm" action="saveReducedCoverageDealerInput">
		<div class="policyRegn_section_div">
		<div class="policy_section_heading">
		<s:text name="label.reduced.coverage"/> : <s:text name="title.reduced.coverage.request.Extension"/></div>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" >
				<s:iterator value="itemsWithReducedCoverage" status="iter">
				  
					<tr>						
						<td width="15%" class="labelStyle"><s:text name="label.common.serialNumber"/> :
						<s:hidden name="id" value="%{id}" />
						<s:hidden name="itemSelected['%{inventoryItem.id}']"  value="true" /> 
						</td>
						<td width="75%">
						  <s:if test="!(inventoryItem.serializedPart)"> 
						     <s:a id="SerialNumberId" href="#"><s:property value="inventoryItem.serialNumber" /> </s:a>
								 <script type="text/javascript">
									dojo.addOnLoad(function() {
									dojo.connect(dojo.byId("SerialNumberId"), "onclick", function(event){															
									var url = "inventoryDetail.action?id="+ "<s:property value="inventoryItem.id"/>" ;																
									var thisTabLabel = getMyTabLabel();
									parent.publishEvent("/tab/open", {
									                                   label: "Inventory Item",
																       url: url, 
																       decendentOf: thisTabLabel,
																       forceNewTab: true
											                  });
											                });
														});	
							   </script>
						   </s:if>
						   <s:else>
							   <u:openTab autoPickDecendentOf="true" id="major_Component_Details[%{id}]" cssClass="link"
                                          tabLabel="EquipmentInfo %{inventoryItem.serialNumber}" forceNewTab="true"
                                          url="majorComponentInventoryDetail.action?id=%{inventoryItem.id}" catagory="majorComponents">
                                  <u style="cursor: pointer;">
                        	          <s:property value="inventoryItem.serialNumber" />
                    	          </u>
                               </u:openTab>
						 </s:else>						    
						</td>
					</tr>
					<tr>
						<td colspan="2" >						
							<table width="95%" border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
								
									<tr class="row_head">
										<th><s:text name="columnTitle.listPolicies.warranty_type"/></th>
										<th><s:text name="columnTitle.listPolicies.standard_coverage"/></th>
										<th><s:text name="columnTitle.listPolicies.reduction_in_coverage"/></th>
										<th><s:text name="columnTitle.listPolicies.plan_name"/></th>
									</tr>
									<s:iterator value="policiesWithReducedCoverage">
										<tr>
											<td><s:property value="%{getText(policyDefinition.warrantyType.displayValue)}" /></td>
											<td><s:property
												value="policyDefinition.coverageTerms.monthsCoveredFromDelivery" />&nbsp;
													<s:text name="label.common.months"/></td>																	
											<td>
											<s:if test="reductionInCvg[policyDefinition.code] != null">											
											<s:property
												value="reductionInCvg[policyDefinition.code]" />
											<s:text name="label.common.months"/>
											</s:if>
											<s:else>
											<s:property
												value="reductionInCvgDays[policyDefinition.code]" />
												<s:text name="label.common.days"/>																						
											</s:else>
											</td>											
											<td>
						                       <u:openTab autoPickDecendentOf="true"
												id="policy_%{inventoryItem.id}_%{policyDefinition.id}"
												tabLabel="Policy %{policyDefinition.code}"
												url="get_policy_detail.action?policyId=%{policyDefinition.id}">
												<s:property value="policyDefinition.description" /></u:openTab>
											</td>
										</tr>
									</s:iterator>
							</table>						
						</td>
					</tr>
					<tr>
					<td colspan="2" >					
					<table width="100%" border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
					  <thead >
						  <th class="warColHeader non_editable"><s:text name="label.common.user"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="columnTitle.common.status"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="label.common.comments"/> </th>
						  <th class="warColHeader non_editable"> <s:text name="columnTitle.common.updateDate" /></th>
					  </thead>			
					  <s:iterator value="audits" >
					  <s:if test="assignedBy != null">
					  <tr>
						  <td>
							  <s:if test="assignedBy != null">
							  <s:property value="assignedBy.name" /> 
							  </s:if>
							  <s:else>
							    <s:text name="label.common.userTypeSystem"></s:text>
							  </s:else>
						  </td>
						  <td><s:property value="status" /> </td>
						  <td><s:property value="comments" /> </td>
						  <td><s:property value="createDateAsCalendarDate" /> </td>					  
					  </tr>
					  </s:if>
					  </s:iterator>			
					</table>
					</td>
					</tr>								
					<s:if test="(audits.size==1)">		
					<tr>
						
						<td colspan="2"><input type="radio" id="noCoverageRadio" name="itemRequestStatus['<s:property value="inventoryItem.id"/>']"
							value="false"/>
							<s:text name="label.requests.coverage.acceptReduced"></s:text>
						<input type="radio" id="requestCoverageRadio" name="itemRequestStatus['<s:property value="inventoryItem.id"/>']"
							value="true"/>
							<s:text name="label.requests.coverage.reduced"></s:text>
						</td>
														        
						  <script type="text/javascript" >		
									dojo.addOnLoad(function() {							
									    var value = "<s:property value="itemRequestStatus[inventoryItem.id]"/>";
									    if(value == 'true'){ 												
										dojo.byId("requestCoverageRadio").checked= "checked";
										}
										 										
										if(value == 'false') {
										dojo.byId("noCoverageRadio").checked="checked";
										}										
										
										
									});
							</script>
								
					</tr>				
					</s:if>
					<s:else>						
						<s:hidden name="itemRequestStatus['%{inventoryItem.id}']" value="true"/>
					</s:else>				
					<tr>
						<td class="labelStyle" width="15%"><s:text name="label.common.comments"/>:</td>
						<td><t:textarea name="itemComments['%{inventoryItem.id}']" rows="3"  cols="30" /></td>					
					</tr>
				  </div>
				</s:iterator>
		</table>
		</div>
		<div align="center">
  	      <s:submit value="Submit" cssClass="buttonGeneric" />
	    </div>	    	
	</s:form>	
	</div>
	</div>
</u:body>
</html>
