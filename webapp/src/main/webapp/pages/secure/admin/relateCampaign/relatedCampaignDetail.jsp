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

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title><s:text name="title.common.warranty" /></title>
<s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css" />

<script type="text/javascript" src="scripts/RepeatTable.js"></script>
<script type="text/javascript" src="scripts/AdminToggle.js"></script>
<script type="text/javascript"
	src="scripts/adminAutocompleterValidation.js"></script>

</head>
<u:body>
	<s:form name="baseForm" id="baseFormId" method="post">
		<u:actionResults />
		<s:hidden name="id" id="blahblah" />
		<div style="background: #F3FBFE; border: 1px solid #EFEBF7">
		<div class="policy_section_heading"><s:text
			name="title.relatedCampaign.add" /></div>

		<table border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
					name="columnTitle.common.FullCode" /> :</td>
				<td><s:textfield name="relateCampaign.code" cssStyle="width:100px"
					value="%{relateCampaign.code}" /></td>
			</tr>
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
					name="columnTitle.common.description" /> :</td>
				<td><s:textarea name="relateCampaign.description" cssClass="textarea" cols="40"
					value="%{relateCampaign.description}" /></td>
			</tr>
		</table>
		</div>

		<div class="admin_section_div" style="margin: 5px; width: 99%">


		<div class="policy_section_heading"><s:text
			name="label.campaign.addCampaign" /></div>
		<u:repeatTable id="myTable" cssClass="grid borderForTable"
			cellpadding="0" cellspacing="0" cssStyle="margin:5px;" theme="simple">
			<thead>
				<tr class="title">
					<th width="98" class="colHeader"><s:text
						name="dropdown.common.campaigns" /></th>
					<th width="2%" class="colHeader">
					<u:repeatAdd id="related_campaign_adder"
						theme="simple">
						<img id="addCampaign" src="image/addRow_new.gif" border="0"
							style="cursor: pointer; padding-right: 4px;"
							title="<s:text name="label.campaign.addCampaign" />" />
					</u:repeatAdd></th>
				</tr>
			</thead>
			<u:repeatTemplate id="related_campaign_mybody" value="relateCampaign.includedCampaigns"
				index="myindex" theme="twms">
				<tr index="#myindex">
					<td style="border: 1px solid #EFEBF7;"/>
					
					<sd:autocompleter id='campaigns_#myindex_Id' showDownArrow='false' href='list_active_campaign_starts_with.action' cssStyle='width:200px' name='relateCampaign.includedCampaigns[#myindex]' keyName='relateCampaign.includedCampaigns[#myindex]' value='%{relateCampaign.includedCampaigns[#myindex].code}' key='%{relateCampaign.includedCampaigns[#myindex].id}' />

					<td style="border: 1px solid #EFEBF7;"><u:repeatDelete
						id="relate_campaign_deleter_#myindex" theme="simple">
						<img id="deleteRelatedCampaign" src="image/remove.gif" border="0"
							style="cursor: pointer; padding-right: 4px;"
							title="<s:text name="label.campaign.deleteCampaign" />" />
					</u:repeatDelete><s:hidden name="relateCampaign.includedCampaigns[#myindex]"></s:hidden>
					</td>
				</tr>
			</u:repeatTemplate>
		</u:repeatTable></div>
		<div align="center" style="margin: 10px 0px 0px 0px;"><s:submit
			id="closeTab" value="Cancel" cssClass="buttonGeneric" action="" /> <script
			type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			                 
			    });
			</script> <s:submit value="Submit" cssClass="buttonGeneric"
			action="saveRelatedCampaign" /></div>
	</s:form>
</u:body>
</html>
