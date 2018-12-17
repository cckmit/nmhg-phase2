<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/validateAddress.js"></script>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title><s:text name="pageTitle.updateProfile"></s:text></title>
<s:head theme="twms" />
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<script type="text/javascript">
   dojo.require("dijit.layout.LayoutContainer");
   dojo.require("dijit.layout.ContentPane");
</script>
</head>

<u:body>
	<u:actionResults />
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white;">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form action="update_user_location" theme="twms" validate="true">
		<s:hidden name="context" value="Dealer" />
		<s:hidden name="user" value="%{user.id}"/>
		<s:hidden name="userOrgAddress" value="%{userOrgAddress.id}"/>
		
		<div id="user_info" style="border:1px solid #EFEBF7;background:#F3FBFE;margin:5px">
		<div id="user_info_title" class="section_heading"><s:text
			name="label.userMgmt.userLocationMgmt" /></div>
	<script type="text/javascript">
       dojo.addOnLoad(function() {
			 var validatableStateId = dijit.byId("validatable_state_company_new");
			 var freeTextStateId = dojo.byId("free_text_state_company_new");
			 var validatableCityId = dijit.byId("validatable_city_company_new");
			 var freeTextCityId = dojo.byId("free_text_city_company_new");
			 var validatableZipId = dijit.byId("validatable_zip_company_new");
			 var freeTextZipId = dojo.byId("free_text_zip_company_new");
		     	 var selectedVar = "<s:property value="checkForValidatableCountry(userOrgAddress.country)"/>";
			 dojo.html.setDisplay(validatableStateId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextStateId,!selectedVar);
			 dojo.html.setDisplay(validatableCityId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextCityId,!selectedVar);
			 dojo.html.setDisplay(validatableZipId.domNode,selectedVar);
			 dojo.html.setDisplay(freeTextZipId,!selectedVar);		 
      });
   </script>		

		<table width="96%" cellpadding="0" cellspacing="0" class="grid"
			align="center">
		<s:if test="userOrgAddress.id!=null">
			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.name" /></td>
				<td><s:property value="user.name" /></td>
				<td colspan="2">&nbsp;</td>
			</tr>
		</s:if>
			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.address.line1"/></td>
    			<td colspan="3"><s:textfield maxlength="255" name="userOrgAddress.addressLine1" id="userAddress1" cssStyle="width: 50%"/></td>
			</tr>
			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.address.line2"/></td>
   				<td colspan="3"><s:textfield maxlength="255" name="userOrgAddress.addressLine2" id="userAddress2" cssStyle="width: 50%"/></td>
			</tr>
			<tr>
		<td class="non_editable labelStyle"><s:text name="label.country" /></td>
		<td>   <s:select label ="Country" id="country_company_new"
                    name="userOrgAddress.country"
                    list="countryList" required="true" theme="twms"/>
               <script type="text/javascript">
	               dojo.addOnLoad(function() {
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
	       <sd:autocompleter label='State' id='validatable_state_company_new' listenTopics='topic_state_company_new' name='userOrgAddress.state' />
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
	      <s:textfield id="free_text_state_company_new" maxlength="255" name="stateCode" /> 
		</td> 	
	</tr>    
	
   <tr>
		<td class="non_editable labelStyle"><s:text name="label.city" /></td>
		<td>
	       <sd:autocompleter label='City' id='validatable_city_company_new' listenTopics='topic_city_company_new' name='userOrgAddress.city' required='true' />
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
	      <s:textfield id="free_text_city_company_new" maxlength="255" name="cityCode" />      
		</td>
		<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
			<td>
		       <sd:autocompleter label='Zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='userOrgAddress.zipCode' />             
		      <s:textfield id="free_text_zip_company_new" maxlength="255" name="zipCode" />      
		    </td>
		    </tr>

			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.phone" /></td>
				<td><s:textfield maxlength="255" name="userOrgAddress.phone" /></td>

				<td class="non_editable labelStyle"><s:text name="label.common.email" /></td>
				<td><s:textfield maxlength="255" name="userOrgAddress.email" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.fax" /></td>
				<td><s:textfield maxlength="255" name="userOrgAddress.secondaryPhone" /></td>
			</tr>
		</table>
		</div>
		<div id="separator"></div>

		<div id="submit" align="center" class="spacingAtTop">
		<input id="submit_btn"
			class="buttonGeneric" type="submit"
			value="<s:text name='button.common.save'/>" />
		<input id="cancel_btn"
			class="buttonGeneric" type="button"
			value="<s:text name='button.common.cancel'/>"
			onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
		</div>
	</s:form>
        </div>
	</div>
</u:body>
</html>