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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<style type="text/css">
    .separator {
        margin-left: 17px;
    }
</style>

<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
	 dojo.require("twms.widget.TitlePane");
	 dojo.require("dijit.Tooltip");
</script>

<authz:ifProcessor>
    <s:if test="claim.forMultipleItems && processorReview">
        <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                var multiCheckBoxControl = new CheckBoxListControl(dojo.byId("masterCheckbox"));

                var rowCount = <s:property value="claim.claimedItems.size" />;
				dojo.connect(dojo.byId("masterCheckbox"), "onclick", function(event) {
					if(event.target.checked){
				    	for (var i = 0; i < rowCount; i++) {
			                var currentElement = dojo.byId(String(i));
			                currentElement.checked=true;
						}
					}else{
						for (var j = 0; j < rowCount; j++) {
							var currentElement = dojo.byId(String(j));
							currentElement.checked=false;
						}
					}					
			   	});
				
				var hoverMessage = new dijit.Tooltip({
					connectId: ["myElement1","myElement2"]
				});
            });
        </script>
    </s:if>
</authz:ifProcessor>


<table  cellspacing="0" cellpadding="0"   class="grid borderForTable" align="center" style="margin:5px;width:95%;">
    <thead>        
        <tr class="row_head">
            <authz:ifProcessor>
                <s:if test="claim.forMultipleItems && processorReview">
                    <th align="center">
                        <s:text name="label.common.approve"/>
                        <div class="separator">
                            <input type="checkbox" id="masterCheckbox" />
                        </div>
                    </th>
                </s:if>
            </authz:ifProcessor>
            <th ><s:text name="label.common.serialNumber"/></th>
            <th><s:text name="label.common.product"/></th>    
            <th><s:text name="label.common.model"/></th>
            <th ><s:text name="label.common.seriesDescription"/></th>
            <th><s:text name="label.common.hoursOnTruck"/></th>
            <authz:ifUserNotInRole roles="supplier">
	            <s:if test="claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">                         
		             <th ><s:text name="label.common.warrantyStartDate"/></th>             
		             <th><s:text name="label.common.warrantyEndDate"/></th>           
	            </s:if>
            </authz:ifUserNotInRole>
            <th><s:text name="label.deliveryDate"/></th>
            <th><s:text name ="label.common.additionalAttributes"/></th>
            <s:if test="claim.forMultipleItems==true">
            	<s:if test="!claim.state.state.equalsIgnoreCase('Draft')">
            	<th ><s:text name="label.common.approved"/></th>
            	</s:if>
            </s:if>
        </tr>
    </thead>
    <tbody>
    <s:hidden id="attributes" name="attributes"/>
    <s:iterator value="claim.claimedItems" status="claimedItems">
    <tr>
    <authz:ifProcessor>
        <s:if test="claim.forMultipleItems && processorReview">
            <td align="center" valign="middle">
                <s:checkbox id="%{#claimedItems.index}"
                            name="task.claim.claimedItems[%{#claimedItems.index}].processorApproved" 
                            value="%{processorApproved}"/>
            </td>
        </s:if>
    </authz:ifProcessor>
        <td >
        	<span style="color:blue;cursor:pointer;text-decoration:underline">
        	<s:set name="itemReferred" value="itemReference.referredInventoryItem.id"/>
				<s:if test="itemReference.referredInventoryItem!=null">
                    <s:hidden name="claim.claimedItems[%{#claimedItems.index}].itemReference.referredInventoryItem"
        		    value="%{itemReference.referredInventoryItem.id}" /> 
                    <authz:ifUserInRole roles="supplier">
						<span style="color:black">
							<s:property value="itemReference.referredInventoryItem.serialNumber" />
						</span>
	        	    </authz:ifUserInRole>
    	    	    <authz:else>
 	    	    		<u:openTab cssClass="link" url="inventoryDetail.action?id=%{itemReference.referredInventoryItem.id}"
		        			id="serialLink_%{#claimedItems.index}" tabLabel="Serial Number %{itemReference.referredInventoryItem.serialNumber}"
	                		autoPickDecendentOf="true">
							<s:property value="itemReference.referredInventoryItem.serialNumber" />
			         	</u:openTab>
        		    </authz:else>
			    </s:if>
			    </span>
				<s:elseif test="partsClaim && claim.partInstalled && (claim.competitorModelBrand == null || claim.competitorModelBrand.isEmpty())">
					<s:property value="claim.itemReference.unszdSlNo" />
				</s:elseif>
		        <s:else>
		        	<s:property value="itemReference.unszdSlNo" />
		        </s:else>
	        
        </td>

        <td >
        	<s:if test="itemReference.serialized">
				<s:property value="itemReference.unserializedItem.product.groupCode" />
			</s:if>
			<s:else>
				<s:property value="itemReference.model.isPartOf.groupCode" />
			</s:else>
        </td>
        <td>        	
			<s:if test="itemReference.referredInventoryItem!=null">
        	<s:property value="itemReference.unserializedItem.model.itemGroupDescription" />
        	</s:if>
        	<s:else>
        		<s:property value="itemReference.model.itemGroupDescription" />
        	</s:else>
        </td>   
                <td >			
        	<s:if test="itemReference.serialized">
				<s:property value="itemReference.unserializedItem.product.itemGroupDescription" />
			</s:if>
			<s:else>
				<s:property value="itemReference.model.isPartOf.itemGroupDescription" />
			</s:else>        </td>  
        <td>
       		<s:textfield name="task.claim.claimedItems[%{#claimedItems.index}].hoursInService"
		        		value="%{hoursInService}" cssStyle="width:95px;"  />
        </td> 
        <authz:ifUserNotInRole roles="supplier"> 
         <s:if test="null!=itemReference.referredInventoryItem && itemReference.referredInventoryItem.isRetailed()">      	
            <td><s:property value="itemReference.referredInventoryItem.wntyStartDate" /></td>
            <td><s:property value="itemReference.referredInventoryItem.wntyEndDate" /></td>
        </s:if>
        </authz:ifUserNotInRole>
       <s:if test="null!=itemReference.referredInventoryItem && itemReference.referredInventoryItem.isRetailed()">
        <td><s:property value="itemReference.referredInventoryItem.deliveryDate" /></td>
        </s:if>
        <td style="border-right:1px solid #EFEBF7;">
        <s:if test="claimAttributes != null && ! claimAttributes.empty">
		        <s:if test="JobCodeFaultCodeEditable">
        		<a cssStyle="cursor:pointer">
        		<span id="enter_attribute<s:property value="%{#claimedItems.index}"/>">
        			<s:text name="label.additionalAttribute.enterAttribute"/>
        		</span>
        		</a>
        		</s:if>
        		<s:else>
        		<a cssStyle="cursor:pointer">
        		<span id="enter_attribute<s:property value="%{#claimedItems.index}"/>">
        			<s:text name="label.additionalAttribute.viewAdditionalAttributes"/> 
        		</span>
        		</a>
        		</s:else>
        	
        	 	<script type="text/javascript">
				dojo.addOnLoad(function(){	
					var itemCounter='<s:property value="%{#claimedItems.index}"/>'
					dijit.byId("attribute_"+itemCounter).formNode=document.getElementById("claim_form");
					dojo.connect(dojo.byId("enter_attribute"+itemCounter),"onclick",function(){
					dojo.publish("/attribute"+itemCounter+"/show");
				});
					dojo.subscribe("/attribute"+itemCounter+"/show", null, function() {
					dijit.byId("attribute_"+itemCounter).show();
		    	}); 
				    dojo.subscribe("/attribute"+itemCounter+"/hide", null, function() {
					dijit.byId("attribute_"+itemCounter).hide();
		    	}); 
		    		dojo.connect(dojo.byId("closePopup"+itemCounter),"onclick",function() {
		            dojo.publish("/attribute"+itemCounter+"/hide");
						});      
		})	;
		</script>
        
		        	<div id = "dialogBoxContainer" style="display: none"> 
					<div dojoType="twms.widget.Dialog" id="attribute_<s:property value="%{#claimedItems.index}"/>" bgColor="#FFF" bgOpacity="0.5" toggle="fade" 
						toggleDuration="250" style="width:550px; height:300px;" title="<s:text name="label.common.additionalAttributes"/>">
						<div dojoType="dijit.layout.LayoutContainer" style="height:250px;  padding: 0px; margin: 0px;">
		 				<div dojoType="dijit.layout.ContentPane" layoutAlign="top" >
							<table width="100%" border="0"  cellpadding="0" cellspacing="0" class="grid">
		        				<tbody>
		        		 		<s:iterator value="claimAttributes" status="attribute" id ="attr">
		        		 		<tr>
		        		 			<s:hidden name="task.claim.claimedItems[%{#claimedItems.index}].claimAttributes[%{#attribute.index}].attributes" 
		        		 				value="%{claimAttributes[#attribute.index].attributes.id}"/>
		        		 			<td width ="10%" class="label">
		        					 <s:property value="claimAttributes[#attribute.index].attributes.name" />
		        		 			</td>
		        		 			<td width ="10%" class="labelNormal">
		        		 				<s:if test="jobCodeFaultCodeEditable">
		        		 				 	<s:if test="claimAttributes[#attribute.index].attributes.attributeType.equals('Number') ||
		        		 						claimAttributes[#attribute.index].attributes.attributeType.equals('Text') ">
		        		 							<s:textfield theme="simple" name="task.claim.claimedItems[%{#claimedItems.index}].claimAttributes[%{#attribute.index}].attrValue"
		        		 								value="%{attrValue}"/>
		        		 					</s:if>
		        		 					<s:elseif test="claimAttributes[#attribute.index].attributes.attributeType.equals('Text Area')">
		        		 						<t:textarea theme="simple" name="task.claim.claimedItems[%{#claimedItems.index}].claimAttributes[%{#attribute.index}].attrValue"
		        		 							value="%{attrValue}"	/>
		        		 					</s:elseif>
		        		 					<s:elseif test="claimAttributes[#attribute.index].attributes.attributeType.equals('Date')">
		        		 						<sd:datetimepicker name='task.claim.claimedItems[%{#claimedItems.index}].claimAttributes[%{#attribute.index}].attrValue' value='%{attrValue}' />
		        		 					</s:elseif>
		        		 				</s:if>
		        		 				<s:else>
		        		 					<s:property value="attrValue"/>
		        		 				</s:else>
		        		 			</td>
		        		 		</tr>
		        		 </s:iterator>
		        		 </tbody>
		        	</table>
		        	<table width="95%">
 					<tr>
 					<td width="30%">&nbsp;</td>
       					<td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
       					<s:if test="claim.state.state.equalsIgnoreCase('Draft')">
            			<input type="button" id="closePopup<s:property value="%{#claimedItems.index}"/>"  value='<s:text name="button.common.continue"/>'/>
            			</s:if>
            			<s:else>
            			<input type="button" id="closePopup<s:property value="%{#claimedItems.index}"/>"  value='<s:text name="button.common.close"/>'/>
            			</s:else>
            			</td>
        			</tr>
					</table>
		        	</div>
		        	</div>
		        	</div>
		   </div>
        </s:if>
       </td>
       <s:if test="claim.forMultipleItems==true">
       		<s:if test="claim.state.state!='draft'">
       		<td>
       		<s:if test="processorApproved">
	             <span style="color:green">
	                 <s:text name="label.common.yes" />
	             </span>
	         </s:if>
	         <s:else>
	             <span style="color:red">
	                 <s:text name="label.common.no" />
	             </span>
	         </s:else>
       		</td>
       		</s:if>
	   </s:if>
	   </tr>
   </s:iterator>
   </tbody>
 </table>

<authz:ifProcessor>
 <s:if test="claim.forMultipleItems==true">
	 <table>
	    <tr>
	     <td><b><s:text  name="label.equipmentDetail.inventory_Count"/></b></td>
	     <td><b><s:property value="claim.claimedItems.size"/></b></td>
	   </tr>
	 </table>
 </s:if>
</authz:ifProcessor>

<authz:ifProcessor>
<s:if test="viewUnitComments">
<span style="color:blue;cursor:pointer;text-decoration:underline;font-style:Italic">
<s:if test="#itemReferred!=null">
 	    	    		<u:openTab cssClass="link" url="viewTruckComments.action?inventoryItemId=%{#itemReferred}"
		        			id="serialLink_%{#claimedItems.index}" tabLabel="Unit Comments %{#itemReferred}"
	                		autoPickDecendentOf="true">
							<s:text name="label.equipment.clickComments" />
			         	</u:openTab>
			         	</s:if>
			    </span>
			    </s:if>
</authz:ifProcessor>