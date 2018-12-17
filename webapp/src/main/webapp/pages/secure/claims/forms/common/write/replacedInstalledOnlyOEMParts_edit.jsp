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

<style type="text/css">
  .partReplacedClass{
      border:1px solid #EFEBF7;
	  font-size:9pt;
	  color:#545454;
  }
  .title td{
  color:#5577B4;
  }
</style>

<div id="partReplacedInstalledDiv" >
<table width="97%">
	<thead>
		<tr class="title">
            <td width="92%" style="color:#5577B4"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></td>
            <td width="8%"></td>
        </tr>
		<tr><td colspan="2" class="borderTable">&nbsp;</td></tr>
	</thead>
	<tbody id="addRepeatBody" > 
		<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null && !task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.isEmpty()">
			<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
				 <tr><td>
				<table width="100%" class="grid borderForTable">
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title">
							<td colspan="9" nowrap="nowrap"><s:text name="label.claim.removedParts" /></td>
                        </tr>
						<tr class="row_head">
						<s:if test="showPartSerialNumber">
							<th width="13%">
								<s:text	name="label.common.serialNumber" />
							</th>
						</s:if>	
							<th width="20%">
								<s:text	name="label.newClaim.partNumber" />
							</th>
							<th width="5%">
								<s:text name="label.common.dateCode" />
							</th>							
							<th width="5%">
								<s:text name="label.common.quantity" />
							</th>
							<th width="20%">
								<s:text name="label.common.description" />
							</th>
                            <s:if test="loggedInUserAnInternalUser">
								<th width="12%">
									<s:text name="label.partReturn.markPartForReturn" />
								</th>
								<th width="12%">
									<s:text name="columnTitle.dueParts.return_location" />
								</th>
								<th width="12%">
									<s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
								</th>
								<th width="5%">
									<s:text name="label.common.dueDays" />
								</th>
							</s:if>
                            <th width="20%">
								<s:text name="label.common.failureReport" />
							</th>
						 </tr>
							<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null" >
								<s:iterator value="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />">
									<s:if test="showPartSerialNumber">	
										<td class="partReplacedClass">
											<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.serialNumber" />
										</td>
									</s:if>	
										<td class="partReplacedClass">
											<s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.serialized">
												<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.number" />
											   <s:hidden id="replacedPartNumber" value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.number"/>
											</s:if>
											<s:else>
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.itemNumber" />
											    <s:hidden id="replacedPartNumber" value="%{getOEMPartCrossRefForDisplay(task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem, oemDealerPartReplaced, true, task.claim.forDealer)}"/>
											</s:else>
										</td>
										<td class="partReplacedClass">
											<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}"  name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].dateCode"/>
										</td>
										<td class="partReplacedClass">
											<s:textfield id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}"  name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].numberOfUnits"/>
										</td>
										<td class="partReplacedClass">
										    <s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.serialized">
												<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.description" />
											</s:if>
											<s:else>
												<s:property value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.description" />
											</s:else>											
										</td>
                                        <s:if test="loggedInUserAnInternalUser">
											<td class="partReplacedClass" align="center">
									            <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned"
									            	 name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
											</td>
											<td class="partReplacedClass">
										        <s:property value="activePartReturn.returnLocation.code"/>
											</td>
											<td class="partReplacedClass">
												<s:property value="activePartReturn.paymentCondition.description"/>
											</td>
											<td class="partReplacedClass">
												<s:property value="activePartReturn.dueDays" />
											</td>
                                        </s:if>
										<td class="partReplacedClass" style="padding-left:10px;">
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
                                                        var replacedPart = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index]}"/>';
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
									</tr>
									 <script type="text/javascript">
								                	dojo.addOnLoad(function() {
								                		   var inIndex='<s:property value="%{#mainIndex.index}"/>';
				                                            var subInc='<s:property value="%{#subIndex.index}" />';
				                                            var value = '<s:property value="%{#replacedPartReadOnly}"/>';
				                                            if(value=='true'){
				                                            	 twms.ajax.fireJavaScriptRequest("getUnserializedOemPartDetails.action", {
				                                                           claimNumber:  '<s:property value="task.claim.id"/>',
				                                                           number: dojo.byId("replacedPartNumber").value
				                                                       }
				                                                   );
				                                            }
								                    });
								     </script>
								</s:iterator>
							</s:if>
											
					</tbody>
				</table>
				</td></tr>
				<tr><td>
				<table width="100%" class="grid borderForTable">
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title">
				<%-- <table width="100%" >
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title"> --%>
							<td colspan="9" nowrap="nowrap"><s:text name="label.claim.installedParts" /></td>
                        </tr>
						<tr class="row_head">
						<s:if test="showPartSerialNumber">
							<th width="13%">
								<s:text	name="label.common.serialNumber" />
							</th>
					    </s:if>
							<th width="10%">
								<s:text	name="label.newClaim.partNumber" />
							</th>
							<th width="5%">
								<s:text name="label.common.dateCode" />
							</th>
							<th width="5%">
								<s:text name="label.common.quantity" />
							</th>
							<th width="20%"> <s:text name="label.common.description" /></th>
                            <th width="20%">
								<s:text name="label.common.failureReport" />
							</th>
                        </tr>
						<s:if test="task.claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null">
							<s:iterator	value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">													
														<s:hidden
                                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}]"
                                            id="installedPart_%{#mainIndex.index}_%{#subIndex.index}"
                                            value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].id}"/>
													
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	>
									
									<s:if test="showPartSerialNumber">
									<td class="partReplacedClass">&nbsp;</td>
									</s:if>
									<td class="partReplacedClass">
											<sd:autocompleter id='installedHussmanBrandPartNumber' showDownArrow='false' required='true' notifyTopics='/installedPart/description/show'
											href='list_oem_servicepart_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}'
											name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].brandItem' keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].brandItem' keyValue='%{brandItem.id}' value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].brandItem.itemNumber}' />

											<s:hidden id="installedHussmanPartNumber" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].item" value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.number}" />
										</td>
										<td  class="partReplacedClass">
											<s:textfield name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].dateCode"/>
										</td>
										<td  class="partReplacedClass">
											<s:textfield name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].hussmanInstalledParts[%{#subIndex.index}].numberOfUnits"/>
										</td>
										<td class="partReplacedClass">
											<span
												id="descriptionInstalledSpan_replacedPartDescription">
											<s:property
												value="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.description" />
											</span>
										</td>
										<td class="partReplacedClass" style="padding-left:10px;">
                                        <s:if test="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id!=null}">
                                                <span style="color:blue;text-decoration:underline;cursor:pointer;"
                                                      id="report_<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id}"/>">
                                                <s:text name="home_jsp.menuBar.view"/>
                                                <script type="text/javascript">
                                                    dojo.addOnLoad(function() {
                                                        var claimId = '<s:property value="%{task.claim.id}"/>'
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
                                            dojo.subscribe("/installedPart/description/show", null, function(number, type, request) {  
                                            var oldBrandPartNumber = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].brandItem.id}"/>';
                                                     var itemNumber;
                                                   if(oldBrandPartNumber == number )
                                                    {
                                                  itemNumber='<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].brandItem.id}"/>';
                                                  }
                                               else
                                                 {
                                                itemNumber=dijit.byId("installedHussmanBrandPartNumber").getValue();
                                                 }                                            
                                                if (type != "valuechanged") {
                                              
                                                    return;
                                                }
                                                twms.ajax.fireJavaScriptRequest("getUnserializedBrandPartDetails.action", {
                                                    claimNumber: '<s:property value="%{task.claim.id}"/>',
                                                    number: itemNumber
                                                }, function(details) {   
                                                                                                   
                                                    dojo.byId("descriptionInstalledSpan_replacedPartDescription").innerHTML = details[0];
                                                    dojo.byId("installedHussmanPartNumber").value = details[2];
                                                }
                                                        );
                                            });
                                        });
                                 </script>

								</s:iterator>
						</s:if>
					</tbody>
				</table>
				</td></tr>
				<tr><td><table><tr><td>&nbsp;</td></tr></table></td></tr>
			</s:iterator>			
		</s:if>		
	</tbody>
</table>
</div>