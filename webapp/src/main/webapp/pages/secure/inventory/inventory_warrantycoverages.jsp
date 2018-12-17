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
<%@taglib prefix="authz" uri="authz"%>	

<style>
.borderForTable tr td{
border:none !important;
}
</style>
    <table cellspacing="0" cellpadding="0" width="100%" class="grid borderForTable" style="margin:5px;width:96%;">
	    <thead>
	    	<tr class="row_head">
				<th><s:text name="label.warrantyAdmin.policyCode"/></th>
			    <th><s:text name="label.warrantyAdmin.policyName"/></th>
			    <th><s:text name="label.warrantyAdmin.type"/></th>
			    <th><s:text name="label.common.startDate"/></th>
			    <th><s:text name="label.common.endDate"/></th>
			    <th><s:text name="label.common.monthsCovered"/></th>
			    <th><s:text name="label.warrantyAdmin.hoursCovered"/></th>
			    <th><s:text name="columnTitle.common.status"/></th>
			    <th><s:text name="label.extendedwarrantyplan.orderNumber"/></th>
			    <th><s:text name="label.warrantyAdmin.alert"/></th>
		    </tr>
	    </thead>
	    <tbody>
	       <s:iterator value="registeredPolicies">
	         <s:if test="getLoggedInUser().getBelongsToOrganization().isThirdParty()"> 
	           <s:if test="policyDefinition.isThirdPartyPolicy">
	             <tr> 
				    <td align="left"><s:property value="code" /></td>
	                <td align="left"><s:property value="description" /></td>
	                <td align="left"><s:property value="%{getText(policyDefinition.warrantyType.displayValue)}" /> </td>
	                <td align="right"><s:property value="warrantyPeriod.fromDate" /> </td>
	                <td align="right"><s:property value="warrantyPeriod.tillDate" /> </td>	            
	                <td align="right"> 
	            	   <s:property value="%{(warrantyPeriod.fromDate).through(warrantyPeriod.tillDate).lengthInMonthsInt()}" /> 
	                </td>
	               <s:iterator value="policyAudits" status="policyAuditStatus">
	            	  <s:if test="#policyAuditStatus.last">
		            	<td align="right"> 
	            			<s:property value="serviceHoursCovered" /> 
	            		</td>
	            		<td align="right">
		            		<s:property value="status" />
		            	</td>	
	            	</s:if>
	               </s:iterator>	       	   
	               <td align="center">
	            	  <s:property value = "purchaseOrderNumber" />
	                </td>                     
	                <td align="center">
	            	  <s:if test="@com.domainlanguage.timeutil.Clock@today().isAfter(warrantyPeriod.tillDate)">
	            	    <span style="color:#FF0000">
		            	<s:text name='message.inventory.policyExpired'/>
		            	</span>
	            	  </s:if>
	               </td>            
	            </tr>
	          </s:if> 
	        </s:if>
	        
	        <s:else>                
	            <tr> 
				    <td align="left"><s:property value="code" /></td>
	                <td align="left"><s:property value="description" /></td>
	                <s:if test="warrantyType.type.equals('EXTENDED') && getCurrentBusinessUnit().getName().equals('AMER')" >
          				<td align="left"><s:label value="%{getText('dropdown.common.extended')}" /></td>
          			</s:if>
          			<s:else>
	                <td align="left"><s:property value="warrantyType.type" /> </td>
	                </s:else>            
	                <td align="right"><s:property value="warrantyPeriod.fromDate" /> </td>
	                <td align="right"><s:property value="warrantyPeriod.tillDate" /> </td>	            
	                <td align="right"> 
	            	   <s:property value="%{(warrantyPeriod.fromDate).through(warrantyPeriod.tillDate).lengthInMonthsInt()}" /> 
	                </td>
	                <s:iterator value="policyAudits" status="policyAuditStatus">
	            	  <s:if test="#policyAuditStatus.last">
		            	<td align="right"> 
	            			<s:property value="serviceHoursCovered" /> 
	            		</td>
	            		<td align="right">
		            		<s:property value="status" />
		            	</td>	
	            	  </s:if>
	               </s:iterator>	       	   
	               <td align="center">
	            	  <s:property value = "purchaseOrderNumber" />
	               </td>                     
	               <td align="center">
	            	  <s:if test="@com.domainlanguage.timeutil.Clock@today().isAfter(warrantyPeriod.tillDate)">
	            	    <span style="color:#FF0000">
		            	<s:text name='message.inventory.policyExpired'/>
		            	</span>
	            	  </s:if>
	               </td>            
	               </tr>	            
	        </s:else>        
	     </s:iterator> 	                
        </tbody>
	</table>