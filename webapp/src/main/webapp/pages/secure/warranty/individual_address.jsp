<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="t" uri="twms" %>
 <script type="text/javascript" src="scripts/validateAddress.js"></script>
 <script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
</script>

<div id="addressDiv" style="width: 100%">
<div id="NewAddress">
<div dojoType="twms.widget.TitlePane" labelNodeClass="collapseAdmin"
	containerNodeClass="section_div" title="New Address"><s:set
	name="newIndex"
	value="%{customer !=null && customer.id !=null ? customer.addresses.size(): 0}" />
<script type="text/javascript">
dojo.addOnLoad(function() {
		<s:if test="customer==null || customer.id==null || #newIndex>0">
			 var validatableStateId = dijit.byId("validatable_state_individual_new");
			 var freeTextStateId = dojo.byId("free_text_state_individual_new");
			 var validatableCityId = dijit.byId("validatable_city_individual_new");
			 var freeTextCityId = dojo.byId("free_text_city_individual_new");
			 var validatableZipId = dijit.byId("validatable_zip_individual_new");
			 var freeTextZipId = dojo.byId("free_text_zip_individual_new");
			 var selectedVar = "<s:property value="checkForValidatableCountry(customer.addresses[#newIndex].country)"/>";
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
			 dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextCityId,!selectedVar);
			 dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextZipId,!selectedVar);
		</s:if>
   });
</script>	


