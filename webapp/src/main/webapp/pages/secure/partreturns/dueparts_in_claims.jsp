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
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<s:head theme="twms"/>
<title><s:text name="title.common.warranty"/></title>
<script type="text/javascript" src="scripts/partReturnPopup.js"></script>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
</script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="claimForm.css"/>
</head>

<u:body>
  <script type="text/javascript">
        dojo.addOnLoad(function(){
            popUpShipmentTag('<s:property value="shipmentIdString" />');
        })
    </script>
  <s:form action="claimDueParts_generateShipment.action">   	
    <s:hidden id="identifier" name="id"/>     
    <u:actionResults wipeMessages="false"/>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
	  <s:set name="partsCounter" value="0"/>
	  <s:iterator value="claimWithPartBeans" status="claimIterator">					
		<div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>" labelNodeClass="section_header" >
        <%@include file="tables/claim_details.jsp"%>
        </div>
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" labelNodeClass="section_header" >
        
        <%@include file="tables/claim_view.jsp"%>
        </div>
		<div class="separator" />
	  </s:iterator>					
	</div>
    <div class="buttonWrapperPrimary">
      <s:if test="isDuePartsTask == true">				
	    <s:submit value="%{getText('button.partReturnConfiguration.generateShipment')}" cssClass="buttonGeneric" />
	  </s:if>						
	  <s:else>	
		<s:submit name="action:claimOverDueParts_generateShipment" value="%{getText('button.partReturnConfiguration.generateShipment')}" cssClass="buttonGeneric" />
	  </s:else>	
  	</div>	    
  </s:form>	
</u:body>
</html>