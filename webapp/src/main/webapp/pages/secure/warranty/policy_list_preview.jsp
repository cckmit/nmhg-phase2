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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<s:if test="draftPolicies.empty">
    <s:text name="error.transferPlan.noPlan" />
</s:if>
<s:else>
    <table width="98%" cellpadding="0" cellspacing="0" id="policies"
       class="grid borderForTable">
        <thead>
            <tr>
                <th class="warColHeader" align="left"><s:text name="label.planName"/></th>
            </tr>
        </thead>
        <tbody id="policy_list">        
            <s:iterator value="draftPolicies" status="draftPolicies">
            <tr>
                <td><s:property value="policyDefinition.code"/></td>               
            </tr>
            </s:iterator>
        </tbody>
    </table>
</s:else>
