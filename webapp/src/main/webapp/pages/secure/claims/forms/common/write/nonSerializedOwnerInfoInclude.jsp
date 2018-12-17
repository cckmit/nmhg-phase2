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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
  
			
	<td id = "viewLink" align="left" >						
		<a id="fill_ownerinfo_page" class="link" style="display:none">
			<s:text name="label.claim.enterOwnerInfo"/>
		</a>
		<a id="show_ownerinfo_page" class="link" style="display:none">
			<s:text name="label.claim.viewOwnerInfo"/>
		</a>																			
        <jsp:include flush="true" page="nonSerializedOwnerInfo.jsp" />  
	</td>

<s:hidden id="claimOwnerInformation" name="task.claim.ownerInformation" value="%{claim.ownerInformation.id}"></s:hidden>			
