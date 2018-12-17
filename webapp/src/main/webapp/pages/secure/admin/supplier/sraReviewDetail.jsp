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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
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
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });        
        
</script>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="height: 100%; margin: 0; padding: 0; overflow-X: auto; overflow-Y: auto; width: 100%">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
  <authz:ifUserNotInRole roles="supplier">
<div style="color:#008000; font-weight:bold; padding-left:5px; font-size:15px"><s:if test="isCommentsExist(claim)">
			<s:text name="label.internalCommentsAvailable" />
</s:if></div></authz:ifUserNotInRole>

<u:actionResults/>

 	
<s:form id='supplierRecoveryForm' method="post" theme="twms" validate="true">
<s:set name="expandClaimDetailsByDefault" value="false" />
<jsp:include flush="true" page="../../supplier/detail/warranty_claim_details.jsp"/>

    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.recovery.cost"/>"
		labelNodeClass="section_header" id="costPane" open="true">
			<jsp:include flush="true" page="../../supplier/detail/sra_component_detail.jsp"/>
	</div>
     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.recovery.history"/>"
         labelNodeClass="section_header" id="historyPane" open="true">
           <jsp:include flush="true" page="../../supplier/detail/recovery_claim_history.jsp"/>
     </div>
	  <div dojoType="twms.widget.TitlePane" title="<s:text name="title.recoveryClaim.supportDocs"/>" 
			labelNodeClass="section_header" open="<s:property value='#expandClaimDetailsByDefault'/>" id="attachmentsPane">
		<jsp:include flush="true" page="./uploadAttachmentForRecoveryClaim.jsp"/>
	</div>

<s:action name="active_policies" executeResult="true">
	<s:param name="claimDetails" value="%{claim.id}"/>
</s:action>

<s:if test="ReturnThroughDealerDirectly">
         <jsp:include flush="true" page="../../supplier/detail/sra-component-direct-return.jsp"></jsp:include>
    </s:if>
    <s:else>
	     <jsp:include flush="true" page="../../supplier/detail/sra-component.jsp"></jsp:include>
	</s:else>

</s:form>

</div>
</div>
</u:body>
<authz:ifPermitted resource="processorRecoveryDisputedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('supplierRecoveryForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('supplierRecoveryForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>