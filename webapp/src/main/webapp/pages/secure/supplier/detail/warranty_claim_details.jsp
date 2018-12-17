<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.claimDetails" />"
			labelNodeClass="section_header" id="claimHeaderPane" open="<s:property value='#expandClaimDetailsByDefault'/>"> 
		<jsp:include flush="true" page="../../claims/forms/common/supplier_header.jsp"/>		
	</div>

	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.equipmentDetails"/>"
			labelNodeClass="section_header" id="equipmentPane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/equipment.jsp"/>
	</div>
	   <div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.supportDocs"/>" 
			labelNodeClass="section_header" open="<s:property value='#expandClaimDetailsByDefault'/>" id="claimAttachmentsPane">
		<jsp:include flush="true" page="uploadClaimAttachments.jsp"/>
	</div>
	

<s:if test="!#limitedView">

<s:if test="!isClaimCampaign()">
	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.failureDetails" />"
			labelNodeClass="section_header" id="failurePane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/failure.jsp"/>
	</div>
</s:if>
<s:if test="buConfigAMER">
 <authz:ifUserNotInRole roles="supplier">
	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
			labelNodeClass="section_header" id="serviceDetailsPane" 
			open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/service_detail.jsp"/>
	</div>
  </authz:ifUserNotInRole>
</s:if>
<s:else>
	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.serviceDetails"/>"
			labelNodeClass="section_header" id="serviceDetailsPane" 
			open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/service_detail.jsp"/>
	</div>
</s:else>
<s:if test="!isPartsReplacedInstalledSectionVisible()">
	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.componentsReplaced"/>"
			labelNodeClass="section_header" id="componentsPane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/component.jsp"/>
	</div>
</s:if>
<s:elseif test="buPartReplaceableByNonBUPart">
	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.partsReplacedInstalled"/>" 
			labelNodeClass="section_header" id="componentsPane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/replacedInstalledOEMParts.jsp"/>           
	</div>
</s:elseif>
<s:else>
	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.claim.partsReplacedInstalled"/>" 
			labelNodeClass="section_header" id="componentsPane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/replacedInstalledOnlyOEMParts.jsp"/>   
		<jsp:include flush="true" page="../../claims/forms/common/read/component.jsp"/>        
	</div>
</s:else>

	<div dojoType="twms.widget.TitlePane" title="<s:text name="title.viewClaim.comments"/>"
			labelNodeClass="section_header" id="commentsPane" open="<s:property value='#expandClaimDetailsByDefault'/>">
		<jsp:include flush="true" page="../../claims/forms/common/read/comment.jsp"/>
	</div>

    <%-- <div dojoType="twms.widget.TitlePane" title="<s:text name="title.recoveryClaim.supportDocs"/>" 
			labelNodeClass="section_header" open="<s:property value='#expandClaimDetailsByDefault'/>" id="attachmentsPane">
		<jsp:include flush="true" page="../../admin/supplier/uploadAttachmentForRecoveryClaim.jsp"/>
	</div>--%>
	
	<jsp:include page="../../claims/forms/common/write/fileUploadDialog.jsp"/> 
</s:if>