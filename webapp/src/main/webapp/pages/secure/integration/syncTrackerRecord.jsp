<%@ taglib prefix="s" uri="/struts-tags"%>
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
	<h1><u><s:text name="label.integration.integrationTracker" /></u></h1>
	</div>
	<s:form action="showRecordInfo" method="post" id="detailForm"
		validate="false">
		<div class="label" align="left">
		<h2><b><s:text name="label.integration.record"/></b></h2>
		</div>
		<s:actionerror />
				<table class="grid borderForTable" cellpadding="0" cellspacing="0"
			width="100%">
			<tr>
				<td class="label"><s:text name="label.integration.recordID"/></td>
				<td><s:property value="id" /></td>
				<td class="label"><s:text name="label.integration.syncType"/></td>
				<td><s:property value="syncTracker.syncType" /></td>
			</tr>
			<tr>
				<td class="label"><s:text name="label.common.status"/></td>
				<td><s:property value="syncTracker.status.status" /></td>
				<td class="label"><s:text name="label.integration.createdDate"/></td>
				<td><s:property value="syncTracker.createDate" /></td>
			</tr>
			<tr>
				<td class="label"><s:text name="label.integration.errorType"/></td>
				<td><s:property value="syncTracker.errorType" /></td>
				<td class="label"><s:text name="label.integration.errorMessage"/></td>
				<td><s:property value="syncTracker.errorMessage" /></td>
			</tr>
		</table>

		<div align="center">
			<s:if
				test="syncTracker.syncType == 'Claim' 
				   || syncTracker.syncType == 'ExtWarrantyDebitSubmit' 
				   || syncTracker.syncType == 'SupplierDebitSubmit'">
				   <s:submit cssClass="button" value='<s:text name="label.integration.reprocess"/>' method="reprocess"/>
			</s:if>
					<s:submit cssClass="button" value='<s:text name="label.common.back"/>' action="displaySyncTrackerRecords"/>
		</div>
	</s:form>
</u:body>
</html>