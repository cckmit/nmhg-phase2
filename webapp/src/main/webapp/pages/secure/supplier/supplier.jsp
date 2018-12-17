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
<s:head theme="twms"/>
<title><s:text name="title.common.warranty"/></title>
<script type="text/javascript">    
document.addDataInHiddenInput = function(/*string*/ data){
	var input = document.getElementById("taskIdForPartReturn");
	input.value=data;
}
</script>
<!--  TODO:  Need to move out the styles to a css file.  -->
<style type="text/css">

html {
	margin:0;
	padding:0;
	background-color:#FFFFFF
}

body {
	margin:0px;
	background-color:#FFFFFF;
	scrollbar-base-color:#E8EDEF;
	scrollbar-arrow-color:#000000;
	scrollbar-track-color:#f6f6f6;
	scrollbar-3dlight-color:#133173;
	scrollbar-highlight-color:#FFFFFF;
	scrollbar-darkshadow-color:#666666;
	scrollbar-shadow-color:#EFEBF7;
	scrollbar-face-color:#E8EDEF;
	height:100%;
	padding:0px
}
.buttonWrapperPrimary {
	padding-top:7px;
	padding-bottom:7px;
	text-align:center
}
.buttonGeneric {
	background:transparent url(../../../image/buttonBg.jpg) repeat-x scroll left center;
	border:1px solid #EFEBF7;
	color:#666666;
	cursor:pointer;
	font-family:Arial,Verdana,sans-serif,Helvetica;
	font-size:7.5pt;
	font-style:normal;
	overflow:visible;
}
div.separator {
	padding-top:5px;
	background-color:WHITE
}
</style>
</head>

<u:body>
	<div class="separator" />		

<!--  TODO :Making use of a form submission for the button click as of now. Need to change this - in javascript  -->	
	<s:form action="supplierRecovery_submit.action">	
		<s:hidden id="taskIdForPartReturn" name="id"/>
		<div class="buttonWrapperPrimary">
			<s:submit value='<s:text name="label.supplier.sendToSRA"/>' cssClass="buttonGeneric"/>		
	  	</div>		
	 </s:form> 		
</u:body>
</html>