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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<s:if test="inventoryIndex != -1">
	<s:set name="invIndex1" value="0"/>
	<s:set name="invIndex2" value="inventoryIndex"/>
</s:if>


<s:if test="selectedInvItemsPolicies[#invIndex1].availablePolicies.size()==0">
<div style="color:red">
	<s:if test="selectedInvItemsPolicies[#invIndex1].ewpDrivenByPurchaseDate && purchaseDate==null">
	<s:text name="error.extendedwarrantyplan.dateOfPurchase"/>
	</s:if>
	<s:else>
	<s:text name="no.policies.available"/>
	</s:else>
</div>
</s:if>
<s:else>
<s:iterator value="selectedInvItemsPolicies[#invIndex1].availablePolicies" status="policyIterator">
	<table>
		<tbody>
			<tr>
				<td style="border: 0px" align="left" nowrap="nowrap"><s:checkbox
					name="selectedInvItemsPolicies[%{#invIndex2}].selectedPolicies[%{#policyIterator.index}].policyDefinition"
					fieldValue="%{policyDefinition.id}" /> <u:openTab
					autoPickDecendentOf="true"
					id="view_planDetails_%{inventoryItem.id}_%{policyDefinition.id}"
					cssClass="link"
					tabLabel="%{getText('label.extendedwarrantyplan.viewPlanDetails')}-%{policyDefinition.code}"
					url="viewPlanInfo.action?policyDefinitionId=%{policyDefinition.id}">
					<s:property value="code" />
				</u:openTab> <s:hidden name="termsAndConditions"
					value="%{policyDefinition.termsAndConditions}"
					id="terms_%{#invIndex2}_%{#policyIterator.index}" />
				<s:if test="policyDefinition.termsAndConditions !=null">
					<img
						id="termsAndCondition_<s:property value="%{#invIndex2}" />_<s:property value="%{#policyIterator.index}" />"
						src="image/searchIcon.gif" style="cursor: pointer" />
					<script type="text/javascript">
						dojo.addOnLoad( function() {
							
							var mainIndex = '<s:property value="%{#invIndex2}" />';
							var subIndex = '<s:property value="%{#policyIterator.index}" />';
							dojo.connect(dojo.byId("termsAndCondition_" + mainIndex + "_"
									+ subIndex), "onclick", function() {
								dijit.byId("dialogBoxTermsCondition").setContent(
										dojo.byId("terms_" + mainIndex + "_" + subIndex).value);
								dijit.byId("dialogBoxTermsCondition").show();
							})
						})
					</script>
				</s:if></td>
				<td style="border: 0px" align="left">
			   		<u:uploadDocument name="selectedInvItemsPolicies[%{#invIndex2}].selectedPolicies[%{#policyIterator.index}].attachments"
							trimFileNameDisplayTo="6" canDeleteAlreadyUploadedIf="loggedInUserAnAdmin" />
			   	</td>
			</tr>
		</tbody>
	</table>
</s:iterator>
</s:else>