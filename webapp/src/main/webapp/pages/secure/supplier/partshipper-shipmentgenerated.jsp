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
<%@taglib prefix="authz" uri="authz"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>

    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript" src="scripts/clientMachineDate.js"></script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
   
    <script>
    
    	dojo.require("dijit.layout.LayoutContainer");
    	dojo.require("dijit.layout.ContentPane");
        function popupShipmentTag() {
            var selectedLoc = document.getElementsByName("shipment.destination")[0].value;
            var contact_person=document.getElementById("partShipperUpdateTag_submit_shipment_contactPersonName").value;
            window.open('partShipperUpdateTag_shipmentTag.action?shipment=<s:property value="%{shipment.id}"/>&shipment.destination='+selectedLoc+'&shipment.contactPersonName='+contact_person,
                    'ShipmentTag',
                    'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
        }
        function invokePrintPopup(shipmentId){
        	var win = "";
        	var name='Supplier Shipment Generated'
        	var dateTime=getPrintDate();
        	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&taskName='+name;
        	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
        	return false;
         }
        function updateShipmentInfo() {
            var frm = document.getElementById('partShipperUpdateTag_submit');
            frm.action = 'partShipperUpdateTag_submit.action';
            frm.transitionTaken.value = '<s:property value="%{@tavant.twms.jbpm.WorkflowConstants@UPDATE}"/>';
            frm.target = '_self';
            frm.submit();
        }

        function addNewPart() {
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
            }

        function removePart() {
            var frm = document.getElementById('partShipperUpdateTag_submit');
            frm.transitionTaken.value = '<s:property value="%{@tavant.twms.jbpm.WorkflowConstants@REMOVE}"/>';
            frm.action = 'partShipperUpdateTag_removePart.action';
            frm.target = '_self';
            frm.submit();
        }

        function updateMasterView(id) {
            manageRowHide("supplyRecoveryInboxTable", '<s:property value="id"/>');
        }

        <s:if test='shipment.supplierParts.empty'>
        dojo.addOnLoad(function() {
            updateMasterView('<s:property value="%{shipment.id}"/>');
        });
        </s:if>

        function addPartsForPartShipper() {
            var frm = document.getElementById('partShipperUpdateTag_submit');
            frm.transitionTaken.value = "<s:property value='%{@tavant.twms.jbpm.WorkflowConstants@GENERATE_SHIPMENT}'/>";
            frm.action = "partShipperUpdateTag_addNewPartsToShipment.action";
            frm.submit();
        }

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
	<s:form action="partShipperUpdateTag_submit" theme="twms"
		validate="true" method="post" id="partShipperUpdateTag_submit">

		<s:hidden name="id" value="%{shipment.id}" />
		<s:hidden name="shipment" value="%{shipment.id}" />
		<s:hidden name="inboxViewId" value="-1"/>
		<jsp:include flush="true" page="partsgroupedbyclaimforshipment.jsp" />

		<div class="buttonWrapperPrimary">
			<input type="button" value='<s:text name="label.removePartFromThisTag" />'
				class="buttonGeneric" onClick="javascript:removePart();" />
			<input type="button" class="buttonGeneric" onClick="addNewPart()" 
				value='<s:text name="label.addNewPartToThisTag" />' /> 
		</div>
			
		<div id="divForAdditionalParts"></div>
<div class="policy_section_div">
<div class="section_header">Shipment Information</div>
			
		<table width="100%"  cellspacing="0" cellpadding="0" class="grid">

			<tr>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.partShipperPartsShipped.vpraNumber" />:</td>
                <td width="35%"><s:property value="id" /></td>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.shipmentGenerated.return_location" />:</td>
                <td width="35%">
                   <script type="text/javascript">
                        dojo.addOnLoad(function() {
                              dojo.subscribe("/supplier/shipment/destination/changed", function(data){
                                    var loc = document.getElementsByName("shipment.destination")[0].value;
                                    loadAddress(loc);
                              });
                        });

                        function loadAddress(destination){
                            twms.ajax.fireHtmlRequest("displayShippingAddress.action?", {location:destination}, function(data) {
                                dojo.byId("showaddresshere").innerHTML = data;
                            });
                        }

                        </script>
                        <sd:autocompleter name='shipment.destination' keyName='shipment.destination' href='list_shipmentLocations.action'
                                             loadMinimumCount='0' keyValue='%{shipment.destination.id}' value='%{shipment.destination.code}'
                                             listenTopics='/supplier/shipment/destination'
                                             notifyTopics="/supplier/shipment/destination/changed" onselect="updateContactPersonName('%{shipment.destination.address.contactPersonName}');"/>
                        <script type="text/javascript">
                             dojo.addOnLoad(function() {
                                 var isOnLoad=true;
                                 var url= "list_supplierLocations.action";
                                 var supplierId = '<s:property value="#recClaim.contract.supplier.id"/>';
                                 dojo.publish("/supplier/shipment/destination", [{
                                             url: url,
                                             params: {
                                                 "supplier": supplierId
                                             },
                                             makeLocal: true
                                         }]);

                             });
                        </script>
                   </td>

                </td>
            </tr>
            <tr>
                <td width="15%" nowrap="nowrap" class="label"><s:text name="label.manageWarehouse.contactPersonName" />:</td>
                <td width="35%"><s:textfield name="shipment.contactPersonName"/></td>
            </tr>
            <tr>
               <td colSpan="4">
                   <b><s:text name="label.supplier.shipping.address"/>:</b>
                   <div id="showaddresshere"></div>
               </td>
            </tr>
			<tr>
				<td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.duePartsReceipt.tracking_no" />:</td>
				<td width="35%"><s:textfield name="shipment.trackingId" /></td>
				<td width="15%" nowrap="nowrap" class="label"><s:text name="columnTitle.duePartsReceipt.shipment_date" />:</td>
				<td width="35%"><sd:datetimepicker name='shipment.shipmentDate' value='%{shipmentDate}' id='partShipperShipmentDate' /></td>
			</tr>
			<tr>
				<td width="15%" nowrap="nowrap" class="label" valign="top">Carrier:</td>
				<td width="35%" valign="top"><s:select
					name="shipment.carrier" value="%{shipment.carrier.id.toString()}"
					list="carriers" listKey="id" listValue="nameAndCode"
					/></td>
				<td width="15%" class="label" nowrap="nowrap" valign="top"><s:text
							name="label.common.dimensionsComments" />:</td>
				<td nowrap="nowrap"><t:textarea name="shipment.comments"
					cols="35" rows="3" /></td>
			</tr>
		</table>
		</div>
		<s:hidden name="transitionTaken" 
			value='%{@tavant.twms.jbpm.WorkflowConstants@UPDATE}' />

	</s:form>
<s:if test="!isBuConfigAMER()">
<div class="buttonWrapperPrimary" id="buttonsDiv">
	<s:submit label="Submit" cssClass="buttonGeneric" type="button" onclick="updateShipmentInfo();" />
	<button class="buttonGeneric" onClick="javaScript:popupShipmentTag()"><s:text
		name="label.printShipmentTag" /></button>
</div>
</s:if>
	<s:if test="isBuConfigAMER()">
	<div class="buttonWrapperPrimary" id="buttonsDiv">
		<s:submit label="Submit" cssClass="buttonGeneric" type="button" onclick="updateShipmentInfo();" />
	<input type="button" name="Submit223" value="<s:text name="label.printShipmentTag"/>
        " class="buttonGeneric" onclick="invokePrintPopup( <s:property value="%{id}"/>)"/> 
	</div>
	</s:if>
<table>
	<tr align="center">
		<td width="100%"></td>
	</tr>
</table>
</div>
</div>
</u:body>
<authz:ifPermitted resource="partsRecoveryShipmentGeneratedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('partShipperUpdateTag_submit')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('partShipperUpdateTag_submit'))[i].disabled=true;
	        }
	        document.getElementById("buttonsDiv").style.display="none";
	    });
	</script>
</authz:ifPermitted>
</html>
