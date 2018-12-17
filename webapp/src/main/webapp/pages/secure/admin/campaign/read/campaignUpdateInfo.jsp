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
<div  style="width:100%">
<div class="admin_section_div" style="width:100%">	
 <script type="text/javascript"> 
    function update() {
   var url = "field_mod_action_history?id=<s:property value="campaignNotification.id"/>";
      window.open(url, "_blank","directories=no, status=no,toolbar=no,menubar=no, titlebar=no, width=800, height=200,top=200,left=500 resizable=yes, scrollbars=yes");
     }
      </script>
 <table width="100%" class="grid">
   <tr>
	 <td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.campaign.notificationStatus1"/>:</label></td>
     <td width="33%"><s:property value="%{campaignNotification.status}"/></td>
	 <td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.campaign.reason"/>:</label></td>
	 <td><s:property value="%{campaignNotification.fieldModInvStatus.description}"/></td>
	 <td valign="top"><a href="javascript:update()"><s:text name="label.campaignUpdate.veiwHistory"/></a></td>
  </tr>
  <tr>
    <td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.campaignComments"/>:</label></td>
	<td width="33%"><s:property value="%{campaignNotification.comments}"/></td>
	<td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.attachDocuments"/>:</label></td>
	<td>
      <table>
	     <s:iterator value="campaignNotification.attachments" status="attachments1">
			<tr>
			  <td>
			     <a id="attached_file_<s:property value="#attachments1.index" />">
					<s:property	value="campaignNotification.attachments[#attachments1.index].fileName" />
				 </a>
					<script type="text/javascript">
						dojo.addOnLoad(function(){
			               dojo.connect(dojo.byId("attached_file_<s:property value="#attachments1.index" />"), "onclick", function(event) {
			               dojo.stopEvent(event);
			               getFileDownloader().download("downloadDocument.action?docId=<s:property	value="campaignNotification.attachments[#attachments1.index].id" />");
			                 });	
			              });							    
					</script>
			 </td>
			</tr>
		 </s:iterator>
	   </table>	
	 </td>
   </tr>    	
 </table>
 </div> 
</div>

