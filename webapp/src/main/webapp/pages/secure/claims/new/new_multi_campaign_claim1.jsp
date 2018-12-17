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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><s:text name="title.newClaim.campaignClaim"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <script type="text/javascript">
      dojo.require("dijit.layout.ContentPane");
    </script>


    <script type="text/javascript" src="scripts/ClaimForm.js"></script>

    <style type="text/css">
        table.form {
            margin-bottom: 5px;
            background-color:#F3FBFE;
			margin-left:5px;
			width:99%;
        }

        label {
            color: #000000;
            font-weight: 400;
        }

	
    </style>
</head>
	
<u:body>


	<u:actionResults />
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow:auto" >
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">

	<h3><s:text name="label.viewClaim.page1of2"/></h3>
	<s:form method="post" theme="twms" validate="true" id="form"
		name="saveMultiCampaignClaimDraft" action="saveMultiCampaignClaimDraft.action">
		
		<s:hidden name="claim.type.type" value="Campaign"/>	
		<s:hidden name="claimType" value="Campaign" />
		<s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value = "%{currentBusinessUnit.name}"/>
		</s:if>
		<s:else>
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
		</s:else>
		<div style="background:#F3FBFE;border:1px solid #EFEBF7">
		<table class="form" cellpadding="0" cellspacing="0" >
			<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
			<tr>
				<td width="22%" nowrap="nowrap">
				<label for="dealer" class="labelStyle"><s:text name="label.common.dealer"/>:</label></td>
				<td style="text-transform:uppercase" width="34%">
                <s:if test="claim == null || claim.id == null">                	
                    <s:if test="isLoggedInUserADealer()">                    
                        <s:property value="getLoggedInUsersDealership().name"/>
                        <s:hidden name="dealer" value="%{loggedInUsersDealership}"/>
                        <s:hidden name="claim.forDealer" value="%{loggedInUsersDealership.id}" id="dealer"/>
                        <s:hidden name="claim.filedBy" value="%{loggedInUsersDealership}"/>
                        <script type="text/javascript">
					        dojo.addOnLoad(function() {
					            dojo.byId("dealerIdForSearch").value='<s:property value="getLoggedInUsersDealership().id"/>';
							});
    					</script>
                    </s:if>
                    <s:else>                   
                        <sd:autocompleter id='dealer' href='list_claim_dealers.action' name='claim.forDealer' value='%{claim.forDealer.name}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}"/>
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
                        <s:hidden name="claim.filedBy" value="%{loggedInUsersDealership}"/>
                        <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var dealerSelect = dijit.byId("dealer");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(dealerSelect, "onChange", function(value){
					            	dojo.byId("dealerIdForSearch").value=value;
					            });
							});
    					</script>
                    </s:else>
                </s:if>
                <s:else>                
                    <s:hidden name="claim.forDealer" value="%{claim.forDealer.id}" id="dealer"/>
                    <s:hidden name="claim.filedBy"/>
                    <s:property value="claim.forDealer.name"/>
                    <script type="text/javascript">
					        dojo.addOnLoad(function() {
					            dojo.byId("dealerIdForSearch").value='<s:property value="claim.forDealer.id"/>';
							});
    				</script>
                </s:else>
           	  </td>
				<td nowrap="nowrap" width="15%" ><label for="type" class="labelStyle"><s:text name="label.common.claimType"/>:</label></td>
				<td>
				<s:select id="type" name="claimType" list="claimTypes" 
					listKey="type" listValue="%{getText(displayType)}" disabled="true" cssStyle="width:145px;" />
                </td>
			</tr>
			<tr><td style="padding:0;margin:0">&nbsp;</td></tr>
		</table>

		<jsp:include flush="true"
			page="../forms/common/write/multi_campaign_header.jsp" />
</div>
		<table align="center" border="0" cellpadding="0" cellspacing="0"
			class="buttons">
			<tbody>
				<tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" />
                </td>
            </tr>
			</tbody>
		</table>
		<s:hidden name="pageOne" value="true" />
	</s:form></div>
	<jsp:include flush="true" page="../forms/common/write/multiCarSearch.jsp"/>
</u:body>
</html>
