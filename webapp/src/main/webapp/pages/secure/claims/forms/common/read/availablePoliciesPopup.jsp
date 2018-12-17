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

<%--
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<div dojoType="twms.widget.Dialog" id="policies" bgColor="white" bgOpacity="0.5"
    toggle="fade" toggleDuration="250">
<div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
    style="padding: 0; margin: 0;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<table width="98%" class="repeat">
    <thead>
        <tr class="detailsHeader">
            <th colspan="7"> <s:text name="label.newClaim.activePoliciesCaps"/></th>
        </tr>
        <tr class="row_head">
            <th width="15%"><s:text name="label.common.policyCode"/></th>
            <th width="25%"><s:text name="label.common.policyName"/></th>
            <th width="10%"><s:text name="label.common.type"/></th>
            <th width="12%"><s:text name="label.common.startDate"/></th>
            <th width="12%"><s:text name="label.common.endDate"/></th>
            <th width="13%"><s:text name="label.common.monthsCovered"/></th>
            <th width="13%"><s:text name="label.common.hoursCovered"/></th>
        </tr>
    </thead>
    <tbody>
        <s:iterator value="activePolicies">
            <tr>
                <td width="15%"><s:property value="code" /></td>
                <td width="25%"><s:property value="description"/></td>
                <td width="10%"><s:property value="warrantyType" /></td>
                <td width="12%"><s:property value="warrantyPeriod.fromDate" /></td>
                <td width="12%"><s:property value="warrantyPeriod.tillDate" /></td>
                <td width="13%" class="numeric"><s:property value="monthsCovered" /></td>
                <td width="13%" class="numeric"><s:property value="hoursCovered" /></td>
            </tr>
        </s:iterator>
    </tbody>
</table>
</div>
<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="padding-bottom: 3px; ">
<center class="buttons"><input id="policies_hider" value="<s:text name="button.common.close"/>" type="button"/></center>
</div>
</div>
</div>
<script type="text/javascript">
dojo.addOnLoad(function() {
    dojo.connect(dojo.byId("policies_hider"), "onclick", function() {
        dojo.publish("/availablePolicies/hide");
    });
});
</script>

<script type="text/javascript">
dojo.subscribe("/availablePolicies/hide", null, function() {
    dijit.byId("policies").hide();
});
dojo.subscribe("/availablePolicies/show", null, function() {
    dijit.byId("policies").show();
    if(dijit.byId("foo")) {
    	dijit.byId("foo").resize();
    }
});
</script>
--%>