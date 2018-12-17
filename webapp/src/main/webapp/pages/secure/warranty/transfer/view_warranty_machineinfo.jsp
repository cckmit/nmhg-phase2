<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<div style="background-color:#F3FBFE; border:1px solid #EFEBF7; margin-top:10px;">
<table cellspacing="0" cellpadding="0" id="equipment_details_table" class="grid borderForTable" style="width:99%;">
    <thead>
        <tr>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.product"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.itemCondition"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.viewDR.submittedDate"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.deliveryDate"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.hoursOnMachine"/></th>
            <th class="warColHeader non_editable" width="25%" ><s:text name="warranty.transfer.coverage"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.shipmentDate"/></th>
        </tr>
    </thead>
    <tbody>        
        <tr>
            <td>
                <u:openTab autoPickDecendentOf="true" id="equipment"
                tabLabel="Serial Number %{warranty.forItem.serialNumber}"
                url="inventoryDetail.action?id=%{warranty.forItem.id}">
                    <u style="cursor: pointer;">
                        <s:property value="warranty.forItem.serialNumber" />
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
            <td>
            	<s:property value="warranty.filedDate" />
            </td>
            <td>
                <s:property value="warranty.deliveryDate" />
            </td>
            <td>
                <s:property value="warranty.forItem.hoursOnMachine"/>
            </td>
            <td>
                <table width="98%" cellpadding="0" cellspacing="0" id="policies"  align="center">
			        <thead>
			            <tr>
			                <th class="warColHeader" align="left"><s:text name="label.planName"/></th>			                
			            </tr>
			        </thead>
			        <tbody id="policy_list">
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
			        </tbody>
			    </table>
            </td>
            <td>
                <s:property value="warranty.forItem.shipmentDate" />
            </td>            
        </tr>
   </tbody>
</table>
</div>