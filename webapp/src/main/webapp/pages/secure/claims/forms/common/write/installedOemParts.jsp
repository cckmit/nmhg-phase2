<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<table id="oemRemovedInstalledSection_table" class="grid borderForTable">
	<thead>
		<tr>
			<th width="95%" class="mainTitle" style="margin-bottom:5px;">
			<s:text
				name="label.newClaim.oEMPartReplacedInstalled" />
			</th>
			<th width="5%" class="section_heading">
			
			<div class="nList_add" id="oemRemovedInstalledDivOuterAdd"  style="margin-right:5px"/>
			</th>
		</tr>
	</thead>
			<s:hidden cssStyle="height:2px" id="processorReview" name="processorReview" value ="%{isProcessorReview()}" />
			<s:hidden cssStyle="height:2px"  id="showPartSerialNumber" name="showPartSerialNumber" value ="%{isShowPartSerialNumber()}" />
			<s:hidden cssStyle="height:2px" id="multipleClaim" name="task.claim.forMultipleItems" value="%{task.claim.forMultipleItems}"></s:hidden>
			<s:hidden cssStyle="height:2px" id="paymentLength" value="%{paymentConditions.size()}"/>
			<s:hidden cssStyle="height:2px" id="claimId" value="%{task.claim.id}" />
			<s:hidden cssStyle="height:2px" id="rowIndex" value="%{rowIndex}" />
			<s:hidden cssStyle="height:2px" id="partsReplacedInstalledVisibleId" name="partsReplacedInstalledSectionVisible"></s:hidden>
	<tbody>	
	
	<script>
	dojo.addOnLoad(function() {
		dojo.connect(dojo.byId("oemRemovedInstalledDivOuterAdd"),"onclick",function(){
			dojo.html.hide(dojo.byId("oemRemovedInstalledDivOuterAdd"));
		});
	  });
	  function enableAddrowButton() {
			 dojo.html.show(dojo.byId("oemRemovedInstalledDivOuterAdd"));
		}
	
	var extraParams = {
			claimDetail :'<s:property value="task.claim"/>',
			claim :'<s:property value="task.claim"/>',
			task :'<s:property value="task"/>',
			baseFormName :'<s:property value="task.getBaseFormName()"/>',
			selectedBusinessUnit : '<s:property value="selectedBusinessUnit"/>',
			processorReview:'<s:property value="processorReview"/>',
			showPartSerialNumber:'<s:property value="showPartSerialNumber"/>'
		};
	
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
	
	/*function toggleEnableDisableOnLoad(nListIndex,nListName,disabled) {
		console.debug(nListIndex);
		console.debug(nListName);
		console.debug(disabled);

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
	}*/
