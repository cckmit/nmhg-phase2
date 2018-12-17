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
<s:form action="updateAttributeName.action"> 
<s:hidden name="showI18nButton" value="true"/>
<s:hidden name="id" value="%{additionalAttributes.id}"></s:hidden>
<div id="seperator"></div>
	<table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">
			<tr><div  class="section_header">        
        	  		<s:text name="label.common.partReturnSearch"/>
       			</div>	
      		 </tr>
	  <s:iterator value="locales"  status="itr" id="localesItr">
	     	 <tr>
	     		 <td  width="30%"  class="label">
	             	   <s:text name="label.additionalAttribute.name"/> <s:property value='description'/>
	     	 	</td>
		 	 	<td  class="labelNormal">
					<s:set name="additionalAttributesName" value=""/>
					<s:iterator value="additionalAttributes.i18NAdditionalAttributeNames">		
					<s:if test="locales[#itr.index].locale == locale">
					    <s:set name="additionalAttributesName" value="name" />	
					 </s:if>
				   </s:iterator>
	         			<s:textfield name="localizedFailureMessages_%{locales[#itr.index].locale}" value="%{additionalAttributesName}"/>     
			 	</td>
	        </tr>
	 	</s:iterator>
	 	
	 </table>
	 <div id="seperator"></div>
	 <table width="98%"  border="0" cellspacing="0" cellpadding="0">
	 <tr>
	 		<td align="center"><s:submit cssClass="buttonGeneric" name="label.common.submit" /> </td>
	 	</tr>
	 </table>	
 </s:form>
 </u:body>