<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title><s:text name="pageTitle.updateProfile"></s:text></title>
<s:head theme="twms" />
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<script type="text/javascript">
   dojo.require("dijit.layout.BorderContainer");
</script>
</head>

<u:body >
	<u:actionResults />
	<s:form action="save_update_subscription" theme="twms" validate="true" id="baseFormId">
		<div dojoType="dijit.layout.BorderContainer" class="section_div" 
		style="width:100%; height: 100%;" id="root" >
	        <div id="email_notification_table" class="section_header">
	            <s:text name="title.email.subscription"/>
      		</div>
      		
      		<table  cellspacing="0" cellpadding="0" class="grid" style="padding-left:0px">
				
				<tr>
                    <td width="5%" align="center">
                        <input type="checkbox" name="masterCheck" id="masterCheck" style="border:none;" />
                    </td>
                    <td>
                    Select All
                    </td>
                </tr>
				<s:iterator value="listOfNotificationsToDisplay" status="iter">
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                                dojo.connect(dojo.byId("masterCheck"), "onclick", function() {
                                if (dojo.byId(("masterCheck")).checked) {
                                dojo.byId(("listOfNotificationsToDisplay<s:property value='{#iter.index}'/>")).checked = true;
                                } else {
                                   dojo.byId(("listOfNotificationsToDisplay<s:property value='{#iter.index}'/>")).checked = false;
                                }
                            });
                        });
                    </script>
                    <tr>
						<td align="center">
                            <s:checkbox name="listOfNotificationsToDisplay[%{#iter.index}].isSubscribed" value="isSubscribed" 
                            id="listOfNotificationsToDisplay[%{#iter.index}]" cssStyle="border:none;"/>
							<s:hidden name="listOfNotificationsToDisplay[%{#iter.index}].eventState.id" />
	   	      				<s:hidden name="listOfNotificationsToDisplay[%{#iter.index}].eventState.name" />
						</td>
						<td>
							<s:property	value="listOfNotificationsToDisplay[#iter.index].eventState.displayName" />						
						</td>
					</tr>
				</s:iterator>
        
			</table>
			
		</div>
		<div align="center" class="spacingAtTop">
			  <s:submit cssClass="buttonGeneric" value="Update" /> 
			</div>
	</s:form>
</u:body>
<authz:ifPermitted resource="settingsEmailSubscriptionReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>