<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><s:text name="pageTitle.customerRegsitration"></s:text></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>

    <script type="text/javascript">
        dojo.addOnLoad(function() {
            companyAddr = dojo.byId('addressTypesCompany');
            indvAddr = dojo.byId('addressTypesIndividual');
            addr = dojo.byId('addressType');
        <s:if test='%{(customer.id != null && !customer.individual) || "Company".equals(customerType) ? true:false}'>
            dojo.dom.removeNode(indvAddr);
        </s:if>
        <s:else>
            dojo.dom.removeNode(companyAddr);
        </s:else>

        <%-- THIS IS A HACK TO HIGHLIGHT THE TEXTAREA ON VALIDATION FAILURE--%>
        <s:if test="hasErrors()">
            validate(dojo.byId('customerAddress'));
        </s:if>

        });

        function changeAddressType(box) {
            var status = box.checked;
            dojo.byId('individualField').value = !status;
            if (status == true) {
                dojo.dom.removeNode(indvAddr);
                dojo.dom.insertAtIndex(companyAddr, addr, 0);
            }
            else {
                dojo.dom.removeNode(companyAddr);
                dojo.dom.insertAtIndex(indvAddr, addr, 0);
            }
            validate(box);
        }
    </script>
</head>

<u:body>
	<u:actionResults />
	<s:form action="register_customer" theme="twms" validate="true">
	<div dojoType="dijit.layout.LayoutContainer" id="rootLayoutContainer">
	<div dojoType="dijit.layout.ContentPane">
		
		<div id="customer_info" style="border:1px solid #CCCCCC; margin:5px">
		<div id="customer_info_title" class="section_heading"><b><s:text
			name="label.customerDetails" /></b></div>

		<div id="addrDiv">
		<table width="96%" cellpadding="0" cellspacing="0" class="form"
			align="center">
			<tr>
				<td class="non_editable"><s:text name="label.name" /></td>
				<td><s:textfield name="customer.name" /></td>
				<td class="non_editable"><s:text name="label.companyName" /></td>
				<td><s:textfield name="customer.companyName" /></td>
			</tr>
			<tr>
				<td class="non_editable"><s:text
					name="label.doYouRepresentCompany" /></td>
				<td align="center"><s:hidden name="customer.individual" id="individualField"
					value="%{customer == null || customer.id == null ? true : customer.individual}" />
				<s:checkbox onclick="changeAddressType(this);" name="dummyCheckBox" cssStyle="border: 0px"
					value='%{(customer != null && customer.id != null && !customer.individual) ? true:false}' />
				</td>
				<td class="non_editable"></td>
				<td></td>
			</tr>
			<tr>
				<td class="non_editable"><s:text name="label.password" /></td>
				<td><s:password name="customer.password" /></td>
				<td class="non_editable"><s:text name="label.confirmPassword" /></td>
				<td><s:password name="password" /></td>
			</tr>
		</table>
		<table width="96%" cellpadding="0" cellspacing="0" id="indiv-addr"
			class="form" align="center" class="section_div">
			<tr>
				<td class="non_editable"><s:text name="label.addressType" /></td>
				<td>
				<div id='addressType'><span id='addressTypesIndividual'><s:select
					name="customer.addresses[0].type" list="addressTypesIndividual"
					required="true" theme="twms" /></span><span id='addressTypesCompany'><s:select
					name="customer.addresses[0].type" list="addressTypesCompany"
					required="true" theme="twms" /></span></div>
				</td>

				<td class="non_editable"></td>
				<td><input type="hidden" name="primaryAddressIndex" value='0' /></td>
			</tr>

			<tr>
				<td class="non_editable"><s:text name="label.address" /></td>
				<td colspan="3"><t:textarea label="Address"
					name="customer.addresses[0].addressLine1" id="customerAddress"
					rows="2" cssStyle="width: 100%;" /></td>
			</tr>

			<tr>
				<td class="non_editable"><s:text name="label.city" /></td>
				<td><s:textfield name="customer.addresses[0].city" /></td>
				<td class="non_editable"><s:text name="label.state" /></td>
				<td><s:textfield name="customer.addresses[0].state" /></td>
			</tr>

			<tr>
				<td class="non_editable"><s:text name="label.zip" /></td>
				<td><s:textfield name="customer.addresses[0].zipCode" /></td>
				<td class="non_editable"><s:text name="label.country" /></td>
				<td><s:textfield name="customer.addresses[0].country" /></td>
			</tr>

			<tr>
				<td class="non_editable"><s:text name="label.phone" /></td>
				<td><s:textfield name="customer.addresses[0].phone" /></td>

				<td class="non_editable"><s:text name="label.email" /></td>
				<td><s:textfield maxlength="255" name="customer.addresses[0].email" /></td>
			</tr>

			<tr>
				<td class="non_editable"><s:text name="label.fax" /></td>
				<td><s:textfield name="customer.addresses[0].secondaryPhone" /></td>
			</tr>
		</table>
		</div>
		<div id="separator"></div>
		</div>
		<div id="submit" align="center"><input id="submit_btn"
			class="buttonGeneric" type="submit"
			value="<s:text name='button.register'/>" /><input id="cancel_btn"
			class="buttonGeneric" type="button"
			value="<s:text name='button.cancel'/>"
			onclick="document.location.href='login.jsp'" /></div>
		</div>
		<%--
	    <div dojoType="dijit.layout.ContentPane" class="dojoAlignBottom" id="footer">
	        <a href="http://www.tavant.com" id="tavantLogoHolder"><img src="image/tavant_logo1.gif"/></a>
	        <div class="footerData">
	            <s:property value="revision"/>
	            <s:text name="common.copyrightNotice"/>
	            <a href=""><s:text name="common.privacyPolicy"/></a>
	        </div>
	    </div>
	    --%>
	   </div>
	</s:form>
</u:body>
</html>
