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

<form action="claimSearchResult.action" id="claimSearch">

   <div class="snap" style="height:549px;  overflow-X: hidden;overflow-Y: auto; background-color:#FFFFFF">

     <div class="separator" />

     <table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">
       <tr>
	     <authz:ifProcessor>
           <td class="searchLabel"><s:text name="label.common.users" />:</td>
           <td class="searchLabel">
		   <s:select name="claimFiledBy" list="#request['claimSearch.allProcessors']"
				listKey="name" listValue="name" headerKey="-1" headerValue="%{getText('label.selectUser')}" />
           </td>
	     </authz:ifProcessor>
         <td class="searchLabel"><s:text name="label.common.dealerName" />:</td>
         <td  class="searchLabel">
         	<s:textfield name="dealerName" />
         </td>
       </tr>
       <tr>
         <td class="searchLabel"><s:text name="label.common.claimNumber" />:</td>
         <td class="searchLabel">
         	<s:textfield name="claimId" />
         </td>
         <td class="searchLabel"><s:text name="label.common.serialNumber" />:</td>
         <td class="searchLabel">
         	<s:textfield name="serialNumber" />
		 </td>
       </tr>
       <tr>
         <td class="searchLabeltop"><s:text name="label.common.dealerNumbers" />:</td>
         <td class="searchLabeltop">
         	<t:textarea name="dealerNumbers" rows="4" cssClass="bodyText"
         		cssStyle="width:220px;overflow:hidden;" />
         </td>
         <td class="searchLabeltop"><s:text name="label.common.productTypes" />:</td>
         <td class="searchLabeltop">
         	<s:select name="productCodes" list="#request['claimSearch.allProductCodes']"
				listKey="id" listValue="name" multiple="true" cssStyle="width:230px; "/>
		</td>
       </tr>
       <tr>
         <td class="searchLabel"><s:text name="label.common.claimTypes" />:</td>
         <td colspan="3"><table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td width="1%" align="center">
               	<input type="checkbox" name="claimTypes" value="Retrofit" />
               </td>
               <td width="25%" class="searchLabelNormalgray"><s:text name="label.viewClaim.retrofitClaim"/></td>
               <td width="1%">
               		<input type="checkbox" name="claimTypes" value="Machine" />
               	</td>
               <td width="24%" class="searchLabelNormalgray"><s:text name="label.viewClaim.machineClaim"/></td>
               <td width="1%" align="center">
					<input type="checkbox" name="claimTypes" value="Parts" />
			   </td>
               <td class="searchLabelNormalgray"><s:text name="label.viewClaim.partsClaim"/></td>
            </tr>
           </table></td>
       </tr>
       <tr>
         <td colspan="4" ><table width="100%"  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td class="searchLabel" width="14%"><s:text name="label.common.dateOfClaim" />:</td>
               <td nowrap="nowrap" class="searchLabel" width="22%"><s:text name="label.common.from" />:
                 <s:textfield name="startDate" id="startDate" size="12" maxlength="12"/>
                 <img src="image/calendarIcon.gif" alt="Choose Date" width="15" height="12" border="0"
                 	id="startDate_Trigger" style="cursor: pointer;" />
				<script type="text/javascript">
                    Calendar.setup({
						inputField     :    "startDate",
						ifFormat       :    "%m/%d/%Y",
						button         :    "startDate_Trigger"
					});
				</script>
               </td>
               <td colspan="2" nowrap="nowrap" class="searchLabel" align="left"><s:text name="label.common.to" />:
                 <s:textfield name="endDate" id="endDate" size="12" maxlength="12"/>
                 <img src="image/calendarIcon.gif" alt="Choose Date" width="15" height="12" border="0"
                 	id="endDate_Trigger" style="cursor: pointer;" />
				<script type="text/javascript">
				    Calendar.setup({
				        inputField     :    "endDate",
				        ifFormat       :    "%m/%d/%Y",
				        button         :    "endDate_Trigger"
				    });
				</script>
               </td>
             </tr>
           </table></td>
       </tr>
     </table>
     <div class="separator" />
     <table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">
       <tr>
         <td width="17%" class="searchLabel"><s:text name="label.viewClaim.closedClaims" />:</td>
         <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="APPROVED" /></td>
               <td width="14%" class="searchLabelNormalgray"><s:text name="label.common.approved"/></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="REJECTED" /></td>
               <td class="searchLabelNormalgray"><s:text name="label.common.rejected"/></td>
             </tr>
           </table></td>
       </tr>
       <tr>
         <td width="17%" valign="top" class="searchLabel"><s:text name="label.viewClaim.openClaims" />:</td>
         <td><table width="100%"  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="DRAFT" /></td>
               <td width="14%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.draft"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="ON_HOLD" /></td>
               <td width="14%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.onHold"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="SERVICE_MANAGER_REVIEW" /></td>
               <td width="25%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.smr"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="PROCESSOR_REVIEW" /></td>
               <td nowrap="nowrap"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.processorReview"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="REPLIES" /></td>
               <td nowrap="nowrap"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.replies"/></span></td>
             </tr>
           </table></td>
       </tr>
       <tr>
       <td width="17%" valign="top" class="searchLabel"></td>
       <td><table width="100%"  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="TRANSFERRED" /></td>
               <td width="14%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.transferred"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="FORWARDED" /></td>
               <td width="14%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.forwarded"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="SERVICE_MANAGER_RESPONSE" /></td>
               <td width="25%"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.smResponse"/></span></td>
               <td width="3%" align="center"><input type="checkbox" name="claimStates" value="ADVICE_REQUEST" /></td>
               <td nowrap="nowrap"><span class="searchLabelNormalgray"><s:text name="label.viewClaim.adviceRequest"/></span></td>
             </tr>
       		</table></td>
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
               id="searchClaimResetButton" />
       <input type="submit" value="<s:text name='button.common.search' />" class="buttonGeneric"
                  id="searchClaimSubmitButton" />
     </div>
     <script type="text/javascript">
     	var dirtyFormManager = null;
     	configureDirtyCheck = function() {
     		dirtyFormManager = new tavant.twms.DirtyFormManager(getTabDetailsForIframe().tabId);
     		var handle = new tavant.twms.FormHandler(document.forms[0]);
     		handle.registerResetButton(dojo.byId("searchClaimResetButton"));
     		handle.registerSubmitButton(dojo.byId("searchClaimSubmitButton"));
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