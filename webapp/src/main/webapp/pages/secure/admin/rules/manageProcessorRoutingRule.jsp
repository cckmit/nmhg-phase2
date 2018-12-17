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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<head>
<html xmlns="http://www.w3.org/1999/xhtml">
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="base.css"/>
<s:head theme="twms"/>

    <script type="text/javascript">    
        function deActivateRule() {
        	disable();
          var formObj = dojo.byId("baseForm");
          dojo.byId('activeFlagInd').value="INACTIVE";
          formObj.action = "update_processor_routing_rule.action";
          formObj.submit(); 
		} 

 		function showUsers(){
         		var value= dojo.byId("assignedTo").value;
	        		if(value=='assign To Loa Scheme'){
	        			twms.util.adjustVisibilityAndSubmission(dojo.byId("userCluster"), false);
	        			twms.util.adjustVisibilityAndSubmission(dojo.byId("loaScheme"), true);
	        		}else if(value=='assign' || value=='not Assign'){
	        			twms.util.adjustVisibilityAndSubmission(dojo.byId("loaScheme"), false);
	        			twms.util.adjustVisibilityAndSubmission(dojo.byId("userCluster"), true);
		        	}
	            };

        function preSelect(){
                 	var value= dojo.byId("assignedTo").value;
                 	var selectedUser = "<s:property value="rule.action.name"/>"
                 	var i=0;
	            		if(value=='assign To Loa Scheme'){
		                		<s:iterator value="loaSchemes">
									var user = "<s:property value="name"/>";
									if(user == selectedUser){
							  				dojo.byId("loaScheme").options[i].selected=true;
							  		  }
									 i=i+1;
								</s:iterator>
	                	  } else if(value=='assign' || value=='not Assign'){
	                 			<s:iterator value="userClusters">
	 							  	var user = "<s:property value="name"/>";
	 							  	   if(user == selectedUser){
	 					  				   dojo.byId("userCluster").options[i].selected=true;
	 					  				}
	 							  	i=i+1;
	 							</s:iterator>
	        	        	}
        			};

       function disable(){
            		var value= dojo.byId("assignedTo").value;
            	  	 
            		if(value=='assign To Loa Scheme'){
            			dojo.byId("userCluster").disabled = true;
            		}else if(value=='assign' || value=='not Assign'){
            	     	dojo.byId("loaScheme").disabled = true;
            		}
               	}    
    </script>


</head>
<form name="baseForm" id="baseForm">
    <div style="width:100%;height:100%;overflow-y:auto;overflow-x:hidden">
    
<s:if test="!actionMessages.empty">
    <script type="text/javascript">

        dojo.addOnLoad(function () {
            manageTableRefresh("domainRuleTable", true);

            var messageSection = dojo.byId("messageSection");
            setTimeout(function() {
                dojo.fx.html.wipeOut(messageSection, 400, null, function() {
                    dojo.dom.destroyNode(messageSection);
                }).play();
            }, 2000);
        });

    </script>
 
    <div id="messageSection" >
        <table width="100%" border="0" cellspacing="0" cellpadding="0"
               class="successTablebg" align="center" >
            <tr>
                <td valign="top">
                    <table width="100%" border="0" cellspacing="3"
                           cellpadding="3"
                           class="successbg">
                        <tr>
                            <td width="4%" class="successTextbold">
                                <div align="center"><img
                                        src="image/applicationSuccessIcon.gif"
                                        width="16" height="16"/></div>
                            </td>
                            <td width="12%" class="successTextbold"><s:text name="message.common.success"/></td>
                            <td class="successTextnormal"><s:actionmessage/></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</s:if>
<s:actionerror/>

<div class="policy_section_div">
<div class="admin_section_heading" >
    <s:text name="label.manageBusinessRule.ruleDetails"/>
</div>
<table  border="0" cellspacing="0" cellpadding="0"
       class="bgColor" >
      
    <tr >
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle" style="padding-left:5px;">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            <td width="70%" height="19" class="labelNormal" style="padding-left:5px;">
                <s:textfield name="rule.ruleNumber" cssClass="txtField" readonly = "true" id="ruleNumber"/>
                <s:hidden name="context" />
            </td>
            <td width="70%"></td>
    </tr>   
    <tr >
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle" style="padding-left:5px;">
	            <s:text name="label.manageBusinessRule.version"/>
	        </td>
            <td width="70%" height="19" class="labelNormal">
	        <s:label  name="currentVersion"  />
            </td>
            <td width="70%"></td>
    </tr>   	        
    
    <tr >
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle" style="padding-left:5px;">
            <s:text name="label.manageBusinessRule.ruleName"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="ruleAuditName" cssClass="txtField" id="ruleName" value="%{rule.getName()}"/>
        </td>
        <td width="30%"></td>
    </tr>
    <tr>
        <td height="3" colspan="3"></td>
    </tr>
    <tr >
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle" style="padding-left:5px;">
            <s:text name="label.manageBusinessRule.failureMessage"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="ruleAuditFailureMessage" cssClass="txtField"
                         id="failureMessage" value="%{ruleAudit.failureMessage}"/>
        </td>
        <td width="30%"></td>
    </tr>
    <s:if test="doesContextUseRuleGroup()">
        <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleGroup"/>:
            </td>
            <td height="29" class="labelNormal">
                <s:select  name="ruleGroup" emptyOption="false" value="%{rule.ruleGroup.id.toString()}" 
                    list="ruleGroupsInContext" listKey="id" listValue="name"/>
            </td>
        </tr>
        </s:if>
    <tr >
        <td height="19" colspan="2" nowrap="nowrap" class="labelStyle" style="padding-left:5px;">
            <s:text name="label.manageBusinessRule.comment"/>
        </td>
        <td width="70%" height="19" class="labelNormal">
            <s:textfield name="rule.d.internalComments" cssClass="txtField"
                         id="comments"  value="%{rule.d.internalComments}"/>
        </td>        
    </tr>    
   <tr><td style="padding:0">&nbsp;</td></tr>
