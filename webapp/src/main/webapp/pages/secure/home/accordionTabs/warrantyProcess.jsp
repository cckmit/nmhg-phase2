<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 7, 2008
  Time: 3:28:33 AM
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
<div dojoType="dijit.layout.ContentPane" id="warrantyProcess"
    title="<s:text name='accordion_jsp.accordionPane.drApprovalTransfer' />"
    iconClass="inventoryManagement" style="width: 100%; background: white; overflow-y:auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>

        <u:fold label="%{getText('viewInbox_jsp.inboxButton.new_warranty_registration')}"
                id="manage_warranty_registration" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableWarranties" />
        <s:iterator value="warrantyFoldersForDR" id="foldersForDR">
            <s:if test="status.status=='Draft'">
            
            <%-- Added dealerWarrantyAdmin role to fix TSESA-499--%>
                <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration,dealerWarrantyAdmin">
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="folder_%{status.status}" tagType="li" cssClass="warranty_registration folder foldableWarranties sublist"
                tabLabel="%{folderName}"
                url="draftWarranty.action?status=%{status}&folderName=%{status.status}&transactionType=DR" catagory="warranty"
                helpCategory="Inventory/Warranty_Registration.htm">
                <s:property value="folderName"/> (<span class="count"><s:property value="folderCount"/></span>)
                </u:openTab>
                </authz:ifUserInRole>
            </s:if>
            <s:if test="status.status=='Submitted' || status.status=='Replied' || status.status=='Resubmitted'">
                <authz:ifUserInRole roles="warrantyProcessor">
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="folder_%{status.status}" tagType="li" cssClass="warranty_registration folder foldableWarranties sublist"
                tabLabel="%{folderName}"
                url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=DR" catagory="warranty"
                helpCategory="Inventory/Warranty_Registration.htm">
                <s:property value="folderName"/> (<span class="count"><s:property value="folderCount"/></span>)
                </u:openTab>
                </authz:ifUserInRole>
            </s:if>
             <%-- Added dealerWarrantyAdmin role to fix TSESA-499--%>
            <s:if test="status.status=='Rejected' || status.status=='Forwarded'">
                <authz:ifUserInRole roles="dealerSalesAdministration">
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="folder_%{status.status}" tagType="li" cssClass="warranty_registration folder foldableWarranties sublist"
                tabLabel="%{folderName}"
                url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=DR" catagory="warranty"
                helpCategory="Inventory/Warranty_Registration.htm">
                <s:property value="folderName"/> (<span class="count"><s:property value="folderCount"/></span>)
                </u:openTab>
                </authz:ifUserInRole>
            </s:if>
            <script type="text/javascript" language="javascript">
                    dojo.addOnLoad( function() {
                        refreshManager.register("folder_<s:property value="status.status"/>",
                                                "<s:property value="status.status"/>", "registrationTransferFolders.action?transactionType=DR");
                    });
             </script>
        </s:iterator>

    </ol>
    <ol>
             <u:fold label="%{getText('viewInbox_jsp.inboxButton.warranty_transfer')}"
                    id="manage_warranty_transfer" tagType="li" cssClass="uFoldStyle folder"
                    foldableClass="foldableWarranties_ETR" />
             <s:iterator value="warrantyFoldersForETR" id="foldersForETR">
                  <s:if test="status.status=='Draft'">
                    <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration">
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="folder_ETR%{status.status}" tagType="li" cssClass="warranty_transfer folder foldableWarranties_ETR sublist"
                    tabLabel="%{folderName}"
                    url="draftWarranty.action?status=%{status}&folderName=%{status.status}&transactionType=ETR" catagory="warranty"
                    helpCategory="Inventory/Equipment_Transfer_Report.htm">
                    <s:property value="folderName"/> (<span class="count"><s:property value="folderCount"/></span>)
                    </u:openTab>
                    </authz:ifUserInRole>
                  </s:if>
                 <s:if test="status.status=='Submitted' || status.status=='Replied' || status.status=='Resubmitted'">
                    <authz:ifUserInRole roles="warrantyProcessor">
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="folder_ETR%{status.status}" tagType="li" cssClass="warranty_transfer folder foldableWarranties_ETR sublist"
                    tabLabel="%{folderName}"
                    url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=ETR" catagory="warranty"
                    helpCategory="Inventory/Equipment_Transfer_Report.htm">
                    <s:property value="folderName"/>(<span class="count"><s:property value="folderCount"/></span>)
                    </u:openTab>
                    </authz:ifUserInRole>
                </s:if> 
                <s:if test="status.status=='Rejected' || status.status=='Forwarded'">
                    <authz:ifUserInRole roles="dealerSalesAdministration">
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                    id="folder_ETR%{status.status}" tagType="li" cssClass="warranty_transfer folder foldableWarranties_ETR sublist"
                    tabLabel="%{folderName}"
                    url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=ETR" catagory="warranty"
                    helpCategory="Inventory/Equipment_Transfer_Report.htm">
                    <s:property value="folderName"/> (<span class="count"><s:property value="folderCount"/></span>)
                    </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                 <script type="text/javascript" language="javascript">
                        dojo.addOnLoad( function() {
                            refreshManager.register("folder_ETR<s:property value="status.status"/>",
                                                    "<s:property value="status.status"/>", "registrationTransferFolders.action?transactionType=ETR");
                        });
                 </script>           
             </s:iterator>
    </ol>
   <%-- <ol>
        
    	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="Reduced_Coverage_Requested" tagType="li"
		        	tabLabel="%{getText('label.requests.coverage.reduced')}" 
					url="listRequestsForCoverageExtensionDealer.action?view=DealerView" 
		        	 catagory="admin" cssClass="warrantyAdmin_folder folder" helpCategory="Inventory/Warranty_Registration.htm">
		        	<s:text name="label.requests.coverage.reduced" /> (<span class="count"><s:property value="requestForExtensionCount"/></span>)
            <script type="text/javascript" language="javascript">
                dojo.addOnLoad(function() {
                    refreshManager.register("Reduced_Coverage_Requested",
                            "WAITING_FOR_RESPONSE", "requestForExtensionFolder.action");
                });
            </script>
        </u:openTab>
        
		<authz:ifUserInRole roles="reducedCoverageRequestsApprover,admin">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="Reduced_Coverage_Approval" tagType="li"
		        	tabLabel="%{getText('label.requests.coverage.reduced.pending')}" url="listRequestsForCoverageExtensionAdmin.action?view=AdminView" 
		        	catagory="inventory" cssClass="warrantyAdmin_folder folder" helpCategory="Inventory/Warranty_Registration.htm">
		        	<s:text name="label.requests.coverage.reduced.pending"/>(<span class="count"><s:property value="extensionRequestsCount"/></span>)
            <script type="text/javascript" language="javascript">
                dojo.addOnLoad(function() {
                    refreshManager.register("Reduced_Coverage_Approval",
                            "EXTENSION_REQUESTS_COUNT", "refreshExtensionRequestsCount.action");
                });
            </script>		        	
		</u:openTab>
		</authz:ifUserInRole>
		
    </ol>--%>
    </div>
</div>
</div>
