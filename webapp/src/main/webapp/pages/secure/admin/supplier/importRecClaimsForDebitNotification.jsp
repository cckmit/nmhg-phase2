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
  <title><s:text name="fileUploadTest"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
</head>
<u:body>
    <u:actionResults/>
  <s:form action="uploadDebitClaims.action" method="post" enctype="multipart/form-data">
    <div style="margin:5px;background:#F3FBFE;border:1px solid #EFEBF7">
       
       <div style="padding:5px;"> <a href="downloadTemplate.action?type=recoveryClaim"><s:text name="label.uploads.downloadExcelTemplate"/></a>
        <br/>
        <br/>
        	 <span class="label"><s:text name="label.uploads.fileToUploaded"/></span>
                    <s:file cssClass="border:1px solid #666666;" name="file" label="File" />
                    <br/>
           </div>
            <br/>
      </div>
     <br/>
     <div align="center">
           <s:submit type="button" value="Upload"/>
           </div>
  </s:form>
  <br/>
  <br/>
  </u:body>
</html>

