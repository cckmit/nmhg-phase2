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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
  <title><s:text name="title.manageData.documentUpload"/></title>
  <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
</head>
<body>
    <u:actionResults/>
  <s:form action="upload_inventory.action?type=inventory" method="post" enctype="multipart/form-data">
    <div >
        <p style="padding:5px;">
          <a href="downloadTemplate.action?type=inventory"><s:text name="link.manageData.downloadInventoryTemplateFile"/> </a>
          <br>
          <br>
          <span class="label"><s:text name="label.manageData.selectFile"/>:</span>
                    <s:file cssClass="border:1px solid #666666;" name="input" label="File" />
                    <br/>
                    <s:submit type="button" value="%{getText('button.common.upload')}" />
        </p>
    </div>
  </s:form>
  <br/>
  <br/>
  <s:action name="get_history_table" executeResult="true"/>

</body>
</html>