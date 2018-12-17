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
<html>
<head>
<s:head theme="twms" />
<script type="text/javascript">
	dojo.require("dojox.layout.ContentPane");
	dojo.addOnLoad(function() {
		
	});
</script>
</head>
<u:body>
	<div id="technicianDetails">
		<div class="admin_section_div">
			<div class="section_header">
				<s:text name="label.common.loginDetails" />
			</div>
			<table class="form">
				<tr>
					<td class="labelStyle"><s:text name="label.dealerUser.login" /></td>
					<td class="labelStyle"><s:text name="login" /></td>
				</tr>
			</table>
		</div>
		<div class="admin_section_div">
			<div class="section_header">
				<s:text name="label.technician.technicianDetails" />
			</div>
			<table class="form">
				<tr class="non_editable">
					<td class="labelStyle"><s:text
							name="label.dealerUser.dateOfHire" /> :</td>
					<td><sd:datetimepicker name='technician.dateOfHire'
							label='Format (yyyy-MM-dd)' value='%{technician.dateOfHire}'
							id='' /></td>
					<td class="labelStyle"><s:text
							name="label.dealerUser.certification.fromDate" /> :</td>
					<td><sd:datetimepicker name='technician.certificationFromDate'
							label='Format (yyyy-MM-dd)'
							value='%{technician.certificationFromDate}' id='' /></td>
				</tr>
				<tr class="non_editable">
					<td class="labelStyle"><s:text
							name="label.dealerUser.dateOfRenewal" /> :</td>
					<td><sd:datetimepicker name='technician.dateOfRenewal'
							label='Format (yyyy-MM-dd)' value='%{technician.dateOfRenewal}'
							id='' /></td>
					<td class="labelStyle"><s:text
							name="label.dealerUser.certification.toDate" /> :</td>
					<td><sd:datetimepicker name='technician.certificationToDate'
							label='Format (yyyy-MM-dd)'
							value='%{technician.certificationToDate}' id='' /></td>
				</tr>
				<tr class="non_editable">
					<td class="labelStyle"><s:text
							name="label.dealerUser.serviceManagerName" /> :</td>
					<td><s:textfield maxlength="20" size="30"
							name="technicianDetails.serviceManagerName" readOnly="true"
							value="%{technician.serviceManagerName}" /></td>
					<td class="labelStyle"><s:text
							name="label.dealerUser.serviceManagerPhone" /> :</td>
					<td><s:textfield maxlength="20" size="30"
							name="technician.serviceManagerPhone" readOnly="true"
							value="%{technician.serviceManagerPhone}" /></td>
				</tr>
				<tr class="non_editable">
					<td class="labelStyle"><s:text
							name="label.dealerUser.serviceManagerAddress" /> :</td>
					<td><t:textarea name="technician.serviceManagerAddress" readOnly="true"
							id="serviceManagerAddress" cssStyle="width: 100%;" rows="2"
							value="%{technician.serviceManagerAddress}" /></td>
					<td class="labelStyle"><s:text name="label.common.comments" />
						:</td>
					<td><t:textarea name="technician.comments" id="comments" readOnly="true"
							cssStyle="width: 100%;" rows="2" value="%{technician.comments}" /></td>
				</tr>

				<tr class="non_editable">
					<td class="labelStyle"><s:text
							name="label.dealerUser.serviceManagerFax" /> :</td>
					<td><s:textfield maxlength="20" size="30" readOnly="true"
							name="technician.serviceManagerFax"
							value="%{technician.serviceManagerFax}" /></td>
					<td class="labelStyle"><s:text
							name="label.dealerUser.serviceManagerEmail" /> :</td>
					<td><s:textfield maxlength="255" size="30" readOnly="true"
							name="technician.serviceManagerEmail"
							value="%{technician.serviceManagerEmail}" /></td>
				</tr>
			</table>
		</div>
		<div id="certification_details" class="admin_section_div">
			<div class="section_header">
				<s:text name="label.technicianCertification.technicianCertificaiton" />
			</div>
			<u:repeatTable id="technician_certification_table" theme="twms"
				cellspacing="4" cellpadding="0" cssStyle="margin:5px;" width="99%">
				<thead>
					<tr class="admin_table_header">
						<th class="colHeader"><s:text name="label.technicianCertification.series" /></th>
						<th class="colHeader"><s:text name="label.technicianCertification.certificationName" /></th>
						<th class="colHeader"><s:text
								name="label.technicianCertification.certificationExpiryDate" /></th>
					</tr>
				</thead>
				<u:repeatTemplate id="certicationBody"
					value="certificationList" index="index" theme="twms">
					<tr index="#index">
						<td valign="top"><s:textfield
								name="certificationList[#index].series"
								value='%{certificationList[#index].series}' readOnly="true"
								id='series_#index' theme="twms" /></td>
						<td valign="top"><s:textfield
								name="certificationList[#index].certificationName"
								value='%{certificationList[#index].certificationName}' readOnly="true"
								id='certificationName_#index' theme="twms" /></td>
						<td valign="top"><s:textfield
								name="certificationList[#index].certificationToDate"
								value='%{certificationList[#index].certificationToDate}' readOnly="true"
								id='certificationToDate_#index' theme="twms" /></td>
					</tr>
				</u:repeatTemplate>
			</u:repeatTable>
		</div>
	</div>
</u:body>
</html>