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
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<%--     <u:stylePicker fileName="adminPayment.css"/> --%>
<u:stylePicker fileName="yui/reset.css" common="true" />
<s:head theme="twms" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="base.css" />
</head>
<u:body>
	<div class="policy_section_div" style="width: 100%;">
		<div id="dcap_pricing_title" class="section_header">
			<s:text name="title.monthEndScheduler" />
		</div>
		<s:form id="generateScheduler" action="month_end_scheduler"
			method="POST" name="form">
			<u:actionResults wipeMessages="false" />
		<br/>
			<div id="xmlToSapHeading" class="section_header">
			<s:text name="title.xmlToSap.heading" />
		</div>
			<table style="height: 120px;">
			
			<%-- <tr style="width: 100%;">
				<td class="section_header" style="width: 100%;"> aa</td>
			</tr> --%>
				<tr align="center">
				<td></td>
					<td class="searchLabel labelStyle" align="left"><s:text
							name="label.monthEndScheduler.run" /></td>
							<td></td>
					<td><input type="radio"
						id="generateScheduler" name="schedulerToRun" value="true" <s:if test="schedulerToRun">checked="checked"</s:if>><s:text name="label.yes"/> 
						<input type="radio" id="generateScheduler" name="schedulerToRun"
						value="false" <s:if test="!schedulerToRun">checked="checked"</s:if>><s:text name="label.no"/></td>
				</tr>
				<tr align="center">
					<td></td>
					<td align="center"><s:submit cssClass="buttonGeneric"
							value="%{getText('button.common.submit')}" /></td>
					<td align="center"><input type="button"
						value='<s:text name="button.common.cancel" />'
						class="buttonGeneric"
						onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"></td>
					
				</tr>
			</table>
				
				<!-- 				Update credit date -->
<%-- <br/>
			<div id="creditDateHeading" class="section_header">
			<s:text name="title.creditDate.heading" />
		</div>
<table style="height: 120px">
	<tr align="center">
				<td></td>
					<td class="searchLabel labelStyle" align="left"><s:text
							name="label.creditDate.update" /></td>
					<td><input type="radio"
						id="creditDateYes" name="updateCreditDate" value="true" <s:if test="updateCreditDate">checked="checked"</s:if>><s:text name="label.yes"/> 
						<input type="radio" id="creditDateNo" name="updateCreditDate"
						value="false" <s:if test="!updateCreditDate">checked="checked"</s:if>><s:text name="label.no"/></td>
						<td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.common.startDate" />:
                </label>
            </td>
             <td>
                <sd:datetimepicker cssStyle='width:135px;align:left;padding-left:0px;' name='cdeditStartDate' label='Format (dd-MMM-yy)' id='startDate' />
            </td>
            <td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.common.endDate" />:
                </label>
            </td>
             <td>
                <sd:datetimepicker cssStyle='width:135px;align:left;padding-left:0px;' name='cdeditEtartDate' label='Format (dd-MMM-yy)' id='endDate' />
            </td>
			
			</tr>
				<tr align="center">
					<td></td>
					<td align="center"><s:submit cssClass="buttonGeneric"
							value="%{getText('button.common.submit')}" action = "update_credit_date"/></td>
					<td align="center"><input type="button"
						value='<s:text name="button.common.cancel" />'
						class="buttonGeneric"
						onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"></td>
						
						
						<td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.common.updateDate" />:
                </label>
            </td>
            <td>
                <sd:datetimepicker cssStyle='width:135px;align:left;padding-left:0px;' name='dateToUpdate' label='Format (dd-MMM-yy)' id='dateToUpdate' />
            </td>
				</tr>
</table>
			

			<!-- END -->
			 --%>
<!-- 				SLMSPROD-239 Changes (WPRA Scheduler)-->
<br/>
			<div id="wpraHeading" class="section_header">
			<s:text name="title.wpraSchedular.heading" />
		</div>
<table style="height: 120px">
<tr></tr>
	<tr align="center">
				<td></td>
					<td class="searchLabel labelStyle" align="left"><s:text
							name="label.wpraScheduler.run" /></td>
							<td></td>
					<td><input type="radio"
						id="generateWpraScheduler" name="wpraSchedulerToRun" value="true" <s:if test="wpraSchedulerToRun">checked="checked"</s:if>><s:text name="label.yes"/> 
						<input type="radio" id="generateWpraSchedulerNo" name="wpraSchedulerToRun"
						value="false" <s:if test="!wpraSchedulerToRun">checked="checked"</s:if>><s:text name="label.no"/></td>
						<td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.common.startDate" />:
                </label>
            </td>
             <td>
                <sd:datetimepicker cssStyle='width:135px;align:left;padding-left:0px;' name='startDate' label='Format (dd-MMM-yy)' id='wpraStartDate' />
            </td>
            <td width="15%">
                <label for="repairEndDate" class="labelStyle">
                    <s:text name="label.common.endDate" />:
                </label>
            </td>
             <td>
                <sd:datetimepicker cssStyle='width:135px;align:left;padding-left:0px;' name='endDate' label='Format (dd-MMM-yy)' id='wpraEndDate' />
            </td>
			
			</tr>
				<tr align="center">
					<td></td>
					<td align="center"><s:submit cssClass="buttonGeneric"
							value="%{getText('button.common.submit')}" action = "wpra_Schedular"/></td>
					<td align="center"><input type="button"
						value='<s:text name="button.common.cancel" />'
						class="buttonGeneric"
						onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"></td>
				</tr>

			<!-- 		End of	SLMSPROD-239 Changes -->	
				
			</table>


		</s:form>
	</div>
</u:body>
<authz:ifPermitted resource="accountsMonthEndSchedulerReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('generateScheduler')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('generateScheduler'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
