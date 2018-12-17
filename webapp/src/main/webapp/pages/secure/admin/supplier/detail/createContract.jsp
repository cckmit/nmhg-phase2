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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <style type="text/css">
        td.label {
            color: #545454;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 9pt;
            font-style: normal;
            font-weight: normal;
            line-height: 20px;
            padding: 5px;
            text-align: left;
            vertical-align: top;
        }
    </style>


    <script type="text/javascript">
        function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
        function submitForm() {
            document.forms[0].submit();
        }
		var supplierIdForCurrency = '<s:property value="contract.supplier.id"/>';
    </script>

    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.ValidationTextBox");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });        
        
    </script>
</head>

<u:body>

	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; background: white; overflow-y: auto;">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
			<s:form action="contract_submit" theme="twms" name="contractForm" validate="true" method="post" id="baseFormId">
					<s:if test="hasWarning">
						<div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
							<h4 class="twmsActionResultActionHead">
								<s:text name="label.common.warnings" />
							</h4>
							<ol>
								<s:text name="label.claim.part" />
								<s:property value="duplicateItemName" />
								<s:text name="label.supplierReview.contractPresent" />
								<s:property value="prevContractName" />
								.
								<s:text name="label.common.clickSubmit" />
							</ol>
							<hr />
						</div>
					</s:if>
					<u:actionResults />

					<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="background: #F3FBFE; overflow-x:scroll; overflow-y:hidden;"">
						<div class="mainTitle">
							<s:text name="label.contractAdmin.contractDetails" />
						</div>
						<div class="borderTable">&nbsp;</div>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top: -10px;">
							<tbody>
								<tr>
									<td nowrap="nowrap" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.contractCode" />:</td>
									<td class="label"><s:if test="%{contract==null || contract.id == null}">
											<input name="contract.id" disabled="true" />
										</s:if> <s:else>
											<s:property value="contract.id" />
											<s:hidden name="contract" value="%{contract.id}" />
										</s:else></td>
									<td class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.contractName" />:</td>
									<td class="label"><s:textfield name="contract.name" value="%{contract.name}" /></td>
								</tr>
								<tr>
									<td nowrap="nowrap" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.supplier" />:</td>
									<td class="label"><s:if test="%{contract == null || contract.supplier ==null || contract.id ==null}">
											<sd:autocompleter name='contract.supplier' href='list_suppliers.action?supplierName=%{searchString}' keyName='contract.supplier'
												loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' key='%{contract.supplier.id}' keyValue='%{contract.supplier.id}'
												value='%{contract.supplier.name}' id='supplierName' indicator='supplier_indicator' />
											<img style="display: none;" id="supplier_indicator" class="indicator" src="image/indicator.gif" alt="Loading..." />
										</s:if> <s:else>
											<s:property value="contract.supplier.name" />
											<s:hidden name="contract.supplier" id="supplierName" value="%{contract.supplier.id}" />
										</s:else></td>
									<td class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.shipmentLocation" />:</td>
									<td class="label"><sd:autocompleter cssStyle="width:120px" name='contract.location' keyName='contract.location' href='list_shipmentLocations.action' loadMinimumCount='0'
											key='%{contract.location.id}' value='%{contract.location.code}' id='locationCode' listenTopics='/supplier/location/queryAddParams' /> <script
											type="text/javascript">
					
                    dojo.addOnLoad(function() {
                        var isOnLoad=true;
                        var url= "list_shipmentLocations.action";

                        var supplierId = '<s:property value="contract.supplier.id"/>';
                        var supplierIdAbsent = (supplierId == '');

                        var supplierName = dijit.byId("supplierName");
                        if(supplierIdAbsent){
	                        dojo.connect(supplierName, "onChange", function(newValue) {
	                        	supplierIdForCurrency = newValue;
	                            dojo.publish("/supplier/location/queryAddParams", [{
	                                url: url,
	                                params: {
	                                    "contract.supplier": newValue 
	                                },
	                                makeLocal: true
	                            }]);
	                        });
	                     }else{
	                     	dojo.publish("/supplier/location/queryAddParams", [{
	                                url: url,
	                                params: {
	                                    "contract.supplier": supplierId
	                                },
	                                makeLocal: true
	                            }]);
	                     }
                    });
                </script></td>
								</tr>
								<tr>
									<td nowrap="nowrap" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.validityFrom" />:</td>
									<td class="label"><sd:datetimepicker name='contract.validityPeriod.fromDate' value='%{contract.validityPeriod.fromDate}' id='contractFromDate' />
									</td>
									<td class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.validityTo" />:</td>
									<td class="label" ><sd:datetimepicker name='contract.validityPeriod.tillDate' value='%{contract.validityPeriod.tillDate}' id='contractToDate' /></td>
								</tr>

								<tr>
									<td nowrap="nowrap" class="labelStyle" nowrap="nowrap" valign="top"><s:text name="label.contractAdmin.coverage" />:</td>
									<td class="label" valign="top" colspan="3">
										<table>
											<tr>
												<td><s:textfield name="contract.coverageConditions[0].units" size="2" /></td>
												<td class="labelStyle">months from <s:select id="coverageConditionSelect" theme="twms" name="contract.coverageConditions[0].comparedWith"
														list='#{"DATE_OF_DELIVERY" : "Date of Delivery/Purchase/Installation" ,
							        "DATE_OF_MANUFACTURE" : "Date of Manufacture"}'>
														<script type="text/javascript">
					        		dojo.addOnLoad(function() {						        	                          		 
					        		dijit.byId("coverageConditionSelect").onChange = function(value) {	            	
					            	if(value=="DATE_OF_DELIVERY")
					            		{
					            		 dojo.html.show(dojo.byId("dodMsg"));
					            		 dojo.html.hide(dojo.byId("domMsg"));
					            		}
					            	else
					            		{
					            		 dojo.html.show(dojo.byId("domMsg"));
					            		 dojo.html.hide(dojo.byId("dodMsg"));
					            		}				            	
					           		 };
									});
    							</script>
													</s:select> <span id="dodMsg" tabindex="0"> <img src="image/warning.gif" width="15" height="15" />
												</span> <span id="domMsg" tabindex="0"> <img src="image/warning.gif" width="15" height="15" />
												</span> <span dojoType="dijit.Tooltip" connectId="dodMsg" label='<s:text name="label.contract.coverageConditionDetailsForDOD"/>' /> <span
													dojoType="dijit.Tooltip" connectId="domMsg" label='<s:text name="label.contract.coverageConditionDetailsForDOM"/>' />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td nowrap="nowrap" class="labelStyle" nowrap="nowrap" valign="top"><s:text name="label.common.hoursCovered" />:</td>
									<td nowrap="nowrap" class="labelStyle"><s:hidden name="contract.coverageConditions[1].comparedWith" value="ENERGY_UNITS" /> <s:textfield
											name="contract.coverageConditions[1].units" size="8" /></td>

								</tr>
								<tr>
									<td colspan="4" id="coverageConditionDetails" style="display: none; border: 5px;"><s:text name="label.contract.coverageConditionDetails" /></td>
								</tr>
								<tr>
									<td width="18%" class="labelStyle" nowrap="nowrap" title='<s:text name="label.contractAdmin.isPhysicalShipmentNeeded.tootip"/>'
										style="cursor: default;"><s:text name="label.contractAdmin.isPhysicalShipmentNeeded" /></td>
									<td width="32%" class="label" align="center"><s:checkbox cssStyle="border: 0Px" name="contract.physicalShipmentRequired"
											value="%{contract.physicalShipmentRequired}" fieldValue="true" /></td>
									<td width="10%" class="labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier" />:</td>
								 	<td width="25%" class="widgetTopAlign"><s:select cssStyle="width:90px" name="contract.carrier" list="carriers" listKey="id" id="carrierList" listValue="name" /> <script
											type="text/javascript">
						dojo.addOnLoad(function() {
						var carrier="";
						<s:if test="contract.carrier.name!=null">
							carrier='<s:property value="contract.carrier"/>';
							dijit.byId("carrierList").setValue(carrier);
						</s:if>
						});
						</script></td> 
								</tr>
								<tr>
									<td width="18%" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.processorReviewNeeded" />:</td>
									<td width="32%" class="label" align="center"><s:checkbox cssStyle="border: 0Px" name="contract.sraReviewRequired"
											value="%{contract.sraReviewRequired}" id="processorReviewNeeded" /></td>

									<td width="10%" class="labelStyle" nowrap="nowrap"><s:text name="label.recoveryClaim.ShipmentAccountNumber" /></td>
									<td width="10%"><s:textfield name="contract.carrierAccount" /></td>
								</tr>
								<tr>
									<td width="18%" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.autodebitEnabled" /></td>
									<td width="32%" class="label" align="center"><s:checkbox cssStyle="border: 0Px" name="contract.autoDebitEnabled"
											value="%{contract.autoDebitEnabled}" /></td>

									<td width="10%" class="labelStyle" nowrap="nowrap"><s:text name="label.supplierRecovery.recoveryBasedOn" />:</td>
									<td width="25%" class="widgetTopAlign"><s:select cssStyle="width:120px" theme="twms" name="contract.recoveryBasedOnCausalPart"
											list="#{'true' : 'label.common.causalPart' , 'false' : 'label.claim.removedParts'}" id="isCausalOrRemovedParts" listKey="key"
											listValue="%{getText(value)}" onchange="showOtherSupplierParts()" value="%{contract.recoveryBasedOnCausalPart.toString()}">
										</s:select></td>
								</tr>
								<tr>
									<td width="18%" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.supplierDisputePeriod" />:</td>
									<td width="32%" class="label"><s:textfield name="contract.supplierDisputePeriod" value="%{contract.supplierDisputePeriod}" size="5" /> <s:text
											name="label.contractAdmin.days"></s:text></td>
									<td width="18%" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.supplierResponsePeriod" />:</td>
									<td width="32%" class="label"><s:textfield name="contract.supplierResponsePeriod" value="%{contract.supplierResponsePeriod}" size="5" /> <s:text
											name="label.contractAdmin.days"></s:text></td>

								</tr>
								<tr>
									<td width="18%" class="labelStyle" nowrap="nowrap"><s:text name="label.contractAdmin.offlineDebit" /></td>
									<td width="32%" class="label" align="center"><s:hidden name="contract.offlineDebitEnabled" value="true" /> <s:checkbox cssStyle="border: 0Px"
											name="contract.offlineDebitEnabled" value="true" disabled="true" /></td>
									<td width="10%" class="labelStyle" nowrap="nowrap"><s:text name="label.partReturn.RmaNo" />:</td>
									<td><s:textfield name="contract.rmaNumber" /></td>
								</tr>
							</tbody>
						</table>
						<table>
							<tbody>
								<tr>								
                                   <td  width="18%" class="labelStyle" nowrap="nowrap"><label class="labelStyle"><s:text name="label.partReturn.shippingInstruction"/>:</label></td>
                                   <td></td><td></td><td></td>
  			                     	<td><s:textarea cssClass="textarea" name="contract.shippingInstruction" cols="50" rows="4"/></td>
								</tr>
								</tbody>
					</table>

					</div>
					<br>
					<div class="mainTitle">
						<s:text name="label.contractAdmin.recoveryInitiation" />
					</div>
					<br>
					<div class="labelStyle">
						<s:text name="label.contractAdmin.whenToInitiateRecovery" />
					</div>
					<br>
					<table border="0" width="100%">					
					<s:if test="!displayOnAcceptAndOnVendorReviewResponsibilitybutton()"> 
						<tr>
							<td class="labelNormal" width="2%">&nbsp;</td>
							<td class="labelNormal" width="5%"><input name="contract.whenToInitiateRecoveryClaim" type="radio" style="border: 0px" value="On Accept"
								id="initiateSupplierRecoveryOnClaimAccept" /></td>
							<td><s:text name="label.contractAdmin.initiateRecoveryOnClaimAccept" />
						</tr>
						</s:if>
						
						 <s:if test="displayOnAcceptAndOnVendorReviewResponsibilitybutton()"> 
						<tr>
							<td></td>
							<td class="labelNormal"><input name="contract.whenToInitiateRecoveryClaim" type="radio" style="border: 0px"
								id="initiateSupplierRecoveryOnReviewResponsibilityIsVendor" value="On Accept" /></td>
							<td><s:text name="label.contractAdmin.initiateSupplierRecoveryOnAcceptAndReviewResponsibilityIsVendor" /></td>
						</tr>
						</s:if>
						<tr>
							<td></td>
							<td class="labelNormal"><input name="contract.whenToInitiateRecoveryClaim" type="radio" style="border: 0px"
								id="initiateSupplierRecoveryOnClaimSubmit" value="On Submit" /></td>
							<td><s:text name="label.contractAdmin.initiateRecoveryOnClaimSubmit" /></td>
						</tr>
						
						<tr>
							<td></td>
							<td class="labelNormal"><input name="contract.whenToInitiateRecoveryClaim" type="radio" style="border: 0px" id="doNotAutoInitiateRecovery"
								value="Manual" /></td>
							<td><s:text name="label.contractAdmin.doNotAutoInitiateRecovery" /></td>
						</tr>
					</table>
				
					<br>
					<script type="text/javascript">
		dojo.addOnLoad(function(){
			if('<s:property value="contract.whenToInitiateRecoveryClaim"/>' == 'On Submit'){
				dojo.byId("initiateSupplierRecoveryOnClaimSubmit").checked="checked";
			}		
			else if('<s:property value="contract.whenToInitiateRecoveryClaim"/>' == 'On Accept' && dojo.byId("initiateSupplierRecoveryOnReviewResponsibilityIsVendor")){
				dojo.byId("initiateSupplierRecoveryOnReviewResponsibilityIsVendor").checked="checked";
			}
			else if('<s:property value="contract.whenToInitiateRecoveryClaim"/>' == 'On Accept'&& dojo.byId("initiateSupplierRecoveryOnClaimAccept")){
				dojo.byId("initiateSupplierRecoveryOnClaimAccept").checked="checked";
			}
			else{
				dojo.byId("doNotAutoInitiateRecovery").checked = "checked";
			}
			
			<s:if test="contract == null">
				dojo.byId("processorReviewNeeded").checked = "checked";
			</s:if>
		});
	</script>
					<jsp:include page="compensationTerms.jsp" />
					<br>
					<jsp:include flush="true" page="itemsCovered.jsp" />
					<s:hidden name="hasWarning" id="warning" />
				

			</s:form>
			</div>
		</div>
<authz:ifPermitted resource="contractAdminMaintainSupplierContractsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>