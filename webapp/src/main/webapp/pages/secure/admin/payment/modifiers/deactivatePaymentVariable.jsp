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
<s:form action="deactivatePaymentVariable.action"> 
<s:hidden name="paymentVariableId" ></s:hidden>
<div class="policy_section_div" style="width:100%;">
	<div  class="section_header">        
        	  		<s:text name="message.manageModifier.modifierDetails"/>
       			</div>	
	<table width="50%"  border="0" cellspacing="0" cellpadding="0" class="grid">
		
	     	 <tr>
	     		 <td  width="20%"  class="labelStyle" nowrap="nowrap">
	             	   <s:text name="label.managePayment.modifierName"/>
	     	 	</td>
	     		 <td  width="30%"  class="label">
					   <s:property value='paymentVariable.name'/>
	     	 	</td>
	        </tr>
	     	 <tr>
	     		 <td  width="20%"  class="labelStyle" nowrap="nowrap">
	             	   <s:text name="label.managePayment.section"/>
	     	 	</td>
	     		 <td  width="30%"  class="label">
					   <s:property value='paymentVariable.section.name'/>
	     	 	</td>
	        </tr>
	 </table>
	 </div>
	 <s:if test = "!isDeleted()">
		<div align="center" style="margin-top:10px;">
		<s:submit cssClass="button" align="center" value="%{getText('button.common.delete')}"></s:submit>
		</div>
	 </s:if>
 </s:form>
 </u:body>