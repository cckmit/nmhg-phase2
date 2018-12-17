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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>

   <div class="mainTitle" style="margin-top:10px;margin-bottom:5px;">
    <s:text name="label.newClaim.oemPartReplaced"/></div>

<table id="oem_parts_replaced_table" class="grid borderForTable" width="97%">
    <thead>
     

        <tr class="row_head" >
         	<th><s:text name="label.common.serialNumber"/></th>	
            <th><s:text name="label.common.partNumber"/></th>
            <th><s:text name="label.common.quantity"/></th>
            <s:if test="loggedInUserAnInternalUser || (!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded'))">
	            <th><s:text name="label.newClaim.unitPrice"/></th>
            </s:if>
            <s:if test="loggedInUserAnInternalUser && !task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
	            <th><s:text name="label.newClaim.unitCostPrice"/></th>
            </s:if>            
            <s:if test="!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
	            <th><s:text name="label.uom.display"/></th>
	        </s:if>                
            <th><s:text name="label.common.description"/></th>
            <s:if test="processorReview">
                <th><s:text name="label.partReturn.markPartForReturn"/></th>
                <th><s:text name="columnTitle.dueParts.return_location"/></th>	
                <th><s:text name="columnTitle.partReturnConfiguration.paymentCondition"/></th>
                <th><s:text name="label.common.dueDays"/></th>
                <th width="9%"><s:text name="label.common.additionalAttributes"/></th>               		      		  
			</s:if>
        </tr>
    </thead>

    <tbody>
    	<s:set name="costType" value="getCostPriceType()"/>		    
        <s:iterator value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced">
        <s:if test="itemReference.serialized">
        		<tr>
	                <td>
	                    <s:property value="%{itemReference.referredInventoryItem.serialNumber}"/>
	                </td>
	                <td>
	                    <s:property value="%{itemReference.referredInventoryItem.ofType.number}"/>
	                </td>
	                <td>
	                    <span class="numeric">
	                        <s:label name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].numberOfUnits"/>
	                    </span>
	                </td>
	                <s:if test="loggedInUserAnInternalUser || (!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded'))">
		                <td class="numeric">
		                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].uomAdjustedPricePerUnit"/>
		                </td>
	                </s:if>
	                <s:if test="loggedInUserAnInternalUser && !task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
		                <td class="numeric">
		                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].getUomAdjustedCostPrice(#attr['costType'])"/>
		                </td>
	                </s:if>	    
	                 <s:if test="!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
			            <td> 
			            	 <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
			                   		<s:property  value="uomMapping.mappedUomDescription" />
			                   		<span id="UOM_<s:property value="#index"/>" tabindex="0" >
										<img src="image/comments.gif" width="16" height="15" />
									</span>
			
									<span dojoType="dijit.Tooltip" connectId="UOM_<s:property value="#index"/>" >
										<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
									</span>
		                   	  </s:if>	
		                   	  <s:else>
		                   	  		<s:property  value="itemReference.referredItem.uom.type" />
		                   	  </s:else>  
			            </td>
		            </s:if>            
	                <td>
	                    <s:property value="%{itemReference.referredInventoryItem.ofType.description}"/>
	                </td>
	                           
	             </tr>
        	</s:if> 
        	<s:else>        	
        	    <tr>
        	    	<td></td>
	                <td>
	                    <s:property value="%{itemReference.unserializedItem.alternateNumber}"/>
	                </td>
	                <td>
	                    <span class="numeric">
	                        <s:label name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].numberOfUnits"/>
	                    </span>
	                </td>
	                <s:if test="loggedInUserAnInternalUser || (!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded'))">
		                <td class="numeric">
		                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].uomAdjustedPricePerUnit"/>
		                </td>
	                </s:if>
	                <s:if test="loggedInUserAnInternalUser && !task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
		                <td class="numeric">
		                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].getUomAdjustedCostPrice(#attr['costType'])"/>
		                </td>
	                </s:if>	 
	                <s:if test="!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
			            <td> 
			            	 <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
			                   		<s:property  value="uomMapping.mappedUomDescription" />
			                   		<span id="UOM_<s:property value="#index"/>" tabindex="0" >
										<img src="image/comments.gif" width="16" height="15" />
									</span>
			
									<span dojoType="dijit.Tooltip" connectId="UOM_<s:property value="#index"/>" >
										<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
									</span>
		                   	  </s:if>	
		                   	  <s:else>
		                   	  		<s:property  value="itemReference.referredItem.uom.type" />
		                   	  </s:else>  
			            </td>
		            </s:if>    
	                               
	                <td>
	                    <s:property value="%{itemReference.unserializedItem.description}"/>
	                </td>
	                                
	            </tr>
	         </s:else>        
        </s:iterator>
    </tbody>
</table>



<script type="text/javascript">

function alterValue(){
    var check=dojo.byId("oemPart_toBeReturned_installed");
    dojo.byId("oemPart_toBeReturned_installed").value=check.checked;
    var location=dijit.byId("oemPart_location_installed");
    var paymentCondition=dijit.byId("oemPart_paymentCondition_installed")
    var dueDays=dojo.byId("oemPart_dueDays_installed");
    if(location && paymentCondition){
    if(!check.checked) {
        location.setDisabled(true);
        paymentCondition.setDisabled(true);
        if(dueDays != null) {
            dueDays.disabled = true;
        }
    } else {
        location.setDisabled(false);
        paymentCondition.setDisabled(false);
        if(dueDays != null) {
            dueDays.removeAttribute("disabled");
        }
    }
    }
}

function getAdditionalAttributes(index,number){
	dijit.byId('partAtrribute_0').formNode=document.getElementById("claim_form");
	dojo.connect(dojo.byId("partDetailsAttribute_"+index),"onclick",function(){
		dojo.publish("/part_"+index+"/attribute/show");
	});
	
		var attributePresent=!( '<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[' + index + '].claimAttributes.empty"/>' )&& '<s:property value="task.claim.state.state"/>' !='draft' ;
		if(attributePresent){
			dojo.byId("partDetailsAttribute_"+index).innerHTML='<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>';
		}
		var isAttributeDialogDisplayed = false;
		dojo.subscribe("/part_"+index+"/attribute/show", null, function() {
		var dlg = dijit.byId("partAtrribute_0");
		if(! attributePresent && ! isAttributeDialogDisplayed){
			var claim='<s:property value="task.claim.id"/>';
			var params = {
                claimDetails: claim,
                indexId: index,
                partNumber: number
            };
			twms.ajax.fireHtmlRequest("getAttributesForPart.action", params,
                function(data) {
                    var parentContentPane = dijit.byId("partAttributeContentPane_0");
                    parentContentPane.setContent(data);
                    isAttributeDialogDisplayed = true;
                }
            );
		}
		dlg.show();	
  	});
  			    	
}


</script>  		
