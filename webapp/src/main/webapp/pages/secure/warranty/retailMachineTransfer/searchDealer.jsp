<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: 20 Nov, 2007
  Time: 2:28:01 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<style type="text/css">
    .warrantyCoverageTrigger {
        background-image: url( image/writeWA.gif );
        width: 20px;
        height: 17px;
        background-repeat: no-repeat;
        margin-top: 5px;
        cursor: pointer;
    }
</style>
<s:form method="post"  id="searchDealerForm" name="searchMultipleCars"
            		action="search_dealers_for_RMT.action">
     
<s:if test="inventoryItemsForRMT!=null && inventoryItemsForRMT.size>0">
	<s:iterator value="inventoryItemsForRMT" status="inventoryItemsForRMT">
	<input type="hidden" name="inventoryItemsForRMT[<s:property value='%{#inventoryItemsForRMT.index}'/>].inventoryItem" 
	       value='<s:property value="inventoryItem.id"/>'/>      
	</s:iterator>
</s:if>
<div class="section_div" >
<div class="section_heading"><s:text name="label.retailMachineTransfer.searchDealer"/></div>								
<table>
	<tbody>
	<tr>
		<td>
			<s:text name="label.retailMachineTransfer.searchByDealerName"></s:text>
		</td>
		<td>
			<s:textfield name="dealerSearch.name" id="companyName"/>
		</td>											
	</tr>
	<tr>
		<td>
			<s:text name="label.retailMachineTransfer.searchByDealerNumber"></s:text>
		</td>
		<td>
			<s:textfield name="dealerSearch.serviceProviderNumber" id="companyNumber"/>
		</td>							
	</tr>
	</tbody>
</table>
<table width="100%">
<tr>
<td align="center" class="buttons">
 <s:submit id="searchButton"  value="%{getText('label.retailMachineTransfer.searchDealer')}"/>
</td>
</tr>
</table>	
</s:form>
</div>
</div>