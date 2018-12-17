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
 @author: Jhulfikar Ali
--%>


<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="title.partInventory.partAvailInfo"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>

<u:body>

<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white; overflow-y: auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults />
<div class="admin_section_heading">
	<s:text name="label.processorAvail.defaultUserRole"/>
</div>
<s:form name="updateProcessorAvailForm" action="update_processor_avail.action">
<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%">
<thead>
    <tr class="row_head">
        <th colspan="9"><s:text name="label.processorAvail.user"/></th>
        <th colspan="9"><s:text name="label.processorAvail.default"/></th>
    </tr>
</thead>
<tbody>
    <s:iterator value="allProcessors" status="status">
    <tr>
        <td>
        	<s:property value="name" />
        </td>
        <td>
            <input type="radio" name="processor.defaultUser" value="Yes" <s:if test="isDefaultUser">checked="checked"</s:if>/> 
            <input type="radio" name="processor.defaultUser" value="No" <s:if test="!isDefaultUser">checked="checked"</s:if>/>
        </td>
    </tr>
    </s:iterator>
    <tr width="100%">
    	<td colspan="2"><s:submit align="middle" cssClass="button" value="%{getText('button.common.save')}" action="update_processor_default_avail"></s:submit></td> 
   </tr>
</table>
</s:form>
</div>
</div>
</u:body>