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

<%--This JSP neeed some fixing... :D Fixed the heads and tails of the JSP--%>

<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>



<html>
<head>
    <s:head theme="twms" />

<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript" src="scripts/admin.js"></script>
<script type="text/javascript">

    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.form.ComboBox");
</script>
<script type="text/javascript" src="scripts/dealerGroupSearch.js"></script>
<script type="text/javascript">
function searchDealers(submitAction) {
	var url = submitAction +"?" +
              
        "id=" + dojo.byId("schemeId").value +
		"&dealerGroup=" + dojo.byId("groupId").value +
		"&dealerName=" + dojo.byId("p1").value +
		"&dealerNumber=" + dojo.byId("p3").value;
	
	var targetContent=dijit.byId('searchResults');
    targetContent.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
			
	twms.ajax.fireHtmlRequest(url, {}, function(data) {
      dojo.byId("searchResults").innerHTML = data;
       
    });
}
</script>
</head>
<u:body>
<s:form name="baseForm" id="baseForm">
<u:actionResults/>
<s:if test="dealerScheme.id!=null">
    <s:hidden name="dealerSchemeId" value="%{dealerScheme.id}" id="schemeId"/>
</s:if>
<s:else>
    <s:hidden name="id" value="%{dealerScheme.id}" id="schemeId"/>
</s:else>
<s:hidden name="dealerGroup.id" id="groupId"/>
<s:hidden name="dealerGroupConfigParam"/>
<div class="admin_section_div" style="margin:5px;">
    <div class="admin_section_heading">
        <s:text name="%{sectionTitle}"/>
    </div>
    <div class="mainTitle" style="margin-top:5px;">
            <s:text name="label.manageGroup.dealerGroupDetails"/>
        </div>
       
        <div class="borderTable">&nbsp;</div>
        <table class="grid" cellspacing="0" cellpadding="0" style="margin-top:-10px;">
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.manageGroup.groupName"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="200" name="dealerGroup.name"/></td>
            </tr>
            <tr>
                <td class="admin_selections labelStyle"><s:text name="label.common.description"/></td>
                <td class="admin_selections"><s:textfield size="50" maxlength="1000"
                                                          name="dealerGroup.description"/></td>
            </tr>

        </table>
          <br/>
        <table class="grid">
            <tr>
                <td colspan="2">
                    <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_dealer"
                              onclick="selectAllOptions('includedDealerNames',dojo.byId('baseForm'))"/>
                    <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addDealers"/>"
                           onclick="showDialog()"/>
                    <input type="button" id="closeButton1" class="buttonGeneric"
                           value="<s:text name='button.common.cancel'/>"/>
                </td>
            </tr>
        </table>
  
    <s:if test="%{dealerGroup.id == null}">
          <br/>
            <div class="mainTitle">
                <table class="grid">
                    <tr>
                        <td class="admin_section_subheading" width="40%">
                            <s:text name="label.manageGroup.consistingOfGroups"/>
                            <input type="radio" name="groupType"
                                   onclick="setGroupType('manage_dealer_group.action')">
                        </td>
                        <td class="admin_section_subheading">
                            <s:text name="label.manageGroup.consistingOfDealers"/>
                            <input type="radio" name="groupType" checked/>
                        </td>
                    </tr>
                </table>
            </div>
       
    </s:if>
    <div id="divForDealers">
      <div class="mainTitle" style="margin:10px 0px 10px 0px;">
                <s:text name="label.manageGroup.manageIncludedDealers"/>
            </div>
            
                 <s:if test="includedDealers != null && !includedDealers.isEmpty">
                     <div id="div_included_dealers" align="center" width="100%">
                        <table width="100%" class="grid borderForTable" cellspacing="0" cellpadding="0">
                            <tr class="row_head">
                                <th  width="5%" align="center"><input type="checkbox"
                                                                                  onclick="toggleOptions('includedDealerNames', this.checked)"/>
                                </th>
                                <th ><s:text name="label.common.name"/></th>
                            </tr>
                            <s:iterator value="includedDealers">                            
                                <tr>
                                    <td align="center">
                                        <input type="checkbox" name="includedDealerNames"
                                               value="<s:property value="name"/>"/>
                                    </td>
                                    <td class="admin_selections"><s:property value="name"/>
                                    </td>
                                </tr>
                            </s:iterator>
                            <tr>
                                <td colspan="2">
                                    <input type="button" class="buttonGeneric"
                                           value="<s:text name="button.manageGroup.removeDealers"/>"
                                           onclick="removeCheckedOptions('includedDealerNames', 'div_included_dealers', 'div_no_included_dealers')"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <p id="div_no_included_dealers" align="center" style="display:none"><s:text
                            name="error.manageGroup.noDealersIncluded"/></p>
                </s:if>
                       
                <s:else>
                    <p align="center"><s:text name="error.manageGroup.noDealersIncluded"/></p>
                </s:else>
                
        
      <div style="margin-left:7px;margin-top:7px;">
        <s:submit cssClass="buttonGeneric" value="%{buttonLabel}" action="save_dealer"
                  onclick="selectAllOptions('includedDealerNames',dojo.byId('baseForm'))"/>
        <input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.addDealers"/>"
               onclick="showDialog()"/>
        <input type="button" id="closeButton2" class="buttonGeneric" value="<s:text name='button.common.cancel'/>"/>
    </div>
    <br/>
    </div>
