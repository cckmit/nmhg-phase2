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

<!-- TODO: Need to refactor it - seperate out the common parts. -->

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<html>
    <head>
        <s:head theme="twms" />
<script type="text/javascript">
    function onExpressionSelect(expressionName) {
        if (expressionName) {
            document.getElementById("ruleName").value = expressionName;
            document.getElementById("failureMessage").value = expressionName;
        }
    }
</script>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
</head>
<u:body>
<form name="baseForm" id="baseForm" method="POST">
<u:actionResults/>
<div class="policy_section_div">
    <div class="admin_section_heading">
    	<s:text name="label.manageBusinessRule.createDSMAdvisorRoutingRule"/>
 	</div>

    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td width="30%" height="19" class="labelNormal">
                <s:textfield name="ruleNumber" cssClass="txtField" id="ruleNumber"/>
            </td>
            <td width="70%"></td>
        </tr>
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleName"/>
            </td>
            <td width="70%" height="19" class="labelNormal">
                <s:textfield name="ruleName" cssClass="txtField" id="ruleName"/>
            </td>
            <td width="30%"></td>
        </tr>
        <tr>
            <td height="3" colspan="3"></td>
        </tr>
        <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.failureMessage"/>
            </td>

            <td width="70%" height="19" class="labelNormal">
                <s:textfield name="failureMessage" cssClass="txtField"
                             id="failureMessage"/>
            </td>
            <td width="30%"></td>
        </tr>
        <s:if test="doesContextUseRuleGroup()">
        <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleGroup"/>:
            </td>
            <td height="29" class="labelNormal">
                <s:select theme="twms" name="ruleGroup" emptyOption="false" value="%{ruleGroup.id.toString()}" 
                    list="ruleGroupsInContext" listKey="id" listValue="name"/>
            </td>
        </tr>
        </s:if>
        <tr>
            <td height="3" colspan="2"></td>
        </tr>
        <tr>
        <s:if test="searchProcessed">
        <div class="admin_selections">
          <s:if test="!predicates.isEmpty()">
        	 <td class="label" align="center" colspan="2"><s:text
                                    name="columnTitle.manageBusinessRule.businessAction"/></td>
             <td style="border-bottom:1px solid #FCF9F3;">
            	<select name="selectedAssignedTo">
                  <option value="assign"><s:text
                          name="dropdown.manageBusinessRule.assignTo"/></option>
	              <option value="not Assign"><s:text
                          name="dropdown.manageBusinessRule.notAssignTo"/></option>
				</select>
                <select name="selectedUserCluster">
                <s:iterator value="userClusters">
                	<option value="<s:property value="name"/>" selected="selected"><s:property
                    		value="name"/></option>
	            </s:iterator>
      			</select>
			</td>
		  </s:if>
		 </div>
		 </s:if>
        </tr>
    </table>

            <div class="admin_section_div" >

     
            <div class="colHeader" style="height:30px;">
                <s:text name="label.manageBusinessRule.searchBusinessConditions"/>:
                <s:textfield name="name"/>
                <s:submit cssClass="buttonGeneric" action="search_predicates_for_new_DSM_advisor_routing_rule"
                          value="%{getText('button.common.search')}"/>
            </div>

            <s:if test="searchProcessed">
            <div class="admin_selections">
                <s:if test="!predicates.isEmpty()">
                    <table cellspacing="0" cellpadding="0" class="grid borderForTable">
                        <tr>
                            <td class="colHeader" colspan="2">&nbsp;</td>
                            <td class="colHeader" align="center"><s:text
                                    name="columnTitle.manageBusinessRule.businessCondition"/></td>
                            <td class="admin_section_heading" width="15%">&nbsp;</td>
                        </tr>
                        <s:iterator value="predicates">
                            <tr>
                                <td class="admin_selections" style="border-bottom:1px solid #FCF9F3;">
                                    <input type="radio" name="id"
                                           value="<s:property value="id"/>"
                                           alt="<s:property value="name"/>"
                                           onclick="onExpressionSelect(this.alt)"/>
                                </td>
                                <td class="admin_selections" style="border-bottom:1px solid #FCF9F3;" align="center"
                                    width="15%"><s:text name="label.manageBusinessRule.if"/></td>
                                <td class="admin_selections" style="border-bottom:1px solid #FCF9F3;">
                                    <s:property value="name"/>
                                </td>
                                <td class="admin_selections" align="center" style="border-bottom:1px solid #FCF9F3;">
                                    <s:text name="label.manageBusinessRule.then"/></td>
                                <!-- TODO:  Need to refactor this - the getting values from context approach. And the value type can be boolean -->
                            </tr>
                        </s:iterator>
                    </table>
                </s:if>
                <s:else>
                    <jsp:include page="emptySearchResultsMessage.jsp"/>
                </s:else>
            </div>
            </s:if>
        </div>            
    </div>

   
    <div class="spacingAtTop" align="center">
                <s:reset cssClass="buttonGeneric"
                         value="%{getText('button.common.reset')}"/>
                <s:submit cssClass="buttonGeneric" action="save_DSM_advisor_routing_rule"
                          value="%{getText('button.manageBusinessRule.addBusinessRule')}"/>
           </div>
</form>
</u:body>
</html>