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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title>
<s:text name="title.common.warranty"/>
</title>
<s:head theme="twms"/>

<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
	dojo.require("dojox.layout.ContentPane");
</script>

<u:body>
  <s:form id="baseForm" name="baseForm">
    <s:hidden id="identifier" name="id"/>
    <u:actionResults/>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
      <div class="separatorTop" />
      <s:set name="partsCounter" value="0"/>

      <s:iterator value="claimWithPartBeans" status="claimIterator">
      <s:if test="!hasOnlyCevaRole()">
      <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>
      (
      <s:property value="claim.claimNumber"/>
      )" labelNodeClass="section_header" >

      </s:if>
      <s:else>
         <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partShipmentTag.dealerinfo"/>"
                labelNodeClass="section_header" >

      </s:else>
       <s:if test="!hasOnlyCevaRole()">
          <div style="width:98%">
            <%@include file="tables/claim_details.jsp"%>
          </div>
        </s:if>
      <s:iterator value="partReplacedBeans" status="partIterator">
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
        		     value="<s:property value="oemPartReplaced.id"/>"/>
              <input type="hidden"
                     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].cevaTracking"
                     value="<s:property value="cevaTracking" />"/>
              <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
      </s:iterator>
       <table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%">
             <thead>
                 <tr class="row_head">
                     <authz:ifUserInRole roles="processor">
                         <th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
                         <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
                         <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeship" /></th>
                         <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
                         <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.jbpm.task.ceva.tracking" /></th>
                         <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
                  <!-- CR NMHGSLMS-172 start -->
                  <s:if test=" partReplacedBeans[0]!=null">
                   	    <s:if test="claim.isProcessedAutomatically() && partReplacedBeans[0].partReturnTasks[0].partReturn.rmaNumber != null">
       						 <th  width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
     					 </s:if>
     					<s:elseif test="partReplacedBeans[0].partReturnTasks[0].partReturn.rmaNumber != null">
         					 <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.RmaNo" /></th>
      					</s:elseif>
      				</s:if>
    			 <!-- CR NMHGSLMS-172  -->
                         <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.common.wpra" /></th>
                      </authz:ifUserInRole>

                     <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.common.dealerName" /></th>
                     <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="label.claim.dealerPickupLocation" /></th>
                     <th width="12%" valign="middle" class="colHeader" align="right"><s:text name="columnTitle.dueParts.return_location" /></th>

                 </tr>
               </thead>
             <s:iterator value="partReplacedBeans" status="partIterator">
             <tr class="tableDataWhiteText">
             <authz:ifUserInRole roles="processor">
                  <td width="12%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
              	  <td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
                  <td width="12%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
                  <td width="12%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
                  <td width="12%" style="padding-left:3px;"><s:property value="cevaTracking" /></td>
                  <td width="12%" style="padding-left:3px;"><s:property value="shipped" /></td>
             <!-- CR NMHGSLMS-172  start-->
	 	     <s:if test="claim.isProcessedAutomatically() && partReturnTasks[0].partReturn.rmaNumber != null">
	  			 <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.rmaNumber" /></td>
	 		 </s:if>
			 <s:elseif test="partReturnTasks[0].partReturn.rmaNumber !=null">
	 				 <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.rmaNumber" /></td>
	 		 </s:elseif>
	 		  <!-- CR NMHGSLMS-172  end-->
                  
                  <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.wpra.wpraNumber" /></td>
             </authz:ifUserInRole>
                  <td width="12%" style="padding-left:3px;"><s:property value="claim.forDealer.name" /></td>
                  <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.dealerPickupLocation.location" /></td>
                  <td width="12%" style="padding-left:3px;"><s:property value="partReturnTasks[0].partReturn.returnLocation.code" /></td>
             </s:iterator>
          </table>
    </div>
    </div>
    <div class="separator" ></div>

    </s:iterator>
    </div>
    <div width ="100%">

      <div dojoType="dojox.layout.ContentPane" label="Parts List"
         style="overflow-X: hidden; overflow-Y: auto; width:100%;" id="partsToBeAddedDiv"> </div>
      <div class="detailsHeader" style="width:99%; margin-left:5px">
        <s:text name="title.partReturnConfiguration.shipmentInfo"/>
      </div>
      <div class="inspectionResult" style="width:99%">
        <table border="0" cellspacing="0" class="grid" style="width:99%" >

        <tr>
            <td class="carrierLabel labelStyle" nowrap="nowrap"> <s:text name="columnTitle.common.wpra"/> :</td>
            <td class="carrierLabelNormal"> <s:property value="wpra.wpraNumber" /> </td>

            <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.shipmentDate"/>:</td>
            <td  class="carrierLabelNormal"><sd:datetimepicker name='shipmentCalenderDate' value='%{shipmentDate}' id='partShipmentDate'/>

                <select name="hour">
                    <option selected="selected" value="-1">HH</option>
                    <s:iterator var="i" begin="00" end="23" step="1">
                        <option <s:if test="#i == hour"> selected </s:if>> <s:property value="#i"/></option>
                    </s:iterator>
                </select>
                <select name="minute">
                    <option selected="selected" value="-1">MM</option>
                    <s:iterator var="i" begin="00" end="59" step="15">
                        <option <s:if test="#i == minute"> selected </s:if>> <s:if test="#i ==0"> 00 </s:if><s:else><s:property value="#i"/></s:else></option>
                    </s:iterator>
                </select>
            </td>
        </tr>

        <tr>
            <td   class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.trackingNumber"/>
              :</td>
            <td class="carrierLabelNormal"><s:textfield name="trackingNumber" cssStyle="margin-left:5px;"/>
            </td>
            <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier"/>
              :</td>
            <td  class="widgetTopAlign"><s:select name="carrierId" list="shipperCompanies" listKey="id"
                    listValue="name" headerKey="" headerValue='%{getText("label.partreturns.selectCarrierheader")}'
                value = "carrierId.toString()" />
            </td>
        </tr>

        <tr>
           <th class="warColHeader" width="40%"><s:text name="label.partReturnConfiguration.loadType"/></th>
           <th class="warColHeader" width="40%"><s:text name="label.common.shipmentDimensions"/></th>
           <th class="warColHeader" width="20%"><s:text name="columnTitle.shipmentGenerated.shipment_no"/></th>

        </tr>

           <s:iterator value="shipments" status="itr">
              <s:iterator value="shipmentLoadDimension" status="iterator">
                  <tr><td> <s:property value="loadType" /> </td>
                  <td>
                 <table class="tableBorder"><tr>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.length" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.width" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.height" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.weight" />(kg)</td>
                        </tr><tr>
                            <td style="border:1px solid"> <s:property value="length" /> </td>
                            <td style="border:1px solid"> <s:property value="breadth" /> </td>
                            <td style="border:1px solid"> <s:property value="height" /> </td>
                            <td style="border:1px solid"> <s:property value="weight" /> </td>  </tr></table>
                  <td> <s:property value="shipment.id" />  </td>  </tr>
              </s:iterator>
           </s:iterator>


        <tr>
             <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>:</td>
             <td  class="carrierLabelNormal"><t:textarea name="cevaComments" cols="40"/></td>
          </tr>
        </table>
      </div>
      <div class="separator"></div>
      <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
      <div class="buttonWrapperPrimary">
      <s:submit name="action:cevaTracking_submit" value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
      <authz:ifUserInRole roles="processor">
          <s:submit name="action:cevaTracking_cancel" value="%{getText('button.partReturnConfiguration.cancelReturn')}" cssClass="buttonGeneric"/>
      </authz:ifUserInRole>
      </div>
      </authz:ifNotPermitted>
    </div>
  </s:form>
</u:body>
</html>
