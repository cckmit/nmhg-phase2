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
<body>  
    <div dojoType="dijit.layout.ContentPane"     label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
	  <s:set name="partsCounter" value="0"/>
	  <s:iterator value="claimWithPartBeans" status="claimIterator">
	   <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" />
		<jsp:include page="tables/claim_details.jsp"/>
		<div  class="mainTitle" style="margin-top:10px;">
		<s:text name="title.partReturnConfiguration.partDetails"/></div>
		
			<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" >			
			   <thead>
			   <tr class="row_head">    
				<th width="12%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
			    <th width="10%" valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
			    <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
				<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipementGenerated" /></th>
				<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotBeShipped" /></th>
				<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
				<th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>   	
			    <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.common.dueDate" /></th>
			    <s:if test="isDuePartsTask == true">
			      <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.duedays" /></th>
			    </s:if>
			    <s:else>
			      <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.overdueDays" /></th>
			    </s:else>    
			    </tr>
			    </thead>   
			  <s:iterator value="partReplacedBeans" status="partIterator">
			    <tr class="
			     	 <s:if test="oemPartReplaced.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
			     	 <s:else>tableDataWhiteText</s:else>
			     	 ">     
			      <td width="12%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
				  <td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
			      <td width="6%" style="padding-left:3px;"><s:property value="toBeShipped" /></td>
			      <td width="6%" style="padding-left:3px;"><s:property value="shipmentGenerated" /></td>
			      <td width="6%" style="padding-left:3px;"><s:property value="cannotBeShipped" /></td>
			      <td width="6%" style="padding-left:3px;"><s:property value="shipped" /></td>
			      <td width="6%" style="padding-left:3px;"><s:property value="totalNoOfParts" /></td>      
			      <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDate" /></td>
			      <td width="14%" style="padding-left:3px;"><s:property value="oemPartReplaced.activePartReturn.dueDays" /></td>
			      
			       <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
			    </tr>
			  </s:iterator>
			</table>
        </div>
        </div>
	  </s:iterator>		
</body>
</html>