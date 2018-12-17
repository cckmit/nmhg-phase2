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
<s:set name="cancelAllowed" value="true" />
<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%">
  <s:iterator value="partReplacedBeans" status="partIterator">
   <thead>
   <tr class="row_head">
   <authz:ifUserInRole roles="processor">
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
	</authz:ifUserInRole>
	<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeship" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.jbpm.task.ceva.tracking" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
    <!-- CR NMHGSLMS-172 start  -->
    <s:if test="oemPartReplaced.activePartReturn.rmaNumber!=null">
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:if>
      <s:elseif test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
        <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      </s:elseif>
        <!-- CR NMHGSLMS-172 end -->
    <s:if test="WPRA">
   	    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.common.wpra" /></th>
   	</s:if>
    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.common.dealerName" /></th>
    </tr>
    </thead>

    <tr class="tableDataWhiteText">
     <authz:ifUserInRole roles="processor">
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
      </authz:ifUserInRole>
      				
      <s:hidden name="partReplacedBeans[%{#partsCounter}].partReplacedId"	value="%{oemPartReplaced.id}" />
      <td width="12%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
	  <td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
      <td width="12%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      <td width="12%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
      <td width="12%" style="padding-left:3px;"><s:property value="cevaTracking" /></td>
      <td width="12%" style="padding-left:3px;"><s:property value="shipped" /></td>
      <s:if test="toBeShipped == 0 && shipmentGenerated == 0 && cevaTracking == 0">
          <s:set name="cancelAllowed" value="false" />
      </s:if>
      <s:hidden name="partReplacedBeans[%{#partsCounter}].toBeShipped" value="%{toBeShipped}" />
      <s:hidden name="partReplacedBeans[%{#partsCounter}].shipmentGenerated" value="%{shipmentGenerated}" />
      <s:hidden name="partReplacedBeans[%{#partsCounter}].cevaTracking" value="%{cevaTracking}" />
      <!-- CR NMHGSLMS-172 start  -->
          <s:if test = "oemPartReplaced.activePartReturn.rmaNumber!=null">
            <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.rmaNumber" /></td>
          </s:if>
          <s:elseif test="isProcessedAutomatically() && oemPartReplaced.partReturnConfiguration.rmaNumber != null">
            <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.rmaNumber" /></td>
          </s:elseif>       
            <!-- CR NMHGSLMS-172 end  -->
     <s:if test="WPRA">
         <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.wpra.wpraNumber" /></td>
     </s:if>
      <td width="12%" style="padding-left:3px;"><s:property value="claim.forDealer.name" /></td>
       <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
    </tr>
  </s:iterator>
</table>