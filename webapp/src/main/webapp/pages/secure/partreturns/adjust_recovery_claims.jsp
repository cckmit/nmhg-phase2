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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>

<s:head theme="twms"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
</head>
<SCRIPT type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane"); 
</SCRIPT>
<u:body>
  <u:actionResults/>
  <s:form action="initiate_Suppplier_Recovery">
  
  <s:hidden id="identifier1" name="id"/>
  <s:hidden id="recoveryClaimsFlag" name="recoveryClaimsFlag" value="true" ></s:hidden>
  
   <div dojoType="dijit.layout.ContentPane" label="Claim Info" style="overflow-Y: auto;overflow-x:hidden">
	  <div dojoType="twms.widget.TitlePane" 
		title="<s:text name="title.partReturnConfiguration.claimDetails"/>" labelNodeClass="section_header">
		<table cellspacing="0" border="0" cellpadding="0" class="grid">
  			<tr>
  			   <td class="labelStyle" width="20%"  nowrap="nowrap"><s:text name="label.common.claimNumber" />:</td>
               <td class="labelNormal" >
	               <u:openTab tabLabel="Claim Number"
				      		  url="view_search_detail.action?id=%{claim.id}"
				      		  id="claimIdForPart%{claim.id}"
				      		  cssClass="link"
				      		  decendentOf="%{getText('label.invTransaction.tabHeading')}">
	    	            <s:property value="claim.claimNumber" />
	    	       </u:openTab>    	
		        </td>
		        
		        <td class="labelStyle" width="20%"  nowrap="nowrap">
			    	<s:text name="label.common.causalPartNumber" />:</td>			    
			    <td class="labelNormal" >
			    	<s:property value="claim.serviceInformation.causalPart.number" />
			    </td>		        		    		
  			</tr>
  			<tr>
  			<td class="labelStyle" width="20%"  nowrap="nowrap">
			    	<s:text name="columnTitle.viewClaim.claimStatus" />:</td>			    
			    <td class="labelNormal" >
			    	<s:property value="claim.state.state" />
			    </td>
  			</tr>
  		 </table>		
	  </div>
	</div> 


	<div dojoType="dijit.layout.ContentPane" label="Part Info" style="overflow-Y: auto;overflow-x:hidden">
	  <div dojoType="twms.widget.TitlePane" 
		title="<s:text name="label.recoveryClaim.recoveryClaims"/>" labelNodeClass="section_header">
		
		<s:iterator value="recoveryInfo.replacedPartsRecovery" id="infoObject" status="infoObjStatus">
					  <div dojoType="twms.widget.TitlePane"
						title="<s:property value="recoveryClaim.recoveryClaimNumber"/>" labelNodeClass="section_header">
					   <div class="mainTitle"><s:text name="label.supplierRecovery.supplierParts"></s:text></div>   
						        <table cellspacing="0" cellpadding="0"
						        class="grid borderForTable" style="width:97%;">
						         <thead>          
						            <tr class="row_head">
						               <th><s:text name="label.common.partNumber"/></th>
						               <th><s:text name="columnTitle.common.description"/></th>
                                       <th><s:text name="label.newClaim.unitCostPrice"/></th>
                                       <th><s:text name="label.dcap.currency"/></th>
                                       <th><s:text name="label.miscPart.partUom"/></th>
                                       <th><s:text name="columnTitle.common.quantity"/></th>
                                    </tr>
						         </thead>
						         <tbody>
						         <s:iterator value="#infoObject.recoverableParts">
						            <tr>
						               <td><s:property value="oemPart.itemReference.unserializedItem.number" /></td>		               
						               <td><s:property value="oemPart.itemReference.unserializedItem.description" /></td>
                                       <td><s:property value="costPricePerUnit.breachEncapsulationOfAmount()"/></td>
                                       <td><s:property value="costPricePerUnit.breachEncapsulationOfCurrency().getCurrencyCode()"/></td>
                                       <td><s:property value="oemPart.itemReference.unserializedItem.Uom"/></td>
                                       <td><s:property value="quantity"/> </td>
                                    </tr>
                                 </s:iterator>
					             </tbody>
                                </table>
									<s:if test="contract!=null && recoveryClaim.recoveryClaimState!=null">
						        	 	<table cellspacing="0" border="0" cellpadding="0" class="grid">
						        		 <tr>
						        		 	<td class="labelStyle">
						        		 		<s:text name="messages.supplierRecovery.recoveryClaimToBeReopened"></s:text>
						        		 	</td>
						        		 </tr>
						        		</table>
						        	</s:if>
						    		 <s:elseif test="contract == null">
						        	 	<table cellspacing="0" border="0" cellpadding="0" class="grid">
						        		 <tr>
						        		 	<td class="labelStyle">
						        		 		<s:text name="messages.supplierRecovery.closedRecoveryClaim"></s:text>
						        		 	</td>
						        		 </tr>
						        		</table>
						        	</s:elseif>		       
						        
						            <table class="grid borderForTable" style="width:97%;"
							         cellspacing="0" cellpadding="0">
							         <thead>          
							            <tr class="row_head">
							               <th width="4%"></th>
							               <th width="38%"><s:text name="label.common.details"/></th>
							               <th width="11%"></th>
							               <th width="46%"><s:text name="label.common.individual"/></th>						               
							            </tr>
							         </thead>
							         <tbody>
							         	<tr>
							         		<td></td>
							         		<td>
							         			<table class="grid borderForTable"	style="width:85%;">
							         			<tbody>
							         				<tr>
							         					<td  class="labelStyle" width="60">
							         					<s:text name="label.supplier"/></td>
							         					<td width="40"><s:property value="recoveryClaim.contract.supplier.name"/></td>
							         				</tr>
							         				<tr>
							         					<td width="60" class="labelStyle"><s:text name="label.common.contract"/></td>
							         					<td width="40">
							         					           <u:openTab tabLabel="Contract"
														      		   url="contract_view.action?id=%{recoveryClaim.contract.id}"
														      		   id="Contract_%{#infoObjStatus.index}"
														      		   cssClass="link"
														      		   decendentOf="Claim Number %{claim.claimNumber}">	
														      		   <s:property value="recoveryClaim.contract.name"/>
										         					</u:openTab>
							         					</td>
							         				</tr>
							         				<tr>
							         					<td width="60"class="labelStyle"><s:text name="label.shipment.ship"/></td>
							         					<td width="40"><s:checkbox disabled="true" value="recoveryClaim.contract.physicalShipmentRequired" name="shipCheckBox"></s:checkbox>  </td>
							         				</tr>         				
							         			</tbody>
							         			</table>
							         		</td>
							         		<td></td>
							         		<td>
												<s:if test="contract==null">
													<table class="grid borderForTable"	style="width:85%;" id="costDetailsTable_<s:property value="#infoObjStatus.index"/>">
														<tbody>		
															<td class="labelStyle"><s:text name="label.common.costElement"/></td>
															<td class="labelStyle"><s:text name="label.common.share"/></td>
															<td class="labelStyle"><s:text name="label.contract.contractValue"/></td>
															<td class="labelStyle"><s:text name="label.contract.actualValue"/></td>					         			
															<s:iterator value="recoveryClaim.costLineItems" status="lineItemStatus"
																		id="lineItem">
																<s:if test="!costAfterApplyingContract.isZero()">
																	<tr>
																		<td class="labelStyle"><s:property value="%{getText(section.messageKey)}"/></td>
																		<td style="width: 50px;" value="200" valign="middle">
																				<t:money disabled="true" id="costAfterApplyingContractTag" name = "costAfterContract"
																							value="%{#lineItem.actualCost}" defaultSymbol="$"
																						 size="7" maxlength="10" ></t:money>
																		</td>
																		<td style="width: 50px;" value="200" valign="middle">
																				<t:money disabled="true" id="costAfterApplyingContractTag" name = "costAfterContract"	
																							value="%{#lineItem.costAfterApplyingContract}" defaultSymbol="$"
																						 size="7" maxlength="10" ></t:money>
																		</td>
																		
																		<td style="width: 50px;" value="200" valign="middle">
																			<t:money name="actualCost" disabled="true"
																							id="actualCostTag"	value="%{#lineItem.recoveredCost}" defaultSymbol="$" 
																							size="10" maxlength="10" ></t:money>				    							         					
																		</td>
																	</tr>
																</s:if>
															</s:iterator>
															<tr>
															<td class="labelStyle"><s:text name="label.common.total"/></td>
																<td style="width: 50px;" value="200" valign="middle">
																		<t:money disabled="true" id="totalCostAfterApplyingContractTag" name = "totalWarrantyClaimValue"
																					value="%{#infoObject.recoveryClaim.totalActualCost}" defaultSymbol="$"
																				 size="7" maxlength="10" ></t:money>
																</td>
																<td style="width: 50px;" value="200" valign="middle">
																		<t:money disabled="true" id="costAfterApplyingContractTag" name = "totalCostAfterApplyingContract"	
																					value="%{#infoObject.recoveryClaim.totalCostAfterApplyingContract}" defaultSymbol="$"
																				 size="7" maxlength="10" ></t:money>
																</td>
																
																<td style="width: 50px;" value="200" valign="middle">
																	<t:money name="totalRecoveredCost" id="totalActualCostTag_%{#infoObjStatus.index}"	value="%{#infoObject.recoveryClaim.totalRecoveredCost}" defaultSymbol="$" 
																					size="10" maxlength="10" disabled="true"></t:money>				    							         					
																</td>
															
															</tr>
														</tbody>
													</table>
												</s:if>
												<s:else>
													<table class="grid borderForTable"	style="width:85%;" id="costDetailsTable_<s:property value="#infoObjStatus.index"/>">
														<tbody>		
															<td class="labelStyle"><s:text name="label.common.costElement"/></td>
															<td class="labelStyle"><s:text name="label.common.share"/></td>
															<td class="labelStyle"><s:text name="label.contract.contractValue"/></td>
															<td class="labelStyle"><s:text name="label.contract.actualValue"/></td>
															 <s:set var="counter" value="0"/> 																												 				         			
															<s:iterator value="recoveryClaim.costLineItems" status="lineItemStatus"
																		id="lineItem">
																		
																<s:if test="!costAfterApplyingContract.isZero()">
																
																	<tr>
																		<td class="labelStyle"><s:property value="%{getText(section.messageKey)}"/></td>
																		<td style="width: 50px;" value="200" valign="middle">
																			<t:money disabled="true" id="actualCost_%{#lineItemStatus.index}" name = "costAfterContract"
																						value="%{#lineItem.actualCost}" defaultSymbol="$" size="7" maxlength="10" >
																			</t:money>
																		</td>
																		<td style="width: 50px;" value="200" valign="middle">
																			<t:money disabled="true" id="contractCost_%{#lineItemStatus.index}" name = "costAfterContract"
																					 value="%{#lineItem.costAfterApplyingContract}" defaultSymbol="$" size="7" maxlength="10" >
																			</t:money>
																		</td>
																		<td style="width: 50px;" value="200" valign="middle">
																			<t:money name="recoveryInfo.replacedPartsRecovery[%{#infoObjStatus.index}].recoveryClaim.costLineItems[%{#lineItemStatus.index}].recoveredCost" id="Actual_Value_%{#counter}"
																			 		 value="%{#lineItem.recoveredCost}" defaultSymbol="$" onchange="computeTotalValue(this)" size="10" maxlength="10" >
																			</t:money>				    							         					
																		</td>
																	</tr>
																	<s:set var="counter" value="%{#counter+1}"></s:set>   
																</s:if>
															</s:iterator>
															<!-- code for Total values -->
															<tr>
																<td class="labelStyle">
																	<s:text name="label.common.total"/>
																</td>
																<td style="width: 50px;" value="200" valign="middle">
																	<t:money disabled="true" id="totalCostAfterApplyingContractTag_%{#infoObjStatus.index}" name = "totalWarrantyClaimValue"
																				value="%{#infoObject.recoveryClaim.totalActualCost}" defaultSymbol="$" size="7" maxlength="10" >
																	</t:money>
																</td>
																<td style="width: 50px;" value="200" valign="middle">
																	<t:money disabled="true" id="costAfterApplyingContractTag_%{#infoObjStatus.index}" name = "totalCostAfterApplyingContract"	
																				value="%{#infoObject.recoveryClaim.totalCostAfterApplyingContract}" defaultSymbol="$" size="7" maxlength="10" >
																	</t:money>
																</td>
																<!-- Actual total value -->
																<td style="width: 50px;" value="200" valign="middle">
																	<t:money name="totalRecoveredCost" id="totalValue"	
																		value="%{#infoObject.recoveryClaim.totalRecoveredCost}" defaultSymbol="$" size="10" maxlength="10" disabled="true" >
																	</t:money>				    							         					
																</td>
																
															</tr>
															
															
														</tbody>
													</table>
												</s:else>	
							         		</td>
							         	</tr>
						             </tbody>
							        </table>
				      </div>
		</s:iterator>
		<script>
		
		
		function computeTotalValue(field){
			var totalCount=<s:property value='#counter'/>;
			var fieldValue = parseFloat(field.value);
			if(isNaN(fieldValue)){
		        field.value="0.00";
		        }
		    else {
		        field.value=parseFloat(fieldValue).toFixed(2);
			    }
		    var totalvalue=0.00;
		   	var totalCnt = parseFloat(totalCount);
		   	var sectionCost="0";
		    for(var i=0; i<totalCnt; i++)
		    {
		        if(document.getElementById("Actual_Value_"+i).value!=""){
		            sectionCost=document.getElementById("Actual_Value_"+i).value;
		        }
		        var sectionCost = parseFloat(sectionCost);
		        if(!isNaN(sectionCost)){
		            totalvalue = totalvalue + sectionCost;
		        }
		    }
		    document.getElementById("totalValue").value=totalvalue.toFixed(2);
		    document.getElementsByName("totalRecoveredCost")[1].value=totalvalue.toFixed(2);
		  	    
		}		
	
		</script>
		
	 
	    <div id="submit" align="center">
	       <input id="cancel_btn" class="buttonGeneric" type="button" 
		        value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />        
		   <input id="submit_btn" class="buttonGeneric" type="submit"
				value="<s:text name='button.supplierRecovery.initiateSupplierRecovery'/>" />
	    </div>
 	 </div>
      </div>
</s:form>		
</u:body>
</html>