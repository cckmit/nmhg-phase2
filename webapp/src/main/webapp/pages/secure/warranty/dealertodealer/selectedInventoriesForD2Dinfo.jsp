<%--
 
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


	<div id="selectedInvsForD2D_title" class="mainTitle" style="margin-top:10px;margin-bottom:10px;">
		<s:text name="label.retailMachineTransfer.selectedInventories"/>
	</div>

<table  cellspacing="0" cellpadding="0" class="grid borderForTable" style="width:98%;" align="center">
    <thead>
        <tr>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.product"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.dealerName"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.itemCondition"/></th>
            <s:if test="(getLoggedInUser().isInternalUser())">                        
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.buildDate"/></th>
            </s:if>
        </tr>
    </thead>
    <tbody>
	    <s:if test="selectedInventoryItems.empty">
	        <td align="center" colspan="10"><s:text name="label.warrantytransfer.noInventoriesSelected" /></td>
	    </s:if>
	    <s:else>
	        <s:iterator value="selectedInventoryItems" >
	        <tr>            
	            <td>                
	                <s:property value="inventoryItem.serialNumber" />                    
	            </td>
	            <td>
	                <s:property value="inventoryItem.ofType.product.name"/>
	            </td>
	            <td>
	                <s:property value="inventoryItem.ofType.model.name" />
	            </td>
	            <td>
	                <s:property value="inventoryItem.dealer.name" />
	            </td>
	            <td>
	                <s:property value="inventoryItem.conditionType.itemCondition" />
	            </td>
	            <s:if test="(getLoggedInUser().isInternalUser())">
	            <td>
	                <s:property value="inventoryItem.builtOn" />
	            </td>
	            </s:if>            
	        </tr>
	       </s:iterator>
	   </s:else>
   </tbody>
</table>

<div id="searchCompanyTag">
	<jsp:include page="searchDealerForD2D.jsp" />
</div>	