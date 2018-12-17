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
	<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.dealerRequestedParts" /></th>
   	<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.ship" /></th>
    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
    <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.common.dealerName" /></th>
    </tr>
    </thead>
  <s:iterator value="partReplacedBeans" status="partIterator">
    <tr class="tableDataWhiteText">
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
      <td width="12%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
	  <td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
      <td width="12%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
      <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].toBeShipped" value="<s:property value="toBeShipped"/>"/>
      <td width="7%" style="padding-left:3px;"><input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].ship"
      				value="<s:property value="ship"/>"/></td>
      <td width="7%" style="padding-left:3px;"><input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].cannotShip"
      				value="<s:property value="cannotShip"/>"/></td>
      <td width="12%" style="padding-left:3px;"><s:property value="claim.forDealer.name" /></td>
       <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
    </tr>
  </s:iterator>
  <tr><td colspan="13">
	 <div class="detailsHeader">
	    <s:text name="label.partReturnConfiguration.dealerComments"/>
     </div>
	 <div class="inspectionResult" style="width: 99%"  align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
	 	<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
			<s:property value="partReplacedBeans.get(0).oemPartReplaced.getLatestAuditComments()"/>
		</div>
	 </div>
  </td></tr>
</table>

