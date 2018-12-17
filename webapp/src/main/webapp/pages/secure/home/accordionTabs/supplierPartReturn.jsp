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
<div dojoType="dijit.layout.ContentPane" id="supplierPartReturns"
    title="<s:text name="accordion_jsp.accordionPane.supplierRecovery" />"
    iconClass="partreturns">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>
        <u:fold label="%{getText('accordion_jsp.warrantyAdmin.partsClaimed')}"
            id="parts_claimed_supplier" tagType="li" cssClass="uFoldStyle folder"
            foldableClass="foldableSupplier" shownInitially="true"/>

        <s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@NEW" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_1" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_1", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>
        
        <s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@ON_HOLD_FOR_PART_RETURN" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_10" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_10", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>
		
		<s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@ACCEPTED" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_2" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_2", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>
        
        <s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@SUPPLIER_DISPUTED_CLAIMS_TASK_NAME" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_3" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_3", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>
        
        <s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@CONFIRM_PART_RETURNS" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_4" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_4", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>
        
        <s:set name="taskName"
            value="@tavant.twms.jbpm.WorkflowConstants@REOPENED_CLAIMS" />
        <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
        <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="parts_supplier_5" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
            tabLabel="%{getTaskDisplayName(#taskName)}"
            url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
            catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
            <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                value="%{#taskCount}" /></span>)
        </u:openTab>
        <script type="text/javascript" language="javascript">
            dojo.addOnLoad( function() {
                refreshManager.register("parts_supplier_5", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
            });
        </script>

        <s:set name="taskName"
                    value="@tavant.twms.jbpm.WorkflowConstants@CONFIRM_DEALER_PART_RETURNS" />
                <s:set name="actionUrl" value="getPartReturnActionUrl(#taskName)" />
                <s:set name="taskCount" value="getPartReturnTaskCount(#taskName)" />
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="parts_supplier_6" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
                    tabLabel="%{getTaskDisplayName(#taskName)}"
                    url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
                    catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
                    <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                        value="%{#taskCount}" /></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad( function() {
                        refreshManager.register("parts_supplier_6", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
                    });
                </script>

        <s:set name="taskName"
                    value="@tavant.twms.jbpm.WorkflowConstants@PART_FOR_RETURN_TO_NMHG" />
                <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
                <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="parts_supplier_7" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
                    tabLabel="%{getTaskDisplayName(#taskName)}"
                    url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
                    catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
                    <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                        value="%{#taskCount}" /></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad( function() {
                        refreshManager.register("parts_supplier_7", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
                    });
                </script>

        <s:set name="taskName"
                    value="@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_GENERATED_TO_NMHG" />
                <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
                <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="parts_supplier_8" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
                    tabLabel="%{getTaskDisplayName(#taskName)}"
                    url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
                    catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
                    <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                        value="%{#taskCount}" /></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad( function() {
                        refreshManager.register("parts_supplier_8", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
                    });
                </script>
        <s:set name="taskName"
                    value="@tavant.twms.jbpm.WorkflowConstants@PARTS_SHIPPED_TO_NMHG" />
                <s:set name="actionUrl" value="getSupplierRecoveryActionUrl(#taskName)" />
                <s:set name="taskCount" value="getSupplierRecoveryTaskCount(#taskName)" />
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="parts_supplier_9" tagType="li" cssClass="partReturns_folder folder foldableSupplier sublist"
                    tabLabel="%{getTaskDisplayName(#taskName)}"
                    url="%{#actionUrl}.action?folderName=%{#taskName}&actionUrl=%{#actionUrl}&taskName=%{#taskName}"
                    catagory="supplierRecovery" helpCategory="Supplier_Recovery/Supply_Recovery.htm">
                    <s:property value="%{getTaskDisplayName(#taskName)}" /> (<span class="count"><s:property
                        value="%{#taskCount}" /></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad( function() {
                        refreshManager.register("parts_supplier_9", "<s:property value="%{#taskName}"/>", "supplierRecoveryFolders.action");
                    });
                </script>
    </ol>

    	<t:daSection id="recoveryClaimSearchFolders"
			      fetchFrom="recoveryClaimSearchFolders.action"
			      fetchOn="/accordion/refreshsearchfolders"
			      title="%{getText('accordionLabel.viewClaim.searchFolders')}"
			      appendMode="false"
			  cssClass="searchFolders"/>
</div>
</div>