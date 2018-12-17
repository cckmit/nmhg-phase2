<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div>
	<div dojoType="twms.widget.TitlePane"
		title="<s:text name="title.newClaim.supportDocs"/>"
		labelNodeClass="section_header">
		<jsp:include flush="true" page="uploadAttachments.jsp" />

		<jsp:include page="../../../claims/forms/common/write/fileUploadDialog.jsp"/>
	</div>
</div>