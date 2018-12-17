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
<script type="text/javascript">
dojo.addOnLoad(function() {	
 dojo.subscribe("/fault/attribute/hide", null, function() {
    dijit.byId("faultCodeAtrribute").hide();
 });
 dojo.connect(dojo.byId("closeFaultCodeAttrPopup"),"onclick",function() {
    dojo.publish("/fault/attribute/hide");
 });
 });
    

</script>

		      <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
		      	<tbody>
		      	<tr>
		      	<td>     <div id="separator"/></td>
		      	</tr>
		      	<s:if test="! task.claim.state.state.equalsIgnoreCase('Draft')">
		      		<s:set name="attributesList" value="%{task.claim.serviceInformation.faultClaimAttributes}"/>
		        </s:if>
		        <s:else>
		        		<s:set name="attributesList" value="%{claimAttributes}"/>
		        </s:else>
		       
		      		<s:iterator value="attributesList" status="attribute" >
		      	 		<tr>
		      	 			<td width ="10%" class="label">
		        				 <s:property value="attributes.name" />
		        				 <s:hidden name="task.claim.serviceInformation.faultClaimAttributes[%{#attribute.index}].attributes" value="%{attributes.id}"/>
		         			</td>
		         			<td width ="10%" class="labelNormal">
		         				<s:if test="attributes.attributeType.equals('Number') ||
		         						attributes.attributeType.equals('Text') ">
		         							<s:textfield theme="simple" name="task.claim.serviceInformation.faultClaimAttributes[%{#attribute.index}].attrValue"
		         								value="%{attrValue}"
		        					/>
		        				</s:if>
		        				<s:elseif test="attributes.attributeType.equals('Text Area')">
		           					<t:textarea theme="simple" name="task.claim.serviceInformation.faultClaimAttributes[%{#attribute.index}].attrValue"
		        						value="%{attrValue}" />
		        				</s:elseif>
		        				<s:elseif test="attributes.attributeType.equals('Date')">
		        					<sd:datetimepicker name='task.claim.serviceInformation.faultClaimAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
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
            		<input type="button" id="closeFaultCodeAttrPopup"  value='<s:text name="button.common.continue"/>'/>
            	</td>
        		</tr>
			</table>