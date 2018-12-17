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

<table id="curRecoveryPayment" class="grid" cellspacing="0" cellpadding="0">
    <tr colspan="4">
	<td class="label" style="width:25%; text-align:left;"><s:text name="label.common.creditMemoNumber"/></td>
	<td style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.creditMemoNumber"/></td>
	
	
	<td class="label" style="width:25%; text-align:left;"><s:text name="label.common.creditMemoDate"/></td>
	<td class="date" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.creditMemoDate"/></td>
	
	<td/>
	<td/>
	
    </tr>	
    <tr colspan="4">
	
	<td class="label" style="width:25%; text-align:left;"><s:text name="label.common.TaxAmount"/></td>
	<td class="numeric" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.taxAmount"/></td>
	 
	<td class="label" style="width:25%; text-align:left;"><s:text name="claim.prieview.ContentPane.amount"/></td>
	<td class="numeric" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.paidAmount.abs()"/></td>

    </tr>
    <tr colspan="4">
    	<td class="label" style="width:25%; text-align:left;"><s:text name="label.common.crdrFlag"/></td>
		<td class="numeric" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.crDrFlag"/></td>	
		
		<td class="label" style="width:25%; text-align:left;"><s:text name="label.common.creditMemoAmount"/></td>
		<td class="numeric" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.creditAmount.abs()"/></td>
    </tr>
     <tr colspan="4">
    	<td class="label" style="width:25%; text-align:left;"><s:text name="label.supplierRecovery.memoComments"/></td>
		<td class="numeric" style="width:25%; text-align:left;"><s:property value="recoveryClaim.recoveryPayment.activeCreditMemo.creditMemoComments"/></td>	
		
    </tr>
  
</table>
