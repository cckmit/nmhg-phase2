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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript">

function closeCurrentTab() {
    closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
}

function getSummaryPage() {
    var frm = document.getElementById('supplierAdminForm');
    frm.action = '<s:property value="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_FROM_DEALER_TASK)?'supplierRecoveryAdminDirectShipment_summary.action':'supplierRecoveryAdmin_summary.action'}"/>';
    frm.submit();
}


function submitForm(taskName) {
    with(document.forms.supplierRecoveryForm){
    	if(taskName== "For Recovery")
    	{
    		action='supplierRecoveryAdmin_submit.action';
    	}
    	else
    	{
    	if(taskName=="Supplier Response")
    	{
    		action='supplierRecoveryAdminReview_submit.action';
    	}
    	else
    	{
    		if(taskName=="Not For Recovery Response")
    		{
    			action='notForRecoveryResponse_submit.action';
    		}
    		else if(taskName=="Reopened")
    		{

    			action='supplierRecoveryAdminReopened_submit.action';
    		}
    		else if(taskName=="Transferred")
    		{

    			action='supplierRecoveryAdminTransferred_submit.action';
    		}
    		else
    		{
    			action='supplierRecoveryAdminOnHold_submit.action';
    		}
    	}
    	}
        submit();
    }
}

function setComments() {
var field = dojo.byId("com");
dojo.byId("internalComments").value = field.value;
}


</script>

<style type="text/css">
.totalAmountRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	text-transform:uppercase;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
.totalAmountTextRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
</style>



<s:hidden name="id"/>
<s:hidden name="recoveryClaim" value="%{id}" />


<%-- Set the appropriate transition depending on the Task name--%>
<div dojoType="twms.widget.TitlePane" id="recoveredComponentsPane" title="<s:text name='title.supplier.recoveredComponents'/>" labelNodeClass="section_header" overflow-x="auto">

