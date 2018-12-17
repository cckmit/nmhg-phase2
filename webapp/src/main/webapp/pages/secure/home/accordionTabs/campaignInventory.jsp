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


<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms" %>
<%@taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<div dojoType="dijit.layout.ContentPane" id="campaigns"
     title="<s:text name='accordion_jsp.campaigns'/>"
     iconClass="campaigns">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
        <ol>
            <!-- Pending Campaigns Accordion -->
            <authz:ifUserInRole roles="dealerWarrantyAdmin">
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="pending_campaigns" tagType="li" cssClass="inventory_folder folder"
                           tabLabel="%{getText('accordion_jsp.campaigns.pendingCampaigns')}"
                           url="list_notifications.action?folderName=PENDING CAMPAIGNS" catagory="campaigns" helpCategory="Field_Modifications/Pending_Campaigns.htm">
                    <s:text name="accordion_jsp.campaigns.pendingCampaigns"/>(<span class="count"><s:property
                        value="pendingCampaignsCount"/></span>)
                </u:openTab>
                  <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="update_campaigns" tagType="li" cssClass="inventory_folder folder"
                           tabLabel="%{getText('accordion_jsp.campaigns.updatingCampaigns')}"
                           url="list_updatedCampaings.action?folderName=UPDATING CAMPAIGNS" catagory="campaigns" helpCategory="Field_Modifications/Pending_Campaigns.htm">
                    <s:text name="accordion_jsp.campaigns.updatingCampaigns"/>(<span class="count"><s:property
                        value="updatingCampaignsCount"/></span>)
                </u:openTab>
                 <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="denied_campaigns" tagType="li" cssClass="inventory_folder folder"
                           tabLabel="%{getText('accordion_jsp.campaigns.deniedCampaigns')}"
                           url="list_deniedCampaignNotifications.action?folderName=DENIED CAMPAIGNS" catagory="campaigns" helpCategory="Field_Modifications/Pending_Campaigns.htm">
                    <s:text name="accordion_jsp.campaigns.deniedCampaigns"/>(<span class="count"><s:property
                        value="deniedCampaignsCount"/></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad(function() {
                        refreshManager.register("pending_campaigns", "PENDING CAMPAIGNS", "pendingCampaignsFolder.action");
                        refreshManager.register("update_campaigns", "UPDATING CAMPAIGNS", "updatingCampaignsFolder.action");
                        refreshManager.register("denied_campaigns", "DENIED CAMPAIGNS", "deniedCampaignsFolder.action");
                    });
                </script>
            </authz:ifUserInRole>
            <authz:ifPermitted resource="fPIPendingFPIUpdateRequests">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="pending_review_campaigns" tagType="li" cssClass="inventory_folder folder"
                           tabLabel="%{getText('accordion_jsp.campaigns.pendingReviewCampaigns')}"
                           url="list_pendingReviewCampaignNotifications.action?folderName=PENDING REVIEW CAMPAIGNS" catagory="campaigns" helpCategory="Field_Modifications/Pending_Campaigns.htm">
                    <s:text name="accordion_jsp.campaigns.pendingReviewCampaigns"/>(<span class="count"><s:property
                        value="pendingReviewCampaignsCount"/></span>)
                </u:openTab>
                <script type="text/javascript" language="javascript">
                    dojo.addOnLoad(function() {
                       refreshManager.register("pending_review_campaigns", "PENDING REVIEW CAMPAIGNS", "pendingReviewCampaignsFolder.action");                     
                    });
                </script>
            </authz:ifPermitted>
        </ol>
    </div>
           
        <authz:ifPermitted resource="fPIPredefinedSearch"> 
	   		<t:daSection id="fieldModSearchFolders"
	      		fetchFrom="fieldModSearchFolders.action"
	      		fetchOn="/accordion/refreshsearchfolders"
	      		title="%{getText('accordionLabel.viewClaim.searchFolders')}"
	      		appendMode="false"
	      		cssClass="searchFolders"/>
   		</authz:ifPermitted>
    
</div>