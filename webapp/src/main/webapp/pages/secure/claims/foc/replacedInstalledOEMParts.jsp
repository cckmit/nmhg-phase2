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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<u:stylePicker fileName="batterytestsheet.css" />
<u:stylePicker fileName="official.css" />
<s:head theme="twms"/>

<head>
	<script type="text/javascript" >
		dojo.require("dojox.layout.ContentPane");
		dojo.require("twms.widget.ValidationTextBox");
		dojo.require("twms.widget.NumberTextBox");
        dojo.require("twms.widget.Select");
        dojo.require("twms.data.AutoCompleteReadStore");
</script>
<script type="text/javascript" src="scripts/repeatTable/foc/RepeatTableTemplate.js"></script>
</head>

<script type="text/javascript">
     dojo.addOnLoad(function() {
     		var rowIndex,subRowIndex;
	        rowIndex = "<s:property value='%{rowIndex}'/>";
	        subRowIndex = "<s:property value='%{subRowIndex}' />";
	        rowIndex=parseInt(rowIndex);
            connectValueAddButton(rowIndex,subRowIndex);
	});
	
	function displayNonHussman(rowIndex,subRowIndex) {
			subRowIndex = subRowIndex-1;
			connectInstalledNonHusPartsSubButton(rowIndex,subRowIndex);
	}
	
	function displayHussman(rowIndex,subRowIndex) {
			subRowIndex = subRowIndex-1;
			connectInstalledHusPartsSubButton(rowIndex,subRowIndex);
	}
	
	function displayReplaced(rowIndex,subRowIndex) {
			subRowIndex = subRowIndex-1;
			connectReplacedPartsSubButton(rowIndex,subRowIndex);
	}
	
	function deleteRow(rowIndex,subRowIndex) {
			dojo.dom.destroyNode(dojo.byId("tableRow_"+rowIndex));
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
</script>

<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="replacedPartStore" method="get"
             url="list_oem_part_itemnos.action?selectedBusinessUnit=Hussmann"/>

<div dojoType="twms.data.AutoCompleteReadStore" data-dojo-id="returnLocationCodeStore" method="get"
             url="list_part_return_Locations.action?selectedBusinessUnit=Hussmann" >

</div>
<table width="100%" id="test" class="grid borderForTable">
	<s:hidden id="processorReview" name="processorReview" value ="%{isProcessorReview()}" />
	<s:hidden id="paymentLength" value="%{paymentConditions.size()}"/>
	<s:iterator value="paymentConditions" status="paymentStatus" >
		<s:hidden id="paymentConditionscode_%{#paymentStatus.index}" value="%{code}" />
		<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index}" value="%{description}" />
	</s:iterator>
	<thead>
		<tr class="title" width="100%">
            <th><s:text name="label.newClaim.hussmanPartReplaced"/></th>
            <th><s:text name="label.newClaim.hussmanPartsInstalled"/></th>            
            <th></th>
        </tr>
		<tr class="row_head">
			<th align="center" width="45%">
				<table width="100%" height="100%" >
				  <tr class="repeatTable_row_head">
					<th width="45%">
						<s:text	name="label.newClaim.partNumber" />
					</th>
					<th width="45%">
						<s:text name="label.common.quantity" />
					</th>
					<th width="10%"></th>
				 </tr>
				</table>
			</th>
			<th align="center" width="45%">
				<table width="100%" height="100%" >
				  <tr class="repeatTable_row_head">
					<th width="45%">
						<s:text	name="label.newClaim.partNumber" />
					</th>
					<th width="45%">
						<s:text name="label.common.quantity" />
					</th>
					<th width="10%"></th>
				 </tr>
				</table>
			</th>
            <th width="10%">
			<div class="repeat_add" id="addRepeatRow"/></th>
		</tr>
	</thead>
	<tbody id="addRepeatBodyFoc" >
		<s:if test="hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()">
					<s:iterator	value="hussmanPartsReplacedInstalled"
													status="mainIndex">
							<tr id='tableRow_<s:property value="%{#mainIndex.index}" />'>
							<s:if test="hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null && !hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts.isEmpty()">
								<td>
								<s:iterator	value="hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts"
													status="subIndex">
											<table width="100%" height="100%">
											<tbody id='addReplacedSubBodyFoc_<s:property value="%{#mainIndex.index}" />'>
												<tr>
													<td width="30%">
														<input dojoType="twms.widget.Select" id='replacedPartNumber_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />'
														required="true" trim="true" hasDownArrow="false"
                                                        store="replacedPartStore" searchAttr="label"
														name="hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].replacedParts[<s:property value="%{#subIndex.index}" />].itemReference.referredItem"
														value="<s:property value='hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.number'/>" />
													</td>
													<td width="25%">
														<input dojoType="twms.widget.NumberTextBox" id='replacedQuantity_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />'
														required="true" trim="true" 
														name="hussmanPartsReplacedInstalled[<s:property value="%{#mainIndex.index}" />].replacedParts[<s:property value="%{#subIndex.index}" />].numberOfUnits"
														value="<s:property value='hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].numberOfUnits'/>" />
													</td>
													<td width="10%">
														<s:if test="#subIndex.index == 0">
															<div class='repeat_add' id='addPartReplaced_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='displayReplaced(<s:property value="%{#mainIndex.index}" />,<s:property value="%{hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts.size}" />)' width='10%' />
														</s:if>
													</td>
												</tr>
											</tbody>
											</table>
								</s:iterator>
								</td>
							</s:if>
							<s:else>
								<td>
								<table width="100%" height="100%">
									<tr>
										<td width="45%">
										</td>
										<td width="45%">
										</td>
									</tr>
								</table>
								</td>
							</s:else>
							<s:if test="hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null && ! hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts.isEmpty()">
								<td>
								<s:iterator	value="hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">
											<table width="100%" height="100%">
											<tbody id='addHussInstalledSubBody_<s:property value="%{#mainIndex.index}" />'>
												<tr>
													<td width="50%">
														<input dojoType="twms.widget.Select" id='installedHussmanPartNumber_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />'
														required="true" trim="true" hasDownArrow="false"
                                                        store="replacedPartStore" searchAttr="label"
														name="hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}' />].hussmanInstalledParts[<s:property value='%{#subIndex.index}' />].item"
														value="<s:property value='hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.number'/>" />
													</td>
													<td width="40%">
														<input dojoType="twms.widget.NumberTextBox" id='installedHussmanQuantity_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />'
														required="true" trim="true" 
														name="hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}' />].hussmanInstalledParts[<s:property value='%{#subIndex.index}' />].numberOfUnits"
														value="<s:property value='hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].numberOfUnits'/>" />
													</td>
													<td width="10%">
														<s:if test="#subIndex.index == 0">
															<div class='repeat_add' id='hussAddPartInstalled_<s:property value="%{#mainIndex.index}" />_<s:property value="%{#subIndex.index}" />' onclick='displayHussman(<s:property value="%{#mainIndex.index}" />,<s:property value="%{hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts.size}" />)' width='10%' />
														</s:if>
													</td>
												</tr>
											</tbody>
											</table>
								</s:iterator>
								</td>
							</s:if>
							<s:else>
								<td>
								<table width="100%" height="100%">
									<tr>
										<td width="45%">
										</td>
										<td width="45%">
										</td>
									</tr>
								</table>
								</td>
							</s:else>
							<td width="10%">
								<div class='repeat_del' id='deleteRow_<s:property value="#mainIndex.index"/>_0 ' onclick='deleteRow(<s:property value="#mainIndex.index"/>,0)'/>
							</td>
					</tr>
					</s:iterator>			
		</s:if>		
	</tbody>
</table>
<script type="text/javascript">
    function attachInvoice(/*Function*/ callback) {
    dojo.publish("/uploadDocument/dialog/show", [{callback : callback}]);
}
</script>
