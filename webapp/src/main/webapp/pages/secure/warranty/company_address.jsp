
<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="t" uri="twms"%>
<script type="text/javascript" src="scripts/validateAddress.js"></script>
<script type="text/javascript" src="scripts/pushCustomerDetails.js"></script>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
</script>

<div id="companyAddressDiv" style="width: 100%">
	<div id='NewCompanyAddress'>
		<div class="mainTitle" style="margin-top: 10px;">New Address</div>
		<div class="borderTable">&nbsp;</div>
		<s:set name="newIndex"
			value="%{customer !=null && customer.id !=null ? customer.addresses.size(): 0}" />
		<script type="text/javascript">
dojo.addOnLoad(function() {
		<s:if test="customer==null || customer.id==null || #newIndex>0">
			 var validatableStateId = dijit.byId("validatable_state_company_new");
			 var freeTextStateId = dojo.byId("free_text_state_company_new");
			 var validatableCityId = dijit.byId("validatable_city_company_new");
			 var freeTextCityId = dojo.byId("free_text_city_company_new");
			 var validatableZipId = dijit.byId("validatable_zip_company_new");
			 var freeTextZipId = dojo.byId("free_text_zip_company_new");			 
			 var selectedVar = "<s:property value="checkForValidatableCountry(customer.addresses[#newIndex].country)"/>";
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
			 dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextCityId,!selectedVar);
			 dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextZipId,!selectedVar);
			 if(dojo.byId("BUFlagForCounty").value == "true"){
				 var validatableCountyId = dijit.byId("validatable_county_company_new");
				 var freeTextCountyId = dojo.byId("free_text_county_company_new");
				 dojo.html.setDisplay(validatableCountyId.domNode,selectedVar);
				 dojo.html.setDisplay(freeTextCountyId,!selectedVar);
			 }
		</s:if>
   });
