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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="authz" uri="authz"%>
<style>
.mainTitle{
cursor:pointer;
}
table.grid > tbody > tr > td  {
border:none !important;
}
</style>
<table width="100%" cellspacing="0" cellpadding="0" class="grid">
<tbody>
	<tr>
		<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.serialNumber"/>:</td>
        <td class=""  width="35%">
            <s:property value="inventoryItem.serialNumber"/>            
        </td>
        <td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.businessUnitName"/>:</td>
        <td class="" width="35%">
            <s:property value="inventoryItem.businessUnitInfo.name"/>  
        </td>
        
    </tr>
    <tr>
        <td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.productType"/>:</td>
        <td class="">
            <s:property value="inventoryItem.ofType.product.isPartOf.name"/>
        </td>
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="Manufacturing Site"/>:</td>
        <td><s:property value="inventoryItem.manufacturingSiteInventory.description"/></td>
    </tr>
   
    <tr>
        <td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.product"/>:</td>
        <td class=""><s:property value="inventoryItem.ofType.product.groupCode"/></td>
        <s:if test="(getLoggedInUser().isInternalUser())">
        <td  width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.retailMachineTransfer.buildDate"/>:</td>
        <td class=""><s:property value="inventoryItem.builtOn"/></td>
        </s:if>        
    </tr>
    <tr>
       <s:if test="ctsDate!=null"> 
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.ctsDate"/>:</td>
        <td class=""><s:property value="ctsDate"/></td>
       </s:if>
       <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="labe.dealer.shipment.location"/>:</td>
        <td class=""><s:property value="inventoryItem.dealerLocation"/></td>
    </tr>
    <s:if test="!loggedInUserADealer">
     <tr>
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.discountPercent"/>:</td>
        <td class=""><s:property value="inventoryItem.discountPercent"/></td>
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.disc.authorizationNumber"/>:</td>
        <td class=""><s:property value="inventoryItem.discAuthorizationNumber"/></td>
     </tr>
     </s:if>
    <tr>
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.modelWithProductCode"/>:</td>
        <td class=""><s:if test='inventoryItem.ofType.model.machineUrl != null'> <a href="<s:property value='inventoryItem.ofType.model.machineUrl'/>" target="_new"></s:if><s:property value="inventoryItem.ofType.model.itemGroupDescription"/></a></td>
       <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.shipmentDate"/>:</td>
        <td class=""><s:property value="inventoryItem.shipmentDate"/></td>
    </tr>
      <tr>
        <s:if test="orderReceivedDate!=null"> 
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.orderReceivedDate"/>:</td>
        <td class=""><s:property value="orderReceivedDate"/></td>
        </s:if>
       <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.invoiceDate"/>:</td>
        <td class=""><s:property value="inventoryItem.invoiceDate"/></td>
      </tr>
      
      <!-- commented for the CR-NMHGSLMS-276-->
      
  <%--  
	 <tr>
   		<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.itemNumber"/>:</td>
	    <td class="">
	    	<s:property value="inventoryItem.ofType.number"/>
	     </td> --%>
	    <s:if test="buConfigAMER"> 
	     <tr>
	     <td class="labelStyle">
	     <s:text name="label.common.preOrderBooking" />:
	     </td> 
	    <s:if test="inventoryItem.getPreOrderBooking()"> <!--  for the CR-NMHGSLMS-506-->
	  		 <td>
	     	Yes
	     </td>
	     </s:if>
	     <s:else>
	        <td>
	     	No
	     </td>
	     </s:else>
	 </tr> 
	 </s:if>
	 <!-- Commented part Ends here -->
	 
	   <tr>
	      <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.MarketingGroupCode"/>:</td>
	      <td class="">
	    	<s:property value="inventoryItem.marketingGroupCode"/>
	     </td>
	      <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.item.ServiceCategory"/>:</td>
	      <td class="">
	    	<s:property value="inventoryItem.ofType.serviceCategory"/>
	     </td>
	 </tr>  
	 <tr>
	 	<s:if test="itemBrands!=null && itemBrands.size()>0">
	 	<tr>
	 	<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.item.Brands"/>:</td>
	 	  <td class="">
		<s:iterator value="itemBrands" status="status">
   		 <s:property value="itemBrands[#status.index].brand" />
   		 <s:if test="#status.index!=(itemBrands.size()-1)">
   		   ,
   		 </s:if>
		</s:iterator>
		</td>
			</tr>
		</s:if>
	 </tr>
		<s:if test="inventoryItem.isRetailed()">
			<tr>
				<td></td>
				<td></td>
				<td width="24%" nowrap="nowrap" class="labelStyle"><s:text
						name="label.common.dateOfDelivery" />:</td>
				<td class=""><s:property value="inventoryItem.deliveryDate" /></td>
			</tr>
		</s:if>
		<tr>
     <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.item.productDescription"/>:</td>
	<td class="">

		<s:property value="inventoryItem.ofType.product.itemGroupDescription"/>
	</td>
	<s:if test="inventoryItem.isRetailed()">
	         <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.warrantyStartDate"/>:</td>
	        <td class=""><s:property value="inventoryItem.wntyStartDate"/></td>
	        
	 </s:if>
	 <s:else>
	 <td colspan="2">&nbsp;</td>
	 </s:else>
    </tr>
    
    <tr>
    <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.hoursOnService"/>:</td>
        <td class=""><s:property value="inventoryItem.hoursOnMachine"/></td>
        <s:if test="inventoryItem.isRetailed()">
        <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="columnTitle.common.warrantyEndDate"/>:</td>
	        <td class=""><s:property value="inventoryItem.wntyEndDate"/></td>
	         </s:if>
	          <s:else>
	 <td colspan="2">&nbsp;</td>
	 </s:else>
	 </tr>
	 <!-- commenting as per bug NMHGSLMS-882 -->
	 <%-- <s:if test = "isLoggedInUserAnInternalUser()"> --%>
	 <tr>
	  <td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.itaTruckClass"/>:</td>
        <td class=""><s:property value="inventoryItem.ofType.product.isPartOf.groupCode"/></td>        
	 </tr>
	<%--  </s:if> --%>
	 <tr><td colspan="4" style="padding:0; margin:0;"><hr/></td></tr>
	 <tr>
		<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.itemCondition"/>:</td>
        <td class=""  width="35%">
            <s:if test="%{inventoryItem.conditionType.itemCondition.equals('NEW')}">
        		<s:text name="label.warrantyAdmin.itemCondition.new"/>
        	</s:if>
        	<s:elseif test="%{inventoryItem.conditionType.itemCondition.equals('SCRAP')}">
        		<s:text name="label.warrantyAdmin.itemCondition.scrap"/>
        	</s:elseif>
        	<s:elseif test="%{inventoryItem.conditionType.itemCondition.equals('CONSIGNMENT')}">
        		<s:text name="label.warrantyAdmin.itemCondition.consignment"/>
        	</s:elseif>
        	<s:elseif test="%{inventoryItem.conditionType.itemCondition.equals('PREMIUM_RENTAL')}">
        		<s:text name="label.warrantyAdmin.itemCondition.premiumretail"/>
        	</s:elseif>
        	<s:elseif test="%{inventoryItem.conditionType.itemCondition.equals('PREOWNED')}">
        		<s:text name="label.warrantyAdmin.itemCondition.preowned"/>
        	</s:elseif>
        	<s:elseif test="%{inventoryItem.conditionType.itemCondition.equals('STOLEN')}">
        		<s:text name="label.warrantyAdmin.itemCondition.stolen"/>
        	</s:elseif>
        	<s:else>
        		<s:text name="label.warrantyAdmin.itemCondition.refurbished"/>
        	</s:else>         
        </td>
        <s:if test="isDRDoneByLoggedInUser() || isUserInventoryFullView()">
			<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.salesOrderNumber"/>:</td>
	        <td class="" width="35%">
	        	<s:property value="inventoryItem.salesOrderNumber"/>
	        </td>
		</s:if>
    </tr>
    <tr>
		<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownership"/>:</td>
        <td class=""  width="35%">
            <s:property value="inventoryItem.ownershipState.name"/>         
        </td>
        <s:if test="!loggedInUserADealer">
        <s:if test="isDRDoneByLoggedInUser() || isUserInventoryFullView()">	
			<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.newClaim.invoice"/>:</td>
	        <td class="" width="35%"><s:property value="inventoryItem.invoiceNumber"/></td>
	    </s:if>
	    </s:if>
    </tr>
    <tr>
		<s:if test="inventoryAttributes.get('EngineSerialNo')!=null">
		<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.inventory.engineSerialNumber"/>:</td>
        <td width="35%">
            <s:property value="inventoryAttributes.get('EngineSerialNo')"/>         
        </td>
       	</s:if>
		<s:if test="(isDRDoneByLoggedInUser() || isUserInventoryFullView()) && isFactoryOrderNumberRequired()">
			<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.factoryOrderNumber"/>:</td>
		    <td width="35%">
		    	<s:property value="inventoryItem.factoryOrderNumber"/>  
		    </td>
	    </s:if>
	  
        
    </tr>
    <tr>
		<s:if test='inventoryItem.pendingWarranty && !inventoryItem.latestWarranty.status.status.equals("Draft")'>
			<s:if test="inventoryItem.latestWarranty.transactionType.trnxTypeKey=='DR'">
				<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.warranty.registration.DRPendingStatus"/>:</td>
	        </s:if>
	        <s:elseif test="inventoryItem.latestWarranty.transactionType.trnxTypeKey=='ETR'">
                <td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.warranty.registration.ETRPendingStatus"/>:</td>
            </s:elseif>
	        <td width="35%">
	            <s:text name="label.yes"/>         
	        </td>
       	</s:if>
		
		<s:if test="inventoryAttributes.get('DataSource')!=null && (getLoggedInUser().isInternalUser())">
			<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.inventory.dataSource"/>:</td>
		    <td width="35%">
		    	<s:property value="inventoryAttributes.get('DataSource')"/>	  
		    </td>
	    </s:if>
    </tr>
    <tr><td colspan="4" style="height:10px;"></td></tr>
    </tbody>
