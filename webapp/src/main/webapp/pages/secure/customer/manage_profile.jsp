<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<script type="text/javascript" src="scripts/validateAddress.js"></script>


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><s:text name="pageTitle.updateProfile"></s:text></title>
<s:head theme="twms" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="warrantyForm.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="base.css" />
<script type="text/javascript">
   dojo.require("dijit.layout.ContentPane");
</script>
<style>
table.grid tbody tr td {
	padding-left: 0px;
}
</style>
</head>

<u:body>
	<u:actionResults />
	<div dojoType="dijit.layout.ContentPane"
		style="width: 100%; height: 100%;">
        <s:form action="update_profile" theme="twms" validate="true" id="baseFormId">
		<s:hidden name="user" value="%{user.id}" />

		<div id="user_info"
			style="border: 1px solid #EFEBF7; margin: 5px 0px 5px 5px; background: #F3FBFE">
		<div id="user_info_title" class="section_heading"><s:text
			name="label.manageProfile.userDetails" /></div>
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

		<table border="0" cellpadding="0" cellspacing="0" class="grid"
			align="center">
			<tr>
				<td class="non_editable labelStyle" width="15%" nowrap="nowrap"><s:text
					name="label.dealerUser.login" /></td>
				<td style="padding-left: 13px" width="40%"><s:property
					value="user.name" /></td>
				<td class="non_editable labelStyle" width="15%" nowrap="nowrap"><s:text
					name="label.manageProfile.locale" /></td>
				<td><s:select name="user.locale" list="listOfLocale"
					id="locale" required="true" theme="twms" listValue="description"
					listKey="locale" headerKey="null"
					headerValue="%{getText('label.common.selectHeader')}"
					cssStyle="width: 130px" /> <script type="text/javascript">
					  	dojo.addOnLoad(function() {
					  		dijit.byId("locale").setValue("<s:property value="user.locale.toString()"/>");
					  	});
				  </script></td>
			</tr>
			<s:set name="certificationStatus" value="%{certificationFlag}" />
			<s:if test="%{#certificationStatus=='Yes'}">
				<s:iterator value="certified" status="itrc">

					

						<s:if test="certified.size()>1">
						<tr>
							<td class="non_editable labelStyle" width="15%" nowrap="nowrap"><s:text
								name="label.common.serviceProvider" /></td>
								<td style="padding-left: 13px" width="40%"><s:property
					value="organization[#itrc.index]"  /></td>
							
									<td class="non_editable labelStyle" width="15%"  nowrap="nowrap"><s:text
										name="label.common.certification" /></td>
									<td style="padding-left: 13px" width="40%"><s:property
										value="certified[#itrc.index]" /></td>
								
							
						</tr>
						</s:if>
						<s:else>
							<tr>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.common.certification" /></td>
				<td style="padding-left: 13px" ><s:property
										value="certified[#itrc.index]" /></td>
				<td class="non_editable labelStyle" colspan="2"></td>
			</tr>
						</s:else>

					

				</s:iterator>
			</s:if>
			<tr>
                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                    name="label.dealerUser.firstName" /></td>
                <td><s:textfield maxlength="100" name="user.firstName" /></td>
                <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                    name="label.dealerUser.lastName" /></td>
                <td><s:textfield maxlength="100" name="user.lastName" /></td>
            </tr>
			<s:if test="isDisabledPasswordUpdate()">
                <tr>
                    <td class="non_editable labelStyle" colspan="4">
                        <s:text name="message.manageProfile.passwordIssues" />
                    </td>
                </tr>
             </s:if>          
			 <s:else>
			 
                <tr>
                    <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                        name="label.manageProfile.existingPassword" /></td>
                    <td><s:password maxlength="20" name="existingPassword" /></td>
                    <td class="non_editable labelStyle" colspan="2"></td>
                </tr>
              <tr>
                    <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                        name="label.manageProfile.newPassword" /></td>
                    <td><s:password maxlength="20" name="newPassword" /></td>
                    <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                        name="label.manageProfile.confirmPassword" /></td>
                    <td><s:password maxlength="20" name="confirmPassword" /></td>
                </tr>
            </s:else>
            <s:if test="user.businessUnits.size()>1">
             <tr>
                    <td class="non_editable labelStyle" nowrap="nowrap"><s:text
                        name="label.preferredBusinessUnit" /></td>
                        
                    <td><s:select
                        name="user.preferredBu" id="preferredBu" list="user.businessUnits" listValue="name" listKey="name"
                        cssStyle="width: 130px"/>
                       <script
					type="text/javascript">
			  dojo.addOnLoad(function() {
				  dijit.byId("preferredBu").setValue("<s:property value="user.preferredBu"/>");
			  }
			  </script></td></tr>
            </s:if>
			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.address.line1"/></td>
    			<td colspan="3"><s:textfield maxlength="255" name="user.address.addressLine1" id="userAddress1" cssStyle="width: 50%"/></td>
			</tr>
			<tr>
				<td class="non_editable labelStyle"><s:text name="label.common.address.line2"/></td>
   				<td colspan="3"><s:textfield maxlength="255" name="user.address.addressLine2" id="userAddress2" cssStyle="width: 50%"/></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.country" /></td>
				<td><s:select label="Country" id="country_company_new"
					name="user.address.country" cssStyle="width: 130px"
					list="countryList" required="true" theme="twms" /> <script
					type="text/javascript">
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
		       </script></td>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.state" /></td>
				<td><sd:autocompleter label='State' id='validatable_state_company_new' listenTopics='topic_state_company_new' name='user.address.state' /> <script type="text/javascript">
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
		      </script> <s:textfield id="free_text_state_company_new" maxlength="255" name="stateCode" />
				</td>
			</tr>

			<tr>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.city" /></td>
				<td><sd:autocompleter label='City' id='validatable_city_company_new' listenTopics='topic_city_company_new' name='user.address.city' required='true' /> <script type="text/javascript">
   			 dojo.addOnLoad(function() {
			    dojo.connect(dijit.byId("validatable_city_company_new"),"onChange",function(value) {
			    getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_company_new"),
								true, '-1',
								[
								  "free_text_zip_company_new"
								]); 
			       })
	          });		     
		      </script> <s:textfield id="free_text_city_company_new" maxlength="255" name="cityCode" />
				</td>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.zip" /></td>
				<td><sd:autocompleter label='Zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='user.address.zipCode' /> <s:textfield id="free_text_zip_company_new"
					name="zipCode" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.common.phone" /></td>
				<td><s:textfield maxlength="255" name="user.address.phone" /></td>

				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.common.email" /></td>
				<td><s:textfield maxlength="255" name="user.address.email" /></td>
			</tr>

			<tr>
				<td class="non_editable labelStyle" nowrap="nowrap"><s:text
					name="label.common.fax" /></td>
				<td><s:textfield maxlength="255" name="user.address.fax" /></td>
			</tr>
		</table>
		<div id="separator"></div>
		</div>
		<div id="submit" align="center" class="spacingAtTop"><input
			id="submit_btn" class="buttonGeneric" type="submit"
			value="<s:text name='button.common.update'/>" /><input
			id="cancel_btn" class="buttonGeneric" type="button"
			value="<s:text name='button.common.cancel'/>"
			onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" /></div>

	</s:form>
</u:body>
<authz:ifPermitted resource="settingsProfileReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>