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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html>
<head>
<s:head theme="twms" />
<title><s:text name="title.viewClaim.draftClaimDetail" /></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="claimForm.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />

<script type="text/javascript">
	dojo.require("twms.widget.TitlePane");
	dojo.require("twms.widget.Dialog");
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("twms.widget.ValidationTextBox");
	dojo.require("twms.widget.Tree")
	dojo.addOnLoad(function() {
		top.publishEvent("/refresh/folderCount", {
			action : "claimFolders.action"
		});
	});
</script>
<%@ include file="/pages/secure/claims/claculateTravelAndTransportation.jsp"%>
<%@ include file="/i18N_javascript_vars.jsp"%>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; overflow-y: auto;" id="root">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
			<u:actionResults />
			<h3>
				<s:text name="label.viewClaim.page2of2" />
			</h3>

			<s:form method="post" theme="twms" validate="true" id="claim_form"
				name="claim_submit" action="claim_submit.action">
				<s:hidden name="id" />
				<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" />
				<s:if test="task.claim.type.type == 'Campaign'">
					<s:hidden name="claim.type.type" value="Campaign" />
					<s:hidden name="campaign" />
				</s:if>
				<s:if test="task.claim.itemReference.serialized">
					<s:hidden name="forSerialized" value="true" />
				</s:if>
				<s:if test="!task.claim.itemReference.serialized">
					<s:hidden name="forSerialized" value="false" />
				</s:if>
				<div>
					<div dojoType="twms.widget.TitlePane"
						title="<s:text name="title.viewClaim.claimDetails" />"
						labelNodeClass="section_header">

						<s:push value="task">
							<s:if test="processorReview">
								<jsp:include flush="true"
									page="../../claims/forms/common/supplier_header.jsp" />
							</s:if>
							<s:else>
								<jsp:include flush="true" page="../common/read/header.jsp" />
							</s:else>
						</s:push>
					</div>
				</div>

				<s:if test="task.claim.type.type== 'Campaign'">
					<s:push value="task.claim.campaign">
						<jsp:include
							page="/pages/secure/admin/campaign/read/campaignDetails.jsp"
							flush="true" />
					</s:push>
					<s:if test="!getJSONifiedCampaignAttachmentList().equals('[]')">
						<jsp:include
							page="/pages/secure/admin/campaign/read/campaignAttachments.jsp"
							flush="true" />
					</s:if>
				</s:if>

				<s:if
					test="claimSpecificAttributes != null && ! claimSpecificAttributes.empty">
					<div>

						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="label.viewClaim.ClaimAttributes"/>"
							id="attr_details" labelNodeClass="section_header" open="true">
							<jsp:include flush="true"
								page="../common/write/claimSpecificAttributes.jsp" />
						</div>

					</div>
				</s:if>

				<div>
					<div dojoType="twms.widget.TitlePane"
						title="<s:text name="title.viewClaim.servicingLocation"/>"
						id="servicing_locations" labelNodeClass="section_header"
						open="true">
						<jsp:include flush="true"
							page="../common/write/servicingLocation.jsp" />
					</div>

				</div>
				<div>
					<s:if
						test="task.partsClaim && task.claim.partItemReference.serialized">
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.viewClaim.PartDetails"/>"
							id="part_details" labelNodeClass="section_header" open="true">
							<s:push value="task">
								<jsp:include flush="true"
									page="../common/read/serializedPart.jsp" />
							</s:push>

						</div>
					</s:if>
				</div>
				<div>
					<s:if
						test="!task.partsClaim || (task.claim.partInstalled && task.claim.competitorModelBrand.isEmpty())">
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.viewClaim.equipmentDetails"/>"
							id="equipment_details" labelNodeClass="section_header"
							open="true">
							<s:push value="task">
								<jsp:include flush="true" page="../common/read/equipment.jsp" />
							</s:push>
							<s:if
								test="((task.claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')
		 && isMatchReadApplicable()) || task.claim.matchReadInfo.id!=null) && task.claim.type.type != 'Campaign' && task.claim.itemReference.isSerialized()">
								<jsp:include flush="true"
									page="../common/write/matchReadInclude.jsp" />
							</s:if>
						</div>
					</s:if>
				</div>
				<s:if test="task.claim.type.type != 'Campaign'">

					<jsp:include flush="true"
						page="../common/read/availablePoliciesPopup.jsp" />

					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.viewClaim.failureDetails"/>"
							id="failure_details" labelNodeClass="section_header">
							<s:if
								test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand==null || task.claim.competitorModelBrand.isEmpty()))">

								<jsp:include flush="true" page="../common/write/failure.jsp" />
							</s:if>
							<s:else>
								<jsp:include flush="true" page="../common/write/failure2.jsp" />
							</s:else>
						</div>
					</div>
				</s:if>

				<s:if test="isAlarmCodesSectionVisible()">
					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.required.alarmCode"/>"
							id="alarmcode_details" labelNodeClass="section_header">
							<jsp:include flush="true"
								page="../common/write/claimAlarmCode.jsp" />

						</div>
					</div>
				</s:if>


				<s:if test="task.claim.laborConfig">
					<s:hidden name="task.claim.laborConfig" value="true" />
					<s:if
						test="task.partsClaim && (!task.claim.partInstalled || (task.claim.competitorModelBrand!=null && !task.claim.competitorModelBrand.isEmpty()))">
						<div>
							<div dojoType="twms.widget.TitlePane"
								title="<s:text name="title.viewClaim.laborInformation"/>"
								id="labor_information" labelNodeClass="section_header">
								<jsp:include flush="true"
									page="../common/write/labor_detail_part.jsp" />
							</div>
						</div>
					</s:if>
					<s:elseif
						test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand==null || task.claim.competitorModelBrand.isEmpty()))">
						<div>
							<div dojoType="twms.widget.TitlePane"
								title="<s:text name="title.viewClaim.serviceDetails"/>"
								id="service_details" labelNodeClass="section_header">
								<jsp:include flush="true"
									page="../common/write/serviceProcedures.jsp" />
							</div>
						</div>
					</s:elseif>
				</s:if>
				<s:else>
					<s:hidden name="task.claim.laborConfig" value="false" />
				</s:else>
				<s:if test="!partsReplacedInstalledSectionVisible">
					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.viewClaim.componentsReplaced"/>"
							id="components_replaced" labelNodeClass="section_header">
							<jsp:include flush="true" page="../common/write/oemParts.jsp" />
							<s:if
								test="!task.partsClaim || (task.claim.partInstalled && task.claim.competitorModelBrand.isEmpty())">
								<jsp:include flush="true" page="../common/write/nonOemParts.jsp" />
							</s:if>
							<s:if
								test="(!task.partsClaim || (task.claim.partInstalled && task.claim.competitorModelBrand.isEmpty())) && task.claim.miscPartsConfig">
								<jsp:include flush="true" page="../common/write/miscParts.jsp" />
							</s:if>
						</div>
					</div>
				</s:if>
				<s:elseif test="buPartReplaceableByNonBUPart">
					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.viewClaim.componentsReplaced"/>"
							id="parts_replaced_installed" labelNodeClass="section_header">
							<jsp:include flush="true"
								page="../common/write/replacedInstalledOEMParts.jsp" />
						</div>
					</div>
				</s:elseif>

				<s:else>
					<!--For Parts claim -->
					<s:if
						test="task.partsClaim && (!task.claim.partInstalled || (task.claim.partInstalled && (!task.competitorModelBrand.isEmpty() || !task.claim.getItemReference().isSerialized())))">
						<div>
							<div dojoType="twms.widget.TitlePane"
								title="<s:text name="title.viewClaim.componentsReplaced"/>"
								id="components_replaced" labelNodeClass="section_header">

								<s:if
									test="task.claim.partItemReference.referredInventoryItem!=null && task.claim.partInstalled">
									<!-- For serialized parts installed on Competitor model/Non-Serialized host the part-off and part-on will be in write mode with values defaulted with the part given on page1 and non serialized parts can be added as part-off and part-on -->
									<jsp:include flush="true"
										page="../common/write/installedOemParts.jsp" />
								</s:if>
								<s:elseif
									test="task.claim.partItemReference.referredInventoryItem==null && task.claim.partInstalled">
									<!-- For non-serialized parts installed on Competitor model/Non-Serialized host the part-off and part-on will be in write mode with values defaulted with the part given on page1 -->
									<s:set name="replacedPartReadOnly" value="true" />
									<jsp:include flush="true"
										page="../common/write/replacedInstalledOnlyOEMParts_edit.jsp" />
								</s:elseif>
								<s:else>
									<!-- For Parts not installed the part-off and part-on will be read only with values defaulted with the part given on page1 -->
									<s:push value="task">
										<s:set name="replacedPartReadOnly" value="true" />
										<jsp:include flush="true"
											page="../common/read/replacedInstalledOnlyOEMParts.jsp" />
									</s:push>
								</s:else>
							</div>
							<s:if test="task.claim.partInstalled"><jsp:include
									flush="true" page="../common/write/nonOemParts.jsp" />
								<s:if test="task.claim.miscPartsConfig">
									<jsp:include flush="true" page="../common/write/miscParts.jsp" />
								</s:if>
							</s:if>
						</div>
					</s:if>
					<!--For machine claim -->
					<s:else>
						<div>
							<div dojoType="twms.widget.TitlePane"
								title="<s:text name="title.viewClaim.componentsReplaced"/>"
								id="parts_replaced_installed" labelNodeClass="section_header">
								<jsp:include flush="true"
									page="../common/write/installedOemParts.jsp" />
								<jsp:include flush="true" page="../common/write/nonOemParts.jsp" />
								<s:if test="task.claim.miscPartsConfig">
									<jsp:include flush="true" page="../common/write/miscParts.jsp" />
								</s:if>
							</div>
						</div>
					</s:else>
				</s:else>

				<div>
					<div dojoType="twms.widget.TitlePane"
						title="<s:text name="title.viewClaim.travelDetails"/>"
						labelNodeClass="section_header">
						<jsp:include flush="true" page="../common/write/travelDetails.jsp" />
					</div>



					<div>
						<s:if test="incidentalsAvaialable">
							<div dojoType="twms.widget.TitlePane"
								title="<s:text name="title.viewClaim.miscellaneous"/>"
								labelNodeClass="section_header">
								<jsp:include flush="true"
									page="../common/write/otherIncidentals.jsp" />
							</div>
						</s:if>
					</div>

					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.newClaim.claimDescription"/>"
							id="claim_description" labelNodeClass="section_header">
							<div dojoType="dijit.layout.ContentPane">
								<jsp:include flush="true" page="../common/write/description.jsp" />
							</div>
						</div>
					</div>

					<div>
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="title.newClaim.supportDocs"/>"
							labelNodeClass="section_header">
							<jsp:include flush="true"
								page="../common/write/uploadAttachments.jsp" />
						</div>
					</div>

					<jsp:include page="../common/write/fileUploadDialog.jsp" />

					<jsp:include flush="true" page="../common/write/validations.jsp" />
					<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
					<table class="buttons" style="margin-top: 10px;">
						<tr>
							<td>
								<center>
									<t:button value="%{getText('button.common.cancel')}"
										id="cancelButton" label="%{getText('button.common.cancel')}" />
									<script type="text/javascript">
										dojo.addOnLoad(function() {
											dojo.connect(dojo
													.byId("cancelButton"),
													"onclick", closeMyTab);
										});
									</script>
									<s:if test="!task.claim.foc">
										<t:button value="%{getText('button.newClaim.goToPage1')}"
											id="goToPage1"
											label="%{getText('button.newClaim.goToPage1')}" />
									</s:if>
									<s:if test="!task.claim.foc">
										<t:button value="%{getText('button.common.delete')}"
											label="%{getText('button.common.delete')}"
											id="claimDeleteDraftButton" />
									</s:if>
									<t:button value="%{getText('button.common.save')}"
										label="%{getText('button.common.save')}" id="saveDraftButton" />
									<span id="validateButtonDiv"> <s:if
											test="task.claim.type.type == 'Campaign'">
											<s:submit value="%{getText('button.common.validate')}"
												type="input" action="campaign_draft_claim_validate"
												id="validateButton" />
										</s:if> <s:elseif
											test="!task.partsClaim || (task.claim.partInstalled && task.claim.competitorModelBrand.isEmpty())">
											<!-- Doesn't submit the form. We intercept the submit and do an xhrPost. See validations.jsp -->

											<s:submit value="%{getText('button.common.validate')}"
												action="draft_claim_validate" id="validateButton" />

										</s:elseif> <s:else>
											<s:submit value="%{getText('button.common.validate')}"
												type="input" action="parts_draft_claim_validate"
												id="validateButton" />
										</s:else>
									</span>
									<script type="text/javascript">
										function submitClaimFormData(action) {
											document.forms['claim_submit'].action = action;
											document.forms['claim_submit']
													.submit();
										}
										dojo
												.addOnLoad(function() {
													dojo
															.connect(
																	dojo
																			.byId("goToPage1"),
																	"onclick",
																	function() {
																		submitClaimFormData('claim_go_to_page_1.action');
																	});
													dojo
															.connect(
																	dojo
																			.byId("claimDeleteDraftButton"),
																	"onclick",
																	function() {
																		submitClaimFormData('claim_delete_draft.action');
																	});
													dojo
															.connect(
																	dojo
																			.byId("saveDraftButton"),
																	"onclick",
																	function() {
																		submitClaimFormData('claim_save_draft.action');
																		dojo
																				.byId("saveDraftButton").disabled = true;
																	});

												});
									</script>
								</center>
							</td>
						</tr>
					</table>
					</authz:ifNotPermitted>
			</s:form>
			<jsp:include flush="true"
				page="../common/write/thirdPartySearchPage.jsp" />
		</div>
	</div>
</u:body>
<authz:ifPermitted resource="claimsDraftClaimReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('claim_form')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('claim_form'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
