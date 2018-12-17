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
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"></s:head>
    <u:stylePicker fileName="adminPayment.css"/>
	 <u:stylePicker fileName="common.css"/>
	  <u:stylePicker fileName="base.css"/>
	  <u:stylePicker fileName="ui-ext/tst/SummaryTableTag.css"/>
    <script type="text/javascript" src="scripts/admin.js"></script>
	    <script type="text/javascript">
    	dojo.addOnLoad(function(){
        	dojo.subscribe("/campaignItems/populateCriteria",null,function(data){
            	data.returnTo(data.url + '?campaignId=<s:property value="campaign.id"/>');
            });
    	});
    </script>
<script type="text/javascript">
    dojo.require("dijit.Tooltip")
</script>
</head>
<style>
body{
overflow-y: auto !important; 
}
</style>
<u:body >
<div class="admin_section_div" style="margin:5px;">
	<div class="admin_section_heading">
		<s:text name="label.common.summary"></s:text>
	</div>
	<table class="grid" cellspacing="0"
		cellpadding="2" align="center" style="margin:10px 0px 10px 0px;width:99%" >
		<tr class="row_head">
			<td width="20%" nowrap="nowrap" class="labelStyle">
			<span
					id="one" tabindex="0"> <s:text
							name="label.total.serial.numbers.affected" />
				</span> : <span dojoType="dijit.Tooltip" connectId="one"> <s:text
							name="label.campaign.tooltip.serial.numbers.affected" />
				</span></td>
				<td>
					<s:if test="campaign.isNotificationsGenerated() == null or !campaign.isNotificationsGenerated()">
											<s:text name="label.common.numberZero"></s:text>
		</s:if>
		<s:else>
					<s:property value="campaignNotifications.size()"/>
					</s:else>
			</td>
			<td width="20%" nowrap="nowrap"  class="labelStyle">
				<span
					id="two" tabindex="0"> <s:text
							name="label.total.serial.numbers.fixed" />
				</span> : <span dojoType="dijit.Tooltip" connectId="two"> <s:text
							name="label.campaign.tooltip.serial.numbers.fixed" />
				</span></td>
			<td>
				<s:if test="campaign.isNotificationsGenerated() == null or !campaign.isNotificationsGenerated()">
					<s:text name="label.common.numberZero"></s:text>
				</s:if>
				<s:else>
					<s:property value="campaignStatistics.totalSerialNumberFixed"/>
				</s:else>
			</td>
			<td nowrap="nowrap"  class="labelStyle">
			<span
					id="three" tabindex="0"> <s:text
							name="label.campaign.percentageFixed" />
				</span> : <span dojoType="dijit.Tooltip" connectId="three"> <s:text
							name="label.campaign.tooltip.percentageFixed" />
				</span></td>
			<td> 
						<s:if test="campaign.isNotificationsGenerated() == null or !campaign.isNotificationsGenerated()">
											<s:text name="label.common.numberZero"></s:text>%
						</s:if>
						<s:else>
						<s:property value="campaignStatistics.unitsPercentageFixed"/>%
						</s:else>
			</td>
		</tr>			
		<tr>
			<td width="20%" nowrap="nowrap"  class="labelStyle">
			<span
					id="four" tabindex="0"> <s:text
							name="label.campaign.claimsAccepted" />
				</span> : <span dojoType="dijit.Tooltip" connectId="four"> <s:text
							name="label.campaign.tooltip.claimsAccepted" />
				</span></td>
			<td>
					<s:property value="campaignStatistics.totalClaimsAccepted"/>
			</td>
			<td width="20%" nowrap="nowrap"  class="labelStyle">
			<span
					id="five" tabindex="0"> <s:text
							name="label.campaign.acceptancePercentage" />
				</span> : <span dojoType="dijit.Tooltip" connectId="five"> <s:text
							name="label.campaign.tooltip.acceptancePercentage" />
				</span></td>
			<td>
					<s:property value="campaignStatistics.claimsAcceptancePercentage"/>%
			</td>
			<td nowrap="nowrap"  class="labelStyle">
				<span
					id="six" tabindex="0"> <s:text
							name="label.campaign.amountPaid" />
				</span> : <span dojoType="dijit.Tooltip" connectId="six"> <s:text
							name="label.campaign.tooltip.amountPaid" />
				</span></td>
			
			<td>
				<s:property value="campaignStatistics.amountPaidForCompletedItems"/>
			</td>
		</tr>
		<tr>
					<td width="20%" nowrap="nowrap"  class="labelStyle">
					<span
					id="seven" tabindex="0"> <s:text
							name="label.campaign.unitsCompleted" />
				</span> : <span dojoType="dijit.Tooltip" connectId="seven"> <s:text
							name="label.campaign.tooltip.unitsCompleted" />
				</span></td>
		<td>
					<s:property value="campaignStatistics.unitsCompleted"/>
			</td>
						<td width="20%" nowrap="nowrap"  class="labelStyle">
							<span
					id="eight" tabindex="0"> <s:text
							name="label.campaign.unitsCompletedPercent" />
				</span> : <span dojoType="dijit.Tooltip" connectId="eight"> <s:text
							name="label.campaign.tooltip.unitsCompletedPercent" />
				</span></td>
			<td>
					<s:property value="campaignStatistics.unitsCompletedPercentage"/>%
			</td>
			
		</tr>
			<tr>
				<td width="20%" nowrap="nowrap" class="labelStyle">
				<span
					id="nine" tabindex="0"> <s:text
							name="label.campaign.unitsRemaining" />
				</span> : <span dojoType="dijit.Tooltip" connectId="nine"> <s:text
							name="label.campaign.tooltip.unitsRemaining" />
				</span></td>
				<td><s:property value="campaignStatistics.unitsRemaining" /></td>
				<td width="20%" nowrap="nowrap" class="labelStyle"><span
					id="ten" tabindex="0"> <s:text
							name="label.campaign.unitsRemainingPercent" />
				</span> : <span dojoType="dijit.Tooltip" connectId="ten"> <s:text
							name="label.campaign.tooltip.unitsRemainingPercent" />
				</span></td>
				<td><s:property
						value="campaignStatistics.unitsRemainingPercentage" />%</td>

			</tr>
			<tr>
				<td width="20%" nowrap="nowrap" class="labelStyle">
				<span
					id="eleven" tabindex="0"> <s:text
							name="label.campaign.unitsUnavailable" />
				</span> : <span dojoType="dijit.Tooltip" connectId="eleven"> <s:text
							name="label.campaign.tooltip.unitsUnavailable" />
				</span></td>
				<td><s:property value="campaignStatistics.unitsUnavailable" /></td>
				<td width="20%" nowrap="nowrap" class="labelStyle"><span
					id="twleve" tabindex="0"> <s:text
							name="label.campaign.unitsUnavailablePercent" />
				</span> : <span dojoType="dijit.Tooltip" connectId="twleve"><s:text
							name="label.campaign.tooltip.unitsUnavailablePercent" />
				</span></td>
				<td><s:property
						value="campaignStatistics.unitsUnavailablePercentage" />%</td>

			</tr>
			<tr>
			<td nowrap="nowrap"  class="labelStyle"><s:text name="label.common.status"></s:text>:</td>
			<td>
				<s:property value="campaignCurrentStatus"/>
			</td>
		</tr>
	</table>	
	</div>
	<div class="policy_section_div">
	<div class="admin_section_heading">
		<s:text name="label.campaign.items"/>
	</div>
