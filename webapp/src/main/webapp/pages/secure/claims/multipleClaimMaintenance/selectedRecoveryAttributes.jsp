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
	<h4 class="twmsActionResultActionHead">WARNINGS</h4>
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
<s:form method="post" theme="twms" id="updateRecClaimsForMaintenance" name="updateRecClaimsForMaintenance"
            		action="updateRecClaimsForMaintenance.action">
            		
<input type="hidden" name="attributeSelected" value="<s:property value="attributeSelected"/>"/>

<s:iterator value="recoveryClaims" status="claims"> 
    <s:hidden name="recoveryClaims[%{#claims.index}]" value="%{id}"/>
    <s:hidden name="restoreRecClaimsList[%{#claims.index}]" value="%{id}"/>
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
        	<s:if test="attributeMapper.recClaimAcceptanceReason.selected">    	
            <td>
            <s:property value="attributeMapper.recClaimAcceptanceReason.name"/>           	 
            </td>
            <td>
            	<s:hidden name="attributeMapper.recClaimAcceptanceReason.selected"/>
            	<s:select name="attributeMapper.recClaimAcceptanceReason.attribute.code"  list="attributeMapper.getListOfValues('RecoveryClaimAcceptanceReason')"/>                
            </td>            
            </s:if>   
         </tr>
         <tr> 
        	<s:if test="attributeMapper.recClaimRejectionReason.selected">    	
            <td>
            	<s:property value="attributeMapper.recClaimRejectionReason.name"/>        	         
            </td>
            <td>
            <s:hidden name="attributeMapper.recClaimRejectionReason.selected"/>
            <s:select name="attributeMapper.recClaimRejectionReason.attribute.code"  list="attributeMapper.getListOfValues('RecoveryClaimRejectionReason')"/>             
            </td>          
            </s:if>
          </tr>
          <tr>
            <s:if test="attributeMapper.rgaNumber.selected">    	
            <td>
            	<s:property value="attributeMapper.rgaNumber.name"/>        	         
            </td>
            <td>
            <s:hidden name="attributeMapper.rgaNumber.selected"/>
            <s:textfield name="attributeMapper.rgaNumber.attribute" />             
            </td>
            </s:if>
            </tr>
            <tr>
            <s:if test="attributeMapper.carrier.selected">    	
            <td>
            	<s:property value="attributeMapper.carrier.name"/>        	         
            </td>
            <td>
            <s:hidden name="attributeMapper.carrier.selected"/>
            <s:select name="attributeMapper.carrier.attribute.name"  list="allCarriers" listKey="name" listValue="name"/>             
            </td>          
            </s:if>
            </tr>
            <tr>
            <s:if test="attributeMapper.location.selected">    	
            <td>
            	<s:property value="attributeMapper.location.name"/>        	         
            </td>
            <td>
            <s:hidden name="attributeMapper.location.selected"/>
            <s:select name="attributeMapper.location.attribute.code"  list="supplierLocation" listKey="code" listValue="code"/>             
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

