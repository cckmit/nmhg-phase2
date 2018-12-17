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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="base.css"/>

<script type="text/javascript">
    function onExpressionSelect(expressionName) {
        if (expressionName) {
            dojo.byId("ruleName").value = expressionName;
            dojo.byId("failureMessage").value = expressionName;
        }
    }
</script>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>

</head>
<u:body>
<form name="baseForm" id="baseForm" method="POST">
<u:actionResults/>
    <div class="policy_section_div">
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
        <tr >
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td width="30%" height="19" class="labelNormal">
                <s:textfield name="ruleNumber" cssClass="txtField" id="ruleNumber"/>
            </td>
            <td width="70%"></td>
        </tr>
        <tr >
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
                	<td class="labelStyle"><s:text name="columnTitle.manageBusinessRule.businessAction"/></td>
            		   <td width="30%"></td>
                    <td style="border-bottom:1px solid #FCF9F3;">
                        <select name="selectedAssignedTo">
                        <option value="assign"><s:text
                                   name="dropdown.manageBusinessRule.assignTo"/></option>
					    <option value="not Assign"><s:text
                                   name="dropdown.manageBusinessRule.notAssignTo"/></option>
                        </select>
						&nbsp;
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
    </div>
   
    <div class="admin_section_div" style="margin-left:5px;">

       
            <div class="colHeader" style="height:30px;">
                <s:text name="label.manageBusinessRule.searchBusinessConditions"/>:
                <s:textfield name="name"/>
                <s:submit cssClass="buttonGeneric" action="search_predicates_for_new_DSM_routing_rule"
                          value="%{getText('button.common.search')}"/>
            </div>

            <s:if test="searchProcessed">
            <div class="admin_selections">
                <s:if test="!predicates.isEmpty()">
                    <table cellspacing="0" cellpadding="0" class="grid borderForTable">
                        <tr>
                            <td class="colHeader" >&nbsp;</td>
                            <td class="colHeader" ><s:text
                                    name="columnTitle.manageBusinessRule.businessCondition"/></td>
                        </tr>
                        <s:iterator value="predicates">
                            <tr>
                                <td class="admin_selections" style="border-bottom:1px solid #FCF9F3;">
                                   <s:if test= "predicateid==id" >
                                    <input type="radio" name="id"
                                           value="<s:property value="id"/>"
                                           alt="<s:property value="name"/>"
                                           onclick="onExpressionSelect(this.alt)" checked/>
                                </s:if>
                                <s:else>
                                    <input type="radio" name="id"
                                           value="<s:property value="id"/>"
                                           alt="<s:property value="name"/>"
                                           onclick="onExpressionSelect(this.alt)"/>
                                </s:else>
                                </td>
                                <td class="admin_selections" style="border-bottom:1px solid #FCF9F3;">
                                    <s:property value="name"/>
                                </td>
                            </tr>
                        </s:iterator>
                    </table>
                </s:if>
                <s:else>
                    <jsp:include page="emptySearchResultsMessage.jsp"/>
                </s:else>
                </s:if>
            </div>
      
    </div>
    <div class="spacingAtTop" align="center">
                <s:reset cssClass="buttonGeneric"
                         value="%{getText('button.common.reset')}"/>
                <s:submit cssClass="buttonGeneric" action="save_DSM_routing_rule"
                          value="%{getText('button.manageBusinessRule.addBusinessRule')}"/>
           </div>
</form>
</u:body>
</html>