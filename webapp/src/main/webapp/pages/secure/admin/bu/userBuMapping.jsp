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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="authz" uri="authz"%>
<html>
<head>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <s:head theme="twms"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            dojo.connect(dojo.byId("searchButton"), "onclick", function() {
               dojo.byId("searchButton").form.submit();

            });
        });
    </script>
</head>


<u:body>
    <u:actionResults/>
    <s:form name="buForm" action="searchBuUserMap" id="baseFormId">
        
        <div dojoType="dijit.layout.LayoutContainer"
             style="width: 100%; height: 100%; overflow-x:hidden; overflow-y:scroll;" id="root">
            <div class="policy_section_div">
            <div id="bu_user_search_div" class="section_header">
                <s:text name="title.bu.user.mapping"/>
            </div>
            <table width="100%" border="0" cellspacing="6" cellpadding="6"
                   class="header">
                   <tr>

                    <td  width="20%" nowrap="nowrap" ><s:label value="%{getText('label.loginUser')}" cssClass="labelStyle" /></td>

                    <td >
                        <s:textfield name="userLoginSearchStr" id="searchText"/>
                       
                    </td>

                </tr>

            </table>
            </div>
             <div align="center" class="spacingAtTop">
        <table class="buttons">
            <tr>
                <td align="center">
                    <s:submit cssClass="buttonGeneric" value="Search" id="searchButton"  />
                    <s:submit cssClass="buttonGeneric" value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                        });
                    </script>
                 </td>
            </tr>
        </table>

</div>
</div>
          
    </s:form>
</u:body>
<authz:ifPermitted resource="settingsManageUserBusinessUnitMappingReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>