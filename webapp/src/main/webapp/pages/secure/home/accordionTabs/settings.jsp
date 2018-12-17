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

<div dojoType="dijit.layout.ContentPane" id="settings"
    title="<s:text name="accordion_jsp.accordionPane.settings" />"
    iconClass="settings">
  <div dojoType="dijit.layout.ContentPane">
  
    <ol>
    	<authz:ifPermitted resource="settingsProfile">
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="claimreports_settings" tagType="li" cssClass="reports_folder folder"
            tabLabel="%{getText('accordion_jsp.accordionPane.manageProfile')}"
            url="show_profile.action" catagory="settings" helpCategory="Settings/Profile.htm">
            <s:text name="accordion_jsp.accordionPane.profile" />
        </u:openTab>
        </authz:ifPermitted>
    </ol>            
    
	<authz:ifPermitted resource="settingsUserManagement">
	
		<ol>
            <u:fold label="%{getText('accordion_jsp.accordionPane.dealerUserMgmt')}"
	                id="dealer_user_mgmt" tagType="li"
	                cssClass="uFoldStyle folder" foldableClass="foldableDealerUserMgmt" />
            <authz:ifPermitted resource="settingsCreateUser">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="dealer_user_mgmt_create" tagType="li" cssClass="reports_folder folder foldableDealerUserMgmt sublist"
	            tabLabel="%{getText('accordion_jsp.accordionPane.dealerUserMgmt.createUser')}"
	            url="show_dealer_user.action" catagory="settings" helpCategory="Settings/User_Management.htm">
				<s:text name="accordion_jsp.accordionPane.dealerUserMgmt.createUser" />
	        </u:openTab>
	        </authz:ifPermitted>
	        <authz:ifPermitted resource="settingsUpdateUser">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="dealer_user_mgmt_update" tagType="li" cssClass="reports_folder folder foldableDealerUserMgmt sublist"
	            tabLabel="%{getText('accordion_jsp.accordionPane.dealerUserMgmt.updateUser')}"
	            url="forward_to_search_dealer_user.action" catagory="settings" helpCategory="Settings/User_Management.htm">
				<s:text name="accordion_jsp.accordionPane.dealerUserMgmt.updateUser" />
	        </u:openTab>
	        </authz:ifPermitted>

	        <authz:ifPermitted resource="settingsCreateInternalUser">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="internal_user_mgmt_create" tagType="li" cssClass="reports_folder folder foldableDealerUserMgmt sublist"
	            tabLabel="%{getText('accordion_jsp.accordionPane.internalUserMgmt.createUser')}"
	            url="show_internal_user.action" catagory="settings" helpCategory="Settings/User_Management.htm">
				<s:text name="accordion_jsp.accordionPane.internalUserMgmt.createUser" />
	        </u:openTab>
	        </authz:ifPermitted>
	        <authz:ifPermitted resource="settingsUpdateInternalUser">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="internal_user_mgmt_update" tagType="li" cssClass="reports_folder folder foldableDealerUserMgmt sublist"
	            tabLabel="%{getText('accordion_jsp.accordionPane.internalUserMgmt.updateUser')}"
	            url="forward_to_search_internal_user.action" catagory="settings" helpCategory="Settings/User_Management.htm">
				<s:text name="accordion_jsp.accordionPane.internalUserMgmt.updateUser" />
	        </u:openTab>
	        </authz:ifPermitted>
        </ol>
     </authz:ifPermitted>
     
    <ol>
        <authz:ifPermitted resource="settingsSeriesReftoCertification">
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="dealer_user_mgmt_series_ref" tagType="li" cssClass="reports_folder folder"
	            tabLabel="%{getText('accordion_jsp.accordionPane.dealerUserMgmt.seriesRefToCertification')}"
	            url="series_reference_to_certification.action" catagory="settings" helpCategory="Settings/User_Management.htm">
				<s:text name="accordion_jsp.accordionPane.dealerUserMgmt.seriesRefToCertification" />
	        </u:openTab>
        </authz:ifPermitted>
    </ol>
     
     <authz:ifUserInRole roles="dealerWarrantyAdmin,dealerSalesAdministration">

        <ol>
		  <u:fold label="%{getText('accordion_jsp.accordionPane.endCustomerMgmt')}"
	                id="end_customer_mgmt" tagType="li"
	                cssClass="reports_folder folder" foldableClass="foldableGroups" />
          <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
              id="merge_customer" tagType="li"
              cssClass="reports_folder folder foldableGroups sublist"
              tabLabel="%{getText('accordion_jsp.accordionPane.mergeEndCustomer')}"
              url="merge_customer.action" catagory="settings" helpCategory="Settings/End_Customer_Management.htm">
              <s:text name="accordion_jsp.accordionPane.mergeEndCustomer" />
          </u:openTab>
        </ol>
     </authz:ifUserInRole>
     <authz:ifUserInRole roles="supplier,dealerSiteAdmin">
		<ol>
		  <authz:ifUserInRole roles="dealerSiteAdmin">
	       <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="manage_locations" tagType="li" cssClass="reports_folder folder"	            
	            tabLabel="%{getText('accordion_jsp.accordionPane.userLocationMgmt.siteMgt')}"
	            url="show_user_locations.action" catagory="settings" helpCategory="Settings/Site_Management.htm">
				<s:text name="accordion_jsp.accordionPane.userLocationMgmt.siteMgt" />
	       </u:openTab> 
	       </authz:ifUserInRole>
	       <authz:ifUserInRole roles="supplier">
		       <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
		            id="manage_supplier_locations" tagType="li" cssClass="reports_folder folder"	            
		            tabLabel="%{getText('accordion_jsp.accordionPane.userLocationMgmt.siteMgt')}"
		            url="show_supplier_locations.action" catagory="settings" helpCategory="Settings/Site_Management.htm">
					<s:text name="accordion_jsp.accordionPane.userLocationMgmt.siteMgt" />
		       </u:openTab> 
	       </authz:ifUserInRole>
		</ol>
     </authz:ifUserInRole>

    <%--   <authz:ifPermitted resource="settingsManageUserBusinessUnitMapping">
      <ol>
                  <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                      id="bu_settings" tagType="li" cssClass="reports_folder folder"
                      tabLabel="%{getText('accordion_jsp.accordionPane.buSettings')}"
                      url="show_user_bu_mapping.action" catagory="settings" helpCategory="Settings/Manage_User_BU_Mapping.htm">
                      <s:text name="accordion_jsp.accordionPane.buSettings" />
                  </u:openTab>
              </ol>
      </authz:ifPermitted> --%>
		<authz:ifPermitted resource="settingsEmailSubscription">
         
      <ol>
      	     	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
      	            id="email_subscription" tagType="li" cssClass="reports_folder folder"
      	            tabLabel="%{getText('accordion_jsp.accordionPane.manageEmailSubs')}"
      	            url="manage_email_subscription.action" catagory="settings" helpCategory="Settings/Email_Subscription.htm">
      	            <s:text name="accordion_jsp.accordionPane.email" />
      	        </u:openTab>
        </ol>

      </authz:ifPermitted>
      <authz:ifPermitted resource="settingsMapServiceProviderBusinessUnit">
      	<ol>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="dealer_bu_mapping" tagType="li" cssClass="reports_folder folder"
                tabLabel="%{getText('accordion_jsp.accordionPane.dealerBUSettings')}"
                url="show_dealer_bu_mapping.action" catagory="settings" helpCategory="Settings/Map_Service_Provider_Business_Unit.htm">
                <s:text name="accordion_jsp.accordionPane.dealerBUSettings" />
            </u:openTab>
        </ol>
      </authz:ifPermitted> 
       <authz:ifPermitted resource="settingsMapSupplierBusinessUnit">
      	<ol>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="supplier_bu_mapping" tagType="li" cssClass="reports_folder folder"
                tabLabel="%{getText('accordion_jsp.accordionPane.supplierBUSettings')}"
                url="show_supplier_bu_mapping.action" catagory="settings">
                <s:text name="accordion_jsp.accordionPane.supplierBUSettings" />
            </u:openTab>
        </ol>
      </authz:ifPermitted>

     <ol>
     	<authz:ifPermitted resource="settingsManageRoles">
          <u:fold
              label="%{getText('accordionLabel.userRoleConfiguration')}"
              id="manage_roles_users_folder" tagType="li" cssClass="warrantyAdmin_folder folder"
              foldableClass="foldableRoleConfiguration" />

        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="manage_roles" tagType="li"
            tabLabel="%{getText('accordionLabel.manageRoles')}" url="showRoles.action"
            catagory="admin" cssClass="warrantyAdmin_folder folder foldableRoleConfiguration sublist">
            <s:text name="accordionLabel.manageRoles" />
        </u:openTab>

         <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="assign_users_to_role" tagType="li"
            tabLabel="%{getText('accordionLabel.addUsersToRole')}" url="showUsers_to_role.action"
            catagory="admin" cssClass="warrantyAdmin_folder folder foldableRoleConfiguration sublist">
            <s:text name="accordionLabel.addUsersToRole" />
        </u:openTab>

           <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="assign_roles_to_user" tagType="li"
            tabLabel="%{getText('accordionLabel.assignRolesToUser')}" url="showRoles_to_user.action"
            catagory="admin" cssClass="warrantyAdmin_folder folder foldableRoleConfiguration sublist">
            <s:text name="accordionLabel.assignRolesToUser" />
        </u:openTab>
        </authz:ifPermitted>
        <authz:ifUserInRole roles="masterSupplier">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" id="vendor_addresses" tagType="li"
                        tabLabel="%{getText('accordionLabel.vendor.addresses')}" url="vendor_addresses.action"
                        catagory="admin" cssClass="warrantyAdmin_folder folder ">
                        <s:text name="accordionLabel.vendor.addresses" />
            </u:openTab>
        </authz:ifUserInRole>
    </ol>

  </div>
</div>