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
  @author mritunjay.kumar
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<table class="grid" cellspacing="0" cellpadding="0" id="servicing_location" width="100%">
    <tr>
        <td  width="20%">
            <label for="servicingLocation" class="labelStyle"><s:text name="label.viewClaim.servicingLocation"/>:</label>
        </td>
        <td> 
            <s:if test="claim.servicingLocation.location != null">
            <s:if test="buConfigAMER">
             <s:property value="claim.servicingLocation.getLocationWithBrand()"/>
            </s:if>
            <s:else>
        	  <s:property value="claim.servicingLocation.getShipToCodeAppended()"/>
        	  </s:else>
            </s:if>
        	<s:else>        	  
        	    <s:set name="primaryOrgAddressForPrev" value="getPrimaryOrganizationAddressForOrg(claim.forDealer)" /> 
        	   <s:property value="#primaryOrgAddressForPrev.getShipToCodeAppended()"/>
        	</s:else>
                                   
        </td>
    </tr>  
</table>       