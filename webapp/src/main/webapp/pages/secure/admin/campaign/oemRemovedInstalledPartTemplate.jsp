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
<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<tr>
	<td>
		<table id="oemReplacedInstalledSection_table" class="grid borderForTable">
			<tr>
				<td class="mainTitle" style="margin-bottom: 5px;"><s:text name="label.claim.installedParts" />
				</td>
			<tr>
			<tr>
				<td><table id="oemInstalledSection_table" class="grid borderForTable">
						<thead>
							<tr class="row_head">
								<td width="10%" align="center" class="partReplacedClass"><s:text name="label.newClaim.partNumber" /></td>
								<td width="10%" align="center" class="partReplacedClass"><s:text name="label.common.quantity" /></td>
								<td width="55%" align="center" class="partReplacedClass"><s:text name="label.common.description" />
								</td>
								<td width="15%" align="center" class="partReplacedClass">Shipped By OEM</td>
								<td width="5%" align="center" class="partReplacedClass"></td>
								<th width="5%" class="section_heading"><div class="nList_add" style="margin-right: 5px" />
							</tr>
						</thead>
						<tbody>
							<u:nList value="installedParts" rowTemplateUrl="getCampaignOemInstalledPartTemplate.action" paramsVar="extraParams">
								<div id="oemInstalledDiv">
									<jsp:include flush="true" page="campaignInstalledPartTemplate.jsp" />
								</div>
							</u:nList>

						</tbody>
					</table>
				</td>
			</tr>
			<tr>
				<td class="mainTitle" style="margin-bottom: 5px;"><s:text name="label.claim.removedParts" />
				</td>
			<tr>
			<tr>
				<td><table id="oemRemovedSection_table" class="grid borderForTable">

						<thead>
							<tr class="row_head">
								<td width="10%" align="center" class="partReplacedClass"><s:text name="label.newClaim.partNumber" /></td>
								<td width="10%" align="center" class="partReplacedClass"><s:text name="label.common.quantity" /></td>
								<td width="30%" align="center" class="partReplacedClass"><s:text name="label.common.description" /></td>
								<td width="10%" align="center" class="partReplacedClass"><s:text name="columnTitle.dueParts.return_location" /></td>
								<td width="20%" align="center" class="partReplacedClass"><s:text name="columnTitle.partReturnConfiguration.paymentCondition" />
								</td>
								<td width="5%" align="center" class="partReplacedClass"><s:text name="label.common.dueDays" /></td>
								<td width="5%" align="center" class="partReplacedClass"></td>
								<th width="5%" class="section_heading"><div class="nList_add" style="margin-right: 5px" />
								</th>
							</tr>
						</thead>
						<tbody>
							<u:nList value="removedParts" rowTemplateUrl="getCampaignOemRemovedPartTemplate.action" paramsVar="extraParams">
								<div id="oemRemovedDiv">
									<jsp:include flush="true" page="campaignRemovedPartTemplate.jsp" />
								</div>
							</u:nList>
						</tbody>
					</table>
		</table>
	</td>
	<td><s:hidden name="%{#nListName}" value="%{id}" id="%{qualifyId(\"removedInstalledPart_Id\")}" />
		<div class="nList_delete" />
	</td>
</tr>
