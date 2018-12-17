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
<%@taglib prefix="authz" uri="authz"%>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title>
<s:text name="title.common.warranty"/>
</title>
<script type="text/javascript" src="scripts/clientMachineDate.js"></script>
<s:head theme="twms"/>
<script type="text/javascript">
<s:if test="pageReadOnlyAdditional">
dojo.addOnLoad(function() {
    twms.util.makePageReadOnly("dishonourReadOnly");
});
</s:if>
dojo.require("dijit.layout.ContentPane");
var __masterCheckBoxControls = new Array();
<s:if test="isBuConfigAMER()">
 function invokePrintPopup(shipmentId){
	var win = "";
	var dateTime=getPrintDate();
	var comments=dojo.byId("comments").value;
	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&shippingComments='+comments;
	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
	return false;
 }

function invokePrintPopupForDealer(shipmentId){
	var win = "";
	var dateTime=getPrintDate();
	var comments=dojo.byId("comments").value;
	var actionUrlString = 'partreturns_displayForPrint_for_dealer.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&shippingComments='+comments;
	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
	return false;
 }
</s:if>
<s:else>
function invokePrintPopup(shipmentId){
	var win = "";
	var dateTime=getPrintDate();
	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime;
	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
	return false;
 }

function invokePrintPopupForDealer(shipmentId){
	var win = "";
	var dateTime=getPrintDate();
	var actionUrlString = 'partreturns_displayForPrint_for_dealer.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime;
	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
	return false;
 }
</s:else>
 function showPartsToBeAdded() {
        var idString = dojo.byId("identifier").value;
    twms.ajax.fireHtmlRequest("shipmentgenerated_addpart.action", {
        id:idString
        },function(data) {
            var parentDiv = dijit.byId("partsToBeAddedDiv");
            parentDiv.setContent(data);
            delete data, parentDiv;
	      }
    );
 }

 function addPartsToShipment() {
 	var form1 = document.baseForm;
 	form1.action = "shipmentgenerated_addpartToShipment.action";
 	form1.submit();
 }


