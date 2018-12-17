<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/pushCustomerDetails.js"></script>
<script type="text/javascript" src="scripts/validateAddress.js"></script>
<script type="text/javascript">
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


<s:hidden name="customer" value="%{customer.id}" /> 
<s:hidden name="customerId" />
<%-- Existing Organizations --%> 
<s:iterator value="customer.associatedOrganizations">
	<s:hidden name="customer.associatedOrganizations" value="%{id}" />
</s:iterator> 
<s:hidden name="customer.associatedOrganizations" value="%{getOrganization().id}" />
<div id="customer_info" class="policy_section_div" style="width:100%">
<div id="customer_info_title" class="section_heading"><s:text
	name="message.manageCustomer.customerInfo" /></div>
<s:hidden name="customer.individual" value="false" />

<div id="addrDiv"><jsp:include flush="true"
	page="input_customer_details.jsp" /></div>
</div>
<div id="submit" align="center"><s:hidden name="hideSelect"
	id="hideSelect" /> </div>
<s:hidden id="pushInfoPressed" name="pushInfoPressed" /> 
<s:hidden id="selectedAddressId" name="selectedAddressId" /> 
<s:hidden name="transfer" /> 
<s:hidden id="matchRead" name="matchRead" /> 
<s:if test="pushCustomerDetails">
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
</s:if>

