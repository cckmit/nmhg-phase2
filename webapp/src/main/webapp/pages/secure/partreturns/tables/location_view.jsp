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
<s:iterator value="partReplacedBeans" status="partIterator">
   <thead>
   <tr class="row_head">
    <th width="2%" valign="middle" align="center">
      <input id="selectAll_<s:property value="claim.id" />" type="checkbox"
         	<s:if test="selected"> checked="checked" </s:if>
         	value="checkbox" style="border:none"/>
      <script>
          var multiCheckBox = dojo.byId("selectAll_<s:property value="claim.id" />");
          var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox);

          // this var is defined in parent jsp.
          __masterCheckBoxControls.push(multiCheckBoxControl);
      </script>
	</th>
	<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /> </th>
    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
      <!-- CR NMHGSLMS-172 starts -->
      <s:if test="oemPartReplaced.activePartReturn.rmaNumber!=null">
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:if>
      <s:elseif test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
        <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:elseif>
         <!-- CR NMHGSLMS-172 end -->
    <s:if test="WPRA">
        <th width="6%%" valign="middle" class="colHeader"><s:text name="label.partReturn.wpra" /></th>
    </s:if>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.common.returnDirectlyToSupplier" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
   	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.ship" /></th>
    <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
    <s:if test="isDuePartsTask == true">
      <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.duedays" /></th>
    </s:if>
    <s:else>
      <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.overdueDays" /></th>
    </s:else>
    <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.returnlocation" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
    </tr>
    </thead>

    <tr class="
     	 <s:if test="oemPartReplaced.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
     	 <s:else>tableDataWhiteText</s:else>
     	 ">
      <td width="2%" valign="middle" align="center">

      <s:checkbox
 		     name="partReplacedBeans[%{#partsCounter}].selected"
 		     value="selected"
 		     id="%{#claimIterator.index}_%{#partIterator.index}"/>
 	<s:iterator value="partReturnTasks" status ="taskIterator">
  	  <input type="hidden"
 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task"
		     value="<s:property value="task.id"/>"
				/>

    </s:iterator>
         <script>
            var selectElementId = "<s:property value="%{#claimIterator.index}" />_<s:property value="%{#partIterator.index}" />";
            multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
          </script>
      </td>
      <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReplacedId"
      				value="<s:property value="oemPartReplaced.id"/>"/>
      <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.brandItem.itemNumber"/>  </td>
	  <td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
   <!-- CR NMHGSLMS-172  start-->
	  
      <s:if test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
       	 <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.partReturnConfiguration.rmaNumber" /></td>
	   </s:if>
	  <s:elseif test = "oemPartReplaced.activePartReturn.rmaNumber!=null">
	 	 <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.rmaNumber" /></td>
	  </s:elseif>
	  
	   <!-- CR NMHGSLMS-172 end  -->
	  <s:if test="WPRA">
	     <td width="6%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.wpra.wpraNumber" /></td>
	  </s:if>
	  <td width="6%" style="padding-left:3px;">
               <s:if test="oemPartReplaced.returnDirectlyToSupplier">
                    <s:text name="label.common.yes" />
               </s:if>
               <s:else>
                     <s:text name="label.common.no" />
               </s:else>
            </td>
      <td width="6%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
      <td width="6%" style="padding-left:3px;"><s:property value="totalNoOfParts" /></td>
      <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].toBeShipped"
      				value="<s:property value="toBeShipped"/>"/>
      <td width="7%" style="padding-left:3px;"><input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].ship"
      				value="<s:property value="ship"/>"/></td>
      <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
      <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>
      <td width="14%" style="padding-left:3px;">      	
      	 <s:if test="isLoggedInUserACanadianDealer()">
	     	<s:property value="getCentralLogisticLocation()"/>
	      </s:if>
	      <s:else>
	     	<s:property value="oemPartReplaced.activePartReturn.returnLocation.code" />
	      </s:else>
      </td>
      <td width="7%" style="padding-left:3px;"><input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].cannotShip"
      				value="<s:property value="cannotShip"/>"/></td>
       <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
    </tr>
     <tr>
		<td colspan="15">
			<div class="separator" />
			<div class="detailsHeader">
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
		</td>
	</tr>
  </s:iterator>
</table>