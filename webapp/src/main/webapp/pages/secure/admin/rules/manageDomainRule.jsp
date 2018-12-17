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
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    
    <script type="text/javascript">    
        function deActivateRule() {
          var formObj = dojo.byId("baseForm");
          dojo.byId('activeFlagInd').value="INACTIVE";
          formObj.action = "update_domain_rule.action";
          formObj.submit();
        }
    </script>
    <style>
    .bgColor td{
    padding-bottom:10px;
    }
    </style>
</head>
<body class="official">
<u:actionResults/>
<s:form name="baseForm" id="baseForm">

<div class="policyRegn_section_div">
	<div class="admin_section_heading">
    	<s:text name="label.manageBusinessRule.ruleDetails"/>
 	</div>
	<table width="97%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
	   
	    <tr>
	        <td height="19"  class="labelStyle">
	            <s:text name="label.manageBusinessRule.ruleNumber"/>
	        </td>
	        <td  height="19" class="labelNormal" align="left">
	            <s:textfield  name="rule.ruleNumber" cssClass="txtField" id="ruleNumber"
                              cssStyle="font-weight: normal" size="50"/>
	            <s:hidden name="context" />
	        </td>
	    </tr>
	    <tr>
	        <td height="19"  class="labelStyle">
	            <s:text name="label.manageBusinessRule.version"/>
	        </td>
	        <td  height="19" class="labelNormal" align="left">
	            <s:label  name="ver" value="%{rule.version+1}"  />
	        </td>
	    </tr>

	    <tr >
	        <td height="19" class="labelStyle">
	            <s:text name="label.manageBusinessRule.ruleName"/>
	        </td>
	        <td height="19" class="labelNormal">
	            <s:textfield name="ruleAuditName" cssStyle="font-weight: normal" cssClass="txtField" id="ruleName"
                             value="%{ruleAudit.getName()}" size="50"/>
	        </td>
	    </tr>

	    <tr>
	        <td height="19"  class="labelStyle">
	            <s:text name="label.manageBusinessRule.failureMessage"/>
	        </td>
	        <td height="19" class="labelNormal">
	            <s:textfield name="ruleAuditFailureMessage" cssClass="txtField" cssStyle="font-weight: normal"
	                         id="failureMessage" value="%{ruleAudit.failureMessage}" size="50"/>
	        </td>
	    </tr>
        <tr>
            <td height="19" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.priority"/>:
            </td>
            <td height="19" class="labelNormal">
                <s:property value="rule.priority" />
            </td>
        </tr>
        <s:if test="doesContextUseRuleGroup()">
        <tr>
            <td height="19" nowrap="nowrap" class="labelStyle">
                <s:text name="label.manageBusinessRule.ruleGroup"/>:
            </td>
            <td height="19" class="labelNormal">
                <%-- Do not remove the toString() used in the  value="..". This is to bypass the weird issue that it
                  was formatting the value using comma (i.e. 1400 was getting printed as 1,400)! --%>
                <s:select name="ruleGroup" theme="twms" value="%{rule.ruleGroup.id.toString()}"
                          list="ruleGroupsInContext" listKey="id" listValue="name"/>
            </td>
        </tr>
        </s:if>
        <tr >
	        <td height="19"  class="labelStyle">
	            <s:text name="label.manageBusinessRule.comment"/>
	        </td>
	        <td height="19" class="labelNormal">
	        <s:if test="hasActionErrors()">
	          <s:textfield name="rule.d.internalComments" cssClass="txtField" cssStyle="font-weight: normal"
	                         id="comments"  value="%{rule.d.internalComments}" size="50"/>
	                         </s:if>
	                         <s:else>
	            <s:textfield name="rule.d.internalComments" cssClass="txtField" cssStyle="font-weight: normal"
	                         id="comments"  value="" size="50"/></s:else>
	        </td>
	    </tr>
	    <tr >
	    <td height="19"  class="labelStyle">
	            <s:text name="columnTitle.manageBusinessRule.businessAction"/>
	        </td>
	      <td height="19" class="labelNormal">
	                <select name="actionId" id="action1">
	                    <s:iterator value="actions">
	                       <s:if test="name == selectedAction">
	                            <option value="<s:property value="id"/>" selected="selected"><s:property
	                                    value="name"/></option>
	                        </s:if>
	                        <s:else>
	                            <option value="<s:property value="id"/>"><s:property value="name"/></option>
	                        </s:else>
	                    </s:iterator>
	                </select>

	                <script type="text/javascript">
                 	dojo.addOnLoad(function(){
                			var value= dojo.byId("action1").value;
                			if(value==1){
				          		dojo.html.show(dojo.byId("rejectionLabel"));
				          		dojo.html.show(dojo.byId("rejectionList"));
				          		dojo.html.show(dojo.byId("i18nFailureMsg"));

				          	}else{
				          		dojo.html.hide(dojo.byId("i18nFailureMsg"));
				          	}
				      dojo.connect(dojo.byId("action1"),"onchange",function(){
				          	var value= dojo.byId("action1").value;
				          	if(value==1){
				          		dojo.html.show(dojo.byId("rejectionLabel"));
				          		dojo.html.show(dojo.byId("rejectionList"));
				          		dojo.html.show(dojo.byId("i18nFailureMsg"));
				          	}else{
				          		dojo.html.hide(dojo.byId("rejectionLabel"));
				          		dojo.html.hide(dojo.byId("rejectionList"));
				          		dojo.html.hide(dojo.byId("i18nFailureMsg"));
				          	}
				      })
				   });
				</script>
	            </td>
	   </tr>
	    <tr >
	       <td height="19"  class="labelBold" style="display:none" id="rejectionLabel">
		     <s:text name="label.rules.rejectionReasons"/>
		   </td>
		   <td style="border-bottom:1px solid #FCF9F3;display:none" id="rejectionList">
               <s:select list="listOfRejectionReasons" listKey="id" listValue="description"
                         value="%{rule.rejectionReason.id}" emptyOption="true" name="rejectedReason" 
                         id="rejectedReason" />
             </td>
             <script type="text/javascript">
			  	dojo.addOnLoad(function() {
			  		dojo.byId("rejectedReason").value='<s:property value="rule.rejectionReason.id"/>';
			  	});
			  </script>
	        </tr>
	        <tr><td style="padding:0">&nbsp;</td></tr>
	</table>
	<div>
	<div id="seperator"></div>
	<div class="admin_section_div">
    	<div class="admin_section_heading">
        	<s:text name="label.manageBusinessRule.businessCondition"/>
    	</div>
    	<table class="grid borderForTable" cellspacing="0" cellpadding="0">
	        <tr align="left">
	            
	            <td width="20%" class="labelStyle"><s:text name="columnTitle.manageBusinessRule.expression"/>:</td>
	            <td class="labelNormal">
	            <s:property value="rule.predicate.name"/></td>
	        </tr>

    	</table>
    	<s:hidden name="id" value="%{rule.id}"/>
    	<s:hidden name="activeFlag" value="ACTIVE" id="activeFlagInd"/>    	
    	<s:hidden name="ruleId" value="%{ruleId}" />
    </div>
	</div>
