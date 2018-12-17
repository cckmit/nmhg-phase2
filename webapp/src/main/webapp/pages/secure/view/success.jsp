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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<%@ include file="/i18N_javascript_vars.jsp" %>
<s:head theme="twms"/>

<script type="text/javascript">
	dojo.require("dijit.layout.LayoutContainer");
    function closeTabAfterFinishing() {
        var tabDetails = getTabDetailsForIframe();
        var tab = getTabHavingId(tabDetails.tabId);

        parent.publishEvent("/tab/close", {tab:tab});
    }
    
    dojo.addOnLoad(function(){
    	var parent=document.getElementById("parentFrameId").value;
    	var inboxViewName=document.getElementById("inboxView_name").value;
    	var inboxViewId=document.getElementById("inboxView_id").value;
    	window.top.frames[parent].addInboxViewOption(inboxViewId,inboxViewName);
        closeTabAfterFinishing();
    });
</script>
<form>
	<s:hidden name="parentFrameId"/>
	<s:hidden name="inboxView.name"/>
	<s:hidden name="inboxView.id"/>
</form>
