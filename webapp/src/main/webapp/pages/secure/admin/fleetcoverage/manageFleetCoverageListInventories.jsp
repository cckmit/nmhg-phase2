<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
      <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
          type="text/css"> 
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script>
     var selectionParams = {};
     
     function submitConfForm(evt)
     {     	
     	dojo.stopEvent(evt);
     	dojo.xhrPost({
            url: "manageFleetWarrantyCoverageConf.action",
            form: dojo.byId("manageFleetCoverage"),
            load:function(data, e) {            	
        		dijit.byId("fleetcoverage_confirmation").show();
	        	var parentDiv = dojo.byId("fleetcoverage_confirmation_div");
	        	parentDiv.innerHTML = data;        	
        	}         
        });     
     }
     dojo.addOnLoad(function() {
      	
   		if(dojo.byId("fleetCoverageCancel")) {
 			dojo.connect(dojo.byId("fleetCoverageCancel"),"onclick",function(){
			dijit.byId("fleetcoverage_confirmation").hide();
		 });
	    }	
	    
	    dojo.connect(dojo.byId("manage_fleet_coverage_submit"),"onclick",function(evt){submitConfForm(evt);});
	    
	    var multiCheckBoxControl = new CheckBoxListControl(dojo.byId("masterCheckbox_item"));
		var numItems = <s:property value="inventoryItemMappings.size" />;
        for (var i = 0; i < numItems; i++) {
            multiCheckBoxControl.addListElement(dojo.byId("item_" + i));
        }
     });
     
     function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
      }	
      
     dojo.require("twms.widget.MultipleInventoryPicker");    				   
</script>   

<s:form action="manageFleetWarrantyCoverageConf"  id="manageFleetCoverage" validate="true" method="POST">				
<s:hidden name="manageCoverageAction" />
				<s:if test="inventoryItems != null">
					<div  style="clear:both" >
					<div class="section_header">
						<s:if test="manageCoverageAction.equals('Active')">
							<s:text name="label.manageFleetCoverage.activate" />
						</s:if>
						<s:elseif test="manageCoverageAction.equals('Terminated')">
							<s:text name="label.manageFleetCoverage.terminateCoverage" />
						</s:elseif>
						<s:else>
							<s:text name="label.manageFleetCoverage.modifyEndDate" />
						</s:else>	
					</div>
					<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0"  width="100%"> 					
					<thead>
						<tr>
							<th class="warColHeader" align="center" width="2%">
								<input type="checkbox" id="masterCheckbox_item" 
									/>
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
							<tr>
								<td align="center">
									<s:checkbox name="inventoryItemMappings[%{#inventoryIterator.index}].inventoryItem"  
								     		fieldValue="%{inventoryItem.id}" id="item_%{#inventoryIterator.index}"/>   
								</td>
								<td><s:property value="inventoryItem.serialNumber"/></td>
								<td><s:property value="inventoryItem.ofType.model.name"/></td>
								<td><s:property value="inventoryItem.dealer.name" /></td>								
								<td> 								
								   <table width="98%" cellpadding="0" cellspacing="0" id="policies"  align="center" class="borderForTable" style="margin-top:5px;margin-bottom:5px;">
        							<thead>        							   
							            <tr>
							                <th class="warColHeader" align="left" nowrap="nowrap">
							                   <s:text name="label.planName"/> 
							                </th>
							                <th class="warColHeader" align="left">
							                   <s:text name="columnTitle.common.startDate"/>
							                </th> 
							                <th class="warColHeader" align="left">
							                   <s:text name="columnTitle.common.endDate"/>							                  
							                </th>
							                <s:if test="manageCoverageAction.equals('ModifyEndDate')">
							                	<th class="warColHeader" align="left">
							                   		<s:text name="label.common.new" />  <s:text name="columnTitle.common.endDate"/>							                  
							               		 </th>
							                </s:if>
							            </tr>
        							</thead>
        							<tbody>
           								<s:iterator value="availablePolicies" status="policyIterator">
								            <tr>
								                <td  nowrap="nowrap" align="left">							                	
								                  	<s:checkbox  
													name="inventoryItemMappings[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}]"  
										     		fieldValue="%{id}" />
													<s:property value="code"/>                       
								                </td>
								                <td>
								                	<s:property value="warrantyPeriod.fromDate" />  
								                </td>
								                <td>
								                	<s:property value="warrantyPeriod.tillDate" />  
								                </td>								                
								                <s:if test="manageCoverageAction.equals('ModifyEndDate')">
								                  <td>
														<s:set name="policyAuditSize" value="policyAuditSize" />
														<sd:datetimepicker name='inventoryItemMappings[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].policyAudits[%{#policyAuditSize}].warrantyPeriod.tillDate'  displayFormat='MM/dd/yyyy' />
												  </td>	
												</s:if>
								            </tr>
								            </s:iterator>
								        </tbody>
								    </table>									
							   </td>
						 </tr>
				</s:iterator>					
			</tbody>
		</table>	   	
	   <table  border="0" cellspacing="0" cellpadding="0"  width="100%" class="grid">
	   <tr>
	    	<td>
	    	</td>
	    </tr> 
	    <s:if test="!manageCoverageAction.equals('Terminated')">	
		    <tr>		    
		    	<td  width="20%">
		    		<s:if test="manageCoverageAction.equals('ModifyEndDate')">	
	           			<s:text name="label.manageFleetCoverage.modifyEndDateMessage"/>
	           			
	           		</s:if>	
	           		<label for="probably_cause"><s:text name="label.manageFleetCoverage.tillDate"/>:</label>	           		
	           		<s:if test="manageCoverageAction.equals('Active')">	
	           			(<s:text name="label.manageFleetCoverage.message"/>)
	           		</s:if>
	           			           				           		
	       		</td>
	       		<td><sd:datetimepicker name='manageCoverageDate' displayFormat='MM/dd/yyyy' /></td>
	       	</tr>	
       	</s:if>
	    <tr>
	    	<td width="20%">
           		<label for="probably_cause"><s:text name="label.common.comments"/>:</label>
           		
       		</td>
       		<td><t:textarea  rows="3" cols="30"
               		name="manageCoverageComments" /></td>
       	</tr>	
	   </table>
	   	</div>
	   <div id="submit" align="center" class="spacingAtTop">
			<s:if test="inventoryItems.size() != 0">
			<s:submit id="manage_fleet_coverage_submit"
			cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;</s:if><input 
			class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
			onclick="closeCurrentTab()" />
		</div>
		
	</s:if>	
</s:form>	
