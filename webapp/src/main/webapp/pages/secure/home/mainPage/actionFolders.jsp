<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 20, 2008
  Time: 7:42:26 PM
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
<html>
<head><title>Action Folders Page</title></head>
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <td class="ItemsHdrAction" ><s:text name="label.common.actionFolders"/></td>
</tr>

<!-- This section displays the inboxes related to Claim Flow with inboxCount>0 -->
<authz:ifUserInRole roles="processor,dsm,dsmAdvisor,recoveryProcessor,cpAdvisor,dealerWarrantyAdmin">
    <s:iterator value="claimFolders" status="status">
        <s:set name="claimFolderInboxCounter" value="%{top[2]}"/>
        <s:if test="#claimFolderInboxCounter>0">
            <tr>
                <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                	<s:if test="%{top[3].equals(getText('label.jbpm.task.claim.failure.reports'))}">
                   <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_claims_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[3]}" url="displayClaimFailureReports.action?folderName=%{top[1]}" catagory="myClaims">
                        <s:property value="%{top[3]}"/> (<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab>
                </s:if>
                <s:else>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_claims_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[3]}" url="claims.action?folderName=%{top[1]}" catagory="myClaims">
                        <s:property value="%{top[3]}"/> (<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab> 
                </s:else>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	 dojo.connect(dojo.byId("inside_claims_<s:property value="%{top[0]}"/>"), "onmousedown", function()  {                            	
                        	  autoRefreshFolderCount();
                        	  dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);	                         	    
                              refreshManager.register("inside_claims_<s:property value="%{top[0]}"/>",
                                    "<s:property value="%{top[1]}"/>", "claimFolders.action");
                          });
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </s:iterator>
</authz:ifUserInRole>

