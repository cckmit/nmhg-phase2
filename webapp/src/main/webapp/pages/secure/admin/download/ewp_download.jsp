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
<%--
@author: Jhulfikar Ali
--%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%><head>
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="adminPayment.css" />
<s:head theme="twms" />
</head>
<u:body>
	<s:form name="baseForm" id="baseForm">
		<s:hidden name="context" />

		<div class="admin_section_div" style="width: 102%; margin-right: 0px;">
		<div class="admin_section_heading"><s:property
			value="displayContext" /></div>
		<div class="errorMessage"><u:actionResults /></div>

		<table width="100%" cellspacing="0" cellpadding="0" class="grid">
			<tr width="100%">
				<td class="labelBold" width="30%"><s:text name="label.downloadMgt.ewp.coverageType"/> :</td>
				<td class="labelBold" align="left">
    				<s:radio name="inventoryReportSearchBean.coveredOrTerminated" 
    				list="#{'covered':'Covered&nbsp;&nbsp;&nbsp;','notCovered':'Not Covered&nbsp;&nbsp;&nbsp;','terminated':'Terminated'}" 
    				listKey="key" listValue="value" theme="twms" value="%{inventoryReportSearchBean.coveredOrTerminated}" />
				</td>
			</tr>
			<tr width="100%">
				<td class="labelBold"><s:text name="label.extendedwarrantyplans.purchase"/> :</td>
				<td class="labelBold"><s:checkbox id="all_policies"
							name="inventoryReportSearchBean.allExtendedPlansSelected" cssClass="checkbox" />&nbsp;&nbsp;All Extended Plans
					<script type="text/javascript">
						function setupPlansForSelection() {
							if(dojo.byId("all_policies").checked) {
								dojo.byId("ListOfPoliciesId").disabled=true;
							}else {
								dojo.byId("ListOfPoliciesId").disabled=false;
							}
						}
						dojo.addOnLoad(function(){
							dojo.connect(dojo.byId("all_policies"),"onclick",setupPlansForSelection);
							setupPlansForSelection();
						});
					</script>
				</td>
			</tr>
			<tr width="100%">
				<td class="labelBold">&nbsp;</td>
				<td class="labelBold">
					<s:select list="extendedPolicyList" multiple="true" size="10" id="ListOfPoliciesId" 
						cssStyle="margin:0px;padding:0px;" listKey="id" listValue="description"
						name="inventoryReportSearchBean.policyDefinitionIds" />
				</td>
			</tr>

			<tr>
    			<td class="labelBold" colspan="2"><s:text name="label.downloadMgt.ewp.windowPeriod"/></td>
	  		</tr>
	  		
	  		<tr>
    			<td class="labelBold"><s:text name="label.downloadMgt.ewp.windowPeriod.start"/> :</td>
    			<td class="labelBold">
    				<s:textfield name="inventoryReportSearchBean.startWindowPeriodFromDeliveryDate" />
				</td>
	  		</tr>
	  		
	  		<tr>
    			<td class="labelBold"><s:text name="label.downloadMgt.ewp.windowPeriod.end"/> :</td>
    			<td class="labelBold">
    				<s:textfield name="inventoryReportSearchBean.endWindowPeriodFromDeliveryDate" />
				</td>
	  		</tr>

			<tr>
				<td class="labelBold"><s:text
					name="label.downloadMgt.dealerNumber" /></td>
				<s:if test="loggedInUserAnInternalUser">
					<td class="labelBold"><s:textfield
						name="inventoryReportSearchBean.dealerNumber" /> &nbsp;<s:text
						name="label.downloadMgt.dealerNumber.note" /></td>
				</s:if>

				<s:if test="!loggedInUserAnInternalUser">
					<td class="labelBold"><s:hidden
						name="inventoryReportSearchBean.dealerNumber"
						value="%{loggedInUsersDealership.dealerNumber}" /> <s:property
						value="%{loggedInUsersDealership.dealerNumber}" /></td>
				</s:if>
			</tr>
			<authz:ifAdmin>
				<tr>
					<td>&nbsp;</td>
					<td class="labelBold"><s:checkbox
						name="inventoryReportSearchBean.allDealerSelected" cssClass="checkbox" />&nbsp;&nbsp;<s:text
						name="label.downloadMgt.allDealer" /></td>
				</tr>
			</authz:ifAdmin>

			<tr>
				<td class="labelBold"><s:text
					name="label.downloadMgt.delimiter" /></td>
				<td><s:select name="inventoryReportSearchBean.delimiter"
					list="delimiters" cssStyle="width:40px;" /></td>
			</tr>

		</table>
		<script type="text/javascript">
			var __selProdsProperyName = 'inventoryReportSearchBean.selectedProducts'
		</script>
		<s:push value="inventoryReportSearchBean">
			<jsp:include flush="true" page="/pages/secure/admin/policy/policy_productTypes.jsp"/>
		</s:push>

		</div>
		<div align="center" class="spacingAtTop"><s:submit
			cssClass="buttonGeneric" value="%{getText('button.common.submit')}"
			type="input" id="downloadClaimDetailData" action="downloadEWPData" />
		</div>
		
		<jsp:include flush="true" page="download_dialog.jsp"/>
	</s:form>
</u:body>