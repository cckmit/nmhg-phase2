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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="u" uri="/ui-ext" %>

<html>
<u:actionResults/>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.BorderContainer");
    </script>
</head>
<u:body>
<s:form action="saveInventoryComment">
<s:hidden name="inventoryItem" />
<div dojoType="dijit.layout.ContentPane" region="center" id="content">
<div class="policy_section_div" style="width:100%">
<div class="section_header">
	<s:text name="Add Comment"/>
</div>
<table style="width:99%" border="0" cellspacing="0" cellpadding="0" class="grid ">
	<tr>
		<td width="20%" class="labelStyle">
			<s:text name="columnTitle.common.machineSerialNo" /> :
		</td>
		<td>
			<s:text name="inventoryItem.serialNumber" />
		</td>
	</tr>
	<tr>
		<td width="20%" class="labelStyle">
			<s:text name="label.userId" /> :
		</td>
		<td>
			<s:text name="getLoggedInUser().getName().toUpperCase()" />
		</td>
	</tr>
	<tr>
		<td width="20%" class="labelStyle">
			<s:text name="label.inventoryComment.dateOfComment" /> :
		</td>
		<td>
			<sd:datetimepicker name="inventoryComment.dateOfComment" id='commentDate' />
		</td>
	</tr>
	<tr>
		<td width="20%" class="labelStyle">
			<s:text name="label.inventoryComment.comment" /> :
		</td>
		<td>
			 <t:textarea name="inventoryComment.comment" id="condition_found" rows="4" cols="105" maxLength="3990"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<s:submit value="Save Comment" cssClass="buttonGeneric"/>
		</td>
	</tr>
</table>
</div>
</div>
</s:form>
</u:body>
</html>