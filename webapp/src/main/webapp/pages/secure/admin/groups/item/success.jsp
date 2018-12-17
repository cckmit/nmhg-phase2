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
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>

<div class="admin_section_div">
    <u:actionResults/>

    <s:if test="itemGroup.includedItems.size != 0">
        <div class="admin_subsection_div">
            <div class="admin_section_subheading">
                <s:text name="label.manageGroup.memberItems"/>
            </div>
            <s:iterator value="itemGroup.includedItems">
                <div class="admin_selections"><s:property value="number"/></div>
            </s:iterator>
        </div>
    </s:if>
    <s:else>
        <div class="admin_subsection_div">
            <div class="admin_section_subheading">
                <s:text name="label.manageGroup.memberGroups"/>
            </div>
            <s:iterator value="itemGroup.consistsOf">
                <div class="admin_selections"><s:property value="name"/></div>
            </s:iterator>
        </div>
    </s:else>
</div>
