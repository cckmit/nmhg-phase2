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


<html>
<head>
    <title><s:text name="label.uploads.claimUploadSuccessPage"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount")
        });
        dojo.require("twms.widget.Select");
    </script>
</head>

<u:body>
<form name="baseForm" id="baseForm">
	<u:actionResults/>
	<div id="vehicle_info" class="section_div">
		<div id="vehicle_info_title" class="section_heading">
			<s:text name="title.complaints.vehicleInfo" />
		</div>
		<table class="form" cellpadding="0" cellspacing="0">
			<s:if test="complaint.serialNumber != null || complaint.itemNumber != null">
				<tr>					
					<td class="labelStyle"><s:text name="label.common.serialNumber" />:</td>
					<td><s:property value="complaint.serialNumber"/></td>
					
					<td class="labelStyle"><s:text name="label.common.itemNumber" />:</td>
					<td><s:property value="complaint.itemNumber"/></td>					
				</tr>						
			</s:if>
			<tr>
				<td class="labelStyle"><s:text name="label.common.product" />:</td>
				<td><s:property value="complaint.product"/></td>
				<td class="labelStyle"><s:text name="label.common.model" />:</td>
				<td><s:property value="complaint.model"/></td>
				<td class="labelStyle"><s:text name="label.common.year" />:</td>
				<td><s:property value="complaint.year"/></td>						
			</tr>			
		</table>
	</div>	        			
	<div id="incident_details" class="section_div">
		<div id="incident_details_info_title" class="section_heading">
			<s:text name="title.complaints.incidentDetails" />
		</div>        
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td class="labelStyle"><s:text name="label.complaints.incidentDate" />:</td>
                <td><s:property value="complaint.incidentDate" /></td>
				<td class="labelStyle"><s:text name="label.complaints.incidentDescription" />:</td>
				<td><s:property value="complaint.incidentDescription" /></td>				
			</tr>
			<tr>
				<td class="labelStyle"><s:text name="label.complaints.noOfFailures" />:</td>
				<td><s:property value="complaint.numberOfFailures" /></td>
				<td class="labelStyle"><s:text name="label.complaints.noOfDeaths" />:</td>
				<td><s:property value="complaint.numberOfDeaths" /></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text name="label.complaints.noOfPersonsInjured" />:</td>
				<td><s:property value="complaint.numberOfInjuredPersons" /></td>
				<td class="labelStyle"><s:text name="label.complaints.failedComponent" />:</td>
				<td><s:property value="complaint.failedComponent" /></td>
			</tr>
			<tr>			
		        <td><label><s:text name="label.complaints.wasThereFire" />:</label></td>
		        <td>
		        	<s:if test="complaint.isThereAFire">
		        		<s:text name="Yes"/>
		        	</s:if>
		          <s:else>
		          	<s:text name="No"/>	
		          </s:else>
		        </td>
		        <td><label><s:text name="label.complaints.isPropertyDamaged" />:</label></td>
		        <td>
		        	<s:if test="complaint.isPropertyDamaged">
		        		<s:text name="Yes"/>
		        	</s:if>
		          <s:else>
		          	<s:text name="No"/>	
		          </s:else>
		        </td>
			</tr>
			<tr>			
		        <td><label><s:text name="label.complaints.wasThereCrash" />:</label></td>
		        <td>
		        	<s:if test="complaint.isThereACrash">
		        		<s:text name="Yes"/>
		        	</s:if>
		          <s:else>
		          	<s:text name="No"/>	
		          </s:else>
		        </td>
		        <td><label><s:text name="label.complaints.reportedToPolice" />:</label></td>
		        <td>
		        	<s:if test="complaint.hasReportedToPolice">
		        		<s:text name="Yes"/>
		        	</s:if>
		          <s:else>
		          	<s:text name="No"/>	
		          </s:else>
		        </td>
			</tr>			
			<tr>
				<td class="labelStyle"><s:text name="claim.failure.detailsHeader.faultCode" />:</td>
				<td><s:property value="complaint.faultCodeRef.definition.code" /></td>
			</tr>			
		</table>
	</div>
	<div id="customer_info" class="section_div" style="margin:5px;">
		<div id="customer_info_title" class="section_heading">
			<s:text name="title.common.customerInfo" />
		</div>        
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td class="labelStyle"><s:text name="label.complaints.firstName" />:</td>
				<td><s:property value="complaint.consumer.firstName" /></td>
				<td class="labelStyle"><s:text name="label.complaints.lastName" />:</td>
				<td><s:property value="complaint.consumer.lastName" /></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text name="label.common.address" />:</td>
				<td colspan="3"><s:property value="complaint.consumer.address.addressLine1"/></td>
			</tr>			
			<tr>
				<td class="labelStyle"><s:text name="label.common.city" />:</td>
				<td><s:property value="complaint.consumer.address.city" /></td>
				<td class="labelStyle"><s:text name="label.common.state" />:</td>
				<td><s:property value="complaint.consumer.address.state" /></td>				
			</tr>
			<tr>
				<td class="labelStyle"><s:text name="label.common.zipCode" />:</td>
				<td><s:property value="complaint.consumer.address.zipCode" /></td>
				<td class="labelStyle"><s:text name="label.common.country" />:</td>
	            <td><s:property value="complaint.consumer.address.country" /></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text name="label.common.phone" />:</td>
				<td><s:property value="complaint.consumer.address.phone" /></td>
			</tr>
		</table>
	</div>
	<s:hidden name="complaint" value="%{complaint.id}" />
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons">
        <tbody>
            <tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.update')}" cssClass="buttonGeneric"
                    		  action="update_complaint_page1"/>
                    <s:submit value="%{getText('button.common.delete')}"  cssClass="buttonGeneric"
                    		  action="delete_complaint"/>
                </td>
            </tr>
        </tbody>
    </table>		
</form>
</u:body>
</html>