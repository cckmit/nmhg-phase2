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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>

   <div class="mainTitle" style="margin-top:10px;margin-bottom:5px;">
    <s:text name="label.newClaim.oemPartReplaced"/></div>

<table id="oem_parts_replaced_table" class="grid borderForTable" width="97%">
    <thead>
     

        <tr class="row_head" >
            <th>
            	<s:if test="itemReference.serialized">
            		<s:text name="label.common.serialNumber"/>
            	</s:if>
            	<s:else>
            		<s:text name="label.common.partNumber"/>
            	</s:else>	
            </th>
            <%--Change for SLMS-776 adding date code --%>
            <th><s:text name="label.common.dateCode"/></th>
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
                <th><s:text name="label.common.dueDaysOrDate"/></th>
                               		      		  
			</s:if>
			<th width="9%"><s:text name="label.common.additionalAttributes"/></th>
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
	                <%--Change for SLMS-776 adding date code --%>
	                <td>
	                        <s:textfield name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].dateCode"/>
	                    </span>
	                </td>
	                <td>
	                    <span class="numeric">
	                        <s:textfield name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].numberOfUnits"/>
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
	                 <s:if test="processorReview">	                
			           <td width="5%" align="center">
				           <s:if test= "(task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
				               <s:checkbox disabled="true" id="oemPart_toBeReturned_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}" onclick="alterValue();"/>
				               	<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	 value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}"/>
				           </s:if>
				           <s:else>
				               <s:checkbox  id="oemPart_toBeReturned_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}" onclick="alterValue();"/>
				           </s:else>
				           <script type="text/javascript">
				                dojo.addOnLoad(function() {
				                    alterValue();
				                });
					       </script> 
			             </td>
			             <td width="15%">
			               	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
					              <s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.code}"/>
	                             <s:hidden id="oemPart_location_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation"
	               				     value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.id}">
	               				 </s:hidden>				               	  
			               	</s:if>
			               	<s:else>
			               	   <sd:autocompleter id='oemPart_location_installed' size='3' href='list_part_return_locations_for_part.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation' keyName='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.code}' />
						  	 	
							</s:else>							
		                </td>  
			           <td width="10%">
			             	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
				               	<s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced0].partReturn.paymentCondition.description}"/>
				               	<s:hidden id="oemPart_paymentCondition_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition"
				               				value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition.code}">
				               	</s:hidden>
			               	</s:if>
			               	<s:else>
				                <s:select list="paymentConditions" id="oemPart_paymentCondition_installed"
									name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition"
									listKey="code" listValue="description" emptyOption="true" 
									value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition.code}" >
								</s:select>
							</s:else>
                       </td>
                        <td>
				            <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].isDueDaysReadOnly()">
								<s:property value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.dueDate}" />
							</s:if> 
							<s:else >
								<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.dueDaysReadOnly" value="true"/>
								<s:textfield size="3" id="oemPart_dueDays_installed"
									name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.dueDays"></s:textfield>
							</s:else>
			           </td>
						</s:if>
			            <td width="10%">
				           <s:a cssStyle="cursor:pointer" >
					           <span id="partDetailsAttribute_0">
					           </span>
				           </s:a>
					      	<s:if test="! task.claim.state.state.equalsIgnoreCase('Draft')">
					           <s:set name="claimAttributesList" scope="request" value= "%{task.claim.serviceInformation.serviceDetail.oemPartsReplaced[0].claimAttributes}" />
				           </s:if>
				           <script type="text/javascript">
					           	dojo.addOnLoad(function() {
					                //var isNotDraft='<s:property value="task.claim.state.state"/>' !='draft' ;
					                //if(isNotDraft){
					                    getAdditionalAttributes(0,null);
					                //}
					           	});
				           </script>
                       </td>          		              		               
	             </tr>
        	</s:if> 
        	<s:else>        	
        	    <tr>
	                <td>
	                    <s:property value="%{itemReference.unserializedItem.alternateNumber}"/>
	                </td>
	                <td>
	                    <span class="numeric">
	                        <s:textfield name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].datecode"/>
	                    </span>
	                </td>
	                <td>
	                    <span class="numeric">
	                        <s:textfield name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].numberOfUnits"/>
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
	                 <s:if test="processorReview">	                
			           <td width="5%" align="center">
				           <s:if test= "(task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
				               <s:checkbox disabled="true" id="oemPart_toBeReturned_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}" onclick="alterValue();"/>
				               	<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	 value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}"/>
				           </s:if>
				           <s:else>
				               <s:checkbox  id="oemPart_toBeReturned_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned"
				               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partToBeReturned}" onclick="alterValue();"/>
				           </s:else>
				           <script type="text/javascript">
				                dojo.addOnLoad(function() {
				                    alterValue();
				                });
					       </script>
 
			           </td>
			             <td width="15%">
			               	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
					              <s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.code}"/>
	                             <s:hidden id="oemPart_location_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation"
	               				     value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.id}">
	               				 </s:hidden>				               	  
			               	</s:if>
			               	<s:else>
			               	   <sd:autocompleter id='oemPart_location_installed' size='3' href='list_part_return_locations_for_part.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation' keyName='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.returnLocation.code}' />
						  	 	
							</s:else>							
		                </td>  
			           <td width="10%">
			             	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
				               	<s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced0].partReturn.paymentCondition.description}"/>
				               	<s:hidden id="oemPart_paymentCondition_installed" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition"
				               				value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition.code}">
				               	</s:hidden>
			               	</s:if>
			               	<s:else>
				                <s:select list="paymentConditions" id="oemPart_paymentCondition_installed"
									name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition"
									listKey="code" listValue="description" emptyOption="true" 
									value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.paymentCondition.code}" >
								</s:select>
							</s:else>
                       </td>
                        <td>
				            <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.status.isShipmentGenerated()">
								<s:property value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.dueDays}" />
							</s:if> 
							<s:else >
								<s:textfield size="3" id="oemPart_dueDays_installed"
									name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[0].partReturn.dueDays"></s:textfield>
							</s:else>
			           </td>
						</s:if>
			            <td width="10%">
				           <s:a cssStyle="cursor:pointer" >
					           <span id="partDetailsAttribute_0">
					           </span>
				           </s:a>
					      	<s:if test="! task.claim.state.state.equalsIgnoreCase('Draft')">
					           <s:set name="claimAttributesList" scope="request" value= "%{task.claim.serviceInformation.serviceDetail.oemPartsReplaced[#index].claimAttributes}" />
				           </s:if>
				           <script type="text/javascript">
					           	dojo.addOnLoad(function() {
					                //var isNotDraft='<s:property value="task.claim.state.state"/>' !='draft' ;
					                //if(isNotDraft){
					                    getAdditionalAttributes(0,'<s:property value="%{itemReference.unserializedItem.alternateNumber}"/>');
					                //}
					           	});
				           </script>
                       </td>          		              		               
	            </tr>
	         </s:else>        
        </s:iterator>
    </tbody>
