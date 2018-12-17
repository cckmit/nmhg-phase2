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
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
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
      <s:text name="label.managePolicy.policyModifier" />
    </div>
    <div class="admin_subsection_div">
      <div class="admin_section_subheading">
        <s:text name="label.managePolicy.criteria" />
      </div>
      <table width="99%" class="admin_selections">
        <tr>
          <td class="admin_data_table" width="18%"><s:text name="label.managePolicy.dealerCriterion" />
            :</td>
          <td><s:property value="dealerString" /></td>
        </tr>
        <tr>
          <td class="admin_data_table"  width="18%"><s:text name="label.managePolicy.productType" />
            :</td>
          <td><s:property value="productTypeString" /></td>
        </tr>
        <tr>
          <td class="admin_data_table"  width="18%"><s:text name="label.managePolicy.warrantyType" />
            :</td>
          <td><s:property value="%{getText(warrantyTypeString)}" /></td>
        </tr>
        <tr>
          <td class="admin_data_table"  width="18%"><s:text name="columnTitle.managePolicy.registrationType" />
            :</td>
          <td><s:property value="definition.forCriteria.warrantyRegistrationType.type" /></td>
        </tr>
        <tr>
          <td class="admin_data_table"  width="18%"><s:text name="label.managePolicy.customerState" />
            :</td>
          <td><s:property value="customerStateString" /></td>
        </tr>
        <s:if test="definition.policyDefinitions.isEmpty()">
        <tr>
          <td class="admin_data_table"  width="18%"><s:text name="label.managePolicy.policyDefinitions" />
            :</td>
          <td><s:text name="label.managePolicy.allPolicyDefinitions" /></td>
        </tr>
        </s:if>
      </table>
    </div>
    <s:if test="!definition.policyDefinitions.isEmpty()">
    <div class="admin_subsection_div">
      <div class="admin_section_subheading">
        <s:text name="label.managePolicy.policyDefinitions"/>
      </div>
      <table width="100%" cellpadding="2" cellspacing="0" class="admin_entry_table">
      <tr class="admin_table_header">
        <th><s:text name="label.managePolicy.policyDefinitionCode"/></th>
        <th><s:text name="label.managePolicy.policyDefinitionDescription"/></th>
      </tr>
      <s:iterator value="definition.policyDefinitions">
        <tr>
          <td width="50%"><table width="100%">
              <tr>
                <td class="admin_selections"><s:property value="code"/></td>
              </tr>
            </table></td>
          <td width="50%" class="admin_selections" style="border-left:1px solid #DCD5CC"><s:property value="description" />
          </td>
        </tr>
      </s:iterator>
      </table>
    </div>
    </s:if>
    <s:if test="!definition.policyLabels.isEmpty()">
    <div class="admin_subsection_div">
      <div class="admin_section_subheading">
        <s:text name="label.managePolicy.policyLabels"/>
      </div>
      <table width="100%" cellpadding="2" cellspacing="0" width="100%" class="admin_entry_table">
      <tr class="admin_table_header">
        <th><s:text name="label.managePolicy.policyLabelName"/></th>
      </tr>
      <s:iterator value="definition.policyLabels">
        <tr>
          <td><table width="100%">
              <tr>
                <td class="admin_selections"><s:property value="name"/></td>
              </tr>
            </table></td>
        </tr>
      </s:iterator>
      </table>
    </div>
    </s:if>
    <div class="admin_subsection_div">
      <div class="admin_section_subheading">
        <s:text name="label.common.rates"/>
      </div>
      <table width="100%" cellpadding="2" cellspacing="0" width="100%" class="admin_entry_table">
      <tr class="admin_table_header">
        <th><s:text name="label.managePolicy.purchaseDate"/></th>
        <th><s:text name="label.managePolicy.rate"/></th>
      </tr>
      <s:iterator value="rates">
        <tr>
          <td width="50%"><table width="100%">
              <tr>
                <td class="admin_selections" width="8%"><s:text name="label.common.from"/>:</td>
                <td class="admin_selections" width="40%"><s:property value="duration.fromDate" /></td>
                <td class="admin_selections" width="4%"><s:text name="label.common.to"/>:</td>
                <td class="admin_selections" width="48%"><s:property value="duration.tillDate" /></td>
              </tr>
            </table></td>
          <td width="50%" class="admin_selections" style="border-left:1px solid #DCD5CC"><s:property value="value" /> %
          </td>
        </tr>
      </s:iterator>
      </table>
    </div>
  </div>
</form>
</u:body>
</html>
