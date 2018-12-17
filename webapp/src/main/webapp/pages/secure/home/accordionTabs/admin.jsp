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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
    <script type="text/javascript" src="scripts/shortcut.js"></script>
     <script type="text/javascript">
        dojo.require("twms.widget.Select");
        dojo.require("dojox.layout.ContentPane");
    </script>

<div dojoType="dijit.layout.ContentPane" id="admin"
        title="<s:text name="accordionLabel.warrantyAdmin"/>-(<s:property  value="%{warrantyAdminSelectedBusinessUnit}"/>)" 
        iconClass="warrantyAdmin" style="width: 100%;">
    <s:if test="warrantyAdminSelectedBusinessUnit==null">
    <authz:ifAdmin>
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                var defaultBu = dojo.byId("buNames").options[0].value;
                location.href = "bu.action?warrantyAdminSelectedBusinessUnit=" + defaultBu;
            });
        </script>
    </authz:ifAdmin>
    </s:if>
        <ol>
	<authz:ifAdmin>
		<div dojoType="twms.widget.Dialog" id="businessUnit" title="<s:text name="label.common.businessUnit"/>" style="width:200px">
		<s:select id="buNames" label="Business Unit" list="businessUnits"
		listKey="name" listValue="name" name="Business Unit"
		value="%{warrantyAdminSelectedBusinessUnit}" emptyOption="false" cssStyle="width:175px"/>
		<script type="text/javascript">
		var bu = dojo.byId("buNames");
		dojo.connect(bu, "onchange", function() {
		var optionsList = bu.options;
		var i = optionsList.length;
		while(i--) {
		var optionNode = optionsList[i];
		if(optionNode.selected) {
		selectedBu = optionNode.value;
		location.href = "bu.action?warrantyAdminSelectedBusinessUnit=" + selectedBu;
		break;
		}
		}
		});
		</script>
		</div>
   </authz:ifAdmin>
        
            <!-- Manage Business Unit Configuration Accordion-->
	        <authz:ifPermitted resource="warrantyAdminManageBusinessConfigurations">
	        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="list_bu_config" tagType="li"
		        	tabLabel="%{getText('label.businessUnit.configurations')}" url="manageBUConfiguration.action" 
		        	catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_Business_Configurations.htm">
		        	<s:text name="label.businessUnit.configurations" />
		        </u:openTab>
		    </authz:ifPermitted>
        <!-- Manage Policy Accordion -->
       <authz:ifPermitted resource="warrantyAdminManageWarrantyPolicies">
            <u:fold label="%{getText('accordionLabel.managePolicy')}"
                id="manage_warranty_policies" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldablePolicies" />
            <authz:ifPermitted resource="warrantyAdminPolicyDefinition">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="policy_admin" tagType="li" cssClass="warrantyAdmin_folder folder foldablePolicies sublist"
                tabLabel="%{getText('title.managePolicy.policyDefinition')}"
                url="policy.action?folderName=WARRANTY ADMIN" catagory="admin" helpCategory="Warranty_Admin/Policy_Definition.htm">
                <s:text name="accordionLabel.managePolicy.policyDefinition" />
            </u:openTab>
            </authz:ifPermitted>
        </authz:ifPermitted>
          
        <!-- Manage Flat Rates Accordion -->
        <authz:ifPermitted resource="warrantyAdminManageFlatRates">
            <u:fold label="%{getText('accordionLabel.manageRates')}"
                id="manage_rates" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableRates" />
            <authz:ifPermitted resource="warrantyAdminLaborRates">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_lr_list" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableRates sublist"
                tabLabel="%{getText('title.manageRates.manageLaborRateList')}"
                url="list_lr_configuration.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/Labor_Rates.htm">
                <s:text name="accordionLabel.manageRates.manageLaborRateList" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminTravelRates">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_tr_list" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableRates sublist"
                tabLabel="%{getText('title.manageRates.manageTravelRateList')}"
                url="list_tr_configuration.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/Travel_Rates.htm">
                <s:text name="accordionLabel.manageRates.manageTravelRateList" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminItemPriceModifiers">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_item_price_modifiers" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableRates sublist"
                tabLabel="%{getText('title.manageRates.manageItemPriceModifiers')}"
                url="list_item_price_modifiers.action?folderName=WARRANTY ADMIN"
                catagory="admin">
                <s:text name="accordionLabel.manageRates.manageItemPriceModifiers" />
            </u:openTab>
            </authz:ifPermitted>
              <authz:ifPermitted resource="warrantyAdminManagePartsCost">
              <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_part_price_cost" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableRates sublist"
                tabLabel="%{getText('title.manageRates.managePartPriceCost')}"
                url="list_part_prices.action?folderName=WARRANTY ADMIN"
                catagory="admin">
                <s:text name="accordionLabel.manageRates.managePartPriceCost" />
            </u:openTab>
            </authz:ifPermitted>
            </authz:ifPermitted>
            
            <!--This link has been commneted out as IRI doesnot require it-->
            <%--<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_policy_modifiers" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableRates sublist"
                tabLabel="%{getText('title.manageRates.managePolicyModifiers')}"
                url="list_policy_price_modifiers.action?folderName=WARRANTY ADMIN"
                catagory="admin">
                <s:text name="accordionLabel.manageRates.managePolicyModifiers" />
            </u:openTab> --%>
        <!-- Part Return Configuration Accordion -->
        <authz:ifPermitted resource="warrantyAdminPartReturnConfiguration">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="part_return" tagType="li" cssClass="warrantyAdmin_folder folder"
                tabLabel="%{getText('title.partReturnConfiguration')}"
                url="list_part_return_definitions.action" catagory="admin" helpCategory="Warranty_Admin/Part_Return_Configuration.htm">
                <s:text name="accordianLabel.partReturnConfiguration" />
            </u:openTab>      
        </authz:ifPermitted>
            
            <!-- Manage State Mandates Accordion-->
	        <authz:ifPermitted resource="warrantyAdminManageStateMandates">
	        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="list_state_mandates" tagType="li"
		        	tabLabel="%{getText('label.stateMandates')}" url="manage_StateMandates.action" 
		        	catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_State_Mandates.htm">
		        	<s:text name="label.stateMandates" />
		        </u:openTab>
	        </authz:ifPermitted> 
	                     
        <!--  Manage Claim Payment Accordion -->
        <authz:ifPermitted resource="warrantyAdminManageClaimPayment">
            <u:fold
                label="%{getText('accordionLabel.managePayment')}"
                id="manage_payments" tagType="li"
                cssClass="uFoldStyle folder"
                foldableClass="foldablePayments" />
            <authz:ifPermitted resource="warrantyAdminCreateClaimPaymentModifier">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_modifier_list" tagType="li"
                cssClass="warrantyAdmin_folder folder foldablePayments sublist"
                tabLabel="%{getText('title.managePayment.paymentModifier')}"
                url="list_payment_variables.action" catagory="admin" helpCategory="Warranty_Admin/Create_Claim_Payment_Modifier.htm">
                <s:text name="accordionLabel.managePayment.paymentModifier" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminConfigureClaimPaymentDefinition">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_payment_definition" tagType="li"
                cssClass="warrantyAdmin_folder folder foldablePayments sublist"
                tabLabel="%{getText('title.managePayment.paymentDefinition')}"
                url="paymentDefinition.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/Configure_Claim_Payment_Modifier.htm">
                <s:text name="accordionLabel.managePayment.paymentDefinition" />
            </u:openTab>
            </authz:ifPermitted>
        </authz:ifPermitted>
        <!-- Manage Failure Structure Accordion -->
        <authz:ifPermitted resource="warrantyAdminManageFailureStructure">
            <u:fold label="%{getText('accordionLabel.manageFailureStructure')}"
                id="manage_failure_structure" tagType="li"
                cssClass="uFoldStyle folder" foldableClass="foldableFailureStructure" />
            <authz:ifPermitted resource="warrantyAdminSeriesFailureHierarchy">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_jobCodes" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableFailureStructure sublist"
                tabLabel="%{getText('title.manageFailureStructure.productFailureHierarchy')}"
                url="maintainFailureStructure.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/Product_Failure_Hierarchy.htm">
                <s:text name="accordionLabel.manageFailureStructure.productFailureHierarchy" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminListFaultLocations">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="maintain_faultcodes" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableFailureStructure sublist"
                tabLabel="%{getText('title.manageFailureStructure.listFaultCodes')}"
                url="list_fault_code_defs.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/List_Fault_Codes.htm">
                <s:text name="accordionLabel.manageFailureStructure.listFaultCodes" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminListServiceProcedures">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="list_job_codes" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableFailureStructure sublist"
                tabLabel="%{getText('accordionLabel.manageFailureStructure.listSP')}"
                url="list_job_code_defs.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/List_Service_Procedures.htm">
                <s:text name="accordionLabel.manageFailureStructure.listSP" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminFaultFound/CausedByAssociation">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_failure_type_link" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableFailureStructure sublist"
                tabLabel="%{getText('accordionLabel.manageFailureStructure.faultFoundCausedBy')}"
                url="manageFailureTypes.action?folderName=WARRANTY ADMIN"
                catagory="admin">
                <s:text name="accordionLabel.manageFailureStructure.faultFoundCausedBy" />
            </u:openTab> 
            </authz:ifPermitted>
            
            <authz:ifPermitted resource="warrantyAdminMaintainFailureDetailofFailureURL">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_machine_url" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableFailureStructure sublist"
                tabLabel="%{getText('accordionLabel.manageFailureStructure.manageMachineUrl')}"
                url="manageMachineUrl.action?folderName=WARRANTY ADMIN"
                catagory="admin" helpCategory="Warranty_Admin/Maintain_Failure_URL_of_Failure_Detail.htm">
                <s:text name="accordionLabel.manageFailureStructure.manageMachineUrl" />
            </u:openTab>
            </authz:ifPermitted>
        </authz:ifPermitted>

        <!-- Manage Groups Accordion  -->
        	<authz:ifPermitted resource="warrantyAdminManageGroups">
            <u:fold label="%{getText('accordionLabel.manageGroup')}"
	                id="manage_groups" tagType="li"
	                cssClass="uFoldStyle folder" foldableClass="foldableGroups" />
        	<authz:ifPermitted resource="warrantyAdminItemGroups">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                id="manage_item_group" tagType="li"
	                cssClass="warrantyAdmin_folder folder foldableGroups sublist"
	                tabLabel="%{getText('title.manageGroup.itemGroups')}"
	                url="show_item_schemes.action" catagory="admin" helpCategory="Warranty_Admin/Item_Groups.htm">
	                <s:text name="accordionLabel.manageGroup.itemGroups" />
	            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminDealerGroups">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                id="manage_dealer_group" tagType="li"
	                cssClass="warrantyAdmin_folder folder foldableGroups sublist"
	                tabLabel="%{getText('title.manageGroup.dealerGroups')}"
	                url="show_dealer_schemes.action" catagory="admin" helpCategory="Warranty_Admin/Dealer_Groups.htm">
	                <s:text name="accordionLabel.manageGroup.dealerGroups" />
	            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminUserGroups">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                id="manage_user_group" tagType="li"
	                cssClass="warrantyAdmin_folder folder foldableGroups sublist"
	                tabLabel="%{getText('title.manageGroup.userGroups')}"
	                url="show_user_schemes.action" catagory="admin" helpCategory="Warranty_Admin/User_Groups.htm">
	                <s:text name="accordionLabel.manageGroup.userGroups" />
	            </u:openTab>	          
            </authz:ifPermitted>
            </authz:ifPermitted>
            <!-- Manage Business Rule Groups Accordion  -->
            <authz:ifPermitted resource="warrantyAdminManageBusinessRuleGroups">
            <u:fold
                label="%{getText('accordionLabel.manageBusinessRuleGroups')}"
                id="manage_domain_rule_groups_folder" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableDomainRuleGroups" />
            <authz:ifPermitted resource="warrantyAdminClaimsProcessing">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_claim_processing_rule_groups" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableDomainRuleGroups sublist"
                tabLabel="%{getText('title.manageBusinessRuleGroup.claimsProcessing')}"
                url="list_rule_groups.action?context=ClaimRules" catagory="admin" helpCategory="Warranty_Admin/Manage_Business_Rule_Groups.htm">
                <s:text name="accordionLabel.manageBusinessRuleGroup.ClaimRules" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminClaimProcessorRouting">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_claim_processor_routing_rule_groups" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableDomainRuleGroups sublist"
                tabLabel="%{getText('title.manageBusinessRuleGroup.claimProcessorRouting')}"
                url="list_rule_groups.action?context=ClaimProcessorRouting" catagory="admin" helpCategory="Warranty_Admin/Manage_Business_Rule_Groups.htm">
                <s:text name="accordionLabel.manageBusinessRule.ClaimProcessorRouting" />    
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminDSMRouting">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_dsm_routing_rule_groups" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableDomainRuleGroups sublist"
                tabLabel="%{getText('title.manageBusinessRuleGroup.DSMRouting')}"
                url="list_rule_groups.action?context=DSMRouting" catagory="admin" helpCategory="Warranty_Admin/Manage_Business_Rule_Groups.htm">
                <s:text name="accordionLabel.manageBusinessRule.DSMRouting" />    
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminAdvisorRouting">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_dsm_advisor_routing_rule_groups" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableDomainRuleGroups sublist"
                tabLabel="%{getText('title.manageBusinessRuleGroup.DSMAdvisorRouting')}"
                url="list_rule_groups.action?context=DSMAdvisorRouting" catagory="admin" helpCategory="Warranty_Admin/Manage_Business_Rule_Groups.htm">
                <s:text name="accordionLabel.manageBusinessRule.DSMAdvisorRouting" />    
            </u:openTab>
            </authz:ifPermitted>
            </authz:ifPermitted>
        <!-- Manage Business Rules Accordion  -->
        <authz:ifPermitted resource="warrantyAdminManageBusinessRules">
            <u:fold
                label="%{getText('accordionLabel.manageBusinessRules')}"
                id="manage_domain_rules_folder" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableDomainRules" />
                <authz:ifPermitted resource="warrantyAdminClaimProcessorRouting">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	              id="manage_claim_processor_routing" tagType="li"
	              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	              tabLabel="%{getText('title.manageBusinessRule.claimProcessorRouting')}"
	              url="list_claim_processor_routing_rules.action?context=ClaimProcessorRouting" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	              <s:text name="accordionLabel.manageBusinessRule.ClaimProcessorRouting" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminRecoveryClaimProcessorRouting">	           
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	              id="manage_rec_claim_processor_routing" tagType="li"
	              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	              tabLabel="%{getText('title.manageBusinessRule.recClaimProcessorRouting')}"
	              url="list_rec_claim_processor_routing_rules.action" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	              <s:text name="accordionLabel.manageBusinessRule.recClaimProcessorRouting" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminClaimsProcessing">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                id="manage_claim_processing_rules" tagType="li"
	                cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	                tabLabel="%{getText('title.manageBusinessRule.claimProcessing')}"
	                url="list_domain_rules.action?context=ClaimRules" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	                <s:text name="accordionLabel.manageBusinessRule.ClaimRules" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminEntryValidations">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                id="manage_entry_validation_rules" tagType="li"
	                cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	                tabLabel="%{getText('title.manageBusinessRule.entryValidation')}"
	                url="list_entry_validation_rules.action" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	                <s:text name="accordionLabel.manageBusinessRule.EntryValidationRules" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminDSMRouting">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	              id="manage_DSM_routing" tagType="li"
	              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	              tabLabel="%{getText('title.manageBusinessRule.DSMRouting')}"
	              url="list_DSM_rules.action?context=DSMRouting" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	              <s:text name="accordionLabel.manageBusinessRule.DSMRouting" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminAdvisorRouting">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	              id="manage_DSM_advisor_routing" tagType="li"
	              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	              tabLabel="%{getText('title.manageBusinessRule.DSMAdvisorRouting')}"
	              url="list_DSM_advisor_rules.action?context=DSMAdvisorRouting" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	              <s:text name="accordionLabel.manageBusinessRule.DSMAdvisorRouting" />
	            </u:openTab>
	            </authz:ifPermitted>
	            <authz:ifPermitted resource="warrantyAdminProcessorAuthority">
	            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
		              id="manage_processor_authority" tagType="li"
		              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
		              tabLabel="%{getText('title.manageBusinessRule.processorAuthority')}"
		              url="list_processor_authority_rules.action" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
		              <s:text name="accordionLabel.manageBusinessRule.processorAuthority" />
		    </u:openTab>
		    </authz:ifPermitted>
		    <!--<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	              id="manage_CP_advisor_routing" tagType="li"
	              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
	              tabLabel="%{getText('title.manageBusinessRule.CPAdvisorRouting')}"
	              url="list_CP_advisor_rules.action" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
	              <s:text name="accordionLabel.manageBusinessRule.CPAdvisorRouting" />
	            </u:openTab>-->
            </authz:ifPermitted>
            	
        <!-- Manage Business Conditions Accordion -->
        <authz:ifPermitted resource="warrantyAdminManageBusinessConditions">
        <u:fold
        label="%{getText('accordionLabel.manageBusinessConditions')}"
        id="manage_business_cond_folder" tagType="li" cssClass="uFoldStyle folder"
        foldableClass="foldableBusinessConditions" />
        	<authz:ifPermitted resource="warrantyAdminWarrantyBusinessConditions">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_rules" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableBusinessConditions sublist"
                tabLabel="%{getText('title.warranty.manageBusinessCondition')}"
                url="list_expressions.action?context=ClaimRules"
                catagory="admin" helpCategory="Warranty_Admin/Business_Conditions.htm">
                <s:text
                    name="accordionLabel.warranty.manageBusinessCondition" />
            </u:openTab>
            </authz:ifPermitted>
        </authz:ifPermitted>
       
        <!-- Manage Processors Availability -->
        <authz:ifPermitted resource="warrantyAdminManageUserAvailability">
           	<u:fold
        		label="%{getText('title.processor.manageAvail')}"
        		id="manage_processor_availability_folder" tagType="li" cssClass="uFoldStyle folder"
        		foldableClass="foldableProcessAvail" />
    	
    		<authz:ifPermitted resource="warrantyAdminProcessor">	
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
    			id="manage_processors_avail_processor" tagType="li" cssClass="warrantyAdmin_folder folder foldableProcessAvail sublist"
    			tabLabel="%{getText('title.processor.manageAvail.processor')}"
    			url="showProcessorAvailability.action?role=processor&selectedBusinessUnit=%{warrantyAdminSelectedBusinessUnit}" catagory="admin"
    			helpCategory="Warranty_Admin/Manage_User_Availability.htm">
            	<s:text name="accordionLabel.user.manageAvailability.processor" />
            </u:openTab>
            </authz:ifPermitted>
            
            <authz:ifPermitted resource="warrantyAdminDSM">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_processors_avail_dsm" tagType="li" cssClass="warrantyAdmin_folder folder foldableProcessAvail sublist"
                tabLabel="%{getText('title.processor.manageAvail.dsm')}"
                url="showProcessorAvailability.action?role=dsm" catagory="admin" helpCategory="Warranty_Admin/Manage_User_Availability.htm">
                <s:text name="accordionLabel.user.manageAvailability.dsm" />
            </u:openTab>
            </authz:ifPermitted>
            
            <authz:ifPermitted resource="warrantyAdminDSMAdvisor">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="manage_processors_avail_dsmadvisor" tagType="li" cssClass="warrantyAdmin_folder folder foldableProcessAvail sublist"
               tabLabel="%{getText('title.processor.manageAvail.dsmAdvisor')}"
               url="showProcessorAvailability.action?role=dsmAdvisor" catagory="admin" helpCategory="Warranty_Admin/Manage_User_Availability.htm">
               <s:text name="accordionLabel.user.manageAvailability.dsmadvisor" />
            </u:openTab>
            </authz:ifPermitted>
            
			<authz:ifPermitted resource="warrantyAdminRecoveryProcessor">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="manage_processors_avail_rec_processor" tagType="li" cssClass="warrantyAdmin_folder folder foldableProcessAvail sublist"
               tabLabel="%{getText('title.processor.manageAvail.recProcessor')}"
               url="showProcessorAvailability.action?role=recoveryProcessor" catagory="admin" helpCategory="Warranty_Admin/Manage_User_Availability.htm">
               <s:text name="accordionLabel.user.manageAvailability.recProcessor" />
            </u:openTab>
            </authz:ifPermitted>
            
           <!-- <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
               id="manage_processors_avail_cpadvisor" tagType="li" cssClass="warrantyAdmin_folder folder foldableProcessAvail sublist"
               tabLabel="%{getText('title.processor.manageAvail.cpAdvisor')}"
               url="showProcessorAvailability.action?role=cpAdvisor" catagory="admin" helpCategory="Warranty_Admin/Manage_User_Availability.htm">
               <s:text name="accordionLabel.user.manageAvailability.cpadvisor" />
            </u:openTab>-->
            </authz:ifPermitted>

            <!-- Manage Processors Availability -->

            <!-- Service Campaign Accordion -->
            <authz:ifPermitted resource="warrantyAdminFieldProductImprovement">
            <u:fold
        		label="%{getText('title.campaign.serviceCampaign')}"
        		id="service_campaign_folder" tagType="li" cssClass="uFoldStyle folder"
        		foldableClass="foldableServiceCampaign" />
        	<authz:ifPermitted resource="warrantyAdminFieldProductImprovement">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_campaigns" tagType="li" cssClass="warrantyAdmin_folder folder foldableServiceCampaign sublist"
                tabLabel="%{getText('title.campaign.serviceCampaign')}"
                url="list_campaigns.action?folderName=WARRANTY ADMIN" catagory="admin" helpCategory="Warranty_Admin/Service_Campaign.htm">
                <s:text name="accordionLabel.campaign.serviceCampaign" />
            </u:openTab>
            </authz:ifPermitted>
            <!-- relate campaign Codes -->
            <authz:ifPermitted resource="warrantyAdminRelatedFPIsManagement">
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
					id="related_campaign_list" tagType="li"
					cssClass="warrantyAdmin_folder folder foldableServiceCampaign sublist"
					tabLabel="%{getText('label.relatedCampaign.ManageRelatedCampaign')}"
					url="related_campaign_list.action"
					catagory="admin" helpCategory="Warranty_Admin/Related_Field_Modifications.htm">
					<s:text name="label.relatedCampaign.ManageRelatedCampaign" />
			</u:openTab>
			</authz:ifPermitted>
			</authz:ifPermitted>

        <!-- Manage Warehouse Accordion -->
        <authz:ifPermitted resource="warrantyAdminManageWarehouses">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_warehouses" tagType="li" cssClass="warrantyAdmin_folder folder"
                tabLabel="%{getText('title.manageWarehouse')}"
                url="list_warehouses.action" catagory="admin" helpCategory="Warranty_Admin/Warehouses.htm">
                <s:text name="accordionLabel.manageWarehouse" />
            </u:openTab>
         </authz:ifPermitted>
       
        <!-- List Of Values (LOV) Accordion  -->
        <authz:ifPermitted resource="warrantyAdminListOfValues">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_lovs" tagType="li" cssClass="warrantyAdmin_folder folder"
                tabLabel="%{getText('title.listOfValues')}"
                url="list_lov_type.action" catagory="admin" helpCategory="Warranty_Admin/List_of_Values.htm">
                <s:text name="accordionLabel.listOfValues"/>
            </u:openTab>
         </authz:ifPermitted>
         <!-- Alarm Codes -->
         <authz:ifPermitted resource="warrantyAdminManageDisplayedFaultCode">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="alarm_code_list" tagType="li"
				cssClass="warrantyAdmin_folder folder"
				tabLabel="%{getText('label.alarmCode.ManageAlarmCode')}"
				url="alarm_code_list.action?folderName=ALARMCODE"
				catagory="admin" helpCategory="Warranty_Admin/Manage_Alarm_Code.htm">
				<s:text name="label.alarmCode.ManageAlarmCode" />
			</u:openTab>
		</authz:ifPermitted>
        <!-- Additional Attributes Accordion -->
        <authz:ifPermitted resource="warrantyAdminAdditionalAttributes">
        	<u:fold label="%{getText('accordionLabel.manageAdditionalAttributes')}"
                id="attribute_management" tagType="li"
                cssClass="uFoldStyle folder" foldableClass="foldableAttributeManagement" />
            <authz:ifPermitted resource="warrantyAdminCreate/UpdateAttributes">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_attributes" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableAttributeManagement sublist"
                tabLabel="%{getText('title.manageAdditionalAttributes.manageAttributes')}"
                url="list_attributes.action"
                catagory="admin" helpCategory="Warranty_Admin/Create_Update_Attributes.htm">
                <s:text name="accordionLabel.manageAttributes.createUpdateAttr" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminAssociate/DissociatePartAttributes">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="associate_claimed_item_attributes" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableAttributeManagement sublist"
                tabLabel="%{getText('title.manageAdditionalAttributes.parts')}"
                url="list_claimed_part_attributes.action"
                catagory="admin" helpCategory="Warranty_Admin/Associate_Dissociate_Part_Attributes.htm">
                <s:text name="accordionLabel.associateAttributes.parts" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminAssociate/DissociateFlatRatesAttributes">
             <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="associate_part_attribute" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableAttributeManagement sublist"
                tabLabel="%{getText('title.manageAdditionalAttributes.JobCodes')}"
                url="list_job_code_attributes.action"
                catagory="admin">
                <s:text name="accordionLabel.associateAttributes.claimedItem" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminAssociate/DissociateClaimedInventoryAttributes">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="associate_Inventory_attribute" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableAttributeManagement sublist"
                tabLabel="%{getText('title.manageAdditionalAttributes.claimedItem')}"
                url="list_claimed_inventory_attributes.action"
                catagory="admin">
                <s:text name="accordionLabel.associateAttributes.claimedInv" />
            </u:openTab> 
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminAssociate/DissociateClaimAttributes">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="associate_Claim_attribute" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableAttributeManagement sublist"
                tabLabel="%{getText('title.manageAdditionalAttributes.claim')}"
                url="list_claim_attributes.action"
                catagory="admin">
                <s:text name="accordionLabel.associateAttributes.claim" />
            </u:openTab>
            </authz:ifPermitted>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminManageFreight/Shippers">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_shippers" tagType="li" cssClass="warrantyAdmin_folder folder"
                tabLabel="%{getText('title.manageFreightShippers')}"
                url="list_shippers.action" catagory="admin" helpCategory="Warranty_Admin/Warehouses.htm">
                <s:text name="title.manageFreightShippers" />
            </u:openTab>
        </authz:ifPermitted>
        
        <!-- Data Management Accordion -->
				        <authz:ifPermitted resource="dataManagement">
				            <u:fold label="%{getText('accordionLabel.manageData')}"
				                id="data_management" tagType="li"
				                cssClass="uFoldStyle folder" foldableClass="foldableDataManagement" />
				            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				                id="upload_inventory" tagType="li"
				                cssClass="warrantyAdmin_folder folder foldableDataManagement sublist"
				                tabLabel="%{getText('title.manageData.inventoryUpload')}"
				                url="upload_inventory_setup.action"
				                catagory="admin">
				                <s:text name="accordionLabel.manageData.inventoryUpload" />
				            </u:openTab>
				            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				                id="product_structure" tagType="li"
				                cssClass="warrantyAdmin_folder folder foldableDataManagement sublist"
				                tabLabel="%{getText('title.manageData.productStructure')}"
				                url="upload_item_group.action"
				                catagory="admin">
				                <s:text name="accordionLabel.manageData.productStructure" />
				            </u:openTab>
				            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				                id="item_upload" tagType="li"
				                cssClass="warrantyAdmin_folder folder foldableDataManagement sublist"
				                tabLabel="%{getText('title.manageData.itemUpload')}"
				                url="upload_item.action"
				                catagory="admin">
				                <s:text name="accordionLabel.manageData.itemUpload" />
				            </u:openTab>
				            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				                id="part_price" tagType="li"
				                cssClass="warrantyAdmin_folder folder foldableDataManagement sublist"
				                tabLabel="%{getText('title.manageData.partPrice')}"
				                url="upload_item_price.action"
				                catagory="admin">
				                <s:text name="accordionLabel.manageData.partPrice" />
				            </u:openTab>
				        </authz:ifPermitted>
        <authz:ifPermitted resource="warrantyAdminViewSupplier">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="list_supplier" tagType="li"
	        	tabLabel="%{getText('accordionLabel.viewSupplier')}" url="list_supplier.action" 
	        	catagory="supplierRecovery" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/View_Supplier.htm">
	        	<s:text name="accordionLabel.viewSupplier" />
	        </u:openTab>
	    </authz:ifPermitted>
        <authz:ifPermitted resource="warrantyAdminViewLabels">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manageLabels" tagType="li"
	        	tabLabel="%{getText('label.common.manageLabels')}" 
	        	url="manageLabels.action" 
	        	catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/View_Labels.htm">
	        	<s:text name="label.common.manageLabels" />
	        </u:openTab>
	    </authz:ifPermitted>
	   
          <!-- Manage Labor Type Accordion  -->
          <authz:ifPermitted resource="warrantyAdminManageLaborSplit">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_labor" tagType="li" cssClass="warrantyAdmin_folder folder"
                tabLabel="%{getText('title.manage.LaborType')}"
                url="manage_laborType.action" catagory="admin" helpCategory="Warranty_Admin/Manage_Labor_Split.htm">
                <s:text name="accordionLabel.manageLaborType"/>
            </u:openTab>
        </authz:ifPermitted>
        <authz:ifPermitted resource="warrantyAdminCreateLabelsforinventory">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="create_label_for_Inventory" tagType="li"
                       tabLabel="%{getText('title.inventory.createLabel')}"
                       url="new_search_expression.action?context=InventorySearches&isCreateLabelForInventory=true"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/View_Labels.htm">
                <s:text name="title.inventory.createLabel"/>
            </u:openTab>
        </authz:ifPermitted>
        <authz:ifPermitted resource="warrantyAdminCreateLabelsforModels">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="create_label_for_Model" tagType="li"
                       tabLabel="%{getText('title.model.createModelLabel')}"
                       url="list_model.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/List_Models.htm">
                <s:text name="title.model.createModelLabel"/>
            </u:openTab>
        </authz:ifPermitted>
        <authz:ifPermitted resource="maintainUOMMappings">
            <!--<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="uom_mappings" tagType="li"
	        	tabLabel="%{getText('label.uom.mapping')}" url="showMappedUoms.action" 
	        	catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/UOM_Mappings.htm">
	        	<s:text name="label.uom.mappings" />
	        </u:openTab>-->
	    </authz:ifPermitted>
        <authz:ifPermitted resource="warrantyAdminManageMiscellaneousParts">
        <u:fold
        label="%{getText('accordionLabel.miscellaneousParts')}"
        id="manage_miscellaneous_parts_folder" tagType="li" cssClass="uFoldStyle folder"
        foldableClass="foldableMiscellaneousParts" />
            <authz:ifPermitted resource="warrantyAdminManageMiscellaneousParts">     
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_Miscellaneous_Parts" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableMiscellaneousParts sublist"
                tabLabel="%{getText('accordionLabel.miscellaneousParts.manage')}"
                url="showMiscellaneousParts.action"
                catagory="admin" helpCategory="Warranty_Admin/Manage_Miscellaneous_Parts.htm">
                <s:text
                    name="accordionLabel.miscellaneousParts.manage" />
            </u:openTab>
            </authz:ifPermitted>
            <authz:ifPermitted resource="warrantyAdminConfigureMiscellaneousParts">
             <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="configure_Miscellaneous_Parts" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableMiscellaneousParts sublist"
                tabLabel="%{getText('accordionLabel.miscellaneousParts.configure')}"
                url="showMiscellaneousPartsConfigurations.action"
                catagory="admin" helpCategory="Warranty_Admin/Manage_Miscellaneous_Parts.htm">
                <s:text
                    name="accordionLabel.miscellaneousParts.configure" />
            </u:openTab>
            </authz:ifPermitted>
        </authz:ifPermitted>
        <!-- Manage Custom Reports Accordion-->
        <authz:ifPermitted resource="manageCustomReports">
           <!-- <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="custom_reports" tagType="li"
                       tabLabel="%{getText('label.customReport.manageCustomReports')}" url="customReport.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_Custom_Reports.htm">
                <s:text name="label.customReport.manageCustomReports"/>
            </u:openTab>-->
        </authz:ifPermitted>

		<authz:ifPermitted resource="warrantyAdminCostCategoryConfiguration">
        	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="list_cost_category" tagType="li"
	        	tabLabel="%{getText('accordionLabel.costCategoryConfiguration')}" url="list_cost_category.action" 
	        	catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Cost_Category_Configuration.htm">
	        	<s:text name="accordionLabel.costCategoryConfiguration" />
	        </u:openTab>
	     </authz:ifPermitted>
	        
	        <authz:ifPermitted resource="warrantyAdminManageLOASchemes">
	          <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manage_loa_schemes" tagType="li"
                       tabLabel="%{getText('accordionLabel.manageLoaSchemes')}" url="list_loa_schemes.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_LOA_Schemes.htm">
                <s:text name="accordionLabel.manageLoaSchemes"/>
            </u:openTab>
            </authz:ifPermitted>
	        
	        <authz:ifPermitted resource="warrantyAdminManageAdditionalLaborEligibility">
	          <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manage_additional_labour_eligibility" tagType="li"
                       tabLabel="%{getText('accordionLabel.manageAdditionalLabourEligibility')}" url="manageAdditionalLabourEligibility.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_Additional_Labour_Eligibility.htm">
                <s:text name="accordionLabel.manageAdditionalLabourEligibility"/>
            </u:openTab>
            </authz:ifPermitted>
	         <%--  <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manage_minimum_labor_round_up" tagType="li"
                       tabLabel="%{getText('accordionLabel.manageMinimumLaborRoundUp')}" url="manageMinimumLaborRoundUp.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_Minimum_Labour_Round_Up.htm">
                <s:text name="accordionLabel.manageMinimumLaborRoundUp"/>
            </u:openTab> --%>
            
             <!-- Manage Inclusive Job Codes Accordion-->
             
             <authz:ifPermitted resource="manageInclusiveJobCodes">
              <!--<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manage_inclusive_job_codes" tagType="li"
                       tabLabel="%{getText('accordionLabel.manageInclusiveJobCodes')}" url="manageInclusiveJobCodes.action"
                       catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/Manage_Inclusive_Job_Codes.htm">
                <s:text name="accordionLabel.manageInclusiveJobCodes"/>
            </u:openTab>-->	        
            </authz:ifPermitted>
	        
        </ol>
</div>