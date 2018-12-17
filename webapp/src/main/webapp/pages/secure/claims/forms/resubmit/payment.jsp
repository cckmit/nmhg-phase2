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

<s:if test="!claim.payment.lineItemGroups.isEmpty()">
    <table id="payment">
        <thead>
            <tr>
                <th style="text-align:left;"><s:text name="label.common.category"/>:</th>
                <th style="text-align:right;"><s:text name="label.newClaim.amount"/>:</th>
            </tr>
        </thead>
        <tbody>
            <s:iterator value="claim.payment.lineItemGroups">
                <s:if test="groupTotal.isPositive()">
                    <s:if test="lineItems.empty && name != 'Claim Amount'">
                        <tr class="total">
                           <td><s:property value="name"/></td>
                           <td class="numeric"><s:property value="total"/></td>
                        </tr>
                    </s:if>

                    <s:elseif test="name != 'Claim Amount'">
                        <tr>
                            <td><s:property value="name"/></td>
                            <td class="numeric"><s:property value="groupTotal"/></td>
                        </tr>
                        <s:iterator value="lineItems" status="status">
                            <tr>
                                <td><s:property value="name"/></td>
                                <td class="numeric"><s:property value="value"/></td>
                            </tr>
                        </s:iterator>
                        <tr class="total">
                            <td><s:text name="label.common.total"/> <s:property value="name"/></td>
                            <td class="numeric"><s:property value="total"/></td>
                        </tr>
                    </s:elseif>
                </s:if>
	    	  <tr>
	                <td><s:text name="label.newClaim.percentageAccepted"/></td>
	                <td class="numeric">
	                	<s:property value="percentageAcceptance"/>
	                </td>
	         </tr>
	          <tr class="total">
	                <td><s:text name="label.viewClaim.acceptedTotal"/> <s:property value="name"/></td>
	                <td class="numeric"><s:property value="acceptedTotal"/></td>
	          </tr>
            </s:iterator>
            
            <s:iterator value="claim.payment.lineItemGroups">
                <s:if test="groupTotal.isPositive() && name == 'Claim Amount'">
                    <s:if test="lineItems.empty">
                        <tr class="total">
                           <td><s:property value="name"/></td>
                           <td class="numeric"><s:property value="total"/></td>
                        </tr>
                    </s:if>

                    <s:elseif test="name == 'Claim Amount'">
                        <tr>
                            <td><s:property value="name"/></td>
                            <td class="numeric"><s:property value="groupTotal"/></td>
                        </tr>
                        <s:iterator value="lineItems" status="status">
                            <tr>
                                <td><s:property value="name"/></td>
                                <td class="numeric"><s:property value="value"/></td>
                            </tr>
                        </s:iterator>
                        <tr class="total">
                            <td><s:text name="label.common.total"/> <s:property value="name"/></td>
                            <td class="numeric"><s:property value="total"/></td>
                        </tr>
                    </s:elseif>
                </s:if>
	    	  <tr>
	                <td><s:text name="label.newClaim.percentageAccepted"/></td>
	                <td class="numeric">
	                	<s:property value="percentageAcceptance"/>
	                </td>
	         </tr>
	          <tr class="total">
	                <td><s:text name="label.viewClaim.acceptedTotal"/> <s:property value="name"/></td>
	                <td class="numeric"><s:property value="acceptedTotal"/></td>
	          </tr>
            </s:iterator>
            <s:if test="claim.payment.activeCreditMemo != null">
            <tr>
		        <td><s:text name="label.viewClaim.creditMemoNumber"/></td>
		        <td class="numeric"><s:property value="claim.payment.activeCreditMemo.creditMemoNumber"/></td>
			</tr>
			     
            <tr>
		        <td><s:text name="label.viewClaim.tax"/></td>
		        <td class="numeric"><s:property value="claim.payment.activeCreditMemo.taxAmount"/></td>
		     </tr>
			 </s:if>    
		     <tr class="total">
		        <td><s:text name="label.viewClaim.amountPaid"/></td>
		        <td class="numeric"><s:property value="claim.payment.totalAmountPaidAfterTax"/></td>
		     </tr>
        </tbody>
    </table>
</s:if>
