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
<div >
<div class="admin_section_heading"><s:text name="label.campaign.material" /></div>
<!-- OEM Parts -->
<s:if test="!oemPartsToReplace.isEmpty()">
<div class="policy_section_div" >
	<div class="admin_section_subheading"><s:text name="label.campaign.oemParts"/></div>
	
	<table class="grid borderForTable" width="99%" cellspacing="0" cellpadding="0" theme="twms" align="center">
      <thead>
            <tr class="row_head">
                <th width="15%" nowrap="nowrap"><s:text	name="label.common.partNumber" /></th>
                <th width="8%"><s:text name="label.common.quantity" /></th>
                <th width="42%"><s:text name="label.common.description" /></th>
                <th width="15%"><s:text name="label.campaign.paymentCondition" /></th>
                <th width="10%"><s:text name="label.campaign.returnLocation" /></th>
                <th width="5%"><s:text name="label.campaign.dueDays" /></th>
                <th width="10%"><s:text name="label.campaign.shipped" /></th>
            </tr>
      </thead>
      <tbody>
          <s:iterator value="oemPartsToReplace">
              <tr>
                <td><s:property value="item.number"/></td>
                <td><s:property value="noOfUnits"/></td>
                <td><s:property value="item.description"/></td>
                <td><s:property value="paymentCondition"/></td>
                <td><s:property value="returnLocation.code"/></td>
                <td><s:property value="dueDays"/></td>
                <td align="center"><s:checkbox name="shippedByOem" disabled="true"/></td>
              </tr>
          </s:iterator>
      </tbody>
    </table>
</div>
</s:if>


<!-- Non OEM Parts -->
<s:if test="!nonOEMpartsToReplace.isEmpty()">
<div class="admin_subsection_div" style="width: 99%">
	<div class="admin_section_subheading"><s:text name="label.campaign.nonOemParts"/></div>
	
	<table class="defaultBorderedTable" width="99%" cellspacing="0" cellpadding="2" theme="twms">
      <thead>
          <tr class="admin_table_header">
              <th width="40%"><s:text name="label.common.description"/></th>
              <th width="20%"><s:text name="label.common.quantity"/></th>
              <th width="20%"><s:text name="label.campaign.priceLimit"/></th>
          </tr>
      </thead>
      <s:iterator value="nonOEMpartsToReplace">
      <tbody>
          <tr>
              <td><s:property value="description"/></td>
              <td><s:property value="noOfUnits"/></td>
              <td><s:property value="pricePerUnit"/>
                <s:if test="campaignSectionPrice.size!=0">
	                <table>
		                <s:iterator value="campaignSectionPrice" status="iter">
		                  <tr><td><s:property value="campaignSectionPrice[#iter.index].pricePerUnit"/>
						  </td></tr>
		                </s:iterator>
	                </table>
            	</s:if>
            </td>
          </tr>
      </tbody>
      </s:iterator>
    </table>
</div>
</s:if>

</div>