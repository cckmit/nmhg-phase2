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
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <s:head theme="twms"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.Tooltip");
    </script>
    <style type="text/css">
        td{
            width:25%;
        }
		.mainTitle{
		cursor:pointer;
		}
		.admin_data_table{
		font-family:Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-weight:700;
		color:#545454;
		}
    </style>
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; overflow-x:hidden; overflow-y:auto;" id="root">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form name="buForm" action="saveBUConfiguration" id="saveBUConfiguration">


<div dojoType="twms.widget.TitlePane" title="Claim" labelNodeClass="section_header" open="true">
    <u:fold id="claim_section_1" label="Home Page" foldableClass="claim_section_1"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="2"
           class="grid">
           <tbody class="claim_section_1">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_HOME_PAGE">
                    <t:configParam paramName="paramList[#iter.index]" />
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
    <u:fold id="claim_section_2" label="Claim Input Parameters" foldableClass="claim_section_2"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="2"
           class="grid ">
           <tbody class="claim_section_2">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_INPUT">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
    <u:fold id="claim_section_3" label="Claim Submission" foldableClass="claim_section_3"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="claim_section_3">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_SUBMIT">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
    <u:fold id="claim_section_4" label="Claim Processing" foldableClass="claim_section_4"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="claim_section_4">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_PROCESS">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
    <u:fold id="claim_section_5" label="Return Part Management" foldableClass="claim_section_5"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="claim_section_5">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_RETURN">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
    <u:fold id="claim_section_6" label="Field Product Improvement List" foldableClass="claim_section_6"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="claim_section_6">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_FIELD">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="claim_section_7" label="FOC Claim" foldableClass="claim_section_7"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="claim_section_7">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_FOC">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
	
	    <u:fold id="claim_section_8" label="Claim Display" foldableClass="claim_section_8"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="2"
           class="grid ">
           <tbody class="claim_section_8">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_CLAIMS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_CLAIM_DISLAY">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

</div>
<div dojoType="twms.widget.TitlePane" title="Inventory" labelNodeClass="section_header" open="true">
    <u:fold id="inventory_section_1" label="DR/TTR Configuration" foldableClass="inventory_section_1"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid  ">
           <tbody class="inventory_section_1">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_INVENTORY">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_INVENTORY_DR_ETR">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="inventory_section_2" label="Delivery Report" foldableClass="inventory_section_2"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid  ">
           <tbody class="inventory_section_2">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_INVENTORY">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_INVENTORY_DR">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="inventory_section_3" label="Transfers(TTR,D2D)" foldableClass="inventory_section_3"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid  ">
           <tbody class="inventory_section_3">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_INVENTORY">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_INVENTORY_ETR">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="inventory_section_4" label="Search" foldableClass="inventory_section_4"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid  ">
           <tbody class="inventory_section_4">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_INVENTORY">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_INVENTORY_SEARCH">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
</div>
<div dojoType="twms.widget.TitlePane" title="Supplier Recovery" labelNodeClass="section_header"
     open="true">
    <u:fold id="Supplier_section_1" label="Supplier Recovery" foldableClass="Supplier_section_1"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="Supplier_section_1">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_RECOVERY">
                <t:configParam paramName="paramList[#iter.index]"/>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
</div>
<div dojoType="twms.widget.TitlePane" title="Administration" labelNodeClass="section_header"
     open="true">
    <u:fold id="Administration_section_1" label="Modifiers" foldableClass="Administration_section_1"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="Administration_section_1">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_OTHERS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_OTHERS_MODIFIERS">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="Administration_section_2" label="Warranty Plan" foldableClass="Administration_section_2"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="Administration_section_2">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == @tavant.twms.web.bu.ManageBUConfiguration@LOGICAL_GROUP_OTHERS">
                <s:if test="sections == @tavant.twms.web.bu.ManageBUConfiguration@SECTION_OTHERS_WARRANTY_PLAN">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>

    <u:fold id="Administration_section_3" label="Miscellaneous" foldableClass="Administration_section_3"
            tagType="div" cssClass="mainTitle"/>
            <div class="borderTable"></div>
    <table width="100%" border="0" cellspacing="1" cellpadding="1"
           class="grid ">
           <tbody class="Administration_section_3">
        <s:iterator value="paramList" status="iter">
            <s:if test="logicalGroup == null">
                <s:if test="sections == null">
                    <t:configParam paramName="paramList[#iter.index]"/>
                </s:if>
            </s:if>
        </s:iterator>
        </tbody>
    </table>
</div>
<div align="center" class="spacingAtTop">
    <s:submit cssClass="buttonGeneric" value="Update"/>
</div>
<authz:ifPermitted resource="warrantyAdminManageBusinessConfigurationsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select", dojo.byId('saveBUConfiguration')).length; i++) {
	            dojo.query("input, button, textarea, select", dojo.byId('saveBUConfiguration'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</s:form>
</div>
</div>
</u:body>
</html>