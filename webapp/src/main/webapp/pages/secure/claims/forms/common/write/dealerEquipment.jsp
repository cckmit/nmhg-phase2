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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<style type="text/css">
    .separator {
        margin-left: 17px;
    }
</style>
<script type="text/javascript">
dojo.require("dojo.parser");
</script>
<table  cellspacing="0" cellpadding="0" id="equipment_details_table"  width="95%" class="repeat borderForTable">
    <thead>        
        <tr class="row_head">
            <th width="5%">
                <s:text name="label.common.select"/>                       
            </th>                
            <th width="10%"><s:text name="label.common.serialNumber"/></th>
            <th width="10%"><s:text name="label.common.itemNumber"/></th>
            <th width="20%"><s:text name="label.common.product"/></th>           
            <th width="10%"><s:text name="label.common.model"/></th>
            <th width="20%"><s:text name="label.common.hoursOnTruck"/></th>            
        </tr>
    </thead>
    <tbody>
    <s:if test="claim.claimedItems.size>0">
    	<input type="hidden" name="claim.forMultipleItems" value="true"/>
    </s:if>
    
    <s:iterator value="claim.claimedItems" status="claimedItems">    
    <tr> 
       <td align="center" valign="middle">
           <s:checkbox id=" "
                       name="claim.claimedItems[%{#claimedItems.index}].itemReference.referredInventoryItem" 
                       fieldValue="%{itemReference.referredInventoryItem.id}" value="true" /> 
           <SCRIPT type="text/javascript">           
		       	dojo.addOnLoad(function(){	
		       	var index = '<s:property value="%{#claimedItems.index}"/>';		       	
		       	dojo.connect(dojo.byId(index),"onclick",function(){
		       		if(dojo.byId(index).checked){
		       			dojo.byId("hrsInService_"+index).disabled=false;
		       		}else{
		       			dojo.byId("hrsInService_"+index).value="";
		       			dojo.byId("hrsInService_"+index).disabled=true;
		       		}
		       	});
		       });        
           </SCRIPT>                   
       </td>      
     	<input type="hidden" name="tempList[<s:property value="%{#claimedItems.index}"/>].itemReference.referredInventoryItem" 
     	value="<s:property value="itemReference.referredInventoryItem.id"/>"/>
        <td>             	      	
			<u:openTab cssClass="link" url="inventoryDetail.action?id=%{itemReference.referredInventoryItem.id}"
	        	id="serialLink_%{#claimedItems.index}" tabLabel="Serial Number %{itemReference.referredInventoryItem.serialNumber}"
                autoPickDecendentOf="true">
	           <s:property
	               value="itemReference.referredInventoryItem.serialNumber" />
	         </u:openTab>	        
        </td>
        <td>			
            <s:property value="itemReference.unserializedItem.alternateNumber"/>          		
        </td>               
        <td>
			<s:property value="itemReference.unserializedItem.product.name" />
        </td>
        <td>        	
        	<s:property value="itemReference.unserializedItem.model.name" />
        </td>   
        <td>
        	<s:textfield id="hrsInService_%{#claimedItems.index}"
        	name="claim.claimedItems[%{#claimedItems.index}].hoursInService" 
        	value='%{hoursInService}' theme="simple"/>         	
        </td> 
    <s:if test="partsClaim">
	    <tr>
	        <td>
	            <s:text name="label.newClaim.isPartInstalled" />?
	        </td>
	        <td>
	        	<s:if test="claim.partInstalled">
	        		<s:text name="label.common.yes"/>
	        	</s:if>
	          <s:else>
	          	<s:text name="label.common.no"/>	
	          </s:else>
	        </td>	        
	    </tr>
    </s:if>
    </tr>    
   </s:iterator>
   </tbody>
</table>
