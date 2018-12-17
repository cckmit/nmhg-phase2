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
<div dojoType="dijit.layout.ContentPane" id="supplierRecovery"
        title="<s:text name="accordion_jsp.accordionPane.recoveryProcessor" />"
        iconClass="partreturns">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>
    	<authz:ifUserInRole roles="recoveryProcessor,sra">
	    <%-- Claims Due For Recovery Accordion --%>
	        <u:fold label="%{getText('accordion_jsp.warrantyAdmin.claimsDueForRecovery')}"
	            id="claims_due_for_recovery" tagType="li" cssClass="uFoldStyle folder"
	            foldableClass="foldableSRA1" shownInitially="false"/>
	
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@SUPPLIER_RECOVERY_TASK_NAME" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_1" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_1", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@ON_HOLD" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_3" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_3", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	        
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@NOT_FOR_RECOVERY_REQUEST" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_4" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_4", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	        
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@NOT_FOR_RECOVERY_RESPONSE" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_8" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_8", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	        
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@TRANSFERRED" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_11" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_11", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>

	        <s:set name="taskName"
            	            value="@tavant.twms.jbpm.WorkflowConstants@REOPENED" />
            	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
            	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
            	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            	            id="parts_sra_13" tagType="li" cssClass="partReturns_folder folder foldableSRA1 sublist"
            	            tabLabel="%{getTaskDisplayName(#taskName)}"
            	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Due_for_Recovery.htm">
            	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
            	                value="%{#taskCount}" /></span>)
            	        </u:openTab>
            	        <script type="text/javascript" language="javascript">
            	            dojo.addOnLoad( function() {
            	                refreshManager.register("parts_sra_13", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            	            });
            	        </script>
	        
		<%-- Supplier Response Accordion --%>
	        <u:fold label="%{getText('accordion_jsp.warrantyAdmin.claimsUnderRecovery')}"
	            id="claims_under_recovery" tagType="li" cssClass="uFoldStyle folder"
	            foldableClass="foldableSRA2" />
			
			<s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@SRA_REVIEW" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_10" tagType="li" cssClass="partReturns_folder folder foldableSRA2 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Under_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_10", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	        
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@SUPPLIER_ACCEPTED" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_9" tagType="li" cssClass="partReturns_folder folder foldableSRA2 sublist"
	            tabLabel="%{getTaskDisplayName(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Under_Recovery.htm">
	            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_9", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
	        
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@AWAITING_SUPPLIER_RESPONSE" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_5" tagType="li" cssClass="partReturns_folder folder foldableSRA2 sublist"
	            tabLabel="%{getText(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Under_Recovery.htm">
	            <s:property value="%{#taskName}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_5", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
        </authz:ifUserInRole>
        
        <authz:ifUserInRole roles="processor">
	        <s:set name="taskName"
	            value="@tavant.twms.jbpm.WorkflowConstants@NOT_FOR_RECOVERY" />
	        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
	        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="parts_sra_12" tagType="li" cssClass="partReturns_folder folder foldableSRA2 sublist"
	            tabLabel="%{getText(#taskName)}"
	            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
	            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Claims_Under_Recovery.htm">
	            <s:property value="%{#taskName}" /> (<span class="count"><s:property
	                value="%{#taskCount}" /></span>)
	        </u:openTab>
	        <script type="text/javascript" language="javascript">
	            dojo.addOnLoad( function() {
	                refreshManager.register("parts_sra_12", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
	            });
	        </script>
		</authz:ifUserInRole>
		
		<authz:ifUserInRole roles="sra">
	    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="multiple_claim_maintenance_sra" tagType="li"
		        	tabLabel="%{getText('title.claim.multipleClaimMaintenance')}" 
		        	url="new_search_expression.action?context=RecoveryClaimSearches&isMultiRecClaimMaintainace=true" 
		        	catagory="supplierRecovery" cssClass="claims_folder folder" helpCategory="Supplier_Recovery/Multi_Claim_Maintenance.htm">
		        	<s:text name="title.claim.multipleClaimMaintenance" />
		</u:openTab>
	</authz:ifUserInRole>
		
    </ol>

    <authz:ifUserInRole roles="sra,recoveryProcessor,readOnly">
		<t:daSection id="recoveryClaimSearchFolders"
      fetchFrom="recoveryClaimSearchFolders.action"
      fetchOn="/accordion/refreshsearchfolders"
      title="%{getText('accordionLabel.viewClaim.searchFolders')}"
      appendMode="false"
  cssClass="searchFolders"/>
	</authz:ifUserInRole>			  
    </div>
</div>