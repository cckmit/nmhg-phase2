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
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <u:stylePicker fileName="adminPayment.css"/>
    <s:head theme="twms"/>
    <script>
        function searchRuleTemplates() {
            var searchForm = window.document.forms['search'];
            searchForm.submit();
        }
    </script>
</head>
<u:body>
	<s:form action="search_rule_templates.action" name="search">
		<s:hidden name="policyDefinitionId" />
		<table>
			<tr>
				<td><div style="text-transform:capitalize;" class="policy_th"><s:property
      value="%{getText('label.managePolicy.templateName')}" /></div></td>
				<td><s:textfield name="templateNamePattern" /></td>
				<td><img src="image/searchButton.gif" onclick="javascript:searchRuleTemplates();" /></td>
			</tr>
		</table>
	</s:form>
	
	<s:form action="new_applicability_term.action" name="chooseTemplate">
		<s:hidden name="policyDefinitionId" />
		<s:if test="ruleTemplateLabels.isEmpty()==false">
			<s:iterator value="ruleTemplateLabels" status="iterator">
				<div class="admin_section_div">
					<div class="policy_section_subheading">
						<s:property value="name"/>
					</div>
					<table width="100%">
						<tr>
							<th width="10%"></th>
							<th width="30%" class="policy_th"><s:property
      value="%{getText('label.managePolicy.ruleName')}" /></th>
							<th width="60%" class="policy_th"><s:property
      value="%{getText('label.managePolicy.description')}" /></th>
						</tr>
						<s:iterator value="includedTemplates">
							<tr class="policy_td">
								<td align="left">
									<input type="radio" name="ruleTemplateId"
										value="<s:property value="id"/>" />
								</td>
								<td align="left"><s:property value="name" /></td>
								<td align="left"><s:property value="businessLanguageString" /></td>
							</tr>
						</s:iterator>
					</table>
				</div>
			</s:iterator>
			<div>	
				<input type="submit" value="<s:text name="button.managePolicy.createRule"/>" class="buttonGeneric"/>
			</div>
		</s:if>
		<s:else>
			<div class="policy_th"><s:text name="message.managePolicy.noTemplates"/></div>
		</s:else>
	</s:form>
	
	<s:form action='policy_detail.action?policyDefinitionId=<s:property value="policyDefinitionId"/>' method="GET">
		<s:hidden name="policyDefinitionId"/>
		<s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.backToPolicy')}" />
	</s:form>
</u:body>