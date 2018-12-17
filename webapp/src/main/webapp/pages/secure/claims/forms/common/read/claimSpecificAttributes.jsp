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
  @author sushma.manthale
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
 
<table width="100%" border="0"  cellpadding="0" cellspacing="0" class="grid">
   <tbody>
	  <s:if test="!claim.state.state.equalsIgnoreCase('Draft')">
		     <s:set name="attributesList" value="%{claim.claimAdditionalAttributes}"/>
	 </s:if>
	<s:else>
		<s:set name="attributesList" value="%{claimSpecificAttributes}"/>
	 </s:else>
		<s:iterator value="attributesList" status="attribute" id ="attr">
		   <s:if test="#attribute.odd == true ">
						<tr>
					</s:if> 
		      <s:hidden name="claim.claimAdditionalAttributes[%{#attribute.index}].attributes" 
		        	value="%{claimSpecificAttributes[#attribute.index].attributes.id}"/>
		     <td width ="10%" class="label">
		        <s:label
						value="%{attributes.name}" cssClass="labelStyle" />
		     </td>
		     <td width ="10%" class="labelNormal">
		        <s:if test="attrValue.length()>70">
				<s:textarea theme="simple" value="%{attrValue}"  readOnly="true" rows="6" cols="70"/>
			    </s:if> <s:else>
			    <s:property value="attrValue" />
			   </s:else>
		        		 				 	
		    </td>
		  <s:if test="#attribute.odd == false ">
						</tr>
					</s:if> 
		</s:iterator>
   </tbody>
</table>
		         