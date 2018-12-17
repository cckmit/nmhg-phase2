<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><s:text name="pageTitle.updateProfile"></s:text></title>
<s:head theme="twms" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="warrantyForm.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="base.css" />

</head>

<u:body>
	<u:actionResults />
	<div dojoType="LayoutContainer"
		style="width: 100%; height: 100%; background: white;" align="center">
	<div id="separator"></div>
	<s:form action="update_dealerusers" theme="twms" validate="true">
		<s:hidden name="dealership" value="%{dealership.id}" />
		<u:repeatTable id="dealer_users_table" cssClass="repeat">
			<table class="grid borderForTable" align="center" style="margin: 5px; width: 90%;">
				<thead>
					<tr class="title">
						<th colspan="7"><s:text name="label.dealerUser.management" /></th>
					</tr>
					<tr class="row_head">
						<th width="17%"><s:text name="label.dealerUser.firstName" /></th>
						<th width="17%"><s:text name="label.dealerUser.lastName" /></th>
						<th width="10%"><s:text
							name="label.dealerUser.claimSubmitter" /></th>
						<th width="10%"><s:text name="label.dealerUser.salesPerson" /></th>
						<th width="10%"><s:text name="label.dealerUser.technician" /></th>
						<th rowspan="2" width="5%"><u:repeatAdd id="user_adder">
							<div class="repeat_add" />
						</u:repeatAdd></th>
					</tr>
				</thead>
				<u:repeatTemplate id="dealer_users_body"
					value="dealership.dealerUsers">
					<tr index="#index">
						<td><s:hidden name="dealership.dealerUsers[#index]" /> <s:textfield
							name="dealership.dealerUsers[#index].firstName"
							value="%{dealership.dealerUsers[#index].firstName}" /></td>
						<td><s:textfield
							name="dealership.dealerUsers[#index].lastName"
							value="%{dealership.dealerUsers[#index].lastName}" /></td>
						<td align="center"><s:checkbox
							name="dealership.dealerUsers[#index].claimSubmitter"
							fieldValue="true"
							value="%{dealership.dealerUsers[#index].claimSubmitter}" /></td>
						<td align="center"><s:checkbox
							name="dealership.dealerUsers[#index].salesPerson"
							fieldValue="true"
							value="%{dealership.dealerUsers[#index].salesPerson}" /></td>
						<td align="center"><s:checkbox
							name="dealership.dealerUsers[#index].technician"
							fieldValue="true"
							value="%{dealership.dealerUsers[#index].technician}" /></td>
						<td><u:repeatDelete id="user_deleter_#index">
							<div class="repeat_del" />
						</u:repeatDelete></td>
					</tr>
				</u:repeatTemplate>
				</u:repeatTable>
			</table>
			<br>
			<div id="submit" align="center"><input id="submit_btn"
				class="buttonGeneric" type="submit"
				value="<s:text name='button.common.update'/>" /><input
				id="cancel_btn" class="buttonGeneric" type="button"
				value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" /></div></div>
	</s:form>
</u:body>
</html>