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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>
<script type="text/javascript">
   dojo.require("dijit.Tooltip");
</script>
<script type="text/javascript" src="scripts/googleMap.js"></script>
<div class="mainTitle">
	<s:text name="label.newClaim.travelInformation" />
</div>
<div class="borderTable">&nbsp;</div>
<table class="grid" cellspacing="0" cellpadding="0"
	style="width: 95%; margin-top: -10px;">
	<tr>
		<s:if test="task.claim.travelDisConfig">
			<s:hidden name="task.claim.travelDisConfig" value="true" />
			<td width="20%" nowrap="nowrap"><label for="travel_distance"
				class="labelStyle"><s:text
						name="label.newClaim.travelDistance" />:</label></td>
			<td width="27%"><s:hidden
					name="task.claim.serviceInformation.serviceDetail.travelDetails" />
					<s:hidden id="base_travel_distance"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.baseDistance" />
				<s:textfield id="travel_distance" size="5"
					cssStyle="text-align: right;padding-right: 1px;color: #808080"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.distance" readonly="true"/>
				<s:if
					test="task.claim.serviceInformation.serviceDetail==null || 
	            task.claim.serviceInformation.serviceDetail.travelDetails==null ||
	            task.claim.serviceInformation.serviceDetail.travelDetails.uom.equals('miles')">
					<select
						name="task.claim.serviceInformation.serviceDetail.travelDetails.uom" style="color:#808080">
						<option value="miles" >
							<s:text name="dropdown.common.miles" />
						</option>
						<%--<option value="km">
							<s:text name="dropdown.common.kilometers" />
						</option>
					--%>
					</select>
				</s:if> <s:else>
					<select
						name="task.claim.serviceInformation.serviceDetail.travelDetails.uom">
						<%--<option value="km">
							<s:text name="dropdown.common.kilometers" />
						</option>--%>
						<option value="miles">
							<s:text name="dropdown.common.miles" />
						</option>
					</select>
				</s:else></td>
		</s:if>
		<s:else>
			<s:hidden name="task.claim.travelDisConfig" value="false" />
			<s:hidden
				name="task.claim.serviceInformation.serviceDetail.travelDetails.distance"
				value="0.00" />
		</s:else>
		<s:if test="enableGoogleMapsForTravelHours() && task.claim.travelDisConfig">
		<td width="20%"><label for="travel_location" class="labelStyle"><s:text
					name="label.newClaim.travelLocation" />:</label></td>
		<td width="31%"><s:textarea id="travel_location"
				name="task.claim.serviceInformation.serviceDetail.travelDetails.location" cols="60" rows="2" style="overflow-Y:hidden" readonly="true"/>
				<span id="onee" tabindex="0" style="vertical-align:top;"><img id="googleMap" class="clickable" 
					src="css/theme/official/dojo/official/images/googleMapViewer.png"
					onclick="openMap()"/></span>
					<span dojoType="dijit.Tooltip" connectId="onee"><s:text
						name="label.newClaim.travelDistanceCalculation" /></span>
					</td>
		</s:if>
	</tr>
	<s:if test="task.travel.claim.travelDisConfig">
 <tr>
 
        <td width="20%">
	            <label for="additional_travel_distance" class="labelStyle"><s:text name="label.newClaim.additionalTravelDistance"/>:</label>
	        </td>
	        <td width="27%">
	        	<s:textfield id="additional_travel_distance" 
	        		name="task.claim.serviceInformation.serviceDetail.travelDetails.additionalDistance" cssStyle="text-align: right;padding-right: 1px" />
	        </td>
	       <td width="20%">
	            <label for="additional_travel_distance_reason" class="labelStyle"><s:text name="label.newClaim.additionalTravelDistanceReason"/>:</label>
	        </td>
	        <td width="27%">
	        	<s:textfield id="additional_travel_distance_reason" 
	        		name="task.claim.serviceInformation.serviceDetail.travelDetails.additionalDistanceReason" cssStyle="text-align: right;padding-right: 1px" />
	        </td>
        </tr>
        </s:if>
	<tr>
		<s:if test="task.claim.travelTripConfig">
			<s:hidden name="task.claim.travelTripConfig" value="true" />
			<td width="20%"><label for="travel_trips" class="labelStyle"><s:text
						name="label.newClaim.travelTrips" />:</label></td>
			<td width="27%">
				<s:if test="task.claim.serviceInformation.serviceDetail.travelDetails.trips">
				<s:textfield id="travel_trips"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.trips"
					cssStyle="text-align: right;padding-right: 1px" onblur="travelHrsMiles();"/>
					<div dojoType="dijit.Dialog" id="travelTripDecimal" title="Error" content="<br><s:text name="label.error.travelTrip"/><br>" style="width:400px;height:70px"></div>
				</s:if>
				<s:else><s:textfield id="travel_trips"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.trips"
					cssStyle="text-align: right;padding-right: 1px" value="0" onblur="travelHrsMiles();"/>
					<div dojoType="dijit.Dialog" id="travelTripDecimal" title="Error" content="<br><s:text name="label.error.travelTrip"/><br>" style="width:400px;height:70px">	</div>		
				</s:else>
			</td>
		</s:if>
		<s:else>
			<s:hidden name="task.claim.travelTripConfig" value="false" />
			<s:hidden
				name="task.claim.serviceInformation.serviceDetail.travelDetails.trips"
				value="0" />
		</s:else>
		<s:if test="task.claim.travelHrsConfig">
			<s:hidden name="task.claim.travelHrsConfig" value="true" />
			<td width="20%"><label for="travel_hours" class="labelStyle"><s:text
						name="label.newClaim.travelHrs" />:</label></td>
			<td width="27%"><span id="one" tabindex="0"> 
			<!-- fix for SLMSPROD-1392 -->
			<s:hidden
						id="travel_hours"
						name="task.claim.serviceInformation.serviceDetail.travelDetails.hours"cssStyle="text-align: right;padding-right: 1px;color: #808080" readonly="true"/>
						<!-- Fix for SLMSPROD-1392 -->
						<s:textfield id="travel_hours_temp" name="transientTravelHours"cssStyle="text-align: right;padding-right: 1px;color: #808080" readonly="true"onblur="setToTravelHours()"></s:textfield>						
			</span> <span dojoType="dijit.Tooltip" connectId="one"><s:text
						name="label.newClaim.additionalTravelHrs.minutesInDecimals" /></span></td>
						<s:hidden id="base_travel_hours"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.baseHours" />
		</s:if>
		<s:else>
			<s:hidden name="task.claim.travelHrsConfig" value="false" />
			<s:hidden
				name="task.claim.serviceInformation.serviceDetail.travelDetails.hours"
				value="0.00" />
		</s:else>
	</tr>
	<tr>
		<s:if test="task.claim.additionalTravelHoursConfig">
			<s:hidden name="task.claim.additionalTravelHoursConfig" value="true" />
			<td width="20%"><label for="additional_travel_hours"
				class="labelStyle"><s:text
						name="label.newClaim.additionalTravelHrs" />:</label></td>
			<td width="27%"><s:textfield id="additional_travel_hours"
					name="task.claim.serviceInformation.serviceDetail.travelDetails.additionalHours"
					cssStyle="text-align: right;padding-right: 1px" /></td>
			<td width="20%">
	            <label for="additional_travel_hours_reason" class="labelStyle"><s:text name="label.newClaim.additionalTravelHoursReason"/>:</label>
	        </td>
	        <td width="27%">
	        	<s:textfield id="additional_travel_hours_reason" 
	        		name="task.claim.serviceInformation.serviceDetail.travelDetails.additionalHoursReason" cssStyle="text-align: right;padding-right: 1px" />
	        </td>	        
		</s:if>
		<s:else>
			<s:hidden name="task.claim.additionalTravelHoursConfig" value="false" />
			<s:hidden
				name="task.claim.serviceInformation.serviceDetail.travelDetails.additionalHours"
				value="0.00" />
		</s:else>
		<s:if test='buConfigAMER'> 
	<td width="20%"><label class="labelStyle"><s:text
					name="label.newClaim.technician" />:</label></td>
		<td width="27%"><s:textfield id="technicianLoginId" name="task.claim.serviceInformation.serviceDetail.serviceTechnician" value="%{task.claim.serviceInformation.serviceDetail.serviceTechnician}"/><!-- Code added to Default Technician when size is 1 -->
			</td>
			   </s:if>
			   <s:else>
        <s:if test="technicianEnable">
		<td width="20%"><label class="labelStyle"><s:text
					name="label.newClaim.technician" />:</label></td>
		<td width="27%"><s:property
				value="populateTechnicians(task.claim.forDealer,task.claim.businessUnitInfo.name)" />
			<s:select
				name="task.claim.serviceInformation.serviceDetail.technician"
				id="technicianId" list="getTechnicians()" headerKey="-1"
				headerValue="%{getText('label.common.selectHeader')}" /> <!-- Code added to Default Technician when size is 1 -->
			<script type="text/javascript">
							    dojo.addOnLoad(function() {	
							    <s:if test="task.claim.serviceInformation.serviceDetail.technician !=null"> 
								dijit.byId('technicianId').setValue("<s:property value="task.claim.serviceInformation.serviceDetail.technician.id.toString()"/>");
								</s:if>						   
					            /* <s:elseif test="getTechnicians().size()==1">					           
					            dijit.byId('technicianId').setValue("<s:property value="getTechnicians().keySet().toArray()[0].toString()"/>");
					          	</s:elseif>	 */			   
					          });
			   </script></td>
			   </s:if>
			   </s:else>
		<s:if test="isCreateTechnicianEnabled()">
		<authz:ifUserInRole roles="admin">		
			<td width="50%"><u:openTab
					decendentOf="%{getText('home_jsp.tabs.home')}"
					id="open_create_user" tabLabel="Create Technician"
					url="show_dealer_user.action" catagory="dealerInformation">
					<s:text name="label.technician.createTechnician" />
				</u:openTab></td>
		</authz:ifUserInRole>
		</s:if>
	</tr>
	<tr>
		<authz:ifUserInRole roles="processor,admin">
			<s:if test="task.claim.travelDisConfig">
					<s:hidden name="task.claim.travelDisConfig" value="true" />
					<td width="20%"><label for="travel_Address_Changed"
						class="labelStyle"><s:text name="label.newClaim.travelAddressChanged" />:</label></td>
			        <td width="27%">
			        <s:checkbox
						name="task.claim.serviceInformation.serviceDetail.travelDetails.travelAddressChanged"
						onclick="javascript:return false;" id="travelAddressChanged"
						cssStyle="checkbox" />
				</td>
			</s:if>
			<s:else>
				<s:hidden name="task.claim.travelHrsConfig" value="false" />
				<s:hidden
						name="task.claim.serviceInformation.serviceDetail.travelDetails.travelAddressChanged"
						value="false" />
			</s:else>
		</authz:ifUserInRole>
		<authz:else>
			<s:if test="task.claim.travelDisConfig">
					<s:hidden name="task.claim.travelDisConfig" value="true" />
			        <s:checkbox
						name="task.claim.serviceInformation.serviceDetail.travelDetails.travelAddressChanged"
						style="visibility:hidden" onclick="javascript:return false;"
						id="travelAddressChanged" />
			</s:if>
			<s:else>
				<s:hidden name="task.claim.travelHrsConfig" value="false" />
				<s:hidden
						name="task.claim.serviceInformation.serviceDetail.travelDetails.travelAddressChanged"
						value="false" />
			</s:else>
		</authz:else>
	</tr>
</table>
<jsp:include page="googleMap.jsp"/>
<script type = "text/javascript">
dojo.addOnLoad(function(){
	<s:if test="!enableGoogleMapsForTravelHours()">
		if(dojo.byId("travel_distance"))
			dojo.byId("travel_distance").readOnly = false;
		if(dojo.byId("travel_hours"))
			dojo.byId("travel_hours_temp").readOnly = false;
	</s:if>
	<s:else>
		if(dojo.byId("googleMap"))
			dojo.html.show(dojo.byId("googleMap"));
		if(dojo.byId("travel_location")){
			dojo.html.show(dojo.byId("travel_location"));
			dojo.byId("travel_location").readOnly = true;	
		}
		if(dojo.byId("travel_distance"))
			dojo.byId("travel_distance").readOnly = true;
		if(dojo.byId("travel_hours_temp"))
			dojo.byId("travel_hours_temp").readOnly = true;
	</s:else>
});
function setToTravelHours(){
	if(dojo.byId("travel_hours") && dojo.byId("travel_hours_temp") && !dojo.byId("travel_hours_temp").readOnly)		
		dojo.byId("travel_hours").value = dojo.byId("travel_hours_temp").value.replace(':','.');
	}
</script>