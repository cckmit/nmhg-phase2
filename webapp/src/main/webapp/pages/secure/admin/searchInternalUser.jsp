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
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>

<html>
<head>
    <title>
        <s:text name="accordion_jsp.accordionPane.dealerUserMgmt"/>
    </title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <script type="text/javascript">
	    dojo.require("dijit.Tooltip")
	</script>
	<style type="text/css">
		.dijitTooltipContainer {
		position:absolute;left:0;top:0;
		background-color:#CCD9FF;
		border:1px solid gray;
		font-family:Arial, Helvetica, sans-serif;
		font-size: 9pt;
		display: block;
		}
	</style>
</head><script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
       
    </script>
    <u:actionResults/>
<u:body>
           
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-y:auto;" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">

            <s:form action="search_internal_user" id="searchUserForm" theme="twms">
                <div class="section_div">
                    <div class="section_heading">
                        <s:text name="Search User"/>
                    </div>
                </div>
                <table class="form" cellpadding="0" cellspacing="18">
                    <tr style="width:75%;">
                        <td style="width:20%;"><s:text name="label.dealerUser.login"/></td>
                        <td><s:textfield maxlength="20" size="30" name="loginName"/></td>
                    </tr>
                    <tr style="width:75%;">
                        <td  rowspan="2"><input id="submit_btn" class="buttonGeneric" type="submit" value="<s:text name='Search User'/>"/></td>
                       
                    </tr>
                </table>
               
            </s:form>
        </div>
    </div>
</u:body>
<authz:ifPermitted resource="settingsUpdateInternalUserReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('searchUserForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('searchUserForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>