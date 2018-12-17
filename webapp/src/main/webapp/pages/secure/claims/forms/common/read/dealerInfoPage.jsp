<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<u:stylePicker fileName="multiCar.css" />

<script type="text/javascript">
dojo.addOnLoad(function() {
	dojo.subscribe("/dealerInfoPage/show", null, function() {
		dijit.byId("dealerInfo").show();
    }); 
	if (dojo.byId("show_dealer_info"))
	{
		dojo.connect(dojo.byId("show_dealer_info"),"onclick",function() {
			dojo.publish("/dealerInfoPage/show");
		}); 
	}
	dojo.subscribe("/dealerInfoPage/hide", null, function() {
		dijit.byId("dealerInfo").hide();
    });   
});
</script>

<div id="dealerInfo" dojoType="twms.widget.Dialog" title="<s:text name="title.partReturnConfiguration.dealerInfo"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:70%;overflow:hidden;">
    <table cellspacing="0" cellpadding="0" class="grid" style="width:97.8%;margin-top:5px;"  align="center" >
    <tr> 
           <td width="20%" class="labelStyle">
	          	<s:text name="label.common.dealerNumber"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.dealerNumber"/>
	        </td>
	 </tr>
	 <tr>       
	        <td class="labelStyle">	        	
	        	<s:text name="label.common.dealerName"/>:	        	
	        </td>
	        <td class="non_editable">	        	
	        	<s:property value="claim.forDealerShip.name"/>	        	
	        </td>  
	         
	 </tr>
	 <tr>
	     <td class="labelStyle">
	          <s:text name="label.warrantyAdmin.dealerAddress"/>:
	     </td>
	     <td class="non_editable">
	          <s:property value="claim.forDealerShip.address.addressLine1"/>
	        	 <s:hidden name="travel_location" value="%{claim.forDealerShip.address.locationForGoogleMap}"/>
	     </td>
	 </tr>   
      <tr>
          <td class="labelStyle" >
	          	<s:text name="label.common.city"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.address.city"/>	
	        </td>
	  </tr>
	  <tr>      
	        <td class="labelStyle">
	          	<s:text name="label.common.state"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.address.state"/>		
	        </td>
	   </tr>
	   <tr> 
	       <td class="labelStyle">
	          	<s:text name="label.common.country"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.address.country"/>		
	        </td>
	   </tr>     
	   <tr>     
	         <td class="labelStyle">
	          	<s:text name="label.common.zipCode"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.address.zipCode"/>			
	        </td>    
       </tr>
	<tr>
	
	 <td class="labelStyle">
	          	<s:text name="label.common.dealerBrands"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.brand"/>			
	        </td>
	        <tr>
	        
	        
	         <td class="labelStyle">
	          	<s:text name="label.common.dealerMarketingGroupCodes"/>:      
	        </td>
	        <td class="non_editable"  width="20%">
	        	<s:property value="claim.forDealerShip.marketingGroup"/>			
	        </td>
	            
		  
 	</tr>
</table>

</div>
