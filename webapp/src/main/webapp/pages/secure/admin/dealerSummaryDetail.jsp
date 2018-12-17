<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<s:form action="update_certification_status" method="post" id="updateCertificationStatus">
<hr/>
<table class="grid">
    <tr>
        <td  class="labelStyle" nowrap="nowrap" width="20%"><s:text name="Dealership Name"/>:</td>
        <td><s:property value="serviceProvider.name"/></td>
    </tr>
    <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="Dealership Number"/>:</td>
        <td><s:property value="serviceProvider.serviceProviderNumber"/></td>
    </tr>
    <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="Dealership Type"/>:</td>
        <td><s:property value="serviceProvider.type"/></td>
    </tr>
     <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="Classification"/>:</td>
        <td><s:property value="serviceProvider.customerClassification"/></td>
    </tr>
     <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.email"/>:</td>
        <td><s:property value="serviceProvider.address.email"/></td>
    </tr>
      <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="label.dealer.summary.sap.email"/>:</td>
        <td><s:textfield  size="25" name="emailForSapNotifications" value="%{serviceProvider.address.emailForSapNotifications}" id=""/></td>
    </tr>
	<tr>
		<td class="labelStyle" nowrap="nowrap"><s:text
			name="Certification Status" />:</td>

		<s:hidden name="dealerId"
			value="%{serviceProvider.serviceProviderNumber}" />
			<td><s:radio id="certificationstatus" value="%{certification}"
			name="certification" list="#{1:'Certified', 0:'Non-Certified'}"
			label="Certification Status" cssStyle="width:20px;border:0px;align:left;" labelposition="top" /></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><input id="submit_btn" class="buttonGeneric"
			type="submit" value="<s:text name='button.common.update'/>" /></td>
	</tr>
</table>
</s:form>

<!-- Dealer Address Changes -->

<div id="DealerHierachy_header" class="section_header">
    <s:text name="shipment.address"/>
</div>
<br/>
<s:form action="update_shipment_address" method="post" id="updateShipmentAddress">
<table height="280px;" width="100%;">    
<s:hidden name="serviceProvider.shipmentAddress.id" value="%{serviceProvider.shipmentAddress.id}"></s:hidden>	
<s:hidden name="dealerId"
			value="%{serviceProvider.serviceProviderNumber}" />
			
  <tr>
    <td class="labelStyle"><s:text name="label.common.address.line1"/></td>
    <td><t:textarea name="serviceProvider.shipmentAddress.addressLine1" value="%{serviceProvider.shipmentAddress.addressLine1}" id="userAddress1" cssStyle="width: 135px; height: 39px;" rows="1"/></td>
    <td></td>
    <td class="labelStyle"><s:text name="label.common.address.line2"/></td>
    <td><t:textarea name="serviceProvider.shipmentAddress.addressLine2" value="%{serviceProvider.shipmentAddress.addressLine2}" id="userAddress2" cssStyle="width: 135px; height: 39px;" rows="1"/></td>
</tr>

<tr>    
    <td class="labelStyle"><s:text name="label.country"/></td>
    <td><t:textarea name="serviceProvider.shipmentAddress.country" value="%{serviceProvider.shipmentAddress.country}" id="country_company_new" cssStyle="width: 135px; height: 39px;" rows="1"/></td>
   	    <td></td>
	    <td class="labelStyle"><s:text name="label.state"/></td>
    <td>
	      <s:textfield id="free_text_state_company_new" name="serviceProvider.shipmentAddress.state" value="%{serviceProvider.shipmentAddress.state}" style="width: 131px; margin-left: 0px;" /> 
	</td> 
</tr>
<tr>
    <td class="labelStyle"><s:text name="label.dealerUser.email"/></td>
    <td><s:textfield size="30" maxlength="255" name="serviceProvider.shipmentAddress.email" value="%{serviceProvider.shipmentAddress.email}"/></td>
    <td></td>
    <td class="labelStyle"><s:text name="label.common.phone"/></td>
    <td><s:textfield maxLength="30" size="30" name="serviceProvider.shipmentAddress.phone" value="%{serviceProvider.shipmentAddress.phone}"/></td>
    	
</tr>
<tr>
</tr>
<tr>
     <td class="labelStyle"><s:text name="label.city"/></td>
    <td>	           
	      <s:textfield id="free_text_city_company_new" name="serviceProvider.shipmentAddress.city" value="%{serviceProvider.shipmentAddress.city}" style="width: 131px; margin-left: 0px;"/>      
		</td>
		<td></td>
		<td class="labelStyle"><s:text name="label.common.fax"/></td>
    <td ><s:textfield maxLength="30" size="30" name="serviceProvider.shipmentAddress.fax" value="%{serviceProvider.shipmentAddress.fax}"/></td>
