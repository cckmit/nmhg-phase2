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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<%--
This script is to display the pop up page to search the inventories for MULTICLAIM.
It handles the display of the search result in the same page using FormBind.
And also finally renders the selected inventories to the claim new_machine_claim1.jsp
while hiding the pop up search page
--%>
<%@page import="tavant.twms.domain.claim.MatchReadMultiplyFactor"%>
<u:stylePicker fileName="multiCar.css" />
<style type="text/css">
	span.validateError {
	    background-image: url( image/alerts.gif );
	    width: 20px;
	    height: 25px;
	    vertical-align: bottom;
	    background-repeat: no-repeat;
	    position: absolute;
	    margin-left: 3px; /* can't use padding since position is absolute */
	}
	
	.bold {
		font-weight: bold;
	}
</style>
<script type="text/javascript">

dojo.addOnLoad(function() {	
	dojo.subscribe("/matchReadScore/show", null, function() {
		dijit.byId("ownerInfoScore").show();
    }); 
    dojo.connect(dojo.byId("show_matchRead_score_page"),"onclick",function() {
            		dojo.publish("/matchReadScore/show");
				}); 
	dojo.subscribe("/matchReadScore/hide", null, function() {
		dijit.byId("ownerInfoScore").hide();
    });   
});
</script>
<div id="dialogBoxContainer" style="display:none">
<div id="ownerInfoScore" dojoType="twms.widget.Dialog" title="<s:text name="label.matchRead.ownerInformation"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:70%">
<div dojoType="dijit.layout.LayoutContainer">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="padding-bottom: 20px;padding-top: 10px;">
    <table cellpadding="0" cellspacing="0" 
    style="margin-top: 10px; width: 97%; margin-left:9px; margin-bottom:10px;" class="grid borderForTable">
    <thead>
    	<th width="20%"  ></th>
        <th width="30%"  ><s:text name="label.matchRead.equipmentValue"/></th>
        <th width="30%"  ><s:text name="label.matchRead.distributorValue"/></th>
        <th width="10%"  ><s:text name="label.matchRead.score"/></th>
    </thead>
    <tbody>    	
    	<s:set name ="latestOwner" 
    		   value="task.claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy"/>
    	<tr>
            <td  >
                <label id="ownerNameLabel" class="bold">
                    <s:text name="label.warrantyAdmin.ownerName"/>:
                </label>
            </td>
            <td  >
            	<s:property value="#latestOwner.name"/>
            </td>
            <td id="nameField"  >            	
            	<s:property value="task.claim.matchReadInfo.ownerName"/>
            </td>
            <s:if test="#latestOwner.name.equalsIgnoreCase(task.claim.matchReadInfo.ownerName)">
            <td id="nameField"  >            	
            	<s:property value="@tavant.twms.domain.claim.MatchReadMultiplyFactor@OWNER_NAME"/>       
            </td>
            </s:if>
            <s:else>
            <td id="nameField"  >            	
            	0            
            </td>
            </s:else>

        </tr>
        <tr>
            <td  >
                <label id="cityLabel" class="bold">
                    <s:text name="label.common.city"/>:
                </label>
            </td>
            <td  >
            	<s:property value="#latestOwner.address.city"/>
            </td>
            <td  id="cityField"  >
            	<s:property value="task.claim.matchReadInfo.ownerCity"/>               
            </td>
            <s:if test="#latestOwner.address.city.equalsIgnoreCase(task.claim.matchReadInfo.ownerCity)">
            <td id="nameField"  >            	
            	<s:property value="@tavant.twms.domain.claim.MatchReadMultiplyFactor@OWNER_CITY"/>      
            </td>
            </s:if>
            <s:else>
            <td id="nameField"  >            	
            	0             
            </td>
            </s:else>

        </tr>
        <tr>
            <td  >
                <label id="stateLabel" class="bold">
                    <s:text name="label.common.state"/>:
                </label>
            </td>
            <td  >
            	<s:property value="#latestOwner.address.state"/>
            </td>
            <td id="stateField"  >
            	<s:property value="task.claim.matchReadInfo.ownerState"/> 
            </td>
            <s:if test="#latestOwner.address.state.equalsIgnoreCase(task.claim.matchReadInfo.ownerState)">
            <td id="nameField"  >            	
            	<s:property value="@tavant.twms.domain.claim.MatchReadMultiplyFactor@OWNER_STATE"/>       
            </td>
            </s:if>
            <s:else>
            <td id="nameField"  >            	
            	0             
            </td>
            </s:else>
         </tr>
         <tr>
            <td  >
                <label id="zipcodeLabel" class="bold">
                    <s:text name="label.common.zipCode"/>:
                </label>
            </td>
            <td  >
            	<s:property value="#latestOwner.address.zipCode"/>
            </td>
            <td id="zipcodeField"  >
            	<s:property value="task.claim.matchReadInfo.ownerZipcode"/>                 
            </td>
            <s:if test="#latestOwner.address.zipCode.equalsIgnoreCase(task.claim.matchReadInfo.ownerZipcode)">
            <td id="nameField"  >            	
            	<s:property value="@tavant.twms.domain.claim.MatchReadMultiplyFactor@OWNER_ZIPCODE"/>       
            </td>
            </s:if>
            <s:else>
            <td id="nameField"  >            	
            	0             
            </td>
            </s:else>
        </tr>

        <tr>
            <td  >
                <label id="countryLabel" class="bold">
                    <s:text name="label.common.country"/>:
                </label>
            </td>
            <td  >
            	<s:property value="#latestOwner.address.country"/>
            </td>
            <td id="countryField"  >
            	<s:property value="task.claim.matchReadInfo.ownerCountry"/>                
            </td>
            <s:if test="#latestOwner.address.country.equalsIgnoreCase(task.claim.matchReadInfo.ownerCountry)">
            <td id="nameField"  >            	
            	<s:property value="@tavant.twms.domain.claim.MatchReadMultiplyFactor@OWNER_COUNTRY"/>       
            </td>
            </s:if>
            <s:else>
            <td id="nameField"  >            	
            	0             
            </td>
            </s:else>            
        </tr>
        </tbody>
    </table>

</div>
</div>
</div>
</div>