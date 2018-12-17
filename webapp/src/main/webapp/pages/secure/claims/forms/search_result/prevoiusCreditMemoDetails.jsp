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
<%@taglib prefix="t" uri="twms" %>
<%@taglib prefix="u" uri="/ui-ext" %>
<script type="text/javascript">
    dojo.require("dijit.Tooltip")
</script>
<s:if test="!claim.latestPaidPayment != null">
    <div style="overflow:auto; width:100%;">
        <table id="payment" class="grid borderForTable" cellspacing="0" align="center" cellpadding="0"
               style="width:95%">
            <thead>
            <tr class="row_head">
                <th><s:text name="label.common.creditMemoNumber"/></th>
                <th><s:text name="label.common.creditMemoDate"/></th>
                <th><s:text name="label.common.TaxAmount"/></th>
                <th><s:text name="label.common.paidAmountWithoutTax"/></th>
                <th><s:text name="label.common.crdrFlag"/></th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="claim.allPaidPayments" status="claims">
                <tr>
                    <td class="text"><s:property value="activeCreditMemo.creditMemoNumber"/></td>
                    <td class="date"><s:property value="activeCreditMemo.creditMemoDate"/></td>
                    <td class="numeric"> 
                    <span id="taxAmount_<s:property value = '%{#claims.index}' />">
						<s:property value="activeCreditMemo.taxAmount" />
					</span>
					<s:if test="%{!activeCreditMemo.taxAmount.breachEncapsulationOfCurrency().equals(activeCreditMemo.taxAmountErpCurrency.breachEncapsulationOfCurrency())}">
					<span dojoType="dijit.Tooltip" connectId="taxAmount_<s:property value = '%{#claims.index}' />">
						<s:property value="%{activeCreditMemo.taxAmountErpCurrency.abs()}"/>
					</span>	
					</s:if>
					</td>
  					<td class="numeric">
					<span id="paidAmount_<s:property value = '%{#claims.index}' />">
						<s:property value="activeCreditMemo.paidAmount.abs()" />
					</span>
					<s:if test="%{!activeCreditMemo.paidAmount.breachEncapsulationOfCurrency().equals(activeCreditMemo.paidAmountErpCurrency.breachEncapsulationOfCurrency())}">
					<span dojoType="dijit.Tooltip" connectId="paidAmount_<s:property value = '%{#claims.index}' />">
						<s:property value="%{activeCreditMemo.paidAmountErpCurrency.abs()}"/>
					</span>	
					</s:if>
					</td>
                    <td class="text"><s:property value="activeCreditMemo.crDrFlag"/></td>
                </tr>
            </s:iterator>
            </tbody>
        </table>
    </div>
</s:if>