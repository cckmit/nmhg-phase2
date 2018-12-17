<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
     
 </head>
 <u:actionResults/>
 <u:stylePicker fileName="inboxLikeButton.css"/>
  <script type="text/javascript">  	  	
      	dojo.require("dijit.layout.LayoutContainer"); 
      	dojo.require("dijit.layout.ContentPane");
      	
   </script>
   
<u:body smudgeAlert="false">
	 <s:form name="labelSearchForm" id="labelSearchForm" action="listLabelNames.action">
	 	<div class="section" style="width:99%">
	 	<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor" colspan="6">
			<tr><div  class="section_header">        
        	  		<s:text name="label.common.labelSearch"/>
       			</div>	
      		 </tr>
      		 <tr>
				<td class="labelStyle" style="padding-top:5px;">
	        		<s:text name="button.common.addLabel"/>
	        	</td>
	        </tr>
      		<tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" checked="checked" value="INVENTORY"/><s:text name="accordion_jsp.accordionPane.inventory"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="POLICY DEFINITION" /><s:text name="accordionLabel.managePolicy.policyDefinition"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="SUPPLIER" /><s:text name="columnTitle.listContracts.supplier_name"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="FAULT CODE" /><s:text name="columnTitle.duePartsInspection.faultcode"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="JOB CODE" /><s:text name="label.campaign.jobCode"/>
	        	</td>
	        </tr>
            <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="MODEL" /><s:text name="label.common.model"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="CAMPAIGN" /><s:text name="label.common.campaign"/>
	        	</td>
	        </tr>
	        <tr>
				<td class="labelStyle">
	        		<input type="radio" name="labelType" id="labelType" value="WAREHOUSE" /><s:text name="label.common.warehouse"/>
	        	</td>
	        </tr>
         	
	      </table>
	      </div>
	        <div align="center" style="margin-top:10px">
	        		<s:submit name="button.common.continue" cssClass="buttonGeneric"/>
	        	</div>
	  </s:form>
<authz:ifPermitted resource="warrantyAdminViewLabelsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('labelSearchForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('labelSearchForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>      