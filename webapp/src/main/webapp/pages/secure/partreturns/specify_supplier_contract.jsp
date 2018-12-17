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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />

<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
</head>
<SCRIPT type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane"); 

var collateralCovered = new Object();
	collateralCovered[-1] = false; 

 <s:iterator value="contractForCausalPart">
 	collateralCovered['<s:property value="id"/>'] = '<s:property value="collateralDamageToBePaid"/>';
 </s:iterator>
</SCRIPT>

<u:body>
	<u:actionResults />
	<s:form action="suppliercontract_submit" id="form1">
		<s:hidden id="folder" name="folderName" value="%{folderName}" />
		<s:hidden id="taskId" name="id" />
		<s:hidden id="claimObj" name="claim" />
		<s:hidden id= "partLevelflag" name="partLevel"></s:hidden>

		<div dojoType="dijit.layout.ContentPane" label="Claim Info"	style="overflow-x:hidden">
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.partReturnConfiguration.claimDetails"/>"	labelNodeClass="section_header">
		<table cellspacing="0" border="0" cellpadding="0" class="grid">
			<tr>
				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.common.claimNumber" />:</td>
				<td class="labelNormal">
					 <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView">
						<u:openTab
							tabLabel="Claim Number %{claim.number}"
							url="view_search_detail.action?id=%{claim.id}"
							id="claimIdForPart%{claim.id}" cssClass="link"
							decendentOf="%{getText('label.invTransaction.tabHeading')}">
							<s:property value="claim.claimNumber" />
						</u:openTab>
					</authz:ifUserNotInRole>
					<authz:else><s:property value="claim.claimNumber" /></authz:else>
				</td>

				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.common.faultCode" />:</td>
				<td class="labelNormal"><s:property
					value="claim.serviceInformation.faultCode" /></td>
					
				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.common.claimType" />:</td>
				<td class="labelNormal"><s:property
					value="claim.clmTypeName" /></td>
			</tr>
			<tr>
				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.common.serialNumber" />:</td>
				<td class="labelNormal"><s:property
					value="claim.claimedItems.get(0).itemReference.referredInventoryItem.serialNumber" /></td>
				
				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.commom.buildDate" />:</td>
				<td class="labelNormal"><s:property
					value="claim.claimedItems.get(0).itemReference.referredInventoryItem.builtOn" /></td>
				
				<td class="labelStyle" width="20%" nowrap="nowrap"><s:text
					name="label.warrantyAdmin.failureDate" />:</td>
				<td class="labelNormal"><s:property
					value="claim.failureDate" /></td>
				
			</tr>
		</table>
		</div>
		</div>


		<div dojoType="twms.widget.TitlePane" title="<s:text name="columnTitle.duePartsInspection.causalpart"/>"
			labelNodeClass="section_header">
		<table class="grid borderForTable" style="width:97%;">
			<thead>
				<tr class="row_head">
	                <th><s:text name="label.partNumber"/></th>
	                <th><s:text name="label.description"/></th>
	                <th><s:text name="label.supplierRecovery.supplierContract"/></th>
				</tr>
			</thead>
			<tbody>
			   <tr>
	               <td><s:property value="claim.serviceInformation.causalPart.number" /></td>
	               <td><s:property value="claim.serviceInformation.causalPart.description" /></td>    
						<td><s:select cssStyle="width:200px;"
							name="selectedContractForCausalPart" id="causalContractId"
							list="contractForCausalPart" listKey="id" listValue="name"
							value="%{selectedContractForCausalPart.id.toString()}"
							headerKey="-1"
							headerValue="%{getText('label.common.selectHeader')}"
							onchange="check()" /></td>
			   </tr>
			   <tr>
			        <td colspan="3"> <div id="supplier_causal_part_base"></div></td>
			   </tr>
			</tbody>
		</table>
		</div>

		<script type="text/javascript">
    	function check() {
        	var contractId = dojo.byId("causalContractId").value;
        	var nmhgPart = <s:property value = "claim.serviceInformation.causalPart.id" />;
        	var params = {
            	contract:contractId,
            	nmhgPart:nmhgPart
            };
        	if (contractId == "-1") {
        	     dojo.byId("supplier_causal_part_base").innerHTML = "";
            	dojo.query("input, select", dojo.byId("parts")).forEach(function(input) {
                	input.disabled = false;
        		});
        	} else {
        	    twms.ajax.fireHtmlRequest("display_supplier_based_on_contract.action", params, function(data) {
                    dojo.byId("supplier_causal_part_base").innerHTML = data;
                });
	            if (collateralCovered[contractId] == 'true') {
	            	if(null!=dojo.byId("parts")){
	            		dojo.query("input, select", dojo.byId("parts")).forEach(function(input) {
		                	input.disabled = true;
		                	input.value = "";
		        		});
	            	}	            	
	            } else {
	            	dojo.query("input, select", dojo.byId("parts")).forEach(function(input) {
	                	if (input.isCausalPart) {
		                	input.disabled = true;
		                	input.value = "";
	                	} else {
		                	input.disabled = false;
	                	}
	        		});
	            }       
            }             	
        }
   		</script>
		
		<div id="level" align="right" style="width:99%; padding:3px">
	    	<s:if test="partLevel">
	   			<a class="link" onClick="reloadPage(false)">
	   				<s:text	name="toggle.common.quantityLevel" />
	            </a>
	    	</s:if>
	    	<s:else>
				<a class="link" onClick="reloadPage(true)">
					<s:text	name="toggle.common.partLevel" />
				</a>
	    	</s:else>
    	</div>   
    	
    	<script type="text/javascript">
            function reloadPage(isPartLevelView) {
                var form = dojo.byId("dummyForm");
				form.action = "specify_supplier_contract.action?levelChanged=true&partLevel="+isPartLevelView+"&folderName=<s:property value="folderName"/>" +
								"&claim=<s:property value="claim"/>";
                form.submit();
		    };
		</script>


		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.claim.removedParts"/>"
			labelNodeClass="section_header">
			<s:if test="partLevel">
			<table class="grid borderForTable" style="width:97%;" id="parts">
				<thead>
					<tr class="row_head">
						<s:if test="showPartSerialNumber">
							<th><s:text name="label.component.serialNumber" /></th>
						</s:if>						
						<th><s:text name="label.partNumber" /></th>
						<th><s:text name="label.partsInventory.quantity" /></th>
						<th><s:text name="label.description" /></th>
						<th><s:text name="label.inboxView.claimPartReturnStatus" /></th>
						<th><s:text name="label.returnLocation" /></th>
						<th><s:text name="label.supplierRecovery.supplierContract" /></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="completeListOfPartsReplaced" status="currentObj">
						<tr>
							<s:if test="showPartSerialNumber">
								<td><s:property	value="itemReference.referredInventoryItem.serialNumber" /></td>
							</s:if>
							<td><s:property
								value="itemReference.unserializedItem.number" /></td>
							<td><s:property value="numberOfUnits" /></td>
							<td><s:property
								value="itemReference.unserializedItem.description" /></td>
							<td>
								<s:if test="status!=null">
									<s:property value="status.status" />
								</s:if>
								<s:else>Part Not Requested</s:else>
							</td>
							<td>
								<s:property value="getReturnLocationForDisplay(top)" />
							</td>	
							<td><s:select cssStyle="width:200px;"
								name="selectedContractForRemovedPart[%{#currentObj.index}]"
								id="removedContractId_%{#currentObj.index}"
								list="getContractsForRemovedPart(completeListOfPartsReplaced[#currentObj.index])"
								listKey="id" listValue="name" headerKey=""
								headerValue="%{getText('label.common.selectHeader')}"
								value="getSelectedContractForOemPart(completeListOfPartsReplaced[#currentObj.index]).id.toString()"
								onchange="displaySupplier(%{#currentObj.index})"/>
							<SCRIPT type="text/javascript">
								dojo.addOnLoad(function() {
									dojo.byId("removedContractId_<s:property value="#currentObj.index"/>").isCausalPart = 
									<s:property value="%{claim.serviceInformation.causalPart.equals(itemReference.unserializedItem)}" />;
								});
								function displaySupplier(indexId){
								    var contractId_conlevel = dojo.byId("removedContractId_"+indexId).value;
								    var nmhgPart = <s:property value = "itemReference.unserializedItem.id" />;
						        	var params = {
						            	contract:contractId_conlevel,
						            	nmhgPart:nmhgPart
						            };
								    twms.ajax.fireHtmlRequest("display_supplier_based_on_contract.action", params, function(data) {
                                        dojo.byId("supplierDetailsHere_"+indexId).innerHTML = data;
								    });

								}
							</SCRIPT></td>
						</tr>
						<tr>
						    <td colSpan="7"> <div id="supplierDetailsHere_<s:property value='#currentObj.index'/>"></div></td>
						</tr>
					</s:iterator>

				</tbody>
			</table>
		</s:if> 
		<s:else>
			<table class="grid borderForTable" style="width:97%;">
				<thead>
					<tr class="row_head">
						<s:if test="showPartSerialNumber">						
							<th><s:text name="Component Serial Number" /></th>
		                </s:if>
						<th><s:text name="label.partNumber" /></th>
						<th><s:text name="label.description" /></th>
						<th><s:text name="label.inboxView.claimPartReturnStatus" /></th>
						<th><s:text name="label.returnLocation" /></th>
						<th><s:text name="label.supplierRecovery.supplierContract" /></th>
					</tr>
				</thead>
				<tbody>
					<s:set name="partCounter" value="0"/>
					<s:iterator value="completeListOfPartsReplaced" status="currentObj" id="oemPartId">
						<s:bean name="org.apache.struts2.util.Counter" id="counter">
							<s:param name="last" value="%{numberOfUnits}" />
						</s:bean> 
							<s:iterator value="#counter">
								<tr>									
									<s:if test="showPartSerialNumber">						
										<td><s:property value="itemReference.referredInventoryItem.serialNumber"/></td>	            
		                    		</s:if>									
									<td><s:property
										value="itemReference.unserializedItem.number" /></td>
									
									<td><s:property
										value="itemReference.unserializedItem.description" /></td>
									<td>
									<s:if test="partReturnsAtQuantityLevel[#partCounter]!=null">
										<s:property value="partReturnsAtQuantityLevel[#partCounter].status.status" />
									</s:if>
									<s:else>Part Not Requested</s:else>
									</td>
									<td>
										<s:property value="getReturnLocationForDisplay(partReturnsAtQuantityLevel[#partCounter])"/>
									</td>	
									<td><s:select cssStyle="width:200px;"
										name="selectedContractForRemovedPart[%{#partCounter}]"
										id="removedContractId_%{#currentObj.index}_%{#counter.current}"
										list="getContractsForRemovedPart(completeListOfPartsReplaced[#currentObj.index])"
										listKey="id" listValue="name" headerKey=""
										headerValue="%{getText('label.common.selectHeader')}"
										value="selectedContractForRemovedPart[#partCounter].id.toString()"
										onchange="displaySupplier('%{#currentObj.index}', %{#counter.current})"/>
									<SCRIPT type="text/javascript">
									dojo.addOnLoad(function() {
										dojo.byId("removedContractId_<s:property value="#currentObj.index"/>_<s:property value="#counter.current"/>").isCausalPart = 
										<s:property value="%{claim.serviceInformation.causalPart.equals(itemReference.unserializedItem)}" />;
									});
									function displaySupplier(indexId, partCounter){
                                        var contract_Id = dojo.byId("removedContractId_"+indexId+"_"+partCounter).value;
                                        var nmhgPart = <s:property value = "itemReference.unserializedItem.id" />;
                                    	var params = {
                                        	contract:contract_Id,
                                        	nmhgPart:nmhgPart
                                        };
                                        twms.ajax.fireHtmlRequest("display_supplier_based_on_contract.action", params, function(data) {
                                            dojo.byId("supplierDetailsHere_"+indexId+"_"+partCounter).innerHTML = data;
                                        });

                                    }
								</SCRIPT></td>
								</tr>
								<tr>
                                    <td colSpan="7"> <div id="supplierDetailsHere_<s:property value='#currentObj.index'/>_<s:property value='#counter.current'/>"></div></td>
                                </tr>
								<s:set name="partCounter" value="%{#partCounter + 1}"/>
							</s:iterator>
					</s:iterator>
				</tbody>
			</table>
		</s:else></div>



		<div dojoType="dijit.layout.ContentPane" label="Claim Info"
			style="overflow-Y: auto;overflow-x:hidden">
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.supplierRecovery.commentsHistory"/>"
			labelNodeClass="section_header">
		<table class="grid borderForTable" style="width:97%;">
			<thead>
				<tr class="row_head">
					<th><s:text name="label.common.date" /></th>
					<th><s:text name="label.common.user" /></th>
					<th><s:text name="label.common.comments" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="recoveryInfo.comments" status="status">
					<tr>
						<td><s:set name="dateFormat"
							value="@tavant.twms.dateutil.TWMSDateFormatUtil@getDateFormatForLoggedInUser()" />
						<s:date name="madeOn.asJavaUtilDate()" format="%{dateFormat}" />
						</td>
						<td><s:property value="madeBy.completeNameAndLogin" /></td>
						<td><s:property value="comment" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		</div>
		</div>

		<div dojoType="dijit.layout.ContentPane" label="Claim Info"
			style="overflow-Y: auto;overflow-x:hidden">
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.common.comments"/>"
			labelNodeClass="section_header">
		<table width="96%" class="grid">
			<td></td>
			<td></td>
			<tr>
				<td colspan="2" class="labelStyle"><s:text
					name="label.common.externalComments" /> :</td>
				<td width="75%" class="labelNormalTop"><t:textarea rows="3"
					cols="80" name="comments" id="internalCom" wrap="physical"
					cssClass="bodyText" /></td>
			</tr>
		</table>
		</div>
		</div>

		<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
		<div id="submit" align="center">
			<input id="cancel_btn" class="buttonGeneric" type="button"
				value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
			<s:if test="folderName.equals('Pending Recovery Initiation')">
				<input id="submit_btn" class="buttonGeneric" type="submit"
					value="<s:text name='button.common.save'/>" />
				<input id="adjust_recoveryClaims" class="buttonGeneric" type="button"
					value="<s:text name='button.supplierRecovery.adjustRecoveryClaims'/>"
					onclick="submitAdjustRecoveryClaimForm()" />
				<input id="end_recovery_flow" class="buttonGeneric" type="button"
					value="<s:text name='button.supplierRecovery.noRecoveryClaimToBeCreated'/>"
					onclick="endRecoveryFlow()" />
			</s:if>
			<s:else>
				<input id="submit_btn" class="buttonGeneric" type="submit"
					value="<s:text name='button.common.submit'/>" />
			</s:else> 
		</div>
		</authz:ifNotPermitted>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				check();
			});

            function submitAdjustRecoveryClaimForm() {
                var form = dojo.byId("form1");
                form.action = "adjust_recovery_claims.action";
                form.submit();
            };

		    function endRecoveryFlow(){
                var form = dojo.byId("dummyForm");
                var internalComments = "";
                if(document.getElementById("internalCom")){
                internalComments = document.getElementById("internalCom").value;
                document.getElementById("internalComHidden").value = internalComments;
                }
		    	form.action = "end_recovery_flow.action";
                form.submit();
		    };
		</script>
	</s:form>

	<s:form id="dummyForm">
		<s:hidden id="taskId" name="id" />
		<s:hidden name="comments" id="internalComHidden" />
	</s:form>
	
<authz:ifPermitted resource="claimsPendingRecoveryInitiationReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('form1')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('form1'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>