</table>

<div class="borderTable"></div>
 <s:if test="isStockInventory()">
      <u:fold id="inventory_section_1" label="%{getText('label.common.owningDealer')}" foldableClass="inventory_section_1"
	            tagType="div" cssClass="mainTitle" shownInitially="true"/>
 </s:if>
 <s:elseif test="isInternalUser()||isRetailingDealer()">
    <u:fold id="inventory_section_1" label="%{getText('label.common.retailingDealer')}" foldableClass="inventory_section_1"
	            tagType="div" cssClass="mainTitle" shownInitially="true"/> 
 </s:elseif>
            <div class="borderTable"></div>	
 <s:if test="isInternalUser()||isRetailingDealer() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
<table width="100%" class="spacingBtwRows" style="margin-top:10px;">
<tbody class="inventory_section_1">

	<s:if test="!isStockInventory() && !isIri_StockInventory()">
    <tr> 
	        <td class="" >
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()()"> 
	        		<s:property value="getRetailingDealer().getDealerNumber()"/>
	        	</s:if>	
	        </td>
    
    </tr>
    </s:if>   
    <tr>
            <td  nowrap="nowrap" class="labelStyle">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.common.dealerName"/>:
	        	</s:if>
	        </td>
	        <td class="">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:property value="getRetailingDealer().name"/>
	        	</s:if>	
	        </td>         
    
    </tr>	
 <tr>
 	<td  nowrap="nowrap" class="labelStyle">
	           <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">        	
	        		<s:text name="label.common.dealerType"/>:      
	       	   </s:if>
	       	</td>   
	       	<td class="" >
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:property value="getRetailingDealer().getType()"/>
	        	</s:if>	
	        </td>
 	
 </tr>
 
 <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.customerclassification"/>:</td>
        <td><s:property value="getRetailingDealer().getCustomerClassification()"/></td>
  </tr> 
 <tr>
 	<td  nowrap="nowrap" class="labelStyle">
	            <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.warrantyAdmin.dealerAddress"/>:
	        	</s:if>
	        </td>
	        <td class="" >
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:property value="getRetailingDealer().address.addressLine1"/>
	        		<s:if test="getRetailingDealer().address.addressLine2 != null">
	        		, <s:property value="getRetailingDealer().address.addressLine2"/>
	        		</s:if>
	        		<s:hidden name="travel_location" value="%{getRetailingDealer().address.locationForGoogleMap}"/>
	        	</s:if>
	        </td>
 	
 </tr>
 <tr>
 	<td  nowrap="nowrap" class="labelStyle">
        	 <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
        	   <s:text name="label.warrantyAdmin.dealerCity"/>:
        	</s:if> 
        	</td>
        	<td class="" >
        	  <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
        		<s:property value="getRetailingDealer().address.city"/>
        	   </s:if>	
        	</td>
 	
 </tr>
