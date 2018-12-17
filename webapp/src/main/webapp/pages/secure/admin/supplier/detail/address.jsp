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

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");    
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.ValidationTextBox");
</script>

<div dojoType="twms.widget.Dialog" id="supplier_address" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
     toggleDuration="250" formNode="frmCreateSupplier" style="width:70%; height:30%;">
 <div dojoType="dijit.layout.ContentPane" layoutAlign="top">
<table width="100%" style="border-bottom:1px solid #EFEBF7;">
        	<tbody>
            	<tr>
          <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="columnTitle.partSource.address_details"/></td>
        </tr>
        </tbody>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
				<tbody>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.sra.partSource.address.line1"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.addressLine1" 
		            		value='<s:property value="supplier.address.addressLine1"/>'/> 
		            		</td>
					
						<td width="18%" class="labelNormal"><s:text name="label.sra.partSource.address.line2"/>:</td>
						<td width="32%" >
							<input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
							name="supplier.address.addressLine2"
							value='<s:property value="supplier.address.addressLine2"/>'
							/>
		            		</td>
					</tr>
					
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="columnTitle.common.city"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.city" 
		            		value='<s:property value="supplier.address.city"/>'/> 
						</td>
						<td width="18%" class="labelNormal"><s:text name="columnTitle.common.state"/>:</td>
						<td width="32%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.state" 
		            		value='<s:property value="supplier.address.state"/>'/> 
						</td>
					</tr>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.location"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.location" 
		            		value='<s:property value="supplier.address.location"/>'/> 
						</td>					
		            	<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.zipCode"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.zipCode" 
		            		value='<s:property value="supplier.address.zipCode"/>'/> 
						</td>		            		
					</tr>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="columnTitle.common.country"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
		            		name="supplier.address.country" 
		            		value='<s:property value="supplier.address.country"/>'/> 
						</td>
					</tr>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.email"/>:</td>
						<td width="34%" >
						<input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
		            		name="supplier.address.email"
		            		value='<s:property value="supplier.address.email"/>'/>
						</td>
						<td width="18%" class="labelNormal"><s:text name="label.common.phone"/>:</td>
						<td width="32%" >
						<input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
		            		name="supplier.address.phone"
		            		value='<s:property value="supplier.address.phone"/>'/>
		            		
		            		 
						</td>	
					</tr>
				</tbody>
</table>
						     

<table width="100%" cellpadding="0" cellspacing="0">
 			<tr>
 			<td width="40%">&nbsp;</td>
       		<td id="submitSection" colspan="4" align="center" class="buttons" style="padding-top: 20px;">
            	<input type="button" id="closePopup"  value='<s:text name="button.common.continue"/>'/>
            </td>
        	</tr>
		</table>
		</div>
</div>
