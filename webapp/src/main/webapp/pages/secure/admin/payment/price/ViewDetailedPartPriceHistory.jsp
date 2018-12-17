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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <style>
    .admin_selections td{
    padding-bottom:10px;
    }
    </style>
</head>
<u:body smudgeAlert="false">
<s:form name="baseForm" id="baseFormId">
<u:actionResults/>
<s:hidden name="id" />
<s:hidden name="dealerGroupSelected" id="isDealerGroup"></s:hidden>
  <div class="admin_section_div" style="margin:5px;padding-bottom:10px;width:98.8%">
     <div class="admin_section_heading"><s:text name="label.manageRates.partPriceConfig" /></div>
        <div class="borderTable">&nbsp;</div>
        <div style="margin-top:-10px;">
        <table >
             <tr><td class="admin_data_table nowrap="nowrap" style="padding-bottom:1px;">   <s:text name="label.common.brandPart"/>:</td><td>  <s:property value="nmhgPartNumber"/></td></tr>
             <tr><td class="admin_data_table nowrap="nowrap" style="padding-bottom:1px;">   <s:text name="label.miscellaneous.partDescription"/>:</td><td>  <s:property value="nmhgPartNumber"/></td></tr>
             <tr><td class="admin_data_table nowrap="nowrap" style="padding-bottom:1px;"> <s:text name="label.warrantyAdmin.comments"/>:</td><td><s:property value="partPriceAudit.comments"/></td></tr>
        </table>
        <br>
  </div>
   
  <div class="mainTitle" >
   <s:text name="label.common.rates"/></div>
    <br>
     <table class="grid borderForTable" cellpadding="0" cellspacing="0" width="100%" style="border:1px solid #EFEBF7;">
       <thead>
              <tr class="admin_table_header">
                   <th width="34%" class="colHeader"><s:text name="label.manageRates.standardCostPrice"/></th>
                     <th width="33%" class="colHeader"><s:text name="label.manageRates.dealerNetPrice"/></th>
                   <th width="33%" class="colHeader"><s:text name="label.manageRates.plantCostPrice"/></th>
              </tr>
 
  
   
     
          <s:iterator value="partPriceAudit.partPriceRepairDateAudits" status="iter">
            <tr>
             <s:if test="partPriceAudit.partPriceRepairDateAudits[#iter.index].standardCostPrice.breachEncapsulationOfAmount().toString()>0.00 || partPriceAudit.partPriceRepairDateAudits[#iter.index].standardCostPrice.breachEncapsulationOfAmount().toString()>0">
                <td style="border:1px solid #EFEBF7;">  
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].standardCostPrice.breachEncapsulationOfCurrency().getCurrencyCode()" />
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].standardCostPrice.breachEncapsulationOfAmount().toString()" />
                 </td>
               </s:if>
               <s:if test="partPriceAudit.partPriceRepairDateAudits[#iter.index].dealerNetPrice.breachEncapsulationOfAmount().toString()>0.00 || partPriceAudit.partPriceRepairDateAudits[#iter.index].dealerNetPrice.breachEncapsulationOfAmount().toString()>0">
                <td style="border:1px solid #EFEBF7;">
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].dealerNetPrice.breachEncapsulationOfCurrency().getCurrencyCode()" />
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].dealerNetPrice.breachEncapsulationOfAmount().toString()" />
                </td>
               </s:if>
                <s:if test="partPriceAudit.partPriceRepairDateAudits[#iter.index].plantCostPrice.breachEncapsulationOfAmount().toString()>0.00 || partPriceAudit.partPriceRepairDateAudits[#iter.index].plantCostPrice.breachEncapsulationOfAmount().toString()>0">
              <td style="border:1px solid #EFEBF7;">  
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].plantCostPrice.breachEncapsulationOfCurrency().getCurrencyCode()" />
                   <s:property value="partPriceAudit.partPriceRepairDateAudits[#iter.index].plantCostPrice.breachEncapsulationOfAmount().toString()" />
              </td>
               </s:if>
             </tr>
          </s:iterator>
    
  </thead>
  </table>
  </div>
  </s:form>
 </u:body>
</html>
