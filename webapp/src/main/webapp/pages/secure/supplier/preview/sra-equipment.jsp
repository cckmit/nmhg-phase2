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
	<div class="bgColor" style="width: 98%">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="label.inventory.equipmentDetails" /> </td>
        </tr>
        <tr>
          <td width="20%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.serialNumber" /></td>
          <td width="30%" class="label"><s:property 
          	value="claim.itemReference.referredInventoryItem.serialNumber"/></td>
          <td width="20%" class="labelNormal"><s:text name="label.common.itemNumber" /></td>
          <td width="30%" class="label"><s:property 
          	value="claim.itemReference.unserializedItem.number"/></td>
        </tr>
        <tr>
          <td width="20%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.product" /></td>
          <td width="30%" class="label"><s:property 
          	value="claim.itemReference.unserializedItem.product"/></td>
          <td width="20%" class="labelNormal"><s:text name="label.common.model" /></td>
          <td width="30%" class="label"><s:property 
          	value="claim.itemReference.unserializedItem.model.name"/></td>
        </tr>
        <tr>
          <td width="20%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.hoursOnTruck" /></td>
          <td width="30%" class="label"><s:property value="claim.hoursInService"/> <s:text name="label.common.hrs"/> </td>
          <td width="20%" class="labelNormal"><s:text name="label.common.dateOfDelivery" /></td>
          <td width="30%" class="label"><s:property value="claim.equipmentBilledDate"/></td>
        </tr>
        <tr>
          <td width="20%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.warrantyStartDate" /></td>
          <td width="30%" class="label"><s:property value="claim.applicablePolicy.registeredPolicy.warrantyPeriod.fromDate"/></td>
          <td width="20%" class="labelNormal"><s:text name="label.common.warrantyEndDate" /></td>
          <td width="30%" class="label"><s:property value="claim.applicablePolicy.registeredPolicy.warrantyPeriod.tillDate"/></td>
        </tr>
      </table>
</div>