</tr>
<tr>
</tr>
<tr>
    
    <td class="labelStyle"><s:text name="label.zip"/></td>
    <td>           
	<s:textfield id="free_text_zip_company_new" name="serviceProvider.shipmentAddress.zipCode" value="%{serviceProvider.shipmentAddress.zipCode}" style="width: 131px; margin-left: 0px;"/>      
	</td>
</tr>
<tr>
<td></td>
<td></td>
		<td align="left"><input id="submit_btn" class="buttonGeneric"
			type="submit" value="<s:text name='button.common.update'/>" /></td>
	</tr>
</table>
	
<!--        End         -->



</s:form>

<!-- SLMSPROD-970 Changes - START - Specification of eligible 30 Day NCR Classes -->

<s:if test="buConfigEMEA">
	<div id="DealerHierachy_header" class="section_header">
	    <s:text name="30 Day NCR Claims Eligibility"/>
	</div>
	<br/>
	<s:form action="update_eligible_30day_ncr_classes" method="post" id="updateEligible30DayNcrClasses">
		<s:hidden name="dealerId" value="%{serviceProvider.serviceProviderNumber}" />
		<table style="height: 200px; width:100%">
			<tr>
				<td style="width: 50%"><strong><s:text name="header.30dayncr.existing.section"/></strong></td>
				<td><strong><s:text name="header.30dayncr.update.section"/></strong></td>
			</tr>
			<tr>
				<td style="width:50%; text-align: left; vertical-align:top">
					<s:if test="%{currentAllowed30DayNcrClasses.isEmpty()}">
						<s:text name="message.eligible30dayclass.existing.none"/>
					</s:if>
					<s:else>
						<ul>
							<s:iterator value="currentAllowed30DayNcrClasses" var="item">
								<li><s:property value="name"/></li>
							</s:iterator>
						</ul>
					</s:else>	
				</td>
				<td style="text-align: left; vertical-align:top">
					<s:select id="updateAllowed30DayNcrClasses" list="thirtyDayNcrClasses" listKey="id" listValue="name"
						value="currentAllowed30DayNcrClassIds" name="currentAllowed30DayNcrClasses" multiple="true"
						headerKey="" headerValue="%{getText('label.common.selectHeader')}"
						size="8" 
						style="width: 15em;"
					/>
				</td>
			</tr>
			<tr>
				<td colspan="2" style="text-align: center">
					<input id="submit_btn" class="buttonGeneric" type="submit" value="<s:text name='button.common.update'/>" />
				</td>
			</tr>
		</table>
	</s:form>
</s:if>

<!-- SLMSPROD-970 Changes - END - Specification of eligible 30 Day NCR Classes -->

<div class="policy_section_div" style="width:100%">
<div id="DealerHierachy_header" class="section_header">
    <s:text name="Network Structure"/>
</div>

<table class="grid" cellspacing="0" cellpadding="0">
    <s:iterator value="dealerGroupMap">
    <tr>
        <td>
            <s:iterator value="%{top.value}" status="iter">
                <s:if test="%{#iter.last}">
                    <s:property/>
                </s:if>
                <s:else>
                    <s:property/> >>
                </s:else>
            </s:iterator>
        </td>
    <tr>
        </s:iterator>
</table>
</div>

<div class="spacer4"></div>
<div class="policy_section_div" style="width:100%">
<div id="LaborRate_header" class="section_header">
    <s:text name="Labor Rates"/>
