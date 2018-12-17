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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<%--
This script is to display the pop up page to search the inventories for MULTICLAIM.
It handles the display of the search result in the same page using FormBind.
And also finally renders the selected inventories to the claim new_machine_claim1.jsp
while hiding the pop up search page
--%>
<u:stylePicker fileName="multiCar.css" />

<script type="text/javascript">
    var ismatchReadNull ="<s:property value="task.claim.matchReadInfo.ownerName"/>";
</script>
<script type="text/javascript" src="scripts/matchRead/writeMatchRead.js"></script>
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
</style>

	<script type="text/javascript">
       dojo.addOnLoad(function() {
			 var validatableStateId = dijit.byId("validatable_matchReadInfo_state");
			 var freeTextStateId = dojo.byId("free_text_matchReadInfo_state");
			 var validatableCityId = dijit.byId("validatable_matchReadInfo_city");
			 var freeTextCityId = dojo.byId("free_text_matchReadInfo_city");
			 var validatableZipId = dijit.byId("validatable_matchReadInfo_zip");
			 var freeTextZipId = dojo.byId("free_text_matchReadInfo_zip");
		     var selectedVar = "<s:property value="checkForValidatableCountry(task.claim.matchReadInfo.ownerCountry)"/>";
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
			 dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextCityId,!selectedVar);
			 dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextZipId,!selectedVar);		 
      });
   </script>
