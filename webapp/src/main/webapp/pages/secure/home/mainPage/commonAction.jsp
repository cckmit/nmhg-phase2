<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 20, 2008
  Time: 6:00:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<head>
</head>
<!-- Stock and Inventory Listing Link Only for users having dealer role -->
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <td class="ItemsHdrCommonAction"><s:text name="label.common.commonActions"/></td>
</tr>
<authz:ifUserInRole roles="dealerSalesAdministration">
    <authz:ifUserNotInRole roles="processor">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_stock_inventory" tagType="a"
                           tabLabel="%{getText('accordion_jsp.inventory.stock')}"
                           url="inventory.action?folderName=STOCK"
                           catagory="inventory">
                    <s:text name="accordion_jsp.inventory.stock"/><span class="count" id="inside_stock_inventory_count"></span>
                    </u:openTab>
                   <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_stock_inventory"), "onmousedown", function() {
                        	    populateStockCount();
                        	});
                        });
                    </script>
            </td>
        </tr>
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_retailed_inventory"
                           tagType="a"
                           tabLabel="%{getText('accordion_jsp.inventory.retailed')}"
                           url="inventory.action?folderName=RETAIL"
                           catagory="inventory">
                    <s:text name="accordion_jsp.inventory.retailed"/>
                </u:openTab>
            </td>
        </tr>
          <s:if test = "displayVintageStockInbox()">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_vintage_stock_inventory" tagType="a"
                           tabLabel="%{getText('accordion_jsp.inventory.vintageStock')}"
                           url="inventory.action?folderName=VINTAGE_STOCK"
                           catagory="inventory">
                    <s:text name="accordion_jsp.inventory.vintageStock"/><span class="count" id="inside_vintage_stock_count"></span>
                    </u:openTab>
                   <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_vintage_stock_inventory"), "onmousedown", function() {
                        	    populateVintageStockCount();
                        	});
                        });
                    </script>
            </td>
        </tr>
        </s:if>
    </authz:ifUserNotInRole>
</authz:ifUserInRole>


<authz:ifAdmin>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_stock_inventory" tagType="a"
                           tabLabel="%{getText('accordion_jsp.inventory.stock')}"
                           url="inventory.action?folderName=STOCK"
                           catagory="inventory">
                    <s:text name="accordion_jsp.inventory.stock"/><span class="count" id="inside_stock_inventory_count"></span>
        </u:openTab>
        <script type="text/javascript" language="javascript">
             dojo.addOnLoad(function() {
             	dojo.connect(dojo.byId("inside_stock_inventory"), "onmousedown", function()  {
             	    populateStockCount();
             	});
             });
         </script>
     </td>
</tr>
<s:if test = "displayVintageStockInbox()">
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_vintage_stock_inventory" tagType="a"
                           tabLabel="%{getText('accordion_jsp.inventory.vintageStock')}"
                           url="inventory.action?folderName=VINTAGE_STOCK"
                           catagory="inventory">
                    <s:text name="accordion_jsp.inventory.vintageStock"/><span class="count" id="inside_vintage_stock_count"></span>
                    </u:openTab>
                   <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_vintage_stock_inventory"), "onmousedown", function()  {
                                populateVintageStockCount();
                        	});
                        });
                    </script>
            </td>
        </tr>
        </s:if>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                   id="inside_policy_admin_main" tagType="a"
                   tabLabel="%{getText('title.managePolicy.policyDefinition')}"
                   url="policy.action?folderName=WARRANTY ADMIN" catagory="admin">
            <s:text name="accordionLabel.managePolicy.policyDefinition"/>
        </u:openTab></td>
</tr>

<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                   id="inside_maintain_lr_list" tagType="a"
                   tabLabel="%{getText('title.manageRates.manageLaborRateList')}"
                   url="list_lr_configuration.action?folderName=WARRANTY ADMIN"
                   catagory="admin">
        <s:text name="label.manageRates.manageLaborRateList"/>
    </u:openTab></td>

</tr>

<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="inside_manage_additional_labour_eligibility" tagType="a"
               tabLabel="%{getText('accordionLabel.manageAdditionalLabourEligibility')}"
                url="manageAdditionalLabourEligibility.action?folderName=WARRANTY ADMIN"
               catagory="admin" >
                    <s:text name="accordionLabel.manageAdditionalLabourEligibility"/>
        </u:openTab>
    </td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="inside_manage_alarm_code" tagType="a"
              tabLabel="%{getText('label.alarmCode.ManageAlarmCode')}"
               url="alarm_code_list.action?folderName=ALARMCODE"
              catagory="admin" >
                   <s:text name="label.alarmCode.ManageAlarmCode"/>
        </u:openTab>
    </td>
