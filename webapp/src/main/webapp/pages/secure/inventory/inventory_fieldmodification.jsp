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
<table class="form" width="100%" cellspacing="0" cellpadding="0">
    <tr>

        <!-- The javascripts provided by the UI team (including the checkbox onclick javascripts)
              are not yet added to the code base.
            TODOD - This need to be added  later -  onClick="javascript:checkAllOrUncheckAll()" -->
        <th class="tabesets" align="center"><input type="checkbox"  name="toggle"></th>
        <th class="tabesets"> Campaign Code</th>
        <th class="tabesets"> Class</th>
        <th class="tabesets"> End Date</th>
        <th class="tabesets"> Description</th>
        <th class="tabesets"> Status</th>
        <th class="tabesets"> Status Code</th>
    </tr>
</table>