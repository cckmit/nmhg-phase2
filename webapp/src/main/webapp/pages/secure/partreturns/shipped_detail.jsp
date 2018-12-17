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

<!-- Need to extract the common parts out. -->

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<style>
.colHeader{border:1px solid #d6dee1;}
</style>

<script type="text/javascript" src="scripts/clientMachineDate.js"></script>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
	var __masterCheckBoxControls = new Array();
	 function invokePrintPopup(shipmentId){
			var win = "";
						
			var dateTime=getPrintDate();
			var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId+'&taskName=Parts Shipped&clientDate='+dateTime;
			window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
			return false;
		 }
	  function invokePrintPopupForDealer(shipmentId){
			var win = "";
			
			var dateTime=getPrintDate();
			var actionUrlString = 'partreturns_displayForPrint_for_dealer.action?shipmentIdString='+ shipmentId+'&taskName=Dealer Requested Parts Shipped&clientDate='+dateTime; 
			window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
			return false;
		 }
</script>
<u:body>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
        <s:iterator value="claimWithPartBeans" status="claimIterator">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" >
            <%@include file="tables/claim_details.jsp"%>
            <div style="margin:10px 0; padding-left:5px; color:#5577B4;font-weight:700;"> <s:text name="title.partReturnConfiguration.partDetails"/></div>
			<%@include file="tables/parts_shipped_view.jsp"%>
            </div>
        </s:iterator>
	    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.shipmentInfo"/>" labelNodeClass="section_header" >
		  <table class="grid" cellspacing="0" cellpadding="0">
		    <tr>
			 <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="columnTitle.duePartsReceipt.shipment_no"/>:</td>
				  <td class="carrierLabelNormal" width="35%">
				    <s:property value="shipmentFromPartBeans.id" />
			 </td>
			 <s:if test="!taskName.equals('Dealer Requested Parts Shipped')">
                  <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.trackingNumber"/>: </td>
                  <td width="35%" class="carrierLabelNormal">
                    <s:property value="shipmentFromPartBeans.trackingId" />
                  </td>
             </s:if>
			</tr>
			<tr>
			  <td  class="carrierLabel labelStyle" nowrap="nowrap">
			  <s:if test="taskName.equals('Dealer Requested Parts Shipped')">
			       <s:text name="label.partReturnConfiguration.availableDate"/>
			  </s:if>
			  <s:else>
			       <s:text name="label.partReturnConfiguration.shipmentDate"/>
			  </s:else>
			       :</td>
	          <td  class="carrierLabelNormal">
		          <s:if test="buConfigAMER"> 
		          	<s:property value="shipmentFromPartBeans.shipmentDateForPartsCollected" />
		          </s:if>
		          <s:else>
			      	<s:property value="shipmentFromPartBeans.shipmentDate" />
			      </s:else>
			  </td>
			  <s:if test="!taskName.equals('Dealer Requested Parts Shipped')">
			      <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier"/>:</td>
                  <td  class="carrierLabelNormal">
                      <s:if test="shipmentFromPartBeans.carrier.url != null" >
                          <a href="<s:property value="shipmentFromPartBeans.carrier.url" escape="false"/>" target="_blank">
                            <s:property value="shipmentFromPartBeans.carrier.name" />
                          </a>
                      </s:if>
                      <s:else>
                          <s:property value="shipmentFromPartBeans.carrier.name" />
                      </s:else>
                  </td>
              </s:if>
			</tr>
			<tr>
			<td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>:</td>
			  <td  class="carrierLabelNormal">
			    <s:property value="shipmentFromPartBeans.comments" />
			  </td>
			</tr>
		  </table>
		</div> 
		<div class="buttonWrapperPrimary">
		<s:if test="taskName.equals('Dealer Requested Parts Shipped')">
		       <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
        " class="buttonGeneric" onclick="invokePrintPopupForDealer(
        <s:property value="%{id}"/>
        )"/>
		</s:if>
        <s:else>
            <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
            " class="buttonGeneric" onclick="invokePrintPopup(
            <s:property value="%{id}"/>
            )"/>
         </s:else>
         </div>
		 <div class="separator" />
	</div>	
</u:body>
</html>