<s:if test='campaign.campaignCoverage.items.size()>0'>
	<div align="right" class="spacingAtTop">
		<script type="text/javascript">
	    	function exportToExcel(){
	        	exportExcel("/campaignItems/populateCriteria","exportAllCampaignNotification.action",<s:property value="campaign.campaignCoverage.items.size"/>);
	        }
        </script>
		<s:form name="exportAllCampaignNotification" method="post" id="exportAllCampaignNotificationId" 
					action="exportAllCampaignNotification.action">
			<s:hidden name="campaignId" value="%{campaign.id}"></s:hidden>
			<t:button cssClass="buttonGeneric" id="exportToExcel" onclick="exportToExcel" 
				label="%{getText('button.common.downloadToExcel')}"
				value="%{getText('button.common.downloadToExcel')}"/>
		</s:form>
	</div>
	</s:if>
	<jsp:include flush="true" page="../../../common/ExcelDowloadDialog.jsp"></jsp:include>
	<table class="grid borderForTable"  cellspacing="0"
		cellpadding="0" align="center" style="margin:10px 0px 10px 0px;width:99%">
		<tr class="row_head">
			<th width="25%"><s:text name="label.common.serialNumber"/></th>
			<th width="25%"><s:text name="label.common.description"/></th>
			<th width="25%"><s:text name="label.common.make"/></th>
			<th width="25%"><s:text name="label.common.model"/></th>
			<th width="25%"><s:text name="label.common.policyCustomerName"/></th>
			<th width="25%"><s:text name="columnTitle.common.dealerName"/></th>
			<th width="25%"><s:text name="label.common.truckStatus"/></th>
			<th width="25%"><s:text name="label.warrantyAdmin.campaignComplete"/></th>
		</tr>
		<s:if test="campaign.isNotificationsGenerated() == null or !campaign.isNotificationsGenerated() or campaign.campaignCoverage.items.size()>0">
		  <s:set name="setCampaignStatus" value="campaignCurrentStatus"/> 
		<s:bean name="tavant.twms.domain.campaign.SerialNumberComparator" var="byNumber"/>
         <s:sort comparator="#byNumber" source="campaign.items">
			<s:iterator status="status">
				<tr>
					<td><s:property value="serialNumber" /></td>
					<td><s:property
						value="ofType.description" /></td>
					<td><s:property
						value="ofType.make" /></td>
					<td><s:property
						value="ofType.model.name" /></td>		 
							<td>
								<s:if test="type.type=='RETAIL'">
									<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_customer_info_%{#status.index}" tabLabel="Show Customer"
										url="show_customer_information.action?customerInformationToBeDisplayed=%{latestBuyer}"
										catagory="customerInformation">
										<s:property value="latestBuyer.name" />
									</u:openTab>
								</s:if>
							</td>
							<td>
							 <s:if test="type.type=='RETAIL'">
								<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_dealer_info_%{#status.index}" tabLabel="Show Dealer"
										url="show_dealer_information.action?dealerInformationToBeDisplayed=%{currentOwner}"
										catagory="dealerInformation">
										<s:property value="currentOwner.name" />
									</u:openTab>
								</s:if>
								 <s:if test="type.type=='STOCK'">
								<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_dealer_info_%{#status.index}" tabLabel="Show Dealer"
										url="show_dealer_information.action?dealerInformationToBeDisplayed=%{currentOwner}"
										catagory="dealerInformation">
										<s:property value="currentOwner.name" />
									</u:openTab>
								</s:if>		
							</td>
												<td><s:property
					value="campaign.getCampaignStatusToDisplay(serialNumber)" /></td>
					<td>
					      <s:property
						value="%{campaign.getNotificationStatusTODisplay(serialNumber)}"/>
					</td>		
				</tr>
			</s:iterator>
			</s:sort>
		</s:if>
		<s:else>
		<s:bean name="tavant.twms.web.admin.campaign.SerialNumberComparator" var="bySerialNumber"/>
         <s:sort comparator="#bySerialNumber" source="campaign.items">
			<s:iterator status="status">
				<tr>
					<td><s:property value="item.serialNumber" /></td>
					<td><s:property
						value="item.ofType.description" /></td>
					<td><s:property
						value="item.ofType.make" /></td>
					<td><s:property
						value="item.ofType.model.name" /></td>		
					<td>
					  <s:if test="item.type.type=='RETAIL'">
									<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_customer_info_%{#status.index}" tabLabel="Show Customer" cssClass="link"
										url="show_customer_information.action?customerInformationToBeDisplayed=%{item.latestBuyer}"
										catagory="customerInformation">
										<s:property value="item.latestBuyer.name" />
									</u:openTab>
								
					  </s:if>
					  </td>
					  <td>
					    <s:if test="item.type.type=='RETAIL'">
					    	<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_dealer_info_%{#status.index}" tabLabel="Show Dealer" cssClass="link"
										url="show_dealer_information.action?dealerInformationToBeDisplayed=%{item.getRetailedDealer()}"
										catagory="dealerInformation">
										<s:property value="item.getRetailedDealer().name" />
							</u:openTab>
					    </s:if>
					  <s:if test="item.type.type=='STOCK'">
									<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
										id="show_dealer_info_%{#status.index}" tabLabel="Show Dealer"
										url="show_dealer_information.action?dealerInformationToBeDisplayed=%{item.currentOwner}"
										catagory="dealerInformation">
										<s:property value="item.currentOwner.name" />
									</u:openTab>
					  </s:if>
					  </td>	
					  					<td><s:property
 						value="campaign.getCampaignStatusToDisplay(item.serialNumber)" /></td> 			
					<td><s:property
						value="getNotificationStatusForAdmin()" /></td>		
				</tr>
				
			</s:iterator>
			</s:sort>
		</s:else>
	</table>
	
	</div>
	
</u:body>
</html>
