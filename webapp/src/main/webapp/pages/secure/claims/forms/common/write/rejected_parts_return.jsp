<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<script type="text/javascript">
	dojo.require("dijit.Tooltip")
</script>

<s:iterator
	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
	status="partReplacedStatus">

	<%-- <s:if test="replacedParts!=null"> --%>
	<div class="mainTitle" style="margin-top: 5px;">
		<s:text name="label.common.rejectedParts.section.title" />
	</div>
	<table class="grid borderForTable" style="width: 97%" cellspacing="0"
		cellpadding="0">
		<thead>
			<tr class="row_head">
				<th>
				<script type="text/javascript">
				
			 /* 	dojo.addOnLoad(function() {
					if(dojo.byId('totalPartsCheckBox'))
					 selectUnselectCheckBox(); 
				}); */
				 
					function selectUnselectCheckBox() {
						var replacePartsSize = <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#partReplacedStatus.index].replacedParts.size"/>;						
						for ( var i = 0; i < replacePartsSize; i++) {
							if (dojo.byId('totalPartsCheckBox_0').checked == true && dojo.byId('partCheckBox_' + i)) {							
								dojo.byId('partCheckBox_' + i).checked = true;
							} else if(dojo.byId('partCheckBox_' + i)){
								dojo.byId('partCheckBox_' + i).checked = false;
							}
						}
					}
				</script>
				 <s:checkbox
						checked="false"
						name=" "
						id="totalPartsCheckBox_%{#partReplacedStatus.index}" onchange="selectUnselectCheckBox()" />
				</th>
				<th><s:text name="label.common.partNumber" /></th>
				<th><s:text name="label.common.description" /></th>
				<th><s:text name="label.common.quantity" /></th>
				<th><s:text name="label.partReturnAudit.rejectionReason" /></th>
				<th><s:text name="label.common.returnlocation" /></th>
				<th><s:text name="label.common.dealerName" /></th>
			</tr>
		</thead>

		<tbody>
			<s:iterator value="replacedParts" status="removePartsStatus">
			 <s:if test="PartReceivedAndVerified">
				<tr>
					<td>		
								
					<s:checkbox
							checked="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#partReplacedStatus.index].replacedParts[#removePartsStatus.index].partReturnToDealer}"
							name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#partReplacedStatus.index}].replacedParts[%{#removePartsStatus.index}].partReturnToDealer"
							id='partCheckBox_%{#removePartsStatus.index}' />
						 <s:if test="PartShippedToDealer">
								<script type="text/javascript">								
									var partIndex = <s:property value='#removePartsStatus.index'/>;
									var husIndex = <s:property value='#partReplacedStatus.index'/>;			
									dojo.byId('partCheckBox_'+partIndex).checked = true;
									dojo.byId('partCheckBox_'+partIndex).disabled=true;
									dojo.byId('totalPartsCheckBox_'+husIndex).disabled=true;
								</script>
							</s:if>
							<s:else>
							<script type="text/javascript">								
									var partIndex = <s:property value='#removePartsStatus.index'/>;
									var husIndex = <s:property value='#partReplacedStatus.index'/>;			
									dojo.byId('partCheckBox_'+partIndex).checked = false;									
									dojo.byId('totalPartsCheckBox_'+husIndex).checked=false;
								</script>
							</s:else>
							
							</td>
					<td><s:property
							value="%{brandItem.itemNumber}" />
					</td>
					<td><s:property
							value="%{itemReference.referredItem.description}" /></td>
					<td><s:property value="getShippedPartsQuantity()" />
					</td>
					<td><s:property
							value="partReturns[0].inspectionResult.failureReason.description" />
					</td>
					<td><s:property
							value="%{activePartReturn.returnLocation.code}" /></td>
					<td><s:property value="%{claim.forDealer.name}" /></td>
				</tr>
			 </s:if>	
			</s:iterator>
		</tbody>
	</table>
</s:iterator>

