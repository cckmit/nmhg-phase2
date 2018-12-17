<%@ taglib prefix="s" uri="/struts-tags" %>

<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="t" uri="twms" %>

<%@ taglib prefix="u" uri="/ui-ext" %>

<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.BorderContainer");

        function enableText()
        {
            document.getElementById("savedQueryName").disabled = !document.getElementById("notATemporaryQuery").checked;
            if (document.getElementById("notATemporaryQuery").checked) {
                document.getElementById("savedQueryName").value = '';
            }
            return true;
        }
        dojo.addOnLoad(function() {
            if(document.getElementById("savedQueryName").value!=''){
            	document.getElementById("savedQueryName").value =  "<s:property value='%{savedQueryName}'/>";
            }
            else{
            	document.getElementById("savedQueryName").value =  "Name of the Query";
            }
            });
    </script>
    <style>
    .bgColor tr td{
    padding-bottom:10px;
    }
    </style>
</head>
<u:body smudgeAlert="false">
<div dojoType="dijit.layout.BorderContainer" style="width: 100%; height: 100%; overflow-x:hidden;overflow-y:auto" id="root">
    <div dojoType="dijit.layout.ContentPane" region="center" id="content">
<u:actionResults/>
<s:fielderror theme="xhtml"/>
<form action="validatePreDefinedInventorySearchFields.action?context_Predefined=InventorySTOCKSearch" method="POST" name="form">
<s:hidden name="context"/>
<s:hidden name="queryId"/>
<s:hidden name="inventorySearchCriteria.inventoryType.type" value="STOCK"/>
<div class="policy_section_div" style="width:100%">
    <div id="dcap_pricing_title" class="section_header">
        <s:text name="label.common.stockInventorySearch"/>
    </div>
<table style="width:99%" border="0" cellspacing="0" cellpadding="0" class="grid ">

<tr>
    <td></td>
    <s:if test="(getLoggedInUser().businessUnits).size>1">
        <td class="searchLabel labelStyle"><s:text name="label.common.businessUnit"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:iterator value="businessUnits" status="buItr">
                <s:if test="inventorySearchCriteria.selectedBusinessUnits[#buItr.index] != null">
                    <input type="checkbox"
                           name="inventorySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
                           value="<s:property value="name" />" checked="true" style="border:none"/>
                    <s:property value="name"/>

                </s:if>
                <s:else>
                    <input type="checkbox"
                           name="inventorySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
                           value="<s:property value="name" />" style="border:none;" />
                    <s:property value="name"/>
                </s:else>
            </s:iterator>
        </td>
    </s:if>
</tr>
<s:if test="loggedInUserAnInternalUser">
    <tr>
        <td></td>
        <td class="searchLabel labelStyle"><s:text name="label.common.dealerNumber"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:textfield name="inventorySearchCriteria.dealerNumber" id="dealerNumber"/>
        </td>
    </tr>
    <tr>
        <td></td>
        <td class="searchLabel labelStyle"><s:text name="label.common.dealerName"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:textfield name="inventorySearchCriteria.dealerName" id="dealerName" size="40"/>
        </td>
    </tr>
</s:if>
<s:if test="loggedInUserAnEnterpriseDealer">
    <tr>
        <td></td>
        <td class="searchLabel labelStyle" width="20%" valign="top"><s:text name="label.common.childDealer"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:select name="inventorySearchCriteria.ChildDealers" list="childDealerShip"
                      listKey="id" listValue="name" multiple="true" size="6" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
</s:if>
<s:if test="manufacturingSiteVisible">
    <tr>
        <td></td>
        <td class="searchLabel labelStyle" width="20%" valign="top"><s:text name="label.inventory.manufacturingSite"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:select name="inventorySearchCriteria.ManufacturingSite" list="listOfManufacturingSite"
                      listKey="id" listValue="description" multiple="true" size="6" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
</s:if>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.serialNumber"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td>
        <s:textfield name="inventorySearchCriteria.serialNumber" id="serialNumber"/>
    </td>
</tr>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.productType"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td><s:select name="inventorySearchCriteria.productType"
                  id="inventorySearchCriteria.productType"
                  listKey="name" listValue="name" list="productTypes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.products"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td><s:select name="inventorySearchCriteria.productGroupCode"
                  id="inventorySearchCriteria.products"
                  listKey="groupCode" listValue="groupCode" list="productCodes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.modelWithProductCode"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td><s:select name="inventorySearchCriteria.modelNumber"
                  id="inventorySearchCriteria.modelNumber"
                  listKey="name" listValue="itemGroupDescription" list="modelTypes" emptyOption="true"/></td>
    
</tr>
<s:if test="loggedInUserAnInternalUser">
    <tr>
        <td></td>
        <td class="searchLabel labelStyle"><s:text name="label.common.itemNumber"/>:</td>
        <td class="searchLabel labelStyle"></td>
        <td>
            <s:textfield name="inventorySearchCriteria.itemNumber" id="itemNumber"/>
        </td>
    </tr>
</s:if>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.condition"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td>
        <s:radio name="inventorySearchCriteria.condition" list="conditions" cssStyle="border:none"/>
    </td>
