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
<s:set name="replacedPartsExists" value="false"/>
<s:iterator
	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
	status="status">
	<s:iterator
		value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].replacedParts">
		<s:set name="replacedPartsExists" value="true"/>
	</s:iterator>
</s:iterator>
	<s:if test="#replacedPartsExists">
    <table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin:5px;">
        <thead>
            <tr class="title">
                <td colspan="9"><s:text name="label.inventory.oemReplacedParts"/></td>
            </tr>
            <tr class="row_head">
            	<s:if test="enableComponentDateCode()">
                <th width="9%"><s:text name="label.newClaim.sNo"/></th> 
                </s:if>
                <s:if test="%{isShowPartSerialNumber()}">               
                <th width="17%"><s:text name="columnTitle.partReturnConfiguration.partSerialNumber"/></th>
               </s:if>
                <th width="17%"><s:text name="label.partReturnConfiguration.partNumberWithCol"/></th>
                <%--Change for SLMS-776 adding date code --%>
                <s:if test="enableComponentDateCode()">
                <th  width="12%"><s:text name="label.common.dateCode"/></th>
                </s:if>
                <th  width="11%"><s:text name="label.common.quantity"/></th>
                <th width="25%"><s:text name="label.common.description"/></th>
                <authz:ifProcessor>
               <th><s:text name="label.newClaim.unitCostPrice" /></th>
                <th><s:text name="label.uom.display"/></th>
                </authz:ifProcessor>
                <s:if test="claim.forMultipleItems">
                <th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
                </s:if>
                <th width="15%"><s:text name="label.common.returnlocation"/></th>
                <th width="15%"><s:text name="label.common.dueDate"/></th>
            </tr>
        </thead>
        <tbody>
        	
            <s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="status">
            	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].replacedParts" status="dojoindex1" >           
                <tr>
                    <s:if test="serialNumber!=null && serialNumber!=''"> 
                    <s:if test="enableComponentDateCode()">                   	
                        <td align="center">
                            <input type="checkbox" checked="true" disabled="true"/>
                        </td>   
                         </s:if>    
                          <s:if test="%{isShowPartSerialNumber()}">                       
                        <td class="text">
                            <s:property value="serialNumber" /> 
                        </td>
                    	</s:if>
                        <td class="text">
                            <s:property value="%{brandItem.itemNumber}"/>
                            
                        </td>
                    </s:if>

                    <s:else>   
                     <s:if test="enableComponentDateCode()">         
                        <td align="center">
                            <input type="checkbox" disabled="true"/>
                        </td>  
                        </s:if>
                        <s:if test="%{isShowPartSerialNumber()}">                      
                          <td class="text">
                            <s:property value="serialNumber" />
                        </td>
                       </s:if>
                        <td class="text">
                            <s:property value="%{brandItem.itemNumber}"/>
                            
                        </td>
                    </s:else>           
                    <%--Change for SLMS-776 adding date code --%>
                    <s:if test="enableComponentDateCode()">
                    <td class="numeric">
                   		<s:property  value="dateCode" />
                   	</td>
                   	</s:if>
                   	
                   	<td class="numeric">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                   	  <s:if test="itemReference.serialized == true && itemReference.referredItem==null">
                        <td class="text">
                            <s:property value="itemReference.referredInventoryItem.ofType.description"/>
                        </td>
                    </s:if>
                    <s:else>
                        <td class="text">
                            <s:property value="%{getOEMPartCrossRefForDisplay(itemReference.referredItem, oemDealerPartReplaced,false,claim.forDealer)}"/>        
                        </td>
                    </s:else>
                   	<authz:ifProcessor>
                   	<td class="text"> <s:property value="costPricePerUnit"/></td>
                   	
                   	<td class="text">
                   	<s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
    	               	<s:property value="uomMapping.mappedUomDescription"/>     	
              			<span id="UOM_1<s:property value="%{#status.index}"/>_<s:property value="%{#dojoindex1.index}"/>" tabindex="0" >
							<img src="image/comments.gif" width="16" height="15" />
						</span>

						<span dojoType="dijit.Tooltip" connectId="UOM_1<s:property value="%{#status.index}"/>_<s:property value="%{#dojoindex1.index}"/>" >
							<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
						</span>
                   	</s:if>
                   	<s:else>
                   		<s:if test="itemReference.serialized == true">
                   			<s:property value="itemReference.referredInventoryItem.ofType.Uom"/>
                   		</s:if>
                   		<s:else>
                   			<s:property  value="itemReference.referredItem.uom.type" />
                   		</s:else>
                   	</s:else>
                   	</td>
                   	</authz:ifProcessor>
                                       
                    <s:if test="claim.forMultipleItems">
                    <td class="text">
			            <s:if test="claim.serviceInformation.serviceDetail.
			            		hussmanPartsReplacedInstalled[#status.index].inventoryLevel">
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
                </tr>
                </s:iterator>
            </s:iterator>            
            
        </tbody>
	</table>
	</s:if>
<s:set name="installedPartsExists" value="false"/>
<s:iterator
	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
	status="status">
	<s:iterator
		value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].hussmanInstalledParts">
		<s:set name="installedPartsExists" value="true"/>
	</s:iterator>
</s:iterator>
	<s:if test="#installedPartsExists">
	<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%" style="margin:5px;">
        <thead>
            <tr class="title">
                <td colspan="9"><s:text name="label.inventory.oemInstalledParts"/></td>
            </tr>
            <tr class="row_head">
             	<s:if test="enableComponentDateCode()">
                <th width="3%"><s:text name="label.newClaim.sNo"/></th>  
                </s:if>
                <s:if test="%{isShowPartSerialNumber()}">              
                <th width="6%"><s:text name="columnTitle.partReturnConfiguration.partSerialNumber"/></th>
               </s:if>
                <th width="6%"><s:text name="label.partReturnConfiguration.partNumberWithCol"/></th>
                <%--Change for SLMS-776 adding date code --%>
                <s:if test="enableComponentDateCode()">
                <th width="6%"><s:text name="label.common.dateCode"/></th>
                </s:if>
                <th width="3%"><s:text name="label.common.quantity"/></th>
                <th width="10%"><s:text name="label.common.description"/></th>
                <s:if test="claim.forMultipleItems">
                	<th width="10%"><s:text name="label.common.claim/inventoryLevel"/></th>
                </s:if>
                 <authz:ifProcessor>
                  <th width="6%">
                 <s:text name="label.newClaim.unitCostPrice" /></th>
                 </authz:ifProcessor>
                <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                    <th width="6%">	<s:text name="label.uom.display" /></th>
                    <th width="6%"><s:text name="label.newClaim.unitPrice"/></th>
                </authz:ifCondition>
            </tr>
        </thead>
        <tbody>
        	
            <s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="status">
            	<s:iterator value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#status.index].hussmanInstalledParts"  status="dojoindex">           
                <tr> 
 					<s:if test="serialNumber!=null && serialNumber!=''">    
 					  <s:if test="enableComponentDateCode()">                	
                        <td align="center">
                            <input type="checkbox" checked="true" disabled="true"/>
                        </td>   
                        </s:if>     
                        <s:if test="%{isShowPartSerialNumber()}">                    
                        <td class="text">
                            <s:property value="serialNumber" />
                        </td>
                       </s:if>
                        <td class="text">
                            <s:property value="brandItem.itemNumber"/> 
                            
                        </td>
                    </s:if>
                    <s:else>  
                     <s:if test="enableComponentDateCode()">     
                        <td align="center">
                            <input type="checkbox" disabled="true"/>
                        </td>   
                        </s:if>      
                        <s:if test="%{isShowPartSerialNumber()}">                 
                          <td class="text">
                            <s:property value="serialNumber" />
                        </td>
                      </s:if>
                        <td class="text">
                            <s:property value="brandItem.itemNumber"/>
                            
                        </td>
                    </s:else>
                    
                    <%--Change for SLMS-776 adding date code --%>   
                    <s:if test="enableComponentDateCode()">        
                    <td class="numeric" align="center">
                   		<s:property  value="dateCode" />
                   	</td>
                   	</s:if>
                   	
                   	<td class="numeric" align="center">
                   		<s:property  value="numberOfUnits" />
                   	</td>
                                       
                    <s:if test="item.serialized == true">
                        <td align="center">
                            <s:property value="item.description"/>
                        </td>
                    </s:if>
                    <s:else>
                        <td align="center">
                            <s:property value="%{getOEMPartCrossRefForDisplay(item, oemDealerPartReplaced,false,claim.forDealer)}"/>        
                        </td>
                    </s:else>
                    <s:if test="claim.forMultipleItems">
                    <td>
			            <s:if test="claim.serviceInformation.serviceDetail.
			            		hussmanPartsReplacedInstalled[#status.index].inventoryLevel">
			            <s:text name="accordion_jsp.accordionPane.inventory"/>
			            </s:if>
			            <s:else>
			            <s:text name="claim.prieview.ContentPane.claim"/>
			            </s:else>
            		</td>
            		</s:if>
            		 <authz:ifProcessor>
            		  <td align="center">
            		  <s:property value="costPricePerUnit"/>
            		 </authz:ifProcessor></td>

                    <authz:ifCondition condition="!currentUserJustADealer || claim.filedBy.id == currentUser.id">
                                <s:if test="!loggedInUserAnInternalUser && claim.warrantyOrder==true">
                                         <td align="center">
                                             <s:property value="item.uom.type"/>
                                         </td>
                                         <td class="numeric" align="center">
                                             <s:text name="label.common.zeroPointzero"/>
                                         </td>
                                </s:if>
                                <s:else>
                                     <td align="center">
                                        <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
                                            <s:property value="uomMapping.mappedUomDescription"/>
                                            <span id="UOM_<s:property value="%{#status.index}"/>_<s:property value="%{#dojoindex.index}"/>" tabindex="0" >
                                                <img src="image/comments.gif" width="16" height="15" />
                                            </span>
                                            <span dojoType="dijit.Tooltip" connectId="UOM_<s:property value="%{#status.index}"/>_<s:property value="%{#dojoindex.index}"/>" >
                                                <s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
                                            </span>
                                        </s:if>
                                        <s:else>
                                            <s:property value="item.uom.type"/>
                                        </s:else>
                                     </td>
                                     <td class="numeric" align="center">
                                         <s:property value="uomAdjustedPricePerUnit"/>
                                     </td>
                                </s:else>
                    </authz:ifCondition>
                </tr>
                </s:iterator>
            </s:iterator>            
            
        </tbody>
	</table>
	</s:if>
</div>