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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%><head>
	<script type="text/javascript" >
		dojo.require("dojox.layout.ContentPane");
		dojo.require("twms.widget.ValidationTextBox");
		dojo.require("twms.widget.NumberTextBox");
        dojo.require("twms.widget.Select");
        dojo.require("twms.data.AutoCompleteReadStore");
</script>

<script type="text/javascript" src="scripts/repeatTable/RepeatTableTemplate2.js"></script>
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
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled";
         createDeleteHiddenTag(collectionName, deleteSpan);
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = false;
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).value = "null";
         dojo.dom.removeNode(dojo.byId("replacedInstalledRowBody_" + rowIndex));
         dojo.dom.removeNode(dojo.byId("multiClaimButton_" + rowIndex));
         //delete dojo.byId("replacedInstalledRowBody_" + rowIndex);
         delete deleteSpan;
    }
	
	function deleteReplacedRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_ReplacedParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].replacedParts";
         createDeleteHiddenTag(collectionName, deleteSpan);
         dojo.byId("replacedPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;
         dojo.dom.removeNode(dojo.byId("ReplacedRow_" + rowIndex + "_" + subRowIndex));
         //delete dojo.byId("ReplacedRow_" + rowIndex + "_" + subRowIndex);
         delete deleteSpan;
    }

    function deleteHussInstalledRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_HussInstallParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].hussmanInstalledParts";
         createDeleteHiddenTag(collectionName, deleteSpan);
         dojo.byId("installedPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.dom.removeNode(dojo.byId("HussmannInstalledRow_" + rowIndex + "_" + subRowIndex));
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;
         //delete dojo.byId("HussmannInstalledRow_" + rowIndex + "_" + subRowIndex);
         delete deleteSpan;
	}
	
	function deleteNonHussInstalledRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_NonHussInstallParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].nonHussmanInstalledParts";
         createDeleteHiddenTag(collectionName, deleteSpan);
         dojo.byId("nonHussInstalledPart_" + rowIndex + "_" + subRowIndex).value = "null";
         dojo.dom.removeNode(dojo.byId("NonHussmannInstalledRow_" + rowIndex + "_" + subRowIndex));
         dojo.byId("hussmanPartsReplacedInstalled_" + rowIndex).disabled = true;
         //delete dojo.byId("NonHussmannInstalledRow_" + rowIndex + "_" + subRowIndex);
         delete deleteSpan;
    }

    function createDeleteHiddenTag(collectionName,deleteSpan){
        var completeCollectionName = "__remove." + collectionName;
        var row = getExpectedParent(deleteSpan, "tr");
        var deleter = document.createElement("input");
        deleter.type = "hidden";
        deleter.name = completeCollectionName;
        deleter.id="remove_"+deleteSpan.id;
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
	    if(location && paymentCondition){
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
	
	function alterOnLoad(mainIndex,subIndex,disabled) {
		var check=dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned");
	    dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    var location=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_location");
	    var paymentCondition=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_paymentCondition");
	    var dueDays=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_dueDays");
	    if(disabled){
	    	dojo.byId("hid_oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    }
	    if(!disabled && location && paymentCondition ){
	    	if(!check.checked) {		    	   	
		        location.setDisabled(true);		       	        
		        paymentCondition.setDisabled(true);
	            dueDays.disabled = true;
		     } else {			    
		        location.setDisabled(false);
		        paymentCondition.setDisabled(false);
	            dueDays.disabled = false;
		     }
		}
	}
</script>
<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="replacedPartStore" method="post"
             url="list_oem_part_itemnos.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>"/>

<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="returnLocationCodeStore" method="post"
             url="list_part_return_Locations.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>" >

<div id="partReplacedInstalledDiv">
<table width="97%">
	<s:hidden id="processorReview" name="processorReview" value ="%{isProcessorReview()}" />
	<s:hidden id="multipleClaim" name="task.claim.forMultipleItems" value="%{task.claim.forMultipleItems}"></s:hidden>
	<s:hidden id="paymentLength" value="%{paymentConditions.size()}"/>
	<s:hidden id="claimId" value="%{task.claim.id}" />
	<s:hidden id="rowIndex" value="%{rowIndex}" />
	<s:hidden id="nonOEMPartsInstalledLabel" />
	<script type="text/javascript">
		dojo.byId("nonOEMPartsInstalledLabel").value = '<s:text name="label.newClaim.nonHussmanPartsInstalled" />';
	</script>
	<s:hidden id="partsReplacedInstalledVisibleId" name="partsReplacedInstalledSectionVisible"></s:hidden>		
	<s:iterator value="paymentConditions" status="paymentStatus" >
		<s:hidden id="paymentConditionscode_%{#paymentStatus.index}" value="%{code}" />
		<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index}" value="%{description}" />
	</s:iterator>
	<thead>
		<tr class="title">
            <td width="92%"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></td>
            <td width="8%">
			<div class="repeat_add" id="addRepeatRow"/></td>
        </tr>
	</thead>
	<tbody id="addRepeatBody" >
		<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null && !task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.isEmpty()">
			<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
						<s:iterator value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="status">
   							  <s:hidden name="initialOEMReplacedParts[%{#mainIndex.index}]" value="%{id}"/>
 						</s:iterator>
				<s:set name="readOnlyForParts" value="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].readOnly" />							
				<tr id="multiClaimButton_<s:property value='%{#mainIndex.index}' />" style="width:98%">
                <td><table>
                    <s:if test="task.claim.forMultipleItems">
						<tr>
							<s:if test="inventoryLevel">
								<script type="text/javascript" >		
									dojo.addOnLoad(function() {
										var index = "<s:property value='#mainIndex.index' />";
										dojo.byId("inventory_Inventory"+index).checked=true;
										dojo.byId("inventory_Claim"+index).checked=false;
									});
								</script>
							</s:if>
							<s:elseif test="!inventoryLevel">
								<script type="text/javascript" >		
									dojo.addOnLoad(function() {
										var index = "<s:property value='#mainIndex.index' />";
										dojo.byId("inventory_Claim"+index).checked=true;
										dojo.byId("inventory_Inventory"+index).checked=false;
									});
								</script>
							</s:elseif>
							<td>
								<input type="radio"  value="true" id="inventory_Inventory<s:property value='%{#mainIndex.index}' />" 
								onclick="checkInventoryLevel(<s:property value='%{#mainIndex.index}' />)"
								name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}'/>].inventoryLevel"/>
          						<s:text name="accordion_jsp.accordionPane.inventory"/>
          					</td>
						</tr>
						<tr>
							<td >
								<input type="radio"  value="false" id="inventory_Claim<s:property value='%{#mainIndex.index}' />"
								onclick="checkClaimLevel(<s:property value='%{#mainIndex.index}' />)"
								name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}'/>].inventoryLevel"/>
          						<s:text name="claim.prieview.ContentPane.claim"/>
          					</td>
						</tr>
						<script type="text/javascript" >
								function checkInventoryLevel(mainIndex){
									var inventoryLevel = dojo.byId("inventory_Inventory"+mainIndex);
									var claimLevel = dojo.byId("inventory_Claim"+mainIndex);
									inventoryLevel.checked=true;
									claimLevel.checked=false;									
								}
								
								function checkClaimLevel(mainIndex){
									var inventoryLevel = dojo.byId("inventory_Inventory"+mainIndex);
									var claimLevel = dojo.byId("inventory_Claim"+mainIndex);
									inventoryLevel.checked=false;
									claimLevel.checked=true;
								}
						</script>
					</s:if>
				</table></td></tr>
				<tr id="replacedInstalledRowBody_<s:property value="%{#mainIndex.index}" />" style="width:98%">
				<td width="97%">
				<div id="replacedInstalledDivBody_<s:property value="%{#mainIndex.index}" />">
				<table width="100%" >
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="25%" nowrap="nowrap" ><s:text name="label.claim.removedParts" /></td>
							<td width="10%"/>
                            <s:if test="processorReview">
                                <td width="20%" />
                                <td width="5%" />
                                <td width="10%" />
                                <td width="20%" />                                
                                <td width="5%" >
                                </td>
                            </s:if>
                            <s:else>
                                <td width="60%" />
                            </s:else>

                            <td width="5%">
								<div class='repeat_add' id='addRepeatRow_Replaced_<s:property value="%{#mainIndex.index}" />' 
										onclick="createReplacedWidget(<s:property value="%{#mainIndex.index}" />)"/> </div>
							</td>
						</tr>
						<tr class="row_head">
							<td width="25%" align="center" class="partReplacedClass"  nowrap="nowrap">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.dateCode" />
							</td>
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<s:if test="%{isProcessorReview()}">
                                <td width="20%" align="center" class="partReplacedClass">
                                    <s:text name="label.common.description" />
                                </td>
							    <td width="5%" align="center" class="partReplacedClass">
									<s:text name="label.partReturn.markPartForReturn" />
								</td>
								<td width="10%" align="center" class="partReplacedClass">
									<s:text name="columnTitle.dueParts.return_location" />
								</td>
								<td width="20%" align="center" class="partReplacedClass">
									<s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
								</td>
								<td width="5%" align="center" class="partReplacedClass">
									<s:text name="label.common.dueDays" />
								</td>
							</s:if>
                            <s:else>
                                <td width="60%" align="center" class="partReplacedClass">
                                    <s:text name="label.common.description" />
                                </td>
                            </s:else>
                            <td width="10%" align="center"  class="partReplacedClass"></td>
						</tr>
							<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null" >
								<s:iterator value="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">
									<script type="text/javascript">
                                        dojo.addOnLoad(function() {
                                            var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                            var subInc='<s:property value="%{#subIndex.index}"/>';
                                            dojo.subscribe("/replacedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJavaScriptRequest("getUnserializedOemPartDetails.action", {
                                                    claimNumber: dojo.byId("claimId").value,
                                                    number: number
                                                }, function(details) {
                                                    dojo.byId("descriptionSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
                                                }
                                                        );
                                            });
                                        });
                                    </script>
                                    <s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}]"
                                            id="replacedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].id}"/>
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />" subReplacedRowIndex="<s:property value='%{#subIndex.index}' />">
										<td width="25%" align="center" class="partReplacedClass">
										   <s:if test="isPartShippedOrCannotBeShipped()">
										   <sd:autocompleter id='replacedPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' disabled='true' required='true' notifyTopics='/replacedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.alternateNumber}' />
										   </s:if>
										   <s:else>
											<sd:autocompleter id='replacedPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true' notifyTopics='/replacedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.alternateNumber}' />
										      </s:else>
										</td>
										<td width="10%" align="center" class="partReplacedClass">
											<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="6"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].dateCode" />
										</td>
										<td width="10%" align="center" class="partReplacedClass">
											<s:if test="isPartShippedOrCannotBeShipped()">
												<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" disabled="true" size="3"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits" />
												
												<s:hidden
