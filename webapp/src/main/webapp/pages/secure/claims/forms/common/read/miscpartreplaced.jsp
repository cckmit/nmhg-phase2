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



<div dojoType="dojox.layout.ContentPane" layoutAlign="client" >
<div style="margin:5px;">
<div class="mainTitle" style="padding-bottom:5px;margin-top:5px;">
                <s:text name="accordionLabel.miscellaneousParts"/>
            </div>
	<table class="grid borderForTable"  style="margin-left:0px; width:97%;" cellspacing="0" cellpadding="0">
		<thead>
			<tr class="row_head">
				<th width="20%"><s:text name="columnTitle.miscellaneousParts.partNumber"/></th>
				<th width="30%"><s:text name="columnTitle.miscellaneousParts.partDescription"/></th>
				<th width="10%"><s:text name="label.common.quantity" /></th>
				<s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id==loggedInUsersDealership.id
					|| claim.filedBy.id == loggedInUser.id">
					<th width="20%"><s:text name="label.newClaim.unitPrice" /></th>
				</s:if>
				<th width="15%"><s:text name="columnTitle.miscellaneousParts.uom"/></th>
				<s:if test="loggedInUserAnInternalUser">
					<th width="15%"><s:text name="label.miscpart.thresHold"/></th>
				</s:if>
			</tr>
		</thead>
		<s:iterator
			value="claim.serviceInformation.serviceDetail.miscPartsReplaced">
			<tr>
			    <s:if test="miscItemConfig.miscellaneousItem != null">			        
			        <td class="labelStyle" nowrap="nowrap" width="20%"><s:property
					value="miscItemConfig.miscellaneousItem.partNumber" /></td>
				<td><s:property
					value="miscItemConfig.miscellaneousItem.description" /></td>
			   </s:if>
			   <s:else>
					 <td class="labelStyle" nowrap="nowrap" width="20%"><s:property
					value="miscItem.partNumber" /></td>
				     <td><s:property
					value="miscItem.description" /></td>
				</s:else>	
				<td class="labelStyle" nowrap="nowrap"><s:property value="numberOfUnits" /></td>
				<s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id==loggedInUsersDealership.id
					|| claim.filedBy.id == loggedInUser.id">
					<td><s:property value="pricePerUnit" /></td>
				</s:if>
				<td><s:property value="miscItemConfig.uom.type" /></td>
				<s:if test="loggedInUserAnInternalUser">
					<th width="15%"><s:property value="miscItemConfig.tresholdQuantity"/></th>
				</s:if>
			</tr>
		</s:iterator>

	</table>
</div>
</div>
