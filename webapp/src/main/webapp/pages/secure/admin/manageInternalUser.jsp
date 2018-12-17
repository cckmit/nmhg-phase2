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
 <s:head theme="twms"/>
 <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");   
        dojo.require("dijit.layout.ContentPane");
 </script>
</head>
<u:body>
<u:actionResults />
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-y:auto;" id="root">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form action="create_internal_user" id="createUserForm" theme="twms">
<jsp:include page="createOrUpdateInternalUser.jsp"></jsp:include>
<table class="form">
    <tr>
        <td class="non_editable"><s:text name="label.dealerUser.login"/></td>
        <td colspan="2"><s:textfield maxlength="20" size="30" name="user.name"/></td>
        <td><s:text name="label.common.internalUserCreationWarningMessage"/></td>
    </tr>
     <tr>
        <td class="non_editable"><s:text name="label.dealerUser.password"/></td>
        <td colspan="3"><s:password maxlength="20" size="30" name="userPassword" /></td>
    </tr>
    <tr>
        <td class="non_editable"><s:text name="label.dealerUser.confirmPassword"/></td>
        <td colspan="3"><s:password maxlength="20" size="30" name="confirmPassword" /></td>
    </tr>
</table><br></br>
<div id="submit" align="center">
    <s:if test="%{!userCreated}" >
        <input id="submit_btn" class="buttonGeneric" type="submit" value="<s:text name='label.common.addUser'/>" tabindex="14" onclick="selectAllOptions(this.form['list2'])"/>
    </s:if>
    <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
           onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
</div>


</s:form>
</div>
</div>
</u:body>
<authz:ifPermitted resource="settingsCreateInternalUserReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('createUserForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('createUserForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>