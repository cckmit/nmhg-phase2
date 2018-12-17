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
 <div class="mainTitle" style="margin-top:10px;margin-bottom:5px;">
	<s:text name="label.newClaim.outsidePartsServices"/></div>
<u:repeatTable id="nonoem_part_table" cellspacing="0" width="100%"
	theme="twms" cssClass="admin_entry_table">
	<thead>
		<tr class="admin_table_header">
			<th width="55%"><s:text name="label.common.description" /></th>
			<th width="15%"><s:text name="label.common.quantity" /></th>
			<th width="15%"><s:text name="label.campaign.priceLimit" /></th>
			<th width="2%"><u:repeatAdd id="nonoem_part_adder" theme="twms">
				<div align="center"><img src="image/addRow_new.gif" border="0"
					style="cursor: pointer;" title="<s:text name="label.common.addRow" />" /></div>
			</u:repeatAdd></th>
		</tr>
	</thead>
	<tbody>
	<u:repeatTemplate id="nonoem_parts_table_body"
		value="campaign.nonOEMpartsToReplace" index="indx">
		<tr index="#indx">
			<td width="55%" align="center" style="border-left:1px solid #DCD5CC;">
			<s:hidden name="campaign.nonOEMpartsToReplace[#indx]" />
			
				<s:textfield size="50"
					name="campaign.nonOEMpartsToReplace[#indx].i18nNonOemPartsDescription[0].description" />
				<s:hidden  name="campaign.nonOEMpartsToReplace[#indx].i18nNonOemPartsDescription[0].locale"
					 value="%{getDefaultLocale()}"/>
				<s:if test="campaign.nonOEMpartsToReplace[#indx]!=null">	
					<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				         id="internationalizeCampainDesc_#indx" tagType="a" cssClass="inventory_folder folder"
				         tabLabel="%{getText('label.common.internationalize')}"
				         url="updateNonOemDescription.action?campaign.id=%{campaign.id}&index=#indx"
				          catagory="campaign_info">
			            <s:text name="label.common.internationalize" />
	           		 </u:openTab>
	           	</s:if>
			</td>
			
			<td width="15%" align="center" style="border-left:1px solid #DCD5CC;">
			<s:textfield size="15" id="nonoem.quantity"
				name="campaign.nonOEMpartsToReplace[#indx].noOfUnits" /></td>

			<td width="15%" align="center" style="border-left:1px solid #DCD5CC;">
            <s:if test="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice.size!=0">
                <table>
                <s:iterator value="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice" status="iter">
                  <tr><td><t:money id="unit_price_#indx_%{#iter.index}" 
                  		name="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice[%{#iter.index}].pricePerUnit"
				        value="%{campaignSectionPrice[#iter.index].pricePerUnit}"
				        size="10" defaultSymbol="%{campaignSectionPrice[#iter.index].pricePerUnit.breachEncapsulationOfCurrency()}"/> 
				        <s:hidden name="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice[%{#iter.index}].sectionName"
				        	value="%{@tavant.twms.domain.claim.payment.CostCategory@NON_OEM_PARTS_COST_CATEGORY_CODE}"></s:hidden>
				        </td></tr>
                </s:iterator>
                </table>
            </s:if>
            <s:else>
                <table>
                <s:iterator value="currencies" status="currIter">
                  <tr><td> <t:money id="unit_price_#indx_%{#currIter.index}"
				        name="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice[%{#currIter.index}].pricePerUnit"
				        value="%{campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice[%{#currIter.index}].pricePerUnit}"
				        size="10" defaultSymbol="%{currencyCode}"/> 
				        <s:hidden name="campaign.nonOEMpartsToReplace[#indx].campaignSectionPrice[%{#currIter.index}].sectionName"
				        	value="%{@tavant.twms.domain.claim.payment.CostCategory@NON_OEM_PARTS_COST_CATEGORY_CODE}"></s:hidden>
				        </td></tr>
                </s:iterator>
                </table>
            </s:else>
            </td>

			<td width="2%" style="border-left:1px solid #DCD5CC;"><u:repeatDelete
				id="nonoem_parts_deleter_#indx">
				<div align="center"><img id="deleteConfiguration"
					src="image/remove.gif" border="0" style="cursor: pointer;"
					title="<s:text name="label.common.deleteRow" />" /></div>
			</u:repeatDelete></td>

		</tr>
	</u:repeatTemplate>
	</tbody>
</u:repeatTable>


