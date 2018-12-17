<%--

   Copyright (c)2006 Tavant Technologies All Rights Reserved.

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
	<title><s:text name="title.treadReportForm"/></title>
	<u:stylePicker fileName="yui/reset.css" common="true"/>
	<u:stylePicker fileName="common.css"/>
	<u:stylePicker fileName="form.css"/>
	<s:head theme="twms"/>
</head>
<body>
	<s:form method="POST" theme="twms" validate="true" 
			name="treadReportInput" 
			id="tread_report_input_form" action="generateTreadReport.action">
	 <div dojoType="dijit.layout.ContentPane" >
   		<table class="form">
			<tr>
				<td><s:text name="label.year"/></td>
				<td>
					<s:select name="year" 
       						list="years"
      						 required="true"/>
       			</td>
				<td><s:text name="label.quarter"/></td>
				<td>
					<s:select name="quarter" 
						list="quarters"
						required="true"/>
				</td>
			</tr> 
		</table>
	</div>
		<s:submit type="input" value="%{getText('button.download')}" cssClass="button"/>
	</s:form>		
</body>	
</html>