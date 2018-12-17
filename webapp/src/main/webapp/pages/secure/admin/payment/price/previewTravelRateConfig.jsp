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
<form name="baseForm" id="baseForm" style="width:99%">
  <div class="admin_section_div">
    <div class="admin_section_heading">
      <s:text name="label.manageRates.travelRateConfig" />
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
          <td class="previewPaneNormalText" width="82%"><s:property value="dealerString" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText" width="18%"><s:text name="label.common.seriesDescription" />
            :</td>
          <td class="previewPaneNormalText" width="82%"><s:property value="productTypeString" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText"  width="18%"><s:text name="label.manageRates.warrantyType" />
            :</td>
          <td class="previewPaneNormalText" width="82%"><s:property value="%{getText(warrantyTypeString)}" /></td>
        </tr>
        <tr>
          <td class="previewPaneBoldText" width="18%"><s:text name="label.manageRates.claimType" />
            :</td>
          <td class="previewPaneNormalText" width="82%"><s:property value="claimTypeString" /></td>
        </tr>
      </table>

   </div>
      <div class="mainTitle" >
        <s:text name="label.common.rates"/>
      </div>
      <table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable">
      <tr class="row_head">
        <th class="colHeader">
            <s:text name="label.manageRates.repairDate"/>
          </th>
        <th colspan="3" class="colHeader">
            <s:text name="label.manageRates.travelRate"/>
          </th>
      </tr>
      <tr class="row_head">
        <th width="20%" class="colHeader"><s:text name="label.manageRates.perHour"/></th>
        <th width="20%" class="colHeader"><s:text name="label.manageRates.byDistance"/></th>
        <th width="20%" class="colHeader"><s:text name="label.manageRates.byTrip"/></th>
      </tr>
      <s:iterator value="rates">
        <tr>
          <td width="50%">
		  <table width="100%">
              <tr>
                <td class="admin_selections" width="8%" nowrap="nowrap"  style="color:grey; font-weight:bold"><s:text name="label.common.from"/>:</td>
                <td class="admin_selections" width="40%"><s:property value="duration.fromDate" /></td>
                <td class="admin_selections" width="4%"  style="color:grey; font-weight:bold"><s:text name="label.common.to"/>:</td>
                <td class="admin_selections" width="52%"><s:property value="duration.tillDate" /></td>
              </tr>
            </table></td>
            
          <td class="admin_selections" style="border:1px solid #EFEBF7;">
          	<s:iterator value="travelRateValues" >
          		<s:property value="hourlyRate.breachEncapsulationOfCurrency().currencyCode" />
          		<s:property value="hourlyRate.breachEncapsulationOfAmount()" /><BR>
          	</s:iterator>
             <s:if test="valueIsHourFlatRate" >
             	(<s:text name ="label.admin.travelFlatRate" />)
             </s:if><BR><BR>
          </td>
          <td class="admin_selections" style="border:1px solid #EFEBF7;">
          	<s:iterator value="travelRateValues" >
          		<s:property value="distanceRate.breachEncapsulationOfCurrency().currencyCode" />
				<s:property value="distanceRate.breachEncapsulationOfAmount()" /><BR>
          		
          	</s:iterator>
          	<s:if test="valueIsDistanceFlatRate" >
             	(<s:text name ="label.admin.travelFlatRate" />)
             </s:if><BR><BR>
          </td>
          <td class="admin_selections" style="border:1px solid #EFEBF7;">
          	<s:iterator value="travelRateValues" status="iter">
          		<s:property value="tripRate.breachEncapsulationOfCurrency().currencyCode" />	
          		<s:property value="tripRate.breachEncapsulationOfAmount()" /><BR>
          	</s:iterator>
            <s:if test="valueIsTripFlatRate" >
             	(<s:text name ="label.admin.travelFlatRate" />)
            </s:if><BR><BR>
          </td>
        </tr>
      </s:iterator>
      </table>
    </div>
 
</form>
</u:body>
</html>
