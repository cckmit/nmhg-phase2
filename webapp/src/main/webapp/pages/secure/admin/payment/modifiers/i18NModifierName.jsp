<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
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
<s:form action="updateModifierName.action"> 
<s:hidden name="paymentVariableId" ></s:hidden>
	<div style="margin:5px;width:100%">
	<table width="50%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">
			<tr><div  class="section_header">        
        	  		<s:text name="label.managePayment.modifierName"/>
       			</div>	
      		 </tr>
	  <s:iterator value="locales"  status="itr" id="localesItr">
	     	 <tr>
	     		 <td  width="30%"  class="labelStyle">
	             	   <s:text name="label.managePayment.modifierName"/> <s:property value='description'/>
	     	 	</td>
		 	 	<td  class="labelNormal">
					<s:set name="modifierName" value=""/>
					<s:iterator value="paymentVariable.i18NModiferNames">		
					<s:if test="locales[#itr.index].locale == locale">
					    <s:set name="modifierName" value="name" />	
					 </s:if>
				   </s:iterator>
	         			<s:textfield name="localizedNames_%{locales[#itr.index].locale}" value="%{modifierName}"/>     
			 	</td>
	        </tr>
	 	</s:iterator>
	 	
	 		 
	 	
	 </table>
	 </div>
	 <div align="center" style="margin-top:10px;">
	 <s:submit cssClass="buttonGeneric" value="%{getText('label.common.submit')}"></s:submit>
	 </div>	
 </s:form>
 </u:body>