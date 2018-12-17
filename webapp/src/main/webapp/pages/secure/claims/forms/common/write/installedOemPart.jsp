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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<u:stylePicker fileName="batterytestsheet.css"/>
 <script type="text/javascript">
     dojo.require("dojox.layout.ContentPane");
            var indexCounter;
 			var jsonCounter='<s:property value="jsonCounter"/>';
 			var invCount=0;
 			var totalInvItems = '<s:property value="task.claim.claimedItems.size"/>';
 </script>
 <s:iterator value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced" status="status">
     <s:hidden name="initialReplacedParts[%{#status.index}]" value="%{id}"/>
 </s:iterator>

     <div class="mainTitle" style="margin-bottom:5px;">
       <s:text name="label.newClaim.oemPartReplaced"/></div>
<u:repeatTable id="oem_parts_replaced_table" cssClass="repeat borderForTable" width="97%">
    <thead>


        <tr class="row_head">
			<th align="center" width="20%"><s:text name="label.newClaim.partNumber"/></th>
			<%--Change for SLMS-776 adding date code --%>
			<th width="20%"><s:text name="label.common.dateCode"/></th>
			<th width="20%"><s:text name="label.common.quantity"/></th>
			<s:if test="loggedInUserAnInternalUser || (!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded'))">
			<th><s:text name="label.newClaim.unitPrice"/></th>
			</s:if>
			<s:if test="loggedInUserAnInternalUser && !task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
			<th><s:text name="label.newClaim.unitCostPrice"/></th>
			</s:if>
			<s:if test="!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
			<th><s:text name="label.uom.display"/></th>
			</s:if>
			<th width="31%"><s:text name="label.common.description"/></th>
			<s:if test="task.claim.forMultipleItems">
			<th ><s:text name="label.common.claim/inventoryLevel"/></th>
			</s:if>
			<s:if test="task.baseFormName == 'processor_review'">
			<th ><s:text name="label.partReturn.markPartForReturn"/></th>
			<th ><s:text name="columnTitle.dueParts.return_location"/></th>
			<th ><s:text name="columnTitle.partReturnConfiguration.paymentCondition"/></th>
			<th ><s:text name="label.common.dueDaysOrDate"/></th>
			</s:if>
			<th width="20%"><s:text name="label.common.additionalAttributes"/></th>
			<th width="9%" align="center"><u:repeatAdd id="oem_parts_adder"><div class="repeat_add"/></u:repeatAdd></th>
	</tr>


    </thead>
    <s:set name="startEditableOemPartsFrom" value="0"/>
    <s:set name="costType" value="getCostPriceType()"/>

    <u:repeatTemplate id="oem_parts_replaced_body"
        value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced" startFrom="#startEditableOemPartsFrom">
        <tr index="#index">


            <script type="text/javascript">
            dojo.addOnLoad(function() {
            	var indexCounter = #index;
            	dojo.subscribe("/oem_part/itemno/changed/#index", null, function(data, type, request) {
                    fillUnserializedOemPartDetails(#index, data, type);
                });

            });

			dojo.addOnLoad(function()
			{
				dojo.connect(dojo.byId("oempart_qty_#index"), "onchange", function(evt){
					var oemPartIndex = #index;
					if(dojo.byId("oempart_qty_"+oemPartIndex) && dojo.byId("oempart_qty_"+oemPartIndex).value)
					{
	 					dojo.byId("oempart_qty_"+oemPartIndex).value = Trim(dojo.byId("oempart_qty_"+oemPartIndex).value);
	 				}
	 			});
			});
            </script>
            <td width="10%">
                <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index]" />
                <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
            		<s:property value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredInventoryItem.serialNumber}' />
	                <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredInventoryItem"/>

	                <s:property value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem.alternateNumber}' />
	                <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem"/>
            	</s:if>
            	<s:else>
            	 <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem.duplicateAlternateNumber">
	                <sd:autocompleter id='oemPart_#index_itemNo' cssStyle='width:65px' href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem' value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem.number}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/oem_part/itemno/changed/#index' />
	             </s:if>
	             <s:else>
	             	<sd:autocompleter id='oemPart_#index_itemNo' cssStyle='width:65px' href='list_oem_part_itemnos.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem' value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem.alternateNumber}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/oem_part/itemno/changed/#index' />
	             </s:else>

	                    <authz:ifProcessor>
			                <s:if test="task.claim != null && task.claim.claimNumber !=null && task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].oemDealerPartReplaced != null">
			                <img  src="image/comments.gif" id= "replaced_oem_crossRef_part_#index"
					            		title="<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].oemDealerPartReplaced.number" />"
					            		alt="<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].oemDealerPartReplaced.number" />"/>
					        </s:if>
		                </authz:ifProcessor>
            	</s:else>
            </td>
            <%--Change for SLMS-776 adding date code --%>
            <td width="10%">
           		 <s:textfield size="2" cssStyle="text-align: right;padding-right: 1px;width:25px;" 
                       name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].dateCode"/>
            </td>
            <td width="10%">
            <s:if test="!%{isProcessorReview()}">
              	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
              		<s:textfield size="2" cssStyle="text-align: right;padding-right: 1px;width:25px;" id="oempart_qty_#index" disabled="true"
                       name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"/>
                   <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"
                       id="oempart_qty_#index_hidden"/>
              	</s:if>
              	<s:else>
              		<s:textfield size="2" cssStyle="text-align: right;padding-right: 1px;width:25px;" id="oempart_qty_#index"
                      	name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"/>
              	</s:else>
            </s:if>
            <s:else>
                <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
              		<s:textfield size="2" cssStyle="text-align: right;padding-right: 1px;width:25px;" id="oempart_qty_#index" disabled="true"
                       name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"/>
                   <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"
                       id="oempart_qty_#index_hidden"/>
              	</s:if>
              	<s:else>
              		<s:textfield size="2" cssStyle="text-align: right;padding-right: 1px;width:25px;" id="oempart_qty_#index"
                      	name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].numberOfUnits"/>
              	</s:else>
            </s:else>
            </td>
            <s:if test="loggedInUserAnInternalUser || (!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded'))">
                <td class="numeric">
                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].uomAdjustedPricePerUnit"/>
                </td>
            </s:if>
            <s:if test="loggedInUserAnInternalUser && !task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
                <td class="numeric">
                	<s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].getUomAdjustedCostPrice(#attr['costType'])"/>
                </td>
            </s:if>
            <s:if test="!task.claim.state.state.equalsIgnoreCase('Draft') && !task.claim.state.state.equalsIgnoreCase('Forwarded')">
	            <td>
	            	 <s:if test ="uomMapping != null && (uomMapping.mappedUom.length() > 0)  " >
	                   		<s:property  value="uomMapping.mappedUomDescription" />
	                   		<span id="UOM_<s:property value="#index"/>" tabindex="0" >
								<img src="image/comments.gif" width="16" height="15" />
							</span>

							<span dojoType="dijit.Tooltip" connectId="UOM_<s:property value="#index"/>" >
								<s:property  value="uomMapping.baseUom.type" />(<s:property value="pricePerUnit"/> )
							</span>
                   	  </s:if>
                   	  <s:else>
                   	  		<s:property  value="itemReference.referredItem.uom.type" />
                   	  </s:else>
	            </td>
            </s:if>

            <td width="10%" id="oempartdesc_#index">
                <span>
                    <s:property value="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].itemReference.referredItem.description"/>
                </span>
              </td>
            <s:if test="task.claim.forMultipleItems">
            <td width="10%">
            <table border="0">
            <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced.empty ||
            task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel==null">
            <tr>
            <td style="border:none">
            <span id="level_for_part_#index_true">
            <input type="radio"  value="true" id="inventory_level_#index" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span>
            </td>
            </tr>
            <tr>
            <td style="border:none">
            <span id="level_for_part_#index_false">
            <input type="radio" checked="checked" id="claim_level_#index" value="false" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr>
            </s:if>
            <s:else>
            <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel">
            <tr>
            <td style="border:none">
            <span id="level_for_part_#index_true">
            <input type="radio" checked="checked"  value="true" id="inventory_level_#index" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span>
            </td>
            </tr>
            <tr>
            <td style="border:none">
            <span id="level_for_part_#index_false">
            <input type="radio" id="claim_level_#index" value="false" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr>
            </s:if>
            <s:else>
            <tr>
            <td style="border:none">
             <span id="level_for_part_#index_true">
            <input type="radio"   value="true" id="inventory_level_#index" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="accordion_jsp.accordionPane.inventory"/>
            </span>
            </td>
            </tr>
            <tr>
            <td style="border:none">
            <span id="level_for_part_#index_false">
            <input type="radio" checked="checked" id="claim_level_#index" value="false" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel"/>
            <s:text name="claim.prieview.ContentPane.claim"/>
            </span>
            </td>
            </tr>
            </s:else>
            </s:else>
            </table>
            </td>
            </s:if>
            <s:else>
            <s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].inventoryLevel" value="false"/>
            </s:else>

           <s:if test="%{isProcessorReview()}">
           <td width="5%" align="center">
           <s:if test= "task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
               <s:checkbox disabled="true" id="oemPart_#index_toBeReturned" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned"
               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned}" onclick="alterValue(#index);"/>
               	<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned"
               	 value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned}"/>
           </s:if>
           <s:else>
               <s:checkbox  id="oemPart_#index_toBeReturned" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned"
               	value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partToBeReturned}" onclick="alterValue(#index);"/>
           </s:else>
           </td>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    alterValue(#index);
                });
	        </script>

            <td width="10%">
               	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
               	<s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation.code}"/>
               	<s:hidden id="oemPart_#index_location" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation"
               				value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation.id}"></s:hidden>
               	</s:if>
               	<s:else>
                <sd:autocompleter id='oemPart_#index_location' size='3' cssStyle='width:65px;' href='list_part_return_locations_for_part.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation' keyName='task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation' loadOnTextChange='true' showDownArrow='false' value='%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.returnLocation.code}' />
				</s:else>

		    </td>

            <td width="15%">
             	<s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
               	<s:property escape="false" value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.paymentCondition.description}"/>
               	<s:hidden id="oemPart_#index_paymentCondition" name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.paymentCondition"
               				value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.paymentCondition.code}"></s:hidden>
               	</s:if>
               	<s:else>
                <s:select list="paymentConditions" id="oemPart_#index_paymentCondition" cssStyle="width:70px;"
				name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.paymentCondition"
				listKey="code" listValue="description" emptyOption="true"
				value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.paymentCondition.code}" >
				</s:select>
				</s:else>
            </td>
            <td>
            <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isDueDaysReadOnly()">
					<s:property value="%{task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.dueDate}" />
				</s:if>
				<s:else >
					<s:hidden name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.dueDaysReadOnly" value="true"/>
					<s:textfield size="3" id="oemPart_#index_dueDays"
						name="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].partReturn.dueDays"></s:textfield>
				</s:else>
			</td>
           </s:if>
           <td width="10%">
           <s:a cssStyle="cursor:pointer" >
           <span id="partDetailsAttribute_#index">
           </span>
           </s:a>

           <div style="display: none">
	       	<div dojoType="twms.widget.Dialog" id="partAtrribute_#index" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
	       		toggleDuration="250" >
	       		<div dojoType="dijit.layout.LayoutContainer" style="height:225px;background: #F3FBFE; border: 1px solid #EFEBF7">
	       		 	<div dojoType="dojox.layout.ContentPane" id="partAttributeContentPane_#index" layoutAlign="top" executeScripts="true">
	       				<jsp:include flush="true" page="part-claim-attributes.jsp" />
	       			</div>
	       		</div>
	       	</div>
	       </div>
            </td>
            <td width="5%">
                <u:repeatDelete id="oem_parts_deleter_#index">
	                    <div class="repeat_del"/>
	                </u:repeatDelete>
                <s:if test="task.claim.serviceInformation.serviceDetail.oEMPartsReplaced[#index].isPartShippedOrCannotBeShipped()">
                    <script type="text/javascript">
                        dojo.html.hide(dojo.byId("oem_parts_deleter_"+#index));
                    </script>
                </s:if>
            </td>
        </tr>
    </u:repeatTemplate>
</u:repeatTable>