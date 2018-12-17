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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: WARRANTY ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <style type="text/css">
        .addRow {
            margin-top: -14px;
            height: 14px;
            text-align: right;
            padding-right: 17px;
        }
    </style>
</head>
 <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.ValidationTextBox");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });

    </script>

<u:body>

  <u:actionResults/>
  <s:fielderror theme="xhtml"/>
  
<authz:ifUserInRole roles = "recoveryProcessor">
<div style="color:#008000; font-weight:bold; padding-left:5px; font-size:15px"><s:if test="isCommentsExist(claim)">
			<s:text name="label.internalCommentsAvailable" />
</s:if></div>
</authz:ifUserInRole>

	<authz:ifUserInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
		<s:set name="limitedView" value="true" />
	</authz:ifUserInRole>
	<s:if test="taskName != null">
		<s:set name="expandClaimDetailsByDefault" value="false" />
	</s:if>
	<jsp:include flush="true" page="../../supplier/detail/warranty_claim_details.jsp"/>



	<s:form action="claimsReopen.action" id="baseFormId">
	<authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
    <div dojoType="twms.widget.TitlePane" title="<s:text name='title.viewClaim.recovery.cost' />"
        labelNodeClass="section_header" id="costPane" >
            <jsp:include flush="true" page="../../supplier/preview/sra_component_detail.jsp"/>
    </div>

    <div dojoType="twms.widget.TitlePane" title="<s:text name='accordion_jsp.accordionPane.supplierRecovery'/> <s:text name='label.claim.preview.creditMemo.sectionHeader' />"
    labelNodeClass="section_header" id="currentCreditPane" >
        <jsp:include flush="true" page="../../supplier/detail/recovery_claim_current_credit_memo_detail.jsp"/>
    </div>

    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.history"/>"
        labelNodeClass="section_header" id="recoveryHistory" open="true">
            <jsp:include flush="true" page="../../supplier/detail/recovery_claim_history.jsp" />
    </div>

    <div dojoType="twms.widget.TitlePane" title="<s:text name='title.viewClaim.prevoiusCreditMemoDetails' />"
        labelNodeClass="section_header" id="creditPane" >
            <jsp:include flush="true" page="../../supplier/detail/recovery_claim_credit_memo_details.jsp"/>
    </div>
	</authz:ifUserNotInRole>
	<jsp:include page="sra_claims_partdetails.jsp"/>

    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
        labelNodeClass="section_header" id="partReturnAuditHistory" open="<s:property value='#expandClaimDetailsByDefault'/>">
            <s:push value="recoveryClaim">
            <jsp:include flush="true" page="../../claims/forms/common/read/partsAuditHistory.jsp"/>
            </s:push>
    </div>

    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.supplier.part.recovery.history" />"
                          labelNodeClass="section_header" open="true" id="partsRecoveryHistoryPane">
         <jsp:include flush="true" page="../../supplier/detail/partRecoveryAudit.jsp"/>
    </div>
     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.recoveryClaim.supportDocs"/>" 
			labelNodeClass="section_header" open="<s:property value='#expandClaimDetailsByDefault'/>" id="attachmentsPane">
		<jsp:include flush="true" page="./uploadAttachmentForRecoveryClaim.jsp"/>
	</div>

	<s:if test="recoveryClaim.recoveryClaimState.state =='Debited and Closed' ||
				recoveryClaim.recoveryClaimState.state == 'Disputed Auto Debited and Closed' ||
				recoveryClaim.recoveryClaimState.state == 'No Response Auto Debited and Closed' ||
				recoveryClaim.recoveryClaimState.state == 'Auto Closed' ||
				recoveryClaim.recoveryClaimState.state == 'No Amount To Recover Closed' ||
				recoveryClaim.recoveryClaimState.state == 'Closed Unrecovered'" >
    <s:if test="recoveryClaim.claim.state != @tavant.twms.domain.claim.ClaimState@REJECTED &&
                recoveryClaim.claim.state != @tavant.twms.domain.claim.ClaimState@DENIED &&
                recoveryClaim.claim.state != @tavant.twms.domain.claim.ClaimState@DENIED_AND_CLOSED">
	<authz:ifUserInRole roles="recoveryProcessor">
	<s:hidden name="recoveryClaim"></s:hidden>
	<s:hidden name="transitionTaken" value="Reopen"/>
	<s:if test="!recoveryClaim.flagForAnotherRecovery">
        <div class="buttonWrapperPrimary">
        <s:submit cssClass="buttonGeneric" align="center" name="Submit" value="%{getText('label.common.reopen')}"/>
        </div>
	</s:if>
	</authz:ifUserInRole>
	</s:if>
	<s:if test="disputeValid">
	<authz:ifUserInRole roles="supplier">
	<div id="separator"></div>
	<div dojoType="twms.widget.TitlePane" title="Recovery Comments" labelNodeClass="section_header">
		<s:hidden name="recoveryClaim"></s:hidden>
		<s:hidden name="transitionTaken" value="Reject"/>
	<table>
		<tr>
			<td width="5%" class="carrierLabel"><s:text name="label.supplierRecovery.supplierComments"/>:</td>
		  	<td width="35%" class="carrierLabelNormal">
		    	<t:textarea rows="4" cols="105" name="recoveryClaim.comments"
              	wrap="physical" cssClass="bodyText" maxLength="4000" value=""/>
		  	</td>
		</tr>
	</table>
	</div>
 
	<div id="separator"></div>
	<div class="buttonWrapperPrimary">
		<s:submit cssClass="buttonGeneric" align="center"
				name="Submit" value="%{getText('label.common.dispute')}"/>
	</div>

	</authz:ifUserInRole>
	</s:if>
	</s:if>
	</s:form>


	<authz:ifUserNotInRole roles="supplier">
	<s:action name="active_policies" executeResult="true">
		<s:param name="claimDetails" value="%{claim.id}"/>
	</s:action>
	<div align="center">
			<a id="recoveryClaimPrint" href="#"><s:text name="link.print.recovery.claim"/></a>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("recoveryClaimPrint"), "onclick", function(event){
						var recoveryClaimId = '<s:property value="recoveryClaim"/>';
						var thisTabLabel = getMyTabLabel();
	                    parent.publishEvent("/tab/open", {
						                    label: "Print Recovery Claim",
						                    url: "printRecoveryClaim.action?recoveryClaimId="+recoveryClaimId,
						                    decendentOf: thisTabLabel,
						                    forceNewTab: true
	                                       });
	                });
				});
			</script>
	</div>
	</authz:ifUserNotInRole>
	<authz:ifUserInRole roles="supplier,recoveryProcessor,supplierRecoveryInitiator,warrantyProcessor">
	<div align="center">
		<a id="recoveryClaimPrintSupplierView" href="#"><s:text name="link.print.recovery.claim.supplier.view"/></a>
		<script type="text/javascript">
			dojo.addOnLoad(function() {
				dojo.connect(dojo.byId("recoveryClaimPrintSupplierView"), "onclick", function(event){
					var recoveryClaimId = '<s:property value="recoveryClaim"/>';
					var thisTabLabel = getMyTabLabel();
                    parent.publishEvent("/tab/open", {
					                    label: "Print Recovery Claim",
					                    url: "printRecoveryClaimForSupplier.action?supplierView=true&recoveryClaimId="+recoveryClaimId,
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});
		</script>
</div>
	</authz:ifUserInRole>
	
<authz:ifPermitted resource="partsRecoveryAwaitingShipmentReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="partsRecoveryPredefinedSearchReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="processorRecoveryNotForRecoveryRequestReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="processorRecoveryAcceptedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="processorRecoveryAwaitingSupplierResponseReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>

</u:body>
</html>