<!-- This section displays the inboxes related to PartReturn Flow with inboxCount>0 -->
<authz:ifUserInRole roles="cevaProcessor,processor,admin,receiver,sra,partshipper,dsmAdvisor,recoveryProcessor,inspector,
                           dealerWarrantyAdmin">
    <s:iterator value="partsReturnFolders" status="status">
        <s:set name="partReturnFolderInboxCounter" value="%{top[2]}"/>
        <s:if test="#partReturnFolderInboxCounter>0">
            <tr>
                <td class="ItemsLabels" >
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="part_returns_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[4]}"
                               url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
                               catagory="partReturns">
                        <s:property value="%{top[4]}"/> (<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab>
                        <%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("part_returns_<s:property value="%{top[0]}"/>"), "onmousedown", function()  {
                          	  autoRefreshFolderCount(); 
                          	  dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                         	     
                              refreshManager.register("part_returns_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "partsReturnFolders.action");
                        	});
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </s:iterator>
</authz:ifUserInRole>

<!-- This section displays the inboxes related to CampaignClaim inbox of dealer -->
<authz:ifUserInRole roles="dealerWarrantyAdmin">
    <authz:ifUserNotInRole roles="processor">        
        <s:if test="pendingCampaignsCount>0">
            <tr>
                <td class="ItemsLabels" >
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="pending_campaigns_mainPage"
                               tagType="a" tabLabel="%{getText('accordion_jsp.campaigns.pendingCampaigns')}"
                               url="list_notifications.action?folderName=PENDING CAMPAIGNS" catagory="campaigns">
                        <s:text name="accordion_jsp.campaigns.pendingCampaigns"/>(<span class="count"><s:property
                            value="pendingCampaignsCount"/></span>)
                    </u:openTab>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("pending_campaigns_mainPage"), "onmousedown", function()  {
                              autoRefreshFolderCount();
                              dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                             
                              refreshManager.register("pending_campaigns_mainPage", "PENDING CAMPAIGNS", "pendingCampaignsFolder.action");
                        	});  
                        });
                    </script>
                </td>
            </tr>
        </s:if>
        <s:if test="updatingCampaignsCount>0">
            <tr>
                <td class="ItemsLabels" >
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="updating_campaigns_mainPage"
                               tagType="a" tabLabel="%{getText('accordion_jsp.campaigns.updatingCampaigns')}"
                               url="list_updatedCampaings.action?folderName=UPDATING CAMPAIGNS" catagory="campaigns">
                        <s:text name="accordion_jsp.campaigns.updatingCampaigns"/>(<span class="count"><s:property
                            value="updatingCampaignsCount"/></span>)
                    </u:openTab>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("updating_campaigns_mainPage"), "onmousedown", function()  {
                              autoRefreshFolderCount();
                              dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                             
                              refreshManager.register("updating_campaigns_mainPage", "UPDATING CAMPAIGNS", "updatingCampaignsFolder.action");
                        	});  
                        });
                    </script>
                </td>
            </tr>
        </s:if>
         <s:if test="deniedCampaignsCount>0">
            <tr>
                <td class="ItemsLabels" >
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="denied_campaigns_mainPage"
                               tagType="a" tabLabel="%{getText('accordion_jsp.campaigns.deniedCampaigns')}"
                               url="list_deniedCampaignNotifications.action?folderName=DENIED CAMPAIGNS" catagory="campaigns">
                        <s:text name="accordion_jsp.campaigns.deniedCampaigns"/>(<span class="count"><s:property
                            value="deniedCampaignsCount"/></span>)
                    </u:openTab>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("denied_campaigns_mainPage"), "onmousedown", function()  {
                              autoRefreshFolderCount();
                              dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                             
                              refreshManager.register("denied_campaigns_mainPage", "DENIED CAMPAIGNS", "deniedCampaignsFolder.action");
                        	});  
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </authz:ifUserNotInRole>
    <authz:ifUserInRole roles="internalUserAdmin"> 
     <s:if test="pendingReviewCampaignsCount>0">
            <tr>
                <td class="ItemsLabels" >
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="pending_review_campaigns_mainPage"
                               tagType="a" tabLabel="%{getText('accordion_jsp.campaigns.pendingReviewCampaigns')}"
                               url="list_pendingReviewCampaignNotifications.action?folderName=PENDING REVIEW CAMPAIGNS" catagory="campaigns">
                        <s:text name="accordion_jsp.campaigns.pendingReviewCampaigns"/>(<span class="count"><s:property
                            value="pendingReviewCampaignsCount"/></span>)
                    </u:openTab>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("pending_review_campaigns_mainPage"), "onmousedown", function()  {
                              autoRefreshFolderCount();
                              dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                             
                              refreshManager.register("pending_review_campaigns_mainPage", "PENDING REVIEW CAMPAIGNS", "pendingReviewCampaignsFolder.action");
                        	});  
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </authz:ifUserInRole>
</authz:ifUserInRole>

		<authz:ifUserInRole roles="processor,admin,receiver,sra,partshipper,dsmAdvisor,recoveryProcessor,inspector,
                           inventoryAdmin,warrantyProcessor,dealerSalesAdministration,sra,supplier">
			<s:if test="requestForExtensionCount>0">
			<tr>
				<td class="ItemsLabels">	
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>			  
						<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="reduced_coverage_mainPage" tagType="a"
				        	tabLabel="Pending Reduced Coverage Requests" 
							url="listRequestsForCoverageExtensionDealer.action?view=DealerView" 
				        	catagory="inventory">
				        	<s:text name="label.requests.coverage.reduced" />(<span class="count"><s:property value="requestForExtensionCount"/></span>)
				       </u:openTab>			
					   <script type="text/javascript" language="javascript">
								dojo.addOnLoad( function() {
									dojo.connect(dojo.byId("reduced_coverage_mainPage"), "onmousedown", function()  {
			                           autoRefreshFolderCount();
			                           dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);			                             	
									   refreshManager.register("reduced_coverage_mainPage", "WAITING_FOR_RESPONSE", "requestForExtensionFolder.action");
									});   
								});
						</script>
				</td></tr>
			</s:if>
		</authz:ifUserInRole>
