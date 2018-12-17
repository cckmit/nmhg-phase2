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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/RuleSearch.js"></script>
<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
	var searchRulesDialog = null;
	var searchResultsPane = null;
	var ruleSearchWizard = null;
	var applicableTermsTable = null;
	var ruleSearchStringInput = null;
	var applicabilityTermsHiddenInput = null;
	dojo.addOnLoad(function() {
		searchRulesDialog = dijit.byId("searchRulesDialog");
		searchResultsPane = dijit.byId("searchResults");
		applicableTermsTable = dojo.byId("applicableTermsTable");
		ruleSearchStringInput = dojo.byId("ruleSearchStringInput");
		applicabilityTermsHiddenInput = dojo.byId("applicabilityTerms");
		dojo.byId("dialogBoxContainer").style.display = "block";
		ruleSearchWizard = new tavant.twms.RuleSearchWizard(searchResultsPane,
                "search_policy_rule_fragments.action",
                "searchKey",
                "",
                "PolicyRules");
		<s:if test="%{applicabilityTermsJSON != null}">
		existingTerms = <s:property value="applicabilityTermsJSON" escape="false"/>;
		</s:if>
		<s:else>
		existingTerms = [];
		</s:else>
		ruleRenderer = new tavant.twms.RuleRenderer(applicableTermsTable, existingTerms, applicabilityTermsHiddenInput);
	})
	function showSearchRuleDialog() {
		searchRulesDialog.show();
	}
	function requestRules() {
		ruleSearchWizard.startSearch(ruleSearchStringInput.value);
	}
	function addTermsToForm() {
		ruleRenderer.addRows(ruleSearchWizard.getSelectedRules());		
		applicableTermsTable;
		closeDialog();
	}
	function closeDialog() {
		searchRulesDialog.hide();
	}
</script>
<div id="appl_terms" class="appl_section_div">
	<div id="sectionTitle" class="appl_section_heading">
		<table style="width:100%" cellpadding="0" cellspacing="0" class="appl_subsection_div">
			<tr>
				<td align="left" style="padding:8px 0 0 4px;">
					<s:property value="%{getText('label.managePolicy.applicabilityTerms')}" />
				</td>
				<td align="right" style="padding:4px 5px 0 0;">
					<t:a id="searchRules" onclick="showSearchRuleDialog">
						<img border="0" src="image/addWA.gif" style="cursor:pointer;" align="absmiddle" />
					</t:a>
				</td>
			</tr>
		</table>
	</div>
	<table style="width:97%">
		<tbody id="applicableTermsTable">
			<%--tr id="noRulesConfigured">
				<td>
					<s:text name="error.managePolicy.termsNotDefined" />
				</td>
			</tr--%>			
		</tbody>
	</table>
	<t:hidden id="applicabilityTerms" name="applicabilityTerms"/>
</div>
<%@include file="ruleSearchWizard.jsp"%>