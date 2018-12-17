<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 2:55:57 PM
  To change this template use File | Settings | File Templates.
--%>
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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<s:if test="inventoryItemIndex == -1">
    <s:set name="firstTime" value="true"/>
    <s:set name="inventoryItemIndex" value="#inventoryItemMappings.index" />
</s:if>
<s:else>
    <s:set name="inventoryItemIndex" value="inventoryItemIndex" />
</s:else>
<s:if test="availablePolicies.empty">
	<span style="color:red">
		<s:text name="error.transferPlan.noPlan" />
	</span>
</s:if>
<s:else>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            dojo.addOnLoad(function() {
            var inventoryItemId = <s:property value="inventoryItem.id"/>;
            var policyListSize = <s:property value="availablePolicies.size"/>;
            dojo.connect(dojo.byId("masterCheckbox_policy_"+inventoryItemId),"onclick",function(){
                if (dojo.byId("masterCheckbox_policy_"+inventoryItemId).checked) {
                    for (var i = 0; i < policyListSize; i++) {
                        dojo.byId("policy_"+inventoryItemId+"_"+ i).checked = true;
                    }
                } else {
                    for (var i = 0; i < policyListSize; i++) {
                        dojo.byId("policy_"+inventoryItemId+"_"+ i).checked = false;
                    }
                }
            });
        });
        });
    </script>

<table width="98%" cellpadding="1" cellspacing="0" id="policies" class="grid borderForTable">
        <thead>
            <tr class="row_head">
                <th class="warColHeader" align="left" width="2%" align="center">
                    <input type="checkbox" id="masterCheckbox_policy_<s:property value="%{inventoryItem.id}"/>" class="policy_checkboxes"/>
                </th>
                <th class="warColHeader" align="left"><s:text name="label.planName"/></th>
            </tr>
        </thead>
        <tbody id="policy_list">
            <s:iterator value="availablePolicies" status="availablePolicies">
            <tr>
                <td align="center">
                    <s:if test="#firstTime">
                    <s:checkbox id = "policy_%{inventoryItem.id}_%{#availablePolicies.index}"
                        name="inventoryItemMappings[%{#inventoryItemIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
                        cssClass="policy_checkboxes"
                        fieldValue="%{policyDefinition.id}"
                        value="true" />
                    </s:if>
                    <s:else>
                    <s:checkbox id = "policy_%{inventoryItem.id}_%{#availablePolicies.index}"
                        name="inventoryItemMappings[%{#inventoryItemIndex}].selectedPolicies[%{#availablePolicies.index}].policyDefinition"
                        cssClass="policy_checkboxes"
                        fieldValue="%{policyDefinition.id}"
                        value="true"
                        />
                    </s:else> 
                    <s:hidden
                            name="inventoryItemMappings[%{#inventoryItemIndex}].availablePolicies[%{#availablePolicies.index}].policyDefinition"
                            value="%{policyDefinition.id}" />
                </td>
                <td><u:openTab autoPickDecendentOf="true"
                               id="policy_%{inventoryItem.id}_%{policyDefinition.id}"
                               tabLabel="Policy %{policyDefinition.code}"
                               url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                    <s:if test="policyDefinition.termsAndConditions !=null">
                        <s:hidden name="termsAndConditions"
                                  value="%{policyDefinition.termsAndConditions}"
                                  id="terms_%{inventoryItem.id}_%{policyDefinition.id}"/>
                    <img id="termsAndCondition_<s:property value="%{inventoryItem.id}" />_<s:property value="%{policyDefinition.id}" />"
                         src="image/advSearchIcon.gif" />
                    <script type="text/javascript">
                        dojo.addOnLoad(function(){
                                var invId = '<s:property value="%{inventoryItem.id}" />';
                                var policyId = '<s:property value="%{policyDefinition.id}" />';
                                dojo.connect(dojo.byId("termsAndCondition_" + invId + "_" + policyId), "onclick", function() {
                                    dijit.byId("dialogBoxTermsAndCondition").setContent(
                                            dojo.byId("terms_" + invId + "_" + policyId).value);
                                    dijit.byId("dialogBoxTermsAndCondition").show();
                                });
                         });
                        </script>
                </s:if>
                </td>
            </tr>
            </s:iterator>
        </tbody>
    </table>
</s:else>
