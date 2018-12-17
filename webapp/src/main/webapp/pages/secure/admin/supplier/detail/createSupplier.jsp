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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title>:: <s:text name="title.common.warranty" /> ::</title>
<s:head theme="twms" />
<script type="text/javascript">
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.BorderContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.form.ComboBox");
    </script>
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="claimForm.css"/>

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


<script type="text/javascript">
        function closeCurrentTab() {
        	 closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
        function submitForm(button) {
		var frm =document.getElementById('frmCreateSupplier');    
    	if(button.value == 'Remove Suuplier')
    	{
    		frm.action='delete_supplier.action';
    	}
    	else 
    	{
    		frm.action='submit_supplier.action';
    	}
    	frm.submit();
        
    
}
</script>

<u:stylePicker fileName="master.css" />
<u:stylePicker fileName="inventory.css" />
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">

<s:form action="submit_supplier" theme="twms" validate="true" id="frmCreateSupplier" name="createform"
		method="post">
        <s:if test="validateDialog==false">  
		<u:actionResults />
		</s:if>
		<div dojoType="twms.widget.TitlePane" title="Supplier Details"
				labelNodeClass="sectionTitle">
				<jsp:include flush="true" page="supplierDetails.jsp"/>
		</div>	
	
		
	<s:if test="supplierContracts != null && supplierContracts.size() > 0">
		<div >
			<div dojoType="twms.widget.TitlePane" title="Contract Details"
				labelNodeClass="sectionTitle">
				<jsp:include flush="true" page="supplierContractDetails.jsp"/>
			</div>
		</div>
	</s:if>
	
	
		<jsp:include flush="true" page="supplierItems.jsp"/>
	    <br/>
		
		<div class="buttonWrapperPrimary">
			<input type="button"
				onClick="closeCurrentTab()" value="<s:text name='label.common.cancel' />" class="buttonGeneric" />
			<s:if test="supplier.id !=null">
			<input type="button" name ="Add" value="<s:text name='label.supplierManagement.updateSupplier' />" class="buttonGeneric" onClick="submitForm(this);"/>
			</s:if>
			<s:else>
			<input type="button" name ="Add" value="<s:text name='label.supplierManagement.addSupplier' />" class="buttonGeneric" onClick="submitForm(this);"/>
			</s:else>
		</div>
		
	</s:form>
</div>
</div>
<s:if test="validateDialog==true">   
           
                            <script type="text/javascript">
                          dojo.addOnLoad(function() {
                          dijit.byId("dlgAddItem").show();
                          
                         });
</script> 
</s:if> 
<div dojoType="twms.widget.Dialog" id="dlgAddItem"  bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"  title="<s:text name="label.contractAdmin.addSupplierPart" />" style="width: 50%">
           
            <div dojoType="dijit.layout.ContentPane">
                <form id="add_supplier_item" action="add_supplier_location_item">
                 <u:actionResults/>
                <s:hidden name="supplier"/>
                <s:hidden name="validateDialog" value="true"/>
                <table align="center" style="margin-top: 10px; width: 95%">
                  <tbody>
                    <tr>
                        <td>
                            <s:text name="label.common.partNumber" />:
                        </td>
                        <td>
                            <sd:autocompleter name="supplierItemLocation.itemMapping.fromItem" key='%{supplierItemLocation.itemMapping.fromItem.id}'
													keyName='supplierItemLocation.itemMapping.fromItem.id' value='%{supplierItemLocation.itemMapping.fromItem.number}' id='itemAutocompleter' showDownArrow='false' href='list_parts_for_supplier.action' />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <s:text name="columnTitle.duePartsInspection.supplier_part_no" />:
                        </td>
                        <td>
                            <s:textfield size="27" name="supplierItemLocation.itemMapping.toItem.number" id="supplierItemNumberText"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <s:text name="label.sra.partSouce.supPartDescription" />:
                        </td>
                        <td>
                            <s:textarea rows="3" cols="27" name="supplierItemLocation.itemMapping.toItem.description" id="supplierItemDescriptionText"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <s:text  name="label.common.location_code" />:
                        </td>
                        <td>
                            <s:textfield size="27" name="supplierItemLocation.locationCode" id="supplierLocationText" />
                        </td>
                    </tr>
                    
                    <tr>
                        <td>
                            <s:text name="label.sra.partSource.fromDate" />:
                        </td>
                        <td>
                            <sd:datetimepicker name="supplierItemLocation.fromDate" id='supplierItemFromDate' />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <s:text name="label.sra.partSource.toDate" />:
                        </td>
                        <td>
                            <sd:datetimepicker name="supplierItemLocation.toDate" id='supplierItemToDate' />
                        </td>
                    </tr>
                    </tbody>
                </table>
                 
                <div align="center" style="margin-top: 20px;" id="buttonsDiv">
                    <button class="buttonGeneric" id="btnAddItem" onclick="addSupplierItem()"><s:text name="button.label.add" /></button>
                 </div>
                </form>
            </div>
 </div>
<authz:ifPermitted resource="contractAdminManageSuppliersReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('frmCreateSupplier')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('frmCreateSupplier'))[i].disabled=true;
	        }
	        document.getElementById("buttonsDiv").style.display="none";
	    });
	</script>
</authz:ifPermitted>
</u:body>