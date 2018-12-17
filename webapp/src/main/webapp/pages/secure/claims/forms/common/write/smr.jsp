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

<table cellspacing="0" cellpadding="0"  class="grid borderForTable" align="center" style="margin:5px;width:95%;" id="smr_reason">
 <thead>
  <tr class="row_head">
  <th><s:text name="label.viewClaim.smrReason"/></th>
  <th><s:text name ="label.common.additionalAttributes"/></th>
  </tr>
  </thead>
    <tbody>
    <tr>
         <td  valign="middle">
           <s:property value="task.claim.reasonForServiceManagerRequest.description"/>
        </td>
       
         <td style="border-right:1px solid #EFEBF7;">
      
      		<s:if test="smrClaimAttributes != null && ! smrClaimAttributes.empty">
		       
        		<a cssStyle="cursor:pointer">
        		<span id="smr_enter_attribute<s:property value="%{#claimAdditionalAttributes.index}"/>">
        			<s:text name="label.additionalAttribute.enterAttribute"/>
        		</span>
        		</a>
        		 <script type="text/javascript">
				dojo.addOnLoad(function(){	
					var itemCounter='<s:property value="%{#claimAdditionalAttributes.index}"/>'
					dijit.byId("smr_attribute_"+itemCounter).formNode=document.getElementById("claim_form");
					dojo.connect(dojo.byId("smr_enter_attribute"+itemCounter),"onclick",function(){
					dojo.publish("/attribute"+itemCounter+"/show");
				});
					dojo.subscribe("/attribute"+itemCounter+"/show", null, function() {
					dijit.byId("smr_attribute_"+itemCounter).show();
		    	}); 
				    dojo.subscribe("/attribute"+itemCounter+"/hide", null, function() {
					dijit.byId("smr_attribute_"+itemCounter).hide();
		    	}); 
		    		dojo.connect(dojo.byId("closePopup"+itemCounter),"onclick",function() {
		            dojo.publish("/attribute"+itemCounter+"/hide");
						});      
		})	;
		</script> 
 	 
        <div id = "dialogBoxContainer" style="display: none">
	<div dojoType="twms.widget.Dialog" id="smr_attribute_<s:property value="%{#claimAdditionalAttributes.index}"/>" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250" style="width:550px; height:300px;" title="<s:text name="label.common.additionalAttributes"/>">
		<div  style="padding: 0px; margin: 0px; background: #F3FBFE border: 1px solid #EFEBF7">
		 	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" >
							<table width="100%" border="0"  cellpadding="0" cellspacing="0" class="grid">
		        				<tbody>
		        			<s:if test="!task.claim.state.state.equalsIgnoreCase('Draft')">
		      		             <s:set name="attributesList" value="%{task.claim.claimAdditionalAttributes}"/>
		                  </s:if>
		                   <s:else>
		                  		<s:set name="attributesList" value="%{smrClaimAttributes}"/>
		                   </s:else>
		        		 		<s:iterator value="attributesList" status="attribute" id ="smrattr">
		        		 		<tr>
		        		 			<s:hidden name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attributes" 
		        		 				value="%{smrClaimAttributes[#attribute.index].attributes.id}"/>
		        		 			<td width ="10%" class="label">
		        					 <s:property value="smrClaimAttributes[#attribute.index].attributes.name" />
		        		 			</td>
		        		 			<td width ="10%" class="labelNormal">
		        		 				 	<s:if test="smrClaimAttributes[#attribute.index].attributes.attributeType.equals('Number') ||
		        		 						smrClaimAttributes[#attribute.index].attributes.attributeType.equals('Text') ">
		        		 							<s:textfield theme="simple" name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue"
		        		 								value="%{attrValue}"/>
		        		 					</s:if>
		        		 					<s:elseif test="smrClaimAttributes[#attribute.index].attributes.attributeType.equals('Text Area')">
		        		 						<t:textarea theme="simple" name="task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue"
		        		 							value="%{attrValue}"	/>
		        		 					</s:elseif>
		        		 					<s:elseif test="smrClaimAttributes[#attribute.index].attributes.attributeType.equals('Date')">
		        		 						<sd:datetimepicker name='task.claim.claimAdditionalAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
		        		 					</s:elseif>
		        		 			
		        		 			</td>
		        		 		</tr>
		        		 </s:iterator>
		        		 </tbody>
		        	</table>
		        	<table width="95%">
 					<tr>
 					<td width="30%">&nbsp;</td>
       					<td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
       				
            			<input type="button" id="closePopup<s:property value="%{#claimAdditionalAttributes.index}"/>"  value='<s:text name="button.common.continue"/>'/>
            		
            			
            			</td>
        			</tr>
					</table>
		        	</div>
	</div>
</div>
</div> 
        	</s:if>
        </td>
    </tr>  
</table>       