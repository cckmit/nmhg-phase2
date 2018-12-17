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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title>:: <s:text name="title.common.warranty" /> ::</title>
<s:head theme="twms" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="claimForm.css" />
<u:stylePicker fileName="master.css" />
<u:stylePicker fileName="inventory.css" />
<style type="text/css">
.addRow {
	margin-top: -14px;
	height: 14px;
	text-align: right;
	padding-right: 17px;
}
</style>
</head>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
    </script>
	<div dojoType="dijit.layout.LayoutContainer" style="overflow-y: auto">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<s:form action="attributes_part_associate.action" theme="twms" validate="true" id="baseForm">
			<u:actionResults />
			<div class="policy_section_div">
				<div class="section_header">
					<s:text name="title.additionalAtrribute.attributeDetails" />
				</div>
				<jsp:include page="attributePreview.jsp"></jsp:include>
			</div>
			<s:hidden name="additionalAttributes"
				value="%{additionalAttributes.id}" />
			<div class="policy_section_div">
				<div class="section_header">
					<s:text name="title.additionalAtrribute.associateParts" />
				</div>
				<jsp:include page="partAssociation.jsp"></jsp:include>
			</div>
			<div align="center" style="margin-top: 10px;">
				<input type="button" value="<s:text name='label.cancel'/>"
					class="buttonGeneric" onclick="closeCurrentTab();" />
				<s:submit cssClass="buttonGeneric"
					value="%{getText('button.common.submit')}" />
			</div>
		</s:form>
	</div>
	</div>
<authz:ifPermitted resource="warrantyAdminAssociate/DissociatePartAttributesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
