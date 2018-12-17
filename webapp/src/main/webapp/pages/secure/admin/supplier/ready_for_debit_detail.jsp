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
    <u:stylePicker fileName="form.css"/>
    
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


  <div id="separator"></div>
	
<u:actionResults/>
  <!--
  The above line was added to show error message properly as following output a simple line with no CSS applied.
  This was done as part of fix for SLMSPROD-593 
  <s:actionerror theme="xhtml"/>
  <s:fielderror theme="xhtml"/>
  <s:actionmessage theme="xhtml"/>

    <authz:ifUserNotInRole roles="supplier">
<div style="color:#008000; font-weight:bold; padding-left:5px; font-size:15px"><s:if test="isCommentsExist(claim)">
	<s:text name="label.internalCommentsAvailable" />
</s:if></div></authz:ifUserNotInRole>

  -->

  <table width="100%" border="0" cellspacing="0" cellpadding="0" >
      </table>
      
<div dojoType="dijit.layout.LayoutContainer"
     style="height:98%; margin: 0; padding: 0; overflow-X: hidden; overflow-Y: auto; width: 100%">      

	<div dojoType="dijit.layout.ContentPane">

 	<jsp:include flush="true" page="../../supplier/detail/warranty_claim_details.jsp"/>
	
	<div dojoType="dijit.layout.ContentPane" >
		<div dojoType="twms.widget.TitlePane" title="<s:text name="label.partReturnAudit.PartHistory"/>"
			labelNodeClass="section_header" id="partReturnAuditHistory" open="true">
				<s:push value="recoveryClaim">
                <jsp:include flush="true" page="../../claims/forms/common/read/partsAuditHistory.jsp"/>
             </s:push>
		</div>
	</div>

	<s:form action="readyForOfflineDebit_submit.action" theme="twms">
		<div dojoType="dijit.layout.ContentPane" >
			<div dojoType="twms.widget.TitlePane" title="<s:text name='title.viewClaim.recovery.cost' />"
				labelNodeClass="section_header" id="costPane" >
					<jsp:include flush="true" page="../../supplier/detail/sra_component_detail.jsp"/>
			</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" >
		     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.recovery.history"/>"
		         labelNodeClass="section_header" id="historyPane" open="true">
		             <jsp:include flush="true" page="../../supplier/detail/recovery_claim_history.jsp"/>
		     </div>
		</div>
	
		<!-- TKTSA-934 & TSA-1070 Bug fix : Clearing the value in comments string -->
		<s:hidden name="recoveryClaim.comments" value=""/>
		<s:hidden name="claim" value="%{recoveryClaim.claim}"/>
		<s:hidden name="selectedBusinessUnit" value="%{recoveryClaim.businessUnitInfo.name}"/>
		<jsp:include page="sra_claims_partdetails.jsp"/>
		<jsp:include page="recovery_claim_debit_notification_details.jsp"/>
		  <div dojoType="twms.widget.TitlePane" title="<s:text name="title.recoveryClaim.supportDocs"/>" 
			labelNodeClass="section_header" open="true" id="attachmentsPane">
		<jsp:include flush="true" page="./uploadAttachmentForRecoveryClaim.jsp"/>
	</div>
	
		
<div align="center" class="spacingAtTop">		
		<s:if test="recoveryClaim.recoveryClaimState.state =='Ready for Debit' || 
					recoveryClaim.recoveryClaimState.state == 'No Response Auto Debited' ||
					recoveryClaim.recoveryClaimState.state == 'Disputed and Auto Debited'">
		<authz:ifUserInRole roles="recoveryProcessor">
			<s:hidden name="recoveryClaim"/>
			<s:hidden name="transitionTaken" value="goToCheckOfflineDebitState"/>
			<s:submit cssClass="buttonGeneric" align="center" name="Submit" value="%{getText('button.common.submit')}"/>
		</authz:ifUserInRole>                                              
		</s:if>
		</div>
	</s:form>
	
	</div>
	
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
					                    url: "printRecoveryClaim.action?supplierView=true&recoveryClaimId="+recoveryClaimId, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                });
			});	
		</script>
</div>
	</authz:ifUserInRole>	
</div>
</u:body>
</html>
