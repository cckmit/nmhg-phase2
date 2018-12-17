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

<%--The following JSP is fixed with heads and tails :D--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>



<html>
<head>
    <title><s:text name="title.common.warranty" /></title>    
    <s:head theme="twms" />

<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
</head>
<u:body>
<form name="baseForm" id="baseForm">
    <u:actionResults/>
    <s:hidden name="id" value="%{dealerScheme.id}" id="schemeId"/>
    <s:hidden name="dealerGroupConfigParam"/>
    <div class="admin_section_div" style="margin:5px;">
        <div class="admin_section_heading">
            <s:text name="label.manageGroup.createNewGroup"/>
        </div>
       
            <div class="mainTitle" style="margin-top:5px;">
                <s:text name="label.manageGroup.dealerGroupDetails"/>
            </div>
          
            <div class="borderTable">&nbsp;</div>
           <div style="margin-top:-10px;">
            <table class="grid" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/></td>
                    <td class="admin_selections"><s:textfield size="50" maxlength="200" name="dealerGroup.name"/></td>
                </tr>
                <tr>
                    <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
                    <td class="admin_selections"><s:textfield size="50" maxlength="1000"
                                                              name="dealerGroup.description"/></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                    <td colspan="2">
                        <s:submit cssClass="buttonGeneric" value="%{getText('button.manageGroup.createDealerGroup')}"
                                  action="save_dealergroup"/>
                        <input type="button" id="closeButton" class="buttonGeneric"
                               value="<s:text name='button.common.cancel'/>"/>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.connect(dojo.byId('closeButton'), "onclick", function() {
                                    var thisTabId = getTabDetailsForIframe().tabId;
                                    var thisTab = getTabHavingId(thisTabId);
                                    closeTab(thisTab);
                                });
                            });
                        </script>
                    </td>
                </tr>
            </table>
      </div>
        <br/>
            <div class="mainTitle">
                <table class="grid " style="width:50%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td width="30%" class="colHeader" valign="center">
                            <s:text name="label.manageGroup.consistingOfGroups"/>
                            <input type="radio" name="groupType" 
                                   onclick="setGroupType('<s:url action="manage_dealer_group"/>')">
                        </td>
                        <td class="colHeader" width="40%" valign="center">
                            <s:text name="label.manageGroup.consistingOfDealers"/>
                            <input type="radio" name="groupType"
                                   onclick="setGroupType('<s:url action="manage_dealer" />')">
                        </td>
                    </tr>
                </table>
            </div>
        </div>
  <br/>
</form>
</u:body>
</html>
