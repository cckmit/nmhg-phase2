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


<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:5px;">	
	<tr>
		<td><s:hidden name="claimAttList[0]" value="%{claimAttList[0].id}" id="claimAttList"/></td>    
		
		<s:hidden id="association_0" name="claimAttList[0].associated" value="true" ></s:hidden>
	</tr>
    <tr>
         <td  class="labelStyle" width="10%"><s:text name="label.viewClaim.smrReason"/>:</td>
          <td>
            <span id="smrReasonSpan">
            	<tda:lov name="claimAttList[0].smrreason" id="smrReason" 
            		className="SmrReason" cssStyle="width:360px; text-align: left"/>
            </span>
          </td>
    </tr>
</table>
            
     	