<div id="dialogBoxContainer" style="display:none">
<div id="ownerInfoMR" dojoType="twms.widget.Dialog" title="<s:text name="label.matchRead.ownerInformation"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:60%;">
<div dojoType="dijit.layout.LayoutContainer">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="top">           
    <table cellpadding="0" cellspacing="0" style="margin-top: 5px; width: 98%; padding-bottom: 10px;">
    <tbody>
    	<tr>
            <td>
                <label id="ownerNameLabel" class="">
                    <s:text name="label.warrantyAdmin.ownerName"/>:
                </label>
            </td>
            <td colspan="3" id="nameField">            	
            	<input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
            		id="ownerName"
            		name="task.claim.matchReadInfo.ownerName" 
            		value="<s:property value="task.claim.matchReadInfo.ownerName"/>"/>                
            </td>

        </tr>
      
        <tr>
            <td>
                <label id="countryLabel" class="">
                    <s:text name="label.common.country"/>:
                </label>
            </td>
            <td id="countryField">
               	<s:select label ="Country" id="matchReadInfo_country"
                   name="task.claim.matchReadInfo.ownerCountry"
                   list="countryList" required="true" theme="twms"/>
                   <script type="text/javascript">
				  	  dojo.addOnLoad(function() {
				  	  
				  	        var countryName = "<s:property value="task.claim.matchReadInfo.ownerCountry"/>";
				  	  		  	
				  	  		  if(countryName == null || countryName == "")
				  	  		  {						  	  		  	  		  	  			
					  	            <s:if test="task.claim.matchReadInfo==null || task.claim.matchReadInfo.ownerCountry==null">
				  	           			 dijit.byId("matchReadInfo_country").setValue("US");
				  	        		</s:if>	
				  	          }	   
				  	       
						    dojo.connect(dijit.byId("matchReadInfo_country"),"onChange",function(value) {
						  	getStatesByCountry(value, dijit.byId("validatable_matchReadInfo_state"),
						  	                          [ 
						  	                              "free_text_matchReadInfo_state",
						  	                              "free_text_matchReadInfo_city",
						  	                              "free_text_matchReadInfo_zip"
						  	                          ],
						  	                          [
						  	                             "validatable_matchReadInfo_city",
						  	                             "validatable_matchReadInfo_zip"
						  	                          ]
						  	                     );
						  })
		             });
	          </script>
            </td>            
        </tr>
        
        <tr>
            <td>
                <label id="stateLabel" class="">
                    <s:text name="label.common.state"/>:
                </label>
            </td>
            <td id="stateField">
            <sd:autocompleter label='State' id='validatable_matchReadInfo_state' listenTopics='topic_matchReadInfo_state' name='task.claim.matchReadInfo.ownerState' />
			   <script type="text/javascript">
   				  	   dojo.addOnLoad(function() {
	   					    dojo.connect(dijit.byId("validatable_matchReadInfo_state"),"onChange", function(value) {
	   					    getCitiesByCountryAndState(value, dijit.byId("matchReadInfo_country"),
	   					                               dijit.byId("validatable_matchReadInfo_city"),
	   					                                [
	   					                                     "free_text_matchReadInfo_city",
	   					                                     "free_text_matchReadInfo_zip"
	   					                                ],
	   					                                [
	   					                                     "validatable_matchReadInfo_zip"
	   					                                ]);
	   					   })
   		               });
		      </script>
		      <s:if test="stateCode == null && task.claim.matchReadInfo.ownerState != null">
		      	<s:textfield id="free_text_matchReadInfo_state" name="stateCode" value="%{task.claim.matchReadInfo.ownerState}"/>
		      </s:if>
		      <s:else>
		      	<s:textfield id="free_text_matchReadInfo_state" name="stateCode"/>
		      </s:else>
            </td>
         </tr>
         
         <tr>
            <td>
                <label id="cityLabel" class="">
                    <s:text name="label.common.city"/>:
                </label>
            </td>
            <td colspan="3" id="cityField">
              <sd:autocompleter label='City' id='validatable_matchReadInfo_city' listenTopics='topic_matchReadInfo_city' name='task.claim.matchReadInfo.ownerCity' required='true' />
		     <script type="text/javascript">
   				  	  dojo.addOnLoad(function() {
	   					    dojo.connect(dijit.byId("validatable_matchReadInfo_city"),"onChange",function(value) {
	   					    getZipsByCountryStateAndCity(value, 
	   					                                 dijit.byId("matchReadInfo_country"),
	   					                                 dijit.byId("validatable_matchReadInfo_state"),
	   					                                 [
   					                                          "free_text_matchReadInfo_zip"
   					                                     ],
   					                                     [
   					                                         "validatable_matchReadInfo_zip"
   					                                     ]); 
	   					  })
   		          });
		      </script>
		      <s:if test="cityCode == null && task.claim.matchReadInfo.ownerCity != null">
		      	<s:textfield id="free_text_matchReadInfo_city" name="cityCode" value="%{task.claim.matchReadInfo.ownerCity}"/>
		      </s:if>
		      <s:else>
		      	<s:textfield id="free_text_matchReadInfo_city" name="cityCode"/> 
		      </s:else>             
            </td>

        </tr>
        
        <tr>
            <td>
                <label id="zipcodeLabel" class="">
                    <s:text name="label.common.zipCode"/>:
                </label>
            </td>
            <td id="zipcodeField">
                <sd:autocompleter label='Zip' id='validatable_matchReadInfo_zip' listenTopics='topic_matchReadInfo_zip' name='task.claim.matchReadInfo.ownerZipcode' />   
			     
			    
			     <s:if test="zipCode == null && task.claim.matchReadInfo.ownerZipcode != null">
		      		<s:textfield id="free_text_matchReadInfo_zip" name="zipCode" value="%{task.claim.matchReadInfo.ownerZipcode}"/>
		      	</s:if>
		        <s:else>
		      		<s:textfield id="free_text_matchReadInfo_zip" name="zipCode"/> 
		        </s:else>                          
            </td>
        </tr>
        
  
        <tr>
            <td id="submitSection" colspan="4" align="center" class="buttons" style="padding-top: 20px;">
            	<input type="button" id="closePopup"  value='<s:text name="button.common.continue"/>'/>
            </td>
        </tr>
        </tbody>
    </table>
    
      <jsp:include flush="true" page="claimOwnerInfo.jsp" />

</div>
</div>
</div>
</div>
