<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ page contentType="html;charset=UTF-8" language="java"%>


<tr>
	<td>
	<s:select name="%{#nListName}.type"
			id="%{qualifyId(\"additionalComponentType\")}"
			list="additionalComponentTypes" listKey="description" listValue="description"
			value="%{type}" size="1" headerKey="" headerValue="--Select--" cssStyle="width:100px" /></td>
	<td><s:select name="%{#nListName}.subType"
			id="%{qualifyId(\"additionalComponentSubType\")}"
			list="additionalComponentSubTypes" listKey="description" listValue="description"
			value="%{subType}" headerKey="" headerValue="--Select--" cssStyle="width:80px" /></td>
	<td><s:textfield name="%{#nListName}.serialNumber"
			id="%{qualifyId(\"additionalComponent_serialNumber\")}" size="12"
			value="%{serialNumber}" /></td>

	<td>			
			<s:div>
				<s:textfield
 					name='%{#nListName}.partNumber' value='%{partNumber}'  
 					loadOnTextChange='true' showDownArrow='false' 
  					id="%{qualifyId(\"additionalComponentParts_itemNo\")}"/>  
					
<%-- 			 <sd:autocompleter id='%{qualifyId("additionalComponentParts_itemNo")}' href='list_causal_part_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{inventoryItem.brandType}' name='task.claim.serviceInformation.causalPart' value='%{task.claim.serviceInformation.causalPart.getBrandItemNumber(task.claim.brand)}' keyValue='%{task.claim.serviceInformation.causalPart.number}' loadOnTextChange='true' showDownArrow='false'/> --%>
<%-- 			 	        <sd:autocompleter delay='1000' href='list_causal_part_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{inventoryItem.brandType}' name='%{#nListName}.partNumber' value='%{partNumber}' loadOnTextChange='true' showDownArrow='false' id='%{qualifyId("additionalComponentParts_itemNo")}'/> --%>
			 
						<script type="text/javascript">
	           			dojo.addOnLoad(function(){	
	            		dojo.connect(dojo.byId('<s:property value="qualifyId(\"additionalComponentParts_itemNo\")" />'),"onchange",function(){	
	            		fillPartDetails('<s:property value="qualifyId(\"additionalComponentParts_itemNo\")" />','<s:property value="qualifyId(\"additionalComponentPart_Desc\")" />');
	            	});
		            });
	       			</script>
			</s:div>
		</td>

	<td><s:textarea name="%{#nListName}.partDescription"
			value="%{partDescription}" id="%{qualifyId(\"additionalComponentPart_Desc\")}"
			rows="1" cols="20"/></td>

	<td><s:textfield name="%{#nListName}.dateCode"
			id="%{qualifyId(\"additionalComponent_dateCode\")}" size="12"
			value="%{dateCode}" /></td>
			
	<td><s:textfield name="%{#nListName}.manufacturer"
			id="%{qualifyId(\"additionalComponent_manufacturer\")}" size="12"
			value="%{manufacturer}" /></td>

	<td><s:textfield name="%{#nListName}.model"
			id="%{qualifyId(\"additionalComponent_model\")}" size="12"
			value="%{model}" /></td>
	<td width="5%" align="center">
	
	<s:set name="inventoryItem" value="inventoryItem"/>
	<s:hidden name="%{#nListName}.item" value='%{inventoryItem}'/>
	<s:hidden name="%{#nListName}"
			value="%{id}" id="%{qualifyId(\"inventoryItem_additionalComp_id\")}" />
	<s:hidden id='brand' value='%{inventoryItem.brandType}'/>
			
			
		<div class="nList_delete"></div></td>
</tr>

<script type="text/javascript">	
function fillPartDetails(id,descId) {	
if(document.getElementById(id)){
	document.getElementById(descId).value='';
   var number = document.getElementById(id).value; 
   var brand = document.getElementById('brand').value
    twms.ajax.fireJavaScriptRequest("list_description_for_brand_part.action",{    		
            number: number,
            claimBrand:brand
        }, function(details) {
        if(details[0]!='-')      
            document.getElementById(descId).value=details[0];
        }
    );    
    }
    }	
</script>
