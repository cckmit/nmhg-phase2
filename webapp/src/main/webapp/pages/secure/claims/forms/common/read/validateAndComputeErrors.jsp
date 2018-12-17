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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:actionResults wipeMessages="false"/>
<div dojoType="dijit.layout.ContentPane">
<table align="center" class="buttons">
    <tr>
        <td align="center">
            <s:submit id="validations_hider2" value="Edit Claim" type="button"/>
            <script type="text/javascript">
            dojo.addOnLoad(function() {
                dojo.connect(dojo.byId("validations_hider2"), "onclick", function() {
                    dijit.byId("validations").hide();
                });
            });
            </script>
       </td>
    </tr>
</table>
</div>