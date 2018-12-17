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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<div>
	<input type="button" class="buttonGeneric" id="assignCampaigns" name="assignCampaigns" value="<s:text name='link.campaign.assignCampaigns'/>" id="assignCampaigns" align="left"/>
	<input type="button" class="buttonGeneric" id="viewAllItems" name="viewItems" value="<s:text name='link.campaign.viewAllItems'/>" id="viewItems" align="left"/>
</div>	
<script type="text/javascript">
	dojo.addOnLoad(function(){
		dojo.connect(dojo.byId("viewAllItems"), "onclick", function(event){
            var url = "campaignItemsSearch.action?campaign=<s:property value="campaign"/>";
			var tabLabel = "Campaign Item View";
			parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : ""});
		    dojo.stopEvent(event);
		});
		
		dojo.connect(dojo.byId("assignCampaigns"), "onclick", function(event) {
			win = window.open("assign_campaigns.action", 'win','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,copyhistory=no,Width=600,height=150,top=250,left=230');
		    dojo.stopEvent(event);
		});
	});
</script>	
