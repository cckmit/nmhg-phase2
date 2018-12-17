<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

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
  .bgColor td{
  padding-bottom:10px;
  }
  </style>
<u:body smudgeAlert="false">

<div dojoType="dijit.layout.BorderContainer" style="width: 100%; height: 100%;overflow-x:hidden;overflow-y:auto;" id="root">
    <div dojoType="dijit.layout.ContentPane" region="center" id="content">

	<form action="validatePreDefinedPartReturnSearchFields.action" method="POST" >
	<s:hidden name="context" />
	<s:hidden name="queryId"/>
	    <div>
	    	<u:actionResults/>
			<s:fielderror theme="xhtml" />
	    </div>
		<div class="policy_section_div" style="width:100%">
		<div  class="section_header">
        	  		<s:text name="label.common.partReturnSearch"/>
       			</div>
		<table width="99%"  border="0" cellspacing="0" cellpadding="0" class="bgColor">

			<tr>
			    <s:if test="(getLoggedInUser().businessUnits).size>1">
			 	 <td class="searchLabel labelStyle"><s:text name="label.common.businessUnit" />:</td>
			 	  <td>
		 	 	    <s:iterator value="businessUnits" status="buItr">
		 	 	        <s:if test="partReturnSearchCriteria.selectedBusinessUnits[#buItr.index] != null" >
		 	 	       	 	<input type="checkbox"
			 				       name="partReturnSearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		        value="<s:property value="name" />" checked="true"/>
				 	 		        <s:property value="name"/>

			 			 </s:if>
		 				 <s:else>
			 				<input type="checkbox"
			 				       name="partReturnSearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		       value="<s:property value="name" />" />
				 	 		       <s:property value="name"/>
			 			</s:else>
		 	 	   </s:iterator>
	   	 	 	 </td>
	   	 	    </s:if>
	   		</tr>
	   		<s:if test="loggedInUserAnInternalUser">
	   			<authz:ifUserNotInRole roles = "receiverLimitedView, inspectorLimitedView">
					<tr>
					 	 <td  width="20%" class="searchLabel labelStyle"><s:text name="label.common.dealerName" />:</td>
		       	 	 	 <td>
		       	 	 	 	<s:textfield name="partReturnSearchCriteria.dealerName" id="dealerName" size="40"/>
		       	 	 	 </td>
					</tr>
					<tr>
					 	 <td class="searchLabel labelStyle"><s:text name="label.common.dealerNumber" />:</td>
		       	 	 	 <td>
		       	 	 	 	<s:textfield name="partReturnSearchCriteria.dealerNumber" id="dealerNumber"/>
		       	 	 	 </td>
					</tr>
				</authz:ifUserNotInRole>
			</s:if>
			<s:else>
				<s:hidden name="partReturnSearchCriteria.dealerNumber" id="dealerNumber" value="%{loggedInUsersDealership.serviceProviderNumber}"/>
			</s:else>
			<tr>
			 	 <td class="searchLabel labelStyle"><s:text name="label.common.claimNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partReturnSearchCriteria.claimNumber" id="claimNumber"/>
       	 	 	 </td>
			</tr>
			<tr>
						<td class="searchLabel labelStyle"><s:text name="columnTitle.viewClaim.claimStatus"/>:</td>
						<td class="searchLabel labelStyle"><s:select name="partReturnSearchCriteria.claimStatus"
						 id="partReturnSearchCriteria.claimStatus" list="claimsStatus" multiple="true" size="5" 
						 listKey="state" listValue="state"/></td>
						 <td colspan="2"/>
			</tr>
			<tr>
			 	 <td  class="searchLabel labelStyle"><s:text name="label.common.serialNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partReturnSearchCriteria.serialNumber" id="serialNumber" />
       	 	 	 </td>
       	 	</tr>


			<tr>
			 	 <td class="searchLabel labelStyle"><s:text name="label.partReturnConfiguration.trackingNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partReturnSearchCriteria.trackingNumber" id="trackingNumber"/>
       	 	 	 </td>
			</tr>
			<tr>
						<td class="searchLabel labelStyle"><s:text name="label.common.status"/>:</td>
						<td class="searchLabel labelStyle"><s:select theme="twms" name="partReturnSearchCriteria.status"
						 id="partReturnSearchCriteria.status" list="partReturnStatus"
						 listKey="status" listValue="status" emptyOption="true"/></td>
						 <td colspan="2"/>
			</tr>
			<tr>
						<td class="searchLabel labelStyle"><s:text name="label.partReturn.returnToLocation"/>:</td>
						<td class="searchLabel labelStyle"><s:select theme="twms" name="partReturnSearchCriteria.returnToLocation"
						 id="partReturnSearchCriteria.returnToLocation" list="partReturnLocation" emptyOption="true"  /></td>
						 <td colspan="2"/>
			</tr>
			
			<tr>
			    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.dueDate"/>:</td>
			    <td class="searchLabel labelStyle">
			        <s:text name="label.common.from"/>:
			    </td>
			    <td><sd:datetimepicker name='partReturnSearchCriteria.fromDate' id='partReturnSearchCriteria.fromDate' value='%{partReturnSearchCriteria.fromDate}' />
			    </td>
			</tr>
			<tr>
			    <td></td>
			    <td class="searchLabel labelStyle" style="padding-left:16px;" >
			        <s:text name="label.common.to"/>:
			    </td>
			    <td><sd:datetimepicker name='partReturnSearchCriteria.toDate' id='partReturnSearchCriteria.toDate' value='%{partReturnSearchCriteria.toDate}' />
			    </td>
			
			</tr>

		    <%-- This will be only for warranty processor, inspector and recovery processor --%>
		    <authz:ifUserInRole roles = "processor, recoveryProcessor, inspectorLimitedView">
                <tr>
                      <td  style="vertical-align: center;" class="searchLabel labelStyle"><s:text name="label.partInventory.partNumber"/>:</td>
                      <td style="vertical-align: center;" class="searchLabel labelStyle"><s:textfield name="partReturnSearchCriteria.partNumber" id="partNumber" />
                      <s:checkbox name="partReturnSearchCriteria.scrapped" id="scrapped" > </s:checkbox> Scrapped </td><td colspan="2"/>
                </tr>
            </authz:ifUserInRole>
            <s:if test="!isBuConfigAMER()">
	            <tr>
	                  <td  style="vertical-align: center;" class="searchLabel labelStyle"><s:text name="label.partReturn.wpra"/>:</td>
	                  <td style="vertical-align: center;" class="searchLabel labelStyle"><s:textfield name="partReturnSearchCriteria.wpraNumber" id="wpraNumber" />
	            </tr>
            </s:if>
			<tr >
				<td class="searchLabel labelStyle" align="right" style="padding-right:12px;" >
				<s:text name="button.common.saveSearch" />
					<s:checkbox	cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
							value="notATemporaryQuery" onclick="enableText()"
							>
					</s:checkbox>
					</td>
				<td class="label" valign="bottom" align="center">
				<!-- Fix for NMHGSLMS-992 -->
					<s:if test="null != searchQueryName">
						<s:textfield name="searchQueryName" id="searchQueryName"></s:textfield>
					</s:if>
					<s:else>
						<s:textfield name="searchQueryName" disabled="true" id="searchQueryName" value="Name of the Query"></s:textfield>
					</s:else>					
				</td>
		</tr>
		<tr>
		<td>&nbsp;</td>
		<td><s:reset label="reset" cssClass="buttonGeneric"></s:reset>
					<s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"  /></td>
		</tr>
		</table>
		</div>
	</form>
	</div>
</div>
</u:body>
</html>