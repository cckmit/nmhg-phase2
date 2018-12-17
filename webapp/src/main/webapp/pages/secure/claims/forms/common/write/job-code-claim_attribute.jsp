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

		      <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
		      	<tbody>
		      		<s:iterator value="claimAttributes" status="attribute" >
		      	 		<tr>
		      	 			<td width ="10%" class="label">
		        				 <s:property value="attributes.name" />
		        				<s:hidden name="task.claim.serviceInformation.serviceDetail.laborPerformed[%{indexId}].claimAttributes[%{#attribute.index}].attributes" 
		        				value="%{attributes.id}" />
		         			</td>
		         			<td width ="10%" class="labelNormal">
		         					<s:if test="attributes.attributeType.equals('Number') ||
		         						attributes.attributeType.equals('Text') ">
		         							<s:textfield theme="simple" name="task.claim.serviceInformation.serviceDetail.laborPerformed[%{indexId}].claimAttributes[%{#attribute.index}].attrValue"
			        								value="%{attrValue}" />
			        				</s:if>
			        				<s:elseif test="attributes.attributeType.equals('Text Area')">
			           					<s:textarea theme="simple" name="task.claim.serviceInformation.serviceDetail.laborPerformed[%{indexId}].claimAttributes[%{#attribute.index}].attrValue"
			        					value="%{attrValue}" />
			        				</s:elseif>
			        				<s:elseif test="attributes.attributeType.equals('Date')">
			        					<sd:datetimepicker name='task.claim.serviceInformation.serviceDetail.laborPerformed[%{indexId}].claimAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
			        				</s:elseif>

		       			</td>
		       		</tr>
		       		
		       	 </s:iterator>
		        </tbody>
		      </table>
		      <table width="100%" cellpadding="0" cellspacing="0">
 				<tr>
 				<td width="30%">&nbsp;</td>
       				<td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
            		<input type="button" id="jobCodeAttrPopupClose_<s:property value="indexId"/>"  value='<s:text name="button.common.continue"/>'/>
            	</td>
        		</tr>
			</table>
			
			
