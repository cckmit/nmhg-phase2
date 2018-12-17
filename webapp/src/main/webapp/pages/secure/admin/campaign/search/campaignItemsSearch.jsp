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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<!-- Need to move the styles out to a file. Added here for now.  -->
<s:head theme="twms"/>

<script type="text/javascript" src="scripts/DirtyForm.js"></script>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="inventoryForm.css"/>
<u:stylePicker fileName="searchForm.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="layout.css" common="true"/>
</head>
<u:body>
<form action="campaignItemsSearchResult.action" id="searchForm">
	<div class="snap"> 
	<div class="separator" />
    <s:hidden name="campaign"/>
   				<table class="form" cellspacing="0" cellpadding="0">
       				<tr>
           				<td class="searchLabel" style="width: 40%"><s:text name="label.common.dealerName"/></td>
           				<td class="searchLabel">
           					<s:select size="100" list="dealers" theme="twms" listKey="id" listValue="name" 
           						emptyOption="true" name="searchCriteria.assignedDealerId"/>
           				</td>
           				<td class="searchLabel"><s:text name="label.common.status"/></td>
				        <td class="searchLabel">
           					<s:select size="100" list="statuses" theme="twms" listKey="code" listValue="description" 
           						emptyOption="true" name="searchCriteria.status"/>
           				</td>
		       		</tr>
		       		
					<tr>
					    <td class="searchLabeltop"><s:text name="label.common.serialNumber"/></td>
					    <td class="searchLabeltop"><s:textfield name="searchCriteria.serialNumber"/></td>
					    <td colspan="2"/>
					</tr>
				</table>

     <div class="separator" />
     <div class="buttonWrapperPrimary"> 
       <input type="reset" value="<s:text name='button.common.reset'/>" class="buttonGeneric" id="campaignSearchResetButton"/>
       <input type="submit" value="<s:text name='button.common.search'/>" class="buttonGeneric" id="campaignSearchSubmitButton"/>
     </div> 
     <script type="text/javascript">
     	var dirtyFormManager = null;
     	configureDirtyCheck = function() {
     		dirtyFormManager = new tavant.twms.DirtyFormManager(getTabDetailsForIframe().tabId);
     		var handle = new tavant.twms.FormHandler(document.forms[0]);
     		handle.registerResetButton(dojo.byId("campaignSearchResetButton"));
     		handle.registerSubmitButton(dojo.byId("campaignSearchSubmitButton"));
     		dirtyFormManager.registerHandler(handle);
     	}
     	dojo.addOnLoad(function() {
     		configureDirtyCheck();
     	});
     </script>
 </div>
</form>
</u:body>
</html>