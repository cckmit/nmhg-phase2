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

     <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="causalPartNumber"/>  </td>
          <td width="36%" class="label" ><s:property value="claim.serviceInformation.causalPart.number"/></td>
          <td colspan="2" nowrap="nowrap" class="label"><s:property value="claim.serviceInformation.causalPart.description"/></td>
        </tr>
        <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="faultCode"/></td>
          <td width="36%" class="label" colspan="3" ><s:property 
          	value="claim.serviceInformation.faultCode"/></td>
        </tr>
		 <tr>
          <td width="14%" nowrap="nowrap" class="labelNormal"><s:text name="faultFound"/></td>
          <td width="36%" class="label"><s:property value="claim.serviceInformation.faultFound.name"/></td>
          <td width="14%" class="labelNormal"><s:text name="causedBy"/></td>
          <td width="36%" class="label" ><s:property value="claim.serviceInformation.causedBy.name"/></td>
        </tr>     
        </table>