</div>
<div style="margin-top:6px;margin-bottom:6px;">
<table class="gridDetails borderForTable" align="center" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr class="row_head">                                             
            <th width="10%"><s:text name="Product Type"/></th>
            <th width="10%"><s:text name="Claim Type"/></th>
            <th width="10%"><s:text name="Warranty Type"/></th>
            <th width="10%"><s:text name="Customer Type"/></th>
            <th width="28%"><s:text name="Base Rate"/>(<s:property value="serviceProvider.preferredCurrency"/>)</th>
            <th width="10%"><s:text name="From Date"/></th>
            <th width="10%"><s:text name="To Date"/></th>
            <th width="10%"><s:text name="label.common.businessUnit"/></th>
        </tr>
    </thead>
    <tbody>
        <s:if test="%{laborRatesList==null || laborRatesList.isEmpty()}">
            <tr>
                <td colspan="8" align="center"><s:text name="No Data Found"/></td>
            </tr>
        </s:if>
        <s:else>
            <s:iterator value="laborRatesList">
                <tr>
                    <td><s:property value="laborRates.forCriteria.productName"/></td>
                    <td><s:property value="%{getText(laborRates.forCriteria.clmTypeName)}"/></td>
                    <td><s:property value="%{getText(laborRates.forCriteria.wntyTypeName)}"/></td>
                    <td>
                        <s:if test="laborRates.customerType == null">
                            ALL
                        </s:if>
                        <s:else>
                            <s:property value="laborRates.customerType"/>
                        </s:else>
                    </td>
                    <td>
                        <s:iterator value="laborRateValues" status="selectedLaborRates">
                            <s:if test="%{rate.breachEncapsulationOfCurrency().equals(serviceProvider.preferredCurrency)}">
                                <s:property value="%{rate.breachEncapsulationOfAmount()}"/>
                            </s:if>
                        </s:iterator>
                    </td>
                    <td><s:property value="duration.fromDate"/></td>
                    <td><s:property value="duration.tillDate"/></td>
                    <td><s:property value="laborRates.businessUnitInfo.name"/> </td>
                </tr>
            </s:iterator>
        </s:else>
    </tbody>
</table>
</div>
</div>
<div class="policy_section_div" style="width:100%">
<div id="TravelRates_header" class="section_header">
    <s:text name="Travel Rates"/>
</div>
<div style="margin-top:6px;margin-bottom:6px;">
<table class="gridDetails borderForTable" align="center" cellspacing="0" cellpadding="0" >
    <thead>
        <tr class="row_head">
            <th width="10%"><s:text name="Product Type"/></th>
            <th width="10%"><s:text name="Claim Type"/></th>
            <th width="10%"><s:text name="Warranty Type"/></th>
            <th width="10%"><s:text name="Customer Type"/></th>
            <th width="12%"><s:text name="Distance Rate"/>(<s:property value="serviceProvider.preferredCurrency"/>)</th>
            <th width="10%"><s:text name="Hourly Rate"/>(<s:property value="serviceProvider.preferredCurrency"/>)</th>
            <th width="10%"><s:text name="Trip Rate"/>(<s:property value="serviceProvider.preferredCurrency"/>)</th>
            <th width="8%"><s:text name="From Date"/></th>
            <th width="8%"><s:text name="To Date"/></th>
            <th width="10%"><s:text name="label.common.businessUnit"/></th>
        </tr>
    </thead>
    <tbody>
        <s:if test="%{travelRatesList==null || travelRatesList.isEmpty()}">
            <tr>
                <td colspan="10" align="center"><s:text name="No Data Found"/></td>
            </tr>
        </s:if>
        <s:else>
            <s:iterator value="travelRatesList">
                <tr>
                    <td><s:property value="travelRates.forCriteria.productName"/></td>
                    <td><s:property value="%{getText(travelRates.forCriteria.clmTypeName)}"/></td>
                    <td><s:property value="%{getText(travelRates.forCriteria.wntyTypeName)}"/></td>
                    <td>
                        <s:if test="travelRates.customerType == null">
                            ALL
                        </s:if>
                        <s:else>
                            <s:property value="travelRates.customerType"/>
                        </s:else>
                    </td>
                    <td>
                        <s:iterator value="travelRateValues">
                            <s:if test="%{distanceRate.breachEncapsulationOfCurrency().equals(serviceProvider.preferredCurrency)}">
                                <s:property value="%{distanceRate.breachEncapsulationOfAmount()}"/>
                            </s:if>
                        </s:iterator>
                        <s:if test="%{getValueIsDistanceFlatRate()}">
                            (flat Rate)
                        </s:if>
                    </td>
                    <td>
                        <s:iterator value="travelRateValues">
                            <s:if test="%{hourlyRate.breachEncapsulationOfCurrency().equals(serviceProvider.preferredCurrency)}">
                                <s:property value="%{hourlyRate.breachEncapsulationOfAmount()}"/>
                            </s:if>
                        </s:iterator>
                        <s:if test="%{getValueIsHourFlatRate()}">
                            (flat Rate)
                        </s:if>
                    </td>
                    <td>
                        <s:iterator value="travelRateValues">
                            <s:if test="%{tripRate.breachEncapsulationOfCurrency().equals(serviceProvider.preferredCurrency)}">
                                <s:property value="%{tripRate.breachEncapsulationOfAmount()}"/>
                            </s:if>
                        </s:iterator>
                        <s:if test="%{getValueIsTripFlatRate()}">
                           (flat Rate) 
                        </s:if>
                    </td>
                    <td><s:property value="duration.fromDate"/></td>
                    <td><s:property value="duration.tillDate"/></td>
                    <td><s:property value="travelRates.businessUnitInfo.name"/> </td>
                </tr>
            </s:iterator>
        </s:else>
    </tbody>
