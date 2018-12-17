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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" />  ::</title>
    <s:head theme="twms"/>
<script type="text/javascript">
      
    </script>
	
	<u:stylePicker fileName="common.css"/>
	<u:stylePicker fileName="form.css"/>
	<u:stylePicker fileName="claimForm.css"/>
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
dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.Dialog");

function closeCurrentTab() {
    closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
}
</script>
<u:body>
<s:form action="attributes_inventory_associate.action" id="attrForm" theme="twms" validate="true">
<!--<div dojoType="dijit.layout.LayoutContainer" id ="test" layoutAlign="client"
	style="overflow-X:hidden; overflow-Y: auto; width:100%; height: 100%">-->
<u:actionResults/>

<div class="policy_section_div">
<div class="section_header"><s:text name="title.additionalAtrribute.attributeDetails"/></div>
<jsp:include page="attributePreview.jsp"></jsp:include>
</div>
<div class="policy_section_div">
<div class="section_header">
<s:text name="title.manageAdditionalAttributes.associateAttributes"/></div>

<jsp:include flush="true" page="claimedItemAssociation.jsp"></jsp:include>
<s:hidden name="additionalAttributes" value="%{additionalAttributes.id}"/>
</div>
<div align="center" style="margin-top:10px;">
 <input type="button"
    value="<s:text name='label.cancel' />" class="buttonGeneric" onclick="closeCurrentTab();" />
    <s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}"> </s:submit>
   
</div>
<!--</div>-->

</s:form>
</u:body>

