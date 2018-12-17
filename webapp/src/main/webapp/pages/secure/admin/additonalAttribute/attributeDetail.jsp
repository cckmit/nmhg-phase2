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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
        <title>:: <s:text name="title.common.warranty" /> ::</title>
        <s:head theme="twms"/>
        <u:stylePicker fileName="detailDesign.css"/>
        <u:stylePicker fileName="yui/reset.css" common="true"/>
        <u:stylePicker fileName="common.css"/>
        <u:stylePicker fileName="claimForm.css"/>
        <u:stylePicker fileName="base.css"/>
        <u:stylePicker fileName="adminPayment.css"/>
        <style type="text/css">
            .addRow {
                margin-top: -14px;
                height: 14px;
                text-align: right;
                padding-right: 17px;
            }
        </style>
    </head>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
    </script>
    <u:actionResults/>
    <u:body>
        <div dojoType="dijit.layout.LayoutContainer" layoutAlign="client"
             style="overflow-X: auto; overflow-Y: auto; width:100%; height: 100%">
            <div dojoType="dijit.layout.ContentPane" style="overflow-X: auto; overflow-Y: auto; width:99%; height: 99%">
                <s:form action="attributes_create" name="attributeForm" theme="twms" id="attributeForm">
                    <s:hidden name="showI18nButton" value="true"/>
                    <div class="admin_section_div" style="margin:5px;width:99%">
                        <div class="admin_section_heading"><s:text name="title.additionalAtrribute.attributeDetails" /></div>

                        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid"  style="margin-top:5px;">


                            <s:hidden name="additionalAttributes" value="%{additionalAttributes.id}"></s:hidden>

                            <s:if test="additionalAttributes == null || additionalAttributes.id == null || additionalAttributes.id == 0">
                                <tr>
                                    <td  width="10%"  class="label">
                                        <s:text name="label.additionalAttribute.name"/>
                                    </td>
                                    <td  class="labelNormal">
                                        <s:textfield name="localizedFailureMessages_en_US" value="%{additionalAttributesName}"/>     
                                    </td>
                                </s:if>      			
                                <s:else>
                                    <td width="20%" class="label" ><s:text name="label.additionalAttribute.name"/></td> 
                                    <td><s:property value="additionalAttributes.attributeName"/>
                                        <s:hidden name="localizedFailureMessages_en_US" value="%{additionalAttributes.name}"/>
                                        <s:hidden name="id" value="%{additionalAttributes.id}" />
                                    </td>
                                    <td>
                                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                                   id="additionalAttributeName" 
                                                   tabLabel="%{getText('label.common.internationalize')}"
                                                   url="internationalizeAttributeName.action?additionalAttributes=%{additionalAttributes.id}"
                                                   catagory="inventory">
                                            <s:text name="label.common.internationalize" />
                                        </u:openTab>
                                    </td>
                                </s:else>

                            </tr>

                            <tr>
                                <td width="10%" class="label">
                                    <s:text name="label.additionalAttribute.claimType">:</s:text>
                                </td>
                               <%--  <td width="20%" class="labelNormal">
                                    <s:select id="type"  listKey="type"  list="claimTypes1" listValue="displayType"  value="%{additionalAttributes.claimTypes}"  />
                                    <s:if test="additionalAttributes != null">
                                        <script type="text/javascript">
                                            dojo.addOnLoad(function() {
                                                var clmTypes = '<s:property value="additionalAttributes.claimTypes"/>';
                                                var typesArray = clmTypes.split(", ");
                                                for(var i=0 ; i<typesArray.length ;i++) {
                                                    if(typesArray[i] == 'Machine')
                                                        dojo.byId("claimTypes").options[0].selected=true;
                                                    else if(typesArray[i] == 'Parts')
                                                        dojo.byId("claimTypes").options[1].selected=true;
                                                    if(typesArray[i] == 'Campaign')
                                                        dojo.byId("claimTypes").options[2].selected=true;
                                                }
                                            });
                                        </script>
                                    </s:if>
                                </td> --%>
                                  <td style="padding-top:5px;" valign="top">  
                	       <s:select theme="simple" id="claimTypes" name="additionalAttributes.claimTypes" list="claimTypes" cssStyle="width:145px;" multiple="true"
                	    listKey="type" listValue="%{getText(displayType)}" value="%{additionalAttributes.claimTypes}"/>        
                	     <s:if test="additionalAttributes != null">
                                        <script type="text/javascript">
                                            dojo.addOnLoad(function() {
                                                var clmTypes = '<s:property value="additionalAttributes.claimTypes"/>';
                                                var typesArray = clmTypes.split(", ");
                                                for(var i=0 ; i<typesArray.length ;i++) {
                                                    if(typesArray[i] == 'Machine')
                                                        dojo.byId("claimTypes").options[0].selected=true;
                                                    else if(typesArray[i] == 'Parts')
                                                        dojo.byId("claimTypes").options[1].selected=true;
                                                    if(typesArray[i] == 'Campaign')
                                                        dojo.byId("claimTypes").options[2].selected=true;
                                                        if(typesArray[i] == 'Attachment')
                                                        dojo.byId("claimTypes").options[3].selected=true;
                                                }
                                            });
                                        </script>
                                    </s:if>       
            </td>
                            </tr>
                            <tr>
                                <td width="10%" class="label">
                                    <s:text name="label.additionalAttribute.attributeType">:</s:text>
                                </td>
                                <td width="20%" class="labelNormal">
                                    <s:if test="additionalAttributes == null || additionalAttributes.id == null || 
                                          (additionalAttributes.id != null && additionalAttributes.id==0)">
                                        <s:select  name= "additionalAttributes.attributeType" list="{'Number','Text','Text Area','Date'}"/>
                                    </s:if>
                                    <s:else>
                                        <s:property value="additionalAttributes.attributeType"/>
                                    </s:else>
                                </td>
                            </tr>

                            <tr>
                                <td width="10%"  class="label">
                                    <s:text name="label.additionalAttribute.purpose">:</s:text>
                                </td>
                                <td width="20%" class="labelNormal">
                                    <s:if test="additionalAttributes == null || additionalAttributes.id == null || 
                                          (additionalAttributes.id != null && additionalAttributes.id==0)">
                                        <s:select name= "attributePurpose" list="{'Part Source Purpose','Claimed Inventory Purpose','Claim Purpose','Job Code Purpose'}" value="%{additionalAttributes.attributePurpose.purpose}"/>
                                    </s:if>
                                    <s:else>
                                        <s:property value="additionalAttributes.attributePurpose.purpose"/>
                                    </s:else>
                                </td>
                            </tr>

                            <tr>
                                <td  width="10%" class="label">
                                    <s:text name="label.additionalAttribute.mandatory">:</s:text>
                                </td>
                                <td width="5%" class="labelNormal">
                                    <s:radio name= "additionalAttributes.mandatory" list="yesNo" listKey="key" listValue="value"/>
                                </td>
                            </tr>

                        </table></div>
                    <div align="center" style="margin-top:10px;">

                        <s:submit cssClass="buttonGeneric" name="button.common.submit" />&nbsp;
                        <s:if test="additionalAttributes != null || additionalAttributes.id != null || additionalAttributes.id != 0">
                            <s:submit cssClass="buttonGeneric" value="%{getText('button.common.delete')}"  action="attributes_delete"/>&nbsp;	
                        </s:if>
                        <input type="button" value="<s:text name='label.cancel' />" class="buttonGeneric" onClick="closeCurrentTab();" />

                    </div>
                </s:form>
            </div>
        </div>
<authz:ifPermitted resource="warrantyAdminCreate/UpdateAttributesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('attributeForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('attributeForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
    </u:body>