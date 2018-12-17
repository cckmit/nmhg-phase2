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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
var compensationDetailsDiv = new Array(<s:property value="sections.size()"/>);
var detailsDiv = new Array(<s:property value="sections.size()"/>);
var stdlaborRateRadioDiv	;
var spllaborRateRadioDiv	;
var splLaborRateCompensationDiv;
var collateralDamageToBePaid ;

function deleteCompensationItem(index,name)
{  
	dojo.dom.removeNode(compensationDetailsDiv[index]);
	if(name == 'Labor'){
		dojo.dom.removeNode(stdlaborRateRadioDiv);
		dojo.dom.removeNode(spllaborRateRadioDiv);
 		dojo.dom.removeNode(splLaborRateCompensationDiv);
	 }
}	

function addCompensationItem(index,name)
{
	if(name == 'Labor'){
		dojo.dom.removeNode(splLaborRateCompensationDiv);
		dojo.dom.insertAtIndex(compensationDetailsDiv[index], detailsDiv[index], 1);
		dojo.dom.insertAtIndex(spllaborRateRadioDiv, detailsDiv[index], 2);
		checkStdPriceType();
	}else{
 		dojo.dom.insertAtIndex(compensationDetailsDiv[index], detailsDiv[index], 0);
 	}
}
function addLabourCompensationItem(index)
{
	if(!splLaborRateCompensationDiv){
		splLaborRateCompensationDiv=dojo.byId('splLabourCompensationDetails');
	}
	dojo.dom.removeNode(compensationDetailsDiv[index]);
	dojo.dom.insertAtIndex(splLaborRateCompensationDiv, detailsDiv[index], 2);
	checkPriceType();
}
 function showLaborCompensation(index){
  if(stdlaborRateRadioDiv){
 		dojo.dom.insertAtIndex(stdlaborRateRadioDiv, detailsDiv[index], 0);
 		if(dojo.byId("standardLabourRadio").checked == true){
 			dojo.dom.insertAtIndex(compensationDetailsDiv[index], detailsDiv[index], 1);
 			checkStdPriceType();
		}
 	}
 if(spllaborRateRadioDiv){
	 		dojo.dom.insertAtIndex(spllaborRateRadioDiv, detailsDiv[index], 2);
	 	}
}
 
function showOtherSupplierParts()
{ 		  
    var recoveryBasedOn =document.getElementById("isCausalOrRemovedParts").value;	
	if(recoveryBasedOn == '<s:text name="label.common.causalPart"/>'){          
          dojo.html.show(dojo.byId("showOtherSupplierPartsSection"));	   
	  }
	else{	
		document.getElementById("collateralDamageToBePaidtrue").value = false;
		dojo.html.hide(dojo.byId("showOtherSupplierPartsSection"));         	    
	  }	  												
}
</script>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
	style="background: #F3FBFE; overflow-x: hidden">	
	
<div class="mainTitle"><s:text
	name="label.contractAdmin.compensationTerms" /></div>
