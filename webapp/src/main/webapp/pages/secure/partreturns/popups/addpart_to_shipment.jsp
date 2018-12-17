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
<script type="text/javascript">
 function addPartToShipment(){
 	window.close();
	return true;
 }
</script>
<s:head theme="twms"/>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
</head>

<u:body>
<s:if test="claimWithPartBeans.size() == 0"> <!-- Need to move out this common style for status messages.  -->
  <div style="border:1px solid #AAAAAA;background:#F5F5F5;padding:1px; width:97%; margin:5px;">
    <div style="background:#EFEBF7;color:#336600;padding:1px;">
	  <table>
	    <tr>
	  	  <td style="color:#336600;"><img src="images/applicationSuccessIcon.gif"/></td>
	  	  <td style="color:#336600;">
	  	    <p style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 7.5pt;">
	  	      <s:text name="message.partReturnConfiguration.noDueParts"/>
	  		</p>   
	  	  </td>
	  	</tr>
	  </table>
	</div>
  </div>
</s:if>
<s:else>
  <s:form action="shipmentgenerated_addpartToShipment.action"> 
    <s:hidden id="identifier" name="id"/>     
    <s:actionerror theme="xhtml"/>
    <s:fielderror theme="xhtml"/>
    <s:actionmessage theme="xhtml"/>
    	    
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">
		<div class="subtitle"><s:text name="label.partReturnConfiguration.shipmentDetails" /></div>
		<table class="tablestyling" cellspacing="0" cellpadding="0" >
		  <tr>
		    <td class="label"><s:text name="label.partReturnConfiguration.shipmentNumber" />:</td>
		    <td class="labelNormal">		    	
		    <s:property value="id"/>
			</td>		    		     
		    <td class="label"><s:text name="label.partReturnConfiguration.returnLocationWithCol" />:</td>
		    <td class="labelNormal"> 
		    
			</td>
		  </tr>
		</table>
		</div>
	  <% int partsCounter = 0; %>	
	  
	  <div class="subtitle"><s:text name="title.partReturnConfiguration.claimDetails"/></div>		  
	  <s:iterator value="claimWithPartBeans" status="claimIterator">							
		<%@include file="./../tables/claim_details.jsp"%>
		<%@include file="./../tables/location_view.jsp"%>
		<div class="separator" />
	  </s:iterator>							
	</div>    
	<div class="buttonWrapperPrimary">		
		<input type="submit" name="Submit2232" value="<s:text name="button.common.add"/>" class="buttonGeneric" onclick="addPartToShipment()"/>
  	</div>	    
  </s:form>	
</s:else>  
</u:body>
</html>