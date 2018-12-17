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


<div class="policy_section_div">
			<div class="section_header">Supplier Details </div>
			<table border="0" cellspacing="0" cellpadding="0" class="grid" >
				<tbody>
				
					<tr>
						<td width="20%" nowrap="nowrap" class="labelStyle">Supplier
						<s:text name="label.common.number"/>:</td>
						<td width="34%" ><s:property
							value="supplier.supplierNumber"/></td>
						<td width="20%" class="labelStyle"><s:text name="columnTitle.partSource.supplier_name"/>:</td>
						<td width="32%" ><s:property
							 value="supplier.name" /></td>
					</tr>
					
					<tr>
						<td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.sra.partSource.contactPerson" />:</td>
						<td width="34%" ><s:property
							value="supplier.address.contactPersonName"/></td>
					</tr>
					
					<tr>
						<td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.sra.partSource.address.line1"/>:</td>
						<td width="34%"><s:property
							value="supplier.address.addressLine1"/></td>
						<td width="18%" class="labelStyle"><s:text name="label.sra.partSource.address.line2"/>:</td>
						<td width="32%"><s:property
							value="supplier.address.addressLine2"/></td>
					</tr>
					
					<tr>
						<td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.city" />:</td>
						<td width="34%" ><s:property
							value="supplier.address.city"/></td>
						<td width="18%" class="labelStyle"><s:text name="label.common.state" />:</td>
						<td width="32%" ><s:property
							value="supplier.address.state"/></td>
					</tr>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.country" />:</td>
						<td width="34%" ><s:property
							value="supplier.address.country"/></td>
					</tr>
					<tr>
						<td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.email" />:</td>
						<td width="34%" ><s:property
							value="supplier.address.email"/></td>
						<td width="18%" class="labelStyle"><s:text name="label.common.phone" />:</td>
						<td width="32%" ><s:property
							value="supplier.address.phone"/></td>	
					</tr>
					
					<tr>
						<td width="18%" class="labelStyle"><s:text name="label.sra.partSource.prefferedLocationType" />:</td>
						<td width="32%" >
							<s:property value="supplier.preferredLocationType"/></td>
						<td width="18%" nowrap="nowrap" class="labelStyle"><s:text name="label.sra.partSource.prefferedLocation" />:</td>
						<td width="32%" >
						<s:property value="supplier.preferredLocation.code"/></td>
					</tr>
										
				</tbody>
			</table>
 
</div>