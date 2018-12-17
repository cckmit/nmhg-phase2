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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>

    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <style type="text/css">
        .labelBold {
            color: #545454;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 9pt;
            font-weight: bold;
            font-style: normal;
            vertical-align: middle;
            padding-left: 5px;
            text-align: left;
            line-height: 20px;
           
        }
    </style>

</head>

<u:body>

<div dojoType="dijit.layout.LayoutContainer"
     layoutChildPriority='top-bottom'
     style="height: 100%; margin: 0; padding: 0; overflow-X: hidden; overflow-Y: auto; width: 100%">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
			<div class="policy_section_div">
			<div dojoType="dijit.layout.ContentPane" label="<s:text name="label.contractAdmin.contractDetails"/>"
				labelNodeClass="section_header">
				<jsp:include flush="true" page="contractDetails.jsp"></jsp:include>
			</div>
			</div>

            <div class="policy_section_div">
			<div dojoType="dijit.layout.ContentPane" label="<s:text name="label.contractAdmin.coverageConditions"/>"
				labelNodeClass="section_header">
				<jsp:include flush="true" page="applicabilityTerms.jsp"></jsp:include>
			</div>
			</div>
			<div class="policy_section_div">
			<div dojoType="dijit.layout.ContentPane" label="<s:text name="label.contractAdmin.compensationTerms"/>"
				labelNodeClass="section_header" >
			 <div class="section_header"><s:text name="label.contract.compensationCondition" /> </div>
     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
       
        <s:iterator value="sections" id="section" status="sectionStatus">
        	<s:if test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
        		<s:if test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TRAVEL&&#section.name != @tavant.twms.domain.claim.payment.definition.Section@OTHERS">
				<tr>
    	      		<td colspan="3" class="labelBold">
    	      		<s:text
					name="%{getI18NMessageKey(#section.name)}" />
    	     	 	</td>
    	      		<td width="80%">&nbsp;</td> 
        		</tr>
        	</s:if>
			<s:if test="contract.doesCoverSection(name)">        	
				<s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS">
			
		        	<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>
		            <td class="labelNormal" width="5%"><input 
		            	name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
		            	style="border: 0px" value="radiobutton" disabled="true"/></td>
		            <td class="labelNormal"><s:text name="label.contract.replaceable" /></td>
					</tr>        
					<tr>
						<td class="labelNormal" width="2%">&nbsp;</td>
						<td class="labelNormal" width="5%"><input 
							name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
							style="border: 0px" value="radiobutton" checked="checked" disabled="true"/></td>
				        <td class="labelNormal"><s:text name="label.contract.reimbursable" /></td>
					</tr>
					<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>			
					<td colspan="2" nowrap="nowrap">
					<s:property value="%{contract.getPreparedRecoveryFormula(name)}"></s:property>	
					</td>
					</tr>
				</s:if>
				<s:else>
				<s:if test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TRAVEL&&#section.name != @tavant.twms.domain.claim.payment.definition.Section@OTHERS">
				 	<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>
		            <td class="labelNormal" width="5%"><input 
		            	name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
		            	style="border: 0px" value="radiobutton" disabled="true"/></td>
		            <td class="labelNormal"><s:text name="label.contract.notCovered" /> </td>
					</tr>        
					<tr>
						<td class="labelNormal" width="2%">&nbsp;</td>
						<td class="labelNormal" width="5%"><input 
							name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
							style="border: 0px" value="radiobutton" checked="checked" disabled="true"/></td>
				        <td class="labelNormal"><s:text name="label.contract.covered" /></td>
					</tr>
					<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>			
					<td colspan="2" nowrap="nowrap">
					<s:property value="%{contract.getPreparedRecoveryFormula(name)}"></s:property>	
					</td>
					</tr>
					</s:if>
				
				</s:else>
			</s:if>
			<s:else>
				<s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS">
					<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>
		            <td class="labelNormal" width="5%"><input 
		            	name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
		            	style="border: 0px" value="radiobutton" checked="checked" disabled="true"/></td>
		            <td class="labelNormal"><s:text name="label.contract.replaceable" /></td>
					</tr>        
					<tr>
						<td class="labelNormal" width="2%">&nbsp;</td>
						<td class="labelNormal" width="5%"><input 
							name="radiobutton<s:property value="%{#sectionStatus.index}"/>" 
							style="border: 0px" type="radio" value="radiobutton" disabled="true"/></td>
				        <td class="labelNormal"><s:text name="label.contract.reimbursable" /></td>
					</tr>
				</s:if>
				<s:else>
				<s:if test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TRAVEL&&#section.name != @tavant.twms.domain.claim.payment.definition.Section@OTHERS">
					<tr>
					<td class="labelNormal" width="2%">&nbsp;</td>
		            <td class="labelNormal" width="5%"><input 
		            	name="radiobutton<s:property value="%{#sectionStatus.index}"/>" 
		            	style="border: 0px" type="radio" value="radiobutton" checked="checked" disabled="true"/></td>
		            <td class="labelNormal"><s:text name="label.contract.notCovered" /></td>
					</tr>        
					<tr>
						<td class="labelNormal" width="2%">&nbsp;</td>
						<td class="labelNormal" width="5%"><input 
							name="radiobutton<s:property value="%{#sectionStatus.index}"/>" type="radio" 
							style="border: 0px" value="radiobutton" disabled="true"/></td>
				        <td class="labelNormal"><s:text name="label.contract.covered" /></td>
					</tr>
					</s:if>
				</s:else>
			</s:else>
		</s:if>
		<s:if test="#section.name == @tavant.twms.domain.claim.payment.definition.Section@OEM_PARTS || #section.name == 'Club Car Parts'">		  	
		   <s:if test="contract.recoveryBasedOnCausalPart">		  		
				   <tr>
						<td colspan="3" class="labelBold">
							<s:text name="label.supplierRecovery.otherSupplierParts" />
						</td>
		           </tr>
		           <tr>		           
						<td colspan="3">
							<s:radio id="collateralDamageToBePaid" name="contract.collateralDamageToBePaid" value="%{contract.collateralDamageToBePaid.toString()}"
									list="#{'true':'label.contractAdmin.covered'}"
									listKey="key" listValue="%{getText(value)}" disabled="true" cssStyle="width: 40px;border:0;background:#F3FBFE;" labelposition="top" />
			            </td>                	      			
		           </tr>
		           <tr>            
						<td colspan="3">
						  <s:radio name="contract.collateralDamageToBePaid" value="%{contract.collateralDamageToBePaid.toString()}"
									list="#{'false':'label.contractAdmin.notCovered'}"
									listKey="key" listValue="%{getText(value)}" disabled="true" cssStyle="width: 40px;border:0;background:#F3FBFE;" labelposition="top"/>
						</td>
		           </tr>		                           
		   </s:if>										
		</s:if>	
		
        </s:iterator>	
</table>

	</div>
			</div>
			<div class="policy_section_div">
			<div dojoType="dijit.layout.ContentPane" label="<s:text name="label.contractAdmin.itemsCovered"/>"
				labelNodeClass="section_header" >
				<jsp:include flush="true" page="itemsCovered.jsp"></jsp:include>
			</div>
			</div>
			
	</div>	
</div>

</u:body>
</html>