<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
 </head>
   <script type="text/javascript"> 
        dojo.require("dijit.layout.ContentPane"); 	  	
      	dojo.require("dijit.layout.BorderContainer");
      	function enableText()
		{
			document.getElementById("searchQueryName").disabled=!document.getElementById("notATemporaryQuery").checked;
			if(document.getElementById("notATemporaryQuery").checked){
			document.getElementById("searchQueryName").value='';
			}
			return true;
		}
  </script>
  <style>
  .bgColor tr td{
  padding-bottom:10px;
  }
  </style>
<u:body>

    <div dojoType="dijit.layout.BorderContainer" style="width: 100%; height: 100%;" id="root" >
    <div dojoType="dijit.layout.ContentPane" region="client" id="content">
	<form action="validatePreDefinedCampaignSearchFields.action?context=CampaignSearch" method="POST" >
	<s:hidden name="savedQueryId"/>
	    <div>
	    	<u:actionResults/>
			<s:fielderror theme="xhtml" />
	    </div>
		<div class="policy_section_div" style="width:100%">
		<div class="section_header"><s:text name="title.campaign.search" /></div>
		<table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor"  >
		<tr>
		     <s:if test="(getLoggedInUser().businessUnits).size>1">
			 	 <td class="searchLabel labelStyle"><s:text name="label.common.businessUnit" />:</td>
			 	 <td>
		 	 	    <s:iterator value="businessUnits" status="buItr">
		 	 	        <s:if test="campaignCriteria.selectedBusinessUnits[#buItr.index] != null" >
		 	 	       	 	<input type="checkbox" 
			 				       name="campaignCriteria.selectedBusinessUnits"
				 	 		        value="<s:property value="name" />" checked="true"/>
				 	 		        <s:property value="name"/>
			 				 		    
			 			 </s:if>
		 				 <s:else> 				 
			 				<input type="checkbox" 
			 				       name="campaignCriteria.selectedBusinessUnits"
				 	 		       value="<s:property value="name" />"/> 
				 	 		       <s:property value="name"/>
			 			</s:else>
		 	 	   </s:iterator>	 
	   	 	 	 </td>
	   	 	 </s:if>
             <s:else>
		 	 	    <s:iterator value="businessUnits" status="buItr">
		 	 	       	 	<input type="hidden" 
			 				       name="campaignCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		        value="<s:property value="name" />" />
		 	 	   </s:iterator>	 
             </s:else>     
	    </tr>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.class" />:</td>
			<td class="searchLabel"><s:select
				name="campaignCriteria.campaignClass" id="campaignClass"
				list="campaignClasses" listKey="code" listValue="name" emptyOption="true" 
				headerValue="%{campaignCriteria.campaignClass}"/></td>
			
		</tr>
	<s:if test="isInternalUser()">
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.dealerNumber"/>:</td>
			<td class="searchLabel"><s:textfield
				name="campaignCriteria.dealerNumber" id="dealerNumber" /></td>
			
		</tr>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.dealerName"/>:</td>
			<td class="searchLabel"><s:textfield
				name="campaignCriteria.dealerName" id="dealerName" /></td>
			
		</tr>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.dealerGroup"/>:</td>
			<td class="searchLabel"><sd:autocompleter
				name="campaignCriteria.dealerGroup" id="dealerGroup"  href='suggest_org_dealer_groups.action' showDownArrow='false'/></td>
			
		</tr>
		
	</s:if>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.serialNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="campaignCriteria.serialNumber" id="serialNumber" /></td>
			
		</tr>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.campaign.search.campaignCode" />:</td>
			<td class="searchLabel"><s:textfield
				name="campaignCriteria.campaignCode" id="campaignCode" /></td>
			
		</tr>
		<tr>
						<td class="searchLabel labelStyle"><s:text name="label.campaign.search.status" />:</td>
						<td class="searchLabel"><s:select name="campaignCriteria.campaignStatus"
						   id="campaignCriteria.campaignStatus" list="campaignStatus" emptyOption="true" 
						   headerValue="%{campaignCriteria.campaignStatus}"   />
						</td>
						 <td colspan="2"/>
		</tr>
		
		<tr>
						<td class="searchLabel labelStyle"><s:text name="label.campaign.search.Reason" />:</td>
						<td class="searchLabel"><s:select
				              name="campaignCriteria.campaignReason" id="campaignReason"
				              list="fieldModificationInventoryStatus" listKey="code" listValue="description" emptyOption="true" 
				             headerValue="%{campaignCriteria.campaignReason}"/></td>
				         
		</tr>	
		
							   		
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text name="label.campaign.search.campaignAgeing" />:</td></tr>
			<tr>
			<td class="searchLabel labelStyle" align="right" style="padding-right:12px;">
			<s:text name="label.campaign.search.From" /> :</td>
			<td class="searchLabel" style="padding-bottom:2px;"><s:select
				name="campaignCriteria.minRangeCampaignAge"
				id="campaignCriteria.minRangeCampaignAge" list="#{'1 Month':'1','2 Months':'2'
				,'3 Months':'3','4 Months':'4','5 Months':'5','6 Months':'6','7 Months':'7','8 Months':'8','9 Months':'9','10 Months':'10','11 Months':'11','12 Months':'12'}"
				 listKey="value" listValue="key" emptyOption="true" headerValue="%{campaignCriteria.minRangeCampaignAge}"/></td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle" align="right" style="padding-right:12px;"><s:text name="label.campaign.search.To" /> :</td>
			<td class="searchLabel"><s:select
				name="campaignCriteria.maxRangeCampaignAge"
				id="campaignCriteria.maxRangeCampaignAge" list="#{'1 Month':'1','2 Months':'2'
				,'3 Months':'3','4 Months':'4','5 Months':'5','6 Months':'6','7 Months':'7','8 Months':'8','9 Months':'9','10 Months':'10','11 Months':'11','12 Months':'12'}"
				 listKey="value" listValue="key" emptyOption="true" headerValue="%{campaignCriteria.maxRangeCampaignAge}"/></td>
		</tr>
		<tr>		
		<td width="20%" class="searchLabel labelStyle" align="right" style="padding-right:12px;" > 
		<s:text name="button.common.saveSearch" /> 
		<s:checkbox	cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
							value="notATemporaryQuery" onclick="enableText()">
					</s:checkbox>
		</td>
				<td class="label" valign="bottom"  style="padding:5px;">				
				<!-- Fix for NMHGSLMS-992 -->
				<s:if test="null != searchQueryName">
					<s:textfield id="searchQueryName" name="searchQueryName" ></s:textfield>
				</s:if>
				<s:else>
					<s:textfield id="searchQueryName" name="searchQueryName" disabled="true" value="Name of the Query" ></s:textfield>
				</s:else>					
				</td>
		</tr>
		<tr>
		<td>&nbsp;</td>
		<td>
		<s:reset label="reset" cssClass="buttonGeneric"></s:reset>
		<s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"  />
		</td>
		</tr>
	  </table>

	</div>	
	</form>
	</div>
	</div>
	</u:body>
</html>
