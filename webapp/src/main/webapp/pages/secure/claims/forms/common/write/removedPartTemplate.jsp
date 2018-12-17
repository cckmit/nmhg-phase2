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
<s:hidden name="initialOEMReplacedParts[%{#nListIndex}]" value="%{id}"/>
<tr id="ReplacedRow_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />"
	subReplacedRowIndex="<s:property value='%{#subIndex.index}' />">

	<s:if test="showPartSerialNumber">
		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="10%" align="center" class="partReplacedClass">
		<s:textfield cssStyle="margin:0 3px;" name="%{#nListName}.serialNumber" size="20"/>

		</td>
	</s:if>

	<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" <s:if test="showPartSerialNumber"> width="10%" </s:if>
		<s:else>width="20%" </s:else> align="center" class="partReplacedClass">

		<sd:autocompleter cssStyle='margin:0 3px; width:90px;'
			id='%{qualifyId("replacedBrandPartNumber")}' showDownArrow='false' required='true'
			notifyTopics='/replacedPart/description/show/%{#nListIndex}'
			href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}' name='%{#nListName}.brandItem'
			keyName='%{#nListName}.brandItem' keyValue='%{brandItem.id}' value='%{brandItem.itemNumber}' />

			<s:hidden id='%{qualifyId("replacedPartNumber")}' name="%{#nListName}.itemReference.referredItem" value='%{itemReference.referredItem.number}'/>

			<script type="text/javascript">
                                        dojo.addOnLoad(function() {                                        	
                                        	var index = '<s:property value="%{#nListIndex}" />';
                                            dojo.subscribe("/replacedPart/description/show/"+index, null, function(number, type, request) {                                            	

                                                var autoId='<s:property value="qualifyId(\"replacedBrandPartNumber\")" />';
                                                var oldBrandPartNumber = '<s:property value="brandItem.id"/>';
	                                            var newBrandPartNumber= dijit.byId(autoId).getValue();
	                                            var hiddenPartId = '<s:property value="qualifyId(\"replacedPartNumber\")" />';

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
                                                	var  descId = '<s:property value="qualifyId(\"descriptionSpan_replacedPartDescription\")" />';
                                                    dojo.byId(descId).innerHTML = details[0];
                                                    dojo.byId(hiddenPartId).value = details[2];
                                                    }
                                                );
                                            });
                                        });
                                    </script>
	</td>
	<%--Change for SLMS-776 adding date code --%>
	<s:if test="enableComponentDateCode()">
	<td valign="center" style="padding-top: 6px; padding-bottom: 6px; padding-top: 5px\9; padding-bottom: 5px\9;"  align="center"
		class="partReplacedClass">
		<s:textfield cssStyle="margin:0 3px;" name="%{#nListName}.dateCode" size="20"/>
	</td>
	</s:if>
	<td valign="center" style="padding-top: 6px; padding-bottom: 6px; padding-top: 5px\9; padding-bottom: 5px\9;"  align="center"
		class="partReplacedClass"><s:if test="isPartShippedOrCannotBeShipped()">
			<s:textfield cssStyle="margin:0 3px;" id='%{qualifyId("replacedQuantity")}' disabled="true" size="3" name="%{#nListName}.numberOfUnits" />
		</s:if> <s:elseif test="itemReference.referredInventoryItem !=null  ">
			<s:textfield cssStyle="margin:0 3px;" id='%{qualifyId("replacedQuantity")}' disabled="true" size="3" value="1"
				name="%{#nListName}.numberOfUnits" />
			<s:hidden name="%{#nListName}.numberOfUnits" value="1" />
		</s:elseif> <s:else>
			<s:textfield cssStyle="margin:0 3px;" id='%{qualifyId("replacedQuantity")}' size="3" name="%{#nListName}.numberOfUnits" />
		</s:else>
	</td>

	<s:if test="processorReview">
	    <td class="partReplacedClass"><s:property value="uomAdjustedPricePerUnit" /></td>
	    <td class="partReplacedClass"><s:property
				value="costPricePerUnit" />
		</td>
		<td class="partReplacedClass"><s:if test="uomMapping != null && (uomMapping.mappedUom.length() > 0)  ">
        <s:property value="uomMapping.mappedUomDescription" />
        <span id="RUOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>" tabindex="0"> <img
            src="image/comments.gif" width="16" height="15" /> </span>

        <span dojoType="dijit.Tooltip" connectId="RUOM_<s:property value="%{#status.index}"/>_<s:property value="%{#subIndex.index}"/>">
            <s:property value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit" /> ) </span>
	</s:if>
	<s:else>
            <s:if test="itemReference.serialized == true">
                <s:property value="itemReference.referredInventoryItem.ofType.uom.type" />
            </s:if>
            <s:else>
                <s:property value="itemReference.referredItem.uom.type" />
            </s:else>
	</s:else>
		</td>		

		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="10%" align="center" class="partReplacedClass"><s:label
				id='%{qualifyId("descriptionSpan_replacedPartDescription")}' value="%{itemReference.referredItem.description}" />
		</td>
		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="5%" align="center" class="partReplacedClass">
		<s:if test="(isPartShippedOrCannotBeShipped())">
				<s:checkbox disabled="true" id='%{qualifyId("oemRepPart_toBeReturned")}' name="%{#nListName}.partToBeReturned" />
				<s:hidden id='%{qualifyId("hid_oemRepPart_toBeReturned")}' name="%{#nListName}.partToBeReturned" />
		</s:if>
		<s:else>
				<s:checkbox id='%{qualifyId("oemRepPart_toBeReturned")}'  name="%{#nListName}.partToBeReturned"
					onclick="toggleEnableDisableOnLoad(%{#nListIndex},'%{qualifyId(\"\")}');" />
		</s:else>
		</td>
		<script type="text/javascript">
            dojo.addOnLoad(function() {
                toggleEnableDisableOnLoad('<s:property value="%{#nListIndex}"/>','<s:property value="qualifyId(\"\")" />','<s:property value = "isPartShippedOrCannotBeShipped()"/>');
            });
        </script>
        <s:set name="partNumber" value="%{itemReference.referredItem.number}" />
        		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="5%" align="center" class="partReplacedClass">
        		 <table>
                   <s:if test="(isPartShippedOrCannotBeShipped())">
                         <s:if test="appliedContract != null && (!isSupplierReturnAlreadyInitiated() || returnDirectlyToSupplier)" >
                             <s:if test="returnDirectlyToSupplier">
                                 <tr><td>
                                   <s:checkbox disabled="true" id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'/>
                                   <s:text name="label.claim.oem"/>
                                 </td></tr>
                                 <tr><td>
                                      <s:checkbox disabled="true" checked="true" id='%{qualifyId("supplierRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' checked="true"/>
                                      <s:text name="label.claim.supplier"/>
                                 </td></tr>
                             </s:if>
                             <s:else>
                                 <tr><td>
                                        <s:checkbox disabled="true" checked="true" id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'  checked="true" />
                                        <s:text name="label.claim.oem"/>
                                      </td></tr>
                                      <tr><td>
                                           <s:checkbox disabled="true" id='%{qualifyId("supplierRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'/>
                                           <s:text name="label.claim.supplier"/>
                                      </td></tr>
                             </s:else>
                         </s:if>
                         <s:else>
                             <tr><td>
                                   <s:checkbox checked="true" value="true" disabled="true" id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' />
                                   <s:text name="label.claim.oem"/>
                              </td></tr>
                          </s:else>
                   </s:if>
                   <s:else>
                        <s:if test="appliedContract != null && (!isSupplierReturnAlreadyInitiated() || returnDirectlyToSupplier)" >
                            <s:if test="returnDirectlyToSupplier">
                                 <tr><td>
                                      <s:checkbox id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'
                                      onclick="showOem(%{#nListIndex},'%{qualifyId(\"\")}');" />
                                      <s:text name="label.claim.oem"/>
                                 </td></tr>
                                 <tr><td>
                                      <s:checkbox id='%{qualifyId("supplierRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'
                                       onclick="showSupplier(%{#nListIndex},'%{qualifyId(\"\")}');" checked="true"/>
                                      <s:text name="label.claim.supplier"/>
                                 </td></tr>
                            </s:if>
                            <s:else>
                                 <tr><td>
                                       <s:checkbox id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'
                                       onclick="showOem(%{#nListIndex},'%{qualifyId(\"\")}');" checked="true" />
                                       <s:text name="label.claim.oem" />
                                  </td></tr>
                                  <tr><td>
                                       <s:checkbox id='%{qualifyId("supplierRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'
                                       onclick="showSupplier(%{#nListIndex},'%{qualifyId(\"\")}');"   />
                                       <s:text name="label.claim.supplier"/>
                                  </td></tr>
                            </s:else>
                         </s:if>
                         <s:else>
                          <tr><td>
                                <s:checkbox value="true" disabled="true" id='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}' name='%{qualifyId("oemRepPart_returnDirectlyToSupplier")}'  />
                                <s:text name="label.claim.oem"/>
                           </td></tr>
                         </s:else>
                   </s:else>
                   </table>
        		<s:hidden id='%{qualifyId("hid_oemRepPart_returnDirectlyToSupplier")}' name="%{#nListName}.partToBeReturned" />
        		<s:set name="rma" value="" />
                 <s:if test="partReturn.rmaNumber != null" >
                      <s:set name="rma" value="%{partReturn.rmaNumber}" />
                 </s:if>
                 <s:elseif test="partReturnConfiguration.rmaNumber != null">
                    <s:set name="rma" value="%{partReturnConfiguration.rmaNumber}" />
                 </s:elseif>
                     <script type="text/javascript">
                           //Show return detail on load
                               function showOem(nlist,idPrefix){
                                  var oemCheck = dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier");
                                  if(oemCheck.checked){
                                     dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier").checked=false;
                                     dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier").value=false;
                                     dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value=false;
                                     displayOEMData(nlist, idPrefix);
                                  }
                                  else{
                                      dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier").checked=true;
                                      dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier").value=true;
                                      dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value=true;
                                      displaySupplierData(nlist, idPrefix);
                                  }
                               }

                               function showSupplier(nlist, idPrefix){
                                   var supplierCheck = dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier");
                                   var oemCheck = dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier");
                                   if(supplierCheck.checked){
                                        dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier").checked=false;
                                        dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier").value=false;
                                        dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value=supplierCheck.checked;
                                        displaySupplierData(nlist, idPrefix);
                                    }
                                   else{
                                         dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier").checked=true;
                                         dojo.byId(idPrefix+"oemRepPart_returnDirectlyToSupplier").value=true;
                                         dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value=supplierCheck.checked;
                                         displayOEMData(nlist, idPrefix);
                                   }
                               }

                                function displaySupplierData(nlist,idPrefix){
                                     dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value = dojo.byId(idPrefix+"supplierRepPart_returnDirectlyToSupplier").checked;
                                       //Show Supplier data
                                      dojo.html.hide(dijit.byId(idPrefix+"oemRepPart_location").domNode);
                                      dojo.html.show(dojo.byId(idPrefix+"supplierRepPart_location"));
                                      dojo.byId(idPrefix+"hid_oemRepPart_location").value = dojo.byId(idPrefix+"hid_supplier_location_id").value ;
                                      dojo.byId(idPrefix+"rma").value = dojo.byId(idPrefix+"hid_supplier_rma").value;
                                      document.getElementById(idPrefix+"oemRepPart_dueDays").value = dojo.byId(idPrefix+"hid_oem_duedays").value;

                                }

                                function displayOEMData(nlist,idPrefix){
                                       //show oem data
                                       dojo.html.show(dijit.byId(idPrefix+"oemRepPart_location").domNode);
                                       dojo.html.hide(dojo.byId(idPrefix+"supplierRepPart_location"));
                                       dojo.byId(idPrefix+"hid_oemRepPart_location").value = dojo.byId(idPrefix+"hid_oem_location_id").value ;
                                       dojo.byId(idPrefix+"hid_oemRepPart_returnDirectlyToSupplier").value =false;
                                       dojo.byId(idPrefix+"rma").value = dojo.byId(idPrefix+"hid_oem_rma").value;
                                       document.getElementById(idPrefix+"oemRepPart_dueDays").value = dojo.byId(idPrefix+"hid_oem_duedays").value;
                                       dojo.byId(idPrefix+"oemRepPart_location").value = dojo.byId(idPrefix+"hid_oem_location_code").value ;

                                }
                     </script>

        </td>
		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="8%" align="left" class="partReplacedClass">
		  <table><tr><td> <s:text name="columnTitle.partReturnConfiguration.returnLocation" /> </td><td>
		  <s:if test="isPartShippedOrCannotBeShipped()">
		      <s:property escape="false" value="%{activePartReturn.returnLocation.code}" />
              <s:hidden id="%{qualifyId('hid_oemRepPart_location')}" name="%{#nListName}.partReturn.returnLocation" value="%{activePartReturn.returnLocation.id}" />
          </s:if>
          <s:else>
		     <s:if test="appliedContract != null" >
				<sd:autocompleter cssStyle='margin:0 3px;width:99px;' id='%{qualifyId("oemRepPart_location")}' size='3'
					href='list_part_return_locations.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='%{qualifyId("oemRepPart_location")}'
					keyName='%{qualifyId("oemRepPart_location")}' loadOnTextChange='true' showDownArrow='false' value='%{activePartReturn.returnLocation.code}' keyValue="%{activePartReturn.returnLocation.id}"
					listenTopics='/partReturn/returnLocation/#nListIndex' />

			    <input type="text" readonly="true" style="display:none" id='<s:property value="qualifyId(\"supplierRepPart_location\")" />' value='<s:property value="appliedContract.location.code"/>' />

                <s:hidden id="%{qualifyId('hid_oem_location_id')}"
                                                    value="%{activePartReturn.returnLocation.id}" />
                <s:hidden id="%{qualifyId('hid_oem_location_code')}"
                                                    value="%{activePartReturn.returnLocation.code}" />
                <s:hidden id="%{qualifyId('hid_supplier_location_id')}"
                                                    value="%{appliedContract.location.id}" />

			    <s:hidden id="%{qualifyId('hid_oemRepPart_location')}"
                                    name="%{#nListName}.partReturn.returnLocation"
                                    value="%{activePartReturn.returnLocation.id}" />

                 <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var idPrefix='<s:property value="qualifyId(\"\")" />';
                        var retLocation = dijit.byId(idPrefix+"oemRepPart_location");
                        dojo.connect(retLocation, "onChange", function(value) {
                        dojo.byId(idPrefix+"hid_oemRepPart_location").value = value;
                    });

                   });
                 </script>
             </s:if>
             <s:else>
		        <sd:autocompleter cssStyle='margin:0 3px;width:99px;' id='%{qualifyId("oemRepPart_location")}' size='3'
                     href='list_part_return_locations.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='%{#nListName}.partReturn.returnLocation'
                     keyName='%{#nListName}.partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{activePartReturn.returnLocation.code}' keyValue="%{partReturn.returnLocation.id}"
                     listenTopics='/partReturn/returnLocation/#nListIndex' />
		     </s:else>

			<script type="text/javascript">
                dojo.addOnLoad(function() {
                   var nListInx = '<s:property value="%{#nListIndex}"/>';
                   var idPrefix='<s:property value="qualifyId(\"\")" />';
                   var locationId = idPrefix+"oemRepPart_location";
                   dijit.byId(locationId).store.includeSearchPrefixParamAlias=false;
                           dojo.publish("/partReturn/returnLocation/" + nListInx, [{
                               addItem: {
                                   key: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].activePartReturn.returnLocation.id}"/>',
                                   label: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].activePartReturn.returnLocation.code}"/>'
                               }
                           }]);
                       });
            </script>
          </s:else>
		</td></tr>
		<tr><td><s:text name="columnTitle.partReturnConfiguration.paymentCondition" /> </td>
		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="12%" align="left" class="partReplacedClass">
		<s:if test="isPartShippedOrCannotBeShipped()">
				<s:property escape="false"
					value="%{partReturn.paymentCondition.description}" />
				<s:hidden id='%{qualifyId("oemRepPart_paymentCondition")}' name="%{#nListName}.partReturn.paymentCondition"
					value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].activePartReturn.paymentCondition.code}"></s:hidden>
		</s:if>
		<s:else>
				<s:if
					test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].activePartReturn.paymentCondition.description != null">
					<s:select list="paymentConditions" id='%{qualifyId("oemRepPart_paymentCondition")}'
						name="%{#nListName}.partReturn.paymentCondition" listKey="code" listValue="description" emptyOption="false"
						cssClass="hussmannPartReplaced"
						value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].activePartReturn.paymentCondition.code}" cssStyle="width:100px;">
					</s:select>
				</s:if>
				<s:else>
					<s:select cssStyle="width:90px;" list="paymentConditions" id='%{qualifyId("oemRepPart_paymentCondition")}'
						name="%{#nListName}.partReturn.paymentCondition" listKey="code" listValue="description" emptyOption="false"
						cssClass="hussmannPartReplaced"
						value="%{activePartReturn.paymentCondition.code}" cssStyle="width:100px;">
					</s:select>
				</s:else>
		</s:else>
		</td> </tr>
		<tr><td><s:text name="label.dueDays" /></td>
		<td valign="center" style="padding-top: 3px; padding-bottom: 6px; padding-top: 5px\9; padding-bottom: 5px\9;" width="5%" align="left"
			class="partReplacedClass">
			<s:if test="useDefaultDueDays()">
							<s:set name="duedaysTouse" value="%{getDefaultDueDays()}" />
			</s:if>
			<s:if test="activePartReturn.dueDays != null">
							<s:set name="duedaysTouse"
								value="%{activePartReturn.dueDays}" />
			</s:if> 
			<s:if test="partShippedOrCannotBeShipped">
				<s:textfield cssStyle="margin:0 1px;" size="16" id='%{qualifyId("oemRepPart_dueDays")}'  disabled="true"
					name="%{#nListName}.partReturn.dueDays" value="%{#duedaysTouse}"></s:textfield>
					<s:hidden name="%{#nListName}.partReturn.dueDaysReadOnly" value="true"/>
			</s:if> <s:else>
				<s:textfield cssStyle="margin:0 1px;" size="16" id='%{qualifyId("oemRepPart_dueDays")}'
					name="%{#nListName}.partReturn.dueDays" value="%{#duedaysTouse}"></s:textfield>
			</s:else>
			 <s:hidden id="%{qualifyId('hid_oem_duedays')}"  value="%{#duedaysTouse}" />
		</td> </tr>
		<tr><td>

		<s:text name="label.claim.rmaNumber" /></td>


        <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
               <s:if test="partShippedOrCannotBeShipped">
                   <s:textfield cssStyle="margin:0 1px;" size="16" id='%{qualifyId("rma")}'  disabled="true"
                       name="%{#nListName}.partReturn.rmaNumber" value="%{#rma}"> </s:textfield>
               </s:if>
               <s:else>
                   <s:textfield cssStyle="margin:0 1px;" size="16" id='%{qualifyId("rma")}'
                       name="%{#nListName}.partReturn.rmaNumber" value="%{#rma}"></s:textfield>
               </s:else>
               <s:hidden id="%{qualifyId('hid_oem_rma')}"  value="%{#rma}" />
               <s:hidden id="%{qualifyId('hid_supplier_rma')}"  value="%{appliedContract.rmaNumber}" />
        </td>

        <tr><td><s:text name="label.claim.dealerPickupLocation" /></td>
            <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
            <s:if test="partShippedOrCannotBeShipped">
              <s:property escape="false"
              					value="%{partReturn.dealerPickupLocation.location}" />
              				<s:hidden id='%{qualifyId("oemRepPart_dealerLocation")}' name="%{#nListName}.dealerPickupLocation"
              					value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.dealerPickupLocation.id}"></s:hidden>
            </s:if>
            <s:else>
                <s:select cssStyle="width:90px;" list="task.claim.forDealer.orgAddresses" id='%{qualifyId("dealerPickUpLocation")}'
                    name="%{#nListName}.partReturn.dealerPickupLocation" listKey="id" listValue="shipToCodeAppended" emptyOption="false"
                    cssClass="hussmannPartReplaced"
                    value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dealerPickupLocation.id}">
                </s:select>
             </s:else>
            </td>
        </tr>

		</table>
	</s:if>
	<s:else>
		<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="33%" align="center" class="partReplacedClass"><s:label
				id='%{qualifyId("descriptionSpan_replacedPartDescription")}' value="%{itemReference.referredItem.description}" />
		</td>
	</s:else>
<%-- 	<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" class="partReplacedClass" width="7%" align="center"><s:if
			test="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].customReportAnswer.id!=null}">
			<span style="color: blue; text-decoration: underline; cursor: pointer;"
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
	</td> --%>
	<td valign="center" style="padding-top: 6px; padding-bottom: 6px;" width="5%" align="center" class="partReplacedClass"><s:if
			test="!isPartShippedOrCannotBeShipped()">
			<s:hidden name="%{#nListName}" value="%{id}" id="%{qualifyId(\"removedPart_Id\")}" />
			<div class="nList_delete" />
		</s:if>
	</td>


</tr>