</table>
</div>


<div class="admin_section_div" style="margin:5px;">

    <div class="admin_section_heading">
        <s:text name="label.manageBusinessRule.businessCondition"/>
    </div>
    <table width="100%" class="grid borderForTable" cellspacing="0" cellpadding="0" align="center">
        <tr>
            <th width="15%"  class="colHeader"></th>
            <th  class="colHeader"><s:text name="columnTitle.manageBusinessRule.expression"/></th>
            <th width="15%"  class="colHeader"></th>
            <th align="center" colspan="2"  class="colHeader"><s:text name="columnTitle.manageBusinessRule.businessAction"/></th>
        </tr>
        <tr>
            <td class="admin_selections" align="center"><s:text name="label.manageBusinessRule.if"/></td>
            <td class="admin_selections"><s:property value="rule.predicate.name"/></td>
            <td class="admin_selections" align="center"><s:text name="label.manageBusinessRule.then"/></td>

            <td class="admin_selections">
                <select id="assignedTo" name="result" onchange="showUsers()">
                    <s:if test='rule.action.state.equals("assign")'>
                        <option value="assign" selected="selected"><s:text
                                name="dropdown.manageBusinessRule.assignToUserGroup"/></option>
                        <option value="assign To Loa Scheme"><s:text
                                 name="dropdown.manageBusinessRule.assignToLoaScheme"/></option>
                        <option value="not Assign"><s:text name="dropdown.manageBusinessRule.notAssignTo"/></option>
                    </s:if>
                     <s:elseif test='rule.action.state.equals("assign To Loa Scheme")'>
                          <option value="assign"><s:text name="dropdown.manageBusinessRule.assignToUserGroup"/></option>
                          <option value="assign To Loa Scheme" selected="selected"><s:text
                                 name="dropdown.manageBusinessRule.assignToLoaScheme"/></option>
                          <option value="not Assign"><s:text
                                name="dropdown.manageBusinessRule.notAssignTo"/></option>
                    </s:elseif>
                    <s:else>
                          <option value="assign"><s:text name="dropdown.manageBusinessRule.assignToUserGroup"/></option>
                          <option value="assign To Loa Scheme"><s:text
                                 name="dropdown.manageBusinessRule.assignToLoaScheme"/></option>
                          <option value="not Assign" selected="selected"><s:text
                                name="dropdown.manageBusinessRule.notAssignTo"/></option>
                    </s:else>
                </select>
            </td>
            <td class="admin_selections">
        	            <select id="userCluster" name="actionId" style="display: none;">
								<s:iterator value="userClusters">
									<option value="<s:property value="name"/>" ><s:property
										value="name" /></option>
								</s:iterator>
						</select>
						<select id="loaScheme" name="actionId" style="display: none;">
								<s:iterator value="loaSchemes">
									<option value="<s:property value="name"/>" ><s:property
										value="name" /></option>
								</s:iterator>
						</select>
						   <script type="text/javascript">
				                dojo.addOnLoad(function() {
				                        showUsers();
				                        preSelect();
				                    });
                		   </script>
            </td>
        </tr>
    </table>
    <s:hidden name="id" value="%{rule.id}"/>
	<s:hidden name="ruleId" value="%{ruleId}"/>
    <s:hidden name="activeFlag" value="ACTIVE" id="activeFlagInd"/>
		
</div>
<%@ include file="include_rule_audit_history.jsp" %>
<div align="center" style="margin-top:10px;">
			<s:if test='"INACTIVE".equals(rule.status)'>           
            <s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.activate')}"
                      action="update_processor_routing_rule" onclick="disable()"/>
            </s:if>                      
            <s:else>
            <script type="text/javascript">
				dojo.addOnLoad(function () {
					dojo.connect(dojo.byId('deactivate_rule'),'onclick',deActivateRule);
				});
			</script>
            <s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.updateRule')}"
                      action="update_processor_routing_rule" onclick="disable()"/>          
           <%-- Fix for NMHGSLMS-1171 
            <t:button cssClass="buttonGeneric" label="%{getText('button.manageBusinessRule.deActivate')}"
            		id="deactivate_rule" value="%{getText('button.manageBusinessRule.deActivate')}"/> --%>
            		
            <input type="button"class="buttonGeneric" id="deactivate_rule" value="<s:text name="button.manageBusinessRule.deActivate"/>"/>   
            </s:else>   
            <s:submit id="i18nRuleDescrpt" cssClass="buttonGeneric" action="save_i18n_Rule_Description"
                      value="%{getText('button.manageBusinessRule.addI18nRuleDescription')}"/>
     
        </div>
</div>
</form>
<authz:ifPermitted resource="warrantyAdminClaimProcessorRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="warrantyAdminDSMRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="warrantyAdminAdvisorRoutingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>