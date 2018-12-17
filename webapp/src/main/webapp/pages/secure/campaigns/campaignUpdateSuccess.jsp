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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"></s:head>
    <u:stylePicker fileName="adminPayment.css"/>
    <script type="text/javascript" src="scripts/admin.js"></script>
</head>
<u:body>
<script type="text/javascript">
          dojo.addOnLoad(function() {
              var summaryTableId = getFrameAttribute("TST_ID");
              if (summaryTableId) {
                  manageTableRefresh(summaryTableId, true);
              }
          });
      </script>
<table width="100%"><tr>
	<td class="admin_section_heading" align="center">
		<s:text name="message.campaign.updateSuccess"/>
	</td></tr>
</table>
        <div id="submit" align="center">
            <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.close'/>"
                   onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
        </div>
</u:body>
</html>
