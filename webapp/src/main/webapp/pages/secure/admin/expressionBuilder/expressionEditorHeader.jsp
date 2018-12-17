<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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
<title><s:text name="title.manageBusinessRule"/></title>
<s:head theme="twms"/>
<script type="text/javascript" src="scripts/ruleBuilder/RuleEditor.js"></script>
<script type="text/javascript" src="scripts/ruleBuilder/Controller.js"></script>
<script type="text/javascript" src="scripts/RuleSearch.js"></script>

<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="ruleSearchWizard.css"/>

<%-- START ReadOnly-vs-Normal Rendering... --%>
<s:if test="readOnly">
    <u:stylePicker fileName="ruleBuilderReadOnly.css"/>
</s:if>
<s:else>
    <u:stylePicker fileName="ruleBuilder.css"/>
</s:else>
<%-- END ReadOnly-vs-Normal Rendering... --%>

<%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript">

var userDateFormat  = '<s:property value="dateFormatForLoggedInUser"/>';

dojo.require("twms.widget.Dialog");
dojo.require("dijit.layout.LayoutContainer");

var $ = dojo.byId;
var $$ = dijit.byId;
var currentTree = null;
var savedTree = null;
var savedRuleName = null;

var readOnly = <s:property value="readOnly" />;
var isDuplicateCheck = <s:property value="duplicateCheck" />;
var isDcapContext = false;
var isInventoryContext = <s:property value="inventoryContext"/>;

<s:if test="ruleTreeJSON!=null">
currentTree = <s:property value="ruleTreeJSON" escape="false"/>;
savedTree = <s:property value="savedRuleTreeJSON"
				    escape="false"/>;
savedRuleName = "<s:property value="savedRuleName"
                    escape="true"/>";
</s:if>
var topLevelDataElements = <s:property value="availableVariablesJSON" escape="false"/>;
var allDataElements = <s:property value="allAvailableVariablesJSON" escape="false"/>;
var topDuplicacyDataElements = <s:property value="availableDuplicacyVariablesJSON" escape="false"/>;
var allDuplicacyDataElements = <s:property value="allAvailableDuplicacyVariablesJSON" escape="false"/>;

globalObj.allCurrencies = <s:property value="allCurrenciesJSON" escape="false"/>;

var validationTextbox;
var operatorCombo;

dojo.addOnLoad(function() {

    globalObj.model = new tavant.twms.ruleBuilder.Model(
            currentTree, topLevelDataElements, allDataElements,
            topDuplicacyDataElements, readOnly, isDuplicateCheck, isDcapContext, isInventoryContext, allDuplicacyDataElements);

    var expressionNameField = $("expressionNameField");

    if (expressionNameField) {
        validationTextbox = globalObj.createValidationTextBox(expressionNameField);
        dojo.style(validationTextbox.domNode, "width", "100%");
    }

    globalObj.controller =
    new tavant.twms.ruleBuilder.Controller($("nodeEditorBody"),
            $("ruleTreeJSON"), validationTextbox,
            $("referredPredicatesField"),
            $("ruleSearchStringInput"), $$("errorDialog"));

    globalObj.rhsManager = new tavant.twms.ruleBuilder.RhsManager();

    operatorCombo = globalObj.createComboBox($("operator"), globalObj.model.getAllRootOperators(),
            function(value) {
                globalObj.controller.operatorValue = value;
            }
    );

    initOperator();

    // This is just to avoid the perceptible delay that happens
    // when the user selects a date operator for the first time.
    new twms.widget.DateTextBox({});

    globalObj.controller.createRows();

<%-- START ReadOnly-vs-Normal Rendering... --%>
<s:if test="!readOnly" >
    globalObj.ruleSearchWizard = new tavant.twms.RuleSearchWizard($$("searchResults"),
            "search_policy_rule_fragments.action",
            "searchKey",
            "<s:property value="id"/>",
            "<s:property value="context"/>");
    globalObj.searchRulesDialog = $$("searchRulesDialog");

    dojo.connect($("addButton"), "onclick",
            dojo.hitch(globalObj.controller,
                    globalObj.controller.addNewRow));
    dojo.connect($("saveButton"), "onclick",
            dojo.hitch(globalObj.controller,
                    globalObj.controller.requestSave));
    dojo.connect($("searchPrebuiltExpressions"), "onclick",
            launchSearchDialog);
    dojo.connect($("resetButton"), "onclick", function() {
        revertToSavedOrEmptyData(validationTextbox);
    });

    dojo.style($("dialogBoxContainer"), "display", "block");
    dojo.style($("errorBoxContainer"), "display", "block");
</s:if>
<%-- END ReadOnly-vs-Normal Rendering... --%>

    if (validationTextbox) {
        globalObj.validateSubmission();
    }
});

function revertToSavedOrEmptyData(validationTextbox) {
    validationTextbox.setValue(savedRuleName);
    globalObj.controller.destroyAllRows();
    initOperator();
    globalObj.model.tree = savedTree;
    globalObj.controller.createRows();
    globalObj.model.referredPredicates =
    globalObj.controller.getReferredPredicates();
}

function initOperator() {
	if(savedTree && savedTree.name){
    	operatorCombo.setValue(savedTree.name);
    }else{
    	operatorCombo.setValue("all");
    }
}

function launchSearchDialog() {
    globalObj.controller.launchSearchDialog();
}
function requestRules() {
    globalObj.controller.startRuleSearch();
}
function addTermsToForm() {
    //ruleRenderer.addRows(ruleSearchWizard.getSelectedRules());
    globalObj.controller.addSelectedRules();
}
function closeDialog() {
    globalObj.controller.closeSearchDialog();
}

function conditionTypeChanged(isDuplicateCheck) {
    isDuplicateCheck = eval(isDuplicateCheck);
    globalObj.model.isDuplicateCheck = isDuplicateCheck;
    if (this.isDuplicateCheck == isDuplicateCheck) {
        revertToSavedOrEmptyData(validationTextbox);
    } else {
        globalObj.controller.destroyAllRows();
    }
}

</script>
</head>