name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits"
value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].numberOfUnits}"/>
												
											</s:if>
											<s:else>
												<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits" />
											</s:else>
										</td>
                                        <s:if test="processorReview">
											<td width="20%" align="center" class="partReplacedClass">
                                                <span id="descriptionSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
                                                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem.description"/>
                                                </span>
											</td>
                                            <td width="5%" align="center" class="partReplacedClass">
												<s:if test= "(numberOfUnits<=0 ) ||
													(isPartShippedOrCannotBeShipped() )" >
										            <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"
										              	 onclick="alterRepValue(%{#mainIndex.index},%{#subIndex.index});"/>
										            <s:hidden id="hid_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
										        </s:if>
										        <s:else>
										        	<s:checkbox id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"
										                onclick="alterOnLoad(%{#mainIndex.index},%{#subIndex.index});"/>
												</s:else>
											</td>											
											<td width="10%" align="center" class="partReplacedClass">
												<s:if test="isPartShippedOrCannotBeShipped()">
										        <sd:autocompleter id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location' size='3' disabled='true' href='list_part_return_Locations.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.code}' listenTopics='/partReturn/returnLocation/%{#mainIndex.index}/%{#subIndex.index}' />
									            </s:if>
									            <s:else>
													<sd:autocompleter id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location' size='3' href='list_part_return_Locations.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.code}' listenTopics='/partReturn/returnLocation/%{#mainIndex.index}/%{#subIndex.index}' />
													</s:else>	
										          <script type="text/javascript">
														dojo.addOnLoad(function() {
										                var inIndex='<s:property value="%{#mainIndex.index}"/>';
			                                            var subInc='<s:property value="%{#subIndex.index}" />';
			                                            dijit.byId("oemRepPart_" + inIndex + "_" +subInc+ "_location").store.includeSearchPrefixParamAlias=false;
										                        dojo.publish("/partReturn/returnLocation/" + inIndex + "/" + subInc, [{
										                            addItem: {
										                                key: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.id}"/>',
										                                label: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.code}"/>'
										                            }
										                        }]);
										                    });
							               			 </script>
											</td>
											<td width="20%" align="center" class="partReplacedClass">
												<s:if test="isPartShippedOrCannotBeShipped()">
													<s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.description}"/>
													<s:hidden id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.paymentCondition"
																value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.code}"></s:hidden>
												</s:if>
												<s:else>
													<s:select list="paymentConditions" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition"
														name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.paymentCondition"
														listKey="code" listValue="description" emptyOption="false"
														value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.code}" />												
												</s:else>
											</td>
											<td width="5%" align="center" class="partReplacedClass">
												<s:if test="isPartShippedOrCannotBeShipped()">
													<s:textfield size="3" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dueDays"  disabled="true"
														name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDays" ></s:textfield>
												</s:if> 
												<s:else >
													<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].isDueDaysReadOnly()">
														<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDate}" />
													</s:if> 
													<s:else >
														<s:hidden name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDaysReadOnly" value="false"/>
														<s:textfield size="3" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dueDays"
															name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDays"></s:textfield>
													</s:else>
												</s:else>
											</td>
											<script type="text/javascript">
									        	dojo.addOnLoad(function() {
									            	alterOnLoad(<s:property value='%{#mainIndex.index}' />,<s:property value='%{#subIndex.index}' />,<s:property value="isPartShippedOrCannotBeShipped()"/>);
												});
										    </script>
											</s:if>
                                            <s:else>
                                                <td width="60%" align="center" class="partReplacedClass">
                                                <span id="descriptionSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
                                                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem.description"/>
                                                </span>
                                            </td>
                                            </s:else>
											<s:if test="!isPartShippedOrCannotBeShipped()">
                                            <td width="10%" align="center" class="partReplacedClass">
												<div class='repeat_del' id='deleteRepeatRow_ReplacedParts_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='deleteReplacedRow(<s:property value="%{#mainIndex.index}" />,<s:property value="%{#subIndex.index}" />)' />
											</td>
											</s:if>
											<script type="text/javascript">
												dojo.addOnLoad(function() {
													var inIndex='<s:property value="%{#mainIndex.index}"/>';
													var subIndex='<s:property value="%{#subIndex.index}"/>';
													var readOnly = '<s:property value="#readOnlyForParts"/>';
													
													if(dojo.byId("deleteRepeatRow_ReplacedParts_"+inIndex+"_"+subIndex) == null){
														dojo.html.hide(dojo.byId("deleteSection_"+inIndex));
													}
													if(readOnly=="true"){
														if(dojo.byId("addRepeatRow_Replaced_"+inIndex) != null){
															dojo.html.hide(dojo.byId("addRepeatRow_Replaced_"+inIndex));
														}
														if(dojo.byId("deleteRepeatRow_ReplacedParts_"+inIndex+"_"+subIndex) != null){
															dojo.html.hide(dojo.byId("deleteRepeatRow_ReplacedParts_"+inIndex+"_"+subIndex));
														}
														if(dojo.byId("replacedPartNumber_"+inIndex+"_"+subIndex) != null){
															dojo.byId("replacedPartNumber_"+inIndex+"_"+subIndex).readOnly=true;
														}
														if(dojo.byId("replacedQuantity_"+inIndex+"_"+subIndex) != null){
															dojo.byId("replacedQuantity_"+inIndex+"_"+subIndex).readOnly=true;
														}
														if(dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_toBeReturned") != null){
															dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_toBeReturned").readOnly=true;
														}
														if(dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_location") != null){
															dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_location").readOnly=true;
														}
														if(dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_paymentCondition") != null){
															dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_paymentCondition").readOnly=true;
														}
														if(dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_dueDays") != null){
															dojo.byId("oemRepPart_"+inIndex+"_"+subIndex+"_dueDays").readOnly=true;
														}
													}	
												});
										 </script>
									</tr>
								</s:iterator>
							</s:if>
											
					</tbody>
				</table>
				<table width="100%" >
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="25%"  nowrap="nowrap" ><s:text name="label.newClaim.hussmanPartsInstalled" /></td>
							<td width="10%"/>
							<td width="60%"/>
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
								<s:text name="label.common.dateCode" />
							</td>
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<td width="53%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></td>
							<td width="10%" align="center"  class="partReplacedClass"></td>
						</tr>
						<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null">
							<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">
								<s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}]"
                                            id="installedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].id}"/>
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	subHussInstallRowIndex="<s:property value='%{#subIndex.index}' />">
									<td width="25%" align="center" class="partReplacedClass">
											<sd:autocompleter id='installedHussmanPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true' notifyTopics='/installedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.alternateNumber}' />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
											<s:textfield id="installedHussmanQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="6"
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].dateCode" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
											<s:textfield id="installedHussmanQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].numberOfUnits" />
									</td>
									<td width="53%" align="center" class="partReplacedClass">
										<span id="descriptionInstalledSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">                
						                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item.description"/>
						                </span>
				                    </td>
									<td width="10%" align="center" class="partReplacedClass">
										<div class='repeat_del' id='deleteRepeatRow_HussInstallParts_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='deleteHussInstalledRow(<s:property value="%{#mainIndex.index}" />,<s:property value="%{#subIndex.index}" />)' />
									</td>
								</tr>
								<script type="text/javascript">
                                        dojo.addOnLoad(function() {
                                            var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                            var subInc='<s:property value="%{#subIndex.index}"/>';
                                            dojo.subscribe("/installedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJavaScriptRequest("getUnserializedOemPartDetails.action", {
                                                    claimNumber: dojo.byId("claimId").value,
                                                    number: number
                                                }, function(details) {
                                                    dojo.byId("descriptionInstalledSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
                                                }
                                                        );
                                            });
                                        });
                                 </script>		
                                 <script type="text/javascript">
										dojo.addOnLoad(function() {
											var inIndex='<s:property value="%{#mainIndex.index}"/>';
											var subIndex='<s:property value="%{#subIndex.index}"/>';
											var readOnly = '<s:property value="#readOnlyForParts"/>';
											if(readOnly=="true"){
												if(dojo.byId("addRepeatRow_HussInstalled_"+inIndex) != null){
													dojo.html.hide(dojo.byId("addRepeatRow_HussInstalled_"+inIndex));
												}
												if(dojo.byId("deleteRepeatRow_HussInstallParts_"+inIndex+"_"+subIndex) != null){
													dojo.html.hide(dojo.byId("deleteRepeatRow_HussInstallParts_"+inIndex+"_"+subIndex));
												}
												if(dojo.byId("installedHussmanPartNumber_"+inIndex+"_"+subIndex) != null){
													dojo.byId("installedHussmanPartNumber_"+inIndex+"_"+subIndex).readOnly=true;
												}
												if(dojo.byId("installedHussmanQuantity_"+inIndex+"_"+subIndex) != null){
													dojo.byId("installedHussmanQuantity_"+inIndex+"_"+subIndex).readOnly=true;
												}
											}	
										});
								 </script>
							</s:iterator>
						</s:if>
					</tbody>
				</table>		
				<table width="100%">
					<tbody id='addRepeatBody_NonHussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="25%"  nowrap="nowrap"><s:text name="label.newClaim.nonHussmanPartsInstalled" /></td>
							<td width="10%"/>
							<td width="20%"/>
							<td width="15%"/>
							<td width="20%"/>
							<td width="5%">
								<div class='repeat_add' id='addRepeatRow_NonHussInstalled_<s:property value="%{#mainIndex.index}" />' 
										onclick="createNonHussInstalledWidget(<s:property value="%{#mainIndex.index}" />)"/>
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
							<td width="10%" align="center" class="partReplacedClass">
								<s:text name="label.common.price" />
							</td>
							<td width="25%" align="center" class="partReplacedClass">
								<s:text name="label.newClaim.invoice"/>
							</td>
							<td width="10%" align="center"  class="partReplacedClass"></td>
						</tr>
						<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts != null">
							<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts"
													status="subIndex">
								<s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].nonHussmanInstalledParts[%{#subIndex.index}]"
                                            id="nonHussInstalledPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].id}"/>
								<tr id="NonHussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	subNonHussInstallRowIndex="<s:property value='%{#subIndex.index}'/>">
									<td width="25%" align="center" class="partReplacedClass">
											<input dojoType="twms.widget.ValidationTextBox" id='installedNonHussmanPartNumber_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />'
											trim="true" hasDownArrow="false" 
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}' />].nonHussmanInstalledParts[<s:property value='%{#subIndex.index}' />].partNumber"
											value="<s:property value='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].partNumber'/>" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
											<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].nonHussmanInstalledParts[%{#subIndex.index}].numberOfUnits" />
									</td>
									<td width="20%" align="center" class="partReplacedClass">
										<input dojoType="twms.widget.ValidationTextBox"
										required="true" trim="true" 
										name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}' />].nonHussmanInstalledParts[<s:property value='%{#subIndex.index}' />].description"
										value="<s:property value='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].description'/>" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<t:money id='installedNonHussmanPrice_%{#mainIndex.index}_%{#subIndex.index}' size="3"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].nonHussmanInstalledParts[%{#subIndex.index}].pricePerUnit"
												value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].pricePerUnit}" 
												defaultSymbol="%{task.claim.forDealer.preferredCurrency.symbol}" />
									</td>
									<s:hidden name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].nonHussmanInstalledParts[%{#subIndex.index}].invoice"									
                							id='hiddenNonOemInvoice_%{#mainIndex.index}_%{#subIndex.index}'/>             					
									<td width="15%" align="center" class="partReplacedClass">
										<a href="#" id='invoice_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />'><s:text name="label.newClaim.attachInvoice"/></a>
						                <a id='downloadInvoice_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />' href="#">
						                	<span class="documentName"><s:property value="invoice.fileName"/></span>							                						                    
						                    <img class="dropUpload" src="image/remove.gif" id="deleteForUpload_#index"/>
						                </a>
						                <script type="text/javascript">						                	
						                    dojo.addOnLoad(function() {
						                    	var mainIndex='<s:property value="%{#mainIndex.index}"/>';
												var subIndex='<s:property value="%{#subIndex.index}"/>';
												var downloadLink = dojo.byId("downloadInvoice_"+mainIndex+"_"+subIndex);
						                        var attachInvoiceLink = dojo.byId("invoice_"+mainIndex+"_"+subIndex);
						                        var attachedInvoiceId = dojo.byId("hiddenNonOemInvoice_"+mainIndex+"_"+subIndex);
						                        dojo.html.hide(downloadLink);
						                        <s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].invoice != null">						                        						                        	
						                            showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId,
						                                    <s:property value="invoice.id"/>, "<s:property value="invoice.fileName"/>");
						                        </s:if>
												initValues(downloadLink, attachInvoiceLink, attachedInvoiceId);
						                    });
						                </script>
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<div class='repeat_del' id='deleteRepeatRow_NonHussInstallParts_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='deleteNonHussInstalledRow(<s:property value="%{#mainIndex.index}" />,<s:property value="%{#subIndex.index}" />)' />
									</td>
								</tr>		
							</s:iterator>
							<script type="text/javascript">
								dojo.addOnLoad(function() {
									var inIndex='<s:property value="%{#mainIndex.index}"/>';
									var readOnly = '<s:property value="#readOnlyForParts"/>';
									if(readOnly=="true"){
										if(dojo.byId("addRepeatRow_NonHussInstalled_"+inIndex) != null){
											dojo.html.hide(dojo.byId("addRepeatRow_NonHussInstalled_"+inIndex));
										}
									}	
								});
						 </script>
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
                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}]"
                            id="hussmanPartsReplacedInstalled_%{#mainIndex.index}"
                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].id}"/>
				<script type="text/javascript">
						dojo.addOnLoad(function() {
							var inIndex='<s:property value="%{#mainIndex.index}"/>';
							var readOnly = '<s:property value="#readOnlyForParts"/>';
							if(readOnly=="true"){
								if(dojo.byId("deleteSection_"+inIndex) != null){
									dojo.html.hide(dojo.byId("deleteSection_"+inIndex));
								}
							}	
						});
				 </script>
			</s:iterator>
		</s:if>
	</tbody>
