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
<%--
  @author Sushma.manthale
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<table  width="100%" border="0"  cellpadding="0" cellspacing="0" class="grid">
 <tbody>
<tr>
 <td width="26%">
                <label for="isSupplierRecovery" class="labelStyle">
                    <s:text name="label.viewClaim.IsSupplierRecovery"/>:
                </label>
            </td>
             <s:if test="claim.supplierRecovery">
                <td><s:text name="label.common.yes"/></td>
            </s:if>
            <s:else>
            <td><s:text name="label.common.no"/></td>
            </s:else>
            </tr>
           
            <tr>
            
             <td width="15%">
                <label id="supplier"  class="labelStyle">
                    <s:text name="label.viewClaim.Supplier"/>:
                </label>
            </td>
           
			 <td style="padding-bottom:15px;">
               <s:property value="claim.suppliers.description"/>
           </td>
             </tr>

            </tbody>
            </table>

