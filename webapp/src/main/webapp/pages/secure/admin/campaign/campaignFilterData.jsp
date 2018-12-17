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

<div class="admin_section_div">
  <div class="admin_section_heading"><s:text name="label.campaign.criteria" /></div>
  <table style="width:30%" class="grid">
   
        <tr>
	      <td>
	        <input type="radio" name="campaignFor" id="campaignForParam"
                   value="SERIAL_NUMBERS" onclick="toggleFilterCriteria(this.value)" checked="checked"/>
	      </td>
	      <td class="labelStyle" nowrap="nowrap"><s:text name="label.campaign.serialNumbers"/></td>
	      <td>
	        <input type="radio" name="campaignFor" id="campaignForParam" 
                   value="SERIAL_NUMBER_RANGES" onclick="toggleFilterCriteria(this.value)" checked="checked"/>           
	      </td>
	      <td class="labelStyle" nowrap="nowrap"><s:text name="label.campaign.specifySerialNumberPatterns"/></td>
	    </tr>
	   <s:hidden name="campaign.campaignCoverage" id="campaignCoverage" value='%{campaign.campaignCoverage.id}'/>
	    <s:hidden name="campaign.campaignCoverage.serialNumberCoverage" id="campaignCoverageParam" value='%{campaign.campaignCoverage.serialNumberCoverage.id}'/>
	   
    </table>
    </div>
    <div id="modifyItemData">
   <s:if test="campaign.id != null">
    <div   class="admin_section_div">	
    <div class="admin_section_heading"><s:text name="label.campaign.inventories.addRemove" /></div>
		<table style="width:30%" class="grid">
 		    <tr>
				<td><input type="radio" name="attachOrDelete" value="attach"/></td>
                <td class="labelStyle" nowrap="nowrap"><s:text
					name="label.campaign.addSerialNumbers" /></td>
				<td><input type="radio" name="attachOrDelete" value="delete"/></td>
                <td class="labelStyle" nowrap="nowrap"><s:text name="label.campaign.removeSerialNumbers" /> 
                </td>
		</tr>  
		    </table>
	       </div>
	       </s:if>
	       </div>  
   <div class="admin_section_div">
   <div  id="criteria_sn" style="display:none;">
    <div class="borderTable">&nbsp;</div>
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr class="errorbg">
        <td width="4%"><div align="center"><img src="image/warningsImg.gif" width="16" height="15" /></div></td>
        <td width="6%" class="warningTextbold"><s:text name="label.campaign.note" /></td>
	    <td width="90%" class="warningTextnormal">
		  <s:url id="url" action="download_template" includeParams="none" />
          <s:a href="%{url}"><s:text name="label.campaign.downloadTemplate" /></s:a>
        </td>
	  </tr> 
	</table>
	<table width="100%" cellspacing="0" cellpadding="0" class="grid">  
     <tr>
      <td style="padding-left:8px;"><s:file name="upload" theme="twms" />
       <s:if test="campaign.campaignFor == 'SERIAL_NUMBERS'">
		<s:iterator value="campaign.campaignCoverage.items" status="iter">
			<s:property value="campaign.campaignCoverage.items[%{#iter.index}]" />
		</s:iterator>
	  </s:if></td>
	  </tr>
	</table>
  </div>
  <div  id="criteria_snp" style="display:none;">
   <div class="borderTable">&nbsp;</div>
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr class="errorbg">
        <td width="4%"><div align="center"><img src="image/warningsImg.gif" width="16" height="15" /></div></td>
        <td width="6%" class="warningTextbold"><s:text name="label.campaign.note" /></td>
        <td width="90%" class="warningTextnormal">
		<a id="patternExamples"><s:text name="label.campaign.patternExample"/></a>
        </td>
	    </tr>
	</table>
	<u:repeatTable id="myTable" cssClass="grid borderForTable" 
	 theme="twms" cellspacing="0" cellpadding="0" cssStyle="margin:5px;">
	<thead>
		<tr class="admin_table_header">
			<th class="colHeader" style="width: 360px";><s:text name="label.campaign.patternStart" /></th>
			<th class="colHeader" style="width: 360px";><s:text name="label.campaign.patternEnd" /></th>
			<th class="colHeader" style="width: 360px";><s:text name="label.campaign.patternToApply" /></th>
			<th class="colHeader" style="width: 100px";><s:text name="label.campaign.addSerialNumbers" /></th>
			<th class="colHeader" style="width: 150px";><s:text name="label.campaign.removeSerialNumbers" /></th>
			<th class="colHeader" width="9%"><u:repeatAdd id="adder" theme="twms"   >
				<img id="addProductIcon" src="image/addRow_new.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.common.addRow" />"/>
			</u:repeatAdd></th>
		</tr>
	</thead>	
	<u:repeatTemplate id="mybody" value="campaign.campaignCoverage.rangeCoverage.ranges" index="indx"
		theme="twms" >
		<tr index="#indx">
		<td>
		<s:hidden name="campaign.campaignCoverage.ranges[#indx]"/>
		<s:textfield name="campaign.campaignCoverage.rangeCoverage.ranges[#indx].fromSerialNumber" theme="twms" /></td>
			<td><s:textfield name="campaign.campaignCoverage.rangeCoverage.ranges[#indx].toSerialNumber" theme="twms" /></td>
				<td><s:textfield name="campaign.campaignCoverage.rangeCoverage.ranges[#indx].patternToApply" theme="twms" /></td>
				<td><input type="radio" id="patternAddOperation_#indx" name="campaign.campaignCoverage.rangeCoverage.ranges[#indx].attachOrDelete" value="attach"/></td>
				<td><input type="radio" id="patternRemoveOperation_#indx" name="campaign.campaignCoverage.rangeCoverage.ranges[#indx].attachOrDelete" value="delete"/></td>
		<td><u:repeatDelete id="deleter_#indx" theme="twms">
				<img id="deleteConfiguration" src="image/remove.gif" border="0"	style="cursor: pointer;"
					title="<s:text name="label.common.deleteRow" />"/>
			</u:repeatDelete></td>
		</tr>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
			var operation= '<s:property value="%{campaign.campaignCoverage.rangeCoverage.ranges[#indx].attachOrDelete}"/>';
			  if("attach"==operation)
	        	 {
				    dojo.byId("patternAddOperation_"+#indx).checked = true;
	        	 }
	         else if("delete"==operation)
	        	 {
	        	 dojo.byId("patternRemoveOperation_"+#indx).checked = true;
	        	 }
			});
			</script>
		</u:repeatTemplate>
