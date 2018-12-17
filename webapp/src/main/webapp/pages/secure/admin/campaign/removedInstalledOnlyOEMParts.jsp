<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<table id="oemRemovedInstalledSection_table" class="grid borderForTable">
	<thead>
		<tr>
			<th width="95%" class="mainTitle" style="margin-bottom:5px;">
			<s:text
				name="label.newClaim.oEMPartReplacedInstalled" />
			</th>
			<th width="5%" class="section_heading">
			
			<div class="nList_add" style="margin-right:5px"/>
			</th>
		</tr>
	</thead>
		<s:hidden id="paymentLength" value="%{paymentConditions.size()+1}"/>	
		<s:hidden id="rowIndex" value="%{rowIndex}" />
		<s:hidden id="locale" value="%{defaultLocale}" />	
		<s:hidden id="paymentConditionscode_0" value="" />
		<s:hidden id="paymentConditionsdesc_0" value="" />
		<s:iterator value="paymentConditions" status="paymentStatus" >
		<s:hidden id="paymentConditionscode_%{#paymentStatus.index+1}" value="%{code}" />
		<s:hidden id="paymentConditionsdesc_%{#paymentStatus.index+1}" value="%{description}" />
	</s:iterator>
	<tbody>	
	<script>
	var extraParams = {
			
		};
	
	</script>
		<u:nList value="campaign.hussPartsToReplace"
			rowTemplateUrl='getCampaignOemRemovedInstalledPartTemplate.action' paramsVar="extraParams">	
			<div id="oemRemovedInstalledDiv">		
			<jsp:include flush="true" page="oemRemovedInstalledPartTemplate.jsp" />	
			</div>		
		</u:nList>		
	</tbody>
</table>