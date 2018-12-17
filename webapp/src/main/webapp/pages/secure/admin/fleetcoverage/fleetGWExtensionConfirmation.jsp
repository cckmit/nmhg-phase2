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
		<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; ">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
				<s:form action="fleetGWExtension" theme="twms" validate="true">
					<s:hidden name="selectedGoodWillPolicy" value="%{selectedGoodWillPolicy.id}"/>
					
						<table border="0" cellspacing="0" cellpadding="0" width="100%" class="grid">
							<tr>
								<td width="14%"  class="labelStyle">	
									<s:text name="label.common.policyName"/>
								</td>
								<td>	
									<s:property value="selectedGoodWillPolicy.description"/>
								</td>
							</tr>
							<tr>
								<td width="14%" class="labelStyle">		
						    		<s:text name="label.common.policyCode"/>
						    	</td>
						    	<td>	
						    		<s:property value="selectedGoodWillPolicy.code"/>
						    	</td>
						    </tr>	
						</table>
					
						<div class="section_head" >
						
						<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin-left:5px;">  					
						<thead>
							<tr>
								<th class="warColHeader" align="center" style="padding:0;margin:0" width="2%">
									<input type="checkbox" name="checkbox" value="checkbox" id="masterCheckBox" 
										class="policy_checkboxes" disabled/>
								</th>
						   		<th class="warColHeader" align="left"><s:text name="label.common.serialNumber"/></th>
					   			<th class="warColHeader" align="left"><s:text name="label.common.modelNumber"/></th>
					   			<th class="warColHeader" align="left"><s:text name="columnTitle.common.description"/></th>
					   			<th class="warColHeader" align="left"><s:text name="label.common.hoursOnTruck"/></th>
							</tr>
						</thead>				
						<tbody id="policy_list">							
							<s:iterator value = "inventoryItemMappings" status="inventoryIterator">
								<tr>
									<td align="center" style="padding:0;margin:0">
										<s:hidden name="inventoryItemMappings[%{#inventoryIterator.index}].inventoryItem"  
									     		value="%{inventoryItem.id}" />
									     <input type="checkbox" checked disabled>		   
									</td>
									<td><s:property value="inventoryItem.serialNumber"/></td>
									<td><s:property value="inventoryItem.ofType.model.name"/></td>
									<td><s:property value="inventoryItem.ofType.description" /></td>								
									<td>
										<s:property value="inventoryItem.hoursOnMachine" />
								   </td>							   
							 </tr>							 
					</s:iterator>										
				</tbody>
			</table>
		
			
		   </div>
		 
		   <table  border="0" cellspacing="0" cellpadding="0" width="100%" class="grid">
			   <tr>
		    	 <td>
	           		<label for="probably_cause" class="labelStyle"><s:text name="label.manageFleetCoverage.tillDate"/>:</label>
	           		<s:property value="manageCoverageDate"/> 
	                <s:hidden name="manageCoverageDate" />
	       		 </td>
	       	   </tr>
		  	   <tr>		    
		    	<td nowrap="nowrap">		    		
		          		<label for="probably_cause" class="labelStyle"><s:text name="label.common.hoursCovered"/>:</label>
		          		<s:property value="serviceHoursCovered"/> 
                		<s:hidden name="serviceHoursCovered" />	          				           		
		    	</td>
		  	   </tr>
		       <tr>
			    	<td>
	            		<label for="probably_cause" class="labelStyle"><s:text name="label.common.comments"/>:</label><br/>
	                	<s:property value="manageCoverageComments"/> 
	                	<s:hidden name="manageCoverageComments" />	
	        		</td>
        		</tr>
		   </table>
		
			<div id="submit" align="center" style="margin:5px;">				
				<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}"/>					
			</div>
	  </s:form>	
	</div>
	</div>	

</html>
