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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title>:: <s:text name="title.common.warranty" /> ::</title>
<s:head theme="twms" />
<script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="claimForm.css"/>


<script type="text/javascript">
    function showPopup(id){
       twms.ajax.fireHtmlRequest("getAddresssDetail.action", {address:id}, function(data) {
           dojo.byId("showithere").innerHTML = data;
       });
    }

    function updateAddress(){
       var frm =document.getElementById('frmCreateSupplier');
        frm.action="updateSupplierAddress.action";
        frm.submit();
    }

    function cancelUpdateAddress(){
       dojo.byId("showithere").innerHTML = "";
    }

    function populateForm(supplierId){
       twms.ajax.fireHtmlRequest("populateFormForNewSupplierSite.action",{supplier:supplierId}, function(data) {
           dojo.byId("showithere").innerHTML = data;
       });
    }

    function createNewAddress(supplierId){
           var frm =document.getElementById('frmCreateSupplier');
           frm.action="createNewSupplierAddress.action";
           frm.submit();
        }
</script>

<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.ValidationTextBox");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });
</script>



<style type="text/css">
td.label {
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	font-style: normal;
	font-weight: normal;
	line-height: 20px;
	padding-left: 5px;
	text-align: left;
	vertical-align: top;
}
</style>

<u:stylePicker fileName="master.css" />
<u:stylePicker fileName="inventory.css" />
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">

<s:form action="submit_supplier" theme="twms" validate="true" id="frmCreateSupplier"
		method="post">

		<u:actionResults />
		<div dojoType="twms.widget.TitlePane" title="Supplier Details"
				labelNodeClass="sectionTitle">

                			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
                				<tbody>
                					<s:hidden id="supplierid" name="supplier" value="%{supplier.id}"/>
                					<tr>
                						<td width="20%" nowrap="nowrap" class="labelStyle" ><s:text name="label.supplierNumber"></s:text>:</td>

                						<td width="34%" class="label">
                						<s:if test="supplier == null || supplier.id == null">
                						<s:textfield
                							name="supplier.supplierNumber" />
                						</s:if>
                						<s:else>
                						<s:property
                							value="supplier.supplierNumber" />
                						</s:else>
                							</td>
                						<td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="columnTitle.partSource.supplier_name"/>:</td>
                						<td width="32%" class="label">
                						<s:if test="supplier == null || supplier.id == null">
                						<s:textfield
                							name="supplier.name"  />
                							</s:if>
                							<s:else>
                							<s:property
                							value="supplier.name"  />
                							</s:else>
                						</td>
                					</tr>
                				</tbody>
                			</table>

 <%-- Display current supplier address --%>

 <table id="supplier_items_table" class="grid borderForTable" cellspacing="0"
        cellpadding="0" style="width:100%">
     <thead>
         <tr class="row_head">
             <th width="14%"><s:text name="label.common.location_code" /></th>
             <th width="14%"><s:text name="label.manageWarehouse.contactPersonName" /></th>
             <th width="18%"><s:text name="columnTitle.common.address" /></th>
             <th width="14%"><s:text name="columnTitle.common.city" /></th>
             <th width="6%"><s:text name="columnTitle.common.state" /></th>
             <th width="6%"><s:text name="columnTitle.common.country" /></th>
             <th width="10%"><s:text name="label.common.zipCode" /></th>
             <th width="10%"><s:text name="label.common.phone" /></th>
             <th width="12%"><s:text name="label.common.email" /></th>
             <th width="5%" align="center"/>
         </tr>
     </thead>
     <tbody>
         <!-- Iterator over all supplier addresses -->
         <s:iterator value="supplier.locations" status="loc">
                <tr>
                     <td><s:property value="code"/> </td>
                     <td><s:property value="address.contactPersonName"/> </td>
                     <td><s:property value="address.addressLine1"/>,<s:property value="address.addressLine2"/>  </td>
                     <td><s:property value="address.city"/>  </td>
                     <td><s:property value="address.state"/>  </td>
                     <td><s:property value="address.country"/>  </td>
                     <td> <s:property value="address.zipCode"/>  </td>
                     <td> <s:property value="address.phone"/></td>
                     <td><s:property value="address.email"/> </td>
                     <td> <a id="edit_address_<s:property value='%{#loc.index'/>}" onclick="showPopup(<s:property value='address.id'/>)"> Edit </a> </td>

                </tr>
         </s:iterator>
     </tbody>

     </table>

