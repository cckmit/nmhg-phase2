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
<div dojoType="dijit.layout.ContentPane" id="partreturns"
	title="<s:text name='accordion_jsp.accordionPane.partReturns' />"
	iconClass="partreturns">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<ol>
	<s:iterator value="partsReturnFolders" status="status">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="parts_%{top[0]}" tagType="li"
			cssClass="partReturns_folder folder" tabLabel="%{top[4]}"
			url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
			catagory="partReturns" helpCategory="%{getHtmlFileBasedOnFolderName('Part_Returns',top[4])}">
			<s:property value="%{top[4]}" /> (<span class="count"><s:property
				value="%{top[2]}" /></span>)
            </u:openTab>
		<%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
		<script type="text/javascript" language="javascript">
                dojo.addOnLoad( function() {
                    refreshManager.register("parts_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "partsReturnFolders.action");
                });
            </script>
	</s:iterator>
	<authz:ifUserInRole roles="dealerWarrantyAdmin">
		<s:if test="!isLoggedInUserAnInternalUser() && isEnableDealersToViewPR()">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="part_return_for_Dealer_home_Page" tagType="li"
				cssClass="partReturns_folder folder"
				tabLabel="%{getText('title.required.PartsReturn')}"
				url="list_required_part_returns_for_dealer.action"
				catagory="partReturns">
				<s:text name="accordion.label.requiredPartsReturn" />
			</u:openTab>
		</s:if>
	</authz:ifUserInRole>
</ol>

	<!--Adding Prepare Due parts inbox for dealer 
	
	<ol>
	    <authz:ifUserInRole roles="dealer">	    
			 <u:openTab 
				  id="part_return_prepare_due_parts" tagType="li" cssClass="partReturns_folder folder"
				  tabLabel="label.partReturn.prepareDueParts" url="prepareDueParts.action?folderName=Prepare Due Parts"
				  catagory="partReturns">
				  <s:text name="label.partReturn.prepareDueParts"/> 
		     </u:openTab> 
	    </authz:ifUserInRole>	
	 </ol>

	  Add wrpa inbox for processor
	 	  Note : the action will be same for both preparedueparts and wpra as they fetch same data for different roles 
	 
	 
	 <ol>
	    <authz:ifUserInRole roles="processor">	    
			 <u:openTab 
				  id="part_return_WPRA" tagType="li" cssClass="partReturns_folder folder"
				  tabLabel="label.partReturn.wpra" url="prepareDueParts.action?folderName=WPRA"
				  catagory="partReturns">
				  <s:text name="label.partReturn.wpra"/> 
		     </u:openTab> 
	    </authz:ifUserInRole>	
	 </ol>
 -->
<s:if test="!hasOnlyCevaRole()">
<t:daSection id="partReturnSearchFolders"
	fetchFrom="partReturnSearchFolders.action"
	fetchOn="/accordion/refreshsearchfolders"
	title="%{getText('accordionLabel.viewClaim.searchFolders')}"
	appendMode="false" cssClass="searchFolders" />
</s:if>
<s:if test="isLoggedInUserADealer()">
	<ol>
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="accepted_claim_search_without_part_return" tagType="li"
				cssClass="partReturns_folder folder"
				tabLabel="%{getText('accordion.inbox.acceptedClaimSearch')}"
				url="acceptedClaimSearch.action?folderName=ACCEPTED_CLAIM_SEARCH"
				catagory="partReturns">
				<s:text name="accordion.inbox.acceptedClaimSearch" />
		</u:openTab>
	</ol>
</s:if>
<s:if test="isLoggedInUserAnInternalUser()">
	<ol>
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="part_shipped_not_received_search" tagType="li"
				cssClass="partReturns_folder folder"
				tabLabel="%{getText('label.jbpm.task.part.shipped.not.received')}"
				url="partShippedNotReceivedSearch.action?folderName=PART_SHIPPED_NOT_RECEIVED_SEARCH"
				catagory="partReturns">
				<s:text name="label.jbpm.task.part.shipped.not.received" />
		</u:openTab>
	</ol>
</s:if>
</div>
</div>