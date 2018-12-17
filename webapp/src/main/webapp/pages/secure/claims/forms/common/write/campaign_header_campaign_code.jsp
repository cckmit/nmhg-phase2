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
<s:if test="campaigns.size==1">
<s:property value="%{campaigns[0].code}"/>
<s:hidden name="campaign" value="%{campaigns[0].id}"/>
</s:if>
<s:else>
<s:select name="campaign" list="campaigns" theme="twms"
listKey="id" listValue="code"  disabled="false"/>
</s:else>
