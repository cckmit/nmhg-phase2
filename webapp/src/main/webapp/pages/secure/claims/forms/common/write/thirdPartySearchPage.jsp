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
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");    
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane"); 
    dojo.require("dojox.layout.ContentPane");    
</script>
<script type="text/javascript" src="scripts/thirdParty/thirdPartySearchPage.js"></script>
<%--
This script is to display the pop up page to search the third parties on whose behalf the claim is being filed.
It gives the options to search a third party based on the third party name or number.
--%>

<div id="outlineThirdPartySearch" style="display:none">
	<div dojoType="twms.widget.Dialog" id="thirdPartySearch" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
     	toggleDuration="250" title="<s:text name='label.common.thirdPartySearch' />">
     	
	<div dojoType="dijit.layout.LayoutContainer" style="overflow: auto;">
    	<div dojoType="dojox.layout.ContentPane" layoutAlign="top">
    
    		<s:form method="post" theme="twms" id="thirdPartySearchForm" name="searchThirdParty"
            	action="searchThirdPartiesForClaim.action">
   	  
    <table class="grid" cellpadding="0" cellspacing="0" style="margin-top: 5px; width: 98%; padding-bottom: 10px;">
    <tbody>
        <tr>
            <td>
                <label id="thirdPartyNumberForSearch" class="labelStyle">
                    <s:text name="label.claim.thirdPartyNumber"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="thirdPartySearch.thirdPartyNumber" id="searchThirdPartyNumber"/>
            </td>
            <td>
                <label id="thirdPartyNameForSearch" class="labelStyle">
                    <s:text name="label.claim.thirdPartyName"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="thirdPartySearch.thirdPartyName" id="searchThirdPartyName"/>
            </td>
        </tr>
        <tr>
            <td colspan="4" align="center" class="buttons" style="padding-top: 20px; padding-bottom:5px;">
                <input type="button" id="resetThirdPartySearch" 
                	value="<s:property value="%{getText('button.common.reset')}"/>" />
                <s:submit id="searchThirdPartiesButton" value="%{getText('button.common.search')}" />
            </td>
        </tr>
        </tbody>
    </table>
	</s:form>
	<div id="thirdPartyResultDiv" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
    	 style="padding-bottom: 3px; height: 220px;">
	</div>


	</div>
    </div>
</div>
</div>