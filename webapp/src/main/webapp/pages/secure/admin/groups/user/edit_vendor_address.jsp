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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("twms.widget.ValidationTextBox");
     dojo.require("twms.widget.TitlePane");
</script>

<div dojoType="twms.widget.TitlePane" id="supplier_address" bgColor="#FFF" style="width:100%; height:100%;">
 <div dojoType="dijit.layout.ContentPane" layoutAlign="top">
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
</div>