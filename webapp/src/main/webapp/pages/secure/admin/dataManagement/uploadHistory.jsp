<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div >
      <div class="section_header"><s:text name="label.manageData.recentUpload"/></div>
      <table class="grid borderForTable" style="font-size:7.5pt" width="100%" >
        <tr class="row_head">
          <th><s:text name="label.manageData.dateOfUpload"/> </th>
          <th><s:text name="label.manageData.uploadedFile"/> </th>
          <th><s:text name="label.manageData.successfulUpdates"/> </th>
          <th><s:text name="label.manageData.unsuccessfulUpdates"/> </th>
          <th><s:text name="label.manageData.errorFile"/> </th>
        </tr>
        <s:iterator value="uploadHistory">
        <tr>
          <td>
            <s:property value="dateOfUpload"/>
          </td>
          <td>
            <s:url action="download_input_file" id="url">
               <s:param name="id" value="%{id}"/>
             </s:url>
             <a href="<s:property value="#url" escape="false"/>"> <s:text name="link.manageData.inputFile"/> </a>
          </td>
          <td> <s:property value="numberOfSuccessfulUploads"/></td>
          <td>
            <s:property value="numberOfErrorUploads"/>
          </td>

          <td>
           <s:if test="numberOfErrorUploads != 0">
             <s:url action="download_error_file" id="url">
               <s:param name="id" value="%{id}"/>
             </s:url>
             <a href="<s:property value="#url" escape="false"/>"> <s:text name="link.manageData.errorFile"/> </a>
           </s:if>
         </td>
        </tr>
        </s:iterator>
      </table>
    </div>