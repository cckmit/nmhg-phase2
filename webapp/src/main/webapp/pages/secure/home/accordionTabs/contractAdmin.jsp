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
<%-- Contract Admin Accordion Pane --%>
<div dojoType="dijit.layout.ContentPane" id="contractAdmin"   
    title="<s:text name="accordion_jsp.accordionPane.supplierContractAdmin"/>-(<s:property  value="%{warrantyAdminSelectedBusinessUnit}"/>)"
    iconClass="partreturns">
    <s:if test="warrantyAdminSelectedBusinessUnit==null">
    	<authz:ifUserInRole roles="sra">    
		<authz:ifUserNotInRole roles="admin">
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                var defaultBu = dojo.byId("buNames").options[0].value;
                location.href = "bu.action?warrantyAdminSelectedBusinessUnit=" + defaultBu;
            });
        </script>
        </authz:ifUserNotInRole>  
    	</authz:ifUserInRole>
    </s:if>
    
    
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<ol>



	<authz:ifUserInRole roles="sra">    
		<authz:ifUserNotInRole roles="admin">
           <div dojoType="dijit.Dialog" id="businessUnit" title="<s:text name="label.common.businessUnit"/>" style="width:200px">
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
                        		selectedBu =  optionNode.value;
                        		location.href = "bu.action?warrantyAdminSelectedBusinessUnit="  + selectedBu;
                        		break;
                        	}
                        }
                    });
            </script>
            </div>
         </authz:ifUserNotInRole>  
    </authz:ifUserInRole>  
             
<%-- Maintain Supplier Contracts Accrdion --%>
	<authz:ifPermitted resource="contractAdminMaintainSupplierContracts">
    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
        id="maintain_supplier_contracts" tagType="li"
        cssClass="partReturns_folder folder"
        tabLabel="%{getText('accordion_jsp.warrantyAdmin.maintainSupplierContracts')}"
        url="list_contracts.action?folderName=SUPPLIER RECOVERY"
        catagory="partReturns" helpCategory="Contract_Admin/Maintain_Supplier_Contracts.htm">
        <s:text name="accordion_jsp.warrantyAdmin.maintainSupplierContracts" />
    </u:openTab>
    </authz:ifPermitted>
<%-- Business Conditions Accordion --%>
	<authz:ifPermitted resource="contractAdminManageBusinessConditions">
    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
        id="manage_domain_conditions" tagType="li"
        cssClass="warrantyAdmin_folder folder"
        tabLabel="%{getText('accordionLabel.manageBusinessCondition')}"
        url="list_expressions.action?context=ContractApplicabilityRules"
        catagory="admin" helpCategory="Warranty_Admin/Business_Conditions.htm">
        <s:text
            name="accordionLabel.manageBusinessCondition" />
    </u:openTab>
    </authz:ifPermitted>

	<authz:ifPermitted resource="contractAdminManageBusinessRules">
	<u:fold
                label="%{getText('accordionLabel.manageBusinessRules')}"
                id="manage_rec_claim_processor_domain_rules_folder" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableDomainRules" />
            <authz:ifPermitted resource="contractAdminRecoveryClaimProcessorRouting">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
              id="manage_rec_claim_processor_routing_sra" tagType="li"
              cssClass="warrantyAdmin_folder folder foldableDomainRules sublist"
              tabLabel="%{getText('title.manageBusinessRule.recClaimProcessorRouting')}"
              url="list_rec_claim_processor_routing_rules.action" catagory="admin" helpCategory="Warranty_Admin/Business_Rules.htm">
              <s:text name="accordionLabel.manageBusinessRule.recClaimProcessorRouting" />
            </u:openTab>
            </authz:ifPermitted>
            </authz:ifPermitted>
</ol>
</div>
<div dojoType="dijit.layout.ContentPane" id="contractAdminPartReturn"
    label="Supplier Admin"
    iconClass="partreturns">
<ol>
<%-- Maintain Supplier Contracts Accrdion --%>
	<authz:ifPermitted resource="contractAdminManageSuppliers">
    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
        id="maintain_suppliers" tagType="li"
        cssClass="partReturns_folder folder"
        tabLabel="label.sra.partSouce.manageSuppliers"
        url="listSuppliers.action"
        catagory="partReturns" helpCategory="Contract_Admin/Manage_Suppliers.htm">
       <s:text
            name="label.sra.partSouce.manageSuppliers" />
    </u:openTab>
    </authz:ifPermitted>
    <authz:ifPermitted resource="contractAdminViewPartSourceHistory">
    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
        id="view_part_source" tagType="li"
        cssClass="partReturns_folder folder"
        tabLabel="label.sra.partSouce.viewPartSource"
        url="list_part_source.action"
        catagory="partReturns" helpCategory="Contract_Admin/View_Part_Source_History.htm">
       <s:text name="label.sra.partSouce.viewPartSource" />
    </u:openTab>
    </authz:ifPermitted>
    <authz:ifPermitted resource="contractAdminViewSupplier">
   	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="list_supplier_sra" tagType="li"
        	tabLabel="%{getText('accordionLabel.viewSupplier')}" url="list_supplier.action" 
        	catagory="supplierRecovery" cssClass="warrantyAdmin_folder folder" helpCategory="Warranty_Admin/View_Supplier.htm">
        	<s:text name="accordionLabel.viewSupplier" />
    </u:openTab>  
    </authz:ifPermitted>
    
     <!-- Manage Groups Accordion  -->
     <authz:ifPermitted resource="contractAdminManageGroups">
     <u:fold label="%{getText('accordionLabel.manageGroup')}"
                id="manage_groups_contract_admin" tagType="li"
                cssClass="uFoldStyle folder" foldableClass="foldableGroups" />            

			<authz:ifPermitted resource="contractAdminUserGroups">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_user_group_sra_contract_admin" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableGroups sublist"
                tabLabel="%{getText('title.manageGroup.userGroups')}"
                url="show_user_schemes.action" catagory="admin" helpCategory="Warranty_Admin/User_Groups.htm">
                <s:text name="accordionLabel.manageGroup.userGroups" />
            </u:openTab> 
            </authz:ifPermitted>
            </authz:ifPermitted>

</ol>
</div>
</div>