<table width="96%" cellpadding="0" cellspacing="0" id="indiv-addr"
	align="center" border="0" style="margin:0px">
	
	<s:if test="#newIndex > 0">
	  <s:set name="disableAddressBookType" value="true"/>
	  <s:set name="existingAddressBookType" value="addressBookAddressMappings[0].addressBook.type"/>
	</s:if>
	<s:else>
	   <s:set name="disableAddressBookType" value="false"/>
	</s:else>
	
	
	<tr>
	    <authz:ifUserInRole roles="dealer">
		     <td width="13%"><s:text name="label.common.addressBookType" /></td>
		     <td width="30%"><s:select label="Address Book Type" id="addressBookType_dealer_individual_new"
					 name="addressBookAddressMappings[%{#newIndex}].addressBook.type"
					 list="addressBookTypesForDealer" required="true" theme="twms"/> 
			         <script type="text/javascript">
			           dojo.addOnLoad(function() {
	                          var addressBookTypeId = "addressBookType_dealer_individual_new"; 
					          var addressBookTypeCombo = dijit.byId(addressBookTypeId);
	                          <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].addressBook.type !=null">
						  		addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].addressBook.type"/>");
			                  </s:if> 
			                  addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);						          
					    });
			        </script>			 
			</td>			 
		</authz:ifUserInRole>		
		
		 <authz:ifUserNotInRole roles="dealer">
		     <td width="13%"><s:text name="label.common.addressBookType" /></td>
		     <td width="30%"><s:select label="Address Book Type" id="addressBookType_not_dealer_individual_new"
					 name="addressBookAddressMappings[%{#newIndex}].addressBook.type" 
					 list="addressBookTypesForCompany" required="true" theme="twms"/>
					  <script type="text/javascript">
			           dojo.addOnLoad(function() {
                              var addressBookTypeId = "addressBookType_not_dealer_individual_new"; 
					          var addressBookTypeCombo = dijit.byId(addressBookTypeId);
                              <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].addressBook.type !=null">
						  		addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].addressBook.type"/>");
			                  </s:if> 
			                  addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);						          
					    });
			        </script>					
			 </td>
		</authz:ifUserNotInRole>			 
	</tr>
	 
	 <s:if test="customer == null || customer.id == null">
	   <s:set name="isPrimaryAddress" value="true"/>
	 </s:if>
	<tr>
		<td width="13%"><s:text name="label.addressType" /></td>
		<td width="30%"><s:select label="Address Type" id="addressType_individual_new"
			name="addressBookAddressMappings[%{#newIndex}].type" 
			list="addressTypesIndividual" required="true" theme="twms"/>
			<script type="text/javascript">
			       dojo.addOnLoad(function() {
	                           var addressTypeId = "addressType_individual_new"; 
							   var addressTypeCombo = dijit.byId(addressTypeId);
	                           <s:if test ="(addressBookAddressMappings[#newIndex] ==null || addressBookAddressMappings[#newIndex].id ==null) && addressBookAddressMappings[#newIndex].type !=null">
							  	  addressTypeCombo.setValue("<s:property value="addressBookAddressMappings[#newIndex].type"/>");
				               </s:if>  						           
					});
			 </script>				
	    </td>	
			

		<td width="16%"><s:text name="label.markAsPrimary" /></td>
		<td align="center"><s:checkbox name="addressBookAddressMappings[%{#newIndex}].primary" value="#isPrimaryAddress">
		    </s:checkbox></td>
			
	</tr>
	
	 

    <tr>
		<td class="non_editable"><s:text name="label.address" /></td>
		<td colspan="3"><t:textarea label="Address"
			name="customer.addresses[%{#newIndex}].addressLine1"
			id="customerAddress" rows="2" cssStyle="width: 100%;" />
			<s:hidden id="company_travel_location_%{#customerStatus.index}" name="company_travel_location_%{#customerStatus.index}" value="%{customer.addresses[#customerStatus.index].locationForGoogleMap}"/>
		</td>
	</tr>
	

    <tr>
		<td class="non_editable"><s:text name="label.country" /></td>
		<td>   <s:select label ="Country" id="country_individual_new"
                    name="customer.addresses[%{#newIndex}].country"
                    list="countryList" required="true" theme="twms"/>
               <script type="text/javascript">
				  	 dojo.addOnLoad(function() {
				  	 		var countryName = "<s:property value="customer.addresses[#newIndex].country"/>";
				  	  		  	
				  	  		  if(countryName == null || countryName == "")
				  	  		  {						  	  		  	  		  	  			
					  	           <s:if test="(customer== null || customer.id == null )">					  	          	
					  	             dijit.byId("country_company_new").setValue("US");
					  	           </s:if>
				  	          }	   
				  	        
						    dojo.connect(dijit.byId("country_individual_new"),"onChange", function(value) {
						  	getStatesByCountry(value, dijit.byId("validatable_state_individual_new"),
						  	                          true,
						  	                          -1,
						  	                          [ 
						  	                              "free_text_state_individual_new",
						  	                              "free_text_city_individual_new",
						  	                              "free_text_zip_individual_new"
						  	                          ],
						  	                          [
						  	                             "validatable_city_individual_new",
						  	                             "validatable_zip_individual_new"
						  	                          ]
						  	                          );
						  })
		             });
		       </script>
	    </td>
	    <td class="non_editable"><s:text name="label.state" /></td>
		<td>      
	       <sd:autocompleter label='State' id='validatable_state_individual_new' listenTopics='topic_state_individual_new' name='customer.addresses[%{#newIndex}].state' />
              <script type="text/javascript">
   				   dojo.addOnLoad(function() {
	   					  dojo.connect(dijit.byId("validatable_state_individual_new"),"onChange", function(value) {
	   					  getCitiesByCountryAndState(value, dijit.byId("validatable_city_individual_new"),
	   					                                    true,
	   					                                    '-1',
	   					                                    [
	   					                                      "free_text_city_individual_new",
	   					                                      "free_text_zip_individual_new"
	   					                                    ],
	   					                                    [
	   					                                      "validatable_zip_individual_new"
	   					                                    ]);
	   					                                   
	   					  })
   		           });
		      </script>     
		  <s:textfield id="free_text_state_individual_new" name="states[%{#newIndex}]" /> 
		</td> 	
	</tr>    
	
   <tr>
		<td class="non_editable"><s:text name="label.city" /></td>
		<td>
		       
		    <sd:autocompleter label='City' id='validatable_city_individual_new' listenTopics='topic_city_individual_new' name='customer.addresses[%{#newIndex}].city' />           
		      <script type="text/javascript">
	  				  dojo.addOnLoad(function() {
	   					 dojo.connect(dijit.byId("validatable_city_individual_new"),"onChange",function(value) {
	   					 getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_individual_new"),
	   					                                     true, '-1',
	   					                                     [
   					                                           "free_text_zip_individual_new"
   					                                         ]); 
	   					 })
	  		          });
		      </script>           
	       <s:textfield id="free_text_city_individual_new" name="cities[%{#newIndex}]" />      
		</td>
		<td class="non_editable"><s:text name="label.zip" /></td>
		<td>
			  <sd:autocompleter label='Zip' id='validatable_zip_individual_new' listenTopics='topic_zip_individual_new' name='customer.addresses[%{#newIndex}].zipCode' />
		      <s:textfield id="free_text_zip_individual_new" name="zips[%{#newIndex}]" />      
		</td>
   </tr>
	
	<tr>
		<td class="non_editable"><s:text name="label.phone" /></td>
		<td><s:textfield name="customer.addresses[%{#newIndex}].phone" /></td>

		<td class="non_editable"><s:text name="label.email" /></td>
		<td><s:textfield maxlength="255" name="customer.addresses[%{#newIndex}].email" /></td>
	</tr>

	<tr>
		<td class="non_editable"><s:text name="label.fax" /></td>
		<td><s:textfield
			name="customer.addresses[%{#newIndex}].secondaryPhone" /></td>
	</tr>
</table>
<div class="buttonWrapperPrimary"><s:if test="!hideSelect">
    <input id='remove_btn' class="buttonGeneric" style="height:21px; margin-right:5px; overflow:visible; padding-left:5px; padding-right:5px !important; vertical-align:middle;" type="Button"
	value="Remove" /></s:if></div>

<script>
		dojo.addOnLoad(function() {	
			// For new Address
			newAddr = dojo.byId('NewAddress');
			<s:if test="!hideSelect">
				dojo.connect(dojo.byId('remove_btn'), "onclick", function(event) {
					dojo.dom.removeNode(newAddr);
				});
			</s:if>
			<s:if test="customer!=null && customer.id !=null">
				dojo.dom.removeNode(newAddr);
			</s:if>
			var addressDiv = dojo.byId('addressDiv');
			dojo.connect(dojo.byId('addNewAddr'), "onclick", function(event) {
				dojo.dom.insertAtIndex(newAddr, addressDiv, 0);
			});
		});
	</script></div>
</div>
<s:if test="customer!=null && customer.id !=null">
	<s:iterator value="customer.addresses" status="customerStatus">	
		<div dojoType="twms.widget.TitlePane" labelNodeClass="collapseAdmin"
			containerNodeClass="section_div" 
			label="<s:property value='%{addressLine1}'/>, <s:property value='%{city}'/>, <s:property value='%{state}'/>, <s:property value='%{country}'/>-<s:property value='%{zip}'/>"
			id='Address_individual<s:property value="id"/>'
		    <s:if test="id != selectedAddressId">
				open="false"
			</s:if>>	
		
			 <s:hidden name="addressBookAddressMappings[%{#customerStatus.index}].id" /> 
			 <s:hidden name="addressBookAddressMappings[%{#customerStatus.index}].address.id" /> 
	         <s:hidden name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.id" />
	         <s:hidden name="addressBookAddressMappings[%{#customerStatus.index}].version" />
	         
	 <script type="text/javascript">
	      dojo.addOnLoad(function() {
				 var validatableStateId = dijit.byId("validatable_state_individual_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextStateId = dojo.byId("free_text_state_individual_" + <s:property value = "#customerStatus.index"/>);
				 var validatableCityId = dijit.byId("validatable_city_individual_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextCityId = dojo.byId("free_text_city_individual_" + <s:property value = "#customerStatus.index"/>);
				 var validatableZipId = dijit.byId("validatable_zip_individual_" + <s:property value = "#customerStatus.index"/>);
				 var freeTextZipId = dojo.byId("free_text_zip_individual_" + <s:property value = "#customerStatus.index"/>);
				 var selectedVar = "<s:property value="checkForValidatableCountry(customer.addresses[#customerStatus.index].country)"/>";
			 	 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextStateId,!selectedVar);
			     dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextCityId,!selectedVar);
			     dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			     dojo.html.setDisplay(freeTextZipId,!selectedVar);
	    });
    </script>
	  
	   <table width="96%" cellpadding="0" cellspacing="0" id="indiv-addr"
			    align="center" style="margin:0px" >
	           
			  <tr>
			     <authz:ifUserInRole roles="dealer">
			         <td width="13%"><s:text name="label.common.addressBookType" /></td>
					 <td width="30%"><s:select label="Address Book Type" id="addressBookType_dealer_individual_%{#customerStatus.index}"
						 name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.type" 
						 list="addressBookTypesForDealer" required="true" theme="twms"/>
						  <script type="text/javascript">
						  	dojo.addOnLoad(function() {
                                    var addressBookTypeId = "addressBookType_dealer_individual_" + <s:property value = "#customerStatus.index"/>
						  	        var addressBookTypeCombo = dijit.byId(addressBookTypeId);
							  		addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#customerStatus.index].addressBook.type"/>");
							  		addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);							                           
				          });
				         </script> 
					</td>		 
				 </authz:ifUserInRole>	 	
				 
				 <authz:ifUserNotInRole roles="dealer">
			         <td width="13%"><s:text name="label.common.addressBookType" /></td>
					 <td width="30%"><s:select label="Address Book Type" id="addressBookType_not_dealer_individual_%{#customerStatus.index}"
						 name="addressBookAddressMappings[%{#customerStatus.index}].addressBook.type" 
						 list="addressBookTypesForCompany" required="true" theme="twms" />
						 <script type="text/javascript">
						    dojo.addOnLoad(function() {
                                   var addressBookTypeId = "addressBookType_not_dealer_individual_" + <s:property value = "#customerStatus.index"/>
					  	           var addressBookTypeCombo = dijit.byId(addressBookTypeId);
						  		   addressBookTypeCombo.setValue("<s:property value="addressBookAddressMappings[#customerStatus.index].addressBook.type"/>");
						  		   addressBookTypeCombo.setReadOnly(<s:property value="#disableAddressBookType"/>);								              
				          });
			            </script>	 
			          </td>
				 </authz:ifUserNotInRole>	 
		      </tr>
		      
		      <tr>
				<td width="13%"><s:text name="label.addressType" /></td>
				<td width="30%"><s:select label="Address Type" id="addressType_individual_%{#customerStatus.index}" 
					name="addressBookAddressMappings[%{#customerStatus.index}].type" 
					list="addressTypesIndividual" required="true" theme="twms"/>
				    <script type="text/javascript">
					  dojo.addOnLoad(function() {
                                var addressTypeId = "addressType_individual_" + <s:property value = "#customerStatus.index"/>
					  	        var addressTypeCombo = dijit.byId(addressTypeId);
						  		addressTypeCombo.setValue("<s:property value="addressBookAddressMappings[#customerStatus.index].type"/>");						          
				      });
			        </script>		
				</td>
		
				<td width="16%"><s:text name="label.markAsPrimary" /></td>
			    <td align="center"><s:checkbox name="addressBookAddressMappings[%{#customerStatus.index}].primary">
				    </s:checkbox></td>
		     </tr>
		     
		     <tr>
			    <td width="20%"><s:text name="label.contactPersonName" /></td>
				<td ><s:textfield name="customer.addresses[%{#customerStatus.index}].contactPersonName" /></td>
	        </tr>
		      
		   
		     
			 <tr>
				<td><s:text name="label.address" /></td>
				<td colspan="3"><t:textarea label="Address"
					name="customer.addresses[%{#customerStatus.index}].addressLine1"
					id="customerAddress" rows="2" cssStyle="width: 100%;" /></td>
			</tr>
			
			
			<tr>
					<td class="non_editable"><s:text name="label.country" /></td>
					<td>   <s:select label ="Country" id="country_individual_%{#customerStatus.index}"
			                    name="customer.addresses[%{#customerStatus.index}].country"
			                    list="countryList" required="true" theme="twms"/>
			               <script type="text/javascript">
							  dojo.addOnLoad(function() {
							  			
							  		   var countryName = "<s:property value="customer.addresses[%{#customerStatus.index}].country"/>";
				  	  		  	
						  	  		   if(countryName == null || countryName == "")
						  	  		   {						  	  		  	  		  	  			
							  	           <s:if test="(customer== null || customer.id == null )">					  	          	
							  	             dijit.byId("country_company_new").setValue("US");
							  	           </s:if>
						  	            }	 
									    dojo.connect(dijit.byId("country_individual_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
									  	getStatesByCountry(value, dijit.byId("validatable_state_individual_" + <s:property value = "#customerStatus.index"/>),
									  	                          false,
									  	                          <s:property value = "#customerStatus.index"/>,
									  	                          [
									  	                              "free_text_state_individual_" + <s:property value = "#customerStatus.index"/>,
									  	                              "free_text_city_individual_" + <s:property value = "#customerStatus.index"/>,
									  	                              "free_text_zip_individual_" + <s:property value = "#customerStatus.index"/>
									  	                          ],
									  	                          [
									  	                             "validatable_city_individual_" + <s:property value = "#customerStatus.index"/>,
									  	                             "validatable_zip_individual_" + <s:property value = "#customerStatus.index"/>
									  	                          ]);

									    })
					          });
					       </script>
				    </td>
				    <td class="non_editable"><s:text name="label.state" /></td>
					<td>
					       <sd:autocompleter label='State' id='validatable_state_individual_%{#customerStatus.index}' listenTopics='topic_state_individual_%{#customerStatus.index}' name='customer.addresses[%{#customerStatus.index}].state' />
							   <script type="text/javascript">
				   				  	  dojo.addOnLoad(function() {
					   					   dojo.connect(dijit.byId("validatable_state_individual_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
					   					   getCitiesByCountryAndState(value, dijit.byId("validatable_city_individual_" + <s:property value = "#customerStatus.index"/>),
					   					                                      false,<s:property value = "#customerStatus.index"/>,
					   					                                      [
									  	                                         "free_text_city_individual_" + <s:property value = "#customerStatus.index"/>,
									  	                                         "free_text_zip_individual_" + <s:property value = "#customerStatus.index"/>,
									  	                                      ],
									  	                                      [
												  	                             "validatable_zip_individual_" + <s:property value = "#customerStatus.index"/>
									  	                                      ]);                                     
					   					  })
				   		          });
						      </script>
					      <s:textfield id="free_text_state_individual_%{#customerStatus.index}" name="states[%{#customerStatus.index}]" /> 
					</td> 	
				</tr>    
				
			   <tr>
					<td class="non_editable"><s:text name="label.city" /></td>
					<td>
						   <sd:autocompleter label='City' id='validatable_city_individual_%{#customerStatus.index}' listenTopics='topic_city_individual_%{#customerStatus.index}' name='customer.addresses[%{#customerStatus.index}].city' />
						      <script type="text/javascript">
				   				   dojo.addOnLoad(function() {
					   					    dojo.connect(dijit.byId("validatable_city_individual_" + <s:property value = "#customerStatus.index"/>),"onChange",function(value) {
					   					    getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_individual_" + <s:property value = "#customerStatus.index"/>),
					   					                                      false,<s:property value = "#customerStatus.index"/>,
					   					                                      [
   					                                                           "free_text_zip_individual_"+<s:property value = "#customerStatus.index"/>
   					                                                          ]); 
					   					   })
				   		           });
						      </script>      
					      <s:textfield id="free_text_city_individual_%{#customerStatus.index}" name="cities[%{#customerStatus.index}]" />      
					</td>
					<td class="non_editable"><s:text name="label.zip" /></td>
					<td>
						  <sd:autocompleter label='Zip' id='validatable_zip_individual_%{#customerStatus.index}' listenTopics='topic_zip_individual_%{#customerStatus.index}' name='customer.addresses[%{#customerStatus.index}].zipCode' />
					      <s:textfield id="free_text_zip_individual_%{#customerStatus.index}" name="zips[%{#customerStatus.index}]" />      
					</td>
		    </tr>			  


			<tr>
				<td><s:text name="label.phone" /></td>
				<td><s:textfield
					name="customer.addresses[%{#customerStatus.index}].phone" /></td>
	
				<td><s:text name="label.email" /></td>
				<td><s:textfield maxlength="255"
					name="customer.addresses[%{#customerStatus.index}].email" /></td>
			</tr>
	
			<tr>
				<td class="non_editable"><s:text name="label.fax" /></td>
				<td><s:textfield
					name="customer.addresses[%{#customerStatus.index}].secondaryPhone" /></td>
			</tr>
			
		   	
		</table>
	</div>
	</s:iterator>
</s:if>
 <div class="buttonWrapperPrimary"><input class="buttonGeneric"
	type="Button" value="<s:text name='button.label.save' />" style="height:21px; margin-right:5px; overflow:visible; padding-left:5px; padding-right:5px !important; vertical-align:middle;"  
	id='save_btn' /></div>
		<script>
			dojo.addOnLoad(function() {	
				dojo.connect(dojo.byId('save_btn'), "onclick", function() {
						<s:if test="customer==null || customer.id==null">
			               dojo.byId("createCustomerForm").submit();
		                </s:if>
		                <s:else>
		                   dojo.byId("updateCustomerForm").submit();
		                </s:else>
				});

			});
		</script>
</div>

