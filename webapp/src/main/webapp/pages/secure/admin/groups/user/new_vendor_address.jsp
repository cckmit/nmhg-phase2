<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

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
   dojo.require("dijit.layout.TitlePane");
</script>

</head>

<u:body>
	<u:actionResults />
	<div dojoType="dijit.layout.LayoutContainer" bgColor="#FFF" style="width: 100%; height: 100%;">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">

                <table width="100%">
                        	<tbody>
                            	<tr>
                          <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="accordion_jsp.accordionPane.userLocationMgmt.createLocation"/></td>
                        </tr>
                        </tbody>
                </table>
                     <table width="100%" cellpadding="0" cellspacing="0" class="form" align="center" style="background: #F3FBFE">
                         <tr>
                            <td class="non_editable labelStyle"><s:text name="label.common.address.line1"/></td>
                            <td colspan="3"><s:textfield maxlength="255" name="supplierLocation.address.addressLine1" id="userAddress1" cssStyle="width: 50%"/></td>
                         </tr>
                         <tr>
                             <td class="non_editable labelStyle"><s:text name="label.common.address.line2"/></td>
                             <td colspan="3"><s:textfield maxlength="255" name="supplierLocation.address.addressLine2" id="userAddress2" cssStyle="width: 50%"/></td>
                         </tr>
                         <tr>
                              <td class="non_editable labelStyle"><s:text name="label.manageWarehouse.contactPersonName"/></td>
                              <td colspan="3"><s:textfield maxlength="255" name="supplierLocation.address.contactPersonName" id="contactPersonName" cssStyle="width: 50%"/></td>
                          </tr>
                         <tr>
                         		<td class="non_editable labelStyle"><s:text name="label.country" /></td>
                         		<td>   <s:select label ="Country" id="country_company_new"
                                             name="supplierLocation.address.country"
                                             list="countryList" required="true" theme="twms"/>


                         </td>
                         <td class="non_editable labelStyle"><s:text name="label.state" /></td>
                         <td>
                              <s:textfield id="free_text_state_company_new" maxlength="255" name="stateCode" />
                         	 </td>
                         </tr>

                           <tr>
                         		<td class="non_editable labelStyle"><s:text name="label.city" /></td>
                         		<td>

                         	      <s:textfield id="free_text_city_company_new" maxlength="255" name="cityCode" />
                         		</td>
                         		<td class="non_editable labelStyle"><s:text name="label.zip" /></td>
                         			<td>
                         		      <s:textfield id="free_text_zip_company_new" maxlength="255" name="zipCode" />
                         		    </td>
                         		    </tr>

                         			<tr>
                         				<td class="non_editable labelStyle"><s:text name="label.common.phone" /></td>
                         				<td><s:textfield maxlength="255" name="supplierLocation.address.phone" /></td>

                         				<td class="non_editable labelStyle"><s:text name="label.common.email" /></td>
                         				<td><s:textfield maxlength="255" name="supplierLocation.address.email" /></td>
                         			</tr>

                         			<tr>
                         				<td class="non_editable labelStyle"><s:text name="label.common.fax" /></td>
                         				<td><s:textfield maxlength="255" name="supplierLocation.address.fax" /></td>
                         			</tr>
                         			<tr>
                         					<td class="non_editable labelStyle"><s:text
                         						name="label.common.location_code" /></td>
                         					<td>
                         						 <s:if test="supplierLocation == null || supplierLocation.id == null">
                         							<s:textfield maxlength="255" name="locationCode" />
                         						</s:if>
                         					    <s:else>
                         					   	    <s:property value="supplierLocation.code" />
                         						</s:else>
                         					</td>
                         			</tr>

                     </table>
                     <div id="separator"></div>
                     	</div>
                     		<div id="submit" align="center" class="spacingAtTop">
                     		<input id="submit_btn"
                     			class="buttonGeneric" type="button"  onclick="createNewAddress('<s:property value='supplier.id'/>')"
                     			value="<s:text name='button.common.save'/>" />
                     		<input id="cancel_btn"
                     			class="buttonGeneric" type="button"
                     			value="<s:text name='button.common.cancel'/>"
                     			onclick="javascript: dojo.byId('showithere').innerHTML = '';" />
                     		</div>

        </div>
	</div>
</u:body>
</html>