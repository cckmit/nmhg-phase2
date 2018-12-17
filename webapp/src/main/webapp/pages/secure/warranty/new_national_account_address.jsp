<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<s:head theme="twms" />
<head>
    <title>FIXME</title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/pushCustomerDetails.js"></script>
    <script type="text/javascript" src="scripts/validateAddress.js"></script>
<script type="text/javascript">
dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");</script>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    
</head>

<body>
<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults/>
	<s:form action="update_national_account.action" method="POST" id="updateNAForm" name="create" validate="true">		
		
			<div id="na_info_title" class="section_heading">
				<s:text name="message.manageCustomer.customerInfo" />
			</div>
			<div id="national_account_info" class="section_div">
			<div id="addrNADiv"><jsp:include flush="true" page="na_address.jsp" />
			</div>
		</div>
	</s:form></div></div>
</body>
</html>