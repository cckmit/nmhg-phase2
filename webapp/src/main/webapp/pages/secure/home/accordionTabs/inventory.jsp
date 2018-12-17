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
<div dojoType="dijit.layout.ContentPane" id="inventory"
    title="<s:text name='accordion_jsp.accordionPane.inventory' />"
    iconClass="inventory" style="width: 100%; background: white; overflow-y:auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>
    <%-- Club Car Starts here --%>
          <authz:ifPermitted resource="inventoryStock">
            <%-- Stock Accordion --%>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="stock_inventory" tagType="li" cssClass="inventory_folder folder"
                tabLabel="%{getText('accordion_jsp.inventory.stock')}"
                url="inventory.action?folderName=STOCK&draft=false" catagory="inventory" helpCategory="Inventory/Stock.htm">
                <s:text name="accordion_jsp.inventory.stock" /><span class="count" id="stock_inventory_count"></span>
                 <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("stock_inventory"), "onmousedown", function()  {
                        	    populateStockCount();
                        	});
                        });
                    </script>
            </u:openTab>
            </authz:ifPermitted>

            <%-- Retailed Accordion --%>
            <authz:ifPermitted resource="inventoryRetailed">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="retailed_inventory" tagType="li"
                cssClass="inventory_folder folder"
                tabLabel="%{getText('accordion_jsp.inventory.retailed')}"
                url="inventory.action?folderName=RETAIL&draft=false" catagory="inventory" helpCategory="Inventory/Retailed.htm">
                <s:text name="accordion_jsp.inventory.retailed" />
            </u:openTab>
            </authz:ifPermitted>

             <%-- Vintage Stock Accordion --%>
            <s:if test = "displayVintageStockInbox()">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="vintage_stock_inventory" tagType="li" cssClass="inventory_folder folder"
                tabLabel="%{getText('accordion_jsp.inventory.vintageStock')}"
                url="inventory.action?folderName=VINTAGE_STOCK&draft=false" catagory="inventory" helpCategory="Inventory/Stock.htm">
                <s:text name="accordion_jsp.inventory.vintageStock" /><span class="count" id="vintage_stock_count"></span>
                 <script type="text/javascript" language="javascript">
                        dojo.addOnLoad(function() {
                        	dojo.connect(dojo.byId("vintage_stock_inventory"), "onmousedown", function() {
                        	    populateVintageStockCount();
                        	});
                        });
                    </script>
            </u:openTab>
            </s:if>
        <authz:ifUserInRole roles="dealer">
                <s:if test="dealerEligibleToPerformRMT">
                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                               id="retail_machine_transfer" tagType="li"
                               tabLabel="%{getText('title.fleetmanagement.retailMachineTransfer')}"
                               url="show_retail_machine_transfer.action?bulkRTT=true"
                               catagory="inventory" cssClass="inventory_folder folder" helpCategory="Inventory/Retail_Machine_Transfer.htm">
                        <s:text name="title.fleetmanagement.retailMachineTransfer"/>
                    </u:openTab>
                </s:if>
        </authz:ifUserInRole>
        <!-- Fix for SLMSPROD-1263 -->
	<s:if test="!isBuConfigAMER()">
        <authz:ifUserInRole roles="inventoryAdmin,admin">
        <%-- Fleet Inventory Management Accordion --%>

        	<u:fold label="%{getText('accordion_jsp.warrantyAdmin.fleetManagement')}"
                id="manage_fleet_inventory" tagType="li" cssClass="uFoldStyle folder"
                foldableClass="foldableFleetInv" />
        <authz:ifUserInRole roles="inventoryAdmin">
            <u:openTab decendentOf="%{getText('title.dealertodealer.dealertodealertransfer')}"  id="fleet_inventory_D2D" tagType="li"
			     tabLabel="%{getText('title.dealertodealer.dealertodealertransfer')}" url="show_dealertodealer_transfer.action"
			     catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Dealer_to_Dealer_Transfer.htm">
			     <s:text name="title.dealertodealer.dealertodealertransfer"></s:text>
			</u:openTab>

			<u:openTab decendentOf="%{getText('title.managePolicy.warrantyRegistration')}"  id="fleet_inventory_DR" tagType="li"
			     tabLabel="%{getText('title.managePolicy.warrantyRegistration')}" url="create_warranty.action?allowInventorySelection=true"
			     catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Warranty_Registration.htm">
			     <s:text name="title.managePolicy.warrantyRegistration"></s:text>
			</u:openTab>

			<u:openTab decendentOf="%{getText('home_jsp.fileMenu.warrantyTransfer')}"  id="fleet_inventory_ETR" tagType="li"
			     tabLabel="%{getText('home_jsp.fileMenu.warrantyTransfer')}" url="show_warranty_transfer.action?allowInventorySelection=true"
			     catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Equipment_Transfer_Report.htm">
			     <s:text name="home_jsp.fileMenu.warrantyTransfer"></s:text>
			</u:openTab>

			<u:openTab decendentOf="%{getText('title.fleetmanagement.retailMachineTransfer')}" id="retail_machine_transfer" tagType="li"
	        	tabLabel="%{getText('title.fleetmanagement.retailMachineTransfer')}" url="show_retail_machine_transfer.action?bulkRTT=true"
	        	catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Retail_Machine_Transfer.htm">
	        	<s:text name="title.fleetmanagement.retailMachineTransfer" />
			</u:openTab>

			<u:openTab decendentOf="%{getText('title.fleetmanagement.inventoryscrap')}"  id="fleet_inventory_scrap" tagType="li"
			     tabLabel="%{getText('title.fleetmanagement.inventoryscrap')}" url="select_inventories_fleetscrap.action"
			     catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Scrap_Inventories.htm">
			     <s:text name="title.fleetmanagement.inventoryscrap"></s:text>
			</u:openTab>

			<u:openTab decendentOf="%{getText('title.fleetmanagement.inventoryUnscrap')}"  id="fleet_inventory_unScrap" tagType="li"
			     tabLabel="%{getText('title.fleetmanagement.inventoryUnScrap')}" url="select_inventories_fleetUnScrap.action"
			     catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Scrap_Inventories.htm">
			     <s:text name="title.fleetmanagement.inventoryUnScrap"></s:text>
			</u:openTab>

        </authz:ifUserInRole>
        </authz:ifUserInRole>
        <authz:ifAdmin>
           	<u:openTab decendentOf="%{getText('title.fleetmanagement.managewarrantycoverage')}" id="fleet_coverage_management" tagType="li"
	        	tabLabel="%{getText('title.fleetmanagement.managewarrantycoverage')}" url="select_inventories_fleetcoverage.action"
	        	catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Manage_Warranty_Coverage.htm">
	        	<s:text name="title.fleetmanagement.managewarrantycoverage" />
	        </u:openTab>

	        <u:openTab decendentOf="%{getText('title.fleetmanagement.goodWillExtension')}" id="fleet_goodwill_extension" tagType="li"
	        	tabLabel="%{getText('title.fleetmanagement.goodWillExtension')}" url="select_plan_fleetGWextension.action"
	        	catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/GoodWill_Warranty_Extension.htm">
	        	<s:text name="title.fleetmanagement.goodWillExtension" />
			</u:openTab>

			<%--<u:openTab decendentOf="%{getText('summaryTable.inboxButton.purchase_warranty')}" id="extended_warranty_purchase" tagType="li"
	        	tabLabel="%{getText('summaryTable.inboxButton.purchase_warranty')}" url="display_searchinventories_PEW.action"
	        	catagory="inventory" cssClass="inventory_fleet folder foldableFleetInv sublist" helpCategory="Inventory/Extended_Warranty_Purchase.htm">
	        	<s:text name="summaryTable.inboxButton.purchase_warranty" />
			</u:openTab>--%>
		 </authz:ifAdmin>
	</s:if>
    </ol>
  </div>
  <%-- Club Car Starts here --%>
	    <t:daSection id="inventorySearchFolders"
	      fetchFrom="inventorySearchFolders.action"
		  fetchOn="/accordion/refreshsearchfolders"
			  title="%{getText('accordionLabel.viewClaim.searchFolders')}"
		      appendMode="false"
		  cssClass="searchFolders"/>
</div>
</div>
