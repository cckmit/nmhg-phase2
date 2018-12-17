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
<u:stylePicker fileName="layout.css" common="true"/>
</head>
<u:body>
<form action="inventorySearchResult.action" id="inventorySearch">
	<div class="snap" style="background-color:#FFFFFF"> 
	<div class="separator" />
    
   				<table class="bgColor" cellspacing="0" cellpadding="0">
       				<tr>
           				<td class="searchLabel" style="width: 16%"><s:text name="dealerName"/></td>
           				<td class="searchLabel"><s:textfield name="dealerName"/></td>
           				<td class="searchLabel"><s:text name="customer"/></td>
				        <td class="searchLabel"><s:textfield name="customer"/></td>
		       		</tr>
		       		
					<tr>
					    <td class="searchLabeltop"><s:text name="itemModel"/></td>
					    <td class="searchLabeltop">
					  	   <s:select name="itemModels" value="%{itemModels}" list="#request['inventorySearch.allModels']" 
					listKey="name" listValue="name" multiple="true" cssStyle="width:220px; height:50px"/>           
					    </td>
					    <td class="searchLabeltop"><s:text name="productCodes"/></td>
				        <td class="searchLabeltop">
				      	  <s:select name="productCodes" value="%{productCodes}" list="#request['inventorySearch.allProductCodes']" 
					  listKey="id" listValue="name" multiple="true" cssStyle="width:250px; height:50px"/>
				        
				        </td>
					</tr>
					
					<tr>
					    <td class="searchLabel"><s:text name="itemNumber"/></td>
					    <td class="searchLabel"><s:textfield name="itemNumber" /></td>
					    <td class="searchLabel"><s:text name="serialNumber"/></td>
				        <td class="searchLabel"><s:textfield name="serialNumber" /></td>
					</tr>
				</table>

     <div class="separator" />
     <div class="buttonWrapperPrimary"> 
       <input type="reset" value="<s:text name='reset'/>" class="buttonGeneric" id="inventorySearchResetButton"/>
       <input type="submit" value="<s:text name='search'/>" class="buttonGeneric" id="inventorySearchSubmitButton"/> 
     </div> 
 </div>
</form>
<script type="text/javascript">
  	var dirtyFormManager = null;
   	configureDirtyCheck = function() {
    	dirtyFormManager = new tavant.twms.DirtyFormManager(getTabDetailsForIframe().tabId);
    	var handle = new tavant.twms.FormHandler(document.forms[0]);
    	handle.registerResetButton(dojo.byId("inventorySearchResetButton"));
    	handle.registerSubmitButton(dojo.byId("inventorySearchSubmitButton"));
    	dirtyFormManager.registerHandler(handle);
    }
    dojo.addOnLoad(function() {
    	configureDirtyCheck();
    });
</script>
</u:body>
</html>