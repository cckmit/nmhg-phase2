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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<table class="grid borderForTable" cellspacing="0" cellpadding="0">			
	<tr>
		<th width="2%" valign="middle"  class="colHeader">	</th>
		<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.part.serialNumber" /></th>
		<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
	    <th width="20%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
	   	<th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.shipmentStatus" /></th>
		<th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
		<th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.returnlocation" /></th>
	</tr>
	<s:iterator value="partReplacedBeans" status="iter2Status">
		<s:iterator value="partReturnTasks" status ="taskIterator">
		    <tr class="
			   	 <s:if test="part.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
			   	 <s:else>tableDataWhiteText</s:else>
			   	 ">
			   	  <input type="hidden" 
				 		     name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
						     value="<s:property value="task.id"/>"/>	
			   	 <td><s:checkbox name="selectedPartReplacedToAdd[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].selected"
	 		     	id="%{#claimIterator.index}_%{#partIterator.index}_%{#taskIterator.index}"/>
			   	 </td>
				<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.referredInventoryItem.serialNumber"/>  </td> 			   	
				<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
				<td width="30%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
	      		<td><s:select name="selectedPartReplacedToAdd[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].shipmentStatus" list="shipmentStatusList"
			                  id="shipmentStatus_%{#claimIterator.index}_%{#partIterator.index}_%{#taskIterator.index}" required="true" 
			                  listValue="value" listKey="key"
			                  cssStyle="width: 130px" />
		   
		  		 </td>
		  		 <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
		  		 <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.returnLocation.code"/></td>
	      	</tr>
	      	<input type="hidden"  name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].claim" 
			     value="<s:property value="claim.id"/>"/>
      	</s:iterator>
	    <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
	</s:iterator>
</table>	