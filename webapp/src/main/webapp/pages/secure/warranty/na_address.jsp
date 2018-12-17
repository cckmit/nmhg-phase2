
<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
</script>
<script type="text/javascript" src="scripts/pushCustomerDetails.js"></script>
    <script type="text/javascript" src="scripts/validateAddress.js"></script>
<div id="companyAddressDiv" style="width: 100%">
<div id='NewCompanyAddress'>
<div class="mainTitle" style="margin-top:10px;">New Address</div>
<div class="borderTable">&nbsp;</div>

<script type="text/javascript">
dojo.addOnLoad(function() {
		<s:if test="serviceProvider==null || serviceProvider.id==null || #newIndex>0">
			 var validatableStateId = dijit.byId("validatable_state_company_new");
			 var freeTextStateId = dojo.byId("free_text_state_company_new");
			 var validatableCityId = dijit.byId("validatable_city_company_new");
			 var freeTextCityId = dojo.byId("free_text_city_company_new");
			 var validatableZipId = dijit.byId("validatable_zip_company_new");
			 var freeTextZipId = dojo.byId("free_text_zip_company_new");
			 var selectedVar = "<s:property value="checkForValidatableCountry(serviceProvider.orgAddresses[#newIndex].country)"/>";
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
			 dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextCityId,!selectedVar);
			 dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextZipId,!selectedVar);
		</s:if>
   });
</script>	
<table  cellpadding="0" cellspacing="0" id="indiv-addr" style="margin-top:-10px;width:98%">	
	
	<tr>	
		<td width="16%" class="labelStyle"><s:text name="label.companyName" /></td>
		<td width="40%" colspan="3"><s:hidden name = "addressBookType" value = "NATIONALACCOUNT" /><sd:autocompleter id='nationalAccountNameAutoCompleter' showDownArrow='false' href='list_nationalAccountNames.action'
			name='serviceProvider' value="%{serviceProvider.name}" key="{%serviceProvider.id%}" keyValue="{%serviceProvider.id%}"
			keyName="serviceProvider" /></td>
	</tr>
<tr><td colspan="4">&nbsp;</td></tr>
	
	<tr>
	    <td class="non_editable labelStyle"><s:text name="label.common.address.line1" />:</td>
		<td colspan="3"><s:textfield size="60" name="orgAddresses.addressLine1" /></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
	    <td  class="labelStyle"><s:text name="label.common.address.line2" />:</td>
		<td colspan="3"><s:textfield size="60" name="orgAddresses.addressLine2" /></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
	    <td  class="labelStyle"><s:text name="label.common.address.line3" />:</td>
		<td colspan="3"><s:textfield size="60" name="orgAddresses.addressLine3" /></td>
	</tr>
