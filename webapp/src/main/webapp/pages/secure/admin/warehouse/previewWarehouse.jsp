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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
	<head>
		<meta http-equiv="Context-Type"	content="text/html; charset=ISO-8859-1">
		<title><s:text name="title.common.warranty"/></title>
        <s:head theme="twms" />
        <u:stylePicker fileName="preview.css"/>
	</head>
	
	<u:body>
		<div dojoType="dijit.layout.LayoutContainer">
			<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.manageWarehouse.locationInfo"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div"><jsp:include flush="true" page="warehouse_locationInfo.jsp"/></div>
				</div>
				
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.manageWarehouse.userInfo"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div"><jsp:include flush="true" page="warehouse_userInfo.jsp"/></div>
				</div>
		    <s:if test="!isShipmentThroughCEVA()">
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.manageWarehouse.shipperFreightCarriers"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
						<div class="section_header">
							<s:text name="label.manageWarehouse.shippers"/>
						</div>
						<table>
							<tbody>
								<tr class="title">
									<th class="warColHeader" width="40%"><s:text name="label.manageWarehouse.shipper"/></th>
									<th class="warColHeader" width="15%"><s:text name="label.manageWarehouse.accountNumber"/></th>
								</tr>
								<s:iterator value="warehouse.warehouseShippers" status="shipper" id="shippers">
									<tr>
										<td>
											<s:property value="forCarrier.name"/>
										</td>
										<td>
											<s:property value="accountNumber"/>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</div>
				</div>
			</s:if>
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.manageWarehouse.binsInfo"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
						<div class="section_header">
							<s:text name="label.manageWarehouse.bins"/>
						</div>
						<table>
							<tbody>
								<s:iterator value="bins">
									<tr>
										<td>
										<s:property/>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</div>
				</div>
				
			</div>
		</div>
	</u:body>
</html>