<tr>
<td  nowrap="nowrap" class="labelStyle">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.warrantyAdmin.dealerState"/>:
	        	</s:if>	
	        </td>
	        <td class="" >
		        <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
		        	<s:property value="getRetailingDealer().address.state"/>
		        </s:if>	
	        </td>

</tr>
<tr>
<td nowrap="nowrap" class="labelStyle">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.warrantyAdmin.dealerZip"/>:
	        	</s:if>	
	        </td>
	        <td class="" >
		        <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
		        	<s:property value="getRetailingDealer().address.zipCode"/>
		        </s:if>	
	        </td>

</tr>
<tr>
<td  nowrap="nowrap" class="labelStyle">
        		<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
        			<s:text name="label.warrantyAdmin.dealerCountry"/>:
        		</s:if>
        	</td>
        	<td class="" >
        		<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
        			<s:property value="getRetailingDealer().address.country"/>
        		</s:if>	
        	</td>        

</tr>
 <tr>
	 	<s:if test="dealerBrands!=null && dealerBrands.size()>0">
	 	<tr>
	 	<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.dealerBrands"/>:</td>
	 	  <td class="">
		<s:iterator value="dealerBrands" status="status">
   		 <s:property value="dealerBrands[#status.index]" />
   		 <s:if test="#status.index!=(dealerBrands.size()-1)">
   		   ,
   		 </s:if>
		</s:iterator>
		</td>
			</tr>
		</s:if>
 </tr>
  <tr>
	 	<s:if test="dealerMarketingGroupCodes!=null && dealerMarketingGroupCodes.size()>0">
	 	<tr>
	 	<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.dealerMarketingGroupCodes"/>:</td>
	 	  <td class="">
		<s:iterator value="dealerMarketingGroupCodes" status="status">
   		 <s:property value="dealerMarketingGroupCodes[#status.index]" />
   		 <s:if test="#status.index!=(dealerMarketingGroupCodes.size()-1)">
   		   ,
   		 </s:if>
		</s:iterator>
		</td>
			</tr>
		</s:if>
 </tr> 
