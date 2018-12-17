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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <s:head theme="twms"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<u:body>
<form name="baseForm" id="baseForm" style="width:100%">
  <div class="admin_section_div" style="margin:5px;padding-bottom:10px;">
    <div class="admin_section_heading">
      <s:text name="label.manageRates.partPriceConfig" />
    </div>
     <div class="borderTable">&nbsp;</div>
      <div style="margin-top:-10px;">
      <table width="99%" class="grid" cellspacing="0" cellpadding="0">
        <tr>
          <td class="previewPaneBoldText" width="18%"><s:text name="label.common.itemLabel" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="definition.nmhg_part_number.number" /></td>
		             	  
        </tr>
		<tr>
		     <td class="previewPaneBoldText">
                        <s:text name="label.miscellaneous.partDescription"/> :
                </td>
			 <td class="previewPaneNormalText">
                 <s:property value="definition.nmhg_part_number.description" />
             </td>
        </tr>			 
        <tr>
          <td class="previewPaneBoldText"  ><s:text name="label.warrantyAdmin.comments" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="definition.comments" /></td>
        </tr>
      </table>
   </div>
  </div>
</form>
</u:body>
</html>