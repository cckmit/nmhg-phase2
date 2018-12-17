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
<%@taglib prefix="authz" uri="authz"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
<title><s:text name="title.common.warranty" /></title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<script language="javascript" type="text/javascript">
	mm='JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER';
	mm=mm.split(', ');
	//dd='Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday';
	//dd=dd.split(', ')
	var t=new Date();
	var year = t.getYear();
	if (year < 1900) {
	year+=1900;
	}
	dateStr= mm[t.getMonth()] +' '+ t.getDate()  +', '+ year;
</script>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="base.css"/>

<style type="text/css">
.top_Header{
	font-size:11px;
	font-weight:700;
	color:#333;
	padding:0 10px;
  }
.ItemsHdrAction{
	color:#324162;
	font-family: Arial, Helvetica, sans-serif;
	font-weight:bold;
	background:transparent url(image/actionFolders.gif) no-repeat scroll 0 0;
	height:43px;
	line-height:20px;
	padding-left:52px;
	padding-top:4px;
	vertical-align:top;
}

.ItemsHdrGoto{
    height:43px;
    line-height:20px;
    background:transparent url(image/goTo.gif) no-repeat ;
    color:#324162;
    font-family: Arial, Helvetica, sans-serif;
    font-weight:bold;
    height:43px;
    line-height:20px;
    padding-left:52px;
    padding-top:3px;
    vertical-align:top;
}
.ItemsHdrQuickSearch{
	color:#324162;
	font-family: Arial, Helvetica, sans-serif;
	background:transparent url(image/quickSearch.gif) no-repeat scroll 0 0;
    font-weight:bold;
    height:43px;
    line-height:20px;
    padding-left:52px;
    padding-top:3px;
    vertical-align:top;
}
.ItemsHdrCommonAction{
	color:#324162;
    font-family: Arial, Helvetica, sans-serif;
    font-weight:bold;
    background:transparent url(image/commonActions.gif) no-repeat scroll 0 0;
    height:43px;
    line-height:20px;
    padding-left:52px;
    padding-top:1px;
    vertical-align:top;
}
.ItemsLabels{
    font-family:Arial, Helvetica, sans-serif;
    font-size:9pt;
    font-weight:400;
    color:#111111;
    padding:2px 0 2px 54px;
}
.ItemsLabels a{
	cursor: pointer;
	padding-left:5px;
}
.ItemsSubHdr{
	height:20px;
    line-height:20px;
    font-family: Arial, Helvetica, sans-serif;
    font-size:8pt;
    font-weight:700;
    color:#000000;
    background-color:#EBEBEB;
    padding-left:5px;
}
.accordianLeftArrow{
	float:right;
	background:transparent url(image/accrodionImages.png) no-repeat right center;
    background-position: -10px -10px;
	cursor:pointer;
	margin:4px 5px 0 0;
	width:8px;
	height:8px;
}
.calendarText{
	float:left;
	font-family:Arial, Helvetica, sans-serif;
	font-size:8pt;
	font-weight:700;
	margin-left:4px;
}
</style>

<s:head theme="twms"/>
<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
<script type="text/javascript" src="scripts/ui-ext/common/RefreshManager.js"></script>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.BorderContainer");
    
	dojo.require("twms.net.SessionTimeoutNotifier");
	
	<s:if test="allowSessionTimeOut">
	    // Session Timeout Notifier.
	    dojo.addOnLoad(function() {
	        window.sessionTimeoutNotifier = new twms.net.SessionTimeoutNotifier({
	            sessionTimeOutInterval: <s:property value="sessionTimeOutInterval" />
	        });
	    });
	 </s:if>
	 <s:if test="#session['CMSINFO'] != null">	 
	 dojo.addOnLoad(function() {
	    	parent.publishEvent("/tab/open", {
	    	url: "<s:property value="#session.CMSINFO"/>",
	    	label: "Truck Serial Number",
	    	decendentOf: "Home",
	    	forceNewTab: true
	    	});
	      
	    });
		<s:set name="CMSINFO" value="" scope="session"/>
	 </s:if>

</script>
<authz:ifUserNotInRole roles="customer">
    <script type="text/javascript" src="scripts/navigator.js"></script>
</authz:ifUserNotInRole>
<script type="text/javascript" src="scripts/testingScripts/testHome_jsp.js"></script>
<script type="text/javascript" src="scripts/Home.js"></script>
<%@include file="/i18N_javascript_vars.jsp"%>

</head>