</tbody>
</table>

</s:if>

<!--NMHGSLMS-578 changes  -->

<div class="borderTable"></div>
<!-- commented to resolve bug:NMHGSLMS-651  -->
<%--  <s:if test="isStockInventory()">
      <u:fold id="inventory_section_1" label="%{getText('label.common.shipToDealer')}" foldableClass="inventory_section_1"
	            tagType="div" cssClass="mainTitle" shownInitially="true"/>
 </s:if> --%>
 <%-- <s:elseif test="isInternalUser()||isRetailingDealer()"> --%>
 <s:if test="displayShipToDealerDetails()">
    <u:fold id="inventory_section_1" label="%{getText('label.common.shipToDealer')}" foldableClass="inventory_section_1"
	            tagType="div" cssClass="mainTitle" shownInitially="true"/> 
<%--  </s:elseif> --%>
            <div class="borderTable"></div>	
               <tr> 
 <s:if test="isInternalUser()||isRetailingDealer() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
<table width="70%" class="spacingBtwRows" style="margin-top:10px;">
<tbody class="inventory_section_1">
 <tr>
            <td  nowrap="nowrap" class="labelStyle">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.common.dealerName"/>:
	        	</s:if>
	        </td>
	        <td class="">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:property value="inventoryItem.getShipTo().name"/>
	        	</s:if>	
	        </td>         
    
    </tr>	
 	 <tr>
 			<td  nowrap="nowrap" class="labelStyle">
	            <s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:text name="label.warrantyAdmin.dealerLocation"/>:
	        	</s:if>
	        </td>
	        <td class="">
	        	<s:if test="isRetailingDealer() || isUserInventoryFullView() || isLoggedInDealerParentDealer() || isLoggedInDealerShipToDealer()">
	        		<s:property value="getShipToDealerAddress()"/>	        		
	        	</s:if>
	        </td> 	
 	</tr>
