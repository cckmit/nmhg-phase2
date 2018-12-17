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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>

    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>

    <script>

    	dojo.require("dijit.layout.LayoutContainer");
    	dojo.require("dijit.layout.ContentPane");
        function popupShipmentTag() {
            var top = "115";var left="230";
            <s:iterator value="shipments" status="shipsta">
               var shipment_count='<s:property value="%{#shipsta.index}"/>';
               var contact_person=document.getElementById("contact_person_"+shipment_count).value;
               window.open('partShipperUpdateTag_shipmentTag.action?shipment=<s:property value="%{id}"/>&shipment.destination='+document.getElementsByName("shipments["+shipment_count+"].destination")[0].value
                       +'&shipment.contactPersonName='+contact_person,
                    'ShipmentTag<s:property value="%{#shipsta.index}"/>',
                    'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=680,height=400,top='+top+',left='+left);
               top = "185"; left="320";
            </s:iterator>
        }

        function updateShipmentInfo() {
            var frm = document.getElementById('partShipperUpdateTag_reclaimView_submit');
            frm.action = 'partShipperUpdateTag_reclaimView_submit.action';
            frm.transitionTaken.value = '<s:property value="%{@tavant.twms.jbpm.WorkflowConstants@UPDATE}"/>';
            frm.target = '_self';
            frm.submit();
        }

      /*  function addNewPart() {
            var params = {
                id: "<s:property value="shipment.supplier.id"/>",
                shipment: "<s:property value="%{id}"/>"
            }

            twms.ajax.fireHtmlRequest("partShipperUpdateTag_addNewPart.action", params,
                    function(data) {
                    var parentDiv = dojo.byId("divForAdditionalParts");
                    parentDiv.innerHTML = data;
                    delete data,parentDiv;
                });
            } */

      /*  function removePart() {
            var frm = document.getElementById('partShipperUpdateTag_submit');
            frm.transitionTaken.value = '<s:property value="%{@tavant.twms.jbpm.WorkflowConstants@REMOVE}"/>';
            frm.action = 'partShipperUpdateTag_removePart.action';
            frm.target = '_self';
            frm.submit();
        } */

        function updateMasterView(id) {
            manageRowHide("supplyRecoveryInboxTable", '<s:property value="id"/>');
        }

        <s:if test='shipment.supplierParts.empty'>
        dojo.addOnLoad(function() {
            updateMasterView('<s:property value="%{shipment.id}"/>');
        });
        </s:if>

       /* function addPartsForPartShipper() {
            var frm = document.getElementById('partShipperUpdateTag_submit');
            frm.transitionTaken.value = "<s:property value='%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}'/>";
            frm.action = "partShipperUpdateTag_addNewPartsToShipment.action";
            frm.submit();
        }  */

    </script>
</head>

<u:body>
<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults/>
	<s:if test="hasActionMessages()">
		<script type="text/javascript">
			dojo.addOnLoad(function() {
	             var summaryTableId = getFrameAttribute("TST_ID");
	             if (summaryTableId) {
	                 manageTableRefresh(summaryTableId, true);
	             }
	         });
		</script>
	</s:if>
	<s:form action="partShipperUpdateTag_reclaimView_submit" theme="twms"
		validate="true" method="post" >

		<jsp:include flush="true" page="partsgroupedbyclaimforshipment.jsp" />

		<%--<div class="buttonWrapperPrimary">
			<input type="button" value='<s:text name="label.removePartFromThisTag" />'
				class="buttonGeneric" onClick="javascript:removePart();" />
			<input type="button" class="buttonGeneric" onClick="addNewPart()"
				value='<s:text name="label.addNewPartToThisTag" />' />
		</div>  --%>

		<div id="divForAdditionalParts"></div>
