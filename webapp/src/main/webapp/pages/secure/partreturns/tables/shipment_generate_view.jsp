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

<table  cellspacing="0" class="grid borderForTable" border="0" cellpadding="0">	
  <s:iterator value="partReplacedBeans" status="partIterator">		
  <tr class="row_head">
    <th width="2%" valign="middle" align="center">
      <input id="selectAll_<s:property value="claim.id" />" type="checkbox" 		          		 
         	checked="checked" value="checkbox" style="border:none"/>
	  <script>
			  var masterCheckBox = new CheckBoxListControl(dojo.byId("selectAll_<s:property value="claim.id" />") );																		
	</script>
	</th>
	<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
    <th width="20%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
    <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
    <!-- CR NMHGSLMS-172 start  -->
    <s:if test="oemPartReplaced.activePartReturn.rmaNumber!=null">
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:if>
      <s:elseif test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
        <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:elseif>
        <!-- CR NMHGSLMS-172 end -->
    </s:if>
    <s:if test="WPRA">
        <th width="6%%" valign="middle" class="colHeader"><s:text name="label.partReturn.wpra" /></th>
    </s:if>
    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="label.common.returnDirectlyToSupplier" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
	<s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
        <th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
        <th width="6%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.duedays" /></th>
        <th width="6%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.overdueDays" /></th>
        <th width="12%" valign="middle" class="colHeader"><s:text name="label.partReturn.qtyAddedToShipment" /></th>
    </s:if>
  </tr>

    <tr class="tableDataWhiteText">
      <td width="2%" valign="middle" align="center">
        <s:checkbox  
 		     name="partReplacedBeans[%{#partsCounter}].selected"
 		     value="selected" 
 		     id="%{#claimIterator.index}_%{#partIterator.index}"/>
        <script>			
			    var selectElementId = "<s:property value="%{#claimIterator.index}" />_<s:property value="%{#partIterator.index}" />";				
			    var selectElement =	dojo.byId(selectElementId);
				masterCheckBox.addListElement(selectElement);
			  </script>			          		 
      </td>
       <s:iterator value="partReturnTasks" status ="taskIterator">
      
  	  <input type="hidden" 
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
		     value="<s:property value="task.id"/>"/>	
	  </s:iterator>
      <input type="hidden" 
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].claim" 
		     value="<s:property value="claim.id"/>"/>
      <input type="hidden"
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].oemPartReplaced"
		     value="<s:property value="partReplacedBeans[#partsCounter].oemPartReplaced.id"/>"/> 
      <s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null">
      <s:if test="isLoggedInUserADealer()">
	  	<td width="12%" style="padding-left:3px;"><s:property value="%{oemPartReplaced.brandItem.itemNumber}" /></td>
	  	</s:if>
	  	<s:else>
	  	<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.oemDealerPartReplaced.number" /></td>
	  	</s:else>
      <td width="20%" style="padding-left:3px;"><s:property value="oemPartReplaced.oemDealerPartReplaced.description" /></td>
	  </s:if>
	  <s:else>
	  <td width="12%" style="padding-left:3px;"><s:property value="%{oemPartReplaced.brandItem.itemNumber}" /></td>
      <td width="20%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
	  </s:else>
      <%-- <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.partReturn.rmaNumber" /></td> --%>
      
      <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">   
         <!-- CR NMHGSLMS-172 start  -->
          <s:if test = "oemPartReplaced.activePartReturn.rmaNumber!=null">
             <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.rmaNumber" /></td>
          </s:if>
          <s:elseif test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
               <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.partReturnConfiguration.rmaNumber" /></td>
          </s:elseif>
          <!-- CR NMHGSLMS-172 end  -->
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
      <td width="6%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
        <input type="hidden"
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].shipmentGenerated"
		     value="<s:property value="shipmentGenerated" />"/>
      <td width="6%" style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
        <input type="hidden"
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].cannotBeShipped"
		     value="<s:property value="cannotBeShipped" />"/>
      <td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="totalNoOfParts" /></td>
      <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
          <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
          <s:if test="partReturn.partOverDue">
            <td width="12%" style="padding-left:3px; text-align: center;"> - </td>
            <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>
          </s:if>
          <s:else>
            <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>
            <td width="12%" style="padding-left:3px; text-align: center;"> - </td>
          </s:else>
          <td><s:property value="qtyForShipment"/></td>
      </s:if>
    </tr>
    <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
     <tr class="tableDataWhiteText">
		<td colspan="14">
			<!-- <div class="separator" /> -->
			<div class="detailsHeader" style="width: 99%">
				<s:text name="label.partReturnDetail.shipmentComments" />
			</div>
			<div class="inspectionResult" style="width: 99%">
				<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
					<s:property value="%{oemPartReplaced.partReturnConfiguration.partReturnDefinition.shippingInstructions}"/>
				</div>
			</div>
			<div class="inspectionResult" style="width: 99%">
				<div class="mainTitleNoborder" style="margin-top:10px;padding-bottom:10px;">
					<s:text name="label.recClaim.shippingComments"/>:
				</div>
				<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
					<s:property value="%{shippingCommentsInRecClaim(claim, oemPartReplaced)}" />
				</div>
			</div>
			<div class="inspectionResult" style="width: 99%">
				<div class="mainTitleNoborder" style="margin-top:10px;padding-bottom:10px;">
					<s:text name="label.claim.shippingComments"/>:
				</div>
				<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
					<s:property value="%{shippingCommentsInClaim(claim)}" />
				</div>
			</div>
			<div class="inspectionResult" style="width: 99%">
				<div class="mainTitleNoborder" style="margin-top:10px;padding-bottom:10px;">
					<s:text name="label.claim.shippingCommentsBySupplier"/>:
				</div>
				<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">													
							<s:property value="supplierComments"/> <br/>																															
				</div>
			</div>
		</td>
	</tr>
	</s:if>
    <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
  </s:iterator>
</table>	