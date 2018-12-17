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

<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div dojoType="dijit.layout.ContentPane" id="partInventoryAdmin"
        title="<s:text name="accordionLabel.partInventoryAdmin"/>"
        iconClass="warrantyAdmin" style="width: 100%;">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
        <ol>
        <!-- Manage Groups Accordion  -->
            <u:fold label="%{getText('accordionLabel.manageGroup')}"
                id="manage_groups" tagType="li"
                cssClass="uFoldStyle folder" foldableClass="foldableGroups" />

        <!-- Manage Dealer Groups -->

            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="manage_dealer_group_pia" tagType="li"
                cssClass="warrantyAdmin_folder folder foldableGroups sublist"
                tabLabel="%{getText('title.manageGroup.dealerGroups')}"
                url="show_dealer_schemes.action" catagory="partInventoryAdmin">
                <s:text name="accordionLabel.manageGroup.dealerGroups" />
            </u:openTab>
        </ol>
    </div>
</div>