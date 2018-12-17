<table class="partReplacedClass">
	     <tr>
	       <td class="partReplacedClass">
	          <s:text name="columnTitle.partReturnConfiguration.returnLocation" />
	       </td>
	       <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
	       <s:if test="appliedContract != null" >
                <sd:autocompleter cssStyle='margin:0 3px; width:90px;'
                id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location' size='3'
                href='list_part_return_locations.action?selectedBusinessUnit=%{selectedBusinessUnit}'
                name='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location'
                 keyName='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location'
                 loadOnTextChange='true' showDownArrow='false'
                 value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation.code}'
                 listenTopics='/partReturn/returnLocation/%{#mainIndex.index}/%{#subIndex.index}' disabled='%{#isDisabled}' />

                <%-- <input type="text" readonly="true" style="display:none" id="returnloc_<s:property value='%{#mainIndex.index}' />_<s:property value='%{#subIndex.index}' />" value='<s:property value="appliedContract.location.code"/>' /> --%>

 				<s:select cssStyle="width:90px;" list="appliedContract.supplier.locations" id="returnloc_%{#mainIndex.index}_%{#subIndex.index}"
                    name="returnloc_%{#mainIndex.index}_%{#subIndex.index}" listKey="id" listValue="code" emptyOption="false"
                    cssClass="hussmannPartReplaced"
                    value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation}'>
                </s:select>

                <s:hidden id="hid_supplier_return_location_%{#mainIndex.index}_%{#subIndex.index}"
                value="%{appliedContract.location.id}"  />

                <s:hidden id="hid_oem_return_location_%{#mainIndex.index}_%{#subIndex.index}"
                                value="%{partReturn.returnLocation.id}"  />
                 <s:hidden id="hid_oem_return_location_code_%{#mainIndex.index}_%{#subIndex.index}"
                                                value="%{partReturn.returnLocation.code}"  />

                <s:hidden id="hid_return_location_%{#mainIndex.index}_%{#subIndex.index}"
                    name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation"
                    value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation.id}" />

                 <script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        var inIndex='<s:property value="%{#mainIndex.index}"/>';
                                        var subInc='<s:property value="%{#subIndex.index}" />';
                                        var retLocation = dijit.byId("oemRepPart_" + inIndex + "_" +subInc+ "_location");
                                        var retLocation1 = dijit.byId("returnloc_" + inIndex + "_" +subInc);
                                        dojo.connect(retLocation, "onChange", function(value) {
                                             dojo.byId("hid_return_location_"+ inIndex + "_" + subInc).value = value;
                                    });
                                        dojo.connect(retLocation1, "onChange", function(value) {
                                            dojo.byId("hid_return_location_"+ inIndex + "_" + subInc).value = value;
                                   });
                                        var code = '<s:property value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation}"/>';
                                        var oemChecked = dojo.byId("oem_oemRepPart_"+inIndex+"_"+subInc+"_isReturnDirectlyToSupplier");
                                        if(oemChecked.checked){
                                        	dojo.html.show(dijit.byId("oemRepPart_"+inIndex+"_"+subInc+"_location").domNode);
                                            dojo.html.hide(dijit.byId("returnloc_"+inIndex+"_"+subInc).domNode);
                                        }else{
                                        	dojo.html.hide(dijit.byId("oemRepPart_"+inIndex+"_"+subInc+"_location").domNode);
                                            dojo.html.show(dijit.byId("returnloc_"+inIndex+"_"+subInc).domNode);
                                            retLocation1.setAttribute('value',code);
                                        }
                                   });
                                 </script>
           </s:if>
           <s:else>
                <sd:autocompleter cssStyle='margin:0 3px; width:90px;display:block'
                id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_location' size='3'
                href='list_part_return_locations.action?selectedBusinessUnit=%{selectedBusinessUnit}'
                name='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation'
                keyName='task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation'
                loadOnTextChange='true' showDownArrow='false'
                value='%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.returnLocation.code}'
                listenTopics='/partReturn/returnLocation/%{#mainIndex.index}/%{#subIndex.index}' disabled='%{#isDisabled}' />
           </s:else>
                 <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var inIndex='<s:property value="%{#mainIndex.index}"/>';
                        var subInc='<s:property value="%{#subIndex.index}" />';
                        dijit.byId("oemRepPart_" + inIndex + "_" +subInc+ "_location").store.includeSearchPrefixParamAlias=false;
                        dojo.publish("/partReturn/returnLocation/" + inIndex + "/" + subInc, [{
                            addItem: {
                                key: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.id}"/>',
                                label: '<s:property value ="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.returnLocation.code}"/>'
                            }
                        }]);
                   });
                 </script>

           </td>
           </tr>
            <tr>
          <td class="partReplacedClass">
              <s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
          </td>
          <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
                  <s:if test="isPartShippedOrCannotBeShipped()">
                      <s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.description}"/>
                      <s:hidden id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition" name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.paymentCondition"
                                  value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.code}"></s:hidden>
                  </s:if>
                  <s:else>
                      <s:if test="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.description != null">
                          <s:select cssStyle="width:90px;" list="paymentConditions" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition"
                          name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.paymentCondition"
                          listKey="code" listValue="description" emptyOption="false"  cssClass="hussmannPartReplaced"
                          value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.code}" >
                          </s:select>
                      </s:if>
                      <s:else>
                          <s:select cssStyle="width:90px;" list="paymentConditions" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_paymentCondition"
                          name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.paymentCondition"
                          listKey="code" listValue="description" emptyOption="false"  cssClass="hussmannPartReplaced"
                          value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].partReturn.paymentCondition.code}" >
                          </s:select>
                      </s:else>
                  </s:else>
              </td>
         </tr>

		   <tr>
              <td class="partReplacedClass">
                 <s:text name="label.dueDays" />
              </td>
              <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
			<s:if test="useDefaultDueDays()">
				<s:set name="duedaysTouse" value="%{getDefaultDueDays()}" />
			</s:if> <s:if test="activePartReturn.dueDays != null">
				<s:set name="duedaysTouse"
					value="%{activePartReturn.dueDays}" />
			</s:if>
			 <s:if test="isPartShippedOrCannotBeShipped()">
                        <s:textfield cssStyle="margin:0 3px;" size="3" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dueDays"  disabled="true"
                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDays" value="%{#duedaysTouse}" ></s:textfield>
                    </s:if>
                    <s:else>
                        <s:textfield cssStyle="margin:0 3px;" size="3" id="oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dueDays"
                            name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dueDays" value="%{#duedaysTouse}" ></s:textfield>
                    </s:else>
                    <s:hidden id="hid_oem_duedays_%{#mainIndex.index}_%{#subIndex.index}"  value="%{#duedaysTouse}" />
              </td>
           </tr>
           <tr>
             <td class="partReplacedClass">
                <s:text name="label.claim.rmaNumber" />
             </td>
             <%--get the rma number --%>
             <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
               <s:if test="partShippedOrCannotBeShipped">
                   <s:textfield cssStyle="margin:0 1px;" size="16" id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_rma'  disabled="true"
                       name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.rmaNumber" value="%{#rma}"></s:textfield>
               </s:if>
               <s:else>
                  <s:textfield cssStyle="margin:0 1px;" size="16" id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_rma'
                                         name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.rmaNumber" value="%{#rma}"></s:textfield>

               </s:else>
               <s:hidden id="hid_supllier_rma_%{#mainIndex.index}_%{#subIndex.index}"  value="%{appliedContract.rmaNumber}" />
               <s:hidden id="hid_oem_rma_%{#mainIndex.index}_%{#subIndex.index}"  value="%{#rma}" />
             </td>
          </tr>
           <tr>
           <td>
              <s:text name="label.claim.dealerPickupLocation" />
           </td>
           <td valign="center" style="padding-top:6px; padding-bottom:6px;" align="left" class="partReplacedClass">
                <s:select cssStyle="width:90px;" list="task.claim.forDealer.orgAddresses" id='oemRepPart_%{#mainIndex.index}_%{#subIndex.index}_dealerPickUpLocation'
                    name="task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dealerPickupLocation" listKey="id" listValue="shipToCodeAppended" emptyOption="false"
                    cssClass="hussmannPartReplaced"
                    value="%{task.claim.serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[%{#mainIndex.index}].replacedParts[%{#subIndex.index}].partReturn.dealerPickupLocation.id}">
                </s:select>
           </td>
        </tr>
     </table>
