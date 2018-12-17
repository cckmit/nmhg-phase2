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

<style>
.borderForTable tr td{
border:none !important;
}
</style>
<table width="96%" class="grid borderForTable" cellpadding="0" cellspacing="0" >
    <thead>
        <tr class="row_head">
            <th><s:text name="label.common.claimNumber"/></th>
            <th><s:text name="label.common.dateOfClaim"/></th>
            
            <th><s:text name="label.common.status"/></th>
            <th><s:text name="label.warrantyAdmin.failureDate"/></th>
            <th><s:text name="label.newClaim.oemPartReplaced"/></th>
            <th><s:text name="label.common.hoursOnTruck"/></th>
            <th><s:text name="label.common.faultCode"/></th>
            <s:if test="isRetailingDealer()">
            <th><s:text name="label.warrantyAdmin.totalAmountAsked"/></th>
            </s:if>
        </tr>
    </thead>
    <tbody>
    <s:iterator value="previousClaimsForItem" status="status">
    		<s:if test="#status.even">
            <tr class="invTableDataWhiteBg">
        </s:if>
        <s:else>
            <tr class="invTableDataAltRow">
        </s:else>

            <td style="cursor:pointer;">
                <u:openTab id="claims_Details[%{id}]" cssClass="claims_folder folder"
                           tabLabel="Claim Number %{claimNumber}"
                           url="view_search_detail.action?id=%{id}"
                           catagory="myClaims">
                    <%--TODO : someone please remove this xforms thingy which is still in use!!!--%>
                    <s:property value="claimNumber"/>
                </u:openTab>
            </td>
            <td><s:property value="filedOnDate"/> </td>
            
            <td><s:property value="state.state" /> </td>
            
            <td><s:property value="failureDate" /> </td>
            <td>
	            <s:iterator	value="serviceInformation.serviceDetail.hussmanPartsReplacedInstalled" status="mainIndex">											
	              <s:iterator value="serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts" status="subIndex">											
	            	  <br>
	            	    <s:property value="serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].brandItem.itemNumber" /> -
			            <s:property value="serviceInformation.serviceDetail.hussmanPartsReplacedInstalled[#mainIndex.index].replacedParts[#subIndex.index].itemReference.referredItem.description"/>		            
	               </s:iterator> 
	            </s:iterator>        	
            </td>
            <td><s:property value="hoursInServiceForClaimsOnInventory(id)" /> </td>
            <td>
            	<s:property value="serviceInformation.getFaultCodeDescription()" /> (<s:property value="serviceInformation.faultCode" />)
            <s:if test="serviceInformation.faultCode != null">
            	<img  src="image/comments.gif" 
		            		title="<s:property value="serviceInformation.getFaultCodeDescription()" />" 
		            		alt="<s:property value="serviceInformation.getFaultCodeDescription()" />"/>
		    </s:if>        		
             </td>
             <s:if test="isRetailingDealer()">
            	<td><s:property value="payment.totalAmountPaidAfterTax"/> </td>
            </s:if>
        </tr>
    </s:iterator>
    </tbody>
</table>