</tr>
 <%-- <tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="inside_manage_minimum_labour_round_up" tagType="a"
              tabLabel="%{getText('accordionLabel.manageMinimumLaborRoundUp')}"
               url="manageMinimumLaborRoundUp.action?folderName=WARRANTY ADMIN"
              catagory="admin" >
                   <s:text name="accordionLabel.manageMinimumLaborRoundUp"/>
        </u:openTab>
    </td>
</tr>  --%>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="inside_manage_inclusive_job_codes" tagType="a"
              tabLabel="%{getText('accordionLabel.manageInclusiveJobCodes')}"
               url="manageInclusiveJobCodes.action?folderName=WARRANTY ADMIN"
              catagory="admin" >
                   <s:text name="accordionLabel.manageInclusiveJobCodes"/>
        </u:openTab>
    </td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                                    id="inside_maintain_tr_list" tagType="a"
                                    tabLabel="%{getText('title.manageRates.manageTravelRateList')}"
                                    url="list_tr_configuration.action?folderName=WARRANTY ADMIN"
                                    catagory="admin">
     	<s:text name="label.manageRates.manageTravelRateList"/>
 	 </u:openTab>
 	</td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
		<u:openTab id="inside_maintain_modifier_list" tagType="a"
                                       tabLabel="%{getText('title.managePayment.paymentModifier')}"
                                       url="list_payment_variables.action?folderName=WARRANTY ADMIN"
                                       catagory="admin"
                                       decendentOf="%{getText('home_jsp.tabs.home')}">
        	<s:text name="label.managePayment.paymentModifier"/>
    	</u:openTab>
    </td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_maintain_payment_definition"
                   tagType="a"
                   tabLabel="%{getText('title.managePayment.paymentDefinition')}"
                   url="paymentDefinition.action?folderName=WARRANTY ADMIN" catagory="admin">
            <s:text name="label.managePayment.paymentDefinitions"/>
        </u:openTab></td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_part_return" tagType="a"
                   tabLabel="%{getText('title.partReturnConfiguration')}"
                   url="list_part_return_definitions.action" catagory="admin">
            <s:text name="label.partReturnConfiguration.config"/>
        </u:openTab></td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_maintain_jobCodes" tagType="a"
                   tabLabel="%{getText('title.manageFailureStructure.productFailureHierarchy')}"
                   url="maintainFailureStructure.action?folderName=WARRANTY ADMIN" catagory="admin">
            <s:text name="label.manageFailureStructure.productFailureHierarchy"/>
        </u:openTab></td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_manage_item_group" tagType="a"
                   tabLabel="%{getText('title.manageGroup.manageItemSchemes')}"
                   url="show_item_schemes.action" catagory="admin">
            <s:text name="label.manageGroup.manageItemGroups"/>
        </u:openTab></td>
</tr>
<tr>
    <td class="ItemsLabels">
		<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_manage_campaigns" tagType="a"
                   tabLabel="%{getText('title.campaign.serviceCampaign')}"
                   url="list_campaigns.action?folderName=WARRANTY ADMIN" catagory="admin">
            <s:text name="label.campaign.serviceCampaign"/>
        </u:openTab></td>
</tr>
</authz:ifAdmin>

<!-- Common action Folders for Users having SRA role -->
<authz:ifUserInRole roles="sra">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="maintain_supplier_contracts_home_page"
                       tagType="a"
                       tabLabel="%{getText('accordion_jsp.warrantyAdmin.maintainSupplierContracts')}"
                       url="list_contracts.action?folderName=Maintain Supplier Contracts" catagory="partReturns">
                <s:text name="accordion_jsp.warrantyAdmin.maintainSupplierContracts"/>
            </u:openTab>
        </td>
    </tr>
    <script type="text/javascript" language="javascript">
        dojo.addOnLoad(function() {
        	dojo.connect(dojo.byId("maintain_supplier_contracts_home_page"), "onmousedown", function()  {
                autoRefreshFolderCount();
                dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);
                refreshManager.register("maintain_supplier_contracts_home_page", "Maintain Supplier Contracts", "contracts_table_body.action");
        	});
        });
    </script>

