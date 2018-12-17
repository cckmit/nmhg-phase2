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
<%
            response.setHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "must-revalidate");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="authz" uri="authz" %>

<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css" />

<script type="text/javascript">
dojo.require("dijit.Tooltip");
dojo.require("twms.widget.Dialog");
dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.layout.ContentPane");

dojo.subscribe("/userPresent/show", null, function() {
	dijit.byId("userPresent").show();
});
dojo.subscribe("/userPresent/hide", null, function() {
	dijit.byId("userPresent").hide();
});

var checked = false;
var addedUsers = new Array();
function closeBox(){
	 dojo.publish("/userPresent/hide");	
}
function fill(){
	 dojo.byId("chooseBtn").disabled= true;
	 if(addedUsers[id]==id){
		 dojo.publish("/userPresent/show");
		  return ;
		 } 
	 var rownum = dojo.byId("usertable").getElementsByTagName('tr').length ;
	 var uname = dojo.byId("userNameAutoComplete").value;
	 
    if(rownum == 0) 
    {       
    	var  x= document.createElement('TR');
    	var y =document.createElement('TD');
    	y.innerHTML ="<input type = 'checkbox' onClick = 'checkUncheckAll()' > <s:text name="label.selectUnselect" />"  ;
        x.appendChild(y);
        dojo.byId("usertable").appendChild(x);
        var  x= document.createElement('TR');
    	var y =document.createElement('TD');
    	y.innerHTML =  "<input type = 'checkbox' name = 'usersToAdd' value = " +id+" checked > <a  cssStyle='cursor:pointer' id='UserId' href='javascript:userdtl(&#39;"  +uname+ "&#39;, "+id+")'>"  + dojo.byId("userNameAutoComplete").value + "</a>"	 ;
    	x.appendChild(y);
        dojo.byId("usertable").appendChild(x);
    	dojo.byId("submitBtn").disabled= false; 
    }
    else
    {
    	var cols = dojo.byId("usertable").rows[rownum-1].getElementsByTagName('td').length ;
    	if (cols<5){
    	y = dojo.byId("usertable").rows[rownum-1].insertCell(cols) ;

    	y.innerHTML =	"<input type = 'checkbox' name = 'usersToAdd' value = " +id+" checked > <a  cssStyle='cursor:pointer' id='UserId' href='javascript:userdtl(&#39;" +uname+ "&#39; , "+id+")'>"  + dojo.byId("userNameAutoComplete").value + "</a>"	 ;
    	
                   }
    	else
    	           {

    		
    		    x = dojo.byId("usertable").insertRow(rownum);
    	    	 y=x.insertCell(0);
     	    	y.innerHTML =  "<input type = 'checkbox' name = 'usersToAdd' value = " +id+" checked > <a  cssStyle='cursor:pointer' id='UserId' href='javascript:userdtl(&#39;" +uname+ "&#39; , "+id+")'>"  + dojo.byId("userNameAutoComplete").value + "</a>"	 ;
    		
        	       }	
    	       

     }
			 
    addedUsers[id] = id;
	
}
function checkUncheckAll()
{
	if (checked == false){checked = true}else{checked = false}
	for (var i = 0; i < dojo.byId('baseForm').elements.length; i++) {
		dojo.byId('baseForm').elements[i].checked = checked;
	}
}
function userdtl( uname , id){
			var url = "roles_to_userPreview.action?id=" + id ;
			var thisTabLabel = getMyTabLabel();
			parent.publishEvent("/tab/open", {
				label :"Roles "+ uname  ,
				url :url,
				decendentOf :thisTabLabel,
				forceNewTab :true
			});
		}
</script>
<s:iterator value="users">
	<script type="text/javascript">
addedUsers[<s:property value="id" />] = <s:property value="id" />;
</script>
</s:iterator>

