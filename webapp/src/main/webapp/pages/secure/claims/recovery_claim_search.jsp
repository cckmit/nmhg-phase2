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
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<s:head theme="twms" />
<!-- Need to move the styles out to a file. Added here for now.  -->

<u:stylePicker fileName="searchForm.css"/>

<!-- start: added for getting the Calendar in Claim Search screen to work -->
<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
<!-- end -->

<script type="text/javascript" src="scripts/DirtyForm.js"></script>
<script>
	dojo.addOnLoad(function () {
			parent.publishEvent("/accordion/refreshsearchfolders");
	    });
</script>
</head>
<u:body >

<s:actionerror theme="xhtml" />
<s:fielderror theme="xhtml" />

<form action="recoveryClaimSearchResult.action" id="recoveryClaimSearch">

   <div class="snap" style="height:549px;  overflow-X: hidden;overflow-Y: auto; background-color:#FFFFFF">

     <div class="separator" />

     <table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">
       <tr>
         <td class="searchLabel"><s:text name="label.common.claimNumber" />:</td>
         <td class="searchLabel">
         	<s:textfield name="claim.claimNumber" />
         </td>
         <td class="searchLabel"><s:text name="label.recoveryClaim.recoveryClaimState" />:</td>
         <td class="searchLabel">
         	<s:textfield name="recoveryClaimState.state" />
         </td>
       </tr>
     </table>  
     <div class="separator" />
     <table class="bgColor" width="100%" border="0" cellspacing="0"
            cellpadding="0">
        <tr>
            <td class="searchLabel" width="17%"><s:text name="label.viewClaim.saveSearchAs"/> : </td>
            <td class="searchLabel">
                <s:textfield name="searchName" />
            </td>
        </tr>
        <tr>
            <td class="searchLabeltop" width="17%"><s:text name="label.viewClaim.detailedDescription"/> : </td>
            <td class="searchLabeltop">
                <t:textarea name="searchDescription" rows="4" cssClass="bodyText"
                            cssStyle="width:220px;overflow:hidden;margin-bottom:5px;"/>
            </td>
        </tr>
     </table>
     <div class="separator" />
     <div class="buttonWrapperPrimary">
       <input type="reset" class="buttonGeneric" value="<s:text name='button.common.reset' />"
               id="searchRecClaimResetButton" />
       <input type="submit" value="<s:text name='button.common.search' />" class="buttonGeneric"
                  id="searchRecClaimSubmitButton" />
     </div>
     <script type="text/javascript">
     	var dirtyFormManager = null;
     	configureDirtyCheck = function() {
     		dirtyFormManager = new tavant.twms.DirtyFormManager(getTabDetailsForIframe().tabId);
     		var handle = new tavant.twms.FormHandler(document.forms[0]);
     		handle.registerResetButton(dojo.byId("searchRecClaimResetButton"));
     		handle.registerSubmitButton(dojo.byId("searchRecClaimSubmitButton"));
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