</script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
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
  <div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
    <h4 class="twmsActionResultActionHead"></h4>
    <s:text name="message.printPart.warning"></s:text>
    <hr/>
  </div>
  <s:form id="baseForm" name="baseForm">
    <s:hidden id="identifier" name="id"/>
    <u:actionResults/>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
      <div class="separatorTop" />
      <s:set name="partsCounter" value="0"/>
      <s:iterator value="claimWithPartBeans" status="claimIterator">
      <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>
      (
      <s:property value="claim.claimNumber"/>
      )" labelNodeClass="section_header" >
      <div style="width:98%">
        <%@include file="tables/claim_details.jsp"%>
      </div>
      <div class="mainTitleNoborder">
        <s:text name="title.partReturnConfiguration.partDetails"/>
      </div>
        <%@include file="tables/shipment_generate_view.jsp"%>
    </div>
    </div>
    <div class="separator" ></div>
    </s:iterator>
    </div>
    <div width ="100%">
      <!-- HACK for IE -->
      <s:if test="!(taskName.equals('Shipment Generated For Dealer'))">
          <div class="buttonWrapperPrimary">
             <s:submit name="action:shipmentgenerated_removepart" value="%{getText('button.partReturnConfiguration.removePart')}" cssClass="buttonGeneric"/>
             <input type="button" value="<s:text name="button.partReturnConfiguration.addpart"/>
             " class="buttonGeneric" onclick="showPartsToBeAdded()"/> </div>
      </s:if>
      <div dojoType="dojox.layout.ContentPane" label="Parts List"
         style="overflow-X: hidden; overflow-Y: auto; width:100%;" id="partsToBeAddedDiv"> </div>
      <div class="detailsHeader" style="width:99%; margin-left:5px">
        <s:text name="title.partReturnConfiguration.shipmentInfo"/>
      </div>
      <div class="inspectionResult" style="width:99%">
        <table border="0" cellspacing="0" class="grid" style="width:99%" >

          <tr>
          <s:if test="!buConfigAMER"> 
            <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="columnTitle.duePartsReceipt.shipment_no"/>
              :</td>
            <td  class="carrierLabelNormal"><s:property value="id" />
            </td>
            </s:if>
             <%-- <s:if test="!isShipmentThroughCEVA()"> --%>
                 <s:if test="(taskName.equals('Shipment Generated For Dealer'))">
                    <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.availableDate"/>
                      :</td>
                     <td  class="carrierLabelNormal"><sd:datetimepicker name='shipmentCalenderDate' value='%{shipmentDate}' id='partShipmentDate'/>
                     <s:if test="!buConfigAMER"> 
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
                           </s:if>
                 </s:if>
                 <s:elseif test="!isShipmentThroughCEVA()">
                    <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.shipmentDate"/>
                      :</td>

                     <td  class="carrierLabelNormal"><sd:datetimepicker name='shipmentCalenderDate' value='%{shipmentDate}' id='partShipmentDate'/>
                     <s:if test="!buConfigAMER"> 
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
                        </s:if>
                 </s:elseif>


                </td>
             <%--  </s:if> --%>
          </tr>
           <s:if test="!taskName.equals('Shipment Generated For Dealer')">
              <s:if test="!isShipmentThroughCEVA()">
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
              </s:if>
          </s:if>
          <tr> <td colspan="4">
            <u:repeatTable id="load_dimension_table" cssClass="grid borderForTable" theme="twms"  cellspacing="0" cellpadding="0" cssStyle="margin:5px;width:100%">
                       		<thead>
                       			<tr class="title">
                       				<th class="warColHeader" width="20%"><s:text name="label.partReturnConfiguration.loadType"/></th>
                       				<s:if test="!buConfigAMER"> 
                       				<th class="warColHeader" width="20%"><s:text name="label.common.shipmentDimensions"/></th>
                       				</s:if>
                       				<th class="warColHeader" width="10%"><s:text name="label.partreturns.AddToShipment"/></th>
                       				<th class="warColHeader" width="5%">
                       					<u:repeatAdd id="load_dimension_adder" theme="twms">
                       						<img id="addLoadDimension" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/>
                       					</u:repeatAdd>
                       				</th>

                       			</tr>
                       		</thead>
                       		<u:repeatTemplate id="bodyForLoadDimension" value="shipmentLoadDimension" index="index" theme="twms">
                       	        <tr index="#index">

                                        <td width="20%">
                                            <s:select list="loadTypes" id="loadTypes" headerKey="" headerValue='%{getText("label.partreturns.selectLoadType")}'
                                                                  name="shipmentLoadDimension[#index].loadType" value="%{shipmentLoadDimension[#index].loadType}"> </s:select>
                                        </td>
                                        <s:if test="!buConfigAMER">
                                        <td width="20%">
                                            <table>
                                                <tr><td><s:text name="label.partShipmentTag.shipment.length" />(m)</td>
                                                <td><s:text name="label.partShipmentTag.shipment.width" />(m)</td>
                                                <td><s:text name="label.partShipmentTag.shipment.height" />(m)</td>
                                                <td><s:text name="label.partShipmentTag.shipment.weight" />(kg)</td>
                                                <tr><td><s:textfield id="Length" value="%{shipmentLoadDimension[#index].Length}" name="shipmentLoadDimension[#index].length" size="5" /></td>
                                                    <td><s:textfield id="Breadth" value="%{shipmentLoadDimension[#index].Breadth}" name="shipmentLoadDimension[#index].breadth" size="5" /></td>
                                                    <td><s:textfield id="Height" value="%{shipmentLoadDimension[#index].height}" name="shipmentLoadDimension[#index].height" size="5" /></td>
                                                    <td><s:textfield id="Weight"  value="%{shipmentLoadDimension[#index].Weight}" name="shipmentLoadDimension[#index].weight" size="5" /></td>
                                                </tr>
                                            </table>
                                        </td>
                                        </s:if>
                                        <td width="10%">
                                            <s:property value="id" />
                                        </td>
                       	        	    <td width="5%">
                       	                    <u:repeatDelete id="dealers_LoadDimension_#index"  theme="twms">
                       						   <img id="deleteLoadDimension" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteRow" />"/>
                       	                    </u:repeatDelete>
                                   	</td>
                       			</tr>
                       		</u:repeatTemplate>
                       	</u:repeatTable>   </td>
          </tr>

          <tr>
             <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>:</td>
             <td  class="carrierLabelNormal"><t:textarea name="comments" id="comments" cols="40"/></td>
          </tr>
        </table>
      </div>
      <div class="separator"></div>
      <div class="buttonWrapperPrimary">
      <s:if test="taskName.equals('Shipment Generated For Dealer')">
          <s:submit name="action:generateShipmentForDealer_submit" value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
       <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
        " class="buttonGeneric" onclick="invokePrintPopupForDealer(<s:property value="%{id}"/>)"/>  
      </s:if>
      <s:else>
        <s:submit name="action:shipmentgenerated_submit" value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
        <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
        " class="buttonGeneric" onclick="invokePrintPopup(
        <s:property value="%{id}"/>
        )"/>    
        </s:else>
        </div>
    </div>
  </s:form>
<authz:ifPermitted resource="partReturnsShipmentGeneratedForDealerReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