<table border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" >
    <tr>
        <td width="7%" class="colHeaderTop" style="word-wrap:break-word"><s:text name="label.common.partNumber"/></td>
        <td width="12%" class="colHeaderTop" style="word-wrap:break-word"><s:text name="label.component.serialNumber"/></td>
        <td width="7%" class="colHeaderTop" style="word-wrap:break-word"><s:text name="label.common.dateCode"/></td>
        <td width="7%" class="colHeaderTop"><s:text name="columnTitle.common.quantity"/></td>
        <td width="8%" class="colHeaderTop"><s:text name="columnTitle.common.description"/></td>
        <td width="5%" class="colHeaderTop"><s:text name="label.recovery.return"/></td>
        <td width="7%" class="colHeaderTop"><s:text name="label.recovery.contract"/></td>
        <authz:ifUserNotInRole roles="supplier">
        <td width="8%" class="colHeaderTop" ><s:text name="columnTitle.listContracts.supplier_name"/></td>
        </authz:ifUserNotInRole>
        <td width="7%" class="colHeaderTop"><s:text name="columnTitle.partShipperPartsShipped.tracking_number"/></td>
        <td width="7%" class="colHeaderTop"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
        <td width="8%" class="colHeaderTop"><s:text name="columnTitle.common.returnlocation"/></td>
        <td width="7%" class="colHeaderTop"><s:text name="columnTitle.duePartsReceipt.carrier"/></td>
        <td width="7%" class="colHeaderTop"><s:text name="label.recoveryClaim.ShipmentAccountNumber"/></td>
    </tr>
    <s:set name="partsToBeShownCount" value="0" scope="page"></s:set>
    <s:iterator
        value="recoveryClaim.recoveryClaimInfo.recoverableParts"
        id="recoverablePart" status="partsStatus">
        <s:set id="oEMPartReplaced" value="#recoverablePart.oemPart" name="oEMPartReplaced"/>
            <tr>
                <td width="7%" ><s:property
                    value="#oEMPartReplaced.itemReference.referredItem.number" /></td>
                <td width="12%" style="word-break: break-all"><s:property
                    value="#oEMPartReplaced.serialNumber" /></td>
                <td width="7%" ><s:property
                    value="#oEMPartReplaced.dateCode" /></td>
                <td width="7%" style="text-align:center"><s:property
                    value="quantity" /></td>
                <td width="8%" ><s:property
                    value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
                <td width="5%" class="tableDataAltRow" align="center">
                <s:if test="!supplierPartReturnModificationAllowed" >
                <s:checkbox   disabled="true"
                name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded"
                id="val_%{#partsStatus.index}"/>
                </s:if>
                <s:else>
                <s:checkbox
                name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded"
                id="val_%{#partsStatus.index}"/>
                <s:hidden name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded" id="hide_%{#partsStatus.index}" />
                </s:else>
                <script type="text/javascript">
                function toggleEnableDisableShippingComments(){
                	if(dojo.byId("shippingCommentsToDealer")){
                		dojo.byId("shippingCommentsToDealer").disabled = true;
                		<s:iterator value="recoveryClaim.recoveryClaimInfo.recoverableParts" status="recoverableParts">
                			var index = <s:property value="#recoverableParts.index" />;
                			if(dojo.byId("val_"+index) && dojo.byId("val_"+index).checked
                					&& dojo.byId("shippingCommentsToDealer").disabled){
                				dojo.byId("shippingCommentsToDealer").disabled = false;
                			}
                		</s:iterator>
                	}
                 }
                 dojo.addOnLoad(function(){
                	 toggleEnableDisableShippingComments();
                 	 var isChecked = dojo.byId("val_<s:property value="%{#partsStatus.index}"/>").checked;
                     isDisabled = dojo.byId("val_<s:property value="%{#partsStatus.index}"/>").disabled;
                 	dojo.connect(dojo.byId("val_<s:property value="%{#partsStatus.index}"/>"),"onclick",function(){
                 		toggleEnableDisableShippingComments();
                 		isChecked = dojo.byId("val_<s:property value="%{#partsStatus.index}"/>").checked;
                 		if( ! isChecked){
                 			dojo.byId("hide_<s:property value="%{#partsStatus.index}"/>").value="false";
                 		}
                 		dijit.byId("carrier_<s:property value="%{#partsStatus.index}"/>").setDisabled(! isChecked);
						dijit.byId("location_<s:property value="%{#partsStatus.index}"/>").setDisabled(!isChecked);
						dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").disabled = (!isChecked);
	               });
                 		   dijit.byId("carrier_<s:property value="%{#partsStatus.index}"/>").setDisabled(!isChecked||isDisabled);
							dijit.byId("location_<s:property value="%{#partsStatus.index}"/>").setDisabled(!isChecked||isDisabled);
							dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").disabled = (!isChecked);
                 });
                </script>

                </td>
                <td width="7%" >
                <s:property
                	value="recoveryClaim.contract.name"
                    />
                    </td>

                <authz:ifUserNotInRole roles="supplier">
                <td width="8%" >
                <s:property
                	value="recoveryClaim.contract.supplier.name"
                    />
                </td>
                </authz:ifUserNotInRole>
                <td width="7%" ><s:if test="!supplierPartReturns.isEmpty() && supplierPartReturns[0].supplierShipment!=null"><s:property
                   value="supplierPartReturns[0].supplierShipment.trackingId"/></s:if></td>
                <td width="7%" >
                <s:if test="hasActionErrors()">

                <s:if test="supplierPartReturns[0].rgaNumber != null" >
                    <s:set name="rgaNumber" value="%{supplierPartReturns[0].rgaNumber}" />
                </s:if>
                <s:elseif test="recoveryClaim.recoveryClaimInfo.contract.rmaNumber != null">
                    <s:set name="rgaNumber" value="%{recoveryClaim.recoveryClaimInfo.contract.rmaNumber}" />
                </s:elseif>
                <s:textfield size="16" id="rgaNumber_%{#partsStatus.index}"
                                  	                   name="rgaNumbersProvided[%{#partsStatus.index}]"/>
               </s:if>
               <s:else>
                <s:set name="rgaNumber" value="" />
                <s:if test="supplierPartReturns[0].rgaNumber != null" >
                    <s:set name="rgaNumber" value="%{supplierPartReturns[0].rgaNumber}" />
                </s:if>
                <s:elseif test="recoveryClaim.recoveryClaimInfo.contract.rmaNumber != null">
                    <s:set name="rgaNumber" value="%{recoveryClaim.recoveryClaimInfo.contract.rmaNumber}" />
                </s:elseif>
                  <s:elseif test="oemPart.returnDirectlyToSupplier || oemPart.partToBeReturned">
                     <s:set name="rgaNumber" value="%{oemPart.partReturns[0].rmaNumber}" />
                   </s:elseif>
                <s:textfield size="16" id="rgaNumber_%{#partsStatus.index}" value="%{#rgaNumber}"
                                  	                   name="rgaNumbersProvided[%{#partsStatus.index}]"/>
               </s:else>
                </td>

                <td width="8%" >
                	<s:select id="location_%{#partsStatus.index}" cssStyle="width:65px;"
						name="selectedLocations[%{#partsStatus.index}]"
						list="locations"
						listKey="id"
						listValue="code"
						value='<s:property value="%{code}"/>'
						/>
					  <script type="text/javascript">
					  dojo.addOnLoad(function(){
						  <s:if test="supplierPartReturns[0] != null">
						  	dijit.byId('carrier_<s:property value="%{#partsStatus.index}"/>').setValue('<s:property value="%{supplierPartReturns[0].carrier.id}"/>' );
						  </s:if>
						  <s:if test="supplierPartReturns[0].returnLocation != null">
						  	dijit.byId('location_<s:property value="%{#partsStatus.index}"/>').setValue('<s:property value="%{supplierPartReturns[0].returnLocation.id}"/>');
						  </s:if>
					  });
					  </script>
			    </td>
                <td width="7%" >
                	<s:select id="carrier_%{#partsStatus.index}" cssStyle="width:65px;"
                	name="selectedCarriers[%{#partsStatus.index}]" list="carriers" listKey="id" listValue="name" value='<s:property value="%{name}"/>'/>
                	<script type="text/javascript">
                 		dojo.addOnLoad(function(){
                 			twms.ajax.fireHtmlRequest("get_carrierDetails.action", {
                    			carrierId: dijit.byId('carrier_<s:property value="%{#partsStatus.index}"/>').getValue()
                    			}, function(data) {
									var carrierInfo = eval(data)[0];
									dojo.byId('shipmentNumber_<s:property value="%{#partsStatus.index}"/>').innerHTML = carrierInfo["carrierCode"];
									delete carrierInfo;
								});
                 		});
                 	</script>
                </td>
                <td width="7%" >
                <span id='shipmentNumber_<s:property value="%{#partsStatus.index}"/>'></span>
                </td >
            </tr>
            <s:set name="partsToBeShownCount" value="#attr['partsToBeShownCount']+1" scope="size"></s:set>
         </s:iterator>

</table>
</div>

        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
             labelNodeClass="section_header" open="true" id="partsAuditHistoryPane">
             <s:push value="recoveryClaim">
                <jsp:include flush="true" page="../../claims/forms/common/read/partsAuditHistory.jsp"/>
             </s:push>
        </div>

       <div dojoType="twms.widget.TitlePane" title="<s:text name="label.supplier.part.recovery.history" />"
                  labelNodeClass="section_header" open="true" id="partsRecoveryHistoryPane">
                      <jsp:include flush="true" page="partRecoveryAudit.jsp"/>
        </div>

<div dojoType="twms.widget.TitlePane" id="recoveryCommentsPane" title="<s:text name='label.common.recoveryComments'/>" labelNodeClass="section_header">
	<table>
		<tr>
           <td width="30%" class="labelStyle"><s:text name="label.common.externalComments"/>:</td>
           <td width="70%" class="labelNormalTop">
           <s:if test="hasActionErrors()">
           <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
           </s:if>
           <s:else>
              <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments" value=""
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
          </s:else>
          </td>
        </tr>
		<tr>
           <td width="30%" class="labelStyle"><s:text name="label.common.internalComments"/>:</td>
           <td width="70%" class="labelNormalTop">
           <s:if test="hasActionErrors()">
           <t:textarea rows="4" cols="105" name="recoveryClaim.comments"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
           </s:if>
           <s:else>
              <t:textarea rows="4" cols="105" name="recoveryClaim.comments" value=""
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
          </s:else>
          </td>
        </tr>
        <tr>
           <td width="30%" class="labelStyle"><s:text name="label.shippingCommentsToDealer"/>:</td>
           <td width="70%" class="labelNormalTop">
           <s:if test="hasActionErrors()">
           <s:textarea rows="4" cols="105" name="recoveryClaim.partReturnCommentsToDealer"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" id="shippingCommentsToDealer" disabled="true" />
           </s:if>
           <s:else>
              <s:textarea rows="4" cols="105" name="recoveryClaim.partReturnCommentsToDealer" value=""
              	wrap="physical" cssClass="bodyText"  maxLength="4000" id="shippingCommentsToDealer" disabled="true" />
           </s:else>
          </td>
        </tr>
	</table>
</div>

	<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
	<div dojoType="twms.widget.TitlePane" id="actionPane" title="<s:text name='label.newClaim.decision'/>" labelNodeClass="section_header">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<jsp:include page="../../admin/supplier/actions.jsp"></jsp:include>
		</table>
	</div>
<div class="buttonWrapperPrimary"><input type="button"
    value="<s:text name="cancel"/>" class="buttonGeneric" onclick="closeCurrentTab();" />
    <input type="button" value="<s:text name="submit"/>"
    class="buttonGeneric" onclick="submitForm('<s:property value="taskName"/>');" /></div>
    </authz:ifNotPermitted>

<div align="center">
		<a id="recoveryClaimPrint" href="#"><s:text name="link.print.recovery.claim"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("recoveryClaimPrint"), "onclick", function(event){
					var recoveryClaimId = '<s:property value="recoveryClaim"/>';
					var thisTabLabel = getMyTabLabel();
                    parent.publishEvent("/tab/open", {
					                    label: "Print Recovery Claim",
					                    url: "printRecoveryClaim.action?recoveryClaimId="+recoveryClaimId,
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});
		</script>
</div>

<div align="center">
		<a id="recoveryClaimPrintSupplierView" href="#"><s:text name="link.print.recovery.claim.supplier.view"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("recoveryClaimPrintSupplierView"), "onclick", function(event){
					var recoveryClaimId = '<s:property value="recoveryClaim"/>';
					var thisTabLabel = getMyTabLabel();
                    parent.publishEvent("/tab/open", {
					                    label: "Print Recovery Claim",
					                    url: "printRecoveryClaimForSupplier.action?supplierView=true&recoveryClaimId="+recoveryClaimId,
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});
		</script>
</div>

