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

<%@taglib prefix="s" uri="/struts-tags" %>
<%@include file="/i18N_javascript_vars.jsp"%>
<%@ taglib prefix="authz" uri="authz" %>
<div class="admin_section_div"  style="width:99%;margin:5px;">
	
	<div class="admin_section_heading">
		<s:text name="label.manageGroup.dealerGroupSchemeDetails"/>
	</div>

	<div  style="padding:4px;" class="labelStyle">
		<s:text name="label.common.name" />:
		<span class="labelNormal"><s:textfield name="name" size="35"/></span>
	</div>
	
	<div class="section">
		<div class="mainTitle">
			<s:text name="label.common.purpose"/>
		</div>
		<div class="borderTable">&nbsp;</div>
		<table>
			<tr>
				<td align="center" valign="top">
					
						<div class="mainTitle">
							<s:text name="label.manageGroup.assigned"/>
						</div>
						<div class="admin_selections">
							<s:updownselect id="updownlist2" cssClass="admin_selections" list="dealerScheme.purposes" 
									name="purposeNames" size="%{dealerScheme.purposes.size()+ availablePurposes.size()}" 
									allowMoveDown="false" allowMoveUp="false" allowSelectAll="false"/>
						</div>
					
				</td>
				<td align="center" valign="middle">
					<input type="button" class="buttonGeneric" name="right" value="&gt;&gt;" onclick="moveSelectedOptions(this.form['updownlist2'],this.form['list2'])"><br/><br/>
					<input type="button" class="buttonGeneric" name="left" value="&lt;&lt;" onclick="moveSelectedOptions(this.form['list2'],this.form['updownlist2'])">		
				</td>
				<td align="center" valign="top">
					
						<div class="mainTitle">
							<s:text name="label.manageGroup.available"/>
						</div>
						<div class="admin_selections">
							<s:updownselect id="list2" cssClass="admin_selections" list="availablePurposes" 
									name="somethingElse" size="17" 
									allowMoveDown="false" allowMoveUp="false" allowSelectAll="false"/>
						</div>
					
				</td>
			</tr>
		</table>
	</div>
	
	
</div>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
	<div align="center">
		<s:if test="isPreview() == false">
			<input type="button" id="closeButton" class="buttonGeneric" value="<s:text name='button.common.cancel'/>" />
		</s:if>
		<s:else>
			<s:hidden name="preview"></s:hidden>
		</s:else>
		<s:hidden name="schemeId" value="%{dealerScheme.id}"/>
		<s:hidden name="dealerScheme.id"/>
		<s:submit cssClass="buttonGeneric" onclick="selectAllProps(this.form['updownlist2'])" action="save_dealer_scheme" value="%{getText('button.common.update')}" />
		<input type="button" class="buttonGeneric" value="<s:text name="button.manageGroup.manageMemberGroups"/>" onclick="javascript:sendThisRequest('<s:property value="dealerScheme.name"/>','<s:property value="dealerScheme.id"/>');"/>
	</div>
</authz:ifNotPermitted>


<script type="text/javascript">
	
	dojo.addOnLoad(function() {
			if(dojo.byId('closeButton')) {
			dojo.connect(dojo.byId('closeButton'), "onclick", function() {
				var thisTabId = getTabDetailsForIframe().tabId;
				var thisTab = getTabHavingId(thisTabId);
				closeTab(thisTab);		
			});
			}
		});
		
		
	function sendThisRequest(name, id) {
		var url = "list_dealer_groups.action?schemeId=" + id;
		var tabLabel = i18N.manage_dealer_groups + " " + name;
		var decendent_of = getTabDetailsForIframe().folderName;
		top.publishEvent("/tab/open", {label: tabLabel, 
										url: url,
										decendentOf: decendent_of});
	}
</script>