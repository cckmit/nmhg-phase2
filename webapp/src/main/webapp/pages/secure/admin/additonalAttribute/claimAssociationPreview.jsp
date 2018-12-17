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
<title>:: <s:text name="title.common.warranty" /> ::</title>

<s:head theme="twms"/>
<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="paymentSection.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
</head>

<u:body>



<div dojoType="dijit.layout.LayoutContainer"
     layoutChildPriority='top-bottom'
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: none;">
	
	<div dojoType="dijit.layout.TabContainer" labelPosition="bottom" layoutAlign="client">
	
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.additionalAtrribute.attributeDetails" />"
			style="overflow-X: none; overflow-Y: auto"> 
		<div class="policy_section_div">
			<div class="section_header"><s:text name="title.additionalAtrribute.attributeDetails" /></div>
			<jsp:include flush="true" page="attributePreview.jsp"/>		
		</div>
	</div>
	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.manageAdditionalAttributes.associatedAttributes"/>" style="overflow-X: none; overflow-Y: auto">
		<div class="policy_section_div">
			<div class="section_header"><s:text name="title.additionalAtrribute.associateParts"/></div>
			<jsp:include flush="true" page="claimAssociations.jsp"/>
		</div>	
	</div>
	</div>
	</div>
</u:body>
</html>