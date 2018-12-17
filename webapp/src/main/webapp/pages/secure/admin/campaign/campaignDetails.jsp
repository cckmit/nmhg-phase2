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

<s:hidden name="campaign" />
<s:hidden name="campaign.code" />
<s:hidden name="campaign.campaignClass.code" />
<s:hidden name="campaign.fromDate" />
<s:hidden name="campaign.tillDate" />
<s:hidden name="campaign.description" />
<s:hidden name="campaignFor" />

<s:if test="campaignFor == 'SERIAL_NUMBER_RANGES'">
	<s:iterator value="campaign.campaignCoverage.ranges" status="iter">
		<s:hidden name="campaign.campaignCoverage.ranges[%{#iter.index}].fromSerialNumber" />
		<s:hidden name="campaign.campaignCoverage.ranges[%{#iter.index}].toSerialNumber" />
	</s:iterator>
</s:if>

<s:if test="campaign.oemPartsToReplace.size() > 0">
	<s:iterator value="campaign.oemPartsToReplace" status="iter">
		<s:hidden name="campaign.oemPartsToReplace[%{#iter.index}].id" />
		<s:hidden name="campaign.oemPartsToReplace[%{#iter.index}].item" />
		<s:hidden name="campaign.oemPartsToReplace[%{#iter.index}].noOfUnits" />
	</s:iterator>
</s:if>

<s:if test="campaign.nonOEMpartsToReplace.size() > 0">
	<s:iterator value="campaign.nonOEMpartsToReplace" status="iter">
		<s:hidden name="campaign.nonOEMpartsToReplace[%{#iter.index}].id" />
		<s:hidden name="campaign.nonOEMpartsToReplace[%{#iter.index}].pricePerUnit" />
		<s:hidden name="campaign.nonOEMpartsToReplace[%{#iter.index}].noOfUnits" />
		<s:hidden name="campaign.nonOEMpartsToReplace[%{#iter.index}].partNumber" />
		<s:hidden name="campaign.nonOEMpartsToReplace[%{#iter.index}].description" />
	</s:iterator>
</s:if>