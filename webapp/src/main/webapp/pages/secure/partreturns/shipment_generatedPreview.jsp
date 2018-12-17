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

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
</script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
</head>
<u:body>
  <s:form id="baseForm" name="baseForm"> 
    <s:hidden id="identifier" name="id"/>     
    <u:actionResults/>
	<div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
	<div class="separatorTop" />				
	  <s:set name="partsCounter" value="0"/>
	  <s:iterator value="claimWithPartBeans" status="claimIterator">
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" >
        <%@include file="tables/claim_details.jsp"%>
        <div class="mainTitle">
        <s:text name="title.partReturnConfiguration.partDetails"/></div>
        
            <table  cellspacing="0" class="grid borderForTable" cellpadding="0">
              <tr class="row_head">
                <th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
                <th width="20%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
                <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
                <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
                <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
                <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
                <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
                <th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
                <th width="6%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.duedays" /></th>
                <th width="6%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.overdueDays" /></th>
                <th width="12%" valign="middle" class="colHeader"><s:text name="label.partReturn.qtyAddedToShipment" /></th>
              </tr>
              <s:iterator value="partReplacedBeans" status="partIterator">
                <tr class="tableDataWhiteText">


                  <s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null">
                      <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.oemDealerPartReplaced.number" /></td>
                  <td width="20%" style="padding-left:3px;"><s:property value="oemPartReplaced.oemDealerPartReplaced.description" /></td>
                  </s:if>
                  <s:else>
                  <td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
                  <td width="20%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
                  </s:else>

                  <td width="6%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
                  <td width="6%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
                  <td width="6%" style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
                  <td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
                  <td width="6%" style="padding-left:3px;"><s:property value="totalNoOfParts" /></td>
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
                </tr>
                 <tr>
					<td colspan="11">
						<div class="separator" />
						<div class="detailsHeader">
							<s:text name="label.partReturnDetail.shipmentComments" />
						</div>
						<div class="inspectionResult" style="width: 99%">
							<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
								<s:property value="%{oemPartReplaced.partReturnConfiguration.partReturnDefinition.shippingInstructions}"/>
							</div>
						</div>
					</td>
				</tr>
                <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
              </s:iterator>
            </table>
        </div>
        </div>
        </s:iterator>
        </div>
  </s:form>
</u:body>
</html>
	