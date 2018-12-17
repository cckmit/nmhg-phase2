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
<%@page import="tavant.twms.domain.campaign.CampaignAdminService"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <s:head theme="twms"/>
    <title><s:text name="title.common.warranty"/></title>
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
         dojo.require("dijit.layout.LayoutContainer");
         dojo.require("dijit.layout.ContentPane");
         dojo.require("twms.widget.Dialog");
         dojo.require("dijit.form.CheckBox");
    </script>
    <script type="text/javascript">
        function openItemSearchView(event, dataId) {
            var url = "campaignItemsSearch.action?campaign=<s:property value="campaign"/>";
            var tabLabel = "Campaign Item Search View ";
            parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : ""});
        }
    </script>
    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <%@include file="/i18N_javascript_vars.jsp"%>

    <style type="text/css">
        h3 {
            color: #555555;
            font-size: 10pt;
            font-weight: bold;
            padding-left: 5px;
            margin-bottom: 0px;
        }
    </style>
</head>
<u:body>

	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<h3><s:text name="label.campaign.step2Of2"/></h3>
		<u:actionResults/>
		<s:form theme="twms" method="post" action="save_campaign" id="saveCampaign" validate="true">
			<s:hidden name="campaign"/>
			<s:push value="campaign">
				<!-- Campaign Info -->
				<jsp:include page="read/campaignInfo.jsp" flush="true" />
				<!-- Campaign Affected Serial Numbers  -->
				<jsp:include page="read/campaignFilterData.jsp" flush="true" />
			</s:push>

			<!-- Campaign Service details -->
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@LABOR_COST_CATEGORY_CODE)">
				<jsp:include page="campaignServiceDetails.jsp" flush="true"/>
			</s:if>	
			<!-- Campaign Parts To Replace  -->
			<s:if test="partsReplacedInstalledSectionVisible" >
				
				 <s:if test="buPartReplaceableByNonBUPart">
	 					<jsp:include page="replacedInstalledOEMParts.jsp" flush="true" />
 				 </s:if>
				 <s:else>
					<jsp:include page="removedInstalledOnlyOEMParts.jsp" flush="true" />
					<jsp:include page="nonOemParts.jsp" flush="true" />
				 </s:else>	
			</s:if>			
			<s:else>
			   <jsp:include page="campaignParts.jsp" flush="true" />
			</s:else>		
			
			<!-- Misc Parts -->
			<s:if test="configuredCostCategories.get(@tavant.twms.domain.claim.payment.CostCategory@MISC_PARTS_COST_CATEGORY_CODE)">
				<jsp:include page="miscParts.jsp" flush="true"/>
			</s:if>	

			
			<!-- Campaign Other Incidentals -->
			<jsp:include page="campaignIncidentals.jsp" flush="true"/>
			
			<!-- Campaign Contract Supplier Details -->			
			<jsp:include page="supplierRecoveryData.jsp" flush="true"/>
			
			<!-- Campaign Related Attachments-->
			<jsp:include page="campaignAttachments.jsp" flush="true"/>
			
			<jsp:include page="campaignFileUploadDialog.jsp"/>
			
			<jsp:include page="campaignComments.jsp"/>
			
			<jsp:include page="campaignNationalAccounts.jsp"/>
			
			<jsp:include page="campaignAudits.jsp"/>
			<div align="center">

				<s:if test="campaign.status == 'Inactive'">
					<s:submit value="%{getText('button.campaign.activateCampaign')}" cssClass="buttonGeneric" action="activate_campaign" />
				</s:if>
				<s:else>
				<s:submit value="%{getText('button.common.previous')}" cssClass="buttonGeneric" action="save_for_previous" />
				<s:submit value="%{getText('button.common.save')}" cssClass="buttonGeneric" action="save_campaign" />

					<s:if test="campaign.status == 'Active'">
						<s:submit value="%{getText('button.campaign.deActivateCampaign')}" cssClass="buttonGeneric" action="deactivate_campaign" />
					</s:if>
				
					<s:if test="attachOrDelete == 'delete'">
					<s:hidden name="attachOrDelete"/>
					     <s:submit value="%{getText('button.campaign.removeItemsFromCampaign')}" cssClass="buttonGeneric" action="delete_items_from_campaign"/>
					</s:if>
					<s:else>
						 <s:submit id = "assignCampaignForAllCampaignItems" value="%{getText('button.campaign.assignCampaigForAllItems')}" cssClass="buttonGeneric" action="assignCampaignForAllCampaignItems"/>
					</s:else>
				</s:else>

				<input type="button" class="buttonGeneric"  style="display:none"  onclick="openItemSearchView();" name="View Items" value="<s:text name='button.common.viewItems'/>"/>
			</div>	
		</s:form>
	</div>
</u:body>
<authz:ifPermitted resource="warrantyAdminFieldProductImprovementReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('saveCampaign')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('saveCampaign'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
