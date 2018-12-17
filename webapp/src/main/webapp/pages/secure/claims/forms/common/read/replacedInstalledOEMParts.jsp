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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<u:stylePicker fileName="batterytestsheet.css" />
<u:stylePicker fileName="official.css" />
<s:head theme="twms"/>
<style type="text/css">
  .partReplacedClass{
      border:1px solid #EFEBF7;
	  font-size:9pt;
	  color:#545454;
	  background-color:#c6d7e8;
	  padding:2px;
	  border:1px solid #a8b5c3;
  }
    .partReplacedData{
	  font-size:9pt;
	  color:#545454;
	  background-color:#fff;
	  padding:2px;
	  border:1px solid #a8b5c3;

  }
  .title td{
  color:#5577B4;
  }
</style>
<div id="partReplacedInstalledDiv" >
<table cellpadding="0" cellspacing="0" border="0" width="97%">
	<thead>
		<tr class="title">
            <td style="color:#5577B4"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></td>
        </tr>
		<tr><td><hr/></td></tr>
	</thead>
	<tbody id="addRepeatBody" >
		<s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null && !claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.isEmpty()">
			<s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
				<tr>
                <td>
				<table width="100%">
                    <s:if test="claim.forMultipleItems">
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
								onclick="checkInventoryLevel(<s:property value='%{#mainIndex.index}' />)" disabled="true"
								name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[<s:property value='%{#mainIndex.index}'/>].inventoryLevel"/>
          						<s:text name="accordion_jsp.accordionPane.inventory"/>
          					</td>
						</tr>
						<tr>
							<td >
								<input type="radio"  value="false" id="inventory_Claim<s:property value='%{#mainIndex.index}' />"
								onclick="checkClaimLevel(<s:property value='%{#mainIndex.index}' />)" disabled="true"
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
				<tr><td>
				<table width="100%" cellpadding="0" cellspacing="0">
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td  nowrap="nowrap"><s:text name="label.claim.removedParts" /></td>
													</tr>
						<tr class="row_head">
							<td class="partReplacedClass">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<%--Change for SLMS-776 adding date code --%>
							<td class="partReplacedClass">
								<s:text	name="label.common.dateCode" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.description" />
							</td>
                            <s:if test="claimWithLoggedInUser || loggedInUserAnInternalUser">
								<td class="partReplacedClass">
									<s:text name="label.partReturn.markPartForReturn" />
								</td>
								<td class="partReplacedClass">
									<s:text name="columnTitle.dueParts.return_location" />
								</td>
								<td class="partReplacedClass">
									<s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
								</td>
								<td class="partReplacedClass">
									<s:text name="label.common.dueDays" />
								</td>
							</s:if>
							<td></td>
						</tr>
							<s:if test="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null" >
								<s:iterator value="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />">
										<td class="partReplacedData">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.number" />
										</td>
										<%--Change for SLMS-776 adding date code --%>
										<td class="partReplacedData">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].dateCode"/>
										</td>
										<td class="partReplacedData">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].numberOfUnits"/>
										</td>
										<td class="partReplacedData">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.description"/>
										</td>
                                        <s:if test="claimWithLoggedInUser || loggedInUserAnInternalUser">
											<td class="partReplacedData" align="center">
									            <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned"
									            	 name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
											</td>
											<td class="partReplacedData">
										        <s:property value="activePartReturn.returnLocation.code"/>
											</td>
											<td class="partReplacedData">
												<s:property value="activePartReturn.paymentCondition.description"/>
											</td>
											<td class="partReplacedData">
												<s:property value="activePartReturn.dueDays" />
											</td>
										</s:if>
											
										<td >
										</td>
									</tr>
								</s:iterator>
							</s:if>
											
					</tbody>
				</table>
				</td></tr>
				<tr><td>
				<table width="100%" >
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td  nowrap="nowrap"><s:text name="label.newClaim.hussmanPartsInstalled" /></td>
												</tr>
						<tr class="row_head">
							<td class="partReplacedClass">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<%--Change for SLMS-776 adding date code --%>
							<td class="partReplacedClass">
								<s:text name="label.common.dateCode" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<td class="partReplacedClass"> <s:text name="label.common.description" /></td>
                        </tr>
						<s:if test="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null">
							<s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	>
									<td class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.number"/>
									</td>
									<%--Change for SLMS-776 adding date code --%>
									<td  class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].dateCode"/>
									</td>
									<td  class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].numberOfUnits"/>
									</td>
									<td  class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.description"/>
									</td>
                                </tr>
							</s:iterator>
						</s:if>
					</tbody>
				</table>
				</td></tr>
				<tr><td>
				<table width="100%" >
					<tbody id='addRepeatBody_NonHussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title" >
							<td nowrap="nowrap" ><s:text name="label.newClaim.nonHussmanPartsInstalled" /></td>
													</tr>
						<tr class="row_head">
							<td class="partReplacedClass">
								<s:text	name="label.newClaim.partNumber" />
							</td>
							<%--Change for SLMS-776 adding date code --%>
							<td class="partReplacedClass">
								<s:text name="label.common.dateCode" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.quantity" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.description" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.common.price" />
							</td>
							<td class="partReplacedClass">
								<s:text name="label.newClaim.invoice" />
							</td>
						</tr>
						<s:if test="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts != null">
							<s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts"
													status="subIndex">
								<tr id="NonHussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	>
									<td  class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].partNumber"/>
									</td>
									<%--Change for SLMS-776 adding date code --%>
									<td class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].dateCode" />
									</td>
									<td class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].numberOfUnits" />
									</td>
									<td  class="partReplacedData">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].description" />
									</td>
									<td  class="partReplacedData">
                                        <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].pricePerUnit"/>
									</td>
									<td class="partReplacedData">
										<s:hidden name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].invoice.id"
                							id="hiddenNonOemInvoice_%{#mainIndex.index}_%{#subIndex.index}"/>
										<a id="downloadInvoice_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />">
						                    <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].nonHussmanInstalledParts[#subIndex.index].invoice.fileName"/>						                    
						                </a>
						                <script type="text/javascript">
                    						dojo.addOnLoad(function() {
                    							var mainIndex = "<s:property value='#mainIndex.index' />";
                    							var subIndex = "<s:property value='#subIndex.index' />";
                        						var downloadLink = dojo.byId("downloadInvoice_"+mainIndex+"_"+subIndex);
                        						var attachedInvoiceId = dojo.byId("hiddenNonOemInvoice_"+mainIndex+"_"+subIndex);                        						
                                            	dojo.connect(dojo.byId(downloadLink), "onclick", function(event) {                                            		
                                                    dojo.stopEvent(event);
                                                    getFileDownloader().download("downloadDocument.action?docId="+attachedInvoiceId.value);
                                                });
                    						});
                    					</script>
									</td>
								</tr>		
							</s:iterator>
						</s:if>
					</tbody>
				</table>
				</td></tr>
			</s:iterator>			
		</s:if>		
	</tbody>
	
</table>
</div>