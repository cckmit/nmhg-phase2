<%-- 
    Document   : previewRemoteLog
    Created on : Mar 11, 2010, 5:11:49 PM
    Author     : prasad.r
--%>

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

<u:stylePicker fileName="preview.css"/>

<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.TabContainer");
</script>

<div dojoType="dijit.layout.LayoutContainer">
    <div  dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
        <div dojoType="dijit.layout.ContentPane" title="Detail" class="scrollYNotX">
            <div class="policy_section_div">
                <div class="section_header">Remote Interaction Detail</div>
                <table class="grid" cellspacing="0" cellpadding="0" id="log_deatil_table">
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.manageRemoteInteractions.syncType" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.syncType" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.businessUnit" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.businessUnitInfo" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.manageRemoteInteractions.logicalId" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.businessId" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.manageRemoteInteractions.status" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.status.status" /></td>
                    </tr>
<s:if test="previewSyncObj.status.status == 'Failed'">
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.errorType" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.errorType" /></td>
                    </tr>
</s:if>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.numberOfAttempts" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.noOfAttempts" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.uniqueIDName" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.uniqueIdName" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.manageRemoteInteractions.transactionId" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.uniqueIdValue" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.startTime" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.startTime" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.lastUpdatedDate" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.updateDate" /></td>
                    </tr>
					<tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="Is Hidden" /> :</td>
                        <td  nowrap="nowrap">
                            <s:if test="previewSyncObj.deleted">
                                Yes
                            </s:if>
                            <s:else>
                                    No
                            </s:else>
                            </td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="Hidden By" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.hiddenBy" /></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" nowrap="nowrap"><s:text name="Hidden On" /> :</td>
                        <td  nowrap="nowrap"><s:property value="previewSyncObj.hiddenOn" /></td>
                    </tr>
                </table>
            </div>
        </div>
        <div dojoType="dijit.layout.ContentPane" title="Request XML" class="scrollYNotX">
            <div class="policy_section_div">
                <div class="section_header">Request XML</div>
                <table class="grid" cellspacing="0" cellpadding="0" id="log_deatil_request_xml_table">
                    <tr>
                        <td width="20%" class="labelStyle" nowrap="nowrap">Request XML :</td>
                        <td><s:property value="previewSyncObj.bodXML" /></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <s:url action="download_request_xml" id="url">
                               <s:param name="id" value="previewSyncObj.id"/>
                             </s:url>
                             <a href="<s:property value="#url" escape="false"/>"> <s:text name="label.manageRemoteInteractions.download"/> </a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
<s:if test="previewSyncObj.status.status == 'Failed'">
    <div dojoType="dijit.layout.ContentPane" title='<s:text name="label.integration.errorMessage"/>' class="scrollYNotX">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.integration.errorMessage"/></div>
                    <table class="grid" cellspacing="0" cellpadding="0" id="log_deatil_request_xml_table">
                        <tr>
                            <td width="20%" class="labelStyle" nowrap="nowrap">Error Message :</td>
                            <td><s:property value="previewSyncObj.errorMessage" /></td>
                        </tr>
                    </table>
            </div>
        </div>
</s:if>
    </div>
</div>