<s:iterator value="shipments" status="shipstats" >
     <div class="policy_section_div">

     <s:hidden name="shipments[%{#shipstats.index}]" value="%{id}" />
     <s:hidden name="shipments[%{#shipstats.index}].shipment" value="%{id}" />
     <s:hidden name="shipments[%{#shipstats.index}].transientId" value="%{id}" />
     <s:hidden name="inboxViewId" value="%{inboxViewId}"/>
     <div class="section_header">Shipment Information (<s:property value="id" />)</div>

		<table width="100%"  cellspacing="0" cellpadding="0" class="grid">
            <tr>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.partShipperPartsShipped.vpraNumber" />:</td>
                <td width="35%"><s:property value="id" /></td>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.shipmentGenerated.return_location" />:</td>
                <td width="35%">
                     <script type="text/javascript">
                         dojo.addOnLoad(function() {
                               var index_count='<s:property value="%{#shipstats.index}"/>';
                               dojo.subscribe("/supplier/shipment/destination/changed/"+index_count, function(data){
                                     var loc = document.getElementsByName("shipments["+index_count+"].destination")[0].value;
                                     loadAddress(loc,index_count);
                               });
                       });

                       function loadAddress(destination,index){
                           twms.ajax.fireHtmlRequest("displayShippingAddress.action?", {location:destination}, function(data) {
                                dojo.byId('showaddresshere_'+index+"").innerHTML = data;
                           });
                       }

                     </script>
                     <sd:autocompleter name='shipments[%{#shipstats.index}].destination' keyName='shipments[%{#shipstats.index}].destination' href='list_shipmentLocations.action'
                                          loadMinimumCount='0' keyValue='%{destination.id}' value='%{destination.code}'
                                          listenTopics='/supplier/shipment/destination/%{#shipstats.index}'
                                          notifyTopics="/supplier/shipment/destination/changed/%{#shipstats.index}"/>
                     <script type="text/javascript">
                          dojo.addOnLoad(function() {
                              var isOnLoad=true;
                              var inIndex='<s:property value="%{#shipstats.index}"/>';
                              var url= "list_supplierLocations.action";
                              var supplierId = '<s:property value="#recClaim.contract.supplier.id"/>';
                              dojo.publish("/supplier/shipment/destination/"+inIndex, [{
                                          url: url,
                                          params: {
                                              "supplier": supplierId
                                          },
                                          makeLocal: true
                                      }]);

                          });
                     </script>
                </td>
            </tr>
            <tr>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="label.manageWarehouse.contactPersonName" />:</td>
                <td width="35%"><s:textfield id='contact_person_%{#shipstats.index}' name="shipments[%{#shipstats.index}].contactPersonName"  /></td>
            </tr>
            <tr>
               <td colSpan="4">
                   <b><s:text name="label.supplier.shipping.address"/>:</b>
                   <div id="showaddresshere_<s:property value='%{#shipstats.index}'/>">
                   </div>
               </td>
            </tr>
			<tr>
				<td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.duePartsReceipt.tracking_no" />:</td>
				<td width="35%"><s:textfield name="shipments[%{#shipstats.index}].trackingId" /></td>
				<td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.duePartsReceipt.shipment_date" />:</td>
				<td width="35%"><sd:datetimepicker name='shipments[%{#shipstats.index}].shipmentDate' displayFormat="dd-MM-yyyy"
				 value='%{shipmentsDate[%{#shipstats.index}]}' id='partShipperShipmentDate%{#shipstats.index}' /></td>
			</tr>
			<tr>
				<td width="15%" nowrap="nowrap" class="label" valign="top">Carrier:</td>
				<td width="35%" valign="top"><s:select
					id="carrier%{#shipstats.index}" name="shipments[%{#shipstats.index}].carrier" value="%{carrier.id.toString()}"
					list="carriers" listKey="id" listValue="nameAndCode"
					/></td>
				<td width="15%" class="label" nowrap="nowrap" valign="top"><s:text
							name="label.common.dimensionsComments" />:</td>
				<td nowrap="nowrap"><t:textarea name="shipments[%{#shipstats.index}].comments"
					cols="35" rows="3" /></td>
			</tr>
		</table>
		</div>

</s:iterator>

<s:hidden name="transitionTaken"
			value='%{@tavant.twms.jbpm.WorkflowConstants@UPDATE}' />
</s:form>
<div class="buttonWrapperPrimary">
	<s:submit label="Submit" cssClass="buttonGeneric" type="button" onclick="updateShipmentInfo();" />
	<button class="buttonGeneric" onClick="javaScript:popupShipmentTag()"><s:text
		name="label.printShipmentTag" /></button>
</div>

<table>
	<tr align="center">
		<td width="100%"></td>
	</tr>
</table>
</div>
</div>
</u:body>
</html>
