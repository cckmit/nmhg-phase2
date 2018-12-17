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

	<div class="policy_section_div">
	<div class="section_header">
	<s:text name="label.contract.compensationCondition" />
	</div>
     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
   
        <s:iterator value="sections" id="section" status="sectionStatus">
        <s:if test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
			<tr>
    	      <td colspan="3" class="labelBold">
    	      	<s:text name="%{getI18NMessageKey(#section.name)}"/>
    	      </td>
    	      <td width="80%">&nbsp;</td> 
        	</tr>
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
				</s:else>
			</s:else>
		</s:if>
        </s:iterator>	
</table>
	</div>
