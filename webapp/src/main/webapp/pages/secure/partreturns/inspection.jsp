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
<%@ taglib prefix="tda" uri="twmsDomainAware" %>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
</script>
<u:body>
	<div class="separatorTop" />		
	<div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
	<s:set name="partsCounter" value="0"/>
	<s:iterator value="claimWithPartBeans" status="claimIterator">
      <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>" labelNodeClass="section_header" >
      <%@include file="tables/claim_details.jsp"%>
      </div>
      <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" labelNodeClass="section_header" >
      
      <table  cellspacing="0" cellpadding="0" class="grid borderForTable">
        <tr>
          <th width="12%"  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
          <th width="19%"  valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
          <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.location" /></th>
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
		  <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.received" /></th>
		  <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.inspected" /></th>
		  <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
		  </tr>	
        <s:set name="partsCounter" value="0"/>      
	    <s:iterator value="partReplacedBeans" status="partIterator">
	      <tr index="#index" class="tableDataWhiteText">
	      <td width="12%" >
			<s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" />
			<s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null">
               <img  src="image/comments.gif" id= "oem_dealer_causal_part"
	            		title="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />" 
	            		alt="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />"/>   			    		  		
	        </s:if>  
			</td>
	        <td width="19%" ><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
	        <td width="14%" ><s:property value="oemPartReplaced.activePartReturn.warehouseLocation" /></td>
	        <td width="6%" ><s:property value="toBeShipped" /></td>
	        <td width="6%" ><s:property value="shipped" /></td>
	        <td width="6%" ><s:property value="cannotBeShipped" /></td>
	        <td width="6%" ><s:property value="received" /></td>
	        <td width="6%" ><s:property value="inspected" /></td>
	        <td width="6%"><s:property value="totalNoOfParts" /></td>
	        
	     </tr>   
	     <s:set name="partsCounter" value="%{#partsCounter + 1}"/>	
         </s:iterator>
	  </table>
      
      
      </div>
      <div class="separator" />
	</s:iterator>		
		 
</u:body>
</html>