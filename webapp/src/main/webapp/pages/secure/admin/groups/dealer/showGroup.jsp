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
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<u:stylePicker fileName="adminPayment.css"/>

<form name="baseForm" id="baseForm">
    <s:hidden id="groupId" name="id" value="%{dealerGroup.id}"/>
    <s:hidden name="dealerGroupConfigParam"/>
    <div class="admin_section_div">
        <div class="admin_section_heading">
            <s:text name="label.manageGroup.dealerGroupDetails"/>
        </div>
        <table>
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/>:</td>
            <td class="admin_selections"><s:textfield size="50" maxlength="200" name="dealerGroup.name"/></td>
        </tr>
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.common.description"/>:</td>
            <td class="admin_selections"><s:textfield size="50" maxlength="1000"
                                                          name="dealerGroup.description"/></td>
        </tr>
        
        <tr>
            <td class="admin_selections labelStyle"><s:text name="label.common.code"/>:</td>
            <td class="admin_selections"><s:textfield size="50" maxlength="200"
                                                          name="dealerGroup.code"/></td>
        </tr>
        
        </table>
        <s:if test="!dealerGroup.includedDealers.isEmpty">
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <s:text name="label.manageGroup.memberDealers"/>
                </div>
                <table width="100%">
                    <tr>
                        <th class="admin_table_heading"><s:text name="label.common.dealer"/></th>
                    </tr>
                    <s:iterator value="dealerGroup.includedDealers">
                        <tr>
                            <td class="admin_selections"><s:property value="name"/></td>
                        </tr>
                    </s:iterator>
                </table>
            </div>
        </s:if>
        <s:else>
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <s:text name="label.manageGroup.includedGroups"/>
                </div>
                <table width="100%">
                    <tr>
                        <th class="admin_table_heading"><s:text name="label.manageGroup.groupName"/></th>
                        <th class="admin_table_heading"><s:text name="label.common.description"/></th>
                        
                        <th class="admin_table_heading"><s:text name="label.common.code"/></th>
                        
                    </tr>
                    <s:iterator value="dealerGroup.consistsOf">
                        <tr>
                            <td class="admin_selections"><s:property value="name"/></td>
                            <td class="admin_selections"><s:property value="description"/></td>
                            
                             <td class="admin_selections"><s:property value="code"/></td>
                            
                        </tr>
                    </s:iterator>
                </table>
            </div>
        </s:else>
        <input type="button" class="buttonGeneric"
               value="<s:property value="%{getText('button.manageGroup.updateDealerGroup')}"/>"
               onclick="sendThisRequest()"/>
    </div>
</form>
<script type="text/javascript">
    function sendThisRequest() {
        var url = "<s:url action="view_dealer_group.action" includeParams="none" />?dealerGroup.id=<s:property value="dealerGroup.id" />";
        var tabLabel = i18N.name + "<s:property value="dealerGroup.name" />"
        var decendent_of = getMyTabLabel() ;
        top.publishEvent("/tab/open", {label: tabLabel,
            url: url,
            decendentOf: decendent_of});
    }
</script>