</script>

		<table cellpadding="0" cellspacing="0" id="indiv-addr"
			style="margin-top: -10px; width: 98%">

			<s:if test="#newIndex > 0">
				<s:set name="disableAddressBookType" value="true" />
				<s:set name="existingAddressBookType"
					value="addressBookAddressMappings[0].addressBook.type" />
			</s:if>
			<s:else>
				<s:set name="disableAddressBookType" value="false" />
			</s:else>
			<tr>
				<td class="labelStyle"><s:text name="label.contactPersonName" /></td>
				<td><s:textfield
						name="customer.addresses[%{#newIndex}].contactPersonName" /></td>

				<td class="labelStyle"><s:text
						name="label.customerContactTitle" /></td>
				<td><s:textfield
						name="customer.addresses[%{#newIndex}].customerContactTitle" /></td>
			</tr>


			<s:if test="loggedInUserADealer">
				<tr>
					<s:if test="customer.id !=null">
						<td class="labelStyle"><s:text name="label.contactPersonName" /></td>
						<td><s:textfield
								name="customer.addresses[%{#newIndex}].contactPersonName" /></td>

						<td class="labelStyle"><s:text
								name="label.customerContactTitle" /></td>
						<td><s:textfield
								name="customer.addresses[%{#newIndex}].customerContactTitle" /></td>

					</s:if>
				</tr>
				<tr>
					<td width="16%" class="labelStyle"><s:text
							name="label.common.addressBookType" /></td>
					<td width="40%"><s:select label="Address Book Type"
							id="addressBookType_dealer_company_new"
							name="addressBookAddressMappings[%{#newIndex}].addressBook.type"
							list="addressBookTypesForDealer" required="true" theme="twms" />
						<script type="text/javascript">
			           dojo.addOnLoad(function() {
                               var addressBookTypeId = "addressBookType_dealer_company_new"; 
						       var addressBookTypeCombo = dijit.byId(addressBookTypeId);
                               <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].addressBook.type !=null">
						  		  addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].addressBook.type"/>");
			                   </s:if>
			                   addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);	
			                   var defaultAddressBookType = dijit.byId("addressBookType_dealer_company_new");
                               dojo.connect(defaultAddressBookType,"onChange",function(){
                            	   console.debug("AddressBookType:"+defaultAddressBookType.value);
                             
                               });
					    });
			        </script></td>
				</tr>
			</s:if>

			<s:if test="!loggedInUserADealer">
				<s:if test="customer.id !=null">
					<tr>
						<td class="labelStyle"><s:text name="label.contactPersonName" /></td>
						<td><s:textfield
								name="customer.addresses[%{#newIndex}].contactPersonName" /></td>

						<td class="labelStyle"><s:text
								name="label.customerContactTitle" /></td>
						<td><s:textfield
								name="customer.addresses[%{#newIndex}].customerContactTitle" /></td>
					</tr>
				</s:if>
				<tr>
					<td width="16%" class="labelStyle"><s:text
							name="label.common.addressBookType" /></td>


					<td width="40%"><s:select label="Address Book Type"
							id="addressBookType_not_dealer_company_new"
							name="addressBookAddressMappings[%{#newIndex}].addressBook.type"
							list="addressBookTypesForCompany" required="true" theme="twms" />
						<script type="text/javascript">
			           dojo.addOnLoad(function() {
                               var addressBookTypeId = "addressBookType_not_dealer_company_new"; 
						       var addressBookTypeCombo = dijit.byId(addressBookTypeId);
                               <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].addressBook.type !=null">
						  		  addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].addressBook.type"/>");
			                   </s:if>
			                   addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);	
			            
                               dojo.connect(addressBookTypeCombo,"onChange",function(){
                            	   
                             if(addressBookTypeCombo.value == "NATIONALACCOUNT"){
                            	  var form = dojo.byId("createCustomerForm");
                                  form.action = "national_account.action";
                                  form.submit();
                              }	
                               });
					    });
			        </script></td>
				</tr>
			</s:if>



			<tr>
				<td width="16%" class="labelStyle"><s:text
						name="label.addressType" /></td>
				<td width="40%"><s:select label="Address Type"
						id="addressType_company_new"
						name="addressBookAddressMappings[%{#newIndex}].type"
						list="addressTypesCompany" required="true" theme="twms" /> <script
						type="text/javascript">
			          dojo.addOnLoad(function() {
                              var addressTypeId = "addressType_company_new"; 
						      var addressTypeCombo = dijit.byId(addressTypeId);
                              <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].type !=null">
						  		 addressTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].type"/>");
			                  </s:if> 			              
					});
			 </script></td>

				<td width="16%" class="labelStyle"><s:text
						name="label.markAsPrimary" /></td>
				<td><s:checkbox id="primaryAddressIndex"
						name="addressBookAddressMappings[%{#newIndex}].primary">
					</s:checkbox></td>
			</tr>


			<tr>
				<td class="non_editable labelStyle"><s:text
						name="label.common.address.line1" />:</td>
				<td colspan="3"><s:textfield size="60"
						name="customer.addresses[%{#newIndex}].addressLine1" /></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text
						name="label.common.address.line2" />:</td>
				<td colspan="3"><s:textfield size="60"
						name="customer.addresses[%{#newIndex}].addressLine2" /></td>
			</tr>
			<tr>
				<td class="labelStyle"><s:text
						name="label.common.address.line3" />:</td>
				<td colspan="3"><s:textfield size="60"
						name="customer.addresses[%{#newIndex}].addressLine3" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle"><s:text
						name="label.country" /></td>
				<s:hidden id="locale_country"
					value="%{getLoggedInUser().getLocale().getCountry()}" />
				<td><s:select label="Country" id="country_company_new"
						name="customer.addresses[%{#newIndex}].country" list="countryList"
						required="true" theme="twms" /> <script type="text/javascript">
				  	  dojo.addOnLoad(function() {	
				  	  		  var countryName = "<s:property value="customer.addresses[#newIndex].country"/>";				  	  		 				  	  		  	
				  	  		  if(countryName == null || countryName == "")
				  	  		  {				  	  		 					  	  		  	  		  	  			
					  	           <s:if test="(customer== null || customer.id == null )">
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
							  	                             "free_text_zip_company_new",
							  	                             "free_text_county_company_new"
						  	                              ],
						  	                              [
							  	                             "validatable_city_company_new",
							  	                             "validatable_zip_company_new",
							  	                             "validatable_county_company_new"
						  	                              ]
						  	                              );
							  })
		             });
		       </script></td>
				<td class="non_editable labelStyle"><s:text name="label.state" /></td>
				<td><sd:autocompleter label='State'
						id='validatable_state_company_new'
						listenTopics='topic_state_company_new' name='customer.addresses[%{#newIndex}].state' />
					<script type="text/javascript">
   				       dojo.addOnLoad(function() {
	   					  dojo.connect(dijit.byId("validatable_state_company_new"),"onChange",function(value) {
	   					    getCitiesByCountryAndState(value, dijit.byId("validatable_city_company_new"),
	   					                                      true,
	   					                                      '-1',
	   					                                      [
	   					                                       "free_text_city_company_new",
	   					                                       "free_text_zip_company_new",
	   					                                       "free_text_county_company_new"
	   					                                      ],
	   					                                      [
	   					                                       "validatable_zip_company_new",
	   					                                       "validatable_county_company_new"
	   					                                      ]);
	   					  })
   		          });
		      </script> <s:textfield id="free_text_state_company_new"
						name="states[%{#newIndex}]" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle"><s:text name="label.city" /></td>
				<td><sd:autocompleter label='City'
						id='validatable_city_company_new'
						listenTopics='topic_city_company_new'
						name='customer.addresses[%{#newIndex}].city' /> <script
						type="text/javascript">
   				  dojo.addOnLoad(function() {
   					    dojo.connect(dijit.byId("validatable_city_company_new"),"onChange",function(value) {
   					    getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_company_new"),
   					                                        true, '-1',
   					                                        [
   					                                          "free_text_zip_company_new",
   					                                          "free_text_county_company_new"
   					                                        ],
   					                                        [
   					                                          "validatable_county_company_new"
   					                                        ]); 
   					       })
   		          });
		      </script> <s:textfield id="free_text_city_company_new"
						name="cities[%{#newIndex}]" /></td>
			<s:hidden id="BUFlagForCounty" value="%{isDisplayCountyOnCustomerPage()}"/>	
			<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			
			<s:if test="isDisplayCountyOnCustomerPage()">
			<td><sd:autocompleter label='Zip'
						id='validatable_zip_company_new'
						listenTopics='topic_zip_company_new'
						name='customer.addresses[%{#newIndex}].zipCode' /> <script
						type="text/javascript">
   				  dojo.addOnLoad(function() {
   					    dojo.connect(dijit.byId("validatable_zip_company_new"),"onChange",function(value) {
   					    
   					    	getCountiesByCountryStateAndZip(value, dijit.byId("validatable_county_company_new"),
   					                                        true, '-1',
   					                                        [
   					                                          "free_text_county_company_new"
   					                                        ]); 
   					     
   					       })
   		          });
		      </script> <s:textfield id="free_text_zip_company_new"
						name="zips[%{#newIndex}]" /></td>
			  </s:if>
			  <s:else>		
			  <td>
				   <sd:autocompleter label='Zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='customer.addresses[%{#newIndex}].zipCode' />        
			       <s:textfield id="free_text_zip_company_new" name="zips[%{#newIndex}]" />      
		      </td>
		      </s:else>
			</tr>
			
			<tr>
			<s:if test="isDisplayCountyOnCustomerPage()">
				<td class="non_editable labelStyle"><s:text name="label.county" /></td>
				<td><sd:autocompleter label='County'
						id='validatable_county_company_new'
						listenTopics='topic_county_company_new'
						name='customer.addresses[%{#newIndex}].countyCodeWithName'/>
						 <s:textfield
						id="free_text_county_company_new" name="counties[%{#newIndex}]" /></td>
			</s:if>
			</tr>

			<tr>
				<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
				<td><s:textfield name="customer.addresses[%{#newIndex}].phone" /></td>

				<td class="non_editable labelStyle"><s:text name="label.email" /></td>
				<td><s:textfield maxlength="255"
						name="customer.addresses[%{#newIndex}].email" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
				<td><s:textfield name="customer.addresses[%{#newIndex}].fax" />
				</td>
				<s:if test="%{showCustomerCompanyWebSite()}">
					<td class="non_editable labelStyle"><s:text
							name="label.customerCompanyWebsite" /></td>
					<td><s:textfield
							name="customer.addresses[%{#newIndex}].customerCompanyWebsite" /></td>
				</s:if>
			</tr>
		</table>

		<%--<div class="buttonWrapperPrimary"><s:if
	test="!hideSelect">
<input id='company_remove_btn' class="buttonGeneric" type="Button"
	style="height:21px; margin-right:5px; overflow:visible; padding-left:5px; padding-right:5px !important; vertical-align:middle;" 
	value="Remove" /></s:if></div>--%>

		<script>
		dojo.addOnLoad(function() {	
			// For new Address
			var newCompanyAddress = dojo.byId('NewCompanyAddress');
			<s:if test="customer!=null && customer.id !=null">
				dojo.dom.removeNode(newCompanyAddress);
			</s:if>
		});
	</script>
	</div>

	<s:if test="customer!= null && customer.id!=null">
		<s:iterator value="customer.addresses" status="customerStatus">
			<div id='Address_company<s:property value="id"/>' class="mainTitle"
				style="margin-top: 10px;">
				<s:hidden value='%{addressLine1}' />
				<s:hidden value='%{city}' />
				<s:hidden value='%{state}' />
				<s:hidden value='%{country}' />
				<s:hidden value='%{zipCode}' />
				<s:hidden value='%{county}' />
			</div>
			<s:hidden
				name="addressBookAddressMappings[%{#customerStatus.index}].id" />
			<s:hidden
				name="addressBookAddressMappings[%{#customerStatus.index}].address.id" />
			<s:hidden
				name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.id" />
			<s:hidden
				name="addressBookAddressMappings[%{#customerStatus.index}].version" />


			<script type="text/javascript">
	      dojo.addOnLoad(function() {
				 var validatableStateId = dijit.byId("validatable_state_company_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextStateId = dojo.byId("free_text_state_company_" + <s:property value = "#customerStatus.index"/>);
				 var validatableCityId = dijit.byId("validatable_city_company_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextCityId = dojo.byId("free_text_city_company_" + <s:property value = "#customerStatus.index"/>);
				 var validatableZipId = dijit.byId("validatable_zip_company_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextZipId = dojo.byId("free_text_zip_company_" + <s:property value = "#customerStatus.index"/>);
				 var selectedVar = "<s:property value="checkForValidatableCountry(customer.addresses[#customerStatus.index].country)"/>";
			 	 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextStateId,!selectedVar);
			     dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextCityId,!selectedVar);
			     dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextZipId,!selectedVar);
			     
			     /* if(dojo.byId("BUFlagForCounty").value == "true"){
			    	var validatableCountyId = dijit.byId("validatable_county_company_" + <s:property value = "#customerStatus.index"/>);
			    	var freeTextCountyId = dojo.byId("free_text_county_company_" + <s:property value = "#customerStatus.index"/>);
			     	dojo.html.setDisplay(validatableCountyId.domNode,selectedVar);
				 	dojo.html.setDisplay(freeTextCountyId,!selectedVar);
			     } */
	     });
     </script>
			<div class="borderTable">&nbsp;</div>
			<table width="96%" cellpadding="0" cellspacing="0" id="indiv-addr">


				<s:if test="loggedInUserADealer">
					<tr>
						<td class="labelStyle"><s:text name="label.contactPersonName" /></td>
						<td><s:textfield
								name="customer.addresses[%{#customerStatus.index}].contactPersonName" /></td>

						<td class="labelStyle"><s:text
								name="label.customerContactTitle" /></td>
						<td><s:textfield
								name="customer.addresses[%{#customerStatus.index}].customerContactTitle" /></td>
					</tr>
					<tr>
						<td width="16%" class="non_editable labelStyle"><s:text
								name="label.addressBookType" /></td>
						<td width="16%"><s:select label="Address Book Type"
								id="addressBookType_dealer_company_%{#customerStatus.index}"
								name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.type"
								list="addressBookTypesForDealer" required="true" theme="twms" />
							<script type="text/javascript">
						  	dojo.addOnLoad(function() {
                                   var addressBookTypeId = "addressBookType_dealer_company_" + <s:property value = "#customerStatus.index"/>
					  	           var addressBookTypeCombo = dijit.byId(addressBookTypeId);
					  	           dojo.byId(addressBookTypeId).value="<s:property value="addressBookAddressMappings[#customerStatus.index].addressBook.type"/>";
					  	           addressBookTypeCombo.valueNode.attributes['value'].nodeValue="<s:property value="addressBookAddressMappings[#customerStatus.index].addressBook.type"/>";
						  		   addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);								              
				            });
			            </script></td>
					</tr>
				</s:if>

				<s:if test="!loggedInUserADealer">
					<tr>
						<td class="labelStyle"><s:text name="label.contactPersonName" /></td>
						<td><s:textfield
								name="customer.addresses[%{#customerStatus.index}].contactPersonName" /></td>

						<td class="labelStyle"><s:text
								name="label.customerContactTitle" /></td>
						<td><s:textfield
								name="customer.addresses[%{#customerStatus.index}].customerContactTitle" /></td>
					</tr>

					<tr>
						<td width="16%" class="non_editable labelStyle"><s:text
								name="label.addressBookType" /></td>

						<td width="16%"><s:select label="Address Book Type"
								id="addressBookType_not_dealer_company_%{#customerStatus.index}"
								name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.type"
								list="addressBookTypesForCompany" required="true" theme="twms" />
							<script type="text/javascript">
						  	dojo.addOnLoad(function() {
                                    var addressBookTypeId = "addressBookType_not_dealer_company_" + <s:property value = "#customerStatus.index"/>
						  	        var addressBookTypeCombo = dijit.byId(addressBookTypeId);
							  		addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#customerStatus.index].addressBook.type"/>");
							  		addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);	
				          });
			            </script></td>
					</tr>
				</s:if>


				<tr>
					<td width="16%" class="non_editable labelStyle"><s:text
							name="label.addressType" /></td>
					<td width="40%"><s:select label="Address Type"
							id="addressType_company_%{#customerStatus.index}"
							name="addressBookAddressMappings[%{#customerStatus.index}].type"
							list="addressTypesCompany" required="true" theme="twms" /> <script
							type="text/javascript">
						  dojo.addOnLoad(function() {
                                    var addressTypeId = "addressType_company_" + <s:property value = "#customerStatus.index"/>
						  	        var addressTypeCombo = dijit.byId(addressTypeId);
							  		addressTypeCombo.setValue("<s:property value="addressBookAddressMappings[#customerStatus.index].type"/>");									          
				           });
			        </script></td>
					
					<td width="16%" class="non_editable labelStyle"><s:text
							name="label.markAsPrimary" /></td>
					<td><s:checkbox
							name="addressBookAddressMappings[%{#customerStatus.index}].primary">
						</s:checkbox></td>
				</tr>

				<tr>
					<td class="non_editable labelStyle"><s:text
							name="label.common.address.line1" />:</td>
					<td colspan="3"><s:textfield size="60"
							name="customer.addresses[%{#customerStatus.index}].addressLine1" />
						<s:hidden id="company_travel_location_%{#customerStatus.index}"
							name="company_travel_location_%{#customerStatus.index}"
							value="%{customer.addresses[#customerStatus.index].locationForGoogleMap}" />
					</td>
				</tr>
				<tr>
					<td class="non_editable labelStyle"><s:text
							name="label.common.address.line2" />:</td>
					<td colspan="3"><s:textfield size="60"
							name="customer.addresses[%{#customerStatus.index}].addressLine2" />
					</td>
				</tr>
				<tr>
					<td class="non_editable labelStyle"><s:text
							name="label.common.address.line3" />:</td>
					<td colspan="3"><s:textfield size="60"
							name="customer.addresses[%{#customerStatus.index}].addressLine3" />
					</td>
				</tr>
				<tr>
					<td class="non_editable labelStyle"><s:text
							name="label.country" /></td>
					<td><s:select label="Country"
							id="country_company_%{#customerStatus.index}"
							name="customer.addresses[%{#customerStatus.index}].country"
							list="countryList" required="true" theme="twms" /> <script
							type="text/javascript">
							  dojo.addOnLoad(function() {
							    
							  	      var countryName = "<s:property value="customer.addresses[%{#customerStatus.index}].country"/>";
				  	  		  	
						  	  		  if(countryName == null || countryName == "")
						  	  		  {						  	  		  	  		  	  			
							  	           <s:if test="(customer== null || customer.id == null )">					  	          	
							  	             dijit.byId("country_company_new").setValue("US");
							  	           </s:if>
						  	          }	 		
									  dojo.connect(dijit.byId("country_company_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
									  getStatesByCountry(value, dijit.byId("validatable_state_company_" + <s:property value = "#customerStatus.index"/>),
									                              false,
									                              <s:property value = "#customerStatus.index"/>,
									  	                          [
									  	                             "free_text_state_company_" + <s:property value = "#customerStatus.index"/>,
									  	                             "free_text_city_company_" + <s:property value = "#customerStatus.index"/>,
									  	                             "free_text_zip_company_" + <s:property value = "#customerStatus.index"/>,
									  	                             "free_text_county_company_"+<s:property value = "#customerStatus.index"/>
									  	                          ],
									  	                          [
									  	                             "validatable_city_company_" + <s:property value = "#customerStatus.index"/>,
									  	                             "validatable_zip_company_" + <s:property value = "#customerStatus.index"/>,
									  	                             "validatable_county_company_"+<s:property value = "#customerStatus.index"/>
									  	                          ]);   
									  })
					          });
					       </script></td>
					<td class="non_editable labelStyle"><s:text name="label.state" /></td>
					<td><sd:autocompleter label='State'
							id='validatable_state_company_%{#customerStatus.index}'
							listenTopics='topic_state_company_%{#customerStatus.index}'
							name='customer.addresses[%{#customerStatus.index}].state' /> <script
							type="text/javascript">
				   				  dojo.addOnLoad(function() {
					   					    dojo.connect(dijit.byId("validatable_state_company_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
					   					    getCitiesByCountryAndState(value, dijit.byId("validatable_city_company_" + <s:property value = "#customerStatus.index"/>),
					   					                                      false,<s:property value = "#customerStatus.index"/>,
					   					                                       [
									  	                                         "free_text_city_company_" + <s:property value = "#customerStatus.index"/>,
									  	                                         "free_text_zip_company_" + <s:property value = "#customerStatus.index"/>,
									  	                                         "free_text_county_company_"+<s:property value = "#customerStatus.index"/>
									  	                                      ],
									  	                                      [
												  	                             "validatable_zip_company_" + <s:property value = "#customerStatus.index"/>,
												  	                             "validatable_county_company_"+<s:property value = "#customerStatus.index"/>
									  	                                      ]);   
					   					    })
				   		          });
						      </script> <s:textfield
							id="free_text_state_company_%{#customerStatus.index}"
							name="states[%{#customerStatus.index}]" /></td>
				</tr>

				<tr>
					<td class="non_editable labelStyle"><s:text name="label.city" /></td>
					<td><sd:autocompleter label='City'
							id='validatable_city_company_%{#customerStatus.index}'
							listenTopics='topic_city_company_%{#customerStatus.index}'
							name='customer.addresses[%{#customerStatus.index}].city' /> <script
							type="text/javascript">
				   				  dojo.addOnLoad(function() {
					   					    dojo.connect(dijit.byId("validatable_city_company_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
					   					    getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_company_" + <s:property value = "#customerStatus.index"/>),
					   					                                      false,<s:property value = "#customerStatus.index"/>,
					   					                                      [
   					                                                           "free_text_zip_company_"+<s:property value = "#customerStatus.index"/>,
   					                                                           "free_text_county_company_"+<s:property value = "#customerStatus.index"/>
   					                                                           ],
   					                                                       	  [
											  	                              "validatable_county_company_"+<s:property value = "#customerStatus.index"/>
   					                                                          ]); 
					   					   })
				   		          });
						      </script> <s:textfield
							id="free_text_city_company_%{#customerStatus.index}"
							name="cities[%{#customerStatus.index}]" /></td>
				</tr>

				<tr>
					<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
					<s:if test="isDisplayCountyOnCustomerPage()">
					<td><sd:autocompleter label='Zip'
							id='validatable_zip_company_%{#customerStatus.index}'
							listenTopics='topic_zip_company_%{#customerStatus.index}'
							name='customer.addresses[%{#customerStatus.index}].zipCode' /> <script
							type="text/javascript">
				   				  dojo.addOnLoad(function() {
					   					    dojo.connect(dijit.byId("validatable_zip_company_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
					   					    
					   					    	getCountiesByCountryStateAndZip(value, dijit.byId("validatable_county_company_" + <s:property value = "#customerStatus.index"/>),
					   					                                      false,<s:property value = "#customerStatus.index"/>,
					   					                                      [
   					                                                           "free_text_county_company_"+<s:property value = "#customerStatus.index"/>
   					                                                          ]); 
					   					     
					   					     })
				   		          });
						      </script> <s:textfield
							id="free_text_zip_company_%{#customerStatus.index}"
							name="zips[%{#customerStatus.index}]" /></td>
					</s:if>
					<s:else>
						<td>
						  <sd:autocompleter label='Zip' id='validatable_zip_company_%{#customerStatus.index}' listenTopics='topic_zip_company_%{#customerStatus.index}' name='customer.addresses[%{#customerStatus.index}].zipCode' />
					      <s:textfield id="free_text_zip_company_%{#customerStatus.index}" name="zips[%{#customerStatus.index}]" />      
						</td>
					</s:else>

				</tr>
				<s:if test="isDisplayCountyOnCustomerPage()">
				<tr>
					<td class="non_editable labelStyle"><s:text
							name="label.county" /></td>
					<td><sd:autocompleter label='County'
							id='validatable_county_company_%{#customerStatus.index}'
							listenTopics='topic_county_company_%{#customerStatus.index}'
							name='customer.addresses[%{#customerStatus.index}].countyCodeWithName' /> <s:textfield
							id="free_text_county_company_%{#customerStatus.index}"
							name="counties[%{#customerStatus.index}]" /></td>
				</tr>
				</s:if>
				
				<tr>
					<td class="non_editable labelStyle"><s:text name="label.phone" /></td>
					<td><s:textfield
							name="customer.addresses[%{#customerStatus.index}].phone" /></td>

					<td class="non_editable labelStyle"><s:text name="label.email" /></td>
					<td><s:textfield maxlength="255"
							name="customer.addresses[%{#customerStatus.index}].email" /></td>
				</tr>
				<tr>
					<td class="non_editable labelStyle"><s:text name="label.fax" /></td>
					<td><s:textfield
							name="customer.addresses[%{#customerStatus.index}].fax" /></td>
				</tr>
			</table>
</div>
</s:iterator>
</s:if>
<div class="buttonWrapperPrimary" style="margin-top: 30px">
	<input class="buttonGeneric" type="Button" value="Save"
		style="height: 21px; margin-right: 5px; overflow: visible; padding-left: 5px; padding-right: 5px !important; vertical-align: middle;"
		id='company_save_btn' />
</div>
<div id="submit" align="center">
	<s:hidden name="hideSelect" id="hideSelect" />
	<input id="cancel_btn" class="buttonGeneric" type="button"
		value="<s:text name='label.common.cancel'/>" />
</div>
<script>
			dojo.addOnLoad(function() {	
				dojo.connect(dojo.byId('company_save_btn'), "onclick", function() {
						<s:if test="customer==null || customer.id==null">
			               dojo.byId("createCustomerForm").submit();
			               
		                </s:if>
		                <s:else>
		                   dojo.byId("updateCustomerForm").submit();
		                </s:else>
				});

			});
		</script>
