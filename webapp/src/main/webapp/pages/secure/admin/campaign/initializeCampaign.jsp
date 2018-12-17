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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <!-- Scripts and stylesheet for the Calendar -->
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="scripts/admin.js"></script>
    <%--TODO: can we remove this??? --%>
 <script type="text/javascript">
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("twms.widget.Dialog");
      dojo.addOnLoad(function() {
	  	    dojo.connect(dojo.byId("submitButton"), "onclick", function() {

               //Disable Save and Cancel button when submit is clicked
	  	       dojo.byId("saveButton").disabled = true;
			   dojo.byId("cancelButton").disabled = true;

			   var form = document.updateCampaign;
			   form.action = "update_campaign_criteria.action";
			   form.submit();
			});

			dojo.connect(dojo.byId("saveButton"), "onclick", function() {

               //Disable Submit and Cancel button when Save is clicked
			   dojo.byId("submitButton").disabled = true;
			   dojo.byId("cancelButton").disabled = true;

			   var form = document.updateCampaign;
			   form.action = "save_partial_campaign.action";
			   form.submit();
			});
		}); 
	  
 </script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
          type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <style type="text/css">
        h3 {
            color: #545454;
            font-size: 10pt;
            font-weight: bold;
            padding-left: 5px;
            margin-bottom: 0px;
        }
    </style>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow:auto">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<u:actionResults/>
			<!--  Campaign Definition starts here -->
			<s:form  enctype="multipart/form-data" method="POST" theme="twms" validate="true" id="updateCampaign" name="updateCampaign">
				<s:hidden name="campaign" />
				
				<h3><s:text name="label.campaign.step1Of2"/></h3>
				<!-- Campaign Info -->
				
				<jsp:include page="campaignInfo.jsp" flush="true" />
				
				<!-- Campaign Affected Serial Numbers  --> 
				<jsp:include page="campaignFilterData.jsp" flush="true" />
		
				<!-- Warnings -->
				<s:if test="!itemsNotInCampaign.size == 0 || hasWarnings() ">
					<div id="warnings" class="twmsActionResults" style="width: 100%;">
					<div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
					<table width="100%" class="grid borderForTable">
						<tr>
							<td colspan="2">
								<div class="admin_section_heading"><s:text name="label.campaign.summary"/></div>
							</td>
						</tr>
						<tr class="admin_selections">
							<td width="15%"><s:text name="upload.count.serialnumbers" /></td>
							<td width="80%"><s:property value="summary.totalCount" /></td>
						</tr>
						<tr class="admin_selections">
							<td width="15%"><s:text name="valid.count.serialnumbers" /></td>
							<td width="80%"><s:property value="summary.validCount" /></td>
						</tr>
						<tr class="admin_selections">
							<td width="15%"><s:text name="invalid.serialnumbers" /></td>
							<td width="80%" style="WORD-BREAK:BREAK-ALL;"><s:property value="summary.invalidNumbers" /></td>
						</tr>
						<s:if test="!itemsNotInCampaign.size == 0">
							<tr class="admin_selections">
							<td width="15%"><s:text name="serialnumbers.notInCampaign" /></td>
							<s:iterator value="itemsNotInCampaign" status="itemsNotInCampaign">
							<td width="80%"><s:property value="serialNumber" /></td>
							</s:iterator>
						</tr>
						</s:if>
					</table>
						<table  width="100%" >
						   <tr>
						       <td  align="left" width="60%">
								   	<span style="color: green;">
								   		<s:text name="error.campaign.removeItemsWarning" /> 
								   	</span>
							   </td>
						   </tr>	
					   </table>
					</div>
					</div>
				</s:if>
				<!-- End of Warnings section -->
		
				<!-- Validations -->
				<table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons">
				    <tbody>
				        <tr>
				          <td>
                            <center>
				               <s:submit cssClass="buttonGeneric" value="%{getText('button.common.save')}" id="saveButton"/>
				               <s:submit value="%{getText('button.common.continue')}" cssClass="buttonGeneric" id="submitButton" />
				               <s:submit cssClass="buttonGeneric" value="%{getText('button.common.cancel')}" id="cancelButton" onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
				            </center>
				          </td> 
				        </tr>
				    </tbody>
				</table>
			</s:form>
		</div>
	</div>
	
</u:body>
<authz:ifPermitted resource="warrantyAdminFieldProductImprovementReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('updateCampaign')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('updateCampaign'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
