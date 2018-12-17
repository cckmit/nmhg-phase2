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
		<s:hidden id="campaign.id" name="campaign.id" value="%{campaign.id}"/>
	<table width="100%" class="grid">
		<tr>
			<td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.campaign.code" />:</label></td>
			<td width="33%"><s:property value="code"/></td>
			<td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.campaign.classCode" />:</label></td>
			<td ><s:property value="campaignClass.description"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.startDate" />:</label></td>
			<td><s:property value="fromDate"/></td>
			<td nowrap="nowrap" ><label class="labelStyle"><s:text name="label.common.endDate" />:</label></td>
			<td><s:property value="tillDate"/></td>
		</tr>
		<tr>
			<td width="20%" nowrap="nowrap"><label class="labelStyle"><s:text name="label.campaign.description" />:</label></td>
			<td width="33%"><s:property value="description"/></td>
			<td width="20%"nowrap="nowrap" ><label class="labelStyle"><s:text name="columnTitle.campaign.status" />:</label></td>
			<td colspan="3"><s:property value="getStatus()"/></td>
			</tr>
		<tr>
		    <td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="columnTitle.campaign.field_mod_age" />:</label></td>
			<td width="20%"colspan="3"><s:property value="getFieldModAge()"/></td>
			<td><a class="alinkclickable">
			<authz:ifUserInRole roles="admin">
				 <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			         id="internationalizeCampainDesc" tagType="a" cssClass="inventory_folder folder"
			         tabLabel="%{getText('label.common.internationalize')}"
			         url="internationalizeCampaignDescription.action?campaign.id=%{campaign.id}"
			          catagory="campaign_info">
		           <s:text name="label.common.internationalize" />
           		 </u:openTab>
           	</authz:ifUserInRole>
			</a>
           </td>
		</tr>
		<tr>
		 <s:if test="budgetedAmount!= null && getLoggedInUser().isInternalUser()">
		<td width="20%" nowrap="nowrap">
		       <label for="campaign_budget" class="labelStyle"><s:text name="label.campaign.budgetedAmount" />:</label>
		   </td>
		   <td width="25%">
		    $<s:property value="budgetedAmount"/>
		   </td>
		   </s:if>
		</tr>
      
    
      	<tr>
      		<authz:ifUserNotInRole roles="admin">
      		<s:if test="campaign.attachments[0]!=null">
      	    <td width="20%" nowrap="nowrap" valign="top"><label class="labelStyle"><s:text name="columnTitle.campaign.attached_document_link"/>:</label></td>
      	   <td>
      	   <table>
			<s:iterator value="campaign.attachments" status="attachments">
			<tr>
			<td>
			    <a id="attached_file_<s:property value="#attachments.index" />">
					 <s:property	value="campaign.attachments[#attachments.index].fileName" />
						  </a>
					 <script type="text/javascript">
						     dojo.addOnLoad(function(){
			                    dojo.connect(dojo.byId("attached_file_<s:property value="#attachments.index" />"), "onclick", function(event) {
			                        dojo.stopEvent(event);
			                        getFileDownloader().download("downloadDocument.action?docId=<s:property	value="campaign.attachments[#attachments.index].id" />");
			                    });	
			                 });							    
							</script>
							</td>
							</tr>
						</s:iterator>
						</table>	
				</td>
				</s:if>
			</authz:ifUserNotInRole>
			</tr>
			
	</table>
 </div> 
</div>