<authz:ifUserInRole roles="reducedCoverageRequestsApprover">
    <s:if test="extensionRequestsCount>0">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="Reduced_Coverage_Approval_Main"
                           tagType="a"
                           tabLabel="Pending Reduced Coverage Requests"
                           url="listRequestsForCoverageExtensionAdmin.action?view=AdminView"
                           catagory="inventory">
                    <s:text name="label.requests.coverage.reduced.pending"/>(<span class="count"><s:property
                        value="extensionRequestsCount"/></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad(function() {
                    	dojo.connect(dojo.byId("Reduced_Coverage_Approval_Main"), "onmousedown", function()  {
	                        autoRefreshFolderCount();
	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);	                           
                            refreshManager.register("Reduced_Coverage_Approval_Main",
                                "EXTENSION_REQUESTS_COUNT", "refreshExtensionRequestsCount.action");
                    	});
                    });
                </script>
            </td>
        </tr>
    </s:if>
</authz:ifUserInRole>
<!-- This section displays the inboxes related to SupplierRecovery Flow with inboxCount>0 for sra/recoveryProcessor-->
<authz:ifUserInRole roles="sra,recoveryProcessor">
    <s:iterator value="supplierRecoveryFolders" status="status">
        <s:set name="supplierRecoveryFolderInboxCounter" value="%{top[2]}"/>
        <s:set name="recClaimInboxName" value="%{top[4]}" />
        <s:if test="#supplierRecoveryFolderInboxCounter>0">
            <tr>
                <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_supplier_parts_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[4]}"
                               url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
                               catagory="supplierRecovery">
                        <s:property value="%{top[4]}"/> <s:if test="#recClaimInboxName == 'On Hold' || #recClaimInboxName == 'Transferred'"> (<s:text name="accordion_jsp.accordionPane.recoveryProcessor" /> )</s:if>(<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab>
                        <%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_supplier_parts_<s:property value="%{top[0]}"/>"), "onmousedown", function()  {
    	                        autoRefreshFolderCount(); 
    	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);   	                          
                                refreshManager.register("inside_supplier_parts_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "supplierRecoveryFolders.action");
                        	});
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </s:iterator>
</authz:ifUserInRole>

<!-- This section displays the inboxes related to SupplierRecovery Flow with inboxCount>0 for supplier-->
<authz:ifUserInRole roles="supplier">
    <s:iterator value="supplierRecoveryFolders" status="status">
        <s:set name="supplierRecoveryFolderInboxCounter" value="%{top[2]}"/>
        <s:if test="#supplierRecoveryFolderInboxCounter>0">
            <tr>
                <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_parts_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[4]}"
                               url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
                               catagory="supplierRecovery">
                        <s:property value="%{top[4]}"/> (<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab>
                        <%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_parts_<s:property value="%{top[0]}"/>"), "onmousedown", function()  {
    	                        autoRefreshFolderCount(); 
    	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);                       
                               refreshManager.register("inside_parts_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "supplierRecoveryFolders.action");
                        	});
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </s:iterator>
</authz:ifUserInRole>

<!-- This section displays the inboxes related to SupplierRecovery Flow with inboxCount>0 for partshipper/supplier-->
<authz:ifUserInRole roles="partshipper,supplier"> 
    <s:iterator value="supplierRecoveryPartReturnFolders" status="status">
        <s:set name="supplierRecoveryFolderInboxCounter" value="%{top[2]}"/>
        <s:if test="#supplierRecoveryFolderInboxCounter>0">
            <tr>
                <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="inside_parts_partShipper_%{top[0]}"
                               tagType="a"
                               tabLabel="%{top[4]}"
                               url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
                               catagory="supplierRecovery">
                        <s:property value="%{top[4]}"/> (<span class="count"><s:property value="%{top[2]}"/></span>)
                    </u:openTab>
                        <%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
                    <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("inside_parts_partShipper_<s:property value="%{top[0]}"/>"), "onmousedown", function()  {
    	                        autoRefreshFolderCount();
    	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);  
                                refreshManager.register("inside_parts_partShipper_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "supplierRecoveryPartReturnFolders.action");
                        	});
                        });
                    </script>
                </td>
            </tr>
        </s:if>
    </s:iterator>    
</authz:ifUserInRole>

