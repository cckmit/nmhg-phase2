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
<tr>
      <td width="95%" height="100%">

      <table width="95%" cellpadding="0" cellspacing="0" border="0">
            <tr>
                  <td>
                  <div dojoType="dijit.layout.ContentPane">
                  <div dojoType="twms.widget.TitlePane"
                        title="<s:text name="label.machineInfo"/>"
                        labelNodeClass="section_header" open="true">
                        <table cellspacing="0" cellpadding="0" border="0">
                        <tbody>
                              <tr>
                              
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.serialNumber" />:</td>

                                    <td class="" width="37%"><s:hidden
                                          id="%{qualifyId(\"inventoryIemSN\")}"
                                          name="%{#nListName}.inventoryItem" value="%{inventoryItem.id}" />
                                          <s:hidden
                                          id="%{qualifyId(\"indexFlag\")}"
                                          name="indexFlag" value="%{#nListIndex}" />
                                          <s:hidden
                                          id="%{qualifyId(\"nameFlag\")}"
                                          name="nameFlag" value="%{#nListName}" />
                                    <sd:autocompleter id='%{qualifyId("serialNumber")}' loadOnTextChange='false' loadMinimumCount='3' showDownArrow='false' indicator='indicator' value='%{inventoryItem.serialNumber}' listenTopics='/unitInvItemChanged/setSerialNumber/%{#nListIndex}' />
                                          <script type="text/javascript">
                                           dojo.addOnLoad(function() {
                                                var index = '<s:property value="%{#nListIndex}" />';                                   
                                          var url= "listAllRetailSerialsStartingWith.action";
                                           dojo.publish("/unitInvItemChanged/setSerialNumber/"+index, [{
                                                             url: url,
                                                             params: {
                                                             dealer: dojo.byId("dealerId").value
                                                      }
                                                      }]);
                                                             });
                              </script>
                                          
                                    </td>
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.model" />:</td>
                                    <td class="" width="35%"><s:label
                                          id="%{qualifyId(\"model\")}"
                                          value="%{inventoryItem.ofType.model.name}" /></td>

                              </tr>

                              <tr>
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.product" />:</td>
                                    <td class="" width="37%"><s:label
                                          id="%{qualifyId(\"product\")}"
                                          value="%{inventoryItem.ofType.product.name}" /></td>
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.shipmentDate" />:</td>
                                    <td class="" width="35%"><s:label
                                          id="%{qualifyId(\"shipment\")}"
                                          value="%{inventoryItem.shipmentDate}" /></td>
                              </tr>

                              <tr>
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.buildDate" />:</td>
                                    <td class="" width="37%"><s:label
                                          id="%{qualifyId(\"build\")}" value="%{inventoryItem.builtOn}" /></td>
                                     <s:if test="!buConfigAMER || inventoryItem.latestWarranty.pdiGenerated">
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.transferDate" />:</td>
                                    <td class="" width="35%"><sd:datetimepicker name='%{#nListName}.warrantyDeliveryDate' value='%{warrantyDeliveryDate}' id='%{qualifyId("deliveryDate")}' />
                                          <script type="text/javascript">
                        dojo.addOnLoad(function(){                     
                        dojo.connect(dijit.byId('<s:property value="qualifyId(\"deliveryDate\")" />'),"onChange",function(){                                                   
                        getAllPolicies('<s:property value="%{#nListIndex}"/>','<s:property value="%{#nListName}" />');                          
                        });
                       });

                        </script></td>
                        </s:if>
                              </tr>

                              <tr>
                                    <s:if test="isInstallingDealerEnabled()">
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.dateInstall" />:</td>
                                    <td class="" width="37%"><sd:datetimepicker name='%{#nListName}.installationDate' value='%{installationDate}' id='%{qualifyId("installationDateForUnit")}' />
                                    <script type="text/javascript">
                        dojo.addOnLoad(function(){                     
                        dojo.connect(dijit.byId('<s:property value="qualifyId(\"installationDateForUnit\")" />'),"onChange",function(){                                                                               
                        getAllPolicies('<s:property value="%{#nListIndex}"/>','<s:property value="%{#nListName}" />');                          
                        });
                        });                                       
                        </script></td></s:if>                     
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.hoursOnMachine" />:</td>
                                    <td class="" width="35%"><s:textfield
                                          id="%{qualifyId(\"hoursOnMachine\")}"
                                          name="%{#nListName}.inventoryItem.hoursOnMachine"
                                          value="%{inventoryItem.hoursOnMachine}"
                                          onchange="getAllPolicies('%{#nListIndex}','%{#nListName}')" /></td>
                              </tr>
                              <tr>
                                    <td width="20%" class="labelStyle" nowrap="nowrap"><s:text
                                          name="label.common.oem" />:</td>
                                    <td class="" width="37%"><s:label
                                          id="%{qualifyId(\"oem\")}"
                                          value="%{inventoryItem.brandType}" /></td>
                                          <td width="20%" class="labelStyle" nowrap="nowrap" colspan="2" style="border:0"></td>
                              </tr>                         
                              
                        </tbody>
                  </table>
                  </div>
                  </div>
                  </td>
            </tr>
            
            <tr>
                  <td>
                  <table width="100%"
                        id='<s:property value="qualifyId(\"policyDetailsTable\")" />'>
                        <tr>
                              <td>
                              <div dojoType="twms.widget.TitlePane"
                                    title="<s:text name="label.common.warrantyCoverage"/>"
                                    labelNodeClass="section_header" open="true">
                              <div dojoType="dijit.layout.ContentPane"
                                    id='<s:property value="qualifyId(\"policyDetails\")" />'><jsp:include
                                    page="warranty_transfer_policy_list.jsp" flush="true" /></div></div>
                              </td>
                        </tr>
                  </table>
                  </td>
            </tr>


            <tr>
            <td style="border:0">
                <div dojoType="twms.widget.TitlePane" title='<s:text name="label.disclaimerInfo"/>' labelNodeClass="section_header">
                <div dojoType="dijit.layout.ContentPane" id='<s:property value="qualifyId(\"disclaimerPage\")" />'>
                        <s:if test="dieselTierWaiver != null">
                              <jsp:include page="../disclaimer.jsp"  flush="true"/>
                              </s:if>
                              <s:else>
                                    <jsp:include page="../disclaimer_error.jsp"  flush="true"/>
                              </s:else>
                  </div>
                  </div>
             </td>
        </tr>

            <tr>
                  <td>

                  <div dojoType="twms.widget.TitlePane"
                        title="<s:text name="label.majorComponents"/>"
                        labelNodeClass="section_header" open="true">
                  <div dojoType="dijit.layout.ContentPane"
                        id='<s:property value="qualifyId(\"majorComponentDetails\")" />'>
                  <jsp:include page="../warranty_major_components.jsp"></jsp:include></div>
                  </div>
                  </td>
            </tr>
            	<tr>
            <s:if test="buConfigAMER">
			<td style="border:0">
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.additionalComponents"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='<s:property value="qualifyId(\"additionalComponentDetails\")" />'>
			<jsp:include page="../warranty_additional_components.jsp"></jsp:include></div>
			</div>
			</td>
			</s:if>
		</tr>
            
            <s:if test="isMarketInfoApplicable()">    
            <tr>
                  <td>
                  <div dojoType="twms.widget.TitlePane"
                              title="<s:text name="label.marketInfo"/>"
                              labelNodeClass="section_header" open="true">
                        <div dojoType="dojox.layout.ContentPane" executeScripts="true"
                              scriptSeparation="false"
                              id='<s:property value="qualifyId(\"marketInfo\")" />'>
                              <table width="100%"
                        id="<s:property value="qualifyId(\"marketInformationTable\")" />">
                        <s:if test="selectedMarketingInfo.size()>0">
                        <s:iterator
                              value="selectedMarketingInfo" status="selectedMarketingInfo">
                              <s:if test="#selectedMarketingInfo.odd == true ">
                                    <tr>
                              </s:if>
                              <td width="20%" class="labelStyle" nowrap="nowrap"><s:label
                                    value="%{addtlMarketingInfo.fieldName}" cssClass="labelStyle" /></td>
                              <td width="1%" class="labelStyle" nowrap="nowrap"><s:textfield
                                    cssStyle="display:none;"
                                    name='%{#nListName}.selectedMarketingInfo[%{#selectedMarketingInfo.index}].addtlMarketingInfo'
                                    value="%{addtlMarketingInfo}" /></td>
                              <td width="30%" class="labelStyle" nowrap="nowrap"><s:if
                                    test="addtlMarketingInfo.infoType.type.equalsIgnoreCase('DropDown')">

                                    <s:set name="selectOptions" value="addtlMarketingInfo.options" />
                                    <s:select
                                          name='%{#nListName}.selectedMarketingInfo[%{#selectedMarketingInfo.index}].value'
                                          list="selectOptions" listKey="optionValue"
                                          listValue="optionValue" headerKey=""
                                          headerValue="%{getText('label.common.selectHeader')}" />

                              </s:if> <s:else>
                                    <s:textfield
                                          name='%{#nListName}.selectedMarketingInfo[%{#selectedMarketingInfo.index}].value'
                                          id="%{qualifyId(\"selectedMarketingInfo\")}" value="%{value}"></s:textfield>

                              </s:else></td>
                              <s:if test="#selectedMarketingInfo.odd == false ">
                                    </tr>
                              </s:if>
                        </s:iterator>
                        </s:if>
                        <s:elseif test="marketingInfo.size()>0">
                        <s:include value="../marketInfo.jsp"></s:include>
                        </s:elseif>
                  </table></div>
                        </div>                  
                  </td>
            </tr>
            </s:if>
            
            <tr>
			<td style="border:0">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.warranty.supportDocs"/>" labelNodeClass="section_header" open="true">
				<jsp:include flush="true" page="../../warranty/fileUpload/uploadCommonAttachments.jsp"/>
				<jsp:include page="../../warranty/fileUpload/fileUploadDialogForNList.jsp"/>
			</div>
			</td>
		</tr>
		<s:if test="displayStockUnitDiscountDetails()">
		<tr>
			<td style="border:0">
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.title.stockUnitDiscount"/>" labelNodeClass="section_header" open="true">
				<jsp:include flush="true" page="../stock_unit_discount_detail.jsp"/>
			</div>
			</td>
		</tr>
		</s:if>
      </table>
      </td>
      <td width="5%">

      <div class="nList_delete" id="nListDelete_id_<s:property value="%{#nListIndex}" />" />
      </td>

</tr>
<script type="text/javascript">
dojo.addOnLoad(function(){    
var serialNumberAutoCompleter = dijit.byId('<s:property value="qualifyId(\"serialNumber\")" />');
serialNumberAutoCompleter.fireOnLoadOnChange=false;
dojo.connect(serialNumberAutoCompleter,"onChange",function(){
var index = '<s:property value="%{#nListIndex}" />';
var name = '<s:property value="%{#nListName}" />';
var serialNumber =dijit.byId('<s:property value="qualifyId(\"serialNumber\")" />').getDisplayedValue();     
var params={serialNumber:serialNumber,"inventoryItemMappingIndex":index,"type":"RETAIL"};
var url = "getDetailsForInventory.action?";                                   
twms.ajax.fireHtmlRequest(url, params, function(data) {                                   
var inventoryDetails = eval(data);                                                                                    
document.getElementById('<s:property value="qualifyId(\"product\")" />').innerHTML = inventoryDetails[0];
document.getElementById('<s:property value="qualifyId(\"model\")" />').innerHTML = inventoryDetails[1];
document.getElementById('<s:property value="qualifyId(\"shipment\")" />').innerHTML = inventoryDetails[2];
document.getElementById('<s:property value="qualifyId(\"build\")" />').innerHTML = inventoryDetails[3];    
document.getElementById('<s:property value="qualifyId(\"inventoryIemSN\")" />').value = inventoryDetails[4];
document.getElementById('<s:property value="qualifyId(\"hoursOnMachine\")" />').value = inventoryDetails[5];
document.getElementById('<s:property value="qualifyId(\"oem\")" />').innerHTML = inventoryDetails[6];
getAllPolicies(index,name);
getAllMajorComponents(index,name);
}); 
      });
});
dojo.addOnLoad(function(){    
      var serialNumberAutoCompleter = dijit.byId('<s:property value="qualifyId(\"serialNumber\")" />'); 
      serialNumberAutoCompleter.fireOnLoadOnChange=false;
      dojo.connect(serialNumberAutoCompleter,"onChange",function(){     
            var serialNumber =dijit.byId('<s:property value="qualifyId(\"serialNumber\")" />').getDisplayedValue();          
            var index = '<s:property value="%{#nListIndex}" />';
            var name = '<s:property value="%{#nListName}" />';    
            <s:if test="isMarketInfoApplicable()">                      
            getMarketInfo(index,name,serialNumber);
            </s:if>
      });
      
 	<s:if test = "buConfigAMER">
  		var nListDeleteButton = dojo.byId("nListDelete_id_<s:property value="%{#nListIndex}" />");
  		if(nListDeleteButton) {
  			dojo.connect(nListDeleteButton,"onclick",function() {
  				var addRowButton = dojo.byId("nListAdd_id");
  				dojo.html.show(addRowButton);
  			});
  		}
  	</s:if>
});
</script>

