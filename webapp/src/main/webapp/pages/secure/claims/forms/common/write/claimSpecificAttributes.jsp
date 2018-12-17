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
<%--
  @author Sushma.manthale
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<table width="100%" border="0"  cellpadding="0" cellspacing="0" class="grid">
 <tbody>
  <s:if test="!task.claim.state.state.equalsIgnoreCase('Draft')">
		<s:set name="attributesList" value="%{task.claim.claimAdditionalAttributes}"/>
  </s:if>
	<s:else>
		<s:set name="attributesList" value="%{claimSpecificAttributes}"/>
	</s:else>
		 <s:iterator value="attributesList" status="attribute" id ="Claimattr">
		    <s:if test="#attribute.odd == true ">
						<tr>
					</s:if> 
		        <s:hidden name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attributes" 
		           value="%{claimSpecificAttributes[#attribute.index].attributes.id}"/>
		          <td width ="10%" class="label">
		          <s:label
						value="%{attributes.name}" cssClass="labelStyle" />
		          </td>
		        <td width ="10%" class="labelNormal">
		        	<s:if test="claimSpecificAttributes[#attribute.index].attributes.attributeType.equals('Number') ||
		        		 	claimSpecificAttributes[#attribute.index].attributes.attributeType.equals('Text') ">
		        	<s:textfield theme="simple" name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue"
		        		 								value="%{attrValue}"/>
		        	</s:if>
		        	<s:elseif test="claimSpecificAttributes[#attribute.index].attributes.attributeType.equals('Text Area')">
		        		<t:textarea theme="simple" name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue"
		        		 							value="%{attrValue}"	/>
		        	</s:elseif>
		        	<s:elseif test="claimSpecificAttributes[#attribute.index].attributes.attributeType.equals('Date')">
		        		 <sd:datetimepicker name='task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
		        	</s:elseif>
		        		 			
		        </td>
		       <s:if test="#attribute.odd == false ">
						</tr>
					</s:if> 
		 </s:iterator>
  </tbody>
</table>



        	

   