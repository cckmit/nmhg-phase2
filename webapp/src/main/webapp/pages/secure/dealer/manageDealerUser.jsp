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
<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
	<u:stylePicker fileName="adminPayment.css" />
    <title>
        <s:text name="accordion_jsp.accordionPane.dealerUserMgmt"/>
    </title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <script type="text/javascript">
    <s:if test="pageReadOnlyAdditional">
    dojo.addOnLoad(function() {
        twms.util.makePageReadOnly("dishonourReadOnly");
    });
    </s:if>
    
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.addOnLoad(function() {
            var validatableStateId = dijit.byId("validatable_state_company_new");
            var freeTextStateId = dojo.byId("free_text_state_company_new");
            var validatableCityId = dijit.byId("validatable_city_company_new");
            var freeTextCityId = dojo.byId("free_text_city_company_new");
            var validatableZipId = dijit.byId("validatable_zip_company_new");
            var freeTextZipId = dojo.byId("free_text_zip_company_new");
            var selectedVar = "<s:property value="checkForValidatableCountry(countryCode)"/>";
            dojo.html.setDisplay(validatableStateId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextStateId, !selectedVar);
            dojo.html.setDisplay(validatableCityId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextCityId, !selectedVar);
            dojo.html.setDisplay(validatableZipId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextZipId, !selectedVar);
            <s:if test="!isAssignedTechnicianRole()">
            	dojo.html.hide(dojo.byId("technician_details"));
            </s:if>
            
//             <s:if test="!isAssignedTechnicianRole()">
//         		dojo.html.hide(dojo.byId("technician_company_details"));
//             </s:if>
            
            <s:if test="!isAssignedTechnicianRole()">
        		dojo.html.hide(dojo.byId("technician_certification_details"));
        	</s:if>
        	
        
        	
        	<authz:ifUserNotInRole roles="admin">
        		dojo.html.hide(dojo.byId("technician_certification_details"));
        	</authz:ifUserNotInRole>
           
            
        });

        function showDealerNumber() {
            dojo.html.show(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(false);
            dojo.html.show(dojo.byId("dealerNumberText"));
            dojo.html.show(dojo.byId("toggleToDealerName"));
            dojo.html.hide(dijit.byId("dealerNameAutoComplete").domNode);
            dijit.byId("dealerNameAutoComplete").setDisabled(true);
            dojo.html.hide(dojo.byId("dealerNameText"));
            dojo.html.hide(dojo.byId("toggleToDealerNumber"));
        }

        function showDealerName() {
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
            dojo.html.hide(dojo.byId("dealerNumberText"));
            dojo.html.hide(dojo.byId("toggleToDealerName"));
            dojo.html.show(dijit.byId("dealerNameAutoComplete").domNode);
            dijit.byId("dealerNameAutoComplete").setDisabled(false);
            dojo.html.show(dojo.byId("dealerNameText"));
            dojo.html.show(dojo.byId("toggleToDealerNumber"));
        }       
    </script>
    <script type="text/javascript" src="scripts/validateAddress.js"></script>
    <script type="text/javascript" src="scripts/manageLists.js"></script>
</head>

<u:body>
<authz:ifUserInRole roles="admin">
<s:hidden id="IsUserAnAdmin" value="Admin" />
</authz:ifUserInRole>
<authz:else>
<script type="text/javascript">
dojo.byId("IsUserAnAdmin").value="User";
</script>
</authz:else>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-y:auto;" id="root">

<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form action="create_dealer_user" id="createUserForm" theme="twms">
<u:actionResults />
<div class="section_div">
    <div class="section_heading">
        <s:text name="label.dealerUser.createUser"/>
    </div>
<!-- <div class="section_div" id="userRoleIsNotDealerTechnician">
<table class="form" cellspacing="0" cellpadding="0">
	
	<s:if test="loggedInUserAnInternalUser">
    <tr >
        <td id="dealerNameText" class="labelStyle" width="15%" nowrap="nowrap">
            <s:text name="label.common.dealerName"/>:
        </td>
        <td id="dealerNumberText" width="15%" nowrap="nowrap" class="labelStyle" >
            <s:text name="label.common.dealerNumber"/>:
        </td>
        <td width="25%">
            <sd:autocompleter id='dealerNameAutoComplete' href='list_dealer_names_dealer_summary.action' name='serviceProvider' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' listenTopics='/setDealerIdForName/onLoad' />
							<script type="text/javascript">
                                dojo.addOnLoad(function() {
                                    dojo.publish("/setDealerIdForName/onLoad", [{
                                        addItem: {
                                            key:'<s:property value="%{serviceProvider.id}"/>',
                                            label:'<s:property value="%{serviceProvider.name}"/>'
                                        }
                                    }]);
                                });
                         	</script>
            <sd:autocompleter id='dealerNumberAutoComplete' href='list_dealer_numbers_dealer_summary.action' name='serviceProvider' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' listenTopics='/setDealerIdForNumber/onLoad' />
                             <script type="text/javascript">
                             	dojo.addOnLoad(function() {   
                                    dojo.publish("/setDealerIdForNumber/onLoad", [{
                                        addItem: {
                                            key:'<s:property value="%{serviceProvider.id}"/>',
                                            label:'<s:property value="%{serviceProvider.serviceProviderNumber}"/>'
                                        }
                                    }]);
                                });
                         	</script>
            <img style="display: none;" id="indicator" class="indicator"
                 src="image/indicator.gif" alt="Loading..."/>
        </td>
        <td>
            <div id="toggle" style="cursor:pointer;">
                <div id="toggleToDealerNumber" class="alinkclickable" onclick="showDealerNumber()">
                    <s:text name="toggle.common.toDealerNumber"/>
                </div>
                <div id="toggleToDealerName" class="alinkclickable" onclick="showDealerName()">
                    <s:text name="toggle.common.toDealerName"/>
                </div>
            </div>
        </td>
    </tr>
    </s:if>
	<s:else>
    <tr>
        <td class="labelStyle"><s:text name="label.dealerUser.companyName"/></td>
        <td colspan="3"><s:property value="serviceProvider.name"/></td>
    </tr>
    <tr>
        <td class="labelStyle"><s:text name="label.dealerUser.companyNumber"/></td>
        <td colspan="3"><s:property value="serviceProvider.serviceProviderNumber"/></td>
    </tr>
    </s:else>
</table>
</div>
<div id="userRoleIsDealerTechnician">
</div>  -->

<div id="technician_company_details" class="mainTitle" style="margin:10px 0px 0px 0px;">

	<div class="section_div">
	    <div class="section_heading">
	        <s:text name="label.technicianCertification.companiesWorkingFor"/>
	    </div>
	</div>
	
<u:repeatTable id="technician_company_table" theme="twms" cellspacing="4" cellpadding="0" cssStyle="margin:5px;" width="99%">
	<thead>
		<tr class="admin_table_header">
			<th class="colHeader"><s:text name="label.technicianCertification.companyName" /></th>
			<th class="colHeader" width="9%"><u:repeatAdd id="companyAdder" theme="twms">
				<img id="addCompanyIcon" src="image/addRow_new.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.technician.addCompany" />" />
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="companyBody" value="companiesWorkingFor" index="index" theme="twms">
		 <tr index="#index">
		   <td valign="top"> <sd:autocompleter id='dealerNameAutoComplete_#index' cssStyle='width:265px' href='list_dealer_names_dealer_summary.action' name='dealersForUser[#index]' value='%{name}' keyValue="%{id}" loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' listenTopics='/setDealerIdForName/onLoad/#index' /></td>
							<script type="text/javascript">
                                dojo.addOnLoad(function() {
                                    dojo.publish("/setDealerIdForName/onLoad", [{
                                        addItem: {
                                            key:'<s:property value="%{serviceProvider.id}"/>',
                                            label:'<s:property value="%{serviceProvider.name}"/>'
                                        }
                                    }]);
                                });
                         	</script>
		    
		    </td>
		 	<td valign="top"><u:repeatDelete id="deleter_#index"
				theme="twms">
				<img id="deleteCompany" src="image/remove.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.technician.deleteCompany" />" />
			</u:repeatDelete></td>
		 </tr>
	</u:repeatTemplate>
	</u:repeatTable> 
</div>
<div>
 <div class="section_div">
    <div class="section_heading">
        <s:text name="User Details"/>
    </div>
<table class="form">
<jsp:include page="rolesDetail.jsp" flush="true"/>	            
<tr>
    <td class="labelStyle"><s:text name="label.dealerUser.firstName"/></td>
    <td><s:textfield maxlength="20" size="30" name="user.firstName"/></td>
    <td class="labelStyle"><s:text name="label.common.address.line1"/></td>
    <td><t:textarea name="user.address.addressLine1" id="userAddress1" cssStyle="width: 135px; height: 39px;" rows="1"/></td>
</tr>

<tr>
    <td class="labelStyle"><s:text name="label.dealerUser.lastName"/></td>
    <td><s:textfield maxlength="20" size="30" name="user.lastName"/></td>
    <td class="labelStyle"><s:text name="label.common.address.line2"/></td>
    <td><t:textarea name="user.address.addressLine2" id="userAddress2" cssStyle="width: 135px; height: 39px;" rows="1"/></td>
</tr>
<tr>
    <td class="labelStyle"><s:text name="label.manageProfile.locale"/></td>
    <td>
        <s:select name="defaultLocale" list="listOfLocale"
                  id="locale" required="true" theme="twms"
                  listValue="description" listKey="locale"
                  headerKey="null" headerValue="%{getText('label.common.selectHeader')}"
                  cssStyle="width: 130px" value="%{getDefaultLocale()}"/>

    </td>
    <td class="labelStyle"><s:text name="label.country"/></td>
    <td>   <s:select label ="Country" id="country_company_new"
                    name="user.address.country" cssStyle="width: 130px"
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
</tr>
<tr>
    <td class="labelStyle"><s:text name="label.dealerUser.email"/></td>
    <td><s:textfield size="30" maxlength="255" name="user.email"/></td>
    <td class="labelStyle"><s:text name="label.state"/></td>
    <td>
	       <sd:autocompleter label='State' id='validatable_state_company_new' listenTopics='topic_state_company_new' name='user.address.state' />
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
	      <s:textfield id="free_text_state_company_new" name="stateCode"  style="width: 131px; margin-left: 0px;" /> 
		</td> 	
</tr>
<tr>
    <td class="labelStyle"><s:text name="label.common.phone"/></td>
    <td><s:textfield maxLength="30" size="30" name="user.address.phone"/></td>
    <td class="labelStyle"><s:text name="label.city"/></td>
    <td>
	       <sd:autocompleter label='City' id='validatable_city_company_new' listenTopics='topic_city_company_new' name='user.address.city' required='true' />
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
	      <s:textfield id="free_text_city_company_new" name="cityCode" style="width: 131px; margin-left: 0px;"/>      
		</td>
</tr>
<tr>
    <td class="labelStyle"><s:text name="label.common.fax"/></td>
    <td ><s:textfield maxLength="30" size="30" name="user.address.fax"/></td>
    <td class="labelStyle"><s:text name="label.zip"/></td>
    <td>
             <sd:autocompleter label='Zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='user.address.zipCode' />             
		      <s:textfield id="free_text_zip_company_new" name="zipCode" style="width: 131px; margin-left: 0px;"/>      
		</td>
</tr>
</table></div></div>
<div id="technician_details" >
	<div class="section_div">
	    <div class="section_heading">
	        <s:text name="Technician Details"/>
	    </div>
	
    <table class="form">

		<tr>
			<td class="labelStyle"><s:text name="label.dealerUser.serviceManagerName"/> : </td>
			<td>
				<s:textfield maxlength="20" size="30" name="technicianDetails.serviceManagerName" value="%{technicianDetails.serviceManagerName}"/>
			</td>
			<td class="labelStyle"><s:text name="label.common.comments"/> : </td>
			<td>
				<t:textarea name="technicianDetails.comments" id="comments" cssStyle="width: 100%;" rows="2" value="%{technicianDetails.comments}"/>
			</td>
		</tr>
		<tr>
			<%-- <td class="labelStyle"><s:text name="label.dealerUser.serviceManagerFax"/> : </td>
			<td>
				<s:textfield maxlength="20" size="30" name="technicianDetails.serviceManagerFax" value="%{technicianDetails.serviceManagerFax}"/>
			</td> --%>
			<td class="labelStyle"><s:text name="label.dealerUser.serviceManagerEmail"/> : </td>
			<td>
				<s:textfield maxlength="255" size="30" name="technicianDetails.emailId" value="%{technicianDetails.emailId}"/>
			</td>
		</tr>
	</table></div>
</div>

<div id="technician_certification_details" class="mainTitle" style="margin:10px 0px 0px 0px;">

	<div class="section_div">
	    <div class="section_heading">
	        <s:text name="label.technicianCertification.technicianCertificaiton"/>
	    </div>

	
	<u:repeatTable id="technician_certification_table" theme="twms" cellspacing="4" cellpadding="0" cssStyle="margin:5px;" width="99%">
	<thead>
		<tr class="admin_table_header">			
			<th class="colHeader"><s:text name="label.technicianCertification.brandName" /></th>
			<th class="colHeader"><s:text name="label.technicianCertification.certificationName" /></th>
			<th class="colHeader"><s:text name="label.technicianCertification.certificationFrom" /></th>
			<th class="colHeader"><s:text name="label.technicianCertification.certificationExpiry" /></th>
			<th class="colHeader"><s:text name="label.technicianCertification.categoryLevel" /></th>
			<th class="colHeader"><s:text name="label.technicianCertification.categoryName" /></th>
			<th class="colHeader" width="9%"><u:repeatAdd id="adder" theme="twms">
				<img id="addCertificationIcon" src="image/addRow_new.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.technician.addCertification" />" />
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="certicationBody" value="certificationList" index="index" theme="twms">
		 <tr index="#index">
		 <s:hidden name="technicianDetails.technicianCertifications[#index].techUser" value='%{certificationList[0].techUser}'/>
		 	<td valign="top"><s:select name="technicianDetails.technicianCertifications[#index].brand" list="{'HYSTER','YALE'}" value='%{brand}' id='brand_#index' theme="twms" disabled="false" cssStyle="width:90px" onchange="fillCertificateDetails(dojo.byId('brand_#index'),dojo.byId('categoryLevel_#index'),dojo.byId('categoryName_#index'),dojo.byId('series_#index'),dojo.byId('certificationName_#index'))"/></td>		 			 	
		 	<td valign="top"><s:textfield name="technicianDetails.technicianCertifications[#index].certificationName" value='%{certificationName}' id='certificationName_#index' theme="twms"/></td>
			<td valign="top"><sd:datetimepicker name='technicianDetails.technicianCertifications[#index].certificationFromDate' value='%{certificationFromDate}' id='certificationFromDate_#index' /></td>
			<td valign="top"><sd:datetimepicker name='technicianDetails.technicianCertifications[#index].certificationToDate' value='%{certificationToDate}' id='certificationToDate_#index' /></td>
			<td valign="top" ><s:label value='%{categoryLevel}' id='categoryLevel_#index' theme="twms" /></td>
		 	<td valign="top"><s:label value='%{categoryName}' id='categoryName_#index' theme="twms" /></td>
			<td valign="top"><u:repeatDelete id="certDeleter_#index"
				theme="twms">
				<img id="deleteCertification" src="image/remove.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.technician.deleteCertification" />" />
			</u:repeatDelete>
				
				<script type="text/javascript">
				dojo.addOnLoad(function(){	
					var certificateName = dojo.byId('certificationName_#index');
					var brand = dojo.byId('brand_#index');
					var categoryLevel = dojo.byId('categoryLevel_#index');
					var categoryName = dojo.byId('categoryName_#index');
					var series = dojo.byId('series_#index');
					certificateName.fireOnLoadOnChange=false;
					dojo.connect(certificateName,"onchange",function(){	
					fillCertificateDetails(brand,categoryLevel,categoryName,series,certificateName);
				});
				});
				</script>
			</td>
		 </tr>
	</u:repeatTemplate>
	</u:repeatTable>

</div>	</div>

<div class="section_div">
    <div class="section_heading">
        <s:text name="Login Details"/>
    </div>


<table class="form">

    <tr>
        <td class="labelStyle"><s:text name="label.dealerUser.login"/></td>
        <td colspan="3"><s:textfield maxlength="20" size="30" name="user.name"/></td>
    </tr>
    <tr id="passwordTr">
        <td class="labelStyle"><s:text name="label.dealerUser.password"/></td>
        <td colspan="3"><s:password maxlength="20" size="30" name="user.password" value="%{user.password}"/></td>
    </tr>
    <tr id="confirmPasswordTr">
        <td class="labelStyle"><s:text name="label.dealerUser.confirmPassword"/></td>
        <td colspan="3"><s:password maxlength="20" size="30" name="confirmPassword" value="confirmPassword"/></td>
    </tr>


</table>
</div>
<div id="submit" align="center" class="spacingAtTop">
    <s:if test="%{!userCreated}" >
        <input id="submit_btn" class="buttonGeneric" type="submit" value="<s:text name='Add User'/>" onclick="selectAllOptions(this.form['list2'])"/>
    </s:if>
    <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
           onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
</div>


<script type="text/javascript">	
function fillCertificateDetails(brand,categoryLevel,categoryName,series,certificationName) {	
if(certificationName && brand){
	 var certificateName =certificationName.value;
	   var brand = brand.value;
	    twms.ajax.fireJavaScriptRequest("list_certificate_details_for_certificateName.action",{    		
	            certificateName: certificateName,
	            brand:brand
	        }, function(details) {
	        	for(var i=0; i<details.length;i++){
	        		 categoryLevel.innerHTML="<tr><td>"+details[i].categoryLevel+"</td></tr>";
	        	        categoryName.innerHTML="<tr><td>"+details[i].categoryName+"</td></tr>";
	        	}
	        }
	    );    
	    }		
}
</script>
</s:form>
</div>
</div>
</u:body>
<authz:ifPermitted resource="settingsCreateUserReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('createUserForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('createUserForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>