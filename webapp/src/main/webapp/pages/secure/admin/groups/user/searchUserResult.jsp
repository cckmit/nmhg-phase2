
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr width="100%">
		<td colspan="5">
		<div class="admin_section_subheading"><s:text
			name="label.manageProfile.userDetails" /></div>


		</td>
	</tr>
	<tr width="100%">
		<td width="25%" class="labelBold"><s:text
			name="label.dealerUser.firstName" />:</td>
		<td width="25%" class="labelNormal"><s:property
			value="%{user.firstName}" /></td>
		<td width="25%" class="labelBold"><s:text
			name="label.dealerUser.lastName" />:</td>
		<td width="25%" class="labelNormal"><s:property
			value="%{user.lastName}" /></td>
	</tr>
				<s:if test="organisations.entrySet().size() < 2" >
			<s:iterator value = "organisations.entrySet()">
			<tr width="100%">
			<td width="25%" class="labelBold"><s:text name="label.serviceLocation.companyName" />:</td>
		   <td width="25%" class="labelNormal"><s:property
			value="key" /></td>
			<td width="25%" class="labelBold"><s:text name="label.common.type" />:</td>
			<td width="25%" class="labelNormal"><s:property
			value="value" /></td>
			</tr>
			</s:iterator>
			</s:if>			
			<s:else>
			<tr class="row_head">
	<th  colspan = "5"  ><div align = "center"><s:text name="label.user.organizations" /></div></th>
	
	</tr>
	<tr class="row_head">
	<th  colspan = "2" width="50%" ><s:text name="label.serviceLocation.companyName" /></th>
	<th   colspan = "2"  width="50%" ><s:text name="label.common.type" /></th>
	</tr>	
	
	 <s:iterator value = "organisations.entrySet()">
	
	   <tr width="100%">
		<td width="25%" class="labelNormal"><s:property
			value="key" /></td>		
		<td width="25%" class="labelNormal"><s:property
			value="value" /></td>
			</tr>
			</s:iterator>
			</s:else>
	
	
	
	 <tr width="100%">
		<td colspan="6">
		<div class="admin_section_heading"><s:text
			name="label.roles.currentRolesCurrent" /></div>
		</td>
	</tr>
</table>
<table width="100%" cellspacing="0" cellpadding="0">
	<tr class="row_head">

		<th width="10%" colspan="1"><s:text name="label.common.remove" /></th>
		<th width="40%" colspan="2"><s:text name="label.roles.roleName" /></th>
		<th width="40%" colspan="2"><s:text
			name="label.roles.roleDescription" /></th>
        <th width="10%" colspan="1"><s:text
			name="columnTitle.roles.roleType" /></th>			
			

	</tr>
	<s:iterator value="user.roles" status="iter">


		<script type="text/javascript">
			
				</script>

		<tr>
			<td width="10%" colspan="1"><s:checkbox
				name="rolesToRemove[%{#iter.index}]" value="" fieldValue="%{id}" /></td>
			<td width="40%" colspan="2" class="labelBold"><s:property
				value="%{name}" /></td>
			<td width="40%" colspan="2" class="labelBold"><s:property
				value="%{description}" /></td>
				<td width="10%" colspan="1" class="labelBold"><s:property
				value="roleType.type" /></td>
		</tr>
	</s:iterator>

	<tr width="100%">
		<td colspan="6">
		<div class="admin_section_heading"><s:text
			name="label.roles.assignNewRoles" /></div>
		</td>
	</tr>
</table>
<table width="100%" cellspacing="0" cellpadding="0" ><tbody id="selectedRoles">
	<tr class="row_head">

		<th width="10%" colspan="1"><s:text name="button.common.add" /></th>
		<th width="40%" colspan="2"><s:text name="label.roles.roleName" /></th>
		<th width="40%" colspan="2"><s:text
			name="label.roles.roleDescription" /></th>
			<th width="10%" colspan="1"><s:text
			name="columnTitle.roles.roleType" /></th>

	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
</tbody>
</table>
<table width="100%" frame="above">
	<tr>
		<td width="50%" id="viewLink"><a id="showRoles" class="link"
			href="javascript:opnlnk()"> <s:text
			name="label.roles.chooseRoles" /> </a></td>
			<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
		<td width="50%"><s:submit action="updateUserRoles"
			value="%{getText('button.common.update')}" /></td>
			</authz:ifNotPermitted>
	</tr>


</table>
<jsp:include flush="true" page="showRoles.jsp" />