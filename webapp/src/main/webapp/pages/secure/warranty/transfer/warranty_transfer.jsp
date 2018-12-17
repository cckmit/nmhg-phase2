<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><s:text name="summaryTable.inboxButton.warranty_transfer" /></title>

<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="warrantyForm.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="claimForm.css" />
<s:head theme="twms" />

<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script type="text/javascript" src="scripts/WarrantyTransfer.js"></script>
<script type="text/javascript" src="scripts/commonWarranty.js"></script>
<u:stylePicker fileName="warrantyForm.css" />
<%@ include file="/i18N_javascript_vars.jsp"%>
</head>
<body onkeypress="return disableKeyPress(event)">
	<script type="text/javascript">
            dojo.require("dijit.layout.LayoutContainer");
            dojo.require("dijit.layout.ContentPane");
            function disableKeyPress(e)
            {
                var key;
                if (window.event)
                    key = window.event.keyCode; //IE
                else
                    key = e.which; //firefox
                return (key != 13);
            }
        </script>
	<u:body>
		<div dojoType="dijit.layout.LayoutContainer"
			style="background: white;">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
				style="overflow-y: auto">
				<u:actionResults />
				<s:form id="transfer_warranty" action="confirmTransfer" theme="twms"
					validate="true" method="POST">
					<s:hidden name="selectedBusinessUnit" />
					<s:hidden name="allowInventorySelection" />
					<s:hidden name="warranty" value="%{warranty.id}" />
					<s:hidden id="dealerRentalAllowed" value="" />
					<authz:ifUserInRole roles="inventoryAdmin, enterpriseDealership">
						<s:if test="allowInventorySelection">
							<script type="text/javascript"
								src="scripts/warrantyRegForInternalUser.js"></script>
							<script type="text/javascript">
                                    var dealerSelect;
                                    dojo.addOnLoad(function(){

                                    <s:if test="dealerNameSelected">
                                            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
                                            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
                                            dealerSelect = dijit.byId("dealerNameAutoComplete");
                                            dojo.html.hide(dojo.byId("dealerNumberText"));
                                            dojo.html.hide(dojo.byId("toggleToDealerName"));
                                            <s:if test="forDealerSelected">
                                            	dojo.html.hide(dojo.byId("toggleToDealerNumber"));
                                            </s:if>
                                    </s:if>
                                    <s:else>
                                            dojo.html.hide(dijit.byId("dealerNameAutoComplete").domNode);
                                            dijit.byId("dealerNameAutoComplete").setDisabled(true);
                                            dealerSelect = dijit.byId("dealerNumberAutoComplete");
                                            dojo.html.hide(dojo.byId("dealerNameText"));
                                            dojo.html.hide(dojo.byId("toggleToDealerNumber"));
                                    </s:else>
                                    <s:if test="forDealer==null">
                                            dojo.html.hide(dojo.byId("warrantyRegDiv"));
                                    </s:if>
                                    <s:else>
                                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                                    </s:else>
                                    <s:if test="forDealer!=null && warranty.id!=null">
                                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                                            dojo.byId("dealerName").value='<s:property value="forDealer.name"/>';
                                            dojo.byId("dealerId").value = '<s:property value="forDealer.Id"/>';
                                        <s:if test="dealerNameSelected">
                                                isDealerNameSelected = true;
                                                dojo.html.hide(dojo.byId("toggleToDealerNumber"));
                                                dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
                                                dijit.byId("dealerNumberAutoComplete").setDisabled(true);
                                        </s:if>
                                        <s:else>
                                                isDealerNameSelected = false;
                                                dojo.html.hide(dojo.byId("toggleToDealerName"));
                                        </s:else>
                                    </s:if>
                                        });
                                </script>
							<div
								style="background: #F3FBFE; border: 1px solid #EFEBF7; margin: 5px; padding-bottom: 10px;">
								<table class="form" cellpadding="0" cellspacing="0">
									<input type="hidden" name="dealerNameSelected"
										id="dealerNameOrNumberSelected"
										value="<s:property value="dealerNameSelected"/>" />
									<tr style="width: 50%">
										<td id="dealerNameText" class="labelStyle"><s:text
												name="label.common.dealerName" />:</td>
										<td id="dealerNumberText" class="labelStyle"><s:text
												name="label.common.dealerNumber" />:</td>
										<td><s:if test="forDealer!=null && warranty.id!=null">
												<s:if test="dealerNameSelected">
													<s:text name="%{forDealer.name}" />
												</s:if>
												<s:else>
													<s:text name="%{forDealer.dealerNumber}" />
												</s:else>
												<s:hidden name="forDealer" value="%{forDealer.id}"
													id="dealer" />
											</s:if> <s:else>
												<s:if test="forDealerSelected">
													<sd:autocompleter id='dealerNameAutoComplete'
														href='list_warranty_reg_dealer_name_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}'
														name='forDealer' loadOnTextChange='true' readonly='true' cssStyle='width:350px'
														loadMinimumCount='1' showDownArrow='false'
														indicator='indicator' delay='500' key='%{forDealer.id}'
														keyName='forDealer' value='%{forDealer.name}' />
												</s:if>
												<s:else>
													<sd:autocompleter id='dealerNameAutoComplete'
														href='list_warranty_reg_dealer_name_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}'
														name='forDealer' loadOnTextChange='true'  cssStyle='width:350px'
														loadMinimumCount='1' showDownArrow='false'
														indicator='indicator' delay='500' key='%{forDealer.id}'
														keyName='forDealer' value='%{forDealer.name}' />
												</s:else>
												<s:hidden name="forDealerSelected"></s:hidden>
												<img style="display: none;" id="indicator" class="indicator"
													src="image/indicator.gif" alt="Loading..." />
												<sd:autocompleter id='dealerNumberAutoComplete'
													href='list_warranty_reg_dealer_number_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}'
													name='forDealer' loadOnTextChange='true'
													loadMinimumCount='1' showDownArrow='false'
													indicator='indicator' delay='500' key='%{forDealer.id}'
													keyName='forDealer' value='%{forDealer.dealerNumber}' />
												<img style="display: none;" id="indicator" class="indicator"
													src="image/indicator.gif" alt="Loading..." />
												<script type="text/javascript">
                                                        dojo.addOnLoad(function() {
                                                            dojo.byId("dealerId").value = '<s:property value="forDealer.Id"/>';
                                                            var dealerNameAutoCompleter = dijit.byId("dealerNameAutoComplete");
                                                            dealerNameAutoCompleter.fireOnLoadOnChange=false;
                                                            var dealerNumberAutoCompleter = dijit.byId("dealerNumberAutoComplete");
                                                            dealerNumberAutoCompleter.fireOnLoadOnChange=false;
                                                            <s:if test="forDealerSelected">
                                                            dojo.connect(dealerNameAutoCompleter, "onChange", function(value){
                                                            	
                                                            }
                                                            </s:if>
                                                            <s:else>
                                                            dojo.connect(dealerNameAutoCompleter, "onChange", function(value){
                                                                if(!dijit.byId("dealerNameAutoComplete").isValid()){
                                                                    setUiForInvalidDealerName();
                                                                }else{
                                                        <s:if test="forDealer!=null">
                                                                        var dealerName='<s:property value="forDealer.name"/>';
                                                                        if(dealerName!=value){
                                                                            dijit.byId("selectedInventoriesPane").destroyDescendants();
                                                                            dijit.byId("selectedInventoriesPane").setContent("");
                                                                            if(dijit.byId("searchResultsNode"))
                                                                                dijit.byId("searchResultsNode").setContent("");
                                                                            dojo.byId("customerDetailsDiv").innerHTML = "";
                                                                            dojo.byId("dealerNameOrNumberSelected").value=true;
                                                                        }
                                                        </s:if>
                                                        <s:else>
                                                                        dojo.byId("dealerId").value = value;
                                                                        dojo.byId("dealerName").value = dealerSelect.getDisplayedValue();
                                                        </s:else>
                                                                        isDealerNameSelected = true;
                                                        <s:if test="inventoryItem==null">
                                                                        dojo.byId("draft_btn").form.reset();
                                                                        var hiddenList = dojo.query("input[id $= 'inventoryIemSN']");
                                                                        for ( var i = 0; i < hiddenList.length; i++) {
                                                                            hiddenList[i].value = "";
                                                                        }
                                                                        dojo.byId("draft_btn").form.action="show_warranty_transfer.action?forDealer="+value+"&dealerNameSelected=true";
                                                                        dojo.byId("draft_btn").form.submit();
                                                        </s:if>
                                                        <s:else>
                                                                        dojo.html.show(dojo.byId("warrantyRegDiv"));
                                                        </s:else>
                                                                    }
                                                                });
                                                        </s:else>
                                                                dojo.connect(dealerNumberAutoCompleter, "onChange", function(value){
                                                                    if(!dijit.byId("dealerNumberAutoComplete").isValid()){
                                                                        setUiForInvalidDealerNumber();
                                                                    }else{
                                                        <s:if test="forDealer!=null">
                                                                        var dealerName='<s:property value="forDealer.name"/>';
                                                                        if(dealerName!=value){
                                                                            //dijit.byId("selectedInventoriesPane").destroyDescendants();
                                                                            //dijit.byId("selectedInventoriesPane").setContent("");
                                                                            if(dijit.byId("searchResultsNode"))
                                                                                dijit.byId("searchResultsNode").setContent("");
                                                                            dojo.byId("customerDetailsDiv").innerHTML = "";
                                                                            dojo.byId("dealerNameOrNumberSelected").value=true;
                                                                        }
                                                        </s:if>
                                                        <s:else>
                                                                        dojo.byId("dealerId").value = value;
                                                                        dojo.byId("dealerName").value = "";
                                                        </s:else>
                                                                        isDealerNameSelected = false;
                                                        <s:if test="inventoryItem==null">
                                                                        dojo.byId("draft_btn").form.reset();
                                                                        var hiddenList = dojo.query("input[id $= 'inventoryIemSN']");
                                                                        for ( var i = 0; i < hiddenList.length; i++) {
                                                                            hiddenList[i].value = "";
                                                                        }
                                                                        dojo.byId("draft_btn").form.action="show_warranty_transfer.action?forDealer="+value+"&dealerNameSelected=false";
                                                                        dojo.byId("draft_btn").form.submit();
                                                        </s:if>
                                                        <s:else>
                                                                        dojo.html.show(dojo.byId("warrantyRegDiv"));
                                                        </s:else>
                                                                    }
                                                                });
                                                            });
                                                    </script>
											</s:else></td>
									</tr>
								</table>
							</div>
							<div id="toggle" style="cursor: pointer; padding-left: 3px;">
								<div id="toggleToDealerNumber" class="clickable">
									<s:text name="toggle.common.toDealerNumber" />
								</div>
								<div id="toggleToDealerName" class="clickable">
									<s:text name="toggle.common.toDealerName" />
								</div>
							</div>
						</s:if>
						<s:else>
							<input type="hidden" name="forDealer"
								value="<s:property value="forDealer.name"/>" />
							<script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.byId("dealerName").value='<s:property value="forDealer.name"/>';
                                        dojo.byId("dealerId").value = '<s:property value="forDealer.id"/>';
                                    });
                                </script>
						</s:else>
					</authz:ifUserInRole>
					<s:if test="loggedInUserADealer">
						<script type="text/javascript">
                                dojo.addOnLoad(function() {
                                    <s:if test="!loggedInUserAParentDealer">
                                    dojo.byId("dealerName").value="<s:property value="%{loggedInUsersDealership.name}"/>";
                                    dojo.byId("dealerId").value = "<s:property value="%{loggedInUsersDealership.id}"/>";
                                    if(dojo.byId("dealerName").value.indexOf("&amp;") > 0)
                                    {
                                        dojo.byId("dealerName").value = dojo.byId("dealerName").value.replace("&amp;","&");
                                    }
                                    </s:if>
                                });
                               
                            </script>
                              <jsp:include flush="true" page="../parentDealer_selection.jsp"/> 
                          
					<s:elseif test="inventoryItemMappings.size!=0">
						<script type="text/javascript">
                                dojo.addOnLoad(function(){
                                    dojo.byId("dealerName").value="<s:property value="inventoryItemMappings[0].inventoryItem.currentOwner.name"/>";
                                    dojo.byId("dealerId").value = "<s:property value="inventoryItemMappings[0].inventoryItem.currentOwner.id"/>";
                                    if(dojo.byId("dealerName").value.indexOf("&amp;") > 0)
                                    {
                                        dojo.byId("dealerName").value = dojo.byId("dealerName").value.replace("&amp;","&");
                                    }
                                });
                            </script>
					</s:elseif>
