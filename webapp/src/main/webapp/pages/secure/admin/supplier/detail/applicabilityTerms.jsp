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
<script type="text/javascript" src="scripts/RuleSearch.js"></script>
<script type="text/javascript" src="scripts/RuleSearchV2.js"></script>

<u:stylePicker fileName="ruleSearchWizard.css"/>
<script type="text/javascript">

	dojo.require("dojo.dom");
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
		applicabilityTermsHiddenInput = dojo.byId("contract.applicabilityTerms[?].predicate");
		dojo.byId("dialogBoxContainer").style.display = "block";
		ruleSearchWizard = new tavant.twms.RuleSearchWizard(searchResultsPane,
                "search_policy_rule_fragments.action",
                "searchKey",
                "",
                "ContractApplicabilityRules");
		<s:if test="${applicabilityTermsJSON != null}">
		existingTerms = <s:property value="applicabilityTermsJSON" escape="false"/>;
		</s:if>
		<s:else>
		existingTerms = [];
		</s:else>
		ruleRenderer = new tavant.twms.MultipleHiddenValuesRuleRenderer(applicableTermsTable, existingTerms, applicabilityTermsHiddenInput);
	})
	function showSearchRuleDialog() {
		searchRulesDialog.show();
	}
	function requestRules() {
		ruleSearchWizard.startSearch(ruleSearchStringInput.value);
	}
	function addTermsToForm() {
		ruleRenderer.addRows(ruleSearchWizard.getSelectedRules());
		closeDialog();
	}
	function closeDialog() {
		searchRulesDialog.hide();
	}
</script>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="width: 99%; background: #FCFCFC; border: #EFEBF7 1px solid;  overflow-x: hidden">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text 
						name="label.contractAdmin.coverageConditions"/></td>
			<td width="5%" class="sectionTitle">
				<t:a id="searchRules" onclick="showSearchRuleDialog">
					<img border="0" src="image/addWA.gif" style="cursor:pointer; vertical-align:middle; padding-right:5px;" />
				</t:a>
			</td>
		</tr>
		<tr>
			<td>
				<table style="width:100%">
					<tbody id="applicableTermsTable">

					</tbody>
				</table>
				<div id="contract.applicabilityTerms[?].predicate"/>
			</td>
		</tr>
	</table>
</div>
<%@include file="../../policy/ruleSearchWizard.jsp"%>
