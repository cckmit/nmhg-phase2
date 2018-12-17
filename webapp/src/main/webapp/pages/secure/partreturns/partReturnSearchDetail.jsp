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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<s:head theme="twms" />
<title><s:text name="title.common.warranty" /></title>


    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
</head>

<u:body>
<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.ValidationTextBox");      
        
    </script>
<form action="markPartAsScrapped.action" method="post" id="baseFormId">
        <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
              
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>" id="claim_details"
                 labelNodeClass="section_header"  open="true">
        <table class="grid" cellspacing="0" cellpadding="0"
         width="97%">
            <tr>
            <td class="labelStyle"><s:text name="label.common.claimNumber" />:</td>
            <td class="labelNormal">
             <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
                <u:openTab
                    tabLabel="Claim Number %{partReturnClaimSummary.claim.claimNumber}"
                    url="view_search_detail.action?id=%{partReturnClaimSummary.claim.id}"
                    id="claimIdForPart%{partReturnClaimSummary.claim.id}"
                    cssClass="Inboxlink">
                    <s:property value="partReturnClaimSummary.claim.claimNumber" />
                </u:openTab>
            </authz:ifUserNotInRole>
            <authz:else><s:property value="partReturnClaimSummary.claim.claimNumber" /></authz:else>
            </td>
            <td class="labelStyle"><s:text name="label.common.serialNumber" />:</td>
            <td class="labelNormal"><s:if
                test="partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber != null">
                <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
                    <u:openTab
                        tabLabel="Serial Number %{partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber}"
                        url="inventoryDetail.action?id=%{partReturnClaimSummary.claim.itemReference.referredInventoryItem.id}"
                        id="SerialNoForPart%{partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber}%{partReturnClaimSummary.claim.id}"
                        cssClass="Inboxlink">
                        <s:property
                            value="partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber" />
                    </u:openTab>
                 </authz:ifUserNotInRole>
                 <authz:else><s:property value="partReturnClaimSummary.claim.itemReference.referredInventoryItem.serialNumber" /></authz:else>
            </s:if> <s:else>
                <div>-</div>
            </s:else></td>
        </tr>
        <tr>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.modelnumber" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.claim.itemReference.unserializedItem.model.name" /></td>
            <td class="labelStyle"><s:text name="label.common.itemNumber" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.claim.itemReference.unserializedItem.alternateNumber" /></td>
        </tr>
    </table>
    </div>
  
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" id="part_details"
                 labelNodeClass="section_header"  open="true">

    <table class="grid" cellspacing="0" cellpadding="0"
         width="97%">

        <tr>
            <s:if test="part.itemReference.serialized == true">
                <td class="labelStyle"><s:text name="label.common.serialNumber" />:</td>
                <td class="labelNormal"><s:property
                    value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.referredInventoryItem.serialNumber" />
                </td>
            </s:if>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.partNumberWithCol" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.unserializedItem.alternateNumber" /></td>
            <s:hidden name="oemPartReplaced" value="%{partReturnClaimSummary.partReturn.oemPartReplaced}" />
        </tr>

        <tr>
            <td class="labelStyle"><s:text name="columnTitle.common.description" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.oemPartReplaced.itemReference.unserializedItem.description" /></td>
            <td class="labelStyle"><s:text name="columnTitle.common.quantity" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.oemPartReplaced.numberOfUnits" /></td>
        </tr>

        <tr>
            <td class="labelStyle"><s:text name="columnTitle.common.dueDate" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.dueDate" /></td>
            <td class="labelStyle"><s:text name="label.common.status" />:</td>
            <td class="labelNormal">
	            <s:if test="partReturnClaimSummary.claim.partReturnStatus != null||
	             partReturnClaimSummary.claim.partReturnStatus !='' "> 
	            <s:property value="partReturnClaimSummary.claim.partReturnStatus" />
	            </s:if>
	            <s:if test="partReturnClaimSummary.partReturn.oemPartReplaced.isPartScrapped()" > (
	             <s:text name="label.warrantyAdmin.itemCondition.scrap" />) 
                </s:if>
            </td>
        </tr>

        <tr>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.carrier" />:</td>
            <td class="labelNormal">
                <a href="<s:property value="partReturnClaimSummary.partReturn.shipment.carrier.url" escape="false"/>" target="_blank">
                        <s:property value="partReturnClaimSummary.partReturn.shipment.carrier.name" />
                </a>
            </td>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.trackingNumber" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.shipment.trackingId" /></td>
        </tr>

        <tr>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.shipmentDate" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.shipment.shipmentDate" /></td>
            <td class="labelStyle"><s:text
                name="label.partReturnConfiguration.shipmentNumber" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.shipment.transientId"/></td>
        </tr>

        <tr>
            <td class="labelStyle"><s:text
                name="columnTitle.partShipperPartsClaimed.location" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.returnLocation.code" /></td>
            <td class="labelStyle"><s:text
                name="columnTitle.partReturnConfiguration.failurecause" />:</td>
            <td class="labelNormal"><s:property
                value="partReturnClaimSummary.partReturn.inspectionResult.failureReason.description" /></td>
        </tr>
        <tr>
           <td class="labelStyle"><s:text name="columnTitle.common.wpra" />:</td>
           <td class="labelNormal">
              <s:set name="wpraLinkEnable" value="true" />
              <s:iterator value="partReturnClaimSummary.partReturn.oemPartReplaced.partReturnAudits" status="auditStatus">
                  <s:if test="partReturnAction1 != null && partReturnAction1.actionTaken.equals(@tavant.twms.domain.partreturn.PartReturnStatus@REMOVED_BY_PROCESSOR.status)">
                        <s:set name="wpraLinkEnable" value="false" />
                  </s:if>
              </s:iterator>
               <s:if test="wpraLinkEnable">
               <u:openTab
                      tabLabel="WPRA Number %{partReturnClaimSummary.partReturn.wpra.wpraNumber}"
                      url="wpraGeneratedForParts_detail.action?id=%{partReturnClaimSummary.partReturn.wpra.id}"
                      id="%{partReturnClaimSummary.partReturn.wpra.id}"
                      cssClass="Inboxlink">
                      <s:property value="partReturnClaimSummary.partReturn.oemPartReplaced.wpra.wpraNumber" />
                  </u:openTab>

               </s:if>
               <s:else>
                   <s:property value="partReturnClaimSummary.partReturn.wpra.wpraNumber" />
               </s:else>
           </td>
        </tr>
    </table>
    </div>
    
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.actionHistory"/>" id="action_history"
                 labelNodeClass="section_header"  open="true">

    <table  class="grid borderForTable" style="width:100%" cellspacing="0" cellpadding="0">
        <thead>
            <tr class="row_head">
                <th ><s:text name="label.common.user"/></th>
                <th><s:text name="label.partReturnAudit.actionPerformed"/></th>
                <th><s:text name="label.common.date" /></th>
                <th ><s:text name="label.common.comments"/></th>
            </tr>
        </thead>
        <tbody>
           <s:iterator value="partReturnClaimSummary.partReturn.oemPartReplaced.partReturnAudits" status="auditStatus">
             <s:if test="!(isLoggedInUserADealer() && (prStatus=='Part Received' || prStatus=='Part Accepted' || prStatus== 'Part Rejected'))">
               <tr>
                <td ><s:property value="d.lastUpdatedBy.completeNameAndLogin"/></td>
                <td>
                    <table border="0">
                        <s:if test="partReturnAction1!=null">
                        <tr>
                            <td>
                                <s:property value="partReturnAction1.actionTaken"/>:<s:property value="partReturnAction1.value"/>
                                <s:if test="(prStatus=='Partially Shipped' || prStatus=='Part Shipped') &&  partReturnAction1.trackingNumber!=null">
                                    (<s:property value="partReturnAction1.trackingNumber"/>)
                                </s:if>
                                <s:if test="(prStatus=='Shipment Generated' || prStatus=='Partially Shipment Generated') && partReturnAction1.shipmentId!=null">
                                    (<s:property value="partReturnAction1.shipmentId"/>)
                                </s:if>
                                <s:if test="(prStatus=='WPRA Generated')">
                                    (<s:property value="partReturnClaimSummary.partReturn.oemPartReplaced.wpra.wpraNumber"/>)
                                </s:if>
                            </td>

                        </tr>
                        <s:if test="partReturnAction1.actionTaken=='Part Accepted' && !acceptanceCause.isEmpty()">
                    <tr>
                    <td> <s:text name="label.partReturnAudit.acceptanceReason"/>:&nbsp;<s:property value="acceptanceCause"/></td>
                    </tr>
                    	</s:if>
                        </s:if>
                        <s:if test="partReturnAction2!=null">
                        <tr>
                            <td>
                                <s:if test="prStatus=='Partially Received' ||prStatus=='Part Received' ||prStatus=='Part Not Received' ">
                                    <s:text name="label.common.notReceived"/>:
                                </s:if>
                                <s:else>
                                <s:property value="partReturnAction2.actionTaken"/>:
                                </s:else>
                                <s:property value="partReturnAction2.value"/> </td>
                        </tr>
                        
                          <s:if test="partReturnAction2.actionTaken=='Part Rejected' && !failureCause.isEmpty()">
                    <tr>
                    
                       <td> <s:text name="label.partReturnAudit.rejectionReason"/>:&nbsp;<s:property value="failureCause"/></td>
                        	
                    
                    </tr></s:if></s:if>
                    </table>
                </td>
                <td ><s:property value="d.updatedOn"/></td>
                <td><s:property value="comments"/></td>
                </tr>
                </s:if>
            </s:iterator>
        </tbody>
    </table>
    </div>
    
            <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.shipmentComments"/>" id="shipment_comments"
                 labelNodeClass="section_header"  open="true">

    <table class="grid" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td class="labelStyle" width="10%"><s:text name="label.partReturnDetail.shipmentComments" />:</td>
            <td><s:property
                value="%{partReturnClaimSummary.partReturn.oemPartReplaced.partReturnConfiguration.partReturnDefinition.shippingInstructions}" /></td>
        </tr>
         <tr>
            <td class="labelStyle" width="10%"><s:text name="label.partReturnDetail.cevaComments" />:</td>
            <td><s:property
                value="%{partReturnClaimSummary.partReturn.shipment.cevaComments}" /></td>
        </tr>
    </table>
    </div>
     <authz:ifUserInRole roles = "processor, recoveryProcessor, inspectorLimitedView">
        <s:if test="showScrapButton()" >
            <div class="buttonWrapperPrimary">
                <s:submit value="%{getText('button.scrap.markAsScrap')}" cssClass="buttonGeneric" />
            </div>
        </s:if>
     </authz:ifUserInRole>
   
    </div>
    </div>
</form>
<authz:ifPermitted resource="partReturnsPredefinedSearchReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
