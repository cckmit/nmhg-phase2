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
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />


<u:body>
	<s:form name="baseForm" id="baseForm" theme="twms" cssStyle="overflow-x:hidden">
		<div class="admin_section_div">
		<table width="100%" cellspacing="0" cellpadding="0">
			<tr class="errorMessage">
				<td colspan="4"><u:actionResults /></td>
			</tr>
			<tr width="100%">
				<td colspan="4">
				<div class="admin_section_heading"><s:text
					name="label.roles.roleDetails" /></div>
				</td>
			</tr>
			<tr width="100%">
				<td width="20%" class="labelBold"><s:text
					name="label.roles.roleName" />:</td>
				<td width="30%">&nbsp;&nbsp;&nbsp;&nbsp;<s:textfield disabled="%{primaryRole}"
					name="role.name" value="%{role.name}" /></td>

				<td width="20%" class="labelBold"><s:text
					name="label.roles.roleDescription" />:</td>
				<td width="30%" class="label"><s:textfield disabled="%{primaryRole}"
					name="role.description" value="%{role.description}" /></td>
			</tr>


<%-- 			<tr width="100%">
				<td style="padding-bottom: 10px;" width="20%" class="labelBold"><s:text
					name="label.roles.roleType" />:</td>
				<td width="30%" class="label" style="padding-bottom: 10px;" colspan="3">
				<s:select name="role.roleType" id="roleType" list="roleTypeMap" value="%{role.roleType}"
					cssStyle="width:128px" /></td>
			</tr> --%>
			<s:hidden name="role.roleType"></s:hidden>


			<tr width="100%">
				<td colspan="4">
				<div class="admin_section_heading"><s:text
					name="label.roles.permissions" /></div>
				</td>
			</tr>
		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="1"
			class="borderForTable">
			<thead>
				<tr class="row_head">
					<th width="19%" rowspan="2"><s:text
						name="label.roles.subjectArea" /></th>

					<th width="19%" rowspan="2"><s:text
						name="label.roles.functionalArea" /></th>
					<th width="62%" colspan="5">
					<div align="center"><s:text name="label.roles.permissions" /></div>
					</th>
				</tr>
					<tr class="row_head">
						<th width="34%">
							<div align="center">
								<s:text name="label.roles.permissions.view" />
							</div></th>
						<th width="34%">
							<div align="center">
								<s:text name="label.roles.permissions.update" />
							</div></th>
					</tr>
				</thead>
		</table>
		</div>
		<div class="admin_section_div" style="overflow-y: auto; overflow-x: hidden;height: 59%;position: relative">
		<table width="100%" border="0" cellspacing="0" cellpadding="1"
			class="borderForTable">

			<s:iterator value="subjectAreas" status="iter">
				<s:set name="subjectAreaId" value="%{subjectArea.id}" />


				<s:iterator value="functionalAreas" status="iter1">
					<tr width="100%">
						<s:if test="#iter1.index == 0">
							<td width="19%" valign="middle" align="center" class="labelBold"
								rowspan="<s:property value="functionalAreas.size()" />"><s:property
								value="subjectAreas[#iter.index].subjectArea.description" /> <s:hidden
								name="subjectAreas[%{#iter.index}].subjectArea"
								value="%{#subjectAreaId}" /></td>
						</s:if>
						<td width="19%" class="labelBold"><s:property
							value="%{functionalArea.description}" /> <s:hidden
							name="subjectAreas[%{#iter.index}].functionalAreas[%{#iter1.index}].functionalArea"
							value="%{functionalArea.id}" /></td>
							<td width="34%" class="labelNormal">
								<div align="center">
									<s:checkbox
										name="subjectAreas[%{#iter.index}].functionalAreas[%{#iter1.index}].permissions['view']"
										id="%{#subjectAreaId}_%{functionalArea.id}_view" value=""
										fieldValue="%{actionsMaster['view'].id}" />
								</div></td>
							<td width="34%" class="labelNormal">
						<div align="center"><s:checkbox
							name="subjectAreas[%{#iter.index}].functionalAreas[%{#iter1.index}].permissions['update']"
							id="%{#subjectAreaId}_%{functionalArea.id}_update" value=""
							fieldValue="%{actionsMaster['update'].id}" /></div>
						</td>
					</tr>

					<script type="text/javascript">
				dojo.addOnLoad( function() {
				    var subjectArea = '<s:property value="#subjectAreaId"/>';
					var functionalArea = '<s:property value="%{functionalArea.id}"/>';
					var viewPerm = "<s:property value="permissions['view'].action" />";					
					if (viewPerm == 'view') {
					dojo.byId(subjectArea + "_" + functionalArea + "_view").checked = true;
					}
					var createPerm = "<s:property value="permissions['create'].action" />";
					if (createPerm == 'create') {
						dojo.byId(subjectArea + "_" + functionalArea + "_create").checked = true;
					}
					var deletePerm = "<s:property value="permissions['delete'].action" />";
					if (deletePerm == 'delete') {
						dojo.byId(subjectArea + "_" + functionalArea + "_delete").checked = true;
					}
					var updatePerm = "<s:property value="permissions['update'].action" />";
					if (updatePerm == 'update') {
						dojo.byId(subjectArea + "_" + functionalArea + "_update").checked = true;
					}
					
					var searchPerm = "<s:property value="permissions['search'].action" />";
					if (searchPerm == 'search') {
						dojo.byId(subjectArea + "_" + functionalArea + "_search").checked = true;
					}
				});
			</script>


				</s:iterator>
			</s:iterator>
		</table>
		</div>
		<div class="admin_section_div" style="margin-top: 5px;"><s:hidden
			name="role" value="%{role.id}" /> <s:hidden
			name="selectedBusinessUnit" id="selectedBusinessUnit" />
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="policyRegn_table">
			<tr>
				<td align="center"><s:submit
					value="%{getText('button.common.update')}" action="updateRole"
					cssClass="buttonGeneric" /></td>
			</tr>
		</table>
		</div>
	</s:form>
<authz:ifPermitted resource="settingsManageRolesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
