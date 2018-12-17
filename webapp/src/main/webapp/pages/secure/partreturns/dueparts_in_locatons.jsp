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


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<script type="text/javascript" src="scripts/partReturnPopup.js"></script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

<u:stylePicker fileName="common.css"/>
   <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	 dojo.require("twms.widget.TitlePane");
</script>
<body onLoad="popUpShipmentTag('<s:property value="shipmentIdString" />')">
  <s:form action="dueParts_generateShipment.action"> 
    <s:hidden id="identifier" name="id"/>     
    <u:actionResults wipeMessages="false"/>
    <div dojoType="dijit.layout.ContentPane"     label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
	  <s:set name="partsCounter" value="0"/>
	  <s:iterator value="claimWithPartBeans" status="claimIterator">
	   <div  >
	   <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" />			
		sssss<jsp:include page="tables/claim_details.jsp"/>
		</div>
		</div>
		<div  >
		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" labelNodeClass="section_header">
		<jsp:include page="tables/location_view.jsp"/>
		</div>
		</div>
	  </s:iterator>					
	
	<div class="buttonWrapperPrimary">
	  <s:if test="isDuePartsTask == true">
	    <s:submit value="%{getText('button.partReturnConfiguration.generateShipment')}" cssClass="buttonGeneric" />
	  </s:if>						
      <s:else>	
	    <s:submit name="action:OverdueParts_generateShipment" value="%{getText('button.partReturnConfiguration.generateShipment')}" cssClass="buttonGeneric" />
	  </s:else>	
  	</div>	
  	</div>    
  </s:form>	
</body>
</html>