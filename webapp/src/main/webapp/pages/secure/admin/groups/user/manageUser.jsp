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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<s:head theme="twms"/>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>

<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script type="text/javascript">

    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.form.ComboBox");
</script>
<script type="text/javascript" src="scripts/userGroupSearch.js"></script>
</head>
<u:body>
<s:form name="baseForm" id="baseForm">
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
        <table class="grid " cellspacing="0" cellpadding="0" style="margin-top:-10px;">
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="200" name="userGroup.name"/></td>
            </tr>
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="1000" name="userGroup.description"/></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td colspan="2">
                    <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_user"
                              onclick="selectAllOptions('includedUserNames',dojo.byId('baseForm'))"/>
                    <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addUsers"/>"
                           onclick="showDialog()"/>
                    <input type="button" id="closeButton1" class="buttonGeneric"
                           value="<s:text name='button.common.cancel'/>"/>
                </td>
            </tr>
        </table>
  
    <s:if test="%{userGroup.id == null}">
        
            <div class="mainTitle">
                <table class="grid">
                    <tr>
                        <td class="colHeader" width="40%">
                            <s:text name="label.manageGroup.consistingOfGroups"/>
                            <input type="radio" name="groupType"
                                   onclick="setGroupType('manage_user_group.action')">
                        </td>
                        <td class="colHeader">
                            <s:text name="label.manageGroup.consistingOfUsers"/>
                            <input type="radio" name="groupType" checked/>
                        </td>
                    </tr>
                </table>
            </div>
     
    </s:if>
    <div id="divForUsers">
                       
            <div class="mainTitle" style="margin:10px 0px 10px 0px;">
                <s:text name="label.manageGroup.manageIncludedUsers"/>
            </div>
       
                <s:if test="includedUsers != null && !includedUsers.isEmpty">
                    <div id="div_included_users"  align="center" width="100%">
                        <table width="100%" class="grid borderForTable" cellspacing="0"  cellpadding="0">
                            <tr class="row_head">
                                <th  width="5%" align="center"><input type="checkbox"
                                                                                  onclick="toggleOptions('includedUserNames', this.checked)"/>
                                </th>
                                <th><s:text name="label.common.name"/></th>
                            </tr>
                            <s:iterator value="includedUsers">
                                <tr>
                                    <td align="center">
                                        <input type="checkbox" name="includedUserNames"
                                               value="<s:property value="name"/>"/>
                                    </td>
                                    <td class="admin_selections"><s:property value="completeNameAndLogin"/></td>
                                </tr>
                            </s:iterator>
                            <tr>
                                <td colspan="2">
                                    <input type="button" class="buttonGeneric"
                                           value="<s:text name="button.manageGroup.removeUsers"/>"
                                           onclick="removeCheckedOptions('includedUserNames', 'div_included_users', 'div_no_included_users')"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <p id="div_no_included_users" align="center" style="display:none"><s:text
                            name="error.manageGroup.noUsersIncluded"/></p>
                </s:if>
                <s:else>
                    <p align="center"><s:text name="error.manageGroup.noUsersIncluded"/></p>
                </s:else>
                        <br/>
      <div style="margin-left:7px;">
        <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_user"
                  onclick="selectAllOptions('includedUserNames',dojo.byId('baseForm'))"/>
        <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addUsers"/>"
               onclick="showDialog()"/>
        <input type="button" id="closeButton2" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
    <div>
      <br/>
    </div>
</div>

<div id="dialogBoxContainer" style="display:none">
    <div dojoType="twms.widget.Dialog" id="searchUsersDialog" 
     title="<s:text name="label.common.searchUsers"/>"  style="width:90%;height:70%;">

            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="border-bottom:1px solid #EFEBF7">
                <table width="99%" class="admin_subsection_div">
                    <tr>
                        <td valign="bottom"><s:text name="label.manageGroup.userName"/> : <s:textfield id="p1"
                                                                                                       name="userName"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="userSearchButton"
                                   onclick="searchUsers('search_users_for_usergroup.action')">
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
                                       onclick="showSelectedUsers('add_users_to_usergroup.action')"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addUsers')}"/>"
                                       onclick="showSelectedUsers('add_users_to_usergroup.action')"/>
                            </s:else>
                        </td>
                        <td align="center">
                            <input type="button" name="Submit3" value="<s:text name="button.common.close"/>"
                                   class="buttonGeneric" onclick="closeDialog();"/>
                        </td>
                    </tr>
                </table>
            </div>

    </div>
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
</s:form>
</u:body>
</html>