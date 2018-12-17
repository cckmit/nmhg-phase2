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
     <div class="admin_section_heading"><s:text name="label.manageRates.labourRateConfig" /></div>
  
        <div class="borderTable">&nbsp;</div>
        <div style="margin-top:-10px;">
        <table width="100%" class="grid" cellspacing="0" cellpadding="0">
           <tr><td height="19"  class="labelStyle" width="20%" >            
                      <s:text name="label.common.dealer"/>/ <s:text name="label.common.dealerGroupLabel"/>:
                </td>                          
                <td>
                    <s:if test="laborRateAudit.dealerCriterion == null">
                        <s:text name="label.common.allDealers"/>
                    </s:if><s:else>
                        <s:property value="laborRateAudit.dealerCriterion.getIdentifier()" />
                    </s:else>
                </td>
           </tr>
              
              <tr><td class="admin_data_table"> <s:text name="label.common.seriesDescription" />:</td><td><s:property value="laborRateAudit.productName"/></td></tr>
              <tr><td class="admin_data_table">  <s:text name="label.manageRates.warrantyType" />:</td><td>
              <s:if test="laborRateAudit.warrantyType == null">
                 <s:text name="dropdown.common.all"/>
              </s:if>
              <s:else>
                 <s:property value="%{getText(laborRateAudit.warrantyType)}"/>
              </s:else></td></tr>
              <tr><td class="admin_data_table">  <s:text name="label.manageRates.claimType" />:</td><td>  <s:property value="laborRateAudit.claimType"/></td></tr>
            
              
            <tr><td class="admin_data_table"><s:text name="label.warrantyAdmin.customerType"/></td>
	            <td ><s:property value="laborRateAudit.customerType"/></td>
            </tr>

        </table>
        <br>
  </div>
   
  <div class="mainTitle" >
   <s:text name="label.common.rates"/></div>
    <br>
     <table width="100%" style="border:1px solid #EFEBF7;">
       <thead>
              <tr class="admin_table_header">
                  <th width="70%" class="colHeader"><s:text name="label.manageRates.repairDate"/></th>
                  <th width="30%" class="colHeader"><s:text name="label.manageRates.labourRate"/></th>
              </tr>
 </thead>
   <tbody>
    <s:iterator value="laborRateAudit.laborRateRepairDateAudits" status="iterPa">
     <tr>
     <s:if test="#iterPa.index==0">
       <td width="70%" style="border:1px solid #EFEBF7;">
             <table  style="border:1px solid #EFEBF7;">
                 <tr>     
                     <td style="width:10%; padding: 0 0 4px 30px; margin:1px; text-align:left; font-weight:bold; border:1px solid #EFEBF7;">
                     <s:text name="label.common.from"/>:</td>
                     <td  style="width:40%; padding: 0 0 4px 30px; margin:1px;  text-align:left; border:1px solid #EFEBF7;">
                     <s:property value="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.fromDate" />   </td>          									 
                     <td   style="width:10%; padding: 0 0 4px 30px; margin:1px;  text-align:left;  font-weight:bold; border:1px solid #EFEBF7;">
                     <s:text name="label.common.to"/>:</td>
                     <td  style="width:40%; padding: 0 0 4px 30px; margin:1px;  text-align:left; border:1px solid #EFEBF7;"> 
                     <s:property value="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.tillDate" /></td>
                </tr>
             </table>
       </td>
      <td width="30%" style="padding: 0 0 4px 30px; margin:1px; text-align:left; border:1px solid #EFEBF7;">
          <s:iterator value="laborRateAudit.laborRateRepairDateAudits" status="iter">
            <s:if test="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.fromDate.toString().equals(laborRateAudit.laborRateRepairDateAudits[#iter.index].duration.fromDate.toString())">
               
               <s:if test="laborRateAudit.laborRateRepairDateAudits[#iter.index].rate.breachEncapsulationOfAmount().toString()==0.00 || laborRateAudit.laborRateRepairDateAudits[#iter.index].rate.breachEncapsulationOfAmount().toString()==0">
               </s:if>
               <s:else>
                   <s:property value="laborRateAudit.laborRateRepairDateAudits[#iter.index].rate.breachEncapsulationOfCurrency().getCurrencyCode()" />
                   <s:property value="laborRateAudit.laborRateRepairDateAudits[#iter.index].rate.breachEncapsulationOfAmount().toString()" />
               </s:else>
               <br>
            </s:if>
          </s:iterator>
     </td>
   </s:if>
   <s:elseif test="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.fromDate.toString().equals(laborRateAudit.laborRateRepairDateAudits[#iterPa.index-1].duration.fromDate.toString())"></s:elseif>
      <s:else>
           <td width="70%" style="border:1px solid #EFEBF7;">
             <table  style="border:1px solid #EFEBF7;">
                <tr>     
                   <td   style="width:10%; padding: 0 0 4px 30px; margin:1px; text-align:left; font-weight:bold; border:1px solid #EFEBF7;">
                   <s:text name="label.common.from"/>:</td>
                   <td  style="width:40%; padding: 0 0 4px 30px; margin:1px;  text-align:left; border:1px solid #EFEBF7;">
                   <s:property value="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.fromDate" />   </td>          									 
                   <td   style="width:10%; padding: 0 0 4px 30px; margin:1px;  text-align:left; font-weight:bold; border:1px solid #EFEBF7;">
                   <s:text name="label.common.to"/>:</td>
                   <td  style="width:40%; padding: 0 0 4px 30px; margin:1px;  text-align:left; border:1px solid #EFEBF7;"> 
                   <s:property value="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.tillDate" /></td>
               </tr>
             </table>
         </td>
         <td width="30%" style="padding: 0 0 4px 30px; margin:1px; text-align:left;border:1px solid #EFEBF7;">
          <s:iterator value="laborRateAudit.laborRateRepairDateAudits" status="iter1">
            <s:if test="laborRateAudit.laborRateRepairDateAudits[#iterPa.index].duration.fromDate.toString().equals(laborRateAudit.laborRateRepairDateAudits[#iter1.index].duration.fromDate.toString())">
                   <s:if test="laborRateAudit.laborRateRepairDateAudits[#iter1.index].rate.breachEncapsulationOfAmount().toString()==0.00 || laborRateAudit.laborRateRepairDateAudits[#iter1.index].rate.breachEncapsulationOfAmount().toString()==0">
                   </s:if>
                   <s:else>
                  <s:property value="laborRateAudit.laborRateRepairDateAudits[#iter1.index].rate.breachEncapsulationOfCurrency().getCurrencyCode()" />
                  <s:property value="laborRateAudit.laborRateRepairDateAudits[#iter1.index].rate.breachEncapsulationOfAmount().toString()" />
                  <br>
                  </s:else>
           </s:if>
          </s:iterator>
      </s:else>

    </s:iterator>
   </td>
  </tr>
  </tbody>
  </table> 
  </s:form>
 </u:body>
</html>
