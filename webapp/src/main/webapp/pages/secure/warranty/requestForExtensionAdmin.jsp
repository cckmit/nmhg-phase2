<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="t" uri="twms"%>
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
      	
      	function submitForm(action){
	      	var adminAction = dojo.byId("adminAction");
	      	adminAction.value = action;
	      	var formObj = dojo.byId("requestExtensionForm");
	      	formObj.submit();      	
      	}
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
	<s:form name="requestExtensionForm" action="saveReducedCoverageAdminInput">
		<div class="policy_section_heading" style="width:100%;margin:5px 5px 0px 5px;">
		<s:text name="label.requests.coverage.reduced" />
		</div>
		<div style="margin:0px 5px 5px 5px;background:#F3FBFE;border:1px solid #EFEBF7;width:100% ">
		<div class="policyRegn_section_div" style="border:none;width:99.5%">
		<table style="width:100%" cellspacing="0" cellpadding="0" class="grid">
					<tr>						
						<td width="25%" class="labelStyle"><s:text name="label.common.serialNumber"/> : 
						</td>
						<td>
						   <s:if test="!(itemsWithReducedCoverage[0].inventoryItem.serializedPart)">  
						      <s:a id="SerialNumberId" href="#"><s:property value="itemsWithReducedCoverage[0].inventoryItem.serialNumber" /> </s:a>
									 <script type="text/javascript">
											dojo.addOnLoad(function() {
											dojo.connect(dojo.byId("SerialNumberId"), "onclick", function(event){															
											var url = "inventoryDetail.action?id="+ "<s:property value="itemsWithReducedCoverage[0].inventoryItem.id"/>" ;																
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
                                          tabLabel="EquipmentInfo %{itemsWithReducedCoverage[0].inventoryItem.serialNumber}" forceNewTab="true"
                                          url="majorComponentInventoryDetail.action?id=%{itemsWithReducedCoverage[0].inventoryItem.id}" catagory="majorComponents">
                                  <u style="cursor: pointer;">
                        	          <s:property value="itemsWithReducedCoverage[0].inventoryItem.serialNumber" />
                    	          </u>
                               </u:openTab>
							</s:else>
						    
						</td>
					</tr>
					<tr>
						<td class="labelStyle"><s:text name="label.common.warrantyStartDate"/> : </td>							
						<td ><s:property value="itemsWithReducedCoverage[0].inventoryItem.wntyStartDate" />
                        </td>														
					</tr>	
					
					<tr>
						<td class="labelStyle"><s:text name = "label.common.dealerName"/> : </td>
						<td ><s:property value="itemsWithReducedCoverage[0].inventoryItem.currentOwner.name" />
						</td>
 					</tr>
					
					<tr>
						<td class="labelStyle"><s:text name = "label.common.dealerNumber"/> : </td>
						<td ><s:property value="itemsWithReducedCoverage[0].inventoryItem.dealer.dealerNumber" />
						</td>
 					</tr> 					
 					
 					
					
									
                     <tr>
						<td colspan="2" >						
							<table  border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
								
									<tr class="admin_table_header">
									    <th class="warColHeader">    </th>													
										<th class="warColHeader"><s:text name="columnTitle.listPolicies.plan_name"/></th>
										<th class="warColHeader"><s:text name="columnTitle.listPolicies.standard_coverage"/></th>
										<th class="warColHeader"><s:text name="columnTitle.listPolicies.reduction_in_coverage"/></th>
										<th class="warColHeader"><s:text name="columnTitle.policies.goodWill.select"/></th>
										<th class="warColHeader"><s:text name="columnTitle.common.warrantyEndDate"/></th>
										<s:if test="!itemsWithReducedCoverage[0].inventoryItem.serializedPart">
										  <th class="warColHeader"><s:text name="label.common.hoursCovered"/></th>
										</s:if>  
									</tr>
									<s:iterator value="itemsWithReducedCoverage[0].policiesWithReducedCoverage" status="iterStatus">
										<tr>
											<td align="center"><s:checkbox name="goodWillPoliciesSelected[%{#iterStatus.index}].selected" value="true"/></td>
											<td><u:openTab cssClass="link" url="policy_detail.action?id=%{policyDefinition}"
									        	tabLabel="Plan Name %{policyDefinition.description}"
									        		id="%{policyDefinition}" autoPickDecendentOf="true">
														<s:property value="policyDefinition.description" /> 
											    </u:openTab>
											    <s:if test="!itemsWithReducedCoverage[0].inventoryItem.serializedPart">
											     (<s:property value="policyDefinition.coverageTerms.serviceHoursCovered"/>)
											     </s:if>
											</td>
											<td><s:property
												value="policyDefinition.coverageTerms.monthsCoveredFromDelivery" />&nbsp;
													<s:text name="label.common.months"/></td>																	
											<td>
											<s:if test="reductionInCvg[code] != null">											
											<s:property
												value="reductionInCvg[code]" />
											<s:text name="label.common.months"/>
											</s:if>
											<s:else>
											<s:property
												value="reductionInCvgDays[code]" />
												<s:text name="label.common.days"/>																						
											</s:else>
											</td>											
											<td>																											
											 	<select name="goodWillPoliciesSelected[<s:property value='#iterStatus.index'/>].goodWillPolicyId">
											  		 <s:iterator value="goodWillPolicies" status="status">											   
											     		 <option value="<s:property value="goodWillPolicies[#status.index].id"/>" <s:if test="goodWillPoliciesSelected[#iterStatus.index].goodWillPolicyId == goodWillPolicies[#status.index].id">selected="selected"</s:if>><s:property value="goodWillPolicies[#status.index].description"/></option>
											   		 </s:iterator>
											 	</select>
											</td>											
											<td><sd:datetimepicker name='goodWillPoliciesSelected[%{#iterStatus.index}].warrantyEndDate' value='%{warrantyEndDateOfPolicy[code]}' /> </td>
											<s:if test="!itemsWithReducedCoverage[0].inventoryItem.serializedPart">
											 <td><s:textfield name="goodWillPoliciesSelected[%{#iterStatus.index}].serviceHoursCovered" value="%{serviceHoursCovered[code]}" cssStyle="width:70px;" /> </td>
											</s:if> 																			
										</tr>
									</s:iterator>
							</table>						
						</td>
					</tr>																		
					<tr>
					<td colspan="2" >
					<div class="mainTitle">
					<s:text name="label.requests.coverage.reduced.comments.section" />
					</div>
					<div class="policyRegn_section_div" style="border:none">
					<table width="100%" border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
					  <thead >
						  <th class="warColHeader non_editable"><s:text name="label.common.user"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="columnTitle.common.status"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="label.common.comments"/> </th>
						  <th class="warColHeader non_editable"> <s:text name="columnTitle.common.updateDate" /></th>
					  </thead>			
					  <s:iterator value="itemsWithReducedCoverage[0].audits" >
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
					</div>								
					</td>
					</tr>
					<s:hidden name="id" value="%{id}" />
					<s:hidden name="adminAction" id="adminAction" />
					<tr>
						<td class="labelStyle"><s:text name="label.common.comments"/>:</td>
						<td><t:textarea name="audit.comments" rows="3"  cols="30" /></td>											
					</tr>
		</table>		 
		</div>
		<div>
		</div>
		</div>
		<div style="margin-top:10px;width:100%" align="center">
		 
		 <s:submit type="button" onclick="submitForm('Approve')" value="%{getText('button.common.approve')}"/>
		<s:submit type="button" onclick="submitForm('Deny')" value="%{getText('button.common.reject')}" />
		 <s:submit type="button" onclick="submitForm('RequestMoreInfo')" value="%{getText('button.common.requestInfo')}" />
		</div>
		
		
	</s:form>	
	</div>
	</div>
</u:body>
</html>