</table>
</div>
</div>
<div class="policy_section_div" style="width:100%">
<div id="Modifier_header" class="section_header">
    <s:text name="Modifiers"/>
</div>
<div style="margin-top:6px;margin-bottom:6px;">
<table class="gridDetails borderForTable" align="center" cellspacing="0" cellpadding="0">
    <thead>
        <tr class="row_head">
            <th width="10%"><s:text name="Product Type"/></th>
            <th width="10%"><s:text name="Claim Type"/></th>
            <th width="10%"><s:text name="Warranty Type"/></th>
            <th width="10%"><s:text name="Customer Type"/></th>
            <th width="10%"><s:text name="Cost Category"/></th>
            <th width="10%"><s:text name="Modifier Name"/></th>
            <th width="10%"><s:text name="Modifier Value"/></th>
            <th width="10%"><s:text name="From Date"/></th>
            <th width="10%"><s:text name="To Date"/></th>
            <th width="10%"><s:text name="label.common.businessUnit"/></th>
        </tr>
    </thead>

    <tbody>
        <s:if test="%{criteriaBasedValuesList==null || criteriaBasedValuesList.isEmpty()}">
            <tr>
                <td colspan="10" align="center"><s:text name="No Data Found"/></td>
            </tr>
        </s:if>
        <s:else>
            <s:iterator value="criteriaBasedValuesList">
                <tr>
                    <td><s:property value="parent.forCriteria.productName"/></td>
                    <td><s:property value="%{getText(parent.forCriteria.clmTypeName)}"/></td>
                    <td><s:property value="%{getText(parent.forCriteria.wntyTypeName)}"/></td>
                    <td>
                        <s:if test="parent.customerType == null">
                            ALL
                        </s:if>
                        <s:else>
                            <s:property value="parent.customerType"/>
                        </s:else>
                    </td>
                    <td>
                        <s:property value="%{criteriaBasedValueSectionNameMap.get(id)}"/>
                    </td>
                    <td><s:property value="parent.forPaymentVariable.name"/></td>
                    <td>
                        <s:if test="%{getIsFlatRate()}">
                            <s:property value="value"/>(flat Rate)
                        </s:if>
                        <s:else>
                            <s:property value="value"/>%
                        </s:else>
                    </td>
                    <td><s:property value="duration.fromDate"/></td>
                    <td><s:property value="duration.tillDate"/></td>
                    <td><s:property value="parent.forPaymentVariable.businessUnitInfo.name"/> </td>
                </tr>
            </s:iterator>
        </s:else>
    </tbody>
</table>
</div>
</div>

<div class="spacer4"></div>
<div class="policy_section_div" style="width:100%">
<div id="DealerHierachy_header" class="section_header">
    <s:text name="label.dealerSummary.servicingLocations"/>
</div>
<div style="margin-top:6px;margin-bottom:6px;">
<table class="gridDetails borderForTable" align="center" cellspacing="0" cellpadding="0" width="100%">
    <thead>
        <tr class="row_head">                                             
            <th width="20%"><s:text name="label.dealerSummary.siteNumber"/></th>
            <th width="65%"><s:text name="label.dealerSummary.locationAddress"/></th>
            <th width="15%"><s:text name="label.common.lastModified"/></th>
        </tr>
    </thead>
    <tbody>
    <s:iterator value="orgAddresses">
    	<tr>
        	<td><s:property value="siteNumberForDisplay"/></td>
        	<td><s:property value="location"/></td>
        	<td><s:property value="d.updatedOn"/></td>
        </tr>
    </s:iterator>
    </tbody>
</table>
</div>
</div>
<authz:ifPermitted resource="dealerInformationDealerSummaryReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('updateCertificationStatus')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('updateCertificationStatus'))[i].disabled=true;
	        }
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('updateShipmentAddress')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('updateShipmentAddress'))[i].disabled=true;
	        }
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('updateEligible30DayNcrClasses')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('updateEligible30DayNcrClasses'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>