</s:if>
<div id="warrantyRegDiv" class="policy_section_div" style="border:0">

					<jsp:include flush="true" page="../warranty_customerinfo.jsp" />
					<%-- <jsp:include flush="true" page="../warranty_operatorinfo.jsp" /> --%>
					<div dojoType="twms.widget.TitlePane"
						title="<s:text name="label.marketingInformation"/>"
						id="marketing_info" labelNodeClass="section_header" open="true">
						<s:if
							test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null ">
							<jsp:include flush="true" page="../warranty_marketinginfo.jsp" />
						</s:if>
					</div>
					<s:if test="isInstallingDealerEnabled()">
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.common.serviceDetails"/>"
							id="servicedetails" labelNodeClass="section_header" open="true">
							<jsp:include flush="true" page="../installingDealer.jsp" />
						</div>
					</s:if>
					 
					<jsp:include flush="true" page="warranty_transfer_machineinfo.jsp" />

					<div class="section_div" style="width: 99.4%">
						<div class="section_heading">
							<s:text name="label.comments" />
						</div>
						<div style="padding: 2px; padding-left: 7px; padding-right: 10px;">
							<t:textarea name="registrationComments" rows="3"
								cssStyle="width:96%;"></t:textarea>
						</div>
					</div>
					<div class="buttonWrapperPrimary">
						<script>
                                        function submitForm(btn,val) {
                                        	 if(dojo.byId('isForPDI')){
                          						dojo.byId('isForPDI').value="false";
												dojo.byId('printPDI').disabled="true";
                                        	 }
                                            dojo.byId('warrantyDraft').value= val;
                                            btn.form.action="save_draft_equipmentTransfer.action";
                                            btn.form.submit();

                                        }
                                    </script>

						<s:if test="confirmTransfer">
							<script type="text/javascript" xml:space="preserve">
                                        dojo.addOnLoad(function() {
                                            dijit.byId("confirmTransfer").show();
                                            dojo.connect(dojo.byId("btnEdit"), "onclick", function() {
                                                dijit.byId("confirmTransfer").hide();
                                                bindOnChangeForTDateHrs();
                                            });
                                        });
                                    </script>
						</s:if>
						<s:if test="pdiGeneration">
								<script type="text/javascript">
			dojo.addOnLoad(function() {
				 var inventoryItem = '<s:property value="inventoryItemMappings[0].inventoryItem"/>';
						var thisTabLabel = getMyTabLabel();
                    	parent.publishEvent("/tab/open", {
					                    label: "Print PDI",
					                    url: "printTransfer.action?forETR=true&inventoryItems[0]="+inventoryItem, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                		});
			</script>
							</s:if>

						<s:hidden name="invTransaction.invTransactionType"
							value="%{transactionType}" />
						<s:hidden name="saveAsDraft" id="warrantyDraft" />

						<div id="submit" align="center">
							<button class="buttonGeneric" id="draft_btn"
								name="warranty.draft" onclick="submitForm(this,'true')">
								<s:text name="button.label.saveAsDraft" />
							</button>
													
							<s:if test="printPdf">
							 <input type="button" class="buttonGeneric" value='<s:text name="button.label.printPDI"/>'
								id="printPDI" />
							<s:hidden name="forPrintPDI" id="isForPDI" />
							<script type="text/javascript">
			dojo.addOnLoad(function() {
				var printPDIButton = dojo.byId("printPDI");
				if(printPDIButton){
					dojo.connect(printPDIButton, "onclick", function(event){
						 if(dojo.byId('isForPDI')){
						dojo.byId('isForPDI').value="true";
					    dojo.byId('warrantyDraft').value = "true";
					    if(dojo.byId('draft_btn')){
						    dojo.byId('draft_btn').disabled="true";
					    }
					    var form = dojo.byId("transfer_warranty");
		                form.action="save_draft_equipmentTransfer.action";
		                form.submit();
						 }
					});
				}
			});	
		</script>
		</s:if>
							<s:submit name="button.label.transfer" cssClass="buttonGeneric" />
						</div>
						
					
					</div>
				</div>
				</s:form>
			</div>
		</div>
		<div dojoType="twms.widget.Dialog" id="confirmTransfer"
			title="<s:text name="label.warranty.confirmTransfer" />"
			closable="false" style="width: 90%; height: 80%">
			<div dojoType="dijit.layout.LayoutContainer"
				style="background: #F3FBFE; height: 450px; overflow: auto;">
				<div dojoType="dojox.layout.ContentPane" layoutAlign="top"
					id="confirmTransferContent">
					<jsp:include page="transfer_warranty_conf.jsp" />
				</div>
				<div dojoType="dojox.layout.ContentPane" layoutAlign="client"
					style="padding: 10px 0 10px 0;">
					<center class="buttons">
						<s:submit value="%{getText('button.common.edit')}" id="btnEdit"
							type="button" />
						<s:submit value="%{getText('button.common.submit')}"
							id="btnSubmit" />
					</center>
				</div>
			</div>
		</div>
		<div style="display: none">
			<div dojoType="twms.widget.Dialog" id="dialogBoxTermsAndCondition"
				title="<s:text name="label.common.termsAndConditions" />">
				<div dojoType="dijit.layout.LayoutContainer"
					style="height: 280px; overflow: auto;">
					<div dojoType="dijit.layout.ContentPane" style="margin: 5px;">

					</div>
				</div>
			</div>
		</div>
	</div>
	</u:body>
</body>
</html>
<script type="text/javascript">
	dojo.addOnLoad (function() {
		<s:if test = "noPolicyForDemoTruckWithMoreThan80Hours()">
			var contractCodeElement = dijit.byId("contractCode");
			if(contractCodeElement){
				contractCodeElement.fireOnLoadOnChange=false;
				dojo.connect(contractCodeElement, "onChange", function() {
				var indexList =  dojo.query("input[id $= 'indexFlag']"); 
				var nameList =  dojo.query("input[id $= 'nameFlag']");
				for(var i=0;i<indexList.length;i++){
					getAllPolicies(indexList[i].value,nameList[i].value);
				}
				});
			}
		</s:if>
	});
</script>