<tr><td colspan="4">&nbsp;</td></tr>
	 <tr>
		<td class="non_editable labelStyle"><s:text name="label.country" /></td>
		<s:hidden id="locale_country" value="%{getLoggedInUser().getLocale().getCountry()}"/>
		<td>  <s:select label ="Country" id="country_company_new"
                    name="orgAddresses.country"
                    list="countryList" required="true" theme="twms"/>                  
               <script type="text/javascript">
				  	  dojo.addOnLoad(function() {	
				  	  		  var countryName = "<s:property value="serviceProvider.orgAddresses[#newIndex].country"/>";				  	  		 				  	  		  	
				  	  		  if(countryName == null || countryName == "")
				  	  		  {				  	  		 					  	  		  	  		  	  			
					  	           <s:if test="(serviceProvider== null || serviceProvider.id == null )">
					  	           	 var country = dojo.byId("locale_country").value;
					  	           	 dijit.byId("country_company_new").setValue(country);
					  	           </s:if>
				  	          }	              
							  dojo.connect(dijit.byId("country_company_new"),"onChange", function(value) {
							  	getStatesByCountry(value, dijit.byId("validatable_state_company_new"),
							  	                          true,
							  	                          -1,
							  	                          [
							  	                             "free_text_state_company_new",
							  	                             "free_text_city_company_new",
							  	                             "free_text_zip_company_new"
						  	                              ],
						  	                              [
							  	                             "validatable_city_company_new",
							  	                             "validatable_zip_company_new"
						  	                              ]
						  	                              );
							  })
		             });
		       </script>
	    </td>
	    <td class="non_editable labelStyle"><s:text name="label.state" /></td>
		<td>      
	        <sd:autocompleter label='State' id='validatable_state_company_new' listenTopics='topic_state_company_new' name='orgAddresses.state' />
              <script type="text/javascript">
   				       dojo.addOnLoad(function() {
	   					  dojo.connect(dijit.byId("validatable_state_company_new"),"onChange",function(value) {
	   					    getCitiesByCountryAndState(value, dijit.byId("validatable_city_company_new"),
	   					                                      true,
	   					                                      '-1',
	   					                                      [
	   					                                       "free_text_city_company_new",
	   					                                       "free_text_zip_company_new"
	   					                                      ],
	   					                                      [
	   					                                       "validatable_zip_company_new"
	   					                                      ]);
	   					  })
   		          });
		      </script>     
	       <s:textfield id="free_text_state_company_new" name="states[%{#newIndex}]" /> 
		</td> 	
	</tr>    
	<tr><td colspan="4">&nbsp;</td></tr>
   <tr>
		<td class="non_editable labelStyle"><s:text name="label.city" /></td>
		<td>
		    <sd:autocompleter label='City' id='validatable_city_company_new' listenTopics='topic_city_company_new' name='orgAddresses.city' />          
		      <script type="text/javascript">
   				  dojo.addOnLoad(function() {
   					    dojo.connect(dijit.byId("validatable_city_company_new"),"onChange",function(value) {
   					    getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_company_new"),
   					                                        true, '-1',
   					                                        [
   					                                          "free_text_zip_company_new"
   					                                        ]); 
   					       })
   		          });
		      </script>           
	        <s:textfield id="free_text_city_company_new" name="cities[%{#newIndex}]" />      
		</td>
		<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td>
			   <sd:autocompleter label='Zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='orgAddresses.zipCode' />        
		      <s:textfield id="free_text_zip_company_new" name="zips[%{#newIndex}]" />      
		    </td>
   </tr>
   <tr><td colspan="4">&nbsp;</td></tr>
   <tr>
		<td class="non_editable labelStyle"><s:text name="label.county" /></td>
		<td><s:textfield name="orgAddresses.phone" /></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
		<td><s:textfield name="orgAddresses.phone" /></td>

		<td class="non_editable labelStyle"><s:text name="label.email" /></td>
		<td><s:textfield maxlength="255" name="orgAddresses.email" /></td>
	</tr>
<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
		<td><s:textfield
			name="orgAddresses.fax" /></td>
		<s:if test="%{showCustomerCompanyWebSite()}">
		<td class="non_editable labelStyle"><s:text name="label.customerCompanyWebsite" /></td>
		<td><s:textfield
			name="orgAddresses.customerCompanyWebsite" /></td>
		</s:if>		
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
</table></div>



<script>
		dojo.addOnLoad(function() {	
			// For new Address
			var newCompanyAddress = dojo.byId('NewCompanyAddress');
			<s:if test="serviceProvider!=null && serviceProvider.id !=null">
				dojo.dom.removeNode(newCompanyAddress);
			</s:if>
		});
	</script></div>

<table width="400px;"><tr><td>
 <div class="buttonWrapperPrimary" style="margin-top:30px;display: table-cell;"><input class="buttonGeneric"
	type="Button" value="Save" style="height:21px; margin-right:5px; overflow:visible; padding-left:5px; padding-right:5px !important; vertical-align:middle;"  
	id='company_save_btn'/></div>
 <div id="submit" align="center" style="display: table-cell;height:21px; margin-right:5px; overflow:visible; padding-left:5px; padding-right:5px !important; vertical-align:middle;"><s:hidden name="hideSelect"
			id="hideSelect" /> <input id="cancel_btn" class="buttonGeneric"
			type="button" value="<s:text name='label.common.cancel'/>" /></div>	
		<script>
			dojo.addOnLoad(function() {	
				dojo.connect(dojo.byId('company_save_btn'), "onclick", function() {
						
			               dojo.byId("updateNAForm").submit();
		                
				});

			});
		</script></td></tr></table>

