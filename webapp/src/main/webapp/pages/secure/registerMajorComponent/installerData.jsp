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


 <div class="policy_section_div">
   <div class="section_header"> 
      <s:if test="inventoryItem.warranty.certifiedInstaller == null">         
        <s:text name="label.majorComponent.nonCertifiedInstallerInfo"/>
      </s:if>
      <s:else>
         <s:text name="label.majorComponent.certifiedInstallerInfo"/>
      </s:else>  
   </div>
   
   <table cellspacing="0" cellpadding="0" class="grid">
       <s:if test="inventoryItem.warranty.certifiedInstaller == null">   
	          <tr>
			      <td width="20%" class="labelStyle">
			       	<s:text name="label.common.name"/>:      
			    </td>
			    <td class="non_editable"  width="25%">
			      	<s:property value="inventoryItem.warranty.nonCertifiedInstaller.name"/>
			    </td>	     
			  </tr>
			  <tr>
			    <td class="labelStyle">	        	
			       	<s:text name="label.address"/>:	        	
			    </td>
			    <td class="non_editable">	        	
			       	<s:property value="inventoryItem.warranty.nonCertifiedInstaller.address.addressLine1"/>			       	  
			    </td>
			  </tr>
			  <tr>
			      <td class="labelStyle">
			         <s:text name="label.common.city"/>:
			       </td>
			       <td class="non_editable">
			          <s:property value="inventoryItem.warranty.nonCertifiedInstaller.address.city"/>
			       </td>
			  </tr>
			  <tr>
			     <td class="labelStyle">
			         <s:text name="label.common.state"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="inventoryItem.warranty.nonCertifiedInstaller.address.state"/>
			     </td>
			  </tr>	
			  <tr>
			     <td class="labelStyle">
			         <s:text name="label.common.zipCode"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="inventoryItem.warranty.nonCertifiedInstaller.address.zipCode"/>
			     </td>
			  </tr>
			  <tr>
			    <td class="labelStyle">
			       <s:text name="label.common.country"/>:
			    </td>
			    <td class="non_editable">
			       <s:property value="inventoryItem.warranty.nonCertifiedInstaller.address.country"/>
			    </td>
			  </tr>
			  <tr>
			      <td class="labelStyle">
			         <s:text name="label.warrantyAdmin.customerType"/>:
			      </td>
			      <td class="non_editable">
			          <s:property value="inventoryItem.warranty.nonCertifiedInstaller.getType()"/>
			      </td>
			  </tr>
	  </s:if>   
	  <s:else>	     
		    <tr>
			    <td width="20%" class="labelStyle">
			       	<s:text name="label.common.name"/>:      
			    </td>
			      <td class="non_editable" width="42%">
			      	<s:property value="inventoryItem.warranty.certifiedInstaller.name"/>
			    </td>
			   		        
			</tr>
			<tr>
			    <td class="labelStyle">	        	
			       	<s:text name="label.address"/>:	        	
			    </td>
			    <td class="non_editable">	        	
			       	<s:property value="inventoryItem.warranty.certifiedInstaller.address.addressLine1"/>			       	  
			    </td>
			</tr>
			<tr>
			    <td class="labelStyle">
			         <s:text name="label.common.city"/>:
			    </td>
			    <td class="non_editable">
			          <s:property value="inventoryItem.warranty.certifiedInstaller.address.city"/>
			    </td>
			</tr>
			<tr>
			     <td class="labelStyle">
			         <s:text name="label.common.state"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="inventoryItem.warranty.certifiedInstaller.address.state"/>
			     </td>
			</tr>	
			<tr>
			     <td class="labelStyle">
			         <s:text name="label.common.zipCode"/>:
			     </td>
			     <td class="non_editable">
			         <s:property value="inventoryItem.warranty.certifiedInstaller.address.zipCode"/>
			     </td>
			</tr>
			<tr>
			    <td class="labelStyle">
			       <s:text name="label.common.country"/>:
			    </td>
			    <td class="non_editable">
			       <s:property value="inventoryItem.warranty.certifiedInstaller.address.country"/>
			    </td>
			</tr>
			<tr>
			      <td class="labelStyle">
			         <s:text name="label.warrantyAdmin.customerType"/>:
			      </td>
			      <td class="non_editable">
			          <s:property value="inventoryItem.warranty.certifiedInstaller.getType()"/>
			      </td>
			</tr>
	   </s:else>  
	 </table>     
 </div>	 
	 
	 