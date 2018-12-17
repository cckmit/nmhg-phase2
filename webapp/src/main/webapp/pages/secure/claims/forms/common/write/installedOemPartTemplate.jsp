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
<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<tr>
	<td>
		<table id="oemReplacedInstalledSection_table" class="grid borderForTable">
		 <tr><td class="mainTitle" style="margin-bottom:5px;"><s:text name="label.claim.installedParts" /></td></<tr><tr><td><table id="oemInstalledSection_table" class="grid borderForTable">
			<thead>
				<tr class="row_head">
					<%-- <th width="95%" class="section_heading"><s:text name="label.claim.installedParts" /></th> --%>
					<s:if test="%{isShowPartSerialNumber()}">
						<s:if test="enableComponentDateCode()">
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
								name="columnTitle.partReturnConfiguration.partSerialNumber" /></th>
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" /></th>
						<%--Change for SLMS-776 adding date code --%>
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.common.dateCode" /></th>
						<th <s:if test="%{isProcessorReview()}"> width="7%" </s:if> <s:else>width="10%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" /></th>
					</s:if>
					<s:else>
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
								name="columnTitle.partReturnConfiguration.partSerialNumber" /></th>
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" /></th>						
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" /></th>
					</s:else>
					</s:if>
					<s:else>
						<s:if test="enableComponentDateCode()">
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" />
						</th>
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.common.dateCode" /></th>
						<th <s:if test="%{isProcessorReview()}"> width="5%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" />
						</th>
						</s:if>
						<s:else>
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" /></th>						
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" /></th>
						</s:else>
					</s:else>
					<s:if test="%{isProcessorReview()}">
						<th width="8%" align="center" class="partReplacedClass"><s:text name="label.inventory.unitPrice" /></th>
						<th width="8%" align="center" class="partReplacedClass"><s:text name="label.newClaim.unitCostPrice" /></th>
						<th width="8%" align="center" class="partReplacedClass"><s:text name="label.uom.display" /></th>						
						<th width="40%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></th>
					</s:if>
					<s:else>
						<th width="30%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></th>
					</s:else>
						<%-- <th <s:if test="%{isProcessorReview()}"> width="6%" </s:if> <s:else>width="15%"</s:else> align="center" class="partReplacedClass">
							<s:text	name="label.common.failureReport" />
						</th> --%>
					<th width="5%" class="section_heading"><div class="nList_add" style="margin-right: 5px" /></th>
				</tr>
			</thead>
			<tbody>
				<u:nList value="hussmanInstalledParts" rowTemplateUrl="getOemInstalledPartTemplate.action" paramsVar="extraParams">
					<div id="oemInstalledDiv">
						<jsp:include flush="true" page="installedPartTemplate.jsp" />
					</div>
				</u:nList>

			</tbody></table></td></tr><tr><td class="mainTitle" style="margin-bottom:5px;"><s:text name="label.claim.removedParts" /></td></<tr><tr><td><table id="oemRemovedSection_table" class="grid borderForTable">
			<s:hidden id="multipleClaim" name="task.claim.forMultipleItems" value="%{task.claim.forMultipleItems}"></s:hidden>
			<s:hidden id="paymentLength" value="%{paymentConditions.size()}" />
			<s:hidden id="claimId" value="%{task.claim.id}" />
			<s:hidden id="rowIndex" value="%{rowIndex}" />
			<s:hidden id="partsReplacedInstalledVisibleId" name="partsReplacedInstalledSectionVisible"></s:hidden>
			<s:iterator value="paymentConditions" status="paymentStatus">
				<s:hidden id="paymentConditionscode_%{#paymentStatus.index}" value="%{code}" />
				<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index}" value="%{description}" />
			</s:iterator>
			<thead>
				<tr class="row_head">
					<s:if test="%{isShowPartSerialNumber()}">
						<s:if test="enableComponentDateCode()">
							<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
									name="columnTitle.partReturnConfiguration.partSerialNumber" /></th>
							<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
								nowrap="nowrap"><s:text name="label.newClaim.partNumber" /></th>
							<%--Change for SLMS-776 adding date code --%>
							<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
								nowrap="nowrap"><s:text name="label.common.dateCode" /></th>
							<th <s:if test="%{isProcessorReview()}"> width="5%" </s:if> <s:else>width="10%"</s:else> align="center" class="partReplacedClass"><s:text
									name="label.common.quantity" /></th>
						</s:if>
						<s:else>
							<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
									name="columnTitle.partReturnConfiguration.partSerialNumber" /></th>
							<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
								nowrap="nowrap"><s:text name="label.newClaim.partNumber" /></th>
							<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
									name="label.common.quantity" /></th>
						</s:else>
					</s:if>
					<s:else>
					 <s:if test="enableComponentDateCode()">
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" />
						</th>
						<th <s:if test="%{isProcessorReview()}"> width="10%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.common.dateCode" /></th>
						<th <s:if test="%{isProcessorReview()}"> width="5%" </s:if> <s:else>width="20%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" />
						</th>
						</s:if>
						<s:else>
							<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"
							nowrap="nowrap"><s:text name="label.newClaim.partNumber" />
						</th>
						<th <s:if test="%{isProcessorReview()}"> width="20%" </s:if> <s:else>width="30%"</s:else> align="center" class="partReplacedClass"><s:text
								name="label.common.quantity" />
						</th>
						</s:else>
					</s:else>

					<s:if test="%{isProcessorReview()}">
						<th width="6%" align="center" class="partReplacedClass"><s:text name="label.inventory.unitPrice" /></th>
						<th width="6%" align="center" class="partReplacedClass"><s:text name="label.newClaim.unitCostPrice" /></th>
						<th width="6%" align="center" class="partReplacedClass"><s:text name="label.uom.display" /></th>						
						<th width="10%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></th>
						<th width="5%" align="center" class="partReplacedClass"><s:text name="label.partReturn.markPartForReturn" /></th>
						<th width="8%" align="center" class="partReplacedClass"><s:text name="columnTitle.dueParts.return_to" /></th>
						<th width="25%" align="center" class="partReplacedClass"><s:text name="columnTitle.dueParts.return_details" /></th>
					</s:if>
					<s:else>
						<th width="30%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></th>
					</s:else>
						<%-- <th <s:if test="%{isProcessorReview()}"> width="6%" </s:if> <s:else>width="15%"</s:else> align="center" class="partReplacedClass">
							<s:text	name="label.common.failureReport" />
						</th> --%>
					<th width="5%" class="section_heading"><div class="nList_add" style="margin-right: 5px" /></th>
				</tr>
			</thead>
			<tbody>
				<u:nList value="replacedParts" rowTemplateUrl="getOemRemovedPartTemplate.action" paramsVar="extraParams">
					<div id="oemRemovedDiv">
						<jsp:include flush="true" page="removedPartTemplate.jsp" />
					</div>
				</u:nList>

			</tbody>
		</table>
		</table></td><td>
		<s:hidden name="%{#nListName}" value="%{id}" id="%{qualifyId(\"removedInstalledPart_Id\")}" />
		<div class="nList_delete" id="oemRemovedInstalledDivOuterDelete" onclick="enableAddrowButton()"/></td>
</tr>
