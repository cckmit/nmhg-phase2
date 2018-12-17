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

<script>
function closeCurrentTab() {
    closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
}

function computeTotalValue(partIndex,totalCount, field){
	var fieldValue = parseFloat(field.value);
    if(isNaN(fieldValue)){
        field.value="0.00";
    }
    else {
        field.value=parseFloat(fieldValue).toFixed(2);
    }
    var totalvalue=0.00;
    var totalCnt = parseFloat(totalCount);
    var sectionCost="0";
    for(var i=0; i!=totalCnt; i++)
    {
    	 if(document.getElementById("Actual_Value_"+partIndex+"_"+i).value!=""){
            sectionCost=document.getElementById("Actual_Value_"+partIndex+"_"+i).value;
        }
       
        var sectionCost = parseFloat(sectionCost);
        if(!isNaN(sectionCost)){
            totalvalue = totalvalue + sectionCost;
        }
        
        totalvalue = parseFloat(totalvalue)+ parseFloat(sectionCost);
    }
    document.getElementById("totalValue"+partIndex).innerHTML=totalvalue.toFixed(2);
}

function expandParts(oemPartReplaced){
    var partStyle = document.getElementById(oemPartReplaced).style.display;
    if(partStyle=="none"){
        document.getElementById(oemPartReplaced).style.display="block";
        document.getElementById("td"+oemPartReplaced).className = "sectionOpenSM";
    }else{
        document.getElementById(oemPartReplaced).style.display="none";
        document.getElementById("td"+oemPartReplaced).className = "sectionCloseSM";
    }
}

function expandPartsSection(partSection,count){
    var partClass = document.getElementById("td"+partSection).className;
    if(partClass=="tdsectionCloseSM"){
        for(var i=0; i!=count; i++)
        {
            document.getElementById(partSection+i).style.display="block";;
            document.getElementById("td"+partSection+i).className = "sectionOpenSM";
            document.getElementById("td"+partSection).className="tdsectionOpenSM";
        }
    }else{
        for(var i=0; i!=count; i++)
        {
            document.getElementById(partSection+i).style.display="none";;
            document.getElementById("td"+partSection+i).className = "sectionCloseSM";
            document.getElementById("td"+partSection).className="tdsectionCloseSM";
        }
    }
}

// Global variables to store the dom's
<s:set name="arraySize" value="%{claim.serviceInformation.serviceDetail.oemPartsReplaced.size()}"></s:set>
var enabledCostLineItems = new Array(<s:property value="%{#arraySize}"/>);
var disabledCostLineItems = new Array(<s:property value="%{#arraySize}"/>);
var zeroCostLineItems = new Array(<s:property value="%{#arraySize}"/>);
</script>

<style type="text/css">
.totalAmountRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	text-transform:uppercase;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
.totalAmountTextRightalign {
	font-family:Verdana,sans-serif,Arial,Helvetica;
	font-size:7pt;
	background-color:#F5F5F5;
	color:#636363;
	padding-left:2px;
	align: right;
}
</style>

<s:hidden name="id"></s:hidden>

<div dojoType="dijit.layout.ContentPane" >
	<div dojoType="twms.widget.TitlePane" title="Recoverable Parts" labelNodeClass="section_header"
		id="recoverableParts" open="true">
             
    <table width="100%" border="0" cellspacing="1" cellpadding="0" class="grid">
    <tr>
        <td  colspan="3"><s:text name="label.supplier.supplierTitle" />: </td>
        <td class="label" colspan="2"><s:property value="claim.serviceInformation.oemPartReplaced.supplierPartReturn.contract.supplier.name"></s:property></td>
        <td  colspan="2"><s:text name="label.supplier.rejectionReason" />: </td>
        <td class="label" colspan="2"><s:property value="claim.serviceInformation.oemPartReplaced.supplierPartReturn.supplierComment"></s:property></td>
    </tr>
    </table>
