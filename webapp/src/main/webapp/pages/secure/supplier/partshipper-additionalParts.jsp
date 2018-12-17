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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<body style="overflow-X: auto; overflow-Y: auto; width:100%;">

<div dojoType="dijit.layout.LayoutContainer">
	
	<div id="separatorTop"></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><jsp:include flush="true" page="partsToBeAddedGroupedByClaim.jsp" />
			</td>
		</tr>
		<tr>
			<td>
			
				<div class="buttonWrapperPrimary">
				<s:if test="%{taskInstances.size() != 0}"><input type="button"
					class="buttonGeneric" value="<s:text name='button.partReturnConfiguration.addSelectedParts'/>" 
					onclick="addPartsForPartShipper()"/></s:if>
				</div>
			</td>
		</tr>
	</table>
</div>
</body>
</html>
