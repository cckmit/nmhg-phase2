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
<div style="background-color:#F3FBFE; border:1px solid #EFEBF7;">
    <table class="grid" cellpadding="0" cellspacing="0" style="margin:5px 5px;width:98%">
        <tbody>
            <tr>
                <td width="38%">
                    <table>
                        <tbody>
                              <tr>
                               <%--  <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.firstTimeOwnerOfProductBeingRegistered"/></td>
                                <td>
                                    <s:if test="marketingInformation.firstTimeOwnerOfProductBeingRegistered">
                                        <s:text name="label.common.yes"/>
                                    </s:if>
                                    <s:else>
                                        <s:text name="label.no"/>
                                    </s:else>
                                </td> --%>
                            </tr>
                            <%-- <tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.numberOfYears"/></td>
                                <td><s:property value="marketingInformation.years"/></td>
                            </tr> --%>
                            	<tr>
                            	<s:if test="buConfigAMER">
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.ulClassification"/>:</td>
                                <td><s:property value="%{marketingInformation.ulClassification}"/></td>
                                </s:if>
                            </tr>
							<tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.salesMan"/></td>
                                <td><s:property value="%{marketingInformation.dealerRepresentative}"/></td>
                            </tr>
                            
                            
                           <tr>
                           
                           
                           <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="CustomerRepresentative: "/></td>
                                <td><s:property value="%{marketingInformation.customerRepresentative}"/></td>
                            </tr>
                            <tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.industryCode"/></td>
                                <td><s:property value="marketingInformation.industryCode.getDisplayIndustryCode()"/></td>
                            </tr>

                        </tbody>
                    </table>
                </td>
                <td width="40%">
                    <table>
                        <tbody>
                        	<s:if test="getLoggedInUser().isInternalUser() && displayInternalInstallType()">
                        	<tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.internalInstallType"/></td>
                                <td><s:property value="marketingInformation.internalInstallType.internalInstallType"/></td>
                            </tr>
                            </s:if>
                            <tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.contractCode"/></td>
                                <td><s:property value="marketingInformation.contractCode.contractCode"/></td>
                            </tr>
                            <tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.maintenanceContract"/></td>
                                <td><s:property value="marketingInformation.maintenanceContract.maintenanceContract"/></td>
                            </tr>
                            <%-- <tr>
                                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                                        name="label.numberOfMonths"/></td>
                                <td><s:property value="marketingInformation.months"/></td>
                            </tr> --%>
                        </tbody>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>
</div>
