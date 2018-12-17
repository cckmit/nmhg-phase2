
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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%><head>
	<script type="text/javascript" >
		dojo.require("dojox.layout.ContentPane");
		dojo.require("twms.widget.ValidationTextBox");
		dojo.require("twms.widget.NumberTextBox");
        dojo.require("twms.widget.Select");
        dojo.require("twms.data.AutoCompleteReadStore");
</script>
<script type="text/javascript" src="scripts/repeatTable/campaign/RepeatTableTemplate.js"></script>
</head>

<u:stylePicker fileName="batterytestsheet.css" />
<u:stylePicker fileName="official.css" />

<style type="text/css">
  .partReplacedClass{
      border:1px solid #EFEBF7;
	  color:#545454;
  }
  .title td{
  color:#5577B4;
  }
</style>
<script type="text/javascript">
     dojo.addOnLoad(function() {
            connectValueAddButton();
	});
	
	function deleteAllSubSections(rowIndex) {
		 var deleteSpan = dojo.byId("deleteSection_" + rowIndex);
         var collectionName = "campaign.hussPartsToReplace";
         createDeleteHiddenTag(collectionName, deleteSpan, rowIndex);
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = false;         
         dojo.dom.removeNode(dojo.byId("replacedInstalledRowBody_" + rowIndex));
         dojo.dom.removeNode(dojo.byId("multiClaimButton_" + rowIndex));         
         delete deleteSpan;
    }
	
	function deleteReplacedRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_ReplacedParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "campaign.hussPartsToReplace[" + rowIndex
                 + "].removedParts";
         createDeleteHiddenTag(collectionName, deleteSpan, subRowIndex);
         dojo.byId("replacedPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;
         dojo.dom.removeNode(dojo.byId("ReplacedRow_" + rowIndex + "_" + subRowIndex));         
         delete deleteSpan;
    }

    function deleteHussInstalledRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_HussInstallParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "campaign.hussPartsToReplace[" + rowIndex
                 + "].installedParts";
         createDeleteHiddenTag(collectionName, deleteSpan, subRowIndex);
         dojo.byId("installedPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.dom.removeNode(dojo.byId("HussmannInstalledRow_" + rowIndex + "_" + subRowIndex));
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;        
         delete deleteSpan;
	}
	
	function deleteNonHussInstalledRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_NonHussInstallParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "campaign.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].nonHussmanInstalledParts";
         createDeleteHiddenTag(collectionName, deleteSpan, subRowIndex);
         dojo.byId("nonHussInstalledPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.dom.removeNode(dojo.byId("NonHussmannInstalledRow_" + rowIndex + "_" + subRowIndex));
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;         
         delete deleteSpan;
    }

    function createDeleteHiddenTag(collectionName, deleteSpan, rowIndex){
        var completeCollectionName = "__remove." + collectionName;
        var row = getExpectedParent(deleteSpan, "tr");
        var deleter = document.createElement("input");
        deleter.type = "hidden";
        deleter.name = completeCollectionName;
        deleter.id="remove_"+deleteSpan.id;
        deleter.value= collectionName + "[" + rowIndex + "]";
        row.parentNode.appendChild(deleter);
    }

</script>

<script type="text/javascript" >
	function alterRepValue(mainIndex,subIndex){
	    var check=dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned");
	    dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    var location=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_location");
	    var paymentCondition=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_paymentCondition");
	    var dueDays=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_dueDays");
	    if(location && paymentCondition ){
		    if(!check.checked) {
		        location.setDisabled(true);
		        paymentCondition.disabled=true;
	            dueDays.disabled = true;
		     } else {
		        location.setDisabled(false);
		        paymentCondition.disabled=false;
	            dueDays.disabled = false;
		     }
		}
	}
	
	function alterOnLoad(mainIndex,subIndex) {
		var check=dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned");
	    dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    var location=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_location");
	    var paymentCondition=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_paymentCondition");
	    var dueDays=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_dueDays");
	    if(location && paymentCondition ){
	    	if(!check.checked) {
		        location.setDisabled(true);
		        paymentCondition.disabled=true;
	            dueDays.disabled = true;
		     } else {
		        location.setDisabled(false);
		        paymentCondition.disabled=false;
	            dueDays.disabled = false;
		     }
		}
	}
</script>
<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="replacedPartStore" method="post"
             url="list_parts.action?selectedBusinessUnit=<s:property value='%{campaign.businessUnitInfo.name}' />"/>

