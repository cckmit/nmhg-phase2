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
		<th width="2%" valign="middle">
			
		</th>
		<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
	    <th width="20%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
	   	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
		<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
		<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
		<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
		<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
    	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.ship" /></th>
	    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
		<th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.duedays" /></th>
		<th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.overdueDays" /></th>
	</tr>
	<s:iterator value="partReplacedBeans" status="iter2Status">
	    <tr class="
		   	 <s:if test="part.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
		   	 <s:else>tableDataWhiteText</s:else>
		   	 ">
		   	 <td align="center">
		   	<s:checkbox  
 		     name="selectedPartReplacedToAdd[%{#partsCounter}].selected"
 		      
 		     id="%{#claimIterator.index}_%{#partIterator.index}"/>
      		 <s:iterator value="partReturnTasks" status ="taskIterator">
        	  <input type="hidden" 
		 		     name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
				     value="<s:property value="task.id"/>"/>	
						   	
      		</s:iterator>
      </td>	                  
			<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
			<td width="30%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
			<td width="6%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      		<td width="6%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
      		<td width="6%" style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
      		<td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
      		<td width="6%" style="padding-left:3px;"><s:property value="totalNoOfParts" />
      		<input type="hidden" id="part_#taskIterator.index_ship" size="3" name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].cannotShip"  
      				value="0"/>
      		<input type="hidden"  size="3" name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].toBeShipped"  
      				value="<s:property value="toBeShipped"/>"/>
      				</td>
      		<td width="6%" style="padding-left:3px;"><input type="text" id="part_#taskIterator.index_ship" size="3" name="selectedPartReplacedToAdd[<s:property value="%{#partsCounter}"/>].ship"  
      				value="<s:property value="ship"/>"/></td>
			<td width="10%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
			<s:if test="oemPartReplaced.activePartReturn.isPartOverDue">
	        	<td width="10%" style="padding-left:3px; text-align: center;"> - </td>      
	        	<td width="10%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>
      		</s:if>
		    <s:else>
		    	<td width="10%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>      
		    	<td width="10%" style="padding-left:3px; text-align: center;"> - </td>      
		    </s:else>
	    </tr>
	    <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
	</s:iterator>
</table>	