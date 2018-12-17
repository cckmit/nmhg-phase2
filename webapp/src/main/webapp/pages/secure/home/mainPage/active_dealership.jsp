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


<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<div id="selectLocationBox " style="padding: 0pt; color: black;">Select Location:
<s:if test="buConfigAMER">
<s:select id="dealerships" label="Dealership"
	list="belongsToOrganizations" listKey="id" listValue="nameWithBrand"
	name="Dealership"
	value="%{loggedInUser.currentlyActiveOrganization.id.toString()}"
	emptyOption="false" />
	</s:if>
	<s:else>
	<s:select id="dealerships" label="Dealership"
	list="belongsToOrganizations" listKey="id" listValue="name"
	name="Dealership"
	value="%{loggedInUser.currentlyActiveOrganization.id.toString()}"
	emptyOption="false" />
	</s:else>
<script type="text/javascript">
	var org = dojo.byId("dealerships");
	var activeOrg = '<s:property value="%{loggedInUser.currentlyActiveOrganization.id}"/>';
	var selectedOrg;
	dojo.connect(org, "onchange", function() {
		dijit.byId("confirmDealership").show();
	});

	dojo.addOnLoad( function() {
		dojo.connect(dojo.byId("changeDealership"), "onclick", function() {
			changeDealership();
		});
		dojo.connect(dojo.byId("resetDealership"), "onclick", function() {
			resetDealership();
		});
		dojo.html.hide(dojo.byId("changingDealershipLid"));
	});

	function changeDealership() {
		dijit.byId("confirmDealership").hide();
		dojo.html.show(dojo.byId("changingDealershipLid"));
		dojo.byId("dealerships").disabled = true;
		var optionsList = org.options;
		var i = optionsList.length;
		while (i--) {
			var optionNode = optionsList[i];
			if (optionNode.selected) {
				selectedOrg = optionNode.value;
				//location.href = "org.action?selectedOrganization="  + selectedOrg;
				twms.ajax.fireHtmlRequest("org.action",{
					selectedOrganization : selectedOrg
		        },function(data) {
		        	location.href = "home.action";
				});
				break;
			}
		}
	}

	function resetDealership() {
		dijit.byId("confirmDealership").hide();
		org.value = activeOrg;
	}

</script>
</div>

<div style="display: none;">
	<div dojoType="twms.widget.Dialog" id="confirmDealership"
		title="Confirm Dealership" style="width: 300px" closable="false">
		<div dojoType="dijit.layout.LayoutContainer"
			style="background: #F3FBFE; overflow: auto;">
			<div id="confirmDealershipContent" style="padding: 0 10px 0 10px">
				Are you sure you want to change the active dealership ?
			</div>
			<div style="padding: 10px 0 10px 0;">
				<center>
					<button class="buttonGeneric" id="changeDealership"
						style="padding: 0 10px 0 10px; margin-left: 10px"><s:text name="label.common.yes"/></button>
					<button class="buttonGeneric" id="resetDealership"
						style="padding: 0 10px 0 10px"><s:text name="label.common.no"/></button>
				</center>
			</div>
		</div>
	</div>
</div>


