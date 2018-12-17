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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <script type="text/javascript" src="scripts/admin.js"></script>

    <script type="text/javascript">
        dojo.addOnLoad(function() {
			dojo.connect(dojo.byId("assignCampaignForAllCampaignItems"), "onclick", function(event) {
				win = window.open("assign_campaigns.action?forAllCampaignItems=true&campaign=<s:property value="campaign"/>", 'win','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=600,height=150,top=250,left=230');
			    dojo.stopEvent(event);
			});
			dojo.connect(dojo.byId("assignCampaignForNewCampaignItems"), "onclick", function(event) {
				win = window.open("assign_campaigns.action?forAllCampaignItems=false&campaign=<s:property value="campaign"/>", 'win','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=600,height=150,top=250,left=230');
			    dojo.stopEvent(event);
			});
		});
    </script>
<s:form method="POST" theme="twms" id="removeItems" name="removeItems" action="delete_items_from_campaign.action">
<s:hidden name="campaign"></s:hidden>

<table width="100%">
   <tr>
         <s:if test="attachOrDelete == 'delete'">
             <s:submit id="deleteItemsFromCampaign" value="%{getText('button.campaign.removeItemsFromCampaign')}" cssClass="buttonGeneric" />
         </s:if>
         <s:else>
			 <s:submit id = "assignCampaignForAllCampaignItems" value="%{getText('button.campaign.assignCampaigForAllItems')}" cssClass="buttonGeneric"/>
			 <s:submit id = "assignCampaignForNewCampaignItems" value="%{getText('button.campaign.assignCampaigForNewItems')}" cssClass="buttonGeneric"/>
		</s:else>
	</tr>
</table>
</s:form>