</tr>
<s:if test="buildDateVisible">
    <tr>
        <td></td>
        <td class="searchLabel labelStyle"><s:text name="label.retailMachineTransfer.buildDate"/>:</td>
        <td class="searchLabel labelStyle" width="10%">
            <s:text name="label.common.from"/>:
        </td>
        <td><sd:datetimepicker name='inventorySearchCriteria.buildFromDate' id='buildFromDate' value='%{inventorySearchCriteria.buildFromDate}' />
        </td>
    </tr>
    <tr>
        <td></td>
        <td></td>
        <td class="searchLabel labelStyle" style="padding-left:16px;" >
            <s:text name="label.common.to"/>:
        </td>
        <td><sd:datetimepicker name='inventorySearchCriteria.buildToDate' id='buildToDate' value='%{inventorySearchCriteria.buildToDate}' />
        </td>

    </tr>
</s:if>
<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.shipmentDate"/>:</td>
    <td class="searchLabel labelStyle">
        <s:text name="label.common.from"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.fromDate' id='fromDate' value='%{inventorySearchCriteria.fromDate}' />
    </td>
</tr>
<tr>
    <td></td>
    <td></td>
    <td class="searchLabel labelStyle" style="padding-left:16px;" >
        <s:text name="label.common.to"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.toDate' id='toDate' value='%{inventorySearchCriteria.toDate}' />
    </td>

</tr>

<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.submitDate"/>:</td>
    <td class="searchLabel labelStyle">
        <s:text name="label.common.from"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.submitFromDate' id='submitFromDate' value='%{inventorySearchCriteria.submitFromDate}' />
    </td>
</tr>
<tr>
    <td></td>
    <td></td>
    <td class="searchLabel labelStyle" style="padding-left:16px;" >
        <s:text name="label.common.to"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.submitToDate' id='submitToDate' value='%{inventorySearchCriteria.submitToDate}' />
    </td>

</tr>

<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.inventory.pending.approval.dr"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td class="searchLabel labelStyle">
    	<s:checkbox cssClass="buttonGeneric" name="inventorySearchCriteria.pendingApprovalDr" id="pendingApprovalDr" />
	</td>
</tr>

<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.inventory.salesOrderNumber"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td class="searchLabel labelStyle">
        <s:textfield name="inventorySearchCriteria.saleOrderNumber" id="saleOrderNumber"/>
	</td>
</tr>
<authz:ifUserInRole roles="processor">
<tr>
    <td></td>
    <td class="searchLabel labelStyle"> 
		<s:text name="label.common.itaTruckClass"></s:text>
	</td>
    <td class="searchLabel labelStyle"></td>
    <td> 
		<s:select id="inventorySearchCriteria.itaTruckClass" cssStyle="width:180px;" name="inventorySearchCriteria.groupCodeForProductFamily" 
    			  list="listItaTruckClass()" 
    			  headerKey="" headerValue="%{getText('label.common.selectHeader')}" />     			  
	</td>
</tr>
</authz:ifUserInRole>

<tr>
    <td></td>
    <td class="searchLabel labelStyle"><s:text name="label.common.MarketingGroupCode"/>:</td>
    <td class="searchLabel labelStyle"></td>
    <td>
        <s:textfield name="inventorySearchCriteria.marketingGroupCode" id="marketingGroupCode"/>
    </td>
</tr>

					<%-- 	<tr><td></td>
						<s:if test="loggedInUserAnInvAdmin">
							<td class="searchLabel labelStyle"><s:text name="label.common.preOrderBooking" />:</td>
							<td class="searchLabel labelStyle"><s:checkbox cssClass="buttonGeneric"
									name="inventorySearchCriteria.preOrderBooking" id="preOrderBooking" />
							</td></s:if>
						</tr> --%>
						<tr>
    
    <td></td>
   <td class="searchLabel labelStyle"><s:text name="Option Code"/>:</td>
    <td class="searchLabel labelStyle"></td>
	 <td>
	<s:textfield name="inventorySearchCriteria.options" id="inventorySearchCriteria.options"/> </td>
	</tr>
	 
<tr>
</tr>
  
    <td></td>
<td class="searchLabel labelStyle"><s:text name="Option Description"/>:</td>
     <td class="searchLabel labelStyle"></td>
	 <td>
	<s:textfield name="inventorySearchCriteria.optionDescription" id="inventorySearchCriteria.optionDescription"/> </td>
<tr>
	</tr>
	
	    </td>
	    
   <td class="searchLabel labelStyle" colspan="2" align="right"> 
   <s:text name="button.common.saveSearch"/>
        <s:checkbox cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
                    value="notATemporaryQuery" onclick="enableText()"
               >
        </s:checkbox>
    </td>
    <td> <!-- Fix for NMHGSLMS-992 -->
    	<s:if test="null != savedQueryName">
			<s:textfield name="savedQueryName" id="savedQueryName"></s:textfield>
		</s:if>
		<s:else>
			<s:textfield name="savedQueryName" id="savedQueryName" disabled="true" ></s:textfield>
		</s:else>	        
    </td>
</tr>
<tr>
<td></td>
<td></td>
<td></td>
<td class="label" valign="bottom">
           <s:reset label="reset" cssClass="buttonGeneric" onClick="location.href='preDefined_search_inventory.action?context=InventorySearches&refreshPage=true'" > 
         </s:reset>  
 
        <s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"/>  
   </td>
</tr>
<tr><td style="padding:0">&nbsp;</td></tr>
</table>
</div>
</form>
 
</div>
</div>
</u:body>
</html>