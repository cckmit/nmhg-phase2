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

<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
    <script type="text/javascript" src="scripts/manageLists.js"></script>
    <u:stylePicker fileName="adminPayment.css"/>
	<u:stylePicker fileName="common.css"/>
	<u:stylePicker fileName="form.css"/>
    <s:head theme="twms"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        var countForCheckBox = 0;

        function moveSelectedOptionsFromAvailable(avail, selected) {
            copySelectedOptions(avail, selected, false, true);
            removeSelectedOptions(avail);
        }

        function moveSelectedOptionsFromSelected(avail, selected) {
            moveSelectedOptions(selected, avail, false);
        }

        function submitForm(btn) {
            btn.form.submit();
        }

        function eraseErrorMessage() {
            setTimeout(function() {
                dojo.query(".erasableErrorMessage").wipeOut().play();
            }, 1000);
        }



    </script>
    <style>
    .admin_selections {
		font-size:9pt;
		width:120px;
	}
	.hederTxt{
	color:#0054A6;
	font-family:Arial, Helvetica, sans-serif;
	font-size:9pt;
	}
    </style>
</head>


<u:body>
<div style="display:none;" id="errorDiv">
<u:actionResults/>
</div>

<s:form name="buForm" id="buForm"  theme="twms" action="updateUserBUMapping">
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-x:hidden; overflow-y:auto;" id="root" >
        <div dojoType="dijit.layout.ContentPane">
        <div class="policy_section_div" style="width:100%;margin-right:5px;">
        <div id="dcap_pricing_title" class="section_header">
            <s:text name="title.bu.user.search"/>
      </div>
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" style="width:99%" align="center"> 
            <tr class="row_head">
                <th colspan="2"><s:label  value="%{getText('label.user.name')}" cssClass="hederTxt"/></th>
                <th><s:label value="%{getText('label.availableBu')}" cssClass="hederTxt"/></th>
                <th>&nbsp;</th>
                 <th><s:label value="%{getText('label.addedBuName')}" cssClass="hederTxt"/> </th>
            </tr>
            <s:iterator value="userList" status="userResults" id="user"  >
               <tr>
                     <td>
                         <input type="checkbox" name="userListSelected<s:property value='{#userResults.index}'/>" value='<s:property value="#user.id"/>'
                                                                     id="userListCheck<s:property value='{#userResults.index}'/>" />
                     </td>
                      <td>
                           <s:property value='#user.firstName' />
                           <s:property value='#user.LastName' />
                         ( <s:property value='#user.name' />)
                     </td>


                     <td>
                         <s:updownselect id="bu_unselected_%{#userResults.index}" cssClass="admin_selections" list="getBusinessUnits(#user)"
                            listKey="name" listValue="displayName"
                            name="buUselected%{#userResults.index}" size="6"
                            allowMoveDown="false" allowMoveUp="false" allowSelectAll="false"  theme="simple"/>

                      </td>
                      <td>

                            <input type="button" class="buttonGeneric" name="right" value="&lt;&lt;"
                             onclick="moveSelectedOptionsFromAvailable(
                                dojo.byId('bu_selected_<s:property value="%{#userResults.index}"/>'),
                                dojo.byId('bu_unselected_<s:property value="%{#userResults.index}"/>'))"><br/>
                            <input type="button" class="buttonGeneric" name="left" value="&gt;&gt;"
                             onclick="moveSelectedOptionsFromSelected(
                                dojo.byId('bu_selected_<s:property value="%{#userResults.index}"/>'),
                                dojo.byId('bu_unselected_<s:property value="%{#userResults.index}"/>'))">
                       </td>
                       <td>
                        <s:updownselect id="bu_selected_%{#userResults.index}" cssClass="admin_selections" list="#user.businessUnits"
                            listKey="name" listValue="displayName" size="6" name="userListSelected[%{#userResults.index}].businessUnitAdded"
                            allowMoveDown="false" allowMoveUp="false" allowSelectAll="false" theme="simple"/>

                       </td>
                   </tr>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                       
                        dojo.connect(dojo.byId("userListCheck<s:property value='{#userResults.index}'/>"), "onchange", function() {
                            if (dojo.byId(("userListCheck<s:property value='{#userResults.index}'/>")).checked) {
                                countForCheckBox = countForCheckBox + 1;

                            } else {
                                countForCheckBox = countForCheckBox - 1;

                            }
                        });
                    });
                </script>
            </s:iterator>
        </table>

</div>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<div align="center" class="buttons spacingAtTop">
    
                <%--  <input type="button" class="searchButton" id="updateButton" value="Update"/> --%>
                    

                   <s:submit cssClass="buttonGeneric" value="Update" id="updateButton"/>

                    <s:submit cssClass="buttonGeneric" value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                            dojo.connect(dojo.byId("updateButton"), "onclick", function(event) {
                                if (countForCheckBox == 0) {
                                    dojo.style(dojo.byId("errorDiv"),"display","block");
                                    dojo.stopEvent(event);
                                 }
                            });
                        });
                        
                    </script>
               </div>
               </authz:ifNotPermitted>
               </div>
            </div>
</s:form>
</u:body>

</html>