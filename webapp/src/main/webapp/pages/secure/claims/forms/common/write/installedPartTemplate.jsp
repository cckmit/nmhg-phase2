<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<s:if test="isPartShippedOrCannotBeShipped">
	<s:set name="isDisabled" value="true" />
</s:if>
<s:else>
	<s:set name="isDisabled" value="false" />
</s:else>
<tr id="InstalledRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"
	subReplacedRowIndex="<s:property value='%{#subIndex.index}' />">

	<s:if test="showPartSerialNumber">
		<td valign="top" style="padding-top: 6px; padding-bottom: 6px;" width="10%" align="center" class="partReplacedClass">
		<s:textfield  id='%{qualifyId("installedHussmanSerialNumber")}' size="15"
												name="%{#nListName}.serialNumber" />	</td>				
	
	</s:if>
	
	<td valign="top" style="padding-top: 6px; padding-bottom: 6px;" <s:if test="showPartSerialNumber"> width="10%" </s:if>
		<s:else>width="20%" </s:else> align="center" class="partReplacedClass">
		<sd:autocompleter cssStyle='margin:0 3px; width:90px;'
			id='%{qualifyId("installedBrandPartNumber")}' showDownArrow='false' required='true'
			notifyTopics='/installedPart/description/show/%{#nListIndex}'
			href='list_active_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}' name='%{#nListName}.brandItem'
			keyName='%{#nListName}.brandItem' keyValue='%{brandItem.id}' value='%{brandItem.itemNumber}' />

			<s:hidden id='%{qualifyId("installedPartNumber")}' name="%{#nListName}.item" value="%{item.number}" />
			<script type="text/javascript">
                dojo.addOnLoad(function() {
                    var index = '<s:property value="%{#nListIndex}" />';
                    dojo.subscribe("/installedPart/description/show/"+index, null, function(number, type, request) {
                    var autoId='<s:property value="qualifyId(\"installedBrandPartNumber\")" />';
                    var hiddenPartId = '<s:property value="qualifyId(\"installedPartNumber\")" />';

                    var oldBrandPartNumber = '<s:property value="brandItem.id"/>';
                    var newBrandPartNumber= dijit.byId(autoId).getValue();

                    if(oldBrandPartNumber == newBrandPartNumber )
                    {
                       partNumber='<s:property value="brandItem.id"/>';
                    }
                    else
                    {
                       partNumber=newBrandPartNumber;
                    }

                        if (type != "valuechanged") {
                            return;
                        }
                        twms.ajax.fireJsonRequest("getUnserializedBrandPartDetails.action", {
                            claimNumber: dojo.byId("claimId").value,
                            number: partNumber
                        }, function(details) {
                            var  descId = '<s:property value="qualifyId(\"descriptionSpan_installedPartDescription\")" />';
                            dojo.byId(descId).innerHTML = details[0];
                            dojo.byId(hiddenPartId).value = details[2];
                        }
                    );

                  });
                });
            </script></td>
    <%--Change for SLMS-776 adding date code --%>
    <s:if test="enableComponentDateCode()">
	<td valign="top" style="padding-top: 6px; padding-bottom: 6px; padding-top: 5px\9; padding-bottom: 5px\9;"  align="center"
		class="partReplacedClass">											<s:textfield cssStyle="margin:0 3px;" name="%{#nListName}.dateCode"  size="20"/></td>
	</s:if>
	<td valign="top" style="padding-top: 6px; padding-bottom: 6px; padding-top: 5px\9; padding-bottom: 5px\9;"  align="center"
		class="partReplacedClass">											<s:textfield cssStyle="margin:0 3px;" id='%{qualifyId("installedHussmanQuantity")}' size="3"
											name="%{#nListName}.numberOfUnits" /></td>
	<s:if test="processorReview">
	<td >
	<s:if test="item !=null">
	<t:money id='%{qualifyId("installedPricePerUnit")}' 
	                         	name="%{#nListName}.pricePerUnit" 
	                         	value="%{uomAdjustedPricePerUnit}" ></t:money>
	<s:checkbox readonly="false" id='%{qualifyId("installedPriceUpdated")}' name="%{#nListName}.priceUpdated"/>
				<script type="text/javascript">
					dojo
							.addOnLoad(function() {
								var priceId = '<s:property value="qualifyId(\"installedPricePerUnit\")" />';
								var isUpdatable = '<s:property value="qualifyId(\"installedPriceUpdated\")" />';
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
			</s:if>        
		</td>
		<td class="partReplacedClass"><s:property
				value="costPricePerUnit" />
		</td>
		<td class="partReplacedClass"><s:if test="uomMapping != null && (uomMapping.mappedUom.length() > 0)  ">
				<s:property value="uomMapping.mappedUomDescription" />
				<span id='%{qualifyId("RUOM")}'  tabindex="0"> <img
					src="image/comments.gif" width="16" height="15" /> </span>

				<span dojoType="dijit.Tooltip" connectId='%{qualifyId("RUOM")}'>
					<s:property value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit" /> ) </span>
			</s:if> <s:else>
					<s:property value="item.uom.type"/>
				<s:else>
					<s:property value="itemReference.referredItem.uom.type" />
				</s:else>
			</s:else>
		</td>

		<td valign="top" style="padding-top: 6px; padding-bottom: 6px;"  align="center" class="partReplacedClass"><s:label
				id='%{qualifyId("descriptionSpan_installedPartDescription")}' value="%{itemReference.referredItem.description}" />
		</td>
		
		
		
	</s:if>
	<s:else>
		<td valign="top" style="padding-top: 6px; padding-bottom: 6px;" width="33%" align="center" class="partReplacedClass"><s:label
				id='%{qualifyId("descriptionSpan_installedPartDescription")}' value="%{itemReference.referredItem.description}" />
		</td>
	</s:else>
<%-- 	<td valign="top" style="padding-top: 6px; padding-bottom: 6px;" class="partReplacedClass" width="7%" align="center"><s:if
			test="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].customReportAnswer.id!=null}">
			<span style="color: blue; text-decoration: underline; cursor: pointer;"
				id="report_<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].customReportAnswer.id}"/>">
				<s:text name="home_jsp.menuBar.view" /> <script type="text/javascript">
                                                    dojo.addOnLoad(function() {
                                                        var claimId ='<s:property value="%{task.claim.id}"/>';
                                                        var reportId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].customReportAnswer.id}"/>';
                                                        var itemId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].itemReference.referredItem.id}"/>';
                                                        var invItemId = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].itemReference.referredInventoryItem.id}"/>';
                                                        var installedPart = '<s:property value="%{claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index]}"/>';
                                                        var failureReportName = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].installedParts[#subIndex.index].customReportAnswer.customReport.name}"/>';
                                                        dojo.connect(dojo.byId("report_"+reportId),"onclick",function(){
                                                           displayFailureReport(
                                                                   claimId,
                                                                   reportId,
                                                                   failureReportName,
                                                                   itemId,
                                                                   invItemId,"",installedPart);
                                                        });
                                                    });
                                                </script> </span>
		</s:if>
	</td> --%>
	<td valign="top" style="padding-top: 6px; padding-bottom: 6px;" width="5%" align="center" class="partReplacedClass"><s:if
			test="!isPartShippedOrCannotBeShipped()">
			<s:hidden name="%{#nListName}" value="%{id}" id="%{qualifyId(\"removedPart_Id\")}" />
			<div class="nList_delete" />
		</s:if>
	</td>


</tr>