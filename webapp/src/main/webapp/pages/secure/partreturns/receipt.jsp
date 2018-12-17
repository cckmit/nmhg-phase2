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
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>

<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="common.css"/>
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
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" >
       
        <%@include file="tables/claim_details.jsp"%>
        
        <div id="separator"></div>
      
        <div style="width:100%;margin-bottom:10px;" class="mainTitle">
         <s:text name="title.partReturnConfiguration.partDetails"/>
         </div>
        <table cellspacing="0" cellpadding="0" class="grid borderForTable" align="center">
	      <tr>	      	      	
			<th  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
	        <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
	        <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
			<th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
			<th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.received" /></th>
			<th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.notReceived" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>			
	      </tr>
	      <s:iterator value="partReplacedBeans" status="partIterator">
	        <tr class="tableDataWhiteText"> 
		      <td >
		      <s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" />
   	                <s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null">
   	                <img  src="image/comments.gif" id= "oem_dealer_causal_part"
   			            		title="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />" 
   			            		alt="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />"/>   			    		  		
   			        </s:if>   
		      </td>
		      <td ><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>
		      <td  ><s:property value="shipped" /></td>
		      <td  ><s:property value="cannotBeShipped" /></td>
		      <td ><s:property value="received" /></td>
		      <td ><s:property value="notReceived" /></td>
		      <td><s:property value="totalNoOfParts" /></td>  
      		  <s:set name="partsCounter" value="%{#partsCounter + 1}"/>		        
	         </tr>
		  </s:iterator>
		</table>
        </div>
     
        <div id="separator"></div>
	  </s:iterator>					
	</div>    
	<div width="100%" />
  </div>
  
</u:body>

</html>