<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="returnLocationCodeStore" method="post"
             url="list_part_return_Locations.action?selectedBusinessUnit=<s:property value='%{campaign.businessUnitInfo.name}' />" >


<div id="partReplacedInstalledDiv" >
<table width="97%">	
	<s:hidden id="paymentLength" value="%{paymentConditions.size()+1}"/>	
	<s:hidden id="rowIndex" value="%{rowIndex}" />
	<s:hidden id="locale" value="%{defaultLocale}" />	
	<s:hidden id="paymentConditionscode_0" value="" />
	<s:hidden id="paymentConditionsdesc_0" value="" />
	<s:iterator value="paymentConditions" status="paymentStatus" >
		<s:hidden id="paymentConditionscode_%{#paymentStatus.index+1}" value="%{code}" />
		<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index+1}" value="%{description}" />
	</s:iterator>
	<thead>
		<tr class="title" >
            <td width="92%"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></td>
            <td width="8%">
			<div class="repeat_add" id="addRepeatRow"/></td>
        </tr>
	</thead>
	<tbody id="addRepeatBody" >
		<s:if test="campaign.hussPartsToReplace != null && !campaign.hussPartsToReplace.isEmpty()">
			<s:iterator	value="campaign.hussPartsToReplace"
											status="mainIndex">											
				<tr id="replacedInstalledRowBody_<s:property value="%{#mainIndex.index}" />" style="width:98%">
				<td width="100%">
				<div id="replacedInstalledDivBody_<s:property value="%{#mainIndex.index}" />">
				<table width="100%" class="grid" >
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="25%"  nowrap="nowrap"><s:text name="label.claim.removedParts" /></td>
							<td width="10%"/>
                            <td width="20%" />                            
                            <td width="8%" />
                            <td width="15%" ></td>
                            <td width="5%" ></td>
                            <td width="5%">
								<div class='repeat_add' id='addRepeatRow_Replaced_<s:property value="%{#mainIndex.index}" />' 
										onclick="createReplacedWidget(<s:property value="%{#mainIndex.index}" />)"/>
							</td>
						</tr>
						<tr class="row_head">
							<td width="25%" align="center" class="partReplacedClass">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
                               <td width="20%" align="center" class="partReplacedClass">
                                   <s:text name="label.common.description" />
                               </td>
							<td width="8%" align="center" class="partReplacedClass">
								<s:text name="columnTitle.dueParts.return_location" />
							</td>
							<td width="15%" align="center" class="partReplacedClass">
								<s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
							</td>
							<td width="5%" align="center" class="partReplacedClass">
								<s:text name="label.common.dueDays" />
							</td>
                            <td width="10%" align="center"  class="partReplacedClass"></td>
						</tr>
							<s:if test="campaign.hussPartsToReplace[#mainIndex.index].removedParts != null" >
							
								<s:iterator value="campaign.hussPartsToReplace[#mainIndex.index].removedParts" status="subIndex">															
									                                           
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />" subReplacedRowIndex="<s:property value='%{#subIndex.index}' />">
									 <s:hidden
                                            name="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}]"
                                            id="replacedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].id}"/>
                                    <s:hidden
                                            name="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].isHussPartInstalled"
                                            id="replacedPart_%{#mainIndex.index}_%{#subIndex.index}_isHussPartInstalled"
                                            value="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].isHussPartInstalled}"/>
                                            
										<td width="25%" align="center" class="partReplacedClass">
											<sd:autocompleter id='replacedPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true' notifyTopics='/replacedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_parts.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].item' value='%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].item.id}' />

												<script type="text/javascript">
										            dojo.addOnLoad(function(){
										            	var remPartNo = '<s:property value="campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].item.number"/>';
										            	var inIndex='<s:property value="%{#mainIndex.index}"/>';
														var subInc='<s:property value="%{#subIndex.index}"/>';		
														dijit.byId("replacedPartNumber_"+inIndex+"_"+subInc).setDisplayedValue(remPartNo);
														
														dojo.publish("/replacedPart/setIdValue/" + inIndex + "/" + subInc, [{
								                            addItem: {
								                                key: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].item.id}"/>',
								                                label: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].item.number}"/>'
								                            }
								                        }]);
														
			                                            dojo.subscribe("/replacedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {			                                               
			                                                if (type != "valuechanged") {
			                                                    return;
			                                                }
			                                                twms.ajax.fireJavaScriptRequest("getUnserializedOemPartInfo.action", {
			                                                    claimType: 'Campaign',
			                                                    number: dijit.byId("replacedPartNumber_"+inIndex+"_" +subInc).getValue()
			                                                }, function(details) {                                           
			                                    	        	if(navigator.appName == 'Microsoft Internet Explorer') {
			                                    	        		dojo.byId("descriptionSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerText = details[0];
			                                    			    }   
			                                    			    else {
			                                    			    	dojo.byId("descriptionSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
			                                    			    }	
			                                    	        }
			                                               );
			                                            });
										            });
									            </script>																					
										</td>
								   
										
										<td width="10%" align="center" class="partReplacedClass">
												<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
												name="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].noOfUnits" />																							
										</td>
                                        
										<td width="20%" align="center" class="partReplacedClass">
                                               <span id="descriptionSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
                                                   <s:property value="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].item.description"/>
                                               </span>
										</td>
											<td width="8%" align="center" class="partReplacedClass">
													<sd:autocompleter id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location' size='3' href='list_part_return_Locations.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].returnLocation.code}' listenTopics='/partReturn/returnLocation/%{#mainIndex.index}/%{#subIndex.index}' />
											</td>		
							                <script type="text/javascript">
											dojo.addOnLoad(function() {
							                var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                            var subInc='<s:property value="%{#subIndex.index}" />';
                                            dijit.byId("oemRepPart_" + inIndex + "_" +subInc+ "_location").store.includeSearchPrefixParamAlias=false;
							                        dojo.publish("/partReturn/returnLocation/" + inIndex + "/" + subInc, [{
							                            addItem: {
							                                key: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].returnLocation.id}"/>',
							                                label: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].returnLocation.code}"/>'
							                            }
							                        }]);
							                    });
							                </script>
																				
											<td width="15%" align="center" class="partReplacedClass">
												<s:select list="paymentConditions" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition"
												name="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].paymentCondition"
												listKey="code" listValue="description" emptyOption="true"  cssClass="hussmannPartReplaced"
												value="%{campaign.hussPartsToReplace[#mainIndex.index].removedParts[#subIndex.index].paymentCondition}" >
												</s:select>												
											</td>
											<td width="5%" align="center" class="partReplacedClass">
													<s:textfield size="3" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dueDays" 
														name="campaign.hussPartsToReplace[%{#mainIndex.index}].removedParts[%{#subIndex.index}].dueDays" ></s:textfield>
											</td>

                                            <td width="5%" align="center" class="partReplacedClass">
												<div class='repeat_del' id='deleteRepeatRow_ReplacedParts_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='deleteReplacedRow(<s:property value="%{#mainIndex.index}" />,<s:property value="%{#subIndex.index}" />)' />
											</td>
									</tr>								  	
								</s:iterator>
							</s:if>
											
					</tbody>
				</table>
				<table width="100%" >
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="25%"  nowrap="nowrap"><s:text name="label.newClaim.hussmanPartsInstalled" /></td>
							<td width="10%"/>
							<td width="35%"/>
							<td width="20%"/>
							<td width="5%">
								<div class='repeat_add' id='addRepeatRow_HussInstalled_<s:property value="%{#mainIndex.index}" />' 
										onclick="createHussInstalledWidget(<s:property value="%{#mainIndex.index}" />)"/>
							</td>
						</tr>
						<tr class="row_head">
							<td width="25%" align="center" class="partReplacedClass">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<td width="35%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></td>							
							<td width="20%" align="center" class="partReplacedClass">Shipped By OEM</td>
							<td width="10%" align="center"  class="partReplacedClass"></td>
						</tr>
						<s:if test="campaign.hussPartsToReplace[#mainIndex.index].installedParts != null">
							<s:iterator	value="campaign.hussPartsToReplace[#mainIndex.index].installedParts"
													status="subIndex">						                                            
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	subHussInstallRowIndex="<s:property value='%{#subIndex.index}'/>">
																<s:hidden
                                            name="campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}]"
                                            id="installedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].id}"/>
                                            
								<s:hidden
								            name="campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}].isHussPartInstalled"
                                            id="installedPart_%{#mainIndex.index}_%{#subIndex.index}_isHussPartInstalled"
                                            value="%{campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].isHussPartInstalled}"/>
                                            
									<td width="25%" align="center" class="partReplacedClass">
											<sd:autocompleter id='installedHussmanPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true' notifyTopics='/installedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_parts.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}].item' value='%{campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].item.id}' />
                                                <script type="text/javascript">
									            dojo.addOnLoad(function(){
									            	var instPartNo = '<s:property value="campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].item.number"/>';
									            	var inIndex='<s:property value="%{#mainIndex.index}"/>';
													var subInc='<s:property value="%{#subIndex.index}"/>';
													dijit.byId("installedHussmanPartNumber_"+inIndex+"_"+subInc).setDisplayedValue(instPartNo);
													
													dojo.publish("/installedPart/setIdValue/" + inIndex + "/" + subInc, [{
							                            addItem: {
							                                key: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].item.id}"/>',
							                                label: '<s:property value ="%{campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].item.number}"/>'
							                            }
							                        }]);
													
		                                            dojo.subscribe("/installedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {		                                               
		                                                if (type != "valuechanged") {
		                                                    return;
		                                                }
		                                                twms.ajax.fireJavaScriptRequest("getUnserializedOemPartInfo.action", {
		                                                    claimType: 'Campaign',
		                                                    number: dijit.byId("installedHussmanPartNumber_"+inIndex+"_" +subInc).getValue()
		                                                }, function(details) {                                           
		                                    	        	if(navigator.appName == 'Microsoft Internet Explorer') {
		                                    	        		dojo.byId("descriptionInstalledSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerText = details[0];
		                                    			    }   
		                                    			    else {
		                                    			    	dojo.byId("descriptionInstalledSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
		                                    			    }	
		                                    	        }
		                                              );
		                                            });
									            });
								            </script>																						
									</td>
									<td width="10%" align="center" class="partReplacedClass">
											<s:textfield id="installedHussmanQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
											name="campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}].noOfUnits" />
									</td>
									<td width="35%" align="center" class="partReplacedClass">
										<span id="descriptionInstalledSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">                
						                    <s:property value="campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}].item.description"/>
						                </span>
				                    </td>
				                    <td width="20%" align="center" class="partReplacedClass">
				                     <input type="checkbox" id="installedShippedByOEM_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>" 
				                     name="campaign.hussPartsToReplace[<s:property value='%{#mainIndex.index}'/>].installedParts[<s:property value='%{#subIndex.index}'/>].shippedByOem"
				                      value="true" />
				                     <script type="text/javascript">
							            dojo.addOnLoad(function(){
							            	var installedShippedByOEM = '<s:property value="campaign.hussPartsToReplace[#mainIndex.index].installedParts[#subIndex.index].shippedByOem.booleanValue()"/>';				            								            								            	
							            	var inIndex="<s:property value="%{#mainIndex.index}"/>";
											var subIndex="<s:property value="%{#subIndex.index}"/>";
							            	if(installedShippedByOEM == 'true' && dojo.byId("installedShippedByOEM_"+inIndex+"_"+subIndex))							            	
							            		dojo.byId("installedShippedByOEM_"+inIndex+"_"+subIndex).checked = 'checked';							                								     							            	
							            });
						            </script>				                      							                     
				                      											                      				                      	                     
				                     </td>                       
									<td width="5%" align="center" class="partReplacedClass">
										<div class='repeat_del' id='deleteRepeatRow_HussInstallParts_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='deleteHussInstalledRow(<s:property value="%{#mainIndex.index}" />,<s:property value="%{#subIndex.index}" />)' />
									</td>
								</tr>								                           	
							</s:iterator>
						</s:if>
					</tbody>
				</table>
				
				</div>
				</td>
				<td width="2%">
                    <div id="deleteSection_<s:property value='%{#mainIndex.index}' />" class="repeat_del" onclick="deleteAllSubSections(<s:property value='%{#mainIndex.index}' />)"></div>
				</td>
				</tr>
                <s:hidden
                            name="campaign.hussPartsToReplace[%{#mainIndex.index}]"
                            id="hussmanPartsReplacedInstalled_%{#mainIndex.index}"
                            value="%{campaign.hussPartsToReplace[#mainIndex.index].id}"/>
			</s:iterator>
		</s:if>		
	</tbody>
</table>
</div>
<script type="text/javascript">
    function attachInvoice(/*Function*/ callback) {
    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
}
</script>
</div>
</div>