</div>




<div id="dialogBoxContainer" style="display:none">
    <div dojoType="twms.widget.Dialog" id="searchDealersDialog" 
    title="<s:text name="label.common.searchDealers"/>"style="width:90%;height:70%;">

            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="border-bottom:1px solid #EFEBF7;">
                <table class="admin_subsection_div" style="width:99%;">
                    <tr>
                        <td valign="bottom" class="labelStyle"><s:text name="label.common.dealerName"/> : <s:textfield id="p1"
                                                                                                    name="dealerName"/>&nbsp;</td>
                        <td valign="bottom" class="labelStyle"><s:text name="label.common.dealerNumber"/> : <s:textfield id="p3"
                                                                                                    name="dealerNumber"/>&nbsp;</td>
                        <td>
                            <input type="button" class="buttonGeneric"
                                   value="<s:property value="%{getText('button.common.search')}"/>"
                                   id="dealerSearchButton"
                                   onclick="searchDealers('search_dealers_for_dealergroup.action')">
                        </td>
                    </tr>
                </table>
            </div>
            
             <div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="searchResults">
            </div>
           
            <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="border-top:0px solid #EFEBF7">
                <table width="100%" class="buttonWrapperPrimary">
                    <tr>                    
                        <td align="center">
                            <s:if test="%{dealerGroup.id}">
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.common.update')}"/>"
                                       onclick="showSelectedDealers('add_dealers_to_dealergroup.action')"/>
                            </s:if>
                            <s:else>
                                <input type="button" class="buttonGeneric"
                                       value="<s:property value="%{getText('button.manageGroup.addDealers')}"/>"
                                       onclick="showSelectedDealers('add_dealers_to_dealergroup.action')"/>
                            </s:else>
                        </td>
                         <td align="center">    
                            <input type="button" name="Submit3" value="<s:text name="button.common.close"/>"
                                   class="buttonGeneric" onclick="closeDialog();"/>
                        </td>                        
                    </tr>
                </table>
            </div>
        </div>


</div>

<div id="addedDealersHere" style="display:none"></div>
<script type="text/javascript">

    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId('closeButton1'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        });

        dojo.connect(dojo.byId('closeButton2'), "onclick", function() {
            var thisTabId = getTabDetailsForIframe().tabId;
            var thisTab = getTabHavingId(thisTabId);
            closeTab(thisTab);
        });
    });
</script>
</s:form>
</u:body>
</html>

