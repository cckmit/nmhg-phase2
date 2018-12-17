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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<u:stylePicker fileName="batterytestsheet.css" />
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dijit.layout.LayoutContainer");
</script>



<div dojoType="dojox.layout.ContentPane" layoutAlign="client">
<div style="margin: 5px;">
<table class="grid borderForTable" style="margin-left: 0px; width: 97%;"
	cellspacing="0" cellpadding="0">
	<thead>
		<tr class="row_head">
			<th width="20%" class="colHeader"><s:text
				name="title.required.alarmCode" /></th>
			<th width="80%" class="colHeader"><s:text
				name="columnTitle.common.description" /></th>
		</tr>
	</thead>
	<s:iterator value="claim.alarmCodes">
		<tr>
			<td class="labelStyle" nowrap="nowrap" width="20%">
			   <s:property value="code" />
			</td>
			<td>
			   <s:property value="description" />
		   </td>
		</tr>
	</s:iterator>

</table>
</div>
</div>
