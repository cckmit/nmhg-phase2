<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<u:stylePicker fileName="multiCar.css" />

<script type="text/javascript">
dojo.addOnLoad(function() {
	dojo.subscribe("/nonSerializedOwnerInfoPage/show", null, function() {
		dijit.byId("nonSerializedOwnerInfoDisplay").show();
    }); 
    if(dojo.byId("show_nonserialized_owner_info")){
    dojo.connect(dojo.byId("show_nonserialized_owner_info"),"onclick",function() {
         		dojo.publish("/nonSerializedOwnerInfoPage/show");
	 });
    }
});
</script>

<div id="nonSerializedOwnerInfoDisplay" dojoType="twms.widget.Dialog" title="<s:text name="label.matchRead.ownerInformation"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:70%">
		<table cellspacing="0" cellpadding="0" width="100%"  style="width:97.8%;margin-top:5px;font-family:Verdana, Arial, Helvetica, sans-serif; font-size:7.5pt;"  align="center" >
		    <tr> 
		    	<td>
			    	   <table cellspacing="0" cellpadding="0" width="100%"  style="width:97.8%;margin-top:5px;font-family:Verdana, Arial, Helvetica, sans-serif; font-size:7.5pt;"  align="center" >
		    <tr> 
		        <td width="20%" class="labelStyle">
			       	<s:text name="label.warrantyAdmin.ownerName"/>:      
			    </td>
			    <td class="non_editable"  width="20%">
			      	<s:property value="claim.ownerInformation.belongsTo.name"/>
			    </td>
			</tr>
			<tr>
			    <td class="labelStyle">	        	
			       	<s:text name="label.warrantyAdmin.ownerAddress"/>:	        	
			    </td>
			    <td class="non_editable">	        	
			         	<s:property value="claim.ownerInformation.addressLine1"/>
			       	    <s:hidden name="travel_location1" value="%{claim.ownerInformation.locationForGoogleMap}"/>
			    </td>
			</tr>
			<tr>
			      <td class="labelStyle">
			         <s:text name="label.warrantyAdmin.ownerCity"/>:
			       </td>
			       <td class="non_editable">
			          <s:property value="claim.ownerInformation.city"/>
			       </td>
			</tr>
			<tr>
			     <td class="labelStyle">
			         <s:text name="label.warrantyAdmin.ownerState"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="claim.ownerInformation.state"/>
			     </td>
			</tr>	     
			<tr>
			     <td class="labelStyle">
			         <s:text name="label.warrantyAdmin.ownerZip"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="claim.ownerInformation.zipCode"/>
			     </td>
			</tr>
			<tr>
			    <td class="labelStyle">
			       <s:text name="label.warrantyAdmin.ownerCountry"/>:
			    </td>
			    <td class="non_editable">
			       <s:property value="claim.ownerInformation.country"/>
			    </td>
			 </tr>	
		</table>
	    	
    	</td>
    </tr>
	</table>

		
   	 
	</div>
