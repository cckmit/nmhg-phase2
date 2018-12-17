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



<div dojoType="dojox.layout.ContentPane" executeScripts="true" layoutAlign="client">
    <table class="grid" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin:5px;">
        <thead>
            <tr class="row_head">
                <th colspan="8"><s:text name="label.bussinessUnit.clubCarPartsReplaced1"/></th>
            </tr>
            <tr class="row_head">
                <th><s:text name="label.newClaim.sNo"/></th>
                <th><s:text name="label.newClaim.partOrSerialNumber"/></th>
                <th><s:text name="label.common.quantity"/></th>
                
                <th><s:text name="label.common.description"/></th>
                <s:if test="claim.forMultipleItems">
                <th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
                </s:if>
                <th><s:text name="label.common.returnlocation"/></th>
                <th><s:text name="label.common.dueDate"/></th>
            </tr>
        </thead>
        <tbody>
        	
            <s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="status">
            	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].replacedParts" >           
                <tr>
                    <s:if test="itemReference.serialized == true">                    	
                        <td align="center">
                            <input type="checkbox" checked="true" disabled="true"/>
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
                            <s:property value="%{getOEMPartCrossRefForDisplay(itemReference.referredItem, oemDealerPartReplaced, true, claim.forDealer)}"/>                                                                                
                            
                        </td>
                    </s:else>           
                    
                   	<td class="numeric">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                   	
                   
                                       
                    <s:if test="itemReference.serialized == true">
                        <td>
                            <s:property value="itemReference.referredInventoryItem.ofType.description"/>
                        </td>
                    </s:if>
                    <s:else>
                        <td>
                            <s:property value="%{getOEMPartCrossRefForDisplay(itemReference.referredItem, oemDealerPartReplaced,false,claim.forDealer)}"/>        
                        </td>
                    </s:else>
                    <s:if test="claim.forMultipleItems">
                    <td>
			            <s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].inventoryLevel">
			            <s:text name="accordion_jsp.accordionPane.inventory"/>
			            </s:if>
			            <s:else>
			            <s:text name="claim.prieview.ContentPane.claim"/>
			            </s:else>
            		</td>
            		</s:if>
                    
                    
                    <td>                    	
                        <s:property value="activePartReturn.returnLocation.code"/>                        
                    </td>
                    <td>                    	
                        <s:property value="activePartReturn.dueDate"/>
                    </td>
                </tr>
                </s:iterator>
            </s:iterator>            
            
        </tbody>
	</table>
	
	<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin: 5px;">
        <thead>
            <tr class="row_head">
                <th colspan="8"><s:text name="label.bussinessUnit.clubCarPartsReplaced2"/></th>
            </tr>
            <tr class="row_head">
                <th><s:text name="label.newClaim.sNo"/></th>
                <th><s:text name="label.newClaim.partOrSerialNumber"/></th>
                <th><s:text name="label.common.quantity"/></th>
                
                <th><s:text name="label.common.description"/></th>
                <th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
                <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                    <th width="12%"><s:text name="label.newClaim.unitPrice"/></th>
                </authz:ifCondition>
                <th><s:text name="label.common.additionalAttributes"/></th>
            </tr>
        </thead>
        <tbody>
        	
            <s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="status">
            	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].hussmanInstalledParts"  status="subStatus">           
                <tr>
                    <s:if test="item.serialized == true">                    	
                        <td align="center">
                            <input type="checkbox" checked="true" disabled="true"/>
                        </td>
                        <td>
                            <s:property value="item.serialNumber" />
                        </td>
                    </s:if>

                    <s:else>                   
                        <td align="center">
                            <input type="checkbox" disabled="true"/>
                        </td>
                        <td>
                            <s:property value="%{getOEMPartCrossRefForDisplay(item, oemDealerPartReplaced, true, claim.forDealer)}"/>                                                                                
                            
                        </td>
                    </s:else>           
                    
                   	<td class="numeric">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                   	
                   
                                       
                    <s:if test="item.serialized == true">
                        <td>
                            <s:property value="item.ofType.description"/>
                        </td>
                    </s:if>
                    <s:else>
                        <td>
                            <s:property value="%{getOEMPartCrossRefForDisplay(item, oemDealerPartReplaced,false,claim.forDealer)}"/>        
                        </td>
                    </s:else>
                    <s:if test="claim.forMultipleItems">
                    <td>
			            <s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].inventoryLevel">
			            <s:text name="accordion_jsp.accordionPane.inventory"/>
			            </s:if>
			            <s:else>
			            <s:text name="claim.prieview.ContentPane.claim"/>
			            </s:else>
            		</td>
            		</s:if>
                    
                    <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                        <td class="numeric">
                            <s:property value="pricePerUnit"/>
                        </td>
                    </authz:ifCondition>
                    <td>
                    	<s:a cssStyle="cursor-pointer">
                        <span id="partAttributesForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>" >
                        </span>
                        </s:a>
                     <script type="text/javascript">
                        dojo.addOnLoad(function(){
                        var attributePresent=!( <s:property value='claimAttributes.empty'/>);
                        if(attributePresent){
                        	dojo.byId("partAttributesForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>").innerHTML='<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>';
                        	dojo.connect(dojo.byId("partAttributesForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>"),"onclick",function(){
								dojo.publish("/partForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>}/attribute/show");
							});
							dojo.subscribe("/partForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>}/attribute/show", null, function() {
							dijit.byId("partAtrributeReadForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>").show();
							});
		    				dojo.subscribe("/partForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>/attribute/hide", null, function() {
						 	dijit.byId("partAtrributeReadForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>").hide();
						    }); 
							dojo.connect(dojo.byId("closePartAttrPopup_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>"),"onclick",function() {
						    dojo.publish("/partForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>/attribute/hide");
						});  
					}
					});
                        
                 </script>
                         <div style="display: none">           
							<div dojoType="twms.widget.Dialog" id="partAtrributeReadForHussmann_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
								toggleDuration="250" style="width:500px; height:300px;">
								
								 	<div dojoType="dojox.layout.ContentPane" id="partAttributeContentPaneRepInstall_<s:property value="%{#status.index}"/>_<s:property value="%{#subStatus.index}"/>" layoutAlign="top" executeScripts="true">
									  <table width="100%" style="border-bottom:1px solid #EFEBF7;">
									        	<tbody>
									          		<tr>
									         			<td colspan="2" nowrap="nowrap" class="sectionTitle"><s:text name="label.common.additionalAttributes"/></td>
									       			</tr>
									        	</tbody>
			       			           </table>
			       			           
				        <div id="separatorRepInstall"/>
					        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
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
				       				<td id="submitSection_Rep_Install"  align="left" class="buttons" style="padding-top: 20px;">
				       				<input type="button" id="closePartAttrPopupRepInst_<s:property value="%{#subStatus.index}"/>"  value='<s:text name="button.common.close"/>'/>
				            		</td>
				        		</tr>
							</table>							
				         </div>
				</div>
				</div>
                </td>
                </tr>
                </s:iterator>
            </s:iterator>            
            
        </tbody>
	</table>
	<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin: 5px;">
        <thead>
            <tr class="row_head">
                <th colspan="8"><s:text name="label.recoveryClaim.outsidePartsInstalled"/></th>
            </tr>
            <tr class="row_head">
                <th><s:text name="label.newClaim.partOrSerialNumber"/></th>
                <th><s:text name="label.common.quantity"/></th>
                <th><s:text name="label.common.description"/></th>
                <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                    <th width="12%"><s:text name="label.newClaim.unitPrice"/></th>
                </authz:ifCondition>
            </tr>
        </thead>
        <tbody>
           <s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="status">
           	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].nonHussmanInstalledParts" >           
                <tr>
					<td><s:property value="partNumber" /> </td>
                   	<td class="numeric">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                   	<td><s:property value="description" /></td>
                    <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                        <td class="numeric">
                            <s:property value="pricePerUnit"/>
                        </td>
                    </authz:ifCondition>
                </tr>
             </s:iterator>
           </s:iterator>            
        </tbody>
	</table>
</div>