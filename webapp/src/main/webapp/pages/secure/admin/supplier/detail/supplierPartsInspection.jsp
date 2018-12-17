<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<s:head theme="twms" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="partreturn.css" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
	dojo.require("twms.widget.Dialog");
</script>
<u:body>
	<u:actionResults />
	<s:form id="supplierPartsInspection_form" action="supplierPartsInspection_submit">
	<div dojoType="dijit.layout.ContentPane" label="Parts List"
		style="overflow-Y: auto; overflow-x: hidden; width: 100%;"
		id="mainDiv">
		<s:set name="partsCounter" value="0" />
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.common.recoveryClaimDetails"/>
      (
      <s:if test="recoveryClaim.documentNumber !=null && recoveryClaim.documentNumber.length() > 0" >
		<s:property value="recoveryClaim.recoveryClaimNumber" />-<s:property
				value="recoveryClaim.documentNumber" />
	   </s:if>
	   <s:else>
	      <s:property value="recoveryClaim.recoveryClaimNumber" />
	    </s:else>		
      )"
			labelNodeClass="section_header">
			<%@include file="recoveryClaimDetails.jsp"%>
			<hr />
			<%@include file="partReturnDetails_inspection.jsp"%>
		</div>
	</div>
	<%@include file="uploadDocuments.jsp" %>
	<%@include file="commentsAndAction.jsp" %>
	</s:form>
</u:body>
</html>