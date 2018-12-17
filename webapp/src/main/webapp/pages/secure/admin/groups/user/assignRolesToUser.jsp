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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="authz" uri="authz" %>

<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />


<u:body  >

	<script type="text/javascript">
	var addedRoles = new Array();
	dojo.require("twms.widget.Dialog");
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("dijit.layout.ContentPane");
	dojo.subscribe("/roleSearch/show", null, function() {
		dijit.byId("searchCriteria").show();
	});
	dojo.subscribe("/roleSearch/hide", null, function() {
		dijit.byId("searchCriteria").hide();
	});

	function opnlnk() {
		dojo.byId("chkAll").checked = false ;
		dojo.publish("/roleSearch/show");
	}
	function populatelist() {
	 var rownum = dojo.query("tr", dojo.byId("allRoles")).length;
			for ( var i = 0; i < rownum ; i++) {
			 var input = dojo.query("input", dojo.byId("allRoles").rows[i]);
			if (input[0].checked ) {
				if (dojo.query("td", dojo.byId("selectedRoles").rows[1]).length == 1)
				{
					dojo.byId("selectedRoles").deleteRow(1);
					}
			var template = 	"<tr>" + dojo.byId("allRoles").rows[i].innerHTML + "<tr>";
				var markupTxt = dojo.string.substitute(template);
				var tableRow = dojo.html.createNodesFromText(markupTxt);
				console.log(dojo.query("input", tableRow)[0]);
				dojo.query("input", tableRow)[0].checked = "checked" ;
				console.log(dojo.query("input", tableRow)[0]);
				dojo.byId("selectedRoles").appendChild(tableRow);
				dojo.byId("allRoles").deleteRow(i);
				--rownum;
				--i;
			}
		}
		dojo.publish("/roleSearch/hide");
	}

	
</script>

	<s:form name="baseForm" id="baseForm">

	<div class="admin_section_div">
	<div id="userDiv"></div>

	<table width="100%" cellspacing="0" cellpadding="0">
<tr class="errorMessage">
			<td colspan="5"><u:actionResults /></td>
		</tr>
		<tr width="100%">
			<td colspan="5">
			<div class="admin_section_heading"><s:text name= "label.roles.assignRoles"/></div>
			</td>
		</tr>
		<tr width="100%">
			<td width="20%" class="labelBold"><s:text name ="label.roles.enterLogin"/> :</td>
			<td width="30%" class="label"><sd:autocompleter
				id="userNameAutoComplete" href="list_users.action"
				name="forUser" loadOnTextChange="true" loadMinimumCount="3"
				showDownArrow="false" indicator="indicator" /> <img
				style="display: none;" id="indicator" class="indicator"
				src="image/indicator.gif" alt="Loading..." /></td>
			
			
			<script type="text/javascript">	
	       var params =  new Array() ;
	          dojo.addOnLoad( function() {
		       dojo.connect(dijit.byId("userNameAutoComplete"), "onChange", function(
				value) {
			dojo.html.show(dojo.byId("userDiv"));
			if (!dijit.byId("userNameAutoComplete").isValid()) {
				dojo.html.hide(dojo.byId("userDiv"));
			} else {

				
				dojo.byId("user_id").value = value;				
				params["user"]= value ;
				getUserDetails();
			}

		});
	});
</script>
		</tr>
	</table>
<div id="showUserDetails" dojoType="dijit.layout.ContentPane"  executeScripts="true"
 style="padding-top: 3px; overflow:hidden; " >
     </div>

	<s:hidden id="user_id" name="user" value="%{user.id}" /> 
	</s:form>
<s:if test = "user != null"> 
 <script type="text/javascript">	 
	 twms.ajax.fireHtmlRequest("showUserDetails.action?",{user : <s:property value="user.id"/>}  , function(data) {
			var parentContentPane = dijit.byId("showUserDetails");
			parentContentPane.setContent(data);
		    });
    </script> 

</s:if>
	<script type="text/javascript">
    
	function getUserDetails() {
		
		var url = "showUserDetails.action?";
		var targetContentPane = dijit.byId("showUserDetails");
		targetContentPane.domNode.innerHTML = "<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";

		twms.ajax.fireHtmlRequest(url,params  , function(data) {
			var parentContentPane = dijit.byId("showUserDetails");
			parentContentPane.destroyDescendants();
			parentContentPane.setContent(data);
		});
	}
 
</script>
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