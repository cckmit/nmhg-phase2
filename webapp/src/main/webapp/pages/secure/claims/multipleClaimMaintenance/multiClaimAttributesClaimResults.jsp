<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
    dojo.require("dijit.Tooltip")
</script>
<div id="multiClaimAttributes" dojoType="dojox.layout.ContentPane" layoutAlign="client">
<script type="text/javascript">
		var selectedClaimsId= [];
		var claimSize1='<s:property value="claims.size"/>'; 
		var idsInString='<s:property value="selectedClaimsId"/>';
		idsInString=idsInString.replace(/\s+/g,''); 
		if(idsInString!=null && !idsInString==""){
		    selectedClaimsId=idsInString.split(",");
	    } 
		dojo.addOnLoad(function(){
			dojo.connect(dojo.byId("masterCheckboxForClaims"), "onclick", function(event) {
				if(event.target.checked){
			    	for (var i = 0; i < claimSize1; i++) {
		                var currentElement = dojo.byId("claim_"+i);
		                currentElement.checked=true;
						var indexOf =dojo.indexOf(selectedClaimsId, currentElement.value);		
						if (indexOf == -1) {
							selectedClaimsId.push(currentElement.value);	
						}		
					}
				}else{
					for (var j = 0; j < claimSize1; j++) {
						var currentElement = dojo.byId("claim_"+j);
						currentElement.checked=false;
					}			
					selectedClaimsId=[];
					}
				});
				setSelectedInventories();
		        for (var i=0; i<claimSize1; i++) {		
		        var checkBox = dojo.byId("claim_"+i);        
		        dojo.connect(checkBox, "onclick", function(event) {
		            var indexOfClaim;
		            var targetElement = event.target;
		            if(targetElement.checked) {
						indexOfClaim=dojo.indexOf(selectedClaimsId,targetElement.value);
						if (indexOfClaim == -1) {
							selectedClaimsId.push(targetElement.value);
						}
		            } else {
						indexOfClaim =dojo.indexOf(selectedClaimsId,targetElement.value);
						if (indexOfClaim >= 0) {
							selectedClaimsId.splice(indexOfClaim, 1);
						}
		            }
		            });
    			}
		});
		
		
		
		function setSelectedInventories(){
		 for (var i=0; i< claimSize1; i++) {
	        var currentElement = dojo.byId("claim_"+i);
	        var indexOf=dojo.indexOf(selectedClaimsId,currentElement.value);
			if(indexOf >= 0){
				currentElement.checked=true;
			}
		 }
	}
</script>

<s:hidden id="contextName" name="contextName"/>
<s:hidden id="savedQueryId" name="savedQueryId"/>
<s:hidden id="domainPredicateId" name="domainPredicateId"/>
<s:hidden id="isMultiClaimMaintenance" name="isMultiClaimMaintenance"/>
<s:hidden id="selectedBusinessUnit" name="selectedBusinessUnit"/>

<table  cellspacing="0" cellpadding="0" id="claim_details_table" width="100%" class="grid borderForTable" style="clear: both;margin:0px;">
    <thead>    
        <tr>        
            <th class="warColHeader" width="1%" style="padding: 0" align="center">
                <input type="checkbox" id="masterCheckboxForClaims" style="padding: 0"/>
            </th>        
            <th class="warColHeader" width="5%" class="non_editable"><s:text name="columnTitle.common.claimNo"/></th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.serialNo"/></th>
            <th class="warColHeader" width="5%"><s:text name="label.claim.workOrderNumber"/></th>
            <th class="warColHeader" width="4%"><s:text name="label.common.faultCode"/></th>
            <th class="warColHeader" width="5%"><s:text name="label.common.dateOfClaim"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.status"/></th>
            <th class="warColHeader" width="10%"><s:text name="claim.payment.detailsHeader.amountAsked"/></th>  
            <th class="warColHeader" width="10%"><s:text name="claim.payment.detailsHeader.amountCredited"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.viewClaim.creditMemoNumber"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.viewClaim.creditMemoDate"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.dealerName"/></th>                       
        </tr>
    </thead>
    <tbody>
    <s:hidden name="selectedClaimsId" id="selectedClaims"/>
    <s:if test="claims.empty">
        <td align="center" colspan="13"><s:text name="error.claim.noClaimFound" /></td>
    </s:if>
     <s:else>
     	
        <s:iterator value="claims" status="claims"> 
        <s:hidden name="restoreClaimsList[%{#claims.index}]" value="%{id}"/>       
        <tr>        	
            <td align="center" valign="middle">            
                <s:checkbox id="claim_%{#claims.index}"
                        name="claims[%{#claims.index}]" value="false"
                       	fieldValue="%{id}"/>            
            </td>
            <td>
                <s:property value="claimNumber"/>
            </td>
            <td>
                <s:property value="claimedItems[0].itemReference.referredInventoryItem.ofType.model.name" />
            </td>
            <td>
                <s:property value="claimedItems[0].itemReference.referredInventoryItem.serialNumber" />
            </td>
            <td>
                <s:property value="workOrderNumber" />
            </td>
             <td>
                <s:property value="serviceInformation.faultCodeRef.definition.code" />
            </td>
            <td>
                <s:property value="filedOnDate" />
            </td>
            <td>
                <s:property value="state" />
            </td> 
            <td>
                <s:property value="payment.claimedAmount" />
            </td>
            <td> 
				<span id="paidAmount_<s:property value = '%{#claims.index}' />">
					<s:property value="payment.totalAmount" />
					<s:if test="%{!payment.activeCreditMemo.paidAmount.breachEncapsulationOfCurrency().equals(payment.activeCreditMemo.paidAmountErpCurrency.breachEncapsulationOfCurrency())}">
					<span dojoType="dijit.Tooltip" connectId="paidAmount_<s:property value = '%{#claims.index}' />">
						<s:property value="%{payment.activeCreditMemo.paidAmountErpCurrency.abs()}"/>
				</span>	
				</s:if>
			</td>
            <td>
                <s:property value="payment.activeCreditMemo.creditMemoNumber" />
            </td>   
            <td>
                <s:property value="payment.activeCreditMemo.creditMemoDate" />
            </td>
            <td>
                <s:property value="forDealer.name" />
            </td>        
        </tr>
       </s:iterator>
   </s:else>
   </tbody>
</table>

<div>
<center><s:iterator value="pageNoList" status="pageCounter">
		&nbsp;
		<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
		<span id="page_<s:property value="%{#pageCounter.index}"/>">
	</s:if>
	<s:else>
		<span id="page_<s:property value="%{#pageCounter.index}"/>"
			style="cursor: pointer; text-decoration: underline">
	</s:else>
	<s:property value="%{intValue()}" />
	<script type="text/javascript">
			dojo.addOnLoad(function(){	
				var index = '<s:property value="%{#pageCounter.index}"/>';
				var pageNo='<s:property value="pageNo"/>';				
				if(index!=pageNo){
					dojo.connect(dojo.byId("page_"+index),"onclick",function(){
						getMatchingClaims(index);  
					});
				}	 
			});
		</script>
	</span>
</s:iterator></center>
</div>
</div>



