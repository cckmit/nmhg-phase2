<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>    
</head>
<script type="text/javascript">		
</script>

	<u:actionResults />	
		<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
				<s:form action="manageFleetWarrantyCoverage" theme="twms" validate="true" method="POST">
						<div class="section_div" >
						<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0"  width="100%"> 					
						<thead>
							<tr>
								<th class="warColHeader" align="center" style="padding:0;margin:0" width="2%">
									<input type="checkbox" name="checkbox" value="checkbox" id="masterCheckBox" 
										class="policy_checkboxes" disabled/>
								</th>
						   		<th class="warColHeader" align="left"><s:text name="label.common.serialNumber"/></th>
					   			<th class="warColHeader" align="left"><s:text name="label.common.modelNumber"/></th>
					   			<th class="warColHeader" align="left"><s:text name="columnTitle.common.dealerName"/></th>
					   			<th class="warColHeader" align="left">
					   			<s:if test="manageCoverageAction.equals('Active')">
					   				<s:text name="label.manageFleetCoverage.existingTerminatedCoverage"/>
					   			</s:if>
					   			<s:elseif test="manageCoverageAction.equals('Terminated')">
					   				<s:text name="label.manageFleetCoverage.existingActiveCoverage"/>
					   			</s:elseif>
					   			<s:else>
									<s:text name="label.manageFleetCoverage.existingActiveCoverage" />/
									<s:text name="label.manageFleetCoverage.tillDate" />
								</s:else>	
					   		</th>	
							</tr>
						</thead>				
						<tbody id="policy_list">
							<s:iterator value = "inventoryItemMappings" status="inventoryIterator">
							 <s:if test="inventoryItem != null">
								<tr>
									<td align="center" style="padding:0;margin:0">
										<s:hidden name="inventoryItemMappings[%{#inventoryIterator.index}].inventoryItem"  
									     		value="%{inventoryItem.id}" />
									     <input type="checkbox" checked disabled>		   
									</td>
									<td><s:property value="inventoryItem.serialNumber"/></td>
									<td><s:property value="inventoryItem.ofType.model.name"/></td>
									<td><s:property value="inventoryItem.dealer.name" /></td>									
									<td >																						
										<s:iterator value="selectedPolicies"  status="policyIterator">												  
												<s:hidden  
													name="inventoryItemMappings[%{#inventoryIterator.index}].selectedPolicies"  
										     		value="%{id}"/>    	
												<input type="checkbox" checked disabled>
												<s:property value="policyDefinition.code"/>			
												
												<s:if test="manageCoverageAction.equals('ModifyEndDate')">
													<s:set name="policyAuditSize" value="policyAuditSize" />
													<s:hidden  name="inventoryItemMappings[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].policyAudits[%{#policyAuditSize-1}].warrantyPeriod.tillDate" />
													<s:if test = "inventoryItemMappings[#inventoryIterator.index].selectedPolicies[#policyIterator.index].policyAudits[policyAuditSize-1].warrantyPeriod.tillDate != null">
													 (<s:property value="inventoryItemMappings[#inventoryIterator.index].selectedPolicies[#policyIterator.index].policyAudits[policyAuditSize-1].warrantyPeriod.tillDate" />)
													</s:if>													       	
												</s:if>	
												<br>																		
										</s:iterator>										
								    </td>								   
							 	</tr>		
							 </s:if>					 
					</s:iterator>										
				</tbody>
			</table>
		   </div>	
		   <table  border="0" cellspacing="0" cellpadding="0"  width="100%">
		   <s:if test="!manageCoverageAction.equals('Terminated')">	
			   <tr>
		    	<td>
	           		<label for="probably_cause"><s:text name="label.manageFleetCoverage.tillDate"/>:</label>
	           		<s:property value="manageCoverageDate"/> 
	                <s:hidden name="manageCoverageDate" />
	       		</td>
	       		</tr>
       		</s:if>
		    <tr>
		    	<td>
            		<label for="probably_cause"><s:text name="label.common.comments"/>:</label><br/>
                	<s:property value="manageCoverageComments"/> 
                	<s:hidden name="manageCoverageComments" />	
        		</td>
        	</tr>
		   </table>
			<div id="submit" align="center">				
				<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}"/>					
			</div>
			<s:hidden name="manageCoverageAction" />	
	  </s:form>	
	</div>
	</div>	

</html>
