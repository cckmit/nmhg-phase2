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
    <title><s:text name="title.common.warranty"/></title>
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<u:body>
<form name="baseForm" id="baseForm" style="width: 99%">
<div class="admin_section_div">
    <div class="admin_section_heading"><s:text name="label.partReturnConfiguration" /></div>
    
    	<div class="mainTitle"><s:text name="label.partReturnConfiguration.criteria" /></div>
    	<div class="borderTable">&nbsp;</div>
    	<div style="margin-top:-10px;">
    	<table width="100%" class="admin_selections">
    		<tr>
        		<td class="previewPaneBoldText" width="23%"><s:text name="label.partReturnConfiguration.dealer" />:</td>
        		<td class="previewPaneNormalText"><s:property value="selectedDealerOrGroup" /></td>
        		<td class="previewPaneBoldText" width="15%"><s:text name="label.partReturnConfiguration.warrantyType" />:</td>
        		<td class="previewPaneNormalText"><s:property value="%{getText(selectedWarrantyType)}" /></td>
      		</tr>
      		<tr>
    	    	<td class="previewPaneBoldText"><s:text name="label.partReturnConfiguration.claimType" />:</td>
    	    	<td class="previewPaneNormalText"><s:property value="selectedClaimType" /></td>
    			<td class="previewPaneBoldText"><s:text name="label.partReturnConfiguration.productType" />:</td>
        		<td class="previewPaneNormalText"><s:property value="selectedProduct" /></td>
    		</tr>
    		<tr>
    			<td class="previewPaneBoldText"><s:text name="label.partReturnConfiguration.partNumber" />:</td>
    			<td class="previewPaneNormalText" colspan="3"><s:property value="partReturnDefinition.itemCriterion.identifier" /></td>
    		</tr>
    		<tr>
				<td class="previewPaneBoldText"> <s:text name="label.partReturnConfiguration.shippingInstructions"/>:</td>
				<td class="previewPaneNormalText">	        
			        <s:property value="%{partReturnDefinition.shippingInstructions}"/>
			    </td>
			    <td class="previewPaneBoldText"> <s:text name="label.partReturnConfiguration.receiverInstructions"/>:</td>
			    <td class="previewPaneNormalText">
			        <s:property value="%{partReturnDefinition.receiverInstructions}"/>
			    </td>
			</tr>
			<tr>
				<td class="previewPaneBoldText"><s:text name="label.partReturnConfiguration.comments"/>:</td>
				<td class="previewPaneNormalText" colspan="3">
			        <s:property value="%{partReturnDefinition.comments}"/>
			    </td>
			</tr>
    	</table>
    </div>

       <div class="mainTitle"><s:text name="label.partReturnConfiguration.config"/></div>
    	<table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable">
    		<tr>
    			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.billingDate"/></th>
    			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.paymentCondition"/></th>
    			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.isCausalPart"/></th>
    			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.returnLocation"/></th>
    			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.daysDue"/></th>
    		</tr>
    		<s:iterator value="partReturnDefinition.configurations">
    	    <tr>
    	    	<td>
    	    		<table>
    					<tr>
    						<td class="previewPaneBoldText" ><s:text name="label.common.from"/></td>
    						<td class="previewPaneNormalText"> : <s:property value="duration.fromDate" /></td>
    					</tr>
    					<tr>
    						<td class="previewPaneBoldText"><s:text name="label.common.to"/></td>
    						<td class="previewPaneNormalText"> : <s:property value="duration.tillDate" /></td>
    					</tr>
    				</table>
    			</td>		
    			<td class="previewPaneNormalText">
    				<s:property value="paymentCondition.description" />
    			</td>
    			<td class="previewPaneNormalText">
    				<s:if test="%{causalPart}"><s:text name="label.common.yes"/></s:if>
    				<s:else><s:text name="label.common.no"/></s:else>
    			</td>
    			<td class="previewPaneNormalText">
    				<s:property value="returnLocation.code" />
    			</td>
    			<td class="previewPaneNormalText">
    				<s:property value="dueDays" />
    			</td>
    		</tr>
    		</s:iterator>
    	</table>	
		<jsp:include flush="true" page="partReturnConfigActionHistory.jsp"></jsp:include>
</div>    
</form>
</u:body>
</html>