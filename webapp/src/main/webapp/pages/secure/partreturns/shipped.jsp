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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


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
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
</script>
<u:body>
    <s:form>
    <u:actionResults/>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
        <div class="separatorTop" />
            <s:set name="claimCounter" value="0"/>
            <s:iterator value="claimWithPartBeans" status="claimIterator">
                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" >
                <%@include file="tables/claim_details.jsp"%>
                <div class="mainTitle">
                <s:text name="title.partReturnConfiguration.partDetails"/></div>
                
                <%@include file="tables/parts_shipped_view.jsp"%>
                </div>
                </div>
            </s:iterator>
    </div>
	    <div class="separatorTop" />
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.shipmentInfo"/>" labelNodeClass="section_header" >
		  <table cellspacing="0" cellpadding="0" >
		   <tr>
                 <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="columnTitle.duePartsReceipt.shipment_no"/>:</td>
                      <td width="25%" class="carrierLabelNormal">
                        <s:property value="shipmentFromPartBeans.id" />
                        <s:hidden name="id" value="%{shipmentFromPartBeans.id}" />
                  </td>
                  <s:if test="!taskName.equals('Claimed Parts Receipt')">
                      <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.trackingNumber"/>: </td>
                      <td  class="carrierLabelNormal">
                        <s:property value="shipmentFromPartBeans.trackingId" />
                      </td>
                  </s:if>
			</tr>
			<s:if test="!buConfigAMER">
			 <tr>
           <th class="carrierLabel labelStyle" width="40%"><s:text name="label.common.shipmentDimensions"/>:</th>
              <s:iterator value="shipmentFromPartBeans.shipmentLoadDimension" status="iterator">
                  <td>
                 <table class="tableBorder"><tr>
                             <td style="border:1px solid"><s:text name="label.partReturnConfiguration.loadType"/></td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.length" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.width" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.height" />(m)</td>
                            <td style="border:1px solid"><s:text name="label.partShipmentTag.shipment.weight" />(kg)</td>
                        </tr><tr>
                            <td style="border:1px solid"> <s:property value="loadType" /> </td>
                            <td style="border:1px solid"> <s:property value="length" /> </td>
                            <td style="border:1px solid"> <s:property value="breadth" /> </td>
                            <td style="border:1px solid"> <s:property value="height" /> </td>
                            <td style="border:1px solid"> <s:property value="weight" /> </td>
                          </tr></table></td>
                </s:iterator>               
             </tr>
             </s:if>
			<tr>
                  <td  class="carrierLabel labelStyle" nowrap="nowrap">
                  <s:if test="taskName.equals('Claimed Parts Receipt')">
                       <s:text name="label.partReturnConfiguration.availableDate"/>
                  </s:if>
                  <s:else>
                      <s:text name="label.partReturnConfiguration.shipmentDate"/>
                  </s:else>:</td>
                  <td  class="carrierLabelNormal">
                      <s:property value="shipmentFromPartBeans.shipmentDate" />
                  </td>
                  <s:if test="!taskName.equals('Claimed Parts Receipt')">
                      <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier"/>:</td>
                      <td  class="carrierLabelNormal">
                          <a href="<s:property value="shipmentFromPartBeans.carrier.url" escape="false"/>" target="_blank">
			      			<s:property value="shipmentFromPartBeans.carrier.name" /> 
					      </a>
                      </td>
                  </s:if>
			 </tr>
			<tr>
			      <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.shipper.comments"/>:</td>
                  <td  class="carrierLabelNormal">
                    <s:property value="shipmentFromPartBeans.comments" />
                  </td>
			</tr>
            <s:if test="taskName.equals('Claimed Parts Receipt')">
			    <tr>
                    <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>:</td>
                    <td  class="carrierLabelNormal"><t:textarea name="comments" cols="40"/></td>
                </tr>
            </s:if>
		  </table>
		  <s:if test="taskName.equals('Claimed Parts Receipt')">
		    <div class="buttonWrapperPrimary">
               <s:submit name="action:claimedPartReceipt_submit" value="%{getText('label.button.part.received')}" cssClass="buttonGeneric"/>
            </div>
          </s:if>
		</div>
		<div class="separator" />				
	</div>
  </s:form>
</u:body>
</html>