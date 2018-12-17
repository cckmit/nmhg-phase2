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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>




<html>
<head>
    <title><s:text name="title.common.warranty" /></title>    
    <s:head theme="twms" />
	<u:stylePicker fileName="adminPayment.css"/>
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
<u:actionResults/>
<s:form name="baseForm" id="baseForm" method="POST">
<s:hidden name="context" />
<div class="policyRegn_section_div" style="width:99%;">
	<div class="admin_section_heading">
    	<s:text name="label.manageBusinessRule.createNewProcessingRule"/>
 	</div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
         <tr>
            <td height="19" class="label" width="48%">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td class="labelNormal" width="48%">
                <s:textfield name="ruleNumber" id="ruleNumber" size="30" />
            </td>            
        </tr>
        <tr>
            <td height="19" class="label" width="48%">
                <s:text name="label.manageBusinessRule.ruleName"/>
            </td>
            <td height="19" class="labelNormal" >
                <s:textfield name="ruleName" id="ruleName" size="50"/>
            </td>
        </tr>
        <tr>
            <td height="19" class="label" width="48%">
                <s:text name="label.manageBusinessRule.failureMessage"/>
            </td>
            <td  height="19" class="labelNormal" align="left" >
                <s:textfield name="failureMessage" id="failureMessage" size="50"/>
            </td>
        </tr>
        <s:if test="doesContextUseRuleGroup()">
        <tr>
            <td height="19" nowrap="nowrap" class="label" width="48%">
                <s:text name="label.manageBusinessRule.ruleGroup"/>:
            </td>
            <td height="29" class="labelNormal">
                <%-- Do not remove the toString() used in the  value="..". This is to bypass the weird issue that it
                  was formatting the value using comma (i.e. 1400 was getting printed as 1,400)! --%>
                <s:select theme="twms" name="ruleGroup" emptyOption="false" value="%{ruleGroup.id.toString()}" 
                    list="ruleGroupsInContext" listKey="id" listValue="name"/>
            </td>
        </tr>
        </s:if>
        <s:if test="actions.size>1">
        <tr>
        	<td class="label" width="48%"><s:text name="columnTitle.manageBusinessRule.businessAction"/></td>
            <td style="border-bottom:1px solid #FCF9F3;">
                <select name="selectedAction" id="action">
                    <s:iterator value="actions">
                        <option value="<s:property value="id"/>" selected="selected"><s:property
                                value="name"/></option>
                    </s:iterator>
                </select>
                <script type="text/javascript">
                 	dojo.addOnLoad(function(){
                			var value= dojo.byId("action").value;
				          	if(value==1){
				          		dojo.html.show(dojo.byId("rejectionLabel"));
				          		dojo.html.show(dojo.byId("rejectionList"));
				          	}
				      dojo.connect(dojo.byId("action"),"onchange",function(){
				          	var value= dojo.byId("action").value;
				          	if(value==1){
				          		dojo.html.show(dojo.byId("rejectionLabel"));
				          		dojo.html.show(dojo.byId("rejectionList"));
				          	}else{
				          		dojo.html.hide(dojo.byId("rejectionLabel"));
				          		dojo.html.hide(dojo.byId("rejectionList"));
				          	}
				      })
				   });
				</script>
            </td>
         </tr>
         </s:if>
         <tr>
		  	 <td class="label" style="display:none" id="rejectionLabel">
		    	<s:text name="Rejection Reasons"/>
		     </td>
		     <td style="border-bottom:1px solid #FCF9F3;display:none" id="rejectionList">
                 <s:select list="listOfRejectionReasons" name="rejectedReason" listKey="id" listValue="description"
                         emptyOption="true"/>
             </td>
		  </tr>
    </table>

    <div class="policy_section_div" style="width:99%;vertical-align: central">
        <div class="colHeader" style="height: 30px;">
                <s:text name="label.manageBusinessRule.searchBusinessConditions"/>:
                <s:textfield name="name"/>
                <s:submit cssClass="buttonGeneric" action="search_predicates_for_new_rule"
                          value="%{getText('button.common.search')}"/>
            </div>

            
                <s:if test="!predicates.empty">
                    <table cellspacing="0" cellpadding="0" class="grid borderForTable" align="center" style="padding-bottom:10px;">
                       
                        <tr>
                        	<th class="colHeader" >&nbsp;</th>
                            <th class="colHeader" ><s:text
                                    name="columnTitle.manageBusinessRule.businessCondition"/></th>                                                        
                        </tr>
                        <tr><td style="padding:0">&nbsp;</td></tr>
                        <s:iterator value="predicates" status="status">
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
                <s:if test="searchProcessed && predicates.empty">
                    <jsp:include page="emptySearchResultsMessage.jsp"/>
                </s:if>
    </div>
</div>  
<div align="center" class="spacingAtTop">
        <s:reset cssClass="buttonGeneric"
                 value="%{getText('button.common.reset')}"/>
        <s:submit cssClass="buttonGeneric" action="save_domain_rule"
                  value="%{getText('button.manageBusinessRule.addBusinessRule')}"/>
</div>
</s:form>
</u:body>
</html>
