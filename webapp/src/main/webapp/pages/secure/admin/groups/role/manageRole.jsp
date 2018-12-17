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
<s:head theme="twms"/>
<u:stylePicker fileName="adminPayment.css"/>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script type="text/javascript">

    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.form.ComboBox");
</script>
<script type="text/javascript" src="scripts/roleGroupSearch.js"></script>
<u:body smudgeAlert="false">
<form name="baseForm" id="baseForm">
<u:actionResults/>

<s:hidden name="id" value="%{roleScheme.id}" id="schemeId"/>
<s:hidden name="roleGroup.id" id="groupId"/>
<div class="admin_section_div">
    <div class="admin_section_heading">
        <s:text name="%{sectionTitle}"/>
    </div>
    <div class="admin_subsection_div">
        <div class="admin_section_subheading">
            <s:text name="label.manageGroup.roleGroupDetails"/>
        </div>
        <br/>
        <table>
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="200" name="roleGroup.name"/></td>
            </tr>
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="1000" name="roleGroup.description"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_role"
                              onclick="selectAllOptions('includedRoleNames')"/>
                    <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addRoles"/>"
                           onclick="showDialog()"/>
                    <input type="button" id="closeButton1" class="buttonGeneric"
                           value="<s:text name='button.common.cancel'/>"/>
                </td>
            </tr>
        </table>
    </div>
    <s:if test="%{roleGroup.id == null}">
        <div class="admin_subsection_div">
            <div class="admin_section_subheading">
                <table>
                    <tr>
                        <td class="admin_section_subheading" width="40%">
                            <s:text name="label.manageGroup.consistingOfGroups"/>
                            <input type="radio" name="groupType"
                                   onclick="setGroupType('<s:url action="manage_role_group" />')">
                        </td>
                        <td class="admin_section_subheading">
                            <s:text name="label.manageGroup.consistingOfRoles"/>
                            <input type="radio" name="groupType" checked/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </s:if>
    <div id="divForRoles">
        <div class="admin_subsection_div">
            <div class="admin_section_heading">
                <s:text name="label.manageGroup.manageIncludedRoles"/>
            </div>
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <s:text name="label.manageGroup.memberRoles"/>
                </div>
                <s:if test="includedRoles != null && !includedRoles.isEmpty">
                    <div id="div_included_roles" class="admin_section_div" align="left" width="100%">
                        <table width="100%">
                            <tr>
                                <th class="admin_table_heading" width="5%" align="center"><input type="checkbox"
                                                                                  onclick="toggleOptions('includedRoleNames', this.checked)"/>
                                </th>
                                <th class="admin_table_heading"><s:text name="label.common.name"/></th>
                            </tr>
                            <s:iterator value="includedRoles">
                                <tr>
                                    <td align="center">
                                        <input type="checkbox" name="includedRoleNames"
                                               value="<s:property value="name"/>"/>
                                    </td>
                                    <td class="admin_selections"><s:property value="name"/></td>
                                </tr>
                            </s:iterator>
                            <tr>
                                <td colspan="2">
                                    <input type="button" class="buttonGeneric"
                                           value="<s:text name="button.manageGroup.removeRoles"/>"
                                           onclick="removeCheckedOptions('includedRoleNames', 'div_included_roles', 'div_no_included_roles')"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <p id="div_no_included_roles" align="center" style="display:none"><s:text
                            name="error.manageGroup.noRolesIncluded"/></p>
                </s:if>
                <s:else>
                    <p align="center"><s:text name="error.manageGroup.noRolesIncluded"/></p>
                </s:else>
            </div>
        </div>
        <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_role"
                  onclick="selectAllOptions('includedRoleNames')"/>
        <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addRoles"/>"
               onclick="showDialog()"/>
        <input type="button" id="closeButton2" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
    </div>
</div>

<div id="dialogBoxContainer" style="display:none">
    <div dojoType="twms.widget.Dialog" id="searchRolesDialog" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
         toggleDuration="250">
        <div dojoType="dijit.layout.LayoutContainer"
             style="width:500px; height:400px; background: #F3FBFE; border: 1px solid #EFEBF7;overflow:auto">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="border-bottom:1px solid #EFEBF7">
                <table width="99%" class="admin_subsection_div">
                    <tr>
                        <td valign="bottom"><s:text name="label.manageGroup.roleName"/> : <s:textfield id="p1"
                                                                                                       name="roleName"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="roleSearchButton"
                                   onclick="searchRoles('<s:url action="search_roles_for_rolegroup" includeParams="none" />')">
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
                            <s:if test="%{roleGroup.id}">
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.common.update')}"/>"
                                       onclick="showSelectedRoles('<s:url action="add_roles_to_rolegroup" />')"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addRoles')}"/>"
                                       onclick="showSelectedRoles('<s:url action="add_roles_to_rolegroup" />')"/>
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
</div>

<div id="addedRolesHere" style="display:none"></div>
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