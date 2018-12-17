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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>

<s:head theme="twms"/>
<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="paymentSection.css"/>
<u:stylePicker fileName="base.css"/>

</head>

<u:body>

<s:hidden name="id" value="%{claim.id}"/>

<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: hidden;">
	
	<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
	
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.header" />"
			style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.viewClaim.claimDetails" /></div>
			<jsp:include flush="true" page="../../claims/forms/common/read/header.jsp"/>		
		</div>
	</div>
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.equipment"/>" style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.viewClaim.equipmentDetails"/></div>
			<jsp:include flush="true" page="../../claims/forms/common/read/equipment.jsp"/>
		</div>	
	</div>
	<s:if test="!isClaimCampaign()">
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.failure" />" style="overflow-X: hidden; overflow-Y: auto">
			<div class="policy_section_div">
				<div class="section_header"><s:text name="label.claim.failureDetails" /></div>
				<jsp:include flush="true" page="../../claims/forms/common/read/failure.jsp"/>
			</div>
		</div>
	</s:if>

 <s:if test="!isPartsReplacedInstalledSectionVisible()">
	    <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.components"/>" style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.viewClaim.componentsReplaced"></s:text></div>
			<jsp:include flush="true" page="../../claims/forms/common/read/component.jsp"/>
		</div>
	</div>
	</s:if>
	<s:else>
	  <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.components"/>" style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.claim.partsReplacedInstalled"></s:text></div>
			<jsp:include flush="true" page="../../claims/forms/common/read/replacedInstalledOEMParts.jsp"/>           
		</div>
	</div>	
    </s:else>
		
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.comments"/>" style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.viewClaim.comments"></s:text> </div>
			<jsp:include flush="true" page="../../claims/forms/common/read/comment.jsp"/>
		</div>
	</div>
	
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.history"/>" style="overflow-X: hidden; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="label.viewClaim.history"></s:text> </div>
			<jsp:include flush="true" page="../detail/recovery_claim_history.jsp"/>
		</div>
	</div>
	
	</div>
	
	<s:action name="active_policies" executeResult="true">
		<s:param name="claimDetails" value="%{claim.id}"/>
	</s:action>
</u:body>
</html>