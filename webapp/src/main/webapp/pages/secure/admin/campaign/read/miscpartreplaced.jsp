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
<u:stylePicker fileName="batterytestsheet.css"/>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("dijit.layout.LayoutContainer");
</script>


<div style="background:#F3FBFE ">
<div dojoType="dojox.layout.ContentPane" layoutAlign="client" >
<div class="mainTitle" style="padding-bottom:5px;margin-top:5px;">
                <s:text name="accordionLabel.miscellaneousParts"/>
            </div>
	<table class="grid borderForTable"  style="margin-left:0px; width:97%;">
		<thead>
			<tr class="row_head">
				<th width="20%"><s:text name="columnTitle.miscellaneousParts.partNumber"/></th>
				<th width="30%"><s:text name="columnTitle.miscellaneousParts.partDescription"/></th>
				<th width="10%"><s:text name="label.common.quantity" /></th>
				<th width="20%"><s:text name="label.newClaim.unitPrice" /></th>
				<th width="20%"><s:text name="columnTitle.miscellaneousParts.uom"/></th>
			</tr>
		</thead>
		<s:iterator
			value="miscPartsToReplace">
			<tr>
				<td><s:property
					value="miscItemConfig.miscellaneousItem.partNumber" /></td>
				<td><s:property
					value="miscItemConfig.miscellaneousItem.description" /></td>
				<td><s:property value="noOfUnits" /></td>
				<td><s:property value="miscItemConfig.miscellaneousItem.getMiscItemRateForCurrency('USD').rate" /></td>
				<td><s:property value="miscItemConfig.uom.type" /></td>
			</tr>
		</s:iterator>

	</table>
</div>
</div>
