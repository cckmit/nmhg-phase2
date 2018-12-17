<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
 </head>
 <script type="text/javascript">  	  	
      	dojo.require("dijit.layout.LayoutContainer"); 
 </script>
<u:actionResults/>
<u:body smudgeAlert="false">
<s:form action="updateI18nTerms.action"> 
<s:hidden name="policyDefinitionId"></s:hidden>
	<table  border="0" cellspacing="0" cellpadding="0" class="bgColor" style="margin:5px; width:98.5%">
			<tr><div  class="section_header">        
        	  		<s:text name="label.policy.internationalize"/>
       			</div>	
      		 </tr>
			 <tr><td style="padding:0 ">&nbsp;</td></tr>
	  <s:iterator value="locales"  status="itr" id="localesItr">
	     	 <tr>
	     		 <td  width="30%"  class="labelStyle">
	             	   <s:text name="label.policy.termsAndConditions"/> <s:property value='description'/>
	     	 	</td>
		 	 	<td  class="label">
					<s:set name="termsAndCondition" value=""/>
					<s:iterator value="policyDefinition.i18NPolicyTermsAndConditions">		
					<s:if test="locales[#itr.index].locale == locale">
					    <s:set name="termsAndCondition" value="termsAndConditions" />	
					 </s:if>
				   </s:iterator>
	         			<t:textarea name="localizedFailureMessages_%{locales[#itr.index].locale}" value="%{termsAndCondition}" cols="40" rows="4"/>     
			 	</td>
	        </tr>
	 	</s:iterator>
	 	<tr>
	 		<td style="padding:5px 0px 5px 0px;" align="center" colspan="2"><s:submit cssClass="buttonGeneric" name="label.common.submit" /> </td>
	 	</tr>
		<tr><td style="padding:0 ">&nbsp;</td></tr>
	 </table>	
 </s:form>
 <authz:ifPermitted resource="warrantyAdminPolicyDefinitionReadOnlyView">
				<script type="text/javascript">
				    dojo.addOnLoad(function() {
				        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
				            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
				        }
				    });
				</script>
			</authz:ifPermitted>
 </u:body>