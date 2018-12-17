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
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<%-- FIXME: something like <s:property value="#session['session.cssTheme']"/>, someone plz set session.cssTheme it in session and change this --%>
<u:stylePicker fileName="inventoryForm.css"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.TabContainer");
</script>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
            <div dojoType="dijit.layout.TabContainer" layoutAlign="client" tabPosition="bottom" id="tabs">

                <div dojoType="dijit.layout.ContentPane" title="Equipment Info" style="overflow-x:hidden; overflow-y:scroll">
                    <%@include file="inventory_equipmentinfo.jsp"%>
                </div>
  				
  				<div dojoType="dijit.layout.ContentPane" title="Major Components" style="overflow-x:hidden; overflow-y:scroll">
                    <%@include file="inventory_majorcomponents.jsp"%>
                </div>

                <div dojoType="dijit.layout.ContentPane" title="Transaction History">
                    <%@include file="inventory_transactionhistory.jsp"%>
                </div>

                <s:if test="%{isEverScrapped()}">
                <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.scrapHistory"/>">
                    <%@include file="scrap_details.jsp"%>
                </div>
                </s:if>

                <s:if test="isStockInventory() == false">
                    <div dojoType="dijit.layout.ContentPane" title="Warranty Coverages">
                        <%@include file="inventory_warrantycoverages.jsp"%>
                    </div>
                </s:if>

                <div dojoType="dijit.layout.ContentPane" title="Claim History">
                    <%@include file="inventory_warrantyclaimhistroy.jsp"%>
                </div>
               <s:if test="!isBuConfigAMER()">
                <div dojoType="dijit.layout.ContentPane" title="Service Campaign">
                    <jsp:include flush="true" page="inventory_campaigns.jsp"/>
                </div>
                </s:if>
                <s:else>
                  <div dojoType="dijit.layout.ContentPane" title="Field Product Improvement">
                    <jsp:include flush="true" page="inventory_campaigns.jsp"/>
                </div>
                </s:else>
                
            </div>
    </div>    
</u:body>
</html>
