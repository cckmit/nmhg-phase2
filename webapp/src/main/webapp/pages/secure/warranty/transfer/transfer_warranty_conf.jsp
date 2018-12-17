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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<table cellspacing="0" cellpadding="0" class="grid borderForTable" style="width: 98%; margin: 7px 0 0 7px">
  <thead>
        <tr>
            <th class="warColHeader" width="15%" class="non_editable"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader" ><s:text name="warranty.transfer.coverage"/></th>
            <th class="warColHeader" width="15%"><s:text name="label.common.totalTransferFee"/></th>

        </tr>
    </thead>
    <tbody>
       <s:iterator value="inventoryItemMappings" status="inventoryItemMappings">
       <tr>
            <td>
                <s:property value="inventoryItem.serialNumber" />
            </td>
            <td align="center">
                <s:if test="selectedPolicies.empty">
                    <s:text name="label.equipmentTransfer.noPoliciesSelected" />
                </s:if>
                <s:else>
                    <table cellspacing="0" cellpadding="0" width="99%" class="grid" style="margin: 5px 5px 1px 2px">
                        <thead>
                            <tr>
                                <th class="warColHeader" class="non_editable"><s:text name="label.planName"/></th>
                                <th class="warColHeader" ><s:text name="label.policy.transferFee"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:iterator value="selectedPolicies"  status="policyIterator">
                            <tr>
                                <td><s:property value="policyDefinition.code"/></td>
                                <td><s:property  value="price"/><s:property value="breachEncapsulationOfCurrency(price)"/></td>
                            </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                </s:else>
            </td>
            <td>
                <s:property value="getPolicyFeeTotalForInv(inventoryItem.id)"/>
           </td>
       </tr>
       
    </s:iterator>
    <tr>
           <td colspan="3" align="center">
               <s:text name="label.extendedwarrantyplan.totalFee"/> : <s:property value="getPolicyFeeTotal()"/>
           </td>
       </tr>
    </tbody>
</table>
