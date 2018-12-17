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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<title><s:text name="title.common.warranty" /></title>
    <s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css"/>
 <u:stylePicker fileName="base.css"/>
     <u:stylePicker fileName="common.css"/>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script type="text/javascript">
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.form.ComboBox");
</script>
<script type="text/javascript" src="scripts/userGroupSearch.js"></script>
</head>
<u:body>
<form name="baseForm" id="baseForm">
<u:actionResults/>
<s:if test="userScheme.id!=null">
 	<s:hidden name="userSchemeId" value="%{userScheme.id}" id="schemeId"/>
</s:if>
<s:else>
    <s:hidden name="id" value="%{userScheme.id}" id="schemeId"/>
</s:else>
<s:hidden name="userGroup.id" id="groupId"/>
<div class="admin_section_div" style="margin:5px;">
<div class="admin_section_heading">
    <s:text name="%{sectionTitle}"/>
</div>
 
    <div class="mainTitle" style="margin-top:5px;">
        <s:text name="label.manageGroup.userGroupDetails"/>
    </div>
<div class="borderTable">&nbsp;</div>
    <table class="grid" cellspacing="0" cellpadding="0">
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/></td>
            <td class="admin_selections"><s:textfield size="50" maxlength="200" name="userGroup.name"/></td>
        </tr>
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
            <td class="admin_selections"><s:textfield size="50" maxlength="1000" name="userGroup.description"/></td>
        </tr>
        <tr><td style="padding:0">&nbsp;</td></tr>
        <tr>
            <td colspan="2">
                <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_usergroup"
                          onclick="selectAllOptions('includedGroupNames')"/>
                <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addUserGroups"/>"
                       onclick="showDialog()"/>
                <input type="button" id="closeButton2" class="buttonGeneric"
                       value="<s:text name='button.common.cancel'/>"/>
            </td>
        </tr>
    </table>

<s:if test="%{userGroup.id == null}">

    <div class="mainTitle">
        <table  class="grid" style="width:50%" cellspacing="0" cellpadding="0">
            <tr>
                <td class="warColHeader" width="40%">
                    <s:text name="label.manageGroup.consistingOfGroups"/>
                    <input type="radio" name="groupType" checked/>
                </td>
                <td class="warColHeader">
                    <s:text name="label.manageGroup.consistingOfUsers"/>
                    <input type="radio" name="groupType" onclick="setGroupType('<s:url action="manage_user" />')">
                </td>
            </tr>
        </table>
    </div>

</s:if>
<div id="divForGroups">
       <div class="policy_section_div">
        <div class="section_header">
            <s:text name="label.manageGroup.manageIncludedGroups"/>
        </div>
         
            <div class="mainTitle" style="margin-top:5px;">
                <s:text name="label.manageGroup.memberGroups"/>
            </div>
              
            <div class="admin_selections">
                <s:if test="includedGroups != null && !includedGroups.isEmpty">
                    <div id="div_included_groups" align="center" width="100%">
                        <table width="100%" class="grid borderForTable" cellspacing="0" cellpadding="0">
                            <tr>
                                <th width="5%" class="warColHeader" align="center"><input type="checkbox"
                                                                                  onclick="toggleOptions('includedGroupNames', this.checked)"/>
                                </th>
                                <th class="warColHeader"><s:text name="label.manageGroup.groupName"/></th>
                                <th class="warColHeader"><s:text name="label.common.description"/></th>
                            </tr>
                            <s:iterator value="includedGroups">
                                <tr>
                                    <td align="center">
                                        <input type="checkbox" name="includedGroupNames"
                                               value="<s:property value="name"/>"/>
                                    </td>
                                    <td ><s:property value="name"/></td>
                                    <td ><s:property value="description"/></td>
                                </tr>
                            </s:iterator>
                            <tr>
                                <td colspan="3">
                                    <input type="button" class="buttonGeneric"
                                           value="<s:text name="button.manageGroup.removeUserGroups"/>"
                                           onclick="removeCheckedOptions('includedGroupNames', 'div_included_groups', 'div_no_included_groups')"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <p id="div_no_included_groups" align="center" style="display:none"><s:text
                            name="error.manageGroup.noGroupsIncluded"/></p>
                </s:if>
                <s:else>
                    <p align="center"><s:text name="error.manageGroup.noGroupsIncluded"/></p>
                </s:else>
            </div>
            </div>
    <div class="spacingAtTop" align="center">
        <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_usergroup"
                  onclick="selectAllOptions('includedGroupNames')"/>
        <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addUserGroups"/>"
               onclick="showDialog()"/>
        <input type="button" id="closeButton1" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
    </div>
      <br/>
</div>
    <div dojoType="twms.widget.Dialog" id="searchUsersDialog" 
     title="<s:text name="label.common.searchUsers"/>"  style="width:90%;height:70%;">
<!--         <div dojoType="dijit.layout.LayoutContainer" style="overflow:auto;width:100%;height:69%;">-->
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="border-bottom:1px solid #EFEBF7">
                <table width="99%" class="admin_subsection_div">
                    <tr>
                        <td valign="bottom"><s:text name="label.manageGroup.groupName"/> : <s:textfield id="p1"
                                                                                                        name="groupName"/>&nbsp;</td>
                        <td valign="bottom"><s:text name="label.common.description"/> : <s:textfield id="p2"
                                                                                                     name="groupDescription"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="userSearchButton"
                                   onclick="searchUserGroups('<s:url action="search_groups_for_usergroup" includeParams="none" />')">
                        </td>
                    </tr>
                </table>
            </div>
            <div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="searchResults">
            </div>
            <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="border-top:1px solid #EFEBF7">
                <table width="100%" class="buttonWrapperPrimary">
                    <tr>
                        <td align="center">
                            <s:if test="%{userGroup.id}">
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.common.update')}"/>"
                                       onclick="showSelectedUserGroups('<s:url action="add_groups_to_usergroup" />')"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addGroups')}"/>"
                                       onclick="showSelectedUserGroups('<s:url action="add_groups_to_usergroup" />')"/>
                            </s:else>
                        </td>
                        <td align="center">
                            <input type="button" name="Submit3" value="<s:text name="button.common.close"/>"
                                   class="buttonGeneric" onclick="closeDialog();"/>
                        </td>
                    </tr>
                </table>
            </div>        
<!--    </div>-->
    </div>
<div id="addedUsersHere" style="display:none"></div>
<script type="text/javascript">

    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId('closeButton1'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        });

        dojo.connect(dojo.byId('closeButton2'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        });
    });    
</script>
</form>
</u:body>
</html>