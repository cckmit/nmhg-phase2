<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/> 
    <u:stylePicker fileName="base.css"/>  
</head>
<u:body>
<u:actionResults/>
<s:if test="attributeMapper.rejectionReason.selected && attributeMapper.acceptanceReason.selected">
<div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
	<h4 class="twmsActionResultActionHead"><s:text name="label.common.warnings" /></h4>
		<ol>
			<s:text name="warning.MultiClaimMaintainance.reason"></s:text>				
		</ol>
		<hr/>
</div>
</s:if>

<div>
<script type="text/javascript">
dojo.require("dijit.layout.ContentPane");
</script>
<s:form method="post" theme="twms" id="updateClaimsForMaintenance" name="updateClaimsForMaintenanceForm"
            		action="updateClaimsForMaintenance.action">
            		
<input type="hidden" name="attributeSelected" value="<s:property value="attributeSelected"/>"/>

<s:iterator value="claims" status="claims"> 
    <s:hidden name="claims[%{#claims.index}]" value="%{id}"/>
    <s:hidden name="restoreClaimsList[%{#claims.index}]" value="%{id}"/>
</s:iterator>
<div class=bgColor>
<div class="sectionTitle"  style="color:#FFFFFF">
<s:text name="title.attributes.attributeValue"/>
</div>

<table  cellspacing="0" cellpadding="0" id="selected_attributes" width="100%" class="grid borderForTable" style="clear: both;">  
	<thead>
        <tr>        
            <th class="warColHeader" width="40%" class="non_editable"><s:text name="title.attributes.attributeName"/></th>        
            <th class="warColHeader" width="60%" class="non_editable"><s:text name="title.attributes.newValue"/></th>
        </tr>
    </thead>  
    <tbody>
         <tr> 
        	<s:if test="attributeMapper.accountabilityCode.selected">    	
            <td>
            <s:property value="attributeMapper.accountabilityCode.name"/>           	 
            </td>
            <td>
            	<s:hidden name="attributeMapper.accountabilityCode.selected"/>
            	<s:select name="attributeMapper.accountabilityCode.attribute.code"  list="attributeMapper.getListOfValues('AccountabilityCode')"/>                
            </td>
            </s:if>   
         </tr>
         <tr> 
        	<s:if test="attributeMapper.rejectionReason.selected">    	
            <td>
            	<s:property value="attributeMapper.rejectionReason.name"/>        	         
            </td>
            <td>
            <s:hidden name="attributeMapper.rejectionReason.selected"/>
            <s:select name="attributeMapper.rejectionReason.attribute.code"  list="attributeMapper.getListOfValues('RejectionReason')"/>             
            </td>
            </s:if>   
         </tr>
         <tr> 
        	<s:if test="attributeMapper.acceptanceReason.selected">    	
            <td>
            	<s:property value="attributeMapper.acceptanceReason.name"/>
            </td>
            <td>
            <s:hidden name="attributeMapper.acceptanceReason.selected"/>
            <s:select name="attributeMapper.acceptanceReason.attribute.code" list="attributeMapper.getListOfValues('AcceptanceReason')"/>                           
            </td>
            </s:if>   
         </tr>
         <s:if test="!isMultiDealerClaimsSelected()"> 
         <tr> 
        	<s:if test="attributeMapper.technician.selected">    	
            <td>
            	<s:property value="attributeMapper.technician.name"/>
            </td>
            <td>
            <s:hidden name="attributeMapper.technician.selected"/>
            <s:select name="attributeMapper.technician.attribute.id" list="getCommonTechnicians()"/>                           
            </td>
            </s:if>   
         </tr>
         </s:if> 
         <tr> 
        	<s:if test="attributeMapper.processingNotes.selected">    	
            <td>
            	<s:property value="attributeMapper.processingNotes.name"/>           	        
            </td>
            <td align="center">
            <s:hidden name="attributeMapper.processingNotes.selected"/>
            <t:textarea rows="2" cols="55" name="attributeMapper.processingNotes.attribute"
                wrap="physical" cssClass="bodyText" value=""/>               
            <s:checkbox id="appendOrEditProcessingNotes" name="appendOrEditProcessingNotes"/>
            <s:text name="%{getText('label.common.append')}"/>
            </td>
            </s:if>
         </tr>        
   </tbody>
</table>
</div>
<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="padding-bottom: 10px">
	<table class="buttons">
		<tr>
		<td>
		<center>
			 <s:submit value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                        });
                    </script>
			<s:submit id="Submit"  value="%{getText('button.common.submit')}" />
		</center>
		</td>
		</tr>
	</table>	
</div>
</s:form>
</div>
</u:body>