</table>
</div>
<script type="text/javascript">
	function initValues(downloadLink, attachInvoiceLink, attachedInvoiceId) {
		//alert('HI');
	    dojo.connect(_getUploadDropButton(downloadLink), "onclick", function() {
	        dropAttachedInvoice(attachInvoiceLink, downloadLink, attachedInvoiceId);
	    });
	    dojo.connect(attachInvoiceLink, "onclick", function() {
	    	//alert('HIHI');
	        attachInvoice(function(doc) {							                            
	            showFileDownloadLink(attachInvoiceLink, downloadLink, attachedInvoiceId, doc[0].id, doc[0].name);
	        });
	    });
	    dojo.connect(_getFileHolder(downloadLink), "onclick", function() {
	        getFileDownloader().download(downloadLink.url);
	    });		
	}
	function attachInvoice(/*Function*/ callback) {
	    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
	}
	function showFileDownloadLink(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
	                              /*domNode [input type="hidden"]*/ attachedInvoiceId, /*Long*/docId, /*String*/fileName) {
	    //alert('downloadLink ::'+downloadLink);
	    dojo.html.hide(attachInvoiceLink);
	    if(downloadLink){
            _getFileHolder(downloadLink).innerHTML = fileName;
            downloadLink.url = "downloadDocument.action?docId=" + docId;
            attachedInvoiceId.value=docId;
            dojo.html.show(downloadLink);
	    }
	    if(fileName.length==0){
	        dojo.html.hide(downloadLink);
	        dojo.html.show(attachInvoiceLink);
	    }
	}
	function dropAttachedInvoice(/*domNode (span)*/ attachInvoiceLink, /*domNode (span)*/ downloadLink,
	                             /*domNode [input type="hidden"]*/ attachedInvoiceId) {
	    downloadLink.url="";
	    attachedInvoiceId.value = "";
	    dojo.html.hide(downloadLink);
	    dojo.html.show(attachInvoiceLink);
	}
	function _getFileHolder(downloadLink) {
	    return getElementByClass("documentName", downloadLink);
	}
	function _getUploadDropButton(downloadLink) {
	    return getElementByClass("dropUpload", downloadLink);
	}
</script>
<script type="text/javascript">
    function attachInvoice(/*Function*/ callback) {
    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
}
</script>
</div>
</div>
