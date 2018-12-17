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
      <s:text name="label.manageRates.labourRateConfig" />
    </div>
    
      <div class="mainTitle">
        <s:text name="label.manageRates.conditions" />
      </div>
      <div class="borderTable">&nbsp;</div>
      <div style="margin-top:-10px;">
      <table width="99%" class="grid" cellspacing="0" cellpadding="0">
        <tr>
          <td class="previewPaneBoldText" width="18%"><s:text name="label.manageRates.dealer" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="dealerString" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText"  width="18%"><s:text name="label.common.allProductTypes" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="productTypeString" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText"  width="18%"><s:text name="label.manageRates.warrantyType" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="%{getText(warrantyTypeString)}" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText"  width="18%"><s:text name="label.manageRates.claimType" />
            :</td>
          <td class="previewPaneNormalText"><s:property value="claimTypeString" /></td>
        </tr>
      </table>
   </div>
   <div style="margin:5px">
      <div class="mainTitle">
        <s:text name="label.common.rates"/>
      </div>
      <table cellpadding="0" cellspacing="0"  class="grid borderForTable">
      <tr class="admin_table_header">
        <th class="colHeader"><s:text name="label.manageRates.repairDate"/></th>
        <th class="colHeader"><s:text name="label.manageRates.labourRate"/></th>
      </tr>
       <s:iterator value="rates" status="iter">
        <tr>
          <td width="50%"><table width="100%">
              <tr>
                <td style="color:#656565; font-weight:bold;" width="8%" ><s:text name="label.common.from"/>:</td>
                <td width="40%"><s:property value="duration.fromDate" /></td>
                <td style="color:#656565; font-weight:bold;" width="4%" ><s:text name="label.common.to"/>:</td>
                <td width="48%"><s:property value="duration.tillDate" /></td>
              </tr>
            </table></td>
                   
                   <td width="50%" class="admin_selections" style="border-left:1px solid #DCD5CC">
                   	<s:iterator value="laborRateValues" >
                   		<s:property value="rate.breachEncapsulationOfCurrency().currencyCode" />
						<s:property value="rate.breachEncapsulationOfAmount()" /><BR>
                   	</s:iterator>
                    </td>
                  
                    	
        </tr>
      </s:iterator>
      </table>
   </div>
  </div>
</form>
</u:body>
</html>