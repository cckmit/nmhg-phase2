<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: May 16, 2008
  Time: 10:58:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
  <head>
      <title>Simple jsp page</title>
      <s:head theme="twms"/>
      <u:stylePicker fileName="form.css"/>
      <u:stylePicker fileName="warrantyForm.css"/>
      <u:stylePicker fileName="common.css"/>
      <u:stylePicker fileName="base.css"/>
      <style type="text/css">
        label {
            color: #000000;
            font-weight: 400;
        }
      </style>
  </head>
  <body>
  <script type="text/javascript">
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("twms.widget.TitlePane");
      dojo.require("twms.widget.Dialog");
  </script>
  <div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y:auto; ">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
        <u:actionResults></u:actionResults>
        <div id="customer_info" class="section_div">
            <div id="customer_info_title" class="section_heading">
                <s:text name="label.manageCustomer.newCustomerInfo" /></div>
            <div id="comp-addr" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
                <table width="96%" cellpadding="0" cellspacing="0" align="center" style="margin:2px;">
                    <tr>
                        <td width="20%" nowrap="nowrap" ><label class="labelStyle"><s:text name="label.companyName" /></label></td>
                        <td width="40%"><s:property value="customer.companyName" /></td>
                        <td width="20%" nowrap="nowrap"><label class="labelStyle"><s:text name="label.corporateName" /></label></td>
                        <td><s:property value="customer.corporateName" /></td>
                    </tr>
                    <tr>
						<td width="16%" class="labelStyle"><s:text name="customer.search.customerNumber" /></td>
						<td colspan="3"><s:property value="customer.customerId" /></td>
					</tr>
                </table>
                <s:iterator value="addressBookAddressMappings" status="addressMappings">
                <s:set name="counter" value="%{#addressMappings.index}"></s:set>
                    <div id="customer_info">
                    <br/>
                    <div class="mainTitle">&nbsp;<s:text name="columnTitle.common.address"/></div>
                    <div class="borderTable">&nbsp;</div>
                    <div style="margin:5px;margin-top:-10px;">
                    <table width="96%" cellpadding="0" cellspacing="0" id="indiv-addr" align="center" style="margin:0px">
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.addressBookType" /></label></td>
                            <td><s:property value="addressBookAddressMappings[#counter].addressBook.type.type"/> </td>
                        </tr>
                        <tr>
                           <td width="20%" nowrap="nowrap"><label class="labelStyle"><s:text name="label.addressType" /></label></td>
		                   <td width="40%"><s:property value="addressBookAddressMappings[#counter].type.type"/></td>
			               <td width="20%" nowrap="nowrap">
                               <s:if test="addressBookAddressMappings[#counter].primary">
                               <s:text name="label.markedAsPrimary"/>
                               </s:if>
                           </td>
                        </tr>
                        <tr>
	                        <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.contactPersonName" /></label></td>
		                    <td ><s:property value="customer.addresses[#counter].contactPersonName" /></td>
	                    </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.common.address.line1" /></label></td>
                            <td ><s:property value="customer.addresses[#counter].addressLine1"/></td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.common.address.line2" /></label></td>
                            <td ><s:property value="customer.addresses[#counter].addressLine2"/></td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.common.address.line3" /></label></td>
                            <td ><s:property value="customer.addresses[#counter].addressLine3"/></td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.country" /></label></td>
                            <td><s:property value="customer.addresses[#counter].country"/> </td>
                            <td width="20%"><label class="labelStyle"><s:text name="label.state" /></label></td>
                            <td><s:property value="customer.addresses[#counter].state"/></td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.city" /></label></td>
                            <td><s:property value="customer.addresses[#counter].city"/> </td>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.zip" /></label></td>
                            <td><s:property value="customer.addresses[#counter].zipCode"/> </td>
                        </tr>
                        <tr>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.phone" /></label></td>
		                    <td><s:property value="customer.addresses[#counter].phone" /></td>
                            <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.email" /></label></td>
                            <td><s:property value="customer.addresses[#counter].email" /></td>
                        </tr>
                        <tr>
		                    <td nowrap="nowrap"><label class="labelStyle"><s:text name="label.fax" /></label></td>
		                    <td><s:property value="customer.addresses[#coounter].secondaryPhone" /></td>
	                    </tr>
                    </table>
                </div>
                </div>
                </div>
                </s:iterator>
            </div>
        </div>
    </div>
  </body>
</html>