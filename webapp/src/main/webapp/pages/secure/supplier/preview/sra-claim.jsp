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

<div class="bgColor" style="width: 98%">
     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="label.common.claimHeader"/></td>
        </tr>
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="label.supplier.claimType"/> </td>
          <td width="36%" class="label"> <s:property value="claim.type.type"/></td>
          <td width="14%" class="labelNormal"><s:text name="label.common.claimNumber"/>:</td>
          <td width="36%" class="label"><s:property value="claim.id"/></td>
        </tr>
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="label.supplier.claimStatus"/> </td>
          <td width="36%" class="label"><s:property value="claim.state.state"/> </td>
          <td width="14%" class="labelNormal"><s:text name="label.supplier.dateOfClaim"/> </td>
          <td width="36%" class="label"><s:property value="claim.filedOnDate"/></td>
        </tr>
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal">Date of Failure: </td>
          <td width="36%" class="label"><s:property value="claim.failureDate"/></td>
          <td width="14%" class="labelNormal"><s:text name="label.supplier.dateOfRepair"/></td>
          <td width="36%" class="label"><s:property value="claim.repairDate"/></td>
        </tr>
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="label.supplier.requestForSMR" /> </td>
          <td width="36%" class="label"><s:property value="claim.serviceManagerRequest"/></td>
          <td width="14%" class="labelNormal"><s:text name="label.common.reasonSMR"/></td>
          <td width="36%" class="label">TODO</td>
        </tr>
      </table>
</div>