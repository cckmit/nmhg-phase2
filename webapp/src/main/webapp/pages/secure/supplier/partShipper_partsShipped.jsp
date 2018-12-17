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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html xmlns="http://www.w3.org/1999/xhtml" class="rule">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        function popupShipmentTag() {
        <s:if test="shipment != null">
            var contact_person=document.getElementById("partShipperUpdateTag_submit_shipment_contactPersonName").value;
            var contact_person=document.getElementById("contact_person_0").value;
            window.open('partShipperUpdateTag_shipmentTag_shipped.action?shipment=<s:property value="%{shipment.id}"/>&taskName=<s:property value="taskName"/>
            		+'&shipment.contactPersonName='+contact_person,
                    'ShipmentTag',
                    'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=680,height=400,top=115,left=230');
        </s:if>
        <s:elseif test="shipments !=null">
             var top = "115";var left="230";
                <s:iterator value="shipments" status="shipsta">
                var shipment_count='<s:property value="%{#shipsta.index}"/>';
                var contact_person=document.getElementById("contact_person_"+shipment_count).value;
                   window.open('partShipperUpdateTag_shipmentTag_shipped.action?shipment=<s:property value="%{id}"/>&shipment.contactPersonName='+contact_person,
                        'ShipmentTag<s:property value="%{#shipsta.index}"/>',
                        'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=680,height=400,top='+top+',left='+left);
                   top = "185"; left="320";
                </s:iterator>
        </s:elseif>
        }
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
</head>
<u:body>
 <u:actionResults />
 <s:form action="update_contact_person" theme="twms"  method="post" id="baseFormId">
  <s:iterator value="shipments" status="shipmentStatus">
     <s:if test="detailView">
      <table width="100%"  cellspacing="0" cellpadding="0" class="grid">
        <tr>
          <td class="label" width="15%">
				Return Location:	
		 </td>
		  <td class="labelNormal" width="35%">
				<s:property value="destination.code"></s:property>
		 </td>
		 <td class="label" width="10%">
			<s:text name="columnTitle.duePartsReceipt.carrier"></s:text>:
		</td>
		<td class="labelNormal" >
		  <a href="<s:property value="carrier.url" escape="false"/>" target="_blank">
	      	<s:property value="carrier.name" />
		  </a>
		</td>
        </tr>
        <tr>
		<td width="15%" nowrap="nowrap" class="label"><s:text name="label.contactPersonName" /></td>
		<td width="35%"><s:textfield id='contact_person_%{#shipmentStatus.index}' name="shipments[%{#shipmentStatus.index}].contactPersonName"  value="%{contactPersonName}"/></td>
		<s:hidden name="shipments[%{#shipmentStatus.index}]"  value="%{shipments[#shipmentStatus.index].id}"/>
			
		</tr>
      </table> 
     </s:if>
     <div class="policy_section_div">
      <table width="98%" border="0" cellspacing="1" cellpadding="1" align="center"  style="margin:5px; background-color:#b6c2cf">
        <tr>
          <th width="15%" valign="middle" nowrap="nowrap" class="warColHeader">Supplier Part Number</th>					
		  <th width="15%" valign="middle" nowrap="nowrap"  class="warColHeader">Part Number</th>
		  <th width="30%" valign="middle" nowrap="nowrap"  class="warColHeader">Description</th>
		  <th valign="middle" nowrap="nowrap"  class="warColHeader"><s:text name="columnTitle.common.quantity"></s:text></th>
		  <th valign="middle" nowrap="nowrap"  class="warColHeader"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></th>
	   </tr>
	   <s:iterator value="returnWithUniquePart()" status="partReturnStatus" id="supplierPartReturn">
	    <tr>
			<td width="15%" nowrap="nowrap" class="warColDataBg">
				<s:property value="recoverablePart.supplierItem.number" />
			</td>
			<td width="15%" valign="middle" nowrap="nowrap" class="warColDataBg">
				<s:property value="recoverablePart.oemPart.itemReference.unserializedItem.number" />
			</td>
			<td width="30%" valign="middle" nowrap="nowrap" class="warColDataBg">
				<s:property value="recoverablePart.oemPart.itemReference.unserializedItem.description"/>
			</td>
			
			<td valign="middle" nowrap="nowrap" class="warColDataBg">
					<s:property value="recoverablePart.receivedFromSupplier" />
			</td>
			<td valign="middle" nowrap="nowrap" class="warColDataBg">
				<s:property value="rgaNumber" />
			</td>
			</tr>
	   </s:iterator>
      </table>
     </div>
  </s:iterator>
     	<div id="submit" align="center" class="spacingAtTop"><input
			id="submit_btn" class="buttonGeneric" type="submit"
			value="<s:text name='button.common.update'/>" /><input
			id="cancel_btn" class="buttonGeneric" type="button"
			value="<s:text name='button.common.cancel'/>"
			onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
		</div>	
</s:form>
 <div class="buttonWrapperPrimary" align="center" id="buttonsDiv">
			<button class="buttonGeneric" onClick="javaScript:popupShipmentTag()">Print Shipment Tag</button>
</div>
</u:body>
<authz:ifPermitted resource="partsRecoverySupplierPartsShippedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	        document.getElementById("buttonsDiv").style.display="none";
	    });
	</script>
</authz:ifPermitted>
</html>