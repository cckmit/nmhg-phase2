<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>   


    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
          type="text/css">
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>    

<script type="text/javascript">	
dojo.require("dojox.layout.ContentPane");	
dojo.require("dijit.layout.LayoutContainer");
</script>

	
		<div dojoType="dijit.layout.LayoutContainer" style="width: 800px; height: 100%;">
			<div dojoType="dojox.layout.ContentPane" layoutAlign="client">
				<s:form action="register_extended_warranty" theme="twms" validate="true">
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
						   		<th class="warColHeader" align="left"><s:text name="columnTitle.listRegisteredWarranties.customer_name"/></th>
						   		<th class="warColHeader" align="left"><s:text name="label.warrantyAdmin.customerType"/></th>
						   		<th class="warColHeader" align="left"><s:text name="label.extendedwarrantyplan.selectedPlans"/></th>				   		
						   		<th class="warColHeader" align="left"><s:text name="label.extendedwarrantyplans.totalPlanFee"/></th>
							</tr>
						</thead>				
						<tbody id="policy_list">							
							<s:iterator value = "selectedInvItemsPolicies" status="inventoryIterator">
								<tr>
									<td align="center" style="padding:0;margin:0">
										<s:hidden name="selectedInvItemsPolicies[%{#inventoryIterator.index}].inventoryItem"  
									     		value="%{inventoryItem.id}" />
									     <input type="checkbox" checked disabled>		   
									</td>
									<td><s:property value="inventoryItem.serialNumber"/></td>
									<td><s:property value="inventoryItem.ofType.model.name"/></td>
									<td>
										<s:if test="inventoryItem.ownedBy.name == null">
			                    			<s:property value="inventoryItem.ownedBy.companyName" />
			                			</s:if>
			                			<s:else>
			                    			<s:property value="inventoryItem.ownedBy.name" />
			                			</s:else>
									</td>
									<td>						
							            <s:if test="inventoryItem.dealer == inventoryItem.buyer">
			                    			<s:text name="label.common.dealer"/>
							            </s:if>
							            <s:else>
			                    			<s:text name="label.warrantyAdmin.endCustomer"/>
			            				</s:else>
									</td>
									<td>																						
										<s:iterator value="selectedPolicies"  status="policyIterator">
												<s:hidden  
													name="selectedInvItemsPolicies[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].policyDefinition"  
										     		value="%{policyDefinition.id}"/>    	
												<s:property value="policyDefinition.code"/> (<s:property value="price"/>)
												<s:hidden name="selectedInvItemsPolicies[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].price" value="%{price.breachEncapsulationOfCurrency()}"/>
												<s:hidden name="selectedInvItemsPolicies[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].price" value="%{price.breachEncapsulationOfAmount()}"/>
												<s:iterator value="attachments"  status="attachmentIterator">
													<s:hidden name="selectedInvItemsPolicies[%{#inventoryIterator.index}].selectedPolicies[%{#policyIterator.index}].attachments[%{#attachmentIterator.index}]" value="%{id}"/>
												</s:iterator>									
												<input type="checkbox" checked disabled>										
										</s:iterator>
								   </td>
								   <td>
								   		<s:property value="getPolicyFeeTotalForInv(inventoryItem.id)"/>
								   </td>
							 </tr>							 
					</s:iterator>
					<tr>
						<td colspan="5">							   
						</td>
						<td align="right">
							<s:text name="label.extendedwarrantyplan.totalFee"/>
					   </td>
					   <td>		
					   		<s:property value="getPolicyFeeTotal()"/>		   		
					   </td>
					</tr>					
				</tbody>
			</table>
		   </div>	
		   <table  border="0" cellspacing="0" cellpadding="0"  width="100%">
			   <tr>		    
			    	<td nowrap="nowrap" class="labelStyle" width="10%">
			    		<s:text name="label.extendedwarrantyplan.dateOfPurchase"/>:
                    </td>
                    <td align="left">
                        <s:property value="purchaseDate"/>
	                	<s:hidden name="purchaseDate" />	           			           				           		
		       		</td>
		       	</tr>	
		       	<tr>		    
			    	<td nowrap="nowrap" class="labelStyle" width="10%">
			    		<s:text name="label.extendedwarrantyplan.orderNumber"/>:
                    </td>
                    <td align="left">
                        <s:property value="purchaseOrderNumber"/>
	                	<s:hidden name="purchaseOrderNumber" />	           				           		
		       		</td>
	       		</tr>
			    <tr>
			    	<td nowrap="nowrap" class="labelStyle" width="10%">
	            		<s:text name="label.common.comments"/>:
                    </td>
                    <td align="left">
                        <s:property value="purchaseComments"/>
	                	<s:hidden name="purchaseComments" />	
	        		</td>
	        	</tr>        	
		   </table>
			<div id="submit" align="center">				
				<s:submit cssClass="buttonGeneric" value="%{getText('label.purchase')}"/>					
			</div>
			<s:hidden name="invTransaction.invTransactionType" value="%{transactionTypeString}"/>
	  </s:form>	
	</div>
	</div>	

