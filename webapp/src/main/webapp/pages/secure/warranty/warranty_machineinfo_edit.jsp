<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="authz" uri="authz"%>


<table cellspacing="0" cellpadding="0" id="equipment_details_table" class="grid borderForTable" style="width:100%;">
    <thead>
        <tr>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.product"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.itemCondition"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.deliveryDate"/></th>
            <s:if test="isInstallingDealerEnabled()">
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.dateInstall"/></th>
			</s:if>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.hoursOnMachine"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.shipmentDate"/></th>
        </tr>
    </thead>
    <tbody>        
        <tr>
            <td>
                <u:openTab autoPickDecendentOf="true" id="equipment1"
                tabLabel="Serial Number %{warranty.forItem.serialNumber}"
                url="inventoryDetail.action?id=%{warranty.forItem.id}">
                    <u style="cursor: pointer;">
                        <s:property value="warranty.forItem.serialNumber" />
                        <s:hidden id="equipment" value="%{warranty.forItem.id}" />
                        <s:hidden id="warranty" value="%{warranty.id}"/>
                    </u>
                </u:openTab>
            </td>
            <td>
                <s:property value="warranty.forItem.ofType.product.name"/>
            </td>
            <td>
                <s:property value="warranty.forItem.ofType.model.name" />
            </td>
            <td>
                <s:property value="warranty.forItem.conditionType.itemCondition" />
            </td>
            <authz:ifUserInRole roles="admin">
            <td>
            	<s:if test="warranty.transactionType.getTrnxTypeKey() == 'DR' || warranty.transactionType.getTrnxTypeKey() == 'DEMO' || warranty.transactionType.getTrnxTypeKey() == 'DR_MODIFY' ">
                	<sd:datetimepicker id="deliveryDate" name="warranty.forItem.deliveryDate" value="%{warranty.forItem.deliveryDate}" onchange="getAllPoliciesForEdit(0,0)"/>
                </s:if>
                <s:else>
                	<sd:datetimepicker id="deliveryDate_ETR" name="warranty.deliveryDate" value="%{warranty.deliveryDate}" />
                </s:else>
            </td>
            </authz:ifUserInRole>
            <authz:ifUserNotInRole roles="admin">
	            <td>
	                <s:property value="warranty.deliveryDate" />
	            </td>
            </authz:ifUserNotInRole>
            <s:if test="isInstallingDealerEnabled()">
             <authz:ifUserInRole roles="admin">
							<td style="border:0">
							<sd:datetimepicker name='warranty.forItem.installationDate' value='%{warranty.forItem.installationDate}' id='installationDateForUnit' onchange="getAllPoliciesForEdit(0,0)"/>
							</td>
						
             </authz:ifUserInRole>
             <authz:ifUserNotInRole roles="admin">
	            <td>
	                <s:property value="warranty.forItem.installationDate" />
	            </td>
            </authz:ifUserNotInRole>
            </s:if>	
            <td>
                <s:property value="warranty.forItem.hoursOnMachine"/>
            </td>
            <td>
                <s:property value="warranty.forItem.shipmentDate" />
            </td>  
           
        </tr>
        </tbody>
        </table>
<table cellspacing="0" cellpadding="0" id="equipment_details_table" class="grid borderForTable" style="width:100%;">
        <tbody>
        <tr>
        <td>
                   <div dojoType="twms.widget.TitlePane" title='<s:text name="warranty.transfer.coverage"/>' labelNodeClass="section_header">
            				<div dojoType="dijit.layout.ContentPane" 
					id='policies'>	
                   <jsp:include page="policy_list_for_edit.jsp"></jsp:include>
                   </div>
                   </div>
                   </td>
                   
       <!--      <td style="border:0">
				<div dojoType="dijit.layout.ContentPane" 
					id='policies'>				
                 <table width="98%" cellpadding="0" cellspacing="0"  class="grid borderForTable">
			        <thead>
			            <tr>
			                <th class="warColHeader" align="left"><s:text name="label.planName"/></th>			                
			            </tr>
			        </thead>
			        <tbody id="policy_lists">
			            <s:iterator value="warranty.policies" status="availablePolicies">
			            <tr>
                            <td>
                                <u:openTab autoPickDecendentOf="true"
                                           id="policy_%{policyDefinition.id}"
                                           tabLabel="Policy %{policyDefinition.code}"
                                           url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                            </td>
			            </tr>
			            </s:iterator>
			            

			          <authz:ifUserInRole roles="admin">
			           <s:if test="!isDeliveryReport()">
			           	<s:iterator value="getTransferablePoliciesFromPrevWarranty()" status="missedOutCoverages">
			            <tr>
			                <td>
                                <u:openTab autoPickDecendentOf="true"
                                           id="policy_%{policyDefinition.id}"
                                           tabLabel="Policy %{policyDefinition.code}"
                                           url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                            </td>			                
			            </tr>
			            </s:iterator>
			           </s:if> 
			           </authz:ifUserInRole>
			        </tbody>
			    </table>
</div> !-->
					     <td>
                   </td>
        
        </tr>
        <tr>
			<td style="border:0">

			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.majorComponents"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='<s:property value="qualifyId(\"majorComponentDetails\")" />'>
			<jsp:include page="warranty_major_components.jsp"></jsp:include></div>
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
			<jsp:include page="warranty_additional_components.jsp"></jsp:include></div>
			</div>
			</td>
			</s:if>
		</tr>
	<%-- <s:if test="isMarketInfoApplicable()">
		<tr>
			<td style="border:0">
			<div dojoType="twms.widget.TitlePane" id="marketingInformation"
					title="<s:text name="label.marketInfo"/>"
					labelNodeClass="section_header" open="true">
				<div dojoType="dojox.layout.ContentPane" executeScripts="true"
					scriptSeparation="false"
					id="marketInfo">
					<table width="100%"
				id="marketInformationTable" border="0">
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
						name='selectedMarketingInfo[%{#selectedMarketingInfo.index}].addtlMarketingInfo'
						value="%{addtlMarketingInfo}" /></td>
					<td width="30%" class="labelStyle" nowrap="nowrap"><s:if
						test="addtlMarketingInfo.infoType.type.equalsIgnoreCase('DropDown')">

						<s:set name="selectOptions" value="addtlMarketingInfo.options" />
						<s:select
							name='selectedMarketingInfo[%{#selectedMarketingInfo.index}].value'
							list="selectOptions" listKey="optionValue"
							listValue="optionValue" headerKey=""
							headerValue="%{getText('label.common.selectHeader')}" />

					</s:if>
					<s:else>
					<s:textfield
							name='selectedMarketingInfo[%{#selectedMarketingInfo.index}].value'
							id="selectedMarketingInfo" value="%{value}"></s:textfield>
					</s:else>
					</td>
					<s:if test="#selectedMarketingInfo.odd == false ">
						</tr>
					</s:if>
				</s:iterator>
				</s:if>
				<s:elseif test="marketingInfo.size()>0">
				<s:include value="marketInfo.jsp"></s:include>
				</s:elseif>
				</table></div>
				</div>			
			</td>
		</tr>
		</s:if> --%>	
   </tbody>

</table>