</table>

<div style="display: none"> 
	<div dojoType="twms.widget.Dialog" id="partAtrribute_0" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250" >
		<div dojoType="dijit.layout.LayoutContainer" style="height:225px;background: #F3FBFE; border: 1px solid #EFEBF7">
		 	<div dojoType="dojox.layout.ContentPane" id="partAttributeContentPane_0" layoutAlign="top" executeScripts="true">
				<jsp:include flush="true" page="part-claim-attributes.jsp" />
			</div>
		</div>
	</div>
</div>


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
	
		var attributePresent=!( '<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[' + index + '].claimAttributes.empty"/>' );
		if(attributePresent){
			dojo.byId("partDetailsAttribute_"+index).innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>';
		}
		var isAttributeDialogDisplayed = false;
		dojo.subscribe("/part_"+index+"/attribute/show", null, function() {
		var dlg = dijit.byId("partAtrribute_0");
		if(!isAttributeDialogDisplayed){
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
                	dojo.connect(dojo.byId("closeReplacedPartAttrPopup_"+index),"onclick",function() {
	            		dojo.publish("/part/attribute/hide");
	            	});
                }
            );
		}
		dlg.show();	
  	});

		dojo.subscribe("/part/attribute/hide", null, function() {
			dijit.byId("partAtrribute_0").hide();
		});
  			    	
}


</script>  		
