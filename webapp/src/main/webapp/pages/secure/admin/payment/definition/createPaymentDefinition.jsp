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

<%@ taglib prefix="s" uri="/struts-tags" %>
<!-- start: added for getting the Calendar to work -->
<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript"
        src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript"
        src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
      type="text/css">
<script type="text/javascript" src="scripts/admin.js"></script>
<u:stylePicker fileName="adminPayment.css"/>
<!-- end -->
<style type="text/css">
    .admin_section_div {
        background-color: #FCFCFC;
        border: 1px solid #EFEBF7;
        margin-top: 5px;
        margin-bottom: 5px;
        font-size: 10px;
    }

    .admin_subsection_div {
        background-color: #FCFCFC;
        border: 1px solid #DCD5CC;
        margin-top: 10px;
        margin-bottom: 5px;
        margin-left: 5px;
        margin-right: 5px;
        font-size: 10px;
    }

    .admin_section_heading {
        background-color: #F3FBFE;
        padding: 2px;
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 10px;
        font-weight: normal;
        font-style: normal;
        color: #636363;
    }

    .admin_section_subheading {
        background-color: #F5F5F5;
        padding: 2px;
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 10px;
        font-weight: normal;
        font-style: normal;
        color: #636363;
    }

    .admin_data_table {
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 10px;
        font-weight: bold;
        font-style: normal;
        color: #959494;
    }

    .admin_entry_table td {
        border: 1px solid #DCD5CC;
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 10px;
        font-weight: bold;
        font-style: normal;
        color: #707070;
    }

    .admin_entry_table th {
        border: 1px solid #DCD5CC;
        text-align: left;
    }

    .admin_selections {
        font-size: 10px;
    }

    .admin_table_header {
        background-color: #F3FBFE;
        border: none;
        padding: 2px;
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 10px;
        font-weight: bold;
        font-style: normal;
        color: #BBBBBB;
    }

    .buttonGeneric {
	background:transparent url(../../../image/buttonBg.jpg) repeat-x scroll left center;
	border:1px solid #EFEBF7;
	color:#666666;
	cursor:pointer;
	font-family:Arial,Verdana,sans-serif,Helvetica;
	font-size:7.5pt;
	font-style:normal;
	overflow:visible;
    }
</style>

<form action="create.action" method="post" name="baseForm"><s:actionerror
        theme="xhtml"/> <s:fielderror theme="xhtml"/>
    <div class="admin_subsection_div">
        <div class="admin_section_subheading"><s:text name="label.managePayment.validityPeriod"/></div>
        <table width="50%">
            <tr>
                <td><s:text name="label.managePayment.validityFrom"/>:</td>
                <td><s:textfield cssClass="admin_selections"
                                 name="paymentDefinition.startDate" id="startDate" size="12"
                                 maxlength="12"/> <img src="image/calendarIcon.gif"
                                                       alt="Choose Date" width="15" height="12" border="0"
                                                       id="startDate_Trigger" style="cursor: hand;"/>
                    <script
                            type="text/javascript">
                        Calendar.setup({
                            inputField     :    "startDate",
                            ifFormat       :    "%m/%d/%Y",
                            button         :    "startDate_Trigger"
                        });
                    </script>
                </td>

                <td><s:text name="label.managePayment.validityTo"/>:</td>
                <td><s:textfield cssClass="admin_selections"
                                 name="paymentDefinition.endDate" id="endDate" size="12"
                                 maxlength="12"/> <img src="image/calendarIcon.gif" alt="Choose Date" width="15"
                                                       height="12"
                                                       border="0" id="endDate_Trigger" style="cursor: hand;"/>
                    <script
                            type="text/javascript">
                        Calendar.setup({
                            inputField     :    "endDate",
                            ifFormat       :    "%m/%d/%Y",
                            button         :    "endDate_Trigger"
                        });
                    </script>
                </td>
            </tr>
        </table>
    </div>

    <div class="admin_subsection_div">
        <div class="admin_section_subheading"><s:text name="label.managePayment.paymentDefinitionCaps"/> <s:hidden
                name="sectionId"/> <s:iterator
                value="paymentDefinition.paymentSections" status="rowstatus">
            <div class="admin_subsection_div">
                <div class="admin_section_subheading">
                    <table width="100%">
                        <tr>
                            <td><s:property value="section.name"/></td>
                            <align
                            ="right" width="10%">
                            <td align="right"><img src="image/addRow.gif"
                                                   onclick="sendAdminRequestForAddingRowInPaymentDefinition('add_payment_definition_row.action','<s:property value="#rowstatus.index"/>')"
                                                   alt="add row" style="cursor:pointer;"/></td>
                        </tr>
                    </table>
                </div>
                <table class="admin_entry_table" cellpadding="2px" cellspacing="0"
                       width="100%">
                    <tr class="admin_table_header">
                        <th><s:text name="label.managePayment.paymentItem"/></th>
                        <th><s:text name="label.managePayment.howToCompute"/></th>
                    </tr>
                    <s:iterator value="items" status="itemstatus">
                        <s:if test="#itemstatus.index==items.size-1">
                            <tr>
                                <td><s:textfield readonly="true" cssClass="admin_selections" value="%{name}"
                                                 name="paymentItem[%{#rowstatus.index}][%{#itemstatus.index}]"/></td>
                                <td><s:textfield cssClass="admin_selections"
                                                 value="%{formula}"
                                                 name="howToCompute[%{#rowstatus.index}][%{#itemstatus.index}]"/></td>
                            </tr>
                        </s:if>
                        <s:else>
                            <tr>
                                <td><s:textfield cssClass="admin_selections" value="%{name}"
                                                 name="paymentItem[%{#rowstatus.index}][%{#itemstatus.index}]"/></td>
                                <td><s:textfield cssClass="admin_selections"
                                                 value="%{formula}"
                                                 name="howToCompute[%{#rowstatus.index}][%{#itemstatus.index}]"/></td>
                            </tr>
                        </s:else>
                    </s:iterator>
                </table>
            </div>
        </s:iterator></div>
        <div class="buttonWrapperPrimary"><input type="submit" value="<s:text name="button.common.create"/>"
                                                 class="buttonGeneric"/></div>
</form>
