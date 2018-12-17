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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title><s:text name="title.common.warranty" /></title>
<s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css" />

<script type="text/javascript" src="scripts/RepeatTable.js"></script>
<script type="text/javascript" src="scripts/AdminToggle.js"></script>
<script type="text/javascript"
	src="scripts/adminAutocompleterValidation.js"></script>

</head>
<u:body>
	<s:form name="baseForm" id="baseFormId" method="post">
		<u:actionResults />
		<s:hidden name="id" id="blahblah" />
		<div class="policy_section_div">
		<div class="policy_section_heading"><s:text
			name="title.alarmcode.add" /></div>

		<table border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<td class="labelStyle" nowrap="nowrap" width="13%"><s:text
					name="columnTitle.common.FullCode" /> :</td>
				<td width="87%"><s:textfield name="alarmCode.code" cssStyle="width:500px"
					value="%{alarmCode.code}" /></td>
			</tr>
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
					name="columnTitle.common.description" /> :</td>
				<td><s:textarea name="alarmCode.description" cssClass="textarea" cols="100" rows="4" cssStyle="width:500px;"
					value="%{alarmCode.description}" /></td>
			</tr>
		</table>
		<div class="admin_section_div">
		<div class="policy_section_heading"><s:text
			name="label.managePolicy.addProduct" /></div>
		<u:repeatTable id="myTable" cssClass="borderForTable"
			cellpadding="0" cellspacing="0" cssStyle="margin:5px; width:58%; *width:57%" theme="simple">
			<thead>
				<tr class="title">
					<th width="48%" class="colHeader"><s:text
						name="columnTitle.common.products" /></th>
					<th width="10%" class="colHeader"><div align="center"><u:repeatAdd id="alarm_codes_adder"
						theme="simple">
						<img id="addProducts" src="image/addRow_new.gif" border="0"
							style="cursor: pointer; padding-right: 4px;"
							title="<s:text name="label.managePolicy.addProduct" />" />
					</u:repeatAdd></div></th>
				</tr>
			</thead>
			<u:repeatTemplate id="alarm_codes_mybody" value="alarmCode.applicableProducts"
				index="myindex" theme="twms">
				<tr index="#myindex">
					<td style="border:1px solid #EFEBF7; padding:5px 0;">
					<sd:autocompleter id='products_#myindex_Id' showDownArrow='false' href='list_products_name_value_id.action' cssStyle='width:200px' name='alarmCode.applicableProducts[#myindex]' keyName='alarmCode.applicableProducts[#myindex]' value='%{alarmCode.applicableProducts[#myindex].name}' key='%{alarmCode.applicableProducts[#myindex].id}' />

					<td style="border: 1px solid #EFEBF7;" align="center"><u:repeatDelete
						id="alarm_codes_deleter_#myindex" theme="simple">
						<img id="deletePrice" src="image/remove.gif" border="0"
							style="cursor: pointer; padding-right: 4px;"
							title="<s:text name="label.managePolicy.deleteProduct" />" />
					</u:repeatDelete><s:hidden name="alarmCode.applicableProducts[#myindex]"></s:hidden>
					</td>
				</tr>
			</u:repeatTemplate>
		</u:repeatTable></div>
		<div align="center" style="margin:10px 0; width:58%;"><s:submit
			id="closeTab" value="Cancel" cssClass="buttonGeneric" action="" /> <script
			type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			                 
			    });
			</script> <s:submit value="Submit" cssClass="buttonGeneric"
			action="saveAlarmCodes" /></div>
	</s:form>
	</div>
</u:body>
</html>