</tbody>
</table>
</s:if>
</tr>
</s:if>
<!-- END of NMHGSLMS-578 Changes -->

<s:if test="((isInternalUser()||!isRetailingDealer()) && getServiceDealer() != null)">
<u:fold id="inventory_section_2" label="%{getText('label.equipmentInfo.servicingDealer')}" foldableClass="inventory_section_2"
            tagType="div" cssClass="mainTitle" shownInitially="true"/>
            <div class="borderTable"></div>	
	<table  width="100%" class="spacingBtwRows">
		<tbody class="inventory_section_2">
		    <tr>    
		       	<td width="24%" nowrap="nowrap" class="labelStyle"><s:text name="label.equipmentInfo.servicingDealerNumber"/>:</td>
		       	<td><s:property value="getServiceDealer().dealerNumber"/></td>	        
		    </tr>
		    <tr>   	
		       	<td nowrap="nowrap" class="labelStyle" ><s:text name="label.common.dealerName"/>
		       	</td>
		       	<td><s:property value="getServiceDealer().name"/></td>
		   
		    </tr>
		    <tr>    
		       	<td class="labelStyle"><s:text name="label.common.dealerType"/>
		       	</td>
		       	<td><s:property value="getServiceDealer().getType()"/></td>
		    </tr>
		    <tr>
		       	<td class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.dealerAddress"/>:
		       	</td>
		       	<td><s:property value="getServiceDealer().address.addressLine1"/>
		       		<s:if test="getServiceDealer().address.addressLine2 != null">
	        		, <s:property value="getServiceDealer().address.addressLine2"/>
	        		</s:if>
		       	<s:hidden name="travel_location" value="%{getServiceDealer().address.locationForGoogleMap}"/>
		           </td>
		    </tr>
		    <tr>
		       	<td nowrap="nowrap" class="labelStyle">
		       		<s:text name="label.warrantyAdmin.dealerCity"/>:
		       	</td>        	
		       	<td><s:property value="getServiceDealer().address.city"/></td>
		    </tr>
		    <tr>   	
		       	<td  nowrap="nowrap" class="labelStyle" ><s:text name="label.warrantyAdmin.dealerState"/>:
		       	</td>
		       	<td><s:property value="getServiceDealer().address.state"/>
		       	</td>
		    </tr>
		    <tr>    	
		       	<td nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.dealerZip"/>:
		       	</td>
		       	<td><s:property value="getServiceDealer().address.zipCode"/></td>
		    </tr>
		    <tr>    
		       	<td nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.dealerCountry"/>:
		       	</td>
		       	<td><s:property value="getServiceDealer().address.country"/>
		       	</td>
		    </tr>
		</tbody>
	</table>
</s:if> 

<u:fold id="inventory_section_3" label="%{getText('label.matchRead.ownerInformation')}" foldableClass="inventory_section_3"
            tagType="div" cssClass="mainTitle" shownInitially="true"/>
            <div class="borderTable"></div>	
