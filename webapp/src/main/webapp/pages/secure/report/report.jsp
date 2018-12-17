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

<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<!-- Need to move the styles out to a file. Added here for now.  -->
<s:head theme="twms"/>
<style type="text/css">
div.snap {
    width: 100%;
    float: left;
    overflow-X: hidden;
    overflow-Y: auto;
    padding-top: 0px;
    padding-right: 0px;
    padding-bottom: 0px;
    padding-left: 0px;
    background-color: #F3FBFE
}

div.separator {
	padding-top:7px
}
.searchLabel {
	color:#959494;
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:10px;
	font-weight:bold;
	font-style:normal;
	vertical-align:middle;
	padding-left:5px;
	text-align:left;
	line-height:18px
}

.searchLabeltop {
	color:#959494;
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:10px;
	font-weight:bold;
	font-style:normal;
	vertical-align:top;
	padding-left:5px;
	text-align:left;
	line-height:18px
}
.bgColor {
	padding:1px;
	border:1px solid #EFEBF7;
	background-color:#FCFCFC;
	margin-left:4px;
	margin-right:2px;
	width:99%
}
.bodyText {
	FONT-WEIGHT:normal;
	FONT-SIZE:7.5pt;
	font-family:Verdana,sans-serif,Arial,Helvetica;
	BACKGROUND-COLOR:#ffffff;
	TEXT-DECORATION:none;
	color:#000000
}
.searchLabelNormalgray {
	color:#808080;
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:10px;
	font-weight:normal;
	vertical-align:middle
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
textarea {
	font-family:"Verdana","Arial","Helvetica",sans-serif;
	font-size:9px;
	padding:0px;
	text-transform:none
}
select {
	font-family:"Verdana","Arial","Helvetica",sans-serif;
	font-size:10px;
	font-weight:normal;
	text-align:left
}

.textinputs {
	height:15px;
	border:1px solid #ffffff;
	FONT-SIZE:7pt;
	COLOR:#B4B4B4;
	font-weight:normal;
	font-family:Verdana,sans-serif,Arial,Helvetica;
}
input {
	font-family:"Verdana","Arial","Helvetica",sans-serif;
	font-size:9px;
	font-weight:normal;
	padding:0px

}
input,select {
	font-family:"Verdana","Arial","Helvetica",sans-serif;
	font-size:7.5pt;
	text-align:left;
	margin-top:2px;
	margin-bottom:2px
}

td {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:10px;
	line-height:15px;
	border:0px;
	padding:0px;
	text-align:left
}

.error {
    color:red;
}
</style>

<u:stylePicker fileName="base.css"/>
<!-- start: added for getting the Calendar in Claim Search screen to work -->
<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
	type="text/css">
<!-- end -->


<script type="text/javascript" src="scripts/DirtyForm.js"></script>
</head>
<title><s:text name="title.common.warranty"/></title>
<u:body>
<s:form>
	<div class="snap"
		style=" width:100%; height:549px;  overflow-X: hidden;overflow-Y: auto; background-color:#FFFFFF">
	<div id="separator"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="bgColor">
		<tr>
			<td class="sectionTitle"><s:text name="label.claim.claim"/></td>
		</tr>
		<tr>
			<td nowrap="nowrap" class="labelNormalTop">
			<s:select label="label" name="reportSearchCriteria.reportType" list="{'Claim Type by Dealer', 'Claim Status by Dealer'}" value="%{reportSearchCriteria.reportType}"/>
			</td>
			<td nowrap="nowrap" class="labelNormalTop">			
			<div class="buttonWrapperPrimary"><input type="submit" class="buttonGeneric" value='<s:text name="label.common.go" id="reportTypeSubmitButton" />'></div>
		</td>
		</tr>
	</table>
	<script type="text/javascript">
     	var dirtyFormManager = null;
     	configureDirtyCheck = function() {
     		dirtyFormManager = new tavant.twms.DirtyFormManager(getTabDetailsForIframe().tabId);
     		var handle = new tavant.twms.FormHandler(document.forms[0]);
     		handle.registerSubmitButton(dojo.byId("reportTypeSubmitButton"));
     		dirtyFormManager.registerHandler(handle);
     	}
     	dojo.addOnLoad(function() {
     		configureDirtyCheck();
     	});
     </script>
</s:form>
</u:body>
</html>