<!-- This section displays the inboxes related to Delivery Reports -->
<%-- Added dealerWarrantyAdmin role to fix TSESA-499 --%>
<authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration,warrantyProcessor,dealerWarrantyAdmin">
<s:iterator value="warrantyFoldersForDR" id="foldersForDR">
    <s:if test="folderCount>0">
        <tr>
            <td class="ItemsLabels">
             <s:if test="status.status!='Deleted'">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
			 </s:if> 	
                <s:if test="status.status=='Draft'">
                <%-- Added dealerWarrantyAdmin role  to fix TSESA-499 --%>
                    <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration,dealerWarrantyAdmin">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="draftWarranty.action?status=%{status}&folderName=%{status.status}&transactionType=DR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.new_warranty_registration"/>) (<span class="count"><s:property value="folderCount"/></span>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <s:if test="status.status=='Submitted' || status.status=='Replied' || status.status=='Resubmitted'">
                    <authz:ifUserInRole roles="warrantyProcessor">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=DR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.new_warranty_registration"/>) (<span class="count"><s:property value="folderCount"/></span>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <%-- Added dealerWarrantyAdmin role  to fix TSESA-499 --%>
                <s:if test="status.status=='Rejected' || status.status=='Forwarded'">
                    <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration,dealerWarrantyAdmin">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=DR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.new_warranty_registration"/>) (<span class="count"><s:property value="folderCount"/></span>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad(function() {
                    	dojo.connect(dojo.byId("inside_folder_<s:property value="status.status"/>"), "onmousedown", function()  {                    		
	                        autoRefreshFolderCount();
	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);	                        	                        
                            refreshManager.register("inside_folder_<s:property value="status.status"/>",
                                "<s:property value="status.status"/>", "registrationTransferFolders.action?transactionType=DR");
                    	});
                    });
                </script>
            </td>
        </tr>
    </s:if>
</s:iterator>
</authz:ifUserInRole>

<!-- This section displays the inboxes related to Warranty transfers -->
<authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration,warrantyProcessor">
<s:iterator value="warrantyFoldersForETR" id="foldersForETR">
    <s:if test="folderCount>0">
        <tr>
            <td class="ItemsLabels">
               <s:if test="status.status!='Deleted'">
				 <img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
				</s:if> 
                <s:if test="status.status=='Draft'">
                    <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_ETR%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="draftWarranty.action?status=%{status}&folderName=%{status.status}&transactionType=ETR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.warranty_transfer"/>) (<s:property value="folderCount"/>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <s:if test="status.status=='Submitted' || status.status=='Replied' || status.status=='Resubmitted'">
                    <authz:ifUserInRole roles="warrantyProcessor">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_ETR%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=ETR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.warranty_transfer"/>) (<s:property value="folderCount"/>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <s:if test="status.status=='Rejected' || status.status=='Forwarded'">
                    <authz:ifUserInRole roles="inventoryAdmin,dealerSalesAdministration">
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="inside_folder_ETR%{status.status}" tagType="a"
                                   tabLabel="%{folderName}"
                                   url="warrantyProcess.action?status=%{status}&folderName=%{status.status}&transactionType=ETR"
                                   catagory="warranty">
                            <s:property value="folderName"/> (<s:text name="viewInbox_jsp.inboxButton.warranty_transfer"/>) (<s:property value="folderCount"/>)
                        </u:openTab>
                    </authz:ifUserInRole>
                </s:if>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad(function() {
                    	dojo.connect(dojo.byId("inside_folder_ETR<s:property value="status.status"/>"), "onmousedown", function()  {
	                        autoRefreshFolderCount();
	                        dojo.subscribe("/refresh/stopAutoRefresh", null, stopAutoRefreshing);  
                            refreshManager.register("inside_folder_ETR<s:property value="status.status"/>",
                                "<s:property value="status.status"/>", "registrationTransferFolders.action?transactionType=ETR");
                    	});
                    });
                </script>
            </td>
        </tr>
    </s:if>
</s:iterator>
</authz:ifUserInRole>
</html>