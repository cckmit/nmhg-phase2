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
	<s:form action="displaySyncTrackerRecords" method="post"
		id="searchForm" validate="false">
		<div class="label" align="left">
		<h2><b><s:text name="label.integration.chooseTheSyncType" /></b></h2>
		</div>
		
		<div>
			<li><s:text name="label.claim.claim" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.creditNotification" /></li>
		</div>
		<div>
			<li><s:text name="label.common.customer" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.extWarrantyDebitNotification" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.extWarrantyDebitSubmit" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.installBase" /></li>
		</div>
		<div>
			<li><s:text name="label.common.item" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.oemXRef" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.supplierDebitNotification" /></li>
		</div>
		<div>
			<li><s:text name="supplierDebitSubmit" /></li>
		</div>
		<div>
			<li><s:text name="label.integration.user" /></li>
		</div>
	</s:form>
</u:body>
</html>