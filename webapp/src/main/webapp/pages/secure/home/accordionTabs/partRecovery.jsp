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
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%><div dojoType="dijit.layout.ContentPane" id="parts_recovery"
        title="<s:text name="accordion_jsp.accordionPane.parts_recovery" />"
        iconClass="partreturns">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>
        <s:iterator value="supplierRecoveryPartReturnFolders" status="status">
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                id="parts_%{top[0]}" tagType="li"
                cssClass="partReturns_folder folder" tabLabel="%{top[4]}"
                url="%{top[3]}.action?folderName=%{top[1]}&actionUrl=%{top[3]}&taskName=%{top[1]}"
                catagory="supplierRecovery" helpCategory="%{getHtmlFileBasedOnFolderName('Parts_Recovery',top[4])}">
                <s:property value="%{top[4]}" /> (<span class="count"><s:property value="%{top[2]}" /></span>)
            </u:openTab>
            <%-- FIXME: The buttons here were dependent on the user permissions, this logic needs to be done in the action. --%>
            <script type="text/javascript" language="javascript">
                dojo.addOnLoad( function() {
                    refreshManager.register("parts_<s:property value="%{top[0]}"/>", "<s:property value="%{top[1]}"/>", "supplierRecoveryPartReturnFolders.action");
                });
            </script>
        </s:iterator>
    </ol>
     <t:daSection id="partRecoverySearchFolders"
	      fetchFrom="partRecoverySearchFolders.action"
    	  fetchOn="/accordion/refreshsearchfolders"
		  title="%{getText('accordionLabel.viewClaim.searchFolders')}"
	      appendMode="false"
    	  cssClass="searchFolders"/>
</div>
</div>