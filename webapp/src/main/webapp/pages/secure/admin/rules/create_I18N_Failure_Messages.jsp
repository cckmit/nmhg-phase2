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
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
<script>
  var failureMessage;
</script>
</head>
<u:actionResults/>
<form action="save_failure_messages.action" name="saveForm" id="saveForm" method="POST">
<div class="admin_section_heading">
    	<s:text name="label.manageBusinessRule.createI18NFailureMessage"/>
</div>

<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
        <tr style="padding-top:5px;padding-bottom:5px;">
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.manageBusinessRule.ruleNumber"/>
            </td>
            
            <td height="19" class="labelNormal" >
                <s:textfield name="domainRule.ruleNumber" cssClass="txtField" id="ruleNumber" readonly="true"/>
            </td>
           </tr>
           <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="columnTitle.manageBusinessRule.history.ruleDescription"/>
            </td>            
            <td height="19" class="labelNormal" >
                <s:property value="domainRule.predicate.name" />
            </td>
            <td width="90%"></td>
        </tr>
        
        <s:iterator value="locales"  status="itr" id="localesItr">
           <tr>
            <td height="3" colspan="3"></td>
          </tr>
          <tr>
            <td height="19" colspan="2" nowrap="nowrap" class="label">
                <s:text name="label.manageBusinessRule.failure"/> <s:property value='description'/>
            </td>
			<td width="70%" height="19" class="labelNormal">
			<s:set name="selectedFailureDesc" value=""/>
			<s:iterator value="domainRule.domainRuleTexts">		
				<s:if test="locales[#itr.index].locale == locale">
				    <s:set name="selectedFailureDesc" value="failureDescription" />
				</s:if>
			</s:iterator>
         	<t:textarea cols="40" rows="3" name="localizedFailureMessages_%{locales[#itr.index].locale}" value="%{selectedFailureDesc}"/>
           	</td>
            <td width="30%"></td>
          </tr>
        </s:iterator>
</table>
<s:hidden name="ruleId" value="%{ruleId}" />
<table width="100%" cellspacing="0" cellpadding="0" bgcolor="white" style="margin-top:15px">
    <tr>
        <td align="center">  
          <s:submit cssClass="buttonGeneric" action="save_failure_messages" 
                      value="%{getText('label.common.submit')}"/>
         </td>
    </tr>
</table> 
</form>    
</html>                 