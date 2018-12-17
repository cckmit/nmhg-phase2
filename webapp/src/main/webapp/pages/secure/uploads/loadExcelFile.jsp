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
  <s:form action="uploadFile.action" method="post" enctype="multipart/form-data">
    <div >
        <p style="padding:5px;">
        <a href="downloadTemplate.action?type=claim"><s:text name="label.uploads.downloadExcelTemplate"/></a>
        </p>
        <p style="padding:5px;">
          <input type="checkbox" name="saveClaim" value="true"/><s:text name="label.claim.submitClaim"/>
                    <br/>
          <span class="label"><s:text name="label.uploads.fileToUploaded"/></span>
                    <s:file cssClass="border:1px solid #666666;" name="input" label="File" />
                    <br/>
          <s:submit type="button" value='<s:text name="label.common.upload"/>' />
        </p>
    </div>
  </s:form>
  <br/>
  <br/>
  <s:action name="get_history_table" executeResult="true"/>
</u:body>
</html>

