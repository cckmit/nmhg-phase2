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
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="form.css"/>
    <style type="text/css">
        .addRow {
            margin-top: -14px;
            height: 14px;
            text-align: right;
            padding-right: 17px;
        }
    </style>
 <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
</head>
 <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });        
        
</script>
<script type="text/javascript" src="scripts/clientMachineDate.js"></script>
    <script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
 	dojo.require("dijit.layout.LayoutContainer");
       function invokePrintPopup(shipmentId){
       	var win = "";
       	var name='Parts Shipped to NMHG'
       	var dateTime=getPrintDate();
       	var actionUrlString = 'partreturns_displayForPrint.action?shipmentIdString='+ shipmentId +'&clientDate='+dateTime+'&taskName='+name;
       	window.open(actionUrlString,'win','directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
       	return false;
        }

      </script>
<u:body>

    <u:actionResults/>
<br/>
   <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.recoveryClaimDetails"/>: <s:property value="fetchedRecoveryClaim.recoveryClaimNumber"/>"
             labelNodeClass="section_header" open="true">     
    
    <table cellspacing="0" border="0" cellpadding="0" class="grid" width="100%">
  <tr>
    <td class="labelStyle" width="16%"  nowrap="nowrap"><%-- <s:text name="label.common.claimNumber" /> --%><s:text name="columnTitle.common.recClaimNo" />:</td>
    <td class="labelNormal" width="35%">    	
	      <u:openTab tabLabel="Claim Number"
	      			 url="recovery_claim_search_detail.action?id=%{recoveryClaim.id}"
	      			 id="claimIdForPart%{claim.id}"
	      			 cssClass="link"
	      			 decendentOf="%{getText('label.invTransaction.tabHeading')}">
	    	<s:property value="fetchedRecoveryClaim.recoveryClaimNumber" />
	    	</u:openTab>    	
 
	</td>		    		     
    <td class="labelStyle"  nowrap="nowrap" width="16%"><s:text name="label.common.serialNumber" />:</td>
    <td class="labelNormal" width="35%">
      <s:if test="claim.itemReference.referredInventoryItem.serialNumber != null">    
      
		      <s:property value="claim.itemReference.referredInventoryItem.serialNumber" />
		
	  </s:if>
	  <s:else>
	    <div>-</div>
	  </s:else> 
	</td>
  </tr>
  <tr>
    <s:if test="claim.type.type.equals('Parts') && claim.partInstalled == false">
      <td class="labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.partNumberWithCol" />:</td>
      <td class="labelNormal"><s:property value="%{claim.partItemReference.referredItem.alternateNumber}" /></td>
    </s:if>
    <s:else>  
      <td class="labelStyle"  nowrap="nowrap"><s:text name="label.partReturnConfiguration.modelnumber" />:</td>
      <td class="labelNormal">
      <s:if test="claim.itemReference.isSerialized()">
    	<s:property value="claim.itemReference.unserializedItem.model.name" />
      </s:if>
      <s:else>
    	<s:property value="claim.itemReference.model.name" />
      </s:else>
      </td>
    </s:else>      
  </tr>		    
</table>   
   <%--   <jsp:include flush="true" page="../../partreturns/tables/claim_details.jsp"/>   --%>

   <div style="margin:10px 0; padding-left:5px; color:#5577B4;font-weight:700;"> <s:text name="title.partReturnConfiguration.partDetails"/></div>
  
  <table class="grid borderForTable" style="width: 97%" cellspacing="0"
		cellpadding="0">
		<s:iterator value="recoverablePartsBeans"
       				id="recoverablePartBean" status="partsStatus">
       	<s:set id="oEMPartReplaced" value="#recoverablePartBean.recoverablePart.oemPart" name="oEMPartReplaced"/>
       	<tbody>
       	<s:if test="#partsStatus.index==0">
       	   <tr class="row_head">            
                <th><s:text name="label.common.partNumber" /></th>
                <th><s:text name="label.common.description" /></th>
                <th><s:text name="label.supplierPartNumber" /></th>
                <th><s:text name="label.partReturn.shipped"/></th>
               <th><s:text name="label.partReturn.cannotShip" /></th>
                <th><s:text name="label.partReturn.total"/></th>                
                <th><s:text name="label.common.returnlocation" /></th>              
             </tr>
             </s:if>
			 <tr>					
					<td><s:property
							value="#oEMPartReplaced.itemReference.referredItem.number" /></td>
					<td><s:property
							value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
					<td><s:property value="recoverablePart.supplierItem.number" /></td>
					<td><s:property value="shipped" /></td>
					<td><s:property value="cannotShip" /></td>
					<td><s:property value="totalNoOfParts" /></td>
					<td><s:property
							value="getDefaultReturnLocationForNMHG()" /></td>

				</tr>
			</s:iterator>
		</tbody>
	</table>	
	
	
  </div>
  <br/>  
<div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.shipmentInfo"/>" labelNodeClass="section_header" >
		  <table class="grid" cellspacing="0" cellpadding="0">
		    <tr>
			 <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="columnTitle.duePartsReceipt.shipment_no"/>:</td>
				  <td class="carrierLabelNormal" width="35%">
				    <s:property value="shipment.id" />
			 </td>
			 <s:if test="!taskName.equals('Dealer Requested Parts Shipped')">
                  <td width="20%" class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.trackingNumber"/>: </td>
                  <td width="35%" class="carrierLabelNormal">
                   <%--  <s:property value="shipmentFromPartBeans.trackingId" /> --%>
                    
                    <s:property value="shipment.trackingId"/>
                  </td>
             </s:if>
			</tr>
			<tr>
			  <td  class="carrierLabel labelStyle" nowrap="nowrap">		
			       <s:text name="label.partReturnConfiguration.shipmentDate"/>		
			       :</td>
	          <td  class="carrierLabelNormal">
			      <s:property value="shipment.shipmentDate" />
			  </td>			  
			  <td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier"/>:</td>
              <td  class="carrierLabelNormal">                     
                          <s:property value="shipment.carrier.name" />                   
             </td>
      
			</tr>
			<%-- <tr>
			<td  class="carrierLabel labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>:</td>
			  <td  class="carrierLabelNormal">
			    <s:property value="shipment.comments" />
			  </td>
			</tr> --%>
		  </table>
		</div> 
		    <div class="buttonWrapperPrimary">
	      <input type="button" name="Submit223" value="<s:text name="button.partReturnConfiguration.printShipment"/>
	        " class="buttonGeneric" onclick="invokePrintPopup(
	        <s:property value="%{id}"/>
	        )"/>      
        </div>
</u:body>
<br/>
<br/>


</html>