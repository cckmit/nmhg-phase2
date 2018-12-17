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
<%--
  @author mritunjay.kumar
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<s:head theme="twms" />
<script type="text/javascript">
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.TabContainer");
	dojo.require("dijit.layout.ContentPane");
</script>
<style type="text/css">
pre {
	border-width: 0px 0;
	padding: 0em;
}
</style>
<u:stylePicker fileName="master.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="inventory.css" />
<u:stylePicker fileName="common.css" />

</head>

<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: none;">

	
			
		<div id="model_detail" class="policy_section_div" style="width: 100%; height:100%; overflow-x:auto">
			<div id="model_detail_title" class="section_header">
				<s:property value="%{getText('label.model')}" />
			</div>
			<table width="96%" cellpadding="0" cellspacing="0" class="grid">
				<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.model" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.name" /></pre>
					</td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.common.product" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.isPartOf.name" /></pre>
				</tr>

				<tr>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.item.modelCode" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.groupCode" /></pre>
					</td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.item.productCode" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.isPartOf.groupCode" /></pre>
				</tr>
				<tr>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.item.modelDescription" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.description" /></pre>
					</td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
							name="label.item.productDescription" /> :
					</td>
					<td><pre style="background-color: #F3FBFE"><s:property value="model.isPartOf.description" /></pre>
				</tr>

			</table>
		</div>
	</div>


</u:body>
</html>
