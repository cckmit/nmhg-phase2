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

<u:stylePicker fileName="adminPayment.css"/>
<link href="stylesheets/ruleSearchWizard.css" rel="stylesheet" type="text/css"/>
<style type="text/css">
    html, body {
        background: #ffffff;
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 8pt;
        height: 100%;
        width: 100%;
        padding: 0;
        margin: 0;
        overflow: hidden;
    }
</style>
<script type="text/javascript" src="scripts/RuleSearch.js"></script>
<script type="text/javascript">
    <s:if test="!actionMessages.empty">
    dojo.addOnLoad(function() {
        manageTableRefresh("policyConfigTable");
    });
    </s:if>
</script>
<script type="text/javascript" xml:space="preserve">
    //start of changes as part of dojo migrations
       dojo.require("dijit.layout.LayoutContainer");
       dojo.require("dijit.layout.ContentPane");
       dojo.require("dijit.layout.TabContainer");
       dojo.require("twms.widget.Dialog");
       dojo.require("twms.widget.Select");
       //end of  changes as part of dojo migrations
</script>
<jsp:include flush="true" page="policy_details.jsp"/>
<jsp:include flush="true" page="applicability_terms.jsp"/>
<jsp:include flush="true" page="policy_registrationTerms.jsp" />