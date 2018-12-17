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


<s:if test="!recoveryClaim.recoveryPayment.previousCreditMemos.isEmpty()">
    <table id="recoveryPayment" class="grid borderForTable" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th style="text-align:left;" class="warColHeader"><s:text name="label.common.creditMemoNumber"/></th>
                <th style="text-align:left;" class="warColHeader"><s:text name="label.common.creditMemoDate"/></th>
                <th/>
                <th style="text-align:left;" class="warColHeader"><s:text name="label.common.TaxAmount"/></th>
                <th/>
                <th style="text-align:left;" class="warColHeader"><s:text name="label.common.paidAmount"/></th>
                <th/>
                <th style="text-align:left;" class="warColHeader"><s:text name="label.common.crdrFlag"/></th>
            </tr>
        </thead>
        <tbody>
        <!-- NMHGSLMS-1361 To show active Credit Memo in credit memo history -->
        	<s:iterator value="recoveryClaim.recoveryPayment.activeCreditMemo">
                   <tr class="total">
                      <td><s:property value="creditMemoNumber"/></td>
                      <td class="date" style="text-align:left;"><s:property value="creditMemoDate"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="taxAmount"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="paidAmount.abs()"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="crDrFlag"/></td>	
                   </tr>
            </s:iterator> 
        
            <!-- added by arindam-->
			<s:iterator value="recoveryClaim.recoveryPayment.previousCreditMemos">
                   <tr class="total">
                      <td><s:property value="creditMemoNumber"/></td>
                      <td class="date" style="text-align:left;"><s:property value="creditMemoDate"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="taxAmount"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="paidAmount.abs()"/></td>
                      <td/>
                      <td class="numeric" style="text-align:left;"><s:property value="crDrFlag"/></td>	
                   </tr>
            </s:iterator>        
        </tbody>
    </table>
</s:if>
