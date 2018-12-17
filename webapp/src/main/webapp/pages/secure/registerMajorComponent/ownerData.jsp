<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>


 <div class="policy_section_div" id="ownerInfoDiv" >
   <div class="section_header">        
      <s:text name="label.matchRead.ownerInformation"/>
   </div>
   
	 <table cellspacing="0" cellpadding="0" class="grid">
	  <tr>
	      <td width="10%" class="labelStyle">
	       	<s:text name="label.warrantyAdmin.ownerName"/>:      
	    </td>
	    <td class="non_editable"  width="40%">
	      	<s:property value="inventoryItem.ownedBy.name"/>
	    </td>	     
	  </tr>
	  <tr>
	    <td class="labelStyle">	        	
	       	<s:text name="label.warrantyAdmin.ownerAddress"/>:	        	
	    </td>
	    <td class="non_editable">	        	
	       	<s:property value="inventoryItem.ownedBy.address.addressLine1"/>
	       	    <s:hidden name="travel_location1" value="%{inventoryItem.ownedBy.address.locationForGoogleMap}"/>
	    </td>
	  </tr>
	  <tr>
	      <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerCity"/>:
	       </td>
	       <td class="non_editable">
	          <s:property value="inventoryItem.ownedBy.address.city"/>
	       </td>
	  </tr>
	  <tr>
	     <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerState"/>:
	     </td>
	     <td class="non_editable">
	         <s:property value="inventoryItem.ownedBy.address.state"/>
	     </td>
	  </tr>	
	  <tr>
	     <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerZip"/>:
	     </td>
	     <td class="non_editable">
	         <s:property value="inventoryItem.ownedBy.address.zipCode"/>
	     </td>
	  </tr>
	  <tr>
	    <td class="labelStyle">
	       <s:text name="label.warrantyAdmin.ownerCountry"/>:
	    </td>
	    <td class="non_editable">
	       <s:property value="inventoryItem.ownedBy.address.country"/>
	    </td>
	  </tr>
	  <tr>
	      <td class="labelStyle">
	         <s:text name="label.warrantyAdmin.ownerType"/>:
	      </td>
	      <td class="non_editable">
	          <s:property value="inventoryItem.ownedBy.getType()"/>
	      </td>
	  </tr>   
	
	</table>
</div>

	



  
   
     
  
   
   


  	