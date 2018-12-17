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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
function expandParts(partIndex){
	var tableData = document.getElementById("td"+partIndex);
	var section = document.getElementById("div"+partIndex);
	if(section.style.display=="none"){
		section.style.display="block";
		tableData.className = "sectionOpenSM";
	}else{
		section.style.display="none";
		tableData.className = "sectionCloseSM";
	}
}
function expandPartsSection(partSection,count){	
	var partClass = document.getElementById("td"+partSection).className;	
	if(partClass=="tdsectionCloseSM"){
		for(var i=0; i!=count; i++)
		{	
			document.getElementById("div"+i).style.display="block";;
			document.getElementById("td"+i).className = "sectionOpenSM";
			document.getElementById("td"+partSection).className="tdsectionOpenSM";
		}
	}else{
		for(var i=0; i!=count; i++)
		{
			document.getElementById("div"+i).style.display="none";;
			document.getElementById("td"+i).className = "sectionCloseSM";
			document.getElementById("td"+partSection).className="tdsectionCloseSM";			
		}	
	}			
}

</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
	<tr>
		<td >
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgcolor">
			<tr>
				<td colspan="7" nowrap="nowrap" class="sectionTitle"><s:text name="label.supplier.partDetails" /></td>
			</tr>
			<tr>
			<td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.recClaimNo"/> : </td> 
			    <td>
		       <s:property value="recClaimNo"/>
		       </td>
			</tr>
			<tr>
			 <td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.dealerName"/> : </td> 
			    <td>
		       <s:property value="dealerName"/>
		       </td>
			</tr>
			<tr>
				<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0" class="grid borderForTable">
					<tr>
						<td width="17%" valign="middle" nowrap="nowrap" class="colHeaderTop"><s:text name="label.OemPartNumber"/></td>
						<td width="17%" valign="middle" nowrap="nowrap" class="colHeaderTop"><s:text name="label.supplierPartNumber"/></td>
						<td width="10%" valign="middle" nowrap="nowrap" class="colHeaderTop"><s:text name="label.description"/></td>
						<td width="12%" valign="middle" nowrap="nowrap" class="colHeaderTop"><s:text name="columnTitle.common.quantity"/></td>
					<s:if test="shipment.supplierPartReturns[0]!=null">
					    <s:if test="shipment.supplierPartReturns[0].rgaNumber!=null">
         				   <td width="10" nowrap="nowrap" class="colHeaderTop"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
      				    </s:if>
     				 </s:if>					
						<td width="15%" nowrap="nowrap" class="colHeaderTop"><s:text name="Action"/></td>
					</tr>
					<s:iterator value="shipment.returnWithUniquePart()" status="partStatus"
						id="supplierPartReturn">
						<tr>
							<td width="20%" ><s:property
								value="recoverablePart.oemPart.itemReference.unserializedItem.number" /></td>
							<td width="17%" nowrap="nowrap" ><s:property
								value="recoverablePart.supplierItem.number" />
							</td>
							<td width="20%" ><s:property
								value="recoverablePart.supplierItem.description" /></td>
							
							<td width="12%"><s:property value="recoverablePart.receivedFromSupplier" /></td>
						<s:if test="rgaNumber!=null">	
							<td width="20%" ><s:property
								value="rgaNumber" /></td>
						</s:if>
     				 		<td width="15%" ><s:select theme="twms"
								name="transitions[%{#partStatus.index}]" headerKey=""
								headerValue="-- Select --"
								list="{@tavant.twms.jbpm.WorkflowConstants@RECEIVED, 
                              	@tavant.twms.jbpm.WorkflowConstants@NOT_RECEIVED}" /></td>
						</tr>
						<tr>
							<td colspan="7">
							<div id="div<s:property value="%{#partStatus.index}"/>"
								style="display:none">
							<table width="100%" border="0" cellspacing="1" cellpadding="0">
								<tr>
									<td width="4%" >&nbsp;</td>
									<td width="28%" class="subSectionTitle">
									<div align="center"><s:text name="details"/></div>
									</td>
									<td width="19%" >&nbsp;</td>
									<td width="49%" class="subSectionTitle">
									<div align="center"><s:text name="individual"/></div>
									</td>
								</tr>
								<tr>
									<td width="4%" >&nbsp;</td>
									<td width="28%" valign="top">
									<table width="100%" border="0" cellspacing="1" cellpadding="0" class="noBorderCellbg">
										<tr>
											<td width="60%" ><s:text name="supplierPartNumber"/></td>
											<td width="40%" ><s:property
												value="recoverablePart.supplierItem.number"></s:property></td>
										</tr>
										<tr>
											<td width="60%" ><s:text name="cost"/></td>
											<td width="40%" ><s:property value="recoverablePart.oemPart.cost()"></s:property></td>
										</tr>
										<tr>
											<td width="60%" ><s:text name="price"/></td>
											<td width="40%" ><s:property
												value="recoverablePart.oemPart.getSupplierPartCost()"></s:property></td>
										</tr>
										<tr>
											<td width="60%" ><s:text name="contract"/></td>
											<td width="40%" ><s:property
												value="#oemPartReplaced.supplierPartReturn.contract.name"></s:property></td>
										</tr>
										<tr>
											<td width="60%" ><s:text name="ship"/></td>
											<td width="40%" ><input type="checkbox" checked="checked"
												disabled="disabled"></td>
										</tr>
									</table>
									</td>
									<td width="19%" >&nbsp;</td>
									<td width="49%">
									<table width="100%" border="0" cellspacing="1" cellpadding="0" class="noBorderCellbg">
										<tr>
											<td width="25%" class="subSectionTitle"><s:text name="costElement"/></td>
											<td width="15%" class="subSectionTitle">
											<div align="right"><s:text name="share"/></div>
											</td>
											<td width="30%" class="subSectionTitle"<s:text name="contractValue"/>></td>
											<td width="25%" class="subSectionTitle"><s:text name="actualValue"/></td>
										</tr>
										<s:set name="totalCount%{#partStatus.index}"
											value="%{#oemPartReplaced.supplierPartReturn.costLineItems.size()}"
											scope="page"></s:set>

										<s:iterator
											value="#oemPartReplaced.supplierPartReturn.costLineItems"
											status="lineItemStatus" id="costLineItem">
											<s:hidden
												name="oemPartsReplaced[%{#partStatus.index}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}]"
												value="%{#costLineItem.id}" />
											<tr>
												<td ><s:property
													value="%{#costLineItem.section.name}" /></td>
												<td class="tableDataAmount" valign="middle"><s:property
													value="#oemPartReplaced.supplierPartReturn.getAcutalCostForSection(#costLineItem.section.name)" />
												</td>
												<td class="tableDataAmount" valign="middle"><s:property
													value="#oemPartReplaced.supplierPartReturn.getCostAfterApplyingContractForSection(#costLineItem.section.name)" />
												</td>
												<td class="tableDataAmount" valign="middle"><s:property
													value="#oemPartReplaced.supplierPartReturn.getRecoveredCostForSection(#costLineItem.section.name)" />
												</td>
											</tr>
										</s:iterator>
										<tr>
											<td width="25%" class="totalAmountRightalign"><s:text name="total"/></td>
											<td width="15%" class="totalAmountTextRightalign"><s:property
												value="#oemPartReplaced.supplierPartReturn.getTotalActualCost()"/>
											</td>
											<td width="30%" class="totalAmountTextRightalign"><s:property
												value="#oemPartReplaced.supplierPartReturn.getTotalCostAfterApplyingContract()"/>
											</td>
											<td width="25%" class="totalAmountTextRightalign"><s:property
												value="#oemPartReplaced.supplierPartReturn.getTotalRecoveredCost()"/>
											</td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</div>
							</td>
						</tr>
					</s:iterator>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
