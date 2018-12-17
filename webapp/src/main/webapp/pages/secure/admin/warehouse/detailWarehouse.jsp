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
<%@ taglib prefix="authz" uri="authz" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>

</head>
	
	<u:body>
	<u:actionResults/>
		<s:form name="baseform" id="baseform" method="post">
			<s:if test="warehouse.id != null">
				<s:hidden name="id" value="%{warehouse.id}"></s:hidden>
			</s:if>
			<div class="policy_section_div">
				<div class="section_header">
					<s:text name="label.common.details"></s:text>
				</div>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
				<tbody>
				<tr>
				  <td colspan="6" height="5"></td>
				  </tr>
				<tr>
				<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.manageWarehouse.wareHouseName"/>:</td>
                    <td colspan="5">
                        <s:if test="warehouse.id != null">
                            <s:property value="%{warehouse.location.code}"/>
                        </s:if>
                        <s:else>
                            <s:textfield name="warehouse.location.code"/>
                        </s:else>
                    </td>
				</tr>
				<tr>
				<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.manageWarehouse.businessName"/>:</td>
                    <td colspan="5">
                            <s:textfield name="warehouse.businessName"/>
                    </td>
				</tr>
				<tr>
				  <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.address.line1"/>:</td>
				  <td colspan="5"><s:textfield name="warehouse.location.address.addressLine1" size="94"/></td>
				 </tr>
				 <tr>
				  <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.address.line2"/>:</td>
				  <td colspan="5"><s:textfield name="warehouse.location.address.addressLine2" size="94"/></td>
				 </tr>
				  <tr>
				  <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.city"/>:</td>
				  <td><s:textfield name="warehouse.location.address.city"/></td>
				  <td width="7%" class="labelStyle"><s:text name="label.common.state"/>:</td>
				  <td><s:textfield name="warehouse.location.address.state"/></td>
				  </tr>
				   <tr>
				  <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.country"/>:</td>
				  <td><s:textfield name="warehouse.location.address.country"/></td>
				  <td width="8%" class="labelStyle"><s:text name="label.common.zipCode"/>:</td>
				  <td><s:textfield name="warehouse.location.address.zipCode"/></td>
				  </tr>
				  <tr>
					  <td class="labelStyle" nowrap="nowrap"><s:text name="label.manageWarehouse.contactPersonName"/>:</td>
					  <td colspan="2"><s:textfield name="warehouse.contactPersonName"/></td>
				  </tr>
				</tbody>
				</table>
				
			</div>
			
			<div class="policy_section_div">
				<div class="section_header">
					<s:text name="label.common.users"></s:text>
				</div>
				<table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable" align="center">
					<tbody>
						<tr class="title">
						<th class="warColHeader"><s:text name="label.common.reciever"/></th>
						<th class="warColHeader"><s:text name="label.common.inspector"/></th>
						<th class="warColHeader"><s:text name="label.manageWarehouse.partShipper"/></th>
						</tr>
						<tr>
						<td width="30%">
						<table width="100%" cellpadding="0" cellspacing="0"  >
						
										<s:iterator value="recievers">
											<tr>
												<td style="border:none" align="center">
													<s:if test="warehouse.recievers.contains(top)">
														<input type="checkbox" name="warehouse.recievers" value="<s:property value="id"/>" checked="checked"/>
													</s:if>
													<s:else>
														<input type="checkbox" name="warehouse.recievers" value="<s:property value="id"/>"/>
													</s:else>
												</td>
												<td style="border:none">
													<s:property value="completeNameAndLogin"/>
												</td>
											</tr>
										</s:iterator>
									
						</table>
						</td>
						<td width="30%">
						<table width="100%" cellpadding="0" cellspacing="0" >
						
										<s:iterator value="inspectors">
											<tr>
												<td width="10%" style="border:none" align="center">
													<s:if test="warehouse.inspectors.contains(top)">
														<input type="checkbox" name="warehouse.inspectors" value="<s:property value="id"/>" checked="checked"/>
													</s:if>
													<s:else>
														<input type="checkbox" name="warehouse.inspectors" value="<s:property value="id"/>"/>
													</s:else>
												</td>
												<td style="border:none">
													<s:property value="completeNameAndLogin"/>
												</td>
											</tr>
										</s:iterator>
									
									</table>
						</td>
						<td width="30%">
						<table width="100%" cellpadding="0" cellspacing="0"  >

					
										<s:iterator value="partShippers">
											<tr>
												<td style="border:none" align="center">
													<s:if test="warehouse.partShippers.contains(top)">
														<input type="checkbox" name="warehouse.partShippers" value="<s:property value="id"/>" checked="checked"/>
													</s:if>
													<s:else>
														<input type="checkbox" name="warehouse.partShippers" value="<s:property value="id"/>"/>
													</s:else>
												</td>
												<td style="border:none">
													<s:property value="completeNameAndLogin"/>
												</td>
											</tr>
										</s:iterator>
									
									</table>
						</td>
						</tr>				
						</tbody>
						</table>
				
			</div>
			
		<s:if test="!isShipmentThroughCEVA()">
			<div class="policy_section_div" width="60%">
				<div class="section_header">
					<s:text name="label.manageWarehouse.shipperFreightCarriers"></s:text>
				</div>
				<u:repeatTable id="shippers_table" cssClass="grid borderForTable" 
				theme="twms"  cellspacing="0" cellpadding="0" cssStyle="margin:5px;width:60%">
					<thead>
						<tr class="title">
							<th class="warColHeader" width="40%"><s:text name="label.manageWarehouse.shipper"/></th>
							<th class="warColHeader" width="15%"><s:text name="label.manageWarehouse.accountNumber"/></th>
							<th class="warColHeader" width="5%">
								<u:repeatAdd id="shippers_adder" theme="twms">
									<img id="addShipper" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/>
								</u:repeatAdd>
							</th>
						</tr>
					</thead>
					<u:repeatTemplate id="mybody" value="warehouse.warehouseShippers" index="index" theme="twms">
				        <tr index="#index">
				        	<td>
				        		<s:select 
			                      id="shipper_#index" cssStyle="width:450px;"
							      name="warehouseShippers[#index].forCarrier.id"
							      value="%{warehouse.warehouseShippers[#index].forCarrier.id}"
							      listKey="id"
							      listValue="name"
							      list="getCarriers()"
			                      headerKey="null" headerValue="%{getText('label.common.selectHeader')}" />
				        	</td>
				        	<td>
				        		<s:textfield name="warehouseShippers[#index].accountNumber" value="%{warehouse.warehouseShippers[#index].accountNumber}"></s:textfield>
				        	</td>
				        	<td>
				                <u:repeatDelete id="shippers_deleter_#index"  theme="twms">
									<img id="deleteShipper" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteRow" />"/>
				                </u:repeatDelete>
			            	</td>
						</tr>
					</u:repeatTemplate>
				</u:repeatTable>
			</div>
		</s:if>
			<div class="policy_section_div">
				<div class="section_header">
					<s:text name="label.manageWarehouse.binsForWarehouse"></s:text>
				</div>
				<u:repeatTable id="bins_table" cssClass="grid borderForTable" 
				theme="twms"  cellspacing="0" cellpadding="0" cssStyle="margin:5px;">
					<thead>
						<tr class="title">
							<th class="warColHeader" width="91%"><s:text name="label.manageWarehouse.bins"/></th>
							<th class="warColHeader" width="9%">
								<u:repeatAdd id="bins_adder" theme="twms">
									<img id="addBin" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/>
								</u:repeatAdd>
							</th>
						</tr>
					</thead>
					<u:repeatTemplate id="mybody" value="bins" index="index" theme="twms">
		        <tr index="#index">
		        	<td>
		        		<s:textfield name="bins[#index]"></s:textfield>
		        	</td>
		        	<td>
                <u:repeatDelete id="bins_deleter_#index"  theme="twms">
                    <img id="deleteBin" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteRow" />"/>
                </u:repeatDelete>
            	</td>
						</tr>
					</u:repeatTemplate>
				</u:repeatTable>
			</div>
			<div align="center" class="spacingAtTop">
				<s:submit action="save_warehouse" cssClass="button" align="center" value="%{getText('button.common.submitCaps')}"></s:submit>
			</div>
		</s:form>
		
	</u:body>
<authz:ifPermitted resource="warrantyAdminManageWarehousesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>