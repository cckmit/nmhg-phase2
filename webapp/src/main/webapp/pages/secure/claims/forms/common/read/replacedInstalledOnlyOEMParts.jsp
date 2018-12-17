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

<script type="text/javascript">
	dojo.require("dojox.layout.ContentPane");
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dijit.layout.LayoutContainer");	
	dojo.require("twms.widget.Dialog");
	
</script>
<script type="text/javascript">
    dojo.require("dijit.Tooltip");
</script>
<script type="text/javascript">
    function displayFailureReport(dataId, failureReportName) {
        var claimId = '<s:property value="claim.id"/>';
        var url = "show_failure_report.action?claim="+claimId;
        if (dataId) {
            url += "&customReportAnswer=" + dataId;
        }
        var failure_report = '<s:text name="label.common.failureReport"/>';
		var tabLabel = failure_report + "-" + failureReportName;
        parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : getMyTabLabel()});
        delete url,failure_report,tabLabel;
    }
      
</script>

<div id="partReplacedInstalledDiv" >
<table width="97%">
<s:hidden id="showPartSerialNumber" name="showPartSerialNumber" value ="%{isShowPartSerialNumber()}" />
	<thead>
		<tr class="title">
            <td width="92%" style="color:#5577B4"><s:text name="label.newClaim.oEMPartReplacedInstalled"/></td>
            <td width="8%"></td>
        </tr>
		<tr><td colspan="2" class="borderTable">&nbsp;</td></tr>
	</thead>
	<tbody id="addRepeatBody" > 
		<s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled != null && !claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled.isEmpty()">
			<s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled"
											status="mainIndex">
				 <tr><td>
				<table width="100%" class="grid borderForTable">
					<tbody id='addRepeatBody_Replaced_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title">
							<td colspan="12" nowrap="nowrap"><s:text name="label.claim.removedParts" /></td>
                        </tr>
						<tr class="row_head">
					   	  <s:if test="showPartSerialNumber">	
							<th width="13%">
								<s:text	name="label.claim.partSerialNumber" />
							</th>
						  </s:if>	
							<th width="10%">
								<s:text	name="label.newClaim.partNumber" />
							</th>
							<s:if test="enableComponentDateCode()">
							<th width="10%">
								<s:text name="label.common.dateCode" />
							</th>	
							</s:if>	 				
							<th width="5%">
								<s:text name="label.common.quantity" /> 
							</th>
							<authz:ifUserNotInRole roles="supplier">
							<th width="10%">
								<s:text name="label.inventory.unitPrice" />
							</th>
							</authz:ifUserNotInRole>
						 <s:if test="!isLoggedInUserADealer()">
						<th width="10%"> <s:text name="label.newClaim.unitCostPrice" /></th>
						<th width="8%">	<s:text name="label.uom.display" /> </th>
						 </s:if> 	
								
                        		<th width="15%">	
							<s:text name="label.common.description" />
							</th>
                            <s:if test="loggedInUserAnInternalUser">
								<th width="8%">
									<s:text name="label.partReturn.markPartForReturn" />
								</th>
								<th width="8%">
                                    <s:text name="columnTitle.dueParts.return_to" />
                                </th>
								<th width="10%">
									<s:text name="columnTitle.dueParts.return_location" />
								</th>
								<th width="10%">
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
							<s:if test="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts != null" >
								<s:iterator value="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">
									<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />">
								      <s:if test="showPartSerialNumber">		
										<td class="partReplacedClass">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].serialNumber" />
										</td>
									  </s:if>	
										<td class="partReplacedClass">
											<s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.serialized">
												<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.number" />
												<s:hidden id="replacedPartNumber" value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.getBrandPartNumber(claim.brand)"/>
											</s:if>
											<s:else>
                                                <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.itemNumber" />
											    <s:hidden id="replacedPartNumber" value="%{getOEMPartCrossRefForDisplay(claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem, oemDealerPartReplaced, true, claim.forDealer)}"/>