<table width="100%" border="0" cellspacing="1" cellpadding="0" class="grid borderForTable">
    <tr>
        <td width="3%" class="tdsectionCloseSM" id="tdoemPartReplaced" onclick="expandPartsSection('oemPartReplaced','<s:property value="partsToBeShown.size()"/>');">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td width="10%" class="colHeaderTop"><s:text name="label.claim.partNumber" /></td>
        <td width="11%" class="colHeaderTop" ><s:text name="label.supplier.supplier_part_no" /></td>
        <td width="11%" class="colHeaderTop" ><s:text name="label.common.quantity" /></td>
        <td width="20%" class="colHeaderTop" ><s:text name="label.supplier.supplierReason" /></td>
        <td width="25%" class="colHeaderTop" ><s:text name="label.common.action" /></td>
        <td width="20%" class="colHeaderTop" ><s:text name="label.common.reason" /></td>
    </tr>
    <tr><td colspan="7">

    <s:set name="partsToBeShownCount" value="0" scope="page"></s:set>
    <s:iterator
        value="claim.serviceInformation.serviceDetail.oEMPartsReplaced" status="partsStatus" id="partReplaced">
        <s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}]" value="%{#partReplaced.id}"></s:hidden>
        <tr>
            <td id="tdoemPartReplaced<s:property value="#attr['partsToBeShownCount']"/>" class="sectionCloseSM" width="3%" align="left" onclick="expandParts('oemPartReplaced<s:property value="#attr['partsToBeShownCount']"/>');" ></td>
            <td  width="10%">
                <s:if test="itemReference.serialized == true">
                    <s:property value="itemReference.referredInventoryItem.serialNumber" />
                </s:if><s:else>
                    <s:property value="itemReference.referredItem.number" />
                </s:else>
            </td>
            <td width="11%" ><s:property value="claim.serviceInformation.oemPartReplaced.supplierPartReturn.supplierItem.number"></s:property></td>

            <td width="11%" ><s:property value="claim.serviceInformation.oemPartReplaced.numberOfUnits"></s:property></td>
            <td width="20%" ><s:property value="claim.serviceInformation.oemPartReplaced.supplierPartReturn.status.status"></s:property></td>
           	<td width="25%"  valign="center">
            	<s:select list="%{getAllTransitions(claim.serviceInformation.oemPartReplaced.supplierPartReturn)}" name="transitionsTaken[%{#attr['partsToBeShownCount']}]" 
            		headerKey="null" headerValue="--Select--" id="transition[%{#partsStatus.index}]" />
            </td>
            <td width="20%"  valign="center">
                    <s:textfield name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.sraComment" value="%{supplierPartReturn.sraComment}"></s:textfield>
            </td>
        </tr>
        <tr><td colspan="8">
        <div id="oemPartReplaced<s:property value="#attr['partsToBeShownCount']"/>" style="display:none">
        <table width="100%" border="0" cellspacing="1" cellpadding="0" >
        <tr >
            <td width="3%" >&nbsp;</td>
            <td width="32%" class="subSectionTitle"><div align="center"><s:text name="label.common.details" /></div></td>
            <td width="15%" >&nbsp;</td>
            <td width="50%" class="subSectionTitle" ><div align="center"><s:text name="label.common.individual" /></div></td>
        </tr>
        <tr>
            <td width="3%" >&nbsp;</td>
            <td width="32%" valign="top" >
                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor" >
                    <tr>
                        <td width="60%" ><s:text name="label.supplier.supplier_part_no" /></td>
                        <td width="40%" ><s:property value="supplierPartReturn.supplierItem.number"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.cost" /></td>
                        <td width="40%" ><s:property value="#partReplaced.cost()"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.price" /></td>
                        <td width="40%" ><s:property value="#partReplaced.getSupplierPartCost()"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.contract" /></td>
                        <td width="40%" >
                        <s:a onclick="javascript:sendThisRequest('%{#partReplaced.supplierPartReturn.contract.id}');">
                        	<s:property value="#claim.serviceInformation.serviceDetail.oEMPartsReplaced.supplierPartReturn.contract.name"></s:property>
                        </s:a>
                        </td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.shipment.ship" /></td>
                        <td width="40%" align="center"><input type="checkbox" checked="checked" disabled="true"></td>
                    </tr>
                </table>
            </td>
            <td width="15%" >&nbsp;</td>
            <td width="50%">
            
            	<div id="costLineItemDetails<s:property value='%{#partsStatus.index}'/>">
	                
	                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor" 
	                	id="enabledCostLineItems<s:property value='%{#partsStatus.index}'/>">
	                <tr>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.inventory.costElement" /></td>
	                    <td width="15%" class="subSectionTitle"><div align="right"><s:text name="label.inventory.share" /></div></td>
	                    <td width="30%" class="subSectionTitle"><s:text name="label.contract.contractValue" /></td>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.contract.actualValue" /></td>
	                </tr>
	                <s:set name="totalCount%{#partsStatus.index}" value="%{#partReplaced.supplierPartReturn.costLineItems.size()}" scope="page"></s:set>
	
	                <s:iterator	value="#partReplaced.supplierPartReturn.costLineItems" status="lineItemStatus" id="costLineItem">
	                <s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}]"
	                value="%{#costLineItem.id}"/>
	                <tr>
	                    <td ><s:property value="%{#costLineItem.section.name}"/></td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'actualCost',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'costAfterApplyingContract',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
	                    <t:money id="Actual_Value_%{#partsStatus.index}_%{#lineItemStatus.index}" name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}].recoveredCost"  defaultSymbol="$"
	                         value="%{#costLineItem.recoveredCost}" size="7" maxlength="10" cssStyle="margin:-1px"
	                         onchange="computeTotalValue(%{#partsStatus.index},'%{#attr['totalCount'+#partsStatus.index]}',this)"/>
	                    </td>
	                </tr>
	                </s:iterator>
	                <tr>
	                    <td width="25%" class="totalAmountRightalign"><s:text name="label.common.total" />:</td>
	                    <td width="15%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'actualCost')"></s:property>
	                    </td>
	                    <td width="30%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'costAfterApplyingContract')"></s:property>
	                    </td>
	                    <td width="25%" class="totalAmountTextRightalign"
		                   nowrap="nowrap"><s:set name="totalCostOfSection"
		                   value="%{getTotalCostForSection(id,'recoveredCost')}" /><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfCurrency().getCurrencyCode()}" />
		                   <span id="totalValue<s:property value='%{#partsStatus.index}'/>"><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfAmount()}" /></span>
		               </td>                    
	                </tr>
	                </table>
	                
	                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor" 
	                	id="disabledCostLineItems<s:property value='%{#partsStatus.index}'/>">
	                <tr>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.inventory.costElement" /></td>
	                    <td width="15%" class="subSectionTitle"><div align="right"><s:text name="label.inventory.share" /></div></td>
	                    <td width="30%" class="subSectionTitle"><s:text name="label.contract.contractValue" /></td>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.contract.actualValue" /></td>
	                </tr>
	                <s:set name="totalCount%{#partsStatus.index}" value="%{#partReplaced.supplierPartReturn.costLineItems.size()}" scope="page"></s:set>
	
	                <s:iterator	value="#partReplaced.supplierPartReturn.costLineItems" status="lineItemStatus" id="costLineItem">
	                <s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}]"
	                value="%{#costLineItem.id}"/>
	                <tr>
	                    <td ><s:property value="%{#costLineItem.section.name}"/></td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'actualCost',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'costAfterApplyingContract',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
							<s:property value="%{#costLineItem.recoveredCost.breachEncapsulationOfCurrency().getCurrencyCode()}"/>&nbsp;<s:textfield
								size="7" value="%{#costLineItem.recoveredCost.breachEncapsulationOfAmount()}" disabled="true"/> 
	                     	<s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}].recoveredCost"
	                     		value="%{#costLineItem.recoveredCost.breachEncapsulationOfCurrency().getCurrencyCode()}"/>
	                     	<s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}].recoveredCost"
	                     		value="%{#costLineItem.recoveredCost.breachEncapsulationOfAmount()}"/>
	                    </td>
	                </tr>
	                </s:iterator>
	                <tr>
	                    <td width="25%" class="totalAmountRightalign"><s:text name="label.common.total" />:</td>
	                    <td width="15%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'actualCost')"></s:property>
	                    </td>
	                    <td width="30%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'costAfterApplyingContract')"></s:property>
	                    </td>
	                    <td width="25%" class="totalAmountTextRightalign"
		                   nowrap="nowrap"><s:set name="totalCostOfSection"
		                   value="%{getTotalCostForSection(id,'recoveredCost')}" /><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfCurrency().getCurrencyCode()}" />
		                   <span id="totalValue<s:property value='%{#partsStatus.index}'/>"><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfAmount()}" /></span>
		               </td>                    
	                </tr>
	                </table>
	                
	                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor" 
	                	id="zeroCostLineItems<s:property value='%{#partsStatus.index}'/>">
	                <tr>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.inventory.costElement" /></td>
	                    <td width="15%" class="subSectionTitle"><div align="right"><s:text name="label.inventory.share" /></div></td>
	                    <td width="30%" class="subSectionTitle"><s:text name="label.contract.contractValue" /></td>
	                    <td width="25%" class="subSectionTitle"><s:text name="label.contract.actualValue" /></td>
	                </tr>
	                <s:set name="totalCount%{#partsStatus.index}" value="%{#partReplaced.supplierPartReturn.costLineItems.size()}" scope="page"></s:set>
	
	                <s:iterator	value="#partReplaced.supplierPartReturn.costLineItems" status="lineItemStatus" id="costLineItem">
	                <s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}]"
	                value="%{#costLineItem.id}"/>
	                <tr>
	                    <td ><s:property value="%{#costLineItem.section.name}"/></td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'actualCost',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
	                        <s:property value="getCostForSection(#partReplaced.id,'costAfterApplyingContract',#costLineItem.section.name)"/>
	                    </td>
	                    <td class="tableDataAmount" valign="middle">
							<s:property value="%{#costLineItem.recoveredCost.breachEncapsulationOfCurrency().getCurrencyCode()}"/>&nbsp;<s:textfield
								size="7" value="%{#costLineItem.recoveredCost.breachEncapsulationOfAmount()*0}" disabled="true"/> 
	                     	<s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}].recoveredCost"
	                     		value="%{#costLineItem.recoveredCost.breachEncapsulationOfCurrency().getCurrencyCode()}"/>
	                     	<s:hidden name="oemPartsReplaced[%{#attr['partsToBeShownCount']}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}].recoveredCost"
	                     		value="%{#costLineItem.recoveredCost.breachEncapsulationOfAmount() * 0}"/>
	                    </td>
	                </tr>
	                </s:iterator>
	                <tr>
	                    <td width="25%" class="totalAmountRightalign"><s:text name="label.common.total" />:</td>
	                    <td width="15%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'actualCost')"></s:property>
	                    </td>
	                    <td width="30%" class="totalAmountTextRightalign">
	                        <s:property value="getTotalCostForSection(id,'costAfterApplyingContract')"></s:property>
	                    </td>
	                    <td width="25%" class="totalAmountTextRightalign"
		                   nowrap="nowrap"><s:set name="totalCostOfSection"
		                   value="%{getTotalCostForSection(id,'recoveredCost')}" /><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfCurrency().getCurrencyCode()}" />
		                   <span id="totalValue<s:property value='%{#partsStatus.index}'/>"><s:property
		                   value="%{#totalCostOfSection.breachEncapsulationOfAmount()*0}" /></span>
		               </td>                    
	                </tr>
	                </table>

                </div>
                
                
