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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="t" uri="twms" %>
<style>
td{
padding-bottom:10px;
}
</style>

<div class="admin_section_div">
	<div class="admin_section_heading"><s:text name="label.campaign.info"/></div>
	<s:hidden id="campaign.id" name="campaign.id" value="%{campaign.id}"/>
	
	<table>
		<tr>
			<td width="20%" nowrap="nowrap"><label for="campaign_code" class="labelStyle"><s:text name="label.campaign.code" />:</label></td>
			<td width="25%">
				<s:textfield name="campaign.code" id="campaign_code" />
			</td>
			<td width="20%" nowrap="nowrap"><label for="campaign_class" class="labelStyle"><s:text name="label.campaign.classCode" />:</label></td>
			<td width="40%">
				<s:select list="campaignClasses" name="campaign.campaignClass" 
					id="campaign_class" listKey="code" listValue="description" 
					value="%{campaign.campaignClass.code}" 
					emptyOption="true" theme="twms"/>
			</td>
		</tr>
		<tr>
			<td nowrap="nowrap">
                <label for="campaign_fromdate" class="labelStyle">
                    <s:text name="label.common.startDate" />:
                </label>
            </td>
            
            <td style="padding-left:5;">
           
                <sd:datetimepicker name='campaign.fromDate' value='%{campaign.fromDate}' id='campaign_fromdate' />
                
            </td>
            <td nowrap="nowrap">
                <label for="campaign_tilldate" class="labelStyle">
                    <s:text name="label.common.endDate" />:
                </label>
            </td>
            <td>
                <sd:datetimepicker name='campaign.tillDate' value='%{campaign.tillDate}' id='campaign_tilldate' />
            </td>
		</tr>
		<tr>
		   <td width="20%" nowrap="nowrap">
		       <label for="campaign_budget" class="labelStyle"><s:text name="label.campaign.budgetedAmount" />:</label>
		   </td>
		   <td width="25%">
		    $<s:textfield name="campaign.budgetedAmount" id="campaign_budget"/> 
		     </td>
		    </tr>
		<tr>
		
            <td height="19" colspan="2" nowrap="nowrap" class="labelStyle">
                <s:text name="label.common.campaignDescriptionInUS"/>:
                <br/>
                <s:if test="campaign.id != null">
	                <s:iterator value="campaign.i18nCampaignTexts" status="itr">
	                	<s:if test="locale.equals(defaultLocale)">
							<t:textarea id="campaignDesc" cols="40" rows="3" maxLength="3990" name="campaign.i18nCampaignTexts[%{#itr.index}].description"/>
							<s:hidden name="campaign.i18nCampaignTexts[%{#itr.index}].locale" value="%{defaultLocale}"/>
	                	</s:if>
	                </s:iterator>
				</s:if>
				<s:else>
			    	<t:textarea id="campaignDesc" cols="40" rows="3" maxLength="3990" name="campaign.i18nCampaignTexts[0].description"/>
					<s:hidden name="campaign.i18nCampaignTexts[0].locale" value="%{defaultLocale}"/>
				</s:else>
			</td> 	
        </tr>
                
	</table>
</div>
