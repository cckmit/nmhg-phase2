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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
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

function submitForm(button) {
		var frm =document.getElementById('supplierRecoveryForm');    
    	var taskName=dojo.byId("taskName").value;
    	if(taskName == 'New')
    	{
    		frm.action='supplierRecovery_submit.action';    		
    	}
    	else 
    	{
    		frm.action='supplierRecoveryDisputed_submit.action';
    	}
    	frm.submit();
        
    
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

<div dojoType="twms.widget.TitlePane" id="recoveredComponentsPane" title="<s:text name='title.supplier.recoveredComponents'/>" labelNodeClass="section_header">

<table cellspacing="0" cellpadding="0" class="grid borderForTable">
    <tr>
        <td  class="colHeaderTop" style="word-wrap:break-word"><s:text name="columnTitle.duePartsInspection.supplier_part_no"/></td>
        <td width="5%" class="colHeaderTop"><s:text name="columnTitle.common.quantity"/></td>
        <td class="colHeaderTop"><s:text name="columnTitle.common.description"/></td>
        <td width="5%" class="colHeaderTop"><s:text name="label.recovery.return"/></td>
        <td  class="colHeaderTop"><s:text name="label.recovery.contract"/></td>
        <td width="10%" class="colHeaderTop"><s:text name="columnTitle.partShipperPartsShipped.tracking_number"/></td>
        <td  class="colHeaderTop"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
        <td  class="colHeaderTop"><s:text name="columnTitle.common.returnlocation"/></td>
        <td  class="colHeaderTop"><s:text name="columnTitle.duePartsReceipt.carrier"/></td>
        <td  class="colHeaderTop"><s:text name="label.recoveryClaim.ShipmentAccountNumber"/></td>
    </tr>
    <s:set name="partsToBeShownCount" value="0" scope="page"></s:set>
    <s:set name="supplier_id" value="%{recoveryClaim.contract.supplier.id}" />
    <s:iterator id="recoverablePart" 
        value="recoveryClaim.recoveryClaimInfo.recoverableParts"
        status="partsStatus">
        <s:set id="oEMPartReplaced" value="#recoverablePart.oemPart" name="oEMPartReplaced"/>
            <tr>
                <td width="10%"><s:property value="supplierItem.number"/></td>
                <td width="5%" ><s:property
                    value="#recoverablePart.quantity" /></td>
                <td width="10%" ><s:property
                    value="#oEMPartReplaced.itemReference.unserializedItem.description" /></td>
                <td width="5%" align="center">
               		<s:if test="!supplierPartReturnModificationAllowed" >
                		<s:checkbox   disabled="true"
                			name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded"
                				id="val_%{#partsStatus.index}"/>
                		<script type="text/javascript">	
                			dojo.addOnLoad(function(){
                				dijit.byId("carrier_<s:property value="%{#partsStatus.index}"/>").setDisabled(true);
								dijit.byId("location_<s:property value="%{#partsStatus.index}"/>").setDisabled(true);
								dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").disabled= true;
                			});
                		</script>
                		
                	</s:if>
                <s:else>
                	<s:checkbox 
                		name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded"
                			id="val_%{#partsStatus.index}"/>
                <s:hidden name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].supplierReturnNeeded" id="hide_%{#partsStatus.index}" />
                <s:hidden name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].initiatedBySupplier" id="supplier_init_%{#partsStatus.index}" />
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
                 if( !isChecked){
                    dojo.byId("supplier_init_<s:property value="%{#partsStatus.index}"/>").value="false";
                }else{
                     dojo.byId("supplier_init_<s:property value="%{#partsStatus.index}"/>").value="true";
                     }
                 	dojo.connect(dojo.byId("val_<s:property value="%{#partsStatus.index}"/>"),"onclick",function(){
                 		toggleEnableDisableShippingComments();
                 		isChecked = dojo.byId("val_<s:property value="%{#partsStatus.index}"/>").checked;
                 		if( !isChecked){
                 			dojo.byId("hide_<s:property value="%{#partsStatus.index}"/>").value="false";
                 			dojo.byId("supplier_init_<s:property value="%{#partsStatus.index}"/>").value="false";
                 			if(dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_partToBeReturned")){
                                dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_partToBeReturned").value="false";
                             }
                            if(dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_isReturnDirectlyToSupplier")){
                                dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_isReturnDirectlyToSupplier").value="false";
                            }
                 		}else{
                 		     dojo.byId("supplier_init_<s:property value="%{#partsStatus.index}"/>").value="true";
                                 if(dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_partToBeReturned")){
                                    dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_partToBeReturned").value="true";
                                 }
                 		        if(dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_isReturnDirectlyToSupplier")){
                 		            dojo.byId("hid_revPart_<s:property value="%{#partsStatus.index}"/>_isReturnDirectlyToSupplier").value="true";
                 		        }
                 		     }
                 		dijit.byId("carrier_<s:property value="%{#partsStatus.index}"/>").setDisabled(! isChecked);
						dijit.byId("location_<s:property value="%{#partsStatus.index}"/>").setDisabled(!isChecked);
						if(!isChecked){  
							dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").disabled= true;
						}else{
							dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").removeAttribute("disabled");
						}         		
                 	});
                 		dijit.byId("carrier_<s:property value="%{#partsStatus.index}"/>").setDisabled(! isChecked);
						dijit.byId("location_<s:property value="%{#partsStatus.index}"/>").setDisabled(!isChecked);
						if(!isChecked){  
							dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").disabled= true;
						}else{
							dojo.byId("rgaNumber_<s:property value="%{#partsStatus.index}"/>").removeAttribute("disabled");
						}
                 });
                </script>
                </s:else>
                </td>
                <td width="20%" ><s:property
                    value="recoveryClaim.contract.name"/>
                   </td>
                    
                    <td width="10%" cssStyle="width:65px;"><s:if test="!supplierPartReturns.isEmpty() && supplierPartReturns[0].supplierShipment!=null"><s:property 
                   value="supplierPartReturns[0].supplierShipment.trackingId"/></s:if></td>
                   <s:if test="!supplierPartReturns.isEmpty() && supplierPartReturns[0].rgaNumber!=null">
	                   <td width="10%" ><s:textfield size="6" id="rgaNumber_%{#partsStatus.index}" value="%{supplierPartReturns[0].rgaNumber}" 
	                   name="rgaNumbersProvided[%{#partsStatus.index}]"/>
	                   </td>
                   </s:if>
                   <s:elseif test="oemPart.returnDirectlyToSupplier || oemPart.partToBeReturned">
                     <td width="10%" ><s:textfield size="6" id="rgaNumber_%{#partsStatus.index}" value="%{oemPart.partReturns[0].rmaNumber}" 
	                   name="rgaNumbersProvided[%{#partsStatus.index}]"/>
                   </s:elseif>
                   <s:else>
                   <s:property value="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].oemPart"/>
                   	   <td width="10%" ><s:textfield size="6" id="rgaNumber_%{#partsStatus.index}" 
	                   name="rgaNumbersProvided[%{#partsStatus.index}]"/>
                   </s:else>
                    
                   <td width="15%" >


				    <%-- <sd:autocompleter id="location_%{#partsStatus.index}" name="selectedLocations[%{#partsStatus.index}]" keyName='selectedLocations[%{#partsStatus.index}]' href='list_shipmentLocations.action'
                                                                                  loadMinimumCount='0' keyValue='%{supplierPartReturns[0].returnLocation.id}' value='%{supplierPartReturns[0].returnLocation.code}'
                                                                                  listenTopics='/supplier/returnLocation/destination/%{#partsStatus.index}'
                                                                                  notifyTopics="/supplier/returnLocation/destination/changed/%{#partsStatus.index}"/> --%>
                  <s:if test="returnThroughDealerDirectly && (!oemPart.partToBeReturned || !oemPart.isPartReturnsPresent())">
                           <s:hidden id="hid_revPart_%{#partsStatus.index}_partToBeReturned" name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].oemPart.partToBeReturned" />
                           <s:hidden id="hid_revPart_%{#partsStatus.index}_isReturnDirectlyToSupplier" name="recoveryClaim.recoveryClaimInfo.recoverableParts[%{#partsStatus.index}].oemPart.returnDirectlyToSupplier" />
                           <s:select id="location_%{#partsStatus.index}" cssStyle="width:100px;"
                               name="supplierReturnLocations[%{#partsStatus.index}]" list="locations" listKey="id" listValue="code" value='<s:property value="%{code}"/>'/>

                  </s:if>
                  <s:else>
                        <s:select id="location_%{#partsStatus.index}" cssStyle="width:100px;"
                                        	       name="selectedLocations[%{#partsStatus.index}]" list="locations" listKey="id" listValue="code" value='<s:property value="%{code}"/>'/>
                  </s:else>
                	
                                     <script type="text/javascript">
                                          dojo.addOnLoad(function() {
                                              var isOnLoad=true;
                                              var inIndex='<s:property value="%{#partsStatus.index}"/>';
                                              var url= "list_supplierLocations.action";
                                              var supplierId = '<s:property value="#supplier_id"/>';
                                              dojo.publish("/supplier/returnLocation/destination/"+inIndex, [{
                                                          url: url,
                                                          params: {
                                                              "supplier": supplierId
                                                          },
                                                          makeLocal: true
                                                      }]);

                                          });
                                     </script>
						<script type="text/javascript">
					  dojo.addOnLoad(function(){
						  <s:if test="#oEMPartReplaced.supplierPartReturn.carrier != null">
						  	dijit.byId('carrier_<s:property value="%{#partsStatus.index}"/>').setValue('<s:property value="%{#oEMPartReplaced.supplierPartReturn.carrier.id}"/>' );
						  </s:if>
						  <s:if test="#oEMPartReplaced.supplierPartReturn.returnLocation != null">
						  	dijit.byId('location_<s:property value="%{#partsStatus.index}"/>').setValue('<s:property value="%{#oEMPartReplaced.supplierPartReturn.returnLocation.id}"/>');
						  </s:if>	
					  });
					  </script>
			    </td>
                <td width="10%" >
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
                <td width="10%" >
                <span id='shipmentNumber_<s:property value="%{#partsStatus.index}"/>'></span>
                </td >
            </tr>
            
            <s:set name="partsToBeShownCount" value="#attr['partsToBeShownCount']+1" scope="size"></s:set>
        </s:iterator>

	 </table>
</div>


<div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
        labelNodeClass="section_header" open="false" id="partsAuditHistoryPane">
        <s:push value="recoveryClaim">
        <jsp:include flush="true" page="../../claims/forms/common/read/partsAuditHistory.jsp"/>
        </s:push>   
</div>
<div dojoType="twms.widget.TitlePane" title="<s:text name="label.supplier.part.recovery.history" />"
                          labelNodeClass="section_header" open="true" id="partsRecoveryHistoryPane">
         <jsp:include flush="true" page="../../supplier/detail/partRecoveryAudit.jsp"/>
</div>
<div id="separator"></div>
<div dojoType="dijit.layout.ContentPane" >
<div dojoType="twms.widget.TitlePane" id="recoveryCommentsPane" title="<s:text name='label.common.recoveryComments'/>" labelNodeClass="section_header">
	<table>
		<tr>  	
           <td width="30%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.comments"/>:</td>
           <td width="70%" class="labelNormalTop">
            <s:if test="hasActionErrors()">
           <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" />
           </s:if>
           <s:else>
              <t:textarea rows="4" cols="105" name="recoveryClaim.externalComments" 
              	wrap="physical" cssClass="bodyText" maxLength="4000" value=""/>
              	</s:else>
          </td>
        </tr>
        <tr>  	
           <td width="30%" class="labelStyle" nowrap="nowrap"><s:text name="label.shippingCommentsToDealer"/>:</td>
           <td width="70%" class="labelNormalTop">
            <s:if test="hasActionErrors()">
           <s:textarea rows="4" cols="105" name="recoveryClaim.partReturnCommentsToDealer"
              	wrap="physical" cssClass="bodyText"  maxLength="4000" id="shippingCommentsToDealer" disabled="true"/>
           </s:if>
           <s:else>
              <s:textarea rows="4" cols="105" name="recoveryClaim.partReturnCommentsToDealer" 
              	wrap="physical" cssClass="bodyText" maxLength="4000" value="" id="shippingCommentsToDealer" disabled="true"/>
           </s:else>
          </td>
        </tr>
	</table>
</div>
</div>
<s:hidden id="taskName" name="taskName"/>
<div id="separator"></div>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<div dojoType="dijit.layout.ContentPane" >
	<div dojoType="twms.widget.TitlePane" id="actionPane" title="<s:text name='label.newClaim.decision'/>" labelNodeClass="section_header">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<jsp:include page="actions.jsp"></jsp:include>
		</table>
	</div>
</div>

<div id="separator"></div>
<div class="buttonWrapperPrimary"><input type="button"
    value="<s:text name="cancel"/>" class="buttonGeneric" onclick="closeCurrentTab();" /> 
    <input type="button" value="<s:text name="submit"/>"
    class="buttonGeneric" onclick="submitForm('<s:property value="taskName"/>');" /></div>
</authz:ifNotPermitted>

<div align="center">
		<a id="recoveryClaimPrint" href="#"><s:text name="link.print.recovery.claim.supplier.view"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("recoveryClaimPrint"), "onclick", function(event){
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
