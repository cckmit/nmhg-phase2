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
</head>
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
    
function  OnDivScroll()
{
    var lstPolicyNames = document.getElementById("ListOfPoliciesId");
    if (lstPolicyNames.options.length > 15)
    {
        lstPolicyNames.size=lstCollegeNames.options.length;
    }
    else
    {
        lstPolicyNames.size=15;
    }
    
    }
    function OnSelectFocus()
{
       var lstPolicyNames = document.getElementById('ListOfPoliciesId');
  if( lstPolicyNames.options.length > 15)
    {
        lstPolicyNames.focus();
        lstPolicyNames.size=15;
    }
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
td {
 padding-bottom:15px;
}
</style>
<u:body smudgeAlert="false">

<form action="validatePreDefinedRetailInventorySearchFields.action?context_Predefined=InventoryRETAILSearch"
      method="POST">


<s:fielderror theme="xhtml"/>
<s:hidden name="context"/>
<s:hidden name="queryId"/>
<s:hidden name="inventorySearchCriteria.inventoryType.type" value="RETAIL"/>
<div dojoType="dijit.layout.BorderContainer" style="width: 100%; height: 100%;overflow-x:hidden;overflow-y:auto;" id="root">
    <div dojoType="dijit.layout.ContentPane" region="center" id="content">
<div class="policy_section_div" style="width:100%">
 <div id="dcap_pricing_title" class="section_header">
        <s:text name="label.common.retailInventorySearch"/>
    </div>
<table style="width:100%" border="0" cellspacing="0" cellpadding="0" class="grid">

<u:actionResults/>
<tr>
    <s:if test="(getLoggedInUser().businessUnits).size>1">
        <td class="searchLabel labelStyle" width="15%"><s:text name="label.common.businessUnit"/>:</td>
        <td > 
            <s:iterator value="businessUnits" status="buItr">
                <s:if test="inventorySearchCriteria.selectedBusinessUnits[#buItr.index] != null">
                    <input type="checkbox"
                           name="inventorySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
                           value="<s:property value="name" />" checked="true"/>
                    <s:property value="name"/>

                </s:if>
                <s:else>
                    <input type="checkbox"
                           name="inventorySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
                           value="<s:property value="name" />"/>
                    <s:property value="name"/>
                </s:else>
            </s:iterator>
        </td>
    </s:if>
</tr>
<tr>
	<td class="searchLabel labelStyle"><s:text name="label.warrantyAdmin.customerType" />:</td>
	<td>
	<s:select list="getCustomerTypes()" name="inventorySearchCriteria.customerType" disabled="false"
                                  listKey="key" listValue="value" headerKey="-1" headerValue="--Select--" 
                                   id="customerTypes"/>
	</td>
</tr>
<s:if test="loggedInUserAnInternalUser">
    <tr>
        <td class="searchLabel labelStyle" ><s:text name="label.common.dealerNumber"/>:</td>
        <td class="searchLabel">
            <s:textfield name="inventorySearchCriteria.dealerNumber" id="dealerNumber"/>
        </td>
    </tr>
    <tr>
        <td class="searchLabel labelStyle" ><s:text name="label.common.dealerName"/>:</td>
        <td class="searchLabel">
            <s:textfield name="inventorySearchCriteria.dealerName" id="dealerName" size="40"/>
        </td>
    </tr>
</s:if>
<s:if test="loggedInUserAnEnterpriseDealer">
    <tr>
        <td class="searchLabel labelStyle" width="20%" valign="top"><s:text name="label.common.childDealer"/>:</td>
        <td>
            <s:select name="inventorySearchCriteria.ChildDealers" list="childDealerShip"
                      listKey="id" listValue="name" multiple="true" size="6" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
</s:if>
<s:if test="manufacturingSiteVisible">
    <tr>
        <td class="searchLabel labelStyle" valign="top" width="18%"><s:text name="label.inventory.manufacturingSite"/>:</td>
        <td class="searchLabel" >
            <s:select name="inventorySearchCriteria.ManufacturingSite" list="listOfManufacturingSite"
                      listKey="id" listValue="description" multiple="true" size="6" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
</s:if>
<tr>
 <td class="searchLabel labelStyle" ><s:text name="label.inventory.warrantyType"/>: </td>
 <td class="searchLabel" >
       <s:select name="inventorySearchCriteria.warrantyType" list="warrantyTypes"
                          id="inventorySearchCriteria.warrantyType" listKey="type" listValue="%{getText(displayValue)}"
                           emptyOption="true" cssStyle="width:100px;" /> 
                           
                          
       </td> 
        </tr>
<s:if test="getLoggedInUser().isInternalUser() && displayInternalInstallType()">
<tr>
	<td class="searchLabel labelStyle" ><s:text name="label.internalInstallType"/></td>
	<td><s:select id="installType" cssStyle="width:180px;" name="inventorySearchCriteria.internalInstallType.id" 
    			  list="listInternalInstallTypes()" 
    			  headerKey="" headerValue="%{getText('label.common.selectHeader')}" 
    			  listKey="id" listValue="getDisplayInternalInstallType()"/></td>
</tr>
</s:if>
<tr>
	<td class="searchLabel labelStyle" ><s:text name="label.contractCode"/></td>
	<td><s:select id="contractCode" cssStyle="width:180px;" name="inventorySearchCriteria.contractCode.id" 
    			  list="listContractCodes()" 
    			  headerKey="" headerValue="%{getText('label.common.selectHeader')}" 
    			  listKey="id" listValue="getDisplayContractCode()"/></td>
</tr>

<tr>
    <td class="searchLabel labelStyle" ><s:text name="label.common.serialNumber"/>:</td>
    <td>
        <s:textfield name="inventorySearchCriteria.serialNumber" id="serialNumber"/>
    </td>
</tr>

<tr>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.productType"/>:</td>
    <td><s:select name="inventorySearchCriteria.productType"
                  id="inventorySearchCriteria.productType"
                  listKey="name" listValue="itemGroupDescription" list="productTypes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.products"/>:</td>
    <td><s:select name="inventorySearchCriteria.productGroupCode"
                  id="inventorySearchCriteria.products"
                  listKey="groupCode" listValue="groupCode" list="productCodes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="label.common.modelWithProductCode"/>:</td>
    <td><s:select name="inventorySearchCriteria.modelNumber"
                  id="inventorySearchCriteria.modelNumber"
                  listKey="name" listValue="itemGroupDescription" list="modelTypes" emptyOption="true"/></td>
    
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="label.common.condition"/>:</td>
    <td>
        <s:radio name="inventorySearchCriteria.condition" list="conditions" cssStyle="border:none"/>
    </td>
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="inventorySearchCriteria.customer.endCustomerCompanyName"/>:</td>
    <td>
        <s:textfield name="inventorySearchCriteria.customer.companyName" id="companyName"/>
    </td>
</tr>


<s:if test="loggedInUserAnInternalUser">
    <tr>
        <td class="searchLabel labelStyle"><s:text name="label.common.itemNumber"/>:</td>
        <td class="searchLabel">
            <s:textfield name="inventorySearchCriteria.itemNumber" id="itemNumber"/>
        </td>
    </tr>
</s:if>
<s:if test="buildDateVisible">
    <tr>
        <td class="searchLabel labelStyle" ><span style="float:left;"><s:text name="label.common.buildDate"/>:</span>
      <span style="float:right;"><s:text name="label.common.from"/>: </span>
        </td>
        <td><sd:datetimepicker name='inventorySearchCriteria.buildFromDate' id='buildFromDate' value='%{inventorySearchCriteria.buildFromDate}' />
        </td>
    </tr>
    <tr>
        <td class="preDefinedSearchLabel labelStyle" align="right">
            <s:text name="label.common.to"/>:
        </td>
        <td><sd:datetimepicker name='inventorySearchCriteria.buildToDate' id='buildToDate' value='%{inventorySearchCriteria.buildToDate}' />
        </td>
    </tr>
</s:if>
<tr>
    <td class="searchLabel labelStyle"><span style="float:left;"><s:text name="columnTitle.inventorySearchAction.delivery_date"/>:</span>
     <span style="float:right;"><s:text name="label.common.from"/>:</span>
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.deliveryFromDate' id='deliveryFromDate' value='%{inventorySearchCriteria.deliveryFromDate}' />
    </td>
</tr>
<tr>
    <td class="preDefinedSearchLabel labelStyle" align="right">
        <s:text name="label.common.to"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.deliveryToDate' id='deliveryToDate' value='%{inventorySearchCriteria.deliveryToDate}' />
    </td>
</tr>
<tr>
    <td class="searchLabel labelStyle"><span style="float:left;"><s:text name="label.common.submitDate"/>:</span>
      <span style="float:right;"><s:text name="label.common.from"/>: </span>
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.submitFromDate' id='submitFromDate' value='%{inventorySearchCriteria.submitFromDate}' />
    </td>
</tr>
<tr>
    <td class="preDefinedSearchLabel labelStyle" align="right">
        <s:text name="label.common.to"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.submitToDate' id='submitToDate' value='%{inventorySearchCriteria.submitToDate}' />
    </td>
</tr>
<tr>
        <td class="searchLabel labelStyle" valign="top" ><s:text name="label.inventory.coveredByPlan"/>:</td>
        <td class="searchLabel" style="padding-left:12px;" >
                
            <s:select list="listOfPolicies" multiple="true" size="16" id="ListOfPoliciesId" cssStyle="margin:0px;padding:0px;width:630px;"
                   	  listKey="id" listValue="description" name="inventorySearchCriteria.policies" onfocus="OnSelectFocus();"  />
                   	  
        </td>
    </tr>
    <tr>
        <td class="searchLabel labelStyle" width="20%" valign="top"><s:text name="label.common.discountType"/>:</td>
        <td>
            <s:select name="inventorySearchCriteria.discountType" list="listOfDiscountType"
                      listKey="id" listValue="description" multiple="true" size="3" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
<tr>
    <td class="searchLabel labelStyle" align="right">
       <span> <s:text name="label.common.from"/>: </span>
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.policyFromDate' id='policyFromDate' value='%{inventorySearchCriteria.policyFromDate}' />
    </td>
</tr>
<tr>
    <td class="preDefinedSearchLabel labelStyle" align="right" >
        <s:text name="label.common.to"/>:
    </td>
    <td><sd:datetimepicker name='inventorySearchCriteria.policyToDate' id='policyToDate' value='%{inventorySearchCriteria.policyToDate}' />
    </td>
</tr>
  
    
     <td class="searchLabel labelStyle"><s:text name="Option Code"/>:</td>
    
	 <td>
	<s:textfield name="inventorySearchCriteria.options" id="inventorySearchCriteria.options"/> </td>
	</tr>
	
	<td class="searchLabel labelStyle"><s:text name="Option Description"/>:</td>
    
	 <td>
	<s:textfield name="inventorySearchCriteria.optionDescription" id="inventorySearchCriteria.optionDescription"/> </td>
	</tr>
	
	<authz:ifUserInRole roles="processor">
<tr>
	<td class="searchLabel labelStyle"> 
		<s:text name="label.common.itaTruckClass"></s:text>
	</td>
	<td> 
		<s:select id="inventorySearchCriteria.itaTruckClass" cssStyle="width:180px;" name="inventorySearchCriteria.groupCodeForProductFamily" 
    			  list="listItaTruckClass()" 
    			  headerKey="" headerValue="%{getText('label.common.selectHeader')}" />     			  
	</td>
</tr>	
</authz:ifUserInRole> 

<tr>    
    <td class="searchLabel labelStyle"><s:text name="label.common.MarketingGroupCode"/>:</td>    
    <td>
        <s:textfield name="inventorySearchCriteria.marketingGroupCode" id="marketingGroupCode"/>
    </td>
</tr>

<tr>
    <td class="searchLabel labelStyle"><s:text name="label.inventory.salesOrderNumber"/>:</td>
        <td>    <s:textfield name="inventorySearchCriteria.saleOrderNumber" id="saleOrderNumber"/>
	</td>
</tr>

<tr>
<s:if test="!loggedInUserADealer">
 
  <td class="preDefinedSearchLabel labelStyle" align="right">
        <s:text name="label.warranty.DeliveryReport"/>
		<s:checkbox cssClass="buttonGeneric" name="inventorySearchCriteria.forFlagDR" id="inventorySearchCriteria.forFlagDR"
                    value="inventorySearchCriteria.forFlagDR" >
        </s:checkbox>
        
    </td>
  
</s:if>
    
</tr> 
    <td class="preDefinedSearchLabel labelStyle" align="right">
        <s:text name="button.common.saveSearch"/>
		<s:checkbox cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
                    value="notATemporaryQuery" onclick="enableText()"
                >
        </s:checkbox>
    </td>
    <td>  
    <s:if test = "savedQueryName!=null" >
						<s:textfield name="savedQueryName" id="savedQueryName" value="%{savedQueryName}"></s:textfield>
					</s:if>
					<s:else>
						<s:textfield name="savedQueryName"  id="savedQueryName" disabled="true" value="Name of the Query" ></s:textfield>
					</s:else>    
    </td>
</tr>              

<tr align="center">
    <td></td>
    <td align="left" class="searchLabel" valign="bottom">
        <s:reset label="reset" cssClass="buttonGeneric"></s:reset>
        <s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"/>
   
    </td>
</tr>
</table>
</div>
</div>
</div>        
</form>
</u:body>
</html>
