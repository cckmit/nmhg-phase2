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

<s:form method="post"  id="searchDealerForm" name="searchDealerForm" action="search_dealers_for_D2D.action">     
<s:if test="selectedInventoryItems!=null && selectedInventoryItems.size>0">
	<s:iterator value="selectedInventoryItems" status="selectedInventoryItemsIter">
	<input type="hidden" name="selectedInventoryItems[<s:property value='%{#selectedInventoryItemsIter.index}'/>].inventoryItem" 
	       value='<s:property value="inventoryItem.id"/>'/>        
	</s:iterator>
</s:if>

<div class="mainTitle" style="margin-top:10px;margin-bottom:10px;">
	<s:text name="label.retailMachineTransfer.searchDealer"/>
</div>
<div class="borderTable">&nbsp;</div>
								
<table class="grid" cellspacing="0" cellpadding="0" style="width:98%" align="center">
	<tbody>
	<tr>
		<td class="labelStyle">
			<s:text name="label.retailMachineTransfer.searchByDealerName"></s:text>
		</td>
		<td>
			<s:textfield name="dealerSearch.name" id="companyName"/>
		</td>											
	</tr>
	<tr>
		<td class="labelStyle">
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