<table width="100%" class="spacingBtwRows" style="margin-top:10px;">
<tbody class="inventory_section_3">
    <tr>
    	<s:if test="(inventoryItem.isRetailed() && (isUserInventoryFullView() ||(isDifferentDealerAndOwner() && isCanViewOwner()))) || (inventoryItem.getPreOrderBooking() && isCustomerExists())">
	            <s:if test="inventoryItem.ownedBy.name == null">
	                <td nowrap="nowrap" class="labelStyle" width="24%"><s:text name="label.warrantyAdmin.ownerName"/>:</td>
	                <td><s:property value="inventoryItem.ownedBy.companyName"/></td>
	            </s:if>
	            <s:else>
	                <td nowrap="nowrap" class="labelStyle" width="24%"><s:text name="label.warrantyAdmin.ownerName"/></td>
	                <td ><s:property value="inventoryItem.ownedBy.name"/></td>
	             </s:else>
	    </s:if>	        
    	
	</tr>
	<tr>
		   <s:if test="(inventoryItem.isRetailed() && (isUserInventoryFullView() ||(isDifferentDealerAndOwner() && isCanViewOwner()))) || (inventoryItem.getPreOrderBooking() && isCustomerExists())">
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerType"/>:</td>
	            <td><s:property value="inventoryItem.ownedBy.getType()"/></td>
	        </s:if>
		
	</tr>
	<tr>
		<s:if test ="getServiceDealer()==null">
			<s:set name="fromAddressForGoogleMap" value="%{getRetailingDealer().address.locationForGoogleMap}" />
		</s:if>	
		<s:else>
			<s:set name="fromAddressForGoogleMap" value="%{getServiceDealer().address.locationForGoogleMap}" />
		</s:else>
        <s:if test="(inventoryItem.isRetailed() && (isUserInventoryFullView() ||(isDifferentDealerAndOwner() && isCanViewOwner()))) || (inventoryItem.getPreOrderBooking() && isCustomerExists())">
            <td nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerAddress"/>:</td>
            <td><s:property value="inventoryItem.ownedBy.address.addressLine1"/>
            	<s:if test="inventoryItem.ownedBy.address.addressLine2 != null">
	        		, <s:property value="inventoryItem.ownedBy.address.addressLine2"/>
	        	</s:if>
	            <s:hidden name="travel_location1" value="%{inventoryItem.ownedBy.address.locationForGoogleMap}"/>
            </td>
        </s:if> 
       		
	</tr>
	<s:if test="(inventoryItem.isRetailed() && (isUserInventoryFullView() ||(isDifferentDealerAndOwner() && isCanViewOwner()))) || (inventoryItem.getPreOrderBooking() && isCustomerExists())">
	<tr>
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerCity"/>:</td>
	            <td><s:property value="inventoryItem.ownedBy.address.city"/></td>
	</tr>
	<tr>
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerState"/>:</td>
	            <td ><s:property value="inventoryItem.ownedBy.address.state"/></td>
	</tr>
		<tr>
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerCounty"/>:</td>
	            <td ><s:property value="inventoryItem.ownedBy.address.countyCodeWithName"/></td>
	        </s:if>
	        		
	</tr>
	<tr>
			<s:if test="(inventoryItem.isRetailed() && (isUserInventoryFullView() ||(isDifferentDealerAndOwner() && isCanViewOwner()))) || (inventoryItem.getPreOrderBooking() && isCustomerExists())">
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerZip"/>:</td>
	            <td><s:property value="inventoryItem.ownedBy.address.zipCode"/></td>
	</tr>
	<tr>
	            <td  nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.ownerCountry"/>:</td>
	            <td><s:property value="inventoryItem.ownedBy.address.country"/></td>
		</tr>
	        </s:if>
</tbody>
</table>

<%-- <table width="100%" class="spacingBtwRows"> 
	<tbody>
		<tr><td style="padding: 0pt; margin: 0pt;" colspan="4"><hr></td></tr>
		<tr>
			<td width="24%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.operator"/>:</td>
	        <td width="35%">
	            <s:property value="inventoryItem.operator.name"/>
	        </td>
	        <td nowrap="nowrap" class="labelStyle" width="24%"><s:text name="label.common.installingDealer"/>:</td>
	        <td width="35%"><s:property value="inventoryItem.installingDealer.name"/></td>
		</tr>
		<tr>
			<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.oem"/>:</td>
	        <td>
	            <s:property value="inventoryItem.oem.description"/>
	        </td>
	        <td nowrap="nowrap" class="labelStyle"><s:text name="label.common.dateOfInstallation"/>:</td>
	        <td><s:property value="inventoryItem.installationDate"/></td>
		</tr>
	</tbody>
</table>--%>