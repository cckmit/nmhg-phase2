<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title>FIXME</title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/pushCustomerDetails.js"></script>
    <script type="text/javascript" src="scripts/validateAddress.js"></script>

    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>

<script type="text/javascript">
<s:if test="pageReadOnlyAdditional">
dojo.addOnLoad(function() {
    twms.util.makePageReadOnly("dishonourReadOnly");
});
</s:if>
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
	dojo.addOnLoad(function() {
	
		addr = dojo.byId("addrDiv");
		compAddr = dojo.byId("comp-addr");
        dojo.connect(dojo.byId('cancel_btn'), "onclick", function() {
			closeTheTab();
		});
   });
</script>
<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults/>
 <s:if test="customer==null || customer.id == null">

	<s:form action="create_customer.action" method="POST" id="createCustomerForm" name="create" theme="twms" validate="true">
		<s:hidden name="customer.associatedOrganizations" value="%{getOrganization().id}"/>
        <div id="customer_info" class="section_div" style="margin:5px;width:100%">
		<div id="customer_info_title" class="section_heading"><s:text
			name="label.manageCustomer.newCustomerInfo" /></div>
		<s:hidden name="customer.individual" value="false"/>
		<s:hidden name="dealerName"/>
        <s:hidden name="dealerId"/>

        <div id="addrDiv"><jsp:include flush="true"
			page="input_customer_details.jsp" /></div>
		</div>
		
		<s:hidden id="pushInfoPressed" name="pushInfoPressed" />
		<s:hidden id="selectedAddressId" name="selectedAddressId" />
		<s:hidden id="matchRead" name="matchRead"/>
	</s:form>

</s:if> <s:else>

	<s:form action="update_customer.action" method="POST" id="updateCustomerForm" name="create"
		validate="true">
		<s:hidden name="customer" value="%{customer.id}"/>
		<s:hidden name="customerId" />
		<%-- Existing Organizations --%>
		<s:iterator value="customer.associatedOrganizations">
			<s:hidden name="customer.associatedOrganizations" value="%{id}"/>
		</s:iterator>
		<s:hidden name="customer.associatedOrganizations" value="%{getOrganization().id}"/>
		<div id="customer_info" class="section_div">
            <div id="customer_info_title" class="section_heading"><s:text
                name="message.manageCustomer.customerInfo" /></div>
            <s:hidden name="customer.individual" value="false"/>	

            <div id="addrDiv"><jsp:include flush="true"
                page="input_customer_details.jsp" />
            </div>
		</div>
		
		<s:hidden id="pushInfoPressed" name="pushInfoPressed" />
		<s:hidden id="selectedAddressId" name="selectedAddressId" />
		<s:hidden name="transfer"/>
		<s:hidden id="matchRead" name="matchRead"/>
	</s:form>

</s:else> <s:if test="pushCustomerDetails">
	<s:if test="customer.id != null">
		<script type="text/javascript">
		    <s:if test="matchRead">
		       showNewCustomerDetailsForMatchRead("<s:property value="matchReadInfo.ownerName"/>", "<s:property value="matchReadInfo.ownerCity"/>","<s:property value="matchReadInfo.ownerState"/>","<s:property value="matchReadInfo.ownerZipcode"/>","<s:property value="matchReadInfo.ownerCountry"/>","<s:property value="matchReadInfo.ownerCity"/>","<s:property value="checkForValidatableCountry(matchReadInfo.ownerCountry)"/>",<s:property value="selectedAddressId"/>);
		    </s:if>
			<s:elseif test="transfer">
				showNewCustomerDetails(<s:property value="customer.id"/>, <s:property value="selectedAddressId"/>,'show_customer_details_for_transfer');
			</s:elseif>
			<s:else>
				showNewCustomerDetails(<s:property value="customer.id"/>, <s:property value="selectedAddressId"/>,'show_customer_details_for_registration');
			</s:else>
		</script>
	</s:if>
</s:if></div>
</div>
</u:body>
</html>