<%-- 												<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.number" />	 --%>
											</s:else>	
											 <s:hidden id="batSheetExists_%{#mainIndex.index}_%{#subIndex.index}"/>
										</td>
										<s:if test="enableComponentDateCode()">
										<td class="partReplacedClass">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].dateCode"/>
											<s:hidden id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].dateCode"/>
										</td>
										</s:if>
										<td class="partReplacedClass">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].numberOfUnits"/>
											<s:hidden id="replacedQuantity_%{#mainIndex.index}_%{#subIndex.index}" value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].numberOfUnits"/>
										</td>
										<authz:ifUserNotInRole roles="supplier">
									<td  class="partReplacedClass">
									
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].uomAdjustedPricePerUnit"/>
										<s:checkbox disabled="true" id='%{qualifyId("installedPriceUpdated")}' name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].priceUpdated"/>
										
     								</td>	
     								</authz:ifUserNotInRole>
										 <s:if test="!isLoggedInUserADealer()">
									  		
  									<%--TKTSA-495  --%>		
  									<td class="partReplacedClass">	
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].costPricePerUnit"/> 						
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
        						     
        						  </s:if>
        						    
  																		
  									    	
  									    		   	
  									    		<td class="partReplacedClass">
										    <s:if test="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.serialized">
												<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredInventoryItem.ofType.description" />
											</s:if>
											<s:else>
												<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.description" />
											</s:else>											
										</td>
										
                                        <s:if test="loggedInUserAnInternalUser">
											<td class="partReplacedClass" align="center">
									            <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned"
									            	 name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
											</td>

											<td class="partReplacedClass" align="center">

                                                <s:checkbox disabled="true" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_toBeReturned"
                                                     name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partToBeReturned"/>
                                                <s:if test="partToBeReturned">
                                                    <s:if test="returnDirectlyToSupplier">
                                                        <s:text name="label.claim.supplier"/>
                                                    </s:if>
                                                    <s:else>
                                                        <s:text name="label.claim.oem"/>
                                                    </s:else>
                                                 </s:if>
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
                                            <s:if test="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id!=null}">
                                                <span style="color:blue;text-decoration:underline;cursor:pointer;"
                                                      id="report_<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id}"/>">
                                                <s:text name="home_jsp.menuBar.view" />
                                                <script type="text/javascript">
								                	dojo.addOnLoad(function() {
								                		var reportId = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id}"/>';
								                		var failureReportName = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.customReport.name}"/>';
								                        dojo.connect(dojo.byId("report_"+reportId),"onclick",function(){
								                        	displayFailureReport(reportId, failureReportName);
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
				                                                           claimNumber:  '<s:property value="claim.id"/>',
				                                                           number: dojo.byId("replacedPartNumber").value
				                                                       }, function(details) {
					                                                       console.debug(details[0]);
				                                                   });
				                                            }
								                    });
			                        		 </script>
								</s:iterator>
							</s:if>
											
					</tbody>
				</table>
				</td></tr>
				<tr><td>
				<table width="100%" >
					<tbody id='addRepeatBody_HussInstalled_<s:property value="%{#mainIndex.index}" />'>
						<tr class="title">
							<td colspan="9" nowrap="nowrap"><s:text name="label.claim.installedParts" /></td>
                        </tr>
						<tr class="row_head">
						  <s:if test="showPartSerialNumber">	
							<th width="8%">
								<s:text	name="label.claim.partSerialNumber" />
							</th>
						  </s:if>							
							<th width="7%">
								<s:text	name="label.newClaim.partNumber" />
							</th>
							<s:if test="enableComponentDateCode()">
							<th width="8%">
								<s:text	name="label.common.dateCode" />
							</th>
							</s:if>
							<th width="5%">
								<s:text name="label.common.quantity" />
							</th>
							 <authz:ifUserNotInRole roles="supplier">
							<th width="8%">
								<s:text name="label.inventory.unitPrice" />
							</th>
								
								</authz:ifUserNotInRole>
								<s:if test="!isLoggedInUserADealer()">
										<th width="10%"> <s:text name="label.newClaim.unitCostPrice" /></th>	
									
                                 </s:if>
                                 <authz:ifUserNotInRole roles="supplier">
                                 <th width="8%">	
								<s:text name="label.uom.display" />		
								</th>
								</authz:ifUserNotInRole>
                                 <authz:ifUserInRole roles="supplier">
                                 <th width="8%">	
								<s:text name="label.uom.display" />		
								</th>
                                 </authz:ifUserInRole>
                                 
                                   <th width="15%"> <s:text name="label.common.description" /></th>
                                   <th width="12%">	
                                    <s:text name="label.common.failureReport" />
							      </th>
                        </tr>
						<s:if test="claim.serviceInformation.
											serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts != null">
							<s:iterator	value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts"
													status="subIndex">
								<tr id="HussmannInstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"	>
								  <s:if test="showPartSerialNumber">		
									<td class="partReplacedClass">
											<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].serialNumber" />
									</td>
								  </s:if>
					
 									<td class="partReplacedClass"> 
                                       <%--  <s:property value="%{getOEMPartCrossRefForDisplay(claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].brandItem, oemDealerPartReplaced, true, claim.forDealer,claim.brand)}"/> --%>
                                        <s:property value="brandItem.itemNumber"/>
									</td> 
									<s:if test="enableComponentDateCode()">
									<td  class="partReplacedClass">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].dateCode"/>
									</td>
									</s:if>
									<td  class="partReplacedClass">
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].numberOfUnits"/>
									</td>
									<authz:ifUserNotInRole roles="supplier">
									<td  class="partReplacedClass">
									
										<s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].uomAdjustedPricePerUnit"/>
										<s:checkbox disabled="true" id='%{qualifyId("installedPriceUpdated")}' name="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].priceUpdated"/>
										
     								</td>	
     								</authz:ifUserNotInRole>
     								
     								<s:if test="!isLoggedInUserADealer()">	
                                		<td  class="partReplacedClass">	
                                		  <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].costPricePerUnit"/></td>	
                                  </s:if>
     								<authz:ifUserNotInRole roles="supplier">
     								<td  class="partReplacedClass">	
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
        						    
        						    </authz:ifUserNotInRole>
        						    
        						    
                                  <authz:ifUserInRole roles="supplier">
                                  <td  class="partReplacedClass">
                                   <s:property value="item.uom.type"/>   
                                   </td>	
                                  </authz:ifUserInRole>
                                   <td  class="partReplacedClass">
                                   <s:property value="claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].item.description"/>
									</td>
                                    <td class="partReplacedClass" style="padding-left:10px;">
                                        <s:if test="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id!=null}">
                                                <span style="color:blue;text-decoration:underline;cursor:pointer;"
                                                      id="report_<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id}"/>">
                                                <s:text name="home_jsp.menuBar.view"/>
                                                <script type="text/javascript">
								                	dojo.addOnLoad(function() {
								                		var reportId = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.id}"/>';
								                		var failureReportName = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].hussmanInstalledParts[#subIndex.index].customReportAnswer.customReport.name}"/>';		
								                		dojo.connect(dojo.byId("report_"+reportId),"onclick",function(){
								                        	displayFailureReport(reportId, failureReportName);
								                        });
								                    });
								                </script>
								                </span>
                                        </s:if>
                                    </td>
                                </tr>
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