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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<div style="">
	<div class="mainTitle" style="margin-top: 5px;">
		<s:text name="label.newClaim.travelInformation" />
		:
	</div>
	<div class="borderTable">&nbsp;</div>
	<table class="grid" cellspacing="0" cellpadding="0"
		style="margin-top: -10px;">
		<tr>
			<s:if test="claim.travelDisConfig">
				<td width="22%" class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.travelDistance" />:</td>
				<td width="35%"><s:property
						value="claim.serviceInformation.serviceDetail.travelDetails.distance" />
					<s:if
						test="claim.serviceInformation.serviceDetail.travelDetails.distance != null">
						<s:if
							test="claim.serviceInformation.serviceDetail.travelDetails.uom.equalsIgnoreCase('km')">
							<s:text name="dropdown.common.kilometers" />
						</s:if>
						<s:elseif
							test="claim.serviceInformation.serviceDetail.travelDetails.uom.equalsIgnoreCase('miles')">
							<s:text name="dropdown.common.miles" />
						</s:elseif>
					</s:if></td>
			</s:if>
			<s:if test="claim.travelDisConfig">
			<td class="labelStyle" nowrap="nowrap" width="20%"><s:text
					name="label.newClaim.travelLocation" />:</td>
			<td><s:property
					value="claim.serviceInformation.serviceDetail.travelDetails.location" /></td>
		   </s:if>
		</tr>
		<%-- This is hidden as we don't want to display the additional distance details --%>
		<s:if test="task.claim.travelDisConfig">
		<tr><td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.newClaim.additionalTravelDistance"/>:</td>
		<td><s:property value="claim.serviceInformation.serviceDetail.travelDetails.additionalDistance"/> </td>
		<td class="labelStyle" nowrap="nowrap"><s:text name="label.newClaim.additionalTravelDistanceReason"/>:</td>
		<td><s:property value="claim.serviceInformation.serviceDetail.travelDetails.additionalDistanceReason"/> </td>
		</tr>
		</s:if>
		<tr>
		<s:if test="claim.travelHrsConfig">
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.travelHrs" />:</td>
						<!-- Fix for SLMSPROD-1392 -->
				<td><s:property
						value="claim.serviceInformation.serviceDetail.travelDetails.hours.toString().replace('.',':')" />
				</td>
			</s:if>
			<s:if test="claim.travelTripConfig">
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.travelTrips" />:</td>
				<td><s:property
						value="claim.serviceInformation.serviceDetail.travelDetails.trips" />
				</td>
			</s:if>
			

		</tr>
		<s:if test="claim.additionalTravelHoursConfig">
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.additionalTravelHrs" />:</td>
				<td><s:property
						value="claim.serviceInformation.serviceDetail.travelDetails.additionalHours" />
				</td>
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.additionalTravelHoursReason" />:</td>
				<td><s:property
						value="claim.serviceInformation.serviceDetail.travelDetails.additionalHoursReason" />
				</td>
			</tr>
		</s:if>
		<authz:ifUserInRole roles="processor,admin">
		<s:if test="claim.travelDisConfig">
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="label.newClaim.travelAddressChanged" />:</td>
		       <s:if test="claim.serviceInformation.serviceDetail.travelDetails.travelAddressChanged">
		           <td><s:text name="label.common.yes"/></td>
		       </s:if>
		       <s:else>
					<td colspan="1"><s:text name="label.common.no"/></td>		
		       </s:else>
			</tr>
		</s:if>
		</authz:ifUserInRole>
		<s:if test='buConfigAMER'> 
		<tr>
		<td class="labelStyle" nowrap="nowrap"><s:text
						name="title.attributes.technician" />:</td>
				<td><s:property
							value="claim.serviceInformation.serviceDetail.serviceTechnician" />
		</tr>
		</s:if>
		<s:else>
        <s:if test="technicianEnable">
		<s:if
			test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id ">
			<tr>
				<td class="labelStyle" nowrap="nowrap"><s:text
						name="title.attributes.technician" />:</td>
				<td><a id="show_technician_details" class="link"> <s:property
							value="claim.serviceInformation.serviceDetail.technician.name" />
				</a></td>
			</tr>
			<jsp:include flush="true" page="./technicianInfo.jsp" />
		</s:if>
		</s:if>
		</s:else>

	</table>
</div>