<table width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td width="40%">&nbsp;</td>
        <td id="submitSection" align="center" class="buttons" style="padding-top: 20px;">
            <input type="button" id="create_new_site" onclick="populateForm('<s:property value='supplier.id'/>')" value='<s:text name="accordion_jsp.accordionPane.userLocationMgmt.createLocation"/>'/>
        </td>
    </tr>
 </table>
</div>


<br/><br/>

<s:if test="newAddressCreation">
    <div id="showithere">
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
</s:if>
<s:elseif test="editAddress">
<div id="showithere">
   <table width="100%" >
           	<tbody>
               	<tr>
             <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="columnTitle.partSource.address_details"/></td>
           </tr>
           </tbody>
   </table>

   <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background: #F3FBFE">
           <tbody>
               <tr>
                <s:hidden name="address" value="%{address.id}"/>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.sra.partSource.address.line1"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
                       name="address.addressLine1"
                       value='<s:property value="address.addressLine1"/>'/>
                       </td>

                   <td width="18%" class="labelNormal"><s:text name="label.sra.partSource.address.line2"/>:</td>
                   <td width="32%" >
                       <input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
                       name="address.addressLine2"
                       value='<s:property value="address.addressLine2"/>'
                       />
                       </td>
               </tr>

               <tr>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="columnTitle.common.city"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
                       name="address.city"
                       value='<s:property value="address.city"/>'/>
                   </td>
                   <td width="18%" class="labelNormal"><s:text name="columnTitle.common.state"/>:</td>
                   <td width="32%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
                       name="address.state"
                       value='<s:property value="address.state"/>'/>
                   </td>
               </tr>
               <tr>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.manageWarehouse.contactPersonName"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
                       name="address.contactPersonName"
                       value='<s:property value="address.contactPersonName"/>'/>
                   </td>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.zipCode"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="true" trim="true"
                       name="address.zipCode"
                       value='<s:property value="address.zipCode"/>'/>
                   </td>
               </tr>
               <tr>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="columnTitle.common.country"/>:</td>
                   <td width="34%" >
                   <s:select label ="Country" id="country_company_edit"
                                        name="address.country" list="countryList" required="true" theme="twms"/>
                   </td>
               </tr>
               <tr>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.email"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
                       name="address.email"
                       value='<s:property value="address.email"/>'/>
                   </td>
                   <td width="18%" class="labelNormal"><s:text name="label.common.phone"/>:</td>
                   <td width="32%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
                       name="address.phone"
                       value='<s:property value="address.phone"/>'/>


                   </td>
               </tr>
                <tr>
                   <td width="16%" nowrap="nowrap" class="labelNormal"><s:text name="label.common.fax"/>:</td>
                   <td width="34%" >
                   <input dojoType="twms.widget.ValidationTextBox" required="false" trim="true"
                       name="address.fax"
                       value='<s:property value="address.fax"/>'/>
                   </td>
               </tr>
           </tbody>
      </table>


    <table width="100%" cellpadding="0" cellspacing="0">
       <tr>
           <td width="40%">&nbsp;</td>
           <td id="submitSection" align="center" class="buttons" style="padding-top: 20px;">
               <input type="button" id="closePopup" onclick="updateAddress()" value='<s:text name="button.common.update"/>'/>

               <input type="button" id="cancelPopup" onclick="cancelUpdateAddress()" value='<s:text name="button.common.cancel"/>'/>
           </td>
       </tr>
    </table>
 </div>
</s:elseif>
<s:else>
<div id="showithere"></div>
</s:else>

</s:form>
</div>
</div>
</u:body>