</div>
<div>
<%@ include file="include_rule_audit_history.jsp" %>
</div>
<div style="width:100%;">
<table width="100%" cellspacing="0" cellpadding="0" bgcolor="white">
    <tr>
        <td align="center" >
           	<s:if test='"INACTIVE".equals(rule.status)'>           
            	<s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.activate')}"
                      action="update_domain_rule"/>
            </s:if>
            <s:else>
            	<s:submit cssClass="buttonGeneric"
                      value="%{getText('button.manageBusinessRule.updateRule')}"
                      action="update_domain_rule"/>
                 <input type="button" class="buttonGeneric"
                      value="<s:text name="button.manageBusinessRule.deActivate"/>" onclick="deActivateRule()"/>
            </s:else>
           <s:if test='context.equals("ClaimRules")'>
             <s:submit id="i18nFailureMsg" cssClass="buttonGeneric" action="save_i18n_failure_messages"
                      value="%{getText('button.manageBusinessRule.addI18nFailureMessages')}"/>
             <s:submit id="i18nRuleDescrpt" cssClass="buttonGeneric" action="save_i18n_Rule_Description"
                      value="%{getText('button.manageBusinessRule.addI18nRuleDescription')}"/>
           </s:if>
           <s:if test='context.equals("EntryValidationRules")'>
             <s:submit id="i18nFailureMsgasd" cssClass="buttonGeneric" action="save_i18n_failure_messages"
                      value="%{getText('button.manageBusinessRule.addI18nFailureMessages')}"/>
             <s:submit id="i18nRuleDescription" cssClass="buttonGeneric" action="save_i18n_Rule_Description"
                      value="%{getText('button.manageBusinessRule.addI18nRuleDescription')}"/>
           </s:if>
           
        </td>
    </tr>
</table>
</div>

</s:form>
<authz:ifPermitted resource="warrantyAdminClaimsProcessingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="warrantyAdminEntryValidationsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</body>
</html>