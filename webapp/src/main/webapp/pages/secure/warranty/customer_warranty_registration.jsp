<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><s:text name="label.warranty.DeliveryReport" /></title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/CustomerWarrantyReg.js"></script>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
    <script type="text/javascript">
        dojo.require("dijit.form.NumberSpinner");
    </script>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults/>
	<s:form action="customer_register_warranty" theme="twms" validate="true" method="post">
		<div id="warranty_machine_info" style="border:1px solid #EFEBF7; margin:5px">
		<u:repeatTable id="productInfoTable" cssClass="admin_entry_table" cellpadding="2px"
					 	cellspacing="0" width="100%" theme="twms">
			<thead>
				<tr class="admin_table_header">
	               <th width="98%"><s:text name="label.machineInfo" /></th>
	               <th width="2%">
	                   <u:repeatAdd id="productAdder" theme="twms">
	                       <img id="addProduct" src="image/addRow_new.gif" border="0" style="cursor: pointer;" 
	                       		title="<s:text name="button.common.add" />" />
	             	   </u:repeatAdd>
	               </th>
	           </tr>
			</thead>
			<u:repeatTemplate id="productInfoBody" value="serialNumberList" index="productIndex"
				theme="twms">
				<tr index="#productIndex">

				<td>
				<table cellpadding="1" cellspacing="2" border="0" class="bgcolor">
					<tr>
						<td class="non_editable"><s:text
							name="label.productNameAndModel" />:</td>
						<td>
							<input dojoType="twms.widget.Select"
								autocomplete="false" name="product[#productIndex]" 
								dataProviderClass="tavant.twms.ComboBoxDataProvider"
								dataUrl='product_model_list.action?startsWith=%{searchString}'
								style="width: 250px;" maxListLength="20"
								id="product#productIndex"/>
								<script type="text/javascript">
								    dojo.addOnLoad ( function() {
								    	dojo.connect(dijit.byId("product#productIndex"), "onChange", function(value) {
								    			showMachineInfo('#productIndex');
										});
								  		dijit.byId("product#productIndex").setState({label : '<s:property value="product[#productIndex].productType.name"/><s:property value="product[#productIndex]!=null?' - ':''"/><s:property value="product[#productIndex].model"/>',
												   value : '<s:property value="product[#productIndex].id"/>'});
	    							});
								</script>
						</td>
						<td class="non_editable"><s:text name="label.serialNumber" />:</td>
						<td><s:textfield name="serialNumberList[#productIndex]" id="serialNumber_#productIndex" 
							onchange="showMachineInfo('#productIndex')"/></td>
					</tr>
					
					<tr>
						<td class="non_editable"><s:text name="label.deliveryDate" />:</td>
						<td><sd:datetimepicker name='deliveryDateList[#productIndex]' value='%{deliveryDateList[#productIndex]}' id='deliveryDate_#productIndex' />
							<script type="text/javascript">
							    dojo.addOnLoad ( function() {
							    	dojo.connect(dijit.byId("deliveryDate_#productIndex"), "onChange", function(value) {
							    			showMachineInfo('#productIndex');
									});
    							});
							</script>
						</td>
						<td class="non_editable"><s:text name="label.hoursOnMachine" />:</td>
						<td><input dojoType="dijit.form.NumberSpinner"
							value='<s:property value="hoursOnMachineList[#productIndex]"/>'
							name="hoursOnMachineList[#productIndex]" delta="1" min="0" max="9999999999"
							signed="never" separator="" maxlength="10"
							widgetId="hoursOnMachine_#productIndex"/>
							<script type="text/javascript">
							    dojo.addOnLoad ( function() {
							    	dojo.connect(dijit.byId("hoursOnMachine_#productIndex"), "onchange", function(value) {
							    			showMachineInfo('#productIndex');
									});
							    	dojo.connect(dijit.byId("hoursOnMachine_#productIndex"), "onblur", function(value) {
							    			showMachineInfo('#productIndex');
									});
    							});
							</script>
						</td>
					</tr>
					<tr>
						<td colspan="4"><div id="itemDetails_#productIndex"></div></td>
					</tr>	
				</table>
				</td>
				<td style="border-left:1px solid #DCD5CC; vertical-align:top">
				<br/>
					<u:repeatDelete id="productDeleter_#productIndex" theme="twms">
						<img id="deleteProduct" src="image/remove.gif" border="0" 
							style="cursor: pointer;"  title="<s:text name="button.common.delete" />"/>	
					</u:repeatDelete>
				</td>
			</tr>
		</u:repeatTemplate>
		
		</u:repeatTable>
		</div>

		<div id="submit" align="center"><input id="submit_btn"
			class="buttonGeneric" type="submit"
			value='<s:text name="label.register"/>' /><input
			class="buttonGeneric" type="button"
			value='<s:text name="label.cancel"/>' onclick="closeCurrentTab()" /></div>
	</s:form></div>
	</div>
</u:body>
</html>
