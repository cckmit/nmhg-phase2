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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="t" uri="twms" %>
<style>
.linkColor{
color:blue;
cursor:pointer;
text-decoration:underline;
}
td{
padding-bottom:10px;
}
.grid td{
background:none;
}
	.dijitTextBox{width:130px;}

</style>
<script type="text/javascript">

	function updateTermsAndConditions(val){		
		dojo.byId("termsAndConditions").value = val;
	}
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.addOnLoad(function() {
        var warrantyType = '<s:property value="policyDefinition.warrantyType"/>';
        var definitionId = '<s:property value="policyDefinitionId"/>';
        <s:if test="policyDefinitionId==null">
        dojo.byId("applicableField").checked=true;
        </s:if>
        if (warrantyType == "POLICY") {
            dojo.byId("serviceField").value = '';
            dojo.byId("monthsRegField").value = '';
            if(dojo.byId("monthsBuildField")){
            	dojo.byId("monthsBuildField").value = '';
            }
            if(dojo.byId("monthsOrgnlField")){
            	dojo.byId("monthsOrgnlField").value = '';
            }
            dojo.byId("monthsShipField").value = '';
            dojo.byId("monthsShipForEWPField").value = '';
            dojo.byId("monthsRegForEWPField").value = '';
            dojo.html.hide(dojo.byId("serviceField"));
            dojo.html.hide(dojo.byId("serviceLabel"));
            dojo.html.hide(dojo.byId("deliveryDateRange"));
            dojo.html.hide(dojo.byId("buildDate"));
            dojo.html.hide(dojo.byId("originalDeliveryDate"));
            dojo.html.hide(dojo.byId("shipmentDateRange"));
            dojo.html.hide(dojo.byId("shipmentRangeForEWP"));
            dojo.html.hide(dojo.byId("deliveryRangeForEWP"));
            dojo.html.hide(dojo.byId("deliveryRangeToStartEWP"));
            dojo.html.hide(dojo.byId("forInternalUsersOnly"));
            dojo.html.hide(dojo.byId("attachmentMandatory"));
            dojo.html.hide(dojo.byId("tblSelectedDealers"));
            dojo.html.hide(dojo.byId("divDealersCoveredTitle"));     
            dojo.html.hide(dojo.byId("forcePolicyOnWR"));
            dojo.html.hide(dojo.byId("forcePolicyOnWRLabel"));  
        }else {
            dojo.html.show(dojo.byId("serviceField"));
            dojo.html.show(dojo.byId("serviceLabel"));
            dojo.html.show(dojo.byId("deliveryDateRange"));
            dojo.html.show(dojo.byId("buildDate"));
            dojo.html.show(dojo.byId("originalDeliveryDate"));
            dojo.html.show(dojo.byId("shipmentDateRange"));
            dojo.html.show(dojo.byId("tblSelectedDealers"));
            dojo.html.show(dojo.byId("divDealersCoveredTitle"));  
            if (warrantyType == "EXTENDED") {
                dojo.html.show(dojo.byId("shipmentRangeForEWP"));
                dojo.html.show(dojo.byId("deliveryRangeForEWP"));
                dojo.html.hide(dojo.byId("forcePolicyOnWR"));
                dojo.html.hide(dojo.byId("forcePolicyOnWRLabel"));
            }else{
                dojo.html.hide(dojo.byId("shipmentRangeForEWP"));
                dojo.html.hide(dojo.byId("deliveryRangeForEWP"));
                dojo.html.hide(dojo.byId("deliveryRangeToStartEWP"));
                dojo.html.hide(dojo.byId("forInternalUsersOnly"));
                dojo.html.hide(dojo.byId("attachmentMandatory"));
                dojo.html.show(dojo.byId("forcePolicyOnWR"));
                dojo.html.show(dojo.byId("forcePolicyOnWRLabel"));
            }
        }
        if (dijit.byId("warrantyType")) {
            dojo.connect(dijit.byId("warrantyType"), "onChange", function() {
                if (dijit.byId("warrantyType").getValue() == "POLICY") {
                    dojo.byId("serviceField").value = '';
                    dojo.byId("monthsRegField").value = '';
                    if(dojo.byId("monthsBuildField")){
                    	dojo.byId("monthsBuildField").value = '';
                    }
                    if(dojo.byId("monthsOrgnlField")){
                    	dojo.byId("monthsOrgnlField").value = '';
                    }
                    dojo.byId("monthsShipField").value = '';
                    dojo.html.hide(dojo.byId("serviceField"));
                    dojo.html.hide(dojo.byId("serviceLabel"));
                    dojo.html.hide(dojo.byId("deliveryDateRange"));
                    dojo.html.hide(dojo.byId("buildDate"));
                    dojo.html.hide(dojo.byId("originalDeliveryDate"));
                    dojo.html.hide(dojo.byId("shipmentDateRange"));
                    dojo.byId("monthsShipForEWPField").value = '';
                    dojo.byId("monthsRegForEWPField").value = '';
                    dojo.html.hide(dojo.byId("shipmentRangeForEWP"));
                    dojo.html.hide(dojo.byId("deliveryRangeForEWP"));
                    dojo.html.hide(dojo.byId("deliveryRangeToStartEWP"));
                    dojo.html.hide(dojo.byId("forInternalUsersOnly"));
                    dojo.html.hide(dojo.byId("attachmentMandatory"));
                    dojo.html.hide(dojo.byId("tblSelectedDealers"));
                    dojo.html.hide(dojo.byId("divDealersCoveredTitle"));  
                    dojo.html.hide(dojo.byId("forcePolicyOnWR"));
                    dojo.html.hide(dojo.byId("forcePolicyOnWRLabel"));
                } else {
                    dojo.html.show(dojo.byId("serviceField"));
                    dojo.html.show(dojo.byId("serviceLabel"));
                    dojo.html.show(dojo.byId("deliveryDateRange"));
                    dojo.html.show(dojo.byId("buildDate"));
                    dojo.html.show(dojo.byId("originalDeliveryDate"));
                    dojo.html.show(dojo.byId("shipmentDateRange"));
                    dojo.html.show(dojo.byId("tblSelectedDealers"));
                    dojo.html.show(dojo.byId("divDealersCoveredTitle"));  
                    if (dijit.byId("warrantyType").getValue() == "EXTENDED") {
                        dojo.html.show(dojo.byId("shipmentRangeForEWP"));
                        dojo.html.show(dojo.byId("deliveryRangeForEWP"));
                        dojo.html.show(dojo.byId("deliveryRangeToStartEWP"));
                        dojo.html.show(dojo.byId("forInternalUsersOnly"));
                        dojo.html.show(dojo.byId("attachmentMandatory"));
                        dojo.html.hide(dojo.byId("forcePolicyOnWR"));
                        dojo.html.hide(dojo.byId("forcePolicyOnWRLabel"));
                    } else {
                        dojo.byId("monthsShipForEWPField").value = '';
                        dojo.byId("monthsRegForEWPField").value = '';
                        dojo.byId("monthsRegToStartEWPField").value = '';
                        dojo.byId("forInternalUsersOnlyField").checked = false;
                        dojo.byId("attachmentMandatoryField").checked = false;
                        dojo.html.hide(dojo.byId("shipmentRangeForEWP"));
                        dojo.html.hide(dojo.byId("deliveryRangeForEWP"));
                        dojo.html.hide(dojo.byId("deliveryRangeToStartEWP"));
                        dojo.html.hide(dojo.byId("forInternalUsersOnly"));
                        dojo.html.hide(dojo.byId("attachmentMandatory"));
                        dojo.html.show(dojo.byId("forcePolicyOnWR"));
                        dojo.html.show(dojo.byId("forcePolicyOnWRLabel"));
                    }
                }
            });
            dojo.connect(dijit.byId("warrantyType"), "onChange", function() {
            	 
            	
            	var rowDealer = dojo.query('*[name="dealer_group_row"]');
            	for(var x = 0; x < rowDealer.length; x++){
                 dojo.dom.destroyNode(rowDealer[x]);
               }
            	
                if (dijit.byId("warrantyType").getValue() == "POLICY") {
                	var rowDealer = dojo.query('*[name="dealer_row"]');
                	for(var x = 0; x < rowDealer.length; x++){
                     dojo.dom.destroyNode(rowDealer[x]);
                   }
                	dijit.byId("dealerAutocompleter").setDisabled(true);                	
                    dojo.html.hide(dojo.byId("toggleToDealerGroup"));
                    dojo.html.hide(dojo.byId("forcePolicyOnWR"));
                    dojo.html.hide(dojo.byId("forcePolicyOnWRLabel"));
                    dojo.byId("applicableField").checked = false;
                    dojo.byId("forcePolicyOnWR").checked = false;
                } else {
                    dojo.byId("applicableField").checked=false;
                    if(dijit.byId("warrantyType").getValue() == "STANDARD"){
					dojo.html.show(dojo.byId("forcePolicyOnWR"));
                    dojo.html.show(dojo.byId("forcePolicyOnWRLabel"));
                    dojo.byId("applicableField").checked=true;
                    }
                }
            });
        }
    });


