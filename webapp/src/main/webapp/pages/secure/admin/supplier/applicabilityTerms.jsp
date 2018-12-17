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


        <div class="section_header"><s:text name="label.contract.coverageCondition" /></div>
     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
     
        <s:iterator value="contract.applicabilityTerms">
			<tr>
				<td width="97%"><label><s:property value="predicate.name"></s:property></label></td>
			</tr>
        </s:iterator>
      </table>

