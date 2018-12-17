<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<jsp:include page="/i18N_javascript_vars.jsp"/>

<style>
.spacingInTable{
padding-bottom:10px;
}
</style>


<script type="text/javascript">
    dojo.require("twms.widget.Select");
    dojo.addOnLoad(function() {
        dojo.connect(dijit.byId("firstTimeCustomer"), "_selectOption", "_changefirstTimeCustomer");
        dojo.connect(dijit.byId("transactionTypes"), "_selectOption", "_checksForTransactionTypes");
        _changefirstTimeCustomer();
        _checksForTransactionTypes();


    });
    var transactionTypes = {};
    function _changefirstTimeCustomer(evt) {
        var showFor = dijit.byId('firstTimeCustomer').getValue();
        if ("false" == showFor) {
            dijit.byId("ifPreviousOwner").setDisabled(false);
            dijit.byId("competitionType").setDisabled(false);
            dijit.byId("competitorMake").setDisabled(false);
            dijit.byId("competitorModel").setDisabled(false);

        } else if ("true" == showFor) {
            dijit.byId("ifPreviousOwner").setDisabled(true);
            dijit.byId("competitionType").setDisabled(true);
            dijit.byId("competitorMake").setDisabled(true);
            dijit.byId("competitorModel").setDisabled(true);
        }
        delete showFor;
    }

    function _checksForTransactionTypes(evt) {

        var showFor = dijit.byId('transactionTypes').getValue();
        if ("Cash Sales" == transactionTypes[showFor]) {
            dojo.byId("numberOfMonths").value = '';
            dojo.byId("numberOfYears").value = '';
            dojo.byId("numberOfYears").disabled = true;
            dojo.byId("numberOfMonths").disabled = true;
        } else if ("Installment Sale" == transactionTypes[showFor]) {
            dojo.byId("numberOfMonths").value = '';
            dojo.byId("numberOfYears").value = ''
            dojo.byId("numberOfYears").disabled = true;
            dojo.byId("numberOfMonths").disabled = true;
        } else if ("Others" == transactionTypes[showFor]) {
            dojo.byId("numberOfMonths").value = '';
            dojo.byId("numberOfYears").value = ''
            dojo.byId("numberOfYears").disabled = true;
            dojo.byId("numberOfMonths").disabled = true;
        }else {
            dojo.byId("numberOfYears").disabled = false;
            dojo.byId("numberOfMonths").disabled = false;
        }
        delete showFor;
    }
 </script>

<div id="warranty_marketing_info"  style="width:100%">
<div dojoType="twms.widget.TitlePane" title="Additional Information"
     id="marketing_info" labelNodeClass="section_header" open="true">
<s:iterator value="listOfTransactionTypes">
    <script type="text/javascript">
        transactionTypes['<s:property value="id"/>'] = '<s:property value="type"/>';
    </script>
</s:iterator>
<div style="width:100%">

<table class="grid" cellpadding="0" cellspacing="0" style="margin:5px 5px;width:98%;">
<tbody>
<tr>
<td>
<table>
<tbody>
<tr>
    <td class="spacingInTable labelStyle" width="20%"><s:text name="label.transactionType"/></td>
    <td class="spacingInTable">
        <s:select id="transactionTypes" cssStyle="180px;"
                  list="listOfTransactionTypes"
                  name="marketingInformation.transactionType"
                  headerKey="null" headerValue="%{getText('label.common.selectHeader')}"
                  value="%{marketingInformation.transactionType.id.toString()}"
                  listKey="id" listValue="displayType"/>
    </td>
</tr>
<tr>
    <td class="spacingInTable labelStyle"><s:text name="label.numberOfYears"/></td>
    <td class="spacingInTable"><s:textfield id="numberOfYears" name="marketingInformation.years"/></td>
</tr>
<tr>
    <td class="spacingInTable labelStyle"><s:text name="label.numberOfMonths"/></td>
    <td class="spacingInTable"><s:textfield id="numberOfMonths" name="marketingInformation.months"/></td>
</tr>
<tr>
    <td class="spacingInTable labelStyle" width="20%"><s:text name="label.market"/></td>
    <td width="100%" class="spacingInTable">
        <s:select id="market" cssStyle="180px;"
                  name="marketingInformation.market"
                  list="listOfMarkets"
                  headerKey="" headerValue="%{getText('label.common.selectHeader')}"
                  value="%{marketingInformation.market.id.toString()}"
                  listKey="id" listValue="name"/>
    </td>
</tr>
<tr>
    <td class="spacingInTable labelStyle" width="20%"><s:text name="label.marketType"/></td>
    <td class="spacingInTable">
        <sd:autocompleter id='marketType' cssStyle='180px;' name='marketingInformation.marketType' value='%{marketingInformation.marketType.id.toString()}' indicator='marketType_indicator' listenTopics='/load/marketType' />
    </td>
</tr>
<tr>
    <td class="spacingInTable labelStyle" width="20%"><s:text name="label.application"/></td>
    <td class="spacingInTable">
        <sd:autocompleter id='application' cssStyle='180px;' name='marketingInformation.application' value='%{marketingInformation.application.id.toString()}' indicator='application_indicator' listenTopics='/load/application' headerKey='' headerValue="%{getText('label.common.selectHeader')}" />
    </td>
</tr>
<tr>
<td class="spacingInTable labelStyle" width="20%"><s:text name="label.applicationDecsription"/></td>
    <td class="spacingInTable">
    <s:textarea rows="4" cols="30" value="" disabled="true" id="applicationDescription"/>
    </td>
    </tr>
    </tbody>
    </table>
    </td>
    <td>
    <table>
    <tbody>
    <tr>
    <td class="spacingInTable labelStyle"><s:text name="label.additionalMarketingInfo"/></td>
    <td width="30%" class="spacingInTable">
    <s:select name="marketingInformation.additionalInfo" cssStyle="180px;"
              list="listOfAdditionalInfo"
              value="%{marketingInformation.additionalInfo}"
              headerKey="" headerValue="%{getText('label.common.selectHeader')}" id="additionalInfo"/>
    </td>
    </tr>
    </tbody>
    </table>
    </td>
    </tr>
    </tbody>
    </table>
</div>
</div>
</div>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        dojo.connect(dijit.byId("market"), "onChange", function(newValue) {
            dojo.publish("/load/marketType", [{
                url: "list_market_types.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                params: {
                    marketId: newValue
                },
                makeLocal: true
            }]);
            dojo.publish("/load/application", [{
                url: "list_applications_for_market.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                params: {
                    marketId: newValue
                },
                makeLocal: true
            }]);

        });
        dojo.connect(dijit.byId("application"), "onChange", fillAppDescription);
    });
    function fillAppDescription() {
        var params = {};
        params["marketId"] = dijit.byId("application").getValue();
        dojo.byId("applicationDescription").value = '';
        twms.ajax.fireJsonRequest("find_market_description.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                params, function(details) {
            if (details) {
                dojo.byId("applicationDescription").value = details;
            }
        });
    }
</script>

