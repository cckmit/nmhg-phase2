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
<s:hidden name='campaignFor'/>
<s:if test="campaignFor == 'SERIAL_NUMBER_RANGES'">
	<div class="admin_section_div" style="width: 99%">
	<div class="admin_section_heading"><s:text
		name="label.campaign.criteria" /></div>
	<div class="admin_subsection_div">
	<div class="admin_section_subheading"><s:text
		name="label.campaign.patterns" /></div>
	<table  width="100%" cellspacing="0"
		cellpadding="2" class="grid borderForTable">
		<tr class="row_head">
			<th ><s:text name="label.campaign.patternStart" /></th>
			<th><s:text name="label.campaign.patternEnd" /></th>
			<th ><s:text name="label.campaign.patternToApply" /></th>
			<th ><s:text name="label.campaign.selectedOperation" /></th>
		</tr>
		<s:iterator value="campaign.campaignCoverage.rangeCoverage.ranges" status="stat">
			<tr>
			<s:hidden name='campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}]' value='%{campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}]}'/>
			<s:hidden name='campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}].fromSerialNumber' value='%{fromSerialNumber}'/>
			<s:hidden name='campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}].toSerialNumber' value='%{toSerialNumber}'/>
			<s:hidden name='campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}].patternToApply' value='%{patternToApply}'/>
			<s:hidden name='campaign.campaignCoverage.rangeCoverage.ranges[%{#stat.index}].attachOrDelete' value='%{attachOrDelete}'/>
				<td><s:property value="fromSerialNumber" /></td>
				<td style="border-left:1px solid #DCD5CC"><s:property
					value="toSerialNumber" /></td>
				<td style="border-left:1px solid #DCD5CC"><s:property value="patternToApply" /></td>
				<s:if test="attachOrDelete=='attach'">
				<td style="border-left:1px solid #DCD5CC"><s:text name="Add Items" /></td>
				</s:if> 
				<s:else>
					<td style="border-left:1px solid #DCD5CC"><s:text name="Remove Items" /></td>
				</s:else> 
					              
			</tr>
		</s:iterator>
	</table>
	</div>	

</s:if>

<s:if test="campaignFor == 'SERIAL_NUMBERS'">
	<div class="admin_section_div" style="width: 99%">
		<div class="admin_subsection_div">
			<div class="admin_section_subheading"><s:text name="label.campaign.serialNumbersUploaded" /></div>
		</div>
        
</s:if>

<div align="center">
	<a href="#" id="viewAllItems"><s:text name="link.campaign.viewAllItems"/></a>
   </div>

<script type="text/javascript">
	dojo.addOnLoad(function() {//todo : someone fix me to use dijit.Dialog
		dojo.connect(dojo.byId("viewAllItems"), "onclick", function(event){
			var campaignId = "<s:property value="campaign.id"/>"
			var actionURL = "list_campaign_items.action?id="+campaignId;
		    parent.publishEvent("/tab/open", {label: "View Affected Units", url: actionURL, decendentOf : ""});
		});
	});
</script>

