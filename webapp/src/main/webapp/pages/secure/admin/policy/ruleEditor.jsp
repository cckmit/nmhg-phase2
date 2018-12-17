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
    <u:stylePicker fileName="policy.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <title><s:text name="title.managePolicy.applicabilityTermEditor"/></title>
    <s:head theme="twms"/>
    <style>
        .andOr {
            color: black;
            font-weight: bold;
            font-size: 12pt;
            margin-left: 4px;
        }

        .selectable {
            font-size: 12pt;
            valign: center;
            margin: 4px;
            padding-bottom: 4px;
        }

        .contextMenu {
            background-color: grey;
            display: block;
            width: 0px;
            position: absolute;
            left: 0px;
            padding: 10px 10px 10px 10px;
            border: 10px solid black;
            margin: 10px 10px 10px 10px;
        }

        .treeArrow {
            margin-left: 4px;
            border-width: 0px;
            height: 14px;
            width: 14px;
        }

        .underLine {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <!--
        TODO:  The relative paths are for the static dev env. In web app context, it should be 'scripts...'
    -->
    <script type="text/javascript">
        dojo.require("dojo.io.*");
        dojo.require("dojo.dom.*");
    </script>
    <script type="text/javascript" src="scripts/xmlForScript/xmlsax.js"></script>
    <script type="text/javascript" src="scripts/xmlForScript/xmlw3cdom.js"></script>
    <script type="text/javascript" src="scripts/autoSuggest.js"></script>
    <script type="text/javascript" src="scripts/suggestions.js"></script>
    <u:stylePicker fileName="autoSuggest.css"/>

    <script type="text/javascript">
        window.onload = function () {
            refreshBindings();
        }
        function refreshBindings() {
            handlingDetails = new Array();
            autoSuggestElements = new Array();
            elementsForAutoSuggest = new Array();
            addons = new Array();
            var j = 0;
            var myFormElement = document.getElementById('baseForm');
            for (i = 0; i < myFormElement.elements.length; i++) {
                var myElement = myFormElement.elements[i];
                var isATemplateValue = myElement.name.indexOf("<s:property value="variableValueParameterNamePrefix"/>") != -1;
                var autoSuggestFlag = myElement.getAttribute("autoSuggest");
                var needsAutoSuggest = "true" == autoSuggestFlag;
                if (isATemplateValue && needsAutoSuggest) {
                    elementsForAutoSuggest[j] = myElement;
                    addons[j] = myElement.id;
                    j++;
                }
            }
            bindAutoSuggest("baseForm", "auto_suggest_for_rule.action");
        }
    </script>
    <script type="text/javascript">
        var djConfig = {
        //	debugAtAllCosts: true,
            isDebug: true
        };
    </script>
    <u:body>
        <s:actionerror theme="xhtml"/>
        <s:fielderror theme="xhtml"/>
    <s:if test="applicabilityTermId==null">
<form action="create_applicability_term.action" name="create.rule.form" id="baseForm">
</s:if>
<s:else>
<form action="update_applicability_term.action" name="update.rule.form" id="baseForm">
</s:else>
<div class="policy_section_div">
<div class="policy_section_subheading"/>
<table width="100%">
	<tr>
		<td class="policy_section_subheading"><s:property
      value="%{getText('label.policy.forPolicy')}" /></td>
		<td width="1%"></td>
		<td class="policy_section_subheading"><b><s:property value="policy.description"/></b></td>
	</tr>
	<tr>
		<td class="policy_section_subheading"><s:property
      value="%{getText('label.policy.templateUsed')}" /></td>
		<td width="1%"></td>
		<td class="policy_section_subheading"><b><s:property value="applicabilityTerm.ruleTemplate.name" /></b></td>
	</tr>
</table>
</div>
<table width="100%">
<%--
	Start with the root currentNode here.
--%>
 <s:if test="applicabilityTermId!=null">
	 <s:hidden name="applicabilityTermId" /> 
 </s:if>
 <s:else>
	 <s:hidden name="ruleTemplateId" /> 
 </s:else>
 	 <s:hidden name="policyDefinitionId" />
<s:iterator value="nodes" >
<%--
	For the root node, just display an if, in the first row ( a row for 'if' ONLY ).
--%>
	<s:if test="isRoot()">
	<tr width="100%" class="admin_selections">
		<td width="100%" style="padding-left: 0px;" class="admin_selections">
		<span style="margin-left:<s:property value="10 + depth*15"/>px;">
			<s:if test="isRoot()"><b><s:text name='label.common.if' /></b></s:if>
		</span>
		</td>
	</tr>			
	</s:if>
	<s:elseif test="!isLeftMost()">
		<tr class="admin_selections">
			<td>	<b>	
			<span style="margin-left:<s:property value="10 + depth*15"/>px;">		
					<s:if test="isParentOR()"> <s:text name='label.common.or' /> </s:if>
					<s:else> <s:text name='label.common.and' /> </s:else>
			</span>
			<span style="margin-left:<s:property value="10 + depth*15"/>px;" />				
			</b>
			</td>
		</tr>
	</s:elseif>
	<s:if test="isLeaf()">			
		<tr width="100%" class="admin_selections">
			<td width="100%" style="padding-left: 0px;">
					<span style="margin-left:<s:property value="10 + (depth + 1)*15"/>px;">
					<s:property value="node.domainVariable.domainName" />				
					<s:property value="node.operator.synonym" />				
					<s:if test="isExpressionWithVariable()==false">
						<s:property value="literal.value" />
					</s:if>
					<s:else>
						<input 
							name="<s:property value="[1].variableValueParameterNamePrefix"/><s:property value="variableName"/>" 
							value="<s:property value="variableValue"/>"
							autoSuggest="<s:property value="node.domainVariable.valueConstrained"/>"
							id="<s:property value="node.domainVariable.domainName" />" autocomplete="off"
							/>
					</s:else>
						</span>
			</td>
		</tr>
	</s:if>
</s:iterator>
</table>
</div>
<div style="margin:5px;">
	<s:if test="applicabilityTerm.id==null">
	<input type="submit" class="buttonGeneric" 
		value="<s:text
	name="label.common.createRule" />" id="create.rule.form"/>
	</s:if>
	<s:else>
	<input type="submit" class="buttonGeneric"
		value="<s:text
	name="label.common.updateRule" />" id="update.rule.form"/>
	</s:else>
</div>
</form>
<s:form action="policy_detail.action" method="GET">
	<s:hidden name="policyDefinitionId"/>
	<div style="margin:5px;">
		<s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.backToPolicy')}"/>
	</div>
</s:form>
</u:body>
