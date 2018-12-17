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
<%@taglib prefix="s" uri="/struts-tags"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<s:if test="showCancelButton"> 
    <button class="button" id="formCancelButton">
        <s:text name="button.common.cancel"/>
    </button>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            if (TWMS_UTILITY_JS) {
                dojo.connect(dojo.byId("formCancelButton"), "onclick", function() {
                    closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
                });
            }
        });
    </script>
</s:if>