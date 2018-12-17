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


<div  class="section_div">
<div  class="section_heading"><s:text name="label.retailMachineTransfer.selectedDealer"/></div>
<table  cellspacing="0" cellpadding="0" id="equipment_details_table" width="96%" class="grid" style="clear: both; margin:5px;">
    
    <tbody>
    <s:if test="selectedDealerForRMT==null">
        <td align="center" colspan="9"><s:text name="label.retailMachineTransfer.noDealer" /></td>
    </s:if>
    <s:else>        
        <tr>
        <td colspan="1">
        	<table>
        	<thead>
        	<tr>
        		<td colspan="3" align="center" style="">
        			<s:text name="label.retailMachineTransfer.fromDealer"/>
        		</td>
        	</tr>
        	<tr>	        	
	            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.dealerName"/></th>
	            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.warrantyAdmin.dealerAddress"/></th>
	            <th class="warColHeader" width="10%"><s:text name="label.common.dealerNumber"/></th>                 
        	</tr>
    		</thead>
        		<tbody>
        		<s:iterator value="inventoryItemsForRMT" >
        		<tr>        			
        			<td>
                 		<s:property value="inventoryItem.dealer.name" />                    
            		</td>
            		<td>
                		<s:property value="inventoryItem.dealer.address.city"/><br></br>
                		<s:property value="iinventoryItem.dealer.address.state"/><br></br>
                		<s:property value="inventoryItem.dealer.address.country"/><br></br>
                		<s:property value="inventoryItem.dealer.address.zipCode"/>
            		</td>
            		<td>
                		<s:property value="%{getDealerNumber(inventoryItem.dealer.name)}" />
            		</td>
            	</tr>
            	</s:iterator>
        		</tbody>
        	</table>   
        </td>
        <td colspan="1">         
            <table>          
             <thead>
             <tr>
        		<td colspan="3" align="center">
        			<s:text name="label.retailMachineTransfer.toDealer"/>
        		</td>
        	</tr>
        	<tr>	        
	            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.dealerName"/></th>
	            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.warrantyAdmin.dealerAddress"/></th>
	            <th class="warColHeader" width="10%"><s:text name="label.common.dealerNumber"/></th>                 
        	</tr>
    		</thead>
        		<tbody>
        		<tr>        			
        			<td>
                 		<s:property value="selectedDealerForRMT.name" />                    
            		</td>
            		<td>
                		<s:property value="selectedDealerForRMT.address.city"/><br></br>
                		<s:property value="selectedDealerForRMT.address.state"/><br></br>
                		<s:property value="selectedDealerForRMT.address.country"/><br></br>
                		<s:property value="selectedDealerForRMT.address.zipCode"/>
            		</td>
            		<td>
                		<s:property value="selectedDealerForRMT.dealerNumber" />
            		</td>
            	</tr>
        		</tbody>
        	</table>
        </td>            
        </tr>       
   </s:else>
   </tbody>
</table>
</div>
</div>
<div id="mainRMTForm">
				<s:form method="post" theme="twms" id="performRMTForm" name="performRMT"
	            		action="performRMT.action">
	            	<s:if test="inventoryItemsForRMT!=null && inventoryItemsForRMT.size>0">
						<s:iterator value="inventoryItemsForRMT" status="inventoryItemsForRMT">
						<input type="hidden" name="inventoryItemsForRMT[<s:property value='%{#inventoryItemsForRMT.index}'/>].inventoryItem" 
	      				 value='<s:property value="inventoryItem.id"/>'/>      
						</s:iterator>
					</s:if>
	            	<input type="hidden" name="inventoryItems" value='<s:property value="inventoryItem.id"/>'/>
	            	<input type="hidden" name="selectedDealerForRMT" value="<s:property value="selectedDealerForRMT"/>"/>
	            	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="padding-bottom: 10px">
 						<center class="buttons">
 							<s:submit id="selectDealerButton"  value="%{getText('label.retailMachineTransfer.confirm')}" />
 						</center>
					</div>
				</s:form>
			</div>
