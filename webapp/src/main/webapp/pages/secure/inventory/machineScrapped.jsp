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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<u:stylePicker fileName="yui/reset.css" common="true" />
<s:head theme="twms" />
<u:stylePicker fileName="common.css" />
</head>
<body>
<u:actionResults/>
<table align="center" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <input type="submit" class="buttonGeneric" id="closeTab" value="<s:text name="button.common.close"/>" 
            onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
        </td>
    </tr>
</table>
</body>
</html>