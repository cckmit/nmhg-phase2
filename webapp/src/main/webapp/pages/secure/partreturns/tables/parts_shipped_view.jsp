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
<table  cellspacing="0" cellpadding="0" cellspacing="0" class="grid borderForTable" width="100%">			
     <tr>
       <th valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
       <th valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
       <s:if test="!taskName.equals('Dealer Requested Parts Shipped')">
       <!-- CR NMHGSLMS-172 start -->
       <s:if test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
        <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:if>
       <s:elseif test="oemPartReplaced.activePartReturn.rmaNumber!=null">
          <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:elseif>
     <!-- CR NMHGSLMS-172  -->
       </s:if>
       <s:if test="WPRA">
             <th width="6%%" valign="middle" class="colHeader"><s:text name="label.partReturn.wpra" /></th>
       </s:if>
         <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.common.returnDirectlyToSupplier" /></th>
       <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
	   <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
	   <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
	   <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
	   <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
	 </tr>
	 <s:set name="showRGA" value="oemPartReplaced.activePartReturn.rmaNumber"/>
    <s:iterator value="partReplacedBeans" status="partIterator">
      <tr class="tableDataWhiteText">
       <s:if test="isLoggedInUserADealer()">
        <td ><s:property value="%{oemPartReplaced.brandItem.itemNumber}" /></td>
        </s:if>
        <s:else>
         <td ><s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
        </s:else>
        <td><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
        <s:if test="!taskName.equals('Dealer Requested Parts Shipped')">
           <!-- CR NMHGSLMS-172  start-->
	 	      <s:if test="isProcessedAutomatically() && showRGA != null">
	  			 <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.partReturnConfiguration.rmaNumber" /></td>
	 		 </s:if>
			 <s:elseif test="showRGA!=null">
	 				 <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.rmaNumber" /></td>
	 		 </s:elseif>
	 		    <!-- CR NMHGSLMS-172  end-->
	 	</s:if>
	 	<s:if test="WPRA">
	 	    <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.wpra.wpraNumber" /></td>
	 	</s:if>
	 	<td width="12%" style="padding-left:3px;">
            <s:if test="oemPartReplaced.returnDirectlyToSupplier">
                 <s:text name="label.common.yes" />
            </s:if>
            <s:else>
                  <s:text name="label.common.no" />
            </s:else>
         </td>
        <td  style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      	<td  style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
      	<td  style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
      	<td  style="padding-left:3px;"><s:property value="shipped" /></td>
      	<td  style="padding-left:3px;"><s:property value="totalNoOfParts" /></td>
      </tr>

      <s:if test="taskName.equals('Claimed Parts Receipt')">
          <s:hidden name="partReplacedBeans[%{#claimCounter}].claim" value="%{claim.id}"/>
          <s:hidden name="partReplacedBeans[%{#claimCounter}].oemPartReplaced"
                               value="%{oemPartReplaced.id}"/>
          <s:hidden name="partReplacedBeans[%{#claimCounter}].shipped" value="%{shipped}" />
          <s:set name="claimCounter" value="%{#claimCounter + 1}"/>
      </s:if>
    </s:iterator>
</table>	