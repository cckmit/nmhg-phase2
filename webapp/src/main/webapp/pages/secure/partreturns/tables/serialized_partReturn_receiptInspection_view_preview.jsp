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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<style>
<!--
.colHeader{padding:5px 0 5px 4px;}
-->
</style>
<div style="width:100%">
		<table cellspacing="0" cellpadding="0" class="grid borderForTable">
	      <tr>
		    <th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.part.serialNumber" /></th>
			<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
		    <th width="25%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
		    <!--  PArt of serial number  -->
		    <!--  PArt of part number  -->
		     <!--  PArt of description -->
		    
		    <s:if test="isTaskDuePartsInspection()">
		    <th width="25%" valign="middle" class="colHeader"><s:text name="columnTitle.common.returnlocation" /></th>
		    </s:if>
		  </tr>
	 	  <s:iterator value="partReplacedBeans" status="partIterator">
 		  <s:iterator value="partReturnTasks" status ="taskIterator">
	 		<tr class="
			     	 <s:if test="oemPartReplaced.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
			     	 <s:else>tableDataWhiteText</s:else>
		     		 ">
	 			<td style="padding-left:4px;"><s:property value="oemPartReplaced.itemReference.referredInventoryItem.serialNumber"/> </td> 
			  	<td style="padding-left:4px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
			  	<td style="padding-left:4px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
			  	
			  	<s:if test="isTaskDuePartsInspection()">
			  	<td style="padding-left:4px;"><s:property value="getPartReturn().warehouseLocation"/></td>
			  	</s:if>
			</tr>
	     </s:iterator>
    	<s:set name="partsCounter" value="%{#partsCounter + 1}"/>
   </s:iterator>
</table>