</authz:ifUserInRole>

    <authz:ifUserInRole roles="admin,processor,inventoryAdmin">
     <%--<tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="common_multiple_claim_maintenance" tagType="a"
                       tabLabel="%{getText('label.main.claim.multipleClaimMaintenance')}"
                       url="new_search_expression.action?context=ClaimSearches&isMultiClaimMaintenance=true"
                       catagory="myClaims">
                <s:text name="label.main.claim.multipleClaimMaintenance"/>
            </u:openTab>
        </td>
    </tr> --%>
    </authz:ifUserInRole>


    <authz:ifUserInRole roles="baserole,processor,dsm,dsmAdvisor,recoveryProcessor,cpAdvisor,dealerWarrantyAdmin,readOnly">
	    <tr>
	        <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                       id="QuickClaimSearch" tagType="a"
	                       tabLabel="%{getText('label.common.claim.preDefinedSearch')}"
	                       url="showPreDefinedClaimsSearch.action?context=ClaimSearches&folderName=Search" catagory="myClaims">
	                <s:text name="label.common.claim.preDefinedSearch"/>
	            </u:openTab>
	        </td>
	    </tr>
    </authz:ifUserInRole>
   <authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,inventoryAdmin,dealerSalesAdministration,readOnly">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="PreDefinedStockInventorySearch" tagType="a"
                       tabLabel="%{getText('label.common.stock.preDefinedSearch')}"
                       url="preDefined_search_inventory.action?context=InventorySearches&refreshPage=true" catagory="inventory">
                <s:text name="label.common.stock.preDefinedSearch"/>
            </u:openTab>
        </td>
    </tr>
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="PreDefinedRetailInventorySearch" tagType="a"
                       tabLabel="%{getText('label.common.retail.preDefinedSearch')}"
                       url="preDefined_search_inventory_retail.action?context=InventorySearches&refreshPage=true" catagory="inventory">
                <s:text name="label.common.retail.preDefinedSearch"/>
            </u:openTab>
        </td>
    </tr>
</authz:ifUserInRole>
<authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,dsm,recoveryProcessor,dealerWarrantyAdmin">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="showPreDefinedCampaignsSearchCommonAction" tagType="a"
                       tabLabel="%{getText('label.common.campaign.preDefinedSearch')}"
                       url="showPreDefinedCampaignsSearch.action" catagory="campaigns">
                <s:text name="label.common.campaign.preDefinedSearch"/>
            </u:openTab>
        </td>
    </tr>
</authz:ifUserInRole>
<authz:ifUserInRole roles="recoveryProcessor,processor">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="common_multiple_rec_claim_maintenance" tagType="a"
                       tabLabel="%{getText('label.main.sra.multipleClaimMaintenance')}"
                       url="new_search_expression.action?context=RecoveryClaimSearches& isMultiRecClaimMaintainace=true"
                       catagory="supplierRecovery">
                <s:text name="label.main.sra.multipleClaimMaintenance"/>
            </u:openTab>
        </td>
    </tr>
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="predefined_recovery_claims_search" tagType="a"
                       tabLabel="%{getText('label.common.recClaim.preDefinedSearch')}"
                       url="showPreDefinedRecoveryClaimsSearch.action"
                       catagory="myClaims">
                <s:text name="label.common.recClaim.preDefinedSearch"/>
            </u:openTab>
        </td>
    </tr>
</authz:ifUserInRole>
<authz:ifUserInRole roles="processor,admin,receiver,sra,partshipper,dsmAdvisor,recoveryProcessor,inspector,
                           dealerWarrantyAdmin">
   <s:if test="!isPartShipperLimitedViewOnly()">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="PreDefinedPartReturnSearch" tagType="a"
                       tabLabel="%{getText('label.common.partReturn.preDefinedSearch')}"
                       url="preDefined_search_PartReturn.action?context=PartReturnSearches" catagory="partReturns">
                <s:text name="label.common.partReturn.preDefinedSearch"/>
            </u:openTab>
        </td>
    </tr>
	</s:if>
</authz:ifUserInRole>
<authz:ifUserInRole roles="partshipper">
	    <tr>
	        <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                       id="PreDefinedPartRecoverySearch" tagType="a"
	                       tabLabel="%{getText('label.common.recPartReturn.preDefinedSearch')}"
	                       url="preDefined_search_PartRecovery.action" catagory="partRecovery">
	                <s:text name="label.common.recPartReturn.preDefinedSearch"/>
	            </u:openTab>
	        </td>
	    </tr>
</authz:ifUserInRole>
	<authz:ifUserInRole roles="dealerWarrantyAdmin">
		<s:if test="!isLoggedInUserAnInternalUser() && isEnableDealersToViewPR()">
		<tr>
	        <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
					<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
						id="part_return_for_Dealer" tagType="a"
						tabLabel="%{getText('title.required.PartsReturn')}"
						url="list_required_part_returns_for_dealer.action"
						catagory="partReturns">
						<s:text name="accordion.label.requiredPartsReturn" />
					</u:openTab>
			</td>
		</tr>
		</s:if>
	</authz:ifUserInRole>
</html>