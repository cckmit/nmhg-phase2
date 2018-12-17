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

<%@taglib prefix="s" uri="/struts-tags"%>
<div>
<table width="95%" class="form" cellpadding="0" cellspacing="0" style="margin:5px;">
<tbody>
<tr>
    <td class="non_editable labelStyle"><s:text name="label.numberOfYears"/></td>
    <td><s:property value="warranty.marketingInformation.years"/></td>
    <td class="non_editable labelStyle"><s:text name="label.numberOfMonths"/></td>
    <td><s:property value="warranty.marketingInformation.months"/></td>
</tr>

</tbody>
</table>
</div>