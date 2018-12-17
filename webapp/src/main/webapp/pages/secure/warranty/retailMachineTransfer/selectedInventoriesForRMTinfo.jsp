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

<div id="selectedInvsForRMT" class="section_div" >
<div id="selectedInvsForRMT_title" class="section_heading"><s:text name="label.retailMachineTransfer.selectedInventories"/></div>
<table  cellspacing="0" cellpadding="0"  width="100%" class="grid borderForTable" style="clear: both;">
    <thead>
        <tr>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.product"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.dealerName"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.itemCondition"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.customerName"/></th>            
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.buildDate"/></th>  
            <th class="warColHeader" width="10%"><s:text name="columnTitle.inventoryAction.delivery_date"/></th>        
        </tr>
    </thead>
    <tbody>
    <s:if test="inventoryItemsForRMT.empty">
        <td align="center" colspan="10"><s:text name="label.warrantytransfer.noInventoriesSelected" /></td>
    </s:if>
    <s:else>
        <s:iterator value="inventoryItemsForRMT" >
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
             <td>
                <s:property value="inventoryItem.ownedBy.name" />
            </td>
            <td>
                <s:property value="inventoryItem.builtOn" />
            </td>
            <td>
                <s:property value="inventoryItem.deliveryDate" />
            </td>
        </tr>
       </s:iterator>
   </s:else>
   </tbody>
</table>
</div>
</div>
<div id="searchCompanyTag">
<jsp:include page="searchDealer.jsp" />
</div>	