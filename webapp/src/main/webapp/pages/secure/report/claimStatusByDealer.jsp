<%--

   Copyright (c)2006 Tavant Technologies All Rights Reserved.

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
<s:if test="reportType == 'claimStatusByDealer'">
<script type="text/javascript">
	dojo.require("twms.widget.Select");
	var dealerCheckBox = null;
	var GLOBAL_VARS = {
		dealerCheckBox : null,
		dealerGroupCheckBox:null
	};
	dojo.addOnLoad(function() {
		GLOBAL_VARS.dealerCheckBox = dojo.byId("dealer");
		GLOBAL_VARS.dealerGroupCheckBox = dojo.byId("dealerGroup");
		dojo.connect(deleteAllDealers,"onclick",removeDealers);
		dojo.connect(deleteAllDealerGroups,"onclick",removeDealerGroups);
		
		//dojo.connect(searchDealers,"onclick",showSearchDealersDialog);
		//dojo.connect(searchDealerGroups,"onclick",showSearchDealerGroupsDialog);
	})
	
</script>
<div class="snap">
<div id="separator"></div>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
	<tr>
		<td colspan="4" class="sectionTitle"><s:text name="label.report.description"/></td>
	</tr>
	<tr>
		<td colspan="4" nowrap="nowrap" class="labelNormalTop"><s:text name="label.report.claimStatusDescription"/></td>
	</tr>
</table>
<div id="separator"></div>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor">
	<tr>
		<td colspan="4" class="sectionTitle"><s:text name="label.report.specifyReportParameters"/></td>
	</tr>
	<tr>
		<td colspan="4" nowrap="nowrap" height="3"></td>
	</tr>
	<tr>
		<td colspan="4" nowrap="nowrap" class="label">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="47%" valign="top">
					<table  width="100%" border="0" cellspacing="0" cellpadding="0" style="border:1px dotted #E8DDCA">
					<tbody id="dealersTable">
						<tr>
						<td width="5%">
						<input type="radio" name="optionSelected" value="dealers" id="dealer" onclick="removeDealerGroups()"
							<s:if test="optionSelected!='dealerGroups'">checked="checked"</s:if>/>
						</td>						
				        <td width="95%" class="label"><s:text name="label.report.dealer" />
						<img border="0" src="image/search_logo.gif" style="cursor:pointer;" id="searchDealers" onclick="showSearchDealersDialog()" width="21" height="19" align="absmiddle"/>
						</td>
						<td width="5%">
						<div id="dealersTableDiv2"></div>
						<div id="dealersTableDiv1">
						<img src="image/removeAll.gif" alt="Remove All" style="cursor:pointer;" id="deleteAllDealers" width="16" height="16"/>
						</div>
						</td>
						</tr>
					</tbody>
					</table>
					</td>
					<td width="3%">&nbsp;
					</td>
					<td width="47%" valign="top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" style="border:1px dotted #E8DDCA">
					<tbody id="dealerGroupsTable">
						<tr>
						<td width="5%">
						<input type="radio" name="optionSelected" value="dealerGroups" id="dealerGroup" onclick="removeDealers()"
							<s:if test="optionSelected=='dealerGroups'">checked="checked"</s:if> />
						</td>
				        <td width="95%" class="label"><s:text name="label.report.dealerGroups" />
						<img border="0" src="image/search_logo.gif" style="cursor:pointer;"  onclick="showSearchDealerGroupsDialog()" id="searchDealerGroups" width="21" height="19" 
						align="absmiddle" />
						</td>
						<td width="5%">
						<div id="dealerGroupsTableDiv2"></div>
						<div id="dealerGroupsTableDiv1">
						<img src="image/removeAll.gif" alt="Remove All" style="cursor:pointer;" id="deleteAllDealerGroups" width="16" height="16"/>
						</div></td>
						</tr>
					</tbody>
					</table>
					</td>
					<td width="1%">&nbsp;
					</td>
			</tr>
			</table>
		</td>
	</tr>
	   <tr>
			<td width="8%" class="label"><s:text name="label.common.from" /></td>
			<td width="20%">
			 <sd:datetimepicker name='reportSearchCriteria.startDate' value='%{reportSearchCriteria.startDate}' id='startDate' />
			</td>
			<td width="5%" class="label"><s:text name="label.common.to" /></td>
			<td width="67%">
			<sd:datetimepicker name='reportSearchCriteria.endDate' value='%{reportSearchCriteria.endDate}' id='endDate' />
			</td>
		</tr>
	    	 <tr>
        	  <td class="label"><s:text name="label.report.format" /></td>
	          <td>
	          <s:select label="label" name="taskName"
	          				list="reportTypes" required="true"/></td>
	          <td class="label">&nbsp;</td>
	          <td>&nbsp;</td>
	        </tr>
	  
	</table>
	<div class="buttonWrapperPrimary">
	<s:submit cssClass="buttonGeneric" action="claimStatusReport" value="%{getText('button.report.generate')}"/>
	</div>
</div>
</s:if>
