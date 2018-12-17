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
        <u:stylePicker fileName="common.css"/>
        <u:stylePicker fileName="base.css"/>
	</head>
	
	<u:body>
		<div dojoType="dijit.layout.LayoutContainer">
			<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.equipmenInfo"/>" style="overflow-X: hidden; overflow-Y: auto">
				<div class="policy_section_div">
					<jsp:include flush="true" page="../../inventory/inventory_equipmentinfo.jsp"/>
					<jsp:include flush="true" page="../../inventory/inventory_majorcomponents.jsp"/>
				</div>
				</div>
				
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.transactionHistory"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
					<jsp:include flush="true" page="../../inventory/inventory_transactionhistory.jsp"/>
					</div>
				</div>

                <s:if test="%{isEverScrapped()}">
                <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.scrapHistory"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
					<jsp:include flush="true" page="../../inventory/scrap_details.jsp"/>
					</div>
				</div>
                </s:if>

                <s:if test="isStockInventory() == false">
					<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.common.warrantyCoverages"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
						<jsp:include flush="true" page="../../inventory/inventory_warrantycoverages.jsp"/>
						</div>
					</div>
				</s:if>
				
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.claimHistory"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
					<jsp:include flush="true" page="../../inventory/inventory_warrantyclaimhistroy.jsp"/>
					</div>
				</div>
				
				<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.fieldModification"/>" style="overflow-X: hidden; overflow-Y: auto">
					<div class="policy_section_div">
					<jsp:include flush="true" page="../../inventory/inventory_campaigns.jsp"/>
					</div>
				</div>
			</div>
		</div>
	</u:body>
</html>