<u:body >
	<form name="baseForm" id="baseForm">
	<div id="userDiv"></div>

	<div class="admin_section_div">

	<table width="100%" cellspacing="0" cellpadding="0">

		<tr width="100%">
			<td colspan="5">
			<div class="admin_section_heading"><s:text
				name="label.roles.roleDetails" /></div>
			</td>
		</tr>
		<tr width="100%">
			<td width="20%" class="labelBold"><s:text
				name="label.roles.roleName" />:</td>
			<td width="30%" class="labelBold">&nbsp;&nbsp;&nbsp;<s:property value="role.name" /></td>

			<td><s:a cssStyle="cursor:pointer" id="RoleId" href="#">
				<s:text name="label.roles.viewCurrentUsers" />
			</s:a> <script type="text/javascript">
		dojo.addOnLoad( function() {
			dojo.connect(dojo.byId("RoleId"), "onclick", function(event) {
				var url = "users_to_rolePreview.action?id=" + "<s:property value="role.id"/>";
				var thisTabLabel = getMyTabLabel();
				parent.publishEvent("/tab/open", {
					label :"<s:text	name="label.common.users" /> " +"<s:property value="role.name"/>" ,
					url :url,
					decendentOf :thisTabLabel,
					forceNewTab :true
				});
			});
		});
	</script></td>
			<td colspan="2"></td>
		</tr>
		<tr width="100%">
			<td width="20%" class="labelBold"><s:text
				name="label.roles.addNewUser" />:</td>
			<td width="30%" class="labelBold"><sd:autocompleter
				id="userNameAutoComplete" href="list_users.action?role=%{role.id}"
				name="forUser" loadOnTextChange="true" loadMinimumCount="3"
				showDownArrow="false" indicator="indicator" formId = "baseForm" /> <img
				style="display: none;" id="indicator" class="indicator"
				src="image/indicator.gif" alt="Loading..." /></td>
			<td><input id="chooseBtn" type="button"
				value="<s:text name ='button.roles.addUser'/>"
				Class="buttonGeneric" onClick="fill()" disabled> <span
				dojoType="dijit.Tooltip" connectId="chooseBtn"><s:text
				name="tooltip.roles.addUser" /> </span></td>
			<script type="text/javascript">
        
           var id;
           dojo.addOnLoad(function() {
                    dojo.connect(dijit.byId("userNameAutoComplete"), "onChange", function(value) {
                        dojo.html.show(dojo.byId("userDiv"));
                        if (!dijit.byId("userNameAutoComplete").isValid()) {
                        	dojo.html.hide(dojo.byId("userDiv")); 
                        } else { 
                            
                        	id = value ; 
                        	 dojo.byId("chooseBtn").disabled= false;                	                                       	  
                            }
                       
                    });
                });
              </script>
			<td colspan="2" />
		</tr>


		<tr width="100%">
			<td colspan="5">
			<div class="admin_section_subheading">&nbsp;</div>
			</td>
		</tr>



	</table>

	<table width="100%">
		<tbody id="usertable"></tbody>
	</table>




	<div><s:hidden name="role" value="%{role.id}" /> <s:hidden
		name="selectedBusinessUnit" id="selectedBusinessUnit" />
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="policyRegn_table">
		<tr>
			<td align="center"><s:submit id="submitBtn"
				value="%{getText('button.common.update')}"
				action="updateUsersAssignedToRole" cssClass="buttonGeneric"
				disabled="true" /></td>

		</tr>
	</table>
	</div>
	</form>
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
<div id="dialogBoxContainer" style="display: none">
<div dojoType="twms.widget.Dialog" id="userPresent" bgColor="#FFF"
	bgOpacity="0.5" toggle="fade" toggleDuration="250"
	title="<s:text name='title.userPresent'/>"
	style="height: 120px; width: 300px;">
<div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="center"><s:text name="roles.message.userAlreadyAdded" /></td>
	</tr>

	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="center"><input class="buttonGeneric" type="button"
			value="&nbsp;&nbsp;<s:text name = 'button.commom.ok'/>&nbsp;&nbsp;"
			onclick="closeBox()" /></td>
	</tr>

	</div>
	</div>
	</div>