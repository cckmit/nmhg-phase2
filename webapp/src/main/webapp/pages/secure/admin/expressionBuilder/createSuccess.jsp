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
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
<script type="text/javascript"
        src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>

<script type="text/javascript">

    function closeTabAfterFinishing() {
        var tabDetails = getTabDetailsForIframe();
        var tab = getTabHavingId(tabDetails.tabId);

        parent.publishEvent("/tab/close", {tab:tab});
    }

    dojo.addOnLoad(function () {
        manageTableRefresh("expressionTable");
    });

</script>

<form>

    <table width="100%" border="0" cellspacing="0" cellpadding="0"
           class="successTablebg" align="center">
        <tr>
            <td valign="top">
                <table width="100%" border="0" cellspacing="3" cellpadding="3"
                       class="successbg">
                    <tr>
                        <td width="4%" class="successTextbold">
                            <div align="center"><img
                                    src="image/applicationSuccessIcon.gif"
                                    width="16" height="16"/></div>
                        </td>
                        <td width="12%" class="successTextbold"><s:text name="message.common.success"/></td>
                        <td class="successTextnormal"><s:actionmessage/></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>

    <s:hidden name="context"/>
    <s:submit action="new_expression" cssClass="buttonGeneric"
              value="%{getText('button.manageBusinessCondition.addCondition')}"/>
    <input type="button" value="<s:text name="button.common.close"/>" class="buttonGeneric"
           onclick="closeTabAfterFinishing()"/>
</form>