<u:body>
<div dojoType="dijit.layout.BorderContainer" id="rootLayoutContainer" gutters="false" liveSplitters="false">
	<div dojoType="dijit.layout.ContentPane" class="dojoMenuBar2" region="top">
		<div class="headerMenuPane">
				<table border="0" align="left">
					<tr>
						<td><img src="image/logo_NMHG.gif" height="35" width="225"/></td>
					</tr>
				</table>
				<table border="0" cellspacing="1" cellpadding="1" align="right">
				<tr>
					<td align="right">
						<s:if test="isLoggedInUserADealer() && getBelongsToOrganizations().size()>1">
							<jsp:include page="mainPage/active_dealership.jsp"/>
						</s:if>
					</td>
						<s:if test="applicationSettings.appSSOEnabled && appSSOEnabledForLoggedInUser()">
							<td class="top_Header" nowrap="nowrap">
								<a href=<%request.getContextPath();%>"goToFleet.action">Go to Fleet</a>
							</td>
						</s:if>
					<td class="top_Header" nowrap="nowrap"><s:text name="home_jsp.accordionPane.welcome" /> <s:property value="userForDisplay"/></td>
					<td class="top_Header" nowrap="nowrap">
						<authz:ifUserInRole roles="admin,internalUserAdmin,inventoryAdmin">
							<a href="#" class="removeUnderline" onClick="openHelp('Admin')"><s:text name="home_jsp.menuBar.help" /></a>
						</authz:ifUserInRole>
						<authz:ifUserNotInRole roles="admin,internalUserAdmin,inventoryAdmin">
							<authz:ifProcessor>
								<a href="#" class="removeUnderline" onClick="openHelp('Processor')" ><s:text name="home_jsp.menuBar.help" /></a>
							</authz:ifProcessor>					
							<authz:ifUserNotInRole roles="processor">
								<authz:ifUserInRole roles="dealer,dealerWarrantyAdmin,dealerSiteAdmin,partInventoryDealer,dealerSalesAdministration,dealerAdministrator
										,dealersalesadmin,dealerpartreturn">
									<a href="#" class="removeUnderline" onClick="openHelp('Dealer')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="dsm">
									<a href="#" class="removeUnderline" onClick="openHelp('DSM')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="receiver">
									<a href="#" class="removeUnderline" onClick="openHelp('Receiver')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="inspector">
									<a href="#" class="removeUnderline" onClick="openHelp('Inspector')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="supplier">
									<a href="#" class="removeUnderline" onClick="openHelp('Supplier')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="sra">
									<a href="#" class="removeUnderline" onClick="openHelp('SRA')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="partshipper">
									<a href="#" class="removeUnderline" onClick="openHelp('PartShipper')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<%-- <authz:ifUserInRole roles="system">
									<a href="#" class="removeUnderline" onClick="openHelp('system')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole> --%>
								<authz:ifUserInRole roles="warrantyregistrationadvisor,dsmAdvisor,cpAdvisor,sysAdvisor">
									<a href="#" class="removeUnderline" onClick="openHelp('Advisor')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="recoveryProcessor,supplierRecoveryInitiator">
									<a href="#" class="removeUnderline" onClick="openHelp('Supplier Recovery Processor')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								<authz:else>
								<authz:ifUserInRole roles="readOnly">
									<a href="#" class="removeUnderline" onClick="openHelp('Dealer')" ><s:text name="home_jsp.menuBar.help" /></a>
								</authz:ifUserInRole>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
								</authz:else>
							</authz:ifUserNotInRole>
					
							<%-- <authz:ifUserNotInRole roles="customer">
								<s:if test="isLoggedInUserADirectCustomer() == true">
									<a href="#" class="removeUnderline" onClick="openHelp('DCC')" ><s:text name="home_jsp.menuBar.help" /></a>
								</s:if>
							</authz:ifUserNotInRole> --%>									
						</authz:ifUserNotInRole>
					</td>
					<td><a href="j_acegi_logout"><img border="0" align="absmiddle" alt=<s:text name="home_jsp.fileMenu.logout"/> title=<s:text name="home_jsp.fileMenu.logout" /> src="image/logoutIco_new.gif"/></a></td>
				</tr>
			</table>
		</div>
	</div>
	
    <div dojoType="dijit.layout.ContentPane" region="bottom" id="footer">
        <a href="http://www.tavant.com" id="tavantLogoHolder"><img src="image/tavant.png" alt="Tavant" /></a>
        <div class="footerData">
            <s:property value="revision"/>
            <s:text name="common.copyrightNotice"/>
            <a href="javascript:void(0);"><s:text name="common.privacyPolicy"/></a>
        </div>
    </div>

    <authz:ifUserNotInRole roles="customer">
        <div dojoType="dijit.layout.BorderContainer" id="split" orientation="horizontal" splitter="true" gutters="false" liveSplitters="false" persist="true" live="true"
             activeSizing="false" region="left"  class="rootSplitContainer homePageMainSection"
             persist="false" style="width: 24%">
                <div class="startupLid" id="changingDealershipLid" style="display:none">
                    <div class="startupLidIndication">
                        <div class="startupLidMessage">Please Wait...</div>
                    </div>
                </div>
                <div dojoType="dijit.layout.BorderContainer" id="navigator" sizeMin="0" sizeShare="100" gutters="false" liveSplitters="false" region="center">
                    <div dojoType="dijit.layout.ContentPane" id="dockNavigator" layoutAlign="top" class="welcomeBGForTabContainer">
                        <div class="calendarText"><s:property value="date"/></div>
                        <div class="accordianLeftArrow"></div>
                    </div>
                    <div dojoType="dijit.layout.AccordionContainer" id="accordion" containerNodeClass="accordionBody" allowCollapse="false" sizeShare="28" sizeMin="10" region="center">
                        <%@include file="accordion.jsp"%>
                    </div>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        <%--HACK : This function here ensures that the last accordion pane's body doesn't cut through the bottom pane
                                of the page in FF--%>
                        if (dojo.isFF) {
                            var footer = dijit.byId("footer");
                            dijit.byId("accordion").onSlideEnd = function() {
                                footer.hide();
                                setTimeout(function() {
                                    footer.show();
                                }, 0);
                            };
                        }
                    });
                </script>
                </div>                
        </div>
        <jsp:include page="tabContainer.jsp"/>
    </authz:ifUserNotInRole>
    <authz:ifUserInRole roles="customer">
        <jsp:include page="tabContainer.jsp"/>
    </authz:ifUserInRole>
</div>

</u:body>
</html>