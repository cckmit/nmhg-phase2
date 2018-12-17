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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<s:if test="campaignNotification.campaignStatus!=null">
<div  style="width:100%">
 <div class="admin_section_div" style="width:100%">	
 <script type="text/javascript"> 
 var status= "<s:property value="campaignNotification.status"/>";
 toggleStatus(status);
 function toggleStatus(status) {
  if ("Active"==status) {
	  console.log(status);
 		document.forms[0]["campaignNotification.status"][0].checked = true;
 		dijit.byId("UpdateCampaignNotificationStatus").setDisabled(true);
 		dijit.byId('UpdateCampaignNotificationStatus').setValue(" ");
 		}
 		else
 		{
 		document.forms[0]["campaignNotification.status"][1].checked = true;
 		dijit.byId("UpdateCampaignNotificationStatus").setDisabled(false);
 		}
 		}
function update() {
var url = "field_mod_action_history?id=<s:property value="campaignNotification.id"/>";
window.open(url, "_blank","directories=no, status=no,toolbar=no,menubar=no, titlebar=no, width=800, height=200,top=200,left=500 resizable=yes, scrollbars=yes");
}
</script>

	<table width="100%" class="grid">
	
			 <tr>
			  <authz:ifUserInRole roles="admin">
			<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.fieldModificationStatus"/>:</td>
	      <td class="labelStyle" nowrap="nowrap" valign="top">
	        <input type="radio" name="campaignNotification.status" id="fieldModStatus" value="Active" onclick="toggleStatus(this.value)" checked="checked" />
	      <s:text name="label.common.active"/>
	        <input type="radio" name="campaignNotification.status" id="fieldModStatus"  value="Inactive" onclick="toggleStatus(this.value)" checked="checked"/>
	      <s:text name="label.common.inactive" />
	      </td>
	      </authz:ifUserInRole>
	      <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.campaign.reason"/>:</td>
	      <td class="labelNormalTop" colspan="2"><s:select
						id="fieldModInvStatus" name="campaignNotification.fieldModInvStatus"
						cssClass="processor_decesion" value="%{campaignNotification.fieldModInvStatus.code}"
						list="getReasonCodes('FieldModificationInventoryStatus',campaignNotification)"
						theme="twms" listKey="code"
						listValue="code" cssStyle="width:100px;" headerKey="-1"
						headerValue="%{getText('label.common.selectHeader')}"  onchange="getVal();" />
						<b id='description'></b> 
						
						<script type="text/javascript">
					                						                		
					                		function getVal()
					                		{
					                			if(document.getElementById('fieldModInvStatus').value=='--Select--')
					                			{
					                			document.getElementById("description").innerHTML=" ";
					                			}
					                			var code=document.getElementById("fieldModInvStatus").value;
					                			var params={"fieldModCode":code,"id":'<s:property value = "campaignNotification.id"/>'};
					                			var url = "getReasonDescription.action?";
					                			twms.ajax.fireHtmlRequest(url, params, function(data) {	
					                				var lovDescription = eval(data);	
					                				document.getElementById('description').innerHTML ="("+ lovDescription+")";					                			
					                				});
					                						                							                   		
					                		}

                		                </script>  
		</td>
		<authz:ifUserNotInRole roles="admin">
	     <td></td> <td></td>
	    <s:hidden name="campaignNotification.status" value="Inactive"/>
	      </authz:ifUserNotInRole>
		<td><a href="javascript:update()"><s:text name="label.campaignUpdate.veiwHistory"/></a></td>
	    </tr>
	    <tr>
	    <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.campaignComments"/>:</td>
	    <td><s:textarea name="campaignNotification.comments" cssClass="textarea" cols="40"
					value="%{campaignNotification.comments}" /></td>
	    <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.attachDocuments"/>:</td>
		<td class="labelStyle" nowrap="nowrap" style="border: 0">
	     <u:uploadDocument name="campaignNotification.attachments" trimFileNameDisplayTo="150"
							fieldSize="35" cssStyle="border: 0" canDeleteAlreadyUploadedIf="loggedInUserADealer" />
		</td>

     </tr>
     
     <authz:ifUserInRole roles="admin">
     <tr>
     <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.campaign.accecptance"/>:</td>
      <td class="labelNormalTop" colspan="2">
      			<s:select id="UpdateCampaignNotificationStatus" name="UpdateCampaignNotificationStatus"
						cssClass="processor_decesion"
						list="availableStatuses" value="-1"
						theme="twms"
						cssStyle="width:100px;" headerKey="-1"
						headerValue="%{getText('label.common.selectHeader')}"/>		
	
		</td>	  		
     </tr>
     </authz:ifUserInRole>
     
 </table>
 </div> 
</div>
</s:if>