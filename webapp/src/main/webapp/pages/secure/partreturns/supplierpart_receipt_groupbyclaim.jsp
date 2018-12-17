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
<s:set name="partsCounter"  value="0"/>
<s:iterator value="claimWithPartBeans" status="claimIterator">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" <s:if test="hasActionErrors()"> style="margin-top:80px;" </s:if>>
	<tr>
		<td >
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgcolor">
			<tr>
				<td colspan="7" nowrap="nowrap" class="sectionTitle"><s:text name="label.supplier.partDetails" /></td>
			</tr>
			<tr>
			<td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.recClaimNo"/> : </td> 
			    <td>
		       <s:property value="getRecClaim()"/>
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
						<td width="10" nowrap="nowrap" class="colHeaderTop"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
						<td width="10" nowrap="nowrap" class="colHeaderTop"><s:text name="columnTitle.common.returnlocation"/></td>
						<td width="15%" nowrap="nowrap" class="colHeaderTop"><s:text name="Action"/></td>
					</tr>
					<s:iterator value="partReplacedBeans" status="partStatus" id="supplierPartReturn">
						<tr>
							<td width="20%" ><s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
							        <td width="17%" nowrap="nowrap" >
							        <s:set name="SupplierItem" value="getSupplierItemDescription(oemPartReplaced.id)"/>
                                      <s:property value="#SupplierItem.alternateNumber"/>
							        </td>

							       <td width="20%" >
                                       <s:if test="#SupplierItem.description != null">
                                           <s:property value="#SupplierItem.description" />
                                       </s:if>
                                       <s:else>
                                          <s:property value="oemPartReplaced.itemReference.unserializedItem.description" />
                                       </s:else>
							       </td>

							<td width="12%"><s:property value="shipped" /></td>
							<td width="20%" >
							 <s:if test="(isProcessedAutomatically()&& oemPartReplaced.contract.rmaNumber!=null)||oemPartReplaced.activePartReturn.rmaNumber!=null">
                              <s:if test="isProcessedAutomatically()">
                                 <s:property value="oemPartReplaced.contract.rmaNumber" />
                              </s:if>
                              <s:elseif test = "oemPartReplaced.activePartReturn.rmaNumber!=null">
                                 <s:property value="oemPartReplaced.activePartReturn.rmaNumber" />
                              </s:elseif>
                              </s:if>
							</td>
							<td width="15%">
							<s:if test="isProcessedAutomatically() && oemPartReplaced.contract.location!=null">
								 <s:property value="oemPartReplaced.contract.location.code" />
							</s:if>
							<s:elseif test="oemPartReplaced.activePartReturn.returnLocation!=null">
								  <s:property value="oemPartReplaced.activePartReturn.returnLocation.code" />
							</s:elseif>
							</td>
							<td width="15%" ><s:select theme="twms" id="supplierPartReceipt_submit_transitions_[%{#claimIterator.index}]_[%{#partStatus.index}]"
								name="transitions[%{#partsCounter}]" headerKey=""
								headerValue="-- Select --"
								list="{@tavant.twms.jbpm.WorkflowConstants@RECEIVED,
                              	@tavant.twms.jbpm.WorkflowConstants@NOT_RECEIVED}" /></td>
						    </tr>
                             <s:set name="partsCounter" value="%{#partsCounter + 1}"/>

						</tr>
                    </td>
					</s:iterator>

				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</s:iterator>