</script>
<div dojoType="twms.widget.TitlePane" title="<s:property value="%{getText('label.managePolicy.policyDetails')}"/>"
	 labelNodeClass="section_header" open="true" id="policy_details">
<table border="0" cellspacing="0" cellpadding="0">
  <tbody>
			<tr>
				<td nowrap="nowrap" class="labelStyle" width="20%"><s:property
						value="%{getText('label.managePolicy.code')}" /> :</td>
				<td width="30%"><s:if test="policyDefinitionId!=null">
						<div style="padding-left: 5px;">
							<s:property value="policyDefinition.code" />
						</div>
						<s:hidden name="policyDefinitionId" />
					</s:if> <s:else>
						<s:textfield name="policyDefinition.code" />
					</s:else></td>
				<td width="20%" nowrap="nowrap" class="labelStyle"><s:text
						name="label.common.nomsPolicyOptionCode" /> :</td>
				<td width="30%"><s:textfield name="policyDefinition.nomsPolicyOptionCode" />
				</td>
			</tr>
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:property
						value="%{getText('label.managePolicy.name')}" /> :</td>
				<td><s:textfield name="policyDefinition.description" /></td>
				<td nowrap="nowrap" class="labelStyle"><s:text
						name="label.common.nomsTierDescription" /> :</td>
				<td><s:textfield name="policyDefinition.nomsTierDescription" />
				</td>
			</tr>
			<tr>
      <td nowrap="nowrap" class="labelStyle"><s:property
				value="%{getText('label.managePolicy.dealerCertificationStatus')}" />
        : </td>
      <td><s:if test="policyDefinitionId!=null">
          <div style="padding-left:5px;">
          <s:property value='policyDefinition.certificationStatus' />
          </div>
        </s:if>
        <s:else>
          <div>
            <s:select theme="twms" id="dealerCertificationStatus"
					name="policyDefinition.certificationStatus"
					 list="certificationTypes" cssStyle="width:130px;" />
          </div>
        </s:else></td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle"><s:property value="%{getText('label.managePolicy.warrantyType')}"/>
        : </td>
      <td><s:if test="policyDefinitionId!=null">
          <div style="padding-left:5px;">
          <s:label value="%{getText(policyDefinition.warrantyType.displayValue)}" id="warrantyTypeProp"/>
          </div>
        </s:if>
        <s:else>
          <div>
          <s:if test="hasActionErrors()">
            <s:select name="policyDefinition.warrantyType" theme="twms" list="warrantyTypes"
                         id="warrantyType" cssStyle="width:130px;" listkey="type" listValue="%{getText(displayValue)}" value="%{policyDefinition.warrantyType}"/>
          </s:if>
          <s:else>
          <s:select name="policyDefinition.warrantyType" theme="twms" list="warrantyTypes"
                         id="warrantyType" cssStyle="width:130px;" listkey="type" listValue="%{getText(displayValue)}" value="%{'POLICY'}"/>
          </s:else>
          </div>
        </s:else>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle" id="serviceLabel"><s:property value="%{getText('label.managePolicy.serviceHoursCovered')}"/>
        : </td>
      <td><s:textfield id="serviceField" cssClass="admin_selections"
                     name="policyDefinition.coverageTerms.serviceHoursCovered"/>
      </td>
    </tr>
    <tr id="forInternalUsersOnly">
      <td nowrap="nowrap" class="labelStyle"><s:text name="label.managePolicy.forInternalUsersOnly"/>
      </td>
      <td nowrap="nowrap" class="labelStyle" ><s:checkbox id="forInternalUsersOnlyField" name="policyDefinition.forInternalUsersOnly"></s:checkbox>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle"><s:property value="%{getText('label.warrantyAdmin.customerType')}"/>
        : </td>
      <td><s:select name="selectedCustomerTypes" list="allCustomerTypes"
                  multiple="true" size="5" cssStyle="width:130px;" />
      </td>
    </tr>
    <%-- <tr>
      <td nowrap="nowrap" class="labelStyle"><s:text name="title.policy.buildDateApplicable"></s:text>
      </td>
      <td><s:checkbox name="policyDefinition.buildDateApplicable"></s:checkbox>
      </td>
    </tr> --%>
    <tr id="attachmentMandatory">
      <td nowrap="nowrap" class="labelStyle"><s:text name="label.managePolicy.attachmentMandatory"/>
      </td>
      <td nowrap="nowrap" class="labelStyle" ><s:checkbox id="attachmentMandatoryField" name="policyDefinition.attachmentMandatory"></s:checkbox>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle"><s:text name="title.policy.isThirdPartyPolicy"/>
      </td>
      <td ><s:checkbox name="policyDefinition.isThirdPartyPolicy"></s:checkbox>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle" id="applicableLabel"><s:text name="title.policy.isPolicyApplicable"/>
      </td>
      <td nowrap="nowrap" class="labelStyle" ><s:checkbox name="policyDefinition.isPolicyApplicableForWr" id="applicableField"></s:checkbox>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle" id="invisibleDRLabel"><s:text name="title.policy.isInvisibleFilingDR"/>
      </td>
      <td nowrap="nowrap" class="labelStyle" ><s:checkbox name="policyDefinition.invisibleFilingDr" id="invisibleDRField"></s:checkbox>
      </td>
    </tr>
    <tr>
      <td nowrap="nowrap" class="labelStyle" id="forcePolicyOnWRLabel"><s:text name="title.policy.isPolicyForcedOnWR"/>
      </td>
      <td nowrap="nowrap" class="labelStyle" ><s:checkbox name="policyDefinition.isPolicyForcedOnWr" id="forcePolicyOnWR"></s:checkbox>
      </td>
    </tr>    
    <tr id="deliveryDateRange">
      <td  id="monthsRegLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromRegistration')}"/>
        : </td>
      <td   valign="top"><s:textfield cssClass="admin_selections" id="monthsRegField"
                     name="policyDefinition.coverageTerms.monthsCoveredFromDelivery" theme="twms"/>
      </td>
    </tr>
     <%-- <tr id="originalDeliveryDate">
      <td  id="monthsOrgnlDeliveryLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromDateOfOriginal')}"/>
        : </td>
      <td   valign="top"><s:textfield cssClass="admin_selections" id="monthsOrgnlField"
                     name="policyDefinition.coverageTerms.monthsCoveredFromOriginalDeliveryDate" theme="twms"/>
      </td>
    </tr>
    <tr id="buildDate">
      <td  id="monthsBuildLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromDateOfBuild')}"/>
        : </td>
      <td   valign="top"><s:textfield cssClass="admin_selections" id="monthsBuildField"
                     name="policyDefinition.coverageTerms.monthsCoveredFromBuildDate" theme="twms"/>
      </td>
    </tr> --%>
    <tr id="shipmentDateRange">
      <td  id="monthsShipLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromDateOfShipment')}"/>
        : </td>
      <td  valign="top"><s:textfield cssClass="admin_selections" id="monthsShipField"
                     name="policyDefinition.coverageTerms.monthsCoveredFromShipment"theme="twms"/>
      </td>
    </tr>
    <tr id="deliveryRangeToStartEWP">
      <td  id="monthsRegToEndEWPLabel" class="labelStyle"  nowrap="nowrap"><s:text
            name="label.managePolicy.minMonthsFromDeliveryForEWP"/>
        : </td>
      <td valign="top"><s:textfield cssClass="admin_selections" id="monthsRegToStartEWPField"
                     name="policyDefinition.coverageTerms.minMonthsFromDeliveryForEWP" />
      </td>
    </tr>
    <tr  id="deliveryRangeForEWP">
      <td  id="monthsRegForEWPLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromRegistrationForEWP')}"/>
        : </td>
      <td valign="top"><s:textfield cssClass="admin_selections" id="monthsRegForEWPField"
                     name="policyDefinition.coverageTerms.monthsFromDeliveryForEWP" />
      </td>
    </tr>
    <tr  id="shipmentRangeForEWP">
      <td  id="monthsShipForEWPLabel" class="labelStyle"  nowrap="nowrap"><s:property
            value="%{getText('label.managePolicy.monthsCoveredFromDateOfShipmentForEWP')}"/>
        : </td>
      <td  valign="top"><s:textfield cssClass="admin_selections" id="monthsShipForEWPField"
                     name="policyDefinition.coverageTerms.monthsFromShipmentForEWP" />
      </td>
    </tr>
    <tr>
      <td class="labelStyle"  nowrap="nowrap"><s:property value="%{getText('label.managePolicy.activeFrom')}"/>
        : </td>
      <td><div >
          <sd:datetimepicker name='policyDefinition.availability.duration.fromDate' value='%{policyDefinition.availability.duration.fromDate}' id='fromDate' />
        </div></td>
    </tr>
    <tr>
      <td class="labelStyle"  nowrap="nowrap"><s:property value="%{getText('label.managePolicy.activeTill')}"/>
        : </td>
      <td><div >
          <sd:datetimepicker name='policyDefinition.availability.duration.tillDate' value='%{policyDefinition.availability.duration.tillDate}' id='tillDate' />
        </div></td>
    </tr>
    <tr>
      <td class="labelStyle"  nowrap="nowrap"><s:property value="%{getText('label.managePolicy.comments')}"/>
        : </td>
      <td>&nbsp;
      <%-- Commnet box should be blank on page load , refer to NMHGSLMS-59 --%>
        <t:textarea name="policyDefinition.comments" cols="35" rows="2" value=""
        cssClass="admin_selections"  />
      </td>
    </tr>
    <tr>
      <td class="labelStyle"  nowrap="nowrap"><s:text name="label.managePolicy.priority"/>
        : </td>
      <td><s:if test="policyDefinitionId==null">
          <s:textfield name="policyDefinition.priority" cssClass="admin_selections"/>
        </s:if>
        <s:else>
          <div>&nbsp;
            <s:property value="policyDefinition.priority"/>
          </div>
        </s:else>
      </td>
    </tr>
    <tr>
      <td class="labelStyle"  nowrap="nowrap"><s:text name="label.policy.termsAndConditions"/>
        : </td>
      <td width="30%">&nbsp;
        <t:textarea name="policyDefinition.i18NPolicyTermsAndConditions[0].termsAndConditions" cols="35" rows="2"
                    value="%{policyDefinition.termsAndConditions}" onchange="updateTermsAndConditions(this.value)"/>
        &nbsp;&nbsp;
        <s:hidden name="localizedFailureMessages_%{getLoggedInUser().locale}" id="termsAndConditions"/>
        <s:if test="policyDefinitionId!=null">
          <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="internationalizeTerms" tagType="a" cssClass="inventory_folder folder"
                           tabLabel="%{getText('label.common.internationalize')}"
                           url="i18nTermsAndConditions.action?policyDefinitionId=%{policyDefinitionId}&type=%{type}&ownerState=%{ownerState}"
                           catagory="campaign_info">
            <s:text name="label.common.internationalize"/>
          </u:openTab>
        </s:if>
        <s:hidden name="policyDefinition.i18NPolicyTermsAndConditions[0].locale" value="en_US"/>
      </td>
    </tr>
  </tbody>
</table>
</div>
