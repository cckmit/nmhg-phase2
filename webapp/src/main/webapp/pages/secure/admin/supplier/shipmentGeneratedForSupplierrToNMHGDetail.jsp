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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>   
    <s:head theme="twms"/>   
    <u:stylePicker fileName="common.css"/>   
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="partreturn.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="claimForm.css"/>
 <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<s:if test="isBuConfigAMER()">
 <script type="text/javascript" src="scripts/clientMachineDate.js"></script>

  <script>
    	dojo.require("dijit.layout.LayoutContainer");
    	dojo.require("dijit.layout.ContentPane");
        function invokePrintPopup(shipmentId){
        	var win = "";
        	var name='Shipment Generated To NMHG'
        	var dateTime=getPrintDate();
        	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&taskName='+name;
        	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
        	return false;
         }
</script>
 </s:if>
</head>
 <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });    
</script>
<u:body>
  <s:form id="baseForm" name="baseForm">  
    <u:actionResults/>
<br/>
   <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.recoveryClaimDetails"/>: <s:property value="fetchedRecoveryClaim.recoveryClaimNumber"/>"
             labelNodeClass="section_header" open="true">       

   
  
  <table class="grid borderForTable" style="width: 97%" cellspacing="0"
		cellpadding="0">
		<s:iterator value="recoverablePartsBeans"
       				id="recoverablePartBean" status="partsStatus">
       	<s:set id="oEMPartReplaced" value="#recoverablePartBean.recoverablePart.oemPart" name="oEMPartReplaced"/>
       	<s:iterator value="recoveryPartTaskBeans" status ="taskIterator">
                   <input type="hidden"
                      name="uiRecoverablePartsBeans[<s:property value="%{#partsStatus.index}"/>].recoveryPartTaskBeans[<s:property value="%{#taskIterator.index}"/>].task"
                      value="<s:property value="task.id"/>"/>
        </s:iterator>
        <input type="hidden" name="uiRecoverablePartsBeans[<s:property value="%{#partsStatus.index}"/>].recoverablePart"
              				value="<s:property value="recoverablePartsBeans[#partsStatus.index].recoverablePart.id"/>"/>

        <input type="hidden"
                 		     name="uiRecoverablePartsBeans[<s:property value="%{#partsStatus.index}"/>].shipmentGenerated"
                		     value="<s:property value="shipmentGenerated" />"/>


       	<tbody>
       	<s:if test="#partsStatus.index==0">
       	   <tr class="row_head">

                <th><s:text name="label.common.partNumber" /></th>
                <th><s:text name="label.common.description" /></th>
                <th><s:text name="label.supplierPartNumber" /></th>
                <th><s:text name="label.partReturn.shipementGenerated"/></th>
                <th><s:text name="label.partReturn.cannotShip" /></th>
                <th><s:text name="label.partReturn.total"/></th>
                <th><s:text name="label.common.returnlocation" /></th>
                
             </tr>
             </s:if>
			 <tr>
<s:hidden name="uiRecoverablePartsBeans[%{#partsStatus.index}].selected"
                         		     value="true"
                         		     id="%{#partsStatus.index}" />
					<td><s:property
							value="#oEMPartReplaced.itemReference.referredItem.number" /></td>
					<td><s:property
							value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
					<td><s:property value="recoverablePart.supplierItem.number" /></td>
					<td><s:property value="shipmentGenerated" /></td>
					<td><s:property  value="cannotShip"/></td>
					<td><s:property value="totalNoOfParts" /></td>
					<td><s:property
							value="getDefaultReturnLocationForNMHG()" /></td>

				</tr>
			</s:iterator>
		</tbody>
	</table>	
	
	
  </div>
  <br/>
  	<div id="wpraHeading" class="section_header">
			<s:text name="title.partReturnConfiguration.shipmentInfo" />
		</div>	
		  <div class="inspectionResult" style="width:99%">
        <table border="0" cellspacing="0" class="grid" style="width:99%" >

          <tr>
            <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="columnTitle.duePartsReceipt.shipment_no"/>
              :</td>
            <td  class="carrierLabelNormal"><s:property value="shipment.id" />
            <s:hidden name="shipment.id" value="%{shipment.id}" />
            <s:hidden name="shipment" value="%{shipment}" />
            </td>
            <td>
                      </td>
                    <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.shipmentDate"/>
                      :</td>                    

                     <td  class="carrierLabelNormal"><sd:datetimepicker name='shipment.shipmentDate' value='%{shipment.shipmentDate}' id='partShipmentDate'/>
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
                     <td>
                      </td>
                    <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier"/>
                      :</td>
                    <td  class="widgetTopAlign"><s:select name="carrierId" list="carriers" listKey="id"
                            listValue="name" headerKey="0" headerValue='%{getText("label.partreturns.selectCarrierheader")}'
                        value = "carrierId.toString()" /> 
                  </tr>                 
              
          </table>        
          </div>
            <br/> <br/>
          <div class="buttonWrapperPrimary">	
	 <s:submit name="action:shipmentGeneratedToNMHG_submit" value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
	  <s:hidden name="id" value="%{recoveryClaim.id}" />
	  <s:if test="isBuConfigAMER()">
	<input type="button" name="Submit223" value="<s:text name="label.printShipmentTag"/>
        " class="buttonGeneric" onclick="invokePrintPopup( <s:property value="%{id}"/>)"/> 
      </s:if>
  	</div>	
		
		</s:form>
</u:body>
<br/>
<br/>


</html>