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

<head>
    <title>
        <s:text name="accordion_jsp.accordionPane.dealerUserMgmt"/>
    </title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
    <script type="text/javascript" src="scripts/validateAddress.js"></script>
    <script type="text/javascript" src="scripts/manageLists.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.addOnLoad(function() {
            var validatableStateId = dijit.byId("validatable_state_company_new");
            var freeTextStateId = dojo.byId("free_text_state_company_new");
            var validatableCityId = dijit.byId("validatable_city_company_new");
            var freeTextCityId = dojo.byId("free_text_city_company_new");
            var validatableZipId = dijit.byId("validatable_zip_company_new");
            var freeTextZipId = dojo.byId("free_text_zip_company_new");
            var selectedVar = <s:property value="checkForValidatableCountry(user.address.country)"/>;
            dojo.html.setDisplay(validatableStateId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextStateId, !selectedVar);
            dojo.html.setDisplay(validatableCityId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextCityId, !selectedVar);
            dojo.html.setDisplay(validatableZipId.domNode, selectedVar);
            dojo.html.setDisplay(freeTextZipId, !selectedVar);
            dojo.connect(dijit.byId("country_company_new"), "onChange", function(value) {
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
            });
            dojo.connect(dijit.byId("validatable_state_company_new"), "onChange", function(value) {
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
            });
            dojo.connect(dijit.byId("validatable_city_company_new"), "onChange", function(value) {
                getZipsByCountryStateAndCity(value, dijit.byId("validatable_zip_company_new"),
                        true, '-1',
                        [
                            "free_text_zip_company_new"
                        ]);
            });
        });
    </script>

</head>

<div class="section_div">
    <div class="section_heading">
        <s:text name="label.manageProfile.userDetails"/>
    </div>
</div>
<table class="form">
<s:if test="user==null || user.id==null">
<s:hidden name="selectedBU" id="selectedBU"/>
<tr>
	<td class="non_editable"><s:text name="label.common.businessUnit"/></td>
    <td colspan="3"><s:property value="selectedBU.name"/></td>
</tr>
</s:if>
<tr>
    <td class="non_editable"><s:text name="label.dealerUser.firstName"/></td>
    <td><s:textfield maxlength="20" size="30" name="user.firstName" tabindex="1"/></td>
    <td class="non_editable"><s:text name="label.common.address.line1"/></td>
    <td><t:textarea name="user.address.addressLine1" id="userAddress1" cssStyle="width: 100%;" rows="1" tabindex="3"/></td>
</tr>
<tr>
    <td class="non_editable"><s:text name="label.dealerUser.lastName"/></td>
    <td><s:textfield maxlength="20" size="30" name="user.lastName" tabindex="2"/></td>
    <td class="non_editable"><s:text name="label.common.address.line2"/></td>
    <td><t:textarea name="user.address.addressLine2" id="userAddress2" cssStyle="width: 100%;" rows="1" tabindex="4"/></td>
</tr>

<tr>
     <td class="non_editable"><s:text name="label.manageProfile.locale"/></td>
    <td style="padding-left: 13 " >
    
    <s:select name="defaultLocale" list="listOfLocale"
                  id="locale" required="true" theme="twms"
                  listValue="description" listKey="locale"
                  headerKey='null' headerValue="%{getText('label.common.selectHeader')}"
                  value="%{getDefaultLocale()}" tabindex="5"/>
        
        </td>
                    <td class="non_editable">
                        <s:text name="label.country"/>
                    </td>
                    <td >
                        <s:select label="label.country" id="country_company_new"
                                  name="user.address.country"
                                  list="countryList" required="true" theme="twms" tabindex="6"/>
                    </td>
                    
                    <tr>
                     <td class="non_editable"><s:text name="label.common.email"/></td>
                    <td><s:textfield name="user.email" tabindex="7"/></td>
                    <td class="non_editable">
                        <s:text name="label.state"/>
                    </td>
                    <td>
                        <sd:autocompleter label='label.state' id='validatable_state_company_new' listenTopics='topic_state_company_new' name='user.address.state' tabindex='8' />
                        <s:textfield id="free_text_state_company_new" name="stateCode" maxlength="20"/>
                    </td>
                </tr>

                <tr>
                    <td class="non_editable"><s:text name="label.common.phone"/></td>
                    <td><s:textfield name="user.address.phone" tabindex="9" maxlength="20"/></td>
                    <td class="non_editable">
                        <s:text name="label.city" /></td>
                    <td>
                        <sd:autocompleter label='label.city' id='validatable_city_company_new' listenTopics='topic_city_company_new' name='user.address.city' required='true' tabindex='10' />
                        <s:textfield id="free_text_city_company_new" name="cityCode" maxlength="20"/>
                    </td>
                    </tr>
                    <tr>
                    <td class="non_editable"><s:text name="label.common.fax"/></td>
                    <td><s:textfield name="user.address.fax" tabindex="11" maxlength="20"/></td>
                    <td class="non_editable">
                        <s:text name="label.zip" />
                    </td>
                    <td>
                       <sd:autocompleter label='label.zip' id='validatable_zip_company_new' listenTopics='topic_zip_company_new' name='user.address.zipCode' tabindex='12' />
                      <s:textfield id="free_text_zip_company_new" name="zipCode" maxlength="20"/>
                    </td>
                </tr>
                <jsp:include page="rolesDetail.jsp" flush="true"/>	

</table>
<div class="section_div">
    <div class="section_heading">
        <s:text name="label.common.loginDetails"/>
    </div>
</div>

