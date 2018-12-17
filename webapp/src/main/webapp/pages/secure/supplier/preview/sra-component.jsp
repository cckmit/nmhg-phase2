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


<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>

<div class="bgColor" style="width: 98%">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
	<tr>
		 <td width="100%" colspan="8" nowrap="nowrap" class="sectionTitle"><s:text name="label.supplier.clubCarPartsReplaced"/></td>
	</tr>
	<tr>
		<td width="18%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.supplier.serialNumber"/>&nbsp;</td>
		<td width="10%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.quantity"/></td>
		<td width="17%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.description"/><a			href="#"></a></td>

		<td width="19%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.supplier"/></td>
		<td width="16%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.supplierPartNumber"/></td>
		
		<authz:ifUserNotInRole roles="supplier">
			<td width="10%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.price"/></td>
		<td width="10%" nowrap="nowrap" class="colHeaderTop"><s:text name="label.common.cost"/></td>
		</authz:ifUserNotInRole>	

			
	</tr>
	<s:if test="!isPartsReplacedInstalledSectionVisible()">
	<s:iterator
		value="claim.serviceInformation.serviceDetail.oEMPartsReplaced">
		<tr>
			<td  width="18%">
				<s:if test="itemReference.serialized == true">
					<s:property value="itemReference.referredInventoryItem.serialNumber" />
				</s:if><s:else>
					<s:property value="itemReference.referredItem.number" />
				</s:else>
			</td>
			<td width="10%" ><s:property value="numberOfUnits" />
			</td>
			<td width="17%" ><s:property value="itemReference.unserializedItem.description" /></td>
			<td width="19" ><s:property value="itemReference.unserializedItem.ownedBy.name"></s:property></td>	
			<td width="16" ><s:property value="supplierPartReturn.supplierItem.number"></s:property></td>
			<authz:ifUserNotInRole roles="supplier">	
				<td width="10" ><s:property value="cost()"></s:property></td>	
				<td width="10" ><s:property value="supplierPartCost"/></td>	
			</authz:ifUserNotInRole>	
		</tr>
	</s:iterator>
	</s:if>
	<s:else>
	    <s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
			<s:iterator
			value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts">
			<tr>
				<td  width="18%">
					<s:if test="itemReference.serialized == true">
						<s:property value="itemReference.referredInventoryItem.serialNumber" />
					</s:if><s:else>
						<s:property value="itemReference.referredItem.number" />
					</s:else>
				</td>
				<td width="10%" ><s:property value="numberOfUnits" />
				</td>
				<td width="17%" ><s:property value="itemReference.unserializedItem.description" /></td>
				<td width="19" ><s:property value="itemReference.unserializedItem.ownedBy.name"></s:property></td>	
				<td width="16" ><s:property value="supplierPartReturn.supplierItem.number"></s:property></td>
				<authz:ifUserNotInRole roles="supplier">	
					<td width="10" ><s:property value="cost()"></s:property></td>	
					<td width="10" ><s:property value="supplierPartCost"/></td>	
				</authz:ifUserNotInRole>	
			</tr>
		</s:iterator>
		</s:iterator>
	  </s:else>									
					
	
</table>
</div>