</script>
 <script type="text/javascript">
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
	
	function toggleEnableDisableShippingComments(){
		if(dojo.byId("shippingCommentsToDealer")){
			dojo.byId("shippingCommentsToDealer").disabled = true;
			<s:iterator value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="mainList">
				<s:iterator value = "replacedParts" status = "subList">
					var mainIndex = <s:property value="#mainList.index" />;
					var subIndex = <s:property value="#subList.index" />;
					if(dojo.byId("shippingCommentsToDealer").disabled){
						if(dojo.byId("task_claim_serviceInformation_serviceDetail_hussmanPartsReplacedInstalled"+
								mainIndex+"_replacedParts"+subIndex+"__oemRepPart_toBeReturned")
								&& dojo.byId("task_claim_serviceInformation_serviceDetail_hussmanPartsReplacedInstalled"+
									mainIndex+"_replacedParts"+subIndex+"__oemRepPart_toBeReturned").checked){
										dojo.byId("shippingCommentsToDealer").disabled = false;
						}
					}
				</s:iterator>
			</s:iterator>
		}
	}
	
	function toggleEnableDisableOnLoad(nListIndex,idPrefix,disabled) {
		toggleEnableDisableShippingComments();
		var toBeReturnedId = idPrefix+"oemRepPart_toBeReturned";
		var suppToBeReturnedId=idPrefix+"hid_oemRepPart_returnDirectlyToSupplier";
		var toBeReturnedHiddenId = idPrefix+"hid_oemRepPart_toBeReturned";
		var hid_returnDirectlyToSupplier = idPrefix+"hid_oemRepPart_returnDirectlyToSupplier";
		var locationId = idPrefix+"oemRepPart_location";
		var paymentConditionId = idPrefix+"oemRepPart_paymentCondition";
		var dueDaysId = idPrefix+"oemRepPart_dueDays";
		var check=dojo.byId(toBeReturnedId);
		var suppCheck=dojo.byId(suppToBeReturnedId);
		if(check.checked && dojo.byId("shippingCommentsToDealer") && dojo.byId("shippingCommentsToDealer").disabled){
			dojo.byId("shippingCommentsToDealer").disabled = false;
		}
	    dojo.byId(toBeReturnedId).value=check.checked;
	    dojo.byId(suppToBeReturnedId).value=suppCheck.checked;
	    var location=dijit.byId(locationId);
	    var paymentCondition = dijit.byId(paymentConditionId);
	    var dueDays=document.getElementById(dueDaysId);
	    var rmaNumber=document.getElementById(idPrefix+"rma");
	    var dealerPickUpLoc=dijit.byId(idPrefix+"dealerPickUpLocation");
	    var returnDirectlyToOEM=dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier");
	    var returnDirectlyToSupplier=dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier");
	    if(disabled && dojo.byId(toBeReturnedHiddenId)){
        	    	dojo.byId(toBeReturnedHiddenId).value=check.checked;
        	    }
        if(returnDirectlyToSupplier){
             if(returnDirectlyToOEM.checked){
                 dojo.byId(hid_returnDirectlyToSupplier).value=returnDirectlyToSupplier.checked;
             }
        }
        else{
            dojo.byId(hid_returnDirectlyToSupplier).value=false;
        }
	    if(!disabled && location){
	    	if(!check.checked) {
	    		document.getElementById(locationId).value="";
	    		dueDays.value="";
	    		rmaNumber.value="";
		        location.setDisabled(true);
                dueDays.disabled = true;
                rmaNumber.disabled = true;
		        if(paymentCondition){
		            paymentCondition.setDisabled(true);
		        }
		        else{
		            document.getElementById(paymentConditionId).disabled = true;
		        }
		        if(dealerPickUpLoc){
                    dealerPickUpLoc.setDisabled(true);
                }
                else{
                    document.getElementById(idPrefix+"dealerPickUpLocation").disabled = true;
                }
                if(returnDirectlyToOEM){
                    returnDirectlyToOEM.disabled = true;
                }
                if(returnDirectlyToSupplier){
                    returnDirectlyToSupplier.disabled = true;
                }


		     } else {
		        location.setDisabled(false);
                dueDays.disabled = false;
                rmaNumber.disabled = false;
		        if(paymentCondition){
                    paymentCondition.setDisabled(false);
                }
                else{
                    document.getElementById(paymentConditionId).disabled = false;
                }
                 if(dealerPickUpLoc){
                    dealerPickUpLoc.setDisabled(false);
                }
                else{
                    document.getElementById(idPrefix+"dealerPickUpLocation").disabled = false;
                }
                if(returnDirectlyToOEM){
                    returnDirectlyToOEM.disabled = false;
                }
                if(returnDirectlyToSupplier){
                    returnDirectlyToSupplier.disabled = false;
                }else{
                    returnDirectlyToOEM.disabled = true;
                }

			 }
		}
	}
	</script>
		<u:nList value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
			rowTemplateUrl='getOemRemovedInstalledPartTemplate.action' paramsVar="extraParams">	
			<div id="oemRemovedInstalledDiv">		
			<jsp:include flush="true" page="installedOemPartTemplate.jsp" />	
			</div>		
		</u:nList>		
	</tbody>
</table>