<script type="text/javascript">
	enabledCostLineItems[<s:property value="%{#partsStatus.index}"/>] = dojo.byId("enabledCostLineItems<s:property value='%{#partsStatus.index}'/>");
	disabledCostLineItems[<s:property value="%{#partsStatus.index}"/>] = dojo.byId("disabledCostLineItems<s:property value='%{#partsStatus.index}'/>");
	zeroCostLineItems[<s:property value="%{#partsStatus.index}"/>] = dojo.byId("zeroCostLineItems<s:property value='%{#partsStatus.index}'/>");
	
	dojo.addOnLoad ( function() {
		dojo.dom.removeNode(zeroCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
		<s:if test="supplierPartReturn.status.equals(@tavant.twms.domain.partreturn.PartReturnStatus@PART_REJECTED)">
			dojo.dom.removeNode(disabledCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
		</s:if>
		<s:if test="supplierPartReturn.status.equals(@tavant.twms.domain.partreturn.PartReturnStatus@PART_ACCEPTED)">
			dojo.dom.removeNode(enabledCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
		</s:if>
		var transition = dijit.byId("transition[<s:property value='%{#partsStatus.index}'/>]");
		var costLineItemsDiv = dojo.byId("costLineItemDetails<s:property value='%{#partsStatus.index}'/>");
		dojo.connect(transition, "onChange", function(value) {
			if(transition.getState().value == "Unrecovered Submit"){
				<s:if test="supplierPartReturn.status.equals(@tavant.twms.domain.partreturn.PartReturnStatus@PART_REJECTED)">
					dojo.dom.removeNode(enabledCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
				</s:if>
				<s:else>
					dojo.dom.removeNode(disabledCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
				</s:else>
				dojo.dom.removeNode(disabledCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
				dojo.dom.insertAtIndex(zeroCostLineItems[<s:property value="%{#partsStatus.index}"/>], 
					costLineItemsDiv, 0);
			}
			else {
				dojo.dom.removeNode(zeroCostLineItems[<s:property value="%{#partsStatus.index}"/>]);
				<s:if test="supplierPartReturn.status.equals(@tavant.twms.domain.partreturn.PartReturnStatus@PART_REJECTED)">
					dojo.dom.insertAtIndex(enabledCostLineItems[<s:property value="%{#partsStatus.index}"/>],
						costLineItemsDiv, 0);
				</s:if>
				<s:else>
					dojo.dom.insertAtIndex(disabledCostLineItems[<s:property value="%{#partsStatus.index}"/>], 
						costLineItemsDiv, 0);
				</s:else>
			}
		});
		delete transition;
	});
	
	
</script>
                
            </td>
        </tr>
    </table>
    </div>
    </td></tr>
    <s:set name="partsToBeShownCount" value="#attr['partsToBeShownCount']+1" scope="size"></s:set>
	
    </s:iterator>
    </td>
    </tr>
    </table>
	</div>
</div>


<s:if test="partsNotToBeShown.size() > 0">
    	<div dojoType="dijit.layout.ContentPane" >
		<div dojoType="twms.widget.TitlePane" title="Other Parts" labelNodeClass="section_header"
			id="otherParts" open="true">
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr>
        <td width="5%" class="tdsectionCloseSM" id="tdpartsNotBeShown" onclick="expandPartsSection('partsNotBeShown','<s:property value="partsToBeShown.size()"/>');">&nbsp;&nbsp;&nbsp;</td>
            <td width="10%" class="colHeaderTop"><s:text name="label.contractAdmin.partNumber" /></td>
            <td width="11%" class="colHeaderTop" ><s:text name="label.supplier.supplier_part_no" /></td>
            <td width="10%" class="colHeaderTop" ><s:text name="label.common.quantity" /></td>
            <td width="19%" class="colHeaderTop" ><s:text name="label.common.status" /></td>
            <td width="27%" class="colHeaderTop" ><s:text name="label.supplierSraReview.supplierTitle" /></td>
            <td width="10%" class="colHeaderTop" ><s:text name="label.contract.contractValue" /></td>
            <td width="10%" class="colHeaderTop" ><s:text name="label.contract.actualValue" /></td>
        </tr>
        <s:set name="partsNotToBeShownCount" value="0" scope="size"></s:set>

        <s:iterator
        value="claim.serviceInformation.serviceDetail.oEMPartsReplaced" status="partsStatus" id="partReplaced">
        <s:if test="#partReplaced.id in partsNotToBeShown">
        <tr>
            <td id="tdpartsNotBeShown<s:property value="#attr['partsNotToBeShownCount']"/>" class="sectionCloseSM" width="5%" align="left" onclick="expandParts('partsNotBeShown<s:property value="#attr['partsNotToBeShownCount']"/>');" >&nbsp;&nbsp;&nbsp;</td>
            <td  width="10%">
                <s:if test="itemReference.serialized == true">
                    <s:property value="itemReference.referredInventoryItem.serialNumber" />
                </s:if><s:else>
                    <s:property value="itemReference.referredItem.number" />
                </s:else>
            </td>
            <td width="11%" ><s:property value="supplierPartReturn.supplierItem.number"></s:property></td>

            <td width="10%" ><s:property value="numberOfUnits"></s:property></td>
            <td width="19%" ><s:property value="supplierPartReturn.status.status"></s:property></td>
            <td width="27%"  ><s:property value="supplierPartReturn.supplier.name"/></td>

            <td width="10%" class="tableDataAmount" valign="middle" >
                <s:property value="getTotalCostForSection(id,'costAfterApplyingContract')"></s:property>
            </td>
            <td width="10%" class="tableDataAmount" valign="middle">
                    <s:property value="%{getTotalCostForSection(id,'recoveredCost')}"></s:property>
            </td>
        </tr>
        <tr ><td colspan="8">
        <div id="partsNotBeShown<s:property value="#attr['partsNotToBeShownCount']"/>" style="display:none">
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr >
            <td width="3%" >&nbsp;</td>
            <td width="31%" class="subSectionTitle" ><div align="center"><s:text name="label.common.details" /></div></td>
            <td width="19%" >&nbsp;</td>
            <td width="47%" class="subSectionTitle" ><div align="center"><s:text name="label.common.individual" /></div></td>
        </tr>
        <tr>
            <td width="3%" >&nbsp;</td>
            <td width="31%" valign="top">
                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor">
                    <tr>
                        <td width="60%" ><s:text name="label.supplier.supplier_part_no" /></td>
                        <td width="40%" ><s:property value="supplierPartReturn.supplierItem.number"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.cost" /></td>
                        <td width="40%" ><s:property value="#partReplaced.cost()"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.price" /></td>
                        <td width="40%" ><s:property value="#partReplaced.getSupplierPartCost()"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.common.contract" /></td>
                        <td width="40%" >
                        <s:property value="#partReplaced.supplierPartReturn.contract.name"></s:property></td>
                    </tr>
                    <tr>
                        <td width="60%" ><s:text name="label.shipment.ship" /></td>
                        <td width="40%" align="center"><input type="checkbox" disabled="disabled"></td>
                    </tr>
                </table>
            </td>
            <td width="19%" >&nbsp;</td>
            <td width="47%" >
                <table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgcolor">
                <tr>
                    <td width="25%" class="subSectionTitle"><s:text name="label.inventory.costElement" /></td>
                    <td width="15%" class="subSectionTitle"><div align="right"><s:text name="label.inventory.share" /></div></td>
                    <td width="30%" class="subSectionTitle"><s:text name="label.contract.contractValue" /></td>
                    <td width="25%" class="subSectionTitle"><s:text name="label.contract.actualValue" /></td>
                </tr>
                <s:set name="totalCount%{#partsStatus.index}" value="%{#partReplaced.supplierPartReturn.costLineItems.size()}" scope="page"></s:set>

                <s:iterator	value="#partReplaced.supplierPartReturn.costLineItems" status="lineItemStatus" id="costLineItem">
                <tr>
                    <td ><s:property value="%{#costLineItem.section.name}"/></td>
                    <td class="tableDataAmount" valign="middle">
                        <s:property value="getCostForSection(#partReplaced.id,'actualCost',#costLineItem.section.name)"/>
                    </td>
                    <td class="tableDataAmount" valign="middle">
                        <s:property value="getCostForSection(#partReplaced.id,'costAfterApplyingContract',#costLineItem.section.name)"/>
                    </td>
                    <td class="tableDataAmount" valign="middle">
                        <s:property value="%{#costLineItem.recoveredCost}"></s:property>
                    </td>
                </tr>
                </s:iterator>
                <tr>
                    <td width="25%" class="totalAmountRightalign"><s:text name="label.common.total" />:</td>
                    <td width="15%" class="totalAmountTextRightalign">
                        <s:property value="getTotalCostForSection(id,'actualCost')"></s:property>
                    </td>
                    <td width="30%" class="totalAmountTextRightalign">
                        <s:property value="getTotalCostForSection(id,'costAfterApplyingContract')"></s:property>
                    </td>
                    <td width="25%" class="totalAmountTextRightalign">
                        <s:property value="%{getTotalCostForSection(id,'recoveredCost')}"></s:property>
                    </td>
                </tr>
                </table>
            </td>
        </tr>
    </table>
    </div>
    </td></tr>
    <s:set name="partsNotToBeShownCount" value="#attr['partsNotToBeShownCount']+1" scope="size"></s:set>
    </s:if>
    </s:iterator>
    </table>
    </div>
    </div>
</s:if>

<s:iterator value="partsToBeShown" id="partToBeShown" status="status">
    <s:hidden name="partsToBeShown[%{#status.index}]"></s:hidden>
</s:iterator>
<s:iterator value="partsNotToBeShown" id="partsNotToBeShownCount" status="partNotShownstatus">
    <s:hidden name="partsNotToBeShown[%{#partNotShownstatus.index}]"></s:hidden>
</s:iterator>
<div dojoType="twms.widget.TitlePane" title="Decision" labelNodeClass="section_header">
<jsp:include page="actions.jsp"></jsp:include>
<div>


<div class="buttonWrapperPrimary">
	<input type="button" onClick ="closeCurrentTab()" value="Cancel" class="buttonGeneric" />
    <input type="Submit"  value="<s:text name='button.common.submit' />" class="buttonGeneric" />
</div>
<script type="text/javascript">
    function sendThisRequest(id) {
        var url = "contract_detail.action?id=" + id;
        var tabLabel = 'Contract Code ' + id;
        top.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf: getMyTabLabel() });
    }
</script>