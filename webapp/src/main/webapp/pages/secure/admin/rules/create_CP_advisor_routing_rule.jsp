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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="adminPayment.css"/>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>

</head>
<script type="text/javascript">
    function onExpressionSelect(expressionName) {
        if (expressionName) {
            document.getElementById("ruleName").value = expressionName;
            document.getElementById("failureMessage").value = expressionName;
        }
    }

    function showUsers(){
            var value = (dojo.byId("assignedTo")) ? dojo.byId("assignedTo").value : '';
	     	if(value=='assign To Loa Scheme'){
				twms.util.adjustVisibilityAndSubmission(dojo.byId("userCluster"), false);
				twms.util.adjustVisibilityAndSubmission(dojo.byId("loaScheme"), true);
			}else if(value=='assign' || value=='not Assign'){
				twms.util.adjustVisibilityAndSubmission(dojo.byId("loaScheme"), false);0    			
				twms.util.adjustVisibilityAndSubmission(dojo.byId("userCluster"), true);
			}
        };

  	function disable(){
                var value = (dojo.byId("assignedTo")) ? dojo.byId("assignedTo").value : '';
        		if(value=='assign To Loa Scheme'){
        			dojo.byId("userCluster").disabled = true;
        		}else if(value=='assign' || value=='not Assign'){
        	     	dojo.byId("loaScheme").disabled = true;
        		}
            }    
</script>

<u:body>
<form name="baseForm" id="baseForm" method="POST">
    <u:actionResults/>
<div class="admin_section_div" >
    <div class="admin_section_heading">
    	<s:text name="label.manageBusinessRule.createCPAdvisorRoutingRule"/>
 	</div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td width="30%" height="19" class="labelNormal">
                <s:textfield name="ruleNumber" cssClass="txtField" id="ruleNumber"/>
            </td>
            <td width="70%"></td>
        </tr>
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="label">
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
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.manageBusinessRule.failureMessage"/>
            </td>

            <td width="70%" height="19" class="labelNormal">
                <s:textfield name="failureMessage" cssClass="txtField"
                             id="failureMessage"/>
            </td>
            <td width="30%"></td>
        </tr>
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
            	<select id="assignedTo" name="selectedAssignedTo" onchange="showUsers()">
                 <option value="assign"><s:text
                                 name="dropdown.manageBusinessRule.assignToUserGroup"/></option>
                 <option value="assign To Loa Scheme"><s:text
                                 name="dropdown.manageBusinessRule.assignToLoaScheme"/></option>
	             <option value="not Assign"><s:text
                          name="dropdown.manageBusinessRule.notAssignTo"/></option>
				</select>
	                <select id="userCluster" name="selectedUserCluster" style="">
		                <s:iterator value="userClusters">
		                	<option value="<s:property value="name"/>" ><s:property
		                    		value="name"/></option>
			            </s:iterator>
	      			</select>
	      					
					<select id="loaScheme" name="selectedUserCluster" style="display: none;">
								<s:iterator value="loaSchemes">
									<option value="<s:property value="name"/>"><s:property
										value="name" /></option>
								</s:iterator>
					</select>
			</td>
		  </s:if>
		 </div>
		 </s:if>
        </tr>
    </table>
            <div id="separator"></div>
            <div class="admin_section_div" >

            <div class="colHeader" style="height: 30px;">
                <s:text name="label.manageBusinessRule.searchBusinessConditions"/>:
                <s:textfield name="name"/>
                <s:submit cssClass="buttonGeneric" action="search_predicates_for_new_CP_advisor_routing_rule"
                          value="%{getText('button.common.search')}"/>
            </div>

            <s:if test="searchProcessed">
            <div class="admin_selections">
                <s:if test="!predicates.isEmpty()">
                    <table cellspacing="1" cellpadding="1" class="grid borderForTable">
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
    <div align="center" class="spacingAtTop">
                <s:reset cssClass="buttonGeneric"
                         value="%{getText('button.common.reset')}"/>
                <s:submit cssClass="buttonGeneric" action="save_CP_advisor_routing_rule"
                          value="%{getText('button.manageBusinessRule.addBusinessRule')}" onclick="disable()"/>
    </div>
</form>
</u:body>