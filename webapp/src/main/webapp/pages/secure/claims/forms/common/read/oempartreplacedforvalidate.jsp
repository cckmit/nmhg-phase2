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
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="batterytestsheet.css"/>
<script type="text/javascript">
	dojo.require("dojox.layout.ContentPane");
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dijit.layout.LayoutContainer");	
	dojo.require("twms.widget.Dialog");
	
</script>

<script type="text/javascript">
    dojo.require("dijit.Tooltip");
</script>



<div dojoType="dojox.layout.ContentPane" executeScripts="true" layoutAlign="client" style="margin: 2px; padding: 3px;">
        <div class="mainTitle">
                <s:text name="label.newClaim.oemPartReplaced"/>
            </div>
    <table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%"
    style="margin-top:10px;">
        <thead>
        
         
            <tr class="row_head">
                <th><s:text name="label.newClaim.sNo"/></th>
                <th><s:text name="label.newClaim.partOrSerialNumber"/></th>
                <%--Change for SLMS-776 adding date code --%>
                <th><s:text name="label.common.dateCode"/></th>
                <th><s:text name="label.common.quantity"/></th>
                <s:if test="loggedInUserAnInternalUser || claim.forDealer.id == loggedInUsersDealership.id">
                    <th width="12%"><s:text name="label.newClaim.unitPrice"/></th>
                </s:if>
                <s:if test="loggedInUserAnInternalUser">
                    <th width="12%"><s:text name="label.newClaim.unitCostPrice"/></th>
                </s:if>
                <th><s:text name="label.uom.display"/></th>
                <th><s:text name="label.common.description"/></th>
                <s:if test="claim.forMultipleItems">
                <th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
                </s:if>
                <th><s:text name="label.common.returnlocation"/></th>
                <th><s:text name="label.common.dueDate"/></th>
                <th><s:text name="label.common.additionalAttributes"/></th>
            </tr>
        </thead>
        <tbody>	
        	<s:set name="costType" value="getCostPriceType()"/>		
            <s:iterator value="claim.serviceInformation.serviceDetail.OEMPartsReplaced" status="status">           
                <tr>
                    <s:if test="itemReference.serialized == true">                    	
                        <td>
                            <s:property value="%{#status.index}"/>
                        </td>
                        <td>
                            <s:property value="itemReference.referredInventoryItem.serialNumber" />
                        </td>
                    </s:if>

                    <s:else>                   
                        <td align="center">
                            <input type="checkbox" disabled="true"/>
                        </td>
                        <td>
                            <s:property value="brandItem.itemNumber"/>
                            
                        </td>
                    </s:else>
                    <%--Change for SLMS-776 adding date code --%>
                    <td class="numeric">
                   		<s:property  value="dateCode" />
                   	</td>           
                    
                   	<td class="numeric">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                    <s:if test="loggedInUserAnInternalUser || claim.forDealer.id == loggedInUsersDealership.id">
                        <td class="numeric">
                            <s:property value="uomAdjustedPricePerUnit"/>
                        </td>
                    </s:if>
                    <s:if test="loggedInUserAnInternalUser">
                        <td class="numeric">
                            <s:property value="getUomAdjustedCostPrice(#attr['costType'])"/>
                        </td>
                    </s:if>                     	
                    <td class="text">
                      <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
	                   		<s:property  value="uomMapping.mappedUomDescription" />
	                   		<span id="UOM_validate_<s:property value="%{#status.index}"/>"   tabindex="0">
								<img src="image/comments.gif" width="16" height="15" />
							</span>
	
							<span  dojoType="dijit.Tooltip" connectId="UOM_validate_<s:property value="%{#status.index}"/>" >
								<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
							</span>
                   	  </s:if>	
                   	  <s:else>
                   	  		<s:property  value="itemReference.referredItem.uom.type" />
                   	  </s:else>
                   	</td>
                                       
                    <s:if test="itemReference.serialized == true">
                        <td class="text">
                            <s:property value="itemReference.referredInventoryItem.ofType.description"/>
                        </td>
                    </s:if>
                    <s:else>
                        <td class="text">
                            <s:property value="%{getOEMPartCrossRefForDisplay(itemReference.referredItem, oemDealerPartReplaced,false,claim.forDealer)}"/>        
                        </td>
                    </s:else>
                    <s:if test="claim.forMultipleItems">
                    <td class="text">
			            <s:if test="inventoryLevel">
			            <s:text name="accordion_jsp.accordionPane.inventory"/>
			            </s:if>
			            <s:else>
			            <s:text name="claim.prieview.ContentPane.claim"/>
			            </s:else>
            		</td>
            		</s:if>

                  
                    <td class="text">                    	
                        <s:property value="activePartReturn.returnLocation.code"/>                        
                    </td>
                    <td class="date">                    	
                        <s:property value="activePartReturn.dueDate"/>
                    </td>
                    <td>
                    	<s:a cssStyle="cursor-pointer">
                        <span id="partAttributes_<s:property value="%{#status.index}"/>" >
                        </span>
                        </s:a>
                     <script type="text/javascript">
                        dojo.addOnLoad(function(){
                        var attributePresent=!( <s:property value='claimAttributes.empty'/>);
                        if(attributePresent){
                        	dojo.byId("partAttributes_<s:property value="%{#status.index}"/>").innerHTML='<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>';
                        	dojo.connect(dojo.byId("partAttributes_<s:property value="%{#status.index}"/>"),"onclick",function(){
								dojo.publish("/part_<s:property value="%{#status.index}"/>}/attribute/show");
							});
							dojo.subscribe("/part_<s:property value="%{#status.index}"/>}/attribute/show", null, function() {
							dijit.byId("partAtrributeRead_<s:property value="%{#status.index}"/>").show();
							});
		    				dojo.subscribe("/part_<s:property value="%{#status.index}"/>/attribute/hide", null, function() {
						 	dijit.byId("partAtrributeRead_<s:property value="%{#status.index}"/>").hide();
						    }); 
							dojo.connect(dojo.byId("closePartAttrPopup_<s:property value="%{#status.index}"/>"),"onclick",function() {
						    dojo.publish("/part_<s:property value="%{#status.index}"/>/attribute/hide");
						});  
					}
					});
                        
                 </script>
                         <div style="display: none"> 
							<div dojoType="twms.widget.Dialog" id="partAtrributeRead_<s:property value="%{#status.index}"/>" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
								toggleDuration="250" style="width:500px; height:300px;">
								
								 	<div dojoType="dojox.layout.ContentPane" id="partAttributeContentPaneNew_<s:property value="%{#status.index}"/>" layoutAlign="top" executeScripts="true">
										<table width="100%" style="border-bottom:1px solid #EFEBF7;">
							        	<tbody>
							          		<tr>
							         			<td colspan="2" nowrap="nowrap" class="sectionTitle"><s:text name="label.common.additionalAttributes"/></td>
							       			</tr>
							        	</tbody>
					       			 </table>
							        <div id="separator"/>
							        <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
							      		<tbody>
								      		<s:iterator value="claimAttributes" >
								      	 		<tr>
								      	 			<td width ="10%" class="label">
								        				 <s:property value="attributes.name" />
								        			</td>
								         			<td width ="10%" class="labelNormal">
								         						<s:property value="%{attrValue}"/>
								        	 		</td>
								       			</tr>
								       	 	</s:iterator>
							       	 	</tbody>
							      	</table>
							      	<table width="100%" cellpadding="0" cellspacing="0">
					 				<tr>
					 				<td width="30%">&nbsp;</td>
					       				<td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
					       				<input type="button" id="closePartAttrPopup_<s:property value="%{#status.index}"/>"  value='<s:text name="button.common.close"/>'/>
					            		</td>
					        		</tr>
									</table>
									
							</div>
				</div>
				</div>
                </td>
                </tr>
            </s:iterator>            
            
        </tbody>
    </table>
</div>

