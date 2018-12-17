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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <s:head theme="twms"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="Series Ref Certification"/></title>
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<u:body>
<form name="baseForm" id="baseForm" style="width: 99%">
<div class="admin_section_div">
    <div class="admin_section_heading"><s:text name="title.technician.seriesRefCertification" /></div>
    
    	<div class="mainTitle"></div>
    	<div class="borderTable">&nbsp;</div>
    	<div style="margin-top:-10px;">
    	<table width="100%" class="admin_selections">
    		<tr>
        		<td class="previewPaneBoldText" width="23%"><s:text name="label.technicianCertification.series" />:</td>
        		<td class="previewPaneNormalText"><s:property value="seriesAndCertifications.series.groupCode" /></td>
        		<td class="previewPaneBoldText" width="15%"><s:text name="label.technicianCertification.sisterSeries" />:</td>
        		<td class="previewPaneNormalText"><s:property value="seriesAndCertifications.series.oppositeSeries.groupCode" /></td>
      		</tr>
      		<tr>
    	    	<td class="previewPaneBoldText"><s:text name="label.common.seriesDescription" />:</td>
    	    	<td class="previewPaneNormalText"><s:property value="seriesAndCertifications.series.name" /></td>
    			<td class="previewPaneBoldText"><s:text name="label.common.startDate" />:</td>
        		<td class="previewPaneNormalText"><s:property value="seriesAndCertifications.startDate" /></td>
    		</tr>
    		<tr>
    			<td class="previewPaneBoldText"><s:text name="label.common.endDate" />:</td>
    			<td class="previewPaneNormalText" colspan="3"><s:property value="seriesAndCertifications.startDate" /></td>
    		</tr>
    		<tr>
				<td class="previewPaneBoldText"> <s:text name="label.technician.company"/>:</td>
				<td class="previewPaneNormalText">	        
			        <s:property value="%{seriesAndCertifications.series.brandType}"/>
			    </td>
			</tr>
    	</table>
    </div>

       <br>
       <div class="mainTitle"><s:text name="Certifications"/></div>
    	<table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable">
    		<tr>
    			<th class="colHeader" width="40%"><s:text name="label.technicianCertification.certificationName" /></th>
				<th class="colHeader"  width="10%"><s:text name="label.technicianCertification.categoryLevel" /></th>
				<th class="colHeader"  width="40%"><s:text name="label.technicianCertification.categoryName" /></th>
    		</tr>
    		<s:iterator value="seriesAndCertifications.seriesCertification">
    	    <tr>
    						<td class="previewPaneNormalText"> <s:property value="certificateName" /></td>
    						
    			<td class="previewPaneNormalText">
    				<s:property value="categoryLevel" />
    			</td>
    			<td class="previewPaneNormalText">
    				<s:property value="categoryName" />
    			</td>
    		</tr>
    		</s:iterator>
    	</table>	
</div>    
</form>
</u:body>
</html>