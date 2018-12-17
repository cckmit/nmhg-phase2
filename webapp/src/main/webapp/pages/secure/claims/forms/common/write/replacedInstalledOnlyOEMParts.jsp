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
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript" >
		dojo.require("dojox.layout.ContentPane");
		dojo.require("twms.widget.ValidationTextBox");
		dojo.require("twms.widget.NumberTextBox");
        dojo.require("twms.widget.Select");
        dojo.require("twms.data.AutoCompleteReadStore");
</script>
<style type="text/css">
  .partReplacedClass{
      border:1px solid #EFEBF7;
	  color:#545454;
  }
 .title td{
  color:#5577B4;
  }
  .borderAddClass{border:1px solid #c9d9ea; margin-top:5px;}
  tr.title{background:#DCE9F7;}
  table.tspace{margin-bottom:15px;}
  .borderNoClass{border:0px;}
  .setWidth{width:90px;}
</style>
<script type="text/javascript">

	function deleteAllSubSections(rowIndex) {
		 var deleteSpan = dojo.byId("deleteSection_" + rowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled";
         createDeleteHiddenTag(collectionName, deleteSpan,rowIndex);
         dojo.dom.removeNode(dojo.byId("replacedInstalledRowBody_" + rowIndex));
    	 dojo.query("input[id ^= "+value+"]").forEach(function(node){
			if(node){
				dojo.dom.removeNode(node);
			}

		});
         delete deleteSpan;
    }

	/*function deleteReplacedRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_ReplacedParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].replacedParts";
         createDeleteHiddenTag(collectionName, deleteSpan,subRowIndex);
         dojo.dom.removeNode(dojo.byId("ReplacedRow_" + rowIndex + "_" + subRowIndex));
         delete deleteSpan;
    }  */

    function deleteHussInstalledRow(rowIndex,subRowIndex) {
         var deleteSpan = dojo.byId("deleteRepeatRow_HussInstallParts_" + rowIndex + "_" + subRowIndex);
         var collectionName = "task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[" + rowIndex
                 + "].hussmanInstalledParts";
         createDeleteHiddenTag(collectionName, deleteSpan,subRowIndex);
         dojo.dom.removeNode(dojo.byId("HussmannInstalledRow_" + rowIndex + "_" + subRowIndex));
         delete deleteSpan;
	}

    function createDeleteHiddenTag(collectionName,deleteSpan,rowIndex){
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
	    var returnToOemCheck = dojo.byId("oem_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier");
	    var returnToSupplierCheck = dojo.byId("supplier_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier");
	    if(location && paymentCondition ){
		    if(!check.checked) {
		        location.setDisabled(true);
		        paymentCondition.disabled=true;
	            dueDays.disabled = true;
	            if(returnToOemCheck){
	            returnToOemCheck.disabled = true;
	            }
	            if(returnToSupplierCheck){
	                returnToSupplierCheck.disabled = true;
	            }
		     } else {
		     if(!returnToOemCheck.checked && (returnToSupplierCheck && !returnToSupplierCheck.checked)){
		         returnToOemCheck.checked = true;
		     }
		        location.setDisabled(false);
		        paymentCondition.disabled=false;
	            dueDays.disabled = false;
	            if(returnToOemCheck){
	            returnToOemCheck.disabled = false;
	            }
	            if(returnToSupplierCheck){
                    returnToSupplierCheck.disabled = false;
                }
		     }
		}
	}
	
	function toggleEnableDisableShippingComments(){
		if(dojo.byId("shippingCommentsToDealer")){
			dojo.byId("shippingCommentsToDealer").disabled = true;
			<s:iterator value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="mainList">
				<s:iterator value = "replacedParts" status = "subList">
					var mainIndex = <s:property value="#mainList.index" />;
					var subIndex = <s:property value="#subList.index" />;
					if(dojo.byId("shippingCommentsToDealer").disabled){
						if(dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned")
								&& dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").checked){
									dojo.byId("shippingCommentsToDealer").disabled = false;
						}
					}
				</s:iterator>
			</s:iterator>
		}
	}
	
	function alterOnLoad(mainIndex,subIndex,disabled) {
		toggleEnableDisableShippingComments();
		var check=dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned");
	    dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    var location=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_location");
	    var paymentCondition=dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_paymentCondition");
	    var dueDays=document.getElementById("oemRepPart_"+mainIndex+"_"+subIndex+"_dueDays");
	    var returnToOemCheck = dojo.byId("oem_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier");
        var returnToSupplierCheck = dojo.byId("supplier_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier");
        var rmaNumber = dojo.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_rma");
        var dealerLocation = dijit.byId("oemRepPart_"+mainIndex+"_"+subIndex+"_dealerPickUpLocation");
        var supplierRetLoc = dijit.byId("returnloc_"+mainIndex+"_"+subIndex);
	    if(disabled){
	    	dojo.byId("hid_oemRepPart_"+mainIndex+"_"+subIndex+"_toBeReturned").value=check.checked;
	    }
	     if(returnToSupplierCheck){
             if(returnToSupplierCheck.checked){
                 dojo.byId("hid_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=returnToSupplierCheck.checked;
             }
         } else{
             dojo.byId("hid_oemRepPart_"+mainIndex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=false;
         }
	    if(!disabled && location && paymentCondition){
	    	if(!check.checked) {
                    location.setDisabled(true);
                    paymentCondition.setDisabled(true);
                    dueDays.disabled = true;
                    returnToOemCheck.disabled = true;
                    if(returnToSupplierCheck){
                        returnToSupplierCheck.disabled = true;
                    }
                    rmaNumber.disabled=true;
                    dealerLocation.setDisabled(true);
                    if(supplierRetLoc){
                        supplierRetLoc.disabled=true;
                    }

		    } else {
		        location.setDisabled(false);
		        paymentCondition.setDisabled(false);
	            dueDays.disabled = false;
	            returnToOemCheck.disabled = false;
	            if(returnToSupplierCheck){
                    returnToSupplierCheck.disabled = false;
                }else{
                    returnToOemCheck.disabled = true;
                }
                rmaNumber.disabled = false;
                dealerLocation.setDisabled(false);
                if(supplierRetLoc){
                    supplierRetLoc.disabled=false;
                }
			}
		}
	}
</script>

<s:if test="isPartShippedOrCannotBeShipped()">
	<s:set name="isDisabled" value="true" />
</s:if>
<s:else>
	<s:set name="isDisabled" value="false" />
</s:else>
<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="replacedPartStore" method="post"
             url="list_oem_part_itemnos.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>">
</div>


<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="installedPartStore" method="post"
             url="list_oem_servicepart_itemnos.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>">
</div>
<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="returnLocationCodeStore" method="post"
             url="list_part_return_Locations.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>">
</div>

<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="replacedPartSerialNumber" method="post"
             url="list_serialized_replacedParts.action?selectedBusinessUnit=<s:property value='%{selectedBusinessUnit}'/>&claimedItem=<s:property value='%{task.claim.itemReference.referredInventoryItem.id}'/>&serializedPart=<s:property value='%{task.claim.partItemReference.referredInventoryItem.id}'/>&isPartsClaim=<s:property value='%{task.partsClaim}'/>&claimId=<s:property value='%{task.claim.id}'/>">
</div>


<div id="partReplacedInstalledDiv">
<table width="97%">
	<s:hidden id="processorReview" name="processorReview" value ="%{isProcessorReview()}" />
	<s:hidden id="showPartSerialNumber" name="showPartSerialNumber" value ="%{isShowPartSerialNumber()}" />
	<s:hidden id="multipleClaim" name="task.claim.forMultipleItems" value="%{task.claim.forMultipleItems}"></s:hidden>
	<s:hidden id="paymentLength" value="%{paymentConditions.size()}"/>
	<s:hidden id="claimId" value="%{task.claim.id}" />
	<s:hidden id="rowIndex" value="%{rowIndex}" />
	<s:hidden id="partsReplacedInstalledVisibleId" name="partsReplacedInstalledSectionVisible"></s:hidden>
	<s:hidden id="taskId" value="%{task.task.id}" />
	<s:iterator value="paymentConditions" status="paymentStatus" >
		<s:hidden id="paymentConditionscode_%{#paymentStatus.index}" value="%{code}" />
		<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index}" value="%{description}" />
	</s:iterator>
	<thead>
		<tr class="title">
            <td width="90%" colspan="2">
            	<div style="float:left;"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></div>
            </td>
        </tr>
	</thead>
	<tbody id="addRepeatBody">
	<script type="text/javascript">
	   dojo.addOnLoad(function() {
		   if(dojo.byId("continueDiv")){
           	dojo.html.hide(dojo.byId("continueDiv"));
           }

	   });
	</script>

	 <s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null && !task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.isEmpty()">
			<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
				<s:iterator value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="status">
					 <s:hidden name="initialOEMReplacedParts[%{#mainIndex.index}]" value="%{id}"/>
				</s:iterator>
				<s:set name="readOnlyForParts" value="true" />

				<tr id="replacedInstalledRowBody_<s:property value="%{#mainIndex.index}" />">
				<td style="width:97%">
				<div style="width:100%" id="replacedInstalledDivBody_<s:property value="%{#mainIndex.index}" />">
				<div id="partQuantitySpecifiedPerError" align="center" >
				<s:if test="%{task.claim.forMultipleItems}">
					 <s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.empty ||
                                       task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].inventoryLevel==null">
						<s:text name="label.newClaim.partQuantitySpecifiedPer"/>&nbsp;
						<input type="radio" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_inventoryLevel_<s:property value="%{#mainIndex.index}" />" value="true"/>&nbsp;
						<s:text name="label.newClaim.partQuantiySpecifiedPer.inventory"/>
						&nbsp;&nbsp;
						<input type="radio" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_claimLevel_<s:property value="%{#mainIndex.index}" />" value="false" />&nbsp;
						<s:text name="label.newClaim.partQuantitySpecifedPer.claim"/>
					</s:if>
					<s:else>
						<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].inventoryLevel">
							<s:text name="label.newClaim.partQuantitySpecifiedPer"/>&nbsp;
							<input type="radio" checked="checked" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_inventoryLevel_<s:property value="%{#mainIndex.index}" />" value="true"/>&nbsp;
							<s:text name="label.newClaim.partQuantiySpecifiedPer.inventory"/>
							&nbsp;&nbsp;
							<input type="radio" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_claimLevel_<s:property value="%{#mainIndex.index}" />" value="false" />&nbsp;
							<s:text name="label.newClaim.partQuantitySpecifedPer.claim"/>
						</s:if>
						<s:else>
							<s:text name="label.newClaim.partQuantitySpecifiedPer"/>&nbsp;
							<input type="radio" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_inventoryLevel_<s:property value="%{#mainIndex.index}" />" value="true"/>&nbsp;
							<s:text name="label.newClaim.partQuantiySpecifiedPer.inventory"/>
							&nbsp;&nbsp;
							<input type="radio" checked="checked" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].inventoryLevel" id="multiClaim_claimLevel_<s:property value="%{#mainIndex.index}" />" value="false" />&nbsp;
							<s:text name="label.newClaim.partQuantitySpecifedPer.claim"/>
						</s:else>
					</s:else>
				</s:if>
				</div>
				<div style="height:6px;"></div>
				<table width="100%">
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="10%" ><s:text name="label.claim.removedParts" /></td>
							<td width="10%" nowrap="nowrap"></td>
							<td width="5%"/>
                            <s:if test="processorReview">
                                <td width="8%" />
                                <td width="8%" />
                                <td width="8%" />
                                <td width="10%" />
                                <td width="5%" />
                                <td width="8%" />
                                <td width="25%" />
                                <td width="5%" />
                            </s:if>
                            <s:else>
                                <td width="64%" />
                            </s:else>
						</tr>
						<tr class="row_head">
							<s:if test="showPartSerialNumber">
								<s:if test="enableComponentDateCode()">
									<td width="10%" align="center" class="partReplacedClass">
		                               <s:text name="columnTitle.partReturnConfiguration.partSerialNumber" />
		                            </td>
		                            <td width="10%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.dateCode" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:if>
								<s:else>
									<td width="10%" align="center" class="partReplacedClass">
		                               <s:text name="columnTitle.partReturnConfiguration.partSerialNumber" />
		                            </td>
		                            <td width="10%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:else>
		                    </s:if>
		                    <s:else>
		                    	<s:if test="enableComponentDateCode()">
		                     		<td width="20%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.dateCode" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:if>
								<s:else>
									<td width="20%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:else>
		                    </s:else>

                            <s:if test="%{isProcessorReview()}">
                            	<td width="8%" align="center" class="partReplacedClass">
									<s:text name="label.inventory.unitPrice" />
								</td>
								<td width="8%" align="center" class="partReplacedClass">
									<s:text name="label.uom.display" />
								</td>
								<td width="8%" align="center" class="partReplacedClass">
									<s:text name="label.newClaim.unitCostPrice" />
								</td>
                                <td width="10%" align="center" class="partReplacedClass">
                                    <s:text name="label.common.description" />
                                </td>
                                <td width="5%" align="center" class="partReplacedClass">
									<s:text name="label.partReturn.markPartForReturn" />
								</td>
								<td width="8%" align="center" class="partReplacedClass">
									<s:text name="columnTitle.dueParts.return_to" />
								</td>
								<td width="30%" align="center" class="partReplacedClass">
									<s:text name="columnTitle.dueParts.return_details" />
								</td>

							</s:if>
                            <s:else>
                                <td width="64%" align="center" class="partReplacedClass">
                                    <s:text name="label.common.description" />
                                </td>
                            </s:else>
                            <td width="6%" align="center" class="partReplacedClass">
                                <s:text name="label.common.failureReport"/>
                            </td>
						</tr>
							<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null" >
								<s:iterator value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">
									<script type="text/javascript">
                                        dojo.addOnLoad(function() {
                                            var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                            var subInc='<s:property value="%{#subIndex.index}"/>';

                                            dojo.subscribe("/replacedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {
                                            var brandPartId = dijit.byId("replacedPartNumber_"+inIndex+"_"+subInc).getValue();
                                               var hiddenPartId = "replaced_oemPart_"+inIndex+"_"+subInc;
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJavaScriptRequest("getUnserializedBrandPartDetails.action", {
                                                    claimNumber: dojo.byId("claimId").value,
                                                    number: brandPartId
                                                }, function(details) {
                                                    dojo.byId("descriptionSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
                                                    dojo.byId(hiddenPartId).value = details[2];
                                              });
                                        });
                                        });


                                    	function  defaultClaimLevel(inIndex)  {
                                    		var claimLevel_inIndex=false;
                                        	dojo.query("input[id ^= "+value+"]").forEach(function(node){
                        						if(node.value=='true'){
                            						claimLevel_inIndex=true;
                        		 				}
                        					});

                                        	if(claimLevel_inIndex==true){
											     dojo.byId("multiClaim_claimLevel_"+inIndex).checked=true;
											     dojo.byId("multiClaim_inventoryLevel_"+inIndex).disabled=true;
                       					 	}else{
                       							dojo.byId("multiClaim_claimLevel_"+inIndex).checked=false;
                       							 dojo.byId("multiClaim_inventoryLevel_"+inIndex).disabled=false;
                       				    	 }
                                    	};
                                    </script>
                                    <s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}]"
                                            id="replacedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].id}"/>
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />" subReplacedRowIndex="<s:property value='%{#subIndex.index}' />">
										<s:if test="showPartSerialNumber">
										<td valign="center" style="padding-top:6px; padding-bottom:6px;" width="10%" align="center" class="partReplacedClass">
										 <%-- <s:if test="isPartShippedOrCannotBeShipped() ">
										<sd:autocompleter cssStyle='margin:0 3px;' id='replacedPartSerialNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' disabled='true' required='true' notifyTopics='/replacedPartSerialNumber/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_serialized_replacedParts.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimedItem=%{task.claim.itemReference.referredInventoryItem.id}&serializedPart=%{task.claim.partItemReference.referredInventoryItem.id}&isPartsClaim=%{task.partsClaim}&claimId=%{task.claim.id}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredInventoryItem' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredInventoryItem' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.serialNumber}' listenTopics='/replacedPartSerialNumber/setIdValue/%{#mainIndex.index}/%{#subIndex.index}' />
										 </s:if>
										 <s:else>
											<sd:autocompleter cssStyle='margin:0 3px; width:90px;' id='replacedPartSerialNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true' notifyTopics='/replacedPartSerialNumber/description/show/%{#mainIndex.index}/%{#subIndex.index}' href='list_serialized_replacedParts.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimedItem=%{task.claim.itemReference.referredInventoryItem.id}&serializedPart=%{task.claim.partItemReference.referredInventoryItem.id}&isPartsClaim=%{task.partsClaim}&claimId=%{task.claim.id}' name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredInventoryItem' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredInventoryItem' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.serialNumber}' listenTopics='/replacedPartSerialNumber/setIdValue/%{#mainIndex.index}/%{#subIndex.index}' />

												 <script type="text/javascript">
													dojo.addOnLoad(function() {
									                var inIndex='<s:property value="%{#mainIndex.index}"/>';
		                                            var subInc='<s:property value="%{#subIndex.index}" />';

									                        dojo.publish("/replacedPartSerialNumber/setIdValue/" + inIndex + "/" + subInc, [{
									                            addItem: {
									                                key: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.id}"/>',
									                                label: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.serialNumber}"/>'
									                            }
									                        }]);

									                        dojo.subscribe("/replacedPartSerialNumber/description/show/"+inIndex+"/"+subInc, null, function(number, type, request) {
														    	if (type != "valuechanged") {
														       		return;
														    	}
															    twms.ajax.fireJavaScriptRequest("getSerializedOemPartDetails.action",{
															            number: dijit.byId("replacedPartSerialNumber_"+inIndex+"_" +subInc).getValue()
															        }, function(details) {
															            dojo.byId("replacedPartNumber_"+inIndex+"_"+subInc).value = details[1];
															            dijit.byId("replacedPartNumber_"+inIndex+"_"+subInc).setDisabled(true);
															            dojo.byId("descriptionSpan_replacedPartDescription_"+inIndex+"_"+subInc).innerHTML = details[0];
															            dojo.byId("replacedQuantity_"+inIndex+"_"+subInc).value = 1;
														             	dojo.byId("replacedQuantity_"+inIndex+"_"+subInc).readOnly = true;
															           }
															    );
													         });

									                    }
									                    );
									                </script>
									      </s:else> --%>
											<s:textfield cssStyle="width:90px;" id='replacedPartSerialNumber_%{#mainIndex.index}_%{#subIndex.index}' size="15"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].serialNumber" />
										  </td>  
										</s:if>
										 <s:if test="showPartSerialNumber"><td valign="center" style="padding-top:6px; padding-bottom:6px;" width="10%" align="center" class="partReplacedClass"></s:if>
										 <s:else><td valign="center" style="padding-top:6px; padding-bottom:6px;" width="20%" align="center" class="partReplacedClass"></s:else>
										  <s:if test="itemReference.referredInventoryItem !=null || isPartShippedOrCannotBeShipped() ">
											 <sd:autocompleter cssStyle='margin:0 3px;' id='replacedPartNumber_%{#mainIndex.index}_%{#subIndex.index}' disabled='true' showDownArrow='false' required='true'
											 notifyTopics='/replacedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}'
											 href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}'
											 name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].brandItem'
											 keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].brandItem' keyValue='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.id}' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.itemNumber}' />
										 </s:if>
										 <s:else>
										 	 <sd:autocompleter cssStyle='margin:0 3px; width:90px;' id='replacedPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true'
										 	 notifyTopics='/replacedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}'
										 	 href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}'
										 	 name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].brandItem'
										 	 keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].brandItem' keyValue='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.id}' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.itemNumber}' />
										 </s:else>
										 <s:hidden id="replaced_oemPart_%{#mainIndex.index}_%{#subIndex.index}"
										 name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem"
										 value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.number}"/>
										</td>
										<s:if test="enableComponentDateCode()">
										 <td valign="center" style="padding-top:6px; padding-bottom:6px; padding-top:5px\9; padding-bottom:5px\9;" width="8%" align="center" class="partReplacedClass">
											<s:textfield cssStyle="margin:0 3px;" id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}"   size="6" 
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].dateCode"/>
												 
										</td>  
										</s:if>
										<td valign="center" style="padding-top:6px; padding-bottom:6px; padding-top:5px\9; padding-bottom:5px\9;" width="8%" align="center" class="partReplacedClass">
											<s:if test="isPartShippedOrCannotBeShipped()">
												<s:textfield cssStyle="margin:0 3px;" id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" disabled="true" size="3"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits"
												/>
											</s:if>
											<s:elseif test="itemReference.referredInventoryItem !=null  ">
												<s:textfield cssStyle="margin:0 3px;" id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" disabled="true" size="3" value="1"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits" />
												<s:hidden name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits"
										               	 value="1"/>
											</s:elseif>
											<s:else>
												<s:textfield cssStyle="margin:0 3px;" id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits"
												 />
											</s:else>
										</td>
										
										
                                        <s:if test="processorReview">

                                        <td class="partReplacedClass">

											<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].uomAdjustedPricePerUnit"/>
											 </td>


											<td  class="partReplacedClass">
        						    <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
				    	               	<s:property value="uomMapping.mappedUomDescription"/>
				              			<span id="RUOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>" tabindex="0" >
											<img src="image/comments.gif" width="16" height="15" />
										</span>

										<span dojoType="dijit.Tooltip" connectId="RUOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>" >
											<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
										</span>
				                   	</s:if>
				                   	<s:else>
				                   		<s:if test="itemReference.serialized == true">
				                   			<s:property value="itemReference.referredInventoryItem.ofType.uom.type"/>
				                   		</s:if>
				                   		<s:else>
				                   			<s:property  value="itemReference.referredItem.uom.type" />
				                   		</s:else>
				                   	</s:else>
        						    </td>

									 <td class="partReplacedClass">

											<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].CostPricePerUnit"/>
											 </td>

											<td valign="center" style="padding-top:6px; padding-bottom:6px;" width="10%" align="center" class="partReplacedClass">
                                                <span id="descriptionSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
                                                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem.description"/>
                                                </span>
											</td>
											                                            <td valign="center" style="padding-top:6px; padding-bottom:6px;" width="5%" align="center" class="partReplacedClass">
												<s:if test= "(numberOfUnits<=0 ) ||
													(isPartShippedOrCannotBeShipped() )" >
										            <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned"
										            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
										            <s:hidden id="hid_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"
										               	/>
										        </s:if>
										        <s:else>
										        	<s:checkbox id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"
										                onclick="alterOnLoad(%{#mainIndex.index},%{#subIndex.index});"/>
												</s:else>
											</td>
											<script type="text/javascript">
									        	dojo.addOnLoad(function() {
									            	alterOnLoad(<s:property value='%{#mainIndex.index}' />,<s:property value='%{#subIndex.index}'/>,<s:property value = "isPartShippedOrCannotBeShipped()"/>);
												});
										    </script>
                                            <%-- Our new check box for OEM or Supplier Return --%>
                                            <s:set name="partNumber" value="%{itemReference.referredItem.number}" />
                                            <s:set name="rma" value="" />
                                             <s:if test="partReturn.rmaNumber != null" >
                                                  <s:set name="rma" value="%{partReturn.rmaNumber}" />
                                             </s:if>
                                             <s:elseif test="partReturnConfiguration.rmaNumber != null">
                                                <s:set name="rma" value="%{partReturnConfiguration.rmaNumber}" />
                                             </s:elseif>
										    <td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="5%" align="left" class="partReplacedClass">
                                                       <table>
                                                       <s:if test="(isPartShippedOrCannotBeShipped())">
                                                             <s:if test="appliedContract != null && (!isSupplierReturnAlreadyInitiated() || returnDirectlyToSupplier)" >
                                                                <s:if test="returnDirectlyToSupplier">
                                                                 <tr><td>
                                                                   <s:checkbox disabled="true" id='oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'  name="returnDirectlyToSupplier_[%{#mainIndex.index}]_[%{#subIndex.index}]" />
                                                                   <s:text name="label.claim.oem"/>
                                                                 </td></tr>
                                                                 <tr><td>
                                                                      <s:checkbox disabled="true" checked="true" id='supplier_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'  name="returnDirectlyToSupplier_sup_[%{#mainIndex.index}]_[%{#subIndex.index}]"/>
                                                                      <s:text name="label.claim.supplier"/>
                                                                 </td></tr>
                                                               </s:if>
                                                               <s:else>
                                                                  <tr><td>
                                                                     <s:checkbox disabled="true" checked="true" id='oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'  name="returnDirectlyToSupplier_[%{#mainIndex.index}]_[%{#subIndex.index}]" />
                                                                     <s:text name="label.claim.oem"/>
                                                                   </td></tr>
                                                                   <tr><td>
                                                                        <s:checkbox disabled="true" id='supplier_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'  name="returnDirectlyToSupplier_sup_[%{#mainIndex.index}]_[%{#subIndex.index}]"/>
                                                                        <s:text name="label.claim.supplier"/>
                                                                   </td></tr>
                                                               </s:else>
                                                             </s:if>

                                                             <s:else>
                                                                 <tr><td>
                                                                       <s:checkbox disabled="true" id='oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'  name="returnDirectlyToSupplier_[%{#mainIndex.index}]_[%{#subIndex.index}]" />
                                                                       <s:text name="label.claim.oem"/>
                                                                  </td></tr>
                                                              </s:else>

                                                       </s:if>
                                                       <s:else>
                                                            <s:if test="appliedContract != null && (!isSupplierReturnAlreadyInitiated() || returnDirectlyToSupplier)" >
                                                                 <tr><td>
                                                                     <s:if test="returnDirectlyToSupplier">
                                                                          <s:checkbox id="oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier" onclick="showOem(%{#mainIndex.index},%{#subIndex.index},'<s:property value=%{'partNumber'} />')"
                                                                           name="returnDirectlyToSupplier_%{#mainIndex.index}]_[%{#subIndex.index}]" />
                                                                     </s:if>
                                                                     <s:else>
                                                                          <s:checkbox id="oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier" onclick="showOem(%{#mainIndex.index},%{#subIndex.index},'<s:property value=%{'partNumber'} />')"
                                                                         name="returnDirectlyToSupplier_%{#mainIndex.index}]_[%{#subIndex.index}]" checked="true"/>
                                                                     </s:else>
                                                                      <s:text name="label.claim.oem"/>
                                                                 </td></tr>
                                                                 <tr><td>
                                                                  <s:if test="returnDirectlyToSupplier">
                                                                      <s:checkbox id="supplier_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier"
                                                                      onclick="showSupplier(%{#mainIndex.index},%{#subIndex.index},'<s:property value=%{'partNumber'} />')"
                                                                      name="returnDirectlyToSupplier_sup_%{#mainIndex.index}]_[%{#subIndex.index}]" checked="true"/>
                                                                  </s:if>
                                                                  <s:else>
                                                                      <s:checkbox id="supplier_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier"
                                                                        onclick="showSupplier(%{#mainIndex.index},%{#subIndex.index},'<s:property value=%{'partNumber'} />')"
                                                                        name="returnDirectlyToSupplier_sup_%{#mainIndex.index}]_[%{#subIndex.index}]"/>
                                                                  </s:else>
                                                                  <s:text name="label.claim.supplier"/>
                                                                 </td></tr>
                                                             </s:if>
                                                             <s:else>
                                                              <tr><td>
                                                                   <s:checkbox checked="true" disabled="true" id='oem_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier'
                                                                     name="returnDirectlyToSupplier_[%{#mainIndex.index}]_[%{#subIndex.index}]" />
                                                                    <s:text name="label.claim.oem"/>
                                                               </td></tr>
                                                             </s:else>
                                                       </s:else>
                                                       <s:hidden id="hid_oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_isReturnDirectlyToSupplier" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].returnDirectlyToSupplier" />
                                                       </table>
                                                           <td valign="center" style="padding-top:1px; padding-bottom:1px;" width="25%" align="center" class="partReplacedClass">
                                                                 <%@include file="oem_partReturnDetails.jsp"%>
                                                           </td>

                                                           <script type="text/javascript">
                                                           //Show return detail on load

                                                               function showOem(mainIdex, subIndex,partNumber){
                                                                  var oemCheck = dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier");
                                                                  if(oemCheck.checked){
                                                                     dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked=false;
                                                                     dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=false;
                                                                     dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=false;
                                                                     displayOEMData(mainIdex, subIndex, partNumber);
                                                                  }
                                                                  else{
                                                                      dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked=true;
                                                                      dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=true;
                                                                      dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=true;
                                                                      displaySupplierData(mainIdex, subIndex, partNumber);
                                                                  }


                                                               }
                                                               function showSupplier(mainIdex, subIndex, partNumber){
                                                                   var supplierCheck = dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier");
                                                                   var oemCheck = dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier");
                                                                   if(supplierCheck.checked){
                                                                        dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked=false;
                                                                        dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=false;
                                                                        dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=supplierCheck.checked;
                                                                        displaySupplierData(mainIdex, subIndex, partNumber);
                                                                    }
                                                                   else{
                                                                         dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked=true;
                                                                         dojo.byId("oem_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=true;
                                                                         dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value=supplierCheck.checked;
                                                                         displayOEMData(mainIdex, subIndex, partNumber);
                                                                   }


                                                               }

                                                               function displaySupplierData(mainIdex, subIndex, partNumber){
                                                                  dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value = dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked;
                                                                    //Show Supplier data
                                                                  dojo.html.hide(dijit.byId("oemRepPart_"+mainIdex+"_"+subIndex+"_location").domNode);
                                                                  dojo.html.show(dijit.byId("returnloc_"+mainIdex+"_"+subIndex).domNode);
                                                                  dojo.byId("hid_return_location_" + mainIdex + "_" + subIndex).value = dojo.byId("hid_supplier_return_location_" + mainIdex + "_"+ subIndex).value;
                                                                  dojo.byId("oemRepPart_"+mainIdex+"_"+subIndex+"_rma").value = dojo.byId("hid_supllier_rma_"+mainIdex+"_"+subIndex).value;
                                                                  <s:if test="returnDirectlyToSupplier">
                                                                  document.getElementById("oemRepPart_"+mainIdex+"_"+subIndex+"_dueDays").value=document.getElementById("hid_oem_duedays_"+mainIdex+"_"+subIndex).value;
                                                                  </s:if>
                                                                  <s:else>
                                                                  document.getElementById("oemRepPart_"+mainIdex+"_"+subIndex+"_dueDays").value = document.getElementById("hid_oem_duedays_"+mainIdex+"_"+subIndex).value;
                                                                  </s:else>

                                                               }

                                                               function displayOEMData(mainIdex, subIndex, partNumber){
                                                                    //show oem data
                                                                    dojo.html.show(dijit.byId("oemRepPart_"+mainIdex+"_"+subIndex+"_location").domNode);
                                                                    dojo.html.hide(dijit.byId("returnloc_"+mainIdex+"_"+subIndex).domNode);
                                                                    <s:if test="returnDirectlyToSupplier">
                                                                    dojo.byId("oemRepPart_" + mainIdex + "_" +subIndex+ "_location").value = "";
                                                                    document.getElementById("oemRepPart_"+mainIdex+"_"+subIndex+"_dueDays").value = "";
                                                                    dojo.byId("oemRepPart_"+mainIdex+"_"+subIndex+"_rma").value = "";
                                                                    </s:if>
                                                                    <s:else>
                                                                    dojo.byId("hid_return_location_"+mainIdex+"_"+subIndex).value =  dojo.byId("hid_oem_return_location_" + mainIdex + "_"+ subIndex).value;
                                                                    dojo.byId("hid_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").value = dojo.byId("supplier_oemRepPart_"+mainIdex+"_"+subIndex+"_isReturnDirectlyToSupplier").checked;
                                                                    dojo.byId("oemRepPart_" + mainIdex + "_" +subIndex+ "location").value = dojo.byId("hid_oem_return_location_code" + mainIdex + "_"+ subIndex).value;
                                                                    document.getElementById("oemRepPart_"+mainIdex+"_"+subIndex+"_dueDays").value = dojo.byId("hid_oem_duedays_" + mainIdex + "_"+ subIndex).value;
                                                                    dojo.byId("oemRepPart_"+mainIdex+"_"+subIndex+"_rma").value = dojo.byId("hid_oem_rma_"+mainIdex+"_"+subIndex).value;
                                                                    </s:else>

                                                                    //dojo.byId("hid_return_location_"+ mainIdex + "_" + subIndex).value = "";

                                                               }
                                                           </script>

                                                    </td>

                                           <%-- part return detail goes here --%>
                                            </s:if>
                                            <s:else>
                                                <td valign="center" style="padding-top:6px; padding-bottom:6px;" width="33%" align="center" class="partReplacedClass">
                                                <span id="descriptionSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
                                                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].itemReference.referredItem.description"/>
                                                </span>
                                            </td>
                                            </s:else>
                                        <td valign="center" style="padding-top:6px; padding-bottom:6px;" class="partReplacedClass" width="7%" align="center">
                                            <s:if test="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id!=null}">
                                                <span style="color:blue;text-decoration:underline;cursor:pointer;"
                                                      id="report_<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id}"/>">
                                                <s:text name="home_jsp.menuBar.view" />
                                                <script type="text/javascript">
                                                    dojo.addOnLoad(function() {
                                                        var claimId ='<s:property value="%{task.claim.id}"/>';
                                                        var reportId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id}"/>';
                                                        var itemId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.id}"/>';
                                                        var invItemId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.id}"/>';
                                                        var replacedPart = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index]}"/>';
                                                        var failureReportName = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.customReport.name}"/>';
                                                        dojo.connect(dojo.byId("report_"+reportId),"onclick",function(){
                                                           displayFailureReport(
                                                                   claimId,
                                                                   reportId,
                                                                   failureReportName,
                                                                   itemId,
                                                                   invItemId,"",replacedPart);
                                                        });
                                                    });
                                                </script>
                                                </span>
                                            </s:if>
                                        </td>

											<script type="text/javascript">
												dojo.addOnLoad(function() {
													var inIndex='<s:property value="%{#mainIndex.index}"/>';
													var subIndex='<s:property value="%{#subIndex.index}"/>';
													var readOnly = '<s:property value="#readOnlyForParts"/>';

													if(readOnly=="true"){
														if(dojo.byId("replacedPartNumber_"+inIndex+"_"+subIndex) != null){
															dojo.byId("replacedPartNumber_"+inIndex+"_"+subIndex).readOnly=true;
														}
														 <%--  if(dojo.byId("replacedQuantity_"+inIndex+"_"+subIndex) != null){
															dojo.byId("replacedQuantity_"+inIndex+"_"+subIndex).readOnly=true;
														}  --%>
														/*if(dojo.byId("replacedPartSerialNumber_"+inIndex+"_"+subIndex) != null){
                                                            dojo.byId("replacedPartSerialNumber_"+inIndex+"_"+subIndex).readOnly=true;
                                                        }*/
													}
												});
										 </script>
									</tr>
								</s:iterator>
							</s:if>

					</tbody>
				</table>
				<div style="height:6px;"></div>
				<table width="100%">
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td width="10%"  nowrap="nowrap" ><s:text name="label.claim.installedParts" /></td>
							<td width="10%"  nowrap="nowrap" ></td>
							<td width="5%"/>
							<s:if test="processorReview">
                                <td width="8%" />
                                <td width="8%" />
                                <td width="8%" />
                                <td width="40%" />
                            </s:if>
                            <s:else>
                                <td width="64%" />
                            </s:else>
                            <td width="6%" align="right" colspan="2" style="padding-right:6px;" />
						</tr>
						<tr class="row_head">
							<s:if test="showPartSerialNumber">
								<s:if test="enableComponentDateCode()">
									<td width="10%" align="center" class="partReplacedClass">
		                               <s:text name="columnTitle.partReturnConfiguration.partSerialNumber" />
		                            </td>
		                            <td width="10%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.dateCode" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
									</s:if>
									<s:else>
									<td width="10%" align="center" class="partReplacedClass">
		                               <s:text name="columnTitle.partReturnConfiguration.partSerialNumber" />
		                            </td>
		                            <td width="10%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</s:else>
		                    </s:if>
		                    <s:else>
		                    	<s:if test="enableComponentDateCode()">
		                     		<td width="20%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.dateCode" />
									</td>
									<td width="5%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:if>
								<s:else>
									<td width="20%" align="center" class="partReplacedClass"  nowrap="nowrap">
										<s:text	name="label.newClaim.partNumber" />
									</td>
									<td width="10%" align="center" class="partReplacedClass">
										<s:text name="label.common.quantity" />
									</td>
								</s:else>
		                    </s:else>

                            <s:if test="%{isProcessorReview()}">

							<td width="8%" align="center" class="partReplacedClass">
							<s:text name="label.inventory.unitPrice" />
							</td>
							<td width="8%" align="center" class="partReplacedClass">
							<s:text name="label.uom.display" /></td>
							<td width="8%" align="center" class="partReplacedClass">
							<s:text name="label.newClaim.unitCostPrice" /></td>
                            <td width="40%" align="center" class="partReplacedClass" ><s:text name="label.common.description" /></td> </s:if>
                            <s:else>
                            <td width="64%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></td>
                            </s:else>
                            <td width="6%" colspan="2" align="center" class="partReplacedClass"><s:text name="label.common.failureReport" /></td>
						</tr>
						<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null">
							<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">
								<s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}]"
                                            id="hussmaninstalledPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].id}"/>
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	subHussInstallRowIndex="<s:property value='%{#subIndex.index}'/>" >

									<s:if test="showPartSerialNumber">
									<td width="10%" align="center" class="partReplacedClass">
											  <s:textfield cssStyle="width:90px;" id="installedHussmanSerialNumber_%{#mainIndex.index}_%{#subIndex.index}" size="15"
												name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].serialNumber" />	</td>
									 </s:if>


									<s:if test="showPartSerialNumber"><td width="10%" align="center" class="partReplacedClass"></s:if>
									<s:else><td width="20%" align="center" class="partReplacedClass"></s:else>
											<sd:autocompleter cssStyle='width:90px;' id='installedHussmanPartNumber_%{#mainIndex.index}_%{#subIndex.index}' showDownArrow='false' required='true'
											notifyTopics='/installedPart/description/show/%{#mainIndex.index}/%{#subIndex.index}'
											href='list_oem_servicepart_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}'
											name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].brandItem' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].brandItem' keyValue='%{brandItem.id}' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].brandItem.itemNumber}' />

											<s:hidden id="installedPart_%{#mainIndex.index}_%{#subIndex.index}"
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item"
											value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.number}" />
									</td>
									<s:if test="enableComponentDateCode()">
									<td width="5%" align="center" class="partReplacedClass">
											<s:textfield id="installedHussmanQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="6" 
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].dateCode" />
									</td>
									</s:if>
									<td width="5%" align="center" class="partReplacedClass">
											<s:textfield id="installedHussmanQuantity_%{#mainIndex.index}_%{#subIndex.index}" size="3"
											name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].numberOfUnits" />
									</td>
									 <s:if test="processorReview">
									<td width="8%" align="left" class="partReplacedClass">

											<t:money id="installedPricePerUnit_%{#mainIndex.index}_%{#subIndex.index}"
	                         	name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].pricePerUnit" 
	                         	value="%{uomAdjustedPricePerUnit}" ></t:money>
	<s:checkbox readonly="false" id="installedPriceUpdated_%{#mainIndex.index}_%{#subIndex.index}" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].priceUpdated" />
				<script type="text/javascript">
					dojo
							.addOnLoad(function() {
								var priceId = '<s:property value="qualifyId(\"installedPricePerUnit\")" />';
								var mainIndex = '<s:property value="%{#mainIndex.index}" />';
								var subIndex = '<s:property value="%{#subIndex.index}" />';
								priceId = priceId + "_" + mainIndex + "_" + subIndex;
								var isUpdatable = '<s:property value="qualifyId(\"installedPriceUpdated\")" />';
								isUpdatable = isUpdatable + "_" + mainIndex + "_" + subIndex;
								if (dojo.byId(isUpdatable).checked) {
									dojo.byId(priceId).disabled = false;

								} else {

									dojo.byId(priceId).disabled = true;
								}
								dojo
										.connect(
												dojo.byId(isUpdatable),
												"onclick",
												function() {

													if (dojo.byId(isUpdatable).checked) {
														dojo.byId(priceId).disabled = false;

													} else {

														dojo.byId(priceId).disabled = true;
													}

												});
							});
				</script>
											 </td>
									<td width="8%" align="center"  class="partReplacedClass">
				                   	<s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
										<s:property value="uomMapping.mappedUomDescription"/>
										<span id="UOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>" tabindex="0" >
											<img src="image/comments.gif" width="16" height="15" />
										</span>
										<span dojoType="dijit.Tooltip" connectId="UOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>" >
											<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
										</span>
									</s:if>
									<s:else>
										<s:property value="item.uom.type"/>
									</s:else>
        						    </td>
				                   <td width="8%" align="center" class="partReplacedClass">

											<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].costPricePerUnit"/>
											 </td>
                                    <td width="40%" align="center" class="partReplacedClass" >
										<span id="descriptionInstalledSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
						                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item.description"/>
						                </span>
				                    </td></s:if>
				                    <s:else>
				                    <td width="64%" align="center" class="partReplacedClass">
										<span id="descriptionInstalledSpan_replacedPartDescription_<s:property value='%{#mainIndex.index}'/>_<s:property value='%{#subIndex.index}'/>">
						                    <s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item.description"/>
						                </span>
				                    </td>
				                    </s:else>
                                    <td class="partReplacedClass" colspan="2" align="center" width="6%">
                                        <s:if test="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id!=null}">
                                                <span style="color:blue;text-decoration:underline;cursor:pointer;"
                                                      id="report_<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id}"/>">
                                                <s:text name="home_jsp.menuBar.view"/>
                                                <script type="text/javascript">
                                                    dojo.addOnLoad(function() {
                                                        var claimId ='<s:property value="%{task.claim.id}"/>';
                                                        var reportId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id}"/>';
                                                        var itemId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.id}"/>';
                                                        var serialNumber = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].serialNumber}"/>';
                                                        var failureReportName = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.customReport.name}"/>';
                                                        dojo.connect(dojo.byId("report_" + reportId), "onclick", function() {
                                                            displayFailureReport(
                                                                    claimId,
                                                                    reportId,
                                                                    failureReportName,
                                                                    itemId,
                                                                    "",serialNumber,"");
                                                        });
                                                    });
                                                </script>
                                                </span>
                                        </s:if>
                                    </td>
								</tr>

								<script type="text/javascript">
                                        dojo.addOnLoad(function() {
                                            var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                            var subInc='<s:property value="%{#subIndex.index}"/>';
                                            var installedPartHiddenID = "installedPart_"+inIndex+"_"+subInc;
                                            dojo.subscribe("/installedPart/description/show/" + inIndex + "/" + subInc, null, function(number, type, request) {
                                            var brandPartId= dijit.byId("installedHussmanPartNumber_"+inIndex+"_"+subInc).getValue();
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJavaScriptRequest("getUnserializedBrandPartDetails.action", {
                                                    claimNumber: dojo.byId("claimId").value,
                                                    number: brandPartId
                                                }, function(details) {
                                                    dojo.byId("descriptionInstalledSpan_replacedPartDescription_" + inIndex+"_"+subInc).innerHTML = details[0];
                                                    dojo.byId(installedPartHiddenID).value = details[2];
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
												/* if(dojo.byId("installedHussmanQuantity_"+inIndex+"_"+subIndex) != null){
													dojo.byId("installedHussmanQuantity_"+inIndex+"_"+subIndex).readOnly=true;
												}
												if(dojo.byId("installedHussmanSerialNumber_"+inIndex+"_"+subIndex) != null){
                                                    dojo.byId("installedHussmanSerialNumber_"+inIndex+"_"+subIndex).readOnly=true;
                                                }
                                                if(dojo.byId("replacedPartSerialNumber_"+inIndex+"_"+subIndex) != null){
                                                    dojo.byId("replacedPartSerialNumber_"+inIndex+"_"+subIndex).readOnly=true;
                                                } */
											}
										});
								 </script>
							</s:iterator>
						</s:if>
					</tbody>
				</table>
				</div>
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