</u:repeatTable> 
   </div> 
</div>
<script type="text/javascript">
 var campFor= "<s:property value="campaignFor"/>";
toggleFilterCriteria(campFor);
function toggleFilterCriteria(campFor) {
    var coverageId = "<s:property value="campaign.campaignCoverage.id"/>";
    var coverage = new Array(2);
    var divdata=null;
    var backup=null;
    coverage[0] = coverageId;
    if ("SERIAL_NUMBER_RANGES" == campFor) {
		document.forms[0]["campaignFor"][1].checked = true;
		coverage[1] = 'tavant.twms.domain.campaign.CampaignRangeCoverage';
		show('criteria_snp');hide('criteria_sn');
		hide('modifyItemData');
		}else {
		document.forms[0]["campaignFor"][0].checked = true;
		coverage[1] = 'tavant.twms.domain.campaign.CampaignSerialNumberCoverage';
		show('criteria_sn');hide('criteria_snp');
		show('modifyItemData');
		
		}
	/* document.forms[0]["campaign.campaignCoverage.serialNumberCoverage"].value=coverage; */

	elt = dojo.byId("warnings");
	if (elt) {
		elt.style.display = "none";
	}
}
function swapOptions(srcObjName, trgObjName) {
    addSpecCond(document.forms[0][srcObjName], document.forms[0][trgObjName]);
    return false;
}
</script>

<s:if test="campaign.id != null">
	<div align="center">
		<a id="viewAllItems"><s:text name="link.campaign.viewAllItems"/></a>
	</div>
	
	<script type="text/javascript">	    
		dojo.addOnLoad(function() {
			dojo.connect(dojo.byId("viewAllItems"), "onclick", function(event){
				var campaignId = "<s:property value="campaign.id"/>"
				var actionURL = "list_campaign_items.action?id="+campaignId;
			    parent.publishEvent("/tab/open", {label: "View Affected Units", url: actionURL, decendentOf:""});
			});
		});
	</script>

</s:if>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId("patternExamples"), "onclick", function(event){
            var actionURL = "view_pattern_example.action";
            parent.publishEvent("/tab/open", {label:"View Pattern Example", url: actionURL, decendentOf:""});
        });
    });
</script>



