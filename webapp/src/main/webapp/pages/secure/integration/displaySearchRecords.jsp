<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<title><s:text name="label.integration.integrationTracker"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="claimForm.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="multiCar.css" />
<s:head theme="twms" />

<style type="text/css">
table.form {
	          margin-bottom: 5px;
            border: 1px solid #EFEBF7;
			background-color:#F3FBFE;
			margin-left:5px;
			width:99%;
}

label {
	color: #000000;
	font-weight: 400;
}
</style>

</head>
<u:body>

	<div align="center">
	<h1><u><s:text name="label.integration.integrationTracker"/></u></h1>
	</div>
	<s:form action="displaySyncTrackerRecords" method="post"
		id="searchForm" validate="false">
		<div class="label" align="left">
		<h2><b><s:text name="label.integration.enterSearchCriteria"/></b></h2>
		</div>
		<s:actionerror />
		<table class="grid borderForTable"  cellpadding="0" cellspacing="0"
			width="100%">
			<tr>
				<td class="label"><s:text name="label.integration.syncType"/></td>
				<td><s:select name="syncType" list="syncTypeOptions"
					theme="twms" required="false" emptyOption="true" /></td>
				<td class="label"><s:text name="label.common.status"/></td>
				<td><s:select name="status" list="statusOptions" theme="twms"
					required="false" emptyOption="true" /></td>
			</tr>
			<tr>
				<td class="label"><s:text name="label.integration.fromDate"/></td>
				<td><sd:datetimepicker name='fromDate' value='%{fromDate}' id='fromDate' required='false' /></td>
				<td class="label"><s:text name="label.integration.toDate"/></td>
				<td><sd:datetimepicker name='toDate' value='%{toDate}' id='toDate' required='false' /></td>
			</tr>
		</table>
		<div align="justify"><s:submit value='<s:text name="button.common.search"/>' cssClass="button" />
		</div>
	</s:form>

	<table cellpadding="0" cellspacing="0" class="grid bordeForTable">
		<thead>
			<tr class="row_head">
				<th><s:text name="label.integration.syncType" /></th>
				<th><s:text name="label.integration.uniqueIDName" /></th>
				<th><s:text name="label.integration.uniqueIDValue" /></th>
				<th><s:text name="label.common.status" /></th>
				<th><s:text name="label.integration.startTime" /></th>
				<th><s:text name="label.integration.numberOfAttempts" /></th>
				<th><s:text name="label.integration.lastUpdatedDate" /></th>
			</tr>
		</thead>
		<tbody>
			<s:if
				test="syncTrackerRecords == null or syncTrackerRecords.isEmpty()">
				<tr>
					<td colspan="8"><s:text name="label.integration.noRecordsFound" />'</td>
				</tr>
			</s:if>
			<s:else>
				<s:iterator value="syncTrackerRecords" status="status">
					<tr>
						<td>
							<a href="showRecordInfo.action?id=<s:property value='id'/>">
								<s:property value="syncType" />
							</a>	
						</td>
						<td><s:property value="uniqueIdName" /></td>
						<td><s:property value="uniqueIdValue" /></td>
						<td><s:property value="status.status" /></td>
						<td><s:property value="startTime" /></td>
						<td><s:property value="noOfAttempts" /></td>
						<td><s:property value="updateDate" /></td>
					</tr>
				</s:iterator>
			</s:else>
		</tbody>
	</table>
</u:body>
</html>