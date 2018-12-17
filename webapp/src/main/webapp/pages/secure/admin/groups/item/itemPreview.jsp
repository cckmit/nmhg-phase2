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
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <s:head theme="twms"/>
     <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
	<u:stylePicker fileName="common.css"/>
 
</head>

<u:body>
 <div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: none;">

  <div id="item_detail" class="policy_section_div" style="width:100%">
	<div id="item_detail_title"  class="section_header">
		<s:property value="%{getText('label.itemDetail')}" />
	</div>				
	   <table width="96%" cellpadding="0" cellspacing="0" class="grid"
				>
			<tr>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.itemNumber" /></td>
					<td ><s:property value="item.number" /></td>
					<td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.itemDescription" /></td>
					<td><s:property value="item.description" /></td>
			</tr>
		
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.itemType" /></td>
					<td><s:property value="item.itemType" /></td>
					<td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.itemSearch.status" /></td>
					<td><s:property value="item.status" /></td>
			</tr>
			
			<s:if test="item.itemType !=null && 'PART' == item.itemType">
				
					<tr>
						 <td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReturn" /></td>
						 <td><s:property value="isPartForReturn(item)" /></td>
						<authz:ifUserInRole roles="admin, processor">
							 <td class="labelStyle" nowrap="nowrap"><s:text name="label.itemForReview" /></td>
							 <td><s:property value="isPartForReview(item)" /></td>
						</authz:ifUserInRole>
					</tr>
			</s:if>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.location" /></td>
					<td><s:property value="item.make" /></td>
			</tr>
	   </table>	
     </div>	
   </div>

  
</u:body>
</html>
