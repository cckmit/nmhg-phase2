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
<script type="text/javascript" src="scripts/groupSearch.js"></script>
</head>
<u:body>
<s:form name="baseForm" id="baseForm">
<u:actionResults/>
<s:if test="itemScheme.id!=null">
    <s:hidden name="itemSchemeId" value="%{itemScheme.id}" id="schemeId"/>
</s:if>
<s:else>
    <s:hidden name="id" value="%{itemScheme.id}" id="schemeId"/>
</s:else>
<s:hidden name="itemGroup.id" id="groupId"/>
<div class="admin_section_div" style="margin:5px;">
    <div class="admin_section_heading">
        <s:text name="%{sectionTitle}"/>
    </div>

    <div class="mainTitle" style="margin-top:5px;">
        <s:text name="label.manageGroup.itemGroupDetails"/>
    </div>

    <div class="boderTable">&nbsp;</div>

    <table class="grid" cellspacing="0" cellpadding="0" style="margin-top:-10px;">
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/>:</td>
            <td class="admin_selections"><s:textfield name="itemGroup.name" size="50" maxlength="200"/></td>
        </tr>
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
            <td class="admin_selections"><s:textfield name="itemGroup.description" size="50" maxlength="1000"/></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td colspan="2">
                <s:if test="!itemScheme.purposeProductStructure">
                    <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_group"
                              onclick="selectAllOptions('includedGroupNames',dojo.byId('baseForm'))"/>
                    <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addGroups"/>"
                           onclick="showDialog()"/>
                </s:if>
                <input type="button" id="closeButton1" class="buttonGeneric"
                       value="<s:text name='button.common.cancel'/>"/>
            </td>
        </tr>
    </table>

<s:if test="%{itemGroup.id == null}">
<br/>
    <div class="mainTitle">
        <table class="grid " style="width:50%" cellspacing="0" cellpadding="0">
            <tr>
                <td class="warColHeader" width="40%">
                    <s:text name="label.manageGroup.consistingOfGroups"/>
                    <input type="radio" name="groupType" checked/>
                </td>
                <td class="warColHeader">
                    <s:text name="label.manageGroup.consistingOfItems"/>
                    <input type="radio" name="groupType" onclick="setGroupType('manage_item.action')">
                </td>
            </tr>
        </table>
    </div>

</s:if>
<div id="divForGroups">
    <br/>
        <div class="section_header">
            <s:text name="label.manageGroup.manageMemberGroups"/>
        </div>

            <div class="admin_selections">
                <s:if test="includedGroups != null && !includedGroups.isEmpty">
                    <div id="div_included_groups"  align="center" width="100%">
                        <table width="100%" class="grid borderForTable" cellspacing="0" cellpadding="0">
                            <tr>
                                <th width="5%" class="warColHeader" align="center">
                                    <input type="checkbox" onclick="toggleOptions('includedGroupNames', this.checked)"/>
                                </th>
                                <th width="25%" class="warColHeader"><s:text name="label.manageGroup.groupName"/></th>
                                <th width="70%" class="warColHeader"><s:text name="label.common.description"/></th>
                            </tr>
                            <s:iterator value="includedGroups">
                                <tr>
                                    <td align="center">
                                        <input type="checkbox" name="includedGroupNames"
                                               value="<s:property value="name"/>"/>
                                    </td>
                                    <td ><s:property value="name"/></td>
                                    <td><s:property value="description"/></td>
                                </tr>
                            </s:iterator>
                            <s:if test="!itemScheme.purposeProductStructure">
                                <tr>
                                    <td colspan="3">
                                        <input type="button" class="buttonGeneric"
                                               value="<s:text name="button.manageGroup.removeGroups"/>"
                                               onclick="removeCheckedOptions('includedGroupNames', 'div_included_groups', 'div_no_included_groups')"/>
                                    </td>
                                </tr>
                            </s:if>
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
        <s:if test="!itemScheme.purposeProductStructure">
            <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_group"
                      onclick="selectAllOptions('includedGroupNames',dojo.byId('baseForm'))"/>
            <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addGroups"/>"
                   onclick="showDialog()"/>
        </s:if>  
        <input type="button" id="closeButton2" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
   </div>
     <br/>
    </div>

<div id="dialogBoxContainer" style="display:none">
    <div dojoType="twms.widget.Dialog" id="searchItemsDialog" title="<s:text name="label.common.searchItems"/>"
     style="width:90%;height:70%;">
         
            <div dojoType="dijit.layout.ContentPane" region="top" style="border-bottom:1px solid #EFEBF7;">
                <table width="99%" class="admin_subsection_div">
                    <tr>
                        <td valign="bottom"><s:text name="label.manageGroup.groupName"/> : <s:textfield id="p1"
                                                                                                        name="groupName"/>&nbsp;</td>
                        <td valign="bottom"><s:text name="label.common.description"/> : <s:textfield id="p2"
                                                                                                     name="groupDescription"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="itemSearchButton"
                                   onclick="searchGroups('search_groups_for_itemgroup.action')">
                        </td>
                    </tr>
                </table>
            </div>
            <div dojoType="dijit.layout.ContentPane" region="center" id="searchResults">
            </div>
            <div dojoType="dijit.layout.ContentPane" region="bottom" style="border-top:1px solid #EFEBF7">
                <table width="100%" class="buttonWrapperPrimary">
                    <tr>
                        <td align="center">
                            <s:if test="%{itemGroup.id}">
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.common.update')}"/>"
                                       onclick="showSelectedGroups('add_groups_to_group.action')"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addGroups')}"/>"
                                       onclick="showSelectedGroups('add_groups_to_group.action')"/>
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

<div id="addedItemsHere" style="display:none"></div>
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
