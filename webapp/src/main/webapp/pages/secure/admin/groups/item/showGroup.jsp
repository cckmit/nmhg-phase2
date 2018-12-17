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
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<u:stylePicker fileName="adminPayment.css"/>

<form name="baseForm" id="baseForm">
    <s:hidden id="groupId" name="id" value="%{itemGroup.id}"/>
    <div class="admin_section_div">
        <div class="admin_section_heading">
            <s:text name="label.manageGroup.itemGroupDetails"/>
        </div>
		<div style="padding-left:5px;">
        <s:text name="label.manageGroup.groupName"/> - <s:property value="itemGroup.name"/>
		</div>
        <div style="padding-left:5px;">
        <s:text name="label.common.description"/> - <s:property value="itemGroup.description"/>
       </div>
        <s:if test="!itemGroup.includedItems.isEmpty">
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <s:text name="label.manageGroup.memberItems"/>
                </div>
                <table width="100%">
                    <tr>
                        <th class="admin_table_heading"><s:text name="label.common.itemNumber"/></th>
                        <th class="admin_table_heading"><s:text name="label.common.description"/></th>
                    </tr>
                    <s:iterator value="itemGroup.includedItems">
                        <tr>
                            <td class="admin_selections"><s:property value="number"/></td>
                            <td class="admin_selections"><s:property value="description"/></td>
                        </tr>
                    </s:iterator>
                </table>
            </div>
        </s:if>
        <s:else>
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <s:text name="label.manageGroup.memberGroups"/>
                </div>
                <table width="100%">
                    <tr>
                        <th class="admin_table_heading"><s:text name="label.manageGroup.groupName"/></th>
                        <th class="admin_table_heading"><s:text name="label.common.description"/></th>
                    </tr>
                    <s:iterator value="itemGroup.consistsOf">
                        <tr>
                            <td class="admin_selections"><s:property value="name"/></td>
                            <td class="admin_selections"><s:property value="description"/></td>
                        </tr>
                    </s:iterator>
                </table>
            </div>
        </s:else>
        <input type="button" class="buttonGeneric"
               value="<s:property value="%{getText('button.manageGroup.updateItemGroup')}"/>"
               onclick="sendThisRequest()"/>
    </div>
</form>
<script type="text/javascript">
    function sendThisRequest() {
        var url = "<s:url action="view_item_group.action" includeParams="none"><s:param name="itemGroup.id" value="itemGroup.id" /></s:url>";
        var tab_Label = i18N.group_name + "<s:property value="itemGroup.name" />"
        var decendent_of = getMyTabLabel();
        top.publishEvent("/tab/open", {label: tab_Label,
            url: url,
            decendentOf: decendent_of});
    }
</script>