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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%--NOTE : This file is meant to work only when included in another file, which has all the javascript functions that r used here--%>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
</script>
<div id="dialogBoxContainer" style="display:none">
<div dojoType="twms.widget.Dialog" style="overflow:hidden; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:7.5pt; font-weight:700; width:80%;" id="searchRulesDialog"
     title="<s:text name="label.manageBusinessCondition.searchInput" />" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
     toggleDuration="250">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="top"
             style="border-bottom:1px solid #EFEBF7 ;overflow-x:hidden;">
			<table width="100%">
				<tr>
					<td style="padding:5px; width:30%">
						<s:textfield id="ruleSearchStringInput" name="ruleSearchStringInput" size="70"
                                     cssStyle="height:20px" />
                        <script type="text/javascript">
                            dojo.connect(
                                    dojo.byId("ruleSearchStringInput"),
                                    "onkeypress", function(evt) {
                                switch (evt.keyCode) {
                                    case evt.KEY_ESCAPE:
                                        closeDialog();
                                        break;
                                    case evt.KEY_ENTER: 
                                        requestRules();
                                        break;
                                }

                            });
                        </script>
                    </td>
					<td style="padding:5px;">
						<s:submit onclick="requestRules()" id="startSearch" value="%{getText('button.common.go')}"
                                  cssClass="buttonGeneric"/>
					</td>
				</tr>
			</table>
		</div>
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="searchResults" style="background:#F3FBFE; height: 200px">
        </div>
        <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom"
             style="border-top:1px solid #EFEBF7 overflow-x: hidden">
			<table width="100%">
				<tr>
					<td align="center">
						<s:submit onclick="addTermsToForm()" id="addTerms"
                                  label="button.manageBusinessCondition.addBusinessCondition"
                                  cssClass="buttonGeneric"/>
					</td>
				</tr>
			</table>
		</div>
</div>
</div>