<div class="borderTable">&nbsp;</div>
<table width="100%" border="0" cellspacing="0" cellpadding="0"
	class="grid" style="margin-top: -10px;">
		 	
	<s:iterator value="sections" id="section" status="sectionStatus">
		<s:if
			test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM && #section.name != @tavant.twms.domain.claim.payment.definition.Section@TRAVEL && #section.name != @tavant.twms.domain.claim.payment.definition.Section@OTHERS">
			<s:set name="count" value="-1" />
			
			<s:iterator value="contract.compensationTerms"
				status="compTermStatus" id="compTerm">
				<s:if
					test="%{contract.compensationTerms[#compTermStatus.index].covered && 
		        	#section.name == contract.compensationTerms[#compTermStatus.index].section.name}">
					<s:set name="count" value="#compTermStatus.index" />
				</s:if>
			</s:iterator>
			<tr>
				<td class="labelStyle" colspan="3"><s:text
					name="%{getI18NMessageKey(#section.name)}" /></td>

			</tr>
			<tr>
				<td class="labelNormal" width="2%">&nbsp;</td>
				<s:if test="%{#count != -1}">
					<td class="labelNormal" width="5%"><input
						name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
						type="radio" value="false" style="border: 0px"
						onclick="deleteCompensationItem('<s:property value="%{#sectionStatus.index}"/>','<s:property value="%{#section.name}"/>')" /></td>
				</s:if>
				<s:else>
					<td class="labelNormal" width="5%"><input
						name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
						type="radio" value="false" checked="checked" style="border: 0px"
						onclick="deleteCompensationItem('<s:property value="%{#sectionStatus.index}"/>','<s:property value="%{#section.name}"/>')" /></td>
				</s:else>
				<td class="labelNormal"><s:if
					test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS">
					<s:text name="label.contractAdmin.installable" />
				</s:if> <s:else>
					<s:text name="label.contractAdmin.notCovered" />
				</s:else></td>
			</tr>
			
			<tr>
				<td class="labelNormal" width="2%">&nbsp;</td>
				<s:if test="#count != -1">
					<td class="labelNormal" width="5%"><s:if
						test="#section.name == 'Labor'">
						<input
							name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
							type="radio" value="true" id="laborRadio" checked="checked"
							style="border: 0px"
							onclick="showLaborCompensation('<s:property value="%{#sectionStatus.index}"/>')" />

					</s:if> <s:else>
						<input
							name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
							type="radio" value="true" style="border: 0px" checked="checked"
							onclick="addCompensationItem('<s:property value="%{#sectionStatus.index}"/>','<s:property value="%{#section.name}"/>')" />
					</s:else></td>
				</s:if>
				<s:else>
					<td class="labelNormal" width="5%"><s:if
						test="#section.name == 'Labor'">
						<input
							name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
							type="radio" value="true" style="border: 0px"
							onclick="showLaborCompensation('<s:property value="%{#sectionStatus.index}"/>')" />
					</s:if> <s:else>
						<input
							name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].covered"
							type="radio" value="true" style="border: 0px"
							onclick="addCompensationItem('<s:property value="%{#sectionStatus.index}"/>','<s:property value="%{#section.name}"/>')" />
					</s:else></td>

				</s:else>
				<td class="labelNormal"><s:if
					test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS">
					<s:text name="label.contractAdmin.reimbursable" />
				</s:if> <s:else>
					<s:text name="label.contractAdmin.covered" />
				</s:else></td>
			</tr>
			
			
			<tr>
				<td colspan="3">
				<div id="details<s:property value='%{#sectionStatus.index}'/>">
				<s:if test="#section.name == 'Labor'">
					<div id="StandardLabourRateDiv">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelNormal" width="4%">&nbsp;</td>
							<td width="10%"><input
								name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].stdLaborCovered"
								type="radio" value="true" style="border: 0px" checked="checked"
								id="standardLabourRadio"
								onclick="addCompensationItem('<s:property value="%{#sectionStatus.index}"/>','<s:property value="%{#section.name}"/>')" />
							</td>
							<td class="labelNormal"><s:text
								name="label.sra.contract.labor.standardRate"></s:text></td>
						</tr>
					</table>
					<div id="compensationDetails<s:property value='%{#sectionStatus.index}'/>">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<s:hidden
							name="contract.compensationTerms[%{#sectionStatus.index}].section"
							value="%{#section.id}" />
						<tr>
							<td class="labelNormal" width="7%">&nbsp;</td>
							<td class="labelNormal" colspan="2"><input size="4"
								name="contract.compensationTerms[<s:property value="#sectionStatus.index"/>].recoveryFormula.percentageOfCost"
								value="<s:property value="contract.compensationTerms[#count].recoveryFormula.percentageOfCost"/>">
							% <s:text name="label.contractAdmin.ofCost" /><s:text
								name="%{getI18NMessageKey(#section.name)}" /> <s:if
								test="#section.name == 'Oem Parts'">
								<s:select
									name="contract.compensationTerms[%{#sectionStatus.index}].priceType"
									list="{'Dealer Net Price','Cost Price'}"
									value="%{contract.compensationTerms[#count].priceType}">
								</s:select>
							</s:if> <s:elseif test="#section.name == 'Labor'">
								<s:text name="label.sra.contract.labor.at"></s:text>
								<s:select theme="simple"
									name="contract.compensationTerms[%{#sectionStatus.index}].priceType"
									list="standardRate" id="stdPriceType"
									value="contract.compensationTerms[#count].priceType"
									onchange="checkStdPriceType();" />
								<s:text name="label.sra.contract.labor.of"></s:text>
	
								<s:if
									test="contract.compensationTerms[#count].recoveryFormula.supplierRate!= null">
									<t:money id="stdSupplierRate"
										name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
										value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate}"
										size="12" maxlength="10" cssStyle="margin:-1px"
										defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
								</s:if>
								<s:else>
									<s:hidden id="stdSupplierRateCurrency_%{#sectionStatus.index}"
										name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
										value="%{contract.supplier.preferredCurrency}" />
									<s:textfield id="stdSupplierRate"
										name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
										value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate.breachEncapsulationOfAmount()}"
										size="12" maxlength="10" cssStyle="margin:-1px"
										onchange="alterValue(%{#sectionStatus.index});" />
								</s:else>
							</s:elseif>+ <s:if
								test="contract.compensationTerms[#count].recoveryFormula.addedConstant!= null">
								<t:money
									id="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
									value="%{contract.compensationTerms[#count].recoveryFormula.addedConstant}"
									size="7" maxlength="10" cssStyle="margin:-1px"
									defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
							</s:if><s:else>
								<s:hidden id="addedAmount_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
									value="%{contract.supplier.preferredCurrency}" />
								<s:textfield id="addedConstant_amt_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
									value="%{contract.compensationTerms[#count].recoveryFormula.addedConstant.breachEncapsulationOfAmount()}"
									size="7" maxlength="10" cssStyle="margin:-1px"
									onchange="alterValue(%{#sectionStatus.index});" />
							</s:else> <s:text name="label.contractAdmin.subjectToMaximumOf" /> <s:if
								test="contract.compensationTerms[#count].recoveryFormula.maximumAmount!= null">
								<t:money
									id="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
									value="%{contract.compensationTerms[#count].recoveryFormula.maximumAmount}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
							</s:if> <s:else>
								<s:hidden id="maximumAmount_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
									value="%{contract.supplier.preferredCurrency}" />
								<s:textfield id="maximumAmount_amt_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
									value="%{contract.compensationTerms[#count].recoveryFormula.maximumAmount.breachEncapsulationOfAmount()}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									onchange="alterValue(%{#sectionStatus.index});" />
							</s:else></td>
						</tr>					
					</table>
					</div>					
					</div>
					
				</s:if>
				<s:if test="#section.name != 'Labor'">
				<div
					id="compensationDetails<s:property value='%{#sectionStatus.index}'/>">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<s:hidden
						name="contract.compensationTerms[%{#sectionStatus.index}].section"
						value="%{#section.id}" />
					<tr>
						<td class="labelNormal" width="7%">&nbsp;</td>
						<td class="labelNormal" colspan="2"><input size="4"
							name="contract.compensationTerms[<s:property value="#sectionStatus.index"/>].recoveryFormula.percentageOfCost"
							value="<s:property value="contract.compensationTerms[#count].recoveryFormula.percentageOfCost"/>">
						% <s:text name="label.contractAdmin.ofCost" /><s:text
							name="%{getI18NMessageKey(#section.name)}" /> <s:if
							test="#section.name == 'Oem Parts'">
							<s:select
								name="contract.compensationTerms[%{#sectionStatus.index}].priceType"
								list="{'Dealer Net Price','Cost Price'}"
								value="%{contract.compensationTerms[#count].priceType}">
							</s:select>
						</s:if> <s:elseif test="#section.name == 'Labor'">
							<s:text name="label.sra.contract.labor.at"></s:text>
							<s:select theme="simple"
								name="contract.compensationTerms[%{#sectionStatus.index}].priceType"
								list="standardRate" id="stdPriceType"
								value="contract.compensationTerms[#count].priceType"
								onchange="checkStdPriceType();" />
							<s:text name="label.sra.contract.labor.of"></s:text>

							<s:if
								test="contract.compensationTerms[#count].recoveryFormula.supplierRate!= null">
								<t:money id="stdSupplierRate"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
							</s:if>
							<s:else>
								<s:hidden id="stdSupplierRateCurrency_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.supplier.preferredCurrency}" />
								<s:textfield id="stdSupplierRate"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate.breachEncapsulationOfAmount()}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									onchange="alterValue(%{#sectionStatus.index});" />
							</s:else>
						</s:elseif>+ <s:if
							test="contract.compensationTerms[#count].recoveryFormula.addedConstant!= null">
							<t:money
								id="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
								value="%{contract.compensationTerms[#count].recoveryFormula.addedConstant}"
								size="7" maxlength="10" cssStyle="margin:-1px"
								defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
						</s:if><s:else>
							<s:hidden id="addedAmount_%{#sectionStatus.index}"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
								value="%{contract.supplier.preferredCurrency}" />
							<s:textfield id="addedConstant_amt_%{#sectionStatus.index}"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.addedConstant"
								value="%{contract.compensationTerms[#count].recoveryFormula.addedConstant.breachEncapsulationOfAmount()}"
								size="7" maxlength="10" cssStyle="margin:-1px"
								onchange="alterValue(%{#sectionStatus.index});" />
						</s:else> <s:text name="label.contractAdmin.subjectToMaximumOf" /> <s:if
							test="contract.compensationTerms[#count].recoveryFormula.maximumAmount!= null">
							<t:money
								id="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
								value="%{contract.compensationTerms[#count].recoveryFormula.maximumAmount}"
								size="12" maxlength="10" cssStyle="margin:-1px"
								defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
						</s:if> <s:else>
							<s:hidden id="maximumAmount_%{#sectionStatus.index}"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
								value="%{contract.supplier.preferredCurrency}" />
							<s:textfield id="maximumAmount_amt_%{#sectionStatus.index}"
								name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.maximumAmount"
								value="%{contract.compensationTerms[#count].recoveryFormula.maximumAmount.breachEncapsulationOfAmount()}"
								size="12" maxlength="10" cssStyle="margin:-1px"
								onchange="alterValue(%{#sectionStatus.index});" />
						</s:else></td>
					</tr>					
				</table>
				</div>
				</s:if>
				<s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == 'Club Car Parts'">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" id="showOtherSupplierPartsSection">				
				   <tr>
						<td class="labelStyle" colspan="3">
							<s:text name="label.supplierRecovery.otherSupplierParts" />
						</td>
		           </tr>					 
					<tr>				
					      <td class="labelNormal" width="5%">
					       <s:radio name="contract.collateralDamageToBePaid" value="%{contract.collateralDamageToBePaid.toString()}"
									list="#{'false':'label.contractAdmin.notCovered'}"
									listKey="key" listValue="%{getText(value)}" cssStyle="margin-right: 20px;width: 50px;border:0;background:#F3FBFE;"/>
					      </td>  
					</tr>
					<tr>					  						   
					      <td class="labelNormal" width="5%">
					       <s:radio id="collateralDamageToBePaid" name="contract.collateralDamageToBePaid" value="%{contract.collateralDamageToBePaid.toString()}"
									list="#{'true':'label.contractAdmin.covered'}"
									listKey="key" listValue="%{getText(value)}" cssStyle="margin-right: 20px;width: 50px;border:0;background:#F3FBFE;" />
					      </td>
					</tr>	
				</table>										
				</s:if>	
				
				<s:if test="#section.name == 'Labor'">
					<div id="SpecialLabourRateDiv">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="labelNormal" width="4%">&nbsp;</td>
							<td width="10%"><input
								name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].stdLaborCovered"
								type="radio" value="false" style="border: 0px"
								id="specialLabourRadio"
								onclick="addLabourCompensationItem('<s:property value="%{#sectionStatus.index}"/>')" />
							</td>
							<td class="labelNormal"><s:text
								name="label.sra.contract.labor.specialRate"></s:text></td>
						</tr>
					</table>
					</div>
				</s:if> <s:if test="#section.name == 'Labor'">
					<div id="splLabourCompensationDetails">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<s:hidden
							name="contract.compensationTerms[%{#sectionStatus.index}].section"
							value="%{#section.id}" />
						<tr>
							<td class="labelNormal" width="7%">&nbsp;</td>
							<td class="labelNormal" colspan="2"><input size="4"
								name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].recoveryFormula.percentageOfCost"
								value="<s:property value="contract.compensationTerms[#count].recoveryFormula.percentageOfCost"/>">
							% <s:text name="label.contractAdmin.ofCost" /> <input
								type="text"
								name="contract.compensationTerms[<s:property value="%{#sectionStatus.index}"/>].recoveryFormula.noOfHours"
								maxlength="6" size="3"
								value="<s:property value="contract.compensationTerms[#count].recoveryFormula.noOfHours"/>" />
							<s:text name="label.common.hours" />* <s:select theme="simple"
								name="contract.compensationTerms[%{#sectionStatus.index}].priceType"
								list="specialRate" id="splPriceType"
								value="contract.compensationTerms[#count].priceType"
								onchange="checkPriceType();" /> <s:text
								name="label.sra.contract.labor.of"></s:text> <s:if
								test="contract.compensationTerms[#count].recoveryFormula.supplierRate != null">
								<t:money id="supplierRate"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									defaultSymbol="%{contract.supplier.preferredCurrency}"></t:money>
							</s:if> <s:else>
								<s:hidden id="supplierRateCurrency_%{#sectionStatus.index}"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.supplier.preferredCurrency}" />
								<s:textfield id="supplierRate"
									name="contract.compensationTerms[%{#sectionStatus.index}].recoveryFormula.supplierRate"
									value="%{contract.compensationTerms[#count].recoveryFormula.supplierRate.breachEncapsulationOfAmount()}"
									size="12" maxlength="10" cssStyle="margin:-1px"
									onchange="alterValue(%{#sectionStatus.index});" />
							</s:else></td>
						</tr>
					</table>
					</div>
				</s:if>
				
				
				</div>


				<script type="text/javascript">
		            dojo.addOnLoad(function() {
		            	compensationDetailsDiv[<s:property value='%{#sectionStatus.index}'/>] = 
		            		dojo.byId('compensationDetails<s:property value='%{#sectionStatus.index}'/>');
		            	detailsDiv[<s:property value='%{#sectionStatus.index}'/>] = 
		            		dojo.byId('details<s:property value='%{#sectionStatus.index}'/>');
		            	<s:if test="#section.name == 'Labor'" >
				            if(dojo.byId('StandardLabourRateDiv')){
				            	stdlaborRateRadioDiv=dojo.byId('StandardLabourRateDiv');
				            	}
				            if(dojo.byId('SpecialLabourRateDiv')){
				            	spllaborRateRadioDiv=dojo.byId('SpecialLabourRateDiv');
				            	}
				            if(dojo.byId('splLabourCompensationDetails')){
				            	splLaborRateCompensationDiv=dojo.byId('splLabourCompensationDetails');
				            }
			            </s:if>
		            	<s:if test="%{#count == -1}">
		            		dojo.dom.removeNode(compensationDetailsDiv[<s:property value='%{#sectionStatus.index}'/>]);
		            		if('<s:property value="#section.name"/>' == 'Labor'){
					        	dojo.dom.removeNode(stdlaborRateRadioDiv);
						        dojo.dom.removeNode(spllaborRateRadioDiv);
						        dojo.dom.removeNode(splLaborRateCompensationDiv);
					        }
						</s:if> 
						<s:else>
							if('<s:property value="#section.name"/>' == 'Labor'){
								if('<s:property value="contract.compensationTerms[#count].priceType"/>' == 'Special Supplier Rate' || 
										'<s:property value="contract.compensationTerms[#count].priceType"/>' == 'Special Dealer Rate')
								{
									dojo.byId("specialLabourRadio").checked="checked";
									dojo.dom.removeNode(compensationDetailsDiv[<s:property value='%{#sectionStatus.index}'/>]);
								}else{
									dojo.byId("standardLabourRadio").checked="checked";
									dojo.dom.removeNode(splLaborRateCompensationDiv);
									
								}
							}
						</s:else>           	
		            });
		         
	               	dojo.addOnLoad(function(){
	               		if(dojo.byId("laborRadio")){
		               		if('<s:property value="#section.name"/>' == 'Labor' && dojo.byId("laborRadio").value == 'true'){
		               			showLaborCompensation('<s:property value="%{#sectionStatus.index}"/>');
		               			if('<s:property value="contract.compensationTerms[#count].priceType"/>' == 'Special Supplier Rate' || 
									'<s:property value="contract.compensationTerms[#count].priceType"/>' == 'Special Dealer Rate')
								       {
										dojo.byId("specialLabourRadio").checked="checked";
										dojo.dom.removeNode(compensationDetailsDiv[<s:property value='%{#sectionStatus.index}'/>]);
									}else{
										dojo.byId("standardLabourRadio").checked="checked";
										dojo.dom.removeNode(splLaborRateCompensationDiv);
										}
                               		}
	               		}
	               	});
	            </script></td>

			</tr>
		</s:if>
	</s:iterator>
	
</table>
<script type="text/javascript">
function checkPriceType(){
 	if(dojo.byId("splPriceType").value == 'Special Supplier Rate'){
 		dojo.byId("supplierRate").removeAttribute("readOnly");
 	}else{
 		dojo.byId("supplierRate").readOnly="true";
 		dojo.byId("supplierRate").value="";
 	}
 }
 
 function checkStdPriceType(){
 	if(dojo.byId("stdPriceType").value == 'Standard Supplier Rate'){
 		dojo.byId("stdSupplierRate").removeAttribute("readOnly");
 		
 	}else{
 		dojo.byId("stdSupplierRate").readOnly="true";
 		dojo.byId("stdSupplierRate").value="";
 	
 	}
 }
 
 function alterValue(index){
 	twms.ajax.fireHtmlRequest("get_supplier.action",{
        supplierId:supplierIdForCurrency
        },function(data) {
            var suppliercurrency = eval(data)[0];
			if(suppliercurrency["preferredCurrency"] != ''){
		 		if(dojo.byId("supplierRateCurrency_" + index)){
		 			dojo.byId("supplierRateCurrency_" + index).value = suppliercurrency["preferredCurrency"];
		 		}
		 		if(dojo.byId("maximumAmount_" + index)){
		 			dojo.byId("maximumAmount_" + index).value = suppliercurrency["preferredCurrency"];
		 		}
		 		if(dojo.byId("addedAmount_" + index)){
		 			dojo.byId("addedAmount_" + index).value = suppliercurrency["preferredCurrency"];
		 		}
		 		if(dojo.byId("stdSupplierRateCurrency_" + index)){
		 			dojo.byId("stdSupplierRateCurrency_" + index).value = suppliercurrency["preferredCurrency"];
		 		}
		 	}
			delete suppliercurrency;
        });
 	}
 </script></div>