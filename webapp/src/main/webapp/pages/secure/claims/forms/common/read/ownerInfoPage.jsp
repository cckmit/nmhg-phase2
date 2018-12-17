<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<u:stylePicker fileName="multiCar.css" />

<script type="text/javascript">
dojo.addOnLoad(function() {
	dojo.subscribe("/ownerInfoPage/show", null, function() {
		dijit.byId("ownerInfo").show();
    }); 
    if(dojo.byId("show_owner_info")) {
    dojo.connect(dojo.byId("show_owner_info"),"onclick",function() {
            		dojo.publish("/ownerInfoPage/show");
				}); 
	}
	dojo.subscribe("/ownerInfoPage/hide", null, function() {
		dijit.byId("ownerInfo").hide();
    });   
});
</script>

<div id="ownerInfo" dojoType="twms.widget.Dialog" title="<s:text name="label.matchRead.ownerInformation"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:70%">
    <table cellspacing="0" cellpadding="0" class="grid" style="width:97.8%;margin-top:5px;"  align="center" >
    <tr> 
        <td width="20%" class="labelStyle">
	       	<s:text name="label.warrantyAdmin.ownerName"/>:      
	    </td>
	    <td class="non_editable"  width="20%">
	      	<s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name"/>
	    </td>
	</tr>
	<tr>
	    <td class="labelStyle">	        	
	       	<s:text name="label.warrantyAdmin.ownerAddress"/>:	        	
	    </td>
	    <td class="non_editable">	        	
	       	<s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.addressLine1"/>
	       	    <s:hidden name="travel_location1" value="%{claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.locationForGoogleMap}"/>
	    </td>
	</tr>
	<tr>
	      <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerCity"/>:
	       </td>
	       <td class="non_editable">
	          <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.city"/>
	       </td>
	</tr>
	<tr>
	     <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerState"/>:
	     </td>
	     <td class="non_editable">
	         <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.state"/>
	     </td>
	</tr>	     
	<tr>
	     <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerZip"/>:
	     </td>
	     <td class="non_editable">
	         <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.zipCode"/>
	     </td>
	</tr>
	<tr>
	    <td class="labelStyle">
	       <s:text name="label.warrantyAdmin.ownerCountry"/>:
	    </td>
	    <td class="non_editable">
	       <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.address.country"/>
	    </td>
	 </tr>
	 <tr>
	      <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerType"/>:
	      </td>
	      <td class="non_editable">
	          <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.getType()"/>
	      </td>
	 </tr>   
</table>
   
</div>
