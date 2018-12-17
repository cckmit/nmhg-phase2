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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<jsp:include page="/i18N_javascript_vars.jsp"/>

<script type="text/javascript">
dojo.addOnLoad(function(){	
	if(dojo.byId("closeJobCodeAttrPopup")) {
		dojo.connect(dojo.byId("closeJobCodeAttrPopup"),"onclick",function() {
			dojo.publish("/part/attribute/hide");
		});
	}
});
</script>

<table width="100%" style="border-bottom:1px solid #EFEBF7;">
<tbody>
 		<tr>
			<td colspan="2" nowrap="nowrap" class="sectionTitle"><s:text name="label.common.additionalAttributes"/></td>
	</tr>
</tbody>
</table>
<div id="separator"></div>
<table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
		<tbody>
		     <s:if test="claimAttributes != null && claimAttributes.size() > 0">	
		      <s:iterator value="claimAttributes" status="attribute" >
		   	 		<tr>
		   	 			<td width ="10%" class="label">
		     				 <s:property value="attributes.name" />
		      			</td>
		      			<td width ="10%" class="labelNormal">
		      				
		      					<s:if test="attributes.attributeType.equals('Number') ||
		      						attributes.attributeType.equals('Text') ">
		      							<s:textfield theme="simple" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[%{indexId}].claimAttributes[%{#attribute.index}].attrValue"
		     								value="%{attrValue}"/>
		     				</s:if>
		     				<s:elseif test="attributes.attributeType.equals('Text Area')">
		        					<t:textarea theme="simple" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[%{indexId}].claimAttributes[%{#attribute.index}].attrValue"
		     						value="%{attrValue}"/>
		     				</s:elseif>
		     				<s:elseif test="attributes.attributeType.equals('Date')">
		     					<sd:datetimepicker name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[%{indexId}].claimAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
		     				</s:elseif>
		     				<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[%{indexId}].claimAttributes[%{#attribute.index}].attributes" value="%{attributes}"></s:hidden>
		     				
		    			</td>
		    		</tr>	
	</s:iterator>
	</s:if>
 </tbody>
</table>
<div align="center">
	<input type="button" id="closeReplacedPartAttrPopup_<s:property value="indexId"/>" class="buttonGeneric" value='<s:text name="button.common.continue"/>'/>
</div>