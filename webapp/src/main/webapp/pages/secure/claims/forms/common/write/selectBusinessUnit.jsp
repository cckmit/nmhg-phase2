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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
</head>	

<script type="text/javascript">

dojo.require("twms.widget.TitlePane");
dojo.require("dijit.layout.LayoutContainer");




var partOptions = null;
function fillPartDescription(data, type, request) {
    if (type != "valuechanged") {
        return;
    }
    twms.ajax.fireJavaScriptRequest("list_description_for_part.action",{
        number:data
    },function(details) {
    		if(dojo.byId("partDescription")){
           		dojo.byId("partDescription").innerHTML=details;
           	}
        }
    );
}

dojo.addOnLoad(function() {
    dojo.subscribe("/part/changed", null, fillPartDescription);
});


</script>
	<u:body>
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		
		<s:form method="post" theme="twms" validate="true" id="baseform"
        name="baseform" >
		<s:hidden name="thirdParty" id="thirdPartyId"/>
		<s:hidden id="isTransferOrReProcess" name="isTransferOrReProcess"/>
		<s:hidden id="context" name="context"></s:hidden>	
		<s:hidden id="isMultiClaimMaintenance" name="isMultiClaimMaintenance"/>
			  <div style="margin:5px;background:#F3FBFE;border:1px solid #EFEBF7" >
				<div class="section_header">
					<s:text name="label.common.chooseBU"></s:text>
				</div>
				<table width="96%" border="0" cellspacing="0" cellpadding="0" class="grid">
					<tbody>
					<tr>
				  		<td colspan="6" height="5"></td>
					</tr>
					<tr>
						<td width="16%" class="labelStyle" nowrap="nowrap">
							<s:text name="label.common.businessUnitName"/>:
						</td>
						<td colspan="5">							
	            			<s:select					            
					            id="buNames"
					            label="Business Unit"
					            list="businessUnits"
					            listKey="name"
					            listValue="name"
					            name="selectedBusinessUnit"
					            value="%{selectedBusinessUnit}"
						        emptyOption="false"/>          			
						</td>						
					</tr>
					<tr><td style="padding:0">&nbsp;</td></tr>
					</tbody>
				</table>
			</div>
		   
		   
			<div align="center">
			<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>" onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
				<s:submit action="chooseClaimServiceProviderType" cssClass="button" align="center" value="%{getText('button.common.continue')}"></s:submit>               
            </div>
		</s:form>
		</div>
		</div> 		
	</u:body>
</html>
