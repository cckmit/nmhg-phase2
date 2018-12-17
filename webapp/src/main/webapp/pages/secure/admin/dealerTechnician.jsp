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
<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <script type="text/javascript">
        dojo.require("dojox.layout.ContentPane");
        dojo.addOnLoad(function() {

            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
            dojo.html.hide(dojo.byId("dealerNumberText"));
            dojo.html.hide(dojo.byId("toggleToDealerName"));
            dojo.html.hide(dojo.byId("loadingIndicationDiv"));

            dojo.connect(dijit.byId("dealerNameAutoComplete"), "onChange", function() {
                populateDataForDealer(dijit.byId("dealerNameAutoComplete").getValue());
            });
            dojo.connect(dijit.byId("dealerNumberAutoComplete"), "onChange", function() {
                populateDataForDealer(dijit.byId("dealerNumberAutoComplete").getValue());
            });

        });

        function showDealerNumber() {
            dojo.html.show(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(false);
            dojo.html.show(dojo.byId("dealerNumberText"));
            dojo.html.show(dojo.byId("toggleToDealerName"));
            dojo.html.hide(dijit.byId("dealerNameAutoComplete").domNode);
            dijit.byId("dealerNameAutoComplete").setDisabled(true);
            dojo.html.hide(dojo.byId("dealerNameText"));
            dojo.html.hide(dojo.byId("toggleToDealerNumber"));
        }

        function showDealerName() {
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
            dojo.html.hide(dojo.byId("dealerNumberText"));
            dojo.html.hide(dojo.byId("toggleToDealerName"));
            dojo.html.show(dijit.byId("dealerNameAutoComplete").domNode);
            dijit.byId("dealerNameAutoComplete").setDisabled(false);
            dojo.html.show(dojo.byId("dealerNameText"));
            dojo.html.show(dojo.byId("toggleToDealerNumber"));
        }

        function populateDataForDealer(/*dealerid*/ id) {
            dojo.html.hide(dojo.byId("technicianData"));
            dojo.html.show(dojo.byId("loadingIndicationDiv"));
            twms.ajax.fireHtmlRequest("list_dealer_technician.action", {dealerId:id}, function(data) {
                dijit.byId("technicianData").setContent(data);
                dojo.html.show(dojo.byId("technicianData"));
                dojo.html.hide(dojo.byId("loadingIndicationDiv"));
            });
        }

    </script>
</head>
<u:body>
	<u:actionResults />

            <div class="admin_section_div" style="width:99%.5%">
            <div id="dealerSummary_header" class="section_header">
                <s:text name="label.technician.dealerTechnicianDetails"/>
            </div>
            <table class="grid" cellpadding="0" cellspacing="0">
                <tr>
                    <td id="dealerNameText" class="labelStyle" width="15%" nowrap="nowrap">
                        <s:text name="label.common.dealerName"/>:
                    </td>
                    <td id="dealerNumberText" width="15%" nowrap="nowrap" class="labelStyle" >
                        <s:text name="label.common.dealerNumber"/>:
                    </td>
                    <td width="25%">
                        <sd:autocompleter id='dealerNameAutoComplete' href='list_dealer_names_dealer_summary.action' name='forDealer' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' />

                        <sd:autocompleter id='dealerNumberAutoComplete' href='list_dealer_numbers_dealer_summary.action' name='forDealer' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                             src="image/indicator.gif" alt="Loading..."/>
                    </td>
                    <td>
                        <div id="toggle" style="cursor:pointer;">
                            <div id="toggleToDealerNumber" class="clickable" onClick="showDealerNumber()">
                                <s:text name="Specify Dealer Number"/>
                            </div>
                            <div id="toggleToDealerName" class="clickable" onClick="showDealerName()">
                                <s:text name="Specify Dealer Name"/>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>

        <div dojoType="dojox.layout.ContentPane" id="technicianData"></div>
        <div id="loadingIndicationDiv" style="width: 100%; height: 88%;">
            <div class='loadingLidThrobber'>
                <div class='loadingLidThrobberContent'>Loading...</div>
            </div>
        </div>

    </div>
</u:body>
</html>