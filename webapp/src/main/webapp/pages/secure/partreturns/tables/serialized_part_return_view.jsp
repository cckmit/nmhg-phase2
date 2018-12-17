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

<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%">			
   <thead>
	   <tr>
		    <th width="2%" valign="middle" align="center" class="colHeader">
		    <div align="center">
		     <s:if test="!isTaskPartsShipped()">
		      <input id="selectAll_<s:property value="claim.id" />" type="checkbox" 		          		 
		         	<s:if test="selected"> checked="checked" </s:if> 
		         	value="checkbox" style="border:none"/>
			</s:if>
		      <script type="text/javascript">		      
		          var multiCheckBox = dojo.byId("selectAll_<s:property value="claim.id" />");		         
                  if(multiCheckBox){
		          var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox); 
		          // this var is defined in parent jsp.
		          __masterCheckBoxControls.push(multiCheckBoxControl);		        
                  } 
		      </script>
		      </div>
			</th>
			<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.part.serialNumber" /></th>
			<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
		    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
		    <s:if test="!isTaskShipmentGenerated() && !isTaskPartsShipped()">
		     <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.shipmentStatus" /></th>
			</s:if>
		    <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
		    <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.returnlocation" /></th>
	    </tr>
    </thead>   
	<s:iterator value="partReplacedBeans" status="partIterator"> 
 		<s:iterator value="partReturnTasks" status ="taskIterator">
 			<tr class="
		     	 <s:if test="oemPartReplaced.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
		     	 <s:else>tableDataWhiteText</s:else>
	     		 ">
				<td>
				<input type="hidden"
			 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
					     value="<s:property value="task.id"/>"
							/>
				 <s:if test="!isTaskPartsShipped()">
					<s:checkbox 
						name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].selected"
		 		     value="!selected" 
		 		     id="%{#claimIterator.index}_%{#partIterator.index}_%{#taskIterator.index}"/>
		 		  </s:if>
				</td>
			  	<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.referredInventoryItem.serialNumber"/>  </td> 
			  	<td width="12%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
			  	<td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
				<s:if test="!isTaskShipmentGenerated() && !isTaskPartsShipped()">
					<td><s:select name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].shipmentStatus" list="shipmentStatusList"
				                  id="shipmentStatus_%{#claimIterator.index}_%{#partIterator.index}_%{#taskIterator.index}" required="true" 
				                  listValue="value" listKey="key" value="%{shipmentStatus.name()}"
				                  cssStyle="width: 130px" />
			   
			  		 </td>
				</s:if>
			   	<td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
			  
	    	 	 <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.returnLocation.code"/></td>
		        <script>
		            var selectElementId = "<s:property value="%{#claimIterator.index}" />_<s:property value="%{#partIterator.index}" />_<s:property value="%{#taskIterator.index}" />";
		           try{		            
		            multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
		           }catch(e){
			           console.debug("check"+e);
		           }
	        	 </script> 
       		</tr>	
		 </s:iterator>
         <input type="hidden"
	 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].claim"
			     value="<s:property value="claim.id"/>"/>
         <input type="hidden" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReplacedId"
	      				value="<s:property value="oemPartReplaced.id"/>"/>
      <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
  </s:iterator>
</table>

